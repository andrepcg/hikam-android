package org.jboss.netty.handler.codec.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpVersion implements Comparable<HttpVersion> {
    public static final HttpVersion HTTP_1_0 = new HttpVersion(org.apache.http.HttpVersion.HTTP, 1, 0, false);
    public static final HttpVersion HTTP_1_1 = new HttpVersion(org.apache.http.HttpVersion.HTTP, 1, 1, true);
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\S+)/(\\d+)\\.(\\d+)");
    private final boolean keepAliveDefault;
    private final int majorVersion;
    private final int minorVersion;
    private final String protocolName;
    private final String text;

    public static HttpVersion valueOf(String text) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        text = text.trim().toUpperCase();
        if ("HTTP/1.1".equals(text)) {
            return HTTP_1_1;
        }
        if ("HTTP/1.0".equals(text)) {
            return HTTP_1_0;
        }
        return new HttpVersion(text, true);
    }

    public HttpVersion(String text, boolean keepAliveDefault) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        text = text.trim().toUpperCase();
        if (text.length() == 0) {
            throw new IllegalArgumentException("empty text");
        }
        Matcher m = VERSION_PATTERN.matcher(text);
        if (m.matches()) {
            this.protocolName = m.group(1);
            this.majorVersion = Integer.parseInt(m.group(2));
            this.minorVersion = Integer.parseInt(m.group(3));
            this.text = this.protocolName + '/' + this.majorVersion + '.' + this.minorVersion;
            this.keepAliveDefault = keepAliveDefault;
            return;
        }
        throw new IllegalArgumentException("invalid version format: " + text);
    }

    public HttpVersion(String protocolName, int majorVersion, int minorVersion, boolean keepAliveDefault) {
        if (protocolName == null) {
            throw new NullPointerException("protocolName");
        }
        protocolName = protocolName.trim().toUpperCase();
        if (protocolName.length() == 0) {
            throw new IllegalArgumentException("empty protocolName");
        }
        int i = 0;
        while (i < protocolName.length()) {
            if (Character.isISOControl(protocolName.charAt(i)) || Character.isWhitespace(protocolName.charAt(i))) {
                throw new IllegalArgumentException("invalid character in protocolName");
            }
            i++;
        }
        if (majorVersion < 0) {
            throw new IllegalArgumentException("negative majorVersion");
        } else if (minorVersion < 0) {
            throw new IllegalArgumentException("negative minorVersion");
        } else {
            this.protocolName = protocolName;
            this.majorVersion = majorVersion;
            this.minorVersion = minorVersion;
            this.text = protocolName + '/' + majorVersion + '.' + minorVersion;
            this.keepAliveDefault = keepAliveDefault;
        }
    }

    public String getProtocolName() {
        return this.protocolName;
    }

    public int getMajorVersion() {
        return this.majorVersion;
    }

    public int getMinorVersion() {
        return this.minorVersion;
    }

    public String getText() {
        return this.text;
    }

    public boolean isKeepAliveDefault() {
        return this.keepAliveDefault;
    }

    public String toString() {
        return getText();
    }

    public int hashCode() {
        return (((getProtocolName().hashCode() * 31) + getMajorVersion()) * 31) + getMinorVersion();
    }

    public boolean equals(Object o) {
        if (!(o instanceof HttpVersion)) {
            return false;
        }
        HttpVersion that = (HttpVersion) o;
        if (getMinorVersion() == that.getMinorVersion() && getMajorVersion() == that.getMajorVersion() && getProtocolName().equals(that.getProtocolName())) {
            return true;
        }
        return false;
    }

    public int compareTo(HttpVersion o) {
        int v = getProtocolName().compareTo(o.getProtocolName());
        if (v != 0) {
            return v;
        }
        v = getMajorVersion() - o.getMajorVersion();
        if (v != 0) {
            return v;
        }
        return getMinorVersion() - o.getMinorVersion();
    }
}
