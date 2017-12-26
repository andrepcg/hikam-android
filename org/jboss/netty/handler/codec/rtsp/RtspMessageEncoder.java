package org.jboss.netty.handler.codec.rtsp;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpMessageEncoder;

@Sharable
public abstract class RtspMessageEncoder extends HttpMessageEncoder {
    protected RtspMessageEncoder() {
    }

    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        return !(msg instanceof HttpMessage) ? msg : super.encode(ctx, channel, msg);
    }
}
