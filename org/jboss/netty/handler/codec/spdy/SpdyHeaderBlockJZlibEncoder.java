package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.compression.CompressionException;
import org.jboss.netty.util.internal.jzlib.JZlib;
import org.jboss.netty.util.internal.jzlib.ZStream;

class SpdyHeaderBlockJZlibEncoder extends SpdyHeaderBlockRawEncoder {
    private boolean finished;
    private final ZStream f705z = new ZStream();

    SpdyHeaderBlockJZlibEncoder(SpdyVersion spdyVersion, int compressionLevel, int windowBits, int memLevel) {
        super(spdyVersion);
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        } else if (windowBits < 9 || windowBits > 15) {
            throw new IllegalArgumentException("windowBits: " + windowBits + " (expected: 9-15)");
        } else if (memLevel < 1 || memLevel > 9) {
            throw new IllegalArgumentException("memLevel: " + memLevel + " (expected: 1-9)");
        } else {
            int resultCode = this.f705z.deflateInit(compressionLevel, windowBits, memLevel, JZlib.W_ZLIB);
            if (resultCode != 0) {
                throw new CompressionException("failed to initialize an SPDY header block deflater: " + resultCode);
            }
            resultCode = this.f705z.deflateSetDictionary(SpdyCodecUtil.SPDY_DICT, SpdyCodecUtil.SPDY_DICT.length);
            if (resultCode != 0) {
                throw new CompressionException("failed to set the SPDY dictionary: " + resultCode);
            }
        }
    }

    private void setInput(ChannelBuffer decompressed) {
        byte[] in = new byte[decompressed.readableBytes()];
        decompressed.readBytes(in);
        this.f705z.next_in = in;
        this.f705z.next_in_index = 0;
        this.f705z.avail_in = in.length;
    }

    private void encode(ChannelBuffer compressed) {
        try {
            byte[] out = new byte[(((int) Math.ceil(((double) this.f705z.next_in.length) * 1.001d)) + 12)];
            this.f705z.next_out = out;
            this.f705z.next_out_index = 0;
            this.f705z.avail_out = out.length;
            int resultCode = this.f705z.deflate(2);
            if (resultCode != 0) {
                throw new CompressionException("compression failure: " + resultCode);
            }
            if (this.f705z.next_out_index != 0) {
                compressed.writeBytes(out, 0, this.f705z.next_out_index);
            }
            this.f705z.next_in = null;
            this.f705z.next_out = null;
        } catch (Throwable th) {
            this.f705z.next_in = null;
            this.f705z.next_out = null;
        }
    }

    public synchronized ChannelBuffer encode(SpdyHeadersFrame frame) throws Exception {
        ChannelBuffer channelBuffer;
        if (frame == null) {
            throw new IllegalArgumentException("frame");
        } else if (this.finished) {
            channelBuffer = ChannelBuffers.EMPTY_BUFFER;
        } else {
            ChannelBuffer decompressed = super.encode(frame);
            if (decompressed.readableBytes() == 0) {
                channelBuffer = ChannelBuffers.EMPTY_BUFFER;
            } else {
                channelBuffer = ChannelBuffers.dynamicBuffer();
                setInput(decompressed);
                encode(channelBuffer);
            }
        }
        return channelBuffer;
    }

    public synchronized void end() {
        if (!this.finished) {
            this.finished = true;
            this.f705z.deflateEnd();
            this.f705z.next_in = null;
            this.f705z.next_out = null;
        }
    }
}
