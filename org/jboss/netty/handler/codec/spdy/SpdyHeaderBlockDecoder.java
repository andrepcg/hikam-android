package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.buffer.ChannelBuffer;

abstract class SpdyHeaderBlockDecoder {
    abstract void decode(ChannelBuffer channelBuffer, SpdyHeadersFrame spdyHeadersFrame) throws Exception;

    abstract void end();

    abstract void endHeaderBlock(SpdyHeadersFrame spdyHeadersFrame) throws Exception;

    SpdyHeaderBlockDecoder() {
    }

    static SpdyHeaderBlockDecoder newInstance(SpdyVersion spdyVersion, int maxHeaderSize) {
        return new SpdyHeaderBlockZlibDecoder(spdyVersion, maxHeaderSize);
    }
}
