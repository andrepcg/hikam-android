package com.jwkj;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.hikam.C0291R;
import com.jwkj.activity.AlarmActivity;
import com.jwkj.data.AlarmMask;
import com.jwkj.data.AlarmRecord;
import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.FList;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.MusicManger;
import com.jwkj.utils.Utils;
import com.p2p.core.BaseCoreActivity;
import com.tencent.bugly.Bugly;
import java.text.SimpleDateFormat;
import java.util.Date;

public class P2PConnect {
    public static boolean IS_LIVE_STATE = false;
    public static final int P2P_STATE_ALARM = 4;
    public static final int P2P_STATE_CALLING = 1;
    public static final int P2P_STATE_NONE = 0;
    public static final int P2P_STATE_READY = 2;
    static String TAG = "p2p";
    private static int currentDeviceType = 7;
    private static String current_call_id = "0";
    private static int current_state = 0;
    private static boolean isAlarm = false;
    private static boolean isAlarming = false;
    private static boolean isPlaying = false;
    static Context mContext;
    private static int mode = 5;
    private static int number = 1;

    public P2PConnect(Context context) {
        mContext = context;
    }

    public static int getCurrent_state() {
        return current_state;
    }

    public static void setCurrent_state(int current_state) {
        current_state = current_state;
        switch (current_state) {
            case 0:
                Log.e(TAG, "P2P_STATE_NONE");
                return;
            case 1:
                Log.e(TAG, "P2P_STATE_CALLING");
                return;
            case 2:
                Log.e(TAG, "P2P_STATE_READY");
                return;
            default:
                return;
        }
    }

    public static int getMode() {
        return mode;
    }

    public static void setMode(int mode) {
        mode = mode;
    }

    public static int getNumber() {
        return number;
    }

    public static void setNumber(int number) {
        number = number;
    }

    public static String getCurrent_call_id() {
        return current_call_id;
    }

    public static void setCurrent_call_id(String current_call_id) {
        current_call_id = current_call_id;
    }

    public static void setCurrentDeviceType(int type) {
        currentDeviceType = type;
    }

    public static int getCurrentDeviceType() {
        return currentDeviceType;
    }

    public static boolean isPlaying() {
        return isPlaying;
    }

    public static void setPlaying(boolean isPlaying) {
        isPlaying = isPlaying;
    }

    public static void setAlarm(boolean isAlarm) {
        Log.e("ward", "P2PConnect : " + isAlarm);
        isAlarm = isAlarm;
    }

    public static boolean getIsAlarm() {
        return isAlarm;
    }

    public static synchronized void vCalling(boolean isOutCall, int type) {
        synchronized (P2PConnect.class) {
            Log.e(TAG, "vCalling:" + current_call_id);
            setCurrentDeviceType(type);
            if (!isOutCall && current_state == 0) {
                setCurrent_state(1);
                Intent call = new Intent();
                call.setFlags(268435456);
                call.setClass(mContext, CallActivity.class);
                call.putExtra("callId", current_call_id);
                call.putExtra("type", 0);
                mContext.startActivity(call);
            }
        }
    }

    public static synchronized void vReject(String msg) {
        synchronized (P2PConnect.class) {
            Log.e(TAG, "vReject:" + msg);
            if (!msg.equals("")) {
                C0568T.showShort(mContext, (CharSequence) msg);
            }
            try {
                setCurrent_state(0);
                setMode(5);
                setNumber(1);
                MusicManger.getInstance().stop();
                MusicManger.getInstance().stopVibrate();
                Intent refreshContans = new Intent();
                refreshContans.setAction(Action.ACTION_REFRESH_NEARLY_TELL);
                MyApp.app.sendBroadcast(refreshContans);
                Intent reject = new Intent();
                reject.setAction(P2P.P2P_REJECT);
                mContext.sendBroadcast(reject);
            } catch (Exception e) {
                Log.e(TAG, "vReject:error");
            }
            Log.e(TAG, "vReject:end");
        }
    }

