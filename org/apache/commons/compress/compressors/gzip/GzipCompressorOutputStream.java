package org.apache.commons.compress.compressors.gzip;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import org.apache.commons.compress.compressors.CompressorOutputStream;

public class GzipCompressorOutputStream extends CompressorOutputStream {
    private static final int FCOMMENT = 16;
    private static final int FNAME = 8;
    private boolean closed;
    private final CRC32 crc;
    private final byte[] deflateBuffer;
    private final Deflater deflater;
    private final OutputStream out;

    public GzipCompressorOutputStream(OutputStream out) throws IOException {
        this(out, new GzipParameters());
    }

    public GzipCompressorOutputStream(OutputStream out, GzipParameters parameters) throws IOException {
        this.deflateBuffer = new byte[512];
        this.crc = new CRC32();
        this.out = out;
        this.deflater = new Deflater(parameters.getCompressionLevel(), true);
        writeHeader(parameters);
    }

    private void writeHeader(GzipParameters parameters) throws IOException {
        int i;
        String filename = parameters.getFilename();
        String comment = parameters.getComment();
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort((short) -29921);
        buffer.put((byte) 8);
        if (filename != null) {
            i = 8;
        } else {
            i = 0;
        }
        buffer.put((byte) ((comment != null ? 16 : 0) | i));
        buffer.putInt((int) (parameters.getModificationTime() / 1000));
        int compressionLevel = parameters.getCompressionLevel();
        if (compressionLevel == 9) {
            buffer.put((byte) 2);
        } else if (compressionLevel == 1) {
            buffer.put((byte) 4);
        } else {
            buffer.put((byte) 0);
        }
        buffer.put((byte) parameters.getOperatingSystem());
        this.out.write(buffer.array());
        if (filename != null) {
            this.out.write(filename.getBytes("ISO-8859-1"));
            this.out.write(0);
        }
        if (comment != null) {
            this.out.write(comment.getBytes("ISO-8859-1"));
            this.out.write(0);
        }
    }

    private void writeTrailer() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt((int) this.crc.getValue());
        buffer.putInt(this.deflater.getTotalIn());
        this.out.write(buffer.array());
    }

    public void write(int b) throws IOException {
        write(new byte[]{(byte) (b & 255)}, 0, 1);
    }

    public void write(byte[] buffer) throws IOException {
        write(buffer, 0, buffer.length);
    }

    public void write(byte[] buffer, int offset, int length) throws IOException {
        if (this.deflater.finished()) {
            throw new IOException("Cannot write more data, the end of the compressed data stream has been reached");
        } else if (length > 0) {
            this.deflater.setInput(buffer, offset, length);
            while (!this.deflater.needsInput()) {
                deflate();
            }
            this.crc.update(buffer, offset, length);
        }
    }

    private void deflate() throws IOException {
        int length = this.deflater.deflate(this.deflateBuffer, 0, this.deflateBuffer.length);
        if (length > 0) {
            this.out.write(this.deflateBuffer, 0, length);
        }
    }

    public void finish() throws IOException {
        if (!this.deflater.finished()) {
            this.deflater.finish();
            while (!this.deflater.finished()) {
                deflate();
            }
            writeTrailer();
        }
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    public void close() throws IOException {
        if (!this.closed) {
            finish();
            this.deflater.end();
            this.out.close();
            this.closed = true;
        }
    }
}
