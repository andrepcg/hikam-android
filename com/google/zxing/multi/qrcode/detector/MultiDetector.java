package com.google.zxing.multi.qrcode.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.qrcode.detector.Detector;
import com.google.zxing.qrcode.detector.FinderPatternInfo;
import java.util.Hashtable;
import java.util.Vector;

public final class MultiDetector extends Detector {
    private static final DetectorResult[] EMPTY_DETECTOR_RESULTS = new DetectorResult[0];

    public MultiDetector(BitMatrix image) {
        super(image);
    }

    public DetectorResult[] detectMulti(Hashtable hints) throws NotFoundException {
        FinderPatternInfo[] info = new MultiFinderPatternFinder(getImage()).findMulti(hints);
        if (info == null || info.length == 0) {
            throw NotFoundException.getNotFoundInstance();
        }
        int i;
        Vector result = new Vector();
        for (FinderPatternInfo processFinderPatternInfo : info) {
            try {
                result.addElement(processFinderPatternInfo(processFinderPatternInfo));
            } catch (ReaderException e) {
            }
        }
        if (result.isEmpty()) {
            return EMPTY_DETECTOR_RESULTS;
        }
        DetectorResult[] resultArray = new DetectorResult[result.size()];
        for (i = 0; i < result.size(); i++) {
            resultArray[i] = (DetectorResult) result.elementAt(i);
        }
        return resultArray;
    }
}
