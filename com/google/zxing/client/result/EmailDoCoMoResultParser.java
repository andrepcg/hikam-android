package com.google.zxing.client.result;

import com.google.zxing.Result;

final class EmailDoCoMoResultParser extends AbstractDoCoMoResultParser {
    private static final char[] ATEXT_SYMBOLS = new char[]{'@', '.', '!', '#', '$', '%', '&', '\'', '*', '+', '-', '/', '=', '?', '^', '_', '`', '{', '|', '}', '~'};

    EmailDoCoMoResultParser() {
    }

    public static EmailAddressParsedResult parse(Result result) {
        String rawText = result.getText();
        if (rawText == null || !rawText.startsWith("MATMSG:")) {
            return null;
        }
        String[] rawTo = AbstractDoCoMoResultParser.matchDoCoMoPrefixedField("TO:", rawText, true);
        if (rawTo == null) {
            return null;
        }
        String to = rawTo[0];
        if (isBasicallyValidEmailAddress(to)) {
            return new EmailAddressParsedResult(to, AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("SUB:", rawText, false), AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("BODY:", rawText, false), new StringBuffer().append("mailto:").append(to).toString());
        }
        return null;
    }

    static boolean isBasicallyValidEmailAddress(String email) {
        if (email == null) {
            return false;
        }
        boolean atFound = false;
        for (int i = 0; i < email.length(); i++) {
            char c = email.charAt(i);
            if ((c < 'a' || c > 'z') && ((c < 'A' || c > 'Z') && ((c < '0' || c > '9') && !isAtextSymbol(c)))) {
                return false;
            }
            if (c == '@') {
                if (atFound) {
                    return false;
                }
                atFound = true;
            }
        }
        return atFound;
    }

    private static boolean isAtextSymbol(char c) {
        for (char c2 : ATEXT_SYMBOLS) {
            if (c == c2) {
                return true;
            }
        }
        return false;
    }
}
