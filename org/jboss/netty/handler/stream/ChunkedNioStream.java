package org.jboss.netty.handler.stream;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import org.jboss.netty.buffer.ChannelBuffers;

public class ChunkedNioStream implements ChunkedInput {
    private final ByteBuffer byteBuffer;
    private final int chunkSize;
    private final ReadableByteChannel in;
    private long offset;

    public ChunkedNioStream(ReadableByteChannel in) {
        this(in, 8192);
    }

    public ChunkedNioStream(ReadableByteChannel in, int chunkSize) {
        if (in == null) {
            throw new NullPointerException("in");
        } else if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize: " + chunkSize + " (expected: a positive integer)");
        } else {
            this.in = in;
            this.offset = 0;
            this.chunkSize = chunkSize;
            this.byteBuffer = ByteBuffer.allocate(chunkSize);
        }
    }

    public long getTransferredBytes() {
        return this.offset;
    }

    public boolean hasNextChunk() throws Exception {
        if (this.byteBuffer.position() > 0) {
            return true;
        }
        if (!this.in.isOpen()) {
            return false;
        }
        int b = this.in.read(this.byteBuffer);
        if (b < 0) {
            return false;
        }
        this.offset += (long) b;
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
        int readBytes = this.byteBuffer.position();
        do {
            int localReadBytes = this.in.read(this.byteBuffer);
            if (localReadBytes < 0) {
                break;
            }
            readBytes += localReadBytes;
            this.offset += (long) localReadBytes;
        } while (readBytes != this.chunkSize);
        this.byteBuffer.flip();
        Object buffer = ChannelBuffers.copiedBuffer(this.byteBuffer);
        this.byteBuffer.clear();
        return buffer;
    }
}
