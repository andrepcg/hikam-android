package com.p2p.core.network;

import android.util.Log;
import com.p2p.core.utils.MyUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONObject;

public class GetAlarmRecordListResult {
    public List<GXAlarmRecord> datas = new ArrayList();
    public String error_code;

    public static class GXAlarmRecord implements Serializable, Comparable {
        public long deviceTime;
        public int group;
        public String imageUrl;
        public String index;
        public int item;
        public String sendContactId;
        public long serverTime;
        public int type;

        public int compareTo(Object arg0) {
            GXAlarmRecord o = (GXAlarmRecord) arg0;
            if (this.serverTime > o.serverTime) {
                return -1;
            }
            if (this.serverTime < o.serverTime) {
                return 1;
            }
            return 0;
        }

        public boolean equals(Object arg0) {
            if (this.index.equals(((GXAlarmRecord) arg0).index)) {
                return true;
            }
            return false;
        }
    }

    public GetAlarmRecordListResult(JSONObject json) {
        init(json);
    }

    private void init(JSONObject json) {
        try {
            this.error_code = json.getString("error_code");
            for (String str : json.getString("RL").split(";")) {
                if (!str.equals("")) {
                    String[] data = str.split("&");
                    GXAlarmRecord gxar = new GXAlarmRecord();
                    gxar.index = data[0];
                    gxar.sendContactId = data[1];
                    gxar.deviceTime = MyUtils.convertTimeStringToInterval(data[2]);
                    gxar.imageUrl = data[3];
                    gxar.type = Integer.parseInt(data[4]);
                    gxar.group = Integer.parseInt(data[5]);
                    gxar.item = Integer.parseInt(data[6]);
                    gxar.serverTime = MyUtils.convertTimeStringToInterval(data[7]);
                    this.datas.add(gxar);
                }
            }
            Collections.sort(this.datas);
        } catch (Exception e) {
            if (!MyUtils.isNumeric(this.error_code)) {
                Log.e("my", "GetAccountInfoResult json解析错误");
                this.error_code = String.valueOf(NetManager.JSON_PARSE_ERROR);
            }
        }
    }
}
