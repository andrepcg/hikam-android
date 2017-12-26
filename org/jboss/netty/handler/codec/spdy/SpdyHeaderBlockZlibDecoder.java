package org.jboss.netty.handler.codec.spdy;

import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

final class SpdyHeaderBlockZlibDecoder extends SpdyHeaderBlockRawDecoder {
    private static final int DEFAULT_BUFFER_CAPACITY = 4096;
    private static final SpdyProtocolException INVALID_HEADER_BLOCK = new SpdyProtocolException("Invalid Header Block");
    private ChannelBuffer decompressed;
    private final Inflater decompressor = new Inflater();

    SpdyHeaderBlockZlibDecoder(SpdyVersion spdyVersion, int maxHeaderSize) {
        super(spdyVersion, maxHeaderSize);
    }

    void decode(ChannelBuffer headerBlock, SpdyHeadersFrame frame) throws Exception {
        if (headerBlock == null) {
            throw new NullPointerException("headerBlock");
        } else if (frame == null) {
            throw new NullPointerException("frame");
        } else {
            int len = setInput(headerBlock);
            do {
            } while (decompress(frame) > 0);
            if (this.decompressor.getRemaining() != 0) {
                throw INVALID_HEADER_BLOCK;
            }
            headerBlock.skipBytes(len);
        }
    }

    private int setInput(ChannelBuffer compressed) {
        int len = compressed.readableBytes();
        if (compressed.hasArray()) {
            this.decompressor.setInput(compressed.array(), compressed.arrayOffset() + compressed.readerIndex(), len);
        } else {
            byte[] in = new byte[len];
            compressed.getBytes(compressed.readerIndex(), in);
            this.decompressor.setInput(in, 0, in.length);
        }
        return len;
    }

    private int decompress(SpdyHeadersFrame frame) throws Exception {
        ensureBuffer();
        byte[] out = this.decompressed.array();
        int off = this.decompressed.arrayOffset() + this.decompressed.writerIndex();
        try {
            int numBytes = this.decompressor.inflate(out, off, this.decompressed.writableBytes());
            if (numBytes == 0 && this.decompressor.needsDictionary()) {
                this.decompressor.setDictionary(SpdyCodecUtil.SPDY_DICT);
                numBytes = this.decompressor.inflate(out, off, this.decompressed.writableBytes());
            }
            this.decompressed.writerIndex(this.decompressed.writerIndex() + numBytes);
            super.decodeHeaderBlock(this.decompressed, frame);
            this.decompressed.discardReadBytes();
            return numBytes;
        } catch (IllegalArgumentException e) {
            throw INVALID_HEADER_BLOCK;
        } catch (DataFormatException e2) {
            throw INVALID_HEADER_BLOCK;
        }
    }

    private void ensureBuffer() {
        if (this.decompressed == null) {
            this.decompressed = ChannelBuffers.dynamicBuffer(4096);
        }
        this.decompressed.ensureWritableBytes(1);
    }

    void endHeaderBlock(SpdyHeadersFrame frame) throws Exception {
        super.endHeaderBlock(frame);
        this.decompressed = null;
    }

    public void end() {
        super.end();
        this.decompressed = null;
        this.decompressor.end();
    }
}
