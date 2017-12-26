package org.apache.commons.compress.compressors.lz4;

import android.support.v4.internal.view.SupportMenu;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor.BackReference;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor.Block;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor.Callback;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor.EOD;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor.LiteralBlock;
import org.apache.commons.compress.compressors.lz77support.Parameters;
import org.apache.commons.compress.compressors.lz77support.Parameters.Builder;
import org.apache.commons.compress.utils.ByteUtils;

public class BlockLZ4CompressorOutputStream extends CompressorOutputStream {
    private static final int MIN_BACK_REFERENCE_LENGTH = 4;
    private static final int MIN_OFFSET_OF_LAST_BACK_REFERENCE = 12;
    private final LZ77Compressor compressor;
    private Deque<byte[]> expandedBlocks;
    private boolean finished;
    private final byte[] oneByte;
    private final OutputStream os;
    private Deque<Pair> pairs;

    static final class Pair {
        private int brLength;
        private int brOffset;
        private final Deque<byte[]> literals = new LinkedList();
        private boolean written;

        Pair() {
        }

        private void prependLiteral(byte[] data) {
            this.literals.addFirst(data);
        }

        byte[] addLiteral(LiteralBlock block) {
            byte[] copy = Arrays.copyOfRange(block.getData(), block.getOffset(), block.getOffset() + block.getLength());
            this.literals.add(copy);
            return copy;
        }

        void setBackReference(BackReference block) {
            if (hasBackReference()) {
                throw new IllegalStateException();
            }
            this.brOffset = block.getOffset();
            this.brLength = block.getLength();
        }

        boolean hasBackReference() {
            return this.brOffset > 0;
        }

        boolean canBeWritten(int lengthOfBlocksAfterThisPair) {
            return hasBackReference() && lengthOfBlocksAfterThisPair >= 16;
        }

        int length() {
            return literalLength() + this.brLength;
        }

        private boolean hasBeenWritten() {
            return this.written;
        }

        void writeTo(OutputStream out) throws IOException {
            int litLength = literalLength();
            out.write(lengths(litLength, this.brLength));
            if (litLength >= 15) {
                writeLength(litLength - 15, out);
            }
            for (byte[] b : this.literals) {
                out.write(b);
            }
            if (hasBackReference()) {
                ByteUtils.toLittleEndian(out, (long) this.brOffset, 2);
                if (this.brLength - 4 >= 15) {
                    writeLength((this.brLength - 4) - 15, out);
                }
            }
            this.written = true;
        }

        private int literalLength() {
            int length = 0;
            for (byte[] b : this.literals) {
                length += b.length;
            }
            return length;
        }

        private static int lengths(int litLength, int brLength) {
            int l;
            int br = 15;
            if (litLength < 15) {
                l = litLength;
            } else {
                l = 15;
            }
            if (brLength < 4) {
                br = 0;
            } else if (brLength < 19) {
                br = brLength - 4;
            }
            return (l << 4) | br;
        }

        private static void writeLength(int length, OutputStream out) throws IOException {
            while (length >= 255) {
                out.write(255);
                length -= 255;
            }
            out.write(length);
        }

        private int backReferenceLength() {
            return this.brLength;
        }

        private void prependTo(Pair other) {
            Iterator<byte[]> listBackwards = this.literals.descendingIterator();
            while (listBackwards.hasNext()) {
                other.prependLiteral((byte[]) listBackwards.next());
            }
        }

        private Pair splitWithNewBackReferenceLengthOf(int newBackReferenceLength) {
            Pair p = new Pair();
            p.literals.addAll(this.literals);
            p.brOffset = this.brOffset;
            p.brLength = newBackReferenceLength;
            return p;
        }
    }

    class C11901 implements Callback {
        C11901() {
        }

        public void accept(Block block) throws IOException {
            if (block instanceof LiteralBlock) {
                BlockLZ4CompressorOutputStream.this.addLiteralBlock((LiteralBlock) block);
            } else if (block instanceof BackReference) {
                BlockLZ4CompressorOutputStream.this.addBackReference((BackReference) block);
            } else if (block instanceof EOD) {
                BlockLZ4CompressorOutputStream.this.writeFinalLiteralBlock();
            }
        }
    }

