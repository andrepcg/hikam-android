package com.google.zxing.qrcode.detector;

import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.Collections;
import com.google.zxing.common.Comparator;
import java.util.Hashtable;
import java.util.Vector;

public class FinderPatternFinder {
    private static final int CENTER_QUORUM = 2;
    private static final int INTEGER_MATH_SHIFT = 8;
    protected static final int MAX_MODULES = 57;
    protected static final int MIN_SKIP = 3;
    private final int[] crossCheckStateCount;
    private boolean hasSkipped;
    private final BitMatrix image;
    private final Vector possibleCenters;
    private final ResultPointCallback resultPointCallback;

    private static class CenterComparator implements Comparator {
        private final float average;

        public CenterComparator(float f) {
            this.average = f;
        }

        public int compare(Object center1, Object center2) {
            if (((FinderPattern) center2).getCount() != ((FinderPattern) center1).getCount()) {
                return ((FinderPattern) center2).getCount() - ((FinderPattern) center1).getCount();
            }
            float dA = Math.abs(((FinderPattern) center2).getEstimatedModuleSize() - this.average);
            float dB = Math.abs(((FinderPattern) center1).getEstimatedModuleSize() - this.average);
            if (dA < dB) {
                return 1;
            }
            return dA == dB ? 0 : -1;
        }
    }

    private static class FurthestFromAverageComparator implements Comparator {
        private final float average;

        public FurthestFromAverageComparator(float f) {
            this.average = f;
        }

        public int compare(Object center1, Object center2) {
            float dA = Math.abs(((FinderPattern) center2).getEstimatedModuleSize() - this.average);
            float dB = Math.abs(((FinderPattern) center1).getEstimatedModuleSize() - this.average);
            if (dA < dB) {
                return -1;
            }
            return dA == dB ? 0 : 1;
        }
    }

    public FinderPatternFinder(BitMatrix image) {
        this(image, null);
    }

    public FinderPatternFinder(BitMatrix image, ResultPointCallback resultPointCallback) {
        this.image = image;
        this.possibleCenters = new Vector();
        this.crossCheckStateCount = new int[5];
        this.resultPointCallback = resultPointCallback;
    }

    protected BitMatrix getImage() {
        return this.image;
    }

    protected Vector getPossibleCenters() {
        return this.possibleCenters;
    }

    FinderPatternInfo find(Hashtable hints) throws NotFoundException {
        boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
        int maxI = this.image.getHeight();
        int maxJ = this.image.getWidth();
        int iSkip = (maxI * 3) / 228;
        if (iSkip < 3 || tryHarder) {
            iSkip = 3;
        }
        boolean done = false;
        int[] stateCount = new int[5];
        int i = iSkip - 1;
        while (i < maxI && !done) {
            stateCount[0] = 0;
            stateCount[1] = 0;
            stateCount[2] = 0;
            stateCount[3] = 0;
            stateCount[4] = 0;
            int currentState = 0;
            int j = 0;
            while (j < maxJ) {
                if (this.image.get(j, i)) {
                    if ((currentState & 1) == 1) {
                        currentState++;
                    }
                    stateCount[currentState] = stateCount[currentState] + 1;
                } else if ((currentState & 1) != 0) {
                    stateCount[currentState] = stateCount[currentState] + 1;
                } else if (currentState != 4) {
                    currentState++;
                    stateCount[currentState] = stateCount[currentState] + 1;
                } else if (!foundPatternCross(stateCount)) {
                    stateCount[0] = stateCount[2];
                    stateCount[1] = stateCount[3];
                    stateCount[2] = stateCount[4];
                    stateCount[3] = 1;
                    stateCount[4] = 0;
                    currentState = 3;
                } else if (handlePossibleCenter(stateCount, i, j)) {
                    iSkip = 2;
                    if (this.hasSkipped) {
                        done = haveMultiplyConfirmedCenters();
                    } else {
                        int rowSkip = findRowSkip();
                        if (rowSkip > stateCount[2]) {
                            i += (rowSkip - stateCount[2]) - 2;
                            j = maxJ - 1;
                        }
                    }
                    currentState = 0;
                    stateCount[0] = 0;
                    stateCount[1] = 0;
                    stateCount[2] = 0;
                    stateCount[3] = 0;
                    stateCount[4] = 0;
                } else {
                    stateCount[0] = stateCount[2];
                    stateCount[1] = stateCount[3];
                    stateCount[2] = stateCount[4];
                    stateCount[3] = 1;
                    stateCount[4] = 0;
                    currentState = 3;
                }
                j++;
            }
            if (foundPatternCross(stateCount) && handlePossibleCenter(stateCount, i, maxJ)) {
                iSkip = stateCount[0];
                if (this.hasSkipped) {
                    done = haveMultiplyConfirmedCenters();
                }
            }
            i += iSkip;
        }
        FinderPattern[] patternInfo = selectBestPatterns();
        ResultPoint.orderBestPatterns(patternInfo);
        return new FinderPatternInfo(patternInfo);
    }

