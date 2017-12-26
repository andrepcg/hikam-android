package cn.com.streamax.miotp.p2p.jni;

import android.util.Log;

public class ClientStatusCallback {
    private static final String TAG = "ClientStatusCallback";

    public void invoke() {
        Log.d(TAG, "invoke() start");
        Log.d(TAG, "invoke() end");
    }
}
