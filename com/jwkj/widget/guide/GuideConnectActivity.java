package com.jwkj.widget.guide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.hikam.C0291R;
import com.jwkj.activity.BaseActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.data.DataManager;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class GuideConnectActivity extends BaseActivity implements OnClickListener {
    private ProgressBar bar;
    private TextView camera_id;
    private CheckBox cb_pwd;
    NormalDialog dialog;
    private EditText et_name;
    private EditText et_pwd;
    private EditText et_pwd2;
    private EditText et_source_pwd;
    private ImageView img_back;
    private boolean isAPMode = false;
    private boolean isFirstChecking = true;
    private boolean isInitPwd = true;
    boolean isRegFilter = false;
    private LinearLayout ll_init;
    private LinearLayout ll_modify;
    private Context mContext;
    private BroadcastReceiver mReceiver = new C06454();
    private RelativeLayout rl_bottom_tools;
    private Contact saveContact = null;
    private String str_name;
    private String str_pwd;
    private String str_pwd2;
    private String str_source_pwd;
    private TextView tv_info;

    class C06421 implements OnCheckedChangeListener {
        C06421() {
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                GuideConnectActivity.this.et_pwd.setInputType(145);
                GuideConnectActivity.this.et_pwd2.setInputType(145);
                GuideConnectActivity.this.et_source_pwd.setInputType(145);
                return;
            }
            GuideConnectActivity.this.et_pwd.setInputType(128);
            GuideConnectActivity.this.et_pwd2.setInputType(128);
            GuideConnectActivity.this.et_source_pwd.setInputType(128);
            GuideConnectActivity.this.et_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            GuideConnectActivity.this.et_pwd2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            GuideConnectActivity.this.et_source_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    class C06432 implements Runnable {
        C06432() {
        }

        public void run() {
            if (P2pJni.P2pClientSdkGetSessionStatus(GuideConnectActivity.this.saveContact.contactId) != 3) {
                int con_num = 1;
                while (P2pJni.P2pClientSdkConnectPeer(GuideConnectActivity.this.saveContact.contactId) != 0) {
                    con_num++;
                    if (con_num == 5) {
                        break;
                    }
                }
            }
            P2PHandler.getInstance().checkPassword(GuideConnectActivity.this.saveContact.contactModel, GuideConnectActivity.this.saveContact.contactId, GuideConnectActivity.this.str_source_pwd);
        }
    }

    class C06443 implements Runnable {
        C06443() {
        }

        public void run() {
            if (P2pJni.P2pClientSdkGetSessionStatus(GuideConnectActivity.this.saveContact.contactId) != 3) {
                int con_num = 1;
                while (P2pJni.P2pClientSdkConnectPeer(GuideConnectActivity.this.saveContact.contactId) != 0) {
                    con_num++;
                    if (con_num == 5) {
                        break;
                    }
                }
            }
            if (!P2PValue.HikamDeviceModelList.contains(GuideConnectActivity.this.saveContact.contactModel)) {
                GuideConnectActivity.this.str_pwd = P2PHandler.getInstance().EntryPassword(GuideConnectActivity.this.str_pwd);
            }
            P2PHandler.getInstance().setDevicePassword(GuideConnectActivity.this.saveContact.contactModel, GuideConnectActivity.this.saveContact.contactId, "123", GuideConnectActivity.this.str_pwd);
            int timezone = ((TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings()) / 1000) / 3600;
            int[] tmpIndex = new int[]{-11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
            for (int i = 0; i < tmpIndex.length; i++) {
                if (tmpIndex[i] == timezone) {
                    P2PHandler.getInstance().setTimeZone(GuideConnectActivity.this.saveContact.contactModel, GuideConnectActivity.this.saveContact.contactId, GuideConnectActivity.this.str_pwd, i);
                    break;
                }
            }
            P2PHandler.getInstance().setDeviceTime(GuideConnectActivity.this.saveContact.contactModel, GuideConnectActivity.this.saveContact.contactId, GuideConnectActivity.this.str_pwd, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        }
    }

    class C06454 extends BroadcastReceiver {
        C06454() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(P2P.RET_SET_INIT_PASSWORD)) {
                Log.e("oaosj", "RET_SET_INIT_PASSWORD");
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_INIT_PASSWORD)) {
                Log.e("oaosj", "ACK_RET_SET_INIT_PASSWORD");
            } else if (intent.getAction().equals(P2P.RET_SET_DEVICE_PASSWORD)) {
                Log.e("oaosj", "RET_SET_DEVICE_PASSWORD");
                int result = intent.getIntExtra("result", -1);
                if (GuideConnectActivity.this.dialog != null) {
                    GuideConnectActivity.this.dialog.dismiss();
                    GuideConnectActivity.this.dialog = null;
                }
                if (result == 0) {
                    Contact contact = FList.getInstance().isContact(GuideConnectActivity.this.saveContact.contactId);
                    if (contact != null) {
                        contact.contactName = GuideConnectActivity.this.str_name;
                        contact.userPassword = GuideConnectActivity.this.str_pwd;
                        contact.contactPassword = GuideConnectActivity.this.str_pwd;
                        FList.getInstance().update(contact);
                    } else {
                        GuideConnectActivity.this.saveContact.contactName = GuideConnectActivity.this.str_name;
                        GuideConnectActivity.this.saveContact.userPassword = GuideConnectActivity.this.str_pwd;
                        GuideConnectActivity.this.saveContact.contactPassword = GuideConnectActivity.this.str_pwd;
                        FList.getInstance().insert(GuideConnectActivity.this.saveContact);
                    }
                    FList.getInstance().updateLocalDeviceFlag(GuideConnectActivity.this.saveContact.contactId, 1);
                    GuideConnectActivity.this.sendSuccessBroadcast();
                    GuideConnectActivity.this.showFinalOp();
                    return;
                }
                C0568T.showShort(GuideConnectActivity.this.mContext, (int) C0291R.string.operator_error);
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_DEVICE_PASSWORD)) {
                Log.e("oaosj", "ACK_RET_SET_DEVICE_PASSWORD");
            } else if (intent.getAction().equals(P2P.ACK_RET_CHECK_PASSWORD)) {
                Log.e("oaosj", "ACK_RET_CHECK_PASSWORD");
                GuideConnectActivity.this.doCheckPwd(intent);
            } else if (intent.getAction().equals(P2P.ACK_RET_CHECK_PASSWORD2)) {
                Log.e("oaosj", "ACK_RET_CHECK_PASSWORD2");
                GuideConnectActivity.this.doCheckPwd(intent);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_connect_airlink);
        this.mContext = this;
        this.isAPMode = getIntent().getBooleanExtra("isApMode", false);
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
        this.camera_id = (TextView) findViewById(C0291R.id.camera_id);
        if (this.saveContact.contactId != null) {
            this.camera_id.setText(Utils.showShortDevID(this.saveContact.contactId));
        }
        this.ll_init = (LinearLayout) findViewById(C0291R.id.ll_init_pwd);
        this.ll_modify = (LinearLayout) findViewById(C0291R.id.ll_modify_pwd);
        this.tv_info = (TextView) findViewById(C0291R.id.tv_info);
        this.rl_bottom_tools = (RelativeLayout) findViewById(C0291R.id.rl_bottom_tools);
        this.rl_bottom_tools.setOnClickListener(this);
        this.et_name = (EditText) findViewById(C0291R.id.et_name);
        this.et_pwd = (EditText) findViewById(C0291R.id.et_pwd);
        this.et_pwd2 = (EditText) findViewById(C0291R.id.et_pwd2);
        this.et_source_pwd = (EditText) findViewById(C0291R.id.et_source_pwd);
        this.img_back = (ImageView) findViewById(C0291R.id.img_back);
        this.img_back.setOnClickListener(this);
        this.cb_pwd = (CheckBox) findViewById(C0291R.id.cb_pwd);
        this.cb_pwd.setOnCheckedChangeListener(new C06421());
        P2PHandler.getInstance().checkPassword(this.saveContact.contactModel, this.saveContact.contactId, "123");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.img_back:
                finish();
                return;
            case C0291R.id.rl_bottom_tools:
                this.str_name = this.et_name.getText().toString().trim();
                if (this.isInitPwd) {
                    init_pwd();
                    return;
                } else {
                    check_pwd();
                    return;
                }
            default:
                return;
        }
    }

    public void check_pwd() {
        this.str_source_pwd = this.et_source_pwd.getText().toString().trim();
        if (this.str_name == null) {
            C0568T.showShort((Context) this, (int) C0291R.string.contact_name_error);
        } else if (TextUtils.isEmpty(this.et_source_pwd.getText())) {
            C0568T.showShort((Context) this, (int) C0291R.string.input_password);
        } else if (this.str_source_pwd.length() > 30 || this.str_source_pwd.length() < 6) {
            C0568T.showShort((Context) this, (int) C0291R.string.pwd_lenght_error);
        } else if (Utils.checkDevicePwd(this.str_source_pwd)) {
            if (this.dialog == null) {
                this.dialog = new NormalDialog(this, getResources().getString(C0291R.string.verification), "", "", "");
                this.dialog.setStyle(2);
            }
            for (Contact contact : DataManager.findContactByActiveUser(this.mContext, NpcCommon.mThreeNum)) {
                if (contact.contactId.equals(this.saveContact.contactId)) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.contact_already_exist);
                    return;
                }
            }
            this.dialog.showDialog();
            new Thread(new C06432()).start();
        } else {
            C0568T.showShort((Context) this, (int) C0291R.string.password_composition_new);
        }
    }

    public void init_pwd() {
        this.str_pwd = this.et_pwd.getText().toString().trim();
        this.str_pwd2 = this.et_pwd2.getText().toString().trim();
        if (this.str_name == null) {
            C0568T.showShort((Context) this, (int) C0291R.string.contact_name_error);
        } else if (TextUtils.isEmpty(this.et_pwd.getText()) || TextUtils.isEmpty(this.et_pwd2.getText())) {
            C0568T.showShort((Context) this, (int) C0291R.string.input_password);
        } else if (this.str_pwd.length() > 30 || this.str_pwd.length() < 6 || this.str_pwd2.length() > 30 || this.str_pwd2.length() < 6) {
            C0568T.showShort((Context) this, (int) C0291R.string.pwd_lenght_error);
        } else if (!Utils.checkDevicePwd(this.str_pwd) || !Utils.checkDevicePwd(this.str_pwd2)) {
            C0568T.showShort((Context) this, (int) C0291R.string.password_composition_new);
        } else if (this.str_pwd.equals(this.str_pwd2)) {
            if (this.dialog == null) {
                this.dialog = new NormalDialog(this, getResources().getString(C0291R.string.verification), "", "", "");
                this.dialog.setStyle(2);
            }
            for (Contact contact : DataManager.findContactByActiveUser(this.mContext, NpcCommon.mThreeNum)) {
                if (contact.contactId.equals(this.saveContact.contactId)) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.contact_already_exist);
                    return;
                }
            }
            this.dialog.showDialog();
            new Thread(new C06443()).start();
        } else {
            C0568T.showShort((Context) this, (int) C0291R.string.differentpassword);
        }
    }

    protected void onPause() {
        super.onPause();
        if (this.isRegFilter) {
            this.isRegFilter = false;
            unregisterReceiver(this.mReceiver);
        }
    }

    public void doCheckPwd(Intent intent) {
        int result;
        if (this.isFirstChecking) {
            result = intent.getIntExtra("result", -1);
            String model = intent.getStringExtra("model");
            if (result == 9997) {
                this.saveContact.contactModel = model;
                this.saveContact.contactPassword = "123";
                this.isInitPwd = true;
                this.ll_modify.setVisibility(8);
                this.ll_init.setVisibility(0);
                this.isFirstChecking = false;
                return;
            } else if (result == 9999) {
                this.isFirstChecking = false;
                this.ll_modify.setVisibility(0);
                this.ll_init.setVisibility(8);
                this.isInitPwd = false;
                return;
            } else if (result == 9998 || result == 9996) {
                this.isFirstChecking = false;
                C0568T.showShort(this.mContext, (int) C0291R.string.net_error_operator_fault);
                return;
            }
        }
        if (!this.isInitPwd) {
            result = intent.getIntExtra("result", -1);
            model = intent.getStringExtra("model");
            if (result == 9997) {
                if (this.dialog != null && this.dialog.isShowing()) {
                    this.dialog.dismiss();
                    this.dialog = null;
                }
                Contact contact = FList.getInstance().isContact(this.saveContact.contactId);
                if (contact != null) {
                    contact.contactName = this.str_name;
                    contact.userPassword = this.str_source_pwd;
                    contact.activeUser = NpcCommon.mThreeNum;
                    contact.contactPassword = this.str_source_pwd;
                    FList.getInstance().update(contact);
                } else {
                    this.saveContact.contactName = this.str_name;
                    this.saveContact.userPassword = this.str_source_pwd;
                    this.saveContact.activeUser = NpcCommon.mThreeNum;
                    this.saveContact.contactPassword = this.str_source_pwd;
                    FList.getInstance().insert(this.saveContact);
                    P2PHandler.getInstance().ActiveHumanDetect(this.saveContact.contactModel, this.saveContact.contactId, this.str_source_pwd);
                }
                int timezone = ((TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings()) / 1000) / 3600;
                int[] tmpIndex = new int[]{-11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
                for (int i = 0; i < tmpIndex.length; i++) {
                    if (tmpIndex[i] == timezone) {
                        P2PHandler.getInstance().setTimeZone(this.saveContact.contactModel, this.saveContact.contactId, this.str_source_pwd, i);
                        break;
                    }
                }
                P2PHandler.getInstance().setDeviceTime(this.saveContact.contactModel, this.saveContact.contactId, this.str_source_pwd, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
                FList.getInstance().updateLocalDeviceFlag(this.saveContact.contactId, 1);
                sendSuccessBroadcast();
                showFinalOp();
            } else if (result == 9999) {
                if (this.dialog != null && this.dialog.isShowing()) {
                    this.dialog.dismiss();
                    this.dialog = null;
                }
                C0568T.showShort(this.mContext, (int) C0291R.string.password_error);
            } else if (result == 9998) {
                if (this.dialog != null && this.dialog.isShowing()) {
                    this.dialog.dismiss();
                    this.dialog = null;
                }
                C0568T.showShort(this.mContext, (int) C0291R.string.net_error_operator_fault);
            } else if (result == 9996) {
                if (this.dialog != null && this.dialog.isShowing()) {
                    this.dialog.dismiss();
                    this.dialog = null;
                }
                C0568T.showShort(this.mContext, (int) C0291R.string.insufficient_permissions);
            }
        }
    }

    public void doSetPwd(Intent intent) {
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
        if (this.isAPMode) {
            Intent intent = new Intent(this, ApModeGuideDoneActivity.class);
            intent.putExtra(ContactDB.TABLE_NAME, this.saveContact);
            startActivity(intent);
            finish();
            return;
        }
        intent = new Intent(this, AirLinkGuideDoneActivity.class);
        intent.putExtra(ContactDB.TABLE_NAME, this.saveContact);
        startActivity(intent);
        finish();
    }

    public void checkPwdFormat() {
    }

    public int getActivityInfo() {
        return 235;
    }
}
