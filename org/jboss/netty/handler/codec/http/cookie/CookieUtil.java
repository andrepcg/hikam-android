package org.jboss.netty.handler.codec.http.cookie;

import android.support.v4.media.TransportMediator;
import java.util.BitSet;

final class CookieUtil {
    private static final BitSet VALID_COOKIE_NAME_OCTETS = validCookieNameOctets(VALID_COOKIE_VALUE_OCTETS);
    private static final BitSet VALID_COOKIE_VALUE_OCTETS = validCookieValueOctets();

    private static BitSet validCookieValueOctets() {
        BitSet bits = new BitSet(8);
        for (int i = 35; i < TransportMediator.KEYCODE_MEDIA_PAUSE; i++) {
            bits.set(i);
        }
        bits.set(34, false);
        bits.set(44, false);
        bits.set(59, false);
        bits.set(92, false);
        return bits;
    }

    private static BitSet validCookieNameOctets(BitSet validCookieValueOctets) {
        BitSet bits = new BitSet(8);
        bits.or(validCookieValueOctets);
        bits.set(40, false);
        bits.set(41, false);
        bits.set(60, false);
        bits.set(62, false);
        bits.set(64, false);
        bits.set(58, false);
        bits.set(47, false);
        bits.set(91, false);
        bits.set(93, false);
        bits.set(63, false);
        bits.set(61, false);
        bits.set(123, false);
        bits.set(125, false);
        bits.set(32, false);
        bits.set(9, false);
        return bits;
    }

    static String stripTrailingSeparatorOrNull(StringBuilder buf) {
        return buf.length() == 0 ? null : stripTrailingSeparator(buf);
    }

    static String stripTrailingSeparator(StringBuilder buf) {
        if (buf.length() > 0) {
            buf.setLength(buf.length() - 2);
        }
        return buf.toString();
    }

    static void add(StringBuilder sb, String name, long val) {
        sb.append(name);
        sb.append('=');
        sb.append(val);
        sb.append(';');
        sb.append(' ');
    }

    static void add(StringBuilder sb, String name, String val) {
        sb.append(name);
        sb.append('=');
        sb.append(val);
        sb.append(';');
        sb.append(' ');
    }

    static void add(StringBuilder sb, String name) {
        sb.append(name);
        sb.append(';');
        sb.append(' ');
    }

    static void addQuoted(StringBuilder sb, String name, String val) {
        if (val == null) {
            val = "";
        }
        sb.append(name);
        sb.append('=');
        sb.append('\"');
        sb.append(val);
        sb.append('\"');
        sb.append(';');
        sb.append(' ');
    }

    static int firstInvalidCookieNameOctet(CharSequence cs) {
        return firstInvalidOctet(cs, VALID_COOKIE_NAME_OCTETS);
    }

    static int firstInvalidCookieValueOctet(CharSequence cs) {
        return firstInvalidOctet(cs, VALID_COOKIE_VALUE_OCTETS);
    }

    static int firstInvalidOctet(CharSequence cs, BitSet bits) {
        for (int i = 0; i < cs.length(); i++) {
            if (!bits.get(cs.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    static CharSequence unwrapValue(CharSequence cs) {
        int len = cs.length();
        if (len <= 0 || cs.charAt(0) != '\"') {
            return cs;
        }
        if (len < 2 || cs.charAt(len - 1) != '\"') {
            return null;
        }
        return len == 2 ? "" : cs.subSequence(1, len - 1);
    }

    private CookieUtil() {
    }
}
