package com.tencent.bugly.crashreport;

import android.util.Log;
import com.tencent.bugly.C0693b;
import com.tencent.bugly.proguard.C0760x;

/* compiled from: BUGLY */
public class BuglyLog {
    public static void m39v(String str, String str2) {
        if (str == null) {
            str = "";
        }
        if (str2 == null) {
            str2 = "null";
        }
        if (C0693b.f47c) {
            Log.v(str, str2);
        }
        C0760x.m473a("V", str, str2);
    }

    public static void m35d(String str, String str2) {
        if (str == null) {
            str = "";
        }
        if (str2 == null) {
            str2 = "null";
        }
        if (C0693b.f47c) {
            Log.d(str, str2);
        }
        C0760x.m473a("D", str, str2);
    }

    public static void m38i(String str, String str2) {
        if (str == null) {
            str = "";
        }
        if (str2 == null) {
            str2 = "null";
        }
        if (C0693b.f47c) {
            Log.i(str, str2);
        }
        C0760x.m473a("I", str, str2);
    }

    public static void m40w(String str, String str2) {
        if (str == null) {
            str = "";
        }
        if (str2 == null) {
            str2 = "null";
        }
        if (C0693b.f47c) {
            Log.w(str, str2);
        }
        C0760x.m473a("W", str, str2);
    }

    public static void m36e(String str, String str2) {
        if (str == null) {
            str = "";
        }
        if (str2 == null) {
            str2 = "null";
        }
        if (C0693b.f47c) {
            Log.e(str, str2);
        }
        C0760x.m473a("E", str, str2);
    }

    public static void m37e(String str, String str2, Throwable th) {
        if (str == null) {
            str = "";
        }
        if (str2 == null) {
            str2 = "null";
        }
        if (C0693b.f47c) {
            Log.e(str, str2, th);
        }
        C0760x.m474a("E", str, th);
    }

    public static void setCache(int i) {
        C0760x.m471a(i);
    }
}
