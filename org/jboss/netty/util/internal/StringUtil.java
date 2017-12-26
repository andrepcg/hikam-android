package org.jboss.netty.util.internal;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public final class StringUtil {
    private static final String EMPTY_STRING = "";
    public static final String NEWLINE;

    private StringUtil() {
    }

    static {
        String newLine;
        try {
            newLine = new Formatter().format("%n", new Object[0]).toString();
        } catch (Exception e) {
            newLine = "\n";
        }
        NEWLINE = newLine;
    }

    public static String stripControlCharacters(Object value) {
        if (value == null) {
            return null;
        }
        return stripControlCharacters(value.toString());
    }

    public static String stripControlCharacters(String value) {
        if (value == null) {
            return null;
        }
        int i;
        boolean hasControlChars = false;
        for (i = value.length() - 1; i >= 0; i--) {
            if (Character.isISOControl(value.charAt(i))) {
                hasControlChars = true;
                break;
            }
        }
        if (!hasControlChars) {
            return value;
        }
        StringBuilder buf = new StringBuilder(value.length());
        i = 0;
        while (i < value.length() && Character.isISOControl(value.charAt(i))) {
            i++;
        }
        boolean suppressingControlChars = false;
        while (i < value.length()) {
            if (Character.isISOControl(value.charAt(i))) {
                suppressingControlChars = true;
            } else {
                if (suppressingControlChars) {
                    suppressingControlChars = false;
                    buf.append(' ');
                }
                buf.append(value.charAt(i));
            }
            i++;
        }
        return buf.toString();
    }

    public static String[] split(String value, char delim) {
        int i;
        int end = value.length();
        List<String> res = new ArrayList();
        int start = 0;
        for (i = 0; i < end; i++) {
            if (value.charAt(i) == delim) {
                if (start == i) {
                    res.add("");
                } else {
                    res.add(value.substring(start, i));
                }
                start = i + 1;
            }
        }
        if (start == 0) {
            res.add(value);
        } else if (start != end) {
            res.add(value.substring(start, end));
        } else {
            i = res.size() - 1;
            while (i >= 0 && ((String) res.get(i)).length() == 0) {
                res.remove(i);
                i--;
            }
        }
        return (String[]) res.toArray(new String[res.size()]);
    }

    public static String[] split(String value, char delim, int maxParts) {
        int i;
        int end = value.length();
        List<String> res = new ArrayList();
        int start = 0;
        int cpt = 1;
        for (i = 0; i < end && cpt < maxParts; i++) {
            if (value.charAt(i) == delim) {
                if (start == i) {
                    res.add("");
                } else {
                    res.add(value.substring(start, i));
                }
                start = i + 1;
                cpt++;
            }
        }
        if (start == 0) {
            res.add(value);
        } else if (start != end) {
            res.add(value.substring(start, end));
        } else {
            i = res.size() - 1;
            while (i >= 0 && ((String) res.get(i)).length() == 0) {
                res.remove(i);
                i--;
            }
        }
        return (String[]) res.toArray(new String[res.size()]);
    }

    public static String substringAfter(String value, char delim) {
        int pos = value.indexOf(delim);
        if (pos >= 0) {
            return value.substring(pos + 1);
        }
        return null;
    }
}
