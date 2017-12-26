package org.apache.commons.compress.archivers.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException.Feature;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.ArchiveUtils;
import org.apache.commons.compress.utils.IOUtils;

public class ZipArchiveInputStream extends ArchiveInputStream {
    private static final byte[] CFH = ZipLong.CFH_SIG.getBytes();
    private static final int CFH_LEN = 46;
    private static final byte[] DD = ZipLong.DD_SIG.getBytes();
    private static final byte[] LFH = ZipLong.LFH_SIG.getBytes();
    private static final int LFH_LEN = 30;
    private static final long TWO_EXP_32 = 4294967296L;
    private boolean allowStoredEntriesWithDataDescriptor;
    private final ByteBuffer buf;
    private boolean closed;
    private CurrentEntry current;
    final String encoding;
    private int entriesRead;
    private boolean hitCentralDirectory;
    private final InputStream in;
    private final Inflater inf;
    private ByteArrayInputStream lastStoredEntry;
    private final byte[] lfhBuf;
    private final byte[] shortBuf;
    private final byte[] skipBuf;
    private final byte[] twoDwordBuf;
    private final boolean useUnicodeExtraFields;
    private final byte[] wordBuf;
    private final ZipEncoding zipEncoding;

    private class BoundedInputStream extends InputStream {
        private final InputStream in;
        private final long max;
        private long pos = 0;

        public BoundedInputStream(InputStream in, long size) {
            this.max = size;
            this.in = in;
        }

        public int read() throws IOException {
            if (this.max >= 0 && this.pos >= this.max) {
                return -1;
            }
            int result = this.in.read();
            this.pos++;
            ZipArchiveInputStream.this.count(1);
            ZipArchiveInputStream.this.current.bytesReadFromStream = 1 + ZipArchiveInputStream.this.current.bytesReadFromStream;
            return result;
        }

        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        public int read(byte[] b, int off, int len) throws IOException {
            if (this.max >= 0 && this.pos >= this.max) {
                return -1;
            }
            int bytesRead = this.in.read(b, off, (int) (this.max >= 0 ? Math.min((long) len, this.max - this.pos) : (long) len));
            if (bytesRead == -1) {
                return -1;
            }
            this.pos += (long) bytesRead;
            ZipArchiveInputStream.this.count(bytesRead);
            CurrentEntry access$900 = ZipArchiveInputStream.this.current;
            access$900.bytesReadFromStream = access$900.bytesReadFromStream + ((long) bytesRead);
            return bytesRead;
        }

        public long skip(long n) throws IOException {
            long toSkip;
            if (this.max >= 0) {
                toSkip = Math.min(n, this.max - this.pos);
            } else {
                toSkip = n;
            }
            long skippedBytes = this.in.skip(toSkip);
            this.pos += skippedBytes;
            return skippedBytes;
        }

        public int available() throws IOException {
            if (this.max < 0 || this.pos < this.max) {
                return this.in.available();
            }
            return 0;
        }
    }

    private static final class CurrentEntry {
        private long bytesRead;
        private long bytesReadFromStream;
        private final CRC32 crc;
        private final ZipArchiveEntry entry;
        private boolean hasDataDescriptor;
        private InputStream in;
        private boolean usesZip64;

        private CurrentEntry() {
            this.entry = new ZipArchiveEntry();
            this.crc = new CRC32();
        }
    }

    public ZipArchiveInputStream(InputStream inputStream) {
        this(inputStream, "UTF8");
    }

    public ZipArchiveInputStream(InputStream inputStream, String encoding) {
        this(inputStream, encoding, true);
    }

    public ZipArchiveInputStream(InputStream inputStream, String encoding, boolean useUnicodeExtraFields) {
        this(inputStream, encoding, useUnicodeExtraFields, false);
    }

