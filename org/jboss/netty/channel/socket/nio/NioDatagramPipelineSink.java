package org.jboss.netty.channel.socket.nio;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;

class NioDatagramPipelineSink extends AbstractNioChannelSink {
    static final /* synthetic */ boolean $assertionsDisabled = (!NioDatagramPipelineSink.class.desiredAssertionStatus());
    private final WorkerPool<NioDatagramWorker> workerPool;

    NioDatagramPipelineSink(WorkerPool<NioDatagramWorker> workerPool) {
        this.workerPool = workerPool;
    }

    public void eventSunk(ChannelPipeline pipeline, ChannelEvent e) throws Exception {
        NioDatagramChannel channel = (NioDatagramChannel) e.getChannel();
        ChannelFuture future = e.getFuture();
        if (e instanceof ChannelStateEvent) {
            ChannelStateEvent stateEvent = (ChannelStateEvent) e;
            ChannelState state = stateEvent.getState();
            Object value = stateEvent.getValue();
            switch (state) {
                case OPEN:
                    if (Boolean.FALSE.equals(value)) {
                        channel.worker.close(channel, future);
                        return;
                    }
                    return;
                case BOUND:
                    if (value != null) {
                        bind(channel, future, (InetSocketAddress) value);
                        return;
                    } else {
                        channel.worker.close(channel, future);
                        return;
                    }
                case CONNECTED:
                    if (value != null) {
                        connect(channel, future, (InetSocketAddress) value);
                        return;
                    } else {
                        NioDatagramWorker.disconnect(channel, future);
                        return;
                    }
                case INTEREST_OPS:
                    channel.worker.setInterestOps(channel, future, ((Integer) value).intValue());
                    return;
                default:
                    return;
            }
        } else if (e instanceof MessageEvent) {
            boolean offered = channel.writeBufferQueue.offer((MessageEvent) e);
            if ($assertionsDisabled || offered) {
                channel.worker.writeFromUserCode(channel);
                return;
            }
            throw new AssertionError();
        }
    }

    private static void close(NioDatagramChannel channel, ChannelFuture future) {
        try {
            channel.getDatagramChannel().socket().close();
            if (channel.setClosed()) {
                future.setSuccess();
                if (channel.isBound()) {
                    Channels.fireChannelUnbound((Channel) channel);
                }
                Channels.fireChannelClosed((Channel) channel);
                return;
            }
            future.setSuccess();
        } catch (Throwable t) {
            future.setFailure(t);
            Channels.fireExceptionCaught((Channel) channel, t);
        }
    }

    private static void bind(NioDatagramChannel channel, ChannelFuture future, InetSocketAddress address) {
        boolean bound = false;
        try {
            channel.getDatagramChannel().socket().bind(address);
            bound = true;
            future.setSuccess();
            Channels.fireChannelBound((Channel) channel, (SocketAddress) address);
            channel.worker.register(channel, null);
            if (true || 1 == null) {
                return;
            }
        } catch (Throwable th) {
            if (null == null && bound) {
                close(channel, future);
            }
        }
        close(channel, future);
    }

    private static void connect(NioDatagramChannel channel, ChannelFuture future, InetSocketAddress remoteAddress) {
        boolean bound = channel.isBound();
        future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        channel.remoteAddress = null;
        AbstractNioWorker abstractNioWorker;
        try {
            channel.getDatagramChannel().connect(remoteAddress);
            future.setSuccess();
            if (!bound) {
                Channels.fireChannelBound((Channel) channel, channel.getLocalAddress());
            }
            Channels.fireChannelConnected((Channel) channel, channel.getRemoteAddress());
            if (!bound) {
                channel.worker.register(channel, future);
            }
            if (true && !true) {
                abstractNioWorker = channel.worker;
                abstractNioWorker.close(channel, future);
            }
        } catch (Throwable th) {
            if (null != null && null == null) {
                channel.worker.close(channel, future);
            }
        }
    }

    NioDatagramWorker nextWorker() {
        return (NioDatagramWorker) this.workerPool.nextWorker();
    }
}
