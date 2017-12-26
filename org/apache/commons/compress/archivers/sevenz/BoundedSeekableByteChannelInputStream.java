package org.apache.commons.compress.archivers.sevenz;

import android.support.v4.media.session.PlaybackStateCompat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

class BoundedSeekableByteChannelInputStream extends InputStream {
    private static final int MAX_BUF_LEN = 8192;
    private final ByteBuffer buffer;
    private long bytesRemaining;
    private final SeekableByteChannel channel;

    public BoundedSeekableByteChannelInputStream(SeekableByteChannel channel, long size) {
        this.channel = channel;
        this.bytesRemaining = size;
        if (size >= PlaybackStateCompat.ACTION_PLAY_FROM_URI || size <= 0) {
            this.buffer = ByteBuffer.allocate(8192);
        } else {
            this.buffer = ByteBuffer.allocate((int) size);
        }
    }

    public int read() throws IOException {
        if (this.bytesRemaining <= 0) {
            return -1;
        }
        this.bytesRemaining--;
        int read = read(1);
        if (read < 0) {
            return read;
        }
        return this.buffer.get() & 255;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.bytesRemaining == 0) {
            return -1;
        }
        ByteBuffer buf;
        int bytesRead;
        int bytesToRead = len;
        if (((long) bytesToRead) > this.bytesRemaining) {
            bytesToRead = (int) this.bytesRemaining;
        }
        if (bytesToRead <= this.buffer.capacity()) {
            buf = this.buffer;
            bytesRead = read(bytesToRead);
        } else {
            buf = ByteBuffer.allocate(bytesToRead);
            bytesRead = this.channel.read(buf);
            buf.flip();
        }
        if (bytesRead < 0) {
            return bytesRead;
        }
        buf.get(b, off, bytesRead);
        this.bytesRemaining -= (long) bytesRead;
        return bytesRead;
    }

    private int read(int len) throws IOException {
        this.buffer.rewind().limit(len);
        int read = this.channel.read(this.buffer);
        this.buffer.flip();
        return read;
    }

    public void close() {
    }
}
