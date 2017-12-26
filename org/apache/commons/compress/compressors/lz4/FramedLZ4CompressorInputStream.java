package org.apache.commons.compress.compressors.lz4;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.utils.BoundedInputStream;
import org.apache.commons.compress.utils.ByteUtils;
import org.apache.commons.compress.utils.ByteUtils.ByteSupplier;
import org.apache.commons.compress.utils.ChecksumCalculatingInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.jboss.netty.handler.codec.http.HttpConstants;

public class FramedLZ4CompressorInputStream extends CompressorInputStream {
    static final int BLOCK_CHECKSUM_MASK = 16;
    static final int BLOCK_INDEPENDENCE_MASK = 32;
    static final int BLOCK_MAX_SIZE_MASK = 112;
    static final int CONTENT_CHECKSUM_MASK = 4;
    static final int CONTENT_SIZE_MASK = 8;
    static final byte[] LZ4_SIGNATURE = new byte[]{(byte) 4, HttpConstants.DOUBLE_QUOTE, (byte) 77, (byte) 24};
    private static final byte SKIPPABLE_FRAME_PREFIX_BYTE_MASK = (byte) 80;
    private static final byte[] SKIPPABLE_FRAME_TRAILER = new byte[]{(byte) 42, (byte) 77, (byte) 24};
    static final int SUPPORTED_VERSION = 64;
    static final int UNCOMPRESSED_FLAG_MASK = Integer.MIN_VALUE;
    static final int VERSION_MASK = 192;
    private byte[] blockDependencyBuffer;
    private final XXHash32 blockHash;
    private final XXHash32 contentHash;
    private InputStream currentBlock;
    private final boolean decompressConcatenated;
    private boolean endReached;
    private boolean expectBlockChecksum;
    private boolean expectBlockDependency;
    private boolean expectContentChecksum;
    private boolean expectContentSize;
    private final InputStream in;
    private boolean inUncompressed;
    private final byte[] oneByte;
    private final ByteSupplier supplier;

    class C11911 implements ByteSupplier {
        C11911() {
        }

        public int getAsByte() throws IOException {
            return FramedLZ4CompressorInputStream.this.readOneByte();
        }
    }

    public FramedLZ4CompressorInputStream(InputStream in) throws IOException {
        this(in, false);
    }

    public FramedLZ4CompressorInputStream(InputStream in, boolean decompressConcatenated) throws IOException {
        this.oneByte = new byte[1];
        this.supplier = new C11911();
        this.contentHash = new XXHash32();
        this.blockHash = new XXHash32();
        this.in = in;
        this.decompressConcatenated = decompressConcatenated;
        init(true);
    }

    public int read() throws IOException {
        return read(this.oneByte, 0, 1) == -1 ? -1 : this.oneByte[0] & 255;
    }

