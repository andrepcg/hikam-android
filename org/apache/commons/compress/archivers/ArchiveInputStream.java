package org.apache.commons.compress.archivers;

import java.io.IOException;
import java.io.InputStream;

public abstract class ArchiveInputStream extends InputStream {
    private static final int BYTE_MASK = 255;
    private long bytesRead = 0;
    private final byte[] single = new byte[1];

    public abstract ArchiveEntry getNextEntry() throws IOException;

    public int read() throws IOException {
        if (read(this.single, 0, 1) == -1) {
            return -1;
        }
        return this.single[0] & 255;
    }

    protected void count(int read) {
        count((long) read);
    }

    protected void count(long read) {
        if (read != -1) {
            this.bytesRead += read;
        }
    }

    protected void pushedBackBytes(long pushedBack) {
        this.bytesRead -= pushedBack;
    }

    @Deprecated
    public int getCount() {
        return (int) this.bytesRead;
    }

    public long getBytesRead() {
        return this.bytesRead;
    }

    public boolean canReadEntryData(ArchiveEntry archiveEntry) {
        return true;
    }
}
