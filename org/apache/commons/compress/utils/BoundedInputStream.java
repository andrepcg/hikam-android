package org.apache.commons.compress.utils;

import java.io.IOException;
import java.io.InputStream;

public class BoundedInputStream extends InputStream {
    private long bytesRemaining;
    private final InputStream in;

    public BoundedInputStream(InputStream in, long size) {
        this.in = in;
        this.bytesRemaining = size;
    }

    public int read() throws IOException {
        if (this.bytesRemaining <= 0) {
            return -1;
        }
        this.bytesRemaining--;
        return this.in.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.bytesRemaining == 0) {
            return -1;
        }
        int bytesToRead = len;
        if (((long) bytesToRead) > this.bytesRemaining) {
            bytesToRead = (int) this.bytesRemaining;
        }
        int bytesRead = this.in.read(b, off, bytesToRead);
        if (bytesRead < 0) {
            return bytesRead;
        }
        this.bytesRemaining -= (long) bytesRead;
        return bytesRead;
    }

    public void close() {
    }
}
