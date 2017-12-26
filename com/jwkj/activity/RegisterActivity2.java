package com.jwkj.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.os.StrictMode.VmPolicy;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import cn.com.streamax.miotp.p2p.jni.GWellUserInfo;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.hikam.C0291R;
import com.jwkj.global.Constants;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.MyApp;
import com.jwkj.net.CMD;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.network.NetManager;
import com.p2p.core.utils.MD5;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.json.JSONObject;

public class RegisterActivity2 extends BaseActivity implements OnClickListener {
    private String code;
    private EditText confirm_pwd;
    private Context context;
    private String count;
    NormalDialog dialog;
    private EditText email;
    private GWellUserInfo gwellUserInfo = new GWellUserInfo();
    boolean isDialogCanel = false;
    private boolean isEmailRegister;
    private RelativeLayout layout_email;
    private String phone;
    private EditText pwd;
    Button register;

    class C04371 implements OnCancelListener {
        C04371() {
        }

        public void onCancel(DialogInterface dialog) {
            RegisterActivity2.this.isDialogCanel = true;
        }
    }

    class RegisterTask2 extends AsyncTask {
        String Email;
        String Pwd;
        String RePwd;

        public RegisterTask2(String Email, String Pwd, String RePwd) {
            this.Email = Email;
            this.Pwd = Pwd;
            this.RePwd = RePwd;
        }

        protected Object doInBackground(Object... params) {
            MD5 md5 = new MD5();
            RegisterActivity2.this.gwellUserInfo = new GWellUserInfo();
            return Integer.valueOf(P2pJni.P2pClientSdkCreateNewUser(this.Email, Utils.MD5(this.Pwd), 1, RegisterActivity2.this.gwellUserInfo));
        }

        protected void onPostExecute(Object object) {
            int error_code = ((Integer) object).intValue();
            System.out.println("P2pJni.P2pClientSdkCreateNewUser finish, result = " + error_code);
            switch (error_code) {
                case -400:
                    if (RegisterActivity2.this.dialog != null) {
                        RegisterActivity2.this.dialog.dismiss();
                        RegisterActivity2.this.dialog = null;
                    }
                    if (!RegisterActivity2.this.isDialogCanel) {
                        Utils.showPromptDialog(RegisterActivity2.this.context, C0291R.string.prompt, C0291R.string.email_used);
                        return;
                    }
                    return;
                case -202:
                    new RegisterTask2(this.Email, this.Pwd, this.RePwd).execute(new Object[0]);
                    return;
                case 0:
                    System.out.println("P2pJni.P2pClientSdkCreateNewUser gwell user = " + RegisterActivity2.this.gwellUserInfo.username);
                    if (RegisterActivity2.this.dialog != null) {
                        RegisterActivity2.this.dialog.dismiss();
                        RegisterActivity2.this.dialog = null;
                    }
                    if ("".equals(RegisterActivity2.this.gwellUserInfo.username) && !RegisterActivity2.this.isDialogCanel) {
                        C0568T.showShort(RegisterActivity2.this.context, (int) C0291R.string.operator_error);
                    }
                    if (!RegisterActivity2.this.isDialogCanel) {
                        Intent i;
                        if (RegisterActivity2.this.isEmailRegister) {
                            i = new Intent();
                            i.setAction(Action.REPLACE_EMAIL_LOGIN);
                            if (this.Email.endsWith(Constants.HIKAM_EMAIL_SUFFIX)) {
                                this.Email = this.Email.substring(0, this.Email.indexOf(Constants.HIKAM_EMAIL_SUFFIX));
                            }
                            i.putExtra("username", this.Email);
                            i.putExtra("password", this.Pwd);
                            RegisterActivity2.this.context.sendBroadcast(i);
                            RegisterActivity2.this.finish();
                            return;
                        }
                        i = new Intent();
                        i.setAction(Action.REPLACE_PHONE_LOGIN);
                        i.putExtra("password", this.Pwd);
                        RegisterActivity2.this.context.sendBroadcast(i);
                        RegisterActivity2.this.finish();
                        return;
                    }
                    return;
                default:
                    if (RegisterActivity2.this.dialog != null) {
                        RegisterActivity2.this.dialog.dismiss();
                        RegisterActivity2.this.dialog = null;
                    }
                    if (!RegisterActivity2.this.isDialogCanel) {
                        C0568T.showShort(RegisterActivity2.this.context, (int) C0291R.string.operator_error);
                        return;
                    }
                    return;
            }
        }
    }

