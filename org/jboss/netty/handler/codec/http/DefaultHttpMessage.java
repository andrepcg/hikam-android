package org.jboss.netty.handler.codec.http;

import java.util.Iterator;
import java.util.Map.Entry;
import org.apache.http.cookie.ClientCookie;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.util.internal.StringUtil;

public class DefaultHttpMessage implements HttpMessage {
    private boolean chunked;
    private ChannelBuffer content = ChannelBuffers.EMPTY_BUFFER;
    private final HttpHeaders headers = new DefaultHttpHeaders(true);
    private HttpVersion version;

    protected DefaultHttpMessage(HttpVersion version) {
        setProtocolVersion(version);
    }

    public HttpHeaders headers() {
        return this.headers;
    }

    public boolean isChunked() {
        if (this.chunked) {
            return true;
        }
        return HttpCodecUtil.isTransferEncodingChunked(this);
    }

    public void setChunked(boolean chunked) {
        this.chunked = chunked;
        if (chunked) {
            setContent(ChannelBuffers.EMPTY_BUFFER);
        }
    }

    public void setContent(ChannelBuffer content) {
        if (content == null) {
            content = ChannelBuffers.EMPTY_BUFFER;
        }
        if (content.readable() && isChunked()) {
            throw new IllegalArgumentException("non-empty content disallowed if this.chunked == true");
        }
        this.content = content;
    }

    public HttpVersion getProtocolVersion() {
        return this.version;
    }

    public void setProtocolVersion(HttpVersion version) {
        if (version == null) {
            throw new NullPointerException(ClientCookie.VERSION_ATTR);
        }
        this.version = version;
    }

    public ChannelBuffer getContent() {
        return this.content;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getSimpleName());
        buf.append("(version: ");
        buf.append(getProtocolVersion().getText());
        buf.append(", keepAlive: ");
        buf.append(HttpHeaders.isKeepAlive(this));
        buf.append(", chunked: ");
        buf.append(isChunked());
        buf.append(')');
        buf.append(StringUtil.NEWLINE);
        appendHeaders(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }

    void appendHeaders(StringBuilder buf) {
        Iterator i$ = headers().iterator();
        while (i$.hasNext()) {
            Entry<String, String> e = (Entry) i$.next();
            buf.append((String) e.getKey());
            buf.append(": ");
            buf.append((String) e.getValue());
            buf.append(StringUtil.NEWLINE);
        }
    }
}
