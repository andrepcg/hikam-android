package com.google.zxing.client.result.optional;

final class NDEFRecord {
    public static final String ACTION_WELL_KNOWN_TYPE = "act";
    public static final String SMART_POSTER_WELL_KNOWN_TYPE = "Sp";
    private static final int SUPPORTED_HEADER = 17;
    private static final int SUPPORTED_HEADER_MASK = 63;
    public static final String TEXT_WELL_KNOWN_TYPE = "T";
    public static final String URI_WELL_KNOWN_TYPE = "U";
    private final int header;
    private final byte[] payload;
    private final int totalRecordLength;
    private final String type;

    private NDEFRecord(int header, String type, byte[] payload, int totalRecordLength) {
        this.header = header;
        this.type = type;
        this.payload = payload;
        this.totalRecordLength = totalRecordLength;
    }

    static NDEFRecord readRecord(byte[] bytes, int offset) {
        int header = bytes[offset] & 255;
        if (((header ^ 17) & 63) != 0) {
            return null;
        }
        int typeLength = bytes[offset + 1] & 255;
        int payloadLength = bytes[offset + 2] & 255;
        String type = AbstractNDEFResultParser.bytesToString(bytes, offset + 3, typeLength, "US-ASCII");
        byte[] payload = new byte[payloadLength];
        System.arraycopy(bytes, (offset + 3) + typeLength, payload, 0, payloadLength);
        return new NDEFRecord(header, type, payload, (typeLength + 3) + payloadLength);
    }

    boolean isMessageBegin() {
        return (this.header & 128) != 0;
    }

    boolean isMessageEnd() {
        return (this.header & 64) != 0;
    }

    String getType() {
        return this.type;
    }

    byte[] getPayload() {
        return this.payload;
    }

    int getTotalRecordLength() {
        return this.totalRecordLength;
    }
}