    public ZipArchiveInputStream(InputStream inputStream, String encoding, boolean useUnicodeExtraFields, boolean allowStoredEntriesWithDataDescriptor) {
        this.inf = new Inflater(true);
        this.buf = ByteBuffer.allocate(512);
        this.current = null;
        this.closed = false;
        this.hitCentralDirectory = false;
        this.lastStoredEntry = null;
        this.allowStoredEntriesWithDataDescriptor = false;
        this.lfhBuf = new byte[30];
        this.skipBuf = new byte[1024];
        this.shortBuf = new byte[2];
        this.wordBuf = new byte[4];
        this.twoDwordBuf = new byte[16];
        this.entriesRead = 0;
        this.encoding = encoding;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
        this.useUnicodeExtraFields = useUnicodeExtraFields;
        this.in = new PushbackInputStream(inputStream, this.buf.capacity());
        this.allowStoredEntriesWithDataDescriptor = allowStoredEntriesWithDataDescriptor;
        this.buf.limit(0);
    }

    public ZipArchiveEntry getNextZipEntry() throws IOException {
        boolean firstEntry = true;
        if (this.closed || this.hitCentralDirectory) {
            return null;
        }
        if (this.current != null) {
            closeEntry();
            firstEntry = false;
        }
        long currentHeaderOffset = getBytesRead();
        if (firstEntry) {
            try {
                readFirstLocalFileHeader(this.lfhBuf);
            } catch (EOFException e) {
                return null;
            }
        }
        readFully(this.lfhBuf);
        ZipLong zipLong = new ZipLong(this.lfhBuf);
        if (zipLong.equals(ZipLong.CFH_SIG) || zipLong.equals(ZipLong.AED_SIG)) {
            this.hitCentralDirectory = true;
            skipRemainderOfArchive();
            return null;
        } else if (zipLong.equals(ZipLong.LFH_SIG)) {
            ZipEncoding entryEncoding;
            this.current = new CurrentEntry();
            int off = 4 + 2;
            this.current.entry.setPlatform((ZipShort.getValue(this.lfhBuf, 4) >> 8) & 15);
            GeneralPurposeBit gpFlag = GeneralPurposeBit.parse(this.lfhBuf, off);
            boolean hasUTF8Flag = gpFlag.usesUTF8ForNames();
            if (hasUTF8Flag) {
                entryEncoding = ZipEncodingHelper.UTF8_ZIP_ENCODING;
            } else {
                entryEncoding = this.zipEncoding;
            }
            this.current.hasDataDescriptor = gpFlag.usesDataDescriptor();
            this.current.entry.setGeneralPurposeBit(gpFlag);
            off += 2;
            this.current.entry.setMethod(ZipShort.getValue(this.lfhBuf, off));
            off += 2;
            this.current.entry.setTime(ZipUtil.dosToJavaTime(ZipLong.getValue(this.lfhBuf, off)));
            off += 4;
            ZipLong size = null;
            ZipLong cSize = null;
            if (this.current.hasDataDescriptor) {
                off += 12;
            } else {
                this.current.entry.setCrc(ZipLong.getValue(this.lfhBuf, off));
                off += 4;
                cSize = new ZipLong(this.lfhBuf, off);
                off += 4;
                zipLong = new ZipLong(this.lfhBuf, off);
                off += 4;
            }
            int fileNameLen = ZipShort.getValue(this.lfhBuf, off);
            off += 2;
            int extraLen = ZipShort.getValue(this.lfhBuf, off);
            off += 2;
            byte[] fileName = new byte[fileNameLen];
            readFully(fileName);
            this.current.entry.setName(entryEncoding.decode(fileName), fileName);
            byte[] extraData = new byte[extraLen];
            readFully(extraData);
            this.current.entry.setExtra(extraData);
            if (!hasUTF8Flag && this.useUnicodeExtraFields) {
                ZipUtil.setNameAndCommentFromExtraFields(this.current.entry, fileName, null);
            }
            processZip64Extra(size, cSize);
            this.current.entry.setLocalHeaderOffset(currentHeaderOffset);
            this.current.entry.setDataOffset(getBytesRead());
            this.current.entry.setStreamContiguous(true);
            if (this.current.entry.getCompressedSize() != -1) {
                if (this.current.entry.getMethod() == ZipMethod.UNSHRINKING.getCode()) {
                    this.current.in = new UnshrinkingInputStream(new BoundedInputStream(this.in, this.current.entry.getCompressedSize()));
                } else {
                    if (this.current.entry.getMethod() == ZipMethod.IMPLODING.getCode()) {
                        this.current.in = new ExplodingInputStream(this.current.entry.getGeneralPurposeBit().getSlidingDictionarySize(), this.current.entry.getGeneralPurposeBit().getNumberOfShannonFanoTrees(), new BoundedInputStream(this.in, this.current.entry.getCompressedSize()));
                    } else {
                        if (this.current.entry.getMethod() == ZipMethod.BZIP2.getCode()) {
                            this.current.in = new BZip2CompressorInputStream(new BoundedInputStream(this.in, this.current.entry.getCompressedSize()));
                        }
                    }
                }
            }
            this.entriesRead++;
            return this.current.entry;
        } else {
            throw new ZipException(String.format("Unexpected record signature: 0X%X", new Object[]{Long.valueOf(zipLong.getValue())}));
        }
    }

