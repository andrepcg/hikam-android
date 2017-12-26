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
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.P2PHandler;

public class ModifyAlarmIdActivity extends BaseActivity implements OnClickListener {
    String alarmId;
    String[] data;
    NormalDialog dialog;
    private boolean isRegFilter = false;
    EditText mAlarmId;
    ImageView mBack;
    Contact mContact;
    Context mContext;
    private BroadcastReceiver mReceiver = new C04221();
    Button mSave;
    int position;

    class C04221 extends BroadcastReceiver {
        C04221() {
        }

        public void onReceive(Context arg0, Intent intent) {
            int result;
            if (intent.getAction().equals(P2P.RET_SET_BIND_ALARM_ID)) {
                result = intent.getIntExtra("result", -1);
                if (ModifyAlarmIdActivity.this.dialog != null && ModifyAlarmIdActivity.this.dialog.isShowing()) {
                    ModifyAlarmIdActivity.this.dialog.dismiss();
                    ModifyAlarmIdActivity.this.dialog = null;
                }
                if (result == 0) {
                    C0568T.showShort(ModifyAlarmIdActivity.this.mContext, (int) C0291R.string.modify_success);
                    ModifyAlarmIdActivity.this.finish();
                    return;
                }
                C0568T.showShort(ModifyAlarmIdActivity.this.mContext, (int) C0291R.string.operator_error);
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_BIND_ALARM_ID)) {
                result = intent.getIntExtra("result", -1);
                if (ModifyAlarmIdActivity.this.dialog != null && ModifyAlarmIdActivity.this.dialog.isShowing()) {
                    ModifyAlarmIdActivity.this.dialog.dismiss();
                    ModifyAlarmIdActivity.this.dialog = null;
                }
                if (result == 9999) {
                    ModifyAlarmIdActivity.this.finish();
                    Intent i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    ModifyAlarmIdActivity.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    C0568T.showShort(ModifyAlarmIdActivity.this.mContext, (int) C0291R.string.net_error_operator_fault);
                }
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.modify_npc_alarm_id);
        this.mContact = (Contact) getIntent().getSerializableExtra(ContactDB.TABLE_NAME);
        this.data = getIntent().getStringArrayExtra("data");
        this.mContext = this;
        initCompent();
        regFilter();
    }

    public void initCompent() {
        this.mBack = (ImageView) findViewById(C0291R.id.back_btn);
        this.mSave = (Button) findViewById(C0291R.id.save);
        this.mAlarmId = (EditText) findViewById(C0291R.id.alarmId);
        this.mBack.setOnClickListener(this);
        this.mSave.setOnClickListener(this);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.ACK_RET_SET_BIND_ALARM_ID);
        filter.addAction(P2P.RET_SET_BIND_ALARM_ID);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.save:
                this.alarmId = this.mAlarmId.getText().toString();
                if ("".equals(this.alarmId.trim())) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.input_alarmId);
                    return;
                } else if (this.alarmId.charAt(0) != '0') {
                    C0568T.showShort(this.mContext, (int) C0291R.string.alarm_id_must_first_with_zero);
                    return;
                } else if (this.alarmId.length() > 9) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.alarm_id_too_long);
                    return;
                } else {
                    if (this.dialog == null) {
                        this.dialog = new NormalDialog(this, getResources().getString(C0291R.string.verification), "", "", "");
                        this.dialog.setStyle(2);
                    }
                    this.dialog.showDialog();
                    String[] new_data = new String[(this.data.length + 1)];
                    for (int i = 0; i < this.data.length; i++) {
                        new_data[i] = this.data[i];
                    }
                    new_data[new_data.length - 1] = this.alarmId;
                    P2PHandler.getInstance().setBindAlarmId(this.mContact.contactModel, this.mContact.contactId, this.mContact.contactPassword, new_data.length, new_data, NpcCommon.mThreeNum);
                    return;
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
        return 17;
    }
}
