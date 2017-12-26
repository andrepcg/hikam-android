package cn.com.streamax.miotp.jni;

import android.util.Log;

public class TempJni {
    private static final String TAG = "TempJni";

    public static native int setStreamCallback(VideoCallback videoCallback, int i, String str);

    public static native int startRealPlay(int i, String str, int i2, int i3, int i4);

    public static native int stopRealPlay(int i, String str, int i2);

    static {
        Log.i(TAG, "load so!");
        System.loadLibrary("ffmpeg");
        System.loadLibrary("MuCoDec");
        System.loadLibrary("ffmpegutils");
        Log.i(TAG, "load so successlly");
    }
}
