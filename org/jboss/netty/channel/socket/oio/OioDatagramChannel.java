package org.jboss.netty.channel.socket.oio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.socket.DatagramChannelConfig;
import org.jboss.netty.channel.socket.DefaultDatagramChannelConfig;

final class OioDatagramChannel extends AbstractOioChannel implements DatagramChannel {
    private final DatagramChannelConfig config;
    final MulticastSocket socket;

    OioDatagramChannel(ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {
        super(null, factory, pipeline, sink);
        try {
            this.socket = new MulticastSocket(null);
            try {
                this.socket.setSoTimeout(10);
                this.socket.setBroadcast(false);
                this.config = new DefaultDatagramChannelConfig(this.socket);
                Channels.fireChannelOpen((Channel) this);
            } catch (SocketException e) {
                throw new ChannelException("Failed to configure the datagram socket timeout.", e);
            }
        } catch (IOException e2) {
            throw new ChannelException("Failed to open a datagram socket.", e2);
        }
    }

    public DatagramChannelConfig getConfig() {
        return this.config;
    }

    public ChannelFuture joinGroup(InetAddress multicastAddress) {
        ensureBound();
        try {
            this.socket.joinGroup(multicastAddress);
            return Channels.succeededFuture(this);
        } catch (IOException e) {
            return Channels.failedFuture(this, e);
        }
    }

    public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
        ensureBound();
        try {
            this.socket.joinGroup(multicastAddress, networkInterface);
            return Channels.succeededFuture(this);
        } catch (IOException e) {
            return Channels.failedFuture(this, e);
        }
    }

    private void ensureBound() {
        if (!isBound()) {
            throw new IllegalStateException(DatagramChannel.class.getName() + " must be bound to join a group.");
        }
    }

    public ChannelFuture leaveGroup(InetAddress multicastAddress) {
        try {
            this.socket.leaveGroup(multicastAddress);
            return Channels.succeededFuture(this);
        } catch (IOException e) {
            return Channels.failedFuture(this, e);
        }
    }

    public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
        try {
            this.socket.leaveGroup(multicastAddress, networkInterface);
            return Channels.succeededFuture(this);
        } catch (IOException e) {
            return Channels.failedFuture(this, e);
        }
    }

    boolean isSocketBound() {
        return this.socket.isBound();
    }

    boolean isSocketConnected() {
        return this.socket.isConnected();
    }

    InetSocketAddress getLocalSocketAddress() throws Exception {
        return (InetSocketAddress) this.socket.getLocalSocketAddress();
    }

    InetSocketAddress getRemoteSocketAddress() throws Exception {
        return (InetSocketAddress) this.socket.getRemoteSocketAddress();
    }

    void closeSocket() {
        this.socket.close();
    }

    boolean isSocketClosed() {
        return this.socket.isClosed();
    }
}
