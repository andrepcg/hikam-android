package org.apache.commons.compress.compressors.snappy;

import android.support.v4.media.TransportMediator;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.lz77support.AbstractLZ77CompressorInputStream;
import org.apache.commons.compress.utils.ByteUtils;

public class SnappyCompressorInputStream extends AbstractLZ77CompressorInputStream {
    public static final int DEFAULT_BLOCK_SIZE = 32768;
    private static final int TAG_MASK = 3;
    private boolean endReached;
    private final int size;
    private State state;
    private int uncompressedBytesRemaining;

    private enum State {
        NO_BLOCK,
        IN_LITERAL,
        IN_BACK_REFERENCE
    }

    public SnappyCompressorInputStream(InputStream is) throws IOException {
        this(is, 32768);
    }

    public SnappyCompressorInputStream(InputStream is, int blockSize) throws IOException {
        super(is, blockSize);
        this.state = State.NO_BLOCK;
        this.endReached = false;
        int readSize = (int) readSize();
        this.size = readSize;
        this.uncompressedBytesRemaining = readSize;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.endReached) {
            return -1;
        }
        switch (this.state) {
            case NO_BLOCK:
                fill();
                return read(b, off, len);
            case IN_LITERAL:
                int litLen = readLiteral(b, off, len);
                if (!hasMoreDataInBlock()) {
                    this.state = State.NO_BLOCK;
                }
                if (litLen <= 0) {
                    return read(b, off, len);
                }
                return litLen;
            case IN_BACK_REFERENCE:
                int backReferenceLen = readBackReference(b, off, len);
                if (!hasMoreDataInBlock()) {
                    this.state = State.NO_BLOCK;
                }
                if (backReferenceLen <= 0) {
                    backReferenceLen = read(b, off, len);
                }
                return backReferenceLen;
            default:
                throw new IOException("Unknown stream state " + this.state);
        }
    }

    private void fill() throws IOException {
        if (this.uncompressedBytesRemaining == 0) {
            this.endReached = true;
            return;
        }
        int b = readOneByte();
        if (b == -1) {
            throw new IOException("Premature end of stream reading block start");
        }
        int length;
        switch (b & 3) {
            case 0:
                length = readLiteralLength(b);
                this.uncompressedBytesRemaining -= length;
                startLiteral((long) length);
                this.state = State.IN_LITERAL;
                return;
            case 1:
                length = ((b >> 2) & 7) + 4;
                this.uncompressedBytesRemaining -= length;
                int offset = (b & 224) << 3;
                b = readOneByte();
                if (b == -1) {
                    throw new IOException("Premature end of stream reading back-reference length");
                }
                startBackReference(offset | b, (long) length);
                this.state = State.IN_BACK_REFERENCE;
                return;
            case 2:
                length = (b >> 2) + 1;
                this.uncompressedBytesRemaining -= length;
                startBackReference((int) ByteUtils.fromLittleEndian(this.supplier, 2), (long) length);
                this.state = State.IN_BACK_REFERENCE;
                return;
            case 3:
                length = (b >> 2) + 1;
                this.uncompressedBytesRemaining -= length;
                startBackReference(((int) ByteUtils.fromLittleEndian(this.supplier, 4)) & Integer.MAX_VALUE, (long) length);
                this.state = State.IN_BACK_REFERENCE;
                return;
            default:
                return;
        }
    }

    private int readLiteralLength(int b) throws IOException {
        int length;
        switch (b >> 2) {
            case 60:
                length = readOneByte();
                if (length == -1) {
                    throw new IOException("Premature end of stream reading literal length");
                }
                break;
            case 61:
                length = (int) ByteUtils.fromLittleEndian(this.supplier, 2);
                break;
            case 62:
                length = (int) ByteUtils.fromLittleEndian(this.supplier, 3);
                break;
            case 63:
                length = (int) ByteUtils.fromLittleEndian(this.supplier, 4);
                break;
            default:
                length = b >> 2;
                break;
        }
        return length + 1;
    }

    private long readSize() throws IOException {
        int index = 0;
        long sz = 0;
        while (true) {
            int b = readOneByte();
            if (b == -1) {
                throw new IOException("Premature end of stream reading size");
            }
            int index2 = index + 1;
            sz |= (long) ((b & TransportMediator.KEYCODE_MEDIA_PAUSE) << (index * 7));
            if ((b & 128) == 0) {
                return sz;
            }
            index = index2;
        }
    }

    public int getSize() {
        return this.size;
    }
}
