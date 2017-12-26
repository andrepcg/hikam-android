package com.google.zxing.pdf417.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.common.DecoderResult;
import org.apache.commons.compress.archivers.tar.TarConstants;

final class DecodedBitStreamParser {
    private static final int AL = 28;
    private static final int ALPHA = 0;
    private static final int AS = 27;
    private static final int BEGIN_MACRO_PDF417_CONTROL_BLOCK = 928;
    private static final int BEGIN_MACRO_PDF417_OPTIONAL_FIELD = 923;
    private static final int BYTE_COMPACTION_MODE_LATCH = 901;
    private static final int BYTE_COMPACTION_MODE_LATCH_6 = 924;
    private static final String[] EXP900 = new String[]{"000000000000000000000000000000000000000000001", "000000000000000000000000000000000000000000900", "000000000000000000000000000000000000000810000", "000000000000000000000000000000000000729000000", "000000000000000000000000000000000656100000000", "000000000000000000000000000000590490000000000", "000000000000000000000000000531441000000000000", "000000000000000000000000478296900000000000000", "000000000000000000000430467210000000000000000", "000000000000000000387420489000000000000000000", "000000000000000348678440100000000000000000000", "000000000000313810596090000000000000000000000", "000000000282429536481000000000000000000000000", "000000254186582832900000000000000000000000000", "000228767924549610000000000000000000000000000", "205891132094649000000000000000000000000000000"};
    private static final int LL = 27;
    private static final int LOWER = 1;
    private static final int MACRO_PDF417_TERMINATOR = 922;
    private static final int MAX_NUMERIC_CODEWORDS = 15;
    private static final int MIXED = 2;
    private static final char[] MIXED_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '&', '\r', '\t', ',', ':', '#', '-', '.', '$', '/', '+', '%', '*', '=', '^'};
    private static final int ML = 28;
    private static final int MODE_SHIFT_TO_BYTE_COMPACTION_MODE = 913;
    private static final int NUMERIC_COMPACTION_MODE_LATCH = 902;
    private static final int PAL = 29;
    private static final int PL = 25;
    private static final int PS = 29;
    private static final int PUNCT = 3;
    private static final char[] PUNCT_CHARS = new char[]{';', '<', '>', '@', '[', '\\', '}', '_', '`', '~', '!', '\r', '\t', ',', ':', '\n', '-', '.', '$', '/', '\"', '|', '*', '(', ')', '?', '{', '}', '\''};
    private static final int PUNCT_SHIFT = 4;
    private static final int TEXT_COMPACTION_MODE_LATCH = 900;

    private DecodedBitStreamParser() {
    }

    static DecoderResult decode(int[] codewords) throws FormatException {
        StringBuffer result = new StringBuffer(100);
        int codeIndex = 1 + 1;
        int code = codewords[1];
        int codeIndex2 = codeIndex;
        while (codeIndex2 < codewords[0]) {
            switch (code) {
                case TEXT_COMPACTION_MODE_LATCH /*900*/:
                    codeIndex2 = textCompaction(codewords, codeIndex2, result);
                    break;
                case BYTE_COMPACTION_MODE_LATCH /*901*/:
                    codeIndex2 = byteCompaction(code, codewords, codeIndex2, result);
                    break;
                case NUMERIC_COMPACTION_MODE_LATCH /*902*/:
                    codeIndex2 = numericCompaction(codewords, codeIndex2, result);
                    break;
                case MODE_SHIFT_TO_BYTE_COMPACTION_MODE /*913*/:
                    codeIndex2 = byteCompaction(code, codewords, codeIndex2, result);
                    break;
                case BYTE_COMPACTION_MODE_LATCH_6 /*924*/:
                    codeIndex2 = byteCompaction(code, codewords, codeIndex2, result);
                    break;
                default:
                    codeIndex2 = textCompaction(codewords, codeIndex2 - 1, result);
                    break;
            }
            if (codeIndex2 < codewords.length) {
                codeIndex = codeIndex2 + 1;
                code = codewords[codeIndex2];
                codeIndex2 = codeIndex;
            } else {
                throw FormatException.getFormatInstance();
            }
        }
        return new DecoderResult(null, result.toString(), null, null);
    }

    private static int textCompaction(int[] codewords, int codeIndex, StringBuffer result) {
        int[] textCompactionData = new int[(codewords[0] << 1)];
        int[] byteCompactionData = new int[(codewords[0] << 1)];
        int index = 0;
        boolean end = false;
        while (codeIndex < codewords[0] && !end) {
            int codeIndex2 = codeIndex + 1;
            int code = codewords[codeIndex];
            if (code >= TEXT_COMPACTION_MODE_LATCH) {
                switch (code) {
                    case TEXT_COMPACTION_MODE_LATCH /*900*/:
                        codeIndex = codeIndex2 - 1;
                        end = true;
                        break;
                    case BYTE_COMPACTION_MODE_LATCH /*901*/:
                        codeIndex = codeIndex2 - 1;
                        end = true;
                        break;
                    case NUMERIC_COMPACTION_MODE_LATCH /*902*/:
                        codeIndex = codeIndex2 - 1;
                        end = true;
                        break;
                    case MODE_SHIFT_TO_BYTE_COMPACTION_MODE /*913*/:
                        textCompactionData[index] = MODE_SHIFT_TO_BYTE_COMPACTION_MODE;
                        byteCompactionData[index] = code;
                        index++;
                        codeIndex = codeIndex2;
                        break;
                    case BYTE_COMPACTION_MODE_LATCH_6 /*924*/:
                        codeIndex = codeIndex2 - 1;
                        end = true;
                        break;
                    default:
                        codeIndex = codeIndex2;
                        break;
                }
            }
            textCompactionData[index] = code / 30;
            textCompactionData[index + 1] = code % 30;
            index += 2;
            codeIndex = codeIndex2;
        }
        decodeTextCompaction(textCompactionData, byteCompactionData, index, result);
        return codeIndex;
    }

    private static void decodeTextCompaction(int[] textCompactionData, int[] byteCompactionData, int length, StringBuffer result) {
        int subMode = 0;
        int priorToShiftMode = 0;
        for (int i = 0; i < length; i++) {
            int subModeCh = textCompactionData[i];
            char ch = '\u0000';
            switch (subMode) {
                case 0:
                    if (subModeCh >= 26) {
                        if (subModeCh != 26) {
                            if (subModeCh != 27) {
                                if (subModeCh != 28) {
                                    if (subModeCh != 29) {
                                        if (subModeCh == MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                                            result.append((char) byteCompactionData[i]);
                                            break;
                                        }
                                    }
                                    priorToShiftMode = subMode;
                                    subMode = 4;
                                    break;
                                }
                                subMode = 2;
                                break;
                            }
                            subMode = 1;
                            break;
                        }
                        ch = ' ';
                        break;
                    }
                    ch = (char) (subModeCh + 65);
                    break;
                    break;
                case 1:
                    if (subModeCh >= 26) {
                        if (subModeCh != 26) {
                            if (subModeCh != 28) {
                                if (subModeCh != 28) {
                                    if (subModeCh != 29) {
                                        if (subModeCh == MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                                            result.append((char) byteCompactionData[i]);
                                            break;
                                        }
                                    }
                                    priorToShiftMode = subMode;
                                    subMode = 4;
                                    break;
                                }
                                subMode = 2;
                                break;
                            }
                            subMode = 0;
                            break;
                        }
                        ch = ' ';
                        break;
                    }
                    ch = (char) (subModeCh + 97);
                    break;
                    break;
                case 2:
                    if (subModeCh >= 25) {
                        if (subModeCh != 25) {
                            if (subModeCh != 26) {
                                if (subModeCh != 27) {
                                    if (subModeCh != 28) {
                                        if (subModeCh != 29) {
                                            if (subModeCh == MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                                                result.append((char) byteCompactionData[i]);
                                                break;
                                            }
                                        }
                                        priorToShiftMode = subMode;
                                        subMode = 4;
                                        break;
                                    }
                                    subMode = 0;
                                    break;
                                }
                            }
                            ch = ' ';
                            break;
                        }
                        subMode = 3;
                        break;
                    }
                    ch = MIXED_CHARS[subModeCh];
                    break;
                    break;
                case 3:
                    if (subModeCh >= 29) {
                        if (subModeCh != 29) {
                            if (subModeCh == MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                                result.append((char) byteCompactionData[i]);
                                break;
                            }
                        }
                        subMode = 0;
                        break;
                    }
                    ch = PUNCT_CHARS[subModeCh];
                    break;
                    break;
                case 4:
                    subMode = priorToShiftMode;
                    if (subModeCh >= 29) {
                        if (subModeCh == 29) {
                            subMode = 0;
                            break;
                        }
                    }
                    ch = PUNCT_CHARS[subModeCh];
                    break;
                    break;
            }
            if (ch != '\u0000') {
                result.append(ch);
            }
        }
    }

    private static int byteCompaction(int mode, int[] codewords, int codeIndex, StringBuffer result) {
        int count;
        long value;
        char[] decodedData;
        boolean end;
        int codeIndex2;
        int code;
        int j;
        if (mode == BYTE_COMPACTION_MODE_LATCH) {
            count = 0;
            value = 0;
            decodedData = new char[6];
            int[] byteCompactedCodewords = new int[6];
            end = false;
            while (codeIndex < codewords[0] && !end) {
                codeIndex2 = codeIndex + 1;
                code = codewords[codeIndex];
                if (code < TEXT_COMPACTION_MODE_LATCH) {
                    byteCompactedCodewords[count] = code;
                    count++;
                    value = (900 * value) + ((long) code);
                    codeIndex = codeIndex2;
                } else if (code == TEXT_COMPACTION_MODE_LATCH || code == BYTE_COMPACTION_MODE_LATCH || code == NUMERIC_COMPACTION_MODE_LATCH || code == BYTE_COMPACTION_MODE_LATCH_6 || code == BEGIN_MACRO_PDF417_CONTROL_BLOCK || code == BEGIN_MACRO_PDF417_OPTIONAL_FIELD || code == MACRO_PDF417_TERMINATOR) {
                    codeIndex = codeIndex2 - 1;
                    end = true;
                } else {
                    codeIndex = codeIndex2;
                }
                if (count % 5 == 0 && count > 0) {
                    for (j = 0; j < 6; j++) {
                        decodedData[5 - j] = (char) ((int) (value % 256));
                        value >>= 8;
                    }
                    result.append(decodedData);
                    count = 0;
                }
            }
            for (int i = (count / 5) * 5; i < count; i++) {
                result.append((char) byteCompactedCodewords[i]);
            }
        } else if (mode == BYTE_COMPACTION_MODE_LATCH_6) {
            count = 0;
            value = 0;
            end = false;
            while (codeIndex < codewords[0] && !end) {
                codeIndex2 = codeIndex + 1;
                code = codewords[codeIndex];
                if (code < TEXT_COMPACTION_MODE_LATCH) {
                    count++;
                    value = (900 * value) + ((long) code);
                    codeIndex = codeIndex2;
                } else if (code == TEXT_COMPACTION_MODE_LATCH || code == BYTE_COMPACTION_MODE_LATCH || code == NUMERIC_COMPACTION_MODE_LATCH || code == BYTE_COMPACTION_MODE_LATCH_6 || code == BEGIN_MACRO_PDF417_CONTROL_BLOCK || code == BEGIN_MACRO_PDF417_OPTIONAL_FIELD || code == MACRO_PDF417_TERMINATOR) {
                    codeIndex = codeIndex2 - 1;
                    end = true;
                } else {
                    codeIndex = codeIndex2;
                }
                if (count % 5 == 0 && count > 0) {
                    decodedData = new char[6];
                    for (j = 0; j < 6; j++) {
                        decodedData[5 - j] = (char) ((int) (255 & value));
                        value >>= 8;
                    }
                    result.append(decodedData);
                }
            }
        }
        return codeIndex;
    }

    private static int numericCompaction(int[] codewords, int codeIndex, StringBuffer result) {
        int count = 0;
        boolean end = false;
        int[] numericCodewords = new int[15];
        while (codeIndex < codewords[0] && !end) {
            int codeIndex2 = codeIndex + 1;
            int code = codewords[codeIndex];
            if (codeIndex2 == codewords[0]) {
                end = true;
            }
            if (code < TEXT_COMPACTION_MODE_LATCH) {
                numericCodewords[count] = code;
                count++;
                codeIndex = codeIndex2;
            } else if (code == TEXT_COMPACTION_MODE_LATCH || code == BYTE_COMPACTION_MODE_LATCH || code == BYTE_COMPACTION_MODE_LATCH_6 || code == BEGIN_MACRO_PDF417_CONTROL_BLOCK || code == BEGIN_MACRO_PDF417_OPTIONAL_FIELD || code == MACRO_PDF417_TERMINATOR) {
                codeIndex = codeIndex2 - 1;
                end = true;
            } else {
                codeIndex = codeIndex2;
            }
            if (count % 15 == 0 || code == NUMERIC_COMPACTION_MODE_LATCH || end) {
                result.append(decodeBase900toBase10(numericCodewords, count));
                count = 0;
            }
        }
        return codeIndex;
    }

    private static String decodeBase900toBase10(int[] codewords, int count) {
        int i;
        StringBuffer accum = null;
        for (i = 0; i < count; i++) {
            StringBuffer value = multiply(EXP900[(count - i) - 1], codewords[i]);
            if (accum == null) {
                accum = value;
            } else {
                accum = add(accum.toString(), value.toString());
            }
        }
        String result = null;
        for (i = 0; i < accum.length(); i++) {
            if (accum.charAt(i) == '1') {
                result = accum.toString().substring(i + 1);
                break;
            }
        }
        if (result == null) {
            return accum.toString();
        }
        return result;
    }

    private static StringBuffer multiply(String value1, int value2) {
        int j;
        StringBuffer result = new StringBuffer(value1.length());
        for (int i = 0; i < value1.length(); i++) {
            result.append('0');
        }
        int hundreds = value2 / 100;
        int tens = (value2 / 10) % 10;
        int ones = value2 % 10;
        for (j = 0; j < ones; j++) {
            result = add(result.toString(), value1);
        }
        for (j = 0; j < tens; j++) {
            result = add(result.toString(), new StringBuffer().append(value1).append('0').toString().substring(1));
        }
        for (j = 0; j < hundreds; j++) {
            result = add(result.toString(), new StringBuffer().append(value1).append(TarConstants.VERSION_POSIX).toString().substring(2));
        }
        return result;
    }

    private static StringBuffer add(String value1, String value2) {
        int i;
        StringBuffer temp1 = new StringBuffer(5);
        StringBuffer temp2 = new StringBuffer(5);
        StringBuffer result = new StringBuffer(value1.length());
        for (i = 0; i < value1.length(); i++) {
            result.append('0');
        }
        int carry = 0;
        for (i = value1.length() - 3; i > -1; i -= 3) {
            temp1.setLength(0);
            temp1.append(value1.charAt(i));
            temp1.append(value1.charAt(i + 1));
            temp1.append(value1.charAt(i + 2));
            temp2.setLength(0);
            temp2.append(value2.charAt(i));
            temp2.append(value2.charAt(i + 1));
            temp2.append(value2.charAt(i + 2));
            int intValue1 = Integer.parseInt(temp1.toString());
            int intValue2 = Integer.parseInt(temp2.toString());
            int sumval = ((intValue1 + intValue2) + carry) % 1000;
            carry = ((intValue1 + intValue2) + carry) / 1000;
            result.setCharAt(i + 2, (char) ((sumval % 10) + 48));
            result.setCharAt(i + 1, (char) (((sumval / 10) % 10) + 48));
            result.setCharAt(i, (char) ((sumval / 100) + 48));
        }
        return result;
    }
}
