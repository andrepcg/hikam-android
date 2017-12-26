package com.google.zxing.client.result;

import com.google.zxing.Result;

final class URIResultParser extends ResultParser {
    private URIResultParser() {
    }

    public static URIParsedResult parse(Result result) {
        String rawText = result.getText();
        if (rawText != null && rawText.startsWith("URL:")) {
            rawText = rawText.substring(4);
        }
        if (isBasicallyValidURI(rawText)) {
            return new URIParsedResult(rawText, null);
        }
        return null;
    }

    static boolean isBasicallyValidURI(String uri) {
        if (uri == null || uri.indexOf(32) >= 0 || uri.indexOf(10) >= 0) {
            return false;
        }
        int period = uri.indexOf(46);
        if (period >= uri.length() - 2) {
            return false;
        }
        int colon = uri.indexOf(58);
        if (period < 0 && colon < 0) {
            return false;
        }
        if (colon >= 0) {
            int i;
            char c;
            if (period < 0 || period > colon) {
                for (i = 0; i < colon; i++) {
                    c = uri.charAt(i);
                    if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
                        return false;
                    }
                }
            } else if (colon >= uri.length() - 2) {
                return false;
            } else {
                for (i = colon + 1; i < colon + 3; i++) {
                    c = uri.charAt(i);
                    if (c < '0' || c > '9') {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
