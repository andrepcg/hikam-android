package org.apache.commons.compress.archivers.dump;

import android.support.v4.media.session.PlaybackStateCompat;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.apache.commons.compress.archivers.dump.DumpArchiveConstants.COMPRESSION_TYPE;
import org.apache.commons.compress.utils.IOUtils;

class TapeInputStream extends FilterInputStream {
    private static final int RECORD_SIZE = 1024;
    private byte[] blockBuffer = new byte[1024];
    private int blockSize = 1024;
    private long bytesRead = 0;
    private int currBlkIdx = -1;
    private boolean isCompressed = false;
    private int readOffset = 1024;

    public TapeInputStream(InputStream in) {
        super(in);
    }

    public void resetBlockSize(int recsPerBlock, boolean isCompressed) throws IOException {
        this.isCompressed = isCompressed;
        this.blockSize = recsPerBlock * 1024;
        byte[] oldBuffer = this.blockBuffer;
        this.blockBuffer = new byte[this.blockSize];
        System.arraycopy(oldBuffer, 0, this.blockBuffer, 0, 1024);
        readFully(this.blockBuffer, 1024, this.blockSize - 1024);
        this.currBlkIdx = 0;
        this.readOffset = 1024;
    }

    public int available() throws IOException {
        if (this.readOffset < this.blockSize) {
            return this.blockSize - this.readOffset;
        }
        return this.in.available();
    }

    public int read() throws IOException {
        throw new IllegalArgumentException("all reads must be multiple of record size (1024 bytes.");
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (len % 1024 != 0) {
            throw new IllegalArgumentException("all reads must be multiple of record size (1024 bytes.");
        }
        int bytes = 0;
        while (bytes < len) {
            int n;
            if (this.readOffset == this.blockSize) {
                try {
                    readBlock(true);
                } catch (ShortFileException e) {
                    return -1;
                }
            }
            if (this.readOffset + (len - bytes) <= this.blockSize) {
                n = len - bytes;
            } else {
                n = this.blockSize - this.readOffset;
            }
            System.arraycopy(this.blockBuffer, this.readOffset, b, off, n);
            this.readOffset += n;
            bytes += n;
            off += n;
        }
        return bytes;
    }

    public long skip(long len) throws IOException {
        if (len % PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID != 0) {
            throw new IllegalArgumentException("all reads must be multiple of record size (1024 bytes.");
        }
        long bytes = 0;
        while (bytes < len) {
            long n;
            if (this.readOffset == this.blockSize) {
                try {
                    readBlock(len - bytes < ((long) this.blockSize));
                } catch (ShortFileException e) {
                    return -1;
                }
            }
            if (((long) this.readOffset) + (len - bytes) <= ((long) this.blockSize)) {
                n = len - bytes;
            } else {
                n = ((long) this.blockSize) - ((long) this.readOffset);
            }
            this.readOffset = (int) (((long) this.readOffset) + n);
            bytes += n;
        }
        return bytes;
    }

    public void close() throws IOException {
        if (this.in != null && this.in != System.in) {
            this.in.close();
        }
    }

    public byte[] peek() throws IOException {
        if (this.readOffset == this.blockSize) {
            try {
                readBlock(true);
            } catch (ShortFileException e) {
                return null;
            }
        }
        byte[] b = new byte[1024];
        System.arraycopy(this.blockBuffer, this.readOffset, b, 0, b.length);
        return b;
    }

    public byte[] readRecord() throws IOException {
        byte[] result = new byte[1024];
        if (-1 != read(result, 0, result.length)) {
            return result;
        }
        throw new ShortFileException();
    }

    private void readBlock(boolean decompress) throws IOException {
        boolean compressed = true;
        if (this.in == null) {
            throw new IOException("input buffer is closed");
        }
        if (!this.isCompressed || this.currBlkIdx == -1) {
            readFully(this.blockBuffer, 0, this.blockSize);
            this.bytesRead += (long) this.blockSize;
        } else {
            readFully(this.blockBuffer, 0, 4);
            this.bytesRead += 4;
            int h = DumpArchiveUtil.convert32(this.blockBuffer, 0);
            if ((h & 1) != 1) {
                compressed = false;
            }
            if (compressed) {
                int flags = (h >> 1) & 7;
                int length = (h >> 4) & 268435455;
                byte[] compBuffer = new byte[length];
                readFully(compBuffer, 0, length);
                this.bytesRead += (long) length;
                if (decompress) {
                    switch (COMPRESSION_TYPE.find(flags & 3)) {
                        case ZLIB:
                            Inflater inflator = new Inflater();
                            try {
                                inflator.setInput(compBuffer, 0, compBuffer.length);
                                if (inflator.inflate(this.blockBuffer) == this.blockSize) {
                                    inflator.end();
                                    break;
                                }
                                throw new ShortFileException();
                            } catch (DataFormatException e) {
                                throw new DumpArchiveException("bad data", e);
                            } catch (Throwable th) {
                                inflator.end();
                            }
                        case BZLIB:
                            throw new UnsupportedCompressionAlgorithmException("BZLIB2");
                        case LZO:
                            throw new UnsupportedCompressionAlgorithmException("LZO");
                        default:
                            throw new UnsupportedCompressionAlgorithmException();
                    }
                }
                Arrays.fill(this.blockBuffer, (byte) 0);
            } else {
                readFully(this.blockBuffer, 0, this.blockSize);
                this.bytesRead += (long) this.blockSize;
            }
        }
        this.currBlkIdx++;
        this.readOffset = 0;
    }

    private void readFully(byte[] b, int off, int len) throws IOException {
        if (IOUtils.readFully(this.in, b, off, len) < len) {
            throw new ShortFileException();
        }
    }

    public long getBytesRead() {
        return this.bytesRead;
    }
}
