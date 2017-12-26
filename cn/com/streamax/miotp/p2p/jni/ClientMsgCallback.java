package cn.com.streamax.miotp.p2p.jni;

import android.util.Log;

public class ClientMsgCallback {
    private static final String TAG = "ClientMsgCallback";

    public void invoke() {
        Log.d(TAG, "invoke() start");
        Log.d(TAG, "invoke() end");
    }
}
