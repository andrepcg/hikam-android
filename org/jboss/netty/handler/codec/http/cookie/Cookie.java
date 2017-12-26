package org.jboss.netty.handler.codec.http.cookie;

public interface Cookie extends Comparable<Cookie> {
    String domain();

    boolean isHttpOnly();

    boolean isSecure();

    int maxAge();

    String name();

    String path();

    void setDomain(String str);

    void setHttpOnly(boolean z);

    void setMaxAge(int i);

    void setPath(String str);

    void setSecure(boolean z);

    void setValue(String str);

    void setWrap(boolean z);

    String value();

    boolean wrap();
}
