package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.BitArray;
import com.google.zxing.oned.rss.RSS14Reader;
import com.google.zxing.oned.rss.expanded.RSSExpandedReader;
import java.util.Hashtable;
import java.util.Vector;

public final class MultiFormatOneDReader extends OneDReader {
    private final Vector readers;

    public MultiFormatOneDReader(Hashtable hints) {
        Vector possibleFormats = hints == null ? null : (Vector) hints.get(DecodeHintType.POSSIBLE_FORMATS);
        boolean useCode39CheckDigit = (hints == null || hints.get(DecodeHintType.ASSUME_CODE_39_CHECK_DIGIT) == null) ? false : true;
        this.readers = new Vector();
        if (possibleFormats != null) {
            if (possibleFormats.contains(BarcodeFormat.EAN_13) || possibleFormats.contains(BarcodeFormat.UPC_A) || possibleFormats.contains(BarcodeFormat.EAN_8) || possibleFormats.contains(BarcodeFormat.UPC_E)) {
                this.readers.addElement(new MultiFormatUPCEANReader(hints));
            }
            if (possibleFormats.contains(BarcodeFormat.CODE_39)) {
                this.readers.addElement(new Code39Reader(useCode39CheckDigit));
            }
            if (possibleFormats.contains(BarcodeFormat.CODE_93)) {
                this.readers.addElement(new Code93Reader());
            }
            if (possibleFormats.contains(BarcodeFormat.CODE_128)) {
                this.readers.addElement(new Code128Reader());
            }
            if (possibleFormats.contains(BarcodeFormat.ITF)) {
                this.readers.addElement(new ITFReader());
            }
            if (possibleFormats.contains(BarcodeFormat.CODABAR)) {
                this.readers.addElement(new CodaBarReader());
            }
            if (possibleFormats.contains(BarcodeFormat.RSS14)) {
                this.readers.addElement(new RSS14Reader());
            }
            if (possibleFormats.contains(BarcodeFormat.RSS_EXPANDED)) {
                this.readers.addElement(new RSSExpandedReader());
            }
        }
        if (this.readers.isEmpty()) {
            this.readers.addElement(new MultiFormatUPCEANReader(hints));
            this.readers.addElement(new Code39Reader());
            this.readers.addElement(new Code93Reader());
            this.readers.addElement(new Code128Reader());
            this.readers.addElement(new ITFReader());
            this.readers.addElement(new RSS14Reader());
            this.readers.addElement(new RSSExpandedReader());
        }
    }

    public Result decodeRow(int rowNumber, BitArray row, Hashtable hints) throws NotFoundException {
        int i = 0;
        while (i < this.readers.size()) {
            try {
                return ((OneDReader) this.readers.elementAt(i)).decodeRow(rowNumber, row, hints);
            } catch (ReaderException e) {
                i++;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    public void reset() {
        int size = this.readers.size();
        for (int i = 0; i < size; i++) {
            ((Reader) this.readers.elementAt(i)).reset();
        }
    }
}
