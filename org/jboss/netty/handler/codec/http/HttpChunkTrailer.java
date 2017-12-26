package org.jboss.netty.handler.codec.http;

public interface HttpChunkTrailer extends HttpChunk {
    boolean isLast();

    HttpHeaders trailingHeaders();
}
