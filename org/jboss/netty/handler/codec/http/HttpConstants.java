package org.jboss.netty.handler.codec.http;

import java.nio.charset.Charset;
import org.jboss.netty.util.CharsetUtil;

public final class HttpConstants {
    public static final byte COLON = (byte) 58;
    public static final byte COMMA = (byte) 44;
    public static final byte CR = (byte) 13;
    public static final Charset DEFAULT_CHARSET = CharsetUtil.UTF_8;
    public static final byte DOUBLE_QUOTE = (byte) 34;
    public static final byte EQUALS = (byte) 61;
    public static final byte HT = (byte) 9;
    public static final byte LF = (byte) 10;
    public static final byte SEMICOLON = (byte) 59;
    public static final byte SP = (byte) 32;

    private HttpConstants() {
    }
}
