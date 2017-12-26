package org.jboss.netty.channel.socket.nio;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.util.Enumeration;
import java.util.Map;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.socket.DefaultDatagramChannelConfig;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.internal.ConversionUtil;
import org.jboss.netty.util.internal.DetectionUtil;

class DefaultNioDatagramChannelConfig extends DefaultDatagramChannelConfig implements NioDatagramChannelConfig {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultNioDatagramChannelConfig.class);
    private final DatagramChannel channel;
    private volatile int writeBufferHighWaterMark = 65536;
    private volatile int writeBufferLowWaterMark = 32768;
    private volatile int writeSpinCount = 16;

    DefaultNioDatagramChannelConfig(DatagramChannel channel) {
        super(channel.socket());
        this.channel = channel;
    }

    public void setOptions(Map<String, Object> options) {
        super.setOptions(options);
        if (getWriteBufferHighWaterMark() < getWriteBufferLowWaterMark()) {
            setWriteBufferLowWaterMark0(getWriteBufferHighWaterMark() >>> 1);
            if (logger.isWarnEnabled()) {
                logger.warn("writeBufferLowWaterMark cannot be greater than writeBufferHighWaterMark; setting to the half of the writeBufferHighWaterMark.");
            }
        }
    }

    public boolean setOption(String key, Object value) {
        if (super.setOption(key, value)) {
            return true;
        }
        if ("writeBufferHighWaterMark".equals(key)) {
            setWriteBufferHighWaterMark0(ConversionUtil.toInt(value));
            return true;
        } else if ("writeBufferLowWaterMark".equals(key)) {
            setWriteBufferLowWaterMark0(ConversionUtil.toInt(value));
            return true;
        } else if (!"writeSpinCount".equals(key)) {
            return false;
        } else {
            setWriteSpinCount(ConversionUtil.toInt(value));
            return true;
        }
    }

    public int getWriteBufferHighWaterMark() {
        return this.writeBufferHighWaterMark;
    }

    public void setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        if (writeBufferHighWaterMark < getWriteBufferLowWaterMark()) {
            throw new IllegalArgumentException("writeBufferHighWaterMark cannot be less than writeBufferLowWaterMark (" + getWriteBufferLowWaterMark() + "): " + writeBufferHighWaterMark);
        }
        setWriteBufferHighWaterMark0(writeBufferHighWaterMark);
    }

    private void setWriteBufferHighWaterMark0(int writeBufferHighWaterMark) {
        if (writeBufferHighWaterMark < 0) {
            throw new IllegalArgumentException("writeBufferHighWaterMark: " + writeBufferHighWaterMark);
        }
        this.writeBufferHighWaterMark = writeBufferHighWaterMark;
    }

    public int getWriteBufferLowWaterMark() {
        return this.writeBufferLowWaterMark;
    }

    public void setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        if (writeBufferLowWaterMark > getWriteBufferHighWaterMark()) {
            throw new IllegalArgumentException("writeBufferLowWaterMark cannot be greater than writeBufferHighWaterMark (" + getWriteBufferHighWaterMark() + "): " + writeBufferLowWaterMark);
        }
        setWriteBufferLowWaterMark0(writeBufferLowWaterMark);
    }

    private void setWriteBufferLowWaterMark0(int writeBufferLowWaterMark) {
        if (writeBufferLowWaterMark < 0) {
            throw new IllegalArgumentException("writeBufferLowWaterMark: " + writeBufferLowWaterMark);
        }
        this.writeBufferLowWaterMark = writeBufferLowWaterMark;
    }

    public int getWriteSpinCount() {
        return this.writeSpinCount;
    }

    public void setWriteSpinCount(int writeSpinCount) {
        if (writeSpinCount <= 0) {
            throw new IllegalArgumentException("writeSpinCount must be a positive integer.");
        }
        this.writeSpinCount = writeSpinCount;
    }

    public void setNetworkInterface(NetworkInterface networkInterface) {
        if (DetectionUtil.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        }
        try {
            this.channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public NetworkInterface getNetworkInterface() {
        if (DetectionUtil.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        }
        try {
            return (NetworkInterface) this.channel.getOption(StandardSocketOptions.IP_MULTICAST_IF);
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public int getTimeToLive() {
        if (DetectionUtil.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        }
        try {
            return ((Integer) this.channel.getOption(StandardSocketOptions.IP_MULTICAST_TTL)).intValue();
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public void setTimeToLive(int ttl) {
        if (DetectionUtil.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        }
        try {
            this.channel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, Integer.valueOf(ttl));
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public InetAddress getInterface() {
        NetworkInterface inf = getNetworkInterface();
        if (inf == null) {
            return null;
        }
        Enumeration<InetAddress> addresses = inf.getInetAddresses();
        if (addresses.hasMoreElements()) {
            return (InetAddress) addresses.nextElement();
        }
        return null;
    }

    public void setInterface(InetAddress interfaceAddress) {
        try {
            setNetworkInterface(NetworkInterface.getByInetAddress(interfaceAddress));
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public boolean isLoopbackModeDisabled() {
        if (DetectionUtil.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        }
        try {
            return ((Boolean) this.channel.getOption(StandardSocketOptions.IP_MULTICAST_LOOP)).booleanValue();
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }

    public void setLoopbackModeDisabled(boolean loopbackModeDisabled) {
        if (DetectionUtil.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        }
        try {
            this.channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, Boolean.valueOf(loopbackModeDisabled));
        } catch (Throwable e) {
            throw new ChannelException(e);
        }
    }
}
