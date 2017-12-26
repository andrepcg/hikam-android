package com.google.zxing.oned;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.Enumeration;
import java.util.Hashtable;

public abstract class OneDReader implements Reader {
    protected static final int INTEGER_MATH_SHIFT = 8;
    protected static final int PATTERN_MATCH_RESULT_SCALE_FACTOR = 256;

    public abstract Result decodeRow(int i, BitArray bitArray, Hashtable hashtable) throws NotFoundException, ChecksumException, FormatException;

    public Result decode(BinaryBitmap image) throws NotFoundException, FormatException {
        return decode(image, null);
    }

    public Result decode(BinaryBitmap image, Hashtable hints) throws NotFoundException, FormatException {
        Result doDecode;
        try {
            doDecode = doDecode(image, hints);
        } catch (NotFoundException nfe) {
            boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
            if (tryHarder && image.isRotateSupported()) {
                BinaryBitmap rotatedImage = image.rotateCounterClockwise();
                doDecode = doDecode(rotatedImage, hints);
                Hashtable metadata = doDecode.getResultMetadata();
                int orientation = 270;
                if (metadata != null && metadata.containsKey(ResultMetadataType.ORIENTATION)) {
                    orientation = (((Integer) metadata.get(ResultMetadataType.ORIENTATION)).intValue() + 270) % 360;
                }
                doDecode.putMetadata(ResultMetadataType.ORIENTATION, new Integer(orientation));
                ResultPoint[] points = doDecode.getResultPoints();
                int height = rotatedImage.getHeight();
                for (int i = 0; i < points.length; i++) {
                    points[i] = new ResultPoint((((float) height) - points[i].getY()) - 1.0f, points[i].getX());
                }
            } else {
                throw nfe;
            }
        }
        return doDecode;
    }

    public void reset() {
    }

    private Result doDecode(BinaryBitmap image, Hashtable hints) throws NotFoundException {
        int maxLines;
        int width = image.getWidth();
        int height = image.getHeight();
        BitArray row = new BitArray(width);
        int middle = height >> 1;
        boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
        int rowStep = Math.max(1, height >> (tryHarder ? 8 : 5));
        if (tryHarder) {
            maxLines = height;
        } else {
            maxLines = 15;
        }
        for (int x = 0; x < maxLines; x++) {
            int rowStepsAboveOrBelow = (x + 1) >> 1;
            if (!((x & 1) == 0)) {
                rowStepsAboveOrBelow = -rowStepsAboveOrBelow;
            }
            int rowNumber = middle + (rowStep * rowStepsAboveOrBelow);
            if (rowNumber < 0 || rowNumber >= height) {
                break;
            }
            try {
                row = image.getBlackRow(rowNumber, row);
                int attempt = 0;
                while (attempt < 2) {
                    if (attempt == 1) {
                        row.reverse();
                        if (hints != null && hints.containsKey(DecodeHintType.NEED_RESULT_POINT_CALLBACK)) {
                            Hashtable newHints = new Hashtable();
                            Enumeration hintEnum = hints.keys();
                            while (hintEnum.hasMoreElements()) {
                                Object key = hintEnum.nextElement();
                                if (!key.equals(DecodeHintType.NEED_RESULT_POINT_CALLBACK)) {
                                    newHints.put(key, hints.get(key));
                                }
                            }
                            hints = newHints;
                        }
                    }
                    try {
                        Result result = decodeRow(rowNumber, row, hints);
                        if (attempt == 1) {
                            result.putMetadata(ResultMetadataType.ORIENTATION, new Integer(180));
                            ResultPoint[] points = result.getResultPoints();
                            points[0] = new ResultPoint((((float) width) - points[0].getX()) - 1.0f, points[0].getY());
                            points[1] = new ResultPoint((((float) width) - points[1].getX()) - 1.0f, points[1].getY());
                        }
                        return result;
                    } catch (ReaderException e) {
                        attempt++;
                    }
                }
                continue;
            } catch (NotFoundException e2) {
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    protected static void recordPattern(BitArray row, int start, int[] counters) throws NotFoundException {
        int i;
        int numCounters = counters.length;
        for (i = 0; i < numCounters; i++) {
            counters[i] = 0;
        }
        int end = row.getSize();
        if (start >= end) {
            throw NotFoundException.getNotFoundInstance();
        }
        boolean isWhite;
        if (row.get(start)) {
            isWhite = false;
        } else {
            isWhite = true;
        }
        int counterPosition = 0;
        i = start;
        while (i < end) {
            if ((row.get(i) ^ isWhite) != 0) {
                counters[counterPosition] = counters[counterPosition] + 1;
            } else {
                counterPosition++;
                if (counterPosition == numCounters) {
                    break;
                }
                counters[counterPosition] = 1;
                isWhite = !isWhite;
            }
            i++;
        }
        if (counterPosition == numCounters) {
            return;
        }
        if (counterPosition != numCounters - 1 || i != end) {
            throw NotFoundException.getNotFoundInstance();
        }
    }

    protected static void recordPatternInReverse(BitArray row, int start, int[] counters) throws NotFoundException {
        int numTransitionsLeft = counters.length;
        boolean last = row.get(start);
        while (start > 0 && numTransitionsLeft >= 0) {
            start--;
            if (row.get(start) != last) {
                numTransitionsLeft--;
                last = !last;
            }
        }
        if (numTransitionsLeft >= 0) {
            throw NotFoundException.getNotFoundInstance();
        }
        recordPattern(row, start + 1, counters);
    }

    protected static int patternMatchVariance(int[] counters, int[] pattern, int maxIndividualVariance) {
        int numCounters = counters.length;
        int total = 0;
        int patternLength = 0;
        for (int i = 0; i < numCounters; i++) {
            total += counters[i];
            patternLength += pattern[i];
        }
        if (total < patternLength) {
            return Integer.MAX_VALUE;
        }
        int unitBarWidth = (total << 8) / patternLength;
        maxIndividualVariance = (maxIndividualVariance * unitBarWidth) >> 8;
        int totalVariance = 0;
        for (int x = 0; x < numCounters; x++) {
            int counter = counters[x] << 8;
            int scaledPattern = pattern[x] * unitBarWidth;
            int variance = counter > scaledPattern ? counter - scaledPattern : scaledPattern - counter;
            if (variance > maxIndividualVariance) {
                return Integer.MAX_VALUE;
            }
            totalVariance += variance;
        }
        return totalVariance / total;
    }
}
