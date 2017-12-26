package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.streamax.miotp.p2p.jni.GWellUserInfo;
import cn.com.streamax.miotp.p2p.jni.P2pClientID;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.hikam.C0291R;
import com.jwkj.data.DataManager;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.entity.Account;
import com.jwkj.global.AccountPersist;
import com.jwkj.global.Constants;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.MediaPlayer;
import com.p2p.core.P2PHandler;
import com.p2p.core.network.LoginResult;
import com.p2p.core.network.NetManager;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Set;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity implements OnClickListener {
    public static final int ACCOUNT_NO_EXIST = 3;
    public static final int ANIMATION_END = 2;
    public static Context mContext;
    RelativeLayout choose_country;
    int current_type;
    TextView dfault_count;
    TextView dfault_name;
    NormalDialog dialog;
    RelativeLayout dialog_remember;
    TextView forget_pwd;
    private GWellUserInfo gwellUserInfo = null;
    ImageView help_info_img;
    private boolean isDecouple = false;
    private boolean isDialogCanel = false;
    private boolean isRegFilter = false;
    private EditText mAccountName;
    private EditText mAccountPwd;
    private Handler mHandler = new Handler(new C04024());
    private String mInputName;
    private String mInputPwd;
    Button mLogin;
    private BroadcastReceiver mReceiver = new C03991();
    TextView mRegister_email;
    TextView mRegister_phone;
    Button mregister;
    private String newUser;
    TextView new_ccount_registration;
    TextView notice;
    private String oldUser;
    RelativeLayout remember_pass;
    ImageView remember_pwd_img;
    TextView title_text;
    TextView tv_Anonymous_login;
    RadioButton type_email;
    RadioButton type_phone;

    class C03991 extends BroadcastReceiver {
        C03991() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Action.REPLACE_EMAIL_LOGIN)) {
                LoginActivity.this.type_email.setChecked(true);
                LoginActivity.this.type_phone.setChecked(false);
                LoginActivity.this.choose_country.setVisibility(8);
                LoginActivity.this.mAccountName.setText(intent.getStringExtra("username"));
                LoginActivity.this.mAccountPwd.setText(intent.getStringExtra("password"));
                LoginActivity.this.current_type = 1;
                LoginActivity.this.login(true);
            } else if (intent.getAction().equals(Action.REPLACE_PHONE_LOGIN)) {
                LoginActivity.this.type_email.setChecked(false);
                LoginActivity.this.type_phone.setChecked(true);
                LoginActivity.this.choose_country.setVisibility(0);
                LoginActivity.this.mAccountName.setText(intent.getStringExtra("username"));
                LoginActivity.this.mAccountPwd.setText(intent.getStringExtra("password"));
                LoginActivity.this.dfault_count.setText("+" + intent.getStringExtra("code"));
                LoginActivity.this.current_type = 0;
                LoginActivity.this.login(false);
            } else if (intent.getAction().equals(Action.ACTION_COUNTRY_CHOOSE)) {
                String[] info = intent.getStringArrayExtra("info");
                LoginActivity.this.dfault_name.setText(info[0]);
                LoginActivity.this.dfault_count.setText("+" + info[1]);
            }
        }
    }

    class C04002 implements AnimationListener {
        C04002() {
        }

        public void onAnimationEnd(Animation arg0) {
            Message msg = new Message();
            msg.what = 2;
            LoginActivity.this.mHandler.sendMessageDelayed(msg, 500);
        }

        public void onAnimationRepeat(Animation arg0) {
        }

        public void onAnimationStart(Animation arg0) {
        }
    }

    class C04013 implements AnimationListener {
        C04013() {
        }

        public void onAnimationEnd(Animation arg0) {
            Message msg = new Message();
            msg.what = 2;
            LoginActivity.this.mHandler.sendMessageDelayed(msg, 500);
        }

        public void onAnimationRepeat(Animation arg0) {
        }

        public void onAnimationStart(Animation arg0) {
        }
    }

    class C04024 implements Callback {
        C04024() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    Animation anim_on = new ScaleAnimation(1.0f, 0.1f, 1.0f, 0.1f, 1, 0.5f, 1, 0.5f);
                    anim_on.setDuration(300);
                    LoginActivity.this.dialog_remember.startAnimation(anim_on);
                    LoginActivity.this.dialog_remember.setVisibility(8);
                    break;
                case 3:
                    C0568T.showShort(LoginActivity.mContext, (int) C0291R.string.account_no_exist);
                    if (LoginActivity.this.dialog != null) {
                        LoginActivity.this.dialog.dismiss();
                        LoginActivity.this.dialog = null;
                        break;
                    }
                    break;
            }
            return false;
        }
    }

    class C04035 implements OnCancelListener {
        C04035() {
        }

        public void onCancel(DialogInterface arg0) {
            LoginActivity.this.isDialogCanel = true;
        }
    }

    class C04046 implements Runnable {
        C04046() {
        }

        public void run() {
            if (Utils.checkEmail(LoginActivity.this.mInputName)) {
                new LoginTaskNew(LoginActivity.this.mInputName, LoginActivity.this.mInputPwd).execute(new Object[0]);
            } else if (Utils.isNumeric(LoginActivity.this.mInputName) && LoginActivity.this.mInputName.charAt(0) == '0') {
                new LoginTaskNew(LoginActivity.this.mInputName, LoginActivity.this.mInputPwd).execute(new Object[0]);
            } else {
                LoginActivity.this.mInputName = LoginActivity.this.mInputName + Constants.HIKAM_EMAIL_SUFFIX;
                new LoginTaskNew(LoginActivity.this.mInputName, LoginActivity.this.mInputPwd).execute(new Object[0]);
            }
        }
    }

    static class AsyncLoginTask extends AsyncTask {
        String password;
        String username;

        public AsyncLoginTask(String username, String password) {
            this.username = username;
            this.password = password;
            Log.e("few", "gwelluser task" + this.username);
        }

        protected Object doInBackground(Object... params) {
            Utils.sleepThread(1000);
            return NetManager.getInstance(MyApp.app).login(this.username, this.password);
        }

        protected void onPostExecute(Object object) {
            LoginResult result = NetManager.createLoginResult((JSONObject) object);
            switch (Integer.parseInt(result.error_code)) {
                case 0:
                    Log.e("a", "AsyncLoginTask LOGIN_SUCCESS");
                    String codeStr1 = String.valueOf(Long.parseLong(result.rCode1));
                    String codeStr2 = String.valueOf(Long.parseLong(result.rCode2));
                    Account account = AccountPersist.getInstance().getActiveAccountInfo(LoginActivity.mContext);
                    if (account == null) {
                        account = new Account();
                    }
                    NpcCommon.mThreeNum2 = result.contactId;
                    account.three_number2 = result.contactId;
                    account.sessionId = result.sessionId;
                    account.rCode1 = codeStr1;
                    account.rCode2 = codeStr2;
                    account.countryCode = result.countryCode;
                    AccountPersist.getInstance().setActiveAccount(LoginActivity.mContext, account);
                    int code1 = (int) Long.parseLong(account.rCode1);
                    int code2 = (int) Long.parseLong(account.rCode2);
                    MediaPlayer.getInstance().native_p2p_disconnect();
                    P2PHandler.getInstance().p2pConnect(result.contactId, code1, code2);
                    return;
                case NetManager.CONNECT_CHANGE /*998*/:
                    Log.e("a", "AsyncLoginTask CONNECT_CHANGE");
                    new AsyncLoginTask(this.username, this.password).execute(new Object[0]);
                    return;
                default:
                    return;
            }
        }
    }

    class LoginTask extends AsyncTask {
        String password;
        String username;

        public LoginTask(String username, String password) {
            this.username = username;
            this.password = password;
            Log.e("few", "gwelluser task" + this.username);
        }

        protected Object doInBackground(Object... params) {
            Utils.sleepThread(1000);
            Log.e("few", "gwelluser doInBackground this " + this.username + " - " + this.username);
            return NetManager.getInstance(LoginActivity.mContext).login(this.username, this.password);
        }

        protected void onPostExecute(Object object) {
            LoginResult result = NetManager.createLoginResult((JSONObject) object);
            switch (Integer.parseInt(result.error_code)) {
                case 0:
                    if (!LoginActivity.this.isDecouple && !LoginActivity.this.isDialogCanel) {
                        if (LoginActivity.this.dialog != null) {
                            LoginActivity.this.dialog.dismiss();
                            LoginActivity.this.dialog = null;
                        }
                        String codeStr1 = String.valueOf(Long.parseLong(result.rCode1));
                        String codeStr2 = String.valueOf(Long.parseLong(result.rCode2));
                        Account account = AccountPersist.getInstance().getActiveAccountInfo(LoginActivity.mContext);
                        if (account == null) {
                            account = new Account();
                        }
                        NpcCommon.mThreeNum2 = result.contactId;
                        account.three_number2 = result.contactId;
                        account.three_number = LoginActivity.this.newUser;
                        account.sessionId = result.sessionId;
                        account.rCode1 = codeStr1;
                        account.rCode2 = codeStr2;
                        account.countryCode = result.countryCode;
                        AccountPersist.getInstance().setActiveAccount(LoginActivity.mContext, account);
                        NpcCommon.mThreeNum = AccountPersist.getInstance().getActiveAccountInfo(LoginActivity.mContext).three_number;
                        LoginActivity.this.oldUser = result.contactId;
                        Log.e("few", "new2" + LoginActivity.this.newUser + " old" + LoginActivity.this.oldUser);
                        LoginActivity.this.decouple();
                        return;
                    }
                    return;
                case 2:
                    if (LoginActivity.this.dialog != null) {
                        LoginActivity.this.dialog.dismiss();
                        LoginActivity.this.dialog = null;
                    }
                    if (!LoginActivity.this.isDialogCanel) {
                        C0568T.showShort(LoginActivity.mContext, (int) C0291R.string.account_no_exist);
                        return;
                    }
                    return;
                case 3:
                    if (LoginActivity.this.dialog != null) {
                        LoginActivity.this.dialog.dismiss();
                        LoginActivity.this.dialog = null;
                    }
                    if (!LoginActivity.this.isDialogCanel) {
                        C0568T.showShort(LoginActivity.mContext, (int) C0291R.string.password_error);
                        return;
                    }
                    return;
                case NetManager.CONNECT_CHANGE /*998*/:
                    new LoginTask(this.username, this.password).execute(new Object[0]);
                    return;
                default:
                    C0568T.showShort(LoginActivity.mContext, (int) C0291R.string.loginfail);
                    if (!LoginActivity.this.isDialogCanel && LoginActivity.this.dialog != null) {
                        LoginActivity.this.dialog.dismiss();
                        LoginActivity.this.dialog = null;
                        return;
                    }
                    return;
            }
        }
    }

    class LoginTaskNew extends AsyncTask {
        String password;
        String username;

        public LoginTaskNew(String username, String password) {
            this.username = username;
            this.password = password;
        }

        protected Object doInBackground(Object... params) {
            P2pClientID client = new P2pClientID();
            client.account = this.username;
            client.passwd = Utils.MD5(this.password);
            String localip = LoginActivity.this.getLocalIP();
            String confPath = LoginActivity.mContext.getExternalFilesDir(null).getPath();
            Log.i("alex", "P2pJni.P2pClientSdkInit start");
            P2pJni.P2pClientSdkUnInit();
            Log.i("alex", "P2pJni.P2pClientSdkInit finish, result = " + P2pJni.P2pClientSdkInit(client, localip, confPath));
            Log.i("Register", "P2pJni.P2pClientSdkRegister start");
            LoginActivity.this.gwellUserInfo = new GWellUserInfo();
            return Integer.valueOf(P2pJni.P2pClientSdkRegister(1, LoginActivity.this.gwellUserInfo));
        }

        protected void onPostExecute(Object object) {
            Log.i("Register", "P2pJni.P2pClientSdkRegister finish, result = " + ((Integer) object));
            switch (((Integer) object).intValue()) {
                case -402:
                    if (LoginActivity.this.dialog != null) {
                        LoginActivity.this.dialog.dismiss();
                        LoginActivity.this.dialog = null;
                    }
                    if (!LoginActivity.this.isDialogCanel) {
                        C0568T.showShort(LoginActivity.mContext, (int) C0291R.string.account_no_exist);
                        return;
                    }
                    return;
                case -401:
                    if (LoginActivity.this.dialog != null) {
                        LoginActivity.this.dialog.dismiss();
                        LoginActivity.this.dialog = null;
                    }
                    if (!LoginActivity.this.isDialogCanel) {
                        C0568T.showShort(LoginActivity.mContext, (int) C0291R.string.password_error);
                        return;
                    }
                    return;
                case -202:
                    new LoginTaskNew(this.username, this.password).execute(new Object[0]);
                    return;
                case -3:
                    new LoginTaskNew(this.username, this.password).execute(new Object[0]);
                    return;
                case 0:
                    Log.i("Register", "P2pJni.P2pClientSdkRegister gwellUserInfo.username = " + LoginActivity.this.gwellUserInfo.username);
                    if (LoginActivity.this.current_type == 0) {
                        SharedPreferencesManager.getInstance().putData(LoginActivity.mContext, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTNAME, LoginActivity.this.mInputName);
                        SharedPreferencesManager.getInstance().putData(LoginActivity.mContext, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTPASS, LoginActivity.this.mInputPwd);
                        String code = LoginActivity.this.dfault_count.getText().toString();
                        SharedPreferencesManager.getInstance().putData(LoginActivity.mContext, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTCODE, code.substring(1, code.length()));
                        SharedPreferencesManager.getInstance().putRecentLoginType(LoginActivity.mContext, 0);
                    } else {
                        SharedPreferencesManager.getInstance().putData(LoginActivity.mContext, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTNAME_EMAIL, LoginActivity.this.mInputName);
                        SharedPreferencesManager.getInstance().putData(LoginActivity.mContext, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTPASS_EMAIL, LoginActivity.this.mInputPwd);
                        SharedPreferencesManager.getInstance().putRecentLoginType(LoginActivity.mContext, 1);
                    }
                    Set<String> set = SharedPreferencesManager.getInstance().getDecoupleUser(LoginActivity.mContext);
                    if (set != null) {
                        for (String s : set) {
                            if (this.username.equalsIgnoreCase(s)) {
                                LoginActivity.this.isDecouple = true;
                            }
                        }
                    }
                    LoginActivity.this.newUser = this.username;
                    Log.e("few", "new " + LoginActivity.this.newUser + " olduser" + LoginActivity.this.oldUser);
                    if (LoginActivity.this.isDecouple) {
                        Log.e("few", "had decouple");
                        if (!LoginActivity.this.isDialogCanel) {
                            if (LoginActivity.this.dialog != null) {
                                LoginActivity.this.dialog.dismiss();
                                LoginActivity.this.dialog = null;
                            }
                            if (LoginActivity.this.gwellUserInfo.username != null && LoginActivity.this.gwellUserInfo.username.length() > 0) {
                                if (LoginActivity.this.gwellUserInfo.username.equals(this.username)) {
                                    new AsyncLoginTask(LoginActivity.this.gwellUserInfo.username, this.password).execute(new Object[0]);
                                } else {
                                    new AsyncLoginTask(LoginActivity.this.gwellUserInfo.username, "123456789").execute(new Object[0]);
                                }
                            }
                            Account account = AccountPersist.getInstance().getActiveAccountInfo(LoginActivity.mContext);
                            if (account == null) {
                                account = new Account();
                            }
                            account.email = this.username;
                            account.three_number = this.username;
                            account.three_number2 = NpcCommon.mThreeNum2;
                            account.rCode1 = "0";
                            account.rCode2 = "0";
                            AccountPersist.getInstance().setActiveAccount(LoginActivity.mContext, account);
                            NpcCommon.mThreeNum = AccountPersist.getInstance().getActiveAccountInfo(LoginActivity.mContext).three_number;
                            LoginActivity.this.startActivity(new Intent(LoginActivity.mContext, MainActivity.class));
                            ((LoginActivity) LoginActivity.mContext).finish();
                            return;
                        }
                        return;
                    } else if (LoginActivity.this.gwellUserInfo.username.equals(this.username) || LoginActivity.this.gwellUserInfo.username == null || "".equalsIgnoreCase(LoginActivity.this.gwellUserInfo.username)) {
                        new LoginTask(this.username, this.password).execute(new Object[0]);
                        return;
                    } else {
                        new LoginTask(LoginActivity.this.gwellUserInfo.username, "123456789").execute(new Object[0]);
                        return;
                    }
                default:
                    if (LoginActivity.this.dialog != null) {
                        LoginActivity.this.dialog.dismiss();
                        LoginActivity.this.dialog = null;
                    }
                    if (!LoginActivity.this.isDialogCanel) {
                        C0568T.showShort(LoginActivity.mContext, (int) C0291R.string.loginfail);
                        return;
                    }
                    return;
            }
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(C0291R.layout.activity_login);
        mContext = this;
        initComponent();
        this.current_type = 1;
        this.type_email.setChecked(true);
        regFilter();
        initRememberPass();
    }

    public void initComponent() {
        this.title_text = (TextView) findViewById(C0291R.id.title_text);
        this.mLogin = (Button) findViewById(C0291R.id.login);
        this.mregister = (Button) findViewById(C0291R.id.register);
        this.mAccountName = (EditText) findViewById(C0291R.id.phone_number);
        this.mAccountPwd = (EditText) findViewById(C0291R.id.password);
        this.dialog_remember = (RelativeLayout) findViewById(C0291R.id.dialog_remember);
        this.remember_pass = (RelativeLayout) findViewById(C0291R.id.remember_pass);
        this.remember_pwd_img = (ImageView) findViewById(C0291R.id.remember_pwd_img);
        this.help_info_img = (ImageView) findViewById(C0291R.id.help_info_img);
        this.dfault_name = (TextView) findViewById(C0291R.id.name);
        this.dfault_count = (TextView) findViewById(C0291R.id.count);
        this.choose_country = (RelativeLayout) findViewById(C0291R.id.country_layout);
        this.type_phone = (RadioButton) findViewById(C0291R.id.type_phone);
        this.type_email = (RadioButton) findViewById(C0291R.id.type_email);
        this.forget_pwd = (TextView) findViewById(C0291R.id.forget_pwd);
        this.forget_pwd.getPaint().setFlags(8);
        this.tv_Anonymous_login = (TextView) findViewById(C0291R.id.tv_Anonymous_login);
        this.notice = (TextView) findViewById(C0291R.id.tv_notice);
        this.new_ccount_registration = (TextView) findViewById(C0291R.id.tv_new_ccount_registration);
        this.mAccountPwd.setTypeface(Typeface.DEFAULT);
        this.mAccountPwd.setTransformationMethod(new PasswordTransformationMethod());
        this.forget_pwd.setOnClickListener(this);
        this.type_phone.setOnClickListener(this);
        this.type_email.setOnClickListener(this);
        this.choose_country.setOnClickListener(this);
        this.mLogin.setOnClickListener(this);
        this.mregister.setOnClickListener(this);
        this.remember_pass.setOnClickListener(this);
        this.tv_Anonymous_login.setOnClickListener(this);
        this.help_info_img.setOnClickListener(this);
        this.notice.setOnClickListener(this);
        this.new_ccount_registration.setOnClickListener(this);
    }

    public void initRememberPass() {
        String recentName = "";
        String recentPwd = "";
        String recentCode = "";
        if (this.current_type == 0) {
            recentName = SharedPreferencesManager.getInstance().getData(mContext, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTNAME);
            recentPwd = SharedPreferencesManager.getInstance().getData(mContext, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTPASS);
            recentCode = SharedPreferencesManager.getInstance().getData(mContext, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTCODE);
            if (recentName.equals("")) {
                this.mAccountName.setText("");
            } else {
                this.mAccountName.setText(recentName);
            }
            if (!recentCode.equals("")) {
                this.dfault_count.setText("+" + recentCode);
                this.dfault_name.setText(SearchListActivity.getNameByCode(mContext, Integer.parseInt(recentCode)));
            } else if (getResources().getConfiguration().locale.getCountry().equals("TW")) {
                this.dfault_count.setText("+886");
                this.dfault_name.setText(SearchListActivity.getNameByCode(mContext, 886));
            } else if (getResources().getConfiguration().locale.getCountry().equals("CN")) {
                this.dfault_count.setText("+86");
                this.dfault_name.setText(SearchListActivity.getNameByCode(mContext, 86));
            } else {
                this.dfault_count.setText("+1");
                this.dfault_name.setText(SearchListActivity.getNameByCode(mContext, 1));
            }
            if (SharedPreferencesManager.getInstance().getIsRememberPass(mContext)) {
                this.remember_pwd_img.setImageResource(C0291R.drawable.ic_remember_pwd);
                if (recentPwd.equals("")) {
                    this.mAccountPwd.setText("");
                    return;
                } else {
                    this.mAccountPwd.setText(recentPwd);
                    return;
                }
            }
            this.remember_pwd_img.setImageResource(C0291R.drawable.ic_unremember_pwd);
            this.mAccountPwd.setText("");
            return;
        }
        recentName = SharedPreferencesManager.getInstance().getData(mContext, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTNAME_EMAIL);
        recentPwd = SharedPreferencesManager.getInstance().getData(mContext, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTPASS_EMAIL);
        if (recentName.equals("")) {
            this.mAccountName.setText("");
        } else {
            if (recentName.endsWith(Constants.HIKAM_EMAIL_SUFFIX)) {
                recentName = recentName.substring(0, recentName.indexOf(Constants.HIKAM_EMAIL_SUFFIX));
            }
            this.mAccountName.setText(recentName);
        }
        if (SharedPreferencesManager.getInstance().getIsRememberPass_email(mContext)) {
            this.remember_pwd_img.setImageResource(C0291R.drawable.ic_remember_pwd);
            if (recentPwd.equals("")) {
                this.mAccountPwd.setText("");
                return;
            } else {
                this.mAccountPwd.setText(recentPwd);
                return;
            }
        }
        this.remember_pwd_img.setImageResource(C0291R.drawable.ic_unremember_pwd);
        this.mAccountPwd.setText("");
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.REPLACE_EMAIL_LOGIN);
        filter.addAction(Action.REPLACE_PHONE_LOGIN);
        filter.addAction(Action.ACTION_COUNTRY_CHOOSE);
        mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case C0291R.id.help_info_img:
                new NormalDialog(mContext).showHelpInfoDialog(mContext.getResources().getString(C0291R.string.login_show_info));
                return;
            case C0291R.id.login:
                login(false);
                return;
            case C0291R.id.register:
                intent = new Intent(mContext, RegisterActivity2.class);
                intent.putExtra("isEmailRegister", true);
                startActivity(intent);
                return;
            case C0291R.id.remember_pass:
                boolean isChecked;
                if (this.current_type == 0) {
                    isChecked = SharedPreferencesManager.getInstance().getIsRememberPass(mContext);
                } else {
                    isChecked = SharedPreferencesManager.getInstance().getIsRememberPass_email(mContext);
                }
                TextView dialog_text;
                Animation anim;
                if (isChecked) {
                    dialog_text = (TextView) this.dialog_remember.findViewById(C0291R.id.dialog_text);
                    ((ImageView) this.dialog_remember.findViewById(C0291R.id.dialog_img)).setImageResource(C0291R.drawable.ic_unremember_pwd);
                    dialog_text.setText(C0291R.string.un_rem_pass);
                    dialog_text.setGravity(17);
                    this.dialog_remember.setVisibility(0);
                    anim = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, 1, 0.5f, 1, 0.5f);
                    anim.setDuration(200);
                    anim.setAnimationListener(new C04002());
                    this.dialog_remember.startAnimation(anim);
                    if (this.current_type == 0) {
                        SharedPreferencesManager.getInstance().putIsRememberPass(mContext, false);
                    } else {
                        SharedPreferencesManager.getInstance().putIsRememberPass_email(mContext, false);
                    }
                    this.remember_pwd_img.setImageResource(C0291R.drawable.ic_unremember_pwd);
                    return;
                }
                dialog_text = (TextView) this.dialog_remember.findViewById(C0291R.id.dialog_text);
                ((ImageView) this.dialog_remember.findViewById(C0291R.id.dialog_img)).setImageResource(C0291R.drawable.ic_remember_pwd);
                dialog_text.setText(C0291R.string.rem_pass);
                dialog_text.setGravity(17);
                this.dialog_remember.setVisibility(0);
                anim = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, 1, 0.5f, 1, 0.5f);
                anim.setDuration(200);
                anim.setAnimationListener(new C04013());
                this.dialog_remember.startAnimation(anim);
                if (this.current_type == 0) {
                    SharedPreferencesManager.getInstance().putIsRememberPass(mContext, true);
                } else {
                    SharedPreferencesManager.getInstance().putIsRememberPass_email(mContext, true);
                }
                this.remember_pwd_img.setImageResource(C0291R.drawable.ic_remember_pwd);
                return;
            case C0291R.id.tv_Anonymous_login:
                Account account = AccountPersist.getInstance().getActiveAccountInfo(mContext);
                if (account == null) {
                    account = new Account();
                }
                account.three_number = "517400";
                account.rCode1 = "0";
                account.rCode2 = "0";
                account.sessionId = "0";
                AccountPersist.getInstance().setActiveAccount(mContext, account);
                NpcCommon.mThreeNum = AccountPersist.getInstance().getActiveAccountInfo(mContext).three_number;
                startActivity(new Intent(mContext, MainActivity.class));
                ((LoginActivity) mContext).finish();
                return;
            case C0291R.id.tv_new_ccount_registration:
                intent = new Intent(mContext, RegisterActivity2.class);
                intent.putExtra("isEmailRegister", true);
                startActivity(intent);
                return;
            case C0291R.id.tv_notice:
                new NormalDialog(mContext).showHelpInfoDialog(mContext.getResources().getString(C0291R.string.login_show_info));
                return;
            default:
                return;
        }
    }

    public void onBackPressed() {
        super.isGoExit(true);
        finish();
    }

    private void login(boolean isDelay) {
        this.mInputName = this.mAccountName.getText().toString().trim();
        this.mInputPwd = this.mAccountPwd.getText().toString().trim();
        if (this.mInputName == null || this.mInputName.equals("") || this.mInputPwd == null || this.mInputPwd.equals("")) {
            if ((this.mInputName == null || this.mInputName.equals("")) && this.mInputPwd != null && !this.mInputPwd.equals("")) {
                C0568T.showShort(mContext, (int) C0291R.string.input_account);
            } else if (this.mInputName == null || this.mInputName.equals("") || !(this.mInputPwd == null || this.mInputPwd.equals(""))) {
                C0568T.showShort(mContext, (int) C0291R.string.input_tip);
            } else {
                C0568T.showShort(mContext, (int) C0291R.string.input_password);
            }
        } else if (this.dialog == null || !this.dialog.isShowing()) {
            this.dialog = new NormalDialog(mContext);
            this.dialog.setOnCancelListener(new C04035());
            this.dialog.setTitle(mContext.getResources().getString(C0291R.string.login_ing));
            this.dialog.showLoadingDialog();
            this.dialog.setCanceledOnTouchOutside(false);
            this.isDialogCanel = false;
            if (this.current_type == 0) {
                new LoginTask(this.dfault_count.getText().toString() + "-" + this.mInputName, this.mInputPwd).execute(new Object[0]);
            } else if (isDelay) {
                new Handler().postDelayed(new C04046(), 3000);
            } else if (Utils.checkEmail(this.mInputName)) {
                new LoginTaskNew(this.mInputName, this.mInputPwd).execute(new Object[0]);
            } else if (Utils.isNumeric(this.mInputName) && this.mInputName.charAt(0) == '0') {
                new LoginTaskNew(this.mInputName, this.mInputPwd).execute(new Object[0]);
            } else {
                this.mInputName += Constants.HIKAM_EMAIL_SUFFIX;
                new LoginTaskNew(this.mInputName, this.mInputPwd).execute(new Object[0]);
            }
        } else {
            Log.e("my", "isShowing");
        }
    }

    private boolean ipCheck(String text) {
        if (text == null || text.isEmpty() || !text.matches("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$")) {
            return false;
        }
        return true;
    }

    private boolean isWifiConnected() {
        NetworkInfo mWiFiNetworkInfo = ((ConnectivityManager) getSystemService("connectivity")).getNetworkInfo(1);
        if (mWiFiNetworkInfo != null) {
            return mWiFiNetworkInfo.isConnected();
        }
        return false;
    }

    private String getLocalIP() {
        if (isWifiConnected()) {
            int ipAddress = ((WifiManager) getApplicationContext().getSystemService("wifi")).getConnectionInfo().getIpAddress();
            if (ipAddress == 0) {
                return "0.0.0.0";
            }
            return (ipAddress & 255) + "." + ((ipAddress >> 8) & 255) + "." + ((ipAddress >> 16) & 255) + "." + ((ipAddress >> 24) & 255);
        }
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                Enumeration<InetAddress> enumIpAddr = ((NetworkInterface) en.nextElement()).getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        String text = inetAddress.getHostAddress().toString();
                        if (ipCheck(text)) {
                            return text;
                        }
                    }
                }
            }
        } catch (SocketException e) {
        }
        return "0.0.0.0";
    }

    private void decouple() {
        Animation anim = AnimationUtils.loadAnimation(this, C0291R.anim.abc_slide_in_bottom);
        RelativeLayout rl = (RelativeLayout) findViewById(C0291R.id.rl_decouple);
        rl.setVisibility(0);
        rl.setAnimation(anim);
        rl.setOnClickListener(this);
        anim.start();
        solveSharedPreference();
        final NumberProgressBar pb = (NumberProgressBar) findViewById(C0291R.id.pb);
        new CountDownTimer(10000, 100) {
            public void onTick(long millisUntilFinished) {
                pb.setProgress((int) ((10000 - millisUntilFinished) / 100));
            }

            public void onFinish() {
                pb.setProgress(100);
                LoginActivity.this.startActivity(new Intent(LoginActivity.mContext, MainActivity.class));
                ((LoginActivity) LoginActivity.mContext).finish();
            }
        }.start();
        Log.e("few", "system start : " + System.currentTimeMillis());
        DataManager.deCouple(mContext, this.oldUser, this.newUser);
        Log.e("few", "system end : " + System.currentTimeMillis());
    }

    private void solveSharedPreference() {
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.isRegFilter = false;
            mContext.unregisterReceiver(this.mReceiver);
        }
    }

    public int getActivityInfo() {
        return 2;
    }
}
