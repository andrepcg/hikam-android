package com.jwkj.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.hikam.C0291R;
import com.jwkj.entity.Account;
import com.jwkj.global.AccountPersist;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.network.ModifyLoginPasswordResult;
import com.p2p.core.network.NetManager;
import org.json.JSONObject;

public class ModifyLoginPasswordActivity extends BaseActivity implements OnClickListener {
    NormalDialog dialog;
    ImageView mBack;
    Context mContext;
    Button mSave;
    EditText new_pwd;
    EditText old_pwd;
    String password_new;
    String password_old;
    String password_re_new;
    EditText re_new_pwd;

    class ModifyLoginPasswordTask extends AsyncTask {
        String newPwd;
        String oldPwd;
        String rePwd;
        String sessionId;
        String threeNum;

        public ModifyLoginPasswordTask(String threeNum, String sessionId, String oldPwd, String newPwd, String rePwd) {
            this.threeNum = threeNum;
            this.sessionId = sessionId;
            this.oldPwd = oldPwd;
            this.newPwd = newPwd;
            this.rePwd = rePwd;
        }

        protected Object doInBackground(Object... params) {
            Utils.sleepThread(1000);
            return NetManager.getInstance(ModifyLoginPasswordActivity.this.mContext).modifyLoginPassword(this.threeNum, this.sessionId, this.oldPwd, this.newPwd, this.rePwd);
        }

        protected void onPostExecute(Object object) {
            ModifyLoginPasswordResult result = NetManager.createModifyLoginPasswordResult((JSONObject) object);
            switch (Integer.parseInt(result.error_code)) {
                case 0:
                    if (ModifyLoginPasswordActivity.this.dialog != null) {
                        ModifyLoginPasswordActivity.this.dialog.dismiss();
                        ModifyLoginPasswordActivity.this.dialog = null;
                    }
                    Account account = AccountPersist.getInstance().getActiveAccountInfo(ModifyLoginPasswordActivity.this.mContext);
                    account.sessionId = result.sessionId;
                    AccountPersist.getInstance().setActiveAccount(ModifyLoginPasswordActivity.this.mContext, account);
                    C0568T.showShort(ModifyLoginPasswordActivity.this.mContext, (int) C0291R.string.modify_pwd_success);
                    Intent canel = new Intent();
                    canel.setAction(Action.ACTION_SWITCH_USER);
                    ModifyLoginPasswordActivity.this.mContext.sendBroadcast(canel);
                    ModifyLoginPasswordActivity.this.finish();
                    return;
                case 3:
                    if (ModifyLoginPasswordActivity.this.dialog != null) {
                        ModifyLoginPasswordActivity.this.dialog.dismiss();
                        ModifyLoginPasswordActivity.this.dialog = null;
                    }
                    C0568T.showShort(ModifyLoginPasswordActivity.this.mContext, (int) C0291R.string.old_pwd_error);
                    return;
                case 10:
                    if (ModifyLoginPasswordActivity.this.dialog != null) {
                        ModifyLoginPasswordActivity.this.dialog.dismiss();
                        ModifyLoginPasswordActivity.this.dialog = null;
                    }
                    C0568T.showShort(ModifyLoginPasswordActivity.this.mContext, (int) C0291R.string.pwd_inconsistence);
                    return;
                case 11:
                    if (ModifyLoginPasswordActivity.this.dialog != null) {
                        ModifyLoginPasswordActivity.this.dialog.dismiss();
                        ModifyLoginPasswordActivity.this.dialog = null;
                    }
                    C0568T.showShort(ModifyLoginPasswordActivity.this.mContext, (int) C0291R.string.old_pwd_error);
                    return;
                case 23:
                    Intent i = new Intent();
                    i.setAction(Action.SESSION_ID_ERROR);
                    MyApp.app.sendBroadcast(i);
                    return;
                case NetManager.CONNECT_CHANGE /*998*/:
                    new ModifyLoginPasswordTask(this.threeNum, this.sessionId, this.oldPwd, this.newPwd, this.rePwd).execute(new Object[0]);
                    return;
                default:
                    if (ModifyLoginPasswordActivity.this.dialog != null) {
                        ModifyLoginPasswordActivity.this.dialog.dismiss();
                        ModifyLoginPasswordActivity.this.dialog = null;
                    }
                    C0568T.showShort(ModifyLoginPasswordActivity.this.mContext, (int) C0291R.string.operator_error);
                    return;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_modify_login_password);
        this.mContext = this;
        initCompent();
    }

    public void initCompent() {
        this.mBack = (ImageView) findViewById(C0291R.id.back_btn);
        this.mSave = (Button) findViewById(C0291R.id.save);
        this.old_pwd = (EditText) findViewById(C0291R.id.old_pwd);
        this.new_pwd = (EditText) findViewById(C0291R.id.new_pwd);
        this.re_new_pwd = (EditText) findViewById(C0291R.id.re_new_pwd);
        this.old_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.new_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.re_new_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.mBack.setOnClickListener(this);
        this.mSave.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.save:
                this.password_old = this.old_pwd.getText().toString();
                this.password_new = this.new_pwd.getText().toString();
                this.password_re_new = this.re_new_pwd.getText().toString();
                if ("".equals(this.password_old.trim())) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.input_old_pwd);
                    return;
                } else if ("".equals(this.password_new.trim())) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.input_new_pwd);
                    return;
                } else if (this.password_new.length() > 27) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.password_length_error);
                    return;
                } else if ("".equals(this.password_re_new.trim())) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.input_re_new_device_pwd);
                    return;
                } else if (this.password_re_new.equals(this.password_new)) {
                    if (this.dialog == null) {
                        this.dialog = new NormalDialog(this, getResources().getString(C0291R.string.verification), "", "", "");
                        this.dialog.setStyle(2);
                    }
                    this.dialog.showDialog();
                    this.dialog.setCancelable(false);
                    new ModifyLoginPasswordTask(NpcCommon.mThreeNum, AccountPersist.getInstance().getActiveAccountInfo(this.mContext).sessionId, this.password_old, this.password_new, this.password_re_new).execute(new Object[0]);
                    return;
                } else {
                    C0568T.showShort(this.mContext, (int) C0291R.string.pwd_inconsistence);
                    return;
                }
            default:
                return;
        }
    }

    public int getActivityInfo() {
        return 37;
    }
}