    public BlockLZ4CompressorOutputStream(OutputStream os) throws IOException {
        this(os, createParameterBuilder().build());
    }

    public BlockLZ4CompressorOutputStream(OutputStream os, Parameters params) throws IOException {
        this.oneByte = new byte[1];
        this.finished = false;
        this.pairs = new LinkedList();
        this.expandedBlocks = new LinkedList();
        this.os = os;
        this.compressor = new LZ77Compressor(params, new C11901());
    }

    public void write(int b) throws IOException {
        this.oneByte[0] = (byte) (b & 255);
        write(this.oneByte);
    }

    public void write(byte[] data, int off, int len) throws IOException {
        this.compressor.compress(data, off, len);
    }

    public void close() throws IOException {
        finish();
        this.os.close();
    }

    public void finish() throws IOException {
        if (!this.finished) {
            this.compressor.finish();
            this.finished = true;
        }
    }

    public void prefill(byte[] data, int off, int len) {
        if (len > 0) {
            byte[] b = Arrays.copyOfRange(data, off, off + len);
            this.compressor.prefill(b);
            recordLiteral(b);
        }
    }

    private void addLiteralBlock(LiteralBlock block) throws IOException {
        recordLiteral(writeBlocksAndReturnUnfinishedPair(block.getLength()).addLiteral(block));
        clearUnusedBlocksAndPairs();
    }

    private void addBackReference(BackReference block) throws IOException {
        writeBlocksAndReturnUnfinishedPair(block.getLength()).setBackReference(block);
        recordBackReference(block);
        clearUnusedBlocksAndPairs();
    }

    private Pair writeBlocksAndReturnUnfinishedPair(int length) throws IOException {
        writeWritablePairs(length);
        Pair last = (Pair) this.pairs.peekLast();
        if (last != null && !last.hasBackReference()) {
            return last;
        }
        last = new Pair();
        this.pairs.addLast(last);
        return last;
    }

    private void recordLiteral(byte[] b) {
        this.expandedBlocks.addFirst(b);
    }

    private void clearUnusedBlocksAndPairs() {
        clearUnusedBlocks();
        clearUnusedPairs();
    }

    private void clearUnusedBlocks() {
        int blockLengths = 0;
        int blocksToKeep = 0;
        for (byte[] b : this.expandedBlocks) {
            blocksToKeep++;
            blockLengths += b.length;
            if (blockLengths >= 65536) {
                break;
            }
        }
        int size = this.expandedBlocks.size();
        for (int i = blocksToKeep; i < size; i++) {
            this.expandedBlocks.removeLast();
        }
    }

    private void recordBackReference(BackReference block) {
        this.expandedBlocks.addFirst(expand(block.getOffset(), block.getLength()));
    }

    private byte[] expand(int offset, int length) {
        byte[] expanded = new byte[length];
        if (offset == 1) {
            byte[] block = (byte[]) this.expandedBlocks.peekFirst();
            byte b = block[block.length - 1];
            if (b != (byte) 0) {
                Arrays.fill(expanded, b);
            }
        } else {
            expandFromList(expanded, offset, length);
        }
        return expanded;
    }

    private void expandFromList(byte[] expanded, int offset, int length) {
        int offsetRemaining = offset;
        int lengthRemaining = length;
        int writeOffset = 0;
        while (lengthRemaining > 0) {
            int copyOffset;
            int copyLen;
            byte[] block = null;
            if (offsetRemaining > 0) {
                int blockOffset = 0;
                for (byte[] b : this.expandedBlocks) {
                    if (b.length + blockOffset >= offsetRemaining) {
                        block = b;
                        break;
                    }
                    blockOffset += b.length;
                }
                if (block == null) {
                    throw new IllegalStateException("failed to find a block containing offset " + offset);
                }
                copyOffset = (block.length + blockOffset) - offsetRemaining;
                copyLen = Math.min(lengthRemaining, block.length - copyOffset);
            } else {
                block = expanded;
                copyOffset = -offsetRemaining;
                copyLen = Math.min(lengthRemaining, writeOffset + offsetRemaining);
            }
            System.arraycopy(block, copyOffset, expanded, writeOffset, copyLen);
            offsetRemaining -= copyLen;
            lengthRemaining -= copyLen;
            writeOffset += copyLen;
        }
    }

