package org.apache.commons.compress.compressors.lz4;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.lz77support.AbstractLZ77CompressorInputStream;
import org.apache.commons.compress.utils.ByteUtils;

public class BlockLZ4CompressorInputStream extends AbstractLZ77CompressorInputStream {
    static final int BACK_REFERENCE_SIZE_MASK = 15;
    static final int LITERAL_SIZE_MASK = 240;
    static final int SIZE_BITS = 4;
    static final int WINDOW_SIZE = 65536;
    private int nextBackReferenceSize;
    private State state = State.NO_BLOCK;

    private enum State {
        NO_BLOCK,
        IN_LITERAL,
        LOOKING_FOR_BACK_REFERENCE,
        IN_BACK_REFERENCE,
        EOF
    }

    public BlockLZ4CompressorInputStream(InputStream is) throws IOException {
        super(is, 65536);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int read(byte[] r6, int r7, int r8) throws java.io.IOException {
        /*
        r5 = this;
        r1 = -1;
        r2 = org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream.C08031.f488xf30f4b5;
        r3 = r5.state;
        r3 = r3.ordinal();
        r2 = r2[r3];
        switch(r2) {
            case 1: goto L_0x003c;
            case 2: goto L_0x0029;
            case 3: goto L_0x002c;
            case 4: goto L_0x0042;
            case 5: goto L_0x004d;
            default: goto L_0x000e;
        };
    L_0x000e:
        r2 = new java.io.IOException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Unknown stream state ";
        r3 = r3.append(r4);
        r4 = r5.state;
        r3 = r3.append(r4);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
    L_0x0029:
        r5.readSizes();
    L_0x002c:
        r1 = r5.readLiteral(r6, r7, r8);
        r2 = r5.hasMoreDataInBlock();
        if (r2 != 0) goto L_0x003a;
    L_0x0036:
        r2 = org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream.State.LOOKING_FOR_BACK_REFERENCE;
        r5.state = r2;
    L_0x003a:
        if (r1 <= 0) goto L_0x003d;
    L_0x003c:
        return r1;
    L_0x003d:
        r1 = r5.read(r6, r7, r8);
        goto L_0x003c;
    L_0x0042:
        r2 = r5.initializeBackReference();
        if (r2 != 0) goto L_0x004d;
    L_0x0048:
        r2 = org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream.State.EOF;
        r5.state = r2;
        goto L_0x003c;
    L_0x004d:
        r0 = r5.readBackReference(r6, r7, r8);
        r2 = r5.hasMoreDataInBlock();
        if (r2 != 0) goto L_0x005b;
    L_0x0057:
        r2 = org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream.State.NO_BLOCK;
        r5.state = r2;
    L_0x005b:
        if (r0 <= 0) goto L_0x005f;
    L_0x005d:
        r1 = r0;
        goto L_0x003c;
    L_0x005f:
        r0 = r5.read(r6, r7, r8);
        goto L_0x005d;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream.read(byte[], int, int):int");
    }

    private void readSizes() throws IOException {
        int nextBlock = readOneByte();
        if (nextBlock == -1) {
            throw new IOException("Premature end of stream while looking for next block");
        }
        this.nextBackReferenceSize = nextBlock & 15;
        long literalSizePart = (long) ((nextBlock & 240) >> 4);
        if (literalSizePart == 15) {
            literalSizePart += readSizeBytes();
        }
        startLiteral(literalSizePart);
        this.state = State.IN_LITERAL;
    }

    private long readSizeBytes() throws IOException {
        long accum = 0;
        int nextByte;
        do {
            nextByte = readOneByte();
            if (nextByte == -1) {
                throw new IOException("Premature end of stream while parsing length");
            }
            accum += (long) nextByte;
        } while (nextByte == 255);
        return accum;
    }

    private boolean initializeBackReference() throws IOException {
        try {
            int backReferenceOffset = (int) ByteUtils.fromLittleEndian(this.supplier, 2);
            long backReferenceSize = (long) this.nextBackReferenceSize;
            if (this.nextBackReferenceSize == 15) {
                backReferenceSize += readSizeBytes();
            }
            startBackReference(backReferenceOffset, 4 + backReferenceSize);
            this.state = State.IN_BACK_REFERENCE;
            return true;
        } catch (IOException ex) {
            if (this.nextBackReferenceSize == 0) {
                return false;
            }
            throw ex;
        }
    }
}
