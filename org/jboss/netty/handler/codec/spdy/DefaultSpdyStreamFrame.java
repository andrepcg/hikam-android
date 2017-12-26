package org.jboss.netty.handler.codec.spdy;

public abstract class DefaultSpdyStreamFrame implements SpdyStreamFrame {
    private boolean last;
    private int streamId;

    protected DefaultSpdyStreamFrame(int streamId) {
        setStreamId(streamId);
    }

    public int getStreamId() {
        return this.streamId;
    }

    public void setStreamId(int streamId) {
        if (streamId <= 0) {
            throw new IllegalArgumentException("Stream-ID must be positive: " + streamId);
        }
        this.streamId = streamId;
    }

    public boolean isLast() {
        return this.last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