    public static synchronized void vAccept(int type, int state) {
        synchronized (P2PConnect.class) {
            Log.e(TAG, "vAccept");
            MusicManger.getInstance().stop();
            MusicManger.getInstance().stopVibrate();
            Intent accept = new Intent();
            accept.setAction(P2P.P2P_ACCEPT);
            mContext.sendBroadcast(accept);
        }
    }

    public static synchronized void vConnectReady() {
        synchronized (P2PConnect.class) {
            Log.e(TAG, "vConnectReady");
            if (current_state != 2) {
                setCurrent_state(2);
                Intent ready = new Intent();
                ready.setAction(P2P.P2P_READY);
                mContext.sendBroadcast(ready);
            }
        }
    }

    public static synchronized void vAllarming(String model, String id, int type, boolean isSupport, int group, int item, String uuid) {
        synchronized (P2PConnect.class) {
            Log.e("P2PConnect", "｜￣￣￣￣￣￣￣￣ vAllarming:" + uuid + "￣￣￣￣￣￣￣￣￣｜");
            Log.e("P2PConnect", "｜ isAlarming:" + isAlarming + "id: " + id + "type: " + type);
            Log.e("P2PConnect", "｜group: " + group + " item: " + item + "type: " + type + " currentCallid:" + current_call_id + "currentstate:" + current_state);
            if (!"".equals(uuid)) {
                String[] paths = new String[]{uuid};
                Contact contacts = FList.getInstance().isContact(id);
                P2pJni.P2PClientSdkGetShortAVPic(contacts.contactId, contacts.contactPassword, paths, 1);
            }
            for (AlarmMask alarmMask : DataManager.findAlarmMaskByActiveUser(mContext, NpcCommon.mThreeNum)) {
                if (alarmMask.deviceId.equals(Utils.showShortDevID(id))) {
                    break;
                }
            }
            AlarmRecord alarmRecord = new AlarmRecord();
            alarmRecord.alarmTime = String.valueOf(System.currentTimeMillis() / 1000);
            alarmRecord.deviceId = id;
            alarmRecord.alarmType = type;
            alarmRecord.activeUser = NpcCommon.mThreeNum;
            alarmRecord.uuid = uuid;
            if ((type == 1 || type == 6) && isSupport) {
                alarmRecord.group = group;
                alarmRecord.item = item;
            } else {
                alarmRecord.group = -1;
                alarmRecord.item = -1;
            }
            DataManager.insertAlarmRecord(mContext, alarmRecord);
            Intent i = new Intent();
            i.setAction(Action.REFRESH_ALARM_RECORD);
            i.putExtra("id", alarmRecord.deviceId);
            i.putExtra("operate", "add");
            mContext.sendBroadcast(i);
            if (NpcCommon.mThreeNum != null && !"".equals(NpcCommon.mThreeNum) && ((current_state != 1 || !current_call_id.equals(id)) && (current_state != 2 || !current_call_id.equals(id) || !MonitorActivity.isPlaying))) {
                MyApp.PUSH_METHOD = SharedPreferencesManager.getInstance().getAlarmPushMethod(MyApp.app);
                if (MyApp.PUSH_METHOD == 2 && BaseCoreActivity.isBG()) {
                    String alarm_name;
                    Contact contact = DataManager.findContactByActiveUserAndContactId(mContext, NpcCommon.mThreeNum, String.valueOf(id));
                    if (contact != null) {
                        alarm_name = contact.contactName;
                    } else {
                        alarm_name = Utils.showShortDevID(id);
                    }
                    if (contact != null) {
                        String[] data = new String[]{alarmRecord.deviceId, contact.contactPassword, model, Bugly.SDK_IS_DEV};
                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                        StringBuilder contentBuilder = new StringBuilder();
                        contentBuilder.append(alarm_name + " ");
                        switch (type) {
                            case 1:
                                contentBuilder.append(mContext.getResources().getString(C0291R.string.allarm_type1));
                                if (isSupport) {
                                    contentBuilder.append(" " + mContext.getResources().getString(C0291R.string.area) + ":" + Utils.getDefenceAreaByGroup(mContext, group));
                                    contentBuilder.append(" " + mContext.getResources().getString(C0291R.string.channel) + ":" + (item + 1));
                                    break;
                                }
                                break;
                            case 2:
                                contentBuilder.append(mContext.getResources().getString(C0291R.string.allarm_type2));
                                break;
                            case 3:
                                contentBuilder.append(mContext.getResources().getString(C0291R.string.allarm_type3));
                                break;
                            case 5:
                                contentBuilder.append(mContext.getResources().getString(C0291R.string.allarm_type5));
                                break;
                            case 6:
                                contentBuilder.append(mContext.getResources().getString(C0291R.string.low_voltage_alarm));
                                if (isSupport) {
                                    contentBuilder.append(mContext.getResources().getString(C0291R.string.area) + ":" + Utils.getDefenceAreaByGroup(mContext, group));
                                    contentBuilder.append(mContext.getResources().getString(C0291R.string.channel) + ":" + (item + 1));
                                    break;
                                }
                                break;
                            case 7:
                                contentBuilder.append(mContext.getResources().getString(C0291R.string.allarm_type4));
                                break;
                            case 8:
                                contentBuilder.append(mContext.getResources().getString(C0291R.string.defence));
                                break;
                            case 9:
                                contentBuilder.append(mContext.getResources().getString(C0291R.string.no_defence));
                                break;
                            case 10:
                                contentBuilder.append(mContext.getResources().getString(C0291R.string.battery_low_alarm));
                                break;
                            case 13:
                                contentBuilder.append(mContext.getResources().getString(C0291R.string.door_bell));
                                break;
                            case 31:
                                contentBuilder.append(mContext.getResources().getString(C0291R.string.humanoid_detection));
                                break;
                            case 32:
                                contentBuilder.append(mContext.getResources().getString(C0291R.string.humanoid_detection_fallback));
                                break;
                        }
                        contentBuilder.append(" " + currentTime);
                        MyApp.app.showAlarmNotification(MyApp.app.getString(C0291R.string.alarm_info), contentBuilder.toString(), data);
                    }
                } else {
                    if (!(type == 8 || type == 9)) {
                        if (System.currentTimeMillis() - SharedPreferencesManager.getInstance().getIgnoreAlarmTime(mContext) < ((long) (SharedPreferencesManager.getInstance().getAlarmTimeInterval(mContext) * 1000))) {
                            Log.e("ward", "<<<<<<< return ; ");
                        }
                    }
                    if (isAlarm) {
                        Log.e("ward", "isAlarm : " + isAlarm + ". no send ");
                        Intent it = new Intent();
                        it.setAction(Action.CHANGE_ALARM_MESSAGE);
                        it.putExtra("alarm_id", id);
                        it.putExtra("alarm_type", type);
                        it.putExtra("isSupport", isSupport);
                        it.putExtra("group", group);
                        it.putExtra("item", item);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mContext.sendBroadcast(it);
                    } else {
                        Log.e("ward", "isAlarm : " + isAlarm + ". send ");
                        if (IS_LIVE_STATE) {
                            Intent alarmLive = new Intent();
                            alarmLive.setAction(Action.DIALOG_ALARM_PUSH);
                            alarmLive.putExtra("device_model", model);
                            alarmLive.putExtra("alarm_id", id);
                            alarmLive.putExtra("alarm_type", type);
                            alarmLive.putExtra("isSupport", isSupport);
                            alarmLive.putExtra("group", group);
                            alarmLive.putExtra("item", item);
                            MyApp.app.sendBroadcast(alarmLive);
                        } else {
                            Intent alarm = new Intent();
                            alarm.setFlags(268435456);
                            alarm.setClass(mContext, AlarmActivity.class);
                            alarm.putExtra("device_model", model);
                            alarm.putExtra("alarm_id", id);
                            alarm.putExtra("alarm_type", type);
                            alarm.putExtra("isSupport", isSupport);
                            alarm.putExtra("group", group);
                            alarm.putExtra("item", item);
                            mContext.startActivity(alarm);
                        }
                    }
                }
            }
        }
    }

    public static synchronized void vEndAllarm() {
        synchronized (P2PConnect.class) {
            isAlarming = false;
        }
    }
}
