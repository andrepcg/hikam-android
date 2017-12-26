package com.google.zxing.client.result.optional;

import com.google.zxing.Result;

final class NDEFSmartPosterResultParser extends AbstractNDEFResultParser {
    NDEFSmartPosterResultParser() {
    }

    public static NDEFSmartPosterParsedResult parse(Result result) {
        byte[] bytes = result.getRawBytes();
        if (bytes == null) {
            return null;
        }
        NDEFRecord headerRecord = NDEFRecord.readRecord(bytes, 0);
        if (headerRecord == null || !headerRecord.isMessageBegin() || !headerRecord.isMessageEnd() || !headerRecord.getType().equals(NDEFRecord.SMART_POSTER_WELL_KNOWN_TYPE)) {
            return null;
        }
        int offset = 0;
        int recordNumber = 0;
        NDEFRecord ndefRecord = null;
        byte[] payload = headerRecord.getPayload();
        int action = -1;
        String title = null;
        String uri = null;
        while (offset < payload.length) {
            ndefRecord = NDEFRecord.readRecord(payload, offset);
            if (ndefRecord == null) {
                break;
            } else if (recordNumber == 0 && !ndefRecord.isMessageBegin()) {
                return null;
            } else {
                String type = ndefRecord.getType();
                if (NDEFRecord.TEXT_WELL_KNOWN_TYPE.equals(type)) {
                    title = NDEFTextResultParser.decodeTextPayload(ndefRecord.getPayload())[1];
                } else if (NDEFRecord.URI_WELL_KNOWN_TYPE.equals(type)) {
                    uri = NDEFURIResultParser.decodeURIPayload(ndefRecord.getPayload());
                } else if (NDEFRecord.ACTION_WELL_KNOWN_TYPE.equals(type)) {
                    action = ndefRecord.getPayload()[0];
                }
                recordNumber++;
                offset += ndefRecord.getTotalRecordLength();
            }
        }
        if (recordNumber == 0) {
            return null;
        }
        if (ndefRecord == null || ndefRecord.isMessageEnd()) {
            return new NDEFSmartPosterParsedResult(action, uri, title);
        }
        return null;
    }
}
