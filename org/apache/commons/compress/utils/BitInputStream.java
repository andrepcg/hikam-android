package org.apache.commons.compress.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

public class BitInputStream implements Closeable {
    private static final long[] MASKS = new long[64];
    private static final int MAXIMUM_CACHE_SIZE = 63;
    private long bitsCached = 0;
    private int bitsCachedSize = 0;
    private final ByteOrder byteOrder;
    private final InputStream in;

    static {
        for (int i = 1; i <= 63; i++) {
            MASKS[i] = (MASKS[i - 1] << 1) + 1;
        }
    }

    public BitInputStream(InputStream in, ByteOrder byteOrder) {
        this.in = in;
        this.byteOrder = byteOrder;
    }

    public void close() throws IOException {
        this.in.close();
    }

    public void clearBitCache() {
        this.bitsCached = 0;
        this.bitsCachedSize = 0;
    }

    public long readBits(int count) throws IOException {
        if (count < 0 || count > 63) {
            throw new IllegalArgumentException("count must not be negative or greater than 63");
        } else if (ensureCache(count)) {
            return -1;
        } else {
            if (this.bitsCachedSize < count) {
                return processBitsGreater57(count);
            }
            long bitsOut;
            if (this.byteOrder == ByteOrder.LITTLE_ENDIAN) {
                bitsOut = this.bitsCached & MASKS[count];
                this.bitsCached >>>= count;
            } else {
                bitsOut = (this.bitsCached >> (this.bitsCachedSize - count)) & MASKS[count];
            }
            this.bitsCachedSize -= count;
            return bitsOut;
        }
    }

    private long processBitsGreater57(int count) throws IOException {
        int bitsToAddCount = count - this.bitsCachedSize;
        int overflowBits = 8 - bitsToAddCount;
        long nextByte = (long) this.in.read();
        if (nextByte < 0) {
            return nextByte;
        }
        long overflow;
        if (this.byteOrder == ByteOrder.LITTLE_ENDIAN) {
            this.bitsCached |= (nextByte & MASKS[bitsToAddCount]) << this.bitsCachedSize;
            overflow = (nextByte >>> bitsToAddCount) & MASKS[overflowBits];
        } else {
            this.bitsCached <<= bitsToAddCount;
            this.bitsCached |= (nextByte >>> overflowBits) & MASKS[bitsToAddCount];
            overflow = nextByte & MASKS[overflowBits];
        }
        long bitsOut = this.bitsCached & MASKS[count];
        this.bitsCached = overflow;
        this.bitsCachedSize = overflowBits;
        return bitsOut;
    }

    private boolean ensureCache(int count) throws IOException {
        while (this.bitsCachedSize < count && this.bitsCachedSize < 57) {
            long nextByte = (long) this.in.read();
            if (nextByte < 0) {
                return true;
            }
            if (this.byteOrder == ByteOrder.LITTLE_ENDIAN) {
                this.bitsCached |= nextByte << this.bitsCachedSize;
            } else {
                this.bitsCached <<= 8;
                this.bitsCached |= nextByte;
            }
            this.bitsCachedSize += 8;
        }
        return false;
    }
}
