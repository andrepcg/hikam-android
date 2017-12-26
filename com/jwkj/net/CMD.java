package com.jwkj.net;

import android.content.Context;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.entity.CmdTokenUpdate;
import com.jwkj.entity.CmdTokenUpdate.CmdParamBean;
import com.jwkj.global.MyApp;
import com.jwkj.net.HKHttpClient.HKCallback;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import okhttp3.Call;
import okhttp3.Response;

public class CMD {
    public static final String CMD_ALARM_FEEDBACK = "AlarmFeedbackByApp";
    public static final String CMD_CAMERA_ADD = "deviceAddCamera";
    public static final String CMD_CAMERA_DELETE = "deviceDelCamera";
    public static final String CMD_CAMERA_UPDATE = "deviceUpdateCamera";
    public static final String CMD_TOKEN_UPDATE = "deviceUpdateToken";
    public static final String MAGIC_NUMBER = "1Q2W3E4R5T6Y7U8I9O0P";
    public static final String PHONE_TYPE = "Android";
    public static final String URL = "https://35.158.139.138:8443/HiKamPushServer/sendJson";
    public static final String URL2 = "http://192.168.0.78:8844/HiKamAlarmFeedbackService/sendJson";

    static class C11281 implements HKCallback {
        C11281() {
        }

        public void onFailure(Call call, IOException e) {
        }

        public void onResponse(Call call, Response response) {
        }
    }

    public static String getLanguage(Context context) {
        String language = context.getResources().getConfiguration().locale.getLanguage();
        Object obj = -1;
        switch (language.hashCode()) {
            case 3886:
                if (language.equals("zh")) {
                    obj = null;
                    break;
                }
                break;
        }
        switch (obj) {
            case null:
                language = "zh-CN";
                break;
            default:
                language = "";
                break;
        }
        Log.e("few", language + "  -- lang");
        return language;
    }

    public static int getTimeZoneOffset() {
        return TimeZone.getDefault().getRawOffset() / 1000;
    }

    public static int getDST() {
        return TimeZone.getDefault().getDSTSavings() / 1000;
    }

    public static int getTimeZone() {
        return ((TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings()) / 1000) / 3600;
    }

    public static String getDateTime(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    public static String getDateTime() {
        return getDateTime("yyyy-MM-dd HH:mm");
    }

    public static String getToken() {
        boolean isFirst = true;
        String token = FirebaseInstanceId.getInstance().getToken();
        String oldToken = SharedPreferencesManager.getInstance().getFbPushToken(MyApp.app);
        if (!"".equals(oldToken)) {
            isFirst = false;
        }
        if ("".equals(token) || token == null) {
            return oldToken;
        }
        if (isFirst) {
            SharedPreferencesManager.getInstance().setFbPushToken(MyApp.app, token);
            return token;
        } else if (oldToken.equals(token)) {
            return token;
        } else {
            CmdTokenUpdate cmdTokenUpdate = new CmdTokenUpdate();
            cmdTokenUpdate.setMagic_number(MAGIC_NUMBER);
            cmdTokenUpdate.setMessage_id((int) (System.currentTimeMillis() / 1000));
            cmdTokenUpdate.setDate_time(getDateTime());
            cmdTokenUpdate.setMessage_cmd(CMD_TOKEN_UPDATE);
            CmdParamBean bean = new CmdParamBean();
            bean.setDeviceLang(getLanguage(MyApp.app));
            bean.setDeviceZoneOffset(getTimeZoneOffset());
            bean.setDeviceDstOffset(getDST());
            bean.setNewToken(token);
            bean.setOldToken(oldToken);
            cmdTokenUpdate.setCmd_param(bean);
            HKHttpClient.getInstance().asyncPost(cmdTokenUpdate, URL, new C11281());
            return token;
        }
    }
}
