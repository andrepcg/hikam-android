package com.jwkj.data;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import com.jwkj.global.NpcCommon;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SharedPreferencesManager {
    public static final String ALARM_TIME_INTERVAL = "alarm_time_interval";
    public static final String IGONORE_ALARM_TIME = "ignore_alarm_time";
    public static final String IS_AUTO_START = "is_auto_start";
    public static final String IS_REMEMBER_PASS = "is_remember_pass";
    public static final String IS_REMEMBER_PASS_EMAIL = "is_remember_pass_email";
    public static final String IS_SHOW_NOTIFY = "is_show_notify";
    public static final String KEY_ALARM_PUSH_METHOD = "alarm_push_method";
    public static final String KEY_A_BELL_SELECTPOS = "a_selectpos";
    public static final String KEY_A_BELL_TYPE = "a_bell_type";
    public static final String KEY_A_MUTE_STATE = "a_mute_state";
    public static final String KEY_A_OPEN_VIDEO_STATE = "a_open_video_state";
    public static final String KEY_A_SD_BELL = "a_sd_bell";
    public static final String KEY_A_SYS_BELL = "a_system_bell";
    public static final String KEY_A_VIBRATE_STATE = "a_vibrate_state";
    public static final String KEY_C_BELL_SELECTPOS = "c_selectpos";
    public static final String KEY_C_BELL_TYPE = "c_bell_type";
    public static final String KEY_C_MUTE_STATE = "c_mute_state";
    public static final String KEY_C_SD_BELL = "c_sd_bell";
    public static final String KEY_C_SYS_BELL = "c_system_bell";
    public static final String KEY_C_VIBRATE_STATE = "c_vibrate_state";
    public static final String KEY_NAMES = "names";
    public static final String KEY_RECENTCODE = "recentCode";
    public static final String KEY_RECENTNAME = "recentName";
    public static final String KEY_RECENTNAME_EMAIL = "recentName_email";
    public static final String KEY_RECENTPASS = "recentPass";
    public static final String KEY_RECENTPASS_EMAIL = "recentPass_email";
    public static final String KEY_UPDATE_CHECKTIME = "update_checktime";
    public static final String LAST_AUTO_CHECK_UPDATE_TIME = "last_auto_check_update_time";
    public static final String NOTIFY_VERSION = "notify_version";
    public static final String RECENT_LOGIN_TYPE = "recent_login_type";
    public static final String SP_FILE_GWELL = "gwell";
    public static final int TYPE_BELL_SD = 1;
    public static final int TYPE_BELL_SYS = 0;
    private static SharedPreferencesManager manager = null;

    private SharedPreferencesManager() {
    }

    public static synchronized SharedPreferencesManager getInstance() {
        SharedPreferencesManager sharedPreferencesManager;
        synchronized (SharedPreferencesManager.class) {
            if (manager == null) {
                synchronized (SharedPreferencesManager.class) {
                    if (manager == null) {
                        manager = new SharedPreferencesManager();
                    }
                }
            }
            sharedPreferencesManager = manager;
        }
        return sharedPreferencesManager;
    }

    public String getData(Context context, String fileName, String key) {
        return context.getSharedPreferences(fileName, 0).getString(key, "");
    }

    public void putData(Context context, String fileName, String key, String value) {
        Editor editor = context.getSharedPreferences(fileName, 0).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public int getIntData(Context context, String fileName, String key) {
        return context.getSharedPreferences(fileName, 0).getInt(key, 0);
    }

    public void putIntData(Context context, String fileName, String key, int value) {
        Editor editor = context.getSharedPreferences(fileName, 0).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void putNotifyVersion(int version, Context context) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(NpcCommon.mThreeNum + NOTIFY_VERSION, version);
        editor.commit();
    }

    public int getNotifyVersion(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(NpcCommon.mThreeNum + NOTIFY_VERSION, 0);
    }

    public void putLastAutoCheckUpdateTime(long time, Context context) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putLong(NpcCommon.mThreeNum + LAST_AUTO_CHECK_UPDATE_TIME, time);
        editor.commit();
    }

    public long getLastAutoCheckUpdateTime(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getLong(NpcCommon.mThreeNum + LAST_AUTO_CHECK_UPDATE_TIME, 0);
    }

    public void putIgnoreAlarmTime(Context context, long time) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putLong(NpcCommon.mThreeNum + IGONORE_ALARM_TIME, time);
        editor.commit();
    }

    public long getIgnoreAlarmTime(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getLong(NpcCommon.mThreeNum + IGONORE_ALARM_TIME, 0);
    }

    public void putIsAutoStart(Context context, boolean bool) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putBoolean(NpcCommon.mThreeNum + IS_AUTO_START, bool);
        editor.commit();
    }

    public boolean getIsAutoStart(Context context, String threeNum) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getBoolean(threeNum + IS_AUTO_START, false);
    }

    public void putAlarmTimeInterval(Context context, int time) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(NpcCommon.mThreeNum + ALARM_TIME_INTERVAL, time);
        editor.commit();
    }

    public int getAlarmTimeInterval(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(NpcCommon.mThreeNum + ALARM_TIME_INTERVAL, 10);
    }

    public void putIsShowNotify(Context context, boolean bool) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putBoolean(NpcCommon.mThreeNum + IS_SHOW_NOTIFY, bool);
        editor.commit();
    }

    public boolean getIsShowNotify(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getBoolean(NpcCommon.mThreeNum + IS_SHOW_NOTIFY, false);
    }

    public void putIsRememberPass(Context context, boolean bool) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putBoolean(IS_REMEMBER_PASS, bool);
        editor.commit();
    }

    public boolean getIsRememberPass(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getBoolean(IS_REMEMBER_PASS, true);
    }

    public void putIsRememberPass_email(Context context, boolean bool) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putBoolean(IS_REMEMBER_PASS_EMAIL, bool);
        editor.commit();
    }

    public boolean getIsRememberPass_email(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getBoolean(IS_REMEMBER_PASS_EMAIL, true);
    }

    public void putRecentLoginType(Context context, int type) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(RECENT_LOGIN_TYPE, type);
        editor.commit();
    }

    public int getRecentLoginType(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(RECENT_LOGIN_TYPE, 0);
    }

    public void putCVibrateState(int state, Context context) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(NpcCommon.mThreeNum + KEY_C_VIBRATE_STATE, state);
        editor.commit();
    }

    public int getCVibrateState(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(NpcCommon.mThreeNum + KEY_C_VIBRATE_STATE, 1);
    }

    public void putAVibrateState(int state, Context context) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(NpcCommon.mThreeNum + KEY_A_VIBRATE_STATE, state);
        editor.commit();
    }

    public int getAVibrateState(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(NpcCommon.mThreeNum + KEY_A_VIBRATE_STATE, 1);
    }

    public void putAOpenVideoState(int state, Context context) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(NpcCommon.mThreeNum + KEY_A_OPEN_VIDEO_STATE, state);
        editor.commit();
    }

    public int getAOpenVideoState(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(NpcCommon.mThreeNum + KEY_A_OPEN_VIDEO_STATE, 0);
    }

    public void putAlarmPushMethod(int method, Context context) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(NpcCommon.mThreeNum + KEY_ALARM_PUSH_METHOD, method);
        editor.commit();
    }

    public int getAlarmPushMethod(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(NpcCommon.mThreeNum + KEY_ALARM_PUSH_METHOD, 1);
    }

    public void putCSystemBellId(int bellId, Context context) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(NpcCommon.mThreeNum + KEY_C_SYS_BELL, bellId);
        editor.commit();
    }

    public int getCSystemBellId(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(NpcCommon.mThreeNum + KEY_C_SYS_BELL, -1);
    }

    public void putASystemBellId(int bellId, Context context) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(NpcCommon.mThreeNum + KEY_A_SYS_BELL, bellId);
        editor.commit();
    }

    public int getASystemBellId(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(NpcCommon.mThreeNum + KEY_A_SYS_BELL, -1);
    }

    public int getCSdBellId(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(NpcCommon.mThreeNum + KEY_C_SD_BELL, -1);
    }

    public void putCSdBellId(int bellId, Context context) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(NpcCommon.mThreeNum + KEY_C_SD_BELL, bellId);
        editor.commit();
    }

    public int getASdBellId(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(NpcCommon.mThreeNum + KEY_A_SD_BELL, -1);
    }

    public void putASdBellId(int bellId, Context context) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(NpcCommon.mThreeNum + KEY_A_SD_BELL, bellId);
        editor.commit();
    }

    public void putCBellSelectPos(int selectpos, Context context) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(NpcCommon.mThreeNum + KEY_C_BELL_SELECTPOS, selectpos);
        editor.commit();
    }

    public int getCBellSelectPos(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(NpcCommon.mThreeNum + KEY_C_BELL_SELECTPOS, 0);
    }

    public void putABellSelectPos(int selectpos, Context context) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(NpcCommon.mThreeNum + KEY_A_BELL_SELECTPOS, selectpos);
        editor.commit();
    }

    public int getABellSelectPos(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(NpcCommon.mThreeNum + KEY_A_BELL_SELECTPOS, 0);
    }

    public void putCMuteState(int state, Context context) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(NpcCommon.mThreeNum + KEY_C_MUTE_STATE, state);
        editor.commit();
    }

    public int getCMuteState(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(NpcCommon.mThreeNum + KEY_C_MUTE_STATE, 1);
    }

    public void putAMuteState(int state, Context context) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(NpcCommon.mThreeNum + KEY_A_MUTE_STATE, state);
        editor.commit();
    }

    public int getAMuteState(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(NpcCommon.mThreeNum + KEY_A_MUTE_STATE, 1);
    }

    public void putCBellType(int type, Context context) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(NpcCommon.mThreeNum + KEY_C_BELL_TYPE, type);
        editor.commit();
    }

    public int getCBellType(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(NpcCommon.mThreeNum + KEY_C_BELL_TYPE, 0);
    }

    public void putABellType(int type, Context context) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(NpcCommon.mThreeNum + KEY_A_BELL_TYPE, type);
        editor.commit();
    }

    public int getABellType(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(NpcCommon.mThreeNum + KEY_A_BELL_TYPE, 0);
    }

    public void putAsyncTimeByDevice(Context context, String deviceId, long time) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putLong(deviceId + "sync", time);
        editor.commit();
    }

    public long getAsyncTimeByDevice(Context context, String deviceId) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getLong(deviceId + "sync", 0);
    }

    public void setFbPushToken(Context context, String token) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putString("token", token);
        editor.commit();
    }

    public String getFbPushToken(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getString("token", "");
    }

    public void setRecordResolution(Context context, String tag, int resolution) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putInt(tag, resolution);
        editor.commit();
    }

    public int getRecordResolution(Context context, String tag) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getInt(tag, 0);
    }

    public void setDecoupleUser(Context context, String userValue) {
        Set<String> userSet = getDecoupleUser(context);
        if (userSet == null) {
            userSet = new HashSet();
        }
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        userSet.add(userValue);
        editor.putStringSet("DecoupleUserSet", userSet);
        editor.commit();
    }

    public Set<String> getDecoupleUser(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getStringSet("DecoupleUserSet", null);
    }

    public void setIsFirstInstall(Context context) {
        Editor editor = context.getSharedPreferences(SP_FILE_GWELL, 0).edit();
        editor.putBoolean("firstInstall", false);
        editor.commit();
    }

    public boolean getIsFirstInstall(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getBoolean("firstInstall", true);
    }

    public Map<String, ?> getAllKey(Context context) {
        return context.getSharedPreferences(SP_FILE_GWELL, 0).getAll();
    }
}