    class RegisterTask extends AsyncTask {
        String CountryCode;
        String Email;
        String IgnoreSafeWarning;
        String PhoneNO;
        String Pwd;
        String RePwd;
        String VerifyCode;
        String VersionFlag;

        public RegisterTask(String VersionFlag, String Email, String CountryCode, String PhoneNO, String Pwd, String RePwd, String VerifyCode, String IgnoreSafeWarning) {
            this.VersionFlag = VersionFlag;
            this.Email = Email;
            this.CountryCode = CountryCode;
            this.PhoneNO = PhoneNO;
            this.Pwd = Pwd;
            this.RePwd = RePwd;
            this.VerifyCode = VerifyCode;
            this.IgnoreSafeWarning = IgnoreSafeWarning;
        }

        protected Object doInBackground(Object... params) {
            Utils.sleepThread(1000);
            return NetManager.getInstance(RegisterActivity2.this.context).register(this.VersionFlag, this.Email, this.CountryCode, this.PhoneNO, this.Pwd, this.RePwd, this.VerifyCode, this.IgnoreSafeWarning);
        }

        protected void onPostExecute(Object object) {
            switch (Integer.parseInt(NetManager.createRegisterResult((JSONObject) object).error_code)) {
                case 0:
                    System.out.println("hcws_user_register start");
                    MD5 md = new MD5();
                    GWellUserInfo gwellUserInfo = new GWellUserInfo();
                    System.out.println("P2pJni.P2pClientSdkCreateNewUser finish, result = " + P2pJni.P2pClientSdkCreateNewUser(this.Email, Utils.MD5(this.Pwd), 1, gwellUserInfo));
                    System.out.println("P2pJni.P2pClientSdkCreateNewUser gwell user = " + gwellUserInfo.username);
                    System.out.println("hcws_user_register end");
                    if (RegisterActivity2.this.dialog != null) {
                        RegisterActivity2.this.dialog.dismiss();
                        RegisterActivity2.this.dialog = null;
                    }
                    if (!RegisterActivity2.this.isDialogCanel) {
                        Intent i;
                        if (RegisterActivity2.this.isEmailRegister) {
                            i = new Intent();
                            i.setAction(Action.REPLACE_EMAIL_LOGIN);
                            if (this.Email.endsWith(Constants.HIKAM_EMAIL_SUFFIX)) {
                                this.Email = this.Email.substring(0, this.Email.indexOf(Constants.HIKAM_EMAIL_SUFFIX));
                            }
                            i.putExtra("username", this.Email);
                            i.putExtra("password", this.Pwd);
                            RegisterActivity2.this.context.sendBroadcast(i);
                            RegisterActivity2.this.finish();
                            return;
                        }
                        i = new Intent();
                        i.setAction(Action.REPLACE_PHONE_LOGIN);
                        i.putExtra("username", this.PhoneNO);
                        i.putExtra("password", this.Pwd);
                        i.putExtra("code", this.CountryCode);
                        RegisterActivity2.this.context.sendBroadcast(i);
                        RegisterActivity2.this.finish();
                        return;
                    }
                    return;
                case 4:
                    if (RegisterActivity2.this.dialog != null) {
                        RegisterActivity2.this.dialog.dismiss();
                        RegisterActivity2.this.dialog = null;
                    }
                    if (!RegisterActivity2.this.isDialogCanel) {
                        Utils.showPromptDialog(RegisterActivity2.this.context, C0291R.string.prompt, C0291R.string.email_format_error);
                        return;
                    }
                    return;
                case 7:
                    if (RegisterActivity2.this.dialog != null) {
                        RegisterActivity2.this.dialog.dismiss();
                        RegisterActivity2.this.dialog = null;
                    }
                    if (!RegisterActivity2.this.isDialogCanel) {
                        Utils.showPromptDialog(RegisterActivity2.this.context, C0291R.string.prompt, C0291R.string.email_used);
                        return;
                    }
                    return;
                case 10:
                    if (RegisterActivity2.this.dialog != null) {
                        RegisterActivity2.this.dialog.dismiss();
                        RegisterActivity2.this.dialog = null;
                        return;
                    }
                    return;
                case 23:
                    Intent relogin = new Intent();
                    relogin.setAction(Action.SESSION_ID_ERROR);
                    MyApp.app.sendBroadcast(relogin);
                    return;
                case NetManager.CONNECT_CHANGE /*998*/:
                    new RegisterTask(this.VersionFlag, this.Email, this.CountryCode, this.PhoneNO, this.Pwd, this.RePwd, this.VerifyCode, this.IgnoreSafeWarning).execute(new Object[0]);
                    return;
                default:
                    if (RegisterActivity2.this.dialog != null) {
                        RegisterActivity2.this.dialog.dismiss();
                        RegisterActivity2.this.dialog = null;
                    }
                    if (!RegisterActivity2.this.isDialogCanel) {
                        C0568T.showShort(RegisterActivity2.this.context, (int) C0291R.string.operator_error);
                        return;
                    }
                    return;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.register_form2);
        this.context = this;
        this.isEmailRegister = getIntent().getBooleanExtra("isEmailRegister", false);
        if (!this.isEmailRegister) {
            this.count = getIntent().getStringExtra("count");
            this.phone = getIntent().getStringExtra("phone");
            this.code = getIntent().getStringExtra("code");
        }
        initComponent();
        StrictMode.setThreadPolicy(new Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
    }

    public void initComponent() {
        this.email = (EditText) findViewById(C0291R.id.email);
        this.pwd = (EditText) findViewById(C0291R.id.pwd);
        this.confirm_pwd = (EditText) findViewById(C0291R.id.confirm_pwd);
        this.layout_email = (RelativeLayout) findViewById(C0291R.id.layout_email);
        this.register = (Button) findViewById(C0291R.id.register);
        this.pwd.setTypeface(Typeface.DEFAULT);
        this.pwd.setTransformationMethod(new PasswordTransformationMethod());
        this.confirm_pwd.setTypeface(Typeface.DEFAULT);
        this.confirm_pwd.setTransformationMethod(new PasswordTransformationMethod());
        if (this.isEmailRegister) {
            this.layout_email.setVisibility(0);
        } else if (this.count.equals("86")) {
            this.layout_email.setVisibility(8);
        } else {
            this.layout_email.setVisibility(0);
        }
        this.register.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.register:
                register();
                return;
            default:
                return;
        }
    }

