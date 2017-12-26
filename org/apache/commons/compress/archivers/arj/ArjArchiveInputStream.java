package org.apache.commons.compress.archivers.arj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.utils.BoundedInputStream;
import org.apache.commons.compress.utils.CRC32VerifyingInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class ArjArchiveInputStream extends ArchiveInputStream {
    private static final int ARJ_MAGIC_1 = 96;
    private static final int ARJ_MAGIC_2 = 234;
    private final String charsetName;
    private InputStream currentInputStream;
    private LocalFileHeader currentLocalFileHeader;
    private final DataInputStream in;
    private final MainHeader mainHeader;

    public ArjArchiveInputStream(InputStream inputStream, String charsetName) throws ArchiveException {
        this.currentLocalFileHeader = null;
        this.currentInputStream = null;
        this.in = new DataInputStream(inputStream);
        this.charsetName = charsetName;
        try {
            this.mainHeader = readMainHeader();
            if ((this.mainHeader.arjFlags & 1) != 0) {
                throw new ArchiveException("Encrypted ARJ files are unsupported");
            } else if ((this.mainHeader.arjFlags & 4) != 0) {
                throw new ArchiveException("Multi-volume ARJ files are unsupported");
            }
        } catch (IOException ioException) {
            throw new ArchiveException(ioException.getMessage(), ioException);
        }
    }

    public ArjArchiveInputStream(InputStream inputStream) throws ArchiveException {
        this(inputStream, "CP437");
    }

    public void close() throws IOException {
        this.in.close();
    }

    private int read8(DataInputStream dataIn) throws IOException {
        int value = dataIn.readUnsignedByte();
        count(1);
        return value;
    }

    private int read16(DataInputStream dataIn) throws IOException {
        int value = dataIn.readUnsignedShort();
        count(2);
        return Integer.reverseBytes(value) >>> 16;
    }

    private int read32(DataInputStream dataIn) throws IOException {
        int value = dataIn.readInt();
        count(4);
        return Integer.reverseBytes(value);
    }

    private String readString(DataInputStream dataIn) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        while (true) {
            int nextByte = dataIn.readUnsignedByte();
            if (nextByte == 0) {
                break;
            }
            buffer.write(nextByte);
        }
        if (this.charsetName != null) {
            return new String(buffer.toByteArray(), this.charsetName);
        }
        return new String(buffer.toByteArray());
    }

    private void readFully(DataInputStream dataIn, byte[] b) throws IOException {
        dataIn.readFully(b);
        count(b.length);
    }

    private byte[] readHeader() throws IOException {
        boolean found = false;
        byte[] basicHeaderBytes = null;
        do {
            int second = read8(this.in);
            do {
                int first = second;
                second = read8(this.in);
                if (first == 96) {
                    break;
                }
            } while (second != ARJ_MAGIC_2);
            int basicHeaderSize = read16(this.in);
            if (basicHeaderSize == 0) {
                return null;
            }
            if (basicHeaderSize <= 2600) {
                basicHeaderBytes = new byte[basicHeaderSize];
                readFully(this.in, basicHeaderBytes);
                long basicHeaderCrc32 = ((long) read32(this.in)) & 4294967295L;
                CRC32 crc32 = new CRC32();
                crc32.update(basicHeaderBytes);
                if (basicHeaderCrc32 == crc32.getValue()) {
                    found = true;
                    continue;
                } else {
                    continue;
                }
            }
        } while (!found);
        return basicHeaderBytes;
    }

    private MainHeader readMainHeader() throws IOException {
        byte[] basicHeaderBytes = readHeader();
        if (basicHeaderBytes == null) {
            throw new IOException("Archive ends without any headers");
        }
        DataInputStream basicHeader = new DataInputStream(new ByteArrayInputStream(basicHeaderBytes));
        int firstHeaderSize = basicHeader.readUnsignedByte();
        byte[] firstHeaderBytes = new byte[(firstHeaderSize - 1)];
        basicHeader.readFully(firstHeaderBytes);
        DataInputStream firstHeader = new DataInputStream(new ByteArrayInputStream(firstHeaderBytes));
        MainHeader hdr = new MainHeader();
        hdr.archiverVersionNumber = firstHeader.readUnsignedByte();
        hdr.minVersionToExtract = firstHeader.readUnsignedByte();
        hdr.hostOS = firstHeader.readUnsignedByte();
        hdr.arjFlags = firstHeader.readUnsignedByte();
        hdr.securityVersion = firstHeader.readUnsignedByte();
        hdr.fileType = firstHeader.readUnsignedByte();
        hdr.reserved = firstHeader.readUnsignedByte();
        hdr.dateTimeCreated = read32(firstHeader);
        hdr.dateTimeModified = read32(firstHeader);
        hdr.archiveSize = ((long) read32(firstHeader)) & 4294967295L;
        hdr.securityEnvelopeFilePosition = read32(firstHeader);
        hdr.fileSpecPosition = read16(firstHeader);
        hdr.securityEnvelopeLength = read16(firstHeader);
        pushedBackBytes(20);
        hdr.encryptionVersion = firstHeader.readUnsignedByte();
        hdr.lastChapter = firstHeader.readUnsignedByte();
        if (firstHeaderSize >= 33) {
            hdr.arjProtectionFactor = firstHeader.readUnsignedByte();
            hdr.arjFlags2 = firstHeader.readUnsignedByte();
            firstHeader.readUnsignedByte();
            firstHeader.readUnsignedByte();
        }
        hdr.name = readString(basicHeader);
        hdr.comment = readString(basicHeader);
        int extendedHeaderSize = read16(this.in);
        if (extendedHeaderSize > 0) {
            hdr.extendedHeaderBytes = new byte[extendedHeaderSize];
            readFully(this.in, hdr.extendedHeaderBytes);
            long extendedHeaderCrc32 = 4294967295L & ((long) read32(this.in));
            CRC32 crc32 = new CRC32();
            crc32.update(hdr.extendedHeaderBytes);
            if (extendedHeaderCrc32 != crc32.getValue()) {
                throw new IOException("Extended header CRC32 verification failure");
            }
        }
        return hdr;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.apache.commons.compress.archivers.arj.LocalFileHeader readLocalFileHeader() throws java.io.IOException {
        /*
        r25 = this;
        r5 = r25.readHeader();
        if (r5 != 0) goto L_0x0008;
    L_0x0006:
        r15 = 0;
    L_0x0007:
        return r15;
    L_0x0008:
        r4 = new java.io.DataInputStream;
        r16 = new java.io.ByteArrayInputStream;
        r0 = r16;
        r0.<init>(r5);
        r0 = r16;
        r4.<init>(r0);
        r18 = 0;
        r14 = r4.readUnsignedByte();	 Catch:{ Throwable -> 0x016f, all -> 0x01bf }
        r16 = r14 + -1;
        r0 = r16;
        r13 = new byte[r0];	 Catch:{ Throwable -> 0x016f, all -> 0x01bf }
        r4.readFully(r13);	 Catch:{ Throwable -> 0x016f, all -> 0x01bf }
        r12 = new java.io.DataInputStream;	 Catch:{ Throwable -> 0x016f, all -> 0x01bf }
        r16 = new java.io.ByteArrayInputStream;	 Catch:{ Throwable -> 0x016f, all -> 0x01bf }
        r0 = r16;
        r0.<init>(r13);	 Catch:{ Throwable -> 0x016f, all -> 0x01bf }
        r0 = r16;
        r12.<init>(r0);	 Catch:{ Throwable -> 0x016f, all -> 0x01bf }
        r17 = 0;
        r15 = new org.apache.commons.compress.archivers.arj.LocalFileHeader;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r15.<init>();	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r16 = r12.readUnsignedByte();	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r15.archiverVersionNumber = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r16 = r12.readUnsignedByte();	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r15.minVersionToExtract = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r16 = r12.readUnsignedByte();	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r15.hostOS = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r16 = r12.readUnsignedByte();	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r15.arjFlags = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r16 = r12.readUnsignedByte();	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r15.method = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r16 = r12.readUnsignedByte();	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r15.fileType = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r16 = r12.readUnsignedByte();	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r15.reserved = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r25;
        r16 = r0.read32(r12);	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r15.dateTimeModified = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r20 = 4294967295; // 0xffffffff float:NaN double:2.1219957905E-314;
        r0 = r25;
        r16 = r0.read32(r12);	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r0 = (long) r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r22 = r0;
        r20 = r20 & r22;
        r0 = r20;
        r15.compressedSize = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r20 = 4294967295; // 0xffffffff float:NaN double:2.1219957905E-314;
        r0 = r25;
        r16 = r0.read32(r12);	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r0 = (long) r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r22 = r0;
        r20 = r20 & r22;
        r0 = r20;
        r15.originalSize = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r20 = 4294967295; // 0xffffffff float:NaN double:2.1219957905E-314;
        r0 = r25;
        r16 = r0.read32(r12);	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r0 = (long) r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r22 = r0;
        r20 = r20 & r22;
        r0 = r20;
        r15.originalCrc32 = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r25;
        r16 = r0.read16(r12);	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r15.fileSpecPosition = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r25;
        r16 = r0.read16(r12);	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r15.fileAccessMode = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r20 = 20;
        r0 = r25;
        r1 = r20;
        r0.pushedBackBytes(r1);	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r16 = r12.readUnsignedByte();	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r15.firstChapter = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r16 = r12.readUnsignedByte();	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r15.lastChapter = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r25;
        r0.readExtraData(r14, r12, r15);	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r25;
        r16 = r0.readString(r4);	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r15.name = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r25;
        r16 = r0.readString(r4);	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r15.comment = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r11 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r11.<init>();	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
    L_0x0109:
        r0 = r25;
        r0 = r0.in;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r16 = r0;
        r0 = r25;
        r1 = r16;
        r10 = r0.read16(r1);	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        if (r10 <= 0) goto L_0x0186;
    L_0x0119:
        r7 = new byte[r10];	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r25;
        r0 = r0.in;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r16 = r0;
        r0 = r25;
        r1 = r16;
        r0.readFully(r1, r7);	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r20 = 4294967295; // 0xffffffff float:NaN double:2.1219957905E-314;
        r0 = r25;
        r0 = r0.in;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r16 = r0;
        r0 = r25;
        r1 = r16;
        r16 = r0.read32(r1);	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r0 = (long) r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r22 = r0;
        r8 = r20 & r22;
        r6 = new java.util.zip.CRC32;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r6.<init>();	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r6.update(r7);	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r20 = r6.getValue();	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r16 = (r8 > r20 ? 1 : (r8 == r20 ? 0 : -1));
        if (r16 == 0) goto L_0x0180;
    L_0x0152:
        r16 = new java.io.IOException;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r19 = "Extended header CRC32 verification failure";
        r0 = r16;
        r1 = r19;
        r0.<init>(r1);	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        throw r16;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
    L_0x015e:
        r16 = move-exception;
        throw r16;	 Catch:{ all -> 0x0160 }
    L_0x0160:
        r17 = move-exception;
        r24 = r17;
        r17 = r16;
        r16 = r24;
    L_0x0167:
        if (r12 == 0) goto L_0x016e;
    L_0x0169:
        if (r17 == 0) goto L_0x01d5;
    L_0x016b:
        r12.close();	 Catch:{ Throwable -> 0x01cc, all -> 0x01bf }
    L_0x016e:
        throw r16;	 Catch:{ Throwable -> 0x016f, all -> 0x01bf }
    L_0x016f:
        r16 = move-exception;
        throw r16;	 Catch:{ all -> 0x0171 }
    L_0x0171:
        r17 = move-exception;
        r24 = r17;
        r17 = r16;
        r16 = r24;
    L_0x0178:
        if (r4 == 0) goto L_0x017f;
    L_0x017a:
        if (r17 == 0) goto L_0x01de;
    L_0x017c:
        r4.close();	 Catch:{ Throwable -> 0x01d9 }
    L_0x017f:
        throw r16;
    L_0x0180:
        r11.add(r7);	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        goto L_0x0109;
    L_0x0184:
        r16 = move-exception;
        goto L_0x0167;
    L_0x0186:
        r16 = r11.size();	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r0 = new byte[r0][];	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r16 = r0;
        r0 = r16;
        r16 = r11.toArray(r0);	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r16 = (byte[][]) r16;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        r0 = r16;
        r15.extendedHeaders = r0;	 Catch:{ Throwable -> 0x015e, all -> 0x0184 }
        if (r12 == 0) goto L_0x01a3;
    L_0x019e:
        if (r17 == 0) goto L_0x01c3;
    L_0x01a0:
        r12.close();	 Catch:{ Throwable -> 0x01b6, all -> 0x01bf }
    L_0x01a3:
        if (r4 == 0) goto L_0x0007;
    L_0x01a5:
        if (r18 == 0) goto L_0x01c7;
    L_0x01a7:
        r4.close();	 Catch:{ Throwable -> 0x01ac }
        goto L_0x0007;
    L_0x01ac:
        r16 = move-exception;
        r0 = r18;
        r1 = r16;
        r0.addSuppressed(r1);
        goto L_0x0007;
    L_0x01b6:
        r16 = move-exception;
        r0 = r17;
        r1 = r16;
        r0.addSuppressed(r1);	 Catch:{ Throwable -> 0x016f, all -> 0x01bf }
        goto L_0x01a3;
    L_0x01bf:
        r16 = move-exception;
        r17 = r18;
        goto L_0x0178;
    L_0x01c3:
        r12.close();	 Catch:{ Throwable -> 0x016f, all -> 0x01bf }
        goto L_0x01a3;
    L_0x01c7:
        r4.close();
        goto L_0x0007;
    L_0x01cc:
        r19 = move-exception;
        r0 = r17;
        r1 = r19;
        r0.addSuppressed(r1);	 Catch:{ Throwable -> 0x016f, all -> 0x01bf }
        goto L_0x016e;
    L_0x01d5:
        r12.close();	 Catch:{ Throwable -> 0x016f, all -> 0x01bf }
        goto L_0x016e;
    L_0x01d9:
        r18 = move-exception;
        r17.addSuppressed(r18);
        goto L_0x017f;
    L_0x01de:
        r4.close();
        goto L_0x017f;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.arj.ArjArchiveInputStream.readLocalFileHeader():org.apache.commons.compress.archivers.arj.LocalFileHeader");
    }

    private void readExtraData(int firstHeaderSize, DataInputStream firstHeader, LocalFileHeader localFileHeader) throws IOException {
        if (firstHeaderSize >= 33) {
            localFileHeader.extendedFilePosition = read32(firstHeader);
            if (firstHeaderSize >= 45) {
                localFileHeader.dateTimeAccessed = read32(firstHeader);
                localFileHeader.dateTimeCreated = read32(firstHeader);
                localFileHeader.originalSizeEvenForVolumes = read32(firstHeader);
                pushedBackBytes(12);
            }
            pushedBackBytes(4);
        }
    }

    public static boolean matches(byte[] signature, int length) {
        return length >= 2 && (signature[0] & 255) == 96 && (signature[1] & 255) == ARJ_MAGIC_2;
    }

    public String getArchiveName() {
        return this.mainHeader.name;
    }

    public String getArchiveComment() {
        return this.mainHeader.comment;
    }

    public ArjArchiveEntry getNextEntry() throws IOException {
        if (this.currentInputStream != null) {
            IOUtils.skip(this.currentInputStream, Long.MAX_VALUE);
            this.currentInputStream.close();
            this.currentLocalFileHeader = null;
            this.currentInputStream = null;
        }
        this.currentLocalFileHeader = readLocalFileHeader();
        if (this.currentLocalFileHeader != null) {
            this.currentInputStream = new BoundedInputStream(this.in, this.currentLocalFileHeader.compressedSize);
            if (this.currentLocalFileHeader.method == 0) {
                this.currentInputStream = new CRC32VerifyingInputStream(this.currentInputStream, this.currentLocalFileHeader.originalSize, this.currentLocalFileHeader.originalCrc32);
            }
            return new ArjArchiveEntry(this.currentLocalFileHeader);
        }
        this.currentInputStream = null;
        return null;
    }

    public boolean canReadEntryData(ArchiveEntry ae) {
        return (ae instanceof ArjArchiveEntry) && ((ArjArchiveEntry) ae).getMethod() == 0;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.currentLocalFileHeader == null) {
            throw new IllegalStateException("No current arj entry");
        } else if (this.currentLocalFileHeader.method == 0) {
            return this.currentInputStream.read(b, off, len);
        } else {
            throw new IOException("Unsupported compression method " + this.currentLocalFileHeader.method);
        }
    }
}
