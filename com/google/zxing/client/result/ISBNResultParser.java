package com.google.zxing.client.result;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

public class ISBNResultParser extends ResultParser {
    private ISBNResultParser() {
    }

    public static ISBNParsedResult parse(Result result) {
        if (!BarcodeFormat.EAN_13.equals(result.getBarcodeFormat())) {
            return null;
        }
        String rawText = result.getText();
        if (rawText == null || rawText.length() != 13) {
            return null;
        }
        if (rawText.startsWith("978") || rawText.startsWith("979")) {
            return new ISBNParsedResult(rawText);
        }
        return null;
    }
}