    private static float centerFromEnd(int[] stateCount, int end) {
        return ((float) ((end - stateCount[4]) - stateCount[3])) - (((float) stateCount[2]) / 2.0f);
    }

    protected static boolean foundPatternCross(int[] stateCount) {
        boolean z = true;
        int totalModuleSize = 0;
        for (int i = 0; i < 5; i++) {
            int count = stateCount[i];
            if (count == 0) {
                return false;
            }
            totalModuleSize += count;
        }
        if (totalModuleSize < 7) {
            return false;
        }
        int moduleSize = (totalModuleSize << 8) / 7;
        int maxVariance = moduleSize / 2;
        if (Math.abs(moduleSize - (stateCount[0] << 8)) >= maxVariance || Math.abs(moduleSize - (stateCount[1] << 8)) >= maxVariance || Math.abs((moduleSize * 3) - (stateCount[2] << 8)) >= maxVariance * 3 || Math.abs(moduleSize - (stateCount[3] << 8)) >= maxVariance || Math.abs(moduleSize - (stateCount[4] << 8)) >= maxVariance) {
            z = false;
        }
        return z;
    }

    private int[] getCrossCheckStateCount() {
        this.crossCheckStateCount[0] = 0;
        this.crossCheckStateCount[1] = 0;
        this.crossCheckStateCount[2] = 0;
        this.crossCheckStateCount[3] = 0;
        this.crossCheckStateCount[4] = 0;
        return this.crossCheckStateCount;
    }

    private float crossCheckVertical(int startI, int centerJ, int maxCount, int originalStateCountTotal) {
        BitMatrix image = this.image;
        int maxI = image.getHeight();
        int[] stateCount = getCrossCheckStateCount();
        int i = startI;
        while (i >= 0 && image.get(centerJ, i)) {
            stateCount[2] = stateCount[2] + 1;
            i--;
        }
        if (i < 0) {
            return Float.NaN;
        }
        while (i >= 0 && !image.get(centerJ, i) && stateCount[1] <= maxCount) {
            stateCount[1] = stateCount[1] + 1;
            i--;
        }
        if (i < 0 || stateCount[1] > maxCount) {
            return Float.NaN;
        }
        while (i >= 0 && image.get(centerJ, i) && stateCount[0] <= maxCount) {
            stateCount[0] = stateCount[0] + 1;
            i--;
        }
        if (stateCount[0] > maxCount) {
            return Float.NaN;
        }
        i = startI + 1;
        while (i < maxI && image.get(centerJ, i)) {
            stateCount[2] = stateCount[2] + 1;
            i++;
        }
        if (i == maxI) {
            return Float.NaN;
        }
        while (i < maxI && !image.get(centerJ, i) && stateCount[3] < maxCount) {
            stateCount[3] = stateCount[3] + 1;
            i++;
        }
        if (i == maxI || stateCount[3] >= maxCount) {
            return Float.NaN;
        }
        while (i < maxI && image.get(centerJ, i) && stateCount[4] < maxCount) {
            stateCount[4] = stateCount[4] + 1;
            i++;
        }
        if (stateCount[4] >= maxCount) {
            return Float.NaN;
        }
        if (Math.abs(((((stateCount[0] + stateCount[1]) + stateCount[2]) + stateCount[3]) + stateCount[4]) - originalStateCountTotal) * 5 >= originalStateCountTotal * 2) {
            return Float.NaN;
        }
        return foundPatternCross(stateCount) ? centerFromEnd(stateCount, i) : Float.NaN;
    }

    private float crossCheckHorizontal(int startJ, int centerI, int maxCount, int originalStateCountTotal) {
        BitMatrix image = this.image;
        int maxJ = image.getWidth();
        int[] stateCount = getCrossCheckStateCount();
        int j = startJ;
        while (j >= 0 && image.get(j, centerI)) {
            stateCount[2] = stateCount[2] + 1;
            j--;
        }
        if (j < 0) {
            return Float.NaN;
        }
        while (j >= 0 && !image.get(j, centerI) && stateCount[1] <= maxCount) {
            stateCount[1] = stateCount[1] + 1;
            j--;
        }
        if (j < 0 || stateCount[1] > maxCount) {
            return Float.NaN;
        }
        while (j >= 0 && image.get(j, centerI) && stateCount[0] <= maxCount) {
            stateCount[0] = stateCount[0] + 1;
            j--;
        }
        if (stateCount[0] > maxCount) {
            return Float.NaN;
        }
        j = startJ + 1;
        while (j < maxJ && image.get(j, centerI)) {
            stateCount[2] = stateCount[2] + 1;
            j++;
        }
        if (j == maxJ) {
            return Float.NaN;
        }
        while (j < maxJ && !image.get(j, centerI) && stateCount[3] < maxCount) {
            stateCount[3] = stateCount[3] + 1;
            j++;
        }
        if (j == maxJ || stateCount[3] >= maxCount) {
            return Float.NaN;
        }
        while (j < maxJ && image.get(j, centerI) && stateCount[4] < maxCount) {
            stateCount[4] = stateCount[4] + 1;
            j++;
        }
        if (stateCount[4] >= maxCount) {
            return Float.NaN;
        }
        if (Math.abs(((((stateCount[0] + stateCount[1]) + stateCount[2]) + stateCount[3]) + stateCount[4]) - originalStateCountTotal) * 5 >= originalStateCountTotal) {
            return Float.NaN;
        }
        return foundPatternCross(stateCount) ? centerFromEnd(stateCount, j) : Float.NaN;
    }

