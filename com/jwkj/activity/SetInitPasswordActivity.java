package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.FList;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SetInitPasswordActivity extends BaseActivity implements OnClickListener {
    NormalDialog dialog;
    private boolean isRegFilter = false;
    Contact mContact;
    Context mContext;
    private BroadcastReceiver mReceiver = new C04381();
    Button mSave;
    EditText new_pwd;
    EditText old_pwd;
    String password_new;
    String password_re_new;
    EditText re_new_pwd;
    TextView show_msg;
    String userPassword;
    int version_code3 = 0;
    int version_code4 = 0;

    class C04381 extends BroadcastReceiver {
        C04381() {
        }

        public void onReceive(Context arg0, Intent intent) {
            int result;
            if (intent.getAction().equals(P2P.RET_SET_DEVICE_PASSWORD)) {
                result = intent.getIntExtra("result", -1);
                if (SetInitPasswordActivity.this.dialog != null) {
                    SetInitPasswordActivity.this.dialog.dismiss();
                    SetInitPasswordActivity.this.dialog = null;
                }
                if (result == 0) {
                    SetInitPasswordActivity.this.mContact.contactPassword = SetInitPasswordActivity.this.password_new;
                    SetInitPasswordActivity.this.mContact.userPassword = SetInitPasswordActivity.this.userPassword;
                    FList.getInstance().update(SetInitPasswordActivity.this.mContact);
                    FList.getInstance().updateOnlineState();
                    int timezone = ((TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings()) / 1000) / 3600;
                    int[] tmpIndex = new int[]{-11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
                    for (int i = 0; i < tmpIndex.length; i++) {
                        if (tmpIndex[i] == timezone) {
                            P2PHandler.getInstance().setTimeZone(SetInitPasswordActivity.this.mContact.contactModel, SetInitPasswordActivity.this.mContact.contactId, SetInitPasswordActivity.this.mContact.contactPassword, i);
                            break;
                        }
                    }
                    P2PHandler.getInstance().setDeviceTime(SetInitPasswordActivity.this.mContact.contactModel, SetInitPasswordActivity.this.mContact.contactId, SetInitPasswordActivity.this.mContact.contactPassword, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
                    Intent refreshContans = new Intent();
                    refreshContans.setAction(Action.REFRESH_CONTANTS);
                    refreshContans.putExtra(ContactDB.TABLE_NAME, SetInitPasswordActivity.this.mContact);
                    SetInitPasswordActivity.this.mContext.sendBroadcast(refreshContans);
                    C0568T.showShort(SetInitPasswordActivity.this.mContext, (int) C0291R.string.modify_success);
                    SetInitPasswordActivity.this.finish();
                    return;
                }
                C0568T.showShort(SetInitPasswordActivity.this.mContext, (int) C0291R.string.operator_error);
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_DEVICE_PASSWORD)) {
                result = intent.getIntExtra("result", -1);
                if (SetInitPasswordActivity.this.dialog != null) {
                    SetInitPasswordActivity.this.dialog.dismiss();
                    SetInitPasswordActivity.this.dialog = null;
                }
                if (result == 9999) {
                    C0568T.showShort(SetInitPasswordActivity.this.mContext, (int) C0291R.string.old_pwd_error);
                } else if (result == 9998) {
                    C0568T.showShort(SetInitPasswordActivity.this.mContext, (int) C0291R.string.net_error_operator_fault);
                }
            } else if (intent.getAction().equals(P2P.RET_DEVICE_NOT_SUPPORT)) {
                SetInitPasswordActivity.this.finish();
                C0568T.showShort(SetInitPasswordActivity.this.mContext, (int) C0291R.string.not_support);
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_DEVICE_INFO)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    C0568T.showShort(SetInitPasswordActivity.this.mContext, (int) C0291R.string.password_error);
                } else if (result == 9998) {
                    P2PHandler.getInstance().getDeviceVersion(SetInitPasswordActivity.this.mContact.contactModel, SetInitPasswordActivity.this.mContact.contactId, SetInitPasswordActivity.this.mContact.contactPassword);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_DEVICE_INFO) || intent.getAction().equals(P2P.RET_GET_DEVICE_INFO2)) {
                String cur_version = intent.getStringExtra("cur_version");
                if (P2PValue.HikamDeviceModelList.contains(SetInitPasswordActivity.this.mContact.contactModel)) {
                    SetInitPasswordActivity.this.show_msg.setText(SetInitPasswordActivity.this.mContext.getResources().getString(C0291R.string.password_composition_new));
                    SetInitPasswordActivity.this.new_pwd.setInputType(1);
                    SetInitPasswordActivity.this.re_new_pwd.setInputType(1);
                    return;
                }
                SetInitPasswordActivity.this.version_code3 = Integer.parseInt(cur_version.split("\\.")[2]);
                SetInitPasswordActivity.this.version_code4 = Integer.parseInt(cur_version.split("\\.")[3]);
                if (SetInitPasswordActivity.this.version_code3 > 0 || (SetInitPasswordActivity.this.version_code3 == 0 && SetInitPasswordActivity.this.version_code4 > 77)) {
                    SetInitPasswordActivity.this.show_msg.setText(SetInitPasswordActivity.this.mContext.getResources().getString(C0291R.string.password_composition_new));
                    SetInitPasswordActivity.this.new_pwd.setInputType(1);
                    SetInitPasswordActivity.this.re_new_pwd.setInputType(1);
                    return;
                }
                SetInitPasswordActivity.this.show_msg.setText(SetInitPasswordActivity.this.mContext.getResources().getString(C0291R.string.password_composition));
                SetInitPasswordActivity.this.new_pwd.setInputType(2);
                SetInitPasswordActivity.this.re_new_pwd.setInputType(2);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.set_init_pwd);
        this.mContact = (Contact) getIntent().getSerializableExtra(ContactDB.TABLE_NAME);
        this.mContext = this;
        initCompent();
        regFilter();
        P2PHandler.getInstance().getDeviceVersion(this.mContact.contactModel, this.mContact.contactId, this.mContact.contactPassword);
    }

    public void initCompent() {
        this.mSave = (Button) findViewById(C0291R.id.save);
        this.new_pwd = (EditText) findViewById(C0291R.id.new_pwd);
        this.re_new_pwd = (EditText) findViewById(C0291R.id.re_new_pwd);
        this.show_msg = (TextView) findViewById(C0291R.id.show_msg);
        this.new_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.re_new_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.mSave.setOnClickListener(this);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.ACK_RET_SET_DEVICE_PASSWORD);
        filter.addAction(P2P.RET_SET_DEVICE_PASSWORD);
        filter.addAction(P2P.RET_DEVICE_NOT_SUPPORT);
        filter.addAction(P2P.ACK_RET_GET_DEVICE_INFO);
        filter.addAction(P2P.RET_GET_DEVICE_INFO);
        filter.addAction(P2P.RET_GET_DEVICE_INFO2);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.save:
                this.password_new = this.new_pwd.getText().toString();
                this.password_re_new = this.re_new_pwd.getText().toString();
                if (P2PValue.HikamDeviceModelList.contains(this.mContact.contactModel) || this.version_code3 > 0 || (this.version_code3 == 0 && this.version_code4 > 77)) {
                    if ("".equals(this.password_new.trim())) {
                        C0568T.showShort(this.mContext, (int) C0291R.string.input_new_device_pwd);
                        return;
                    } else if (this.password_new.length() < 6) {
                        C0568T.showShort(this.mContext, (int) C0291R.string.new_pwd_too_short);
                        return;
                    } else if (this.password_new.length() > 30) {
                        C0568T.showShort(this.mContext, (int) C0291R.string.new_pwd_too_long2);
                        return;
                    } else if (this.password_new.charAt(0) == '0') {
                        C0568T.showShort(this.mContext, (int) C0291R.string.contact_pwd_not_beginning_0);
                        return;
                    } else if (!Utils.checkDevicePwd(this.password_new)) {
                        C0568T.showShort(this.mContext, (int) C0291R.string.device_pwd_format_error);
                        return;
                    } else if ("".equals(this.password_re_new.trim())) {
                        C0568T.showShort(this.mContext, (int) C0291R.string.input_re_new_device_pwd);
                        return;
                    } else if (!this.password_re_new.equals(this.password_new)) {
                        C0568T.showShort(this.mContext, (int) C0291R.string.pwd_inconsistence);
                        return;
                    }
                } else if ("".equals(this.password_new.trim())) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.input_new_device_pwd);
                    return;
                } else if (this.password_new.length() < 6) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.new_pwd_too_short);
                    return;
                } else if (this.password_new.length() > 9) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.new_pwd_too_long);
                    return;
                } else if (!Utils.isNumeric(this.password_new) || this.password_new.charAt(0) == '0') {
                    C0568T.showShort(this.mContext, (int) C0291R.string.contact_pwd_must_digit);
                    return;
                } else if ("".equals(this.password_re_new.trim())) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.input_re_new_device_pwd);
                    return;
                } else if (!this.password_re_new.equals(this.password_new)) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.pwd_inconsistence);
                    return;
                }
                if (this.dialog == null) {
                    this.dialog = new NormalDialog(this, getResources().getString(C0291R.string.verification), "", "", "");
                    this.dialog.setStyle(2);
                }
                this.dialog.showDialog();
                this.userPassword = this.password_new;
                if (!P2PValue.HikamDeviceModelList.contains(this.mContact.contactModel)) {
                    this.password_new = P2PHandler.getInstance().EntryPassword(this.password_new);
                }
                P2PHandler.getInstance().setDevicePassword(this.mContact.contactModel, this.mContact.contactId, this.mContact.contactPassword, this.password_new);
                return;
            default:
                return;
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
        return 59;
    }
}
