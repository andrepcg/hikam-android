package com.p2p.core.network;

import android.util.Log;
import com.p2p.core.utils.MyUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class GetDeviceListResult {
    public String bindFlag;
    public List<String> contactIds = new ArrayList();
    public String error_code;
    public List<String> flags = new ArrayList();
    public List<String> nikeNames = new ArrayList();

    public GetDeviceListResult(JSONObject json) {
        init(json);
    }

    private void init(JSONObject json) {
        try {
            this.error_code = json.getString("error_code");
            this.bindFlag = json.getString("BindFlag");
            String[] list = json.getString("RL").split(",");
            for (String split : list) {
                String[] datas = split.split(":");
                this.contactIds.add(datas[0]);
                this.flags.add(datas[1]);
                this.nikeNames.add(datas[2]);
            }
        } catch (Exception e) {
            if (!MyUtils.isNumeric(this.error_code)) {
                Log.e("my", "GetAccountInfoResult json解析错误");
                this.error_code = String.valueOf(NetManager.JSON_PARSE_ERROR);
            }
        }
    }
}
