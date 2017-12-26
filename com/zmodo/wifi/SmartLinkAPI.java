package com.zmodo.wifi;

public class SmartLinkAPI {
    public static final int SYS_LANG_CHINESE = 1;
    public static final int SYS_LANG_ENGLISH = 2;
    private static final String TAG = "SmartLinkAPI";

    public static native int RcveSrvStart(Object obj);

    public static native int RcveSrvStop();

    public static native int SmartLinkClearup();

    public static native int SmartLinkRun(String str, String str2, int i);

    public static native int SmartLinkStartup();

    public static native int SmartLinkStop();
}
