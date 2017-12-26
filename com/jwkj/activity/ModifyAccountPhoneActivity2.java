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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.entity.Account;
import com.jwkj.global.AccountPersist;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.MyInputDialog;
import com.jwkj.widget.MyInputDialog.OnButtonOkListener;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.network.NetManager;

public class ModifyAccountPhoneActivity2 extends BaseActivity implements OnClickListener {
    public static final int CHANGE_BUTTON_TEXT = 8000;
    private String countryCode;
    NormalDialog dialog;
    MyInputDialog dialog_input;
    RelativeLayout dialog_input_mask;
    boolean isDialogCanel = false;
    private ImageView mBack;
    private Context mContext;
    private Handler mHandler = new Handler(new C04191());
    private Button mNext;
    private String phone;
    private TextView phone_view;
    private Button resend;
    private EditText verify_code;

    class C04191 implements Callback {
        C04191() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 8000:
                    int time = msg.arg1;
                    ModifyAccountPhoneActivity2.this.resend.setText(String.valueOf(time));
                    if (time == 0) {
                        ModifyAccountPhoneActivity2.this.resend.setText(C0291R.string.resend);
                        ModifyAccountPhoneActivity2.this.resend.setClickable(true);
                    }
                    if (time == 180) {
                        ModifyAccountPhoneActivity2.this.resend.setClickable(false);
                        break;
                    }
                    break;
            }
            return false;
        }
    }

    class C04202 implements OnCancelListener {
        C04202() {
        }

        public void onCancel(DialogInterface dialog) {
            ModifyAccountPhoneActivity2.this.isDialogCanel = true;
        }
    }

    class C04213 extends Thread {
        C04213() {
        }

        public void run() {
            int time = 180;
            while (time >= 0) {
                Message change = new Message();
                change.what = 8000;
                change.arg1 = time;
                ModifyAccountPhoneActivity2.this.mHandler.sendMessage(change);
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
            return Integer.valueOf(NetManager.getInstance(ModifyAccountPhoneActivity2.this.mContext).getPhoneCode(this.CountryCode, this.PhoneNO));
        }

        protected void onPostExecute(Object object) {
            switch (((Integer) object).intValue()) {
                case 6:
                    if (ModifyAccountPhoneActivity2.this.dialog != null) {
                        ModifyAccountPhoneActivity2.this.dialog.dismiss();
                        ModifyAccountPhoneActivity2.this.dialog = null;
                    }
                    if (!ModifyAccountPhoneActivity2.this.isDialogCanel) {
                        Utils.showPromptDialog(ModifyAccountPhoneActivity2.this.mContext, C0291R.string.prompt, C0291R.string.phone_number_used);
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
                    if (ModifyAccountPhoneActivity2.this.dialog != null) {
                        ModifyAccountPhoneActivity2.this.dialog.dismiss();
                        ModifyAccountPhoneActivity2.this.dialog = null;
                    }
                    ModifyAccountPhoneActivity2.this.changeButton();
                    return;
            }
        }
    }

    class SetAccountInfoTask extends AsyncTask {
        private String checkCode;
        private String countryCode;
        private String password;
        private String phone;

        public SetAccountInfoTask(String password, String phone, String countryCode, String checkCode) {
            this.password = password;
            this.phone = phone;
            this.countryCode = countryCode;
            this.checkCode = checkCode;
        }

        protected Object doInBackground(Object... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Account account = AccountPersist.getInstance().getActiveAccountInfo(ModifyAccountPhoneActivity2.this.mContext);
            return Integer.valueOf(NetManager.getInstance(ModifyAccountPhoneActivity2.this.mContext).setAccountInfo(NpcCommon.mThreeNum, this.phone, account.email, this.countryCode, account.sessionId, this.password, "1", this.checkCode));
        }

        protected void onPostExecute(Object object) {
            switch (((Integer) object).intValue()) {
                case 0:
                    if (ModifyAccountPhoneActivity2.this.dialog != null && ModifyAccountPhoneActivity2.this.dialog.isShowing()) {
                        ModifyAccountPhoneActivity2.this.dialog.dismiss();
                        ModifyAccountPhoneActivity2.this.dialog = null;
                    }
                    Account account = AccountPersist.getInstance().getActiveAccountInfo(ModifyAccountPhoneActivity2.this.mContext);
                    account.phone = this.phone;
                    account.countryCode = this.countryCode;
                    AccountPersist.getInstance().setActiveAccount(ModifyAccountPhoneActivity2.this.mContext, account);
                    C0568T.showShort(ModifyAccountPhoneActivity2.this.mContext, (int) C0291R.string.modify_success);
                    ModifyAccountPhoneActivity2.this.finish();
                    return;
                case 3:
                    if (ModifyAccountPhoneActivity2.this.dialog != null && ModifyAccountPhoneActivity2.this.dialog.isShowing()) {
                        ModifyAccountPhoneActivity2.this.dialog.dismiss();
                        ModifyAccountPhoneActivity2.this.dialog = null;
                    }
                    C0568T.showShort(ModifyAccountPhoneActivity2.this.mContext, (int) C0291R.string.password_error);
                    return;
                case 23:
                    Intent i = new Intent();
                    i.setAction(Action.SESSION_ID_ERROR);
                    MyApp.app.sendBroadcast(i);
                    return;
                case NetManager.CONNECT_CHANGE /*998*/:
                    new SetAccountInfoTask(this.password, this.phone, this.countryCode, this.checkCode).execute(new Object[0]);
                    return;
                default:
                    if (ModifyAccountPhoneActivity2.this.dialog != null && ModifyAccountPhoneActivity2.this.dialog.isShowing()) {
                        ModifyAccountPhoneActivity2.this.dialog.dismiss();
                        ModifyAccountPhoneActivity2.this.dialog = null;
                    }
                    C0568T.showShort(ModifyAccountPhoneActivity2.this.mContext, (int) C0291R.string.operator_error);
                    return;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_modify_account_phone2);
        this.mContext = this;
        this.countryCode = getIntent().getStringExtra("countryCode");
        this.phone = getIntent().getStringExtra("phone");
        initCompent();
        changeButton();
    }

    public void initCompent() {
        this.phone_view = (TextView) findViewById(C0291R.id.phone);
        this.verify_code = (EditText) findViewById(C0291R.id.verify_code);
        this.resend = (Button) findViewById(C0291R.id.resend);
        this.mBack = (ImageView) findViewById(C0291R.id.back_btn);
        this.mNext = (Button) findViewById(C0291R.id.next);
        this.phone_view.setText("+" + this.countryCode + " " + this.phone);
        this.dialog_input_mask = (RelativeLayout) findViewById(C0291R.id.dialog_input_mask);
        this.mBack.setOnClickListener(this);
        this.resend.setOnClickListener(this);
        this.mNext.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
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
        this.dialog.setOnCancelListener(new C04202());
        this.isDialogCanel = false;
        this.dialog.showDialog();
        new GetPhoneCodeTask(this.countryCode, this.phone).execute(new Object[0]);
    }

    public void checkCode() {
        String code = this.verify_code.getText().toString();
        if (code == null || code.equals("")) {
            C0568T.showShort(this.mContext, (int) C0291R.string.input_vf_code);
        } else {
            showInputPwd(this.phone, this.countryCode, code);
        }
    }

    public void changeButton() {
        new C04213().start();
    }

    public void showInputPwd(final String phone, final String countryCode, final String checkCode) {
        this.dialog_input = new MyInputDialog(this.mContext);
        this.dialog_input.setTitle(this.mContext.getResources().getString(C0291R.string.change_phone));
        this.dialog_input.setBtn1_str(this.mContext.getResources().getString(C0291R.string.ensure));
        this.dialog_input.setBtn2_str(this.mContext.getResources().getString(C0291R.string.cancel));
        this.dialog_input.setOnButtonOkListener(new OnButtonOkListener() {
            public void onClick() {
                String password = ModifyAccountPhoneActivity2.this.dialog_input.getInput1Text();
                if ("".equals(password.trim())) {
                    C0568T.showShort(ModifyAccountPhoneActivity2.this.mContext, (int) C0291R.string.input_login_pwd);
                    return;
                }
                ModifyAccountPhoneActivity2.this.dialog_input.hide(ModifyAccountPhoneActivity2.this.dialog_input_mask);
                if (ModifyAccountPhoneActivity2.this.dialog == null) {
                    ModifyAccountPhoneActivity2.this.dialog = new NormalDialog(ModifyAccountPhoneActivity2.this.mContext, ModifyAccountPhoneActivity2.this.mContext.getResources().getString(C0291R.string.verification), "", "", "");
                    ModifyAccountPhoneActivity2.this.dialog.setStyle(2);
                }
                ModifyAccountPhoneActivity2.this.dialog.showDialog();
                new SetAccountInfoTask(password, phone, countryCode, checkCode).execute(new Object[0]);
            }
        });
        this.dialog_input.show(this.dialog_input_mask);
        this.dialog_input.setInput1HintText((int) C0291R.string.input_login_pwd);
    }

    public void onBackPressed() {
        if (this.dialog_input == null || !this.dialog_input.isShowing()) {
            finish();
        } else {
            this.dialog_input.hide(this.dialog_input_mask);
        }
    }

    public int getActivityInfo() {
        return 16;
    }
}
