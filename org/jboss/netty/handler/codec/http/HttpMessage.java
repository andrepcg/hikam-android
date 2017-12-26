package org.jboss.netty.handler.codec.http;

import org.jboss.netty.buffer.ChannelBuffer;

public interface HttpMessage {
    ChannelBuffer getContent();

    HttpVersion getProtocolVersion();

    HttpHeaders headers();

    boolean isChunked();

    void setChunked(boolean z);

    void setContent(ChannelBuffer channelBuffer);

    void setProtocolVersion(HttpVersion httpVersion);
}