    private void register() {
        String email_str = this.email.getText().toString();
        String pwd_str = this.pwd.getText().toString();
        String confirm_pwd_str = this.confirm_pwd.getText().toString();
        if (this.isEmailRegister || !this.count.equals("86")) {
            if (email_str == null || "".equals(email_str)) {
                C0568T.showShort((Context) this, (int) C0291R.string.input_email);
                return;
            } else if (Utils.checkEmail(email_str)) {
                if (email_str.length() > 32 || email_str.length() < 5) {
                    C0568T.showShort((Context) this, (int) C0291R.string.email_too_long);
                    return;
                }
            } else if (Utils.checkUsername(email_str)) {
                email_str = email_str + Constants.HIKAM_EMAIL_SUFFIX;
            } else {
                C0568T.showShort((Context) this, (int) C0291R.string.username_format_error);
                return;
            }
        }
        if (pwd_str == null || "".equals(pwd_str)) {
            C0568T.showShort((Context) this, (int) C0291R.string.inputpassword);
        } else if (pwd_str.length() > 27) {
            C0568T.showShort((Context) this, (int) C0291R.string.password_length_error);
        } else if (confirm_pwd_str == null || "".equals(confirm_pwd_str)) {
            C0568T.showShort((Context) this, (int) C0291R.string.reinputpassword);
        } else if (pwd_str.equals(confirm_pwd_str)) {
            this.dialog = new NormalDialog(this, getResources().getString(C0291R.string.registering), "", "", "");
            this.dialog.setStyle(2);
            this.dialog.setOnCancelListener(new C04371());
            this.isDialogCanel = false;
            this.dialog.showDialog();
            if (this.isEmailRegister) {
                new RegisterTask2(email_str, pwd_str, confirm_pwd_str).execute(new Object[0]);
            } else if (this.count.equals("86")) {
                new RegisterTask("1", "", this.count, this.phone, pwd_str, confirm_pwd_str, this.code, "1").execute(new Object[0]);
            } else {
                new RegisterTask("1", email_str, this.count, this.phone, pwd_str, confirm_pwd_str, "", "1").execute(new Object[0]);
            }
        } else {
            C0568T.showShort((Context) this, (int) C0291R.string.differentpassword);
        }
    }

