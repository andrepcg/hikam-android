package org.apache.commons.compress.compressors.snappy;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.utils.BoundedInputStream;
import org.apache.commons.compress.utils.ByteUtils;
import org.apache.commons.compress.utils.ByteUtils.ByteSupplier;
import org.apache.commons.compress.utils.IOUtils;

public class FramedSnappyCompressorInputStream extends CompressorInputStream {
    static final int COMPRESSED_CHUNK_TYPE = 0;
    static final long MASK_OFFSET = 2726488792L;
    private static final int MAX_SKIPPABLE_TYPE = 253;
    private static final int MAX_UNSKIPPABLE_TYPE = 127;
    private static final int MIN_UNSKIPPABLE_TYPE = 2;
    private static final int PADDING_CHUNK_TYPE = 254;
    private static final int STREAM_IDENTIFIER_TYPE = 255;
    static final byte[] SZ_SIGNATURE = new byte[]{(byte) -1, (byte) 6, (byte) 0, (byte) 0, (byte) 115, (byte) 78, (byte) 97, (byte) 80, (byte) 112, (byte) 89};
    private static final int UNCOMPRESSED_CHUNK_TYPE = 1;
    private final int blockSize;
    private final PureJavaCrc32C checksum;
    private SnappyCompressorInputStream currentCompressedChunk;
    private final FramedSnappyDialect dialect;
    private boolean endReached;
    private long expectedChecksum;
    private final PushbackInputStream in;
    private boolean inUncompressedChunk;
    private final byte[] oneByte;
    private final ByteSupplier supplier;
    private int uncompressedBytesRemaining;

    class C11951 implements ByteSupplier {
        C11951() {
        }

        public int getAsByte() throws IOException {
            return FramedSnappyCompressorInputStream.this.readOneByte();
        }
    }

    public FramedSnappyCompressorInputStream(InputStream in) throws IOException {
        this(in, FramedSnappyDialect.STANDARD);
    }

    public FramedSnappyCompressorInputStream(InputStream in, FramedSnappyDialect dialect) throws IOException {
        this(in, 32768, dialect);
    }

    public FramedSnappyCompressorInputStream(InputStream in, int blockSize, FramedSnappyDialect dialect) throws IOException {
        this.oneByte = new byte[1];
        this.expectedChecksum = -1;
        this.checksum = new PureJavaCrc32C();
        this.supplier = new C11951();
        this.in = new PushbackInputStream(in, 1);
        this.blockSize = blockSize;
        this.dialect = dialect;
        if (dialect.hasStreamIdentifier()) {
            readStreamIdentifier();
        }
    }

    public int read() throws IOException {
        return read(this.oneByte, 0, 1) == -1 ? -1 : this.oneByte[0] & 255;
    }

    public void close() throws IOException {
        if (this.currentCompressedChunk != null) {
            this.currentCompressedChunk.close();
            this.currentCompressedChunk = null;
        }
        this.in.close();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int read = readOnce(b, off, len);
        if (read == -1) {
            readNextBlock();
            if (this.endReached) {
                return -1;
            }
            read = readOnce(b, off, len);
        }
        return read;
    }

    public int available() throws IOException {
        if (this.inUncompressedChunk) {
            return Math.min(this.uncompressedBytesRemaining, this.in.available());
        }
        if (this.currentCompressedChunk != null) {
            return this.currentCompressedChunk.available();
        }
        return 0;
    }

    private int readOnce(byte[] b, int off, int len) throws IOException {
        int read = -1;
        if (this.inUncompressedChunk) {
            int amount = Math.min(this.uncompressedBytesRemaining, len);
            if (amount == 0) {
                return -1;
            }
            read = this.in.read(b, off, amount);
            if (read != -1) {
                this.uncompressedBytesRemaining -= read;
                count(read);
            }
        } else if (this.currentCompressedChunk != null) {
            long before = this.currentCompressedChunk.getBytesRead();
            read = this.currentCompressedChunk.read(b, off, len);
            if (read == -1) {
                this.currentCompressedChunk.close();
                this.currentCompressedChunk = null;
            } else {
                count(this.currentCompressedChunk.getBytesRead() - before);
            }
        }
        if (read > 0) {
            this.checksum.update(b, off, read);
        }
        return read;
    }

    private void readNextBlock() throws IOException {
        verifyLastChecksumAndReset();
        this.inUncompressedChunk = false;
        int type = readOneByte();
        if (type == -1) {
            this.endReached = true;
        } else if (type == 255) {
            this.in.unread(type);
            pushedBackBytes(1);
            readStreamIdentifier();
            readNextBlock();
        } else if (type == PADDING_CHUNK_TYPE || (type > 127 && type <= MAX_SKIPPABLE_TYPE)) {
            skipBlock();
            readNextBlock();
        } else if (type >= 2 && type <= 127) {
            throw new IOException("unskippable chunk with type " + type + " (hex " + Integer.toHexString(type) + ") detected.");
        } else if (type == 1) {
            this.inUncompressedChunk = true;
            this.uncompressedBytesRemaining = readSize() - 4;
            this.expectedChecksum = unmask(readCrc());
        } else if (type == 0) {
            boolean expectChecksum = this.dialect.usesChecksumWithCompressedChunks();
            long size = ((long) readSize()) - (expectChecksum ? 4 : 0);
            if (expectChecksum) {
                this.expectedChecksum = unmask(readCrc());
            } else {
                this.expectedChecksum = -1;
            }
            this.currentCompressedChunk = new SnappyCompressorInputStream(new BoundedInputStream(this.in, size), this.blockSize);
            count(this.currentCompressedChunk.getBytesRead());
        } else {
            throw new IOException("unknown chunk type " + type + " detected.");
        }
    }

    private long readCrc() throws IOException {
        byte[] b = new byte[4];
        int read = IOUtils.readFully(this.in, b);
        count(read);
        if (read == 4) {
            return ByteUtils.fromLittleEndian(b);
        }
        throw new IOException("premature end of stream");
    }

    static long unmask(long x) {
        x = (x - MASK_OFFSET) & 4294967295L;
        return ((x >> 17) | (x << 15)) & 4294967295L;
    }

    private int readSize() throws IOException {
        return (int) ByteUtils.fromLittleEndian(this.supplier, 3);
    }

    private void skipBlock() throws IOException {
        int size = readSize();
        long read = IOUtils.skip(this.in, (long) size);
        count(read);
        if (read != ((long) size)) {
            throw new IOException("premature end of stream");
        }
    }

    private void readStreamIdentifier() throws IOException {
        byte[] b = new byte[10];
        int read = IOUtils.readFully(this.in, b);
        count(read);
        if (10 != read || !matches(b, 10)) {
            throw new IOException("Not a framed Snappy stream");
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

    private void verifyLastChecksumAndReset() throws IOException {
        if (this.expectedChecksum < 0 || this.expectedChecksum == this.checksum.getValue()) {
            this.expectedChecksum = -1;
            this.checksum.reset();
            return;
        }
        throw new IOException("Checksum verification failed");
    }

    public static boolean matches(byte[] signature, int length) {
        if (length < SZ_SIGNATURE.length) {
            return false;
        }
        byte[] shortenedSig = signature;
        if (signature.length > SZ_SIGNATURE.length) {
            shortenedSig = new byte[SZ_SIGNATURE.length];
            System.arraycopy(signature, 0, shortenedSig, 0, SZ_SIGNATURE.length);
        }
        return Arrays.equals(shortenedSig, SZ_SIGNATURE);
    }
}
