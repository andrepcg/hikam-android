package com.jwkj.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.streamax.miotp.p2p.jni.GWellUserInfo;
import cn.com.streamax.miotp.p2p.jni.P2pDevInfo;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.hikam.C0291R;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.guide.IndexGuideActivity;
import com.p2p.core.P2PValue.HikamDeviceModel;

public class AddContactActivity extends BaseActivity implements OnClickListener {
    EditText contactId;
    NormalDialog dialog;
    Handler handler = new C03241();
    private ImageView mBack;
    Contact mContact;
    Context mContext;
    private TextView mNext;

    class C03241 extends Handler {
        C03241() {
        }

        public void handleMessage(Message msg) {
            int value = msg.arg1;
            switch (msg.what) {
                case 1:
                    if (AddContactActivity.this.dialog != null && AddContactActivity.this.dialog.isShowing()) {
                        AddContactActivity.this.dialog.dismiss();
                        AddContactActivity.this.dialog = null;
                    }
                    String fullID = msg.getData().getString("fullID");
                    if (FList.getInstance().isContact(fullID) != null) {
                        C0568T.showShort(AddContactActivity.this.mContext, (int) C0291R.string.contact_already_exist);
                        return;
                    }
                    int type;
                    if (fullID.charAt(0) == '0') {
                        type = 3;
                    } else {
                        type = 7;
                    }
                    Contact saveContact = new Contact();
                    saveContact.contactId = fullID;
                    saveContact.contactType = type;
                    saveContact.activeUser = NpcCommon.mThreeNum;
                    saveContact.messageCount = 0;
                    saveContact.contactModel = HikamDeviceModel.Q3;
                    Intent add_next = new Intent(AddContactActivity.this.mContext, AddContactNextActivity.class);
                    add_next.putExtra(ContactDB.TABLE_NAME, saveContact);
                    AddContactActivity.this.mContext.startActivity(add_next);
                    AddContactActivity.this.finish();
                    return;
                case 2:
                    if (AddContactActivity.this.dialog != null && AddContactActivity.this.dialog.isShowing()) {
                        AddContactActivity.this.dialog.dismiss();
                        AddContactActivity.this.dialog = null;
                    }
                    C0568T.showShort(AddContactActivity.this.mContext, (int) C0291R.string.device_id_error);
                    return;
                default:
                    return;
            }
        }
    }

    class C03252 implements OnCancelListener {
        C03252() {
        }

        public void onCancel(DialogInterface arg0) {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_add_contact);
        this.mContact = (Contact) getIntent().getSerializableExtra(ContactDB.TABLE_NAME);
        this.mContext = this;
        initCompent();
    }

    public void initCompent() {
        this.contactId = (EditText) findViewById(C0291R.id.contactId);
        this.mBack = (ImageView) findViewById(C0291R.id.back_btn);
        this.mNext = (TextView) findViewById(C0291R.id.next);
        if (this.mContact != null) {
            this.contactId.setText(this.mContact.contactId);
        }
        this.mBack.setOnClickListener(this);
        this.mNext.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.back_btn:
                startActivity(new Intent(this, IndexGuideActivity.class));
                finish();
                return;
            case C0291R.id.next:
                next();
                return;
            default:
                return;
        }
    }

    public void onBackPressed() {
        startActivity(new Intent(this, IndexGuideActivity.class));
        finish();
    }

    public void next() {
        String input_id = this.contactId.getText().toString().trim();
        if (!Utils.isNetworkConnected(this.mContext)) {
            C0568T.showShort(this.mContext, (int) C0291R.string.net_error_tip);
        } else if (input_id != null && input_id.trim().equals("")) {
            C0568T.showShort(this.mContext, (int) C0291R.string.input_contact_id);
        } else if (input_id.charAt(0) == '0') {
            C0568T.showShort(this.mContext, (int) C0291R.string.robot_id_not_first_with_zero);
        } else if (input_id.length() > 9) {
            C0568T.showShort(this.mContext, (int) C0291R.string.contact_id_too_long);
        } else if (!Utils.checkDevID(input_id)) {
            C0568T.showShort(this.mContext, (int) C0291R.string.device_id_format_error);
        } else if (!Utils.isNumeric(input_id)) {
            final String shortID = input_id;
            this.dialog = new NormalDialog(this.mContext);
            this.dialog.setOnCancelListener(new C03252());
            this.dialog.showLoadingDialog2();
            if (FList.getInstance().hikam_sdk_register_state == -1) {
                new Thread() {
                    public void run() {
                        Log.i("Register", "P2pJni.P2pClientSdkRegister start");
                        FList.getInstance().hikam_sdk_register_state = 1;
                        int regResult = P2pJni.P2pClientSdkRegister(1, new GWellUserInfo());
                        Log.i("Register", "P2pJni.P2pClientSdkRegister finish, result = " + regResult);
                        Message msg;
                        if (regResult == 0) {
                            FList.getInstance().hikam_sdk_register_state = 0;
                            P2pDevInfo devinfo = new P2pDevInfo();
                            int initResult = P2pJni.P2pClientSdkGetDeviceFullID(shortID, devinfo);
                            String fullID = new String(devinfo.p2p_license);
                            if (initResult != 0 || fullID.length() <= 0) {
                                msg = new Message();
                                msg.what = 2;
                                AddContactActivity.this.handler.sendMessage(msg);
                                return;
                            }
                            msg = new Message();
                            msg.what = 1;
                            Bundle data = new Bundle();
                            data.putString("fullID", fullID);
                            msg.setData(data);
                            AddContactActivity.this.handler.sendMessage(msg);
                            return;
                        }
                        FList.getInstance().hikam_sdk_register_state = -1;
                        msg = new Message();
                        msg.what = 2;
                        AddContactActivity.this.handler.sendMessage(msg);
                    }
                }.start();
            } else {
                new Thread() {
                    public void run() {
                        P2pDevInfo devinfo = new P2pDevInfo();
                        int initResult = P2pJni.P2pClientSdkGetDeviceFullID(shortID, devinfo);
                        String fullID = new String(devinfo.p2p_license);
                        if (initResult != 0 || fullID.length() <= 0) {
                            Message msg = new Message();
                            msg.what = 2;
                            AddContactActivity.this.handler.sendMessage(msg);
                            return;
                        }
                        msg = new Message();
                        msg.what = 1;
                        Bundle data = new Bundle();
                        data.putString("fullID", fullID);
                        msg.setData(data);
                        AddContactActivity.this.handler.sendMessage(msg);
                    }
                }.start();
            }
        } else if (FList.getInstance().isContact(input_id) != null) {
            C0568T.showShort(this.mContext, (int) C0291R.string.contact_already_exist);
        } else {
            int type;
            if (input_id.charAt(0) == '0') {
                type = 3;
            } else {
                type = 7;
            }
            Contact saveContact = new Contact();
            saveContact.contactId = input_id;
            saveContact.contactType = type;
            saveContact.activeUser = NpcCommon.mThreeNum;
            saveContact.messageCount = 0;
            Intent add_next = new Intent(this.mContext, AddContactNextActivity.class);
            add_next.putExtra(ContactDB.TABLE_NAME, saveContact);
            this.mContext.startActivity(add_next);
            finish();
        }
    }

    public int getActivityInfo() {
        return 7;
    }
}
