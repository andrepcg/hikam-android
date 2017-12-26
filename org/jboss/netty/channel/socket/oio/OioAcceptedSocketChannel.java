package org.jboss.netty.channel.socket.oio;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.Socket;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.Channels;

class OioAcceptedSocketChannel extends OioSocketChannel {
    private final PushbackInputStream in;
    private final OutputStream out;

    OioAcceptedSocketChannel(Channel parent, ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink, Socket socket) {
        super(parent, factory, pipeline, sink, socket);
        try {
            this.in = new PushbackInputStream(socket.getInputStream(), 1);
            try {
                this.out = socket.getOutputStream();
                Channels.fireChannelOpen((Channel) this);
                Channels.fireChannelBound((Channel) this, getLocalAddress());
            } catch (IOException e) {
                throw new ChannelException("Failed to obtain an OutputStream.", e);
            }
        } catch (IOException e2) {
            throw new ChannelException("Failed to obtain an InputStream.", e2);
        }
    }

    PushbackInputStream getInputStream() {
        return this.in;
    }

    OutputStream getOutputStream() {
        return this.out;
    }
}
