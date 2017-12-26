package org.jboss.netty.handler.codec.http;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public interface HttpChunk {
    public static final HttpChunkTrailer LAST_CHUNK = new C12831();

    static class C12831 implements HttpChunkTrailer {
        C12831() {
        }

        public ChannelBuffer getContent() {
            return ChannelBuffers.EMPTY_BUFFER;
        }

        public void setContent(ChannelBuffer content) {
            throw new IllegalStateException("read-only");
        }

        public boolean isLast() {
            return true;
        }

        public HttpHeaders trailingHeaders() {
            return HttpHeaders.EMPTY_HEADERS;
        }
    }

    ChannelBuffer getContent();

    boolean isLast();

    void setContent(ChannelBuffer channelBuffer);
}
