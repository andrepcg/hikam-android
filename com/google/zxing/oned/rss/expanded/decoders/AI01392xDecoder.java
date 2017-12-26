package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

final class AI01392xDecoder extends AI01decoder {
    private static final int headerSize = 8;
    private static final int lastDigitSize = 2;

    AI01392xDecoder(BitArray information) {
        super(information);
    }

    public String parseInformation() throws NotFoundException {
        if (this.information.size < 48) {
            throw NotFoundException.getNotFoundInstance();
        }
        StringBuffer buf = new StringBuffer();
        encodeCompressedGtin(buf, 8);
        int lastAIdigit = this.generalDecoder.extractNumericValueFromBitArray(48, 2);
        buf.append("(392");
        buf.append(lastAIdigit);
        buf.append(')');
        buf.append(this.generalDecoder.decodeGeneralPurposeField(50, null).getNewString());
        return buf.toString();
    }
}
