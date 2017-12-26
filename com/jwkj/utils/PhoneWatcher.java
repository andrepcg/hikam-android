package com.jwkj.utils;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneWatcher {
    boolean isWatchering;
    Context mContext;
    OnCommingCallListener onCommingCallListener;

    public interface OnCommingCallListener {
        void onCommingCall();
    }

    private class PhoneListener extends PhoneStateListener {
        private PhoneListener() {
        }

        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case 0:
                    Log.e("my", "CALL_STATE_IDLE");
                    return;
                case 1:
                    if (PhoneWatcher.this.onCommingCallListener != null) {
                        PhoneWatcher.this.onCommingCallListener.onCommingCall();
                        return;
                    }
                    return;
                case 2:
                    Log.e("my", "CALL_STATE_OFFHOOK");
                    return;
                default:
                    return;
            }
        }
    }

    public PhoneWatcher(Context context) {
        this.mContext = context;
    }

    public void startWatcher() {
        ((TelephonyManager) this.mContext.getSystemService("phone")).listen(new PhoneListener(), 32);
    }

    public void stopWatcher() {
        this.onCommingCallListener = null;
    }

    public void setOnCommingCallListener(OnCommingCallListener onCommingCallListener) {
        this.onCommingCallListener = onCommingCallListener;
    }
}
