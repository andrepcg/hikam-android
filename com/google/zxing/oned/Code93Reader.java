package com.google.zxing.oned;

import android.support.v4.media.TransportMediator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.Hashtable;
import okhttp3.internal.http.StatusLine;
import org.apache.http.HttpStatus;

public final class Code93Reader extends OneDReader {
    private static final char[] ALPHABET = ALPHABET_STRING.toCharArray();
    private static final String ALPHABET_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*";
    private static final int ASTERISK_ENCODING = CHARACTER_ENCODINGS[47];
    private static final int[] CHARACTER_ENCODINGS = new int[]{276, 328, 324, 322, 296, 292, 290, 336, 274, 266, HttpStatus.SC_FAILED_DEPENDENCY, 420, 418, HttpStatus.SC_NOT_FOUND, HttpStatus.SC_PAYMENT_REQUIRED, 394, 360, 356, 354, StatusLine.HTTP_PERM_REDIRECT, 282, 344, 332, 326, HttpStatus.SC_MULTIPLE_CHOICES, 278, 436, 434, 428, HttpStatus.SC_UNPROCESSABLE_ENTITY, HttpStatus.SC_NOT_ACCEPTABLE, HttpStatus.SC_GONE, 364, 358, 310, 314, HttpStatus.SC_MOVED_TEMPORARILY, 468, 466, 458, 366, 374, 430, 294, 474, 470, 306, 350};

    public Result decodeRow(int rowNumber, BitArray row, Hashtable hints) throws NotFoundException, ChecksumException, FormatException {
        int[] start = findAsteriskPattern(row);
        int nextStart = start[1];
        int end = row.getSize();
        while (nextStart < end && !row.get(nextStart)) {
            nextStart++;
        }
        StringBuffer result = new StringBuffer(20);
        int[] counters = new int[6];
        char decodedChar;
        do {
            OneDReader.recordPattern(row, nextStart, counters);
            int pattern = toPattern(counters);
            if (pattern < 0) {
                throw NotFoundException.getNotFoundInstance();
            }
            decodedChar = patternToChar(pattern);
            result.append(decodedChar);
            int lastStart = nextStart;
            for (int i : counters) {
                nextStart += i;
            }
            while (nextStart < end && !row.get(nextStart)) {
                nextStart++;
            }
        } while (decodedChar != '*');
        result.deleteCharAt(result.length() - 1);
        if (nextStart == end || !row.get(nextStart)) {
            throw NotFoundException.getNotFoundInstance();
        } else if (result.length() < 2) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            checkChecksums(result);
            result.setLength(result.length() - 2);
            float left = ((float) (start[1] + start[0])) / 2.0f;
            float right = ((float) (nextStart + lastStart)) / 2.0f;
            return new Result(decodeExtended(result), null, new ResultPoint[]{new ResultPoint(left, (float) rowNumber), new ResultPoint(right, (float) rowNumber)}, BarcodeFormat.CODE_93);
        }
    }

    private static int[] findAsteriskPattern(BitArray row) throws NotFoundException {
        int width = row.getSize();
        int rowOffset = 0;
        while (rowOffset < width && !row.get(rowOffset)) {
            rowOffset++;
        }
        int counterPosition = 0;
        int[] counters = new int[6];
        int patternStart = rowOffset;
        boolean isWhite = false;
        int patternLength = counters.length;
        for (int i = rowOffset; i < width; i++) {
            if ((row.get(i) ^ isWhite) != 0) {
                counters[counterPosition] = counters[counterPosition] + 1;
            } else {
                if (counterPosition != patternLength - 1) {
                    counterPosition++;
                } else if (toPattern(counters) == ASTERISK_ENCODING) {
                    return new int[]{patternStart, i};
                } else {
                    patternStart += counters[0] + counters[1];
                    for (int y = 2; y < patternLength; y++) {
                        counters[y - 2] = counters[y];
                    }
                    counters[patternLength - 2] = 0;
                    counters[patternLength - 1] = 0;
                    counterPosition--;
                }
                counters[counterPosition] = 1;
                if (isWhite) {
                    isWhite = false;
                } else {
                    isWhite = true;
                }
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static int toPattern(int[] counters) {
        int i;
        int sum = 0;
        for (int i2 : counters) {
            sum += i2;
        }
        int pattern = 0;
        for (i = 0; i < max; i++) {
            int scaledShifted = ((counters[i] << 8) * 9) / sum;
            int scaledUnshifted = scaledShifted >> 8;
            if ((scaledShifted & 255) > TransportMediator.KEYCODE_MEDIA_PAUSE) {
                scaledUnshifted++;
            }
            if (scaledUnshifted < 1 || scaledUnshifted > 4) {
                return -1;
            }
            if ((i & 1) == 0) {
                for (int j = 0; j < scaledUnshifted; j++) {
                    pattern = (pattern << 1) | 1;
                }
            } else {
                pattern <<= scaledUnshifted;
            }
        }
        return pattern;
    }

    private static char patternToChar(int pattern) throws NotFoundException {
        for (int i = 0; i < CHARACTER_ENCODINGS.length; i++) {
            if (CHARACTER_ENCODINGS[i] == pattern) {
                return ALPHABET[i];
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static String decodeExtended(StringBuffer encoded) throws FormatException {
        int length = encoded.length();
        StringBuffer decoded = new StringBuffer(length);
        int i = 0;
        while (i < length) {
            char c = encoded.charAt(i);
            if (c < 'a' || c > 'd') {
                decoded.append(c);
            } else {
                char next = encoded.charAt(i + 1);
                char decodedChar = '\u0000';
                switch (c) {
                    case 'a':
                        if (next >= 'A' && next <= 'Z') {
                            decodedChar = (char) (next - 64);
                            break;
                        }
                        throw FormatException.getFormatInstance();
                        break;
                    case 'b':
                        if (next < 'A' || next > 'E') {
                            if (next >= 'F' && next <= 'W') {
                                decodedChar = (char) (next - 11);
                                break;
                            }
                            throw FormatException.getFormatInstance();
                        }
                        decodedChar = (char) (next - 38);
                        break;
                        break;
                    case 'c':
                        if (next >= 'A' && next <= 'O') {
                            decodedChar = (char) (next - 32);
                            break;
                        } else if (next == 'Z') {
                            decodedChar = ':';
                            break;
                        } else {
                            throw FormatException.getFormatInstance();
                        }
                        break;
                    case 'd':
                        if (next >= 'A' && next <= 'Z') {
                            decodedChar = (char) (next + 32);
                            break;
                        }
                        throw FormatException.getFormatInstance();
                }
                decoded.append(decodedChar);
                i++;
            }
            i++;
        }
        return decoded.toString();
    }

    private static void checkChecksums(StringBuffer result) throws ChecksumException {
        int length = result.length();
        checkOneChecksum(result, length - 2, 20);
        checkOneChecksum(result, length - 1, 15);
    }

    private static void checkOneChecksum(StringBuffer result, int checkPosition, int weightMax) throws ChecksumException {
        int weight = 1;
        int total = 0;
        for (int i = checkPosition - 1; i >= 0; i--) {
            total += ALPHABET_STRING.indexOf(result.charAt(i)) * weight;
            weight++;
            if (weight > weightMax) {
                weight = 1;
            }
        }
        if (result.charAt(checkPosition) != ALPHABET[total % 47]) {
            throw ChecksumException.getChecksumInstance();
        }
    }
}
