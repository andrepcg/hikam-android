package com.google.zxing.common.reedsolomon;

public final class ReedSolomonDecoder {
    private final GF256 field;

    public ReedSolomonDecoder(GF256 field) {
        this.field = field;
    }

    public void decode(int[] received, int twoS) throws ReedSolomonException {
        int i;
        GF256Poly poly = new GF256Poly(this.field, received);
        int[] syndromeCoefficients = new int[twoS];
        boolean dataMatrix = this.field.equals(GF256.DATA_MATRIX_FIELD);
        boolean noError = true;
        for (i = 0; i < twoS; i++) {
            int i2;
            GF256 gf256 = this.field;
            if (dataMatrix) {
                i2 = i + 1;
            } else {
                i2 = i;
            }
            int eval = poly.evaluateAt(gf256.exp(i2));
            syndromeCoefficients[(syndromeCoefficients.length - 1) - i] = eval;
            if (eval != 0) {
                noError = false;
            }
        }
        if (!noError) {
            GF256Poly syndrome = new GF256Poly(this.field, syndromeCoefficients);
            GF256Poly[] sigmaOmega = runEuclideanAlgorithm(this.field.buildMonomial(twoS, 1), syndrome, twoS);
            GF256Poly sigma = sigmaOmega[0];
            GF256Poly omega = sigmaOmega[1];
            int[] errorLocations = findErrorLocations(sigma);
            int[] errorMagnitudes = findErrorMagnitudes(omega, errorLocations, dataMatrix);
            for (i = 0; i < errorLocations.length; i++) {
                int position = (received.length - 1) - this.field.log(errorLocations[i]);
                if (position < 0) {
                    throw new ReedSolomonException("Bad error location");
                }
                received[position] = GF256.addOrSubtract(received[position], errorMagnitudes[i]);
            }
        }
    }

    private GF256Poly[] runEuclideanAlgorithm(GF256Poly a, GF256Poly b, int R) throws ReedSolomonException {
        if (a.getDegree() < b.getDegree()) {
            GF256Poly temp = a;
            a = b;
            b = temp;
        }
        GF256Poly rLast = a;
        GF256Poly r = b;
        GF256Poly sLast = this.field.getOne();
        GF256Poly s = this.field.getZero();
        GF256Poly tLast = this.field.getZero();
        GF256Poly t = this.field.getOne();
        while (r.getDegree() >= R / 2) {
            GF256Poly rLastLast = rLast;
            GF256Poly sLastLast = sLast;
            GF256Poly tLastLast = tLast;
            rLast = r;
            sLast = s;
            tLast = t;
            if (rLast.isZero()) {
                throw new ReedSolomonException("r_{i-1} was zero");
            }
            r = rLastLast;
            GF256Poly q = this.field.getZero();
            int dltInverse = this.field.inverse(rLast.getCoefficient(rLast.getDegree()));
            while (r.getDegree() >= rLast.getDegree() && !r.isZero()) {
                int degreeDiff = r.getDegree() - rLast.getDegree();
                int scale = this.field.multiply(r.getCoefficient(r.getDegree()), dltInverse);
                q = q.addOrSubtract(this.field.buildMonomial(degreeDiff, scale));
                r = r.addOrSubtract(rLast.multiplyByMonomial(degreeDiff, scale));
            }
            s = q.multiply(sLast).addOrSubtract(sLastLast);
            t = q.multiply(tLast).addOrSubtract(tLastLast);
        }
        int sigmaTildeAtZero = t.getCoefficient(0);
        if (sigmaTildeAtZero == 0) {
            throw new ReedSolomonException("sigmaTilde(0) was zero");
        }
        int inverse = this.field.inverse(sigmaTildeAtZero);
        GF256Poly sigma = t.multiply(inverse);
        GF256Poly omega = r.multiply(inverse);
        return new GF256Poly[]{sigma, omega};
    }

    private int[] findErrorLocations(GF256Poly errorLocator) throws ReedSolomonException {
        int numErrors = errorLocator.getDegree();
        if (numErrors == 1) {
            return new int[]{errorLocator.getCoefficient(1)};
        }
        int[] result = new int[numErrors];
        int e = 0;
        for (int i = 1; i < 256 && e < numErrors; i++) {
            if (errorLocator.evaluateAt(i) == 0) {
                result[e] = this.field.inverse(i);
                e++;
            }
        }
        if (e == numErrors) {
            return result;
        }
        throw new ReedSolomonException("Error locator degree does not match number of roots");
    }

    private int[] findErrorMagnitudes(GF256Poly errorEvaluator, int[] errorLocations, boolean dataMatrix) {
        int s = errorLocations.length;
        int[] result = new int[s];
        for (int i = 0; i < s; i++) {
            int xiInverse = this.field.inverse(errorLocations[i]);
            int denominator = 1;
            for (int j = 0; j < s; j++) {
                if (i != j) {
                    int term = this.field.multiply(errorLocations[j], xiInverse);
                    denominator = this.field.multiply(denominator, (term & 1) == 0 ? term | 1 : term & -2);
                }
            }
            result[i] = this.field.multiply(errorEvaluator.evaluateAt(xiInverse), this.field.inverse(denominator));
            if (dataMatrix) {
                result[i] = this.field.multiply(result[i], xiInverse);
            }
        }
        return result;
    }
}
