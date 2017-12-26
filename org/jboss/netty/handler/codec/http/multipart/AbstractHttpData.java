package org.jboss.netty.handler.codec.http.multipart;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import org.jboss.netty.handler.codec.http.HttpConstants;

public abstract class AbstractHttpData implements HttpData {
    private static final Pattern REPLACE_PATTERN = Pattern.compile("[\\r\\t]");
    private static final Pattern STRIP_PATTERN = Pattern.compile("(?:^\\s+|\\s+$|\\n)");
    protected Charset charset = HttpConstants.DEFAULT_CHARSET;
    protected boolean completed;
    protected long definedSize;
    protected long maxSize = -1;
    protected final String name;
    protected long size;

    protected AbstractHttpData(String name, Charset charset, long size) {
        if (name == null) {
            throw new NullPointerException(HttpPostBodyUtil.NAME);
        }
        name = STRIP_PATTERN.matcher(REPLACE_PATTERN.matcher(name).replaceAll(" ")).replaceAll("");
        if (name.length() == 0) {
            throw new IllegalArgumentException("empty name");
        }
        this.name = name;
        if (charset != null) {
            setCharset(charset);
        }
        this.definedSize = size;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public void checkSize(long newSize) throws IOException {
        if (this.maxSize >= 0 && newSize > this.maxSize) {
            throw new IOException("Size exceed allowed maximum capacity");
        }
    }

    public String getName() {
        return this.name;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public void setCharset(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.charset = charset;
    }

    public long length() {
        return this.size;
    }
}
