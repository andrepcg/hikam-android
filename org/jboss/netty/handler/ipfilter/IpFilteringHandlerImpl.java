package org.jboss.netty.handler.ipfilter;

import java.net.InetSocketAddress;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;

public abstract class IpFilteringHandlerImpl implements ChannelUpstreamHandler, IpFilteringHandler {
    private IpFilterListener listener;

    protected abstract boolean accept(ChannelHandlerContext channelHandlerContext, ChannelEvent channelEvent, InetSocketAddress inetSocketAddress) throws Exception;

    protected ChannelFuture handleRefusedChannel(ChannelHandlerContext ctx, ChannelEvent e, InetSocketAddress inetSocketAddress) throws Exception {
        if (this.listener == null) {
            return null;
        }
        return this.listener.refused(ctx, e, inetSocketAddress);
    }

    protected ChannelFuture handleAllowedChannel(ChannelHandlerContext ctx, ChannelEvent e, InetSocketAddress inetSocketAddress) throws Exception {
        if (this.listener == null) {
            return null;
        }
        return this.listener.allowed(ctx, e, inetSocketAddress);
    }

    protected boolean isBlocked(ChannelHandlerContext ctx) {
        return ctx.getAttachment() != null;
    }

    protected boolean continues(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (this.listener != null) {
            return this.listener.continues(ctx, e);
        }
        return false;
    }

    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            ChannelStateEvent evt = (ChannelStateEvent) e;
            switch (evt.getState()) {
                case OPEN:
                case BOUND:
                    if (evt.getValue() != Boolean.TRUE) {
                        ctx.sendUpstream(e);
                        return;
                    } else if (!isBlocked(ctx) || continues(ctx, evt)) {
                        ctx.sendUpstream(e);
                        return;
                    } else {
                        return;
                    }
                case CONNECTED:
                    if (evt.getValue() != null) {
                        InetSocketAddress inetSocketAddress = (InetSocketAddress) e.getChannel().getRemoteAddress();
                        if (accept(ctx, e, inetSocketAddress)) {
                            handleAllowedChannel(ctx, e, inetSocketAddress);
                        } else {
                            ctx.setAttachment(Boolean.TRUE);
                            ChannelFuture future = handleRefusedChannel(ctx, e, inetSocketAddress);
                            if (future != null) {
                                future.addListener(ChannelFutureListener.CLOSE);
                            } else {
                                Channels.close(e.getChannel());
                            }
                            if (isBlocked(ctx) && !continues(ctx, evt)) {
                                return;
                            }
                        }
                        ctx.setAttachment(null);
                        break;
                    } else if (isBlocked(ctx) && !continues(ctx, evt)) {
                        return;
                    }
            }
        }
        if (!isBlocked(ctx) || continues(ctx, e)) {
            ctx.sendUpstream(e);
        }
    }

    public void setIpFilterListener(IpFilterListener listener) {
        this.listener = listener;
    }

    public void removeIpFilterListener() {
        this.listener = null;
    }
}
