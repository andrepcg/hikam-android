package org.apache.commons.compress.archivers.sevenz;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.zip.CRC32;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.commons.compress.utils.BoundedInputStream;
import org.apache.commons.compress.utils.CRC32VerifyingInputStream;
import org.apache.commons.compress.utils.CharsetNames;
import org.apache.commons.compress.utils.IOUtils;

public class SevenZFile implements Closeable {
    static final int SIGNATURE_HEADER_SIZE = 32;
    static final byte[] sevenZSignature = new byte[]{TarConstants.LF_CONTIG, (byte) 122, (byte) -68, (byte) -81, (byte) 39, (byte) 28};
    private final Archive archive;
    private SeekableByteChannel channel;
    private int currentEntryIndex;
    private int currentFolderIndex;
    private InputStream currentFolderInputStream;
    private final ArrayList<InputStream> deferredBlockStreams;
    private final String fileName;
    private byte[] password;

    public SevenZFile(File filename, byte[] password) throws IOException {
        this(Files.newByteChannel(filename.toPath(), EnumSet.of(StandardOpenOption.READ), new FileAttribute[0]), filename.getAbsolutePath(), password, true);
    }

    public SevenZFile(SeekableByteChannel channel) throws IOException {
        this(channel, "unknown archive", null);
    }

    public SevenZFile(SeekableByteChannel channel, byte[] password) throws IOException {
        this(channel, "unknown archive", password);
    }

    public SevenZFile(SeekableByteChannel channel, String filename, byte[] password) throws IOException {
        this(channel, filename, password, false);
    }

    private SevenZFile(SeekableByteChannel channel, String filename, byte[] password, boolean closeOnError) throws IOException {
        this.currentEntryIndex = -1;
        this.currentFolderIndex = -1;
        this.currentFolderInputStream = null;
        this.deferredBlockStreams = new ArrayList();
        boolean succeeded = false;
        this.channel = channel;
        this.fileName = filename;
        try {
            this.archive = readHeaders(password);
            if (password != null) {
                this.password = new byte[password.length];
                System.arraycopy(password, 0, this.password, 0, password.length);
            } else {
                this.password = null;
            }
            succeeded = true;
        } finally {
            if (!succeeded && closeOnError) {
                this.channel.close();
            }
        }
    }

    public SevenZFile(File filename) throws IOException {
        this(filename, null);
    }

    public void close() throws IOException {
        if (this.channel != null) {
            try {
                this.channel.close();
            } finally {
                this.channel = null;
                if (this.password != null) {
                    Arrays.fill(this.password, (byte) 0);
                }
                this.password = null;
            }
        }
    }

    public SevenZArchiveEntry getNextEntry() throws IOException {
        if (this.currentEntryIndex >= this.archive.files.length - 1) {
            return null;
        }
        this.currentEntryIndex++;
        SevenZArchiveEntry entry = this.archive.files[this.currentEntryIndex];
        buildDecodingStream();
        return entry;
    }

    public Iterable<SevenZArchiveEntry> getEntries() {
        return Arrays.asList(this.archive.files);
    }

