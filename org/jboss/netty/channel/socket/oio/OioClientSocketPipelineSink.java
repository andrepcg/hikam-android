package org.jboss.netty.channel.socket.oio;

import java.io.PushbackInputStream;
import java.net.SocketAddress;
import java.util.concurrent.Executor;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.ThreadRenamingRunnable;
import org.jboss.netty.util.internal.DeadLockProofWorker;

class OioClientSocketPipelineSink extends AbstractOioChannelSink {
    private final ThreadNameDeterminer determiner;
    private final Executor workerExecutor;

    OioClientSocketPipelineSink(Executor workerExecutor, ThreadNameDeterminer determiner) {
        this.workerExecutor = workerExecutor;
        this.determiner = determiner;
    }

    public void eventSunk(ChannelPipeline pipeline, ChannelEvent e) throws Exception {
        OioClientSocketChannel channel = (OioClientSocketChannel) e.getChannel();
        ChannelFuture future = e.getFuture();
        if (e instanceof ChannelStateEvent) {
            ChannelStateEvent stateEvent = (ChannelStateEvent) e;
            ChannelState state = stateEvent.getState();
            Object value = stateEvent.getValue();
            switch (state) {
                case OPEN:
                    if (Boolean.FALSE.equals(value)) {
                        AbstractOioWorker.close(channel, future);
                        return;
                    }
                    return;
                case BOUND:
                    if (value != null) {
                        bind(channel, future, (SocketAddress) value);
                        return;
                    } else {
                        AbstractOioWorker.close(channel, future);
                        return;
                    }
                case CONNECTED:
                    if (value != null) {
                        connect(channel, future, (SocketAddress) value);
                        return;
                    } else {
                        AbstractOioWorker.close(channel, future);
                        return;
                    }
                case INTEREST_OPS:
                    AbstractOioWorker.setInterestOps(channel, future, ((Integer) value).intValue());
                    return;
                default:
                    return;
            }
        } else if (e instanceof MessageEvent) {
            OioWorker.write(channel, future, ((MessageEvent) e).getMessage());
        }
    }

    private static void bind(OioClientSocketChannel channel, ChannelFuture future, SocketAddress localAddress) {
        try {
            channel.socket.bind(localAddress);
            future.setSuccess();
            Channels.fireChannelBound((Channel) channel, channel.getLocalAddress());
        } catch (Throwable t) {
            future.setFailure(t);
            Channels.fireExceptionCaught((Channel) channel, t);
        }
    }

    private void connect(OioClientSocketChannel channel, ChannelFuture future, SocketAddress remoteAddress) {
        boolean bound = channel.isBound();
        future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        try {
            channel.socket.connect(remoteAddress, channel.getConfig().getConnectTimeoutMillis());
            channel.in = new PushbackInputStream(channel.socket.getInputStream(), 1);
            channel.out = channel.socket.getOutputStream();
            future.setSuccess();
            if (!bound) {
                Channels.fireChannelBound((Channel) channel, channel.getLocalAddress());
            }
            Channels.fireChannelConnected((Channel) channel, channel.getRemoteAddress());
            DeadLockProofWorker.start(this.workerExecutor, new ThreadRenamingRunnable(new OioWorker(channel), "Old I/O client worker (" + channel + ')', this.determiner));
            if (!true || true) {
                return;
            }
        } catch (Throwable th) {
            if (null != null && null == null) {
                AbstractOioWorker.close(channel, future);
            }
        }
        AbstractOioWorker.close(channel, future);
    }
}
