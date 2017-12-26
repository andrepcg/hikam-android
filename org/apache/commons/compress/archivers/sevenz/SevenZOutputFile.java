package org.apache.commons.compress.archivers.sevenz;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.utils.CharsetNames;
import org.apache.commons.compress.utils.CountingOutputStream;

public class SevenZOutputFile implements Closeable {
    private CountingOutputStream[] additionalCountingStreams;
    private final Map<SevenZArchiveEntry, long[]> additionalSizes;
    private final SeekableByteChannel channel;
    private final CRC32 compressedCrc32;
    private Iterable<? extends SevenZMethodConfiguration> contentMethods;
    private final CRC32 crc32;
    private CountingOutputStream currentOutputStream;
    private long fileBytesWritten;
    private final List<SevenZArchiveEntry> files;
    private boolean finished;
    private int numNonEmptyStreams;

    private class OutputStreamWrapper extends OutputStream {
        private static final int BUF_SIZE = 8192;
        private final ByteBuffer buffer;

        private OutputStreamWrapper() {
            this.buffer = ByteBuffer.allocate(8192);
        }

        public void write(int b) throws IOException {
            this.buffer.clear();
            this.buffer.put((byte) b).flip();
            SevenZOutputFile.this.channel.write(this.buffer);
            SevenZOutputFile.this.compressedCrc32.update(b);
            SevenZOutputFile.this.fileBytesWritten = 1 + SevenZOutputFile.this.fileBytesWritten;
        }

        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        public void write(byte[] b, int off, int len) throws IOException {
            if (len > 8192) {
                SevenZOutputFile.this.channel.write(ByteBuffer.wrap(b, off, len));
            } else {
                this.buffer.clear();
                this.buffer.put(b, off, len).flip();
                SevenZOutputFile.this.channel.write(this.buffer);
            }
            SevenZOutputFile.this.compressedCrc32.update(b, off, len);
            SevenZOutputFile.this.fileBytesWritten = SevenZOutputFile.this.fileBytesWritten + ((long) len);
        }

        public void flush() throws IOException {
        }

        public void close() throws IOException {
        }
    }

