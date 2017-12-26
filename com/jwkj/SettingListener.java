package com.jwkj;

import android.content.Intent;
import android.util.Log;
import cn.com.streamax.miotp.p2p.jni.GWellUserInfo;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.jwkj.activity.MessageActivity;
import com.jwkj.activity.SysMsgActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.data.DataManager;
import com.jwkj.data.Message;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.data.SysMessage;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.FList;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.MusicManger;
import com.jwkj.utils.Utils;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PInterface.ISetting;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.cookie.ClientCookie;
import org.jboss.netty.handler.codec.rtsp.RtspHeaders.Values;

public class SettingListener implements ISetting {
    String TAG = "SDK";

    class C03181 extends Thread {
        C03181() {
        }

        public void run() {
            Log.i("Register", "P2pJni.P2pClientSdkRegister start");
            FList.getInstance().hikam_sdk_register_state = 1;
            int regResult = P2pJni.P2pClientSdkRegister(1, new GWellUserInfo());
            Log.i("Register", "P2pJni.P2pClientSdkRegister finish, result = " + regResult);
            if (regResult == 0) {
                FList.getInstance().hikam_sdk_register_state = 0;
            } else {
                FList.getInstance().hikam_sdk_register_state = -1;
            }
        }
    }

    public void vRetP2pSdkSessionTimeout() {
        FList.getInstance().hikam_sdk_register_state = -1;
        new C03181().start();
    }

    public void ACK_vRetCheckDevicePassword(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetCheckDevicePassword:" + result);
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_CHECK_PASSWORD);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetCheckDevicePassword2(int msgId, int result, String model) {
        Log.e(this.TAG, "ACK_vRetCheckDevicePassword2:" + result + "###" + model);
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_CHECK_PASSWORD2);
        i.putExtra("result", result);
        i.putExtra("model", model);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetGetNpcSettings(String contactId, int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetGetNpcSettings:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_GET_NPC_SETTINGS);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetGetDefenceStates(String contactId, int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetGetDefenceStates:" + result);
        Intent i;
        if (result == 9998) {
            FList.getInstance().setDefenceState(contactId, 4);
            i = new Intent();
            i.putExtra("state", 4);
            i.putExtra(ContactDB.COLUMN_CONTACT_ID, contactId);
            i.setAction(P2P.RET_GET_REMOTE_DEFENCE);
            MyApp.app.sendBroadcast(i);
        } else if (result == 9999) {
            FList.getInstance().setDefenceState(contactId, 3);
            i = new Intent();
            i.putExtra("state", 3);
            i.putExtra(ContactDB.COLUMN_CONTACT_ID, contactId);
            i.setAction(P2P.RET_GET_REMOTE_DEFENCE);
            MyApp.app.sendBroadcast(i);
        } else if (result == 9996) {
            FList.getInstance().setDefenceState(contactId, 5);
        }
    }

