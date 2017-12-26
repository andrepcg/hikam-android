package org.jboss.netty.handler.stream;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.jboss.netty.buffer.ChannelBuffers;

public class ChunkedFile implements ChunkedInput {
    private final int chunkSize;
    private final long endOffset;
    private final RandomAccessFile file;
    private long offset;
    private final long startOffset;

    public ChunkedFile(File file) throws IOException {
        this(file, 8192);
    }

    public ChunkedFile(File file, int chunkSize) throws IOException {
        this(new RandomAccessFile(file, "r"), chunkSize);
    }

    public ChunkedFile(RandomAccessFile file) throws IOException {
        this(file, 8192);
    }

    public ChunkedFile(RandomAccessFile file, int chunkSize) throws IOException {
        this(file, 0, file.length(), chunkSize);
    }

    public ChunkedFile(RandomAccessFile file, long offset, long length, int chunkSize) throws IOException {
        if (file == null) {
            throw new NullPointerException(HttpPostBodyUtil.FILE);
        } else if (offset < 0) {
            throw new IllegalArgumentException("offset: " + offset + " (expected: 0 or greater)");
        } else if (length < 0) {
            throw new IllegalArgumentException("length: " + length + " (expected: 0 or greater)");
        } else if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize: " + chunkSize + " (expected: a positive integer)");
        } else {
            this.file = file;
            this.startOffset = offset;
            this.offset = offset;
            this.endOffset = offset + length;
            this.chunkSize = chunkSize;
            file.seek(offset);
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
        return this.offset < this.endOffset && this.file.getChannel().isOpen();
    }

    public boolean isEndOfInput() throws Exception {
        return !hasNextChunk();
    }

    public void close() throws Exception {
        this.file.close();
    }

    public Object nextChunk() throws Exception {
        long offset = this.offset;
        if (offset >= this.endOffset) {
            return null;
        }
        int chunkSize = (int) Math.min((long) this.chunkSize, this.endOffset - offset);
        byte[] chunk = new byte[chunkSize];
        this.file.readFully(chunk);
        this.offset = ((long) chunkSize) + offset;
        return ChannelBuffers.wrappedBuffer(chunk);
    }
}
