package org.jboss.netty.handler.codec.spdy;

public interface SpdyHeadersFrame extends SpdyStreamFrame {
    SpdyHeaders headers();

    boolean isInvalid();

    boolean isTruncated();

    void setInvalid();

    void setTruncated();
}
