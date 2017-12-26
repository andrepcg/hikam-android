package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.hikam.C0291R;
import com.jwkj.adapter.DateNumericAdapter;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.P2P;
import com.jwkj.utils.C0568T;
import com.jwkj.wheel.widget.WheelView;
import com.jwkj.widget.NormalDialog;
import com.lib.addBar.AddBar;
import com.lib.addBar.OnItemChangeListener;
import com.lib.addBar.OnLeftIconClickListener;
import com.p2p.core.P2PHandler;
import java.util.ArrayList;
import java.util.List;

public class AlarmTimingActivity extends BaseActivity implements OnClickListener {
    private AddBar addbar;
    private Button btn_apply;
    private ImageView btn_back;
    private Button btn_cancel;
    private Button btn_save;
    String contactId;
    String contactModel;
    String contactPassword;
    private NormalDialog dialog;
    private List<Integer> global_end_hour = new ArrayList();
    private List<Integer> global_end_min = new ArrayList();
    private List<Integer> global_start_hour = new ArrayList();
    private List<Integer> global_start_min = new ArrayList();
    WheelView hour_from;
    WheelView hour_to;
    private boolean isDatePickerShow = false;
    boolean isRegFilter;
    private LinearLayout ll_date_picker;
    private BroadcastReceiver mReceiver = new C03593();
    WheelView minute_from;
    WheelView minute_to;
    private RelativeLayout rl_add;

