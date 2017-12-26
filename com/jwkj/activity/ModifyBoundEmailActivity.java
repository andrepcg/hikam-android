package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.hikam.C0291R;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.P2PHandler;

public class ModifyBoundEmailActivity extends BaseActivity implements OnClickListener {
    private static String FIRMWARE_VERSION_61 = "13.0.0.61";
    private static String FIRMWARE_VERSION_62 = "13.0.0.62";
    private static String HIKAM_EMAIL_BAKSERVER_ADDRESS = "52.28.45.243";
    private static String HIKAM_EMAIL_SERVER_ADDRESS = "52.28.45.243";
    private static String HIKAM_EMAIL_SERVER_CONTENT = "Dear User,\n    Please check the attached picture for more information.";
    private static byte HIKAM_EMAIL_SERVER_ENTRY = (byte) 2;
    private static int HIKAM_EMAIL_SERVER_PORT = 443;
    private static String HIKAM_EMAIL_SERVER_PWD = "Alert618033!";
    private static String HIKAM_EMAIL_SERVER_SUBJECT = "Attention: alarm";
    private static String HIKAM_EMAIL_SERVER_USER = "alert@hikam.de";
    private String curVersion;
    NormalDialog dialog;
    String email;
    String email_name;
    private boolean isRegFilter = false;
    private boolean isSurportSMTP;
    ImageView mBack;
    Contact mContact;
    Context mContext;
    EditText mEmail;
    private BroadcastReceiver mReceiver = new C04231();
    Button mSave;

    class C04231 extends BroadcastReceiver {
        C04231() {
        }

