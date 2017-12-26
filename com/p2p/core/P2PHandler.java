package com.p2p.core;

import android.content.Context;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.jwkj.activity.InfraredRemoteActivity;
import com.jwkj.activity.ShakeActivity;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.widget.MyImageView;
import com.p2p.core.P2PInterface.IP2P;
import com.p2p.core.P2PInterface.ISetting;
import com.p2p.core.P2PValue.HikamDeviceModel;
import com.p2p.core.global.Config.AppConfig;
import com.p2p.core.global.Constants.MsgSection;
import com.p2p.core.network.NetManager;
import com.p2p.core.utils.DES;
import com.p2p.core.utils.MyUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.http.HttpStatus;

public class P2PHandler {
    private static int MESG_GET_HUMAN_DETECT = MsgSection.MESG_GET_HUMAN_DETECT;
    private static int MESG_GET_LAMP_SWITCH = MsgSection.MESG_GET_LAMP_SWITCH;
    private static int MESG_GET_SD_CARD_CAPACITY = MsgSection.MESG_GET_SD_CARD_CAPACITY;
    private static int MESG_ID_STTING_IR_ALARM_EN = MsgSection.MESG_ID_STTING_IR_ALARM_EN;
    private static int MESG_ID_STTING_PIC_REVERSE = MsgSection.MESG_ID_STTING_PIC_REVERSE;
    private static int MESG_SD_CARD_FORMAT = MsgSection.MESG_SD_CARD_FORMAT;
    private static int MESG_SET_GPI1_0 = MsgSection.MESG_SET_GPI1_0;
    private static int MESG_SET_GPIO = MsgSection.MESG_SET_GPIO;
    private static int MESG_SET_HUMAN_DETECT = MsgSection.MESG_SET_HUMAN_DETECT;
    private static int MESG_SET_LAMP_SWITCH = MsgSection.MESG_SET_LAMP_SWITCH;
    private static int MESG_SET_UPLOAD_TO_SER = MsgSection.MESG_SET_UPLOAD_TO_SER;
    private static int MESG_STTING_ID_EXTLINE_ALARM_IN_EN = MsgSection.MESG_STTING_ID_EXTLINE_ALARM_IN_EN;
    private static int MESG_STTING_ID_EXTLINE_ALARM_OUT_EN = MsgSection.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN;
    private static int MESG_STTING_ID_GUEST_PASSWD = MsgSection.MESG_STTING_ID_GUEST_PASSWD;
    private static int MESG_STTING_ID_SECUPGDEV = MsgSection.MESG_STTING_ID_SECUPGDEV;
    private static int MESG_STTING_ID_TIMEZONE = MsgSection.MESG_STTING_ID_TIMEZONE;
    private static int MSG_ID_CANCEL_DEVICE_UPDATE = MsgSection.MSG_ID_CANCEL_DEVICE_UPDATE;
    private static int MSG_ID_CHECK_DEVICE_PASSWORD = MsgSection.MSG_ID_CHECK_DEVICE_PASSWORD;
    private static int MSG_ID_CHECK_DEVICE_UPDATE = MsgSection.MSG_ID_CHECK_DEVICE_UPDATE;
    private static int MSG_ID_CLEAR_DEFENCE_GROUP = MsgSection.MSG_ID_CLEAR_DEFENCE_GROUP;
    private static int MSG_ID_DO_DEVICE_UPDATE = MsgSection.MSG_ID_DO_DEVICE_UPDATE;
    private static int MSG_ID_GETTING_ALARM_BIND_ID = MsgSection.MSG_ID_GETTING_ALARM_BIND_ID;
    private static int MSG_ID_GETTING_ALARM_EMAIL = MsgSection.MSG_ID_GETTING_ALARM_EMAIL;
    private static int MSG_ID_GETTING_DEFENCEAREA = MsgSection.MSG_ID_GETTING_DEFENCEAREA;
    private static int MSG_ID_GETTING_DEVICE_TIME = MsgSection.MSG_ID_GETTING_DEVICE_TIME;
    private static int MSG_ID_GETTING_NPC_SETTINGS = MsgSection.MSG_ID_GETTING_NPC_SETTINGS;
    private static int MSG_ID_GETTING_RECORD_FILE_LIST = MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST;
    private static int MSG_ID_GETTING_WIFI_LIST = MsgSection.MSG_ID_GETTING_WIFI_LIST;
    private static int MSG_ID_GET_DEFENCE_STATE = MsgSection.MSG_ID_GET_DEFENCE_STATE;
    private static int MSG_ID_GET_DEVICE_VERSION = MsgSection.MSG_ID_GET_DEVICE_VERSION;
    private static int MSG_ID_SEND_CUSTOM_CMD = MsgSection.MSG_ID_SEND_CUSTOM_CMD;
    private static int MSG_ID_SEND_MESSAGE = MsgSection.MSG_ID_SEND_MESSAGE;
    private static int MSG_ID_SETTING_ALARM_BIND_ID = MsgSection.MSG_ID_SETTING_ALARM_BIND_ID;
    private static int MSG_ID_SETTING_ALARM_EMAIL = MsgSection.MSG_ID_SETTING_ALARM_EMAIL;
    private static int MSG_ID_SETTING_DEFENCEAREA = MsgSection.MSG_ID_SETTING_DEFENCEAREA;
    private static int MSG_ID_SETTING_DEVICE_PASSWORD = MsgSection.MSG_ID_SETTING_DEVICE_PASSWORD;
    private static int MSG_ID_SETTING_DEVICE_TIME = MsgSection.MSG_ID_SETTING_DEVICE_TIME;
    private static int MSG_ID_SETTING_INIT_PASSWORD = MsgSection.MSG_ID_SETTING_INIT_PASSWORD;
    private static int MSG_ID_SETTING_NPC_SETTINGS_BUZZER = MsgSection.MSG_ID_SETTING_NPC_SETTINGS_BUZZER;
    private static int MSG_ID_SETTING_NPC_SETTINGS_MOTION = MsgSection.MSG_ID_SETTING_NPC_SETTINGS_MOTION;
    private static int MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE = MsgSection.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE;
    private static int MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME = MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME;
    private static int MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME = MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME;
    private static int MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE = MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE;
    private static int MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT = MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT;
    private static int MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME = MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME;
    private static int MSG_ID_SETTING_WIFI = MsgSection.MSG_ID_SETTING_WIFI;
    private static int MSG_ID_SET_REMOTE_DEFENCE = MsgSection.MSG_ID_SET_REMOTE_DEFENCE;
    private static int MSG_ID_SET_REMOTE_RECORD = MsgSection.MSG_ID_SET_REMOTE_RECORD;
    private static P2PHandler manager = null;
    private static int[] regionCode = new int[]{1264, 1268, 1242, 1246, 1441, 1284, 1345, 1767, 1809, 1473, 1876, 1664, 1787, 1869, 1758, 1784, 1868, 1649, 1340, 1671, 1670, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 256, 257, BZip2Constants.MAX_ALPHA_SIZE, 259, 260, 261, 262, TarConstants.VERSION_OFFSET, 264, 265, 266, 267, 268, 269, 290, ShakeActivity.SHAKING_END, 292, 293, 294, 295, 296, 297, 298, 299, 350, 351, 352, 353, 354, 355, 356, 357, 358, 359, 370, 371, 372, 373, 374, 375, 376, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 387, 388, 389, 420, 421, HttpStatus.SC_UNPROCESSABLE_ENTITY, HttpStatus.SC_LOCKED, HttpStatus.SC_FAILED_DEPENDENCY, 425, 426, 427, 428, 429, 500, HttpStatus.SC_NOT_IMPLEMENTED, HttpStatus.SC_BAD_GATEWAY, HttpStatus.SC_SERVICE_UNAVAILABLE, 504, HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED, 506, HttpStatus.SC_INSUFFICIENT_STORAGE, TarConstants.XSTAR_MAGIC_OFFSET, 509, 590, 591, 592, 593, 594, 595, 596, 597, 598, 599, 670, 671, 672, 673, 674, 675, 676, 677, 678, 679, 680, 681, 682, 683, 684, 685, 686, 687, 688, 689, 690, 691, 692, 693, 694, 695, 696, 697, 698, 699, InfraredRemoteActivity.SEND_TIME_INTERVAL, 801, 802, 803, 804, 805, 806, 807, 808, 809, 850, 851, 852, 853, 854, 855, 856, 857, 858, 859, 870, 871, 872, 873, 874, 875, 876, 877, 878, 879, 880, 881, 882, 883, 884, 885, 886, 887, 888, 889, 960, 961, 962, 963, 964, 965, 966, 967, 968, 969, 970, 971, 972, 973, 974, 975, 976, 977, 978, 979, 990, 991, 992, 993, 994, 995, 996, NetManager.JSON_PARSE_ERROR, NetManager.CONNECT_CHANGE, 999, 20, 27, 28, 30, 31, 32, 33, 34, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 51, 52, 53, 54, 55, 56, 57, 58, 60, 61, 62, 63, 64, 65, 66, 81, 82, 83, 84, 86, 90, 91, 92, 93, 94, 95, 98, 1, 7};
    String TAG = "SDK";

