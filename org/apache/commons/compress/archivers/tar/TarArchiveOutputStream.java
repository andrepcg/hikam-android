package org.apache.commons.compress.archivers.tar;

import android.support.v4.media.TransportMediator;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.utils.CountingOutputStream;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.protocol.HTTP;
import org.jboss.netty.handler.codec.rtsp.RtspHeaders.Values;

public class TarArchiveOutputStream extends ArchiveOutputStream {
    private static final ZipEncoding ASCII = ZipEncodingHelper.getZipEncoding(HTTP.ASCII);
    public static final int BIGNUMBER_ERROR = 0;
    public static final int BIGNUMBER_POSIX = 2;
    public static final int BIGNUMBER_STAR = 1;
    public static final int LONGFILE_ERROR = 0;
    public static final int LONGFILE_GNU = 2;
    public static final int LONGFILE_POSIX = 3;
    public static final int LONGFILE_TRUNCATE = 1;
    private boolean addPaxHeadersForNonAsciiNames;
    private final byte[] assemBuf;
    private int assemLen;
    private int bigNumberMode;
    private boolean closed;
    private long currBytes;
    private String currName;
    private long currSize;
    final String encoding;
    private boolean finished;
    private boolean haveUnclosedEntry;
    private int longFileMode;
    private final OutputStream out;
    private final byte[] recordBuf;
    private final int recordSize;
    private final int recordsPerBlock;
    private int recordsWritten;
    private final ZipEncoding zipEncoding;

    public TarArchiveOutputStream(OutputStream os) {
        this(os, 10240, 512);
    }

    public TarArchiveOutputStream(OutputStream os, String encoding) {
        this(os, 10240, 512, encoding);
    }

    public TarArchiveOutputStream(OutputStream os, int blockSize) {
        this(os, blockSize, 512);
    }

    public TarArchiveOutputStream(OutputStream os, int blockSize, String encoding) {
        this(os, blockSize, 512, encoding);
    }

    public TarArchiveOutputStream(OutputStream os, int blockSize, int recordSize) {
        this(os, blockSize, recordSize, null);
    }

    public TarArchiveOutputStream(OutputStream os, int blockSize, int recordSize, String encoding) {
        this.longFileMode = 0;
        this.bigNumberMode = 0;
        this.closed = false;
        this.haveUnclosedEntry = false;
        this.finished = false;
        this.addPaxHeadersForNonAsciiNames = false;
        this.out = new CountingOutputStream(os);
        this.encoding = encoding;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
        this.assemLen = 0;
        this.assemBuf = new byte[recordSize];
        this.recordBuf = new byte[recordSize];
        this.recordSize = recordSize;
        this.recordsPerBlock = blockSize / recordSize;
    }

    public void setLongFileMode(int longFileMode) {
        this.longFileMode = longFileMode;
    }

    public void setBigNumberMode(int bigNumberMode) {
        this.bigNumberMode = bigNumberMode;
    }

    public void setAddPaxHeadersForNonAsciiNames(boolean b) {
        this.addPaxHeadersForNonAsciiNames = b;
    }

    @Deprecated
    public int getCount() {
        return (int) getBytesWritten();
    }

    public long getBytesWritten() {
        return ((CountingOutputStream) this.out).getBytesWritten();
    }

    public void finish() throws IOException {
        if (this.finished) {
            throw new IOException("This archive has already been finished");
        } else if (this.haveUnclosedEntry) {
            throw new IOException("This archives contains unclosed entries.");
        } else {
            writeEOFRecord();
            writeEOFRecord();
            padAsNeeded();
            this.out.flush();
            this.finished = true;
        }
    }

    public void close() throws IOException {
        if (!this.finished) {
            finish();
        }
        if (!this.closed) {
            this.out.close();
            this.closed = true;
        }
    }

    public int getRecordSize() {
        return this.recordSize;
    }

