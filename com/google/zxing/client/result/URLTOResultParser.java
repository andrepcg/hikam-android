package com.google.zxing.client.result;

import com.google.zxing.Result;

final class URLTOResultParser {
    private URLTOResultParser() {
    }

    public static URIParsedResult parse(Result result) {
        String title = null;
        String rawText = result.getText();
        if (rawText == null) {
            return null;
        }
        if (!rawText.startsWith("urlto:") && !rawText.startsWith("URLTO:")) {
            return null;
        }
        int titleEnd = rawText.indexOf(58, 6);
        if (titleEnd < 0) {
            return null;
        }
        if (titleEnd > 6) {
            title = rawText.substring(6, titleEnd);
        }
        return new URIParsedResult(rawText.substring(titleEnd + 1), title);
    }
}
