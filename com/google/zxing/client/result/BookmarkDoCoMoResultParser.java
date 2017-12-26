package com.google.zxing.client.result;

import com.google.zxing.Result;

final class BookmarkDoCoMoResultParser extends AbstractDoCoMoResultParser {
    private BookmarkDoCoMoResultParser() {
    }

    public static URIParsedResult parse(Result result) {
        String rawText = result.getText();
        if (rawText == null || !rawText.startsWith("MEBKM:")) {
            return null;
        }
        String title = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("TITLE:", rawText, true);
        String[] rawUri = AbstractDoCoMoResultParser.matchDoCoMoPrefixedField("URL:", rawText, true);
        if (rawUri == null) {
            return null;
        }
        String uri = rawUri[0];
        if (URIResultParser.isBasicallyValidURI(uri)) {
            return new URIParsedResult(uri, title);
        }
        return null;
    }
}
