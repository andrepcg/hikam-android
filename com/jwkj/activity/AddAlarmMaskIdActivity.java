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
import com.jwkj.data.AlarmMask;
import com.jwkj.data.DataManager;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;

public class AddAlarmMaskIdActivity extends BaseActivity implements OnClickListener {
    private boolean isRegFilter = false;
    EditText mAlarmId;
    ImageView mBack;
    Context mContext;
    private BroadcastReceiver mReceiver = new C03231();
    Button mSave;

    class C03231 extends BroadcastReceiver {
        C03231() {
        }

        public void onReceive(Context arg0, Intent intent) {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_add_alarm_mask_id);
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
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter());
        this.isRegFilter = true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.save:
                String alarmId = this.mAlarmId.getText().toString();
                if (!Utils.checkDevID(alarmId)) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.device_id_format_error);
                    return;
                } else if ("".equals(alarmId.trim())) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.input_alarm_mask_id);
                    return;
                } else if (alarmId.charAt(0) == '0') {
                    C0568T.showShort(this.mContext, (int) C0291R.string.format_error);
                    return;
                } else if (alarmId.length() > 9) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.alarm_mask_id_too_long);
                    return;
                } else {
                    for (AlarmMask alarmMask : DataManager.findAlarmMaskByActiveUser(this.mContext, NpcCommon.mThreeNum)) {
                        if (alarmId.equals(alarmMask.deviceId)) {
                            C0568T.showShort(this.mContext, (int) C0291R.string.account_already_exists_in_mask_list);
                            return;
                        }
                    }
                    AlarmMask alarmMask2 = new AlarmMask();
                    alarmMask2.deviceId = alarmId;
                    alarmMask2.activeUser = NpcCommon.mThreeNum;
                    DataManager.insertAlarmMask(this.mContext, alarmMask2);
                    Intent add_success = new Intent();
                    add_success.setAction(Action.ADD_ALARM_MASK_ID_SUCCESS);
                    add_success.putExtra("alarmMask", alarmMask2);
                    this.mContext.sendBroadcast(add_success);
                    C0568T.showShort(this.mContext, (int) C0291R.string.add_success);
                    finish();
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
        return 6;
    }
}
