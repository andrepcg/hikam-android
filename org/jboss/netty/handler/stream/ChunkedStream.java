package org.jboss.netty.handler.stream;

import java.io.InputStream;
import java.io.PushbackInputStream;
import org.jboss.netty.buffer.ChannelBuffers;

public class ChunkedStream implements ChunkedInput {
    static final int DEFAULT_CHUNK_SIZE = 8192;
    private final int chunkSize;
    private final PushbackInputStream in;
    private long offset;

    public ChunkedStream(InputStream in) {
        this(in, 8192);
    }

    public ChunkedStream(InputStream in, int chunkSize) {
        if (in == null) {
            throw new NullPointerException("in");
        } else if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize: " + chunkSize + " (expected: a positive integer)");
        } else {
            if (in instanceof PushbackInputStream) {
                this.in = (PushbackInputStream) in;
            } else {
                this.in = new PushbackInputStream(in);
            }
            this.chunkSize = chunkSize;
        }
    }

    public long getTransferredBytes() {
        return this.offset;
    }

    public boolean hasNextChunk() throws Exception {
        int b = this.in.read();
        if (b < 0) {
            return false;
        }
        this.in.unread(b);
        return true;
    }

    public boolean isEndOfInput() throws Exception {
        return !hasNextChunk();
    }

    public void close() throws Exception {
        this.in.close();
    }

    public Object nextChunk() throws Exception {
        if (!hasNextChunk()) {
            return null;
        }
        int chunkSize;
        if (this.in.available() <= 0) {
            chunkSize = this.chunkSize;
        } else {
            chunkSize = Math.min(this.chunkSize, this.in.available());
        }
        byte[] chunk = new byte[chunkSize];
        int readBytes = 0;
        do {
            int localReadBytes = this.in.read(chunk, readBytes, chunkSize - readBytes);
            if (localReadBytes < 0) {
                break;
            }
            readBytes += localReadBytes;
            this.offset += (long) localReadBytes;
        } while (readBytes != chunkSize);
        return ChannelBuffers.wrappedBuffer(chunk, 0, readBytes);
    }
}
