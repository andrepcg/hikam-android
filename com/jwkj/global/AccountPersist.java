package com.jwkj.global;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.jwkj.entity.Account;
import com.jwkj.utils.MusicManger;

public class AccountPersist {
    public static final String ACCOUNT_SHARED_PREFERENCE = "account_shared";
    public static final String ACC_INFO_3C = "account_info_3c";
    public static final String ACC_INFO_COUNTRY_CODE = "account_info_country_code";
    public static final String ACC_INFO_EMAIL = "account_info_email";
    public static final String ACC_INFO_PHONE = "account_info_phone";
    public static final String ACC_INFO_SESSION_ID = "account_info_session_id";
    public static final String ACTIVE = "active";
    public static final String CODE1 = "code1";
    public static final String CODE2 = "code2";
    public static final String IS_LOGIN = "active";
    public static String mACCOUNTPWD = "account_pwd";
    public static AccountPersist manager = null;

    private AccountPersist() {
    }

    public static synchronized AccountPersist getInstance() {
        AccountPersist accountPersist;
        synchronized (AccountPersist.class) {
            if (manager == null) {
                synchronized (MusicManger.class) {
                    if (manager == null) {
                        manager = new AccountPersist();
                    }
                }
            }
            accountPersist = manager;
        }
        return accountPersist;
    }

    public void setLogin(Context context, boolean isLogin) {
        Editor editor = context.getSharedPreferences(ACCOUNT_SHARED_PREFERENCE, 0).edit();
        editor.putBoolean("active", isLogin);
        editor.commit();
    }

    public boolean getLogin(Context context) {
        SharedPreferences preference = context.getSharedPreferences(ACCOUNT_SHARED_PREFERENCE, 0);
        Editor editor = preference.edit();
        boolean isLogin = preference.getBoolean("active", false);
        editor.commit();
        return isLogin;
    }

    public void setActiveAccount(Context context, Account account) {
        Editor editor = context.getSharedPreferences(ACCOUNT_SHARED_PREFERENCE, 0).edit();
        editor.putString(ACC_INFO_3C, account.three_number);
        editor.putString("acc_info_3c2", account.three_number2);
        editor.putString(ACC_INFO_PHONE, account.phone);
        editor.putString(ACC_INFO_EMAIL, account.email);
        editor.putString(ACC_INFO_SESSION_ID, account.sessionId);
        editor.putString(CODE1, account.rCode1);
        editor.putString(CODE2, account.rCode2);
        editor.putString(ACC_INFO_COUNTRY_CODE, account.countryCode);
        editor.commit();
    }

    public Account getActiveAccountInfo(Context context) {
        SharedPreferences preference = context.getSharedPreferences(ACCOUNT_SHARED_PREFERENCE, 0);
        Editor editor = preference.edit();
        String three_number = preference.getString(ACC_INFO_3C, "");
        String three_number2 = preference.getString("acc_info_3c2", "");
        String phone = preference.getString(ACC_INFO_PHONE, "");
        String email = preference.getString(ACC_INFO_EMAIL, "");
        String sessionId = preference.getString(ACC_INFO_SESSION_ID, "");
        String code1 = preference.getString(CODE1, "");
        String code2 = preference.getString(CODE2, "");
        String countryCode = preference.getString(ACC_INFO_COUNTRY_CODE, "");
        editor.commit();
        if (three_number.equals("")) {
            return null;
        }
        return new Account(three_number, three_number2, email, phone, sessionId, code1, code2, countryCode);
    }
}
