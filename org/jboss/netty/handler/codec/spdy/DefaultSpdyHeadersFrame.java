package org.jboss.netty.handler.codec.spdy;

import java.util.Iterator;
import java.util.Map.Entry;
import org.jboss.netty.util.internal.StringUtil;

public class DefaultSpdyHeadersFrame extends DefaultSpdyStreamFrame implements SpdyHeadersFrame {
    private final SpdyHeaders headers = new DefaultSpdyHeaders();
    private boolean invalid;
    private boolean truncated;

    public DefaultSpdyHeadersFrame(int streamId) {
        super(streamId);
    }

    public boolean isInvalid() {
        return this.invalid;
    }

    public void setInvalid() {
        this.invalid = true;
    }

    public boolean isTruncated() {
        return this.truncated;
    }

    public void setTruncated() {
        this.truncated = true;
    }

    public SpdyHeaders headers() {
        return this.headers;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getSimpleName());
        buf.append("(last: ");
        buf.append(isLast());
        buf.append(')');
        buf.append(StringUtil.NEWLINE);
        buf.append("--> Stream-ID = ");
        buf.append(getStreamId());
        buf.append(StringUtil.NEWLINE);
        buf.append("--> Headers:");
        buf.append(StringUtil.NEWLINE);
        appendHeaders(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }

    protected void appendHeaders(StringBuilder buf) {
        Iterator i$ = headers().iterator();
        while (i$.hasNext()) {
            Entry<String, String> e = (Entry) i$.next();
            buf.append("    ");
            buf.append((String) e.getKey());
            buf.append(": ");
            buf.append((String) e.getValue());
            buf.append(StringUtil.NEWLINE);
        }
    }
}
