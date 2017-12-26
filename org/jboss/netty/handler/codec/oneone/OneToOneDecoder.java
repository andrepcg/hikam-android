package org.jboss.netty.handler.codec.oneone;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;

public abstract class OneToOneDecoder implements ChannelUpstreamHandler {
    protected abstract Object decode(ChannelHandlerContext channelHandlerContext, Channel channel, Object obj) throws Exception;

    protected OneToOneDecoder() {
    }

    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {
        if (evt instanceof MessageEvent) {
            MessageEvent e = (MessageEvent) evt;
            Object originalMessage = e.getMessage();
            Object decodedMessage = decode(ctx, e.getChannel(), originalMessage);
            if (originalMessage == decodedMessage) {
                ctx.sendUpstream(evt);
                return;
            } else if (decodedMessage != null) {
                Channels.fireMessageReceived(ctx, decodedMessage, e.getRemoteAddress());
                return;
            } else {
                return;
            }
        }
        ctx.sendUpstream(evt);
    }
}
