package com.google.zxing.oned.rss.expanded;

import android.support.v4.media.TransportMediator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import com.google.zxing.oned.OneDReader;
import com.google.zxing.oned.rss.AbstractRSSReader;
import com.google.zxing.oned.rss.DataCharacter;
import com.google.zxing.oned.rss.FinderPattern;
import com.google.zxing.oned.rss.RSSUtils;
import com.google.zxing.oned.rss.expanded.decoders.AbstractExpandedDecoder;
import com.hikam.BuildConfig;
import com.jwkj.widget.MyImageView;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.http.HttpStatus;

public final class RSSExpandedReader extends AbstractRSSReader {
    private static final int[] EVEN_TOTAL_SUBSET = new int[]{4, 20, 52, 104, HttpStatus.SC_NO_CONTENT};
    private static final int[][] FINDER_PATTERNS = new int[][]{new int[]{1, 8, 4, 1}, new int[]{3, 6, 4, 1}, new int[]{3, 4, 6, 1}, new int[]{3, 2, 8, 1}, new int[]{2, 6, 5, 1}, new int[]{2, 2, 9, 1}};
    private static final int[][] FINDER_PATTERN_SEQUENCES = new int[][]{new int[]{0, 0}, new int[]{0, 1, 1}, new int[]{0, 2, 1, 3}, new int[]{0, 4, 1, 3, 2}, new int[]{0, 4, 1, 3, 3, 5}, new int[]{0, 4, 1, 3, 4, 5, 5}, new int[]{0, 0, 1, 1, 2, 2, 3, 3}, new int[]{0, 0, 1, 1, 2, 2, 3, 4, 4}, new int[]{0, 0, 1, 1, 2, 2, 3, 4, 5, 5}, new int[]{0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5}};
    private static final int FINDER_PAT_A = 0;
    private static final int FINDER_PAT_B = 1;
    private static final int FINDER_PAT_C = 2;
    private static final int FINDER_PAT_D = 3;
    private static final int FINDER_PAT_E = 4;
    private static final int FINDER_PAT_F = 5;
    private static final int[] GSUM = new int[]{0, 348, 1388, 2948, 3988};
    private static final int LONGEST_SEQUENCE_SIZE = FINDER_PATTERN_SEQUENCES[FINDER_PATTERN_SEQUENCES.length - 1].length;
    private static final int MAX_PAIRS = 11;
    private static final int[] SYMBOL_WIDEST = new int[]{7, 5, 4, 3, 1};
    private static final int[][] WEIGHTS = new int[][]{new int[]{1, 3, 9, 27, 81, 32, 96, 77}, new int[]{20, 60, 180, 118, 143, 7, 21, 63}, new int[]{189, 145, 13, 39, 117, 140, 209, HttpStatus.SC_RESET_CONTENT}, new int[]{193, 157, 49, 147, 19, 57, 171, 91}, new int[]{62, 186, 136, 197, 169, 85, 44, 132}, new int[]{185, 133, 188, 142, 4, 12, 36, 108}, new int[]{113, 128, 173, 97, 80, 29, 87, 50}, new int[]{150, 28, 84, 41, 123, 158, 52, 156}, new int[]{46, 138, HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION, 187, 139, HttpStatus.SC_PARTIAL_CONTENT, 196, 166}, new int[]{76, 17, 51, 153, 37, 111, 122, TarConstants.PREFIXLEN}, new int[]{43, 129, 176, 106, 107, 110, 119, 146}, new int[]{16, 48, 144, 10, 30, 90, 59, 177}, new int[]{109, 116, 137, 200, 178, 112, 125, 164}, new int[]{70, 210, 208, HttpStatus.SC_ACCEPTED, 184, TransportMediator.KEYCODE_MEDIA_RECORD, 179, 115}, new int[]{134, 191, 151, 31, 93, 68, HttpStatus.SC_NO_CONTENT, 190}, new int[]{TarConstants.CHKSUM_OFFSET, 22, 66, BuildConfig.VERSION_CODE, 172, 94, 71, 2}, new int[]{6, 18, 54, 162, 64, 192, 154, 40}, new int[]{MyImageView.IMAGE_WIDTH, 149, 25, 75, 14, 42, TransportMediator.KEYCODE_MEDIA_PLAY, 167}, new int[]{79, 26, 78, 23, 69, HttpStatus.SC_MULTI_STATUS, 199, 175}, new int[]{103, 98, 83, 38, 114, TarConstants.PREFIXLEN_XSTAR, 182, 124}, new int[]{161, 61, 183, TransportMediator.KEYCODE_MEDIA_PAUSE, 170, 88, 53, 159}, new int[]{55, 165, 73, 8, 24, 72, 5, 15}, new int[]{45, 135, 194, 160, 58, 174, 100, 89}};
    private final int[] currentSequence = new int[LONGEST_SEQUENCE_SIZE];
    private final Vector pairs = new Vector(11);
    private final int[] startEnd = new int[2];

