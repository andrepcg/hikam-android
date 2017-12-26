package com.jwkj.entity;

public class CmdCameraUpdate {
    private CmdParamBean cmd_param;
    private String date_time;
    private String magic_number;
    private String message_cmd;
    private int message_id;

    public static class CmdParamBean {
        private String cameraID;
        private String cameraName;
        private String deviceToken;

        public String getDeviceToken() {
            return this.deviceToken;
        }

        public void setDeviceToken(String deviceToken) {
            this.deviceToken = deviceToken;
        }

        public String getCameraID() {
            return this.cameraID;
        }

        public void setCameraID(String cameraID) {
            this.cameraID = cameraID;
        }

        public String getCameraName() {
            return this.cameraName;
        }

        public void setCameraName(String cameraName) {
            this.cameraName = cameraName;
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
