package org.jboss.netty.channel.socket;

import java.net.Socket;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.DefaultChannelConfig;
import org.jboss.netty.util.internal.ConversionUtil;

public class DefaultSocketChannelConfig extends DefaultChannelConfig implements SocketChannelConfig {
    private final Socket socket;

    public DefaultSocketChannelConfig(Socket socket) {
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
        } else if ("sendBufferSize".equals(key)) {
            setSendBufferSize(ConversionUtil.toInt(value));
            return true;
        } else if ("tcpNoDelay".equals(key)) {
            setTcpNoDelay(ConversionUtil.toBoolean(value));
            return true;
        } else if ("keepAlive".equals(key)) {
            setKeepAlive(ConversionUtil.toBoolean(value));
            return true;
        } else if ("reuseAddress".equals(key)) {
            setReuseAddress(ConversionUtil.toBoolean(value));
            return true;
        } else if ("soLinger".equals(key)) {
            setSoLinger(ConversionUtil.toInt(value));
            return true;
        } else if (!"trafficClass".equals(key)) {
            return false;
        } else {
            setTrafficClass(ConversionUtil.toInt(value));
            return true;
        }
    }

    public int getReceiveBufferSize() {
        try {
            return this.socket.getReceiveBufferSize();
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public int getSendBufferSize() {
        try {
            return this.socket.getSendBufferSize();
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public int getSoLinger() {
        try {
            return this.socket.getSoLinger();
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public int getTrafficClass() {
        try {
            return this.socket.getTrafficClass();
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public boolean isKeepAlive() {
        try {
            return this.socket.getKeepAlive();
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public boolean isReuseAddress() {
        try {
            return this.socket.getReuseAddress();
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public boolean isTcpNoDelay() {
        try {
            return this.socket.getTcpNoDelay();
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public void setKeepAlive(boolean keepAlive) {
        try {
            this.socket.setKeepAlive(keepAlive);
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        this.socket.setPerformancePreferences(connectionTime, latency, bandwidth);
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        try {
            this.socket.setReceiveBufferSize(receiveBufferSize);
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

    public void setSendBufferSize(int sendBufferSize) {
        try {
            this.socket.setSendBufferSize(sendBufferSize);
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public void setSoLinger(int soLinger) {
        if (soLinger < 0) {
            try {
                this.socket.setSoLinger(false, 0);
                return;
            } catch (Throwable e) {
                throw new ChannelException(e);
            }
        }
        this.socket.setSoLinger(true, soLinger);
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        try {
            this.socket.setTcpNoDelay(tcpNoDelay);
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public void setTrafficClass(int trafficClass) {
        try {
            this.socket.setTrafficClass(trafficClass);
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }
}
