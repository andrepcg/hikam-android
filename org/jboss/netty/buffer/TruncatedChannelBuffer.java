package org.jboss.netty.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class TruncatedChannelBuffer extends AbstractChannelBuffer implements WrappedChannelBuffer {
    private final ChannelBuffer buffer;
    private final int length;

    public TruncatedChannelBuffer(ChannelBuffer buffer, int length) {
        if (length > buffer.capacity()) {
            throw new IndexOutOfBoundsException("Length is too large, got " + length + " but can't go higher than " + buffer.capacity());
        }
        this.buffer = buffer;
        this.length = length;
        writerIndex(length);
    }

    public ChannelBuffer unwrap() {
        return this.buffer;
    }

    public ChannelBufferFactory factory() {
        return this.buffer.factory();
    }

    public ByteOrder order() {
        return this.buffer.order();
    }

    public boolean isDirect() {
        return this.buffer.isDirect();
    }

    public int capacity() {
        return this.length;
    }

    public boolean hasArray() {
        return this.buffer.hasArray();
    }

    public byte[] array() {
        return this.buffer.array();
    }

    public int arrayOffset() {
        return this.buffer.arrayOffset();
    }

    public byte getByte(int index) {
        checkIndex(index);
        return this.buffer.getByte(index);
    }

    public short getShort(int index) {
        checkIndex(index, 2);
        return this.buffer.getShort(index);
    }

    public int getUnsignedMedium(int index) {
        checkIndex(index, 3);
        return this.buffer.getUnsignedMedium(index);
    }

    public int getInt(int index) {
        checkIndex(index, 4);
        return this.buffer.getInt(index);
    }

    public long getLong(int index) {
        checkIndex(index, 8);
        return this.buffer.getLong(index);
    }

    public ChannelBuffer duplicate() {
        ChannelBuffer duplicate = new TruncatedChannelBuffer(this.buffer, this.length);
        duplicate.setIndex(readerIndex(), writerIndex());
        return duplicate;
    }

    public ChannelBuffer copy(int index, int length) {
        checkIndex(index, length);
        return this.buffer.copy(index, length);
    }

    public ChannelBuffer slice(int index, int length) {
        checkIndex(index, length);
        if (length == 0) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        return this.buffer.slice(index, length);
    }

    public void getBytes(int index, ChannelBuffer dst, int dstIndex, int length) {
        checkIndex(index, length);
        this.buffer.getBytes(index, dst, dstIndex, length);
    }

    public void getBytes(int index, byte[] dst, int dstIndex, int length) {
        checkIndex(index, length);
        this.buffer.getBytes(index, dst, dstIndex, length);
    }

    public void getBytes(int index, ByteBuffer dst) {
        checkIndex(index, dst.remaining());
        this.buffer.getBytes(index, dst);
    }

    public void setByte(int index, int value) {
        checkIndex(index);
        this.buffer.setByte(index, value);
    }

    public void setShort(int index, int value) {
        checkIndex(index, 2);
        this.buffer.setShort(index, value);
    }

    public void setMedium(int index, int value) {
        checkIndex(index, 3);
        this.buffer.setMedium(index, value);
    }

    public void setInt(int index, int value) {
        checkIndex(index, 4);
        this.buffer.setInt(index, value);
    }

    public void setLong(int index, long value) {
        checkIndex(index, 8);
        this.buffer.setLong(index, value);
    }

    public void setBytes(int index, byte[] src, int srcIndex, int length) {
        checkIndex(index, length);
        this.buffer.setBytes(index, src, srcIndex, length);
    }

    public void setBytes(int index, ChannelBuffer src, int srcIndex, int length) {
        checkIndex(index, length);
        this.buffer.setBytes(index, src, srcIndex, length);
    }

    public void setBytes(int index, ByteBuffer src) {
        checkIndex(index, src.remaining());
        this.buffer.setBytes(index, src);
    }

    public void getBytes(int index, OutputStream out, int length) throws IOException {
        checkIndex(index, length);
        this.buffer.getBytes(index, out, length);
    }

    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        checkIndex(index, length);
        return this.buffer.getBytes(index, out, length);
    }

    public int setBytes(int index, InputStream in, int length) throws IOException {
        checkIndex(index, length);
        return this.buffer.setBytes(index, in, length);
    }

    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        checkIndex(index, length);
        return this.buffer.setBytes(index, in, length);
    }

    public ByteBuffer toByteBuffer(int index, int length) {
        checkIndex(index, length);
        return this.buffer.toByteBuffer(index, length);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= capacity()) {
            throw new IndexOutOfBoundsException("Invalid index of " + index + ", maximum is " + capacity());
        }
    }

    private void checkIndex(int index, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length is negative: " + length);
        } else if (index + length > capacity()) {
            throw new IndexOutOfBoundsException("Invalid index of " + (index + length) + ", maximum is " + capacity());
        }
    }
}
