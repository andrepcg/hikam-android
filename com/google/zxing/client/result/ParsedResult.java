package com.google.zxing.client.result;

public abstract class ParsedResult {
    private final ParsedResultType type;

    public abstract String getDisplayResult();

    protected ParsedResult(ParsedResultType type) {
        this.type = type;
    }

    public ParsedResultType getType() {
        return this.type;
    }

    public String toString() {
        return getDisplayResult();
    }

    public static void maybeAppend(String value, StringBuffer result) {
        if (value != null && value.length() > 0) {
            if (result.length() > 0) {
                result.append('\n');
            }
            result.append(value);
        }
    }

    public static void maybeAppend(String[] value, StringBuffer result) {
        if (value != null) {
            int i = 0;
            while (i < value.length) {
                if (value[i] != null && value[i].length() > 0) {
                    if (result.length() > 0) {
                        result.append('\n');
                    }
                    result.append(value[i]);
                }
                i++;
            }
        }
    }
}