        public void onReceive(Context arg0, Intent intent) {
            int result;
            InputMethodManager imm;
            if (intent.getAction().equals(P2P.RET_SET_ALARM_EMAIL)) {
                result = intent.getIntExtra("result", -1);
                if (ModifyBoundEmailActivity.this.dialog != null && ModifyBoundEmailActivity.this.dialog.isShowing()) {
                    ModifyBoundEmailActivity.this.dialog.dismiss();
                    ModifyBoundEmailActivity.this.dialog = null;
                }
                if (result == 15) {
                    C0568T.showShort(ModifyBoundEmailActivity.this.mContext, (int) C0291R.string.email_format_error);
                } else if (((byte) (result & 1)) == (byte) 0) {
                    C0568T.showShort(ModifyBoundEmailActivity.this.mContext, (int) C0291R.string.modify_success);
                    imm = (InputMethodManager) ModifyBoundEmailActivity.this.getSystemService("input_method");
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(ModifyBoundEmailActivity.this.getWindow().getDecorView().getWindowToken(), 0);
                    }
                    ModifyBoundEmailActivity.this.finish();
                } else {
                    C0568T.showShort(ModifyBoundEmailActivity.this.mContext, (int) C0291R.string.net_error_operator_fault);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_ALARM_EMAIL)) {
                result = intent.getIntExtra("result", -1);
                if (ModifyBoundEmailActivity.this.dialog != null && ModifyBoundEmailActivity.this.dialog.isShowing()) {
                    ModifyBoundEmailActivity.this.dialog.dismiss();
                    ModifyBoundEmailActivity.this.dialog = null;
                }
                if (result == 9999) {
                    Intent i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    ModifyBoundEmailActivity.this.mContext.sendBroadcast(i);
                    imm = (InputMethodManager) ModifyBoundEmailActivity.this.getSystemService("input_method");
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(ModifyBoundEmailActivity.this.getWindow().getDecorView().getWindowToken(), 0);
                    }
                    ModifyBoundEmailActivity.this.finish();
                } else if (result == 9998) {
                    C0568T.showShort(ModifyBoundEmailActivity.this.mContext, (int) C0291R.string.net_error_operator_fault);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_ALARM_EMAIL_WITHSMTP)) {
                result = intent.getIntExtra("result", -1);
                if (ModifyBoundEmailActivity.this.dialog != null && ModifyBoundEmailActivity.this.dialog.isShowing()) {
                    ModifyBoundEmailActivity.this.dialog.dismiss();
                    ModifyBoundEmailActivity.this.dialog = null;
                }
                if (result == 15) {
                    C0568T.showShort(ModifyBoundEmailActivity.this.mContext, (int) C0291R.string.email_format_error);
                } else if (((byte) (result & 1)) == (byte) 0) {
                    C0568T.showShort(ModifyBoundEmailActivity.this.mContext, (int) C0291R.string.modify_success);
                    imm = (InputMethodManager) ModifyBoundEmailActivity.this.getSystemService("input_method");
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(ModifyBoundEmailActivity.this.getWindow().getDecorView().getWindowToken(), 0);
                    }
                    ModifyBoundEmailActivity.this.finish();
                } else {
                    C0568T.showShort(ModifyBoundEmailActivity.this.mContext, (int) C0291R.string.net_error_operator_fault);
                }
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.modify_npc_bound_email);
        this.mContact = (Contact) getIntent().getSerializableExtra(ContactDB.TABLE_NAME);
        this.email_name = getIntent().getStringExtra("email");
        this.curVersion = getIntent().getStringExtra("curVersion");
        this.isSurportSMTP = getIntent().getBooleanExtra("isSurportSMTP", false);
        this.mContext = this;
        initCompent();
        regFilter();
    }

    public void initCompent() {
        this.mBack = (ImageView) findViewById(C0291R.id.back_btn);
        this.mSave = (Button) findViewById(C0291R.id.save);
        this.mEmail = (EditText) findViewById(C0291R.id.email);
        this.mBack.setOnClickListener(this);
        this.mSave.setOnClickListener(this);
        if (Utils.checkEmail(this.email_name)) {
            this.mEmail.setText(this.email_name);
        }
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.ACK_RET_SET_ALARM_EMAIL);
        filter.addAction(P2P.RET_SET_ALARM_EMAIL);
        filter.addAction(P2P.RET_GET_ALARM_EMAIL_WITHSMTP);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.back_btn:
                InputMethodManager imm = (InputMethodManager) getSystemService("input_method");
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                finish();
                return;
            case C0291R.id.save:
                this.email = this.mEmail.getText().toString();
                if (!Utils.checkEmail(this.email) && !this.email.isEmpty()) {
                    C0568T.showShort((Context) this, (int) C0291R.string.email_format_error);
                    return;
                } else if ("".equals(this.email.trim())) {
                    if (this.isSurportSMTP) {
                        P2PHandler.getInstance().setAlarmEmailWithSMTP(this.mContact.contactModel, this.mContact.contactName, HIKAM_EMAIL_BAKSERVER_ADDRESS, this.mContact.contactId, this.mContact.contactPassword, (byte) 3, "0", 0, "", "", "", "", "", (byte) 0, (byte) 0, 0);
                        return;
                    } else {
                        P2PHandler.getInstance().setAlarmEmailWithSMTP(this.mContact.contactModel, this.mContact.contactName, HIKAM_EMAIL_BAKSERVER_ADDRESS, this.mContact.contactId, this.mContact.contactPassword, (byte) 0, "0", 0, "", "", "", "", "", (byte) 0, (byte) 0, 0);
                        return;
                    }
                } else if (this.email.length() > 31 || this.email.length() < 5) {
                    C0568T.showShort((Context) this, (int) C0291R.string.email_too_long);
                    return;
                } else {
                    if (this.dialog == null) {
                        this.dialog = new NormalDialog(this, getResources().getString(C0291R.string.verification), "", "", "");
                        this.dialog.setStyle(2);
                    }
                    this.dialog.showDialog();
                    if (FIRMWARE_VERSION_61.equals(this.curVersion)) {
                        P2PHandler.getInstance().setAlarmEmailWithSMTP(this.mContact.contactModel, this.mContact.contactName, HIKAM_EMAIL_BAKSERVER_ADDRESS, this.mContact.contactId, this.mContact.contactPassword, (byte) 0, this.email, 0, "", "", "", "", "", (byte) 0, (byte) 0, 0);
                        return;
                    } else if (this.isSurportSMTP) {
                        HIKAM_EMAIL_SERVER_SUBJECT = "Attention: alarm from camera '" + this.mContact.contactName + "'";
                        P2PHandler.getInstance().setAlarmEmailWithSMTP(this.mContact.contactModel, this.mContact.contactName, HIKAM_EMAIL_BAKSERVER_ADDRESS, this.mContact.contactId, this.mContact.contactPassword, (byte) 3, this.email, HIKAM_EMAIL_SERVER_PORT, HIKAM_EMAIL_SERVER_ADDRESS, HIKAM_EMAIL_SERVER_USER, HIKAM_EMAIL_SERVER_PWD, HIKAM_EMAIL_SERVER_SUBJECT, HIKAM_EMAIL_SERVER_CONTENT, HIKAM_EMAIL_SERVER_ENTRY, (byte) 0, 0);
                        return;
                    } else {
                        P2PHandler.getInstance().setAlarmEmailWithSMTP(this.mContact.contactModel, this.mContact.contactName, HIKAM_EMAIL_BAKSERVER_ADDRESS, this.mContact.contactId, this.mContact.contactPassword, (byte) 0, this.email, 0, "", "", "", "", "", (byte) 0, (byte) 0, 0);
                        return;
                    }
                }
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
        return 18;
    }
}
