package org.jboss.netty.handler.codec.http;

import org.jboss.netty.buffer.ChannelBuffer;

public class DefaultHttpChunk implements HttpChunk {
    private ChannelBuffer content;
    private boolean last;

    public DefaultHttpChunk(ChannelBuffer content) {
        setContent(content);
    }

    public ChannelBuffer getContent() {
        return this.content;
    }

    public void setContent(ChannelBuffer content) {
        if (content == null) {
            throw new NullPointerException("content");
        }
        this.last = !content.readable();
        this.content = content;
    }

    public boolean isLast() {
        return this.last;
    }
}
