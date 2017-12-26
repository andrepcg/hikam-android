package org.apache.commons.compress.compressors.snappy;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor.BackReference;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor.Block;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor.Callback;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor.LiteralBlock;
import org.apache.commons.compress.compressors.lz77support.Parameters;
import org.apache.commons.compress.compressors.lz77support.Parameters.Builder;
import org.apache.commons.compress.utils.ByteUtils;
import org.apache.commons.compress.utils.ByteUtils.ByteConsumer;
import org.apache.commons.compress.utils.ByteUtils.OutputStreamByteConsumer;

public class SnappyCompressorOutputStream extends CompressorOutputStream {
    private static final int FOUR_BYTE_COPY_TAG = 3;
    private static final int FOUR_SIZE_BYTE_MARKER = 252;
    private static final int MAX_LITERAL_SIZE_WITHOUT_SIZE_BYTES = 60;
    private static final int MAX_LITERAL_SIZE_WITH_ONE_SIZE_BYTE = 256;
    private static final int MAX_LITERAL_SIZE_WITH_THREE_SIZE_BYTES = 16777216;
    private static final int MAX_LITERAL_SIZE_WITH_TWO_SIZE_BYTES = 65536;
    private static final int MAX_MATCH_LENGTH = 64;
    private static final int MAX_MATCH_LENGTH_WITH_ONE_OFFSET_BYTE = 11;
    private static final int MAX_OFFSET_WITH_ONE_OFFSET_BYTE = 1024;
    private static final int MAX_OFFSET_WITH_TWO_OFFSET_BYTES = 32768;
    private static final int MIN_MATCH_LENGTH = 4;
    private static final int MIN_MATCH_LENGTH_WITH_ONE_OFFSET_BYTE = 4;
    private static final int ONE_BYTE_COPY_TAG = 1;
    private static final int ONE_SIZE_BYTE_MARKER = 240;
    private static final int THREE_SIZE_BYTE_MARKER = 248;
    private static final int TWO_BYTE_COPY_TAG = 2;
    private static final int TWO_SIZE_BYTE_MARKER = 244;
    private final LZ77Compressor compressor;
    private final ByteConsumer consumer;
    private boolean finished;
    private final byte[] oneByte;
    private final OutputStream os;

    class C11961 implements Callback {
        C11961() {
        }

        public void accept(Block block) throws IOException {
            if (block instanceof LiteralBlock) {
                SnappyCompressorOutputStream.this.writeLiteralBlock((LiteralBlock) block);
            } else if (block instanceof BackReference) {
                SnappyCompressorOutputStream.this.writeBackReference((BackReference) block);
            }
        }
    }

    public SnappyCompressorOutputStream(OutputStream os, long uncompressedSize) throws IOException {
        this(os, uncompressedSize, 32768);
    }

    public SnappyCompressorOutputStream(OutputStream os, long uncompressedSize, int blockSize) throws IOException {
        this(os, uncompressedSize, createParameterBuilder(blockSize).build());
    }

    public SnappyCompressorOutputStream(OutputStream os, long uncompressedSize, Parameters params) throws IOException {
        this.oneByte = new byte[1];
        this.finished = false;
        this.os = os;
        this.consumer = new OutputStreamByteConsumer(os);
        this.compressor = new LZ77Compressor(params, new C11961());
        writeUncompressedSize(uncompressedSize);
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

    private void writeUncompressedSize(long uncompressedSize) throws IOException {
        boolean more;
        do {
            int currentByte = (int) (127 & uncompressedSize);
            more = uncompressedSize > ((long) currentByte);
            if (more) {
                currentByte |= 128;
            }
            this.os.write(currentByte);
            uncompressedSize >>= 7;
        } while (more);
    }

    private void writeLiteralBlock(LiteralBlock block) throws IOException {
        int len = block.getLength();
        if (len <= 60) {
            writeLiteralBlockNoSizeBytes(block, len);
        } else if (len <= 256) {
            writeLiteralBlockOneSizeByte(block, len);
        } else if (len <= 65536) {
            writeLiteralBlockTwoSizeBytes(block, len);
        } else if (len <= 16777216) {
            writeLiteralBlockThreeSizeBytes(block, len);
        } else {
            writeLiteralBlockFourSizeBytes(block, len);
        }
    }

    private void writeLiteralBlockNoSizeBytes(LiteralBlock block, int len) throws IOException {
        writeLiteralBlockWithSize((len - 1) << 2, 0, len, block);
    }

    private void writeLiteralBlockOneSizeByte(LiteralBlock block, int len) throws IOException {
        writeLiteralBlockWithSize(240, 1, len, block);
    }

    private void writeLiteralBlockTwoSizeBytes(LiteralBlock block, int len) throws IOException {
        writeLiteralBlockWithSize(TWO_SIZE_BYTE_MARKER, 2, len, block);
    }

    private void writeLiteralBlockThreeSizeBytes(LiteralBlock block, int len) throws IOException {
        writeLiteralBlockWithSize(THREE_SIZE_BYTE_MARKER, 3, len, block);
    }

    private void writeLiteralBlockFourSizeBytes(LiteralBlock block, int len) throws IOException {
        writeLiteralBlockWithSize(FOUR_SIZE_BYTE_MARKER, 4, len, block);
    }

    private void writeLiteralBlockWithSize(int tagByte, int sizeBytes, int len, LiteralBlock block) throws IOException {
        this.os.write(tagByte);
        writeLittleEndian(sizeBytes, len - 1);
        this.os.write(block.getData(), block.getOffset(), len);
    }

    private void writeLittleEndian(int numBytes, int num) throws IOException {
        ByteUtils.toLittleEndian(this.consumer, (long) num, numBytes);
    }

    private void writeBackReference(BackReference block) throws IOException {
        int len = block.getLength();
        int offset = block.getOffset();
        if (len >= 4 && len <= 11 && offset <= 1024) {
            writeBackReferenceWithOneOffsetByte(len, offset);
        } else if (offset < 32768) {
            writeBackReferenceWithTwoOffsetBytes(len, offset);
        } else {
            writeBackReferenceWithFourOffsetBytes(len, offset);
        }
    }

    private void writeBackReferenceWithOneOffsetByte(int len, int offset) throws IOException {
        this.os.write((((len - 4) << 2) | 1) | ((offset & 1792) >> 3));
        this.os.write(offset & 255);
    }

    private void writeBackReferenceWithTwoOffsetBytes(int len, int offset) throws IOException {
        writeBackReferenceWithLittleEndianOffset(2, 2, len, offset);
    }

    private void writeBackReferenceWithFourOffsetBytes(int len, int offset) throws IOException {
        writeBackReferenceWithLittleEndianOffset(3, 4, len, offset);
    }

    private void writeBackReferenceWithLittleEndianOffset(int tag, int offsetBytes, int len, int offset) throws IOException {
        this.os.write(((len - 1) << 2) | tag);
        writeLittleEndian(offsetBytes, offset);
    }

    public static Builder createParameterBuilder(int blockSize) {
        return Parameters.builder(blockSize).withMinBackReferenceLength(4).withMaxBackReferenceLength(64).withMaxOffset(blockSize).withMaxLiteralLength(blockSize);
    }
}
