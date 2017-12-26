package com.google.zxing.qrcode.decoder;

import com.google.zxing.common.BitMatrix;

abstract class DataMask {
    private static final DataMask[] DATA_MASKS = new DataMask[]{new DataMask000(null), new DataMask001(null), new DataMask010(null), new DataMask011(null), new DataMask100(null), new DataMask101(null), new DataMask110(null), new DataMask111(null)};

    static class C02901 {
    }

    private static class DataMask000 extends DataMask {
        private DataMask000() {
            super(null);
        }

        DataMask000(C02901 x0) {
            this();
        }

        boolean isMasked(int i, int j) {
            return ((i + j) & 1) == 0;
        }
    }

    private static class DataMask001 extends DataMask {
        private DataMask001() {
            super(null);
        }

        DataMask001(C02901 x0) {
            this();
        }

        boolean isMasked(int i, int j) {
            return (i & 1) == 0;
        }
    }

    private static class DataMask010 extends DataMask {
        private DataMask010() {
            super(null);
        }

        DataMask010(C02901 x0) {
            this();
        }

        boolean isMasked(int i, int j) {
            return j % 3 == 0;
        }
    }

    private static class DataMask011 extends DataMask {
        private DataMask011() {
            super(null);
        }

        DataMask011(C02901 x0) {
            this();
        }

        boolean isMasked(int i, int j) {
            return (i + j) % 3 == 0;
        }
    }

    private static class DataMask100 extends DataMask {
        private DataMask100() {
            super(null);
        }

        DataMask100(C02901 x0) {
            this();
        }

        boolean isMasked(int i, int j) {
            return (((i >>> 1) + (j / 3)) & 1) == 0;
        }
    }

    private static class DataMask101 extends DataMask {
        private DataMask101() {
            super(null);
        }

        DataMask101(C02901 x0) {
            this();
        }

        boolean isMasked(int i, int j) {
            int temp = i * j;
            return (temp & 1) + (temp % 3) == 0;
        }
    }

    private static class DataMask110 extends DataMask {
        private DataMask110() {
            super(null);
        }

        DataMask110(C02901 x0) {
            this();
        }

        boolean isMasked(int i, int j) {
            int temp = i * j;
            return (((temp & 1) + (temp % 3)) & 1) == 0;
        }
    }

    private static class DataMask111 extends DataMask {
        private DataMask111() {
            super(null);
        }

        DataMask111(C02901 x0) {
            this();
        }

        boolean isMasked(int i, int j) {
            return ((((i + j) & 1) + ((i * j) % 3)) & 1) == 0;
        }
    }

    abstract boolean isMasked(int i, int i2);

    DataMask(C02901 x0) {
        this();
    }

    private DataMask() {
    }

    final void unmaskBitMatrix(BitMatrix bits, int dimension) {
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (isMasked(i, j)) {
                    bits.flip(j, i);
                }
            }
        }
    }

    static DataMask forReference(int reference) {
        if (reference >= 0 && reference <= 7) {
            return DATA_MASKS[reference];
        }
        throw new IllegalArgumentException();
    }
}
