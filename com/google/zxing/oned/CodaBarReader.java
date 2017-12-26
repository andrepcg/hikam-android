package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.Hashtable;

public final class CodaBarReader extends OneDReader {
    private static final char[] ALPHABET = ALPHABET_STRING.toCharArray();
    private static final String ALPHABET_STRING = "0123456789-$:/.+ABCDTN";
    private static final int[] CHARACTER_ENCODINGS = new int[]{3, 6, 9, 96, 18, 66, 33, 36, 48, 72, 12, 24, 37, 81, 84, 21, 26, 41, 11, 14, 26, 41};
    private static final char[] STARTEND_ENCODING = new char[]{'E', '*', 'A', 'B', 'C', 'D', 'T', 'N'};
    private static final int minCharacterLength = 6;

    public Result decodeRow(int rowNumber, BitArray row, Hashtable hints) throws NotFoundException {
        int lastStart;
        int[] start = findAsteriskPattern(row);
        start[1] = 0;
        int nextStart = start[1];
        int end = row.getSize();
        while (nextStart < end && !row.get(nextStart)) {
            nextStart++;
        }
        StringBuffer result = new StringBuffer();
        do {
            int i = 7;
            int[] counters = new int[]{0, 0, 0, 0, 0, 0, 0};
            OneDReader.recordPattern(row, nextStart, counters);
            char decodedChar = toNarrowWidePattern(counters);
            if (decodedChar == '!') {
                throw NotFoundException.getNotFoundInstance();
            }
            result.append(decodedChar);
            lastStart = nextStart;
            for (int i2 : counters) {
                nextStart += i2;
            }
            while (nextStart < end && !row.get(nextStart)) {
                nextStart++;
            }
        } while (nextStart < end);
        int lastPatternSize = 0;
        for (int i22 : counters) {
            lastPatternSize += i22;
        }
        int whiteSpaceAfterEnd = (nextStart - lastStart) - lastPatternSize;
        if (nextStart != end && whiteSpaceAfterEnd / 2 < lastPatternSize) {
            throw NotFoundException.getNotFoundInstance();
        } else if (result.length() < 2) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            char startchar = result.charAt(0);
            if (arrayContains(STARTEND_ENCODING, startchar)) {
                int k = 1;
                while (k < result.length()) {
                    if (result.charAt(k) == startchar && k + 1 != result.length()) {
                        result.delete(k + 1, result.length() - 1);
                        k = result.length();
                    }
                    k++;
                }
                if (result.length() > 6) {
                    result.deleteCharAt(result.length() - 1);
                    result.deleteCharAt(0);
                    float left = ((float) (start[1] + start[0])) / 2.0f;
                    float right = ((float) (nextStart + lastStart)) / 2.0f;
                    return new Result(result.toString(), null, new ResultPoint[]{new ResultPoint(left, (float) rowNumber), new ResultPoint(right, (float) rowNumber)}, BarcodeFormat.CODABAR);
                }
                throw NotFoundException.getNotFoundInstance();
            }
            throw NotFoundException.getNotFoundInstance();
        }
    }

    private static int[] findAsteriskPattern(BitArray row) throws NotFoundException {
        int width = row.getSize();
        int rowOffset = 0;
        while (rowOffset < width && !row.get(rowOffset)) {
            rowOffset++;
        }
        int counterPosition = 0;
        int[] counters = new int[7];
        int patternStart = rowOffset;
        boolean isWhite = false;
        int patternLength = counters.length;
        int i = rowOffset;
        while (i < width) {
            if ((row.get(i) ^ isWhite) != 0) {
                counters[counterPosition] = counters[counterPosition] + 1;
            } else {
                if (counterPosition == patternLength - 1) {
                    try {
                        if (arrayContains(STARTEND_ENCODING, toNarrowWidePattern(counters)) && row.isRange(Math.max(0, patternStart - ((i - patternStart) / 2)), patternStart, false)) {
                            return new int[]{patternStart, i};
                        }
                    } catch (IllegalArgumentException e) {
                    }
                    patternStart += counters[0] + counters[1];
                    for (int y = 2; y < patternLength; y++) {
                        counters[y - 2] = counters[y];
                    }
                    counters[patternLength - 2] = 0;
                    counters[patternLength - 1] = 0;
                    counterPosition--;
                } else {
                    counterPosition++;
                }
                counters[counterPosition] = 1;
                isWhite ^= 1;
            }
            i++;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static boolean arrayContains(char[] array, char key) {
        if (array != null) {
            for (char c : array) {
                if (c == key) {
                    return true;
                }
            }
        }
        return false;
    }

    private static char toNarrowWidePattern(int[] counters) {
        int i;
        int numCounters = counters.length;
        int maxNarrowCounter = 0;
        int minCounter = Integer.MAX_VALUE;
        for (i = 0; i < numCounters; i++) {
            if (counters[i] < minCounter) {
                minCounter = counters[i];
            }
            if (counters[i] > maxNarrowCounter) {
                maxNarrowCounter = counters[i];
            }
        }
        do {
            int wideCounters = 0;
            int pattern = 0;
            for (i = 0; i < numCounters; i++) {
                if (counters[i] > maxNarrowCounter) {
                    pattern |= 1 << ((numCounters - 1) - i);
                    wideCounters++;
                }
            }
            if (wideCounters == 2 || wideCounters == 3) {
                for (i = 0; i < CHARACTER_ENCODINGS.length; i++) {
                    if (CHARACTER_ENCODINGS[i] == pattern) {
                        return ALPHABET[i];
                    }
                }
            }
            maxNarrowCounter--;
        } while (maxNarrowCounter > minCounter);
        return '!';
    }
}