    protected boolean handlePossibleCenter(int[] stateCount, int i, int j) {
        int stateCountTotal = (((stateCount[0] + stateCount[1]) + stateCount[2]) + stateCount[3]) + stateCount[4];
        float centerJ = centerFromEnd(stateCount, j);
        float centerI = crossCheckVertical(i, (int) centerJ, stateCount[2], stateCountTotal);
        if (!Float.isNaN(centerI)) {
            centerJ = crossCheckHorizontal((int) centerJ, (int) centerI, stateCount[2], stateCountTotal);
            if (!Float.isNaN(centerJ)) {
                float estimatedModuleSize = ((float) stateCountTotal) / 7.0f;
                boolean found = false;
                int max = this.possibleCenters.size();
                for (int index = 0; index < max; index++) {
                    FinderPattern center = (FinderPattern) this.possibleCenters.elementAt(index);
                    if (center.aboutEquals(estimatedModuleSize, centerI, centerJ)) {
                        center.incrementCount();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    ResultPoint point = new FinderPattern(centerJ, centerI, estimatedModuleSize);
                    this.possibleCenters.addElement(point);
                    if (this.resultPointCallback != null) {
                        this.resultPointCallback.foundPossibleResultPoint(point);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private int findRowSkip() {
        int max = this.possibleCenters.size();
        if (max <= 1) {
            return 0;
        }
        FinderPattern firstConfirmedCenter = null;
        for (int i = 0; i < max; i++) {
            FinderPattern center = (FinderPattern) this.possibleCenters.elementAt(i);
            if (center.getCount() >= 2) {
                if (firstConfirmedCenter == null) {
                    firstConfirmedCenter = center;
                } else {
                    this.hasSkipped = true;
                    return ((int) (Math.abs(firstConfirmedCenter.getX() - center.getX()) - Math.abs(firstConfirmedCenter.getY() - center.getY()))) / 2;
                }
            }
        }
        return 0;
    }

    private boolean haveMultiplyConfirmedCenters() {
        int i;
        int confirmedCount = 0;
        float totalModuleSize = 0.0f;
        int max = this.possibleCenters.size();
        for (i = 0; i < max; i++) {
            FinderPattern pattern = (FinderPattern) this.possibleCenters.elementAt(i);
            if (pattern.getCount() >= 2) {
                confirmedCount++;
                totalModuleSize += pattern.getEstimatedModuleSize();
            }
        }
        if (confirmedCount < 3) {
            return false;
        }
        float average = totalModuleSize / ((float) max);
        float totalDeviation = 0.0f;
        for (i = 0; i < max; i++) {
            totalDeviation += Math.abs(((FinderPattern) this.possibleCenters.elementAt(i)).getEstimatedModuleSize() - average);
        }
        if (totalDeviation <= 0.05f * totalModuleSize) {
            return true;
        }
        return false;
    }

    private FinderPattern[] selectBestPatterns() throws NotFoundException {
        int startSize = this.possibleCenters.size();
        if (startSize < 3) {
            throw NotFoundException.getNotFoundInstance();
        }
        float totalModuleSize;
        int i;
        if (startSize > 3) {
            totalModuleSize = 0.0f;
            float square = 0.0f;
            for (i = 0; i < startSize; i++) {
                float size = ((FinderPattern) this.possibleCenters.elementAt(i)).getEstimatedModuleSize();
                totalModuleSize += size;
                square += size * size;
            }
            float average = totalModuleSize / ((float) startSize);
            float stdDev = (float) Math.sqrt((double) ((square / ((float) startSize)) - (average * average)));
            Collections.insertionSort(this.possibleCenters, new FurthestFromAverageComparator(average));
            float limit = Math.max(0.2f * average, stdDev);
            i = 0;
            while (i < this.possibleCenters.size() && this.possibleCenters.size() > 3) {
                if (Math.abs(((FinderPattern) this.possibleCenters.elementAt(i)).getEstimatedModuleSize() - average) > limit) {
                    this.possibleCenters.removeElementAt(i);
                    i--;
                }
                i++;
            }
        }
        if (this.possibleCenters.size() > 3) {
            totalModuleSize = 0.0f;
            for (i = 0; i < this.possibleCenters.size(); i++) {
                totalModuleSize += ((FinderPattern) this.possibleCenters.elementAt(i)).getEstimatedModuleSize();
            }
            Collections.insertionSort(this.possibleCenters, new CenterComparator(totalModuleSize / ((float) this.possibleCenters.size())));
            this.possibleCenters.setSize(3);
        }
        return new FinderPattern[]{(FinderPattern) this.possibleCenters.elementAt(0), (FinderPattern) this.possibleCenters.elementAt(1), (FinderPattern) this.possibleCenters.elementAt(2)};
    }
}
