package org.jboss.netty.handler.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.jboss.netty.buffer.ChannelBuffers;

public class ChunkedNioFile implements ChunkedInput {
    private final int chunkSize;
    private final long endOffset;
    private final FileChannel in;
    private long offset;
    private final long startOffset;

    public ChunkedNioFile(File in) throws IOException {
        this(new FileInputStream(in).getChannel());
    }

    public ChunkedNioFile(File in, int chunkSize) throws IOException {
        this(new FileInputStream(in).getChannel(), chunkSize);
    }

    public ChunkedNioFile(FileChannel in) throws IOException {
        this(in, 8192);
    }

    public ChunkedNioFile(FileChannel in, int chunkSize) throws IOException {
        this(in, 0, in.size(), chunkSize);
    }

    public ChunkedNioFile(FileChannel in, long offset, long length, int chunkSize) throws IOException {
        if (in == null) {
            throw new NullPointerException("in");
        } else if (offset < 0) {
            throw new IllegalArgumentException("offset: " + offset + " (expected: 0 or greater)");
        } else if (length < 0) {
            throw new IllegalArgumentException("length: " + length + " (expected: 0 or greater)");
        } else if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize: " + chunkSize + " (expected: a positive integer)");
        } else {
            if (offset != 0) {
                in.position(offset);
            }
            this.in = in;
            this.chunkSize = chunkSize;
            this.startOffset = offset;
            this.offset = offset;
            this.endOffset = offset + length;
        }
    }

    public long getStartOffset() {
        return this.startOffset;
    }

    public long getEndOffset() {
        return this.endOffset;
    }

    public long getCurrentOffset() {
        return this.offset;
    }

    public boolean hasNextChunk() throws Exception {
        return this.offset < this.endOffset && this.in.isOpen();
    }

    public boolean isEndOfInput() throws Exception {
        return !hasNextChunk();
    }

    public void close() throws Exception {
        this.in.close();
    }

    public Object nextChunk() throws Exception {
        long offset = this.offset;
        if (offset >= this.endOffset) {
            return null;
        }
        int chunkSize = (int) Math.min((long) this.chunkSize, this.endOffset - offset);
        byte[] chunkArray = new byte[chunkSize];
        ByteBuffer chunk = ByteBuffer.wrap(chunkArray);
        int readBytes = 0;
        do {
            int localReadBytes = this.in.read(chunk);
            if (localReadBytes < 0) {
                break;
            }
            readBytes += localReadBytes;
        } while (readBytes != chunkSize);
        this.offset += (long) readBytes;
        return ChannelBuffers.wrappedBuffer(chunkArray);
    }
}
