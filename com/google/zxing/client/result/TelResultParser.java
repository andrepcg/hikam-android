package com.google.zxing.client.result;

import com.google.zxing.Result;

final class TelResultParser extends ResultParser {
    private TelResultParser() {
    }

    public static TelParsedResult parse(Result result) {
        String rawText = result.getText();
        if (rawText == null || (!rawText.startsWith("tel:") && !rawText.startsWith("TEL:"))) {
            return null;
        }
        String telURI;
        if (rawText.startsWith("TEL:")) {
            telURI = new StringBuffer().append("tel:").append(rawText.substring(4)).toString();
        } else {
            telURI = rawText;
        }
        int queryStart = rawText.indexOf(63, 4);
        return new TelParsedResult(queryStart < 0 ? rawText.substring(4) : rawText.substring(4, queryStart), telURI, null);
    }
}
