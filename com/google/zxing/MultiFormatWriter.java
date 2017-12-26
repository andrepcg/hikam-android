package com.google.zxing;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.Code39Writer;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.oned.EAN8Writer;
import com.google.zxing.oned.ITFWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import java.util.Hashtable;

public final class MultiFormatWriter implements Writer {
    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height) throws WriterException {
        return encode(contents, format, width, height, null);
    }

    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Hashtable hints) throws WriterException {
        Writer writer;
        if (format == BarcodeFormat.EAN_8) {
            writer = new EAN8Writer();
        } else if (format == BarcodeFormat.EAN_13) {
            writer = new EAN13Writer();
        } else if (format == BarcodeFormat.QR_CODE) {
            writer = new QRCodeWriter();
        } else if (format == BarcodeFormat.CODE_39) {
            writer = new Code39Writer();
        } else if (format == BarcodeFormat.CODE_128) {
            writer = new Code128Writer();
        } else if (format == BarcodeFormat.ITF) {
            writer = new ITFWriter();
        } else {
            throw new IllegalArgumentException(new StringBuffer().append("No encoder available for format ").append(format).toString());
        }
        return writer.encode(contents, format, width, height, hints);
    }
}
