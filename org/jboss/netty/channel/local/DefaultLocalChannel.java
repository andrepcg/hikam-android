package org.jboss.netty.channel.local;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.jboss.netty.channel.AbstractChannel;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DefaultChannelConfig;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.util.internal.ThreadLocalBoolean;

final class DefaultLocalChannel extends AbstractChannel implements LocalChannel {
    private static final int ST_BOUND = 1;
    private static final int ST_CLOSED = -1;
    private static final int ST_CONNECTED = 2;
    private static final int ST_OPEN = 0;
    private final ChannelConfig config;
    private final ThreadLocalBoolean delivering = new ThreadLocalBoolean();
    volatile LocalAddress localAddress;
    volatile DefaultLocalChannel pairedChannel;
    volatile LocalAddress remoteAddress;
    final AtomicInteger state = new AtomicInteger(0);
    final Queue<MessageEvent> writeBuffer = new ConcurrentLinkedQueue();

    class C12141 implements ChannelFutureListener {
        C12141() {
        }

        public void operationComplete(ChannelFuture future) throws Exception {
            DefaultLocalChannel.this.state.set(-1);
        }
    }

    DefaultLocalChannel(LocalServerChannel parent, ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink, DefaultLocalChannel pairedChannel) {
        super(parent, factory, pipeline, sink);
        this.pairedChannel = pairedChannel;
        this.config = new DefaultChannelConfig();
        getCloseFuture().addListener(new C12141());
        Channels.fireChannelOpen((Channel) this);
    }

    public ChannelConfig getConfig() {
        return this.config;
    }

    public boolean isOpen() {
        return this.state.get() >= 0;
    }

    public boolean isBound() {
        return this.state.get() >= 1;
    }

    public boolean isConnected() {
        return this.state.get() == 2;
    }

    void setBound() throws ClosedChannelException {
        if (!this.state.compareAndSet(0, 1)) {
            switch (this.state.get()) {
                case -1:
                    throw new ClosedChannelException();
                default:
                    throw new ChannelException("already bound");
            }
        }
    }

    void setConnected() {
        if (this.state.get() != -1) {
            this.state.set(2);
        }
    }

    protected boolean setClosed() {
        return super.setClosed();
    }

    public LocalAddress getLocalAddress() {
        return this.localAddress;
    }

    public LocalAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    void closeNow(ChannelFuture future) {
        LocalAddress localAddress = this.localAddress;
        try {
            if (setClosed()) {
                Channel pairedChannel = this.pairedChannel;
                if (pairedChannel != null) {
                    this.pairedChannel = null;
                    Channels.fireChannelDisconnected((Channel) this);
                    Channels.fireChannelUnbound((Channel) this);
                }
                Channels.fireChannelClosed((Channel) this);
                if (pairedChannel == null || !pairedChannel.setClosed()) {
                    future.setSuccess();
                    if (localAddress == null || getParent() != null) {
                    }
                } else {
                    if (pairedChannel.pairedChannel != null) {
                        pairedChannel.pairedChannel = null;
                        Channels.fireChannelDisconnected(pairedChannel);
                        Channels.fireChannelUnbound(pairedChannel);
                    }
                    Channels.fireChannelClosed(pairedChannel);
                    future.setSuccess();
                    if (localAddress == null || getParent() != null) {
                    }
                }
            }
        } finally {
            future.setSuccess();
            if (localAddress != null && getParent() == null) {
                LocalChannelRegistry.unregister(localAddress);
            }
        }
    }

    void flushWriteBuffer() {
        Channel pairedChannel = this.pairedChannel;
        MessageEvent e;
        if (pairedChannel == null) {
            Throwable cause;
            if (isOpen()) {
                cause = new NotYetConnectedException();
            } else {
                cause = new ClosedChannelException();
            }
            while (true) {
                e = (MessageEvent) this.writeBuffer.poll();
                if (e != null) {
                    e.getFuture().setFailure(cause);
                    Channels.fireExceptionCaught((Channel) this, cause);
                } else {
                    return;
                }
            }
        } else if (pairedChannel.isConnected() && !((Boolean) this.delivering.get()).booleanValue()) {
            this.delivering.set(Boolean.valueOf(true));
            while (true) {
                try {
                    e = (MessageEvent) this.writeBuffer.poll();
                    if (e == null) {
                        break;
                    }
                    Channels.fireMessageReceived(pairedChannel, e.getMessage());
                    e.getFuture().setSuccess();
                    Channels.fireWriteComplete((Channel) this, 1);
                } finally {
                    this.delivering.set(Boolean.valueOf(false));
                }
            }
        }
    }
}
