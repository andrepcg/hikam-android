package cn.com.streamax.miotp.jni;

import android.util.Log;

public class DeviceCallback {
    private static final String TAG = "DeviceCallback";

    public void invoke(int orgId, DeviceInfo[] device, int count, int userdata) {
        Log.d(TAG, "invoke() start");
        Log.d(TAG, "orgid=" + orgId + ",length=" + device.length + ",count=" + count + ",userdata=" + userdata);
        Log.d(TAG, "invoke() end");
    }
}
