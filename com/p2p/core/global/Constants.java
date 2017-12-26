package com.p2p.core.global;

import com.tencent.bugly.BuglyStrategy.C0691a;

public class Constants {

    public static class ACK_RET_TYPE {
        public static final int ACK_INSUFFICIENT_PERMISSIONS = 9996;
        public static final int ACK_NET_ERROR = 9998;
        public static final int ACK_PWD_ERROR = 9999;
        public static final int ACK_SUCCESS = 9997;
    }

    public static class ActivityStatus {
        public static final int STATUS_START = 0;
        public static final int STATUS_STOP = 1;
    }

    public static class MsgSection {
        public static int MESG_GET_HUMAN_DETECT = 48000;
        public static int MESG_GET_LAMP_SWITCH = 47000;
        public static int MESG_GET_SD_CARD_CAPACITY = 41000;
        public static int MESG_ID_STTING_IR_ALARM_EN = 35000;
        public static int MESG_ID_STTING_PIC_REVERSE = 34000;
        public static int MESG_SD_CARD_FORMAT = 42000;
        public static int MESG_SET_GPI1_0 = 44000;
        public static int MESG_SET_GPIO = 43000;
        public static int MESG_SET_HUMAN_DETECT = 49000;
        public static int MESG_SET_LAMP_SWITCH = 46000;
        public static int MESG_SET_UPLOAD_TO_SER = 45000;
        public static int MESG_STTING_ID_EXTLINE_ALARM_IN_EN = 36000;
        public static int MESG_STTING_ID_EXTLINE_ALARM_OUT_EN = 37000;
        public static int MESG_STTING_ID_GUEST_PASSWD = 39000;
        public static int MESG_STTING_ID_SECUPGDEV = 38000;
        public static int MESG_STTING_ID_TIMEZONE = 40000;
        public static int MSG_ID_CANCEL_DEVICE_UPDATE = 29000;
        public static int MSG_ID_CHECK_DEVICE_PASSWORD = 20000;
        public static int MSG_ID_CHECK_DEVICE_UPDATE = 28000;
        public static int MSG_ID_CLEAR_DEFENCE_GROUP = 33000;
        public static int MSG_ID_DO_DEVICE_UPDATE = C0691a.MAX_USERDATA_VALUE_LENGTH;
        public static int MSG_ID_GETTING_ALARM_BIND_ID = 17000;
        public static int MSG_ID_GETTING_ALARM_EMAIL = 15000;
        public static int MSG_ID_GETTING_DEFENCEAREA = 22000;
        public static int MSG_ID_GETTING_DEVICE_TIME = 2000;
        public static int MSG_ID_GETTING_NPC_SETTINGS = 3000;
        public static int MSG_ID_GETTING_RECORD_FILE_LIST = 25000;
        public static int MSG_ID_GETTING_WIFI_LIST = 24000;
        public static int MSG_ID_GET_DEFENCE_STATE = 31000;
        public static int MSG_ID_GET_DEVICE_VERSION = 32000;
        public static int MSG_ID_SEND_CUSTOM_CMD = 27000;
        public static int MSG_ID_SEND_MESSAGE = 26000;
        public static int MSG_ID_SETTING_ALARM_BIND_ID = 16000;
        public static int MSG_ID_SETTING_ALARM_EMAIL = 14000;
        public static int MSG_ID_SETTING_DEFENCEAREA = 21000;
        public static int MSG_ID_SETTING_DEVICE_PASSWORD = 19000;
        public static int MSG_ID_SETTING_DEVICE_TIME = 1000;
        public static int MSG_ID_SETTING_INIT_PASSWORD = 18000;
        public static int MSG_ID_SETTING_NPC_SETTINGS_BUZZER = 8000;
        public static int MSG_ID_SETTING_NPC_SETTINGS_MOTION = 9000;
        public static int MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE = 13000;
        public static int MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME = 12000;
        public static int MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME = 11000;
        public static int MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE = 10000;
        public static int MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT = 6000;
        public static int MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME = 7000;
        public static int MSG_ID_SETTING_WIFI = 23000;
        public static int MSG_ID_SET_REMOTE_DEFENCE = 4000;
        public static int MSG_ID_SET_REMOTE_RECORD = 5000;
    }

    public static class P2P_CALL {

        public static class CALL_RESULT {
            public static final int CALL_PHONE_FORMAT_ERROR = 1;
            public static final int CALL_SUCCESS = 0;
        }
    }

    public static class P2P_SETTING {
        public static final int DEFENCE_AREA_TYPE_CLEAR = 1;
        public static final int DEFENCE_AREA_TYPE_LEARN = 0;

        public static class SETTING_TYPE {
            public static final int SETTING_ALARM_ID = 10;
            public static final int SETTING_BUZZER = 1;
            public static final int SETTING_DEVICE_PWD = 9;
            public static final int SETTING_ID_EXTLINE_ALARM_IN_EN = 18;
            public static final int SETTING_ID_EXTLINE_ALARM_OUT_EN = 19;
            public static final int SETTING_ID_GUEST_PASSWD = 21;
            public static final int SETTING_ID_IR_ALARM_EN = 17;
            public static final int SETTING_ID_SECUPGDEV = 16;
            public static final int SETTING_ID_TIMEZONE = 20;
            public static final int SETTING_IMAGE_REVERSE = 24;
            public static final int SETTING_MOTION_DECT = 2;
            public static final int SETTING_NET_TYPE = 13;
            public static final int SETTING_RECORD_PLAN_TIME = 5;
            public static final int SETTING_RECORD_TIME = 11;
            public static final int SETTING_RECORD_TYPE = 3;
            public static final int SETTING_REMOTE_DEFENCE = 0;
            public static final int SETTING_REMOTE_RECORD = 4;
            public static final int SETTING_VIDEO_FORMAT = 8;
            public static final int SETTING_VOLUME = 14;
            public static final int STTING_ID_GET_AUDIO_DEVICE_TYPE = 27;
        }
    }

    public static class P2P_TYPE {
        public static final int P2P_TYPE_CALL = 0;
        public static final int P2P_TYPE_MONITOR = 1;
        public static final int P2P_TYPE_PLAYBACK = 2;
    }

    public static class P2P_WINDOW {
        public static final int P2P_SURFACE_START_PLAYING_HEIGHT = 240;
        public static final int P2P_SURFACE_START_PLAYING_WIDTH = 320;

        public static class Action {
            public static final String P2P_WINDOW_READY_TO_START = "com.p2p.core.P2P_WINDOW_READY_TO_START";
        }
    }

    public static class SettingConfig {
        public static final int ACK_NET_ERROR = 9998;
        public static final int ACK_PWD_ERROR = 9999;
        public static final int ACK_SUCCESS = 9997;
    }
}
