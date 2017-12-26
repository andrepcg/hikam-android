package org.jboss.netty.handler.codec.http;

import java.util.Set;
import java.util.TreeSet;
import org.jboss.netty.handler.codec.http.cookie.ClientCookieEncoder;
import org.jboss.netty.handler.codec.http.cookie.Cookie;
import org.jboss.netty.handler.codec.http.cookie.ServerCookieEncoder;

public class CookieEncoder {
    private final Set<Cookie> cookies;
    private final boolean server;
    private final boolean strict;

    public CookieEncoder(boolean server) {
        this(server, false);
    }

    public CookieEncoder(boolean server, boolean strict) {
        this.cookies = new TreeSet();
        this.server = server;
        this.strict = strict;
    }

    public void addCookie(String name, String value) {
        this.cookies.add(new DefaultCookie(name, value));
    }

    public void addCookie(Cookie cookie) {
        this.cookies.add(cookie);
    }

    public String encode() {
        String answer;
        if (this.server) {
            answer = encodeServerSide();
        } else {
            answer = encodeClientSide();
        }
        this.cookies.clear();
        return answer;
    }

    private String encodeServerSide() {
        if (this.cookies.size() > 1) {
            throw new IllegalStateException("encode() can encode only one cookie on server mode: " + this.cookies.size() + " cookies added");
        }
        Cookie cookie;
        if (this.cookies.isEmpty()) {
            cookie = null;
        } else {
            Object cookie2 = (Cookie) this.cookies.iterator().next();
        }
        return (this.strict ? ServerCookieEncoder.STRICT : ServerCookieEncoder.LAX).encode(cookie);
    }

    private String encodeClientSide() {
        return (this.strict ? ClientCookieEncoder.STRICT : ClientCookieEncoder.LAX).encode(this.cookies);
    }
}
