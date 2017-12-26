package org.jboss.netty.handler.codec.http.cookie;

import java.util.Iterator;

public final class ClientCookieEncoder extends CookieEncoder {
    public static final ClientCookieEncoder LAX = new ClientCookieEncoder(false);
    public static final ClientCookieEncoder STRICT = new ClientCookieEncoder(true);

    private ClientCookieEncoder(boolean strict) {
        super(strict);
    }

    public String encode(String name, String value) {
        return encode(new DefaultCookie(name, value));
    }

    public String encode(Cookie cookie) {
        if (cookie == null) {
            throw new NullPointerException("cookie");
        }
        StringBuilder buf = new StringBuilder();
        encode(buf, cookie);
        return CookieUtil.stripTrailingSeparator(buf);
    }

    public String encode(Cookie... cookies) {
        if (cookies == null) {
            throw new NullPointerException("cookies");
        } else if (cookies.length == 0) {
            return null;
        } else {
            StringBuilder buf = new StringBuilder();
            for (Cookie c : cookies) {
                if (c == null) {
                    break;
                }
                encode(buf, c);
            }
            return CookieUtil.stripTrailingSeparatorOrNull(buf);
        }
    }

    public String encode(Iterable<? extends Cookie> cookies) {
        if (cookies == null) {
            throw new NullPointerException("cookies");
        }
        Iterator<? extends Cookie> cookiesIt = cookies.iterator();
        if (!cookiesIt.hasNext()) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        while (cookiesIt.hasNext()) {
            Cookie c = (Cookie) cookiesIt.next();
            if (c == null) {
                break;
            }
            encode(buf, c);
        }
        return CookieUtil.stripTrailingSeparatorOrNull(buf);
    }

    private void encode(StringBuilder buf, Cookie c) {
        String name = c.name();
        String value = c.value() != null ? c.value() : "";
        validateCookie(name, value);
        if (c.wrap()) {
            CookieUtil.addQuoted(buf, name, value);
        } else {
            CookieUtil.add(buf, name, value);
        }
    }
}
