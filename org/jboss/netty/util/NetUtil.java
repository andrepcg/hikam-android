package org.jboss.netty.util;

import android.support.v4.internal.view.SupportMenu;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public final class NetUtil {
    private static final int IPV4_BYTE_COUNT = 4;
    private static final int IPV4_MAX_CHAR_BETWEEN_SEPARATOR = 3;
    private static final int IPV4_SEPARATORS = 3;
    private static final int IPV6_BYTE_COUNT = 16;
    private static final int IPV6_MAX_CHAR_BETWEEN_SEPARATOR = 4;
    private static final int IPV6_MAX_CHAR_COUNT = 39;
    private static final int IPV6_MAX_SEPARATORS = 8;
    private static final int IPV6_MIN_SEPARATORS = 2;
    private static final int IPV6_WORD_COUNT = 8;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NetUtil.class);

    public static byte[] createByteArrayFromIpAddressString(String ipAddressString) {
        StringTokenizer tokenizer;
        int i;
        if (isValidIpV4Address(ipAddressString)) {
            tokenizer = new StringTokenizer(ipAddressString, ".");
            byte[] bArr = new byte[4];
            for (i = 0; i < 4; i++) {
                bArr[i] = (byte) Integer.parseInt(tokenizer.nextToken());
            }
            return bArr;
        } else if (!isValidIpV6Address(ipAddressString)) {
            return null;
        } else {
            if (ipAddressString.charAt(0) == '[') {
                ipAddressString = ipAddressString.substring(1, ipAddressString.length() - 1);
            }
            int percentPos = ipAddressString.indexOf(37);
            if (percentPos >= 0) {
                ipAddressString = ipAddressString.substring(0, percentPos);
            }
            tokenizer = new StringTokenizer(ipAddressString, ":.", true);
            ArrayList<String> hexStrings = new ArrayList();
            ArrayList<String> decStrings = new ArrayList();
            String token = "";
            String prevToken = "";
            int doubleColonIndex = -1;
            while (tokenizer.hasMoreTokens()) {
                prevToken = token;
                token = tokenizer.nextToken();
                if (":".equals(token)) {
                    if (":".equals(prevToken)) {
                        doubleColonIndex = hexStrings.size();
                    } else if (prevToken.length() != 0) {
                        hexStrings.add(prevToken);
                    }
                } else if (".".equals(token)) {
                    decStrings.add(prevToken);
                }
            }
            if (":".equals(prevToken)) {
                if (":".equals(token)) {
                    doubleColonIndex = hexStrings.size();
                } else {
                    hexStrings.add(token);
                }
            } else if (".".equals(prevToken)) {
                decStrings.add(token);
            }
            int hexStringsLength = 8;
            if (!decStrings.isEmpty()) {
                hexStringsLength = 8 - 2;
            }
            if (doubleColonIndex != -1) {
                int numberToInsert = hexStringsLength - hexStrings.size();
                for (i = 0; i < numberToInsert; i++) {
                    hexStrings.add(doubleColonIndex, "0");
                }
            }
            byte[] ipByteArray = new byte[16];
            for (i = 0; i < hexStrings.size(); i++) {
                convertToBytes((String) hexStrings.get(i), ipByteArray, i << 1);
            }
            for (i = 0; i < decStrings.size(); i++) {
                ipByteArray[i + 12] = (byte) (Integer.parseInt((String) decStrings.get(i)) & 255);
            }
            return ipByteArray;
        }
    }

    private static void convertToBytes(String hexWord, byte[] ipByteArray, int byteIndex) {
        int hexWordIndex;
        int hexWordIndex2;
        int i;
        int hexWordLength = hexWord.length();
        ipByteArray[byteIndex] = (byte) 0;
        ipByteArray[byteIndex + 1] = (byte) 0;
        if (hexWordLength > 3) {
            hexWordIndex = 0 + 1;
            ipByteArray[byteIndex] = (byte) (ipByteArray[byteIndex] | (getIntValue(hexWord.charAt(0)) << 4));
        } else {
            hexWordIndex = 0;
        }
        if (hexWordLength > 2) {
            hexWordIndex2 = hexWordIndex + 1;
            ipByteArray[byteIndex] = (byte) (ipByteArray[byteIndex] | getIntValue(hexWord.charAt(hexWordIndex)));
            hexWordIndex = hexWordIndex2;
        }
        if (hexWordLength > 1) {
            hexWordIndex2 = hexWordIndex + 1;
            i = byteIndex + 1;
            ipByteArray[i] = (byte) (ipByteArray[i] | (getIntValue(hexWord.charAt(hexWordIndex)) << 4));
        } else {
            hexWordIndex2 = hexWordIndex;
        }
        i = byteIndex + 1;
        ipByteArray[i] = (byte) (ipByteArray[i] | (getIntValue(hexWord.charAt(hexWordIndex2)) & 15));
    }

    static int getIntValue(char c) {
        switch (c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            default:
                switch (Character.toLowerCase(c)) {
                    case 'a':
                        return 10;
                    case 'b':
                        return 11;
                    case 'c':
                        return 12;
                    case 'd':
                        return 13;
                    case 'e':
                        return 14;
                    case 'f':
                        return 15;
                    default:
                        return 0;
                }
        }
    }

    public static boolean isValidIpV6Address(String ipAddress) {
        int length = ipAddress.length();
        boolean doubleColon = false;
        int numberOfColons = 0;
        int numberOfPeriods = 0;
        StringBuilder word = new StringBuilder();
        char c = '\u0000';
        int startOffset = 0;
        int endOffset = ipAddress.length();
        if (endOffset < 2) {
            return false;
        }
        if (ipAddress.charAt(0) == '[') {
            if (ipAddress.charAt(endOffset - 1) != ']') {
                return false;
            }
            startOffset = 1;
            endOffset--;
        }
        int percentIdx = ipAddress.indexOf(37, startOffset);
        if (percentIdx >= 0) {
            endOffset = percentIdx;
        }
        int i = startOffset;
        while (i < endOffset) {
            char prevChar = c;
            c = ipAddress.charAt(i);
            switch (c) {
                case '.':
                    numberOfPeriods++;
                    if (numberOfPeriods > 3) {
                        return false;
                    }
                    if (!isValidIp4Word(word.toString())) {
                        return false;
                    }
                    if (numberOfColons != 6 && !doubleColon) {
                        return false;
                    }
                    if (numberOfColons != 7 || ipAddress.charAt(startOffset) == ':' || ipAddress.charAt(startOffset + 1) == ':') {
                        word.delete(0, word.length());
                        break;
                    }
                    return false;
                    break;
                case ':':
                    if (i != startOffset || (ipAddress.length() > i && ipAddress.charAt(i + 1) == ':')) {
                        numberOfColons++;
                        if (numberOfColons <= 7) {
                            if (numberOfPeriods <= 0) {
                                if (prevChar == ':') {
                                    if (doubleColon) {
                                        return false;
                                    }
                                    doubleColon = true;
                                }
                                word.delete(0, word.length());
                                break;
                            }
                            return false;
                        }
                        return false;
                    }
                    return false;
                default:
                    if (word == null || word.length() <= 3) {
                        if (isValidHexChar(c)) {
                            word.append(c);
                            break;
                        }
                        return false;
                    }
                    return false;
                    break;
            }
            i++;
        }
        if (numberOfPeriods > 0) {
            if (!(numberOfPeriods == 3 && isValidIp4Word(word.toString()) && numberOfColons < 7)) {
                return false;
            }
        } else if (numberOfColons != 7 && !doubleColon) {
            return false;
        } else {
            if (word.length() == 0 && ipAddress.charAt((length - 1) - startOffset) == ':' && ipAddress.charAt((length - 2) - startOffset) != ':') {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidIp4Word(String word) {
        boolean z = true;
        if (word.length() < 1 || word.length() > 3) {
            return false;
        }
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        if (Integer.parseInt(word) > 255) {
            z = false;
        }
        return z;
    }

    private static boolean isValidHexChar(char c) {
        return (c >= '0' && c <= '9') || ((c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f'));
    }

    private static boolean isValidNumericChar(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isValidIpV4Address(String value) {
        int periods = 0;
        int length = value.length();
        if (length > 15) {
            return false;
        }
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c == '.') {
                periods++;
                if (periods > 3 || word.length() == 0 || Integer.parseInt(word.toString()) > 255) {
                    return false;
                }
                word.delete(0, word.length());
            } else if (!Character.isDigit(c) || word.length() > 2) {
                return false;
            } else {
                word.append(c);
            }
        }
        if (word.length() == 0 || Integer.parseInt(word.toString()) > 255 || periods != 3) {
            return false;
        }
        return true;
    }

    public static Inet6Address getByName(CharSequence ip) {
        return getByName(ip, true);
    }

    public static Inet6Address getByName(CharSequence ip, boolean ipv4Mapped) {
        int currentIndex;
        byte[] bytes = new byte[16];
        int ipLength = ip.length();
        int compressBegin = 0;
        int compressLength = 0;
        int value = 0;
        int begin = -1;
        int i = 0;
        int ipv6Seperators = 0;
        int ipv4Seperators = 0;
        boolean needsShift = false;
        int currentIndex2 = 0;
        while (i < ipLength) {
            int tmp;
            char c = ip.charAt(i);
            switch (c) {
                case '.':
                    ipv4Seperators++;
                    if (i - begin <= 3 && ipv4Seperators <= 3 && ((ipv6Seperators <= 0 || currentIndex2 + compressLength >= 12) && i + 1 < ipLength && currentIndex2 < bytes.length && begin >= 0 && (begin != 0 || ((i != 3 || (isValidNumericChar(ip.charAt(2)) && isValidNumericChar(ip.charAt(1)) && isValidNumericChar(ip.charAt(0)))) && ((i != 2 || (isValidNumericChar(ip.charAt(1)) && isValidNumericChar(ip.charAt(0)))) && (i != 1 || isValidNumericChar(ip.charAt(0)))))))) {
                        value <<= (3 - (i - begin)) << 2;
                        begin = (((value & 15) * 100) + (((value >> 4) & 15) * 10)) + ((value >> 8) & 15);
                        if (begin >= 0 && begin <= 255) {
                            currentIndex = currentIndex2 + 1;
                            bytes[currentIndex2] = (byte) begin;
                            value = 0;
                            begin = -1;
                            break;
                        }
                        currentIndex = currentIndex2;
                        return null;
                    }
                    currentIndex = currentIndex2;
                    return null;
                case ':':
                    ipv6Seperators++;
                    if (i - begin <= 4 && ipv4Seperators <= 0 && ipv6Seperators <= 8 && currentIndex2 + 1 < bytes.length) {
                        value <<= (4 - (i - begin)) << 2;
                        if (compressLength > 0) {
                            compressLength -= 2;
                        }
                        currentIndex = currentIndex2 + 1;
                        bytes[currentIndex2] = (byte) (((value & 15) << 4) | ((value >> 4) & 15));
                        currentIndex2 = currentIndex + 1;
                        bytes[currentIndex] = (byte) ((((value >> 8) & 15) << 4) | ((value >> 12) & 15));
                        tmp = i + 1;
                        if (tmp < ipLength && ip.charAt(tmp) == ':') {
                            tmp++;
                            if (compressBegin != 0 || (tmp < ipLength && ip.charAt(tmp) == ':')) {
                                currentIndex = currentIndex2;
                                return null;
                            }
                            ipv6Seperators++;
                            needsShift = ipv6Seperators == 2 && value == 0;
                            compressBegin = currentIndex2;
                            compressLength = (bytes.length - compressBegin) - 2;
                            i++;
                        }
                        value = 0;
                        begin = -1;
                        currentIndex = currentIndex2;
                        break;
                    }
                    currentIndex = currentIndex2;
                    return null;
                    break;
                default:
                    if (isValidHexChar(c) && (ipv4Seperators <= 0 || isValidNumericChar(c))) {
                        if (begin < 0) {
                            begin = i;
                        } else if (i - begin > 4) {
                            currentIndex = currentIndex2;
                            return null;
                        }
                        value += getIntValue(c) << ((i - begin) << 2);
                        currentIndex = currentIndex2;
                        break;
                    }
                    currentIndex = currentIndex2;
                    return null;
                    break;
            }
            i++;
            currentIndex2 = currentIndex;
        }
        boolean isCompressed = compressBegin > 0;
        if (ipv4Seperators <= 0) {
            tmp = ipLength - 1;
            if ((begin <= 0 || i - begin <= 4) && ipv6Seperators >= 2 && ((isCompressed || !(ipv6Seperators + 1 != 8 || ip.charAt(0) == ':' || ip.charAt(tmp) == ':')) && ((!isCompressed || (ipv6Seperators <= 8 && (ipv6Seperators != 8 || ((compressBegin > 2 || ip.charAt(0) == ':') && (compressBegin < 14 || ip.charAt(tmp) == ':'))))) && currentIndex2 + 1 < bytes.length))) {
                if (begin >= 0 && i - begin <= 4) {
                    value <<= (4 - (i - begin)) << 2;
                }
                currentIndex = currentIndex2 + 1;
                bytes[currentIndex2] = (byte) (((value & 15) << 4) | ((value >> 4) & 15));
                currentIndex2 = currentIndex + 1;
                bytes[currentIndex] = (byte) ((((value >> 8) & 15) << 4) | ((value >> 12) & 15));
                currentIndex = currentIndex2;
            } else {
                currentIndex = currentIndex2;
                return null;
            }
        } else if ((begin <= 0 || i - begin <= 3) && ipv4Seperators == 3 && currentIndex2 < bytes.length) {
            if (ipv6Seperators == 0) {
                compressLength = 12;
            } else if (ipv6Seperators < 2 || ip.charAt(ipLength - 1) == ':' || ((isCompressed || ipv6Seperators != 6 || ip.charAt(0) == ':') && (!isCompressed || ipv6Seperators + 1 >= 8 || (ip.charAt(0) == ':' && compressBegin > 2)))) {
                currentIndex = currentIndex2;
                return null;
            } else {
                compressLength -= 2;
            }
            value <<= (3 - (i - begin)) << 2;
            begin = (((value & 15) * 100) + (((value >> 4) & 15) * 10)) + ((value >> 8) & 15);
            if (begin < 0 || begin > 255) {
                currentIndex = currentIndex2;
                return null;
            }
            currentIndex = currentIndex2 + 1;
            bytes[currentIndex2] = (byte) begin;
        } else {
            currentIndex = currentIndex2;
            return null;
        }
        i = currentIndex + compressLength;
        if (needsShift || i >= bytes.length) {
            if (i >= bytes.length) {
                compressBegin++;
            }
            for (i = currentIndex; i < bytes.length; i++) {
                begin = bytes.length - 1;
                while (begin >= compressBegin) {
                    bytes[begin] = bytes[begin - 1];
                    begin--;
                }
                bytes[begin] = (byte) 0;
                compressBegin++;
            }
        } else {
            i = 0;
            while (i < compressLength) {
                begin = i + compressBegin;
                currentIndex = begin + compressLength;
                if (currentIndex < bytes.length) {
                    bytes[currentIndex] = bytes[begin];
                    bytes[begin] = (byte) 0;
                    i++;
                }
            }
        }
        if (ipv4Mapped && ipv4Seperators > 0 && bytes[0] == (byte) 0 && bytes[1] == (byte) 0 && bytes[2] == (byte) 0 && bytes[3] == (byte) 0 && bytes[4] == (byte) 0 && bytes[5] == (byte) 0 && bytes[6] == (byte) 0 && bytes[7] == (byte) 0 && bytes[8] == (byte) 0 && bytes[9] == (byte) 0) {
            bytes[11] = (byte) -1;
            bytes[10] = (byte) -1;
        }
        try {
            return Inet6Address.getByAddress(null, bytes, -1);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toAddressString(InetAddress ip) {
        return toAddressString(ip, false);
    }

    public static String toAddressString(InetAddress ip, boolean ipv4Mapped) {
        if (ip instanceof Inet4Address) {
            return ip.getHostAddress();
        }
        if (ip instanceof Inet6Address) {
            int i;
            int currentLength;
            byte[] bytes = ip.getAddress();
            int[] words = new int[8];
            for (i = 0; i < words.length; i++) {
                words[i] = ((bytes[i << 1] & 255) << 8) | (bytes[(i << 1) + 1] & 255);
            }
            int currentStart = -1;
            int shortestStart = -1;
            int shortestLength = 0;
            i = 0;
            while (i < words.length) {
                if (words[i] == 0) {
                    if (currentStart < 0) {
                        currentStart = i;
                    }
                } else if (currentStart >= 0) {
                    currentLength = i - currentStart;
                    if (currentLength > shortestLength) {
                        shortestStart = currentStart;
                        shortestLength = currentLength;
                    }
                    currentStart = -1;
                }
                i++;
            }
            if (currentStart >= 0) {
                currentLength = i - currentStart;
                if (currentLength > shortestLength) {
                    shortestStart = currentStart;
                    shortestLength = currentLength;
                }
            }
            if (shortestLength == 1) {
                shortestLength = 0;
                shortestStart = -1;
            }
            int shortestEnd = shortestStart + shortestLength;
            StringBuilder b = new StringBuilder(39);
            if (shortestEnd < 0) {
                b.append(Integer.toHexString(words[0]));
                for (i = 1; i < words.length; i++) {
                    b.append(':');
                    b.append(Integer.toHexString(words[i]));
                }
            } else {
                boolean isIpv4Mapped;
                if (inRangeEndExclusive(0, shortestStart, shortestEnd)) {
                    b.append("::");
                    isIpv4Mapped = ipv4Mapped && shortestEnd == 5 && words[5] == SupportMenu.USER_MASK;
                } else {
                    b.append(Integer.toHexString(words[0]));
                    isIpv4Mapped = false;
                }
                i = 1;
                while (i < words.length) {
                    if (!inRangeEndExclusive(i, shortestStart, shortestEnd)) {
                        if (!inRangeEndExclusive(i - 1, shortestStart, shortestEnd)) {
                            if (!isIpv4Mapped || i == 6) {
                                b.append(':');
                            } else {
                                b.append('.');
                            }
                        }
                        if (!isIpv4Mapped || i <= 5) {
                            b.append(Integer.toHexString(words[i]));
                        } else {
                            b.append(words[i] >> 8);
                            b.append('.');
                            b.append(words[i] & 255);
                        }
                    } else if (!inRangeEndExclusive(i - 1, shortestStart, shortestEnd)) {
                        b.append("::");
                    }
                    i++;
                }
            }
            return b.toString();
        }
        throw new IllegalArgumentException("Unhandled type: " + ip.getClass());
    }

    private static boolean inRangeEndExclusive(int value, int start, int end) {
        return value >= start && value < end;
    }

    private NetUtil() {
    }
}