    public void putArchiveEntry(ArchiveEntry archiveEntry) throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        boolean paxHeaderContainsLinkPath;
        TarArchiveEntry entry = (TarArchiveEntry) archiveEntry;
        Map<String, String> paxHeaders = new HashMap();
        String entryName = entry.getName();
        boolean paxHeaderContainsPath = handleLongName(entry, entryName, paxHeaders, ClientCookie.PATH_ATTR, TarConstants.LF_GNUTYPE_LONGNAME, "file name");
        String linkName = entry.getLinkName();
        if (linkName != null && linkName.length() > 0) {
            if (handleLongName(entry, linkName, paxHeaders, "linkpath", TarConstants.LF_GNUTYPE_LONGLINK, "link name")) {
                paxHeaderContainsLinkPath = true;
                if (this.bigNumberMode == 2) {
                    addPaxHeadersForBigNumbers(paxHeaders, entry);
                } else if (this.bigNumberMode != 1) {
                    failForBigNumbers(entry);
                }
                if (!(!this.addPaxHeadersForNonAsciiNames || paxHeaderContainsPath || ASCII.canEncode(entryName))) {
                    paxHeaders.put(ClientCookie.PATH_ATTR, entryName);
                }
                if (this.addPaxHeadersForNonAsciiNames && !paxHeaderContainsLinkPath && ((entry.isLink() || entry.isSymbolicLink()) && !ASCII.canEncode(linkName))) {
                    paxHeaders.put("linkpath", linkName);
                }
                if (paxHeaders.size() > 0) {
                    writePaxHeaders(entry, entryName, paxHeaders);
                }
                entry.writeEntryHeader(this.recordBuf, this.zipEncoding, this.bigNumberMode != 1);
                writeRecord(this.recordBuf);
                this.currBytes = 0;
                if (entry.isDirectory()) {
                    this.currSize = entry.getSize();
                } else {
                    this.currSize = 0;
                }
                this.currName = entryName;
                this.haveUnclosedEntry = true;
            }
        }
        paxHeaderContainsLinkPath = false;
        if (this.bigNumberMode == 2) {
            addPaxHeadersForBigNumbers(paxHeaders, entry);
        } else if (this.bigNumberMode != 1) {
            failForBigNumbers(entry);
        }
        paxHeaders.put(ClientCookie.PATH_ATTR, entryName);
        paxHeaders.put("linkpath", linkName);
        if (paxHeaders.size() > 0) {
            writePaxHeaders(entry, entryName, paxHeaders);
        }
        if (this.bigNumberMode != 1) {
        }
        entry.writeEntryHeader(this.recordBuf, this.zipEncoding, this.bigNumberMode != 1);
        writeRecord(this.recordBuf);
        this.currBytes = 0;
        if (entry.isDirectory()) {
            this.currSize = entry.getSize();
        } else {
            this.currSize = 0;
        }
        this.currName = entryName;
        this.haveUnclosedEntry = true;
    }

    public void closeArchiveEntry() throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        } else if (this.haveUnclosedEntry) {
            if (this.assemLen > 0) {
                for (int i = this.assemLen; i < this.assemBuf.length; i++) {
                    this.assemBuf[i] = (byte) 0;
                }
                writeRecord(this.assemBuf);
                this.currBytes += (long) this.assemLen;
                this.assemLen = 0;
            }
            if (this.currBytes < this.currSize) {
                throw new IOException("entry '" + this.currName + "' closed at '" + this.currBytes + "' before the '" + this.currSize + "' bytes specified in the header were written");
            }
            this.haveUnclosedEntry = false;
        } else {
            throw new IOException("No current entry to close");
        }
    }

    public void write(byte[] wBuf, int wOffset, int numToWrite) throws IOException {
        if (!this.haveUnclosedEntry) {
            throw new IllegalStateException("No current tar entry");
        } else if (this.currBytes + ((long) numToWrite) > this.currSize) {
            throw new IOException("request to write '" + numToWrite + "' bytes exceeds size in header of '" + this.currSize + "' bytes for entry '" + this.currName + "'");
        } else {
            if (this.assemLen > 0) {
                if (this.assemLen + numToWrite >= this.recordBuf.length) {
                    int aLen = this.recordBuf.length - this.assemLen;
                    System.arraycopy(this.assemBuf, 0, this.recordBuf, 0, this.assemLen);
                    System.arraycopy(wBuf, wOffset, this.recordBuf, this.assemLen, aLen);
                    writeRecord(this.recordBuf);
                    this.currBytes += (long) this.recordBuf.length;
                    wOffset += aLen;
                    numToWrite -= aLen;
                    this.assemLen = 0;
                } else {
                    System.arraycopy(wBuf, wOffset, this.assemBuf, this.assemLen, numToWrite);
                    wOffset += numToWrite;
                    this.assemLen += numToWrite;
                    numToWrite = 0;
                }
            }
            while (numToWrite > 0) {
                if (numToWrite < this.recordBuf.length) {
                    System.arraycopy(wBuf, wOffset, this.assemBuf, this.assemLen, numToWrite);
                    this.assemLen += numToWrite;
                    return;
                }
                writeRecord(wBuf, wOffset);
                int num = this.recordBuf.length;
                this.currBytes += (long) num;
                numToWrite -= num;
                wOffset += num;
            }
        }
    }

    void writePaxHeaders(TarArchiveEntry entry, String entryName, Map<String, String> headers) throws IOException {
        String name = "./PaxHeaders.X/" + stripTo7Bits(entryName);
        if (name.length() >= 100) {
            name = name.substring(0, 99);
        }
        TarArchiveEntry pex = new TarArchiveEntry(name, (byte) TarConstants.LF_PAX_EXTENDED_HEADER_LC);
        transferModTime(entry, pex);
        StringWriter w = new StringWriter();
        for (Entry<String, String> h : headers.entrySet()) {
            String key = (String) h.getKey();
            String value = (String) h.getValue();
            int len = ((key.length() + value.length()) + 3) + 2;
            String line = len + " " + key + "=" + value + "\n";
            int actualLength = line.getBytes("UTF-8").length;
            while (len != actualLength) {
                len = actualLength;
                line = len + " " + key + "=" + value + "\n";
                actualLength = line.getBytes("UTF-8").length;
            }
            w.write(line);
        }
        byte[] data = w.toString().getBytes("UTF-8");
        pex.setSize((long) data.length);
        putArchiveEntry(pex);
        write(data);
        closeArchiveEntry();
    }

    private String stripTo7Bits(String name) {
        int length = name.length();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char stripped = (char) (name.charAt(i) & TransportMediator.KEYCODE_MEDIA_PAUSE);
            if (shouldBeReplaced(stripped)) {
                result.append("_");
            } else {
                result.append(stripped);
            }
        }
        return result.toString();
    }

    private boolean shouldBeReplaced(char c) {
        return c == '\u0000' || c == '/' || c == '\\';
    }

    private void writeEOFRecord() throws IOException {
        Arrays.fill(this.recordBuf, (byte) 0);
        writeRecord(this.recordBuf);
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
        if (!this.finished) {
            return new TarArchiveEntry(inputFile, entryName);
        }
        throw new IOException("Stream has already been finished");
    }

    private void writeRecord(byte[] record) throws IOException {
        if (record.length != this.recordSize) {
            throw new IOException("record to write has length '" + record.length + "' which is not the record size of '" + this.recordSize + "'");
        }
        this.out.write(record);
        this.recordsWritten++;
    }

    private void writeRecord(byte[] buf, int offset) throws IOException {
        if (this.recordSize + offset > buf.length) {
            throw new IOException("record has length '" + buf.length + "' with offset '" + offset + "' which is less than the record size of '" + this.recordSize + "'");
        }
        this.out.write(buf, offset, this.recordSize);
        this.recordsWritten++;
    }

    private void padAsNeeded() throws IOException {
        int start = this.recordsWritten % this.recordsPerBlock;
        if (start != 0) {
            for (int i = start; i < this.recordsPerBlock; i++) {
                writeEOFRecord();
            }
        }
    }

    private void addPaxHeadersForBigNumbers(Map<String, String> paxHeaders, TarArchiveEntry entry) {
        addPaxHeaderForBigNumber(paxHeaders, "size", entry.getSize(), TarConstants.MAXSIZE);
        addPaxHeaderForBigNumber(paxHeaders, "gid", entry.getLongGroupId(), TarConstants.MAXID);
        addPaxHeaderForBigNumber(paxHeaders, "mtime", entry.getModTime().getTime() / 1000, TarConstants.MAXSIZE);
        addPaxHeaderForBigNumber(paxHeaders, "uid", entry.getLongUserId(), TarConstants.MAXID);
        addPaxHeaderForBigNumber(paxHeaders, "SCHILY.devmajor", (long) entry.getDevMajor(), TarConstants.MAXID);
        addPaxHeaderForBigNumber(paxHeaders, "SCHILY.devminor", (long) entry.getDevMinor(), TarConstants.MAXID);
        failForBigNumber(Values.MODE, (long) entry.getMode(), TarConstants.MAXID);
    }

    private void addPaxHeaderForBigNumber(Map<String, String> paxHeaders, String header, long value, long maxValue) {
        if (value < 0 || value > maxValue) {
            paxHeaders.put(header, String.valueOf(value));
        }
    }

    private void failForBigNumbers(TarArchiveEntry entry) {
        failForBigNumber("entry size", entry.getSize(), TarConstants.MAXSIZE);
        failForBigNumberWithPosixMessage("group id", entry.getLongGroupId(), TarConstants.MAXID);
        failForBigNumber("last modification time", entry.getModTime().getTime() / 1000, TarConstants.MAXSIZE);
        failForBigNumber("user id", entry.getLongUserId(), TarConstants.MAXID);
        failForBigNumber(Values.MODE, (long) entry.getMode(), TarConstants.MAXID);
        failForBigNumber("major device number", (long) entry.getDevMajor(), TarConstants.MAXID);
        failForBigNumber("minor device number", (long) entry.getDevMinor(), TarConstants.MAXID);
    }

    private void failForBigNumber(String field, long value, long maxValue) {
        failForBigNumber(field, value, maxValue, "");
    }

    private void failForBigNumberWithPosixMessage(String field, long value, long maxValue) {
        failForBigNumber(field, value, maxValue, " Use STAR or POSIX extensions to overcome this limit");
    }

    private void failForBigNumber(String field, long value, long maxValue, String additionalMsg) {
        if (value < 0 || value > maxValue) {
            throw new RuntimeException(field + " '" + value + "' is too big ( > " + maxValue + " )." + additionalMsg);
        }
    }

    private boolean handleLongName(TarArchiveEntry entry, String name, Map<String, String> paxHeaders, String paxHeaderName, byte linkType, String fieldName) throws IOException {
        ByteBuffer encodedName = this.zipEncoding.encode(name);
        int len = encodedName.limit() - encodedName.position();
        if (len >= 100) {
            if (this.longFileMode == 3) {
                paxHeaders.put(paxHeaderName, name);
                return true;
            } else if (this.longFileMode == 2) {
                TarArchiveEntry longLinkEntry = new TarArchiveEntry(TarConstants.GNU_LONGLINK, linkType);
                longLinkEntry.setSize(((long) len) + 1);
                transferModTime(entry, longLinkEntry);
                putArchiveEntry(longLinkEntry);
                write(encodedName.array(), encodedName.arrayOffset(), len);
                write(0);
                closeArchiveEntry();
            } else if (this.longFileMode != 1) {
                throw new RuntimeException(fieldName + " '" + name + "' is too long ( > " + 100 + " bytes)");
            }
        }
        return false;
    }

    private void transferModTime(TarArchiveEntry from, TarArchiveEntry to) {
        Date fromModTime = from.getModTime();
        long fromModTimeSeconds = fromModTime.getTime() / 1000;
        if (fromModTimeSeconds < 0 || fromModTimeSeconds > TarConstants.MAXSIZE) {
            fromModTime = new Date(0);
        }
        to.setModTime(fromModTime);
    }
}
