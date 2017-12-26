package org.apache.commons.compress.archivers.zip;

class CircularBuffer {
    private final byte[] buffer;
    private int readIndex;
    private final int size;
    private int writeIndex;

    CircularBuffer(int size) {
        this.size = size;
        this.buffer = new byte[size];
    }

    public boolean available() {
        return this.readIndex != this.writeIndex;
    }

    public void put(int value) {
        this.buffer[this.writeIndex] = (byte) value;
        this.writeIndex = (this.writeIndex + 1) % this.size;
    }

    public int get() {
        if (!available()) {
            return -1;
        }
        int value = this.buffer[this.readIndex];
        this.readIndex = (this.readIndex + 1) % this.size;
        return value & 255;
    }

    public void copy(int distance, int length) {
        int pos1 = this.writeIndex - distance;
        int pos2 = pos1 + length;
        for (int i = pos1; i < pos2; i++) {
            this.buffer[this.writeIndex] = this.buffer[(this.size + i) % this.size];
            this.writeIndex = (this.writeIndex + 1) % this.size;
        }
    }
}
