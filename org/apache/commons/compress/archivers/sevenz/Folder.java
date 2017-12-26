package org.apache.commons.compress.archivers.sevenz;

import java.util.LinkedList;

class Folder {
    BindPair[] bindPairs;
    Coder[] coders;
    long crc;
    boolean hasCrc;
    int numUnpackSubStreams;
    long[] packedStreams;
    long totalInputStreams;
    long totalOutputStreams;
    long[] unpackSizes;

    Folder() {
    }

    Iterable<Coder> getOrderedCoders() {
        LinkedList<Coder> l = new LinkedList();
        int current = (int) this.packedStreams[0];
        while (current != -1) {
            l.addLast(this.coders[current]);
            int pair = findBindPairForOutStream(current);
            if (pair != -1) {
                current = (int) this.bindPairs[pair].inIndex;
            } else {
                current = -1;
            }
        }
        return l;
    }

    int findBindPairForInStream(int index) {
        for (int i = 0; i < this.bindPairs.length; i++) {
            if (this.bindPairs[i].inIndex == ((long) index)) {
                return i;
            }
        }
        return -1;
    }

    int findBindPairForOutStream(int index) {
        for (int i = 0; i < this.bindPairs.length; i++) {
            if (this.bindPairs[i].outIndex == ((long) index)) {
                return i;
            }
        }
        return -1;
    }

    long getUnpackSize() {
        if (this.totalOutputStreams == 0) {
            return 0;
        }
        for (int i = ((int) this.totalOutputStreams) - 1; i >= 0; i--) {
            if (findBindPairForOutStream(i) < 0) {
                return this.unpackSizes[i];
            }
        }
        return 0;
    }

    long getUnpackSizeForCoder(Coder coder) {
        if (this.coders != null) {
            for (int i = 0; i < this.coders.length; i++) {
                if (this.coders[i] == coder) {
                    return this.unpackSizes[i];
                }
            }
        }
        return 0;
    }

    public String toString() {
        return "Folder with " + this.coders.length + " coders, " + this.totalInputStreams + " input streams, " + this.totalOutputStreams + " output streams, " + this.bindPairs.length + " bind pairs, " + this.packedStreams.length + " packed streams, " + this.unpackSizes.length + " unpack sizes, " + (this.hasCrc ? "with CRC " + this.crc : "without CRC") + " and " + this.numUnpackSubStreams + " unpack streams";
    }
}
