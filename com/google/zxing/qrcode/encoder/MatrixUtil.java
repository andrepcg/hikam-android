package com.google.zxing.qrcode.encoder;

import android.support.v4.media.TransportMediator;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitArray;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public final class MatrixUtil {
    private static final int[][] HORIZONTAL_SEPARATION_PATTERN = new int[][]{new int[]{0, 0, 0, 0, 0, 0, 0, 0}};
    private static final int[][] POSITION_ADJUSTMENT_PATTERN = new int[][]{new int[]{1, 1, 1, 1, 1}, new int[]{1, 0, 0, 0, 1}, new int[]{1, 0, 1, 0, 1}, new int[]{1, 0, 0, 0, 1}, new int[]{1, 1, 1, 1, 1}};
    private static final int[][] POSITION_ADJUSTMENT_PATTERN_COORDINATE_TABLE = new int[][]{new int[]{-1, -1, -1, -1, -1, -1, -1}, new int[]{6, 18, -1, -1, -1, -1, -1}, new int[]{6, 22, -1, -1, -1, -1, -1}, new int[]{6, 26, -1, -1, -1, -1, -1}, new int[]{6, 30, -1, -1, -1, -1, -1}, new int[]{6, 34, -1, -1, -1, -1, -1}, new int[]{6, 22, 38, -1, -1, -1, -1}, new int[]{6, 24, 42, -1, -1, -1, -1}, new int[]{6, 26, 46, -1, -1, -1, -1}, new int[]{6, 28, 50, -1, -1, -1, -1}, new int[]{6, 30, 54, -1, -1, -1, -1}, new int[]{6, 32, 58, -1, -1, -1, -1}, new int[]{6, 34, 62, -1, -1, -1, -1}, new int[]{6, 26, 46, 66, -1, -1, -1}, new int[]{6, 26, 48, 70, -1, -1, -1}, new int[]{6, 26, 50, 74, -1, -1, -1}, new int[]{6, 30, 54, 78, -1, -1, -1}, new int[]{6, 30, 56, 82, -1, -1, -1}, new int[]{6, 30, 58, 86, -1, -1, -1}, new int[]{6, 34, 62, 90, -1, -1, -1}, new int[]{6, 28, 50, 72, 94, -1, -1}, new int[]{6, 26, 50, 74, 98, -1, -1}, new int[]{6, 30, 54, 78, 102, -1, -1}, new int[]{6, 28, 54, 80, 106, -1, -1}, new int[]{6, 32, 58, 84, 110, -1, -1}, new int[]{6, 30, 58, 86, 114, -1, -1}, new int[]{6, 34, 62, 90, 118, -1, -1}, new int[]{6, 26, 50, 74, 98, 122, -1}, new int[]{6, 30, 54, 78, 102, TransportMediator.KEYCODE_MEDIA_PLAY, -1}, new int[]{6, 26, 52, 78, 104, TransportMediator.KEYCODE_MEDIA_RECORD, -1}, new int[]{6, 30, 56, 82, 108, 134, -1}, new int[]{6, 34, 60, 86, 112, 138, -1}, new int[]{6, 30, 58, 86, 114, 142, -1}, new int[]{6, 34, 62, 90, 118, 146, -1}, new int[]{6, 30, 54, 78, 102, TransportMediator.KEYCODE_MEDIA_PLAY, 150}, new int[]{6, 24, 50, 76, 102, 128, 154}, new int[]{6, 28, 54, 80, 106, 132, 158}, new int[]{6, 32, 58, 84, 110, 136, 162}, new int[]{6, 26, 54, 82, 110, 138, 166}, new int[]{6, 30, 58, 86, 114, 142, 170}};
    private static final int[][] POSITION_DETECTION_PATTERN = new int[][]{new int[]{1, 1, 1, 1, 1, 1, 1}, new int[]{1, 0, 0, 0, 0, 0, 1}, new int[]{1, 0, 1, 1, 1, 0, 1}, new int[]{1, 0, 1, 1, 1, 0, 1}, new int[]{1, 0, 1, 1, 1, 0, 1}, new int[]{1, 0, 0, 0, 0, 0, 1}, new int[]{1, 1, 1, 1, 1, 1, 1}};
    private static final int[][] TYPE_INFO_COORDINATES = new int[][]{new int[]{8, 0}, new int[]{8, 1}, new int[]{8, 2}, new int[]{8, 3}, new int[]{8, 4}, new int[]{8, 5}, new int[]{8, 7}, new int[]{8, 8}, new int[]{7, 8}, new int[]{5, 8}, new int[]{4, 8}, new int[]{3, 8}, new int[]{2, 8}, new int[]{1, 8}, new int[]{0, 8}};
    private static final int TYPE_INFO_MASK_PATTERN = 21522;
    private static final int TYPE_INFO_POLY = 1335;
    private static final int VERSION_INFO_POLY = 7973;
    private static final int[][] VERTICAL_SEPARATION_PATTERN;

    private MatrixUtil() {
    }

    static {
        int[][] iArr = new int[7][];
        iArr[0] = new int[]{0};
        iArr[1] = new int[]{0};
        iArr[2] = new int[]{0};
        iArr[3] = new int[]{0};
        iArr[4] = new int[]{0};
        iArr[5] = new int[]{0};
        iArr[6] = new int[]{0};
        VERTICAL_SEPARATION_PATTERN = iArr;
    }

    public static void clearMatrix(ByteMatrix matrix) {
        matrix.clear((byte) -1);
    }

    public static void buildMatrix(BitArray dataBits, ErrorCorrectionLevel ecLevel, int version, int maskPattern, ByteMatrix matrix) throws WriterException {
        clearMatrix(matrix);
        embedBasicPatterns(version, matrix);
        embedTypeInfo(ecLevel, maskPattern, matrix);
        maybeEmbedVersionInfo(version, matrix);
        embedDataBits(dataBits, maskPattern, matrix);
    }

    public static void embedBasicPatterns(int version, ByteMatrix matrix) throws WriterException {
        embedPositionDetectionPatternsAndSeparators(matrix);
        embedDarkDotAtLeftBottomCorner(matrix);
        maybeEmbedPositionAdjustmentPatterns(version, matrix);
        embedTimingPatterns(matrix);
    }

    public static void embedTypeInfo(ErrorCorrectionLevel ecLevel, int maskPattern, ByteMatrix matrix) throws WriterException {
        BitArray typeInfoBits = new BitArray();
        makeTypeInfoBits(ecLevel, maskPattern, typeInfoBits);
        for (int i = 0; i < typeInfoBits.getSize(); i++) {
            boolean bit = typeInfoBits.get((typeInfoBits.getSize() - 1) - i);
            matrix.set(TYPE_INFO_COORDINATES[i][0], TYPE_INFO_COORDINATES[i][1], bit);
            if (i < 8) {
                matrix.set((matrix.getWidth() - i) - 1, 8, bit);
            } else {
                matrix.set(8, (matrix.getHeight() - 7) + (i - 8), bit);
            }
        }
    }

    public static void maybeEmbedVersionInfo(int version, ByteMatrix matrix) throws WriterException {
        if (version >= 7) {
            BitArray versionInfoBits = new BitArray();
            makeVersionInfoBits(version, versionInfoBits);
            int bitIndex = 17;
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 3; j++) {
                    boolean bit = versionInfoBits.get(bitIndex);
                    bitIndex--;
                    matrix.set(i, (matrix.getHeight() - 11) + j, bit);
                    matrix.set((matrix.getHeight() - 11) + j, i, bit);
                }
            }
        }
    }

    public static void embedDataBits(BitArray dataBits, int maskPattern, ByteMatrix matrix) throws WriterException {
        int bitIndex = 0;
        int direction = -1;
        int x = matrix.getWidth() - 1;
        int y = matrix.getHeight() - 1;
        while (x > 0) {
            if (x == 6) {
                x--;
            }
            while (y >= 0 && y < matrix.getHeight()) {
                for (int i = 0; i < 2; i++) {
                    int xx = x - i;
                    if (isEmpty(matrix.get(xx, y))) {
                        boolean bit;
                        if (bitIndex < dataBits.getSize()) {
                            bit = dataBits.get(bitIndex);
                            bitIndex++;
                        } else {
                            bit = false;
                        }
                        if (maskPattern != -1 && MaskUtil.getDataMaskBit(maskPattern, xx, y)) {
                            bit = !bit;
                        }
                        matrix.set(xx, y, bit);
                    }
                }
                y += direction;
            }
            direction = -direction;
            y += direction;
            x -= 2;
        }
        if (bitIndex != dataBits.getSize()) {
            throw new WriterException(new StringBuffer().append("Not all bits consumed: ").append(bitIndex).append('/').append(dataBits.getSize()).toString());
        }
    }

    public static int findMSBSet(int value) {
        int numDigits = 0;
        while (value != 0) {
            value >>>= 1;
            numDigits++;
        }
        return numDigits;
    }

    public static int calculateBCHCode(int value, int poly) {
        int msbSetInPoly = findMSBSet(poly);
        value <<= msbSetInPoly - 1;
        while (findMSBSet(value) >= msbSetInPoly) {
            value ^= poly << (findMSBSet(value) - msbSetInPoly);
        }
        return value;
    }

    public static void makeTypeInfoBits(ErrorCorrectionLevel ecLevel, int maskPattern, BitArray bits) throws WriterException {
        if (QRCode.isValidMaskPattern(maskPattern)) {
            int typeInfo = (ecLevel.getBits() << 3) | maskPattern;
            bits.appendBits(typeInfo, 5);
            bits.appendBits(calculateBCHCode(typeInfo, TYPE_INFO_POLY), 10);
            BitArray maskBits = new BitArray();
            maskBits.appendBits(TYPE_INFO_MASK_PATTERN, 15);
            bits.xor(maskBits);
            if (bits.getSize() != 15) {
                throw new WriterException(new StringBuffer().append("should not happen but we got: ").append(bits.getSize()).toString());
            }
            return;
        }
        throw new WriterException("Invalid mask pattern");
    }

    public static void makeVersionInfoBits(int version, BitArray bits) throws WriterException {
        bits.appendBits(version, 6);
        bits.appendBits(calculateBCHCode(version, VERSION_INFO_POLY), 12);
        if (bits.getSize() != 18) {
            throw new WriterException(new StringBuffer().append("should not happen but we got: ").append(bits.getSize()).toString());
        }
    }

    private static boolean isEmpty(int value) {
        return value == -1;
    }

    private static boolean isValidValue(int value) {
        return value == -1 || value == 0 || value == 1;
    }

    private static void embedTimingPatterns(ByteMatrix matrix) throws WriterException {
        int i = 8;
        while (i < matrix.getWidth() - 8) {
            int bit = (i + 1) % 2;
            if (isValidValue(matrix.get(i, 6))) {
                if (isEmpty(matrix.get(i, 6))) {
                    matrix.set(i, 6, bit);
                }
                if (isValidValue(matrix.get(6, i))) {
                    if (isEmpty(matrix.get(6, i))) {
                        matrix.set(6, i, bit);
                    }
                    i++;
                } else {
                    throw new WriterException();
                }
            }
            throw new WriterException();
        }
    }

    private static void embedDarkDotAtLeftBottomCorner(ByteMatrix matrix) throws WriterException {
        if (matrix.get(8, matrix.getHeight() - 8) == (byte) 0) {
            throw new WriterException();
        }
        matrix.set(8, matrix.getHeight() - 8, 1);
    }

    private static void embedHorizontalSeparationPattern(int xStart, int yStart, ByteMatrix matrix) throws WriterException {
        if (HORIZONTAL_SEPARATION_PATTERN[0].length == 8 && HORIZONTAL_SEPARATION_PATTERN.length == 1) {
            int x = 0;
            while (x < 8) {
                if (isEmpty(matrix.get(xStart + x, yStart))) {
                    matrix.set(xStart + x, yStart, HORIZONTAL_SEPARATION_PATTERN[0][x]);
                    x++;
                } else {
                    throw new WriterException();
                }
            }
            return;
        }
        throw new WriterException("Bad horizontal separation pattern");
    }

    private static void embedVerticalSeparationPattern(int xStart, int yStart, ByteMatrix matrix) throws WriterException {
        if (VERTICAL_SEPARATION_PATTERN[0].length == 1 && VERTICAL_SEPARATION_PATTERN.length == 7) {
            int y = 0;
            while (y < 7) {
                if (isEmpty(matrix.get(xStart, yStart + y))) {
                    matrix.set(xStart, yStart + y, VERTICAL_SEPARATION_PATTERN[y][0]);
                    y++;
                } else {
                    throw new WriterException();
                }
            }
            return;
        }
        throw new WriterException("Bad vertical separation pattern");
    }

    private static void embedPositionAdjustmentPattern(int xStart, int yStart, ByteMatrix matrix) throws WriterException {
        if (POSITION_ADJUSTMENT_PATTERN[0].length == 5 && POSITION_ADJUSTMENT_PATTERN.length == 5) {
            for (int y = 0; y < 5; y++) {
                int x = 0;
                while (x < 5) {
                    if (isEmpty(matrix.get(xStart + x, yStart + y))) {
                        matrix.set(xStart + x, yStart + y, POSITION_ADJUSTMENT_PATTERN[y][x]);
                        x++;
                    } else {
                        throw new WriterException();
                    }
                }
            }
            return;
        }
        throw new WriterException("Bad position adjustment");
    }

    private static void embedPositionDetectionPattern(int xStart, int yStart, ByteMatrix matrix) throws WriterException {
        if (POSITION_DETECTION_PATTERN[0].length == 7 && POSITION_DETECTION_PATTERN.length == 7) {
            for (int y = 0; y < 7; y++) {
                int x = 0;
                while (x < 7) {
                    if (isEmpty(matrix.get(xStart + x, yStart + y))) {
                        matrix.set(xStart + x, yStart + y, POSITION_DETECTION_PATTERN[y][x]);
                        x++;
                    } else {
                        throw new WriterException();
                    }
                }
            }
            return;
        }
        throw new WriterException("Bad position detection pattern");
    }

    private static void embedPositionDetectionPatternsAndSeparators(ByteMatrix matrix) throws WriterException {
        int pdpWidth = POSITION_DETECTION_PATTERN[0].length;
        embedPositionDetectionPattern(0, 0, matrix);
        embedPositionDetectionPattern(matrix.getWidth() - pdpWidth, 0, matrix);
        embedPositionDetectionPattern(0, matrix.getWidth() - pdpWidth, matrix);
        int hspWidth = HORIZONTAL_SEPARATION_PATTERN[0].length;
        embedHorizontalSeparationPattern(0, hspWidth - 1, matrix);
        embedHorizontalSeparationPattern(matrix.getWidth() - hspWidth, hspWidth - 1, matrix);
        embedHorizontalSeparationPattern(0, matrix.getWidth() - hspWidth, matrix);
        int vspSize = VERTICAL_SEPARATION_PATTERN.length;
        embedVerticalSeparationPattern(vspSize, 0, matrix);
        embedVerticalSeparationPattern((matrix.getHeight() - vspSize) - 1, 0, matrix);
        embedVerticalSeparationPattern(vspSize, matrix.getHeight() - vspSize, matrix);
    }

    private static void maybeEmbedPositionAdjustmentPatterns(int version, ByteMatrix matrix) throws WriterException {
        if (version >= 2) {
            int index = version - 1;
            int[] coordinates = POSITION_ADJUSTMENT_PATTERN_COORDINATE_TABLE[index];
            int numCoordinates = POSITION_ADJUSTMENT_PATTERN_COORDINATE_TABLE[index].length;
            for (int i = 0; i < numCoordinates; i++) {
                for (int j = 0; j < numCoordinates; j++) {
                    int y = coordinates[i];
                    int x = coordinates[j];
                    if (!(x == -1 || y == -1 || !isEmpty(matrix.get(x, y)))) {
                        embedPositionAdjustmentPattern(x - 2, y - 2, matrix);
                    }
                }
            }
        }
    }
}
