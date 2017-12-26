package com.google.zxing.client.result.optional;

import com.google.zxing.Result;
import com.google.zxing.client.result.URIParsedResult;

final class NDEFURIResultParser extends AbstractNDEFResultParser {
    private static final String[] URI_PREFIXES = new String[]{null, "http://www.", "https://www.", "http://", "https://", "tel:", "mailto:", "ftp://anonymous:anonymous@", "ftp://ftp.", "ftps://", "sftp://", "smb://", "nfs://", "ftp://", "dav://", "news:", "telnet://", "imap:", "rtsp://", "urn:", "pop:", "sip:", "sips:", "tftp:", "btspp://", "btl2cap://", "btgoep://", "tcpobex://", "irdaobex://", "file://", "urn:epc:id:", "urn:epc:tag:", "urn:epc:pat:", "urn:epc:raw:", "urn:epc:", "urn:nfc:"};

    NDEFURIResultParser() {
    }

    public static URIParsedResult parse(Result result) {
        byte[] bytes = result.getRawBytes();
        if (bytes == null) {
            return null;
        }
        NDEFRecord ndefRecord = NDEFRecord.readRecord(bytes, 0);
        if (ndefRecord != null && ndefRecord.isMessageBegin() && ndefRecord.isMessageEnd() && ndefRecord.getType().equals(NDEFRecord.URI_WELL_KNOWN_TYPE)) {
            return new URIParsedResult(decodeURIPayload(ndefRecord.getPayload()), null);
        }
        return null;
    }

    static String decodeURIPayload(byte[] payload) {
        int identifierCode = payload[0] & 255;
        String prefix = null;
        if (identifierCode < URI_PREFIXES.length) {
            prefix = URI_PREFIXES[identifierCode];
        }
        String restOfURI = AbstractNDEFResultParser.bytesToString(payload, 1, payload.length - 1, "UTF8");
        return prefix == null ? restOfURI : new StringBuffer().append(prefix).append(restOfURI).toString();
    }
}
