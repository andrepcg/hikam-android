package org.jboss.netty.channel;

import java.util.Map;
import java.util.Map.Entry;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.buffer.HeapChannelBufferFactory;

public class DefaultServerChannelConfig implements ChannelConfig {
    private volatile ChannelBufferFactory bufferFactory = HeapChannelBufferFactory.getInstance();
    private volatile ChannelPipelineFactory pipelineFactory;

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
        } else if (!"bufferFactory".equals(key)) {
            return false;
        } else {
            setBufferFactory((ChannelBufferFactory) value);
        }
        return true;
    }

    public ChannelPipelineFactory getPipelineFactory() {
        return this.pipelineFactory;
    }

    public void setPipelineFactory(ChannelPipelineFactory pipelineFactory) {
        if (pipelineFactory == null) {
            throw new NullPointerException("pipelineFactory");
        }
        this.pipelineFactory = pipelineFactory;
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

    public int getConnectTimeoutMillis() {
        return 0;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
    }
}
