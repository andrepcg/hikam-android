package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

final class GeneralAppIdDecoder {
    private final StringBuffer buffer = new StringBuffer();
    private final CurrentParsingState current = new CurrentParsingState();
    private final BitArray information;

    GeneralAppIdDecoder(BitArray information) {
        this.information = information;
    }

    String decodeAllCodes(StringBuffer buff, int initialPosition) throws NotFoundException {
        int currentPosition = initialPosition;
        String remaining = null;
        while (true) {
            DecodedInformation info = decodeGeneralPurposeField(currentPosition, remaining);
            buff.append(FieldParser.parseFieldsInGeneralPurpose(info.getNewString()));
            if (info.isRemaining()) {
                remaining = String.valueOf(info.getRemainingValue());
            } else {
                remaining = null;
            }
            if (currentPosition == info.getNewPosition()) {
                return buff.toString();
            }
            currentPosition = info.getNewPosition();
        }
    }

    private boolean isStillNumeric(int pos) {
        if (pos + 7 <= this.information.size) {
            for (int i = pos; i < pos + 3; i++) {
                if (this.information.get(i)) {
                    return true;
                }
            }
            return this.information.get(pos + 3);
        } else if (pos + 4 <= this.information.size) {
            return true;
        } else {
            return false;
        }
    }

    private DecodedNumeric decodeNumeric(int pos) {
        int numeric;
        if (pos + 7 > this.information.size) {
            numeric = extractNumericValueFromBitArray(pos, 4);
            if (numeric == 0) {
                return new DecodedNumeric(this.information.size, 10, 10);
            }
            return new DecodedNumeric(this.information.size, numeric - 1, 10);
        }
        numeric = extractNumericValueFromBitArray(pos, 7);
        return new DecodedNumeric(pos + 7, (numeric - 8) / 11, (numeric - 8) % 11);
    }

    int extractNumericValueFromBitArray(int pos, int bits) {
        return extractNumericValueFromBitArray(this.information, pos, bits);
    }

    static int extractNumericValueFromBitArray(BitArray information, int pos, int bits) {
        if (bits > 32) {
            throw new IllegalArgumentException("extractNumberValueFromBitArray can't handle more than 32 bits");
        }
        int value = 0;
        for (int i = 0; i < bits; i++) {
            if (information.get(pos + i)) {
                value |= 1 << ((bits - i) - 1);
            }
        }
        return value;
    }

    DecodedInformation decodeGeneralPurposeField(int pos, String remaining) {
        this.buffer.setLength(0);
        if (remaining != null) {
            this.buffer.append(remaining);
        }
        this.current.position = pos;
        DecodedInformation lastDecoded = parseBlocks();
        if (lastDecoded == null || !lastDecoded.isRemaining()) {
            return new DecodedInformation(this.current.position, this.buffer.toString());
        }
        return new DecodedInformation(this.current.position, this.buffer.toString(), lastDecoded.getRemainingValue());
    }

    private DecodedInformation parseBlocks() {
        BlockParsedResult result;
        boolean isFinished;
        do {
            int initialPosition = this.current.position;
            if (this.current.isAlpha()) {
                result = parseAlphaBlock();
                isFinished = result.isFinished();
            } else if (this.current.isIsoIec646()) {
                result = parseIsoIec646Block();
                isFinished = result.isFinished();
            } else {
                result = parseNumericBlock();
                isFinished = result.isFinished();
            }
            if (!(initialPosition != this.current.position) && !isFinished) {
                break;
            }
        } while (!isFinished);
        return result.getDecodedInformation();
    }

    private BlockParsedResult parseNumericBlock() {
        while (isStillNumeric(this.current.position)) {
            DecodedNumeric numeric = decodeNumeric(this.current.position);
            this.current.position = numeric.getNewPosition();
            if (numeric.isFirstDigitFNC1()) {
                DecodedInformation information;
                if (numeric.isSecondDigitFNC1()) {
                    information = new DecodedInformation(this.current.position, this.buffer.toString());
                } else {
                    information = new DecodedInformation(this.current.position, this.buffer.toString(), numeric.getSecondDigit());
                }
                return new BlockParsedResult(information, true);
            }
            this.buffer.append(numeric.getFirstDigit());
            if (numeric.isSecondDigitFNC1()) {
                return new BlockParsedResult(new DecodedInformation(this.current.position, this.buffer.toString()), true);
            }
            this.buffer.append(numeric.getSecondDigit());
        }
        if (isNumericToAlphaNumericLatch(this.current.position)) {
            this.current.setAlpha();
            CurrentParsingState currentParsingState = this.current;
            currentParsingState.position += 4;
        }
        return new BlockParsedResult(false);
    }

