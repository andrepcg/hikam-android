package com.mediatek.elian;

public class ElianNative {
    public native int GetLibVersion();

    public native int GetProtoVersion();

    public native int InitSmartConnection(String str, int i, int i2);

    public native int StartSmartConnection(String str, String str2, String str3, byte b);

    public native int StopSmartConnection();

    public static boolean LoadLib() {
        try {
            System.loadLibrary("elianjni");
            return true;
        } catch (UnsatisfiedLinkError e) {
            System.err.println("WARNING: Could not load elianjni library!");
            return false;
        }
    }
}
