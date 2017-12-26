package cn.com.streamax.miotp.jni;

import android.util.Log;

public class BaseJni {
    private static final String TAG = "BaseJni";

    public static native int checkInitState();

    public static native int checkOnline(int i, String str, int[] iArr);

    public static native void freeBuffer(int i);

    public static native int getDevLists(String str, int i, DeviceList deviceList);

    public static native int getDevState(int i, String str, DeviceList deviceList);

    public static native int getOrgInfo(OrgObjectArray orgObjectArray);

    public static native int initMiddleWare(int i, String str, int i2, String str2);

    public static native int loadMiddleWare(String str, int i);

    public static native int login(String str, String str2, int i, int i2, int i3, LoginResult loginResult);

    public static native int manualCancelAlarm(int i, String str, String str2, int i2);

    public static native int refreshDevList(String str);

    public static native int setDevInfoCallback(DeviceCallback deviceCallback, int i);

    public static native int setLoginServer(String str, int i, String str2);

    public static native int setStreamCallback(VideoCallback videoCallback, int i, String str);

    public static native int startRealPlay(int i, String str, int i2, int i3, int i4);

    public static native int stopRealPlay(int i, String str, int i2);

    public static native int unInitMiddleWare();

    public static native int unLoadMiddleWare();

    static {
        Log.i(TAG, "load so!");
        Log.i(TAG, "load so successlly");
    }
}
