package com.google.zxing.client.result;

import com.google.zxing.Result;
import java.util.Hashtable;
import java.util.Vector;

public abstract class ResultParser {
    public static ParsedResult parseResult(Result theResult) {
        ParsedResult result = BookmarkDoCoMoResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        result = AddressBookDoCoMoResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        result = EmailDoCoMoResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        result = AddressBookAUResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        result = VCardResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        result = BizcardResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        result = VEventResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        result = EmailAddressResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        result = TelResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        result = SMSMMSResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        result = SMSTOMMSTOResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        result = GeoResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        result = WifiResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        result = URLTOResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        result = URIResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        result = ISBNResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        result = ProductResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        result = ExpandedProductResultParser.parse(theResult);
        if (result != null) {
            return result;
        }
        return new TextParsedResult(theResult.getText(), null);
    }

    protected static void maybeAppend(String value, StringBuffer result) {
        if (value != null) {
            result.append('\n');
            result.append(value);
        }
    }

    protected static void maybeAppend(String[] value, StringBuffer result) {
        if (value != null) {
            for (String append : value) {
                result.append('\n');
                result.append(append);
            }
        }
    }

    protected static String[] maybeWrap(String value) {
        if (value == null) {
            return null;
        }
        return new String[]{value};
    }

    protected static String unescapeBackslash(String escaped) {
        if (escaped == null) {
            return escaped;
        }
        int backslash = escaped.indexOf(92);
        if (backslash < 0) {
            return escaped;
        }
        int max = escaped.length();
        StringBuffer unescaped = new StringBuffer(max - 1);
        unescaped.append(escaped.toCharArray(), 0, backslash);
        boolean nextIsEscaped = false;
        for (int i = backslash; i < max; i++) {
            char c = escaped.charAt(i);
            if (nextIsEscaped || c != '\\') {
                unescaped.append(c);
                nextIsEscaped = false;
            } else {
                nextIsEscaped = true;
            }
        }
        return unescaped.toString();
    }

    private static String urlDecode(String escaped) {
        if (escaped == null) {
            return null;
        }
        char[] escapedArray = escaped.toCharArray();
        int first = findFirstEscape(escapedArray);
        if (first < 0) {
            return escaped;
        }
        int max = escapedArray.length;
        StringBuffer unescaped = new StringBuffer(max - 2);
        unescaped.append(escapedArray, 0, first);
        int i = first;
        while (i < max) {
            char c = escapedArray[i];
            if (c == '+') {
                unescaped.append(' ');
            } else if (c != '%') {
                unescaped.append(c);
            } else if (i >= max - 2) {
                unescaped.append('%');
            } else {
                i++;
                int firstDigitValue = parseHexDigit(escapedArray[i]);
                i++;
                int secondDigitValue = parseHexDigit(escapedArray[i]);
                if (firstDigitValue < 0 || secondDigitValue < 0) {
                    unescaped.append('%');
                    unescaped.append(escapedArray[i - 1]);
                    unescaped.append(escapedArray[i]);
                }
                unescaped.append((char) ((firstDigitValue << 4) + secondDigitValue));
            }
            i++;
        }
        return unescaped.toString();
    }

    private static int findFirstEscape(char[] escapedArray) {
        int max = escapedArray.length;
        int i = 0;
        while (i < max) {
            char c = escapedArray[i];
            if (c == '+' || c == '%') {
                return i;
            }
            i++;
        }
        return -1;
    }

    private static int parseHexDigit(char c) {
        if (c >= 'a') {
            if (c <= 'f') {
                return (c - 97) + 10;
            }
        } else if (c >= 'A') {
            if (c <= 'F') {
                return (c - 65) + 10;
            }
        } else if (c >= '0' && c <= '9') {
            return c - 48;
        }
        return -1;
    }

    protected static boolean isStringOfDigits(String value, int length) {
        if (value == null || length != value.length()) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    protected static boolean isSubstringOfDigits(String value, int offset, int length) {
        if (value == null) {
            return false;
        }
        int max = offset + length;
        if (value.length() < max) {
            return false;
        }
        for (int i = offset; i < max; i++) {
            char c = value.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    static Hashtable parseNameValuePairs(String uri) {
        int paramStart = uri.indexOf(63);
        if (paramStart < 0) {
            return null;
        }
        Hashtable result = new Hashtable(3);
        paramStart++;
        while (true) {
            int paramEnd = uri.indexOf(38, paramStart);
            if (paramEnd >= 0) {
                appendKeyValue(uri, paramStart, paramEnd, result);
                paramStart = paramEnd + 1;
            } else {
                appendKeyValue(uri, paramStart, uri.length(), result);
                return result;
            }
        }
    }

    private static void appendKeyValue(String uri, int paramStart, int paramEnd, Hashtable result) {
        int separator = uri.indexOf(61, paramStart);
        if (separator >= 0) {
            result.put(uri.substring(paramStart, separator), urlDecode(uri.substring(separator + 1, paramEnd)));
        }
    }

    static String[] matchPrefixedField(String prefix, String rawText, char endChar, boolean trim) {
        Vector matches = null;
        int i = 0;
        int max = rawText.length();
        while (i < max) {
            i = rawText.indexOf(prefix, i);
            if (i < 0) {
                break;
            }
            i += prefix.length();
            int start = i;
            boolean done = false;
            while (!done) {
                i = rawText.indexOf(endChar, i);
                if (i < 0) {
                    i = rawText.length();
                    done = true;
                } else if (rawText.charAt(i - 1) == '\\') {
                    i++;
                } else {
                    if (matches == null) {
                        matches = new Vector(3);
                    }
                    String element = unescapeBackslash(rawText.substring(start, i));
                    if (trim) {
                        element = element.trim();
                    }
                    matches.addElement(element);
                    i++;
                    done = true;
                }
            }
        }
        if (matches == null || matches.isEmpty()) {
            return null;
        }
        return toStringArray(matches);
    }

    static String matchSinglePrefixedField(String prefix, String rawText, char endChar, boolean trim) {
        String[] matches = matchPrefixedField(prefix, rawText, endChar, trim);
        return matches == null ? null : matches[0];
    }

    static String[] toStringArray(Vector strings) {
        int size = strings.size();
        String[] result = new String[size];
        for (int j = 0; j < size; j++) {
            result[j] = (String) strings.elementAt(j);
        }
        return result;
    }
}