    class C03593 extends BroadcastReceiver {
        C03593() {
        }

        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            if (P2P.RET_SET_ALARM_TIMING.equals(action)) {
                int result = intent.getIntExtra("result", -1);
                if (AlarmTimingActivity.this.dialog != null && AlarmTimingActivity.this.dialog.isShowing()) {
                    AlarmTimingActivity.this.dialog.dismiss();
                }
                if (result == 0) {
                    C0568T.showShort(AlarmTimingActivity.this, (int) C0291R.string.save_success);
                } else {
                    C0568T.showShort(AlarmTimingActivity.this, (int) C0291R.string.save_failed);
                }
            } else if (!P2P.RET_GET_ALARM_TIMING.equals(action)) {
            } else {
                if (intent.getIntExtra("result", -1) != 0) {
                    C0568T.showShort(AlarmTimingActivity.this, (int) C0291R.string.exception);
                    return;
                }
                int[] sH = intent.getIntArrayExtra("startH");
                int[] sM = intent.getIntArrayExtra("startM");
                int[] eH = intent.getIntArrayExtra("endH");
                int[] eM = intent.getIntArrayExtra("endM");
                if (sH.length != 0) {
                    AlarmTimingActivity.this.addSchedule(sH, sM, eH, eM);
                }
            }
        }
    }

    class C10781 implements OnItemChangeListener {
        C10781() {
        }

        public void onChange(int item) {
            if (item > 0) {
                AlarmTimingActivity.this.rl_add.setBackgroundResource(C0291R.drawable.tiao_bg_up);
            } else {
                AlarmTimingActivity.this.rl_add.setBackgroundResource(C0291R.drawable.tiao_bg_single);
            }
        }
    }

    class C10792 implements OnLeftIconClickListener {
        C10792() {
        }

        public void onClick(View icon, int position) {
            AlarmTimingActivity.this.deleteShceduleItem(position);
            AlarmTimingActivity.this.btn_save.performClick();
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(C0291R.layout.activity_alarm_timing);
        this.contactModel = getIntent().getStringExtra(ContactDB.COLUMN_CONTACT_MODEL);
        this.contactPassword = getIntent().getStringExtra(ContactDB.COLUMN_CONTACT_PASSWORD);
        this.contactId = getIntent().getStringExtra(ContactDB.COLUMN_CONTACT_ID);
        initComponent();
        regFilter();
        P2PHandler.getInstance().GetAlarmTiming(this.contactModel, this.contactId, this.contactPassword);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.RET_SET_ALARM_TIMING);
        filter.addAction(P2P.RET_GET_ALARM_TIMING);
        registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    private void initComponent() {
        this.btn_back = (ImageView) findViewById(C0291R.id.back_btn);
        this.btn_save = (Button) findViewById(C0291R.id.save);
        this.rl_add = (RelativeLayout) findViewById(C0291R.id.add_alarm_items);
        this.btn_cancel = (Button) findViewById(C0291R.id.date_cancel);
        this.btn_apply = (Button) findViewById(C0291R.id.save_btn);
        this.ll_date_picker = (LinearLayout) findViewById(C0291R.id.date_pick);
        this.btn_back.setOnClickListener(this);
        this.btn_save.setOnClickListener(this);
        this.btn_save.setVisibility(8);
        this.rl_add.setOnClickListener(this);
        this.btn_cancel.setOnClickListener(this);
        this.btn_apply.setOnClickListener(this);
        this.hour_from = (WheelView) findViewById(C0291R.id.hour_from);
        this.hour_from.setViewAdapter(new DateNumericAdapter(this, 0, 23));
        this.hour_from.setCurrentItem(8);
        this.hour_from.setCyclic(true);
        this.minute_from = (WheelView) findViewById(C0291R.id.minute_from);
        this.minute_from.setViewAdapter(new DateNumericAdapter(this, 0, 59));
        this.minute_from.setCyclic(true);
        this.hour_to = (WheelView) findViewById(C0291R.id.hour_to);
        this.hour_to.setViewAdapter(new DateNumericAdapter(this, 0, 23));
        this.hour_to.setCurrentItem(23);
        this.hour_to.setCyclic(true);
        this.minute_to = (WheelView) findViewById(C0291R.id.minute_to);
        this.minute_to.setViewAdapter(new DateNumericAdapter(this, 0, 59));
        this.minute_to.setCyclic(true);
        this.addbar = (AddBar) findViewById(C0291R.id.add_bar);
        this.addbar.setMax_count(3);
        this.addbar.setArrowVisiable(false);
        this.addbar.setOnItemChangeListener(new C10781());
        this.addbar.setOnLeftIconClickListener(new C10792());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.add_alarm_items:
                if (!this.isDatePickerShow) {
                    show();
                    return;
                }
                return;
            case C0291R.id.back_btn:
                if (this.isDatePickerShow) {
                    close();
                }
                finish();
                return;
            case C0291R.id.date_cancel:
                close();
                return;
            case C0291R.id.save:
                if (this.dialog == null || !this.dialog.isShowing()) {
                    this.dialog = new NormalDialog(this);
                    this.dialog.setTitle((int) C0291R.string.upload);
                    this.dialog.showLoadingDialog();
                    this.dialog.setCanceledOnTouchOutside(false);
                    saveSchedule();
                    return;
                }
                return;
            case C0291R.id.save_btn:
                if (this.addbar.getItemCount() >= 3) {
                    C0568T.showShort((Context) this, (int) C0291R.string.tip_no_more3);
                    return;
                } else {
                    addSheduleItem(this.hour_from.getCurrentItem(), this.minute_from.getCurrentItem(), this.hour_to.getCurrentItem(), this.minute_to.getCurrentItem());
                    return;
                }
            default:
                return;
        }
    }

    public String getScheduleByData(int hour_from, int min_from, int hour_to, int min_to) {
        StringBuilder builder = new StringBuilder();
        if (hour_from >= 0 && hour_from < 10) {
            builder.append("0");
        }
        builder.append(hour_from);
        builder.append(":");
        if (min_from >= 0 && min_from < 10) {
            builder.append("0");
        }
        builder.append(min_from);
        builder.append(" -- ");
        if (hour_to >= 0 && hour_to < 10) {
            builder.append("0");
        }
        builder.append(hour_to);
        builder.append(":");
        if (min_to >= 0 && min_to < 10) {
            builder.append("0");
        }
        builder.append(min_to);
        return builder.toString();
    }

    public void show() {
        this.isDatePickerShow = true;
        Animation animIn = AnimationUtils.loadAnimation(this, C0291R.anim.slide_in_bottom);
        this.ll_date_picker.setVisibility(0);
        this.ll_date_picker.startAnimation(animIn);
    }

    public void close() {
        this.isDatePickerShow = false;
        this.ll_date_picker.startAnimation(AnimationUtils.loadAnimation(this, C0291R.anim.slide_out_top));
        this.ll_date_picker.setVisibility(8);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.isRegFilter = false;
            unregisterReceiver(this.mReceiver);
        }
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.dismiss();
            this.dialog = null;
        }
    }

    public int getActivityInfo() {
        return 0;
    }

    public void addSchedule(int[] sH, int[] sM, int[] eH, int[] eM) {
        if (this.addbar.getItemCount() != 0) {
            this.addbar.removeAll();
        }
        int count = sH.length;
        if (count != 0) {
            for (int i = 0; i < count; i++) {
                this.global_start_hour.add(Integer.valueOf(sH[i]));
                this.global_start_min.add(Integer.valueOf(sM[i]));
                this.global_end_hour.add(Integer.valueOf(eH[i]));
                this.global_end_min.add(Integer.valueOf(eM[i]));
                this.addbar.addItem(getScheduleByData(sH[i], sM[i], eH[i], eM[i]));
            }
        }
    }

    public void addSheduleItem(int sH, int sM, int eH, int eM) {
        if (eH < sH || (eH == sH && eM <= sM)) {
            C0568T.showShort((Context) this, (int) C0291R.string.tip_alarm_timing);
            return;
        }
        int size = this.global_start_hour.size();
        if (size != 0) {
            int i = 0;
            while (i < size) {
                int startTag = (((Integer) this.global_start_hour.get(i)).intValue() * 60) + ((Integer) this.global_start_min.get(i)).intValue();
                int endTag = (((Integer) this.global_end_hour.get(i)).intValue() * 60) + ((Integer) this.global_end_min.get(i)).intValue();
                int newStartTag = (sH * 60) + sM;
                int newEndTag = (eH * 60) + eM;
                Log.e("TIMIE", "" + startTag + " -- " + endTag + " -- " + newStartTag + " -- " + newEndTag);
                if ((newStartTag < startTag || newStartTag > endTag) && ((newEndTag < startTag || newEndTag > endTag) && (newStartTag > startTag || newEndTag < endTag))) {
                    i++;
                } else {
                    C0568T.showShort((Context) this, (int) C0291R.string.tip_alarm_timing_conflict);
                    return;
                }
            }
        }
        this.global_start_hour.add(Integer.valueOf(sH));
        this.global_start_min.add(Integer.valueOf(sM));
        this.global_end_hour.add(Integer.valueOf(eH));
        this.global_end_min.add(Integer.valueOf(eM));
        this.addbar.addItem(getScheduleByData(sH, sM, eH, eM));
        this.btn_save.performClick();
    }

    public void saveSchedule() {
        int count = this.addbar.getItemCount();
        int[] sH = new int[count];
        int[] sM = new int[count];
        int[] eH = new int[count];
        int[] eM = new int[count];
        for (int i = 0; i < count; i++) {
            sH[i] = ((Integer) this.global_start_hour.get(i)).intValue();
            sM[i] = ((Integer) this.global_start_min.get(i)).intValue();
            eH[i] = ((Integer) this.global_end_hour.get(i)).intValue();
            eM[i] = ((Integer) this.global_end_min.get(i)).intValue();
        }
        P2PHandler.getInstance().SetAlarmTiming(this.contactModel, this.contactId, this.contactPassword, sH, sM, eH, eM);
    }

    public void deleteShceduleItem(int position) {
        this.addbar.removeItem(position);
        this.global_start_hour.remove(position);
        this.global_start_min.remove(position);
        this.global_end_hour.remove(position);
        this.global_end_min.remove(position);
    }
}
