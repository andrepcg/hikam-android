package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.Hashtable;

public final class Code39Writer extends UPCEANWriter {
    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Hashtable hints) throws WriterException {
        if (format == BarcodeFormat.CODE_39) {
            return super.encode(contents, format, width, height, hints);
        }
        throw new IllegalArgumentException(new StringBuffer().append("Can only encode CODE_39, but got ").append(format).toString());
    }

    public byte[] encode(String contents) {
        int length = contents.length();
        if (length > 80) {
            throw new IllegalArgumentException(new StringBuffer().append("Requested contents should be less than 80 digits long, but got ").append(length).toString());
        }
        int i;
        int[] widths = new int[9];
        int codeWidth = length + 25;
        for (i = 0; i < length; i++) {
            toIntArray(Code39Reader.CHARACTER_ENCODINGS["0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".indexOf(contents.charAt(i))], widths);
            for (int i2 : widths) {
                codeWidth += i2;
            }
        }
        byte[] result = new byte[codeWidth];
        toIntArray(Code39Reader.CHARACTER_ENCODINGS[39], widths);
        int pos = UPCEANWriter.appendPattern(result, 0, widths, 1);
        int[] narrowWhite = new int[]{1};
        pos += UPCEANWriter.appendPattern(result, pos, narrowWhite, 0);
        for (i = length - 1; i >= 0; i--) {
            toIntArray(Code39Reader.CHARACTER_ENCODINGS["0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".indexOf(contents.charAt(i))], widths);
            pos += UPCEANWriter.appendPattern(result, pos, widths, 1);
            pos += UPCEANWriter.appendPattern(result, pos, narrowWhite, 0);
        }
        toIntArray(Code39Reader.CHARACTER_ENCODINGS[39], widths);
        pos += UPCEANWriter.appendPattern(result, pos, widths, 1);
        return result;
    }

    private static void toIntArray(int a, int[] toReturn) {
        for (int i = 0; i < 9; i++) {
            toReturn[i] = (a & (1 << i)) == 0 ? 1 : 2;
        }
    }
}
