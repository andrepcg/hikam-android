package com.p2p.core.network;

import android.content.Context;
import android.util.Log;
import com.p2p.core.utils.MD5;
import com.p2p.core.utils.MyUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class NetManager {
    private static String ACCOUNT_INFO_URL = (CURRENT_SERVER + "Users/UpdateSafeSet.ashx");
    private static String ALARM_MSSAGE_URL = "http://192.168.1.222/Alarm/AlarmRecordEx.ashx";
    private static String ALARM_RECORD_LIST_URL = (CURRENT_SERVER + "Alarm/AlarmRecordEx.ashx");
    private static String BIND_DEVICE_ACCOUNT = (CURRENT_SERVER + "Account/Bind/BindAccountEx.ashx");
    public static final int CONNECT_CHANGE = 998;
    private static String CURRENT_SERVER = null;
    private static String DEVICE_LIST_URL = (CURRENT_SERVER + "Account/Bind/SearchBindDev.ashx");
    private static String EXIT_APPLICATION_URL = (CURRENT_SERVER + "Users/Logout.ashx");
    private static String GETPHONE_CODE_URL = (CURRENT_SERVER + "Users/PhoneCheckCode.ashx");
    public static final int GET_ACCOUNT_SUCCESS = 0;
    public static final int GET_ALARM_RECORD_EMPTY = 13;
    public static final int GET_ALARM_RECORD_SUCCESS = 0;
    public static final int GET_BIND_DEVICE_ACCOUNT_SUCCESS = 0;
    public static final int GET_DEVICE_LIST_EMPTY = 13;
    public static final int GET_DEVICE_LIST_SUCCESS = 0;
    public static final int GET_PHONE_CODE_PHONE_FORMAT_ERROR = 9;
    public static final int GET_PHONE_CODE_PHONE_USED = 6;
    public static final int GET_PHONE_CODE_SUCCESS = 0;
    public static final int GET_PHONE_CODE_TOO_TIMES = 27;
    public static final int JSON_PARSE_ERROR = 997;
    public static final int LOGIN_PWD_ERROR = 3;
    public static final int LOGIN_SUCCESS = 0;
    private static String LOGIN_URL = (CURRENT_SERVER + "Users/LoginCheck.ashx");
    public static final int LOGIN_USER_UNEXIST = 2;
    public static final int MODIFY_BIND_DEVICE_ACCOUNT_SUCCESS = 0;
    private static String MODIFY_LOGIN_PASSWORD_URL = (CURRENT_SERVER + "Users/ModifyPwd.ashx");
    public static final int MODIFY_LOGIN_PWD_INCONSISTENCE = 10;
    public static final int MODIFY_LOGIN_PWD_OLD_PWD_ERROR = 11;
    public static final int MODIFY_LOGIN_PWD_SUCCESS = 0;
    private static String MODIFY_NIKE_NAME = (CURRENT_SERVER + "Account/Bind/ModifyNickname.ashx");
    public static final int REGISTER_EMAIL_FORMAT_ERROR = 4;
    public static final int REGISTER_EMAIL_USED = 7;
    public static final int REGISTER_PASSWORD_NO_MATCH = 10;
    public static final int REGISTER_PHONE_CODE_ERROR = 18;
    public static final int REGISTER_PHONE_FORMAT_ERROR = 9;
    public static final int REGISTER_PHONE_USED = 6;
    public static final int REGISTER_SUCCESS = 0;
    private static String REGISTER_URL = (CURRENT_SERVER + "Users/RegisterCheck.ashx");
    private static String REMOVE_BIND_DEVICE_ACCOUNT = (CURRENT_SERVER + "Account/Bind/RemoveBindEx.ashx");
    private static String SEARCH_BIND_DEVICE_ACCOUNT = (CURRENT_SERVER + "Account/Bind/SearchBindAccountEx.ashx");
    private static final String SERVER1 = "http://api1.cloudlinks.cn/";
    private static final String SERVER2 = "http://api2.cloudlinks.cn/";
    private static final String SERVER3 = "http://api3.cloud-links.net/";
    private static final String SERVER4 = "http://api4.cloud-links.net/";
    public static final int SESSION_ID_ERROR = 23;
    public static final int SET_ACCOUNT_EMAIL_FORMAT_ERROR = 4;
    public static final int SET_ACCOUNT_EMAIL_USED = 7;
    public static final int SET_ACCOUNT_PWD_ERROR = 3;
    public static final int SET_ACCOUNT_SUCCESS = 0;
    public static final int UNKNOWN_ERROR = 999;
    public static final int VERIFY_CODE_ERROR = 18;
    public static final int VERIFY_CODE_SUCCESS = 0;
    public static final int VERIFY_CODE_TIME_OUT = 21;
    private static String VERIFY_CODE_URL = (CURRENT_SERVER + "Users/PhoneVerifyCodeCheck.ashx");
    private static boolean isInit = false;
    private static NetManager manager = null;
    private static int reconnect_count = 0;
    private static String[] servers = new String[4];
    private Context mContext;

    private NetManager() {
    }

    public static synchronized NetManager getInstance(Context context) {
        NetManager netManager;
        synchronized (NetManager.class) {
            if (manager == null) {
                synchronized (NetManager.class) {
                    manager = new NetManager();
                    manager.mContext = context;
                }
            }
            netManager = manager;
        }
        return netManager;
    }

    private void initServer() {
        Log.e("my", "initNetServer");
        randomServer();
        reconnect_count = 0;
        CURRENT_SERVER = servers[0];
        isInit = true;
    }

    private void randomServer() {
        Random random = new Random();
        if (MyUtils.isZh(this.mContext)) {
            if (random.nextInt(2) == 0) {
                servers[0] = SERVER1;
                servers[1] = SERVER2;
            } else {
                servers[0] = SERVER2;
                servers[1] = SERVER1;
            }
            if (random.nextInt(2) == 0) {
                servers[2] = SERVER3;
                servers[3] = SERVER4;
            } else {
                servers[2] = SERVER4;
                servers[3] = SERVER3;
            }
        } else {
            if (random.nextInt(2) == 0) {
                servers[0] = SERVER3;
                servers[1] = SERVER4;
            } else {
                servers[0] = SERVER4;
                servers[1] = SERVER3;
            }
            if (random.nextInt(2) == 0) {
                servers[2] = SERVER1;
                servers[3] = SERVER2;
            } else {
                servers[2] = SERVER2;
                servers[3] = SERVER1;
            }
        }
        CURRENT_SERVER = servers[0];
    }

    private static void updateUrl(String server) {
        LOGIN_URL = server + "Users/LoginCheck.ashx";
        GETPHONE_CODE_URL = server + "Users/PhoneCheckCode.ashx";
        VERIFY_CODE_URL = server + "Users/PhoneVerifyCodeCheck.ashx";
        REGISTER_URL = server + "Users/RegisterCheck.ashx";
        ACCOUNT_INFO_URL = server + "Users/UpdateSafeSet.ashx";
        ALARM_RECORD_LIST_URL = server + "Alarm/AlarmRecordEx.ashx";
        EXIT_APPLICATION_URL = server + "Users/Logout.ashx";
        MODIFY_LOGIN_PASSWORD_URL = server + "Users/ModifyPwd.ashx";
        BIND_DEVICE_ACCOUNT = server + "Account/Bind/BindAccountEx.ashx";
        SEARCH_BIND_DEVICE_ACCOUNT = server + "Account/Bind/SearchBindAccountEx.ashx";
        REMOVE_BIND_DEVICE_ACCOUNT = server + "Account/Bind/RemoveBindEx.ashx";
        DEVICE_LIST_URL = server + "Account/Bind/SearchBindDev.ashx";
        MODIFY_NIKE_NAME = server + "Account/Bind/ModifyNickname.ashx";
        ALARM_MSSAGE_URL = "http://192.168.1.222/Alarm/AlarmRecordEx.ashx";
    }

    public String doPost(List<NameValuePair> params, String url) throws Exception {
        if (!isInit) {
            initServer();
        }
        updateUrl(CURRENT_SERVER);
        Log.e("my", "current-server:" + CURRENT_SERVER);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter("http.connection.timeout", Integer.valueOf(10000));
        httpClient.getParams().setParameter("http.socket.timeout", Integer.valueOf(10000));
        try {
            HttpResponse httpResp = httpClient.execute(httpPost);
            int http_code = httpResp.getStatusLine().getStatusCode();
            if (http_code == 200) {
                String result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
                Log.e("my", "original http:" + result);
                int error_code = new JSONObject(result).getInt("error_code");
                if (error_code == 1 || error_code == 29 || error_code == 999) {
                    throw new Exception();
                }
                randomServer();
                reconnect_count = 0;
                return result;
            }
            Log.e("my", "HttpPost方式请求失败:" + http_code);
            throw new Exception();
        } catch (Exception e) {
            throw new Exception();
        } catch (Exception e2) {
            reconnect_count++;
            if (reconnect_count <= 3) {
                CURRENT_SERVER = servers[reconnect_count];
                Log.e("my", "更换服务器:" + CURRENT_SERVER);
                return "{\"error_code\":998}";
            }
            randomServer();
            reconnect_count = 0;
            return "{\"error_code\":999}";
        }
    }

    public JSONObject getAccountInfo(String contactId, String sessionId) {
        Exception e;
        JSONObject jObject = null;
        List<NameValuePair> params = new ArrayList();
        try {
            params.add(new BasicNameValuePair("Opion", "GetParam"));
            params.add(new BasicNameValuePair("UserID", String.valueOf(Integer.parseInt(contactId) | Integer.MIN_VALUE)));
            params.add(new BasicNameValuePair("SessionID", sessionId));
            JSONObject jObject2 = new JSONObject(doPost(params, ACCOUNT_INFO_URL));
            try {
                Log.e("my", jObject2.toString());
                return jObject2;
            } catch (Exception e2) {
                e = e2;
                jObject = jObject2;
                e.printStackTrace();
                return jObject;
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
            return jObject;
        }
    }

    public JSONObject getDeviceList(String phone, String sessionId) {
        Exception e;
        JSONObject jObject = null;
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("Account", phone));
        params.add(new BasicNameValuePair("SessionID", sessionId));
        try {
            JSONObject jObject2 = new JSONObject(doPost(params, DEVICE_LIST_URL));
            try {
                Log.e("my", jObject2.toString());
                return jObject2;
            } catch (Exception e2) {
                e = e2;
                jObject = jObject2;
                e.printStackTrace();
                return jObject;
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
            return jObject;
        }
    }

    public JSONObject getBindDeviceAccountList(String phone, String sessionId, String deviceId) {
        Exception e;
        JSONObject jObject = null;
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("Account", phone));
        params.add(new BasicNameValuePair("SessionID", sessionId));
        params.add(new BasicNameValuePair("DevID", deviceId));
        try {
            JSONObject jObject2 = new JSONObject(doPost(params, SEARCH_BIND_DEVICE_ACCOUNT));
            try {
                Log.e("my", jObject2.toString());
                return jObject2;
            } catch (Exception e2) {
                e = e2;
                jObject = jObject2;
                e.printStackTrace();
                return jObject;
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
            return jObject;
        }
    }

    public JSONObject getAlarmRecordList(String contactId, String sessionId, String index, int pageSize, int option) {
        Exception e;
        JSONObject jObject = null;
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("UserID", String.valueOf(Integer.parseInt(contactId) | Integer.MIN_VALUE)));
        params.add(new BasicNameValuePair("SessionID", sessionId));
        params.add(new BasicNameValuePair("MsgIndex", index));
        params.add(new BasicNameValuePair("PageSize", String.valueOf(pageSize)));
        params.add(new BasicNameValuePair("Option", String.valueOf(option)));
        try {
            JSONObject jObject2 = new JSONObject(doPost(params, ALARM_RECORD_LIST_URL));
            try {
                Log.e("my", jObject2.toString());
                return jObject2;
            } catch (Exception e2) {
                e = e2;
                jObject = jObject2;
                e.printStackTrace();
                return jObject;
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
            return jObject;
        }
    }

    public JSONObject modifyLoginPassword(String contactId, String sessionId, String oldPwd, String newPwd, String rePwd) {
        Exception e;
        JSONObject jObject = null;
        List<NameValuePair> params = new ArrayList();
        MD5 md = new MD5();
        params.add(new BasicNameValuePair("UserID", String.valueOf(Integer.parseInt(contactId) | Integer.MIN_VALUE)));
        params.add(new BasicNameValuePair("SessionID", sessionId));
        params.add(new BasicNameValuePair("OldPwd", md.getMD5ofStr(oldPwd)));
        params.add(new BasicNameValuePair("Pwd", md.getMD5ofStr(newPwd)));
        params.add(new BasicNameValuePair("RePwd", md.getMD5ofStr(rePwd)));
        try {
            JSONObject jObject2 = new JSONObject(doPost(params, MODIFY_LOGIN_PASSWORD_URL));
            try {
                Log.e("my", jObject2.toString());
                return jObject2;
            } catch (Exception e2) {
                e = e2;
                jObject = jObject2;
                e.printStackTrace();
                return jObject;
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
            return jObject;
        }
    }

    public int setAccountInfo(String contactId, String phone, String email, String countryCode, String sessionId, String password, String flag, String phoneCheckCode) {
        int result = 999;
        List<NameValuePair> params = new ArrayList();
        MD5 md = new MD5();
        params.add(new BasicNameValuePair("Opion", "SetParam"));
        params.add(new BasicNameValuePair("UserID", String.valueOf(Integer.parseInt(contactId) | Integer.MIN_VALUE)));
        params.add(new BasicNameValuePair("SessionID", sessionId));
        params.add(new BasicNameValuePair("Email", email));
        params.add(new BasicNameValuePair("CountryCode", countryCode));
        params.add(new BasicNameValuePair("PhoneNO", phone));
        params.add(new BasicNameValuePair("UserPwd", md.getMD5ofStr(password)));
        params.add(new BasicNameValuePair("BindFlag", flag));
        params.add(new BasicNameValuePair("PhoneCheckCode", phoneCheckCode));
        try {
            JSONObject jObject = new JSONObject(doPost(params, ACCOUNT_INFO_URL));
            Log.e("my", jObject.toString());
            result = jObject.getInt("error_code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public int setBindDeviceAccount(String phone, String sessionId, String deviceId, String account, String flag, String name) {
        int result = 999;
        List<NameValuePair> params = new ArrayList();
        MD5 md = new MD5();
        params.add(new BasicNameValuePair("Account", phone));
        params.add(new BasicNameValuePair("SessionID", sessionId));
        params.add(new BasicNameValuePair("DevID", deviceId));
        params.add(new BasicNameValuePair("BindAccount", account));
        params.add(new BasicNameValuePair("Level", flag));
        params.add(new BasicNameValuePair("NickName", name));
        try {
            JSONObject jObject = new JSONObject(doPost(params, BIND_DEVICE_ACCOUNT));
            Log.e("my", jObject.toString());
            result = jObject.getInt("error_code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public int modifyNikeName(String phone, String sessionId, String deviceId, String name) {
        int result = 999;
        List<NameValuePair> params = new ArrayList();
        MD5 md = new MD5();
        params.add(new BasicNameValuePair("Account", phone));
        params.add(new BasicNameValuePair("SessionID", sessionId));
        params.add(new BasicNameValuePair("DevID", deviceId));
        params.add(new BasicNameValuePair("NickName", name));
        try {
            JSONObject jObject = new JSONObject(doPost(params, MODIFY_NIKE_NAME));
            Log.e("my", jObject.toString());
            result = jObject.getInt("error_code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public int deleteBindDeviceAccount(String phone, String sessionId, String deviceId, String account) {
        int result = 999;
        List<NameValuePair> params = new ArrayList();
        MD5 md = new MD5();
        params.add(new BasicNameValuePair("Account", phone));
        params.add(new BasicNameValuePair("SessionID", sessionId));
        params.add(new BasicNameValuePair("DevID", deviceId));
        params.add(new BasicNameValuePair("BindAccount", account));
        try {
            JSONObject jObject = new JSONObject(doPost(params, REMOVE_BIND_DEVICE_ACCOUNT));
            Log.e("my", jObject.toString());
            result = jObject.getInt("error_code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public JSONObject login(String username, String password) {
        Exception e;
        if (MyUtils.isNumeric(username)) {
            username = String.valueOf(Integer.parseInt(username) | Integer.MIN_VALUE);
        }
        JSONObject jObject = null;
        List<NameValuePair> params = new ArrayList();
        MD5 md = new MD5();
        params.add(new BasicNameValuePair("User", username));
        params.add(new BasicNameValuePair("Pwd", md.getMD5ofStr(password)));
        params.add(new BasicNameValuePair("VersionFlag", "1"));
        params.add(new BasicNameValuePair("AppOS", "3"));
        params.add(new BasicNameValuePair("AppVersion", MyUtils.getBitProcessingVersion()));
        try {
            JSONObject jObject2 = new JSONObject(doPost(params, LOGIN_URL));
            try {
                Log.e("my", jObject2.toString());
                return jObject2;
            } catch (Exception e2) {
                e = e2;
                jObject = jObject2;
                e.printStackTrace();
                return jObject;
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
            return jObject;
        }
    }

    public JSONObject getAlarmMessage(String username, String sessionId, int pageSize, int option) {
        Exception e;
        JSONObject jObject = null;
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("UserID", String.valueOf(Integer.parseInt(username) | Integer.MIN_VALUE)));
        params.add(new BasicNameValuePair("SessionID", sessionId));
        params.add(new BasicNameValuePair("pageSize", String.valueOf(pageSize)));
        params.add(new BasicNameValuePair("Option", String.valueOf(option)));
        try {
            JSONObject jObject2 = new JSONObject(doPost(params, ALARM_MSSAGE_URL));
            try {
                Log.e("my", jObject2.toString());
                return jObject2;
            } catch (Exception e2) {
                e = e2;
                jObject = jObject2;
                e.printStackTrace();
                return jObject;
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
            return jObject;
        }
    }

    public int getPhoneCode(String CountryCode, String PhoneNO) {
        int result = 999;
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("CountryCode", CountryCode));
        params.add(new BasicNameValuePair("PhoneNO", PhoneNO));
        params.add(new BasicNameValuePair("AppVersion", MyUtils.getBitProcessingVersion()));
        try {
            JSONObject jObject = new JSONObject(doPost(params, GETPHONE_CODE_URL));
            Log.e("my", jObject.toString());
            result = jObject.getInt("error_code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public int verifyPhoneCode(String CountryCode, String PhoneNO, String VerifyCode) {
        int result = 999;
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("CountryCode", CountryCode));
        params.add(new BasicNameValuePair("PhoneNO", PhoneNO));
        params.add(new BasicNameValuePair("VerifyCode", VerifyCode));
        try {
            JSONObject jObject = new JSONObject(doPost(params, VERIFY_CODE_URL));
            Log.e("my", jObject.toString());
            result = jObject.getInt("error_code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public JSONObject register(String VersionFlag, String Email, String CountryCode, String PhoneNO, String password, String rePassword, String VerifyCode, String IgnoreSafeWarning) {
        MD5 md = new MD5();
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("VersionFlag", VersionFlag));
        params.add(new BasicNameValuePair("Email", Email));
        params.add(new BasicNameValuePair("CountryCode", CountryCode));
        params.add(new BasicNameValuePair("PhoneNO", PhoneNO));
        params.add(new BasicNameValuePair("Pwd", md.getMD5ofStr(password)));
        params.add(new BasicNameValuePair("RePwd", md.getMD5ofStr(rePassword)));
        params.add(new BasicNameValuePair("VerifyCode", VerifyCode));
        params.add(new BasicNameValuePair("IgnoreSafeWarning", IgnoreSafeWarning));
        try {
            return new JSONObject(doPost(params, REGISTER_URL));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int exit_application(String contactId, String sessionId) {
        int result = 999;
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("UserID", String.valueOf(Integer.parseInt(contactId) | Integer.MIN_VALUE)));
        params.add(new BasicNameValuePair("SessionID", sessionId));
        try {
            JSONObject jObject = new JSONObject(doPost(params, EXIT_APPLICATION_URL));
            result = jObject.getInt("error_code");
            Log.e("my", jObject.toString());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("my", "exit error");
            return result;
        }
    }

    public static RegisterResult createRegisterResult(JSONObject jObject) {
        return new RegisterResult(jObject);
    }

    public static GetAccountInfoResult createGetAccountInfoResult(JSONObject jObject) {
        return new GetAccountInfoResult(jObject);
    }

    public static ModifyLoginPasswordResult createModifyLoginPasswordResult(JSONObject jObject) {
        return new ModifyLoginPasswordResult(jObject);
    }

    public static LoginResult createLoginResult(JSONObject jObject) {
        return new LoginResult(jObject);
    }

    public static AlarmRecordResult getAlarmRecords(JSONObject jObject) {
        return new AlarmRecordResult(jObject);
    }

    public static GetDeviceListResult createGetDeviceListResult(JSONObject jObject) {
        return new GetDeviceListResult(jObject);
    }

    public static GetBindDeviceAccountResult createGetBindDeviceAccountResult(JSONObject jObject) {
        return new GetBindDeviceAccountResult(jObject);
    }

    public static GetAlarmRecordListResult createGetAlarmRecordListResult(JSONObject jObject) {
        return new GetAlarmRecordListResult(jObject);
    }
}
