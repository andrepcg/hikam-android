package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

final class AI01393xDecoder extends AI01decoder {
    private static final int firstThreeDigitsSize = 10;
    private static final int headerSize = 8;
    private static final int lastDigitSize = 2;

    AI01393xDecoder(BitArray information) {
        super(information);
    }

    public String parseInformation() throws NotFoundException {
        if (this.information.size < 48) {
            throw NotFoundException.getNotFoundInstance();
        }
        StringBuffer buf = new StringBuffer();
        encodeCompressedGtin(buf, 8);
        int lastAIdigit = this.generalDecoder.extractNumericValueFromBitArray(48, 2);
        buf.append("(393");
        buf.append(lastAIdigit);
        buf.append(')');
        int firstThreeDigits = this.generalDecoder.extractNumericValueFromBitArray(50, 10);
        if (firstThreeDigits / 100 == 0) {
            buf.append('0');
        }
        if (firstThreeDigits / 10 == 0) {
            buf.append('0');
        }
        buf.append(firstThreeDigits);
        buf.append(this.generalDecoder.decodeGeneralPurposeField(60, null).getNewString());
        return buf.toString();
    }
}
