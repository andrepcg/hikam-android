package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.util.internal.DetectionUtil;

abstract class SpdyHeaderBlockEncoder {
    abstract ChannelBuffer encode(SpdyHeadersFrame spdyHeadersFrame) throws Exception;

    abstract void end();

    SpdyHeaderBlockEncoder() {
    }

    static SpdyHeaderBlockEncoder newInstance(SpdyVersion spdyVersion, int compressionLevel, int windowBits, int memLevel) {
        if (DetectionUtil.javaVersion() >= 7) {
            return new SpdyHeaderBlockZlibEncoder(spdyVersion, compressionLevel);
        }
        return new SpdyHeaderBlockJZlibEncoder(spdyVersion, compressionLevel, windowBits, memLevel);
    }
}
