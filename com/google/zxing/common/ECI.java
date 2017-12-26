package com.google.zxing.common;

public abstract class ECI {
    private final int value;

    ECI(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ECI getECIByValue(int value) {
        if (value < 0 || value > 999999) {
            throw new IllegalArgumentException(new StringBuffer().append("Bad ECI value: ").append(value).toString());
        } else if (value < 900) {
            return CharacterSetECI.getCharacterSetECIByValue(value);
        } else {
            return null;
        }
    }
}
