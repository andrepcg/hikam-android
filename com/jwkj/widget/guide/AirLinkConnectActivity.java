package com.jwkj.widget.guide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.CallActivity;
import com.jwkj.activity.BaseActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.FList;
import com.jwkj.utils.C0568T;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.P2PHandler;

public class AirLinkConnectActivity extends BaseActivity implements OnClickListener {
    private ProgressBar bar;
    NormalDialog dialog;
    private EditText et_name;
    private EditText et_pwd;
    private EditText et_pwd2;
    private ImageView img_back;
    boolean isRegFilter = false;
    private LinearLayout ll_airlink_init;
    private LinearLayout ll_done;
    private Context mContext;
    private BroadcastReceiver mReceiver = new C06341();
    private RelativeLayout rl_add;
    private RelativeLayout rl_bottom_tools;
    private RelativeLayout rl_exit;
    private RelativeLayout rl_live;
    private Contact saveContact = null;
    private String str_name;
    private String str_pwd;
    private String str_pwd2;
    private TextView tv_info;

    class C06341 extends BroadcastReceiver {
        C06341() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(P2P.RET_SET_INIT_PASSWORD)) {
                Log.e("oaosj", "RET_SET_INIT_PASSWORD");
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_INIT_PASSWORD)) {
                Log.e("oaosj", "ACK_RET_SET_INIT_PASSWORD");
            } else if (intent.getAction().equals(P2P.RET_SET_DEVICE_PASSWORD)) {
                Log.e("oaosj", "RET_SET_DEVICE_PASSWORD");
                result = intent.getIntExtra("result", -1);
                if (AirLinkConnectActivity.this.dialog != null) {
                    AirLinkConnectActivity.this.dialog.dismiss();
                    AirLinkConnectActivity.this.dialog = null;
                }
                if (result == 0) {
                    Contact contact = FList.getInstance().isContact(AirLinkConnectActivity.this.saveContact.contactId);
                    if (contact != null) {
                        contact.contactName = AirLinkConnectActivity.this.str_name;
                        contact.userPassword = AirLinkConnectActivity.this.str_pwd;
                        contact.contactPassword = AirLinkConnectActivity.this.str_pwd;
                        FList.getInstance().update(contact);
                    } else {
                        AirLinkConnectActivity.this.saveContact.contactName = AirLinkConnectActivity.this.str_name;
                        AirLinkConnectActivity.this.saveContact.userPassword = AirLinkConnectActivity.this.str_pwd;
                        AirLinkConnectActivity.this.saveContact.contactPassword = AirLinkConnectActivity.this.str_pwd;
                        FList.getInstance().insert(AirLinkConnectActivity.this.saveContact);
                    }
                    FList.getInstance().updateLocalDeviceFlag(AirLinkConnectActivity.this.saveContact.contactId, 1);
                    AirLinkConnectActivity.this.sendSuccessBroadcast();
                    AirLinkConnectActivity.this.showFinalOp();
                    return;
                }
                C0568T.showShort(AirLinkConnectActivity.this.mContext, (int) C0291R.string.operator_error);
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_DEVICE_PASSWORD)) {
                Log.e("oaosj", "ACK_RET_SET_DEVICE_PASSWORD");
            } else if (intent.getAction().equals(P2P.ACK_RET_CHECK_PASSWORD)) {
                Log.e("oaosj", "ACK_RET_CHECK_PASSWORD");
            } else if (intent.getAction().equals(P2P.ACK_RET_CHECK_PASSWORD2)) {
                Log.e("oaosj", "ACK_RET_CHECK_PASSWORD2");
                result = intent.getIntExtra("result", -1);
                String model = intent.getStringExtra("model");
                if (result == 9997) {
                    if (AirLinkConnectActivity.this.dialog != null && AirLinkConnectActivity.this.dialog.isShowing()) {
                        AirLinkConnectActivity.this.dialog.dismiss();
                        AirLinkConnectActivity.this.dialog = null;
                    }
                    AirLinkConnectActivity.this.saveContact.contactModel = model;
                    AirLinkConnectActivity.this.saveContact.contactPassword = "123";
                    FList.getInstance().insert(AirLinkConnectActivity.this.saveContact);
                    AirLinkConnectActivity.this.sendSuccessBroadcast();
                    P2PHandler.getInstance().ActiveHumanDetect(AirLinkConnectActivity.this.saveContact.contactModel, AirLinkConnectActivity.this.saveContact.contactId, "123");
                    P2PHandler.getInstance().setDevicePassword(AirLinkConnectActivity.this.saveContact.contactModel, AirLinkConnectActivity.this.saveContact.contactId, "123", AirLinkConnectActivity.this.et_pwd.getText().toString().trim());
                } else if (result == 9999) {
                    if (AirLinkConnectActivity.this.dialog != null && AirLinkConnectActivity.this.dialog.isShowing()) {
                        AirLinkConnectActivity.this.dialog.dismiss();
                        AirLinkConnectActivity.this.dialog = null;
                    }
                    C0568T.showShort(AirLinkConnectActivity.this.mContext, (int) C0291R.string.password_error);
                } else if (result == 9998) {
                    if (AirLinkConnectActivity.this.dialog != null && AirLinkConnectActivity.this.dialog.isShowing()) {
                        AirLinkConnectActivity.this.dialog.dismiss();
                        AirLinkConnectActivity.this.dialog = null;
                    }
                    C0568T.showShort(AirLinkConnectActivity.this.mContext, (CharSequence) "通讯失败，请重试一次");
                } else if (result == 9996) {
                    if (AirLinkConnectActivity.this.dialog != null && AirLinkConnectActivity.this.dialog.isShowing()) {
                        AirLinkConnectActivity.this.dialog.dismiss();
                        AirLinkConnectActivity.this.dialog = null;
                    }
                    C0568T.showShort(AirLinkConnectActivity.this.mContext, (int) C0291R.string.insufficient_permissions);
                }
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_connect_airlink);
        this.mContext = this;
        this.saveContact = (Contact) getIntent().getSerializableExtra(ContactDB.TABLE_NAME);
        initComponent();
        regFilter();
    }

    private void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.ACK_RET_SET_INIT_PASSWORD);
        filter.addAction(P2P.RET_SET_INIT_PASSWORD);
        filter.addAction(P2P.ACK_RET_SET_DEVICE_PASSWORD);
        filter.addAction(P2P.RET_SET_DEVICE_PASSWORD);
        filter.addAction(P2P.ACK_RET_CHECK_PASSWORD);
        filter.addAction(P2P.ACK_RET_CHECK_PASSWORD2);
        registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    private void initComponent() {
        this.ll_airlink_init = (LinearLayout) findViewById(C0291R.id.ll_airlink_init);
        this.ll_done = (LinearLayout) findViewById(C0291R.id.ll_done);
        this.tv_info = (TextView) findViewById(C0291R.id.tv_info);
        this.rl_bottom_tools = (RelativeLayout) findViewById(C0291R.id.rl_bottom_tools);
        this.rl_bottom_tools.setOnClickListener(this);
        this.et_name = (EditText) findViewById(C0291R.id.et_name);
        this.et_pwd = (EditText) findViewById(C0291R.id.et_pwd);
        this.et_pwd2 = (EditText) findViewById(C0291R.id.et_pwd2);
        this.img_back = (ImageView) findViewById(C0291R.id.img_back);
        this.rl_live = (RelativeLayout) findViewById(C0291R.id.rl_live);
        this.rl_exit = (RelativeLayout) findViewById(C0291R.id.rl_exit);
        this.rl_add = (RelativeLayout) findViewById(C0291R.id.rl_add);
        this.rl_add.setOnClickListener(this);
        this.rl_exit.setOnClickListener(this);
        this.rl_live.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.img_back:
                finish();
                return;
            case C0291R.id.rl_add:
                finish();
                return;
            case C0291R.id.rl_bottom_tools:
                this.str_name = this.et_name.getText().toString().trim();
                this.str_pwd = this.et_pwd.getText().toString().trim();
                this.str_pwd2 = this.et_pwd2.getText().toString().trim();
                if (this.str_name == null || this.str_pwd == null || this.str_pwd2 == null) {
                    C0568T.showShort((Context) this, (CharSequence) "不能输入空数据");
                    return;
                } else if (this.str_pwd == null || "".equals(this.str_pwd)) {
                    C0568T.showShort((Context) this, (int) C0291R.string.inputpassword);
                    return;
                } else if (this.str_pwd.length() > 9) {
                    C0568T.showShort((Context) this, (int) C0291R.string.password_length_error);
                    return;
                } else if (this.str_pwd2 == null || "".equals(this.str_pwd2)) {
                    C0568T.showShort((Context) this, (int) C0291R.string.reinputpassword);
                    return;
                } else if (this.str_pwd.equals(this.str_pwd2)) {
                    if (this.dialog == null) {
                        this.dialog = new NormalDialog(this, getResources().getString(C0291R.string.verification), "", "", "");
                        this.dialog.setStyle(2);
                    }
                    this.dialog.showDialog();
                    P2PHandler.getInstance().checkPassword(this.saveContact.contactModel, this.saveContact.contactId, "123");
                    return;
                } else {
                    C0568T.showShort((Context) this, (int) C0291R.string.differentpassword);
                    return;
                }
            case C0291R.id.rl_exit:
                finish();
                return;
            case C0291R.id.rl_live:
                Intent monitor = new Intent();
                monitor.setClass(this, CallActivity.class);
                monitor.putExtra("callModel", this.saveContact.contactModel);
                monitor.putExtra("callId", this.saveContact.contactId);
                monitor.putExtra(ContactDB.COLUMN_CONTACT_NAME, this.saveContact.contactName);
                monitor.putExtra("password", this.saveContact.contactPassword);
                monitor.putExtra("isOutCall", true);
                monitor.putExtra("type", 1);
                Log.e("oaosj", "call: " + this.saveContact.contactModel + " " + this.saveContact.contactId + " " + this.saveContact.contactName + " " + this.saveContact.contactPassword + " " + true + " " + 1);
                startActivity(monitor);
                finish();
                return;
            default:
                return;
        }
    }

    public void sendSuccessBroadcast() {
        Intent refreshContans = new Intent();
        refreshContans.setAction(Action.REFRESH_CONTANTS);
        refreshContans.putExtra(ContactDB.TABLE_NAME, this.saveContact);
        this.mContext.sendBroadcast(refreshContans);
        Intent createPwdSuccess = new Intent();
        createPwdSuccess.setAction(Action.UPDATE_DEVICE_FALG);
        createPwdSuccess.putExtra("threeNum", this.saveContact.contactId);
        this.mContext.sendBroadcast(createPwdSuccess);
        Intent add_success = new Intent();
        add_success.setAction(Action.ADD_CONTACT_SUCCESS);
        add_success.putExtra(ContactDB.TABLE_NAME, this.saveContact);
        this.mContext.sendBroadcast(add_success);
        Intent refreshNearlyTell = new Intent();
        refreshNearlyTell.setAction(Action.ACTION_REFRESH_NEARLY_TELL);
        this.mContext.sendBroadcast(refreshNearlyTell);
        C0568T.showShort(this.mContext, (int) C0291R.string.add_success);
    }

    private void showFinalOp() {
        this.ll_done.setVisibility(0);
        this.ll_airlink_init.setVisibility(8);
    }

    public int getActivityInfo() {
        return 104;
    }
}
