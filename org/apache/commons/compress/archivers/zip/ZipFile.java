package org.apache.commons.compress.archivers.zip;

import android.support.v4.internal.view.SupportMenu;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class ZipFile implements Closeable {
    static final int BYTE_SHIFT = 8;
    private static final int CFD_LOCATOR_OFFSET = 16;
    private static final int CFH_LEN = 42;
    private static final long CFH_SIG = ZipLong.getValue(ZipArchiveOutputStream.CFH_SIG);
    private static final int HASH_SIZE = 509;
    private static final long LFH_OFFSET_FOR_FILENAME_LENGTH = 26;
    private static final int MAX_EOCD_SIZE = 65557;
    static final int MIN_EOCD_SIZE = 22;
    static final int NIBLET_MASK = 15;
    private static final int POS_0 = 0;
    private static final int POS_1 = 1;
    private static final int POS_2 = 2;
    private static final int POS_3 = 3;
    private static final int ZIP64_EOCDL_LENGTH = 20;
    private static final int ZIP64_EOCDL_LOCATOR_OFFSET = 8;
    private static final int ZIP64_EOCD_CFD_LOCATOR_OFFSET = 48;
    private final SeekableByteChannel archive;
    private final String archiveName;
    private final ByteBuffer cfhBbuf;
    private final byte[] cfhBuf;
    private volatile boolean closed;
    private final ByteBuffer dwordBbuf;
    private final byte[] dwordBuf;
    private final String encoding;
    private final List<ZipArchiveEntry> entries;
    private final Map<String, LinkedList<ZipArchiveEntry>> nameMap;
    private final Comparator<ZipArchiveEntry> offsetComparator;
    private final byte[] shortBuf;
    private final boolean useUnicodeExtraFields;
    private final ByteBuffer wordBbuf;
    private final byte[] wordBuf;
    private final ZipEncoding zipEncoding;

    class C07992 implements Comparator<ZipArchiveEntry> {
        C07992() {
        }

        public int compare(ZipArchiveEntry e1, ZipArchiveEntry e2) {
            if (e1 == e2) {
                return 0;
            }
            Entry ent1 = e1 instanceof Entry ? (Entry) e1 : null;
            Entry ent2 = e2 instanceof Entry ? (Entry) e2 : null;
            if (ent1 == null) {
                return 1;
            }
            if (ent2 == null) {
                return -1;
            }
            long val = ent1.getLocalHeaderOffset() - ent2.getLocalHeaderOffset();
            if (val == 0) {
                return 0;
            }
            return val < 0 ? -1 : 1;
        }
    }

    private class BoundedInputStream extends InputStream {
        private boolean addDummy = false;
        private final long end;
        private long loc;
        private ByteBuffer singleByteBuffer;

        BoundedInputStream(long start, long remaining) {
            this.end = start + remaining;
            if (this.end < start) {
                throw new IllegalArgumentException("Invalid length of stream at offset=" + start + ", length=" + remaining);
            }
            this.loc = start;
        }

        public synchronized int read() throws IOException {
            int i = 0;
            synchronized (this) {
                if (this.loc < this.end) {
                    if (this.singleByteBuffer == null) {
                        this.singleByteBuffer = ByteBuffer.allocate(1);
                    } else {
                        this.singleByteBuffer.rewind();
                    }
                    i = read(this.loc, this.singleByteBuffer);
                    if (i >= 0) {
                        this.loc++;
                        i = this.singleByteBuffer.get() & 255;
                    }
                } else if (this.loc == this.end && this.addDummy) {
                    this.addDummy = false;
                } else {
                    i = -1;
                }
            }
            return i;
        }

        public synchronized int read(byte[] b, int off, int len) throws IOException {
            int i = 0;
            synchronized (this) {
                if (len > 0) {
                    if (((long) len) > this.end - this.loc) {
                        if (this.loc < this.end) {
                            len = (int) (this.end - this.loc);
                        } else if (this.loc == this.end && this.addDummy) {
                            this.addDummy = false;
                            b[off] = (byte) 0;
                            i = 1;
                        } else {
                            i = -1;
                        }
                    }
                    i = read(this.loc, ByteBuffer.wrap(b, off, len));
                    if (i > 0) {
                        this.loc += (long) i;
                    }
                }
            }
            return i;
        }

        protected int read(long pos, ByteBuffer buf) throws IOException {
            int read;
            synchronized (ZipFile.this.archive) {
                ZipFile.this.archive.position(pos);
                read = ZipFile.this.archive.read(buf);
            }
            buf.flip();
            return read;
        }

        synchronized void addDummy() {
            this.addDummy = true;
        }
    }

    private static final class NameAndComment {
        private final byte[] comment;
        private final byte[] name;

        private NameAndComment(byte[] name, byte[] comment) {
            this.name = name;
            this.comment = comment;
        }
    }

    private class BoundedFileChannelInputStream extends BoundedInputStream {
        private final FileChannel archive;

        BoundedFileChannelInputStream(long start, long remaining) {
            super(start, remaining);
            this.archive = (FileChannel) ZipFile.this.archive;
        }

        protected int read(long pos, ByteBuffer buf) throws IOException {
            int read = this.archive.read(buf, pos);
            buf.flip();
            return read;
        }
    }

    private static class Entry extends ZipArchiveEntry {
        Entry() {
        }

        public int hashCode() {
            return ((super.hashCode() * 3) + ((int) getLocalHeaderOffset())) + ((int) (getLocalHeaderOffset() >> 32));
        }

        public boolean equals(Object other) {
            if (!super.equals(other)) {
                return false;
            }
            Entry otherEntry = (Entry) other;
            if (getLocalHeaderOffset() == otherEntry.getLocalHeaderOffset() && getDataOffset() == otherEntry.getDataOffset()) {
                return true;
            }
            return false;
        }
    }

    public ZipFile(File f) throws IOException {
        this(f, "UTF8");
    }

    public ZipFile(String name) throws IOException {
        this(new File(name), "UTF8");
    }

    public ZipFile(String name, String encoding) throws IOException {
        this(new File(name), encoding, true);
    }

    public ZipFile(File f, String encoding) throws IOException {
        this(f, encoding, true);
    }

    public ZipFile(File f, String encoding, boolean useUnicodeExtraFields) throws IOException {
        this(Files.newByteChannel(f.toPath(), EnumSet.of(StandardOpenOption.READ), new FileAttribute[0]), f.getAbsolutePath(), encoding, useUnicodeExtraFields, true);
    }

    public ZipFile(SeekableByteChannel channel) throws IOException {
        this(channel, "unknown archive", "UTF8", true);
    }

    public ZipFile(SeekableByteChannel channel, String encoding) throws IOException {
        this(channel, "unknown archive", encoding, true);
    }

    public ZipFile(SeekableByteChannel channel, String archiveName, String encoding, boolean useUnicodeExtraFields) throws IOException {
        this(channel, archiveName, encoding, useUnicodeExtraFields, false);
    }

    private ZipFile(SeekableByteChannel channel, String archiveName, String encoding, boolean useUnicodeExtraFields, boolean closeOnError) throws IOException {
        boolean z = true;
        this.entries = new LinkedList();
        this.nameMap = new HashMap(HASH_SIZE);
        this.closed = true;
        this.dwordBuf = new byte[8];
        this.wordBuf = new byte[4];
        this.cfhBuf = new byte[42];
        this.shortBuf = new byte[2];
        this.dwordBbuf = ByteBuffer.wrap(this.dwordBuf);
        this.wordBbuf = ByteBuffer.wrap(this.wordBuf);
        this.cfhBbuf = ByteBuffer.wrap(this.cfhBuf);
        this.offsetComparator = new C07992();
        this.archiveName = archiveName;
        this.encoding = encoding;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
        this.useUnicodeExtraFields = useUnicodeExtraFields;
        this.archive = channel;
        boolean success = false;
        try {
            resolveLocalFileHeaderData(populateFromCentralDirectory());
            success = true;
        } finally {
            if (success) {
                z = false;
            }
            this.closed = z;
            if (!success && closeOnError) {
                IOUtils.closeQuietly(this.archive);
            }
        }
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void close() throws IOException {
        this.closed = true;
        this.archive.close();
    }

    public static void closeQuietly(ZipFile zipfile) {
        IOUtils.closeQuietly(zipfile);
    }

    public Enumeration<ZipArchiveEntry> getEntries() {
        return Collections.enumeration(this.entries);
    }

    public Enumeration<ZipArchiveEntry> getEntriesInPhysicalOrder() {
        ZipArchiveEntry[] allEntries = (ZipArchiveEntry[]) this.entries.toArray(new ZipArchiveEntry[this.entries.size()]);
        Arrays.sort(allEntries, this.offsetComparator);
        return Collections.enumeration(Arrays.asList(allEntries));
    }

    public ZipArchiveEntry getEntry(String name) {
        LinkedList<ZipArchiveEntry> entriesOfThatName = (LinkedList) this.nameMap.get(name);
        return entriesOfThatName != null ? (ZipArchiveEntry) entriesOfThatName.getFirst() : null;
    }

    public Iterable<ZipArchiveEntry> getEntries(String name) {
        List<ZipArchiveEntry> entriesOfThatName = (List) this.nameMap.get(name);
        if (entriesOfThatName != null) {
            return entriesOfThatName;
        }
        return Collections.emptyList();
    }

    public Iterable<ZipArchiveEntry> getEntriesInPhysicalOrder(String name) {
        ZipArchiveEntry[] entriesOfThatName = new ZipArchiveEntry[0];
        if (this.nameMap.containsKey(name)) {
            entriesOfThatName = (ZipArchiveEntry[]) ((LinkedList) this.nameMap.get(name)).toArray(entriesOfThatName);
            Arrays.sort(entriesOfThatName, this.offsetComparator);
        }
        return Arrays.asList(entriesOfThatName);
    }

    public boolean canReadEntryData(ZipArchiveEntry ze) {
        return ZipUtil.canHandleEntryData(ze);
    }

    public InputStream getRawInputStream(ZipArchiveEntry ze) {
        if (ze instanceof Entry) {
            return createBoundedInputStream(ze.getDataOffset(), ze.getCompressedSize());
        }
        return null;
    }

    public void copyRawEntries(ZipArchiveOutputStream target, ZipArchiveEntryPredicate predicate) throws IOException {
        Enumeration<ZipArchiveEntry> src = getEntriesInPhysicalOrder();
        while (src.hasMoreElements()) {
            ZipArchiveEntry entry = (ZipArchiveEntry) src.nextElement();
            if (predicate.test(entry)) {
                target.addRawArchiveEntry(entry, getRawInputStream(entry));
            }
        }
    }

    public InputStream getInputStream(ZipArchiveEntry ze) throws IOException, ZipException {
        if (!(ze instanceof Entry)) {
            return null;
        }
        ZipUtil.checkRequestedFeatures(ze);
        InputStream bis = createBoundedInputStream(ze.getDataOffset(), ze.getCompressedSize());
        switch (ZipMethod.getMethodByCode(ze.getMethod())) {
            case STORED:
                return bis;
            case UNSHRINKING:
                return new UnshrinkingInputStream(bis);
            case IMPLODING:
                return new ExplodingInputStream(ze.getGeneralPurposeBit().getSlidingDictionarySize(), ze.getGeneralPurposeBit().getNumberOfShannonFanoTrees(), new BufferedInputStream(bis));
            case DEFLATED:
                bis.addDummy();
                final Inflater inflater = new Inflater(true);
                return new InflaterInputStream(bis, inflater) {
                    public void close() throws IOException {
                        try {
                            super.close();
                        } finally {
                            inflater.end();
                        }
                    }
                };
            case BZIP2:
                return new BZip2CompressorInputStream(bis);
            default:
                throw new ZipException("Found unsupported compression method " + ze.getMethod());
        }
    }

    public String getUnixSymlink(ZipArchiveEntry entry) throws IOException {
        Throwable th;
        Throwable th2 = null;
        if (entry == null || !entry.isUnixSymlink()) {
            return th2;
        }
        InputStream in = getInputStream(entry);
        try {
            String decode = this.zipEncoding.decode(IOUtils.toByteArray(in));
            if (in == null) {
                return decode;
            }
            if (th2 != null) {
                try {
                    in.close();
                    return decode;
                } catch (Throwable th3) {
                    th2.addSuppressed(th3);
                    return decode;
                }
            }
            in.close();
            return decode;
        } catch (Throwable th22) {
            Throwable th4 = th22;
            th22 = th;
            th = th4;
        }
        throw th;
        if (in != null) {
            if (th22 != null) {
                try {
                    in.close();
                } catch (Throwable th32) {
                    th22.addSuppressed(th32);
                }
            } else {
                in.close();
            }
        }
        throw th;
    }

    protected void finalize() throws Throwable {
        try {
            if (!this.closed) {
                System.err.println("Cleaning up unclosed ZipFile for archive " + this.archiveName);
                close();
            }
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
        }
    }

    private Map<ZipArchiveEntry, NameAndComment> populateFromCentralDirectory() throws IOException {
        HashMap<ZipArchiveEntry, NameAndComment> noUTF8Flag = new HashMap();
        positionAtCentralDirectory();
        this.wordBbuf.rewind();
        IOUtils.readFully(this.archive, this.wordBbuf);
        long sig = ZipLong.getValue(this.wordBuf);
        if (sig == CFH_SIG || !startsWithLocalFileHeader()) {
            while (sig == CFH_SIG) {
                readCentralDirectoryEntry(noUTF8Flag);
                this.wordBbuf.rewind();
                IOUtils.readFully(this.archive, this.wordBbuf);
                sig = ZipLong.getValue(this.wordBuf);
            }
            return noUTF8Flag;
        }
        throw new IOException("central directory is empty, can't expand corrupt archive.");
    }

    private void readCentralDirectoryEntry(Map<ZipArchiveEntry, NameAndComment> noUTF8Flag) throws IOException {
        this.cfhBbuf.rewind();
        IOUtils.readFully((ReadableByteChannel) this.archive, this.cfhBbuf);
        ZipArchiveEntry ze = new Entry();
        int versionMadeBy = ZipShort.getValue(this.cfhBuf, 0);
        int off = 0 + 2;
        ze.setVersionMadeBy(versionMadeBy);
        ze.setPlatform((versionMadeBy >> 8) & 15);
        ze.setVersionRequired(ZipShort.getValue(this.cfhBuf, off));
        off += 2;
        GeneralPurposeBit gpFlag = GeneralPurposeBit.parse(this.cfhBuf, off);
        boolean hasUTF8Flag = gpFlag.usesUTF8ForNames();
        ZipEncoding entryEncoding = hasUTF8Flag ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
        ze.setGeneralPurposeBit(gpFlag);
        ze.setRawFlag(ZipShort.getValue(this.cfhBuf, off));
        off += 2;
        ze.setMethod(ZipShort.getValue(this.cfhBuf, off));
        off += 2;
        ze.setTime(ZipUtil.dosToJavaTime(ZipLong.getValue(this.cfhBuf, off)));
        off += 4;
        ze.setCrc(ZipLong.getValue(this.cfhBuf, off));
        off += 4;
        ze.setCompressedSize(ZipLong.getValue(this.cfhBuf, off));
        off += 4;
        ze.setSize(ZipLong.getValue(this.cfhBuf, off));
        off += 4;
        int fileNameLen = ZipShort.getValue(this.cfhBuf, off);
        off += 2;
        int extraLen = ZipShort.getValue(this.cfhBuf, off);
        off += 2;
        int commentLen = ZipShort.getValue(this.cfhBuf, off);
        off += 2;
        int diskStart = ZipShort.getValue(this.cfhBuf, off);
        off += 2;
        ze.setInternalAttributes(ZipShort.getValue(this.cfhBuf, off));
        off += 2;
        ze.setExternalAttributes(ZipLong.getValue(this.cfhBuf, off));
        off += 4;
        byte[] fileName = new byte[fileNameLen];
        IOUtils.readFully((ReadableByteChannel) this.archive, ByteBuffer.wrap(fileName));
        ze.setName(entryEncoding.decode(fileName), fileName);
        ze.setLocalHeaderOffset(ZipLong.getValue(this.cfhBuf, off));
        this.entries.add(ze);
        byte[] cdExtraData = new byte[extraLen];
        IOUtils.readFully((ReadableByteChannel) this.archive, ByteBuffer.wrap(cdExtraData));
        ze.setCentralDirectoryExtra(cdExtraData);
        setSizesAndOffsetFromZip64Extra(ze, diskStart);
        byte[] comment = new byte[commentLen];
        IOUtils.readFully((ReadableByteChannel) this.archive, ByteBuffer.wrap(comment));
        ze.setComment(entryEncoding.decode(comment));
        if (!hasUTF8Flag && this.useUnicodeExtraFields) {
            noUTF8Flag.put(ze, new NameAndComment(fileName, comment));
        }
    }

    private void setSizesAndOffsetFromZip64Extra(ZipArchiveEntry ze, int diskStart) throws IOException {
        boolean z = true;
        Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField) ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
        if (z64 != null) {
            boolean hasCompressedSize;
            boolean hasRelativeHeaderOffset;
            boolean hasUncompressedSize = ze.getSize() == 4294967295L;
            if (ze.getCompressedSize() == 4294967295L) {
                hasCompressedSize = true;
            } else {
                hasCompressedSize = false;
            }
            if (ze.getLocalHeaderOffset() == 4294967295L) {
                hasRelativeHeaderOffset = true;
            } else {
                hasRelativeHeaderOffset = false;
            }
            if (diskStart != SupportMenu.USER_MASK) {
                z = false;
            }
            z64.reparseCentralDirectoryData(hasUncompressedSize, hasCompressedSize, hasRelativeHeaderOffset, z);
            if (hasUncompressedSize) {
                ze.setSize(z64.getSize().getLongValue());
            } else if (hasCompressedSize) {
                z64.setSize(new ZipEightByteInteger(ze.getSize()));
            }
            if (hasCompressedSize) {
                ze.setCompressedSize(z64.getCompressedSize().getLongValue());
            } else if (hasUncompressedSize) {
                z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
            }
            if (hasRelativeHeaderOffset) {
                ze.setLocalHeaderOffset(z64.getRelativeHeaderOffset().getLongValue());
            }
        }
    }

    private void positionAtCentralDirectory() throws IOException {
        positionAtEndOfCentralDirectoryRecord();
        boolean found = false;
        boolean searchedForZip64EOCD = this.archive.position() > 20;
        if (searchedForZip64EOCD) {
            this.archive.position(this.archive.position() - 20);
            this.wordBbuf.rewind();
            IOUtils.readFully(this.archive, this.wordBbuf);
            found = Arrays.equals(ZipArchiveOutputStream.ZIP64_EOCD_LOC_SIG, this.wordBuf);
        }
        if (found) {
            positionAtCentralDirectory64();
            return;
        }
        if (searchedForZip64EOCD) {
            skipBytes(16);
        }
        positionAtCentralDirectory32();
    }

    private void positionAtCentralDirectory64() throws IOException {
        skipBytes(4);
        this.dwordBbuf.rewind();
        IOUtils.readFully(this.archive, this.dwordBbuf);
        this.archive.position(ZipEightByteInteger.getLongValue(this.dwordBuf));
        this.wordBbuf.rewind();
        IOUtils.readFully(this.archive, this.wordBbuf);
        if (Arrays.equals(this.wordBuf, ZipArchiveOutputStream.ZIP64_EOCD_SIG)) {
            skipBytes(44);
            this.dwordBbuf.rewind();
            IOUtils.readFully(this.archive, this.dwordBbuf);
            this.archive.position(ZipEightByteInteger.getLongValue(this.dwordBuf));
            return;
        }
        throw new ZipException("archive's ZIP64 end of central directory locator is corrupt.");
    }

    private void positionAtCentralDirectory32() throws IOException {
        skipBytes(16);
        this.wordBbuf.rewind();
        IOUtils.readFully(this.archive, this.wordBbuf);
        this.archive.position(ZipLong.getValue(this.wordBuf));
    }

    private void positionAtEndOfCentralDirectoryRecord() throws IOException {
        if (!tryToLocateSignature(22, 65557, ZipArchiveOutputStream.EOCD_SIG)) {
            throw new ZipException("archive is not a ZIP archive");
        }
    }

    private boolean tryToLocateSignature(long minDistanceFromEnd, long maxDistanceFromEnd, byte[] sig) throws IOException {
        boolean found = false;
        long off = this.archive.size() - minDistanceFromEnd;
        long stopSearching = Math.max(0, this.archive.size() - maxDistanceFromEnd);
        if (off >= 0) {
            while (off >= stopSearching) {
                this.archive.position(off);
                try {
                    this.wordBbuf.rewind();
                    IOUtils.readFully(this.archive, this.wordBbuf);
                    this.wordBbuf.flip();
                    if (this.wordBbuf.get() == sig[0] && this.wordBbuf.get() == sig[1] && this.wordBbuf.get() == sig[2] && this.wordBbuf.get() == sig[3]) {
                        found = true;
                        break;
                    }
                    off--;
                } catch (EOFException e) {
                }
            }
        }
        if (found) {
            this.archive.position(off);
        }
        return found;
    }

    private void skipBytes(int count) throws IOException {
        long newPosition = this.archive.position() + ((long) count);
        if (newPosition > this.archive.size()) {
            throw new EOFException();
        }
        this.archive.position(newPosition);
    }

    private void resolveLocalFileHeaderData(Map<ZipArchiveEntry, NameAndComment> entriesWithoutUTF8Flag) throws IOException {
        for (ZipArchiveEntry zipArchiveEntry : this.entries) {
            Entry ze = (Entry) zipArchiveEntry;
            long offset = ze.getLocalHeaderOffset();
            this.archive.position(LFH_OFFSET_FOR_FILENAME_LENGTH + offset);
            this.wordBbuf.rewind();
            IOUtils.readFully(this.archive, this.wordBbuf);
            this.wordBbuf.flip();
            this.wordBbuf.get(this.shortBuf);
            int fileNameLen = ZipShort.getValue(this.shortBuf);
            this.wordBbuf.get(this.shortBuf);
            int extraFieldLen = ZipShort.getValue(this.shortBuf);
            skipBytes(fileNameLen);
            byte[] localExtraData = new byte[extraFieldLen];
            IOUtils.readFully(this.archive, ByteBuffer.wrap(localExtraData));
            ze.setExtra(localExtraData);
            ze.setDataOffset(((((LFH_OFFSET_FOR_FILENAME_LENGTH + offset) + 2) + 2) + ((long) fileNameLen)) + ((long) extraFieldLen));
            ze.setStreamContiguous(true);
            if (entriesWithoutUTF8Flag.containsKey(ze)) {
                NameAndComment nc = (NameAndComment) entriesWithoutUTF8Flag.get(ze);
                ZipUtil.setNameAndCommentFromExtraFields(ze, nc.name, nc.comment);
            }
            String name = ze.getName();
            LinkedList<ZipArchiveEntry> entriesOfThatName = (LinkedList) this.nameMap.get(name);
            if (entriesOfThatName == null) {
                entriesOfThatName = new LinkedList();
                this.nameMap.put(name, entriesOfThatName);
            }
            entriesOfThatName.addLast(ze);
        }
    }

    private boolean startsWithLocalFileHeader() throws IOException {
        this.archive.position(0);
        this.wordBbuf.rewind();
        IOUtils.readFully(this.archive, this.wordBbuf);
        return Arrays.equals(this.wordBuf, ZipArchiveOutputStream.LFH_SIG);
    }

    private BoundedInputStream createBoundedInputStream(long start, long remaining) {
        return this.archive instanceof FileChannel ? new BoundedFileChannelInputStream(start, remaining) : new BoundedInputStream(start, remaining);
    }
}