    public Result decodeRow(int rowNumber, BitArray row, Hashtable hints) throws NotFoundException {
        reset();
        decodeRow2pairs(rowNumber, row);
        return constructResult(this.pairs);
    }

    public void reset() {
        this.pairs.setSize(0);
    }

    Vector decodeRow2pairs(int rowNumber, BitArray row) throws NotFoundException {
        while (true) {
            ExpandedPair nextPair = retrieveNextPair(row, this.pairs, rowNumber);
            this.pairs.addElement(nextPair);
            if (nextPair.mayBeLast()) {
                if (checkChecksum()) {
                    return this.pairs;
                }
                if (nextPair.mustBeLast()) {
                    throw NotFoundException.getNotFoundInstance();
                }
            }
        }
    }

    private static Result constructResult(Vector pairs) throws NotFoundException {
        String resultingString = AbstractExpandedDecoder.createDecoder(BitArrayBuilder.buildBitArray(pairs)).parseInformation();
        ResultPoint[] firstPoints = ((ExpandedPair) pairs.elementAt(0)).getFinderPattern().getResultPoints();
        ResultPoint[] lastPoints = ((ExpandedPair) pairs.lastElement()).getFinderPattern().getResultPoints();
        return new Result(resultingString, null, new ResultPoint[]{firstPoints[0], firstPoints[1], lastPoints[0], lastPoints[1]}, BarcodeFormat.RSS_EXPANDED);
    }

    private boolean checkChecksum() {
        ExpandedPair firstPair = (ExpandedPair) this.pairs.elementAt(0);
        DataCharacter checkCharacter = firstPair.getLeftChar();
        int checksum = firstPair.getRightChar().getChecksumPortion();
        int S = 2;
        for (int i = 1; i < this.pairs.size(); i++) {
            ExpandedPair currentPair = (ExpandedPair) this.pairs.elementAt(i);
            checksum += currentPair.getLeftChar().getChecksumPortion();
            S++;
            if (currentPair.getRightChar() != null) {
                checksum += currentPair.getRightChar().getChecksumPortion();
                S++;
            }
        }
        if (((S - 4) * 211) + (checksum % 211) == checkCharacter.getValue()) {
            return true;
        }
        return false;
    }

    private static int getNextSecondBar(BitArray row, int initialPos) {
        int currentPos = initialPos;
        boolean current = row.get(currentPos);
        while (currentPos < row.size && row.get(currentPos) == current) {
            currentPos++;
        }
        current = !current;
        while (currentPos < row.size && row.get(currentPos) == current) {
            currentPos++;
        }
        return currentPos;
    }

