package org.jboss.netty.channel.socket;

import java.net.ServerSocket;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.DefaultServerChannelConfig;
import org.jboss.netty.util.internal.ConversionUtil;

public class DefaultServerSocketChannelConfig extends DefaultServerChannelConfig implements ServerSocketChannelConfig {
    private volatile int backlog;
    private final ServerSocket socket;

    public DefaultServerSocketChannelConfig(ServerSocket socket) {
        if (socket == null) {
            throw new NullPointerException("socket");
        }
        this.socket = socket;
    }

    public boolean setOption(String key, Object value) {
        if (super.setOption(key, value)) {
            return true;
        }
        if ("receiveBufferSize".equals(key)) {
            setReceiveBufferSize(ConversionUtil.toInt(value));
            return true;
        } else if ("reuseAddress".equals(key)) {
            setReuseAddress(ConversionUtil.toBoolean(value));
            return true;
        } else if (!"backlog".equals(key)) {
            return false;
        } else {
            setBacklog(ConversionUtil.toInt(value));
            return true;
        }
    }

    public boolean isReuseAddress() {
        try {
            return this.socket.getReuseAddress();
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public void setReuseAddress(boolean reuseAddress) {
        try {
            this.socket.setReuseAddress(reuseAddress);
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public int getReceiveBufferSize() {
        try {
            return this.socket.getReceiveBufferSize();
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        try {
            this.socket.setReceiveBufferSize(receiveBufferSize);
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        this.socket.setPerformancePreferences(connectionTime, latency, bandwidth);
    }

    public int getBacklog() {
        return this.backlog;
    }

    public void setBacklog(int backlog) {
        if (backlog < 0) {
            throw new IllegalArgumentException("backlog: " + backlog);
        }
        this.backlog = backlog;
    }
}
