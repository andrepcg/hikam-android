package org.jboss.netty.channel;

import java.util.ArrayList;
import java.util.List;

public class AdaptiveReceiveBufferSizePredictor implements ReceiveBufferSizePredictor {
    static final int DEFAULT_INITIAL = 1024;
    static final int DEFAULT_MAXIMUM = 65536;
    static final int DEFAULT_MINIMUM = 64;
    private static final int INDEX_DECREMENT = 1;
    private static final int INDEX_INCREMENT = 4;
    private static final int[] SIZE_TABLE;
    private boolean decreaseNow;
    private int index;
    private final int maxIndex;
    private final int minIndex;
    private int nextReceiveBufferSize;

    static {
        int i;
        List<Integer> sizeTable = new ArrayList();
        for (i = 1; i <= 8; i++) {
            sizeTable.add(Integer.valueOf(i));
        }
        for (i = 4; i < 32; i++) {
            long v = 1 << i;
            long inc = v >>> 4;
            v -= inc << 3;
            for (int j = 0; j < 8; j++) {
                v += inc;
                if (v > 2147483647L) {
                    sizeTable.add(Integer.valueOf(Integer.MAX_VALUE));
                } else {
                    sizeTable.add(Integer.valueOf((int) v));
                }
            }
        }
        SIZE_TABLE = new int[sizeTable.size()];
        for (i = 0; i < SIZE_TABLE.length; i++) {
            SIZE_TABLE[i] = ((Integer) sizeTable.get(i)).intValue();
        }
    }

    private static int getSizeTableIndex(int size) {
        if (size <= 16) {
            return size - 1;
        }
        int bits = 0;
        int v = size;
        do {
            v >>>= 1;
            bits++;
        } while (v != 0);
        int baseIdx = bits << 3;
        int endIdx = baseIdx - 25;
        for (int i = baseIdx - 18; i >= endIdx; i--) {
            if (size >= SIZE_TABLE[i]) {
                return i;
            }
        }
        throw new Error("shouldn't reach here; please file a bug report.");
    }

    public AdaptiveReceiveBufferSizePredictor() {
        this(64, 1024, 65536);
    }

    public AdaptiveReceiveBufferSizePredictor(int minimum, int initial, int maximum) {
        if (minimum <= 0) {
            throw new IllegalArgumentException("minimum: " + minimum);
        } else if (initial < minimum) {
            throw new IllegalArgumentException("initial: " + initial);
        } else if (maximum < initial) {
            throw new IllegalArgumentException("maximum: " + maximum);
        } else {
            int minIndex = getSizeTableIndex(minimum);
            if (SIZE_TABLE[minIndex] < minimum) {
                this.minIndex = minIndex + 1;
            } else {
                this.minIndex = minIndex;
            }
            int maxIndex = getSizeTableIndex(maximum);
            if (SIZE_TABLE[maxIndex] > maximum) {
                this.maxIndex = maxIndex - 1;
            } else {
                this.maxIndex = maxIndex;
            }
            this.index = getSizeTableIndex(initial);
            this.nextReceiveBufferSize = SIZE_TABLE[this.index];
        }
    }

    public int nextReceiveBufferSize() {
        return this.nextReceiveBufferSize;
    }

    public void previousReceiveBufferSize(int previousReceiveBufferSize) {
        if (previousReceiveBufferSize <= SIZE_TABLE[Math.max(0, (this.index - 1) - 1)]) {
            if (this.decreaseNow) {
                this.index = Math.max(this.index - 1, this.minIndex);
                this.nextReceiveBufferSize = SIZE_TABLE[this.index];
                this.decreaseNow = false;
                return;
            }
            this.decreaseNow = true;
        } else if (previousReceiveBufferSize >= this.nextReceiveBufferSize) {
            this.index = Math.min(this.index + 4, this.maxIndex);
            this.nextReceiveBufferSize = SIZE_TABLE[this.index];
            this.decreaseNow = false;
        }
    }
}
