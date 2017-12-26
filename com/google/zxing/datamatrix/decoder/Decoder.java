package com.google.zxing.datamatrix.decoder;

import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.reedsolomon.GF256;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;

public final class Decoder {
    private final ReedSolomonDecoder rsDecoder = new ReedSolomonDecoder(GF256.DATA_MATRIX_FIELD);

    public DecoderResult decode(boolean[][] image) throws FormatException, ChecksumException {
        int dimension = image.length;
        BitMatrix bits = new BitMatrix(dimension);
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (image[i][j]) {
                    bits.set(j, i);
                }
            }
        }
        return decode(bits);
    }

    public DecoderResult decode(BitMatrix bits) throws FormatException, ChecksumException {
        int i;
        BitMatrixParser parser = new BitMatrixParser(bits);
        DataBlock[] dataBlocks = DataBlock.getDataBlocks(parser.readCodewords(), parser.readVersion(bits));
        int totalBytes = 0;
        for (DataBlock numDataCodewords : dataBlocks) {
            totalBytes += numDataCodewords.getNumDataCodewords();
        }
        byte[] resultBytes = new byte[totalBytes];
        int resultOffset = 0;
        int j = 0;
        while (j < dataBlocks.length) {
            DataBlock dataBlock = dataBlocks[j];
            byte[] codewordBytes = dataBlock.getCodewords();
            int numDataCodewords2 = dataBlock.getNumDataCodewords();
            correctErrors(codewordBytes, numDataCodewords2);
            i = 0;
            int resultOffset2 = resultOffset;
            while (i < numDataCodewords2) {
                resultOffset = resultOffset2 + 1;
                resultBytes[resultOffset2] = codewordBytes[i];
                i++;
                resultOffset2 = resultOffset;
            }
            j++;
            resultOffset = resultOffset2;
        }
        return DecodedBitStreamParser.decode(resultBytes);
    }

    private void correctErrors(byte[] codewordBytes, int numDataCodewords) throws ChecksumException {
        int i;
        int numCodewords = codewordBytes.length;
        int[] codewordsInts = new int[numCodewords];
        for (i = 0; i < numCodewords; i++) {
            codewordsInts[i] = codewordBytes[i] & 255;
        }
        try {
            this.rsDecoder.decode(codewordsInts, codewordBytes.length - numDataCodewords);
            for (i = 0; i < numDataCodewords; i++) {
                codewordBytes[i] = (byte) codewordsInts[i];
            }
        } catch (ReedSolomonException e) {
            throw ChecksumException.getChecksumInstance();
        }
    }
}