    private Archive readHeaders(byte[] password) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN);
        readFully(buf);
        byte[] signature = new byte[6];
        buf.get(signature);
        if (Arrays.equals(signature, sevenZSignature)) {
            byte archiveVersionMajor = buf.get();
            byte archiveVersionMinor = buf.get();
            if (archiveVersionMajor != (byte) 0) {
                throw new IOException(String.format("Unsupported 7z version (%d,%d)", new Object[]{Byte.valueOf(archiveVersionMajor), Byte.valueOf(archiveVersionMinor)}));
            }
            StartHeader startHeader = readStartHeader(4294967295L & ((long) buf.getInt()));
            int nextHeaderSizeInt = (int) startHeader.nextHeaderSize;
            if (((long) nextHeaderSizeInt) != startHeader.nextHeaderSize) {
                throw new IOException("cannot handle nextHeaderSize " + startHeader.nextHeaderSize);
            }
            this.channel.position(32 + startHeader.nextHeaderOffset);
            buf = ByteBuffer.allocate(nextHeaderSizeInt).order(ByteOrder.LITTLE_ENDIAN);
            readFully(buf);
            CRC32 crc = new CRC32();
            crc.update(buf.array());
            if (startHeader.nextHeaderCrc != crc.getValue()) {
                throw new IOException("NextHeader CRC mismatch");
            }
            Archive archive = new Archive();
            int nid = getUnsignedByte(buf);
            if (nid == 23) {
                buf = readEncodedHeader(buf, archive, password);
                archive = new Archive();
                nid = getUnsignedByte(buf);
            }
            if (nid == 1) {
                readHeader(buf, archive);
                return archive;
            }
            throw new IOException("Broken or unsupported archive: no Header");
        }
        throw new IOException("Bad 7z signature");
    }

    private StartHeader readStartHeader(long startHeaderCrc) throws IOException {
        Throwable th;
        StartHeader startHeader = new StartHeader();
        DataInputStream dataInputStream = new DataInputStream(new CRC32VerifyingInputStream(new BoundedSeekableByteChannelInputStream(this.channel, 20), 20, startHeaderCrc));
        Throwable th2 = null;
        try {
            startHeader.nextHeaderOffset = Long.reverseBytes(dataInputStream.readLong());
            startHeader.nextHeaderSize = Long.reverseBytes(dataInputStream.readLong());
            startHeader.nextHeaderCrc = 4294967295L & ((long) Integer.reverseBytes(dataInputStream.readInt()));
            if (dataInputStream != null) {
                if (th2 != null) {
                    try {
                        dataInputStream.close();
                    } catch (Throwable th3) {
                        th2.addSuppressed(th3);
                    }
                } else {
                    dataInputStream.close();
                }
            }
            return startHeader;
        } catch (Throwable th22) {
            Throwable th4 = th22;
            th22 = th3;
            th3 = th4;
        }
        if (dataInputStream != null) {
            if (th22 != null) {
                try {
                    dataInputStream.close();
                } catch (Throwable th5) {
                    th22.addSuppressed(th5);
                }
            } else {
                dataInputStream.close();
            }
        }
        throw th3;
        throw th3;
    }

    private void readHeader(ByteBuffer header, Archive archive) throws IOException {
        int nid = getUnsignedByte(header);
        if (nid == 2) {
            readArchiveProperties(header);
            nid = getUnsignedByte(header);
        }
        if (nid == 3) {
            throw new IOException("Additional streams unsupported");
        }
        if (nid == 4) {
            readStreamsInfo(header, archive);
            nid = getUnsignedByte(header);
        }
        if (nid == 5) {
            readFilesInfo(header, archive);
            nid = getUnsignedByte(header);
        }
        if (nid != 0) {
            throw new IOException("Badly terminated header, found " + nid);
        }
    }

    private void readArchiveProperties(ByteBuffer input) throws IOException {
        int nid = getUnsignedByte(input);
        while (nid != 0) {
            input.get(new byte[((int) readUint64(input))]);
            nid = getUnsignedByte(input);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.nio.ByteBuffer readEncodedHeader(java.nio.ByteBuffer r21, org.apache.commons.compress.archivers.sevenz.Archive r22, byte[] r23) throws java.io.IOException {
        /*
        r20 = this;
        r20.readStreamsInfo(r21, r22);
        r0 = r22;
        r2 = r0.folders;
        r4 = 0;
        r15 = r2[r4];
        r14 = 0;
        r4 = 32;
        r0 = r22;
        r10 = r0.packPos;
        r4 = r4 + r10;
        r10 = 0;
        r16 = r4 + r10;
        r0 = r20;
        r2 = r0.channel;
        r0 = r16;
        r2.position(r0);
        r3 = new org.apache.commons.compress.archivers.sevenz.BoundedSeekableByteChannelInputStream;
        r0 = r20;
        r2 = r0.channel;
        r0 = r22;
        r4 = r0.packSizes;
        r5 = 0;
        r4 = r4[r5];
        r3.<init>(r2, r4);
        r2 = r15.getOrderedCoders();
        r9 = r2.iterator();
    L_0x0037:
        r2 = r9.hasNext();
        if (r2 == 0) goto L_0x006a;
    L_0x003d:
        r6 = r9.next();
        r6 = (org.apache.commons.compress.archivers.sevenz.Coder) r6;
        r4 = r6.numInStreams;
        r10 = 1;
        r2 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1));
        if (r2 != 0) goto L_0x0053;
    L_0x004b:
        r4 = r6.numOutStreams;
        r10 = 1;
        r2 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1));
        if (r2 == 0) goto L_0x005b;
    L_0x0053:
        r2 = new java.io.IOException;
        r4 = "Multi input/output stream coders are not yet supported";
        r2.<init>(r4);
        throw r2;
    L_0x005b:
        r0 = r20;
        r2 = r0.fileName;
        r4 = r15.getUnpackSizeForCoder(r6);
        r7 = r23;
        r3 = org.apache.commons.compress.archivers.sevenz.Coders.addDecoder(r2, r3, r4, r6, r7);
        goto L_0x0037;
    L_0x006a:
        r2 = r15.hasCrc;
        if (r2 == 0) goto L_0x007b;
    L_0x006e:
        r8 = new org.apache.commons.compress.utils.CRC32VerifyingInputStream;
        r10 = r15.getUnpackSize();
        r12 = r15.crc;
        r9 = r3;
        r8.<init>(r9, r10, r12);
        r3 = r8;
    L_0x007b:
        r4 = r15.getUnpackSize();
        r2 = (int) r4;
        r0 = new byte[r2];
        r18 = r0;
        r19 = new java.io.DataInputStream;
        r0 = r19;
        r0.<init>(r3);
        r4 = 0;
        r0 = r19;
        r1 = r18;
        r0.readFully(r1);	 Catch:{ Throwable -> 0x00ae }
        if (r19 == 0) goto L_0x009a;
    L_0x0095:
        if (r4 == 0) goto L_0x00aa;
    L_0x0097:
        r19.close();	 Catch:{ Throwable -> 0x00a5 }
    L_0x009a:
        r2 = java.nio.ByteBuffer.wrap(r18);
        r4 = java.nio.ByteOrder.LITTLE_ENDIAN;
        r2 = r2.order(r4);
        return r2;
    L_0x00a5:
        r2 = move-exception;
        r4.addSuppressed(r2);
        goto L_0x009a;
    L_0x00aa:
        r19.close();
        goto L_0x009a;
    L_0x00ae:
        r4 = move-exception;
        throw r4;	 Catch:{ all -> 0x00b0 }
    L_0x00b0:
        r2 = move-exception;
        if (r19 == 0) goto L_0x00b8;
    L_0x00b3:
        if (r4 == 0) goto L_0x00be;
    L_0x00b5:
        r19.close();	 Catch:{ Throwable -> 0x00b9 }
    L_0x00b8:
        throw r2;
    L_0x00b9:
        r5 = move-exception;
        r4.addSuppressed(r5);
        goto L_0x00b8;
    L_0x00be:
        r19.close();
        goto L_0x00b8;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.sevenz.SevenZFile.readEncodedHeader(java.nio.ByteBuffer, org.apache.commons.compress.archivers.sevenz.Archive, byte[]):java.nio.ByteBuffer");
    }

    private void readStreamsInfo(ByteBuffer header, Archive archive) throws IOException {
        int nid = getUnsignedByte(header);
        if (nid == 6) {
            readPackInfo(header, archive);
            nid = getUnsignedByte(header);
        }
        if (nid == 7) {
            readUnpackInfo(header, archive);
            nid = getUnsignedByte(header);
        } else {
            archive.folders = new Folder[0];
        }
        if (nid == 8) {
            readSubStreamsInfo(header, archive);
            nid = getUnsignedByte(header);
        }
        if (nid != 0) {
            throw new IOException("Badly terminated StreamsInfo");
        }
    }

    private void readPackInfo(ByteBuffer header, Archive archive) throws IOException {
        int i;
        archive.packPos = readUint64(header);
        long numPackStreams = readUint64(header);
        int nid = getUnsignedByte(header);
        if (nid == 9) {
            archive.packSizes = new long[((int) numPackStreams)];
            for (i = 0; i < archive.packSizes.length; i++) {
                archive.packSizes[i] = readUint64(header);
            }
            nid = getUnsignedByte(header);
        }
        if (nid == 10) {
            archive.packCrcsDefined = readAllOrBits(header, (int) numPackStreams);
            archive.packCrcs = new long[((int) numPackStreams)];
            for (i = 0; i < ((int) numPackStreams); i++) {
                if (archive.packCrcsDefined.get(i)) {
                    archive.packCrcs[i] = 4294967295L & ((long) header.getInt());
                }
            }
            nid = getUnsignedByte(header);
        }
        if (nid != 0) {
            throw new IOException("Badly terminated PackInfo (" + nid + ")");
        }
    }

    private void readUnpackInfo(ByteBuffer header, Archive archive) throws IOException {
        int nid = getUnsignedByte(header);
        if (nid != 11) {
            throw new IOException("Expected kFolder, got " + nid);
        }
        long numFolders = readUint64(header);
        Folder[] folders = new Folder[((int) numFolders)];
        archive.folders = folders;
        if (getUnsignedByte(header) != 0) {
            throw new IOException("External unsupported");
        }
        int i;
        for (i = 0; i < ((int) numFolders); i++) {
            folders[i] = readFolder(header);
        }
        nid = getUnsignedByte(header);
        if (nid != 12) {
            throw new IOException("Expected kCodersUnpackSize, got " + nid);
        }
        for (Folder folder : folders) {
            folder.unpackSizes = new long[((int) folder.totalOutputStreams)];
            for (i = 0; ((long) i) < folder.totalOutputStreams; i++) {
                folder.unpackSizes[i] = readUint64(header);
            }
        }
        nid = getUnsignedByte(header);
        if (nid == 10) {
            BitSet crcsDefined = readAllOrBits(header, (int) numFolders);
            for (i = 0; i < ((int) numFolders); i++) {
                if (crcsDefined.get(i)) {
                    folders[i].hasCrc = true;
                    folders[i].crc = 4294967295L & ((long) header.getInt());
                } else {
                    folders[i].hasCrc = false;
                }
            }
            nid = getUnsignedByte(header);
        }
        if (nid != 0) {
            throw new IOException("Badly terminated UnpackInfo");
        }
    }

    private void readSubStreamsInfo(ByteBuffer header, Archive archive) throws IOException {
        int i;
        for (Folder folder : archive.folders) {
            folder.numUnpackSubStreams = 1;
        }
        int totalUnpackStreams = archive.folders.length;
        int nid = getUnsignedByte(header);
        if (nid == 13) {
            totalUnpackStreams = 0;
            for (Folder folder2 : archive.folders) {
                long numStreams = readUint64(header);
                folder2.numUnpackSubStreams = (int) numStreams;
                totalUnpackStreams = (int) (((long) totalUnpackStreams) + numStreams);
            }
            nid = getUnsignedByte(header);
        }
        SubStreamsInfo subStreamsInfo = new SubStreamsInfo();
        subStreamsInfo.unpackSizes = new long[totalUnpackStreams];
        subStreamsInfo.hasCrc = new BitSet(totalUnpackStreams);
        subStreamsInfo.crcs = new long[totalUnpackStreams];
        int nextUnpackStream = 0;
        for (Folder folder22 : archive.folders) {
            if (folder22.numUnpackSubStreams != 0) {
                int nextUnpackStream2;
                long sum = 0;
                if (nid == 9) {
                    i = 0;
                    while (i < folder22.numUnpackSubStreams - 1) {
                        long size = readUint64(header);
                        nextUnpackStream2 = nextUnpackStream + 1;
                        subStreamsInfo.unpackSizes[nextUnpackStream] = size;
                        sum += size;
                        i++;
                        nextUnpackStream = nextUnpackStream2;
                    }
                }
                nextUnpackStream2 = nextUnpackStream + 1;
                subStreamsInfo.unpackSizes[nextUnpackStream] = folder22.getUnpackSize() - sum;
                nextUnpackStream = nextUnpackStream2;
            }
        }
        if (nid == 9) {
            nid = getUnsignedByte(header);
        }
        int numDigests = 0;
        for (Folder folder222 : archive.folders) {
            if (folder222.numUnpackSubStreams != 1 || !folder222.hasCrc) {
                numDigests += folder222.numUnpackSubStreams;
            }
        }
        if (nid == 10) {
            BitSet hasMissingCrc = readAllOrBits(header, numDigests);
            long[] missingCrcs = new long[numDigests];
            for (i = 0; i < numDigests; i++) {
                if (hasMissingCrc.get(i)) {
                    missingCrcs[i] = 4294967295L & ((long) header.getInt());
                }
            }
            int nextCrc = 0;
            int nextMissingCrc = 0;
            for (Folder folder2222 : archive.folders) {
                if (folder2222.numUnpackSubStreams == 1 && folder2222.hasCrc) {
                    subStreamsInfo.hasCrc.set(nextCrc, true);
                    subStreamsInfo.crcs[nextCrc] = folder2222.crc;
                    nextCrc++;
                } else {
                    for (i = 0; i < folder2222.numUnpackSubStreams; i++) {
                        subStreamsInfo.hasCrc.set(nextCrc, hasMissingCrc.get(nextMissingCrc));
                        subStreamsInfo.crcs[nextCrc] = missingCrcs[nextMissingCrc];
                        nextCrc++;
                        nextMissingCrc++;
                    }
                }
            }
            nid = getUnsignedByte(header);
        }
        if (nid != 0) {
            throw new IOException("Badly terminated SubStreamsInfo");
        }
        archive.subStreamsInfo = subStreamsInfo;
    }

    private Folder readFolder(ByteBuffer header) throws IOException {
        int i;
        Folder folder = new Folder();
        Coder[] coders = new Coder[((int) readUint64(header))];
        long totalInStreams = 0;
        long totalOutStreams = 0;
        for (i = 0; i < coders.length; i++) {
            coders[i] = new Coder();
            int bits = getUnsignedByte(header);
            int idSize = bits & 15;
            boolean isSimple = (bits & 16) == 0;
            boolean hasAttributes = (bits & 32) != 0;
            boolean moreAlternativeMethods = (bits & 128) != 0;
            coders[i].decompressionMethodId = new byte[idSize];
            header.get(coders[i].decompressionMethodId);
            if (isSimple) {
                coders[i].numInStreams = 1;
                coders[i].numOutStreams = 1;
            } else {
                coders[i].numInStreams = readUint64(header);
                coders[i].numOutStreams = readUint64(header);
            }
            totalInStreams += coders[i].numInStreams;
            totalOutStreams += coders[i].numOutStreams;
            if (hasAttributes) {
                coders[i].properties = new byte[((int) readUint64(header))];
                header.get(coders[i].properties);
            }
            if (moreAlternativeMethods) {
                throw new IOException("Alternative methods are unsupported, please report. The reference implementation doesn't support them either.");
            }
        }
        folder.coders = coders;
        folder.totalInputStreams = totalInStreams;
        folder.totalOutputStreams = totalOutStreams;
        if (totalOutStreams == 0) {
            throw new IOException("Total output streams can't be 0");
        }
        long numBindPairs = totalOutStreams - 1;
        BindPair[] bindPairs = new BindPair[((int) numBindPairs)];
        for (i = 0; i < bindPairs.length; i++) {
            bindPairs[i] = new BindPair();
            bindPairs[i].inIndex = readUint64(header);
            bindPairs[i].outIndex = readUint64(header);
        }
        folder.bindPairs = bindPairs;
        if (totalInStreams < numBindPairs) {
            throw new IOException("Total input streams can't be less than the number of bind pairs");
        }
        long numPackedStreams = totalInStreams - numBindPairs;
        long[] packedStreams = new long[((int) numPackedStreams)];
        if (numPackedStreams == 1) {
            i = 0;
            while (i < ((int) totalInStreams) && folder.findBindPairForInStream(i) >= 0) {
                i++;
            }
            if (i == ((int) totalInStreams)) {
                throw new IOException("Couldn't find stream's bind pair index");
            }
            packedStreams[0] = (long) i;
        } else {
            for (i = 0; i < ((int) numPackedStreams); i++) {
                packedStreams[i] = readUint64(header);
            }
        }
        folder.packedStreams = packedStreams;
        return folder;
    }

    private BitSet readAllOrBits(ByteBuffer header, int size) throws IOException {
        if (getUnsignedByte(header) == 0) {
            return readBits(header, size);
        }
        BitSet bits = new BitSet(size);
        for (int i = 0; i < size; i++) {
            bits.set(i, true);
        }
        return bits;
    }

    private BitSet readBits(ByteBuffer header, int size) throws IOException {
        BitSet bits = new BitSet(size);
        int mask = 0;
        int cache = 0;
        for (int i = 0; i < size; i++) {
            if (mask == 0) {
                mask = 128;
                cache = getUnsignedByte(header);
            }
            bits.set(i, (cache & mask) != 0);
            mask >>>= 1;
        }
        return bits;
    }

    private void readFilesInfo(ByteBuffer header, Archive archive) throws IOException {
        int i;
        SevenZArchiveEntry[] files = new SevenZArchiveEntry[((int) readUint64(header))];
        for (i = 0; i < files.length; i++) {
            files[i] = new SevenZArchiveEntry();
        }
        BitSet isEmptyStream = null;
        BitSet isEmptyFile = null;
        BitSet isAnti = null;
        while (true) {
            int propertyType = getUnsignedByte(header);
            if (propertyType != 0) {
                long size = readUint64(header);
                BitSet timesDefined;
                switch (propertyType) {
                    case 14:
                        isEmptyStream = readBits(header, files.length);
                        break;
                    case 15:
                        if (isEmptyStream != null) {
                            isEmptyFile = readBits(header, isEmptyStream.cardinality());
                            break;
                        }
                        throw new IOException("Header format error: kEmptyStream must appear before kEmptyFile");
                    case 16:
                        if (isEmptyStream != null) {
                            isAnti = readBits(header, isEmptyStream.cardinality());
                            break;
                        }
                        throw new IOException("Header format error: kEmptyStream must appear before kAnti");
                    case 17:
                        if (getUnsignedByte(header) == 0) {
                            if (((size - 1) & 1) == 0) {
                                byte[] names = new byte[((int) (size - 1))];
                                header.get(names);
                                int nextFile = 0;
                                int nextName = 0;
                                i = 0;
                                while (i < names.length) {
                                    if (names[i] == (byte) 0 && names[i + 1] == (byte) 0) {
                                        int nextFile2 = nextFile + 1;
                                        files[nextFile].setName(new String(names, nextName, i - nextName, CharsetNames.UTF_16LE));
                                        nextName = i + 2;
                                        nextFile = nextFile2;
                                    }
                                    i += 2;
                                }
                                if (nextName == names.length && nextFile == files.length) {
                                    break;
                                }
                                throw new IOException("Error parsing file names");
                            }
                            throw new IOException("File names length invalid");
                        }
                        throw new IOException("Not implemented");
                    case 18:
                        timesDefined = readAllOrBits(header, files.length);
                        if (getUnsignedByte(header) == 0) {
                            for (i = 0; i < files.length; i++) {
                                files[i].setHasCreationDate(timesDefined.get(i));
                                if (files[i].getHasCreationDate()) {
                                    files[i].setCreationDate(header.getLong());
                                }
                            }
                            break;
                        }
                        throw new IOException("Unimplemented");
                    case 19:
                        timesDefined = readAllOrBits(header, files.length);
                        if (getUnsignedByte(header) == 0) {
                            for (i = 0; i < files.length; i++) {
                                files[i].setHasAccessDate(timesDefined.get(i));
                                if (files[i].getHasAccessDate()) {
                                    files[i].setAccessDate(header.getLong());
                                }
                            }
                            break;
                        }
                        throw new IOException("Unimplemented");
                    case 20:
                        timesDefined = readAllOrBits(header, files.length);
                        if (getUnsignedByte(header) == 0) {
                            for (i = 0; i < files.length; i++) {
                                files[i].setHasLastModifiedDate(timesDefined.get(i));
                                if (files[i].getHasLastModifiedDate()) {
                                    files[i].setLastModifiedDate(header.getLong());
                                }
                            }
                            break;
                        }
                        throw new IOException("Unimplemented");
                    case 21:
                        BitSet attributesDefined = readAllOrBits(header, files.length);
                        if (getUnsignedByte(header) == 0) {
                            for (i = 0; i < files.length; i++) {
                                files[i].setHasWindowsAttributes(attributesDefined.get(i));
                                if (files[i].getHasWindowsAttributes()) {
                                    files[i].setWindowsAttributes(header.getInt());
                                }
                            }
                            break;
                        }
                        throw new IOException("Unimplemented");
                    case 24:
                        throw new IOException("kStartPos is unsupported, please report");
                    case 25:
                        if (skipBytesFully(header, size) >= size) {
                            break;
                        }
                        throw new IOException("Incomplete kDummy property");
                    default:
                        if (skipBytesFully(header, size) >= size) {
                            break;
                        }
                        throw new IOException("Incomplete property of type " + propertyType);
                }
            }
            int nonEmptyFileCounter = 0;
            int emptyFileCounter = 0;
            for (i = 0; i < files.length; i++) {
                SevenZArchiveEntry sevenZArchiveEntry = files[i];
                boolean z = isEmptyStream == null || !isEmptyStream.get(i);
                sevenZArchiveEntry.setHasStream(z);
                if (files[i].hasStream()) {
                    files[i].setDirectory(false);
                    files[i].setAntiItem(false);
                    files[i].setHasCrc(archive.subStreamsInfo.hasCrc.get(nonEmptyFileCounter));
                    files[i].setCrcValue(archive.subStreamsInfo.crcs[nonEmptyFileCounter]);
                    files[i].setSize(archive.subStreamsInfo.unpackSizes[nonEmptyFileCounter]);
                    nonEmptyFileCounter++;
                } else {
                    sevenZArchiveEntry = files[i];
                    z = isEmptyFile == null || !isEmptyFile.get(emptyFileCounter);
                    sevenZArchiveEntry.setDirectory(z);
                    sevenZArchiveEntry = files[i];
                    z = isAnti != null && isAnti.get(emptyFileCounter);
                    sevenZArchiveEntry.setAntiItem(z);
                    files[i].setHasCrc(false);
                    files[i].setSize(0);
                    emptyFileCounter++;
                }
            }
            archive.files = files;
            calculateStreamMap(archive);
            return;
        }
    }

    private void calculateStreamMap(Archive archive) throws IOException {
        int numFolders;
        int i;
        int numPackSizes;
        StreamMap streamMap = new StreamMap();
        int nextFolderPackStreamIndex = 0;
        if (archive.folders != null) {
            numFolders = archive.folders.length;
        } else {
            numFolders = 0;
        }
        streamMap.folderFirstPackStreamIndex = new int[numFolders];
        for (i = 0; i < numFolders; i++) {
            streamMap.folderFirstPackStreamIndex[i] = nextFolderPackStreamIndex;
            nextFolderPackStreamIndex += archive.folders[i].packedStreams.length;
        }
        long nextPackStreamOffset = 0;
        if (archive.packSizes != null) {
            numPackSizes = archive.packSizes.length;
        } else {
            numPackSizes = 0;
        }
        streamMap.packStreamOffsets = new long[numPackSizes];
        for (i = 0; i < numPackSizes; i++) {
            streamMap.packStreamOffsets[i] = nextPackStreamOffset;
            nextPackStreamOffset += archive.packSizes[i];
        }
        streamMap.folderFirstFileIndex = new int[numFolders];
        streamMap.fileFolderIndex = new int[archive.files.length];
        int nextFolderIndex = 0;
        int nextFolderUnpackStreamIndex = 0;
        for (i = 0; i < archive.files.length; i++) {
            if (archive.files[i].hasStream() || nextFolderUnpackStreamIndex != 0) {
                if (nextFolderUnpackStreamIndex == 0) {
                    while (nextFolderIndex < archive.folders.length) {
                        streamMap.folderFirstFileIndex[nextFolderIndex] = i;
                        if (archive.folders[nextFolderIndex].numUnpackSubStreams > 0) {
                            break;
                        }
                        nextFolderIndex++;
                    }
                    if (nextFolderIndex >= archive.folders.length) {
                        throw new IOException("Too few folders in archive");
                    }
                }
                streamMap.fileFolderIndex[i] = nextFolderIndex;
                if (archive.files[i].hasStream()) {
                    nextFolderUnpackStreamIndex++;
                    if (nextFolderUnpackStreamIndex >= archive.folders[nextFolderIndex].numUnpackSubStreams) {
                        nextFolderIndex++;
                        nextFolderUnpackStreamIndex = 0;
                    }
                }
            } else {
                streamMap.fileFolderIndex[i] = -1;
            }
        }
        archive.streamMap = streamMap;
    }

    private void buildDecodingStream() throws IOException {
        int folderIndex = this.archive.streamMap.fileFolderIndex[this.currentEntryIndex];
        if (folderIndex < 0) {
            this.deferredBlockStreams.clear();
            return;
        }
        InputStream fileStream;
        SevenZArchiveEntry file = this.archive.files[this.currentEntryIndex];
        if (this.currentFolderIndex == folderIndex) {
            file.setContentMethods(this.archive.files[this.currentEntryIndex - 1].getContentMethods());
        } else {
            this.currentFolderIndex = folderIndex;
            this.deferredBlockStreams.clear();
            if (this.currentFolderInputStream != null) {
                this.currentFolderInputStream.close();
                this.currentFolderInputStream = null;
            }
            Folder folder = this.archive.folders[folderIndex];
            int firstPackStreamIndex = this.archive.streamMap.folderFirstPackStreamIndex[folderIndex];
            this.currentFolderInputStream = buildDecoderStack(folder, (32 + this.archive.packPos) + this.archive.streamMap.packStreamOffsets[firstPackStreamIndex], firstPackStreamIndex, file);
        }
        InputStream fileStream2 = new BoundedInputStream(this.currentFolderInputStream, file.getSize());
        if (file.getHasCrc()) {
            fileStream = new CRC32VerifyingInputStream(fileStream2, file.getSize(), file.getCrcValue());
        } else {
            fileStream = fileStream2;
        }
        this.deferredBlockStreams.add(fileStream);
    }

    private InputStream buildDecoderStack(Folder folder, long folderOffset, int firstPackStreamIndex, SevenZArchiveEntry entry) throws IOException {
        this.channel.position(folderOffset);
        InputStream inputStreamStack = new BufferedInputStream(new BoundedSeekableByteChannelInputStream(this.channel, this.archive.packSizes[firstPackStreamIndex]));
        LinkedList<SevenZMethodConfiguration> methods = new LinkedList();
        for (Coder coder : folder.getOrderedCoders()) {
            if (coder.numInStreams == 1 && coder.numOutStreams == 1) {
                SevenZMethod method = SevenZMethod.byId(coder.decompressionMethodId);
                inputStreamStack = Coders.addDecoder(this.fileName, inputStreamStack, folder.getUnpackSizeForCoder(coder), coder, this.password);
                methods.addFirst(new SevenZMethodConfiguration(method, Coders.findByMethod(method).getOptionsFromCoder(coder, inputStreamStack)));
            } else {
                throw new IOException("Multi input/output stream coders are not yet supported");
            }
        }
        entry.setContentMethods(methods);
        if (!folder.hasCrc) {
            return inputStreamStack;
        }
        return new CRC32VerifyingInputStream(inputStreamStack, folder.getUnpackSize(), folder.crc);
    }

    public int read() throws IOException {
        return getCurrentStream().read();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.io.InputStream getCurrentStream() throws java.io.IOException {
        /*
        r7 = this;
        r6 = 0;
        r1 = r7.archive;
        r1 = r1.files;
        r2 = r7.currentEntryIndex;
        r1 = r1[r2];
        r2 = r1.getSize();
        r4 = 0;
        r1 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r1 != 0) goto L_0x001b;
    L_0x0013:
        r1 = new java.io.ByteArrayInputStream;
        r2 = new byte[r6];
        r1.<init>(r2);
    L_0x001a:
        return r1;
    L_0x001b:
        r1 = r7.deferredBlockStreams;
        r1 = r1.isEmpty();
        if (r1 == 0) goto L_0x002b;
    L_0x0023:
        r1 = new java.lang.IllegalStateException;
        r2 = "No current 7z entry (call getNextEntry() first).";
        r1.<init>(r2);
        throw r1;
    L_0x002b:
        r1 = r7.deferredBlockStreams;
        r1 = r1.size();
        r2 = 1;
        if (r1 <= r2) goto L_0x006a;
    L_0x0034:
        r1 = r7.deferredBlockStreams;
        r0 = r1.remove(r6);
        r0 = (java.io.InputStream) r0;
        r2 = 0;
        r4 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
        org.apache.commons.compress.utils.IOUtils.skip(r0, r4);	 Catch:{ Throwable -> 0x0056 }
        if (r0 == 0) goto L_0x002b;
    L_0x0047:
        if (r2 == 0) goto L_0x0052;
    L_0x0049:
        r0.close();	 Catch:{ Throwable -> 0x004d }
        goto L_0x002b;
    L_0x004d:
        r1 = move-exception;
        r2.addSuppressed(r1);
        goto L_0x002b;
    L_0x0052:
        r0.close();
        goto L_0x002b;
    L_0x0056:
        r2 = move-exception;
        throw r2;	 Catch:{ all -> 0x0058 }
    L_0x0058:
        r1 = move-exception;
        if (r0 == 0) goto L_0x0060;
    L_0x005b:
        if (r2 == 0) goto L_0x0066;
    L_0x005d:
        r0.close();	 Catch:{ Throwable -> 0x0061 }
    L_0x0060:
        throw r1;
    L_0x0061:
        r3 = move-exception;
        r2.addSuppressed(r3);
        goto L_0x0060;
    L_0x0066:
        r0.close();
        goto L_0x0060;
    L_0x006a:
        r1 = r7.deferredBlockStreams;
        r1 = r1.get(r6);
        r1 = (java.io.InputStream) r1;
        goto L_0x001a;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.sevenz.SevenZFile.getCurrentStream():java.io.InputStream");
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return getCurrentStream().read(b, off, len);
    }

    private static long readUint64(ByteBuffer in) throws IOException {
        long firstByte = (long) getUnsignedByte(in);
        int mask = 128;
        long value = 0;
        for (int i = 0; i < 8; i++) {
            if ((((long) mask) & firstByte) == 0) {
                return value | ((((long) (mask - 1)) & firstByte) << (i * 8));
            }
            value |= ((long) getUnsignedByte(in)) << (i * 8);
            mask >>>= 1;
        }
        return value;
    }

    private static int getUnsignedByte(ByteBuffer buf) {
        return buf.get() & 255;
    }

    public static boolean matches(byte[] signature, int length) {
        if (length < sevenZSignature.length) {
            return false;
        }
        for (int i = 0; i < sevenZSignature.length; i++) {
            if (signature[i] != sevenZSignature[i]) {
                return false;
            }
        }
        return true;
    }

    private static long skipBytesFully(ByteBuffer input, long bytesToSkip) throws IOException {
        if (bytesToSkip < 1) {
            return 0;
        }
        int current = input.position();
        int maxSkip = input.remaining();
        if (((long) maxSkip) < bytesToSkip) {
            bytesToSkip = (long) maxSkip;
        }
        input.position(((int) bytesToSkip) + current);
        return bytesToSkip;
    }

    private void readFully(ByteBuffer buf) throws IOException {
        buf.rewind();
        IOUtils.readFully(this.channel, buf);
        buf.flip();
    }

    public String toString() {
        return this.archive.toString();
    }
}