    private void clearUnusedPairs() {
        int pairLengths = 0;
        int pairsToKeep = 0;
        Iterator<Pair> it = this.pairs.descendingIterator();
        while (it.hasNext()) {
            pairsToKeep++;
            pairLengths += ((Pair) it.next()).length();
            if (pairLengths >= 65536) {
                break;
            }
        }
        int size = this.pairs.size();
        for (int i = pairsToKeep; i < size && ((Pair) this.pairs.peekFirst()).hasBeenWritten(); i++) {
            this.pairs.removeFirst();
        }
    }

    private void writeFinalLiteralBlock() throws IOException {
        rewriteLastPairs();
        for (Pair p : this.pairs) {
            if (!p.hasBeenWritten()) {
                p.writeTo(this.os);
            }
        }
        this.pairs.clear();
    }

    private void writeWritablePairs(int lengthOfBlocksAfterLastPair) throws IOException {
        int unwrittenLength = lengthOfBlocksAfterLastPair;
        Iterator<Pair> it = this.pairs.descendingIterator();
        while (it.hasNext()) {
            Pair p = (Pair) it.next();
            if (p.hasBeenWritten()) {
                break;
            }
            unwrittenLength += p.length();
        }
        for (Pair p2 : this.pairs) {
            if (!p2.hasBeenWritten()) {
                unwrittenLength -= p2.length();
                if (p2.canBeWritten(unwrittenLength)) {
                    p2.writeTo(this.os);
                } else {
                    return;
                }
            }
        }
    }

    private void rewriteLastPairs() {
        int brLen = 0;
        LinkedList<Pair> lastPairs = new LinkedList();
        LinkedList<Integer> pairLength = new LinkedList();
        int offset = 0;
        Iterator<Pair> it = this.pairs.descendingIterator();
        while (it.hasNext()) {
            Pair p = (Pair) it.next();
            if (!p.hasBeenWritten()) {
                int len = p.length();
                pairLength.addFirst(Integer.valueOf(len));
                lastPairs.addFirst(p);
                offset += len;
                if (offset >= 12) {
                    break;
                }
            }
            break;
        }
        Iterator it2 = lastPairs.iterator();
        while (it2.hasNext()) {
            this.pairs.remove((Pair) it2.next());
        }
        int toExpand = 0;
        for (int i = 1; i < lastPairs.size(); i++) {
            toExpand += ((Integer) pairLength.get(i)).intValue();
        }
        Pair replacement = new Pair();
        if (toExpand > 0) {
            replacement.prependLiteral(expand(toExpand, toExpand));
        }
        Pair splitCandidate = (Pair) lastPairs.get(0);
        int stillNeeded = 12 - toExpand;
        if (splitCandidate.hasBackReference()) {
            brLen = splitCandidate.backReferenceLength();
        }
        if (!splitCandidate.hasBackReference() || brLen < stillNeeded + 4) {
            if (splitCandidate.hasBackReference()) {
                replacement.prependLiteral(expand(toExpand + brLen, brLen));
            }
            splitCandidate.prependTo(replacement);
        } else {
            replacement.prependLiteral(expand(toExpand + stillNeeded, stillNeeded));
            this.pairs.add(splitCandidate.splitWithNewBackReferenceLengthOf(brLen - stillNeeded));
        }
        this.pairs.add(replacement);
    }

    public static Builder createParameterBuilder() {
        return Parameters.builder(65536).withMinBackReferenceLength(4).withMaxBackReferenceLength(SupportMenu.USER_MASK).withMaxOffset(SupportMenu.USER_MASK).withMaxLiteralLength(SupportMenu.USER_MASK);
    }
}
