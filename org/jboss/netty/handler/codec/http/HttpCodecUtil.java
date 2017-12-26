package org.jboss.netty.handler.codec.http;

import com.google.firebase.analytics.FirebaseAnalytics.Param;
import java.util.Iterator;
import java.util.List;

final class HttpCodecUtil {
    static void validateHeaderName(String name) {
        if (name == null) {
            throw new NullPointerException(HttpPostBodyUtil.NAME);
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
                case ':':
                case ';':
                case '=':
                    throw new IllegalArgumentException("name contains one of the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " + name);
                default:
                    i++;
            }
        }
    }

    static void validateHeaderValue(String value) {
        if (value == null) {
            throw new NullPointerException(Param.VALUE);
        }
        int state = 0;
        int i = 0;
        while (i < value.length()) {
            char c = value.charAt(i);
            switch (c) {
                case '\u000b':
                    throw new IllegalArgumentException("value contains a prohibited character '\\v': " + value);
                case '\f':
                    throw new IllegalArgumentException("value contains a prohibited character '\\f': " + value);
                default:
                    switch (state) {
                        case 0:
                            switch (c) {
                                case '\n':
                                    state = 2;
                                    break;
                                case '\r':
                                    state = 1;
                                    break;
                                default:
                                    break;
                            }
                        case 1:
                            switch (c) {
                                case '\n':
                                    state = 2;
                                    break;
                                default:
                                    throw new IllegalArgumentException("Only '\\n' is allowed after '\\r': " + value);
                            }
                        case 2:
                            switch (c) {
                                case '\t':
                                case ' ':
                                    state = 0;
                                    break;
                                default:
                                    throw new IllegalArgumentException("Only ' ' and '\\t' are allowed after '\\n': " + value);
                            }
                        default:
                            break;
                    }
                    i++;
            }
        }
        if (state != 0) {
            throw new IllegalArgumentException("value must not end with '\\r' or '\\n':" + value);
        }
    }

    static boolean isTransferEncodingChunked(HttpMessage m) {
        List<String> chunked = m.headers().getAll("Transfer-Encoding");
        if (chunked.isEmpty()) {
            return false;
        }
        for (String v : chunked) {
            if (v.equalsIgnoreCase("chunked")) {
                return true;
            }
        }
        return false;
    }

    static void removeTransferEncodingChunked(HttpMessage m) {
        Iterable values = m.headers().getAll("Transfer-Encoding");
        if (!values.isEmpty()) {
            Iterator<String> valuesIt = values.iterator();
            while (valuesIt.hasNext()) {
                if (((String) valuesIt.next()).equalsIgnoreCase("chunked")) {
                    valuesIt.remove();
                }
            }
            if (values.isEmpty()) {
                m.headers().remove("Transfer-Encoding");
            } else {
                m.headers().set("Transfer-Encoding", values);
            }
        }
    }

    static boolean isContentLengthSet(HttpMessage m) {
        return !m.headers().getAll("Content-Length").isEmpty();
    }

    private HttpCodecUtil() {
    }
}
