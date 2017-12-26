package com.google.zxing.multi.qrcode.detector;

import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.Collections;
import com.google.zxing.common.Comparator;
import com.google.zxing.qrcode.detector.FinderPattern;
import com.google.zxing.qrcode.detector.FinderPatternFinder;
import com.google.zxing.qrcode.detector.FinderPatternInfo;
import java.util.Hashtable;
import java.util.Vector;

final class MultiFinderPatternFinder extends FinderPatternFinder {
    private static final float DIFF_MODSIZE_CUTOFF = 0.5f;
    private static final float DIFF_MODSIZE_CUTOFF_PERCENT = 0.05f;
    private static final FinderPatternInfo[] EMPTY_RESULT_ARRAY = new FinderPatternInfo[0];
    private static final float MAX_MODULE_COUNT_PER_EDGE = 180.0f;
    private static final float MIN_MODULE_COUNT_PER_EDGE = 9.0f;

    static class C02891 {
    }

    private static class ModuleSizeComparator implements Comparator {
        private ModuleSizeComparator() {
        }

        ModuleSizeComparator(C02891 x0) {
            this();
        }

        public int compare(Object center1, Object center2) {
            float value = ((FinderPattern) center2).getEstimatedModuleSize() - ((FinderPattern) center1).getEstimatedModuleSize();
            if (((double) value) < 0.0d) {
                return -1;
            }
            return ((double) value) > 0.0d ? 1 : 0;
        }
    }

    MultiFinderPatternFinder(BitMatrix image) {
        super(image);
    }

    MultiFinderPatternFinder(BitMatrix image, ResultPointCallback resultPointCallback) {
        super(image, resultPointCallback);
    }

