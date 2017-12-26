package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.Hashtable;

public final class EAN13Writer extends UPCEANWriter {
    private static final int codeWidth = 95;

    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Hashtable hints) throws WriterException {
        if (format == BarcodeFormat.EAN_13) {
            return super.encode(contents, format, width, height, hints);
        }
        throw new IllegalArgumentException(new StringBuffer().append("Can only encode EAN_13, but got ").append(format).toString());
    }

    public byte[] encode(String contents) {
        if (contents.length() != 13) {
            throw new IllegalArgumentException(new StringBuffer().append("Requested contents should be 13 digits long, but got ").append(contents.length()).toString());
        }
        int i;
        int parities = EAN13Reader.FIRST_DIGIT_ENCODINGS[Integer.parseInt(contents.substring(0, 1))];
        byte[] result = new byte[95];
        int pos = 0 + UPCEANWriter.appendPattern(result, 0, UPCEANReader.START_END_PATTERN, 1);
        for (i = 1; i <= 6; i++) {
            int digit = Integer.parseInt(contents.substring(i, i + 1));
            if (((parities >> (6 - i)) & 1) == 1) {
                digit += 10;
            }
            pos += UPCEANWriter.appendPattern(result, pos, UPCEANReader.L_AND_G_PATTERNS[digit], 0);
        }
        pos += UPCEANWriter.appendPattern(result, pos, UPCEANReader.MIDDLE_PATTERN, 0);
        for (i = 7; i <= 12; i++) {
            pos += UPCEANWriter.appendPattern(result, pos, UPCEANReader.L_PATTERNS[Integer.parseInt(contents.substring(i, i + 1))], 1);
        }
        pos += UPCEANWriter.appendPattern(result, pos, UPCEANReader.START_END_PATTERN, 1);
        return result;
    }
}
