package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.hikam.C0291R;
import com.jwkj.data.ContactDB;
import com.jwkj.entity.Account;
import com.jwkj.global.AccountPersist;
import com.jwkj.global.Constants;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import com.lib.addBar.AddBar;
import com.lib.addBar.OnItemChangeListener;
import com.lib.addBar.OnLeftIconClickListener;
import com.p2p.core.P2PHandler;

public class AlarmPushAccountActivity extends BaseActivity implements OnClickListener {
    Account account;
    String[] accountList;
    RelativeLayout add_alarm_item;
    AddBar addbar;
    ImageView back_btn;
    BroadcastReceiver br = new C03463();
    String contactId;
    String contactModel;
    String contactPassword;
    NormalDialog dialog_loading;
    private Handler handler = new Handler();
    boolean isRegFilter = false;
    String[] last_bind_data;
    private Context mContext;
    ProgressBar progressBar_alarmId;

    class C03463 extends BroadcastReceiver {
        C03463() {
        }

        public void onReceive(Context arg0, Intent intent) {
            String showStr;
            if (intent.getAction().equals(P2P.RET_GET_BIND_ALARM_ID)) {
                String[] data = intent.getStringArrayExtra("data");
                AlarmPushAccountActivity.this.last_bind_data = data;
                int max_count = intent.getIntExtra("max_count", 0);
                AlarmPushAccountActivity.this.addbar.removeAll();
                AlarmPushAccountActivity.this.addbar.setMax_count(max_count);
                Log.e("alarm", "RET_GET_BIND_ALARM_ID : " + max_count);
                for (String showStr2 : data) {
                    if (showStr2.contains(Constants.HIKAM_EMAIL_SUFFIX)) {
                        showStr2 = showStr2.substring(0, showStr2.indexOf(Constants.HIKAM_EMAIL_SUFFIX));
                    }
                    AlarmPushAccountActivity.this.addbar.addItem(showStr2);
                }
                AlarmPushAccountActivity.this.showAlarmIdState();
            } else if (intent.getAction().equals(P2P.RET_SET_BIND_ALARM_ID)) {
                result = intent.getIntExtra("result", -1);
                if (AlarmPushAccountActivity.this.dialog_loading != null && AlarmPushAccountActivity.this.dialog_loading.isShowing()) {
                    AlarmPushAccountActivity.this.dialog_loading.dismiss();
                    AlarmPushAccountActivity.this.dialog_loading = null;
                }
                if (result == 0) {
                    AlarmPushAccountActivity.this.addbar.removeAll();
                    P2PHandler.getInstance().getBindAlarmId(NpcCommon.mThreeNum, AlarmPushAccountActivity.this.contactModel, AlarmPushAccountActivity.this.contactId, AlarmPushAccountActivity.this.contactPassword, AlarmPushAccountActivity.this.account.three_number, AlarmPushAccountActivity.this.account.three_number2);
                    C0568T.showShort(AlarmPushAccountActivity.this.mContext, (int) C0291R.string.modify_success);
                    return;
                }
                C0568T.showShort(AlarmPushAccountActivity.this.mContext, (int) C0291R.string.operator_error);
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_BIND_ALARM_ID)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    if (AlarmPushAccountActivity.this.dialog_loading != null && AlarmPushAccountActivity.this.dialog_loading.isShowing()) {
                        AlarmPushAccountActivity.this.dialog_loading.dismiss();
                        AlarmPushAccountActivity.this.dialog_loading = null;
                    }
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    AlarmPushAccountActivity.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set alarm bind id");
                    P2PHandler.getInstance().setBindAlarmId(AlarmPushAccountActivity.this.contactModel, AlarmPushAccountActivity.this.contactId, AlarmPushAccountActivity.this.contactPassword, AlarmPushAccountActivity.this.last_bind_data.length, AlarmPushAccountActivity.this.last_bind_data, NpcCommon.mThreeNum);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_BIND_ALARM_ID)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    AlarmPushAccountActivity.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:get alarm bind id");
                    P2PHandler.getInstance().getBindAlarmId(NpcCommon.mThreeNum, AlarmPushAccountActivity.this.contactModel, AlarmPushAccountActivity.this.contactId, AlarmPushAccountActivity.this.contactPassword, AlarmPushAccountActivity.this.account.three_number, AlarmPushAccountActivity.this.account.three_number2);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_ALL_ALARM_ACCOUNT)) {
                result = intent.getIntExtra("result", -1);
                AlarmPushAccountActivity.this.accountList = intent.getStringArrayExtra("accountList");
                AlarmPushAccountActivity.this.addbar.removeAll();
                if (size > 0) {
                    AlarmPushAccountActivity.this.addbar.setMax_count(size);
                    for (String showStr22 : AlarmPushAccountActivity.this.accountList) {
                        if (showStr22.contains(Constants.HIKAM_EMAIL_SUFFIX)) {
                            showStr22 = showStr22.substring(0, showStr22.indexOf(Constants.HIKAM_EMAIL_SUFFIX));
                        }
                        AlarmPushAccountActivity.this.addbar.addItem(showStr22);
                        AlarmPushAccountActivity.this.showAlarmIdState();
                    }
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_ALARM_PUSH_STATUS)) {
                result = intent.getIntExtra("result", -1);
                if (AlarmPushAccountActivity.this.dialog_loading != null && AlarmPushAccountActivity.this.dialog_loading.isShowing()) {
                    AlarmPushAccountActivity.this.dialog_loading.dismiss();
                    AlarmPushAccountActivity.this.dialog_loading = null;
                }
                if (result == 0) {
                    AlarmPushAccountActivity.this.addbar.removeAll();
                    P2PHandler.getInstance().getBindAlarmId(NpcCommon.mThreeNum, AlarmPushAccountActivity.this.contactModel, AlarmPushAccountActivity.this.contactId, AlarmPushAccountActivity.this.contactPassword, AlarmPushAccountActivity.this.account.three_number, AlarmPushAccountActivity.this.account.three_number2);
                    C0568T.showShort(AlarmPushAccountActivity.this.mContext, (int) C0291R.string.modify_success);
                    return;
                }
                C0568T.showShort(AlarmPushAccountActivity.this.mContext, (int) C0291R.string.operator_error);
            }
        }
    }

    class C10711 implements OnItemChangeListener {
        C10711() {
        }

        public void onChange(int item) {
            if (item > 0) {
                AlarmPushAccountActivity.this.add_alarm_item.setBackgroundResource(C0291R.drawable.tiao_bg_up);
            } else {
                AlarmPushAccountActivity.this.add_alarm_item.setBackgroundResource(C0291R.drawable.tiao_bg_single);
            }
        }
    }

    class C10732 implements OnLeftIconClickListener {
        C10732() {
        }

        public void onClick(View icon, final int position) {
            String showStr = AlarmPushAccountActivity.this.last_bind_data[position];
            if (showStr.contains(Constants.HIKAM_EMAIL_SUFFIX)) {
                showStr = showStr.substring(0, showStr.indexOf(Constants.HIKAM_EMAIL_SUFFIX));
            }
            NormalDialog dialog = new NormalDialog(AlarmPushAccountActivity.this.mContext, AlarmPushAccountActivity.this.mContext.getResources().getString(C0291R.string.delete_alarm_id), AlarmPushAccountActivity.this.mContext.getResources().getString(C0291R.string.ensure_delete) + showStr + " ?", AlarmPushAccountActivity.this.mContext.getResources().getString(C0291R.string.ensure), AlarmPushAccountActivity.this.mContext.getResources().getString(C0291R.string.cancel));
            final String deleteUserId = AlarmPushAccountActivity.this.last_bind_data[position];
            dialog.setOnButtonOkListener(new OnButtonOkListener() {
                public void onClick() {
                    if (AlarmPushAccountActivity.this.dialog_loading == null) {
                        AlarmPushAccountActivity.this.dialog_loading = new NormalDialog(AlarmPushAccountActivity.this.mContext, AlarmPushAccountActivity.this.mContext.getResources().getString(C0291R.string.verification), "", "", "");
                        AlarmPushAccountActivity.this.dialog_loading.setStyle(2);
                    }
                    AlarmPushAccountActivity.this.dialog_loading.showDialog();
                    String[] data = Utils.getDeleteAlarmIdArray(AlarmPushAccountActivity.this.last_bind_data, position);
                    AlarmPushAccountActivity.this.last_bind_data = data;
                    P2PHandler.getInstance().setBindAlarmId(AlarmPushAccountActivity.this.contactModel, AlarmPushAccountActivity.this.contactId, AlarmPushAccountActivity.this.contactPassword, data.length, data, deleteUserId);
                }
            });
            dialog.showDialog();
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(C0291R.layout.activity_alarm_push_msg);
        this.mContext = this;
        this.contactModel = getIntent().getStringExtra(ContactDB.COLUMN_CONTACT_MODEL);
        this.contactPassword = getIntent().getStringExtra(ContactDB.COLUMN_CONTACT_PASSWORD);
        this.contactId = getIntent().getStringExtra(ContactDB.COLUMN_CONTACT_ID);
        this.account = AccountPersist.getInstance().getActiveAccountInfo(this);
        if (this.account == null) {
            this.account = new Account();
        }
        initComponent();
        regFilter();
        P2PHandler.getInstance().getBindAlarmId(NpcCommon.mThreeNum, this.contactModel, this.contactId, this.contactPassword, this.account.three_number, this.account.three_number2);
    }

    public void initComponent() {
        this.addbar = (AddBar) findViewById(C0291R.id.add_bar);
        this.progressBar_alarmId = (ProgressBar) findViewById(C0291R.id.progressBar_alarmId);
        this.add_alarm_item = (RelativeLayout) findViewById(C0291R.id.add_alarm_item);
        this.back_btn = (ImageView) findViewById(C0291R.id.back_btn);
        this.back_btn.setOnClickListener(this);
        this.addbar.setOnItemChangeListener(new C10711());
        this.addbar.setOnLeftIconClickListener(new C10732());
    }

    public void regFilter() {
        this.isRegFilter = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.ACK_RET_GET_ALL_ALARM_ACCOUNT);
        filter.addAction(P2P.ACK_RET_SET_ALARM_PUSH_STATUS);
        filter.addAction(P2P.RET_GET_BIND_ALARM_ID);
        filter.addAction(P2P.RET_SET_BIND_ALARM_ID);
        filter.addAction(P2P.ACK_RET_SET_BIND_ALARM_ID);
        filter.addAction(P2P.ACK_RET_GET_BIND_ALARM_ID);
        this.mContext.registerReceiver(this.br, filter);
    }

    public void onClick(View v) {
        if (v.getId() == C0291R.id.back_btn) {
            finish();
        }
    }

    public void showAlarmIdState() {
        this.progressBar_alarmId.setVisibility(8);
    }

    public int getActivityInfo() {
        return 53;
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.br);
            this.isRegFilter = false;
        }
    }
}
