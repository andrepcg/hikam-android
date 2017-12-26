package com.google.zxing;

public final class DecodeHintType {
    public static final DecodeHintType ALLOWED_LENGTHS = new DecodeHintType();
    public static final DecodeHintType ASSUME_CODE_39_CHECK_DIGIT = new DecodeHintType();
    public static final DecodeHintType CHARACTER_SET = new DecodeHintType();
    public static final DecodeHintType NEED_RESULT_POINT_CALLBACK = new DecodeHintType();
    public static final DecodeHintType OTHER = new DecodeHintType();
    public static final DecodeHintType POSSIBLE_FORMATS = new DecodeHintType();
    public static final DecodeHintType PURE_BARCODE = new DecodeHintType();
    public static final DecodeHintType TRY_HARDER = new DecodeHintType();

    private DecodeHintType() {
    }
}