    private P2PHandler() {
    }

    public static synchronized P2PHandler getInstance() {
        P2PHandler p2PHandler;
        synchronized (P2PHandler.class) {
            if (manager == null) {
                synchronized (P2PHandler.class) {
                    manager = new P2PHandler();
                }
            }
            p2PHandler = manager;
        }
        return p2PHandler;
    }

    public void p2pInit(Context context, IP2P p2pListener, ISetting settingListener) {
        MediaPlayer mediaPlayer = new MediaPlayer(context);
        MediaPlayer.getInstance().setP2PInterface(p2pListener);
        MediaPlayer.getInstance().setSettingInterface(settingListener);
    }

    public boolean p2pConnect(String activeUser, int codeStr1, int codeStr2) {
        int connect;
        String cHostName = "|cloudlinks.cn|2cu.co|gwelltimes.com|srv0001.hi-kam.com";
        int[] iCustomerID = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        if (activeUser.equals("517400")) {
            connect = MediaPlayer.getInstance().native_p2p_connect(Integer.parseInt(activeUser), 886976412, codeStr1, codeStr2, "|cloudlinks.cn|2cu.co|gwelltimes.com|srv0001.hi-kam.com".getBytes(), iCustomerID);
        } else {
            connect = MediaPlayer.getInstance().native_p2p_connect(Integer.parseInt(activeUser) | Integer.MIN_VALUE, 886976412, codeStr1, codeStr2, "|cloudlinks.cn|2cu.co|gwelltimes.com|srv0001.hi-kam.com".getBytes(), iCustomerID);
        }
        if (connect == 1) {
            return true;
        }
        return false;
    }

    public void p2pDisconnect() {
        MediaPlayer.getInstance().native_p2p_disconnect();
        Log.i(this.TAG, "P2pJni.P2pClientSdkUnRegister start");
        P2pJni.P2pClientSdkUnRegister();
        Log.i(this.TAG, "P2pJni.P2pClientSdkUnRegister finish");
    }

    public void getWifiList(String contactModel, String contactId, String password) {
        Log.e(this.TAG, "P2PHANDLER:getWifiList");
        if (MSG_ID_GETTING_WIFI_LIST >= MsgSection.MSG_ID_GETTING_WIFI_LIST) {
            MSG_ID_GETTING_WIFI_LIST = MsgSection.MSG_ID_GETTING_WIFI_LIST + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkGetDevWifiList(contactId, password, MSG_ID_GETTING_WIFI_LIST);
        } else {
            MediaPlayer.iGetNPCWifiList(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_GETTING_WIFI_LIST);
        }
        MSG_ID_GETTING_WIFI_LIST++;
    }

    public void setWifi(String contactModel, String contactId, String password, int type, String name, String wifiPassword) {
        Log.e(this.TAG, "P2PHANDLER:setWifi");
        String s = null;
        try {
            for (byte b : name.getBytes("UTF-8")) {
                s = s + "  " + b;
            }
            Log.e("setwifiname", "--" + s);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (MSG_ID_SETTING_WIFI >= MsgSection.MSG_ID_SETTING_WIFI) {
            MSG_ID_SETTING_WIFI = MsgSection.MSG_ID_SETTING_WIFI + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetDevWifi(contactId, password, name, type, wifiPassword, MSG_ID_SETTING_WIFI);
        } else {
            MediaPlayer.iSetNPCWifi(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_SETTING_WIFI, type, name.getBytes(), name.length(), wifiPassword.getBytes(), wifiPassword.length());
        }
        MSG_ID_SETTING_WIFI++;
    }

    public void getNpcSettings(String contactModel, String contactId, String password) {
        int iPassword;
        Log.e(this.TAG, "P2PHANDLER:getNpcSettings");
        try {
            iPassword = Integer.parseInt(password);
        } catch (Exception e) {
            iPassword = Integer.MAX_VALUE;
        }
        if (MSG_ID_GETTING_NPC_SETTINGS >= MsgSection.MSG_ID_GETTING_NPC_SETTINGS) {
            MSG_ID_GETTING_NPC_SETTINGS = MsgSection.MSG_ID_GETTING_NPC_SETTINGS + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkGetDevSetting(contactId, password, MSG_ID_GETTING_NPC_SETTINGS);
        } else {
            MediaPlayer.iGetNPCSettings(Integer.parseInt(contactId), iPassword, MSG_ID_GETTING_NPC_SETTINGS);
        }
        MSG_ID_GETTING_NPC_SETTINGS++;
    }

    public void getUserNum(String contactModel, String contactId, String password) {
        Log.e(this.TAG, "P2PHANDLER:getUserNum");
        try {
            int iPassword = Integer.parseInt(password);
        } catch (Exception e) {
        }
        if (MSG_ID_GETTING_NPC_SETTINGS >= MsgSection.MSG_ID_GETTING_NPC_SETTINGS) {
            MSG_ID_GETTING_NPC_SETTINGS = MsgSection.MSG_ID_GETTING_NPC_SETTINGS + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkGetUserNum(contactId, password, MSG_ID_GETTING_NPC_SETTINGS);
        }
        MSG_ID_GETTING_NPC_SETTINGS++;
    }

    public void getDefenceStates(String contactModel, String contactId, String password) {
        int iPassword;
        Log.e(this.TAG, "P2PHANDLER:getDefenceStates");
        try {
            iPassword = Integer.parseInt(password);
        } catch (Exception e) {
            iPassword = Integer.MAX_VALUE;
        }
        if (MSG_ID_GET_DEFENCE_STATE >= MsgSection.MSG_ID_GET_DEFENCE_STATE) {
            MSG_ID_GET_DEFENCE_STATE = MsgSection.MSG_ID_GET_DEFENCE_STATE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel) || HikamDeviceModel.Q5.equals(contactModel)) {
            P2pJni.P2PClientSdkGetAlarmTotalSwitch(contactId, password, MSG_ID_GET_DEFENCE_STATE);
        } else {
            MediaPlayer.iGetNPCSettings(Integer.parseInt(contactId), iPassword, MSG_ID_GET_DEFENCE_STATE);
        }
        MSG_ID_GET_DEFENCE_STATE++;
    }

