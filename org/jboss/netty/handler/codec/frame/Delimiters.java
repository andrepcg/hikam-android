package org.jboss.netty.handler.codec.frame;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpConstants;

public final class Delimiters {
    public static ChannelBuffer[] nulDelimiter() {
        ChannelBuffer[] channelBufferArr = new ChannelBuffer[1];
        channelBufferArr[0] = ChannelBuffers.wrappedBuffer(new byte[]{(byte) 0});
        return channelBufferArr;
    }

    public static ChannelBuffer[] lineDelimiter() {
        ChannelBuffer[] channelBufferArr = new ChannelBuffer[2];
        channelBufferArr[0] = ChannelBuffers.wrappedBuffer(new byte[]{HttpConstants.CR, (byte) 10});
        channelBufferArr[1] = ChannelBuffers.wrappedBuffer(new byte[]{(byte) 10});
        return channelBufferArr;
    }

    private Delimiters() {
    }
}
