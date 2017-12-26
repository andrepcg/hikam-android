package com.google.zxing.multi;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import java.util.Hashtable;
import java.util.Vector;

public final class GenericMultipleBarcodeReader implements MultipleBarcodeReader {
    private static final int MIN_DIMENSION_TO_RECUR = 100;
    private final Reader delegate;

    public GenericMultipleBarcodeReader(Reader delegate) {
        this.delegate = delegate;
    }

    public Result[] decodeMultiple(BinaryBitmap image) throws NotFoundException {
        return decodeMultiple(image, null);
    }

    public Result[] decodeMultiple(BinaryBitmap image, Hashtable hints) throws NotFoundException {
        Vector results = new Vector();
        doDecodeMultiple(image, hints, results, 0, 0);
        if (results.isEmpty()) {
            throw NotFoundException.getNotFoundInstance();
        }
        int numResults = results.size();
        Result[] resultArray = new Result[numResults];
        for (int i = 0; i < numResults; i++) {
            resultArray[i] = (Result) results.elementAt(i);
        }
        return resultArray;
    }

    private void doDecodeMultiple(BinaryBitmap image, Hashtable hints, Vector results, int xOffset, int yOffset) {
        try {
            int i;
            Result result = this.delegate.decode(image, hints);
            boolean alreadyFound = false;
            for (i = 0; i < results.size(); i++) {
                if (((Result) results.elementAt(i)).getText().equals(result.getText())) {
                    alreadyFound = true;
                    break;
                }
            }
            if (!alreadyFound) {
                results.addElement(translateResultPoints(result, xOffset, yOffset));
                ResultPoint[] resultPoints = result.getResultPoints();
                if (resultPoints != null && resultPoints.length != 0) {
                    int width = image.getWidth();
                    int height = image.getHeight();
                    float minX = (float) width;
                    float minY = (float) height;
                    float maxX = 0.0f;
                    float maxY = 0.0f;
                    for (ResultPoint point : resultPoints) {
                        float x = point.getX();
                        float y = point.getY();
                        if (x < minX) {
                            minX = x;
                        }
                        if (y < minY) {
                            minY = y;
                        }
                        if (x > maxX) {
                            maxX = x;
                        }
                        if (y > maxY) {
                            maxY = y;
                        }
                    }
                    if (minX > 100.0f) {
                        doDecodeMultiple(image.crop(0, 0, (int) minX, height), hints, results, xOffset, yOffset);
                    }
                    if (minY > 100.0f) {
                        doDecodeMultiple(image.crop(0, 0, width, (int) minY), hints, results, xOffset, yOffset);
                    }
                    if (maxX < ((float) (width - 100))) {
                        doDecodeMultiple(image.crop((int) maxX, 0, width - ((int) maxX), height), hints, results, xOffset + ((int) maxX), yOffset);
                    }
                    if (maxY < ((float) (height - 100))) {
                        doDecodeMultiple(image.crop(0, (int) maxY, width, height - ((int) maxY)), hints, results, xOffset, yOffset + ((int) maxY));
                    }
                }
            }
        } catch (ReaderException e) {
        }
    }

    private static Result translateResultPoints(Result result, int xOffset, int yOffset) {
        ResultPoint[] oldResultPoints = result.getResultPoints();
        ResultPoint[] newResultPoints = new ResultPoint[oldResultPoints.length];
        for (int i = 0; i < oldResultPoints.length; i++) {
            ResultPoint oldPoint = oldResultPoints[i];
            newResultPoints[i] = new ResultPoint(oldPoint.getX() + ((float) xOffset), oldPoint.getY() + ((float) yOffset));
        }
        return new Result(result.getText(), result.getRawBytes(), newResultPoints, result.getBarcodeFormat());
    }
}
