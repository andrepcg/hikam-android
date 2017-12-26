package org.apache.commons.compress.archivers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public abstract class ArchiveOutputStream extends OutputStream {
    static final int BYTE_MASK = 255;
    private long bytesWritten = 0;
    private final byte[] oneByte = new byte[1];

    public abstract void closeArchiveEntry() throws IOException;

    public abstract ArchiveEntry createArchiveEntry(File file, String str) throws IOException;

    public abstract void finish() throws IOException;

    public abstract void putArchiveEntry(ArchiveEntry archiveEntry) throws IOException;

    public void write(int b) throws IOException {
        this.oneByte[0] = (byte) (b & 255);
        write(this.oneByte, 0, 1);
    }

    protected void count(int written) {
        count((long) written);
    }

    protected void count(long written) {
        if (written != -1) {
            this.bytesWritten += written;
        }
    }

    @Deprecated
    public int getCount() {
        return (int) this.bytesWritten;
    }

    public long getBytesWritten() {
        return this.bytesWritten;
    }

    public boolean canWriteEntryData(ArchiveEntry archiveEntry) {
        return true;
    }
}
