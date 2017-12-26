package org.jboss.netty.handler.codec.http.cookie;

import com.google.firebase.analytics.FirebaseAnalytics.Param;
import org.apache.http.cookie.ClientCookie;

public class DefaultCookie implements Cookie {
    private String domain;
    private boolean httpOnly;
    private int maxAge = Integer.MIN_VALUE;
    private final String name;
    private String path;
    private boolean secure;
    private String value;
    private boolean wrap;

    public DefaultCookie(String name, String value) {
        if (name == null) {
            throw new NullPointerException(HttpPostBodyUtil.NAME);
        }
        name = name.trim();
        if (name.length() == 0) {
            throw new IllegalArgumentException("empty name");
        }
        int i = 0;
        while (i < name.length()) {
            char c = name.charAt(i);
            if (c > '') {
                throw new IllegalArgumentException("name contains non-ascii character: " + name);
            }
            switch (c) {
                case '\t':
                case '\n':
                case '\u000b':
                case '\f':
                case '\r':
                case ' ':
                case ',':
                case ';':
                case '=':
                    throw new IllegalArgumentException("name contains one of the following prohibited characters: =,; \\t\\r\\n\\v\\f: " + name);
                default:
                    i++;
            }
        }
        if (name.charAt(0) == '$') {
            throw new IllegalArgumentException("name starting with '$' not allowed: " + name);
        }
        this.name = name;
        setValue(value);
    }

    public String name() {
        return this.name;
    }

    public String value() {
        return this.value;
    }

    public void setValue(String value) {
        if (value == null) {
            throw new NullPointerException(Param.VALUE);
        }
        this.value = value;
    }

    public boolean wrap() {
        return this.wrap;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public String domain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = validateValue(ClientCookie.DOMAIN_ATTR, domain);
    }

    public String path() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = validateValue(ClientCookie.PATH_ATTR, path);
    }

    public int maxAge() {
        return this.maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public boolean isSecure() {
        return this.secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public boolean isHttpOnly() {
        return this.httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public int hashCode() {
        return name().hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cookie)) {
            return false;
        }
        Cookie that = (Cookie) o;
        if (!name().equalsIgnoreCase(that.name())) {
            return false;
        }
        if (path() == null) {
            if (that.path() != null) {
                return false;
            }
        } else if (that.path() == null) {
            return false;
        } else {
            if (!path().equals(that.path())) {
                return false;
            }
        }
        if (domain() == null) {
            if (that.domain() != null) {
                return false;
            }
            return true;
        } else if (that.domain() == null) {
            return false;
        } else {
            return domain().equalsIgnoreCase(that.domain());
        }
    }

    public int compareTo(Cookie c) {
        int v = name().compareToIgnoreCase(c.name());
        if (v != 0) {
            return v;
        }
        if (path() == null) {
            if (c.path() != null) {
                return -1;
            }
        } else if (c.path() == null) {
            return 1;
        } else {
            v = path().compareTo(c.path());
            if (v != 0) {
                return v;
            }
        }
        if (domain() == null) {
            if (c.domain() == null) {
                return 0;
            }
            return -1;
        } else if (c.domain() == null) {
            return 1;
        } else {
            return domain().compareToIgnoreCase(c.domain());
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder().append(name()).append('=').append(value());
        if (domain() != null) {
            buf.append(", domain=").append(domain());
        }
        if (path() != null) {
            buf.append(", path=").append(path());
        }
        if (maxAge() >= 0) {
            buf.append(", maxAge=").append(maxAge()).append('s');
        }
        if (isSecure()) {
            buf.append(", secure");
        }
        if (isHttpOnly()) {
            buf.append(", HTTPOnly");
        }
        return buf.toString();
    }

    protected String validateValue(String name, String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        if (value.length() == 0) {
            return null;
        }
        int i = 0;
        while (i < value.length()) {
            switch (value.charAt(i)) {
                case '\n':
                case '\u000b':
                case '\f':
                case '\r':
                case ';':
                    throw new IllegalArgumentException(name + " contains one of the following prohibited characters: " + ";\\r\\n\\f\\v (" + value + ')');
                default:
                    i++;
            }
        }
        return value;
    }
}
