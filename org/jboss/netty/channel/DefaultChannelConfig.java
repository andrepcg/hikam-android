package org.jboss.netty.channel;

import java.util.Map;
import java.util.Map.Entry;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.buffer.HeapChannelBufferFactory;
import org.jboss.netty.util.internal.ConversionUtil;

public class DefaultChannelConfig implements ChannelConfig {
    private volatile ChannelBufferFactory bufferFactory = HeapChannelBufferFactory.getInstance();
    private volatile int connectTimeoutMillis = 10000;

    public void setOptions(Map<String, Object> options) {
        for (Entry<String, Object> e : options.entrySet()) {
            setOption((String) e.getKey(), e.getValue());
        }
    }

    public boolean setOption(String key, Object value) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if ("pipelineFactory".equals(key)) {
            setPipelineFactory((ChannelPipelineFactory) value);
        } else if ("connectTimeoutMillis".equals(key)) {
            setConnectTimeoutMillis(ConversionUtil.toInt(value));
        } else if (!"bufferFactory".equals(key)) {
            return false;
        } else {
            setBufferFactory((ChannelBufferFactory) value);
        }
        return true;
    }

    public int getConnectTimeoutMillis() {
        return this.connectTimeoutMillis;
    }

    public ChannelBufferFactory getBufferFactory() {
        return this.bufferFactory;
    }

    public void setBufferFactory(ChannelBufferFactory bufferFactory) {
        if (bufferFactory == null) {
            throw new NullPointerException("bufferFactory");
        }
        this.bufferFactory = bufferFactory;
    }

    public ChannelPipelineFactory getPipelineFactory() {
        return null;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        if (connectTimeoutMillis < 0) {
            throw new IllegalArgumentException("connectTimeoutMillis: " + connectTimeoutMillis);
        }
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public void setPipelineFactory(ChannelPipelineFactory pipelineFactory) {
    }
}
