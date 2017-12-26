package com.p2p.core.network;

import android.util.Log;
import com.p2p.core.utils.MyUtils;
import java.io.Serializable;
import org.json.JSONObject;

public class LoginResult implements Serializable {
    public String contactId;
    public String countryCode;
    public String email;
    public String error_code;
    public String phone;
    public String rCode1;
    public String rCode2;
    public String sessionId;

    public LoginResult(JSONObject json) {
        init(json);
    }

    private void init(JSONObject json) {
        try {
            this.error_code = json.getString("error_code");
            this.contactId = json.getString("UserID");
            this.rCode1 = json.getString("P2PVerifyCode1");
            this.rCode2 = json.getString("P2PVerifyCode2");
            this.phone = json.getString("PhoneNO");
            this.email = json.getString("Email");
            this.sessionId = json.getString("SessionID");
            this.countryCode = json.getString("CountryCode");
            try {
                this.contactId = "0" + String.valueOf(Integer.parseInt(this.contactId) & Integer.MAX_VALUE);
            } catch (Exception e) {
            }
        } catch (Exception e2) {
            this.contactId = "";
            this.rCode1 = "";
            this.rCode2 = "";
            this.phone = "";
            this.email = "";
            this.sessionId = "";
            this.countryCode = "";
            if (!MyUtils.isNumeric(this.error_code)) {
                Log.e("my", "LoginResult json解析错误");
                this.error_code = String.valueOf(NetManager.JSON_PARSE_ERROR);
            }
        }
    }
}
