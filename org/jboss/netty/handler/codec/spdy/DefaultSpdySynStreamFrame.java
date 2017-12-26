package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.util.internal.StringUtil;

public class DefaultSpdySynStreamFrame extends DefaultSpdyHeadersFrame implements SpdySynStreamFrame {
    private int associatedToStreamId;
    private byte priority;
    private boolean unidirectional;

    public DefaultSpdySynStreamFrame(int streamId, int associatedToStreamId, byte priority) {
        super(streamId);
        setAssociatedToStreamId(associatedToStreamId);
        setPriority(priority);
    }

    public int getAssociatedToStreamId() {
        return this.associatedToStreamId;
    }

    public void setAssociatedToStreamId(int associatedToStreamId) {
        if (associatedToStreamId < 0) {
            throw new IllegalArgumentException("Associated-To-Stream-ID cannot be negative: " + associatedToStreamId);
        }
        this.associatedToStreamId = associatedToStreamId;
    }

    public byte getPriority() {
        return this.priority;
    }

    public void setPriority(byte priority) {
        if (priority < (byte) 0 || priority > (byte) 7) {
            throw new IllegalArgumentException("Priority must be between 0 and 7 inclusive: " + priority);
        }
        this.priority = priority;
    }

    public boolean isUnidirectional() {
        return this.unidirectional;
    }

    public void setUnidirectional(boolean unidirectional) {
        this.unidirectional = unidirectional;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getSimpleName());
        buf.append("(last: ");
        buf.append(isLast());
        buf.append("; unidirectional: ");
        buf.append(isUnidirectional());
        buf.append(')');
        buf.append(StringUtil.NEWLINE);
        buf.append("--> Stream-ID = ");
        buf.append(getStreamId());
        buf.append(StringUtil.NEWLINE);
        if (this.associatedToStreamId != 0) {
            buf.append("--> Associated-To-Stream-ID = ");
            buf.append(getAssociatedToStreamId());
            buf.append(StringUtil.NEWLINE);
        }
        buf.append("--> Priority = ");
        buf.append(getPriority());
        buf.append(StringUtil.NEWLINE);
        buf.append("--> Headers:");
        buf.append(StringUtil.NEWLINE);
        appendHeaders(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }
}
