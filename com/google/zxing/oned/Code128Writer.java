package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.Hashtable;

public final class Code128Writer extends UPCEANWriter {
    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Hashtable hints) throws WriterException {
        if (format == BarcodeFormat.CODE_128) {
            return super.encode(contents, format, width, height, hints);
        }
        throw new IllegalArgumentException(new StringBuffer().append("Can only encode CODE_128, but got ").append(format).toString());
    }

    public byte[] encode(String contents) {
        int length = contents.length();
        if (length > 80) {
            throw new IllegalArgumentException(new StringBuffer().append("Requested contents should be less than 80 digits long, but got ").append(length).toString());
        }
        int i;
        int codeWidth = 35;
        for (i = 0; i < length; i++) {
            for (int i2 : Code128Reader.CODE_PATTERNS[contents.charAt(i) - 32]) {
                codeWidth += i2;
            }
        }
        byte[] result = new byte[codeWidth];
        int pos = UPCEANWriter.appendPattern(result, 0, Code128Reader.CODE_PATTERNS[104], 1);
        int check = 104;
        for (i = 0; i < length; i++) {
            check += (contents.charAt(i) - 32) * (i + 1);
            pos += UPCEANWriter.appendPattern(result, pos, Code128Reader.CODE_PATTERNS[contents.charAt(i) - 32], 1);
        }
        pos += UPCEANWriter.appendPattern(result, pos, Code128Reader.CODE_PATTERNS[check % 103], 1);
        pos += UPCEANWriter.appendPattern(result, pos, Code128Reader.CODE_PATTERNS[106], 1);
        return result;
    }
}