    ExpandedPair retrieveNextPair(BitArray row, Vector previousPairs, int rowNumber) throws NotFoundException {
        boolean isOddPattern;
        FinderPattern pattern;
        DataCharacter rightChar;
        if (previousPairs.size() % 2 == 0) {
            isOddPattern = true;
        } else {
            isOddPattern = false;
        }
        boolean keepFinding = true;
        int forcedOffset = -1;
        do {
            findNextPair(row, previousPairs, forcedOffset);
            pattern = parseFoundFinderPattern(row, rowNumber, isOddPattern);
            if (pattern == null) {
                forcedOffset = getNextSecondBar(row, this.startEnd[0]);
                continue;
            } else {
                keepFinding = false;
                continue;
            }
        } while (keepFinding);
        boolean mayBeLast = checkPairSequence(previousPairs, pattern);
        DataCharacter leftChar = decodeDataCharacter(row, pattern, isOddPattern, true);
        try {
            rightChar = decodeDataCharacter(row, pattern, isOddPattern, false);
        } catch (NotFoundException nfe) {
            if (mayBeLast) {
                rightChar = null;
            } else {
                throw nfe;
            }
        }
        return new ExpandedPair(leftChar, rightChar, pattern, mayBeLast);
    }

    private boolean checkPairSequence(Vector previousPairs, FinderPattern pattern) throws NotFoundException {
        int currentSequenceLength = previousPairs.size() + 1;
        if (currentSequenceLength > this.currentSequence.length) {
            throw NotFoundException.getNotFoundInstance();
        }
        int pos;
        for (pos = 0; pos < previousPairs.size(); pos++) {
            this.currentSequence[pos] = ((ExpandedPair) previousPairs.elementAt(pos)).getFinderPattern().getValue();
        }
        this.currentSequence[currentSequenceLength - 1] = pattern.getValue();
        for (int[] validSequence : FINDER_PATTERN_SEQUENCES) {
            if (validSequence.length >= currentSequenceLength) {
                boolean valid = true;
                for (pos = 0; pos < currentSequenceLength; pos++) {
                    if (this.currentSequence[pos] != validSequence[pos]) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    if (currentSequenceLength == validSequence.length) {
                        return true;
                    }
                    return false;
                }
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private void findNextPair(BitArray row, Vector previousPairs, int forcedOffset) throws NotFoundException {
        int rowOffset;
        int[] counters = this.decodeFinderCounters;
        counters[0] = 0;
        counters[1] = 0;
        counters[2] = 0;
        counters[3] = 0;
        int width = row.getSize();
        if (forcedOffset >= 0) {
            rowOffset = forcedOffset;
        } else if (previousPairs.isEmpty()) {
            rowOffset = 0;
        } else {
            rowOffset = ((ExpandedPair) previousPairs.lastElement()).getFinderPattern().getStartEnd()[1];
        }
        boolean searchingEvenPair = previousPairs.size() % 2 != 0;
        boolean isWhite = false;
        while (rowOffset < width) {
            isWhite = !row.get(rowOffset);
            if (!isWhite) {
                break;
            }
            rowOffset++;
        }
        int counterPosition = 0;
        int patternStart = rowOffset;
        for (int x = rowOffset; x < width; x++) {
            if ((row.get(x) ^ isWhite) != 0) {
                counters[counterPosition] = counters[counterPosition] + 1;
            } else {
                if (counterPosition == 3) {
                    if (searchingEvenPair) {
                        reverseCounters(counters);
                    }
                    if (AbstractRSSReader.isFinderPattern(counters)) {
                        this.startEnd[0] = patternStart;
                        this.startEnd[1] = x;
                        return;
                    }
                    if (searchingEvenPair) {
                        reverseCounters(counters);
                    }
                    patternStart += counters[0] + counters[1];
                    counters[0] = counters[2];
                    counters[1] = counters[3];
                    counters[2] = 0;
                    counters[3] = 0;
                    counterPosition--;
                } else {
                    counterPosition++;
                }
                counters[counterPosition] = 1;
                isWhite = !isWhite;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static void reverseCounters(int[] counters) {
        int length = counters.length;
        for (int i = 0; i < length / 2; i++) {
            int tmp = counters[i];
            counters[i] = counters[(length - i) - 1];
            counters[(length - i) - 1] = tmp;
        }
    }

    private FinderPattern parseFoundFinderPattern(BitArray row, int rowNumber, boolean oddPattern) {
        int firstCounter;
        int start;
        int end;
        int firstElementStart;
        if (oddPattern) {
            firstElementStart = this.startEnd[0] - 1;
            while (firstElementStart >= 0 && !row.get(firstElementStart)) {
                firstElementStart--;
            }
            firstElementStart++;
            firstCounter = this.startEnd[0] - firstElementStart;
            start = firstElementStart;
            end = this.startEnd[1];
        } else {
            start = this.startEnd[0];
            firstElementStart = this.startEnd[1] + 1;
            while (row.get(firstElementStart) && firstElementStart < row.size) {
                firstElementStart++;
            }
            end = firstElementStart;
            firstCounter = end - this.startEnd[1];
        }
        int[] counters = this.decodeFinderCounters;
        for (int i = counters.length - 1; i > 0; i--) {
            counters[i] = counters[i - 1];
        }
        counters[0] = firstCounter;
        try {
            return new FinderPattern(AbstractRSSReader.parseFinderValue(counters, FINDER_PATTERNS), new int[]{start, end}, start, end, rowNumber);
        } catch (NotFoundException e) {
            return null;
        }
    }

    DataCharacter decodeDataCharacter(BitArray row, FinderPattern pattern, boolean isOddPattern, boolean leftChar) throws NotFoundException {
        int i;
        int i2;
        int[] counters = this.dataCharacterCounters;
        counters[0] = 0;
        counters[1] = 0;
        counters[2] = 0;
        counters[3] = 0;
        counters[4] = 0;
        counters[5] = 0;
        counters[6] = 0;
        counters[7] = 0;
        if (leftChar) {
            OneDReader.recordPatternInReverse(row, pattern.getStartEnd()[0], counters);
        } else {
            OneDReader.recordPattern(row, pattern.getStartEnd()[1] + 1, counters);
            i = 0;
            for (int j = counters.length - 1; i < j; j--) {
                int temp = counters[i];
                counters[i] = counters[j];
                counters[j] = temp;
                i++;
            }
        }
        float elementWidth = ((float) AbstractRSSReader.count(counters)) / ((float) 17);
        int[] oddCounts = this.oddCounts;
        int[] evenCounts = this.evenCounts;
        float[] oddRoundingErrors = this.oddRoundingErrors;
        float[] evenRoundingErrors = this.evenRoundingErrors;
        for (i = 0; i < counters.length; i++) {
            float value = (1.0f * ((float) counters[i])) / elementWidth;
            int count = (int) (0.5f + value);
            if (count < 1) {
                count = 1;
            } else if (count > 8) {
                count = 8;
            }
            int offset = i >> 1;
            if ((i & 1) == 0) {
                oddCounts[offset] = count;
                oddRoundingErrors[offset] = value - ((float) count);
            } else {
                evenCounts[offset] = count;
                evenRoundingErrors[offset] = value - ((float) count);
            }
        }
        adjustOddEvenCounts(17);
        int value2 = (pattern.getValue() * 4) + (isOddPattern ? 0 : 2);
        if (leftChar) {
            i2 = 0;
        } else {
            i2 = 1;
        }
        int weightRowNumber = (i2 + value2) - 1;
        int oddSum = 0;
        int oddChecksumPortion = 0;
        for (i = oddCounts.length - 1; i >= 0; i--) {
            if (isNotA1left(pattern, isOddPattern, leftChar)) {
                oddChecksumPortion += oddCounts[i] * WEIGHTS[weightRowNumber][i * 2];
            }
            oddSum += oddCounts[i];
        }
        int evenChecksumPortion = 0;
        int evenSum = 0;
        for (i = evenCounts.length - 1; i >= 0; i--) {
            if (isNotA1left(pattern, isOddPattern, leftChar)) {
                evenChecksumPortion += evenCounts[i] * WEIGHTS[weightRowNumber][(i * 2) + 1];
            }
            evenSum += evenCounts[i];
        }
        int checksumPortion = oddChecksumPortion + evenChecksumPortion;
        if ((oddSum & 1) != 0 || oddSum > 13 || oddSum < 4) {
            throw NotFoundException.getNotFoundInstance();
        }
        int group = (13 - oddSum) / 2;
        int oddWidest = SYMBOL_WIDEST[group];
        int evenWidest = 9 - oddWidest;
        int vOdd = RSSUtils.getRSSvalue(oddCounts, oddWidest, true);
        return new DataCharacter(((vOdd * EVEN_TOTAL_SUBSET[group]) + RSSUtils.getRSSvalue(evenCounts, evenWidest, false)) + GSUM[group], checksumPortion);
    }

    private static boolean isNotA1left(FinderPattern pattern, boolean isOddPattern, boolean leftChar) {
        return (pattern.getValue() == 0 && isOddPattern && leftChar) ? false : true;
    }

    private void adjustOddEvenCounts(int numModules) throws NotFoundException {
        boolean oddParityBad;
        boolean evenParityBad = false;
        int oddSum = AbstractRSSReader.count(this.oddCounts);
        int evenSum = AbstractRSSReader.count(this.evenCounts);
        int mismatch = (oddSum + evenSum) - numModules;
        if ((oddSum & 1) == 1) {
            oddParityBad = true;
        } else {
            oddParityBad = false;
        }
        if ((evenSum & 1) == 0) {
            evenParityBad = true;
        }
        boolean incrementOdd = false;
        boolean decrementOdd = false;
        if (oddSum > 13) {
            decrementOdd = true;
        } else if (oddSum < 4) {
            incrementOdd = true;
        }
        boolean incrementEven = false;
        boolean decrementEven = false;
        if (evenSum > 13) {
            decrementEven = true;
        } else if (evenSum < 4) {
            incrementEven = true;
        }
        if (mismatch == 1) {
            if (oddParityBad) {
                if (evenParityBad) {
                    throw NotFoundException.getNotFoundInstance();
                }
                decrementOdd = true;
            } else if (evenParityBad) {
                decrementEven = true;
            } else {
                throw NotFoundException.getNotFoundInstance();
            }
        } else if (mismatch == -1) {
            if (oddParityBad) {
                if (evenParityBad) {
                    throw NotFoundException.getNotFoundInstance();
                }
                incrementOdd = true;
            } else if (evenParityBad) {
                incrementEven = true;
            } else {
                throw NotFoundException.getNotFoundInstance();
            }
        } else if (mismatch != 0) {
            throw NotFoundException.getNotFoundInstance();
        } else if (oddParityBad) {
            if (!evenParityBad) {
                throw NotFoundException.getNotFoundInstance();
            } else if (oddSum < evenSum) {
                incrementOdd = true;
                decrementEven = true;
            } else {
                decrementOdd = true;
                incrementEven = true;
            }
        } else if (evenParityBad) {
            throw NotFoundException.getNotFoundInstance();
        }
        if (incrementOdd) {
            if (decrementOdd) {
                throw NotFoundException.getNotFoundInstance();
            }
            AbstractRSSReader.increment(this.oddCounts, this.oddRoundingErrors);
        }
        if (decrementOdd) {
            AbstractRSSReader.decrement(this.oddCounts, this.oddRoundingErrors);
        }
        if (incrementEven) {
            if (decrementEven) {
                throw NotFoundException.getNotFoundInstance();
            }
            AbstractRSSReader.increment(this.evenCounts, this.oddRoundingErrors);
        }
        if (decrementEven) {
            AbstractRSSReader.decrement(this.evenCounts, this.evenRoundingErrors);
        }
    }
}
