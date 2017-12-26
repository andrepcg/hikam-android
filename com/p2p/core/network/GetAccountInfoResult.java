package com.p2p.core.network;

import android.util.Log;
import com.p2p.core.utils.MyUtils;
import org.json.JSONObject;

public class GetAccountInfoResult {
    public String countryCode;
    public String email;
    public String error_code;
    public String phone;

    public GetAccountInfoResult(JSONObject json) {
        init(json);
    }

    private void init(JSONObject json) {
        try {
            this.error_code = json.getString("error_code");
            this.email = json.getString("Email");
            this.countryCode = json.getString("CountryCode");
            this.phone = json.getString("PhoneNO");
        } catch (Exception e) {
            if (!MyUtils.isNumeric(this.error_code)) {
                Log.e("my", "GetAccountInfoResult json解析错误");
                this.error_code = String.valueOf(NetManager.JSON_PARSE_ERROR);
            }
        }
    }
}