    private String userRegByTcp(String sendMsg) {
        String recvMsg;
        Exception e;
        Throwable th;
        byte[] buffer = new byte[1024];
        Socket csocket = null;
        InputStream input = null;
        OutputStream output = null;
        try {
            System.out.println("Socket start");
            Socket csocket2 = new Socket(Constants.HCWS_REG_SERVER_ADDR, Constants.HCWS_REG_SERVER_PORT);
            try {
                System.out.println("Socket end");
                input = csocket2.getInputStream();
                output = csocket2.getOutputStream();
                System.out.println("sendMsg : " + sendMsg);
                System.out.println("base64 : " + Base64.encodeToString(sendMsg.getBytes(), 0));
                output.write(Base64.encodeToString(sendMsg.getBytes(), 0).getBytes());
                input.read(buffer);
                recvMsg = new String(buffer).trim();
                if (output != null) {
                    try {
                        output.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        csocket = csocket2;
                    }
                }
                if (input != null) {
                    input.close();
                }
                if (csocket2 != null) {
                    csocket2.close();
                }
                csocket = csocket2;
            } catch (Exception e3) {
                e2 = e3;
                csocket = csocket2;
                try {
                    e2.printStackTrace();
                    recvMsg = null;
                    if (output != null) {
                        try {
                            output.close();
                        } catch (Exception e22) {
                            e22.printStackTrace();
                        }
                    }
                    if (input != null) {
                        input.close();
                    }
                    if (csocket != null) {
                        csocket.close();
                    }
                    return recvMsg;
                } catch (Throwable th2) {
                    th = th2;
                    if (output != null) {
                        try {
                            output.close();
                        } catch (Exception e222) {
                            e222.printStackTrace();
                            throw th;
                        }
                    }
                    if (input != null) {
                        input.close();
                    }
                    if (csocket != null) {
                        csocket.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                csocket = csocket2;
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
                if (csocket != null) {
                    csocket.close();
                }
                throw th;
            }
        } catch (Exception e4) {
            e222 = e4;
            e222.printStackTrace();
            recvMsg = null;
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }
            if (csocket != null) {
                csocket.close();
            }
            return recvMsg;
        }
        return recvMsg;
    }

    private void hcws_user_register(String username, String password) {
        String MSG_MAGIC_STRING = "hcws0755";
        String COMMAND_TYPE_REQUSET = "0";
        String COMMAND_CLIENT_USER_REG = "C1";
        String CLIENT_TYPE_PC = "PC";
        String CLIENT_TYPE_ANDROID = CMD.PHONE_TYPE;
        String CLIENT_TYPE_IOS = "iOS";
        String USER_REG_TYPE_AUTO = "AUTO";
        String USER_REG_TYPE_MANUAL = "MANUAL";
        String clientTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String clientTimezone = TimeZone.getDefault().getDisplayName(false, 0);
        String sendMsg = String.format("#%s,%s,%s,%s,%s,%s,%s,%s,%s#", new Object[]{"hcws0755", "C1", "0", CMD.PHONE_TYPE, "MANUAL", username, password, clientTime, clientTimezone});
        System.out.println("userRegByTcp start");
        userRegByTcp(sendMsg);
        System.out.println("userRegByTcp end");
    }

    public int getActivityInfo() {
        return 22;
    }
}
