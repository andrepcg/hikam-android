package com.google.zxing.common;

public final class BitMatrix {
    public final int[] bits;
    public final int height;
    public final int rowSize;
    public final int width;

    public BitMatrix(int dimension) {
        this(dimension, dimension);
    }

    public BitMatrix(int width, int height) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("Both dimensions must be greater than 0");
        }
        this.width = width;
        this.height = height;
        this.rowSize = (width + 31) >> 5;
        this.bits = new int[(this.rowSize * height)];
    }

    public boolean get(int x, int y) {
        return ((this.bits[(this.rowSize * y) + (x >> 5)] >>> (x & 31)) & 1) != 0;
    }

    public void set(int x, int y) {
        int offset = (this.rowSize * y) + (x >> 5);
        int[] iArr = this.bits;
        iArr[offset] = iArr[offset] | (1 << (x & 31));
    }

    public void flip(int x, int y) {
        int offset = (this.rowSize * y) + (x >> 5);
        int[] iArr = this.bits;
        iArr[offset] = iArr[offset] ^ (1 << (x & 31));
    }

    public void clear() {
        int max = this.bits.length;
        for (int i = 0; i < max; i++) {
            this.bits[i] = 0;
        }
    }

    public void setRegion(int left, int top, int width, int height) {
        if (top < 0 || left < 0) {
            throw new IllegalArgumentException("Left and top must be nonnegative");
        } else if (height < 1 || width < 1) {
            throw new IllegalArgumentException("Height and width must be at least 1");
        } else {
            int right = left + width;
            int bottom = top + height;
            if (bottom > this.height || right > this.width) {
                throw new IllegalArgumentException("The region must fit inside the matrix");
            }
            for (int y = top; y < bottom; y++) {
                int offset = y * this.rowSize;
                for (int x = left; x < right; x++) {
                    int[] iArr = this.bits;
                    int i = (x >> 5) + offset;
                    iArr[i] = iArr[i] | (1 << (x & 31));
                }
            }
        }
    }

    public BitArray getRow(int y, BitArray row) {
        if (row == null || row.getSize() < this.width) {
            row = new BitArray(this.width);
        }
        int offset = y * this.rowSize;
        for (int x = 0; x < this.rowSize; x++) {
            row.setBulk(x << 5, this.bits[offset + x]);
        }
        return row;
    }

    public int[] getTopLeftOnBit() {
        int bitsOffset = 0;
        while (bitsOffset < this.bits.length && this.bits[bitsOffset] == 0) {
            bitsOffset++;
        }
        if (bitsOffset == this.bits.length) {
            return null;
        }
        int bit;
        int y = bitsOffset / this.rowSize;
        int x = (bitsOffset % this.rowSize) << 5;
        for (bit = 0; (this.bits[bitsOffset] << (31 - bit)) == 0; bit++) {
        }
        return new int[]{x + bit, y};
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean equals(Object o) {
        if (!(o instanceof BitMatrix)) {
            return false;
        }
        BitMatrix other = (BitMatrix) o;
        if (this.width != other.width || this.height != other.height || this.rowSize != other.rowSize || this.bits.length != other.bits.length) {
            return false;
        }
        for (int i = 0; i < this.bits.length; i++) {
            if (this.bits[i] != other.bits[i]) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int hash = (((((this.width * 31) + this.width) * 31) + this.height) * 31) + this.rowSize;
        for (int i : this.bits) {
            hash = (hash * 31) + i;
        }
        return hash;
    }

    public String toString() {
        StringBuffer result = new StringBuffer(this.height * (this.width + 1));
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                result.append(get(x, y) ? "X " : "  ");
            }
            result.append('\n');
        }
        return result.toString();
    }
}