    public void close() throws IOException {
        if (this.currentBlock != null) {
            this.currentBlock.close();
            this.currentBlock = null;
        }
        this.in.close();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.endReached) {
            return -1;
        }
        int r = readOnce(b, off, len);
        if (r == -1) {
            nextBlock();
            if (!this.endReached) {
                r = readOnce(b, off, len);
            }
        }
        if (r == -1) {
            return r;
        }
        if (this.expectBlockDependency) {
            appendToBlockDependencyBuffer(b, off, r);
        }
        if (!this.expectContentChecksum) {
            return r;
        }
        this.contentHash.update(b, off, r);
        return r;
    }

    private void init(boolean firstFrame) throws IOException {
        if (readSignature(firstFrame)) {
            readFrameDescriptor();
            nextBlock();
        }
    }

    private boolean readSignature(boolean firstFrame) throws IOException {
        String garbageMessage = firstFrame ? "Not a LZ4 frame stream" : "LZ4 frame stream followed by garbage";
        byte[] b = new byte[4];
        int read = IOUtils.readFully(this.in, b);
        count(read);
        if (read == 0 && !firstFrame) {
            this.endReached = true;
            return false;
        } else if (4 != read) {
            throw new IOException(garbageMessage);
        } else {
            read = skipSkippableFrame(b);
            if (read == 0 && !firstFrame) {
                this.endReached = true;
                return false;
            } else if (4 == read && matches(b, 4)) {
                return true;
            } else {
                throw new IOException(garbageMessage);
            }
        }
    }

    private void readFrameDescriptor() throws IOException {
        boolean z = true;
        int flags = readOneByte();
        if (flags == -1) {
            throw new IOException("Premature end of stream while reading frame flags");
        }
        this.contentHash.update(flags);
        if ((flags & VERSION_MASK) != 64) {
            throw new IOException("Unsupported version " + (flags >> 6));
        }
        boolean z2;
        if ((flags & 32) == 0) {
            z2 = true;
        } else {
            z2 = false;
        }
        this.expectBlockDependency = z2;
        if (!this.expectBlockDependency) {
            this.blockDependencyBuffer = null;
        } else if (this.blockDependencyBuffer == null) {
            this.blockDependencyBuffer = new byte[65536];
        }
        if ((flags & 16) != 0) {
            z2 = true;
        } else {
            z2 = false;
        }
        this.expectBlockChecksum = z2;
        if ((flags & 8) != 0) {
            z2 = true;
        } else {
            z2 = false;
        }
        this.expectContentSize = z2;
        if ((flags & 4) == 0) {
            z = false;
        }
        this.expectContentChecksum = z;
        int bdByte = readOneByte();
        if (bdByte == -1) {
            throw new IOException("Premature end of stream while reading frame BD byte");
        }
        this.contentHash.update(bdByte);
        if (this.expectContentSize) {
            byte[] contentSize = new byte[8];
            int skipped = IOUtils.readFully(this.in, contentSize);
            count(skipped);
            if (8 != skipped) {
                throw new IOException("Premature end of stream while reading content size");
            }
            this.contentHash.update(contentSize, 0, contentSize.length);
        }
        int headerHash = readOneByte();
        if (headerHash == -1) {
            throw new IOException("Premature end of stream while reading frame header checksum");
        }
        int expectedHash = (int) ((this.contentHash.getValue() >> 8) & 255);
        this.contentHash.reset();
        if (headerHash != expectedHash) {
            throw new IOException("frame header checksum mismatch.");
        }
    }

    private void nextBlock() throws IOException {
        boolean uncompressed;
        maybeFinishCurrentBlock();
        long len = ByteUtils.fromLittleEndian(this.supplier, 4);
        if ((-2147483648L & len) != 0) {
            uncompressed = true;
        } else {
            uncompressed = false;
        }
        int realLen = (int) (2147483647L & len);
        if (realLen == 0) {
            verifyContentChecksum();
            if (this.decompressConcatenated) {
                init(false);
                return;
            } else {
                this.endReached = true;
                return;
            }
        }
        InputStream capped = new BoundedInputStream(this.in, (long) realLen);
        if (this.expectBlockChecksum) {
            capped = new ChecksumCalculatingInputStream(this.blockHash, capped);
        }
        if (uncompressed) {
            this.inUncompressed = true;
            this.currentBlock = capped;
            return;
        }
        this.inUncompressed = false;
        BlockLZ4CompressorInputStream s = new BlockLZ4CompressorInputStream(capped);
        if (this.expectBlockDependency) {
            s.prefill(this.blockDependencyBuffer);
        }
        this.currentBlock = s;
    }

    private void maybeFinishCurrentBlock() throws IOException {
        if (this.currentBlock != null) {
            this.currentBlock.close();
            this.currentBlock = null;
            if (this.expectBlockChecksum) {
                verifyChecksum(this.blockHash, "block");
                this.blockHash.reset();
            }
        }
    }

    private void verifyContentChecksum() throws IOException {
        if (this.expectContentChecksum) {
            verifyChecksum(this.contentHash, "content");
        }
        this.contentHash.reset();
    }

    private void verifyChecksum(XXHash32 hash, String kind) throws IOException {
        byte[] checksum = new byte[4];
        int read = IOUtils.readFully(this.in, checksum);
        count(read);
        if (4 != read) {
            throw new IOException("Premature end of stream while reading " + kind + " checksum");
        } else if (hash.getValue() != ByteUtils.fromLittleEndian(checksum)) {
            throw new IOException(kind + " checksum mismatch.");
        }
    }

    private int readOneByte() throws IOException {
        int b = this.in.read();
        if (b == -1) {
            return -1;
        }
        count(1);
        return b & 255;
    }

    private int readOnce(byte[] b, int off, int len) throws IOException {
        if (this.inUncompressed) {
            int cnt = this.currentBlock.read(b, off, len);
            count(cnt);
            return cnt;
        }
        BlockLZ4CompressorInputStream l = this.currentBlock;
        long before = l.getBytesRead();
        cnt = this.currentBlock.read(b, off, len);
        count(l.getBytesRead() - before);
        return cnt;
    }

    private static boolean isSkippableFrameSignature(byte[] b) {
        if ((b[0] & 80) != 80) {
            return false;
        }
        for (int i = 1; i < 4; i++) {
            if (b[i] != SKIPPABLE_FRAME_TRAILER[i - 1]) {
                return false;
            }
        }
        return true;
    }

    private int skipSkippableFrame(byte[] b) throws IOException {
        int read = 4;
        while (read == 4 && isSkippableFrameSignature(b)) {
            long len = ByteUtils.fromLittleEndian(this.supplier, 4);
            long skipped = IOUtils.skip(this.in, len);
            count(skipped);
            if (len != skipped) {
                throw new IOException("Premature end of stream while skipping frame");
            }
            read = IOUtils.readFully(this.in, b);
            count(read);
        }
        return read;
    }

    private void appendToBlockDependencyBuffer(byte[] b, int off, int len) {
        len = Math.min(len, this.blockDependencyBuffer.length);
        if (len > 0) {
            int keep = this.blockDependencyBuffer.length - len;
            if (keep > 0) {
                System.arraycopy(this.blockDependencyBuffer, len, this.blockDependencyBuffer, 0, keep);
            }
            System.arraycopy(b, off, this.blockDependencyBuffer, keep, len);
        }
    }

    public static boolean matches(byte[] signature, int length) {
        if (length < LZ4_SIGNATURE.length) {
            return false;
        }
        byte[] shortenedSig = signature;
        if (signature.length > LZ4_SIGNATURE.length) {
            shortenedSig = new byte[LZ4_SIGNATURE.length];
            System.arraycopy(signature, 0, shortenedSig, 0, LZ4_SIGNATURE.length);
        }
        return Arrays.equals(shortenedSig, LZ4_SIGNATURE);
    }
}
