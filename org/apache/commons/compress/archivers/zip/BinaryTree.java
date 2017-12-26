package org.apache.commons.compress.archivers.zip;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

class BinaryTree {
    private static final int NODE = -2;
    private static final int UNDEFINED = -1;
    private final int[] tree;

    public BinaryTree(int depth) {
        this.tree = new int[((1 << (depth + 1)) - 1)];
        Arrays.fill(this.tree, -1);
    }

    public void addLeaf(int node, int path, int depth, int value) {
        if (depth != 0) {
            this.tree[node] = -2;
            addLeaf(((node * 2) + 1) + (path & 1), path >>> 1, depth - 1, value);
        } else if (this.tree[node] == -1) {
            this.tree[node] = value;
        } else {
            throw new IllegalArgumentException("Tree value at index " + node + " has already been assigned (" + this.tree[node] + ")");
        }
    }

    public int read(BitStream stream) throws IOException {
        int value;
        int currentIndex = 0;
        while (true) {
            int bit = stream.nextBit();
            if (bit != -1) {
                int childIndex = ((currentIndex * 2) + 1) + bit;
                value = this.tree[childIndex];
                if (value != -2) {
                    break;
                }
                currentIndex = childIndex;
            } else {
                return -1;
            }
        }
        if (value != -1) {
            return value;
        }
        throw new IOException("The child " + bit + " of node at index " + currentIndex + " is not defined");
    }

    static BinaryTree decode(InputStream in, int totalNumberOfValues) throws IOException {
        int size = in.read() + 1;
        if (size == 0) {
            throw new IOException("Cannot read the size of the encoded tree, unexpected end of stream");
        }
        int k;
        byte[] encodedTree = new byte[size];
        new DataInputStream(in).readFully(encodedTree);
        int maxLength = 0;
        int[] originalBitLengths = new int[totalNumberOfValues];
        int pos = 0;
        int length = encodedTree.length;
        int i = 0;
        while (i < length) {
            byte b = encodedTree[i];
            int numberOfValues = ((b & 240) >> 4) + 1;
            int bitLength = (b & 15) + 1;
            int j = 0;
            int pos2 = pos;
            while (j < numberOfValues) {
                pos = pos2 + 1;
                originalBitLengths[pos2] = bitLength;
                j++;
                pos2 = pos;
            }
            maxLength = Math.max(maxLength, bitLength);
            i++;
            pos = pos2;
        }
        int[] permutation = new int[originalBitLengths.length];
        for (k = 0; k < permutation.length; k++) {
            permutation[k] = k;
        }
        int c = 0;
        int[] sortedBitLengths = new int[originalBitLengths.length];
        for (k = 0; k < originalBitLengths.length; k++) {
            for (int l = 0; l < originalBitLengths.length; l++) {
                if (originalBitLengths[l] == k) {
                    sortedBitLengths[c] = k;
                    permutation[c] = l;
                    c++;
                }
            }
        }
        int code = 0;
        int codeIncrement = 0;
        int lastBitLength = 0;
        int[] codes = new int[totalNumberOfValues];
        for (int i2 = totalNumberOfValues - 1; i2 >= 0; i2--) {
            code += codeIncrement;
            if (sortedBitLengths[i2] != lastBitLength) {
                lastBitLength = sortedBitLengths[i2];
                codeIncrement = 1 << (16 - lastBitLength);
            }
            codes[permutation[i2]] = code;
        }
        BinaryTree binaryTree = new BinaryTree(maxLength);
        for (k = 0; k < codes.length; k++) {
            bitLength = originalBitLengths[k];
            if (bitLength > 0) {
                binaryTree.addLeaf(0, Integer.reverse(codes[k] << 16), bitLength, k);
            }
        }
        return binaryTree;
    }
}
