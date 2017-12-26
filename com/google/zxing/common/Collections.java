package com.google.zxing.common;

import java.util.Vector;

public final class Collections {
    private Collections() {
    }

    public static void insertionSort(Vector vector, Comparator comparator) {
        int max = vector.size();
        for (int i = 1; i < max; i++) {
            Object value = vector.elementAt(i);
            int j = i - 1;
            while (j >= 0) {
                Object valueB = vector.elementAt(j);
                if (comparator.compare(valueB, value) <= 0) {
                    break;
                }
                vector.setElementAt(valueB, j + 1);
                j--;
            }
            vector.setElementAt(value, j + 1);
        }
    }
}
