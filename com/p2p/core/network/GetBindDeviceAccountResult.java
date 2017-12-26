package com.p2p.core.network;

import android.util.Log;
import com.p2p.core.utils.MyUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class GetBindDeviceAccountResult {
    public List<String> contactIds = new ArrayList();
    public List<String> country_codes = new ArrayList();
    public String error_code;
    public List<String> flags = new ArrayList();
    public List<String> phones = new ArrayList();

    public GetBindDeviceAccountResult(JSONObject json) {
        init(json);
    }

    private void init(JSONObject json) {
        try {
            this.error_code = json.getString("error_code");
            String[] list = json.getString("RL").split(",");
            for (String split : list) {
                String[] datas = split.split(":");
                this.country_codes.add(datas[0]);
                this.phones.add(datas[1]);
                this.flags.add(datas[2]);
                String contactId = "";
                try {
                    contactId = "0" + String.valueOf(Integer.parseInt(datas[3]) & Integer.MAX_VALUE);
                } catch (Exception e) {
                }
                this.contactIds.add(contactId);
            }
        } catch (Exception e2) {
            if (!MyUtils.isNumeric(this.error_code)) {
                Log.e("my", "GetAccountInfoResult json解析错误");
                this.error_code = String.valueOf(NetManager.JSON_PARSE_ERROR);
            }
        }
    }
}
