package com.google.zxing;

public class ResultPoint {
    private final float f14x;
    private final float f15y;

    public ResultPoint(float x, float y) {
        this.f14x = x;
        this.f15y = y;
    }

    public final float getX() {
        return this.f14x;
    }

    public final float getY() {
        return this.f15y;
    }

    public boolean equals(Object other) {
        if (!(other instanceof ResultPoint)) {
            return false;
        }
        ResultPoint otherPoint = (ResultPoint) other;
        if (this.f14x == otherPoint.f14x && this.f15y == otherPoint.f15y) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (Float.floatToIntBits(this.f14x) * 31) + Float.floatToIntBits(this.f15y);
    }

    public String toString() {
        StringBuffer result = new StringBuffer(25);
        result.append('(');
        result.append(this.f14x);
        result.append(',');
        result.append(this.f15y);
        result.append(')');
        return result.toString();
    }

    public static void orderBestPatterns(ResultPoint[] patterns) {
        ResultPoint pointB;
        ResultPoint pointA;
        ResultPoint pointC;
        float zeroOneDistance = distance(patterns[0], patterns[1]);
        float oneTwoDistance = distance(patterns[1], patterns[2]);
        float zeroTwoDistance = distance(patterns[0], patterns[2]);
        if (oneTwoDistance >= zeroOneDistance && oneTwoDistance >= zeroTwoDistance) {
            pointB = patterns[0];
            pointA = patterns[1];
            pointC = patterns[2];
        } else if (zeroTwoDistance < oneTwoDistance || zeroTwoDistance < zeroOneDistance) {
            pointB = patterns[2];
            pointA = patterns[0];
            pointC = patterns[1];
        } else {
            pointB = patterns[1];
            pointA = patterns[0];
            pointC = patterns[2];
        }
        if (crossProductZ(pointA, pointB, pointC) < 0.0f) {
            ResultPoint temp = pointA;
            pointA = pointC;
            pointC = temp;
        }
        patterns[0] = pointA;
        patterns[1] = pointB;
        patterns[2] = pointC;
    }

    public static float distance(ResultPoint pattern1, ResultPoint pattern2) {
        float xDiff = pattern1.getX() - pattern2.getX();
        float yDiff = pattern1.getY() - pattern2.getY();
        return (float) Math.sqrt((double) ((xDiff * xDiff) + (yDiff * yDiff)));
    }

    private static float crossProductZ(ResultPoint pointA, ResultPoint pointB, ResultPoint pointC) {
        float bX = pointB.f14x;
        float bY = pointB.f15y;
        return ((pointC.f14x - bX) * (pointA.f15y - bY)) - ((pointC.f15y - bY) * (pointA.f14x - bX));
    }
}
