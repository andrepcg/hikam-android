package org.jboss.netty.handler.codec.frame;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

public class FixedLengthFrameDecoder extends FrameDecoder {
    private final boolean allocateFullBuffer;
    private final int frameLength;

    public FixedLengthFrameDecoder(int frameLength) {
        this(frameLength, false);
    }

    public FixedLengthFrameDecoder(int frameLength, boolean allocateFullBuffer) {
        if (frameLength <= 0) {
            throw new IllegalArgumentException("frameLength must be a positive integer: " + frameLength);
        }
        this.frameLength = frameLength;
        this.allocateFullBuffer = allocateFullBuffer;
    }

    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        if (buffer.readableBytes() < this.frameLength) {
            return null;
        }
        Object frame = extractFrame(buffer, buffer.readerIndex(), this.frameLength);
        buffer.skipBytes(this.frameLength);
        return frame;
    }

    protected ChannelBuffer newCumulationBuffer(ChannelHandlerContext ctx, int minimumCapacity) {
        ChannelBufferFactory factory = ctx.getChannel().getConfig().getBufferFactory();
        if (this.allocateFullBuffer) {
            return factory.getBuffer(this.frameLength);
        }
        return super.newCumulationBuffer(ctx, minimumCapacity);
    }
}
