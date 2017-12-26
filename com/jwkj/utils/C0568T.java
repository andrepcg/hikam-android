package com.jwkj.utils;

import android.content.Context;
import android.widget.Toast;

public class C0568T {
    private static Toast toast;

    public static void showShort(Context context, CharSequence message) {
        try {
            if (toast == null) {
                toast = Toast.makeText(context, message, 0);
            } else {
                toast.setText(message);
            }
            toast.show();
        } catch (Exception e) {
        }
    }

    public static void showShort(Context context, int message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, 0);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    public static void showLong(Context context, CharSequence message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, 1);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    public static void showLong(Context context, int message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, 1);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    public static void show(Context context, CharSequence message, int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, message, duration);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    public static void show(Context context, int message, int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, message, duration);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    public static void hideToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
