package com.google.zxing.datamatrix.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.Collections;
import com.google.zxing.common.Comparator;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.common.GridSampler;
import com.google.zxing.common.detector.WhiteRectangleDetector;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class Detector {
    private static final Integer[] INTEGERS = new Integer[]{new Integer(0), new Integer(1), new Integer(2), new Integer(3), new Integer(4)};
    private final BitMatrix image;
    private final WhiteRectangleDetector rectangleDetector;

    static class C02881 {
    }

    private static class ResultPointsAndTransitions {
        private final ResultPoint from;
        private final ResultPoint to;
        private final int transitions;

        ResultPointsAndTransitions(ResultPoint x0, ResultPoint x1, int x2, C02881 x3) {
            this(x0, x1, x2);
        }

        private ResultPointsAndTransitions(ResultPoint from, ResultPoint to, int transitions) {
            this.from = from;
            this.to = to;
            this.transitions = transitions;
        }

        public ResultPoint getFrom() {
            return this.from;
        }

        public ResultPoint getTo() {
            return this.to;
        }

        public int getTransitions() {
            return this.transitions;
        }

        public String toString() {
            return new StringBuffer().append(this.from).append("/").append(this.to).append('/').append(this.transitions).toString();
        }
    }

    private static class ResultPointsAndTransitionsComparator implements Comparator {
        private ResultPointsAndTransitionsComparator() {
        }

        ResultPointsAndTransitionsComparator(C02881 x0) {
            this();
        }

        public int compare(Object o1, Object o2) {
            return ((ResultPointsAndTransitions) o1).getTransitions() - ((ResultPointsAndTransitions) o2).getTransitions();
        }
    }

    public Detector(BitMatrix image) {
        this.image = image;
        this.rectangleDetector = new WhiteRectangleDetector(image);
    }

    public DetectorResult detect() throws NotFoundException {
        ResultPoint[] cornerPoints = this.rectangleDetector.detect();
        ResultPoint pointA = cornerPoints[0];
        ResultPoint pointB = cornerPoints[1];
        ResultPoint pointC = cornerPoints[2];
        ResultPoint pointD = cornerPoints[3];
        Vector vector = new Vector(4);
        vector.addElement(transitionsBetween(pointA, pointB));
        vector.addElement(transitionsBetween(pointA, pointC));
        vector.addElement(transitionsBetween(pointB, pointD));
        vector.addElement(transitionsBetween(pointC, pointD));
        Collections.insertionSort(vector, new ResultPointsAndTransitionsComparator(null));
        ResultPointsAndTransitions lSideOne = (ResultPointsAndTransitions) vector.elementAt(0);
        ResultPointsAndTransitions lSideTwo = (ResultPointsAndTransitions) vector.elementAt(1);
        Hashtable pointCount = new Hashtable();
        increment(pointCount, lSideOne.getFrom());
        increment(pointCount, lSideOne.getTo());
        increment(pointCount, lSideTwo.getFrom());
        increment(pointCount, lSideTwo.getTo());
        ResultPoint maybeTopLeft = null;
        ResultPoint bottomLeft = null;
        ResultPoint maybeBottomRight = null;
        Enumeration points = pointCount.keys();
        while (points.hasMoreElements()) {
            ResultPoint point = (ResultPoint) points.nextElement();
            if (((Integer) pointCount.get(point)).intValue() == 2) {
                bottomLeft = point;
            } else if (maybeTopLeft == null) {
                maybeTopLeft = point;
            } else {
                maybeBottomRight = point;
            }
        }
        if (maybeTopLeft == null || bottomLeft == null || maybeBottomRight == null) {
            throw NotFoundException.getNotFoundInstance();
        }
        ResultPoint topRight;
        ResultPoint[] corners = new ResultPoint[]{maybeTopLeft, bottomLeft, maybeBottomRight};
        ResultPoint.orderBestPatterns(corners);
        ResultPoint bottomRight = corners[0];
        bottomLeft = corners[1];
        ResultPoint topLeft = corners[2];
        if (!pointCount.containsKey(pointA)) {
            topRight = pointA;
        } else if (!pointCount.containsKey(pointB)) {
            topRight = pointB;
        } else if (pointCount.containsKey(pointC)) {
            topRight = pointD;
        } else {
            topRight = pointC;
        }
        int dimension = Math.min(transitionsBetween(topLeft, topRight).getTransitions(), transitionsBetween(bottomRight, topRight).getTransitions());
        if ((dimension & 1) == 1) {
            dimension++;
        }
        ResultPoint correctedTopRight = correctTopRight(bottomLeft, bottomRight, topLeft, topRight, dimension + 2);
        if (correctedTopRight == null) {
            correctedTopRight = topRight;
        }
        int dimension2 = Math.max(transitionsBetween(topLeft, correctedTopRight).getTransitions(), transitionsBetween(bottomRight, correctedTopRight).getTransitions()) + 1;
        if ((dimension2 & 1) == 1) {
            dimension2++;
        }
        return new DetectorResult(sampleGrid(this.image, topLeft, bottomLeft, bottomRight, correctedTopRight, dimension2), new ResultPoint[]{topLeft, bottomLeft, bottomRight, correctedTopRight});
    }

    private ResultPoint correctTopRight(ResultPoint bottomLeft, ResultPoint bottomRight, ResultPoint topLeft, ResultPoint topRight, int dimension) {
        float corr = ((float) distance(bottomLeft, bottomRight)) / ((float) dimension);
        int norm = distance(topLeft, topRight);
        ResultPoint c1 = new ResultPoint(topRight.getX() + (corr * ((topRight.getX() - topLeft.getX()) / ((float) norm))), topRight.getY() + (corr * ((topRight.getY() - topLeft.getY()) / ((float) norm))));
        corr = ((float) distance(bottomLeft, bottomRight)) / ((float) dimension);
        norm = distance(bottomRight, topRight);
        ResultPoint c2 = new ResultPoint(topRight.getX() + (corr * ((topRight.getX() - bottomRight.getX()) / ((float) norm))), topRight.getY() + (corr * ((topRight.getY() - bottomRight.getY()) / ((float) norm))));
        if (isValid(c1)) {
            if (!isValid(c2)) {
                return c1;
            }
            if (Math.abs(transitionsBetween(topLeft, c1).getTransitions() - transitionsBetween(bottomRight, c1).getTransitions()) <= Math.abs(transitionsBetween(topLeft, c2).getTransitions() - transitionsBetween(bottomRight, c2).getTransitions())) {
                return c1;
            }
            return c2;
        } else if (isValid(c2)) {
            return c2;
        } else {
            return null;
        }
    }

    private boolean isValid(ResultPoint p) {
        return p.getX() >= 0.0f && p.getX() < ((float) this.image.width) && p.getY() > 0.0f && p.getY() < ((float) this.image.height);
    }

    private static int round(float d) {
        return (int) (0.5f + d);
    }

    private static int distance(ResultPoint a, ResultPoint b) {
        return round((float) Math.sqrt((double) (((a.getX() - b.getX()) * (a.getX() - b.getX())) + ((a.getY() - b.getY()) * (a.getY() - b.getY())))));
    }

    private static void increment(Hashtable table, ResultPoint key) {
        Integer value = (Integer) table.get(key);
        table.put(key, value == null ? INTEGERS[1] : INTEGERS[value.intValue() + 1]);
    }

    private static BitMatrix sampleGrid(BitMatrix image, ResultPoint topLeft, ResultPoint bottomLeft, ResultPoint bottomRight, ResultPoint topRight, int dimension) throws NotFoundException {
        return GridSampler.getInstance().sampleGrid(image, dimension, 0.5f, 0.5f, ((float) dimension) - 0.5f, 0.5f, ((float) dimension) - 0.5f, ((float) dimension) - 0.5f, 0.5f, ((float) dimension) - 0.5f, topLeft.getX(), topLeft.getY(), topRight.getX(), topRight.getY(), bottomRight.getX(), bottomRight.getY(), bottomLeft.getX(), bottomLeft.getY());
    }

    private ResultPointsAndTransitions transitionsBetween(ResultPoint from, ResultPoint to) {
        int i;
        int fromX = (int) from.getX();
        int fromY = (int) from.getY();
        int toX = (int) to.getX();
        int toY = (int) to.getY();
        boolean steep = Math.abs(toY - fromY) > Math.abs(toX - fromX);
        if (steep) {
            int temp = fromX;
            fromX = fromY;
            fromY = temp;
            temp = toX;
            toX = toY;
            toY = temp;
        }
        int dx = Math.abs(toX - fromX);
        int dy = Math.abs(toY - fromY);
        int error = (-dx) >> 1;
        int ystep = fromY < toY ? 1 : -1;
        int xstep = fromX < toX ? 1 : -1;
        int transitions = 0;
        BitMatrix bitMatrix = this.image;
        if (steep) {
            i = fromY;
        } else {
            i = fromX;
        }
        boolean inBlack = bitMatrix.get(i, steep ? fromX : fromY);
        int y = fromY;
        for (int x = fromX; x != toX; x += xstep) {
            bitMatrix = this.image;
            if (steep) {
                i = y;
            } else {
                i = x;
            }
            boolean isBlack = bitMatrix.get(i, steep ? x : y);
            if (isBlack != inBlack) {
                transitions++;
                inBlack = isBlack;
            }
            error += dy;
            if (error > 0) {
                if (y == toY) {
                    break;
                }
                y += ystep;
                error -= dx;
            }
        }
        return new ResultPointsAndTransitions(from, to, transitions, null);
    }
}
