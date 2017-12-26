package org.apache.commons.compress.archivers.tar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.utils.ArchiveUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.http.cookie.ClientCookie;

public class TarArchiveInputStream extends ArchiveInputStream {
    private static final int SMALL_BUFFER_SIZE = 256;
    private final int blockSize;
    private TarArchiveEntry currEntry;
    final String encoding;
    private long entryOffset;
    private long entrySize;
    private Map<String, String> globalPaxHeaders;
    private boolean hasHitEOF;
    private final InputStream is;
    private final int recordSize;
    private final byte[] smallBuf;
    private final ZipEncoding zipEncoding;

    private void tryToConsumeSecondEOFRecord() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x002a in list [B:9:0x001d]
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:42)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:60)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r6 = this;
        r1 = 1;
        r2 = r6.is;
        r0 = r2.markSupported();
        if (r0 == 0) goto L_0x0010;
    L_0x0009:
        r2 = r6.is;
        r3 = r6.recordSize;
        r2.mark(r3);
    L_0x0010:
        r2 = r6.readRecord();	 Catch:{ all -> 0x002d }
        r2 = r6.isEOFRecord(r2);	 Catch:{ all -> 0x002d }
        if (r2 != 0) goto L_0x002b;
    L_0x001a:
        r1 = 1;
    L_0x001b:
        if (r1 == 0) goto L_0x002a;
    L_0x001d:
        if (r0 == 0) goto L_0x002a;
    L_0x001f:
        r2 = r6.recordSize;
        r2 = (long) r2;
        r6.pushedBackBytes(r2);
        r2 = r6.is;
        r2.reset();
    L_0x002a:
        return;
    L_0x002b:
        r1 = 0;
        goto L_0x001b;
    L_0x002d:
        r2 = move-exception;
        if (r1 == 0) goto L_0x003d;
    L_0x0030:
        if (r0 == 0) goto L_0x003d;
    L_0x0032:
        r3 = r6.recordSize;
        r4 = (long) r3;
        r6.pushedBackBytes(r4);
        r3 = r6.is;
        r3.reset();
    L_0x003d:
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.tar.TarArchiveInputStream.tryToConsumeSecondEOFRecord():void");
    }

    public TarArchiveInputStream(InputStream is) {
        this(is, 10240, 512);
    }

    public TarArchiveInputStream(InputStream is, String encoding) {
        this(is, 10240, 512, encoding);
    }

    public TarArchiveInputStream(InputStream is, int blockSize) {
        this(is, blockSize, 512);
    }

    public TarArchiveInputStream(InputStream is, int blockSize, String encoding) {
        this(is, blockSize, 512, encoding);
    }

    public TarArchiveInputStream(InputStream is, int blockSize, int recordSize) {
        this(is, blockSize, recordSize, null);
    }

    public TarArchiveInputStream(InputStream is, int blockSize, int recordSize, String encoding) {
        this.smallBuf = new byte[256];
        this.globalPaxHeaders = new HashMap();
        this.is = is;
        this.hasHitEOF = false;
        this.encoding = encoding;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
        this.recordSize = recordSize;
        this.blockSize = blockSize;
    }

    public void close() throws IOException {
        this.is.close();
    }

    public int getRecordSize() {
        return this.recordSize;
    }

    public int available() throws IOException {
        if (isDirectory()) {
            return 0;
        }
        if (this.entrySize - this.entryOffset > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) (this.entrySize - this.entryOffset);
    }

    public long skip(long n) throws IOException {
        if (n <= 0 || isDirectory()) {
            return 0;
        }
        long skipped = this.is.skip(Math.min(n, this.entrySize - this.entryOffset));
        count(skipped);
        this.entryOffset += skipped;
        return skipped;
    }

    public boolean markSupported() {
        return false;
    }

    public void mark(int markLimit) {
    }

    public synchronized void reset() {
    }

    public TarArchiveEntry getNextTarEntry() throws IOException {
        if (this.hasHitEOF) {
            return null;
        }
        if (this.currEntry != null) {
            IOUtils.skip(this, Long.MAX_VALUE);
            skipRecordPadding();
        }
        byte[] headerBuf = getRecord();
        if (headerBuf == null) {
            this.currEntry = null;
            return null;
        }
        try {
            this.currEntry = new TarArchiveEntry(headerBuf, this.zipEncoding);
            this.entryOffset = 0;
            this.entrySize = this.currEntry.getSize();
            if (this.currEntry.isGNULongLinkEntry()) {
                byte[] longLinkData = getLongNameData();
                if (longLinkData == null) {
                    return null;
                }
                this.currEntry.setLinkName(this.zipEncoding.decode(longLinkData));
            }
            if (this.currEntry.isGNULongNameEntry()) {
                byte[] longNameData = getLongNameData();
                if (longNameData == null) {
                    return null;
                }
                this.currEntry.setName(this.zipEncoding.decode(longNameData));
            }
            if (this.currEntry.isGlobalPaxHeader()) {
                readGlobalPaxHeaders();
            }
            if (this.currEntry.isPaxHeader()) {
                paxHeaders();
            } else if (!this.globalPaxHeaders.isEmpty()) {
                applyPaxHeadersToCurrentEntry(this.globalPaxHeaders);
            }
            if (this.currEntry.isOldGNUSparse()) {
                readOldGNUSparse();
            }
            this.entrySize = this.currEntry.getSize();
            return this.currEntry;
        } catch (IllegalArgumentException e) {
            throw new IOException("Error detected parsing the header", e);
        }
    }

    private void skipRecordPadding() throws IOException {
        if (!isDirectory() && this.entrySize > 0 && this.entrySize % ((long) this.recordSize) != 0) {
            count(IOUtils.skip(this.is, (((long) this.recordSize) * ((this.entrySize / ((long) this.recordSize)) + 1)) - this.entrySize));
        }
    }

    protected byte[] getLongNameData() throws IOException {
        ByteArrayOutputStream longName = new ByteArrayOutputStream();
        while (true) {
            int length = read(this.smallBuf);
            if (length < 0) {
                break;
            }
            longName.write(this.smallBuf, 0, length);
        }
        getNextEntry();
        if (this.currEntry == null) {
            return null;
        }
        byte[] longNameData = longName.toByteArray();
        length = longNameData.length;
        while (length > 0 && longNameData[length - 1] == (byte) 0) {
            length--;
        }
        if (length == longNameData.length) {
            return longNameData;
        }
        byte[] l = new byte[length];
        System.arraycopy(longNameData, 0, l, 0, length);
        return l;
    }

    private byte[] getRecord() throws IOException {
        byte[] headerBuf = readRecord();
        this.hasHitEOF = isEOFRecord(headerBuf);
        if (!this.hasHitEOF || headerBuf == null) {
            return headerBuf;
        }
        tryToConsumeSecondEOFRecord();
        consumeRemainderOfLastBlock();
        return null;
    }

    protected boolean isEOFRecord(byte[] record) {
        return record == null || ArchiveUtils.isArrayZero(record, this.recordSize);
    }

    protected byte[] readRecord() throws IOException {
        byte[] record = new byte[this.recordSize];
        int readNow = IOUtils.readFully(this.is, record);
        count(readNow);
        if (readNow != this.recordSize) {
            return null;
        }
        return record;
    }

    private void readGlobalPaxHeaders() throws IOException {
        this.globalPaxHeaders = parsePaxHeaders(this);
        getNextEntry();
    }

    private void paxHeaders() throws IOException {
        Map<String, String> headers = parsePaxHeaders(this);
        getNextEntry();
        applyPaxHeadersToCurrentEntry(headers);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    java.util.Map<java.lang.String, java.lang.String> parsePaxHeaders(java.io.InputStream r15) throws java.io.IOException {
        /*
        r14 = this;
        r13 = -1;
        r3 = new java.util.HashMap;
        r10 = r14.globalPaxHeaders;
        r3.<init>(r10);
    L_0x0008:
        r5 = 0;
        r6 = 0;
    L_0x000a:
        r0 = r15.read();
        if (r0 == r13) goto L_0x0016;
    L_0x0010:
        r6 = r6 + 1;
        r10 = 10;
        if (r0 != r10) goto L_0x0019;
    L_0x0016:
        if (r0 != r13) goto L_0x0008;
    L_0x0018:
        return r3;
    L_0x0019:
        r10 = 32;
        if (r0 != r10) goto L_0x007b;
    L_0x001d:
        r1 = new java.io.ByteArrayOutputStream;
        r1.<init>();
    L_0x0022:
        r0 = r15.read();
        if (r0 == r13) goto L_0x0016;
    L_0x0028:
        r6 = r6 + 1;
        r10 = 61;
        if (r0 != r10) goto L_0x0076;
    L_0x002e:
        r10 = "UTF-8";
        r4 = r1.toString(r10);
        r8 = r5 - r6;
        r10 = 1;
        if (r8 != r10) goto L_0x003d;
    L_0x0039:
        r3.remove(r4);
        goto L_0x0016;
    L_0x003d:
        r7 = new byte[r8];
        r2 = org.apache.commons.compress.utils.IOUtils.readFully(r15, r7);
        if (r2 == r8) goto L_0x0068;
    L_0x0045:
        r10 = new java.io.IOException;
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r12 = "Failed to read Paxheader. Expected ";
        r11 = r11.append(r12);
        r11 = r11.append(r8);
        r12 = " bytes, read ";
        r11 = r11.append(r12);
        r11 = r11.append(r2);
        r11 = r11.toString();
        r10.<init>(r11);
        throw r10;
    L_0x0068:
        r9 = new java.lang.String;
        r10 = 0;
        r11 = r8 + -1;
        r12 = "UTF-8";
        r9.<init>(r7, r10, r11, r12);
        r3.put(r4, r9);
        goto L_0x0016;
    L_0x0076:
        r10 = (byte) r0;
        r1.write(r10);
        goto L_0x0022;
    L_0x007b:
        r5 = r5 * 10;
        r10 = r0 + -48;
        r5 = r5 + r10;
        goto L_0x000a;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.tar.TarArchiveInputStream.parsePaxHeaders(java.io.InputStream):java.util.Map<java.lang.String, java.lang.String>");
    }

    private void applyPaxHeadersToCurrentEntry(Map<String, String> headers) {
        for (Entry<String, String> ent : headers.entrySet()) {
            String key = (String) ent.getKey();
            String val = (String) ent.getValue();
            if (ClientCookie.PATH_ATTR.equals(key)) {
                this.currEntry.setName(val);
            } else if ("linkpath".equals(key)) {
                this.currEntry.setLinkName(val);
            } else if ("gid".equals(key)) {
                this.currEntry.setGroupId(Long.parseLong(val));
            } else if ("gname".equals(key)) {
                this.currEntry.setGroupName(val);
            } else if ("uid".equals(key)) {
                this.currEntry.setUserId(Long.parseLong(val));
            } else if ("uname".equals(key)) {
                this.currEntry.setUserName(val);
            } else if ("size".equals(key)) {
                this.currEntry.setSize(Long.parseLong(val));
            } else if ("mtime".equals(key)) {
                this.currEntry.setModTime((long) (Double.parseDouble(val) * 1000.0d));
            } else if ("SCHILY.devminor".equals(key)) {
                this.currEntry.setDevMinor(Integer.parseInt(val));
            } else if ("SCHILY.devmajor".equals(key)) {
                this.currEntry.setDevMajor(Integer.parseInt(val));
            } else if ("GNU.sparse.size".equals(key)) {
                this.currEntry.fillGNUSparse0xData(headers);
            } else if ("GNU.sparse.realsize".equals(key)) {
                this.currEntry.fillGNUSparse1xData(headers);
            } else if ("SCHILY.filetype".equals(key) && "sparse".equals(val)) {
                this.currEntry.fillStarSparseData(headers);
            }
        }
    }

    private void readOldGNUSparse() throws IOException {
        if (this.currEntry.isExtended()) {
            byte[] headerBuf;
            do {
                headerBuf = getRecord();
                if (headerBuf == null) {
                    this.currEntry = null;
                    return;
                }
            } while (new TarArchiveSparseEntry(headerBuf).isExtended());
        }
    }

    private boolean isDirectory() {
        return this.currEntry != null && this.currEntry.isDirectory();
    }

    public ArchiveEntry getNextEntry() throws IOException {
        return getNextTarEntry();
    }

    public int read(byte[] buf, int offset, int numToRead) throws IOException {
        if (this.hasHitEOF || isDirectory() || this.entryOffset >= this.entrySize) {
            return -1;
        }
        if (this.currEntry == null) {
            throw new IllegalStateException("No current tar entry");
        }
        numToRead = Math.min(numToRead, available());
        int totalRead = this.is.read(buf, offset, numToRead);
        if (totalRead != -1) {
            count(totalRead);
            this.entryOffset += (long) totalRead;
        } else if (numToRead > 0) {
            throw new IOException("Truncated TAR archive");
        } else {
            this.hasHitEOF = true;
        }
        return totalRead;
    }

    public boolean canReadEntryData(ArchiveEntry ae) {
        if (!(ae instanceof TarArchiveEntry) || ((TarArchiveEntry) ae).isSparse()) {
            return false;
        }
        return true;
    }

    public TarArchiveEntry getCurrentEntry() {
        return this.currEntry;
    }

    protected final void setCurrentEntry(TarArchiveEntry e) {
        this.currEntry = e;
    }

    protected final boolean isAtEOF() {
        return this.hasHitEOF;
    }

    protected final void setAtEOF(boolean b) {
        this.hasHitEOF = b;
    }

    private void consumeRemainderOfLastBlock() throws IOException {
        long bytesReadOfLastBlock = getBytesRead() % ((long) this.blockSize);
        if (bytesReadOfLastBlock > 0) {
            count(IOUtils.skip(this.is, ((long) this.blockSize) - bytesReadOfLastBlock));
        }
    }

    public static boolean matches(byte[] signature, int length) {
        if (length < 265) {
            return false;
        }
        if (ArchiveUtils.matchAsciiBuffer("ustar\u0000", signature, 257, 6) && ArchiveUtils.matchAsciiBuffer(TarConstants.VERSION_POSIX, signature, TarConstants.VERSION_OFFSET, 2)) {
            return true;
        }
        if (ArchiveUtils.matchAsciiBuffer(TarConstants.MAGIC_GNU, signature, 257, 6) && (ArchiveUtils.matchAsciiBuffer(TarConstants.VERSION_GNU_SPACE, signature, TarConstants.VERSION_OFFSET, 2) || ArchiveUtils.matchAsciiBuffer(TarConstants.VERSION_GNU_ZERO, signature, TarConstants.VERSION_OFFSET, 2))) {
            return true;
        }
        if (ArchiveUtils.matchAsciiBuffer("ustar\u0000", signature, 257, 6) && ArchiveUtils.matchAsciiBuffer(TarConstants.VERSION_ANT, signature, TarConstants.VERSION_OFFSET, 2)) {
            return true;
        }
        return false;
    }
}
