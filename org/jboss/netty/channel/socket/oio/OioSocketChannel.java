package org.jboss.netty.channel.socket.oio;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.socket.DefaultSocketChannelConfig;
import org.jboss.netty.channel.socket.SocketChannel;
import org.jboss.netty.channel.socket.SocketChannelConfig;

abstract class OioSocketChannel extends AbstractOioChannel implements SocketChannel {
    private final SocketChannelConfig config;
    final Socket socket;

    abstract PushbackInputStream getInputStream();

    abstract OutputStream getOutputStream();

    OioSocketChannel(Channel parent, ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink, Socket socket) {
        super(parent, factory, pipeline, sink);
        this.socket = socket;
        try {
            socket.setSoTimeout(1000);
            this.config = new DefaultSocketChannelConfig(socket);
        } catch (SocketException e) {
            throw new ChannelException("Failed to configure the OioSocketChannel socket timeout.", e);
        }
    }

    public SocketChannelConfig getConfig() {
        return this.config;
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

    void closeSocket() throws IOException {
        this.socket.close();
    }

    boolean isSocketClosed() {
        return this.socket.isClosed();
    }
}
