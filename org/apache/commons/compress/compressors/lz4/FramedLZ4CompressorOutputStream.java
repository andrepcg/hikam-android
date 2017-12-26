package org.apache.commons.compress.compressors.lz4;

import android.support.v4.view.accessibility.AccessibilityEventCompat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.utils.ByteUtils;

public class FramedLZ4CompressorOutputStream extends CompressorOutputStream {
    private static final byte[] END_MARK = new byte[4];
    private final byte[] blockData;
    private byte[] blockDependencyBuffer;
    private final XXHash32 blockHash;
    private int collectedBlockDependencyBytes;
    private final XXHash32 contentHash;
    private int currentIndex;
    private boolean finished;
    private final byte[] oneByte;
    private final OutputStream out;
    private final Parameters params;

    public enum BlockSize {
        K64(65536, 4),
        K256(262144, 5),
        M1(1048576, 6),
        M4(AccessibilityEventCompat.TYPE_WINDOWS_CHANGED, 7);
        
        private final int index;
        private final int size;

        private BlockSize(int size, int index) {
            this.size = size;
            this.index = index;
        }

        int getSize() {
            return this.size;
        }

        int getIndex() {
            return this.index;
        }
    }

    public static class Parameters {
        public static final Parameters DEFAULT = new Parameters(BlockSize.M4, true, false, false);
        private final BlockSize blockSize;
        private final org.apache.commons.compress.compressors.lz77support.Parameters lz77params;
        private final boolean withBlockChecksum;
        private final boolean withBlockDependency;
        private final boolean withContentChecksum;

        public Parameters(BlockSize blockSize) {
            this(blockSize, true, false, false);
        }

        public Parameters(BlockSize blockSize, org.apache.commons.compress.compressors.lz77support.Parameters lz77params) {
            this(blockSize, true, false, false, lz77params);
        }

        public Parameters(BlockSize blockSize, boolean withContentChecksum, boolean withBlockChecksum, boolean withBlockDependency) {
            this(blockSize, withContentChecksum, withBlockChecksum, withBlockDependency, BlockLZ4CompressorOutputStream.createParameterBuilder().build());
        }

        public Parameters(BlockSize blockSize, boolean withContentChecksum, boolean withBlockChecksum, boolean withBlockDependency, org.apache.commons.compress.compressors.lz77support.Parameters lz77params) {
            this.blockSize = blockSize;
            this.withContentChecksum = withContentChecksum;
            this.withBlockChecksum = withBlockChecksum;
            this.withBlockDependency = withBlockDependency;
            this.lz77params = lz77params;
        }

        public String toString() {
            return "LZ4 Parameters with BlockSize " + this.blockSize + ", withContentChecksum " + this.withContentChecksum + ", withBlockChecksum " + this.withBlockChecksum + ", withBlockDependency " + this.withBlockDependency;
        }
    }

    public FramedLZ4CompressorOutputStream(OutputStream out) throws IOException {
        this(out, Parameters.DEFAULT);
    }

    public FramedLZ4CompressorOutputStream(OutputStream out, Parameters params) throws IOException {
        byte[] bArr = null;
        this.oneByte = new byte[1];
        this.finished = false;
        this.currentIndex = 0;
        this.contentHash = new XXHash32();
        this.params = params;
        this.blockData = new byte[params.blockSize.getSize()];
        this.out = out;
        this.blockHash = params.withBlockChecksum ? new XXHash32() : null;
        out.write(FramedLZ4CompressorInputStream.LZ4_SIGNATURE);
        writeFrameDescriptor();
        if (params.withBlockDependency) {
            bArr = new byte[65536];
        }
        this.blockDependencyBuffer = bArr;
    }

    public void write(int b) throws IOException {
        this.oneByte[0] = (byte) (b & 255);
        write(this.oneByte);
    }

