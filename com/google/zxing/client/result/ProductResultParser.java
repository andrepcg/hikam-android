package com.google.zxing.client.result;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.oned.UPCEReader;

final class ProductResultParser extends ResultParser {
    private ProductResultParser() {
    }

    public static ProductParsedResult parse(Result result) {
        BarcodeFormat format = result.getBarcodeFormat();
        if (!BarcodeFormat.UPC_A.equals(format) && !BarcodeFormat.UPC_E.equals(format) && !BarcodeFormat.EAN_8.equals(format) && !BarcodeFormat.EAN_13.equals(format)) {
            return null;
        }
        String rawText = result.getText();
        if (rawText == null) {
            return null;
        }
        String normalizedProductID;
        int length = rawText.length();
        for (int x = 0; x < length; x++) {
            char c = rawText.charAt(x);
            if (c < '0' || c > '9') {
                return null;
            }
        }
        if (BarcodeFormat.UPC_E.equals(format)) {
            normalizedProductID = UPCEReader.convertUPCEtoUPCA(rawText);
        } else {
            normalizedProductID = rawText;
        }
        return new ProductParsedResult(rawText, normalizedProductID);
    }
}
