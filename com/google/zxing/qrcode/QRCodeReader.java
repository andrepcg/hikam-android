package com.google.zxing.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.qrcode.decoder.Decoder;
import com.google.zxing.qrcode.detector.Detector;
import java.util.Hashtable;

public class QRCodeReader implements Reader {
    private static final ResultPoint[] NO_POINTS = new ResultPoint[0];
    private final Decoder decoder = new Decoder();

    protected Decoder getDecoder() {
        return this.decoder;
    }

    public Result decode(BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException {
        return decode(image, null);
    }

    public Result decode(BinaryBitmap image, Hashtable hints) throws NotFoundException, ChecksumException, FormatException {
        DecoderResult decoderResult;
        ResultPoint[] points;
        if (hints == null || !hints.containsKey(DecodeHintType.PURE_BARCODE)) {
            DetectorResult detectorResult = new Detector(image.getBlackMatrix()).detect(hints);
            decoderResult = this.decoder.decode(detectorResult.getBits(), hints);
            points = detectorResult.getPoints();
        } else {
            decoderResult = this.decoder.decode(extractPureBits(image.getBlackMatrix()), hints);
            points = NO_POINTS;
        }
        Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.QR_CODE);
        if (decoderResult.getByteSegments() != null) {
            result.putMetadata(ResultMetadataType.BYTE_SEGMENTS, decoderResult.getByteSegments());
        }
        if (decoderResult.getECLevel() != null) {
            result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, decoderResult.getECLevel().toString());
        }
        return result;
    }

    public void reset() {
    }

    public static BitMatrix extractPureBits(BitMatrix image) throws NotFoundException {
        int height = image.getHeight();
        int width = image.getWidth();
        int minDimension = Math.min(height, width);
        int[] leftTopBlack = image.getTopLeftOnBit();
        if (leftTopBlack == null) {
            throw NotFoundException.getNotFoundInstance();
        }
        int x = leftTopBlack[0];
        int y = leftTopBlack[1];
        while (x < minDimension && y < minDimension && image.get(x, y)) {
            x++;
            y++;
        }
        if (x == minDimension || y == minDimension) {
            throw NotFoundException.getNotFoundInstance();
        }
        int moduleSize = x - leftTopBlack[0];
        if (moduleSize == 0) {
            throw NotFoundException.getNotFoundInstance();
        }
        int rowEndOfSymbol = width - 1;
        while (rowEndOfSymbol > x && !image.get(rowEndOfSymbol, y)) {
            rowEndOfSymbol--;
        }
        if (rowEndOfSymbol <= x) {
            throw NotFoundException.getNotFoundInstance();
        }
        rowEndOfSymbol++;
        if ((rowEndOfSymbol - x) % moduleSize != 0) {
            throw NotFoundException.getNotFoundInstance();
        }
        int dimension = ((rowEndOfSymbol - x) / moduleSize) + 1;
        int backOffAmount = moduleSize == 1 ? 1 : moduleSize >> 1;
        x -= backOffAmount;
        y -= backOffAmount;
        if (((dimension - 1) * moduleSize) + x >= width || ((dimension - 1) * moduleSize) + y >= height) {
            throw NotFoundException.getNotFoundInstance();
        }
        BitMatrix bits = new BitMatrix(dimension);
        for (int i = 0; i < dimension; i++) {
            int iOffset = y + (i * moduleSize);
            for (int j = 0; j < dimension; j++) {
                if (image.get((j * moduleSize) + x, iOffset)) {
                    bits.set(j, i);
                }
            }
        }
        return bits;
    }
}
