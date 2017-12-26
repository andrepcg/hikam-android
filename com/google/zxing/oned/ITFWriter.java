package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.Hashtable;

public final class ITFWriter extends UPCEANWriter {
    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Hashtable hints) throws WriterException {
        if (format == BarcodeFormat.ITF) {
            return super.encode(contents, format, width, height, hints);
        }
        throw new IllegalArgumentException(new StringBuffer().append("Can only encode ITF, but got ").append(format).toString());
    }

    public byte[] encode(String contents) {
        int length = contents.length();
        if (length > 80) {
            throw new IllegalArgumentException(new StringBuffer().append("Requested contents should be less than 80 digits long, but got ").append(length).toString());
        }
        byte[] result = new byte[((length * 9) + 9)];
        int pos = UPCEANWriter.appendPattern(result, 0, new int[]{1, 1, 1, 1}, 1);
        for (int i = 0; i < length; i += 2) {
            int one = Character.digit(contents.charAt(i), 10);
            int two = Character.digit(contents.charAt(i + 1), 10);
            int[] encoding = new int[18];
            for (int j = 0; j < 5; j++) {
                encoding[j << 1] = ITFReader.PATTERNS[one][j];
                encoding[(j << 1) + 1] = ITFReader.PATTERNS[two][j];
            }
            pos += UPCEANWriter.appendPattern(result, pos, encoding, 1);
        }
        pos += UPCEANWriter.appendPattern(result, pos, new int[]{3, 1, 1}, 1);
        return result;
    }
}
