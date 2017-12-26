package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.util.internal.StringUtil;

public class DefaultSpdyWindowUpdateFrame implements SpdyWindowUpdateFrame {
    private int deltaWindowSize;
    private int streamId;

    public DefaultSpdyWindowUpdateFrame(int streamId, int deltaWindowSize) {
        setStreamId(streamId);
        setDeltaWindowSize(deltaWindowSize);
    }

    public int getStreamId() {
        return this.streamId;
    }

    public void setStreamId(int streamId) {
        if (streamId < 0) {
            throw new IllegalArgumentException("Stream-ID cannot be negative: " + streamId);
        }
        this.streamId = streamId;
    }

    public int getDeltaWindowSize() {
        return this.deltaWindowSize;
    }

    public void setDeltaWindowSize(int deltaWindowSize) {
        if (deltaWindowSize <= 0) {
            throw new IllegalArgumentException("Delta-Window-Size must be positive: " + deltaWindowSize);
        }
        this.deltaWindowSize = deltaWindowSize;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getSimpleName());
        buf.append(StringUtil.NEWLINE);
        buf.append("--> Stream-ID = ");
        buf.append(getStreamId());
        buf.append(StringUtil.NEWLINE);
        buf.append("--> Delta-Window-Size = ");
        buf.append(getDeltaWindowSize());
        return buf.toString();
    }
}
