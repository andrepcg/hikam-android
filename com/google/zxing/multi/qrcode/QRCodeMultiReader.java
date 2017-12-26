package com.google.zxing.multi.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.multi.MultipleBarcodeReader;
import com.google.zxing.multi.qrcode.detector.MultiDetector;
import com.google.zxing.qrcode.QRCodeReader;
import java.util.Hashtable;
import java.util.Vector;

public final class QRCodeMultiReader extends QRCodeReader implements MultipleBarcodeReader {
    private static final Result[] EMPTY_RESULT_ARRAY = new Result[0];

    public Result[] decodeMultiple(BinaryBitmap image) throws NotFoundException {
        return decodeMultiple(image, null);
    }

    public Result[] decodeMultiple(BinaryBitmap image, Hashtable hints) throws NotFoundException {
        int i;
        Vector results = new Vector();
        DetectorResult[] detectorResult = new MultiDetector(image.getBlackMatrix()).detectMulti(hints);
        for (i = 0; i < detectorResult.length; i++) {
            try {
                DecoderResult decoderResult = getDecoder().decode(detectorResult[i].getBits());
                Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), detectorResult[i].getPoints(), BarcodeFormat.QR_CODE);
                if (decoderResult.getByteSegments() != null) {
                    result.putMetadata(ResultMetadataType.BYTE_SEGMENTS, decoderResult.getByteSegments());
                }
                if (decoderResult.getECLevel() != null) {
                    result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, decoderResult.getECLevel().toString());
                }
                results.addElement(result);
            } catch (ReaderException e) {
            }
        }
        if (results.isEmpty()) {
            return EMPTY_RESULT_ARRAY;
        }
        Result[] resultArray = new Result[results.size()];
        for (i = 0; i < results.size(); i++) {
            resultArray[i] = (Result) results.elementAt(i);
        }
        return resultArray;
    }
}
