package org.jboss.netty.handler.codec.http;

import java.util.Iterator;
import java.util.Map.Entry;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.util.internal.StringUtil;

public class DefaultHttpChunkTrailer implements HttpChunkTrailer {
    private final HttpHeaders trailingHeaders = new TrailingHeaders(true);

    private static final class TrailingHeaders extends DefaultHttpHeaders {
        TrailingHeaders(boolean validateHeaders) {
            super(validateHeaders);
        }

        public HttpHeaders add(String name, Object value) {
            if (this.validate) {
                validateName(name);
            }
            return super.add(name, value);
        }

        public HttpHeaders add(String name, Iterable<?> values) {
            if (this.validate) {
                validateName(name);
            }
            return super.add(name, (Iterable) values);
        }

        public HttpHeaders set(String name, Iterable<?> values) {
            if (this.validate) {
                validateName(name);
            }
            return super.set(name, (Iterable) values);
        }

        public HttpHeaders set(String name, Object value) {
            if (this.validate) {
                validateName(name);
            }
            return super.set(name, value);
        }

        private static void validateName(String name) {
            if (name.equalsIgnoreCase("Content-Length") || name.equalsIgnoreCase("Transfer-Encoding") || name.equalsIgnoreCase(Names.TRAILER)) {
                throw new IllegalArgumentException("prohibited trailing header: " + name);
            }
        }
    }

    public boolean isLast() {
        return true;
    }

    public ChannelBuffer getContent() {
        return ChannelBuffers.EMPTY_BUFFER;
    }

    public void setContent(ChannelBuffer content) {
        throw new IllegalStateException("read-only");
    }

    public HttpHeaders trailingHeaders() {
        return this.trailingHeaders;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(super.toString());
        buf.append(StringUtil.NEWLINE);
        appendHeaders(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }

    private void appendHeaders(StringBuilder buf) {
        Iterator i$ = trailingHeaders().iterator();
        while (i$.hasNext()) {
            Entry<String, String> e = (Entry) i$.next();
            buf.append((String) e.getKey());
            buf.append(": ");
            buf.append((String) e.getValue());
            buf.append(StringUtil.NEWLINE);
        }
    }
}
