package org.jboss.netty.handler.codec.http;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryStringDecoder {
    private static final int DEFAULT_MAX_PARAMS = 1024;
    private final Charset charset;
    private final boolean hasPath;
    private final int maxParams;
    private int nParams;
    private Map<String, List<String>> params;
    private String path;
    private final String uri;

    public QueryStringDecoder(String uri) {
        this(uri, HttpConstants.DEFAULT_CHARSET);
    }

    public QueryStringDecoder(String uri, boolean hasPath) {
        this(uri, HttpConstants.DEFAULT_CHARSET, hasPath);
    }

    public QueryStringDecoder(String uri, Charset charset) {
        this(uri, charset, true);
    }

    public QueryStringDecoder(String uri, Charset charset, boolean hasPath) {
        this(uri, charset, hasPath, 1024);
    }

    public QueryStringDecoder(String uri, Charset charset, boolean hasPath, int maxParams) {
        if (uri == null) {
            throw new NullPointerException("uri");
        } else if (charset == null) {
            throw new NullPointerException("charset");
        } else if (maxParams <= 0) {
            throw new IllegalArgumentException("maxParams: " + maxParams + " (expected: a positive integer)");
        } else {
            this.uri = uri;
            this.charset = charset;
            this.maxParams = maxParams;
            this.hasPath = hasPath;
        }
    }

    public QueryStringDecoder(URI uri) {
        this(uri, HttpConstants.DEFAULT_CHARSET);
    }

    public QueryStringDecoder(URI uri, Charset charset) {
        this(uri, charset, 1024);
    }

    public QueryStringDecoder(URI uri, Charset charset, int maxParams) {
        if (uri == null) {
            throw new NullPointerException("uri");
        } else if (charset == null) {
            throw new NullPointerException("charset");
        } else if (maxParams <= 0) {
            throw new IllegalArgumentException("maxParams: " + maxParams + " (expected: a positive integer)");
        } else {
            String rawPath = uri.getRawPath();
            if (rawPath != null) {
                this.hasPath = true;
            } else {
                rawPath = "";
                this.hasPath = false;
            }
            this.uri = rawPath + '?' + uri.getRawQuery();
            this.charset = charset;
            this.maxParams = maxParams;
        }
    }

    public String getPath() {
        if (this.path == null) {
            String substring;
            if (this.hasPath) {
                int pathEndPos = this.uri.indexOf(63);
                if (pathEndPos < 0) {
                    this.path = this.uri;
                } else {
                    substring = this.uri.substring(0, pathEndPos);
                    this.path = substring;
                    return substring;
                }
            }
            substring = "";
            this.path = substring;
            return substring;
        }
        return this.path;
    }

    public Map<String, List<String>> getParameters() {
        if (this.params == null) {
            if (this.hasPath) {
                int pathLength = getPath().length();
                if (this.uri.length() == pathLength) {
                    return Collections.emptyMap();
                }
                decodeParams(this.uri.substring(pathLength + 1));
            } else if (this.uri.length() == 0) {
                return Collections.emptyMap();
            } else {
                decodeParams(this.uri);
            }
        }
        return this.params;
    }

    private void decodeParams(String s) {
        Map<String, List<String>> params = new LinkedHashMap();
        this.params = params;
        this.nParams = 0;
        String name = null;
        int pos = 0;
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '=' && name == null) {
                if (pos != i) {
                    name = decodeComponent(s.substring(pos, i), this.charset);
                }
                pos = i + 1;
            } else if (c == '&' || c == ';') {
                if (name != null || pos == i) {
                    if (name != null) {
                        if (addParam(params, name, decodeComponent(s.substring(pos, i), this.charset))) {
                            name = null;
                        } else {
                            return;
                        }
                    }
                } else if (!addParam(params, decodeComponent(s.substring(pos, i), this.charset), "")) {
                    return;
                }
                pos = i + 1;
            }
            i++;
        }
        if (pos != i) {
            if (name == null) {
                addParam(params, decodeComponent(s.substring(pos, i), this.charset), "");
            } else {
                addParam(params, name, decodeComponent(s.substring(pos, i), this.charset));
            }
        } else if (name != null) {
            addParam(params, name, "");
        }
    }

    private boolean addParam(Map<String, List<String>> params, String name, String value) {
        if (this.nParams >= this.maxParams) {
            return false;
        }
        List<String> values = (List) params.get(name);
        if (values == null) {
            values = new ArrayList(1);
            params.put(name, values);
        }
        values.add(value);
        this.nParams++;
        return true;
    }

    public static String decodeComponent(String s) {
        return decodeComponent(s, HttpConstants.DEFAULT_CHARSET);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String decodeComponent(java.lang.String r12, java.nio.charset.Charset r13) {
        /*
        r11 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        r10 = 37;
        if (r12 != 0) goto L_0x000a;
    L_0x0007:
        r12 = "";
    L_0x0009:
        return r12;
    L_0x000a:
        r8 = r12.length();
        r5 = 0;
        r4 = 0;
    L_0x0010:
        if (r4 >= r8) goto L_0x0020;
    L_0x0012:
        r1 = r12.charAt(r4);
        switch(r1) {
            case 37: goto L_0x001c;
            case 43: goto L_0x001e;
            default: goto L_0x0019;
        };
    L_0x0019:
        r4 = r4 + 1;
        goto L_0x0010;
    L_0x001c:
        r4 = r4 + 1;
    L_0x001e:
        r5 = 1;
        goto L_0x0019;
    L_0x0020:
        if (r5 == 0) goto L_0x0009;
    L_0x0022:
        r0 = new byte[r8];
        r6 = 0;
        r4 = 0;
        r7 = r6;
    L_0x0027:
        if (r4 >= r8) goto L_0x00dc;
    L_0x0029:
        r1 = r12.charAt(r4);
        switch(r1) {
            case 37: goto L_0x0040;
            case 43: goto L_0x0039;
            default: goto L_0x0030;
        };
    L_0x0030:
        r6 = r7 + 1;
        r9 = (byte) r1;
        r0[r7] = r9;
    L_0x0035:
        r4 = r4 + 1;
        r7 = r6;
        goto L_0x0027;
    L_0x0039:
        r6 = r7 + 1;
        r9 = 32;
        r0[r7] = r9;
        goto L_0x0035;
    L_0x0040:
        r9 = r8 + -1;
        if (r4 != r9) goto L_0x005d;
    L_0x0044:
        r9 = new java.lang.IllegalArgumentException;
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "unterminated escape sequence at end of string: ";
        r10 = r10.append(r11);
        r10 = r10.append(r12);
        r10 = r10.toString();
        r9.<init>(r10);
        throw r9;
    L_0x005d:
        r4 = r4 + 1;
        r1 = r12.charAt(r4);
        if (r1 != r10) goto L_0x006a;
    L_0x0065:
        r6 = r7 + 1;
        r0[r7] = r10;
        goto L_0x0035;
    L_0x006a:
        r9 = r8 + -1;
        if (r4 != r9) goto L_0x0087;
    L_0x006e:
        r9 = new java.lang.IllegalArgumentException;
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "partial escape sequence at end of string: ";
        r10 = r10.append(r11);
        r10 = r10.append(r12);
        r10 = r10.toString();
        r9.<init>(r10);
        throw r9;
    L_0x0087:
        r1 = decodeHexNibble(r1);
        r4 = r4 + 1;
        r9 = r12.charAt(r4);
        r2 = decodeHexNibble(r9);
        if (r1 == r11) goto L_0x0099;
    L_0x0097:
        if (r2 != r11) goto L_0x00d6;
    L_0x0099:
        r9 = new java.lang.IllegalArgumentException;
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "invalid escape sequence `%";
        r10 = r10.append(r11);
        r11 = r4 + -1;
        r11 = r12.charAt(r11);
        r10 = r10.append(r11);
        r11 = r12.charAt(r4);
        r10 = r10.append(r11);
        r11 = "' at index ";
        r10 = r10.append(r11);
        r11 = r4 + -2;
        r10 = r10.append(r11);
        r11 = " of: ";
        r10 = r10.append(r11);
        r10 = r10.append(r12);
        r10 = r10.toString();
        r9.<init>(r10);
        throw r9;
    L_0x00d6:
        r9 = r1 * 16;
        r9 = r9 + r2;
        r1 = (char) r9;
        goto L_0x0030;
    L_0x00dc:
        r12 = new java.lang.String;	 Catch:{ UnsupportedEncodingException -> 0x00e8 }
        r9 = 0;
        r10 = r13.name();	 Catch:{ UnsupportedEncodingException -> 0x00e8 }
        r12.<init>(r0, r9, r7, r10);	 Catch:{ UnsupportedEncodingException -> 0x00e8 }
        goto L_0x0009;
    L_0x00e8:
        r3 = move-exception;
        r9 = new java.lang.IllegalArgumentException;
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "unsupported encoding: ";
        r10 = r10.append(r11);
        r11 = r13.name();
        r10 = r10.append(r11);
        r10 = r10.toString();
        r9.<init>(r10, r3);
        throw r9;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.codec.http.QueryStringDecoder.decodeComponent(java.lang.String, java.nio.charset.Charset):java.lang.String");
    }

    private static char decodeHexNibble(char c) {
        if ('0' <= c && c <= '9') {
            return (char) (c - 48);
        }
        if ('a' <= c && c <= 'f') {
            return (char) ((c - 97) + 10);
        }
        if ('A' > c || c > 'F') {
            return '￿';
        }
        return (char) ((c - 65) + 10);
    }
}
