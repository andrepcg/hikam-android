package org.jboss.netty.handler.codec.spdy;

import java.util.zip.Deflater;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

class SpdyHeaderBlockZlibEncoder extends SpdyHeaderBlockRawEncoder {
    private final Deflater compressor;
    private boolean finished;

    SpdyHeaderBlockZlibEncoder(SpdyVersion spdyVersion, int compressionLevel) {
        super(spdyVersion);
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        }
        this.compressor = new Deflater(compressionLevel);
        this.compressor.setDictionary(SpdyCodecUtil.SPDY_DICT);
    }

    private int setInput(ChannelBuffer decompressed) {
        int len = decompressed.readableBytes();
        if (decompressed.hasArray()) {
            this.compressor.setInput(decompressed.array(), decompressed.arrayOffset() + decompressed.readerIndex(), len);
        } else {
            byte[] in = new byte[len];
            decompressed.getBytes(decompressed.readerIndex(), in);
            this.compressor.setInput(in, 0, in.length);
        }
        return len;
    }

    private void encode(ChannelBuffer compressed) {
        while (compressInto(compressed)) {
            compressed.ensureWritableBytes(compressed.capacity() << 1);
        }
    }

    private boolean compressInto(ChannelBuffer compressed) {
        byte[] out = compressed.array();
        int off = compressed.arrayOffset() + compressed.writerIndex();
        int toWrite = compressed.writableBytes();
        int numBytes = this.compressor.deflate(out, off, toWrite, 2);
        compressed.writerIndex(compressed.writerIndex() + numBytes);
        return numBytes == toWrite;
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
                channelBuffer = ChannelBuffers.dynamicBuffer(decompressed.readableBytes());
                int len = setInput(decompressed);
                encode(channelBuffer);
                decompressed.skipBytes(len);
            }
        }
        return channelBuffer;
    }

    public synchronized void end() {
        if (!this.finished) {
            this.finished = true;
            this.compressor.end();
            super.end();
        }
    }
}
