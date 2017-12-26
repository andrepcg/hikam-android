package com.jwkj;

import android.content.Intent;
import android.util.Log;
import com.hikam.C0291R;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.MyApp;
import com.jwkj.utils.MusicManger;
import com.p2p.core.P2PInterface.IP2P;
import org.jboss.netty.handler.codec.rtsp.RtspHeaders.Values;

public class P2PListener implements IP2P {
    public void vCalling(boolean isOutCall, String threeNumber, int type) {
        if (isOutCall) {
            P2PConnect.vCalling(true, type);
            return;
        }
        if (SharedPreferencesManager.getInstance().getCMuteState(MyApp.app) == 1) {
            MusicManger.getInstance().playCommingMusic();
        }
        if (SharedPreferencesManager.getInstance().getCVibrateState(MyApp.app) == 1) {
            MusicManger.getInstance().Vibrate();
        }
        P2PConnect.setCurrent_call_id(threeNumber);
        P2PConnect.vCalling(false, type);
    }

    public void vReject(int reason_code) {
        String reason = "";
        switch (reason_code) {
            case 0:
                reason = MyApp.app.getResources().getString(C0291R.string.device_password_error);
                break;
            case 1:
                reason = MyApp.app.getResources().getString(C0291R.string.busy);
                break;
            case 2:
                reason = MyApp.app.getResources().getString(C0291R.string.none);
                break;
            case 3:
                reason = MyApp.app.getResources().getString(C0291R.string.id_disabled);
                break;
            case 4:
                reason = MyApp.app.getResources().getString(C0291R.string.id_overdate);
                break;
            case 5:
                reason = MyApp.app.getResources().getString(C0291R.string.id_inactived);
                break;
            case 6:
                reason = MyApp.app.getResources().getString(C0291R.string.offline);
                break;
            case 7:
                reason = MyApp.app.getResources().getString(C0291R.string.powerdown);
                break;
            case 8:
                reason = MyApp.app.getResources().getString(C0291R.string.nohelper);
                break;
            case 9:
                reason = MyApp.app.getResources().getString(C0291R.string.hungup);
                break;
            case 10:
                reason = MyApp.app.getResources().getString(C0291R.string.timeout);
                break;
            case 11:
                reason = MyApp.app.getResources().getString(C0291R.string.no_body);
                break;
            case 12:
                reason = MyApp.app.getResources().getString(C0291R.string.internal_error);
                break;
            case 13:
                reason = MyApp.app.getResources().getString(C0291R.string.conn_fail);
                break;
            case 14:
                reason = MyApp.app.getResources().getString(C0291R.string.not_support);
                break;
        }
        P2PConnect.vReject(reason);
    }

    public void vAccept(int type, int state) {
        P2PConnect.vAccept(type, state);
    }

    public void vConnectReady() {
        P2PConnect.vConnectReady();
    }

    public void vAllarming(String model, String srcId, int type, boolean isSupportExternAlarm, int iGroup, int iItem, boolean isSurpportDelete, String uuid) {
        P2PConnect.vAllarming(model, srcId, type, isSupportExternAlarm, iGroup, iItem, uuid);
    }

    public void vChangeVideoMask(int state) {
        Intent i = new Intent(P2P.P2P_CHANGE_IMAGE_TRANSFER);
        i.putExtra("state", state);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetPlayBackPos(int length, int currentPos) {
        Intent i = new Intent();
        i.setAction(P2P.PLAYBACK_CHANGE_SEEK);
        i.putExtra("max", length);
        i.putExtra("current", currentPos);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetPlayBackStatus(int state) {
        Intent i = new Intent();
        i.setAction(P2P.PLAYBACK_CHANGE_STATE);
        i.putExtra("state", state);
        MyApp.app.sendBroadcast(i);
    }

    public void vGXNotifyFlag(int flag) {
    }

    public void vRetPlaySize(int iWidth, int iHeight) {
        Log.e("my", "vRetPlaySize:" + iWidth + "-" + iHeight);
        Intent i = new Intent();
        i.setAction(P2P.P2P_RESOLUTION_CHANGE);
        if (iWidth == 1280) {
            P2PConnect.setMode(7);
            i.putExtra(Values.MODE, 7);
        } else if (iWidth == 640) {
            P2PConnect.setMode(5);
            i.putExtra(Values.MODE, 5);
        } else if (iWidth == 320) {
            P2PConnect.setMode(6);
            i.putExtra(Values.MODE, 6);
        }
        MyApp.app.sendBroadcast(i);
    }

    public void vRetPlayNumber(int iNumber) {
        P2PConnect.setNumber(iNumber);
        Intent i = new Intent();
        i.setAction(P2P.P2P_MONITOR_NUMBER_CHANGE);
        i.putExtra("number", iNumber);
        MyApp.app.sendBroadcast(i);
    }

    public void vRecvAudioVideoData(byte[] AudioBuffer, int AudioLen, int AudioFrames, long AudioPTS, byte[] VideoBuffer, int VideoLen, long VideoPTS) {
    }

    public void vRetRTSPNotify(String msg) {
    }

    public void vAllarmingWitghTime(String srcId, int type, boolean isSupportExternAlarm, int iGroup, int iItem, int imagecounts, String imagePath, String alarmCapDir, String VideoPath) {
    }
}
