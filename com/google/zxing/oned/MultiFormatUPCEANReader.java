package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.BitArray;
import java.util.Hashtable;
import java.util.Vector;

public final class MultiFormatUPCEANReader extends OneDReader {
    private final Vector readers;

    public MultiFormatUPCEANReader(Hashtable hints) {
        Vector possibleFormats = hints == null ? null : (Vector) hints.get(DecodeHintType.POSSIBLE_FORMATS);
        this.readers = new Vector();
        if (possibleFormats != null) {
            if (possibleFormats.contains(BarcodeFormat.EAN_13)) {
                this.readers.addElement(new EAN13Reader());
            } else if (possibleFormats.contains(BarcodeFormat.UPC_A)) {
                this.readers.addElement(new UPCAReader());
            }
            if (possibleFormats.contains(BarcodeFormat.EAN_8)) {
                this.readers.addElement(new EAN8Reader());
            }
            if (possibleFormats.contains(BarcodeFormat.UPC_E)) {
                this.readers.addElement(new UPCEReader());
            }
        }
        if (this.readers.isEmpty()) {
            this.readers.addElement(new EAN13Reader());
            this.readers.addElement(new EAN8Reader());
            this.readers.addElement(new UPCEReader());
        }
    }

    public Result decodeRow(int rowNumber, BitArray row, Hashtable hints) throws NotFoundException {
        int[] startGuardPattern = UPCEANReader.findStartGuardPattern(row);
        int size = this.readers.size();
        int i = 0;
        while (i < size) {
            try {
                Result result = ((UPCEANReader) this.readers.elementAt(i)).decodeRow(rowNumber, row, startGuardPattern, hints);
                boolean ean13MayBeUPCA = BarcodeFormat.EAN_13.equals(result.getBarcodeFormat()) && result.getText().charAt(0) == '0';
                Vector possibleFormats = hints == null ? null : (Vector) hints.get(DecodeHintType.POSSIBLE_FORMATS);
                boolean canReturnUPCA = possibleFormats == null || possibleFormats.contains(BarcodeFormat.UPC_A);
                if (ean13MayBeUPCA && canReturnUPCA) {
                    return new Result(result.getText().substring(1), null, result.getResultPoints(), BarcodeFormat.UPC_A);
                }
                return result;
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
