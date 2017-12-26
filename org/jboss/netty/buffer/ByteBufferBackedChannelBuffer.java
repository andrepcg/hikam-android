package org.jboss.netty.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class ByteBufferBackedChannelBuffer extends AbstractChannelBuffer {
    private final ByteBuffer buffer;
    private final int capacity;
    private final ByteOrder order;

    public ByteBufferBackedChannelBuffer(ByteBuffer buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        this.order = buffer.order();
        this.buffer = buffer.slice().order(this.order);
        this.capacity = buffer.remaining();
        writerIndex(this.capacity);
    }

    private ByteBufferBackedChannelBuffer(ByteBufferBackedChannelBuffer buffer) {
        this.buffer = buffer.buffer;
        this.order = buffer.order;
        this.capacity = buffer.capacity;
        setIndex(buffer.readerIndex(), buffer.writerIndex());
    }

    public ChannelBufferFactory factory() {
        if (this.buffer.isDirect()) {
            return DirectChannelBufferFactory.getInstance(order());
        }
        return HeapChannelBufferFactory.getInstance(order());
    }

    public boolean isDirect() {
        return this.buffer.isDirect();
    }

    public ByteOrder order() {
        return this.order;
    }

    public int capacity() {
        return this.capacity;
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
        return this.buffer.get(index);
    }

    public short getShort(int index) {
        return this.buffer.getShort(index);
    }

    public int getUnsignedMedium(int index) {
        return (((getByte(index) & 255) << 16) | ((getByte(index + 1) & 255) << 8)) | (getByte(index + 2) & 255);
    }

    public int getInt(int index) {
        return this.buffer.getInt(index);
    }

    public long getLong(int index) {
        return this.buffer.getLong(index);
    }

    public void getBytes(int index, ChannelBuffer dst, int dstIndex, int length) {
        if (dst instanceof ByteBufferBackedChannelBuffer) {
            ByteBuffer data = ((ByteBufferBackedChannelBuffer) dst).buffer.duplicate();
            data.limit(dstIndex + length).position(dstIndex);
            getBytes(index, data);
        } else if (this.buffer.hasArray()) {
            dst.setBytes(dstIndex, this.buffer.array(), this.buffer.arrayOffset() + index, length);
        } else {
            dst.setBytes(dstIndex, (ChannelBuffer) this, index, length);
        }
    }

    public void getBytes(int index, byte[] dst, int dstIndex, int length) {
        ByteBuffer data = this.buffer.duplicate();
        try {
            data.limit(index + length).position(index);
            data.get(dst, dstIndex, length);
        } catch (IllegalArgumentException e) {
            throw new IndexOutOfBoundsException("Too many bytes to read - Need " + (index + length) + ", maximum is " + data.limit());
        }
    }

    public void getBytes(int index, ByteBuffer dst) {
        ByteBuffer data = this.buffer.duplicate();
        int bytesToCopy = Math.min(capacity() - index, dst.remaining());
        try {
            data.limit(index + bytesToCopy).position(index);
            dst.put(data);
        } catch (IllegalArgumentException e) {
            throw new IndexOutOfBoundsException("Too many bytes to read - Need " + (index + bytesToCopy) + ", maximum is " + data.limit());
        }
    }

    public void setByte(int index, int value) {
        this.buffer.put(index, (byte) value);
    }

    public void setShort(int index, int value) {
        this.buffer.putShort(index, (short) value);
    }

    public void setMedium(int index, int value) {
        setByte(index, (byte) (value >>> 16));
        setByte(index + 1, (byte) (value >>> 8));
        setByte(index + 2, (byte) value);
    }

    public void setInt(int index, int value) {
        this.buffer.putInt(index, value);
    }

    public void setLong(int index, long value) {
        this.buffer.putLong(index, value);
    }

    public void setBytes(int index, ChannelBuffer src, int srcIndex, int length) {
        if (src instanceof ByteBufferBackedChannelBuffer) {
            ByteBuffer data = ((ByteBufferBackedChannelBuffer) src).buffer.duplicate();
            data.limit(srcIndex + length).position(srcIndex);
            setBytes(index, data);
        } else if (this.buffer.hasArray()) {
            src.getBytes(srcIndex, this.buffer.array(), this.buffer.arrayOffset() + index, length);
        } else {
            src.getBytes(srcIndex, (ChannelBuffer) this, index, length);
        }
    }

    public void setBytes(int index, byte[] src, int srcIndex, int length) {
        ByteBuffer data = this.buffer.duplicate();
        data.limit(index + length).position(index);
        data.put(src, srcIndex, length);
    }

    public void setBytes(int index, ByteBuffer src) {
        ByteBuffer data = this.buffer.duplicate();
        data.limit(src.remaining() + index).position(index);
        data.put(src);
    }

    public void getBytes(int index, OutputStream out, int length) throws IOException {
        if (length != 0) {
            if (this.buffer.hasArray()) {
                out.write(this.buffer.array(), this.buffer.arrayOffset() + index, length);
                return;
            }
            byte[] tmp = new byte[length];
            ((ByteBuffer) this.buffer.duplicate().position(index)).get(tmp);
            out.write(tmp);
        }
    }

    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        if (length == 0) {
            return 0;
        }
        return out.write((ByteBuffer) this.buffer.duplicate().position(index).limit(index + length));
    }

    public int setBytes(int index, InputStream in, int length) throws IOException {
        int readBytes = 0;
        int localReadBytes;
        if (this.buffer.hasArray()) {
            index += this.buffer.arrayOffset();
            do {
                localReadBytes = in.read(this.buffer.array(), index, length);
                if (localReadBytes >= 0) {
                    readBytes += localReadBytes;
                    index += localReadBytes;
                    length -= localReadBytes;
                } else if (readBytes == 0) {
                    return -1;
                }
            } while (length > 0);
        }
        byte[] tmp = new byte[length];
        int i = 0;
        do {
            localReadBytes = in.read(tmp, i, tmp.length - i);
            if (localReadBytes >= 0) {
                readBytes += localReadBytes;
                i += readBytes;
            } else if (readBytes == 0) {
                return -1;
            }
            ((ByteBuffer) this.buffer.duplicate().position(index)).put(tmp);
        } while (i < tmp.length);
        ((ByteBuffer) this.buffer.duplicate().position(index)).put(tmp);
        return readBytes;
    }

    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        ByteBuffer slice = (ByteBuffer) this.buffer.duplicate().limit(index + length).position(index);
        int readBytes = 0;
        while (readBytes < length) {
            int localReadBytes;
            try {
                localReadBytes = in.read(slice);
            } catch (ClosedChannelException e) {
                localReadBytes = -1;
            }
            if (localReadBytes < 0) {
                if (readBytes == 0) {
                    return -1;
                }
                return readBytes;
            } else if (localReadBytes == 0) {
                return readBytes;
            } else {
                readBytes += localReadBytes;
            }
        }
        return readBytes;
    }

    public ByteBuffer toByteBuffer(int index, int length) {
        if (index == 0 && length == capacity()) {
            return this.buffer.duplicate().order(order());
        }
        return ((ByteBuffer) this.buffer.duplicate().position(index).limit(index + length)).slice().order(order());
    }

    public ChannelBuffer slice(int index, int length) {
        if (index == 0 && length == capacity()) {
            ChannelBuffer slice = duplicate();
            slice.setIndex(0, length);
            return slice;
        } else if (index < 0 || length != 0) {
            return new ByteBufferBackedChannelBuffer(((ByteBuffer) this.buffer.duplicate().position(index).limit(index + length)).order(order()));
        } else {
            return ChannelBuffers.EMPTY_BUFFER;
        }
    }

    public ChannelBuffer duplicate() {
        return new ByteBufferBackedChannelBuffer(this);
    }

    public ChannelBuffer copy(int index, int length) {
        try {
            ByteBuffer src = (ByteBuffer) this.buffer.duplicate().position(index).limit(index + length);
            ByteBuffer dst = this.buffer.isDirect() ? ByteBuffer.allocateDirect(length) : ByteBuffer.allocate(length);
            dst.put(src);
            dst.order(order());
            dst.clear();
            return new ByteBufferBackedChannelBuffer(dst);
        } catch (IllegalArgumentException e) {
            throw new IndexOutOfBoundsException("Too many bytes to read - Need " + (index + length));
        }
    }
}
