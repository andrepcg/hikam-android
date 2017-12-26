package com.firebase.jobdispatcher;

import android.support.annotation.VisibleForTesting;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Constraint {
    @VisibleForTesting
    static final int[] ALL_CONSTRAINTS = new int[]{2, 1, 4};
    public static final int DEVICE_CHARGING = 4;
    public static final int ON_ANY_NETWORK = 2;
    public static final int ON_UNMETERED_NETWORK = 1;

    @Retention(RetentionPolicy.SOURCE)
    public @interface JobConstraint {
    }

    private Constraint() {
    }

    static int compact(int[] constraints) {
        int result = 0;
        for (int c : constraints) {
            result |= c;
        }
        return result;
    }

    static int[] uncompact(int compactConstraints) {
        int i = 0;
        int length = 0;
        for (int c : ALL_CONSTRAINTS) {
            int i2;
            int c2;
            if ((compactConstraints & c2) == c2) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            length += i2;
        }
        int[] list = new int[length];
        int[] iArr = ALL_CONSTRAINTS;
        int length2 = iArr.length;
        int i3 = 0;
        while (i < length2) {
            int i4;
            c2 = iArr[i];
            if ((compactConstraints & c2) == c2) {
                i4 = i3 + 1;
                list[i3] = c2;
            } else {
                i4 = i3;
            }
            i++;
            i3 = i4;
        }
        return list;
    }
}
