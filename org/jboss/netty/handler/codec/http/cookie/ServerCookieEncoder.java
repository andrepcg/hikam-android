package org.jboss.netty.handler.codec.http.cookie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.jboss.netty.handler.codec.http.HttpHeaderDateFormat;

public final class ServerCookieEncoder extends CookieEncoder {
    public static final ServerCookieEncoder LAX = new ServerCookieEncoder(false);
    public static final ServerCookieEncoder STRICT = new ServerCookieEncoder(true);

    private ServerCookieEncoder(boolean strict) {
        super(strict);
    }

    public String encode(String name, String value) {
        return encode(new DefaultCookie(name, value));
    }

    public String encode(Cookie cookie) {
        if (cookie == null) {
            throw new NullPointerException("cookie");
        }
        String name = cookie.name();
        String value = cookie.value() != null ? cookie.value() : "";
        validateCookie(name, value);
        StringBuilder buf = new StringBuilder();
        if (cookie.wrap()) {
            CookieUtil.addQuoted(buf, name, value);
        } else {
            CookieUtil.add(buf, name, value);
        }
        if (cookie.maxAge() != Integer.MIN_VALUE) {
            CookieUtil.add(buf, CookieHeaderNames.MAX_AGE, (long) cookie.maxAge());
            CookieUtil.add(buf, "Expires", HttpHeaderDateFormat.get().format(new Date((((long) cookie.maxAge()) * 1000) + System.currentTimeMillis())));
        }
        if (cookie.path() != null) {
            CookieUtil.add(buf, CookieHeaderNames.PATH, cookie.path());
        }
        if (cookie.domain() != null) {
            CookieUtil.add(buf, CookieHeaderNames.DOMAIN, cookie.domain());
        }
        if (cookie.isSecure()) {
            CookieUtil.add(buf, CookieHeaderNames.SECURE);
        }
        if (cookie.isHttpOnly()) {
            CookieUtil.add(buf, CookieHeaderNames.HTTPONLY);
        }
        return CookieUtil.stripTrailingSeparator(buf);
    }

    public List<String> encode(Cookie... cookies) {
        if (cookies == null) {
            throw new NullPointerException("cookies");
        } else if (cookies.length == 0) {
            return Collections.emptyList();
        } else {
            List<String> encoded = new ArrayList(cookies.length);
            for (Cookie c : cookies) {
                if (c == null) {
                    return encoded;
                }
                encoded.add(encode(c));
            }
            return encoded;
        }
    }

    public List<String> encode(Collection<? extends Cookie> cookies) {
        if (cookies == null) {
            throw new NullPointerException("cookies");
        } else if (cookies.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<String> encoded = new ArrayList(cookies.size());
            for (Cookie c : cookies) {
                if (c == null) {
                    return encoded;
                }
                encoded.add(encode(c));
            }
            return encoded;
        }
    }

    public List<String> encode(Iterable<? extends Cookie> cookies) {
        if (cookies == null) {
            throw new NullPointerException("cookies");
        } else if (cookies.iterator().hasNext()) {
            return Collections.emptyList();
        } else {
            List<String> encoded = new ArrayList();
            for (Cookie c : cookies) {
                if (c == null) {
                    return encoded;
                }
                encoded.add(encode(c));
            }
            return encoded;
        }
    }
}
