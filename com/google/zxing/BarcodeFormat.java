package com.google.zxing;

import java.util.Hashtable;

public final class BarcodeFormat {
    public static final BarcodeFormat CODABAR = new BarcodeFormat("CODABAR");
    public static final BarcodeFormat CODE_128 = new BarcodeFormat("CODE_128");
    public static final BarcodeFormat CODE_39 = new BarcodeFormat("CODE_39");
    public static final BarcodeFormat CODE_93 = new BarcodeFormat("CODE_93");
    public static final BarcodeFormat DATA_MATRIX = new BarcodeFormat("DATA_MATRIX");
    public static final BarcodeFormat EAN_13 = new BarcodeFormat("EAN_13");
    public static final BarcodeFormat EAN_8 = new BarcodeFormat("EAN_8");
    public static final BarcodeFormat ITF = new BarcodeFormat("ITF");
    public static final BarcodeFormat PDF417 = new BarcodeFormat("PDF417");
    public static final BarcodeFormat QR_CODE = new BarcodeFormat("QR_CODE");
    public static final BarcodeFormat RSS14 = new BarcodeFormat("RSS14");
    public static final BarcodeFormat RSS_EXPANDED = new BarcodeFormat("RSS_EXPANDED");
    public static final BarcodeFormat UPC_A = new BarcodeFormat("UPC_A");
    public static final BarcodeFormat UPC_E = new BarcodeFormat("UPC_E");
    public static final BarcodeFormat UPC_EAN_EXTENSION = new BarcodeFormat("UPC_EAN_EXTENSION");
    private static final Hashtable VALUES = new Hashtable();
    private final String name;

    private BarcodeFormat(String name) {
        this.name = name;
        VALUES.put(name, this);
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    public static BarcodeFormat valueOf(String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException();
        }
        BarcodeFormat format = (BarcodeFormat) VALUES.get(name);
        if (format != null) {
            return format;
        }
        throw new IllegalArgumentException();
    }
}
