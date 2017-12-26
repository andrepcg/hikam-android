package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.adapter.DateNumericAdapter;
import com.jwkj.data.AlarmMask;
import com.jwkj.data.DataManager;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.wheel.widget.WheelView;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import com.lib.addBar.AddBar;
import com.lib.addBar.OnItemChangeListener;
import com.lib.addBar.OnLeftIconClickListener;
import java.util.ArrayList;
import java.util.List;

public class AlarmSetActivity extends BaseActivity implements OnClickListener {
    AddBar addBar;
    RelativeLayout add_alarm_item;
    RelativeLayout alarm_record;
    ImageView back_btn;
    WheelView date_seconds;
    NormalDialog dialog;
    private boolean isRegFilter = false;
    Context mContext;
    List<AlarmMask> mList = new ArrayList();
    private BroadcastReceiver mReceiver = new C03583();
    RelativeLayout setting_time;
    TextView time_text;

    class C03583 extends BroadcastReceiver {
        C03583() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Action.ADD_ALARM_MASK_ID_SUCCESS)) {
                AlarmMask alarmMask = (AlarmMask) intent.getSerializableExtra("alarmMask");
                AlarmSetActivity.this.addBar.addItem(alarmMask.deviceId);
                AlarmSetActivity.this.mList.add(alarmMask);
            }
        }
    }

    class C10751 implements OnItemChangeListener {
        C10751() {
        }

        public void onChange(int item) {
            if (item > 0) {
                AlarmSetActivity.this.add_alarm_item.setBackgroundResource(C0291R.drawable.tiao_bg_up);
            } else {
                AlarmSetActivity.this.add_alarm_item.setBackgroundResource(C0291R.drawable.tiao_bg_single);
            }
        }
    }

    class C10772 implements OnLeftIconClickListener {
        C10772() {
        }

        public void onClick(View icon, final int position) {
            final AlarmMask alarmMask = (AlarmMask) AlarmSetActivity.this.mList.get(position);
            AlarmSetActivity.this.dialog = new NormalDialog(AlarmSetActivity.this.mContext, AlarmSetActivity.this.mContext.getResources().getString(C0291R.string.cancel_shield), AlarmSetActivity.this.mContext.getResources().getString(C0291R.string.ensure_cancel_shield) + " " + alarmMask.deviceId + "?", AlarmSetActivity.this.mContext.getResources().getString(C0291R.string.ensure), AlarmSetActivity.this.mContext.getResources().getString(C0291R.string.cancel));
            AlarmSetActivity.this.dialog.setOnButtonOkListener(new OnButtonOkListener() {
                public void onClick() {
                    DataManager.deleteAlarmMask(AlarmSetActivity.this.mContext, NpcCommon.mThreeNum, alarmMask.deviceId);
                    AlarmSetActivity.this.mList.remove(position);
                    AlarmSetActivity.this.addBar.removeItem(position);
                }
            });
            AlarmSetActivity.this.dialog.showDialog();
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(C0291R.layout.activity_alarm_set);
        this.mContext = this;
        initComponent();
        regFilter();
    }

    public void initComponent() {
        int time_interval = SharedPreferencesManager.getInstance().getAlarmTimeInterval(this.mContext);
        this.date_seconds = (WheelView) findViewById(C0291R.id.date_seconds);
        this.date_seconds.setViewAdapter(new DateNumericAdapter(this.mContext, 1, 90));
        this.date_seconds.setCurrentItem(time_interval - 1);
        this.date_seconds.setCyclic(true);
        this.back_btn = (ImageView) findViewById(C0291R.id.back_btn);
        this.setting_time = (RelativeLayout) findViewById(C0291R.id.setting_time);
        this.time_text = (TextView) findViewById(C0291R.id.time_text);
        this.time_text.setText(String.valueOf(time_interval));
        this.add_alarm_item = (RelativeLayout) findViewById(C0291R.id.add_alarm_item);
        this.alarm_record = (RelativeLayout) findViewById(C0291R.id.alarm_record);
        this.addBar = (AddBar) findViewById(C0291R.id.add_bar);
        this.addBar.setMax_count(999);
        this.addBar.setArrowVisiable(false);
        this.addBar.setOnItemChangeListener(new C10751());
        this.addBar.setOnLeftIconClickListener(new C10772());
        this.mList = DataManager.findAlarmMaskByActiveUser(this.mContext, NpcCommon.mThreeNum);
        for (AlarmMask alarmMask : this.mList) {
            this.addBar.addItem(alarmMask.deviceId);
        }
        this.alarm_record.setOnClickListener(this);
        this.add_alarm_item.setOnClickListener(this);
        this.setting_time.setOnClickListener(this);
        this.back_btn.setOnClickListener(this);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.ADD_ALARM_MASK_ID_SUCCESS);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.add_alarm_item:
                this.mContext.startActivity(new Intent(this.mContext, AddAlarmMaskIdActivity.class));
                return;
            case C0291R.id.alarm_record:
                this.mContext.startActivity(new Intent(this.mContext, AlarmRecordActivity.class));
                return;
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.setting_time:
                SharedPreferencesManager.getInstance().putAlarmTimeInterval(this.mContext, this.date_seconds.getCurrentItem() + 1);
                Log.e("my", this.date_seconds.getCurrentItem() + "");
                this.time_text.setText(String.valueOf(this.date_seconds.getCurrentItem() + 1));
                C0568T.showShort(this.mContext, (int) C0291R.string.modify_success);
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
        return 10;
    }
}
