package com.jwkj.global;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NpcCommon {
    private static Boolean mIsNetWorkAvailable = Boolean.valueOf(false);
    public static NETWORK_TYPE mNetWorkType = NETWORK_TYPE.NETWORK_WIFI;
    public static String mThreeNum;
    public static String mThreeNum2 = "000000";

    public enum NETWORK_TYPE {
        NETWORK_2GOR3G,
        NETWORK_WIFI
    }

    public static void setNetWorkState(boolean state) {
        mIsNetWorkAvailable = Boolean.valueOf(state);
    }

    public static boolean getNetWorkState() {
        return mIsNetWorkAvailable.booleanValue();
    }

    public static boolean verifyNetwork(Context context) {
        NetworkInfo activeNetInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetInfo != null) {
            boolean isNetworkActive;
            if (activeNetInfo.isConnected()) {
                setNetWorkState(true);
                isNetworkActive = true;
            } else {
                setNetWorkState(false);
                isNetworkActive = false;
            }
            if (activeNetInfo.getType() == 1) {
                mNetWorkType = NETWORK_TYPE.NETWORK_WIFI;
                return isNetworkActive;
            }
            mNetWorkType = NETWORK_TYPE.NETWORK_2GOR3G;
            return isNetworkActive;
        }
        setNetWorkState(false);
        return false;
    }
}
