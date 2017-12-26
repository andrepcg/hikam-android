package com.tencent.bugly.proguard;

import android.util.Log;
import java.util.Locale;

/* compiled from: BUGLY */
public final class C0757w {
    public static String f463a = "CrashReport";
    public static boolean f464b = false;
    private static String f465c = "CrashReportInfo";

    private static boolean m454a(int i, String str, Object... objArr) {
        if (!f464b) {
            return false;
        }
        if (str == null) {
            str = "null";
        } else if (!(objArr == null || objArr.length == 0)) {
            str = String.format(Locale.US, str, objArr);
        }
        switch (i) {
            case 0:
                Log.i(f463a, str);
                return true;
            case 1:
                Log.d(f463a, str);
                return true;
            case 2:
                Log.w(f463a, str);
                return true;
            case 3:
                Log.e(f463a, str);
                return true;
            case 5:
                Log.i(f465c, str);
                return true;
            default:
                return false;
        }
    }

    public static boolean m456a(String str, Object... objArr) {
        return C0757w.m454a(0, str, objArr);
    }

    public static boolean m458b(String str, Object... objArr) {
        return C0757w.m454a(5, str, objArr);
    }

    public static boolean m460c(String str, Object... objArr) {
        return C0757w.m454a(1, str, objArr);
    }

    public static boolean m455a(Class cls, String str, Object... objArr) {
        return C0757w.m454a(1, String.format(Locale.US, "[%s] %s", new Object[]{cls.getSimpleName(), str}), objArr);
    }

    public static boolean m461d(String str, Object... objArr) {
        return C0757w.m454a(2, str, objArr);
    }

    public static boolean m457a(Throwable th) {
        return !f464b ? false : C0757w.m454a(2, C0761y.m490a(th), new Object[0]);
    }

    public static boolean m462e(String str, Object... objArr) {
        return C0757w.m454a(3, str, objArr);
    }

    public static boolean m459b(Throwable th) {
        return !f464b ? false : C0757w.m454a(3, C0761y.m490a(th), new Object[0]);
    }
}
