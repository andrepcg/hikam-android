package com.p2p.core.network;

import android.util.Log;
import com.p2p.core.utils.MyUtils;
import org.json.JSONObject;

public class ModifyLoginPasswordResult {
    public String error_code;
    public String sessionId;

    public ModifyLoginPasswordResult(JSONObject json) {
        init(json);
    }

    private void init(JSONObject json) {
        try {
            this.error_code = json.getString("error_code");
            this.sessionId = json.getString("SessionID");
        } catch (Exception e) {
            if (!MyUtils.isNumeric(this.error_code)) {
                Log.e("my", "ModifyLoginPasswordResult json解析错误");
                this.error_code = String.valueOf(NetManager.JSON_PARSE_ERROR);
            }
        }
    }
}
