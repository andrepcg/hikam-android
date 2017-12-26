package com.p2p.core.network;

import android.util.Log;
import com.p2p.core.utils.MyUtils;
import org.json.JSONObject;

public class RegisterResult {
    public String contactId;
    public String error_code;

    public RegisterResult(JSONObject json) {
        init(json);
    }

    private void init(JSONObject json) {
        try {
            this.error_code = json.getString("error_code");
            this.contactId = json.getString("UserID");
        } catch (Exception e) {
            if (!MyUtils.isNumeric(this.error_code)) {
                Log.e("my", "RegisterResult json解析错误");
                this.error_code = String.valueOf(NetManager.JSON_PARSE_ERROR);
            }
        }
    }
}
