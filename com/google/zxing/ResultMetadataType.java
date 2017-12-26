package com.google.zxing;

import java.util.Hashtable;

public final class ResultMetadataType {
    public static final ResultMetadataType BYTE_SEGMENTS = new ResultMetadataType("BYTE_SEGMENTS");
    public static final ResultMetadataType ERROR_CORRECTION_LEVEL = new ResultMetadataType("ERROR_CORRECTION_LEVEL");
    public static final ResultMetadataType ISSUE_NUMBER = new ResultMetadataType("ISSUE_NUMBER");
    public static final ResultMetadataType ORIENTATION = new ResultMetadataType("ORIENTATION");
    public static final ResultMetadataType OTHER = new ResultMetadataType("OTHER");
    public static final ResultMetadataType POSSIBLE_COUNTRY = new ResultMetadataType("POSSIBLE_COUNTRY");
    public static final ResultMetadataType SUGGESTED_PRICE = new ResultMetadataType("SUGGESTED_PRICE");
    private static final Hashtable VALUES = new Hashtable();
    private final String name;

    private ResultMetadataType(String name) {
        this.name = name;
        VALUES.put(name, this);
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    public static ResultMetadataType valueOf(String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException();
        }
        ResultMetadataType format = (ResultMetadataType) VALUES.get(name);
        if (format != null) {
            return format;
        }
        throw new IllegalArgumentException();
    }
}
