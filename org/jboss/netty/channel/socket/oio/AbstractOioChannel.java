package org.jboss.netty.channel.socket.oio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import org.jboss.netty.channel.AbstractChannel;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.socket.Worker;

abstract class AbstractOioChannel extends AbstractChannel {
    final Object interestOpsLock = new Object();
    private volatile InetSocketAddress localAddress;
    volatile InetSocketAddress remoteAddress;
    volatile Worker worker;
    volatile Thread workerThread;

    abstract void closeSocket() throws IOException;

    abstract InetSocketAddress getLocalSocketAddress() throws Exception;

    abstract InetSocketAddress getRemoteSocketAddress() throws Exception;

    abstract boolean isSocketBound();

    abstract boolean isSocketClosed();

    abstract boolean isSocketConnected();

    AbstractOioChannel(Channel parent, ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {
        super(parent, factory, pipeline, sink);
    }

    protected boolean setClosed() {
        return super.setClosed();
    }

    protected int getInternalInterestOps() {
        return super.getInternalInterestOps();
    }

    protected void setInternalInterestOps(int interestOps) {
        super.setInternalInterestOps(interestOps);
    }

    public ChannelFuture write(Object message, SocketAddress remoteAddress) {
        if (remoteAddress == null || remoteAddress.equals(getRemoteAddress())) {
            return super.write(message, null);
        }
        return super.write(message, remoteAddress);
    }

    public boolean isBound() {
        return isOpen() && isSocketBound();
    }

    public boolean isConnected() {
        return isOpen() && isSocketConnected();
    }

    public InetSocketAddress getLocalAddress() {
        InetSocketAddress localAddress = this.localAddress;
        if (localAddress == null) {
            try {
                localAddress = getLocalSocketAddress();
                this.localAddress = localAddress;
            } catch (Throwable th) {
                return null;
            }
        }
        return localAddress;
    }

    public InetSocketAddress getRemoteAddress() {
        InetSocketAddress remoteAddress = this.remoteAddress;
        if (remoteAddress == null) {
            try {
                remoteAddress = getRemoteSocketAddress();
                this.remoteAddress = remoteAddress;
            } catch (Throwable th) {
                return null;
            }
        }
        return remoteAddress;
    }
}
