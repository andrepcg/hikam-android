package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class ModifyAccountEmailActivity extends BaseActivity implements OnClickListener {
    NormalDialog dialog;
    MyInputDialog dialog_input;
    RelativeLayout dialog_input_mask;
    boolean isRegFilter = false;
    ImageView mBack;
    Context mContext;
    EditText mEmail;
    Button mNext;
    private BroadcastReceiver mReceiver = new C04162();
    String old_email;

    class C04151 implements TextWatcher {
        C04151() {
        }

        public void afterTextChanged(Editable arg0) {
            if (ModifyAccountEmailActivity.this.mEmail.getText().toString().equals(ModifyAccountEmailActivity.this.old_email)) {
                ModifyAccountEmailActivity.this.mNext.setBackgroundResource(C0291R.drawable.tab_button_disabled);
                ModifyAccountEmailActivity.this.mNext.setOnClickListener(null);
                return;
            }
            ModifyAccountEmailActivity.this.mNext.setBackgroundResource(C0291R.drawable.tab_button);
            ModifyAccountEmailActivity.this.mNext.setOnClickListener(ModifyAccountEmailActivity.this);
        }

        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    }

    class C04162 extends BroadcastReceiver {
        C04162() {
        }

        public void onReceive(Context arg0, Intent intent) {
        }
    }

    class SetAccountInfoTask extends AsyncTask {
        private String email;
        private String password;

        public SetAccountInfoTask(String password, String email) {
            this.password = password;
            this.email = email;
        }

        protected Object doInBackground(Object... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Account account = AccountPersist.getInstance().getActiveAccountInfo(ModifyAccountEmailActivity.this.mContext);
            return Integer.valueOf(NetManager.getInstance(ModifyAccountEmailActivity.this.mContext).setAccountInfo(NpcCommon.mThreeNum, account.phone, this.email, account.countryCode, account.sessionId, this.password, "2", ""));
        }

        protected void onPostExecute(Object object) {
            switch (((Integer) object).intValue()) {
                case 0:
                    if (ModifyAccountEmailActivity.this.dialog != null && ModifyAccountEmailActivity.this.dialog.isShowing()) {
                        ModifyAccountEmailActivity.this.dialog.dismiss();
                        ModifyAccountEmailActivity.this.dialog = null;
                    }
                    Account account = AccountPersist.getInstance().getActiveAccountInfo(ModifyAccountEmailActivity.this.mContext);
                    account.email = this.email;
                    AccountPersist.getInstance().setActiveAccount(ModifyAccountEmailActivity.this.mContext, account);
                    C0568T.showShort(ModifyAccountEmailActivity.this.mContext, (int) C0291R.string.modify_success);
                    ModifyAccountEmailActivity.this.finish();
                    return;
                case 3:
                    if (ModifyAccountEmailActivity.this.dialog != null && ModifyAccountEmailActivity.this.dialog.isShowing()) {
                        ModifyAccountEmailActivity.this.dialog.dismiss();
                        ModifyAccountEmailActivity.this.dialog = null;
                    }
                    C0568T.showShort(ModifyAccountEmailActivity.this.mContext, (int) C0291R.string.password_error);
                    return;
                case 4:
                    if (ModifyAccountEmailActivity.this.dialog != null && ModifyAccountEmailActivity.this.dialog.isShowing()) {
                        ModifyAccountEmailActivity.this.dialog.dismiss();
                        ModifyAccountEmailActivity.this.dialog = null;
                    }
                    C0568T.showShort(ModifyAccountEmailActivity.this.mContext, (int) C0291R.string.email_format_error);
                    return;
                case 7:
                    if (ModifyAccountEmailActivity.this.dialog != null && ModifyAccountEmailActivity.this.dialog.isShowing()) {
                        ModifyAccountEmailActivity.this.dialog.dismiss();
                        ModifyAccountEmailActivity.this.dialog = null;
                    }
                    C0568T.showShort(ModifyAccountEmailActivity.this.mContext, (int) C0291R.string.email_used);
                    return;
                case 23:
                    Intent i = new Intent();
                    i.setAction(Action.SESSION_ID_ERROR);
                    MyApp.app.sendBroadcast(i);
                    return;
                case NetManager.CONNECT_CHANGE /*998*/:
                    new SetAccountInfoTask(this.password, this.email).execute(new Object[0]);
                    return;
                default:
                    if (ModifyAccountEmailActivity.this.dialog != null && ModifyAccountEmailActivity.this.dialog.isShowing()) {
                        ModifyAccountEmailActivity.this.dialog.dismiss();
                        ModifyAccountEmailActivity.this.dialog = null;
                    }
                    C0568T.showShort(ModifyAccountEmailActivity.this.mContext, (int) C0291R.string.operator_error);
                    return;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_modify_account_email);
        this.mContext = this;
        initCompent();
        regFilter();
    }

    public void initCompent() {
        this.mBack = (ImageView) findViewById(C0291R.id.back_btn);
        this.mNext = (Button) findViewById(C0291R.id.next);
        this.mEmail = (EditText) findViewById(C0291R.id.email);
        Account account = AccountPersist.getInstance().getActiveAccountInfo(this.mContext);
        this.old_email = account.email;
        this.mEmail.addTextChangedListener(new C04151());
        this.mEmail.setText(account.email);
        this.dialog_input_mask = (RelativeLayout) findViewById(C0291R.id.dialog_input_mask);
        this.mBack.setOnClickListener(this);
    }

    public void regFilter() {
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter());
        this.isRegFilter = true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.next:
                String email = this.mEmail.getText().toString();
                if ("".equals(email.trim())) {
                    showInputPwd(email);
                    return;
                } else if (email.length() > 31 || email.length() < 5) {
                    C0568T.showShort((Context) this, (int) C0291R.string.email_too_long);
                    return;
                } else {
                    showInputPwd(email);
                    return;
                }
            default:
                return;
        }
    }

    public void showInputPwd(final String email) {
        this.dialog_input = new MyInputDialog(this.mContext);
        this.dialog_input.setTitle(this.mContext.getResources().getString(C0291R.string.change_email));
        this.dialog_input.setBtn1_str(this.mContext.getResources().getString(C0291R.string.ensure));
        this.dialog_input.setBtn2_str(this.mContext.getResources().getString(C0291R.string.cancel));
        this.dialog_input.setOnButtonOkListener(new OnButtonOkListener() {
            public void onClick() {
                String password = ModifyAccountEmailActivity.this.dialog_input.getInput1Text();
                if ("".equals(password.trim())) {
                    C0568T.showShort(ModifyAccountEmailActivity.this.mContext, (int) C0291R.string.input_login_pwd);
                    return;
                }
                ModifyAccountEmailActivity.this.dialog_input.hide(ModifyAccountEmailActivity.this.dialog_input_mask);
                if (ModifyAccountEmailActivity.this.dialog == null) {
                    ModifyAccountEmailActivity.this.dialog = new NormalDialog(ModifyAccountEmailActivity.this.mContext, ModifyAccountEmailActivity.this.mContext.getResources().getString(C0291R.string.verification), "", "", "");
                    ModifyAccountEmailActivity.this.dialog.setStyle(2);
                }
                ModifyAccountEmailActivity.this.dialog.showDialog();
                new SetAccountInfoTask(password, email).execute(new Object[0]);
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

    public void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.isRegFilter = false;
        }
    }

    public int getActivityInfo() {
        return 14;
    }
}
