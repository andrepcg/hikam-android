package com.p2p.core.network;

import android.util.Log;
import com.p2p.core.utils.MyUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class AlarmRecordResult implements Serializable {
    public String Surplus;
    public List<SAlarmRecord> alarmRecords = new ArrayList();
    public String error_code;

    public static class SAlarmRecord implements Serializable, Comparable {
        public long alarmTime;
        public int alarmType;
        public int channel;
        public int defenceArea;
        public String messgeId;
        public String pictureUrl;
        public long serverReceiveTime;
        public String sourceId;

        public int compareTo(Object arg0) {
            SAlarmRecord o = (SAlarmRecord) arg0;
            if (this.serverReceiveTime > o.serverReceiveTime) {
                return -1;
            }
            if (this.serverReceiveTime < o.serverReceiveTime) {
                return 1;
            }
            return 0;
        }

        public boolean equals(Object arg0) {
            if (this.messgeId.endsWith(((SAlarmRecord) arg0).messgeId)) {
                return true;
            }
            return false;
        }
    }

    public AlarmRecordResult(JSONObject json) {
        init(json);
    }

    private void init(JSONObject json) {
        try {
            this.error_code = json.getString("error_code");
            this.Surplus = json.getString("Surplus");
            for (String str : json.getString("RL").split(";")) {
                if (!str.equals("")) {
                    String[] data = str.split("&");
                    SAlarmRecord ar = new SAlarmRecord();
                    ar.messgeId = data[0];
                    ar.sourceId = data[1];
                    ar.alarmTime = MyUtils.convertTimeStringToInterval(data[2]);
                    ar.pictureUrl = data[3];
                    ar.alarmType = Integer.parseInt(data[4]);
                    ar.defenceArea = Integer.parseInt(data[5]);
                    ar.channel = Integer.parseInt(data[6]);
                    ar.serverReceiveTime = MyUtils.convertTimeStringToInterval(data[7]);
                    this.alarmRecords.add(ar);
                }
            }
        } catch (Exception e) {
            if (!MyUtils.isNumeric(this.error_code)) {
                Log.e("my", "GetAccountInfoResult json解析错误");
                this.error_code = String.valueOf(NetManager.JSON_PARSE_ERROR);
            }
        }
    }
}