    private BlockParsedResult parseIsoIec646Block() {
        while (isStillIsoIec646(this.current.position)) {
            DecodedChar iso = decodeIsoIec646(this.current.position);
            this.current.position = iso.getNewPosition();
            if (iso.isFNC1()) {
                return new BlockParsedResult(new DecodedInformation(this.current.position, this.buffer.toString()), true);
            }
            this.buffer.append(iso.getValue());
        }
        CurrentParsingState currentParsingState;
        if (isAlphaOr646ToNumericLatch(this.current.position)) {
            currentParsingState = this.current;
            currentParsingState.position += 3;
            this.current.setNumeric();
        } else if (isAlphaTo646ToAlphaLatch(this.current.position)) {
            if (this.current.position + 5 < this.information.size) {
                currentParsingState = this.current;
                currentParsingState.position += 5;
            } else {
                this.current.position = this.information.size;
            }
            this.current.setAlpha();
        }
        return new BlockParsedResult(false);
    }

    private BlockParsedResult parseAlphaBlock() {
        while (isStillAlpha(this.current.position)) {
            DecodedChar alpha = decodeAlphanumeric(this.current.position);
            this.current.position = alpha.getNewPosition();
            if (alpha.isFNC1()) {
                return new BlockParsedResult(new DecodedInformation(this.current.position, this.buffer.toString()), true);
            }
            this.buffer.append(alpha.getValue());
        }
        CurrentParsingState currentParsingState;
        if (isAlphaOr646ToNumericLatch(this.current.position)) {
            currentParsingState = this.current;
            currentParsingState.position += 3;
            this.current.setNumeric();
        } else if (isAlphaTo646ToAlphaLatch(this.current.position)) {
            if (this.current.position + 5 < this.information.size) {
                currentParsingState = this.current;
                currentParsingState.position += 5;
            } else {
                this.current.position = this.information.size;
            }
            this.current.setIsoIec646();
        }
        return new BlockParsedResult(false);
    }

    private boolean isStillIsoIec646(int pos) {
        boolean z = true;
        if (pos + 5 > this.information.size) {
            return false;
        }
        int fiveBitValue = extractNumericValueFromBitArray(pos, 5);
        if (fiveBitValue >= 5 && fiveBitValue < 16) {
            return true;
        }
        if (pos + 7 > this.information.size) {
            return false;
        }
        int sevenBitValue = extractNumericValueFromBitArray(pos, 7);
        if (sevenBitValue >= 64 && sevenBitValue < 116) {
            return true;
        }
        if (pos + 8 > this.information.size) {
            return false;
        }
        int eightBitValue = extractNumericValueFromBitArray(pos, 8);
        if (eightBitValue < 232 || eightBitValue >= 253) {
            z = false;
        }
        return z;
    }