    private void readFirstLocalFileHeader(byte[] lfh) throws IOException {
        readFully(lfh);
        ZipLong sig = new ZipLong(lfh);
        if (sig.equals(ZipLong.DD_SIG)) {
            throw new UnsupportedZipFeatureException(Feature.SPLITTING);
        } else if (sig.equals(ZipLong.SINGLE_SEGMENT_SPLIT_MARKER)) {
            byte[] missedLfhBytes = new byte[4];
            readFully(missedLfhBytes);
            System.arraycopy(lfh, 4, lfh, 0, 26);
            System.arraycopy(missedLfhBytes, 0, lfh, 26, 4);
        }
    }

    private void processZip64Extra(ZipLong size, ZipLong cSize) {
        Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField) this.current.entry.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
        this.current.usesZip64 = z64 != null;
        if (!this.current.hasDataDescriptor) {
            if (z64 == null || !(cSize.equals(ZipLong.ZIP64_MAGIC) || size.equals(ZipLong.ZIP64_MAGIC))) {
                this.current.entry.setCompressedSize(cSize.getValue());
                this.current.entry.setSize(size.getValue());
                return;
            }
            this.current.entry.setCompressedSize(z64.getCompressedSize().getLongValue());
            this.current.entry.setSize(z64.getSize().getLongValue());
        }
    }

    public ArchiveEntry getNextEntry() throws IOException {
        return getNextZipEntry();
    }

    public boolean canReadEntryData(ArchiveEntry ae) {
        if (!(ae instanceof ZipArchiveEntry)) {
            return false;
        }
        ZipArchiveEntry ze = (ZipArchiveEntry) ae;
        if (ZipUtil.canHandleEntryData(ze) && supportsDataDescriptorFor(ze)) {
            return true;
        }
        return false;
    }

    public int read(byte[] buffer, int offset, int length) throws IOException {
        if (this.closed) {
            throw new IOException("The stream is closed");
        } else if (this.current == null) {
            return -1;
        } else {
            if (offset > buffer.length || length < 0 || offset < 0 || buffer.length - offset < length) {
                throw new ArrayIndexOutOfBoundsException();
            }
            ZipUtil.checkRequestedFeatures(this.current.entry);
            if (supportsDataDescriptorFor(this.current.entry)) {
                int read;
                if (this.current.entry.getMethod() == 0) {
                    read = readStored(buffer, offset, length);
                } else if (this.current.entry.getMethod() == 8) {
                    read = readDeflated(buffer, offset, length);
                } else if (this.current.entry.getMethod() == ZipMethod.UNSHRINKING.getCode() || this.current.entry.getMethod() == ZipMethod.IMPLODING.getCode() || this.current.entry.getMethod() == ZipMethod.BZIP2.getCode()) {
                    read = this.current.in.read(buffer, offset, length);
                } else {
                    throw new UnsupportedZipFeatureException(ZipMethod.getMethodByCode(this.current.entry.getMethod()), this.current.entry);
                }
                if (read < 0) {
                    return read;
                }
                this.current.crc.update(buffer, offset, read);
                return read;
            }
            throw new UnsupportedZipFeatureException(Feature.DATA_DESCRIPTOR, this.current.entry);
        }
    }

    private int readStored(byte[] buffer, int offset, int length) throws IOException {
        if (this.current.hasDataDescriptor) {
            if (this.lastStoredEntry == null) {
                readStoredEntry();
            }
            return this.lastStoredEntry.read(buffer, offset, length);
        }
        long csize = this.current.entry.getSize();
        if (this.current.bytesRead >= csize) {
            return -1;
        }
        CurrentEntry currentEntry;
        if (this.buf.position() >= this.buf.limit()) {
            this.buf.position(0);
            int l = this.in.read(this.buf.array());
            if (l == -1) {
                return -1;
            }
            this.buf.limit(l);
            count(l);
            currentEntry = this.current;
            currentEntry.bytesReadFromStream = currentEntry.bytesReadFromStream + ((long) l);
        }
        int toRead = Math.min(this.buf.remaining(), length);
        if (csize - this.current.bytesRead < ((long) toRead)) {
            toRead = (int) (csize - this.current.bytesRead);
        }
        this.buf.get(buffer, offset, toRead);
        currentEntry = this.current;
        currentEntry.bytesRead = currentEntry.bytesRead + ((long) toRead);
        return toRead;
    }

    private int readDeflated(byte[] buffer, int offset, int length) throws IOException {
        int read = readFromInflater(buffer, offset, length);
        if (read > 0) {
            return read;
        }
        if (this.inf.finished()) {
            return -1;
        }
        if (this.inf.needsDictionary()) {
            throw new ZipException("This archive needs a preset dictionary which is not supported by Commons Compress.");
        } else if (read != -1) {
            return read;
        } else {
            throw new IOException("Truncated ZIP file");
        }
    }

    private int readFromInflater(byte[] buffer, int offset, int length) throws IOException {
        int read = 0;
        do {
            if (this.inf.needsInput()) {
                int l = fill();
                if (l > 0) {
                    CurrentEntry currentEntry = this.current;
                    currentEntry.bytesReadFromStream = currentEntry.bytesReadFromStream + ((long) this.buf.limit());
                } else if (l == -1) {
                    return -1;
                }
            }
            try {
                read = this.inf.inflate(buffer, offset, length);
                if (read != 0) {
                    break;
                }
            } catch (DataFormatException e) {
                throw ((IOException) new ZipException(e.getMessage()).initCause(e));
            }
        } while (this.inf.needsInput());
        return read;
    }

    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            try {
                this.in.close();
            } finally {
                this.inf.end();
            }
        }
    }

    public long skip(long value) throws IOException {
        if (value >= 0) {
            long skipped = 0;
            while (skipped < value) {
                long rem = value - skipped;
                byte[] bArr = this.skipBuf;
                if (((long) this.skipBuf.length) <= rem) {
                    rem = (long) this.skipBuf.length;
                }
                int x = read(bArr, 0, (int) rem);
                if (x == -1) {
                    break;
                }
                skipped += (long) x;
            }
            return skipped;
        }
        throw new IllegalArgumentException();
    }

    public static boolean matches(byte[] signature, int length) {
        if (length < ZipArchiveOutputStream.LFH_SIG.length) {
            return false;
        }
        if (checksig(signature, ZipArchiveOutputStream.LFH_SIG) || checksig(signature, ZipArchiveOutputStream.EOCD_SIG) || checksig(signature, ZipArchiveOutputStream.DD_SIG) || checksig(signature, ZipLong.SINGLE_SEGMENT_SPLIT_MARKER.getBytes())) {
            return true;
        }
        return false;
    }

    private static boolean checksig(byte[] signature, byte[] expected) {
        for (int i = 0; i < expected.length; i++) {
            if (signature[i] != expected[i]) {
                return false;
            }
        }
        return true;
    }

    private void closeEntry() throws IOException {
        if (this.closed) {
            throw new IOException("The stream is closed");
        } else if (this.current != null) {
            if (currentEntryHasOutstandingBytes()) {
                drainCurrentEntryData();
            } else {
                skip(Long.MAX_VALUE);
                int diff = (int) (this.current.bytesReadFromStream - (this.current.entry.getMethod() == 8 ? getBytesInflated() : this.current.bytesRead));
                if (diff > 0) {
                    pushback(this.buf.array(), this.buf.limit() - diff, diff);
                    CurrentEntry currentEntry = this.current;
                    currentEntry.bytesReadFromStream = currentEntry.bytesReadFromStream - ((long) diff);
                }
                if (currentEntryHasOutstandingBytes()) {
                    drainCurrentEntryData();
                }
            }
            if (this.lastStoredEntry == null && this.current.hasDataDescriptor) {
                readDataDescriptor();
            }
            this.inf.reset();
            this.buf.clear().flip();
            this.current = null;
            this.lastStoredEntry = null;
        }
    }

    private boolean currentEntryHasOutstandingBytes() {
        return this.current.bytesReadFromStream <= this.current.entry.getCompressedSize() && !this.current.hasDataDescriptor;
    }

    private void drainCurrentEntryData() throws IOException {
        long remaining = this.current.entry.getCompressedSize() - this.current.bytesReadFromStream;
        while (remaining > 0) {
            long n = (long) this.in.read(this.buf.array(), 0, (int) Math.min((long) this.buf.capacity(), remaining));
            if (n < 0) {
                throw new EOFException("Truncated ZIP entry: " + ArchiveUtils.sanitize(this.current.entry.getName()));
            }
            count(n);
            remaining -= n;
        }
    }

    private long getBytesInflated() {
        long inB = this.inf.getBytesRead();
        if (this.current.bytesReadFromStream >= TWO_EXP_32) {
            while (inB + TWO_EXP_32 <= this.current.bytesReadFromStream) {
                inB += TWO_EXP_32;
            }
        }
        return inB;
    }

    private int fill() throws IOException {
        if (this.closed) {
            throw new IOException("The stream is closed");
        }
        int length = this.in.read(this.buf.array());
        if (length > 0) {
            this.buf.limit(length);
            count(this.buf.limit());
            this.inf.setInput(this.buf.array(), 0, this.buf.limit());
        }
        return length;
    }

    private void readFully(byte[] b) throws IOException {
        int count = IOUtils.readFully(this.in, b);
        count(count);
        if (count < b.length) {
            throw new EOFException();
        }
    }

    private void readDataDescriptor() throws IOException {
        readFully(this.wordBuf);
        ZipLong val = new ZipLong(this.wordBuf);
        if (ZipLong.DD_SIG.equals(val)) {
            readFully(this.wordBuf);
            val = new ZipLong(this.wordBuf);
        }
        this.current.entry.setCrc(val.getValue());
        readFully(this.twoDwordBuf);
        ZipLong potentialSig = new ZipLong(this.twoDwordBuf, 8);
        if (potentialSig.equals(ZipLong.CFH_SIG) || potentialSig.equals(ZipLong.LFH_SIG)) {
            pushback(this.twoDwordBuf, 8, 8);
            this.current.entry.setCompressedSize(ZipLong.getValue(this.twoDwordBuf));
            this.current.entry.setSize(ZipLong.getValue(this.twoDwordBuf, 4));
            return;
        }
        this.current.entry.setCompressedSize(ZipEightByteInteger.getLongValue(this.twoDwordBuf));
        this.current.entry.setSize(ZipEightByteInteger.getLongValue(this.twoDwordBuf, 8));
    }

    private boolean supportsDataDescriptorFor(ZipArchiveEntry entry) {
        return !entry.getGeneralPurposeBit().usesDataDescriptor() || ((this.allowStoredEntriesWithDataDescriptor && entry.getMethod() == 0) || entry.getMethod() == 8);
    }

    private void readStoredEntry() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int off = 0;
        boolean done = false;
        int ddLen = this.current.usesZip64 ? 20 : 12;
        while (!done) {
            int r = this.in.read(this.buf.array(), off, 512 - off);
            if (r <= 0) {
                throw new IOException("Truncated ZIP file");
            } else if (r + off < 4) {
                off += r;
            } else {
                done = bufferContainsSignature(bos, off, r, ddLen);
                if (!done) {
                    off = cacheBytesRead(bos, off, r, ddLen);
                }
            }
        }
        this.lastStoredEntry = new ByteArrayInputStream(bos.toByteArray());
    }

    private boolean bufferContainsSignature(ByteArrayOutputStream bos, int offset, int lastRead, int expectedDDLen) throws IOException {
        boolean done = false;
        int readTooMuch = 0;
        int i = 0;
        while (!done && i < lastRead - 4) {
            if (this.buf.array()[i] == LFH[0] && this.buf.array()[i + 1] == LFH[1]) {
                if ((this.buf.array()[i + 2] == LFH[2] && this.buf.array()[i + 3] == LFH[3]) || (this.buf.array()[i] == CFH[2] && this.buf.array()[i + 3] == CFH[3])) {
                    readTooMuch = ((offset + lastRead) - i) - expectedDDLen;
                    done = true;
                } else if (this.buf.array()[i + 2] == DD[2] && this.buf.array()[i + 3] == DD[3]) {
                    readTooMuch = (offset + lastRead) - i;
                    done = true;
                }
                if (done) {
                    pushback(this.buf.array(), (offset + lastRead) - readTooMuch, readTooMuch);
                    bos.write(this.buf.array(), 0, i);
                    readDataDescriptor();
                }
            }
            i++;
        }
        return done;
    }

    private int cacheBytesRead(ByteArrayOutputStream bos, int offset, int lastRead, int expecteDDLen) {
        int cacheable = ((offset + lastRead) - expecteDDLen) - 3;
        if (cacheable <= 0) {
            return offset + lastRead;
        }
        bos.write(this.buf.array(), 0, cacheable);
        System.arraycopy(this.buf.array(), cacheable, this.buf.array(), 0, expecteDDLen + 3);
        return expecteDDLen + 3;
    }

    private void pushback(byte[] buf, int offset, int length) throws IOException {
        ((PushbackInputStream) this.in).unread(buf, offset, length);
        pushedBackBytes((long) length);
    }

    private void skipRemainderOfArchive() throws IOException {
        realSkip((((long) this.entriesRead) * 46) - 30);
        findEocdRecord();
        realSkip(16);
        readFully(this.shortBuf);
        realSkip((long) ZipShort.getValue(this.shortBuf));
    }

    private void findEocdRecord() throws IOException {
        int currentByte = -1;
        boolean skipReadCall = false;
        while (true) {
            if (!skipReadCall) {
                currentByte = readOneByte();
                if (currentByte <= -1) {
                    return;
                }
            }
            skipReadCall = false;
            if (isFirstByteOfEocdSig(currentByte)) {
                currentByte = readOneByte();
                if (currentByte == ZipArchiveOutputStream.EOCD_SIG[1]) {
                    currentByte = readOneByte();
                    if (currentByte == ZipArchiveOutputStream.EOCD_SIG[2]) {
                        currentByte = readOneByte();
                        if (currentByte != -1 && currentByte != ZipArchiveOutputStream.EOCD_SIG[3]) {
                            skipReadCall = isFirstByteOfEocdSig(currentByte);
                        } else {
                            return;
                        }
                    } else if (currentByte != (byte) -1) {
                        skipReadCall = isFirstByteOfEocdSig(currentByte);
                    } else {
                        return;
                    }
                } else if (currentByte != (byte) -1) {
                    skipReadCall = isFirstByteOfEocdSig(currentByte);
                } else {
                    return;
                }
            }
        }
    }

    private void realSkip(long value) throws IOException {
        if (value >= 0) {
            long skipped = 0;
            while (skipped < value) {
                long rem = value - skipped;
                InputStream inputStream = this.in;
                byte[] bArr = this.skipBuf;
                if (((long) this.skipBuf.length) <= rem) {
                    rem = (long) this.skipBuf.length;
                }
                int x = inputStream.read(bArr, 0, (int) rem);
                if (x != -1) {
                    count(x);
                    skipped += (long) x;
                } else {
                    return;
                }
            }
            return;
        }
        throw new IllegalArgumentException();
    }

    private int readOneByte() throws IOException {
        int b = this.in.read();
        if (b != -1) {
            count(1);
        }
        return b;
    }

    private boolean isFirstByteOfEocdSig(int b) {
        return b == ZipArchiveOutputStream.EOCD_SIG[0];
    }
}
