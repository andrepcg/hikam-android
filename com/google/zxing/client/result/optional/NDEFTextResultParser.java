package com.google.zxing.client.result.optional;

import com.google.zxing.Result;
import com.google.zxing.client.result.TextParsedResult;

final class NDEFTextResultParser extends AbstractNDEFResultParser {
    NDEFTextResultParser() {
    }

    public static TextParsedResult parse(Result result) {
        byte[] bytes = result.getRawBytes();
        if (bytes == null) {
            return null;
        }
        NDEFRecord ndefRecord = NDEFRecord.readRecord(bytes, 0);
        if (ndefRecord == null || !ndefRecord.isMessageBegin() || !ndefRecord.isMessageEnd() || !ndefRecord.getType().equals(NDEFRecord.TEXT_WELL_KNOWN_TYPE)) {
            return null;
        }
        String[] languageText = decodeTextPayload(ndefRecord.getPayload());
        return new TextParsedResult(languageText[0], languageText[1]);
    }

    static String[] decodeTextPayload(byte[] payload) {
        boolean isUTF16;
        byte statusByte = payload[0];
        if ((statusByte & 128) != 0) {
            isUTF16 = true;
        } else {
            isUTF16 = false;
        }
        int languageLength = statusByte & 31;
        String language = AbstractNDEFResultParser.bytesToString(payload, 1, languageLength, "US-ASCII");
        String text = AbstractNDEFResultParser.bytesToString(payload, languageLength + 1, (payload.length - languageLength) - 1, isUTF16 ? "UTF-16" : "UTF8");
        return new String[]{language, text};
    }
}
