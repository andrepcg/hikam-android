package com.google.zxing.oned.rss.expanded.decoders;

final class DecodedNumeric extends DecodedObject {
    static final int FNC1 = 10;
    private final int firstDigit;
    private final int secondDigit;

    DecodedNumeric(int newPosition, int firstDigit, int secondDigit) {
        super(newPosition);
        this.firstDigit = firstDigit;
        this.secondDigit = secondDigit;
        if (this.firstDigit < 0 || this.firstDigit > 10) {
            throw new IllegalArgumentException(new StringBuffer().append("Invalid firstDigit: ").append(firstDigit).toString());
        } else if (this.secondDigit < 0 || this.secondDigit > 10) {
            throw new IllegalArgumentException(new StringBuffer().append("Invalid secondDigit: ").append(secondDigit).toString());
        }
    }

    int getFirstDigit() {
        return this.firstDigit;
    }

    int getSecondDigit() {
        return this.secondDigit;
    }

    int getValue() {
        return (this.firstDigit * 10) + this.secondDigit;
    }

    boolean isFirstDigitFNC1() {
        return this.firstDigit == 10;
    }

    boolean isSecondDigitFNC1() {
        return this.secondDigit == 10;
    }

    boolean isAnyFNC1() {
        return this.firstDigit == 10 || this.secondDigit == 10;
    }
}
