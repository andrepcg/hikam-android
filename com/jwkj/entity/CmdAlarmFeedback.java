package com.jwkj.entity;

public class CmdAlarmFeedback {
    private CmdParamBean cmd_param;
    private String date_time;
    private String magic_number;
    private String message_cmd;
    private int message_id;

    public static class CmdParamBean {
        private AlarmInfoBean alarm_info;
        private AppInfoBean app_info;
        private FeedbackInfoBean feedback_info;

        public static class AlarmInfoBean {
            private String alarm_type;
            private String alarm_uuid;
            private String camera_id;
            private String camera_name;

            public String getAlarm_uuid() {
                return this.alarm_uuid;
            }

            public void setAlarm_uuid(String alarm_uuid) {
                this.alarm_uuid = alarm_uuid;
            }

            public String getAlarm_type() {
                return this.alarm_type;
            }

            public void setAlarm_type(String alarm_type) {
                this.alarm_type = alarm_type;
            }

            public String getCamera_id() {
                return this.camera_id;
            }

            public void setCamera_id(String camera_id) {
                this.camera_id = camera_id;
            }

            public String getCamera_name() {
                return this.camera_name;
            }

            public void setCamera_name(String camera_name) {
                this.camera_name = camera_name;
            }
        }

        public static class AppInfoBean {
            private String device_token;
            private String user_name;

            public String getUser_name() {
                return this.user_name;
            }

            public void setUser_name(String user_name) {
                this.user_name = user_name;
            }

            public String getDevice_token() {
                return this.device_token;
            }

            public void setDevice_token(String device_token) {
                this.device_token = device_token;
            }
        }

        public static class FeedbackInfoBean {
            private String feedback_content;
            private String feedback_result;

            public String getFeedback_result() {
                return this.feedback_result;
            }

            public void setFeedback_result(String feedback_result) {
                this.feedback_result = feedback_result;
            }

            public String getFeedback_content() {
                return this.feedback_content;
            }

            public void setFeedback_content(String feedback_content) {
                this.feedback_content = feedback_content;
            }
        }

        public AppInfoBean getApp_info() {
            return this.app_info;
        }

        public void setApp_info(AppInfoBean app_info) {
            this.app_info = app_info;
        }

        public AlarmInfoBean getAlarm_info() {
            return this.alarm_info;
        }

        public void setAlarm_info(AlarmInfoBean alarm_info) {
            this.alarm_info = alarm_info;
        }

        public FeedbackInfoBean getFeedback_info() {
            return this.feedback_info;
        }

        public void setFeedback_info(FeedbackInfoBean feedback_info) {
            this.feedback_info = feedback_info;
        }
    }

    public String getMagic_number() {
        return this.magic_number;
    }

    public void setMagic_number(String magic_number) {
        this.magic_number = magic_number;
    }

    public int getMessage_id() {
        return this.message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public String getDate_time() {
        return this.date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getMessage_cmd() {
        return this.message_cmd;
    }

    public void setMessage_cmd(String message_cmd) {
        this.message_cmd = message_cmd;
    }

    public CmdParamBean getCmd_param() {
        return this.cmd_param;
    }

    public void setCmd_param(CmdParamBean cmd_param) {
        this.cmd_param = cmd_param;
    }
}
