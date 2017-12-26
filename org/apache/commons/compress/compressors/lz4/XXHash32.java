package org.apache.commons.compress.compressors.lz4;

import java.util.zip.Checksum;
import org.apache.commons.compress.utils.ByteUtils;

public class XXHash32 implements Checksum {
    private static final int BUF_SIZE = 16;
    private static final int PRIME1 = -1640531535;
    private static final int PRIME2 = -2048144777;
    private static final int PRIME3 = -1028477379;
    private static final int PRIME4 = 668265263;
    private static final int PRIME5 = 374761393;
    private static final int ROTATE_BITS = 13;
    private final byte[] buffer;
    private final byte[] oneByte;
    private int pos;
    private final int seed;
    private final int[] state;
    private int totalLen;

    public XXHash32() {
        this(0);
    }

    public XXHash32(int seed) {
        this.oneByte = new byte[1];
        this.state = new int[4];
        this.buffer = new byte[16];
        this.seed = seed;
        initializeState();
    }

    public void reset() {
        initializeState();
        this.totalLen = 0;
        this.pos = 0;
    }

    public void update(int b) {
        this.oneByte[0] = (byte) (b & 255);
        update(this.oneByte, 0, 1);
    }

    public void update(byte[] b, int off, int len) {
        if (len > 0) {
            this.totalLen += len;
            int end = off + len;
            if (this.pos + len < 16) {
                System.arraycopy(b, off, this.buffer, this.pos, len);
                this.pos += len;
                return;
            }
            if (this.pos > 0) {
                int size = 16 - this.pos;
                System.arraycopy(b, off, this.buffer, this.pos, size);
                process(this.buffer, 0);
                off += size;
            }
            int limit = end - 16;
            while (off <= limit) {
                process(b, off);
                off += 16;
            }
            if (off < end) {
                this.pos = end - off;
                System.arraycopy(b, off, this.buffer, 0, this.pos);
            }
        }
    }

    public long getValue() {
        int hash;
        if (this.totalLen > 16) {
            hash = ((Integer.rotateLeft(this.state[0], 1) + Integer.rotateLeft(this.state[1], 7)) + Integer.rotateLeft(this.state[2], 12)) + Integer.rotateLeft(this.state[3], 18);
        } else {
            hash = this.state[2] + PRIME5;
        }
        hash += this.totalLen;
        int idx = 0;
        while (idx <= this.pos - 4) {
            hash = Integer.rotateLeft((getInt(this.buffer, idx) * PRIME3) + hash, 17) * PRIME4;
            idx += 4;
        }
        while (idx < this.pos) {
            hash = Integer.rotateLeft(((this.buffer[idx] & 255) * PRIME5) + hash, 11) * PRIME1;
            idx++;
        }
        hash = (hash ^ (hash >>> 15)) * PRIME2;
        hash = (hash ^ (hash >>> 13)) * PRIME3;
        return ((long) (hash ^ (hash >>> 16))) & 4294967295L;
    }

    private static int getInt(byte[] buffer, int idx) {
        return (int) (ByteUtils.fromLittleEndian(buffer, idx, 4) & 4294967295L);
    }

    private void initializeState() {
        this.state[0] = (this.seed + PRIME1) + PRIME2;
        this.state[1] = this.seed + PRIME2;
        this.state[2] = this.seed;
        this.state[3] = this.seed - PRIME1;
    }

    private void process(byte[] b, int offset) {
        int s0 = this.state[0];
        int s1 = this.state[1];
        int s2 = this.state[2];
        s1 = Integer.rotateLeft((getInt(b, offset + 4) * PRIME2) + s1, 13) * PRIME1;
        s2 = Integer.rotateLeft((getInt(b, offset + 8) * PRIME2) + s2, 13) * PRIME1;
        int s3 = Integer.rotateLeft((getInt(b, offset + 12) * PRIME2) + this.state[3], 13) * PRIME1;
        this.state[0] = Integer.rotateLeft((getInt(b, offset) * PRIME2) + s0, 13) * PRIME1;
        this.state[1] = s1;
        this.state[2] = s2;
        this.state[3] = s3;
        this.pos = 0;
    }
}
