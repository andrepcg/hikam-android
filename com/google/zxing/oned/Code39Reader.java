package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.Hashtable;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.http.HttpStatus;

public final class Code39Reader extends OneDReader {
    private static final char[] ALPHABET = ALPHABET_STRING.toCharArray();
    static final String ALPHABET_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%";
    private static final int ASTERISK_ENCODING = CHARACTER_ENCODINGS[39];
    static final int[] CHARACTER_ENCODINGS = new int[]{52, 289, 97, 352, 49, HttpStatus.SC_NOT_MODIFIED, 112, 37, 292, 100, 265, 73, 328, 25, 280, 88, 13, 268, 76, 28, 259, 67, 322, 19, 274, 82, 7, 262, 70, 22, 385, 193, 448, 145, HttpStatus.SC_BAD_REQUEST, 208, 133, 388, 196, TarConstants.CHKSUM_OFFSET, 168, 162, 138, 42};
    private final boolean extendedMode;
    private final boolean usingCheckDigit;

    public Code39Reader() {
        this.usingCheckDigit = false;
        this.extendedMode = false;
    }

    public Code39Reader(boolean usingCheckDigit) {
        this.usingCheckDigit = usingCheckDigit;
        this.extendedMode = false;
    }

    public Code39Reader(boolean usingCheckDigit, boolean extendedMode) {
        this.usingCheckDigit = usingCheckDigit;
        this.extendedMode = extendedMode;
    }

    public Result decodeRow(int rowNumber, BitArray row, Hashtable hints) throws NotFoundException, ChecksumException, FormatException {
        int lastStart;
        int[] start = findAsteriskPattern(row);
        int nextStart = start[1];
        int end = row.getSize();
        while (nextStart < end && !row.get(nextStart)) {
            nextStart++;
        }
        StringBuffer result = new StringBuffer(20);
        int[] counters = new int[9];
        char decodedChar;
        do {
            OneDReader.recordPattern(row, nextStart, counters);
            int pattern = toNarrowWidePattern(counters);
            if (pattern < 0) {
                throw NotFoundException.getNotFoundInstance();
            }
            int i;
            decodedChar = patternToChar(pattern);
            result.append(decodedChar);
            lastStart = nextStart;
            for (int i2 : counters) {
                nextStart += i2;
            }
            while (nextStart < end && !row.get(nextStart)) {
                nextStart++;
            }
        } while (decodedChar != '*');
        result.deleteCharAt(result.length() - 1);
        int lastPatternSize = 0;
        for (int i22 : counters) {
            lastPatternSize += i22;
        }
        int whiteSpaceAfterEnd = (nextStart - lastStart) - lastPatternSize;
        if (nextStart == end || whiteSpaceAfterEnd / 2 >= lastPatternSize) {
            if (this.usingCheckDigit) {
                int max = result.length() - 1;
                int total = 0;
                for (i = 0; i < max; i++) {
                    total += ALPHABET_STRING.indexOf(result.charAt(i));
                }
                if (result.charAt(max) != ALPHABET[total % 43]) {
                    throw ChecksumException.getChecksumInstance();
                }
                result.deleteCharAt(max);
            }
            if (result.length() == 0) {
                throw NotFoundException.getNotFoundInstance();
            }
            String resultString;
            if (this.extendedMode) {
                resultString = decodeExtended(result);
            } else {
                resultString = result.toString();
            }
            float left = ((float) (start[1] + start[0])) / 2.0f;
            float right = ((float) (nextStart + lastStart)) / 2.0f;
            return new Result(resultString, null, new ResultPoint[]{new ResultPoint(left, (float) rowNumber), new ResultPoint(right, (float) rowNumber)}, BarcodeFormat.CODE_39);
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static int[] findAsteriskPattern(BitArray row) throws NotFoundException {
        int width = row.getSize();
        int rowOffset = 0;
        while (rowOffset < width && !row.get(rowOffset)) {
            rowOffset++;
        }
        int counterPosition = 0;
        int[] counters = new int[9];
        int patternStart = rowOffset;
        boolean isWhite = false;
        int patternLength = counters.length;
        int i = rowOffset;
        while (i < width) {
            if ((row.get(i) ^ isWhite) != 0) {
                counters[counterPosition] = counters[counterPosition] + 1;
            } else {
                if (counterPosition != patternLength - 1) {
                    counterPosition++;
                } else if (toNarrowWidePattern(counters) == ASTERISK_ENCODING && row.isRange(Math.max(0, patternStart - ((i - patternStart) / 2)), patternStart, false)) {
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
            i++;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static int toNarrowWidePattern(int[] counters) {
        int maxNarrowCounter = 0;
        int wideCounters;
        do {
            int i;
            int counter;
            int minCounter = Integer.MAX_VALUE;
            for (int counter2 : counters) {
                if (counter2 < minCounter && counter2 > maxNarrowCounter) {
                    minCounter = counter2;
                }
            }
            maxNarrowCounter = minCounter;
            wideCounters = 0;
            int totalWideCountersWidth = 0;
            int pattern = 0;
            for (i = 0; i < numCounters; i++) {
                counter2 = counters[i];
                if (counters[i] > maxNarrowCounter) {
                    pattern |= 1 << ((numCounters - 1) - i);
                    wideCounters++;
                    totalWideCountersWidth += counter2;
                }
            }
            if (wideCounters == 3) {
                for (i = 0; i < numCounters && wideCounters > 0; i++) {
                    counter2 = counters[i];
                    if (counters[i] > maxNarrowCounter) {
                        wideCounters--;
                        if ((counter2 << 1) >= totalWideCountersWidth) {
                            return -1;
                        }
                    }
                }
                return pattern;
            }
        } while (wideCounters > 3);
        return -1;
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
            if (c == '+' || c == '$' || c == '%' || c == '/') {
                char next = encoded.charAt(i + 1);
                char decodedChar = '\u0000';
                switch (c) {
                    case '$':
                        if (next >= 'A' && next <= 'Z') {
                            decodedChar = (char) (next - 64);
                            break;
                        }
                        throw FormatException.getFormatInstance();
                        break;
                    case '%':
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
                    case '+':
                        if (next >= 'A' && next <= 'Z') {
                            decodedChar = (char) (next + 32);
                            break;
                        }
                        throw FormatException.getFormatInstance();
                        break;
                    case '/':
                        if (next >= 'A' && next <= 'O') {
                            decodedChar = (char) (next - 32);
                            break;
                        } else if (next == 'Z') {
                            decodedChar = ':';
                            break;
                        } else {
                            throw FormatException.getFormatInstance();
                        }
                }
                decoded.append(decodedChar);
                i++;
            } else {
                decoded.append(c);
            }
            i++;
        }
        return decoded.toString();
    }
}
