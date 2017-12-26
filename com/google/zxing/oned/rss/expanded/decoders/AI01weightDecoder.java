package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.common.BitArray;

abstract class AI01weightDecoder extends AI01decoder {
    protected abstract void addWeightCode(StringBuffer stringBuffer, int i);

    protected abstract int checkWeight(int i);

    AI01weightDecoder(BitArray information) {
        super(information);
    }

    protected void encodeCompressedWeight(StringBuffer buf, int currentPos, int weightSize) {
        int originalWeightNumeric = this.generalDecoder.extractNumericValueFromBitArray(currentPos, weightSize);
        addWeightCode(buf, originalWeightNumeric);
        int weightNumeric = checkWeight(originalWeightNumeric);
        int currentDivisor = BZip2Constants.BASEBLOCKSIZE;
        for (int i = 0; i < 5; i++) {
            if (weightNumeric / currentDivisor == 0) {
                buf.append('0');
            }
            currentDivisor /= 10;
        }
        buf.append(weightNumeric);
    }
}