    public void vRetGetDeviceTimeResult(String time) {
        Log.e(this.TAG, "vRetGetDeviceTimeResult:" + time);
        Intent i = new Intent();
        i.setAction(P2P.RET_GET_TIME);
        i.putExtra(Values.TIME, time);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetDeviceTimeResult(int result) {
        Log.e(this.TAG, "vRetSetDeviceTimeResult:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.RET_SET_TIME);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetDeviceTime(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetDeviceTime:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_SET_TIME);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetGetDeviceTime(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetGetDeviceTime:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_GET_TIME);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetNpcSettingsVideoFormat(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetNpcSettingsVideoFormat:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_SET_VIDEO_FORMAT);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetVideoFormatResult(int result) {
        Log.e(this.TAG, "vRetSetVideoFormatResult:" + result);
        Intent i = new Intent();
        i.setAction(P2P.RET_SET_VIDEO_FORMAT);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetVideoFormatResult(int type) {
        Log.e(this.TAG, "vRetGetVideoFormatResult:" + type);
        Intent format_type = new Intent();
        format_type.setAction(P2P.RET_GET_VIDEO_FORMAT);
        format_type.putExtra("type", type);
        MyApp.app.sendBroadcast(format_type);
    }

    public void ACK_vRetSetNpcSettingsVideoVolume(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetNpcSettingsVideoVolume:" + result);
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_SET_VIDEO_VOLUME);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetVolumeResult(int result) {
        Log.e(this.TAG, "vRetSetVolumeResult:" + result);
        Intent i = new Intent();
        i.setAction(P2P.RET_SET_VIDEO_VOLUME);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetVideoVolumeResult(int value) {
        Log.e(this.TAG, "vRetGetVideoVolumeResult:" + value);
        Intent volume = new Intent();
        volume.setAction(P2P.RET_GET_VIDEO_VOLUME);
        volume.putExtra(Param.VALUE, value);
        MyApp.app.sendBroadcast(volume);
    }

    public void ACK_vRetSetDevicePassword(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetDevicePassword:" + result);
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_SET_DEVICE_PASSWORD);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetDevicePasswordResult(int result) {
        Log.e(this.TAG, "vRetSetDevicePasswordResult:" + result);
        Intent i = new Intent();
        i.setAction(P2P.RET_SET_DEVICE_PASSWORD);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetNpcSettingsNetType(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetNpcSettingsNetType:" + result);
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_SET_NET_TYPE);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetNetTypeResult(int result) {
        Log.e(this.TAG, "vRetSetNetTypeResult:" + result);
        Intent i = new Intent();
        i.setAction(P2P.RET_SET_NET_TYPE);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetNetTypeResult(int type) {
        Log.e(this.TAG, "vRetGetNetTypeResult:" + type);
        Intent net_type = new Intent();
        net_type.setAction(P2P.RET_GET_NET_TYPE);
        net_type.putExtra("type", type);
        MyApp.app.sendBroadcast(net_type);
    }

    public void ACK_vRetSetWifi(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetWifi:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_SET_WIFI);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetGetWifiList(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetGetWifiList:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_GET_WIFI);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetWifiResult(int result, int currentId, int count, int[] types, int[] strengths, String[] names) {
        Log.e(this.TAG, "vRetWifiResult:" + result + ":" + currentId + ":" + count);
        if (result == 1) {
            Intent i = new Intent();
            i.setAction(P2P.RET_GET_WIFI);
            i.putExtra("iCurrentId", currentId);
            i.putExtra("iCount", count);
            i.putExtra("iType", types);
            i.putExtra("iStrength", strengths);
            i.putExtra(SharedPreferencesManager.KEY_NAMES, names);
            MyApp.app.sendBroadcast(i);
            return;
        }
        i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.RET_SET_WIFI);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetAlarmBindId(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetAlarmBindId:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_SET_BIND_ALARM_ID);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetGetAlarmBindId(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetGetAlarmBindId:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_GET_BIND_ALARM_ID);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetBindAlarmIdResult(int result, int maxCount, String[] data) {
        Log.e(this.TAG, "vRetBindAlarmIdResult:" + result + "maxCount : " + maxCount + "data : ");
        if (data != null) {
            for (String str : data) {
                Log.e(this.TAG, "vRetBindAlarmIdResult:" + str);
            }
        }
        if (result == 1) {
            Intent alarmId = new Intent();
            alarmId.setAction(P2P.RET_GET_BIND_ALARM_ID);
            alarmId.putExtra("data", data);
            alarmId.putExtra("max_count", maxCount);
            MyApp.app.sendBroadcast(alarmId);
            return;
        }
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.RET_SET_BIND_ALARM_ID);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetAlarmEmail(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetAlarmEmail:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_SET_ALARM_EMAIL);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetGetAlarmEmail(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetGetAlarmEmail:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_GET_ALARM_EMAIL);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetAlarmEmailResult(int result, String email) {
        Log.e(this.TAG, "vRetAlarmEmailResult:" + result + ":" + email);
        Intent i;
        if (result == 15) {
            i = new Intent();
            i.putExtra("result", result);
            i.setAction(P2P.RET_SET_ALARM_EMAIL);
            MyApp.app.sendBroadcast(i);
        } else if ((result & 1) == 1) {
            i = new Intent();
            i.setAction(P2P.RET_GET_ALARM_EMAIL);
            i.putExtra("email", email);
            i.putExtra("result", result);
            MyApp.app.sendBroadcast(i);
        } else {
            i = new Intent();
            i.putExtra("result", result);
            i.setAction(P2P.RET_SET_ALARM_EMAIL);
            MyApp.app.sendBroadcast(i);
        }
    }

    public void vRetAlarmEmailResultWithSMTP(int result, String email, int smtpport, String[] SmptMessage) {
        Log.e(this.TAG, "vRetAlarmEmailResultWithSMTP:" + result + ":" + email);
        if ((result & 1) == 1) {
            Intent i = new Intent();
            i.setAction(P2P.RET_GET_ALARM_EMAIL_WITHSMTP);
            i.putExtra("result", result);
            i.putExtra("email", email);
            MyApp.app.sendBroadcast(i);
            return;
        }
        i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.RET_SET_ALARM_EMAIL);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetNpcSettingsMotion(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetNpcSettingsMotion:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_SET_MOTION);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetMotionResult(int state) {
        Log.e(this.TAG, "vRetGetMotionResult:" + state);
        Intent motion = new Intent();
        motion.setAction(P2P.RET_GET_MOTION);
        motion.putExtra("motionState", state);
        motion.putExtra("sensitivity", -1);
        MyApp.app.sendBroadcast(motion);
    }

    public void vRetGetMotionResult(int state, int sensitivity) {
        Log.e(this.TAG, "vRetGetMotionResult:" + state + " sensitivity:" + sensitivity);
        Intent motion = new Intent();
        motion.setAction(P2P.RET_GET_MOTION);
        motion.putExtra("motionState", state);
        motion.putExtra("sensitivity", sensitivity);
        MyApp.app.sendBroadcast(motion);
    }

    public void vRetSetMotionResult(int result) {
        Log.e(this.TAG, "vRetSetMotionResult:" + result);
        Intent i = new Intent();
        i.setAction(P2P.RET_SET_MOTION);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetNpcSettingsBuzzer(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetNpcSettingsBuzzer:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction("com.hikam.RET_SET_BUZZER");
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetBuzzerResult(int state) {
        Log.e(this.TAG, "vRetGetBuzzerResult:" + state);
        Intent buzzer = new Intent();
        buzzer.setAction(P2P.RET_GET_BUZZER);
        buzzer.putExtra("buzzerState", state);
        MyApp.app.sendBroadcast(buzzer);
    }

    public void vRetSetBuzzerResult(int result) {
        Log.e(this.TAG, "vRetSetBuzzerResult:" + result);
        Intent i = new Intent();
        i.setAction("com.hikam.RET_SET_BUZZER");
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetNpcSettingsRecordResolution(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetNpcSettingsRecordResolution:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_SET_RECORD_RESOLUTION);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetNpcSettingsRecordType(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetNpcSettingsRecordType:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_SET_RECORD_TYPE);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetRecordResolutionResult(int resolution) {
        Log.e(this.TAG, "vRetGetRecordResolutionResult:" + resolution);
        Intent record_resolution = new Intent();
        record_resolution.setAction(P2P.RET_GET_RECORD_RESOLUTION);
        record_resolution.putExtra("resolution", resolution);
        MyApp.app.sendBroadcast(record_resolution);
    }

    public void vRetGetRecordTypeResult(int type) {
        Log.e(this.TAG, "vRetGetRecordTypeResult:" + type);
        Intent record_type = new Intent();
        record_type.setAction(P2P.RET_GET_RECORD_TYPE);
        record_type.putExtra("type", type);
        MyApp.app.sendBroadcast(record_type);
    }

    public void vRetSetRecordResolutionResult(int result) {
        Log.e(this.TAG, "vRetSetRecordResolutionResult:" + result);
        Intent i = new Intent();
        i.setAction(P2P.RET_SET_RECORD_RESOLUTION);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetRecordTypeResult(int result) {
        Log.e(this.TAG, "vRetSetRecordTypeResult:" + result);
        Intent i = new Intent();
        i.setAction(P2P.RET_SET_RECORD_TYPE);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetNpcSettingsRecordTime(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetNpcSettingsRecordTime:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_SET_RECORD_TIME);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetRecordTimeResult(int time) {
        Log.e(this.TAG, "vRetGetRecordTimeResult:" + time);
        Intent record_time = new Intent();
        record_time.setAction(P2P.RET_GET_RECORD_TIME);
        record_time.putExtra(Values.TIME, time);
        MyApp.app.sendBroadcast(record_time);
    }

    public void vRetSetRecordTimeResult(int result) {
        Log.e(this.TAG, "vRetSetRecordTimeResult:" + result);
        Intent i = new Intent();
        i.setAction(P2P.RET_SET_RECORD_TIME);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetNpcSettingsRecordPlanTime(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetNpcSettingsRecordPlanTime:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_SET_RECORD_PLAN_TIME);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetRecordPlanTimeResult(String time) {
        Log.e(this.TAG, "vRetGetRecordPlanTimeResult:" + time);
        Intent plan_time = new Intent();
        plan_time.setAction(P2P.RET_GET_RECORD_PLAN_TIME);
        plan_time.putExtra(Values.TIME, time);
        MyApp.app.sendBroadcast(plan_time);
    }

    public void vRetSetRecordPlanTimeResult(int result) {
        Log.e(this.TAG, "vRetSetRecordPlanTimeResult:" + result);
        Intent i = new Intent();
        i.setAction(P2P.RET_SET_RECORD_PLAN_TIME);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetDefenceArea(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetDefenceArea:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_SET_DEFENCE_AREA);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetClearDefenceAreaState(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetClearDefenceAreaState:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_CLEAR_DEFENCE_AREA);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetClearDefenceAreaState(int result) {
        Log.e(this.TAG, "vRetClearDefenceAreaState:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.RET_CLEAR_DEFENCE_AREA);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetGetDefenceArea(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetGetDefenceArea:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_GET_DEFENCE_AREA);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetAlarmCodeStatus(int srcID, int iCount, int key, byte[] bData, int iResult) {
        if (iResult == 1) {
            ArrayList<int[]> data = new ArrayList();
            int[] status_key = new int[]{(key >> 0) & 1, (key >> 1) & 1, (key >> 2) & 1, (key >> 3) & 1, (key >> 4) & 1, (key >> 5) & 1, (key >> 6) & 1, (key >> 7) & 1};
            Log.e("area", status_key[0] + " " + status_key[1] + " " + status_key[2] + " " + status_key[3] + " " + status_key[4] + " " + status_key[5] + " " + status_key[6] + " " + status_key[7] + " ");
            data.add(0, status_key);
            for (int i = 0; i < iCount; i++) {
                byte b = bData[i];
                int[] status = new int[]{(b >> 0) & 1, (b >> 1) & 1, (b >> 2) & 1, (b >> 3) & 1, (b >> 4) & 1, (b >> 5) & 1, (b >> 6) & 1, (b >> 7) & 1};
                Log.e("area", status[0] + " " + status[1] + " " + status[2] + " " + status[3] + " " + status[4] + " " + status[5] + " " + status[6] + " " + status[7] + " ");
                data.add(i + 1, status);
            }
            vRetDefenceAreaResult(iResult, data, 0, 0);
            return;
        }
        int group = bData[0];
        int item = bData[4];
        Log.e("HIKAM_NDK", "group :" + group + "item :" + item);
        vRetDefenceAreaResult(iResult, null, group, item);
    }

    public void vRetDefenceAreaResult(int result, ArrayList<int[]> data, int group, int item) {
        Log.e(this.TAG, "vRetDefenceAreaResult:" + result);
        if (result == 1) {
            Intent i = new Intent();
            i.setAction(P2P.RET_GET_DEFENCE_AREA);
            i.putExtra("data", data);
            MyApp.app.sendBroadcast(i);
            return;
        }
        i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.RET_SET_DEFENCE_AREA);
        i.putExtra("group", group);
        i.putExtra("item", item);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetRemoteDefence(String contactId, int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetRemoteDefence:" + result);
        if (result == 9997) {
            Contact contact = FList.getInstance().isContact(contactId);
            if (contact != null) {
                P2PHandler.getInstance().getNpcSettings(contact.contactModel, contact.contactId, contact.contactPassword);
            }
        } else if (result == 9998) {
            FList.getInstance().setDefenceState(contactId, 4);
            i = new Intent();
            i.putExtra("state", 4);
            i.putExtra(ContactDB.COLUMN_CONTACT_ID, contactId);
            i.setAction(P2P.RET_GET_REMOTE_DEFENCE);
            MyApp.app.sendBroadcast(i);
        } else if (result == 9999) {
            FList.getInstance().setDefenceState(contactId, 3);
            i = new Intent();
            i.putExtra("state", 3);
            i.putExtra(ContactDB.COLUMN_CONTACT_ID, contactId);
            i.setAction(P2P.RET_GET_REMOTE_DEFENCE);
            MyApp.app.sendBroadcast(i);
        } else if (result == 9996) {
            FList.getInstance().setDefenceState(contactId, 5);
        }
    }

    public void ACK_vRetSetRemoteRecord(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetRemoteRecord:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.RET_SET_REMOTE_RECORD);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetRemoteDefenceResult(String contactId, int state) {
        Log.e(this.TAG, "vRetGetRemoteDefenceResult:" + state);
        if (state == 1) {
            FList.getInstance().setDefenceState(contactId, 1);
        } else {
            FList.getInstance().setDefenceState(contactId, 0);
        }
        Intent defence = new Intent();
        defence.setAction(P2P.RET_GET_REMOTE_DEFENCE);
        defence.putExtra("state", state);
        defence.putExtra(ContactDB.COLUMN_CONTACT_ID, contactId);
        MyApp.app.sendBroadcast(defence);
    }

    public void vRetGetRemoteRecordResult(int state) {
        Log.e(this.TAG, "vRetGetRemoteRecordResult:" + state);
        Intent record = new Intent();
        record.setAction(P2P.RET_GET_REMOTE_RECORD);
        record.putExtra("state", state);
        MyApp.app.sendBroadcast(record);
    }

    public void vRetSetRemoteDefenceResult(int result) {
        Log.e(this.TAG, "vRetSetRemoteDefenceResult:" + result);
        Intent defence = new Intent();
        defence.setAction(P2P.RET_SET_REMOTE_DEFENCE);
        defence.putExtra("state", result);
        MyApp.app.sendBroadcast(defence);
    }

    public void vRetSetRemoteRecordResult(int result) {
        Log.e(this.TAG, "vRetSetRemoteRecordResult:" + result);
        Intent record = new Intent();
        record.setAction(P2P.RET_SET_REMOTE_RECORD);
        record.putExtra("state", result);
        MyApp.app.sendBroadcast(record);
    }

    public void ACK_vRetSetInitPassword(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetSetInitPassword:" + result);
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_SET_INIT_PASSWORD);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetInitPasswordResult(int result) {
        Log.e(this.TAG, "vRetSetInitPasswordResult******:" + result);
        Intent i = new Intent();
        i.setAction(P2P.RET_SET_INIT_PASSWORD);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetGetDeviceVersion(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetGetDeviceVersion:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_GET_DEVICE_INFO);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetDeviceVersion(int result, String cur_version, int iUbootVersion, int iKernelVersion, int iRootfsVersion) {
        Log.e(this.TAG, "vRetGetDeviceVersion:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.putExtra("cur_version", cur_version);
        i.putExtra("iUbootVersion", iUbootVersion);
        i.putExtra("iKernelVersion", iKernelVersion);
        i.putExtra("iRootfsVersion", iRootfsVersion);
        i.setAction(P2P.RET_GET_DEVICE_INFO);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetDeviceVersion2(int result, String cur_version, int iUbootVersion, int iKernelVersion, int iRootfsVersion, String device_ip) {
        Log.e(this.TAG, "vRetGetDeviceVersion2:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.putExtra("cur_version", cur_version);
        i.putExtra("iUbootVersion", iUbootVersion);
        i.putExtra("iKernelVersion", iKernelVersion);
        i.putExtra("iRootfsVersion", iRootfsVersion);
        i.putExtra("device_ip", device_ip);
        i.setAction(P2P.RET_GET_DEVICE_INFO2);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetCheckDeviceUpdate(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetCheckDeviceUpdate:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_CHECK_DEVICE_UPDATE);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetCheckDeviceUpdate(int result, String cur_version, String upg_version) {
        Log.e(this.TAG, "vRetCheckDeviceUpdate:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.putExtra("cur_version", cur_version);
        i.putExtra("upg_version", upg_version);
        i.setAction(P2P.RET_CHECK_DEVICE_UPDATE);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetDoDeviceUpdate(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetDoDeviceUpdate:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_DO_DEVICE_UPDATE);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetDoDeviceUpdate(int result, int value) {
        Log.e(this.TAG, "vRetDoDeviceUpdate:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.putExtra(Param.VALUE, value);
        i.setAction(P2P.RET_DO_DEVICE_UPDATE);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetCancelDeviceUpdate(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetCancelDeviceUpdate:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_CANCEL_DEVICE_UPDATE);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetCancelDeviceUpdate(int result) {
        Log.e(this.TAG, "vRetCancelDeviceUpdate:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.RET_CHECK_DEVICE_UPDATE);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetGetRecordFileList(int msgId, int result) {
        Log.e(this.TAG, "ACK_vRetGetRecordFileList:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(P2P.ACK_RET_GET_PLAYBACK_FILES);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetRecordFiles(String[] names, int[] frameRate) {
        Log.e(this.TAG, "vRetGetRecordFiles:");
        Intent i = new Intent();
        i.setAction(P2P.RET_GET_PLAYBACK_FILES);
        i.putExtra("recordList", names);
        i.putExtra("rateList", frameRate);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetFriendStatus(int count, String[] contactIDs, int[] status, int[] types) {
        Log.e(this.TAG, "vRetGetFriendStatus:" + count);
        FList flist = FList.getInstance();
        for (int i = 0; i < count; i++) {
            flist.setState(contactIDs[i], status[i]);
            if (contactIDs[i].charAt(0) == '0') {
                flist.setType(contactIDs[i], 3);
            } else if (status[i] == 1) {
                flist.setType(contactIDs[i], types[i]);
            }
        }
        FList.getInstance().sort();
        FList.getInstance().getDefenceState();
        Intent friends = new Intent();
        friends.setAction(Action.GET_FRIENDS_STATE);
        MyApp.app.sendBroadcast(friends);
        Intent i2 = new Intent();
        i2.setAction(Action.GET_DEVICE_TYPE);
        i2.putExtra("contactIDs", contactIDs);
        i2.putExtra("types", types);
        MyApp.app.sendBroadcast(i2);
    }

    public void ACK_vRetMessage(int msgId, int result) {
        Intent i = new Intent();
        i.setAction(Action.RECEIVE_MSG);
        i.putExtra("msgFlag", msgId + "");
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetMessage(String contactId, String msgStr) {
        Contact contact = FList.getInstance().isContact(contactId);
        if (contact != null) {
            Message msg = new Message();
            msg.activeUser = NpcCommon.mThreeNum;
            msg.fromId = contactId;
            msg.toId = NpcCommon.mThreeNum;
            msg.msg = msgStr;
            msg.msgTime = String.valueOf(System.currentTimeMillis());
            msg.msgFlag = String.valueOf(-1);
            msg.msgState = String.valueOf(4);
            contact.messageCount++;
            FList.getInstance().update(contact);
            DataManager.insertMessage(MyApp.app, msg);
            Intent i = new Intent();
            i.setAction(MessageActivity.RECEIVER_MSG);
            Intent k = new Intent();
            k.setAction(Action.REFRESH_CONTANTS);
            MyApp.app.sendBroadcast(i);
            MyApp.app.sendBroadcast(k);
            MusicManger.getInstance().playMsgMusic();
        }
    }

    public void vRetSysMessage(String msg) {
        SysMessage sysMessage = new SysMessage();
        sysMessage.activeUser = NpcCommon.mThreeNum;
        sysMessage.msg = msg;
        sysMessage.msg_time = String.valueOf(System.currentTimeMillis());
        sysMessage.msgState = 0;
        sysMessage.msgType = 2;
        DataManager.insertSysMessage(MyApp.app, sysMessage);
        Intent i = new Intent();
        i.setAction(SysMsgActivity.REFRESH);
        MyApp.app.sendBroadcast(i);
        Intent k = new Intent();
        k.setAction(Action.RECEIVE_SYS_MSG);
        MyApp.app.sendBroadcast(k);
    }

    public void vRetCustomCmd(String contactId, String cmd) {
        Log.e("my", cmd);
    }

    public void ACK_vRetCustomCmd(int msgId, int result) {
        Log.e("my", result + "");
    }

    public void vRetDeviceNotSupport() {
        Intent i = new Intent();
        i.setAction(P2P.RET_DEVICE_NOT_SUPPORT);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetImageReverse(int msgId, int result) {
        Intent i = new Intent();
        i.setAction(P2P.ACK_VRET_SET_IMAGEREVERSE);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetImageReverse(int result) {
    }

    public void vRetGetImageReverseResult(int type) {
        Intent i = new Intent();
        i.setAction(P2P.RET_GET_IMAGE_REVERSE);
        i.putExtra("type", type);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetInfraredSwitch(int msgId, int result) {
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_SET_INFRARED_SWITCH);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetInfraredSwitch(int state) {
        Log.e(this.TAG, "vRetGetInfraredSwitch:" + state);
        Intent i = new Intent();
        i.setAction(P2P.RET_GET_INFRARED_SWITCH);
        i.putExtra("state", state);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetInfraredSwitch(int result) {
    }

    public void ACK_vRetSetWiredAlarmInput(int msgId, int state) {
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_SET_WIRED_ALARM_INPUT);
        i.putExtra("state", state);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetWiredAlarmOut(int msgId, int state) {
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_SET_WIRED_ALARM_OUT);
        i.putExtra("state", state);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetAutomaticUpgrade(int msgId, int state) {
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_SET_AUTOMATIC_UPGRADE);
        i.putExtra("state", state);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetWiredAlarmInput(int state) {
        Intent i = new Intent();
        i.setAction(P2P.RET_GET_WIRED_ALARM_INPUT);
        i.putExtra("state", state);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetWiredAlarmOut(int state) {
        Intent i = new Intent();
        i.setAction(P2P.RET_GET_WIRED_ALARM_OUT);
        i.putExtra("state", state);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetAutomaticUpgrade(int state) {
        Intent i = new Intent();
        i.setAction(P2P.RET_GET_AUTOMATIC_UPGRAD);
        i.putExtra("state", state);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetWiredAlarmInput(int state) {
    }

    public void vRetSetWiredAlarmOut(int state) {
    }

    public void vRetSetAutomaticUpgrade(int state) {
    }

    public void ACK_VRetSetVisitorDevicePassword(int msgId, int state) {
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_SET_VISITOR_DEVICE_PASSWORD);
        i.putExtra("state", state);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetVisitorDevicePassword(int result) {
        Intent i = new Intent();
        i.setAction(P2P.RET_SET_VISITOR_DEVICE_PASSWORD);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetTimeZone(int msgId, int state) {
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_SET_TIME_ZONE);
        i.putExtra("state", state);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetTimeZone(int state) {
        Intent i = new Intent();
        i.setAction(P2P.RET_GET_TIME_ZONE);
        i.putExtra("state", state);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetTimeZone(int result) {
    }

    public void vRetGetSdCard(int result1, int result2, int SDcardID, int state) {
        Intent i = new Intent();
        i.setAction(P2P.RET_GET_SD_CARD_CAPACITY);
        i.putExtra("total_capacity", result1);
        i.putExtra("remain_capacity", result2);
        i.putExtra("SDcardID", SDcardID);
        i.putExtra("state", state);
        MyApp.app.sendBroadcast(i);
        Log.e("sdid", SDcardID + "");
    }

    public void ACK_vRetGetSDCard(int msgId, int state) {
        Intent i = new Intent();
        i.setAction(P2P.ACK_GET_SD_CARD_CAPACITY);
        i.putExtra("result", state);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSdFormat(int msgId, int state) {
        Intent i = new Intent();
        i.setAction(P2P.ACK_GET_SD_CARD_FORMAT);
        i.putExtra("result", state);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSdFormat(int result) {
        Intent i = new Intent();
        i.setAction(P2P.RET_GET_SD_CARD_FORMAT);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void VRetGetUsb(int result1, int result2, int SDcardID, int state) {
        Intent i = new Intent();
        i.setAction(P2P.RET_GET_USB_CAPACITY);
        i.putExtra("total_capacity", result1);
        i.putExtra("remain_capacity", result2);
        i.putExtra("SDcardID", SDcardID);
        i.putExtra("state", state);
        MyApp.app.sendBroadcast(i);
    }

    public void ACK_vRetSetGPIO(int msgId, int state) {
    }

    public void ACK_vRetSetGPIO1_0(int msgId, int state) {
    }

    public void vRetGetAudioDeviceType(int type) {
    }

    public void vRetSetGPIO(int result) {
    }

    public void vRetSetUploadToSvr(int result) {
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_SET_UPLOAD_TO_SVR);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetAlarmPushStatus(int result) {
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_SET_ALARM_PUSH_STATUS);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetAlarmPushStatus(int result, String account, int onoff) {
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_GET_ALARM_PUSH_STATUS);
        i.putExtra("result", result);
        i.putExtra("account", account);
        i.putExtra("onoff", onoff);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetAllPushAccount(int result, int num, String[] accountList) {
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_GET_ALL_ALARM_ACCOUNT);
        i.putExtra("result", result);
        i.putExtra("num", num);
        i.putExtra("accountList", accountList);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetLampSwitch(int result) {
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_SET_LAMP_SWITCH);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetLampSwitch(int result, int onoff) {
        Intent i = new Intent();
        i.setAction(P2P.ACK_RET_GET_LAMP_SWITCH);
        i.putExtra("result", result);
        i.putExtra("onoff", onoff);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetAlarmTiming(int result) {
        Intent i = new Intent();
        i.setAction(P2P.RET_SET_ALARM_TIMING);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetAlarmTiming(int result, int[] startH, int[] startM, int[] endH, int[] endM) {
        Intent i = new Intent();
        i.setAction(P2P.RET_GET_ALARM_TIMING);
        i.putExtra("result", result);
        i.putExtra("startH", startH);
        i.putExtra("startM", startM);
        i.putExtra("endH", endH);
        i.putExtra("endM", endM);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetHumanDetect(int result) {
        Intent i = new Intent();
        i.setAction(P2P.RET_SET_HUMAN_DETECT);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetHumanDetect(int result, int enable) {
        Intent i = new Intent();
        i.setAction(P2P.RET_GET_HUMAN_DETECT);
        i.putExtra("result", result);
        i.putExtra("enable", enable);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetHumanDetectValidData(int result, int days, String time) {
        Intent i = new Intent();
        i.setAction(P2P.RET_GET_HUMAN_DETECT_DATA);
        i.putExtra("result", result);
        i.putExtra("days", days);
        i.putExtra(Values.TIME, time);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetAlarmLogSyncData(int count, int[] type, int[] time, int[] addition, String[] data) {
        Intent i = new Intent();
        i.setAction(P2P.RET_ALARM_LOG_SYNC_DATA);
        i.putExtra("count", count);
        i.putExtra("type", type);
        i.putExtra(Values.TIME, time);
        i.putExtra("addition", addition);
        i.putExtra("data", data);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetAlarmLogSyncStatus(int status) {
        Intent i = new Intent();
        i.setAction(P2P.RET_ALARM_LOG_SYNC_STATUS);
        i.putExtra("status", status);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetImgLdc(int result, int onoff) {
        Intent i = new Intent();
        i.setAction(P2P.RET_GET_IMG_LDC);
        i.putExtra("result", result);
        i.putExtra("onoff", onoff);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetImgLdc(int result) {
        Intent i = new Intent();
        i.setAction(P2P.RET_SET_IMG_LDC);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetDownloadShortAV(int status) {
        Intent i = new Intent();
        i.setAction(P2P.RET_DOWNLOAD_SHORT_AV);
        i.putExtra("status", status);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetPlayShortAV(int status, String path) {
        Intent i = new Intent();
        i.setAction(P2P.RET_PLAY_SHORT_AV);
        i.putExtra("state", status);
        i.putExtra(ClientCookie.PATH_ATTR, path);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetLowWarning(int status) {
        Intent i = new Intent();
        i.setAction(P2P.RET_LOW_WARNING);
        i.putExtra("state", 1);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetDownloadShortAVPic(int status, int count, String path) {
        ArrayList<String> list = null;
        try {
            list = Utils.deCompressTargz(new File(path), "/storage/emulated/0/hikam_shortav/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent i = new Intent();
        i.setAction(P2P.RET_FRESH_PIC);
        i.putExtra("fresh", 1);
        i.putExtra("freshImagePath", list);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetPirLed(int status) {
        Intent i = new Intent();
        i.setAction(P2P.RET_GET_PIR_LED);
        i.putExtra("status", status);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetPirLed(int status) {
        Intent i = new Intent();
        i.setAction(P2P.RET_SET_PIR_LED);
        i.putExtra("status", status);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetGetRtsp(int result, int state) {
        Intent i = new Intent();
        i.setAction(P2P.RET_GET_RTSP_SWITCH);
        i.putExtra("result", result);
        i.putExtra("state", state);
        MyApp.app.sendBroadcast(i);
    }

    public void vRetSetRtsp(int result) {
        Intent i = new Intent();
        i.setAction(P2P.RET_SET_RTSP_SWITCH);
        i.putExtra("result", result);
        MyApp.app.sendBroadcast(i);
    }
}