    public void write(byte[] data, int off, int len) throws IOException {
        if (this.params.withContentChecksum) {
            this.contentHash.update(data, off, len);
        }
        if (this.currentIndex + len > this.blockData.length) {
            flushBlock();
            while (len > this.blockData.length) {
                System.arraycopy(data, off, this.blockData, 0, this.blockData.length);
                off += this.blockData.length;
                len -= this.blockData.length;
                this.currentIndex = this.blockData.length;
                flushBlock();
            }
        }
        System.arraycopy(data, off, this.blockData, this.currentIndex, len);
        this.currentIndex += len;
    }

    public void close() throws IOException {
        finish();
        this.out.close();
    }

    public void finish() throws IOException {
        if (!this.finished) {
            if (this.currentIndex > 0) {
                flushBlock();
            }
            writeTrailer();
            this.finished = true;
        }
    }

    private void writeFrameDescriptor() throws IOException {
        int flags = 64;
        if (!this.params.withBlockDependency) {
            flags = 64 | 32;
        }
        if (this.params.withContentChecksum) {
            flags |= 4;
        }
        if (this.params.withBlockChecksum) {
            flags |= 16;
        }
        this.out.write(flags);
        this.contentHash.update(flags);
        int bd = (this.params.blockSize.getIndex() << 4) & 112;
        this.out.write(bd);
        this.contentHash.update(bd);
        this.out.write((int) ((this.contentHash.getValue() >> 8) & 255));
        this.contentHash.reset();
    }

    private void flushBlock() throws IOException {
        boolean withBlockDependency = this.params.withBlockDependency;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BlockLZ4CompressorOutputStream o = new BlockLZ4CompressorOutputStream(baos, this.params.lz77params);
        Throwable th = null;
        if (withBlockDependency) {
            try {
                o.prefill(this.blockDependencyBuffer, this.blockDependencyBuffer.length - this.collectedBlockDependencyBytes, this.collectedBlockDependencyBytes);
            } catch (Throwable th2) {
                Throwable th3 = th2;
                th2 = th;
                th = th3;
            }
        }
        o.write(this.blockData, 0, this.currentIndex);
        if (o != null) {
            if (th2 != null) {
                try {
                    o.close();
                } catch (Throwable th4) {
                    th2.addSuppressed(th4);
                }
            } else {
                o.close();
            }
        }
        if (withBlockDependency) {
            appendToBlockDependencyBuffer(this.blockData, 0, this.currentIndex);
        }
        byte[] b = baos.toByteArray();
        if (b.length > this.currentIndex) {
            ByteUtils.toLittleEndian(this.out, (long) (this.currentIndex | Integer.MIN_VALUE), 4);
            this.out.write(this.blockData, 0, this.currentIndex);
            if (this.params.withBlockChecksum) {
                this.blockHash.update(this.blockData, 0, this.currentIndex);
            }
        } else {
            ByteUtils.toLittleEndian(this.out, (long) b.length, 4);
            this.out.write(b);
            if (this.params.withBlockChecksum) {
                this.blockHash.update(b, 0, b.length);
            }
        }
        if (this.params.withBlockChecksum) {
            ByteUtils.toLittleEndian(this.out, this.blockHash.getValue(), 4);
            this.blockHash.reset();
        }
        this.currentIndex = 0;
        return;
        if (o != null) {
            if (th2 != null) {
                try {
                    o.close();
                } catch (Throwable th5) {
                    th2.addSuppressed(th5);
                }
            } else {
                o.close();
            }
        }
        throw th4;
        throw th4;
    }

    private void writeTrailer() throws IOException {
        this.out.write(END_MARK);
        if (this.params.withContentChecksum) {
            ByteUtils.toLittleEndian(this.out, this.contentHash.getValue(), 4);
        }
    }

    private void appendToBlockDependencyBuffer(byte[] b, int off, int len) {
        len = Math.min(len, this.blockDependencyBuffer.length);
        if (len > 0) {
            int keep = this.blockDependencyBuffer.length - len;
            if (keep > 0) {
                System.arraycopy(this.blockDependencyBuffer, len, this.blockDependencyBuffer, 0, keep);
            }
            System.arraycopy(b, off, this.blockDependencyBuffer, keep, len);
            this.collectedBlockDependencyBytes = Math.min(this.collectedBlockDependencyBytes + len, this.blockDependencyBuffer.length);
        }
    }
}
