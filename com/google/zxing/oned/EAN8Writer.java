package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.Hashtable;

public final class EAN8Writer extends UPCEANWriter {
    private static final int codeWidth = 67;

    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Hashtable hints) throws WriterException {
        if (format == BarcodeFormat.EAN_8) {
            return super.encode(contents, format, width, height, hints);
        }
        throw new IllegalArgumentException(new StringBuffer().append("Can only encode EAN_8, but got ").append(format).toString());
    }

    public byte[] encode(String contents) {
        if (contents.length() != 8) {
            throw new IllegalArgumentException(new StringBuffer().append("Requested contents should be 8 digits long, but got ").append(contents.length()).toString());
        }
        int i;
        byte[] result = new byte[67];
        int pos = 0 + UPCEANWriter.appendPattern(result, 0, UPCEANReader.START_END_PATTERN, 1);
        for (i = 0; i <= 3; i++) {
            pos += UPCEANWriter.appendPattern(result, pos, UPCEANReader.L_PATTERNS[Integer.parseInt(contents.substring(i, i + 1))], 0);
        }
        pos += UPCEANWriter.appendPattern(result, pos, UPCEANReader.MIDDLE_PATTERN, 0);
        for (i = 4; i <= 7; i++) {
            pos += UPCEANWriter.appendPattern(result, pos, UPCEANReader.L_PATTERNS[Integer.parseInt(contents.substring(i, i + 1))], 1);
        }
        pos += UPCEANWriter.appendPattern(result, pos, UPCEANReader.START_END_PATTERN, 1);
        return result;
    }
}