    private FinderPattern[][] selectBestPatterns() throws NotFoundException {
        Vector possibleCenters = getPossibleCenters();
        int size = possibleCenters.size();
        if (size < 3) {
            throw NotFoundException.getNotFoundInstance();
        } else if (size == 3) {
            r16 = new FinderPattern[1][];
            r16[0] = new FinderPattern[]{(FinderPattern) possibleCenters.elementAt(0), (FinderPattern) possibleCenters.elementAt(1), (FinderPattern) possibleCenters.elementAt(2)};
            return r16;
        } else {
            Collections.insertionSort(possibleCenters, new ModuleSizeComparator(null));
            Vector results = new Vector();
            for (int i1 = 0; i1 < size - 2; i1++) {
                FinderPattern p1 = (FinderPattern) possibleCenters.elementAt(i1);
                if (p1 != null) {
                    for (int i2 = i1 + 1; i2 < size - 1; i2++) {
                        FinderPattern p2 = (FinderPattern) possibleCenters.elementAt(i2);
                        if (p2 != null) {
                            float vModSize12 = (p1.getEstimatedModuleSize() - p2.getEstimatedModuleSize()) / Math.min(p1.getEstimatedModuleSize(), p2.getEstimatedModuleSize());
                            if (Math.abs(p1.getEstimatedModuleSize() - p2.getEstimatedModuleSize()) > DIFF_MODSIZE_CUTOFF && vModSize12 >= DIFF_MODSIZE_CUTOFF_PERCENT) {
                                break;
                            }
                            for (int i3 = i2 + 1; i3 < size; i3++) {
                                FinderPattern p3 = (FinderPattern) possibleCenters.elementAt(i3);
                                if (p3 != null) {
                                    float vModSize23 = (p2.getEstimatedModuleSize() - p3.getEstimatedModuleSize()) / Math.min(p2.getEstimatedModuleSize(), p3.getEstimatedModuleSize());
                                    if (Math.abs(p2.getEstimatedModuleSize() - p3.getEstimatedModuleSize()) > DIFF_MODSIZE_CUTOFF && vModSize23 >= DIFF_MODSIZE_CUTOFF_PERCENT) {
                                        break;
                                    }
                                    Object test = new FinderPattern[]{p1, p2, p3};
                                    ResultPoint.orderBestPatterns(test);
                                    FinderPatternInfo info = new FinderPatternInfo(test);
                                    float dA = ResultPoint.distance(info.getTopLeft(), info.getBottomLeft());
                                    float dC = ResultPoint.distance(info.getTopRight(), info.getBottomLeft());
                                    float dB = ResultPoint.distance(info.getTopLeft(), info.getTopRight());
                                    float estimatedModuleCount = ((dA + dB) / p1.getEstimatedModuleSize()) / 2.0f;
                                    if (estimatedModuleCount <= MAX_MODULE_COUNT_PER_EDGE && estimatedModuleCount >= MIN_MODULE_COUNT_PER_EDGE && Math.abs((dA - dB) / Math.min(dA, dB)) < 0.1f) {
                                        float dCpy = (float) Math.sqrt((double) ((dA * dA) + (dB * dB)));
                                        if (Math.abs((dC - dCpy) / Math.min(dC, dCpy)) < 0.1f) {
                                            results.addElement(test);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (results.isEmpty()) {
                throw NotFoundException.getNotFoundInstance();
            }
            r16 = new FinderPattern[results.size()][];
            for (int i = 0; i < results.size(); i++) {
                r16[i] = (FinderPattern[]) results.elementAt(i);
            }
            return r16;
        }
    }

    public FinderPatternInfo[] findMulti(Hashtable hints) throws NotFoundException {
        boolean tryHarder;
        BitMatrix image;
        int maxI;
        int maxJ;
        int iSkip;
        int[] stateCount;
        int i;
        int currentState;
        int j;
        FinderPattern[][] patternInfo;
        Vector result;
        FinderPatternInfo[] resultArray;
        if (hints != null) {
            if (hints.containsKey(DecodeHintType.TRY_HARDER)) {
                tryHarder = true;
                image = getImage();
                maxI = image.getHeight();
                maxJ = image.getWidth();
                iSkip = (int) ((((float) maxI) / 228.0f) * 3.0f);
                if (iSkip < 3 || tryHarder) {
                    iSkip = 3;
                }
                stateCount = new int[5];
                for (i = iSkip - 1; i < maxI; i += iSkip) {
                    stateCount[0] = 0;
                    stateCount[1] = 0;
                    stateCount[2] = 0;
                    stateCount[3] = 0;
                    stateCount[4] = 0;
                    currentState = 0;
                    j = 0;
                    while (j < maxJ) {
                        if (image.get(j, i)) {
                            if ((currentState & 1) == 1) {
                                currentState++;
                            }
                            stateCount[currentState] = stateCount[currentState] + 1;
                        } else if ((currentState & 1) == 0) {
                            stateCount[currentState] = stateCount[currentState] + 1;
                        } else if (currentState == 4) {
                            currentState++;
                            stateCount[currentState] = stateCount[currentState] + 1;
                        } else if (FinderPatternFinder.foundPatternCross(stateCount)) {
                            stateCount[0] = stateCount[2];
                            stateCount[1] = stateCount[3];
                            stateCount[2] = stateCount[4];
                            stateCount[3] = 1;
                            stateCount[4] = 0;
                            currentState = 3;
                        } else {
                            if (!handlePossibleCenter(stateCount, i, j)) {
                                do {
                                    j++;
                                    if (j < maxJ) {
                                        break;
                                    }
                                } while (!image.get(j, i));
                                j--;
                            }
                            currentState = 0;
                            stateCount[0] = 0;
                            stateCount[1] = 0;
                            stateCount[2] = 0;
                            stateCount[3] = 0;
                            stateCount[4] = 0;
                        }
                        j++;
                    }
                    if (FinderPatternFinder.foundPatternCross(stateCount)) {
                        handlePossibleCenter(stateCount, i, maxJ);
                    }
                }
                patternInfo = selectBestPatterns();
                result = new Vector();
                for (FinderPattern[] pattern : patternInfo) {
                    ResultPoint.orderBestPatterns(pattern);
                    result.addElement(new FinderPatternInfo(pattern));
                }
                if (result.isEmpty()) {
                    return EMPTY_RESULT_ARRAY;
                }
                resultArray = new FinderPatternInfo[result.size()];
                for (i = 0; i < result.size(); i++) {
                    resultArray[i] = (FinderPatternInfo) result.elementAt(i);
                }
                return resultArray;
            }
        }
        tryHarder = false;
        image = getImage();
        maxI = image.getHeight();
        maxJ = image.getWidth();
        iSkip = (int) ((((float) maxI) / 228.0f) * 3.0f);
        iSkip = 3;
        stateCount = new int[5];
        for (i = iSkip - 1; i < maxI; i += iSkip) {
            stateCount[0] = 0;
            stateCount[1] = 0;
            stateCount[2] = 0;
            stateCount[3] = 0;
            stateCount[4] = 0;
            currentState = 0;
            j = 0;
            while (j < maxJ) {
                if (image.get(j, i)) {
                    if ((currentState & 1) == 1) {
                        currentState++;
                    }
                    stateCount[currentState] = stateCount[currentState] + 1;
                } else if ((currentState & 1) == 0) {
                    stateCount[currentState] = stateCount[currentState] + 1;
                } else if (currentState == 4) {
                    currentState++;
                    stateCount[currentState] = stateCount[currentState] + 1;
                } else if (FinderPatternFinder.foundPatternCross(stateCount)) {
                    stateCount[0] = stateCount[2];
                    stateCount[1] = stateCount[3];
                    stateCount[2] = stateCount[4];
                    stateCount[3] = 1;
                    stateCount[4] = 0;
                    currentState = 3;
                } else {
                    if (handlePossibleCenter(stateCount, i, j)) {
                        do {
                            j++;
                            if (j < maxJ) {
                                break;
                            }
                        } while (!image.get(j, i));
                        j--;
                    }
                    currentState = 0;
                    stateCount[0] = 0;
                    stateCount[1] = 0;
                    stateCount[2] = 0;
                    stateCount[3] = 0;
                    stateCount[4] = 0;
                }
                j++;
            }
            if (FinderPatternFinder.foundPatternCross(stateCount)) {
                handlePossibleCenter(stateCount, i, maxJ);
            }
        }
        patternInfo = selectBestPatterns();
        result = new Vector();
        for (FinderPattern[] pattern2 : patternInfo) {
            ResultPoint.orderBestPatterns(pattern2);
            result.addElement(new FinderPatternInfo(pattern2));
        }
        if (result.isEmpty()) {
            return EMPTY_RESULT_ARRAY;
        }
        resultArray = new FinderPatternInfo[result.size()];
        for (i = 0; i < result.size(); i++) {
            resultArray[i] = (FinderPatternInfo) result.elementAt(i);
        }
        return resultArray;
    }
}
