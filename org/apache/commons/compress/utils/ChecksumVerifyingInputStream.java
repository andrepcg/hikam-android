package org.apache.commons.compress.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Checksum;

public class ChecksumVerifyingInputStream extends InputStream {
    private long bytesRemaining;
    private final Checksum checksum;
    private final long expectedChecksum;
    private final InputStream in;

    public ChecksumVerifyingInputStream(Checksum checksum, InputStream in, long size, long expectedChecksum) {
        this.checksum = checksum;
        this.in = in;
        this.expectedChecksum = expectedChecksum;
        this.bytesRemaining = size;
    }

    public int read() throws IOException {
        if (this.bytesRemaining <= 0) {
            return -1;
        }
        int ret = this.in.read();
        if (ret >= 0) {
            this.checksum.update(ret);
            this.bytesRemaining--;
        }
        if (this.bytesRemaining != 0 || this.expectedChecksum == this.checksum.getValue()) {
            return ret;
        }
        throw new IOException("Checksum verification failed");
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int ret = this.in.read(b, off, len);
        if (ret >= 0) {
            this.checksum.update(b, off, ret);
            this.bytesRemaining -= (long) ret;
        }
        if (this.bytesRemaining > 0 || this.expectedChecksum == this.checksum.getValue()) {
            return ret;
        }
        throw new IOException("Checksum verification failed");
    }

    public long skip(long n) throws IOException {
        if (read() >= 0) {
            return 1;
        }
        return 0;
    }

    public void close() throws IOException {
        this.in.close();
    }
}
