package com.jwkj.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
import com.jwkj.widget.MyInputDialog;
import com.jwkj.widget.MyInputDialog.OnButtonOkListener;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.network.NetManager;

public class UnbindPhoneActivity extends BaseActivity implements OnClickListener {
    ImageView back_btn;
    NormalDialog dialog;
    MyInputDialog dialog_input;
    RelativeLayout dialog_input_mask;
    Context mContext;
    TextView phone_text;
    Button unbind;

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
            Account account = AccountPersist.getInstance().getActiveAccountInfo(UnbindPhoneActivity.this.mContext);
            return Integer.valueOf(NetManager.getInstance(UnbindPhoneActivity.this.mContext).setAccountInfo(NpcCommon.mThreeNum, this.phone, account.email, this.countryCode, account.sessionId, this.password, "1", this.checkCode));
        }

        protected void onPostExecute(Object object) {
            switch (((Integer) object).intValue()) {
                case 0:
                    if (UnbindPhoneActivity.this.dialog != null && UnbindPhoneActivity.this.dialog.isShowing()) {
                        UnbindPhoneActivity.this.dialog.dismiss();
                        UnbindPhoneActivity.this.dialog = null;
                    }
                    Account account = AccountPersist.getInstance().getActiveAccountInfo(UnbindPhoneActivity.this.mContext);
                    account.phone = "";
                    account.countryCode = "";
                    AccountPersist.getInstance().setActiveAccount(UnbindPhoneActivity.this.mContext, account);
                    C0568T.showShort(UnbindPhoneActivity.this.mContext, (int) C0291R.string.modify_success);
                    UnbindPhoneActivity.this.finish();
                    return;
                case 3:
                    if (UnbindPhoneActivity.this.dialog != null && UnbindPhoneActivity.this.dialog.isShowing()) {
                        UnbindPhoneActivity.this.dialog.dismiss();
                        UnbindPhoneActivity.this.dialog = null;
                    }
                    C0568T.showShort(UnbindPhoneActivity.this.mContext, (int) C0291R.string.password_error);
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
                    if (UnbindPhoneActivity.this.dialog != null && UnbindPhoneActivity.this.dialog.isShowing()) {
                        UnbindPhoneActivity.this.dialog.dismiss();
                        UnbindPhoneActivity.this.dialog = null;
                    }
                    C0568T.showShort(UnbindPhoneActivity.this.mContext, (int) C0291R.string.operator_error);
                    return;
            }
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(C0291R.layout.activity_unbind_phone);
        this.mContext = this;
        initComponent();
    }

    public void initComponent() {
        this.back_btn = (ImageView) findViewById(C0291R.id.back_btn);
        this.unbind = (Button) findViewById(C0291R.id.unbind);
        this.phone_text = (TextView) findViewById(C0291R.id.phone_text);
        this.dialog_input_mask = (RelativeLayout) findViewById(C0291R.id.dialog_input_mask);
        Account account = AccountPersist.getInstance().getActiveAccountInfo(this.mContext);
        this.phone_text.setText("+" + account.countryCode + "-" + account.phone);
        this.unbind.setOnClickListener(this);
        this.back_btn.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.unbind:
                showInputPwd("0", "0", "0");
                return;
            default:
                return;
        }
    }

    public void showInputPwd(final String phone, final String countryCode, final String checkCode) {
        this.dialog_input = new MyInputDialog(this.mContext);
        this.dialog_input.setTitle(this.mContext.getResources().getString(C0291R.string.unbind_phone));
        this.dialog_input.setBtn1_str(this.mContext.getResources().getString(C0291R.string.ensure));
        this.dialog_input.setBtn2_str(this.mContext.getResources().getString(C0291R.string.cancel));
        this.dialog_input.setOnButtonOkListener(new OnButtonOkListener() {
            public void onClick() {
                String password = UnbindPhoneActivity.this.dialog_input.getInput1Text();
                if ("".equals(password.trim())) {
                    C0568T.showShort(UnbindPhoneActivity.this.mContext, (int) C0291R.string.input_login_pwd);
                    return;
                }
                UnbindPhoneActivity.this.dialog_input.hide(UnbindPhoneActivity.this.dialog_input_mask);
                if (UnbindPhoneActivity.this.dialog == null) {
                    UnbindPhoneActivity.this.dialog = new NormalDialog(UnbindPhoneActivity.this.mContext, UnbindPhoneActivity.this.mContext.getResources().getString(C0291R.string.verification), "", "", "");
                    UnbindPhoneActivity.this.dialog.setStyle(2);
                }
                UnbindPhoneActivity.this.dialog.showDialog();
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
        return 30;
    }
}
