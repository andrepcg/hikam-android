package com.jwkj.entity;

public class CmdTokenUpdate {
    private CmdParamBean cmd_param;
    private String date_time;
    private String magic_number;
    private String message_cmd;
    private int message_id;

    public static class CmdParamBean {
        private int deviceDstOffset;
        private String deviceLang;
        private int deviceZoneOffset;
        private String newToken;
        private String oldToken;

        public String getOldToken() {
            return this.oldToken;
        }

        public void setOldToken(String oldToken) {
            this.oldToken = oldToken;
        }

        public String getNewToken() {
            return this.newToken;
        }

        public void setNewToken(String newToken) {
            this.newToken = newToken;
        }

        public String getDeviceLang() {
            return this.deviceLang;
        }

        public void setDeviceLang(String deviceLang) {
            this.deviceLang = deviceLang;
        }

        public int getDeviceZoneOffset() {
            return this.deviceZoneOffset;
        }

        public void setDeviceZoneOffset(int deviceZoneOffset) {
            this.deviceZoneOffset = deviceZoneOffset;
        }

        public int getDeviceDstOffset() {
            return this.deviceDstOffset;
        }

        public void setDeviceDstOffset(int deviceDstOffset) {
            this.deviceDstOffset = deviceDstOffset;
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