    public void checkPassword(String contactModel, String contactId, String password) {
        Log.e(this.TAG, "P2PHANDLER:checkPassword");
        if (MSG_ID_CHECK_DEVICE_PASSWORD >= MsgSection.MSG_ID_CHECK_DEVICE_PASSWORD) {
            MSG_ID_CHECK_DEVICE_PASSWORD = MsgSection.MSG_ID_CHECK_DEVICE_PASSWORD + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            final String dev_id = contactId;
            final String dev_pwd = password;
            final int msg_id = MSG_ID_CHECK_DEVICE_PASSWORD;
            new Thread() {
                public void run() {
                    P2pJni.P2PClientSdkCheckDevicePassword(dev_id, dev_pwd, msg_id);
                }
            }.start();
        } else if (MyUtils.isNumeric(contactId) && MyUtils.isNumeric(password)) {
            MediaPlayer.iGetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_CHECK_DEVICE_PASSWORD);
        }
        MSG_ID_CHECK_DEVICE_PASSWORD++;
    }

    public void getDefenceArea(String contactModel, String contactId, String password) {
        Log.e(this.TAG, "P2PHANDLER:getDefenceArea");
        if (MSG_ID_GETTING_DEFENCEAREA >= MsgSection.MSG_ID_GETTING_DEFENCEAREA) {
            MSG_ID_GETTING_DEFENCEAREA = MsgSection.MSG_ID_GETTING_DEFENCEAREA + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkGetAlarmCodeStatus(contactId, password, MSG_ID_GETTING_DEFENCEAREA);
        } else {
            MediaPlayer.iGetAlarmCodeStatus(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_GETTING_DEFENCEAREA);
        }
        MSG_ID_GETTING_DEFENCEAREA++;
    }

    public void setRemoteDefence(String contactModel, String contactId, String password, int value) {
        Log.e(this.TAG, "P2PHANDLER:setRemoteDefence");
        if (MSG_ID_SET_REMOTE_DEFENCE >= MsgSection.MSG_ID_SET_REMOTE_DEFENCE) {
            MSG_ID_SET_REMOTE_DEFENCE = MsgSection.MSG_ID_SET_REMOTE_DEFENCE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetAlarmTotalSwitch(contactId, password, value, MSG_ID_SET_REMOTE_DEFENCE);
        } else {
            MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_SET_REMOTE_DEFENCE, 0, value);
        }
        MSG_ID_SET_REMOTE_DEFENCE++;
    }

    public void setRemoteRecord(String contactModel, String contactId, String password, int value) {
        Log.e(this.TAG, "P2PHANDLER:setRemoteRecord");
        if (MSG_ID_SET_REMOTE_RECORD >= MsgSection.MSG_ID_SET_REMOTE_RECORD) {
            MSG_ID_SET_REMOTE_RECORD = MsgSection.MSG_ID_SET_REMOTE_RECORD + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetRecordConfig(contactId, password, 0, 0, value, 0, 0, "", "", MSG_ID_SET_REMOTE_RECORD);
        } else {
            MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_SET_REMOTE_RECORD, 4, value);
        }
        MSG_ID_SET_REMOTE_RECORD++;
    }

    public void setDeviceTime(String contactModel, String contactId, String password, String time) {
        Log.e(this.TAG, "P2PHANDLER:setDeviceTime");
        if (MSG_ID_SETTING_DEVICE_TIME >= MsgSection.MSG_ID_SETTING_DEVICE_TIME) {
            MSG_ID_SETTING_DEVICE_TIME = MsgSection.MSG_ID_SETTING_DEVICE_TIME + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int parseTime = 0;
        if (date != null) {
            if (time.substring(11, 13).equals("12")) {
                parseTime = (((((calendar.get(1) - 2000) << 24) | ((calendar.get(2) + 1) << 18)) | (calendar.get(5) << 12)) | 768) | (calendar.get(12) << 0);
            } else {
                parseTime = (((((calendar.get(1) - 2000) << 24) | ((calendar.get(2) + 1) << 18)) | (calendar.get(5) << 12)) | (calendar.get(11) << 6)) | (calendar.get(12) << 0);
            }
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetDeviceTime(contactId, password, calendar.get(1), calendar.get(2) + 1, calendar.get(5), calendar.get(11), calendar.get(12), calendar.get(13), MSG_ID_SETTING_DEVICE_TIME);
        } else {
            MediaPlayer.iSetNPCDateTime(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_SETTING_DEVICE_TIME, parseTime);
        }
        MSG_ID_SETTING_DEVICE_TIME++;
    }

    public void getDeviceTime(String contactModel, String contactId, String password) {
        Log.e(this.TAG, "P2PHANDLER:getDeviceTime");
        if (MSG_ID_GETTING_DEVICE_TIME >= MsgSection.MSG_ID_GETTING_DEVICE_TIME) {
            MSG_ID_GETTING_DEVICE_TIME = MsgSection.MSG_ID_GETTING_DEVICE_TIME + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkGetDeviceTime(contactId, password, MSG_ID_GETTING_DEVICE_TIME);
        } else {
            MediaPlayer.iGetNPCDateTime(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_GETTING_DEVICE_TIME);
        }
        MSG_ID_GETTING_DEVICE_TIME++;
    }

    public void setVideoVolume(String contactId, String password, int value) {
        Log.e(this.TAG, "P2PHANDLER:setVideoVolume");
        if (MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME >= MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME) {
            MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME = MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME, 14, value);
        MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME++;
    }

    public void setVideoFormat(String contactModel, String contactId, String password, int value) {
        Log.e(this.TAG, "P2PHANDLER:setVideoFormat");
        if (MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT >= MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT) {
            MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT = MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetVideoFormat(contactId, password, value, MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT);
        } else {
            MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT, 8, value);
        }
        MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT++;
    }

    public void setRecordResolution(String contactModel, String contactId, String password, int resolution) {
        Log.e(this.TAG, "P2PHANDLER:setRecordResolution");
        if (MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE >= MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE) {
            MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE = MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetRecordConfig(contactId, password, 4, resolution, 0, 0, 0, "", "", MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE);
        }
        MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE++;
    }

    public void setRecordType(String contactModel, String contactId, String password, int type) {
        Log.e(this.TAG, "P2PHANDLER:setRecordType");
        if (MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE >= MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE) {
            MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE = MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetRecordConfig(contactId, password, 1, 0, 0, type, 0, "", "", MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE);
        } else {
            MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE, 3, type);
        }
        MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE++;
    }

    public void setRecordTime(String contactModel, String contactId, String password, int time) {
        Log.e(this.TAG, "P2PHANDLER:setRecordTime");
        if (MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME >= MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME) {
            MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME = MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetRecordConfig(contactId, password, 2, 0, 0, 0, time, "", "", MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME);
        } else {
            MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME, 11, time);
        }
        MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME++;
    }

    public void setRecordPlanTime(String contactModel, String contactId, String password, String time) {
        Log.e(this.TAG, "P2PHANDLER:setRecordPlanTime");
        if (MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME >= MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME) {
            MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME = MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            String[] times = time.split("-");
            P2pJni.P2PClientSdkSetRecordConfig(contactId, password, 3, 0, 0, 0, 0, times[0], times[1], MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME);
        } else {
            MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME, 5, MyUtils.convertPlanTime(time));
        }
        MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME++;
    }

    public void setDefenceAreaState(String contactModel, String contactId, String password, int group, int item, int type) {
        Log.e(this.TAG, "P2PHANDLER:setDefenceAreaState");
        if (MSG_ID_SETTING_DEFENCEAREA >= MsgSection.MSG_ID_SETTING_DEFENCEAREA) {
            MSG_ID_SETTING_DEFENCEAREA = MsgSection.MSG_ID_SETTING_DEFENCEAREA + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetAlarmCodeStatus(contactId, password, type, group, item, MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE);
        } else {
            MediaPlayer.iSetAlarmCodeStatus(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_SETTING_DEFENCEAREA, 1, type, new int[]{group}, new int[]{item});
        }
        MSG_ID_SETTING_DEFENCEAREA++;
    }

    public void clearDefenceAreaState(String contactModel, String contactId, String password, int group) {
        Log.e(this.TAG, "P2PHANDLER:setDefenceAreaState");
        if (MSG_ID_CLEAR_DEFENCE_GROUP >= MsgSection.MSG_ID_CLEAR_DEFENCE_GROUP) {
            MSG_ID_CLEAR_DEFENCE_GROUP = MsgSection.MSG_ID_CLEAR_DEFENCE_GROUP + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetAlarmCodeStatus(contactId, password, 2, group, 0, MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE);
        } else {
            MediaPlayer.iClearAlarmCodeGroup(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_CLEAR_DEFENCE_GROUP, group);
        }
        MSG_ID_CLEAR_DEFENCE_GROUP++;
    }

    public void setNetType(String contactModel, String contactId, String password, int type) {
        Log.e(this.TAG, "P2PHANDLER:setNetType");
        if (MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE >= MsgSection.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE) {
            MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE = MsgSection.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetDevNetworkType(contactId, password, type, MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE);
        } else {
            MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE, 13, type);
        }
        MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE++;
    }

    public void setBindAlarmId(String contactModel, String contactId, String password, int count, String[] datas, String account) {
        Log.e(this.TAG, "P2PHANDLER:setBindAlarmId");
        if (MSG_ID_SETTING_ALARM_BIND_ID >= MsgSection.MSG_ID_SETTING_ALARM_BIND_ID) {
            MSG_ID_SETTING_ALARM_BIND_ID = MsgSection.MSG_ID_SETTING_ALARM_BIND_ID + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            if ("0".equals(datas[0])) {
                datas[0] = "";
            }
            boolean isPush = false;
            for (String s : datas) {
                if (account.equals(s)) {
                    isPush = true;
                }
            }
            if (isPush) {
                P2pJni.P2PClientSdkSetAlarmPushStatus(contactId, password, account, 1, MSG_ID_SETTING_ALARM_BIND_ID);
            } else {
                P2pJni.P2PClientSdkSetAlarmPushStatus(contactId, password, account, 0, MSG_ID_SETTING_ALARM_BIND_ID);
            }
        } else {
            int[] iData = new int[datas.length];
            int i = 0;
            while (i < datas.length) {
                try {
                    iData[i] = Integer.parseInt(datas[i]);
                    i++;
                } catch (Exception e) {
                    iData = new int[]{0};
                    count = 1;
                }
            }
            MediaPlayer.iSetBindAlarmId(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_SETTING_ALARM_BIND_ID, count, iData);
        }
        MSG_ID_SETTING_ALARM_BIND_ID++;
    }

    public void getBindAlarmId(String id, String contactModel, String contactId, String password, String newuser, String olduser) {
        Log.e(this.TAG, "P2PHANDLER:getBindAlarmId");
        if (MSG_ID_GETTING_ALARM_BIND_ID >= MsgSection.MSG_ID_GETTING_ALARM_BIND_ID) {
            MSG_ID_GETTING_ALARM_BIND_ID = MsgSection.MSG_ID_GETTING_ALARM_BIND_ID + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkGetAllPushAccount(contactId, password, newuser, olduser, MSG_ID_GETTING_ALARM_BIND_ID);
            P2pJni.P2PClientSdkGetAlarmPushStatus(contactId, password, id, MSG_ID_GETTING_ALARM_BIND_ID);
        } else {
            MediaPlayer.iGetBindAlarmId(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_GETTING_ALARM_BIND_ID);
        }
        MSG_ID_GETTING_ALARM_BIND_ID++;
    }

    public void setAlarmEmail(String contactModel, String contactId, String password, String email) {
        Log.e(this.TAG, "P2PHANDLER:setAlarmEmail");
        if (MSG_ID_SETTING_ALARM_EMAIL >= MsgSection.MSG_ID_SETTING_ALARM_EMAIL) {
            MSG_ID_SETTING_ALARM_EMAIL = MsgSection.MSG_ID_SETTING_ALARM_EMAIL + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            if ("0".equals(email)) {
                email = "";
            }
            P2pJni.P2PClientSdkSetAlarmEmail(contactId, password, "", "", email, MSG_ID_SETTING_ALARM_EMAIL);
        } else {
            MediaPlayer.iSetNPCEmail(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_SETTING_ALARM_EMAIL, email.getBytes(), email.length());
        }
        MSG_ID_SETTING_ALARM_EMAIL++;
    }

    public void getAlarmEmail(String contactModel, String contactId, String password) {
        Log.e(this.TAG, "P2PHANDLER:getAlarmEmail");
        if (MSG_ID_GETTING_ALARM_EMAIL >= MsgSection.MSG_ID_GETTING_ALARM_EMAIL) {
            MSG_ID_GETTING_ALARM_EMAIL = MsgSection.MSG_ID_GETTING_ALARM_EMAIL + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkGetAlarmEmail(contactId, password, MSG_ID_GETTING_ALARM_EMAIL);
        } else {
            MediaPlayer.iGetNPCEmail(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_GETTING_ALARM_EMAIL);
        }
        MSG_ID_GETTING_ALARM_EMAIL++;
    }

    public void setAlarmEmailWithSMTP(String contactModel, String contactName, String bakServer, String contactId, String password, byte boption, String emailaddress, int port, String server, String user, String pwd, String subject, String content, byte Entry, byte reserve1, int reserve2) {
        byte[] ppp;
        Log.e(this.TAG, "P2PHANDLER:setAlarmEmailWithSMTP");
        if (MSG_ID_SETTING_ALARM_EMAIL >= MsgSection.MSG_ID_SETTING_ALARM_EMAIL) {
            MSG_ID_SETTING_ALARM_EMAIL = MsgSection.MSG_ID_SETTING_ALARM_EMAIL + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        String pwds = pwd + "##";
        int k = 8 - (pwds.length() % 8);
        for (int i = 0; i < k; i++) {
            pwds = pwds + "0";
        }
        try {
            ppp = DES.des(pwds.getBytes(), 0);
        } catch (Exception e) {
            ppp = new byte[]{(byte) 0};
            e.printStackTrace();
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetAlarmEmail(contactId, password, contactName, bakServer, emailaddress, MSG_ID_SETTING_ALARM_EMAIL);
        } else {
            MediaPlayer.SetRobortEmailNew(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_SETTING_ALARM_EMAIL, boption, emailaddress, port, server, user, ppp, subject, content, Entry, reserve1, reserve2, ppp.length);
        }
        MSG_ID_SETTING_ALARM_EMAIL++;
    }

    public void setBuzzer(String contactModel, String contactId, String password, int value) {
        Log.e(this.TAG, "P2PHANDLER:setBuzzer");
        if (MSG_ID_SETTING_NPC_SETTINGS_BUZZER >= MsgSection.MSG_ID_SETTING_NPC_SETTINGS_BUZZER) {
            MSG_ID_SETTING_NPC_SETTINGS_BUZZER = MsgSection.MSG_ID_SETTING_NPC_SETTINGS_BUZZER + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            int state = 0;
            int time = 60;
            if (value == 0) {
                state = 0;
            } else if (value == 1) {
                state = 1;
                time = 60;
            } else if (value == 2) {
                state = 1;
                time = MyImageView.IMAGE_WIDTH;
            } else if (value == 3) {
                state = 1;
                time = 180;
            }
            P2pJni.P2PClientSdkSetSiren(contactId, password, state, time, MSG_ID_SETTING_DEVICE_PASSWORD);
        } else {
            MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_SETTING_NPC_SETTINGS_BUZZER, 1, value);
        }
        MSG_ID_SETTING_NPC_SETTINGS_BUZZER++;
    }

    public void setMotion(String contactModel, String contactId, String password, int value, int sensitivity) {
        Log.e(this.TAG, "P2PHANDLER:setMotion");
        if (MSG_ID_SETTING_NPC_SETTINGS_MOTION >= MsgSection.MSG_ID_SETTING_NPC_SETTINGS_MOTION) {
            MSG_ID_SETTING_NPC_SETTINGS_MOTION = MsgSection.MSG_ID_SETTING_NPC_SETTINGS_MOTION + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetMotionDetect(contactId, password, value, sensitivity, MSG_ID_SETTING_NPC_SETTINGS_MOTION);
        } else {
            MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_SETTING_NPC_SETTINGS_MOTION, 2, value);
        }
        MSG_ID_SETTING_NPC_SETTINGS_MOTION++;
    }

    public void setInitPassword(String contactId, String password) {
        Log.e(this.TAG, "P2PHANDLER:setInitPassword");
        if (MSG_ID_SETTING_INIT_PASSWORD >= MsgSection.MSG_ID_SETTING_INIT_PASSWORD) {
            MSG_ID_SETTING_INIT_PASSWORD = MsgSection.MSG_ID_SETTING_INIT_PASSWORD + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        int pwdLen = password.length();
        byte[] EntryPwd = getPwdBytes(password);
        String result = MediaPlayer.RTSPEntry("admin:HIipCamera:" + password);
        MediaPlayer.iSetInitPassword(Integer.parseInt(contactId), 0, MSG_ID_SETTING_INIT_PASSWORD, Integer.parseInt(password), "errror".equals(result) ? new byte[32] : result.getBytes(), Integer.parseInt(contactId), pwdLen, EntryPwd);
        MSG_ID_SETTING_INIT_PASSWORD++;
    }

    public int setDevicePassword(String contactModel, String contactId, String oldPassword, String newPassword) {
        int ret;
        Log.e(this.TAG, "P2PHANDLER:setDevicePassword");
        if (MSG_ID_SETTING_DEVICE_PASSWORD >= MsgSection.MSG_ID_SETTING_DEVICE_PASSWORD) {
            MSG_ID_SETTING_DEVICE_PASSWORD = MsgSection.MSG_ID_SETTING_DEVICE_PASSWORD + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            ret = P2pJni.P2PClientSdkSetDevicePassword(contactId, oldPassword, newPassword, MSG_ID_SETTING_DEVICE_PASSWORD);
        } else {
            MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(oldPassword), MSG_ID_SETTING_DEVICE_PASSWORD, 9, Integer.parseInt(newPassword));
            ret = 0;
        }
        MSG_ID_SETTING_DEVICE_PASSWORD++;
        return ret;
    }

    public void setDeviceVisitorPassword(String contactId, String oldPassword, String visitorPassword) {
        Log.e(this.TAG, "P2PHANDLER:setDevicePassword");
        if (MESG_STTING_ID_GUEST_PASSWD >= MsgSection.MESG_STTING_ID_GUEST_PASSWD) {
            MESG_STTING_ID_GUEST_PASSWD = MsgSection.MESG_STTING_ID_GUEST_PASSWD + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(oldPassword), MESG_STTING_ID_GUEST_PASSWD, 21, Integer.parseInt(visitorPassword));
        MESG_STTING_ID_GUEST_PASSWD++;
    }

    public void getFriendStatus(String[] contactModels, String[] contactIds, String[] password, String treeNum, String treeNum2) {
        int i;
        Log.e(this.TAG, "P2PHANDLER:getFriendStatus");
        int hikam_camera_count = 0;
        int no_hikam_camera_count = 0;
        for (i = 0; i < contactIds.length; i++) {
            if (P2PValue.HikamDeviceModelList.contains(contactModels[i])) {
                hikam_camera_count++;
            } else {
                no_hikam_camera_count++;
            }
        }
        int[] friends = new int[no_hikam_camera_count];
        final String[] hikam_camera = new String[hikam_camera_count];
        final String[] hikam_camera_mod = new String[hikam_camera_count];
        final String[] hikam_camera_pwd = new String[hikam_camera_count];
        int j = 0;
        int k = 0;
        for (i = 0; i < contactIds.length; i++) {
            if (P2PValue.HikamDeviceModelList.contains(contactModels[i])) {
                hikam_camera[j] = contactIds[i];
                hikam_camera_mod[j] = contactModels[i];
                int j2 = j + 1;
                hikam_camera_pwd[j] = password[i];
                j = j2;
            } else if (contactIds[i].substring(0, 1).equals("0")) {
                k = k + 1;
                friends[k] = Integer.parseInt(contactIds[i]) | Integer.MIN_VALUE;
                k = k;
            } else {
                k = k + 1;
                friends[k] = Integer.parseInt(contactIds[i]);
                k = k;
            }
        }
        MediaPlayer.iGetFriendsStatus(friends, friends.length);
        final int loop_count = hikam_camera_count;
        final String str = treeNum;
        final String str2 = treeNum2;
        new Thread() {
            public void run() {
                int[] types = new int[loop_count];
                int[] status = new int[loop_count];
                for (int i = 0; i < loop_count; i++) {
                    types[i] = 7;
                    status[i] = 0;
                    if (P2pJni.P2pClientSdkGetSessionStatus(hikam_camera[i]) == 3) {
                        status[i] = 1;
                    } else if (P2pJni.P2pClientSdkConnectPeer(hikam_camera[i]) == 0) {
                        status[i] = 1;
                        P2PHandler.this.getBindAlarmId(str, hikam_camera_mod[i], hikam_camera[i], hikam_camera_pwd[i], str, str2);
                    }
                }
                MediaPlayer.settingInterface.vRetGetFriendStatus(loop_count, hikam_camera, status, types);
            }
        }.start();
    }

    public void disconnectFriend(String[] contactModels, String[] contactIds) {
        Log.e(this.TAG, "P2PHANDLER:disconnectFriend");
        for (int i = 0; i < contactIds.length; i++) {
            if (P2PValue.HikamDeviceModelList.contains(contactModels[i])) {
                P2pJni.P2pClientSdkClosePeer(contactIds[i]);
            }
        }
    }

    public void getRecordFiles(String contactId, String password, int timeInterval) {
        int i_start;
        Log.e(this.TAG, "P2PHANDLER:getRecordFiles");
        if (MSG_ID_GETTING_RECORD_FILE_LIST >= MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST) {
            MSG_ID_GETTING_RECORD_FILE_LIST = MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (now.getDate() < timeInterval) {
            i_start = (((((now.getYear() - 100) << 24) | (now.getMonth() << 18)) | ((timeInterval - now.getDate()) << 12)) | (now.getHours() << 6)) | (now.getMinutes() << 0);
        } else {
            i_start = (((((now.getYear() - 100) << 24) | ((now.getMonth() + 1) << 18)) | ((now.getDate() - timeInterval) << 12)) | (now.getHours() << 6)) | (now.getMinutes() << 0);
        }
        int i_end = (((((now.getYear() - 100) << 24) | ((now.getMonth() + 1) << 18)) | (now.getDate() << 12)) | (now.getHours() << 6)) | (now.getMinutes() << 0);
        Log.e("timestamp", "year" + now.getYear() + "month" + now.getMonth() + "hour" + now.getHours());
        Log.e("timestamp", "i_start=" + i_start + "i_end=" + i_end);
        MediaPlayer.iGetRecFiles(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_GETTING_RECORD_FILE_LIST, i_start, i_end);
        MSG_ID_GETTING_RECORD_FILE_LIST++;
    }

    public void getRecordFiles(String contactModel, String contactId, String password, Date start, Date end) {
        Log.e(this.TAG, "P2PHANDLER:getRecordFiles");
        if (MSG_ID_GETTING_RECORD_FILE_LIST >= MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST) {
            MSG_ID_GETTING_RECORD_FILE_LIST = MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        int i_start = (((((start.getYear() - 100) << 24) | ((start.getMonth() + 1) << 18)) | (start.getDate() << 12)) | (start.getHours() << 6)) | (start.getMinutes() << 0);
        int i_end = (((((end.getYear() - 100) << 24) | ((end.getMonth() + 1) << 18)) | (end.getDate() << 12)) | (end.getHours() << 6)) | (end.getMinutes() << 0);
        Log.e("timestamp", "i_start=" + i_start + "i_end=" + i_end);
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String startDate = sdf.format(start);
            String endDate = sdf.format(end);
            Log.e(this.TAG, "search form :[" + startDate + "] to [" + endDate + "]");
            P2pJni.P2PClientSdkSearchRecordFile(contactId, password, startDate, endDate, 2, MSG_ID_GETTING_RECORD_FILE_LIST);
        } else {
            MediaPlayer.iGetRecFiles(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_GETTING_RECORD_FILE_LIST, i_start, i_end);
        }
        MSG_ID_GETTING_RECORD_FILE_LIST++;
    }

    public void initRecordFiles() {
        P2pJni.P2PClientSdkClearRecordBackup();
    }

    public String sendMessage(String contactId, String msg) {
        Log.e(this.TAG, "P2PHANDLER:sendMessage");
        if (MSG_ID_SEND_MESSAGE >= MsgSection.MSG_ID_SEND_MESSAGE) {
            MSG_ID_SEND_MESSAGE = MsgSection.MSG_ID_SEND_MESSAGE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        MediaPlayer.iSendMesgToFriend(Integer.parseInt(contactId) | Integer.MIN_VALUE, MSG_ID_SEND_MESSAGE, msg.getBytes(), msg.getBytes().length);
        MSG_ID_SEND_MESSAGE++;
        return String.valueOf(MSG_ID_SEND_MESSAGE - 1);
    }

    public String sendCustomCmd(String contactId, String password, String msg) {
        Log.e(this.TAG, "P2PHANDLER:sendCustomCmd");
        if (MSG_ID_SEND_CUSTOM_CMD >= MsgSection.MSG_ID_SEND_CUSTOM_CMD) {
            MSG_ID_SEND_CUSTOM_CMD = MsgSection.MSG_ID_SEND_CUSTOM_CMD + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        MediaPlayer.iSendCmdToFriend(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_SEND_CUSTOM_CMD, msg.getBytes(), msg.getBytes().length);
        MSG_ID_SEND_CUSTOM_CMD++;
        return String.valueOf(MSG_ID_SEND_CUSTOM_CMD - 1);
    }

    public void openAudioAndStartPlaying(int callType) {
        try {
            MediaPlayer.getInstance();
            MediaPlayer.openAudioTrack();
            MediaPlayer.getInstance()._StartPlaying(320, 240, callType);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public synchronized void reject() {
        synchronized (this) {
            MediaPlayer.getInstance().native_p2p_hungup();
        }
    }

    public void accept() {
        MediaPlayer.getInstance().native_p2p_accpet();
    }

    public boolean call(String contactId, String password, boolean isOutCall, int callType, String callModel, String callId, String ipFlag, String pushMsg) {
        boolean result = false;
        byte[] byt = new byte[8];
        if (isOutCall) {
            try {
                if (P2PValue.HikamDeviceModelList.contains(callModel)) {
                    MediaPlayer.getInstance().setP2pType(1);
                    MediaPlayer.getInstance().setCallModel(callModel);
                    MediaPlayer.getInstance().setCallID(callId);
                    MediaPlayer.getInstance().p2p_open_stream(5, password);
                } else {
                    int x;
                    MediaPlayer.getInstance().setCallModel(callModel);
                    String parseNum = callId;
                    if (parseNum.contains("+")) {
                        boolean isPhone = false;
                        for (int i = 0; i < regionCode.length; i++) {
                            int cLength = String.valueOf(regionCode[i]).length();
                            parseNum = parseNum.replace("+", "");
                            String hight = parseNum.substring(0, cLength);
                            String low = parseNum.substring(cLength, parseNum.length());
                            if (Integer.parseInt(hight) == regionCode[i]) {
                                parseNum = String.valueOf((Long.parseLong(hight) << 48) | Long.parseLong(low));
                                isPhone = true;
                                break;
                            }
                        }
                        if (!isPhone) {
                            return 0;
                        }
                    }
                    long id = Long.parseLong(parseNum);
                    if (parseNum.charAt(0) == '0') {
                        id = 0 - id;
                    }
                    int pwd = 0;
                    int isMonitor = 0;
                    if (callType == 1) {
                        isMonitor = 1;
                        pwd = Integer.parseInt(password);
                    }
                    MediaPlayer.getInstance().setCallModel(callModel);
                    if (ipFlag != null) {
                        if (!ipFlag.equals("") && MyUtils.isNumeric(ipFlag)) {
                            x = MediaPlayer.getInstance().native_p2p_call((long) Integer.parseInt(ipFlag), isMonitor, pwd, -1, AppConfig.VideoMode, byt, pushMsg.getBytes("utf-8"), SharedPreferencesManager.SP_FILE_GWELL, Long.parseLong(callId));
                            if (x != 1) {
                                result = true;
                                Log.i("tag", "p2p call success");
                            } else {
                                Log.e("tag", "p2p call fail");
                            }
                        }
                    }
                    x = MediaPlayer.getInstance().native_p2p_call(id, isMonitor, pwd, -1, AppConfig.VideoMode, byt, pushMsg.getBytes("utf-8"), SharedPreferencesManager.SP_FILE_GWELL, Long.parseLong(callId));
                    if (x != 1) {
                        Log.e("tag", "p2p call fail");
                    } else {
                        result = true;
                        Log.i("tag", "p2p call success");
                    }
                }
            } catch (Exception e) {
                result = false;
                e.printStackTrace();
            }
        }
        return result;
    }

    private void call_alter(String threeNumber, String password, boolean isOutCall, int callType, String callId, String ipFlag, String pushMsg) throws UnsupportedEncodingException {
        byte[] byt = new byte[8];
        if (isOutCall) {
            int result;
            String parseNum = callId;
            if (parseNum.contains("+")) {
                boolean isPhone = false;
                int i = 0;
                while (i < regionCode.length) {
                    try {
                        int cLength = String.valueOf(regionCode[i]).length();
                        parseNum = parseNum.replace("+", "");
                        String hight = parseNum.substring(0, cLength);
                        String low = parseNum.substring(cLength, parseNum.length());
                        if (Integer.parseInt(hight) == regionCode[i]) {
                            parseNum = String.valueOf((Long.parseLong(hight) << 48) | Long.parseLong(low));
                            isPhone = true;
                            break;
                        }
                        i++;
                    } catch (Exception e) {
                    }
                }
                if (!isPhone) {
                    return;
                }
            }
            long id = Long.parseLong(parseNum);
            if (parseNum.charAt(0) == '0') {
                id = 0 - id;
            }
            int pwd = 0;
            int isMonitor = 0;
            if (callType == 1) {
                isMonitor = 1;
                if (MyUtils.isNumeric(password)) {
                    pwd = Integer.parseInt(password);
                }
            }
            if (ipFlag != null) {
                if (!ipFlag.equals("") && MyUtils.isNumeric(ipFlag)) {
                    result = MediaPlayer.getInstance().native_p2p_call((long) Integer.parseInt(ipFlag), isMonitor, pwd, -1, AppConfig.VideoMode, byt, pushMsg.getBytes("utf-8"), SharedPreferencesManager.SP_FILE_GWELL, Long.parseLong(callId));
                    if (result != 1) {
                        Log.i("tag", "p2p call success");
                    } else {
                        Log.e("tag", "p2p call fail");
                    }
                }
            }
            result = MediaPlayer.getInstance().native_p2p_call(id, isMonitor, pwd, -1, AppConfig.VideoMode, byt, pushMsg.getBytes("utf-8"), SharedPreferencesManager.SP_FILE_GWELL, Long.parseLong(callId));
            if (result != 1) {
                Log.e("tag", "p2p call fail");
            } else {
                Log.i("tag", "p2p call success");
            }
        }
    }

    public void playbackConnect(String contactModel, String contactId, String password, String filename, int framerate, int recordFilePosition) {
        MediaPlayer.getInstance().setCallModel(contactModel);
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            MediaPlayer.getInstance().setP2pType(2);
            MediaPlayer.getInstance().setCallID(contactId);
            MediaPlayer.getInstance().p2p_start_playback(filename, framerate, 0, password);
            return;
        }
        int i = recordFilePosition;
        MediaPlayer.getInstance().native_p2p_call((long) Integer.parseInt(contactId), 2, Integer.parseInt(password), i, AppConfig.VideoMode, filename.getBytes(), "".getBytes(), SharedPreferencesManager.SP_FILE_GWELL, Long.parseLong(contactId));
    }

    public int setVideoMode(String password, String contactModel, int type) {
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            MediaPlayer.getInstance().p2p_switch_stream(password, type);
            return 0;
        }
        MediaPlayer.getInstance();
        return MediaPlayer.iSetVideoMode(type);
    }

    public void checkDeviceUpdate(String contactModel, String contactId, String password, String curVersion) {
        Log.e(this.TAG, "P2PHANDLER:checkDeviceUpdate");
        if (MSG_ID_CHECK_DEVICE_UPDATE >= MsgSection.MSG_ID_CHECK_DEVICE_UPDATE) {
            MSG_ID_CHECK_DEVICE_UPDATE = MsgSection.MSG_ID_CHECK_DEVICE_UPDATE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            final String strCurVersion = curVersion;
            final String strDevModel = contactModel;
            final String strDevID = contactId;
            new Thread() {
                public void run() {
                    P2pJni.P2PClientSdkQueryLastVersion(strCurVersion, strDevModel, MyUtils.getShortDevID(strDevID));
                }
            }.start();
        } else {
            MediaPlayer.getInstance();
            MediaPlayer.checkDeviceUpdate(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_CHECK_DEVICE_UPDATE);
        }
        MSG_ID_CHECK_DEVICE_UPDATE++;
    }

    public void doDeviceUpdate(String contactModel, String contactId, String password, String upg_version) {
        Log.e(this.TAG, "P2PHANDLER:doDeviceUpdate");
        if (MSG_ID_DO_DEVICE_UPDATE >= MsgSection.MSG_ID_DO_DEVICE_UPDATE) {
            MSG_ID_DO_DEVICE_UPDATE = MsgSection.MSG_ID_DO_DEVICE_UPDATE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkDeviceUpgrade(contactId, contactModel, upg_version);
        } else {
            MediaPlayer.getInstance();
            MediaPlayer.doDeviceUpdate(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_DO_DEVICE_UPDATE);
        }
        MSG_ID_DO_DEVICE_UPDATE++;
    }

    public void cancelDeviceUpdate(String contactModel, String contactId, String password) {
        Log.e(this.TAG, "P2PHANDLER:cancelDeviceUpdate");
        if (MSG_ID_CANCEL_DEVICE_UPDATE >= MsgSection.MSG_ID_CANCEL_DEVICE_UPDATE) {
            MSG_ID_CANCEL_DEVICE_UPDATE = MsgSection.MSG_ID_CANCEL_DEVICE_UPDATE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkCancelDeviceUpgrade(contactId);
        } else {
            MediaPlayer.getInstance();
            MediaPlayer.cancelDeviceUpdate(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_CANCEL_DEVICE_UPDATE);
        }
        MSG_ID_CANCEL_DEVICE_UPDATE++;
    }

    public void getDeviceVersion(String contactModel, String contactId, String password) {
        Log.e(this.TAG, "P2PHANDLER:getDeviceVersion");
        if (MSG_ID_GET_DEVICE_VERSION >= MsgSection.MSG_ID_GET_DEVICE_VERSION) {
            MSG_ID_GET_DEVICE_VERSION = MsgSection.MSG_ID_GET_DEVICE_VERSION + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            final String strContactId = contactId;
            final String strPassword = password;
            final int msgID = MSG_ID_GET_DEVICE_VERSION;
            new Thread() {
                public void run() {
                    P2pJni.P2PClientSdkGetDeviceVersion(strContactId, strPassword, msgID);
                }
            }.start();
        } else {
            MediaPlayer.getInstance();
            MediaPlayer.getDeviceVersion(Integer.parseInt(contactId), Integer.parseInt(password), MSG_ID_GET_DEVICE_VERSION);
        }
        MSG_ID_GET_DEVICE_VERSION++;
    }

    public boolean sendCtlCmd(int cmd, int option) {
        if (MediaPlayer.iSendCtlCmd(cmd, option) == 1) {
            return true;
        }
        return false;
    }

    public void setBindFlag(int flag) {
        MediaPlayer.setBindFlag(flag);
    }

    public void setRecvAVDataEnable(boolean fgEn) {
        MediaPlayer.getInstance()._SetRecvAVDataEnable(fgEn);
    }

    public void setImageReverse(String contactModel, String contactId, String password, int value) {
        Log.e(this.TAG, "P2PHANDLER:setImageReverse");
        if (MESG_ID_STTING_PIC_REVERSE >= MsgSection.MESG_ID_STTING_PIC_REVERSE) {
            MESG_ID_STTING_PIC_REVERSE = MsgSection.MESG_ID_STTING_PIC_REVERSE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetImageReverse(contactId, password, value, MESG_ID_STTING_PIC_REVERSE);
        } else {
            MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(password), MESG_ID_STTING_PIC_REVERSE, 24, value);
        }
        MESG_ID_STTING_PIC_REVERSE++;
    }

    public void setInfraredSwitch(String contactId, String password, int value) {
        if (MESG_ID_STTING_IR_ALARM_EN >= MsgSection.MESG_ID_STTING_IR_ALARM_EN) {
            MESG_ID_STTING_IR_ALARM_EN = MsgSection.MESG_ID_STTING_IR_ALARM_EN + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(password), MESG_ID_STTING_IR_ALARM_EN, 17, value);
        MESG_ID_STTING_IR_ALARM_EN++;
    }

    public void setWiredAlarmInput(String contactId, String password, int value) {
        if (MESG_STTING_ID_EXTLINE_ALARM_IN_EN >= MsgSection.MESG_STTING_ID_EXTLINE_ALARM_IN_EN) {
            MESG_STTING_ID_EXTLINE_ALARM_IN_EN = MsgSection.MESG_STTING_ID_EXTLINE_ALARM_IN_EN + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(password), MESG_STTING_ID_EXTLINE_ALARM_IN_EN, 18, value);
        MESG_STTING_ID_EXTLINE_ALARM_IN_EN++;
    }

    public void setWiredAlarmOut(String contactId, String password, int value) {
        if (MESG_STTING_ID_EXTLINE_ALARM_OUT_EN >= MsgSection.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN) {
            MESG_STTING_ID_EXTLINE_ALARM_OUT_EN = MsgSection.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(password), MESG_STTING_ID_EXTLINE_ALARM_OUT_EN, 19, value);
        MESG_STTING_ID_EXTLINE_ALARM_OUT_EN++;
    }

    public void setAutomaticUpgrade(String contactId, String password, int value) {
        if (MESG_STTING_ID_SECUPGDEV >= MsgSection.MESG_STTING_ID_SECUPGDEV) {
            MESG_STTING_ID_SECUPGDEV = MsgSection.MESG_STTING_ID_SECUPGDEV + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(password), MESG_STTING_ID_SECUPGDEV, 16, value);
        MESG_STTING_ID_SECUPGDEV++;
    }

    public void setTimeZone(String contactModel, String contactId, String password, int value) {
        if (MESG_STTING_ID_TIMEZONE >= MsgSection.MESG_STTING_ID_TIMEZONE) {
            MESG_STTING_ID_TIMEZONE = MsgSection.MESG_STTING_ID_TIMEZONE + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetDeviceTimeZone(contactId, password, value, MESG_STTING_ID_TIMEZONE);
        } else {
            MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId), Integer.parseInt(password), MESG_STTING_ID_TIMEZONE, 20, value);
        }
        MESG_STTING_ID_TIMEZONE++;
    }

    public void getSdCardCapacity(String contactModel, String contactId, String password, String data) {
        if (MESG_GET_SD_CARD_CAPACITY >= MsgSection.MESG_GET_SD_CARD_CAPACITY) {
            MESG_GET_SD_CARD_CAPACITY = MsgSection.MESG_GET_SD_CARD_CAPACITY + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkGetSdcardInfo(contactId, password, MESG_GET_SD_CARD_CAPACITY);
        } else {
            byte[] datas = new byte[16];
            datas[0] = (byte) 80;
            datas[1] = (byte) 0;
            datas[2] = (byte) 0;
            datas[3] = (byte) 0;
            MediaPlayer.iExtendedCmd(Integer.parseInt(contactId), Integer.parseInt(password), MESG_GET_SD_CARD_CAPACITY, datas, 4);
        }
        MESG_GET_SD_CARD_CAPACITY++;
    }

    public void setSdFormat(String contactModel, String contactId, String password, int SDcardID) {
        if (MESG_SD_CARD_FORMAT >= MsgSection.MESG_SD_CARD_FORMAT) {
            MESG_SD_CARD_FORMAT = MsgSection.MESG_SD_CARD_FORMAT + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkFormatSDCard(contactId);
        } else {
            byte[] datas = new byte[16];
            datas[0] = (byte) 81;
            datas[1] = (byte) 0;
            datas[2] = (byte) 0;
            datas[3] = (byte) 0;
            datas[4] = (byte) SDcardID;
            Log.e("id", "id:" + datas[4]);
            MediaPlayer.iExtendedCmd(Integer.parseInt(contactId), Integer.parseInt(password), MESG_SD_CARD_FORMAT, datas, 5);
        }
        MESG_SD_CARD_FORMAT++;
    }

    public byte[] getPwdBytes(String pwd) {
        int k = 8 - (pwd.length() % 8);
        for (int i = 0; i < k; i++) {
            pwd = pwd + "0";
        }
        try {
            return DES.des(pwd.getBytes(), 0);
        } catch (Exception e) {
            byte[] ppp = new byte[]{(byte) 0};
            e.printStackTrace();
            return ppp;
        }
    }

    public void setGPIO(String contactId, String password, int group, int pin) {
        if (MESG_SET_GPIO >= MsgSection.MESG_SET_GPIO) {
            MESG_SET_GPIO = MsgSection.MESG_SET_GPIO + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        byte[] datas = new byte[37];
        datas[0] = (byte) 95;
        datas[1] = (byte) 0;
        datas[2] = (byte) group;
        datas[3] = (byte) pin;
        datas[4] = (byte) 5;
        datas[5] = (byte) -15;
        datas[6] = (byte) -1;
        datas[7] = (byte) -1;
        datas[8] = (byte) -1;
        datas[9] = (byte) -24;
        datas[10] = (byte) 3;
        datas[11] = (byte) 0;
        datas[12] = (byte) 0;
        datas[13] = (byte) 24;
        datas[14] = (byte) -4;
        datas[15] = (byte) -1;
        datas[16] = (byte) -1;
        datas[17] = (byte) -24;
        datas[18] = (byte) 3;
        datas[19] = (byte) 0;
        datas[20] = (byte) 0;
        datas[21] = (byte) 24;
        datas[22] = (byte) -4;
        datas[23] = (byte) -1;
        datas[24] = (byte) -1;
        for (int i = 25; i < datas.length; i++) {
            datas[i] = (byte) 0;
        }
        String s = "";
        for (byte b : datas) {
            s = s + " " + b;
        }
        Log.e("GPIO", "GPIO" + s + " length=" + datas.length);
        MediaPlayer.iExtendedCmd(Integer.parseInt(contactId), Integer.parseInt(password), MESG_SET_GPIO, datas, datas.length);
        MESG_SET_GPIO++;
    }

    public void setGPIO1_0(String contactId, String password) {
        if (MESG_SET_GPI1_0 >= MsgSection.MESG_SET_GPI1_0) {
            MESG_SET_GPI1_0 = MsgSection.MESG_SET_GPI1_0 + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        byte[] datas = new byte[37];
        datas[0] = (byte) 95;
        datas[1] = (byte) 0;
        datas[2] = (byte) 1;
        datas[3] = (byte) 0;
        datas[4] = (byte) 3;
        datas[5] = (byte) -15;
        datas[6] = (byte) -1;
        datas[7] = (byte) -1;
        datas[8] = (byte) -1;
        datas[9] = (byte) 112;
        datas[10] = (byte) 23;
        datas[11] = (byte) 0;
        datas[12] = (byte) 0;
        datas[13] = (byte) -112;
        datas[14] = (byte) -24;
        datas[15] = (byte) -1;
        datas[16] = (byte) -1;
        for (int i = 17; i < datas.length; i++) {
            datas[i] = (byte) 0;
        }
        String s = "";
        for (byte b : datas) {
            s = s + " " + b;
        }
        Log.e("GPIO", "GPIO" + s + " length=" + datas.length);
        MediaPlayer.iExtendedCmd(Integer.parseInt(contactId), Integer.parseInt(password), MESG_SET_GPI1_0, datas, datas.length);
        MESG_SET_GPI1_0++;
    }

    public String EntryPassword(String password) {
        return (!MyUtils.isNumeric(password) || password.length() >= 10) ? String.valueOf(MediaPlayer.EntryPwd(password)) : password;
    }

    public void setUploadToSvr(String contactModel, String contactId, String password) {
        Log.e(this.TAG, "P2PHANDLER:setUploadToSvr");
        if (MESG_SET_UPLOAD_TO_SER >= MsgSection.MESG_SET_UPLOAD_TO_SER) {
            MESG_SET_UPLOAD_TO_SER = MsgSection.MESG_SET_UPLOAD_TO_SER + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetUploadToSvr(contactId, password, MESG_SET_UPLOAD_TO_SER);
        }
        MESG_SET_UPLOAD_TO_SER++;
    }

    public void setLampSwitch(String contactModel, String contactId, String password, int onoff) {
        Log.e(this.TAG, "P2PHANDLER:setLampSwitch");
        if (MESG_SET_LAMP_SWITCH >= MsgSection.MESG_SET_LAMP_SWITCH) {
            MESG_SET_LAMP_SWITCH = MsgSection.MESG_SET_LAMP_SWITCH + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetLampSwitch(contactId, password, onoff, MESG_SET_LAMP_SWITCH);
        }
        MESG_SET_LAMP_SWITCH++;
    }

    public void getLampSwitch(String contactModel, String contactId, String password) {
        Log.e(this.TAG, "P2PHANDLER:getLampSwitch");
        if (MESG_GET_LAMP_SWITCH >= MsgSection.MESG_GET_LAMP_SWITCH) {
            MESG_GET_LAMP_SWITCH = MsgSection.MESG_GET_LAMP_SWITCH + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkGetLampSwitch(contactId, password, MESG_GET_LAMP_SWITCH);
        }
        MESG_GET_LAMP_SWITCH++;
    }

    public void SetAlarmTiming(String contactModel, String contactId, String password, int[] sH, int[] sM, int[] eH, int[] eM) {
        Log.e(this.TAG, "P2PHANDLER:setAlarmTiming");
        if (MESG_GET_LAMP_SWITCH >= MsgSection.MESG_GET_LAMP_SWITCH) {
            MESG_GET_LAMP_SWITCH = MsgSection.MESG_GET_LAMP_SWITCH + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetAlarmTiming(contactId, password, sH, sM, eH, eM, MESG_GET_LAMP_SWITCH);
        }
        MESG_GET_LAMP_SWITCH++;
    }

    public void GetAlarmTiming(String contactModel, String contactId, String password) {
        Log.e(this.TAG, "P2PHANDLER:GetAlarmTiming");
        if (MESG_GET_LAMP_SWITCH >= MsgSection.MESG_GET_LAMP_SWITCH) {
            MESG_GET_LAMP_SWITCH = MsgSection.MESG_GET_LAMP_SWITCH + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkGetAlarmTiming(contactId, password, MESG_GET_LAMP_SWITCH);
        }
        MESG_GET_LAMP_SWITCH++;
    }

    public void GetHumanDetect(String contactModel, String contactId, String password) {
        Log.e(this.TAG, "P2PHANDLER:GetHumanDetect");
        if (MESG_GET_HUMAN_DETECT >= MsgSection.MESG_GET_HUMAN_DETECT) {
            MESG_GET_HUMAN_DETECT = MsgSection.MESG_GET_HUMAN_DETECT + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkGetHumanDetect(contactId, password, MESG_GET_HUMAN_DETECT);
        }
        MESG_GET_HUMAN_DETECT++;
    }

    public void SetHumanDetect(String contactModel, String contactId, String password, int enable) {
        Log.e(this.TAG, "P2PHANDLER:SETHumanDetect");
        if (MESG_SET_HUMAN_DETECT >= MsgSection.MESG_SET_HUMAN_DETECT) {
            MESG_SET_HUMAN_DETECT = MsgSection.MESG_SET_HUMAN_DETECT + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkSetHumanDetect(contactId, password, enable, MESG_SET_HUMAN_DETECT);
        }
        MESG_SET_HUMAN_DETECT++;
    }

    public void ActiveHumanDetect(String contactModel, String contactId, String password) {
        Log.e(this.TAG, "P2PHANDLER:ActiveHumanDetect");
        if (MESG_GET_HUMAN_DETECT >= MsgSection.MESG_GET_HUMAN_DETECT) {
            MESG_GET_HUMAN_DETECT = MsgSection.MESG_GET_HUMAN_DETECT + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkActiveHumanDetect(contactId, password, MESG_GET_HUMAN_DETECT);
        }
        MESG_GET_HUMAN_DETECT++;
    }

    public void GetValidDataHumanDetect(String contactModel, String contactId, String password) {
        Log.e(this.TAG, "P2PHANDLER:GetValidDataHumanDetect");
        if (MESG_GET_HUMAN_DETECT >= MsgSection.MESG_GET_HUMAN_DETECT) {
            MESG_GET_HUMAN_DETECT = MsgSection.MESG_GET_HUMAN_DETECT + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (P2PValue.HikamDeviceModelList.contains(contactModel)) {
            P2pJni.P2PClientSdkGetValidDataHumanDetect(contactId, password, MESG_GET_HUMAN_DETECT);
        }
        MESG_GET_HUMAN_DETECT++;
    }
}