    public SevenZOutputFile(File filename) throws IOException {
        this(Files.newByteChannel(filename.toPath(), EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING), new FileAttribute[0]));
    }

    public SevenZOutputFile(SeekableByteChannel channel) throws IOException {
        this.files = new ArrayList();
        this.numNonEmptyStreams = 0;
        this.crc32 = new CRC32();
        this.compressedCrc32 = new CRC32();
        this.fileBytesWritten = 0;
        this.finished = false;
        this.contentMethods = Collections.singletonList(new SevenZMethodConfiguration(SevenZMethod.LZMA2));
        this.additionalSizes = new HashMap();
        this.channel = channel;
        channel.position(32);
    }

    public void setContentCompression(SevenZMethod method) {
        setContentMethods(Collections.singletonList(new SevenZMethodConfiguration(method)));
    }

    public void setContentMethods(Iterable<? extends SevenZMethodConfiguration> methods) {
        this.contentMethods = reverse(methods);
    }

    public void close() throws IOException {
        if (!this.finished) {
            finish();
        }
        this.channel.close();
    }

    public SevenZArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
        SevenZArchiveEntry entry = new SevenZArchiveEntry();
        entry.setDirectory(inputFile.isDirectory());
        entry.setName(entryName);
        entry.setLastModifiedDate(new Date(inputFile.lastModified()));
        return entry;
    }

    public void putArchiveEntry(ArchiveEntry archiveEntry) throws IOException {
        this.files.add((SevenZArchiveEntry) archiveEntry);
    }

    public void closeArchiveEntry() throws IOException {
        if (this.currentOutputStream != null) {
            this.currentOutputStream.flush();
            this.currentOutputStream.close();
        }
        SevenZArchiveEntry entry = (SevenZArchiveEntry) this.files.get(this.files.size() - 1);
        if (this.fileBytesWritten > 0) {
            entry.setHasStream(true);
            this.numNonEmptyStreams++;
            entry.setSize(this.currentOutputStream.getBytesWritten());
            entry.setCompressedSize(this.fileBytesWritten);
            entry.setCrcValue(this.crc32.getValue());
            entry.setCompressedCrcValue(this.compressedCrc32.getValue());
            entry.setHasCrc(true);
            if (this.additionalCountingStreams != null) {
                long[] sizes = new long[this.additionalCountingStreams.length];
                for (int i = 0; i < this.additionalCountingStreams.length; i++) {
                    sizes[i] = this.additionalCountingStreams[i].getBytesWritten();
                }
                this.additionalSizes.put(entry, sizes);
            }
        } else {
            entry.setHasStream(false);
            entry.setSize(0);
            entry.setCompressedSize(0);
            entry.setHasCrc(false);
        }
        this.currentOutputStream = null;
        this.additionalCountingStreams = null;
        this.crc32.reset();
        this.compressedCrc32.reset();
        this.fileBytesWritten = 0;
    }

    public void write(int b) throws IOException {
        getCurrentOutputStream().write(b);
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        if (len > 0) {
            getCurrentOutputStream().write(b, off, len);
        }
    }

    public void finish() throws IOException {
        if (this.finished) {
            throw new IOException("This archive has already been finished");
        }
        this.finished = true;
        long headerPosition = this.channel.position();
        ByteArrayOutputStream headerBaos = new ByteArrayOutputStream();
        DataOutputStream header = new DataOutputStream(headerBaos);
        writeHeader(header);
        header.flush();
        byte[] headerBytes = headerBaos.toByteArray();
        this.channel.write(ByteBuffer.wrap(headerBytes));
        CRC32 crc32 = new CRC32();
        crc32.update(headerBytes);
        ByteBuffer bb = ByteBuffer.allocate(((((SevenZFile.sevenZSignature.length + 2) + 4) + 8) + 8) + 4).order(ByteOrder.LITTLE_ENDIAN);
        this.channel.position(0);
        bb.put(SevenZFile.sevenZSignature);
        bb.put((byte) 0).put((byte) 2);
        bb.putInt(0);
        bb.putLong(headerPosition - 32).putLong(4294967295L & ((long) headerBytes.length)).putInt((int) crc32.getValue());
        crc32.reset();
        crc32.update(bb.array(), SevenZFile.sevenZSignature.length + 6, 20);
        bb.putInt(SevenZFile.sevenZSignature.length + 2, (int) crc32.getValue());
        bb.flip();
        this.channel.write(bb);
    }

    private OutputStream getCurrentOutputStream() throws IOException {
        if (this.currentOutputStream == null) {
            this.currentOutputStream = setupFileOutputStream();
        }
        return this.currentOutputStream;
    }

    private CountingOutputStream setupFileOutputStream() throws IOException {
        if (this.files.isEmpty()) {
            throw new IllegalStateException("No current 7z entry");
        }
        OutputStream out = new OutputStreamWrapper();
        ArrayList<CountingOutputStream> moreStreams = new ArrayList();
        boolean first = true;
        for (SevenZMethodConfiguration m : getContentMethods((SevenZArchiveEntry) this.files.get(this.files.size() - 1))) {
            if (!first) {
                OutputStream cos = new CountingOutputStream(out);
                moreStreams.add(cos);
                out = cos;
            }
            out = Coders.addEncoder(out, m.getMethod(), m.getOptions());
            first = false;
        }
        if (!moreStreams.isEmpty()) {
            this.additionalCountingStreams = (CountingOutputStream[]) moreStreams.toArray(new CountingOutputStream[moreStreams.size()]);
        }
        return new CountingOutputStream(out) {
            public void write(int b) throws IOException {
                super.write(b);
                SevenZOutputFile.this.crc32.update(b);
            }

            public void write(byte[] b) throws IOException {
                super.write(b);
                SevenZOutputFile.this.crc32.update(b);
            }

            public void write(byte[] b, int off, int len) throws IOException {
                super.write(b, off, len);
                SevenZOutputFile.this.crc32.update(b, off, len);
            }
        };
    }

    private Iterable<? extends SevenZMethodConfiguration> getContentMethods(SevenZArchiveEntry entry) {
        Iterable<? extends SevenZMethodConfiguration> ms = entry.getContentMethods();
        return ms == null ? this.contentMethods : ms;
    }

    private void writeHeader(DataOutput header) throws IOException {
        header.write(1);
        header.write(4);
        writeStreamsInfo(header);
        writeFilesInfo(header);
        header.write(0);
    }

    private void writeStreamsInfo(DataOutput header) throws IOException {
        if (this.numNonEmptyStreams > 0) {
            writePackInfo(header);
            writeUnpackInfo(header);
        }
        writeSubStreamsInfo(header);
        header.write(0);
    }

    private void writePackInfo(DataOutput header) throws IOException {
        header.write(6);
        writeUint64(header, 0);
        writeUint64(header, 4294967295L & ((long) this.numNonEmptyStreams));
        header.write(9);
        for (SevenZArchiveEntry entry : this.files) {
            if (entry.hasStream()) {
                writeUint64(header, entry.getCompressedSize());
            }
        }
        header.write(10);
        header.write(1);
        for (SevenZArchiveEntry entry2 : this.files) {
            if (entry2.hasStream()) {
                header.writeInt(Integer.reverseBytes((int) entry2.getCompressedCrcValue()));
            }
        }
        header.write(0);
    }

    private void writeUnpackInfo(DataOutput header) throws IOException {
        header.write(7);
        header.write(11);
        writeUint64(header, (long) this.numNonEmptyStreams);
        header.write(0);
        for (SevenZArchiveEntry entry : this.files) {
            if (entry.hasStream()) {
                writeFolder(header, entry);
            }
        }
        header.write(12);
        for (SevenZArchiveEntry entry2 : this.files) {
            if (entry2.hasStream()) {
                long[] moreSizes = (long[]) this.additionalSizes.get(entry2);
                if (moreSizes != null) {
                    for (long s : moreSizes) {
                        writeUint64(header, s);
                    }
                }
                writeUint64(header, entry2.getSize());
            }
        }
        header.write(10);
        header.write(1);
        for (SevenZArchiveEntry entry22 : this.files) {
            if (entry22.hasStream()) {
                header.writeInt(Integer.reverseBytes((int) entry22.getCrcValue()));
            }
        }
        header.write(0);
    }

    private void writeFolder(DataOutput header, SevenZArchiveEntry entry) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int numCoders = 0;
        for (SevenZMethodConfiguration m : getContentMethods(entry)) {
            numCoders++;
            writeSingleCodec(m, bos);
        }
        writeUint64(header, (long) numCoders);
        header.write(bos.toByteArray());
        for (long i = 0; i < ((long) (numCoders - 1)); i++) {
            writeUint64(header, i + 1);
            writeUint64(header, i);
        }
    }

    private void writeSingleCodec(SevenZMethodConfiguration m, OutputStream bos) throws IOException {
        byte[] id = m.getMethod().getId();
        byte[] properties = Coders.findByMethod(m.getMethod()).getOptionsAsProperties(m.getOptions());
        int codecFlags = id.length;
        if (properties.length > 0) {
            codecFlags |= 32;
        }
        bos.write(codecFlags);
        bos.write(id);
        if (properties.length > 0) {
            bos.write(properties.length);
            bos.write(properties);
        }
    }

    private void writeSubStreamsInfo(DataOutput header) throws IOException {
        header.write(8);
        header.write(0);
    }

    private void writeFilesInfo(DataOutput header) throws IOException {
        header.write(5);
        writeUint64(header, (long) this.files.size());
        writeFileEmptyStreams(header);
        writeFileEmptyFiles(header);
        writeFileAntiItems(header);
        writeFileNames(header);
        writeFileCTimes(header);
        writeFileATimes(header);
        writeFileMTimes(header);
        writeFileWindowsAttributes(header);
        header.write(0);
    }

    private void writeFileEmptyStreams(DataOutput header) throws IOException {
        boolean hasEmptyStreams = false;
        for (SevenZArchiveEntry entry : this.files) {
            if (!entry.hasStream()) {
                hasEmptyStreams = true;
                break;
            }
        }
        if (hasEmptyStreams) {
            header.write(14);
            BitSet emptyStreams = new BitSet(this.files.size());
            for (int i = 0; i < this.files.size(); i++) {
                emptyStreams.set(i, !((SevenZArchiveEntry) this.files.get(i)).hasStream());
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            writeBits(out, emptyStreams, this.files.size());
            out.flush();
            byte[] contents = baos.toByteArray();
            writeUint64(header, (long) contents.length);
            header.write(contents);
        }
    }

    private void writeFileEmptyFiles(DataOutput header) throws IOException {
        boolean hasEmptyFiles = false;
        int emptyStreamCounter = 0;
        BitSet emptyFiles = new BitSet(0);
        for (SevenZArchiveEntry file1 : this.files) {
            if (!file1.hasStream()) {
                boolean z;
                int i;
                boolean isDir = file1.isDirectory();
                int emptyStreamCounter2 = emptyStreamCounter + 1;
                if (isDir) {
                    z = false;
                } else {
                    z = true;
                }
                emptyFiles.set(emptyStreamCounter, z);
                if (isDir) {
                    i = 0;
                } else {
                    i = 1;
                }
                hasEmptyFiles |= i;
                emptyStreamCounter = emptyStreamCounter2;
            }
        }
        if (hasEmptyFiles) {
            header.write(15);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            writeBits(out, emptyFiles, emptyStreamCounter);
            out.flush();
            byte[] contents = baos.toByteArray();
            writeUint64(header, (long) contents.length);
            header.write(contents);
        }
    }

    private void writeFileAntiItems(DataOutput header) throws IOException {
        boolean hasAntiItems = false;
        BitSet antiItems = new BitSet(0);
        int antiItemCounter = 0;
        for (SevenZArchiveEntry file1 : this.files) {
            if (!file1.hasStream()) {
                boolean isAnti = file1.isAntiItem();
                int antiItemCounter2 = antiItemCounter + 1;
                antiItems.set(antiItemCounter, isAnti);
                hasAntiItems |= isAnti;
                antiItemCounter = antiItemCounter2;
            }
        }
        if (hasAntiItems) {
            header.write(16);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            writeBits(out, antiItems, antiItemCounter);
            out.flush();
            byte[] contents = baos.toByteArray();
            writeUint64(header, (long) contents.length);
            header.write(contents);
        }
    }

    private void writeFileNames(DataOutput header) throws IOException {
        header.write(17);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        out.write(0);
        for (SevenZArchiveEntry entry : this.files) {
            out.write(entry.getName().getBytes(CharsetNames.UTF_16LE));
            out.writeShort(0);
        }
        out.flush();
        byte[] contents = baos.toByteArray();
        writeUint64(header, (long) contents.length);
        header.write(contents);
    }

    private void writeFileCTimes(DataOutput header) throws IOException {
        int numCreationDates = 0;
        for (SevenZArchiveEntry entry : this.files) {
            if (entry.getHasCreationDate()) {
                numCreationDates++;
            }
        }
        if (numCreationDates > 0) {
            header.write(18);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            if (numCreationDates != this.files.size()) {
                out.write(0);
                BitSet cTimes = new BitSet(this.files.size());
                for (int i = 0; i < this.files.size(); i++) {
                    cTimes.set(i, ((SevenZArchiveEntry) this.files.get(i)).getHasCreationDate());
                }
                writeBits(out, cTimes, this.files.size());
            } else {
                out.write(1);
            }
            out.write(0);
            for (SevenZArchiveEntry entry2 : this.files) {
                if (entry2.getHasCreationDate()) {
                    out.writeLong(Long.reverseBytes(SevenZArchiveEntry.javaTimeToNtfsTime(entry2.getCreationDate())));
                }
            }
            out.flush();
            byte[] contents = baos.toByteArray();
            writeUint64(header, (long) contents.length);
            header.write(contents);
        }
    }

    private void writeFileATimes(DataOutput header) throws IOException {
        int numAccessDates = 0;
        for (SevenZArchiveEntry entry : this.files) {
            if (entry.getHasAccessDate()) {
                numAccessDates++;
            }
        }
        if (numAccessDates > 0) {
            header.write(19);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            if (numAccessDates != this.files.size()) {
                out.write(0);
                BitSet aTimes = new BitSet(this.files.size());
                for (int i = 0; i < this.files.size(); i++) {
                    aTimes.set(i, ((SevenZArchiveEntry) this.files.get(i)).getHasAccessDate());
                }
                writeBits(out, aTimes, this.files.size());
            } else {
                out.write(1);
            }
            out.write(0);
            for (SevenZArchiveEntry entry2 : this.files) {
                if (entry2.getHasAccessDate()) {
                    out.writeLong(Long.reverseBytes(SevenZArchiveEntry.javaTimeToNtfsTime(entry2.getAccessDate())));
                }
            }
            out.flush();
            byte[] contents = baos.toByteArray();
            writeUint64(header, (long) contents.length);
            header.write(contents);
        }
    }

    private void writeFileMTimes(DataOutput header) throws IOException {
        int numLastModifiedDates = 0;
        for (SevenZArchiveEntry entry : this.files) {
            if (entry.getHasLastModifiedDate()) {
                numLastModifiedDates++;
            }
        }
        if (numLastModifiedDates > 0) {
            header.write(20);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            if (numLastModifiedDates != this.files.size()) {
                out.write(0);
                BitSet mTimes = new BitSet(this.files.size());
                for (int i = 0; i < this.files.size(); i++) {
                    mTimes.set(i, ((SevenZArchiveEntry) this.files.get(i)).getHasLastModifiedDate());
                }
                writeBits(out, mTimes, this.files.size());
            } else {
                out.write(1);
            }
            out.write(0);
            for (SevenZArchiveEntry entry2 : this.files) {
                if (entry2.getHasLastModifiedDate()) {
                    out.writeLong(Long.reverseBytes(SevenZArchiveEntry.javaTimeToNtfsTime(entry2.getLastModifiedDate())));
                }
            }
            out.flush();
            byte[] contents = baos.toByteArray();
            writeUint64(header, (long) contents.length);
            header.write(contents);
        }
    }

    private void writeFileWindowsAttributes(DataOutput header) throws IOException {
        int numWindowsAttributes = 0;
        for (SevenZArchiveEntry entry : this.files) {
            if (entry.getHasWindowsAttributes()) {
                numWindowsAttributes++;
            }
        }
        if (numWindowsAttributes > 0) {
            header.write(21);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            if (numWindowsAttributes != this.files.size()) {
                out.write(0);
                BitSet attributes = new BitSet(this.files.size());
                for (int i = 0; i < this.files.size(); i++) {
                    attributes.set(i, ((SevenZArchiveEntry) this.files.get(i)).getHasWindowsAttributes());
                }
                writeBits(out, attributes, this.files.size());
            } else {
                out.write(1);
            }
            out.write(0);
            for (SevenZArchiveEntry entry2 : this.files) {
                if (entry2.getHasWindowsAttributes()) {
                    out.writeInt(Integer.reverseBytes(entry2.getWindowsAttributes()));
                }
            }
            out.flush();
            byte[] contents = baos.toByteArray();
            writeUint64(header, (long) contents.length);
            header.write(contents);
        }
    }

    private void writeUint64(DataOutput header, long value) throws IOException {
        int firstByte = 0;
        int mask = 128;
        int i = 0;
        while (i < 8) {
            if (value < (1 << ((i + 1) * 7))) {
                firstByte = (int) (((long) firstByte) | (value >>> (i * 8)));
                break;
            }
            firstByte |= mask;
            mask >>>= 1;
            i++;
        }
        header.write(firstByte);
        while (i > 0) {
            header.write((int) (255 & value));
            value >>>= 8;
            i--;
        }
    }

    private void writeBits(DataOutput header, BitSet bits, int length) throws IOException {
        int cache = 0;
        int shift = 7;
        for (int i = 0; i < length; i++) {
            cache |= (bits.get(i) ? 1 : 0) << shift;
            shift--;
            if (shift < 0) {
                header.write(cache);
                shift = 7;
                cache = 0;
            }
        }
        if (shift != 7) {
            header.write(cache);
        }
    }

    private static <T> Iterable<T> reverse(Iterable<T> i) {
        LinkedList<T> l = new LinkedList();
        for (T t : i) {
            l.addFirst(t);
        }
        return l;
    }
}
