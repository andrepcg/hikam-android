package com.jwkj.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.MyApp;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.network.NetManager;

public class VerifyPhoneActivity extends BaseActivity implements OnClickListener {
    public static final int CHANGE_BUTTON_TEXT = 8000;
    private String count;
    NormalDialog dialog;
    boolean isDialogCanel = false;
    private Context mContext;
    private Handler mHandler = new Handler(new C04581());
    private Button next;
    private String phone;
    private TextView phone_view;
    private Button resend;
    private EditText verify_code;

    class C04581 implements Callback {
        C04581() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 8000:
                    int time = msg.arg1;
                    VerifyPhoneActivity.this.resend.setText(String.valueOf(time));
                    if (time == 0) {
                        VerifyPhoneActivity.this.resend.setText(C0291R.string.resend);
                        VerifyPhoneActivity.this.resend.setClickable(true);
                    }
                    if (time == 180) {
                        VerifyPhoneActivity.this.resend.setClickable(false);
                        break;
                    }
                    break;
            }
            return false;
        }
    }

    class C04592 implements OnCancelListener {
        C04592() {
        }

        public void onCancel(DialogInterface dialog) {
            VerifyPhoneActivity.this.isDialogCanel = true;
        }
    }

    class C04603 implements OnCancelListener {
        C04603() {
        }

        public void onCancel(DialogInterface dialog) {
            VerifyPhoneActivity.this.isDialogCanel = true;
        }
    }

    class C04614 extends Thread {
        C04614() {
        }

        public void run() {
            int time = 180;
            while (time >= 0) {
                Message change = new Message();
                change.what = 8000;
                change.arg1 = time;
                VerifyPhoneActivity.this.mHandler.sendMessage(change);
                time--;
                Utils.sleepThread(1000);
            }
        }
    }

    class GetPhoneCodeTask extends AsyncTask {
        String CountryCode;
        String PhoneNO;

        public GetPhoneCodeTask(String CountryCode, String PhoneNO) {
            this.CountryCode = CountryCode;
            this.PhoneNO = PhoneNO;
        }

        protected Object doInBackground(Object... params) {
            Utils.sleepThread(1000);
            return Integer.valueOf(NetManager.getInstance(VerifyPhoneActivity.this.mContext).getPhoneCode(this.CountryCode, this.PhoneNO));
        }

        protected void onPostExecute(Object object) {
            switch (((Integer) object).intValue()) {
                case 6:
                    if (VerifyPhoneActivity.this.dialog != null) {
                        VerifyPhoneActivity.this.dialog.dismiss();
                        VerifyPhoneActivity.this.dialog = null;
                    }
                    if (!VerifyPhoneActivity.this.isDialogCanel) {
                        Utils.showPromptDialog(VerifyPhoneActivity.this.mContext, C0291R.string.prompt, C0291R.string.phone_number_used);
                        return;
                    }
                    return;
                case 23:
                    Intent i = new Intent();
                    i.setAction(Action.SESSION_ID_ERROR);
                    MyApp.app.sendBroadcast(i);
                    return;
                case NetManager.CONNECT_CHANGE /*998*/:
                    new GetPhoneCodeTask(this.CountryCode, this.PhoneNO).execute(new Object[0]);
                    return;
                default:
                    if (VerifyPhoneActivity.this.dialog != null) {
                        VerifyPhoneActivity.this.dialog.dismiss();
                        VerifyPhoneActivity.this.dialog = null;
                    }
                    VerifyPhoneActivity.this.changeButton();
                    return;
            }
        }
    }

    class VerifyCodeTask extends AsyncTask {
        String code;
        String countryCode;
        String phoneNO;

        public VerifyCodeTask(String countryCode, String phoneNO, String code) {
            this.countryCode = countryCode;
            this.phoneNO = phoneNO;
            this.code = code;
        }

        protected Object doInBackground(Object... params) {
            Utils.sleepThread(1000);
            return Integer.valueOf(NetManager.getInstance(VerifyPhoneActivity.this.mContext).verifyPhoneCode(this.countryCode, this.phoneNO, this.code));
        }

        protected void onPostExecute(Object object) {
            switch (((Integer) object).intValue()) {
                case 0:
                    if (VerifyPhoneActivity.this.dialog != null) {
                        VerifyPhoneActivity.this.dialog.dismiss();
                        VerifyPhoneActivity.this.dialog = null;
                    }
                    if (!VerifyPhoneActivity.this.isDialogCanel) {
                        Intent i = new Intent(VerifyPhoneActivity.this.mContext, RegisterActivity2.class);
                        i.putExtra("phone", VerifyPhoneActivity.this.phone);
                        i.putExtra("count", VerifyPhoneActivity.this.count);
                        i.putExtra("code", this.code);
                        VerifyPhoneActivity.this.startActivity(i);
                        VerifyPhoneActivity.this.finish();
                        return;
                    }
                    return;
                case 18:
                    if (VerifyPhoneActivity.this.dialog != null) {
                        VerifyPhoneActivity.this.dialog.dismiss();
                        VerifyPhoneActivity.this.dialog = null;
                    }
                    if (!VerifyPhoneActivity.this.isDialogCanel) {
                        Utils.showPromptDialog(VerifyPhoneActivity.this.mContext, C0291R.string.prompt, C0291R.string.vfcode_error);
                        return;
                    }
                    return;
                case 21:
                    if (VerifyPhoneActivity.this.dialog != null) {
                        VerifyPhoneActivity.this.dialog.dismiss();
                        VerifyPhoneActivity.this.dialog = null;
                    }
                    if (!VerifyPhoneActivity.this.isDialogCanel) {
                        Utils.showPromptDialog(VerifyPhoneActivity.this.mContext, C0291R.string.prompt, C0291R.string.vfcode_timeout);
                        return;
                    }
                    return;
                case 23:
                    Intent relogin = new Intent();
                    relogin.setAction(Action.SESSION_ID_ERROR);
                    MyApp.app.sendBroadcast(relogin);
                    return;
                case NetManager.CONNECT_CHANGE /*998*/:
                    new VerifyCodeTask(this.countryCode, this.phoneNO, this.code).execute(new Object[0]);
                    return;
                default:
                    if (VerifyPhoneActivity.this.dialog != null) {
                        VerifyPhoneActivity.this.dialog.dismiss();
                        VerifyPhoneActivity.this.dialog = null;
                    }
                    if (!VerifyPhoneActivity.this.isDialogCanel) {
                        C0568T.showShort(VerifyPhoneActivity.this.mContext, (int) C0291R.string.operator_error);
                        return;
                    }
                    return;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.verify_phone);
        this.mContext = this;
        this.count = getIntent().getStringExtra("count");
        this.phone = getIntent().getStringExtra("phone");
        initCompent();
        changeButton();
    }

    public void initCompent() {
        this.phone_view = (TextView) findViewById(C0291R.id.phone);
        this.verify_code = (EditText) findViewById(C0291R.id.verify_code);
        this.resend = (Button) findViewById(C0291R.id.resend);
        this.next = (Button) findViewById(C0291R.id.next);
        this.phone_view.setText("+" + this.count + " " + this.phone);
        this.resend.setOnClickListener(this);
        this.next.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.next:
                checkCode();
                return;
            case C0291R.id.resend:
                resendCode();
                return;
            default:
                return;
        }
    }

    public void resendCode() {
        this.dialog = new NormalDialog(this, getResources().getString(C0291R.string.waiting_verify_code), "", "", "");
        this.dialog.setStyle(2);
        this.dialog.setOnCancelListener(new C04592());
        this.isDialogCanel = false;
        this.dialog.showDialog();
        new GetPhoneCodeTask(this.count, this.phone).execute(new Object[0]);
    }

    public void checkCode() {
        String code = this.verify_code.getText().toString();
        if (code == null || code.equals("")) {
            C0568T.showShort(this.mContext, (int) C0291R.string.input_vf_code);
            return;
        }
        this.dialog = new NormalDialog(this, getResources().getString(C0291R.string.verifing), "", "", "");
        this.dialog.setStyle(2);
        this.dialog.setOnCancelListener(new C04603());
        this.isDialogCanel = false;
        this.dialog.showDialog();
        new VerifyCodeTask(this.count, this.phone, code).execute(new Object[0]);
    }

    public void changeButton() {
        new C04614().start();
    }

    public int getActivityInfo() {
        return 31;
    }
}
