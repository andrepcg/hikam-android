package org.jboss.netty.bootstrap;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.ServerChannelFactory;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ServerBootstrap extends Bootstrap {
    private volatile ChannelHandler parentHandler;

    private final class Binder extends SimpleChannelUpstreamHandler {
        private final DefaultChannelFuture bindFuture = new DefaultChannelFuture(null, false);
        private final Map<String, Object> childOptions = new HashMap();
        private final SocketAddress localAddress;

        class C11981 implements ChannelFutureListener {
            C11981() {
            }

            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    Binder.this.bindFuture.setSuccess();
                } else {
                    Binder.this.bindFuture.setFailure(future.getCause());
                }
            }
        }

        Binder(SocketAddress localAddress) {
            this.localAddress = localAddress;
        }

        public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent evt) {
            try {
                evt.getChannel().getConfig().setPipelineFactory(ServerBootstrap.this.getPipelineFactory());
                Map<String, Object> allOptions = ServerBootstrap.this.getOptions();
                Map<String, Object> parentOptions = new HashMap();
                for (Entry<String, Object> e : allOptions.entrySet()) {
                    if (((String) e.getKey()).startsWith("child.")) {
                        this.childOptions.put(((String) e.getKey()).substring(6), e.getValue());
                    } else if (!"pipelineFactory".equals(e.getKey())) {
                        parentOptions.put(e.getKey(), e.getValue());
                    }
                }
                evt.getChannel().getConfig().setOptions(parentOptions);
                evt.getChannel().bind(this.localAddress).addListener(new C11981());
            } finally {
                ctx.sendUpstream(evt);
            }
        }

        public void childChannelOpen(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {
            try {
                e.getChildChannel().getConfig().setOptions(this.childOptions);
            } catch (Throwable t) {
                Channels.fireExceptionCaught(e.getChildChannel(), t);
            }
            ctx.sendUpstream(e);
        }

        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
            this.bindFuture.setFailure(e.getCause());
            ctx.sendUpstream(e);
        }
    }

    public ServerBootstrap(ChannelFactory channelFactory) {
        super(channelFactory);
    }

    public void setFactory(ChannelFactory factory) {
        if (factory == null) {
            throw new NullPointerException("factory");
        } else if (factory instanceof ServerChannelFactory) {
            super.setFactory(factory);
        } else {
            throw new IllegalArgumentException("factory must be a " + ServerChannelFactory.class.getSimpleName() + ": " + factory.getClass());
        }
    }

    public ChannelHandler getParentHandler() {
        return this.parentHandler;
    }

    public void setParentHandler(ChannelHandler parentHandler) {
        this.parentHandler = parentHandler;
    }

    public Channel bind() {
        SocketAddress localAddress = (SocketAddress) getOption("localAddress");
        if (localAddress != null) {
            return bind(localAddress);
        }
        throw new IllegalStateException("localAddress option is not set.");
    }

    public Channel bind(SocketAddress localAddress) {
        ChannelFuture future = bindAsync(localAddress);
        future.awaitUninterruptibly();
        if (future.isSuccess()) {
            return future.getChannel();
        }
        future.getChannel().close().awaitUninterruptibly();
        throw new ChannelException("Failed to bind to: " + localAddress, future.getCause());
    }

    public ChannelFuture bindAsync() {
        SocketAddress localAddress = (SocketAddress) getOption("localAddress");
        if (localAddress != null) {
            return bindAsync(localAddress);
        }
        throw new IllegalStateException("localAddress option is not set.");
    }

    public ChannelFuture bindAsync(SocketAddress localAddress) {
        if (localAddress == null) {
            throw new NullPointerException("localAddress");
        }
        Binder binder = new Binder(localAddress);
        ChannelHandler parentHandler = getParentHandler();
        ChannelPipeline bossPipeline = Channels.pipeline();
        bossPipeline.addLast("binder", binder);
        if (parentHandler != null) {
            bossPipeline.addLast("userHandler", parentHandler);
        }
        final ChannelFuture bfuture = new DefaultChannelFuture(getFactory().newChannel(bossPipeline), false);
        binder.bindFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    bfuture.setSuccess();
                    return;
                }
                bfuture.getChannel().close();
                bfuture.setFailure(future.getCause());
            }
        });
        return bfuture;
    }
}