    private DecodedChar decodeIsoIec646(int pos) {
        int fiveBitValue = extractNumericValueFromBitArray(pos, 5);
        if (fiveBitValue == 15) {
            return new DecodedChar(pos + 5, '$');
        }
        if (fiveBitValue >= 5 && fiveBitValue < 15) {
            return new DecodedChar(pos + 5, (char) ((fiveBitValue + 48) - 5));
        }
        int sevenBitValue = extractNumericValueFromBitArray(pos, 7);
        if (sevenBitValue >= 64 && sevenBitValue < 90) {
            return new DecodedChar(pos + 7, (char) (sevenBitValue + 1));
        }
        if (sevenBitValue >= 90 && sevenBitValue < 116) {
            return new DecodedChar(pos + 7, (char) (sevenBitValue + 7));
        }
        int eightBitValue = extractNumericValueFromBitArray(pos, 8);
        switch (eightBitValue) {
            case 232:
                return new DecodedChar(pos + 8, '!');
            case 233:
                return new DecodedChar(pos + 8, '\"');
            case 234:
                return new DecodedChar(pos + 8, '%');
            case 235:
                return new DecodedChar(pos + 8, '&');
            case 236:
                return new DecodedChar(pos + 8, '\'');
            case 237:
                return new DecodedChar(pos + 8, '(');
            case 238:
                return new DecodedChar(pos + 8, ')');
            case 239:
                return new DecodedChar(pos + 8, '*');
            case 240:
                return new DecodedChar(pos + 8, '+');
            case 241:
                return new DecodedChar(pos + 8, ',');
            case 242:
                return new DecodedChar(pos + 8, '-');
            case 243:
                return new DecodedChar(pos + 8, '.');
            case 244:
                return new DecodedChar(pos + 8, '/');
            case 245:
                return new DecodedChar(pos + 8, ':');
            case 246:
                return new DecodedChar(pos + 8, ';');
            case 247:
                return new DecodedChar(pos + 8, '<');
            case 248:
                return new DecodedChar(pos + 8, '=');
            case 249:
                return new DecodedChar(pos + 8, '>');
            case 250:
                return new DecodedChar(pos + 8, '?');
            case 251:
                return new DecodedChar(pos + 8, '_');
            case 252:
                return new DecodedChar(pos + 8, ' ');
            default:
                throw new RuntimeException(new StringBuffer().append("Decoding invalid ISO/IEC 646 value: ").append(eightBitValue).toString());
        }
    }

    private boolean isStillAlpha(int pos) {
        boolean z = true;
        if (pos + 5 > this.information.size) {
            return false;
        }
        int fiveBitValue = extractNumericValueFromBitArray(pos, 5);
        if (fiveBitValue >= 5 && fiveBitValue < 16) {
            return true;
        }
        if (pos + 6 > this.information.size) {
            return false;
        }
        int sixBitValue = extractNumericValueFromBitArray(pos, 6);
        if (sixBitValue < 16 || sixBitValue >= 63) {
            z = false;
        }
        return z;
    }

    private DecodedChar decodeAlphanumeric(int pos) {
        int fiveBitValue = extractNumericValueFromBitArray(pos, 5);
        if (fiveBitValue == 15) {
            return new DecodedChar(pos + 5, '$');
        }
        if (fiveBitValue >= 5 && fiveBitValue < 15) {
            return new DecodedChar(pos + 5, (char) ((fiveBitValue + 48) - 5));
        }
        int sixBitValue = extractNumericValueFromBitArray(pos, 6);
        if (sixBitValue >= 32 && sixBitValue < 58) {
            return new DecodedChar(pos + 6, (char) (sixBitValue + 33));
        }
        switch (sixBitValue) {
            case 58:
                return new DecodedChar(pos + 6, '*');
            case 59:
                return new DecodedChar(pos + 6, ',');
            case 60:
                return new DecodedChar(pos + 6, '-');
            case 61:
                return new DecodedChar(pos + 6, '.');
            case 62:
                return new DecodedChar(pos + 6, '/');
            default:
                throw new RuntimeException(new StringBuffer().append("Decoding invalid alphanumeric value: ").append(sixBitValue).toString());
        }
    }

    private boolean isAlphaTo646ToAlphaLatch(int pos) {
        if (pos + 1 > this.information.size) {
            return false;
        }
        int i = 0;
        while (i < 5 && i + pos < this.information.size) {
            if (i == 2) {
                if (!this.information.get(pos + 2)) {
                    return false;
                }
            } else if (this.information.get(pos + i)) {
                return false;
            }
            i++;
        }
        return true;
    }

    private boolean isAlphaOr646ToNumericLatch(int pos) {
        if (pos + 3 > this.information.size) {
            return false;
        }
        for (int i = pos; i < pos + 3; i++) {
            if (this.information.get(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean isNumericToAlphaNumericLatch(int pos) {
        if (pos + 1 > this.information.size) {
            return false;
        }
        int i = 0;
        while (i < 4 && i + pos < this.information.size) {
            if (this.information.get(pos + i)) {
                return false;
            }
            i++;
        }
        return true;
    }
}
