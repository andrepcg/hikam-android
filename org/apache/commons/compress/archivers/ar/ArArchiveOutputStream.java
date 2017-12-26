package org.apache.commons.compress.archivers.ar;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.utils.ArchiveUtils;

public class ArArchiveOutputStream extends ArchiveOutputStream {
    public static final int LONGFILE_BSD = 1;
    public static final int LONGFILE_ERROR = 0;
    private long entryOffset = 0;
    private boolean finished = false;
    private boolean haveUnclosedEntry = false;
    private int longFileMode = 0;
    private final OutputStream out;
    private ArArchiveEntry prevEntry;

    public ArArchiveOutputStream(OutputStream pOut) {
        this.out = pOut;
    }

    public void setLongFileMode(int longFileMode) {
        this.longFileMode = longFileMode;
    }

    private long writeArchiveHeader() throws IOException {
        byte[] header = ArchiveUtils.toAsciiBytes(ArArchiveEntry.HEADER);
        this.out.write(header);
        return (long) header.length;
    }

    public void closeArchiveEntry() throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        } else if (this.prevEntry == null || !this.haveUnclosedEntry) {
            throw new IOException("No current entry to close");
        } else {
            if (this.entryOffset % 2 != 0) {
                this.out.write(10);
            }
            this.haveUnclosedEntry = false;
        }
    }

    public void putArchiveEntry(ArchiveEntry pEntry) throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        ArArchiveEntry pArEntry = (ArArchiveEntry) pEntry;
        if (this.prevEntry == null) {
            writeArchiveHeader();
        } else if (this.prevEntry.getLength() != this.entryOffset) {
            throw new IOException("length does not match entry (" + this.prevEntry.getLength() + " != " + this.entryOffset);
        } else if (this.haveUnclosedEntry) {
            closeArchiveEntry();
        }
        this.prevEntry = pArEntry;
        writeEntryHeader(pArEntry);
        this.entryOffset = 0;
        this.haveUnclosedEntry = true;
    }

    private long fill(long pOffset, long pNewOffset, char pFill) throws IOException {
        long diff = pNewOffset - pOffset;
        if (diff > 0) {
            for (int i = 0; ((long) i) < diff; i++) {
                write(pFill);
            }
        }
        return pNewOffset;
    }

    private long write(String data) throws IOException {
        byte[] bytes = data.getBytes("ascii");
        write(bytes);
        return (long) bytes.length;
    }

    private long writeEntryHeader(ArArchiveEntry pEntry) throws IOException {
        boolean mustAppendName = false;
        String n = pEntry.getName();
        if (this.longFileMode != 0 || n.length() <= 16) {
            long offset;
            if (1 != this.longFileMode || (n.length() <= 16 && !n.contains(" "))) {
                offset = 0 + write(n);
            } else {
                mustAppendName = true;
                offset = 0 + write("#1/" + String.valueOf(n.length()));
            }
            offset = fill(offset, 16, ' ');
            String m = "" + pEntry.getLastModified();
            if (m.length() > 12) {
                throw new IOException("modified too long");
            }
            offset = fill(offset + write(m), 28, ' ');
            String u = "" + pEntry.getUserId();
            if (u.length() > 6) {
                throw new IOException("userid too long");
            }
            offset = fill(offset + write(u), 34, ' ');
            String g = "" + pEntry.getGroupId();
            if (g.length() > 6) {
                throw new IOException("groupid too long");
            }
            offset = fill(offset + write(g), 40, ' ');
            String fm = "" + Integer.toString(pEntry.getMode(), 8);
            if (fm.length() > 8) {
                throw new IOException("filemode too long");
            }
            offset = fill(offset + write(fm), 48, ' ');
            String s = String.valueOf(pEntry.getLength() + ((long) (mustAppendName ? n.length() : 0)));
            if (s.length() > 10) {
                throw new IOException("size too long");
            }
            offset = fill(offset + write(s), 58, ' ') + write(ArArchiveEntry.TRAILER);
            if (mustAppendName) {
                return offset + write(n);
            }
            return offset;
        }
        throw new IOException("filename too long, > 16 chars: " + n);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        this.out.write(b, off, len);
        count(len);
        this.entryOffset += (long) len;
    }

    public void close() throws IOException {
        if (!this.finished) {
            finish();
        }
        this.out.close();
        this.prevEntry = null;
    }

    public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
        if (!this.finished) {
            return new ArArchiveEntry(inputFile, entryName);
        }
        throw new IOException("Stream has already been finished");
    }

    public void finish() throws IOException {
        if (this.haveUnclosedEntry) {
            throw new IOException("This archive contains unclosed entries.");
        } else if (this.finished) {
            throw new IOException("This archive has already been finished");
        } else {
            this.finished = true;
        }
    }
}
