package org.jboss.netty.handler.codec.http.cookie;

import com.jwkj.global.Constants.Image;
import java.text.ParsePosition;
import java.util.Date;
import org.jboss.netty.handler.codec.http.HttpHeaderDateFormat;

public final class ClientCookieDecoder extends CookieDecoder {
    public static final ClientCookieDecoder LAX = new ClientCookieDecoder(false);
    public static final ClientCookieDecoder STRICT = new ClientCookieDecoder(true);

    private static class CookieBuilder {
        private final DefaultCookie cookie;
        private String domain;
        private String expires;
        private boolean httpOnly;
        private int maxAge = Integer.MIN_VALUE;
        private String path;
        private boolean secure;

        public CookieBuilder(DefaultCookie cookie) {
            this.cookie = cookie;
        }

        private int mergeMaxAgeAndExpire(int maxAge, String expires) {
            int i = 0;
            if (maxAge != Integer.MIN_VALUE) {
                return maxAge;
            }
            if (expires != null) {
                Date expiresDate = HttpHeaderDateFormat.get().parse(expires, new ParsePosition(0));
                if (expiresDate != null) {
                    long maxAgeMillis = expiresDate.getTime() - System.currentTimeMillis();
                    long j = maxAgeMillis / 1000;
                    if (maxAgeMillis % 1000 != 0) {
                        i = 1;
                    }
                    return (int) (j + ((long) i));
                }
            }
            return Integer.MIN_VALUE;
        }

        public Cookie cookie() {
            this.cookie.setDomain(this.domain);
            this.cookie.setPath(this.path);
            this.cookie.setMaxAge(mergeMaxAgeAndExpire(this.maxAge, this.expires));
            this.cookie.setSecure(this.secure);
            this.cookie.setHttpOnly(this.httpOnly);
            return this.cookie;
        }

        public void appendAttribute(String header, int keyStart, int keyEnd, String value) {
            setCookieAttribute(header, keyStart, keyEnd, value);
        }

        private void setCookieAttribute(String header, int keyStart, int keyEnd, String value) {
            int length = keyEnd - keyStart;
            if (length == 4) {
                parse4(header, keyStart, value);
            } else if (length == 6) {
                parse6(header, keyStart, value);
            } else if (length == 7) {
                parse7(header, keyStart, value);
            } else if (length == 8) {
                parse8(header, keyStart, value);
            }
        }

        private void parse4(String header, int nameStart, String value) {
            if (header.regionMatches(true, nameStart, CookieHeaderNames.PATH, 0, 4)) {
                this.path = value;
            }
        }

        private void parse6(String header, int nameStart, String value) {
            if (header.regionMatches(true, nameStart, CookieHeaderNames.DOMAIN, 0, 5)) {
                this.domain = value.length() > 0 ? value.toString() : null;
                return;
            }
            if (header.regionMatches(true, nameStart, CookieHeaderNames.SECURE, 0, 5)) {
                this.secure = true;
            }
        }

        private void setExpire(String value) {
            this.expires = value;
        }

        private void setMaxAge(String value) {
            try {
                this.maxAge = Math.max(Integer.valueOf(value).intValue(), 0);
            } catch (NumberFormatException e) {
            }
        }

        private void parse7(String header, int nameStart, String value) {
            if (header.regionMatches(true, nameStart, "Expires", 0, 7)) {
                setExpire(value);
                return;
            }
            if (header.regionMatches(true, nameStart, CookieHeaderNames.MAX_AGE, 0, 7)) {
                setMaxAge(value);
            }
        }

        private void parse8(String header, int nameStart, String value) {
            if (header.regionMatches(true, nameStart, CookieHeaderNames.HTTPONLY, 0, 8)) {
                this.httpOnly = true;
            }
        }
    }

    private ClientCookieDecoder(boolean strict) {
        super(strict);
    }

    public Cookie decode(String header) {
        if (header == null) {
            throw new NullPointerException(Image.USER_HEADER_FILE_NAME);
        }
        int headerLen = header.length();
        if (headerLen == 0) {
            return null;
        }
        CookieBuilder cookieBuilder = null;
        int i = 0;
        while (i != headerLen) {
            char c = header.charAt(i);
            if (c == ',') {
                break;
            } else if (c == '\t' || c == '\n' || c == '\u000b' || c == '\f' || c == '\r' || c == ' ' || c == ';') {
                i++;
            } else {
                int nameBegin = i;
                int nameEnd = i;
                int valueBegin = -1;
                int valueEnd = -1;
                if (i != headerLen) {
                    do {
                        char curChar = header.charAt(i);
                        if (curChar == ';') {
                            nameEnd = i;
                            valueEnd = -1;
                            valueBegin = -1;
                            break;
                        } else if (curChar == '=') {
                            nameEnd = i;
                            i++;
                            if (i == headerLen) {
                                valueEnd = 0;
                                valueBegin = 0;
                            } else {
                                valueBegin = i;
                                int semiPos = header.indexOf(59, i);
                                if (semiPos > 0) {
                                    i = semiPos;
                                } else {
                                    i = headerLen;
                                }
                                valueEnd = i;
                            }
                        } else {
                            i++;
                        }
                    } while (i != headerLen);
                    nameEnd = headerLen;
                    valueEnd = -1;
                    valueBegin = -1;
                }
                if (valueEnd > 0 && header.charAt(valueEnd - 1) == ',') {
                    valueEnd--;
                }
                if (cookieBuilder == null) {
                    DefaultCookie cookie = initCookie(header, nameBegin, nameEnd, valueBegin, valueEnd);
                    if (cookie == null) {
                        return null;
                    }
                    cookieBuilder = new CookieBuilder(cookie);
                } else {
                    cookieBuilder.appendAttribute(header, nameBegin, nameEnd, valueBegin == -1 ? null : header.substring(valueBegin, valueEnd));
                }
            }
        }
        return cookieBuilder.cookie();
    }
}
