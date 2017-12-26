package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.hikam.C0291R;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.P2P;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.P2PHandler;

public class ModifyNpcVisitorPasswordActivity extends BaseActivity implements OnClickListener {
    ImageView back_bt;
    BroadcastReceiver br = new C04251();
    NormalDialog dialog;
    EditText et_pwd;
    boolean isRegFilter = false;
    private Contact mContact;
    private Context mContext;
    Button msave;

    class C04251 extends BroadcastReceiver {
        C04251() {
        }

        public void onReceive(Context arg0, Intent intent) {
            int result;
            if (intent.getAction().equals(P2P.RET_SET_VISITOR_DEVICE_PASSWORD)) {
                result = intent.getIntExtra("result", -1);
                if (ModifyNpcVisitorPasswordActivity.this.dialog != null) {
                    ModifyNpcVisitorPasswordActivity.this.dialog.dismiss();
                    ModifyNpcVisitorPasswordActivity.this.dialog = null;
                }
                if (result == 0) {
                    C0568T.showShort(ModifyNpcVisitorPasswordActivity.this.mContext, (int) C0291R.string.modify_success);
                    ModifyNpcVisitorPasswordActivity.this.finish();
                    return;
                }
                C0568T.showShort(ModifyNpcVisitorPasswordActivity.this.mContext, (int) C0291R.string.operator_error);
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_VISITOR_DEVICE_PASSWORD)) {
                result = intent.getIntExtra("state", -1);
                if (result == 9999) {
                    if (ModifyNpcVisitorPasswordActivity.this.dialog != null) {
                        ModifyNpcVisitorPasswordActivity.this.dialog.dismiss();
                        ModifyNpcVisitorPasswordActivity.this.dialog = null;
                    }
                    C0568T.showShort(ModifyNpcVisitorPasswordActivity.this.mContext, (int) C0291R.string.old_pwd_error);
                } else if (result == 9998) {
                    C0568T.showShort(ModifyNpcVisitorPasswordActivity.this.mContext, (int) C0291R.string.net_error_operator_fault);
                }
            }
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(C0291R.layout.modify_npc_visitor_pwd);
        this.mContext = this;
        this.mContact = (Contact) getIntent().getSerializableExtra(ContactDB.TABLE_NAME);
        initComponent();
        regFilter();
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.RET_SET_VISITOR_DEVICE_PASSWORD);
        filter.addAction(P2P.ACK_RET_SET_VISITOR_DEVICE_PASSWORD);
        this.mContext.registerReceiver(this.br, filter);
        this.isRegFilter = true;
    }

    public void initComponent() {
        this.et_pwd = (EditText) findViewById(C0291R.id.et_pwd);
        this.msave = (Button) findViewById(C0291R.id.save);
        this.back_bt = (ImageView) findViewById(C0291R.id.back_btn);
        this.msave.setOnClickListener(this);
        this.back_bt.setOnClickListener(this);
    }

    public int getActivityInfo() {
        return 47;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.save:
                String visitor_password = this.et_pwd.getText().toString();
                if ("".equals(visitor_password.trim())) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.input_visitor_pwd);
                    return;
                } else if (visitor_password.length() > 10) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.visitor_pwd_to_long);
                    return;
                } else if (!Utils.isNumeric(visitor_password) || visitor_password.charAt(0) == '0') {
                    C0568T.showShort(this.mContext, (int) C0291R.string.visitor_pwd_must_digit);
                    return;
                } else {
                    if (this.dialog == null) {
                        this.dialog = new NormalDialog(this, getResources().getString(C0291R.string.verification), "", "", "");
                        this.dialog.setStyle(2);
                    }
                    this.dialog.showDialog();
                    P2PHandler.getInstance().setDeviceVisitorPassword(this.mContact.contactId, this.mContact.contactPassword, visitor_password);
                    return;
                }
            default:
                return;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.br);
            this.isRegFilter = false;
        }
    }
}
