package com.google.zxing.client.result;

import com.google.zxing.Result;

final class WifiResultParser extends ResultParser {
    private WifiResultParser() {
    }

    public static WifiParsedResult parse(Result result) {
        String rawText = result.getText();
        if (rawText == null || !rawText.startsWith("WIFI:")) {
            return null;
        }
        return new WifiParsedResult(ResultParser.matchSinglePrefixedField("T:", rawText, ';', false), ResultParser.matchSinglePrefixedField("S:", rawText, ';', false), ResultParser.matchSinglePrefixedField("P:", rawText, ';', false));
    }
}
