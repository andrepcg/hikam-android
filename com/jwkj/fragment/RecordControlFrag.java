package com.jwkj.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.activity.MainControlActivity;
import com.jwkj.adapter.DateNumericAdapter;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.wheel.widget.WheelView;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import java.lang.reflect.Field;
import org.jboss.netty.handler.codec.rtsp.RtspHeaders.Values;

public class RecordControlFrag extends BaseFragment implements OnClickListener {
    Button bt_set_plantime;
    RelativeLayout change_plan_time;
    RelativeLayout change_plan_time_title;
    RelativeLayout change_record;
    RelativeLayout change_record_resolution;
    RelativeLayout change_record_time;
    RelativeLayout change_record_type;
    private Contact contact;
    String cur_modify_plan_time;
    int cur_modify_record_resolution;
    int cur_modify_record_time;
    int cur_modify_record_type;
    WheelView hour_from;
    WheelView hour_to;
    public Boolean isRecordTypeBroadcast = Boolean.valueOf(false);
    private boolean isRegFilter = false;
    int last_modify_record;
    int last_record = 0;
    private Context mContext;
    private BroadcastReceiver mReceiver = new C05391();
    WheelView minute_from;
    WheelView minute_to;
    ProgressBar progressBar_plan_time;
    ProgressBar progressBar_record;
    ProgressBar progressBar_record_resolution;
    ProgressBar progressBar_record_time;
    ProgressBar progressBar_record_type;
    RadioButton radio_one;
    RadioButton radio_one_time;
    RadioButton radio_record_hd;
    RadioButton radio_record_sd;
    RadioButton radio_three;
    RadioButton radio_three_time;
    RadioButton radio_two;
    RadioButton radio_two_time;
    int recordState;
    ImageView record_img;
    LinearLayout record_resolution_radio;
    TextView record_text;
    LinearLayout record_time_radio;
    LinearLayout record_type_radio;
    LinearLayout time_picker;
    TextView time_text;

    class C05391 extends BroadcastReceiver {
        C05391() {
        }

        public void onReceive(Context arg0, Intent intent) {
            int result;
            Intent i;
            if (intent.getAction().equals(P2P.ACK_RET_GET_NPC_SETTINGS)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    RecordControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:get npc settings");
                    RecordControlFrag.this.isRecordTypeBroadcast = Boolean.valueOf(true);
                    P2PHandler.getInstance().getNpcSettings(RecordControlFrag.this.contact.contactModel, RecordControlFrag.this.contact.contactId, RecordControlFrag.this.contact.contactPassword);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_RECORD_RESOLUTION)) {
                RecordControlFrag.this.updateRecordResolution(intent.getIntExtra("resolution", -1));
                RecordControlFrag.this.showRecordResolution();
            } else if (intent.getAction().equals(P2P.RET_GET_RECORD_TYPE)) {
                int type = intent.getIntExtra("type", -1);
                Log.e("tga", "oaosjtype : " + type);
                if (RecordControlFrag.this.isRecordTypeBroadcast.booleanValue()) {
                    Log.e("tga", "oaosjtype get: " + type);
                    RecordControlFrag.this.updateRecordType(type);
                    RecordControlFrag.this.showRecordType();
                    RecordControlFrag.this.isRecordTypeBroadcast = Boolean.valueOf(false);
                    return;
                }
                Log.e("tga", "oaosjtype drop: " + type);
            } else if (intent.getAction().equals(P2P.RET_SET_RECORD_RESOLUTION)) {
                if (intent.getIntExtra("result", -1) == 0) {
                    RecordControlFrag.this.updateRecordResolution(RecordControlFrag.this.cur_modify_record_resolution);
                    RecordControlFrag.this.showRecordResolution();
                    C0568T.showShort(RecordControlFrag.this.mContext, (int) C0291R.string.modify_success);
                    return;
                }
                RecordControlFrag.this.showRecordResolution();
                C0568T.showShort(RecordControlFrag.this.mContext, (int) C0291R.string.operator_error);
            } else if (intent.getAction().equals(P2P.RET_SET_RECORD_TYPE)) {
                if (intent.getIntExtra("result", -1) == 0) {
                    RecordControlFrag.this.updateRecordType(RecordControlFrag.this.cur_modify_record_type);
                    RecordControlFrag.this.showRecordType();
                    C0568T.showShort(RecordControlFrag.this.mContext, (int) C0291R.string.modify_success);
                    return;
                }
                RecordControlFrag.this.showRecordType();
                C0568T.showShort(RecordControlFrag.this.mContext, (int) C0291R.string.operator_error);
            } else if (intent.getAction().equals(P2P.RET_GET_RECORD_TIME)) {
                int time = intent.getIntExtra(Values.TIME, -1);
                if (time == 0) {
                    RecordControlFrag.this.radio_one_time.setChecked(true);
                } else if (time == 1) {
                    RecordControlFrag.this.radio_two_time.setChecked(true);
                } else if (time == 2) {
                    RecordControlFrag.this.radio_three_time.setChecked(true);
                }
                RecordControlFrag.this.radio_one_time.setEnabled(true);
                RecordControlFrag.this.radio_two_time.setEnabled(true);
                RecordControlFrag.this.radio_three_time.setEnabled(true);
                RecordControlFrag.this.progressBar_record_time.setVisibility(8);
            } else if (intent.getAction().equals(P2P.RET_SET_RECORD_TIME)) {
                if (intent.getIntExtra("result", -1) == 0) {
                    if (RecordControlFrag.this.cur_modify_record_time == 0) {
                        RecordControlFrag.this.radio_one_time.setChecked(true);
                    } else if (RecordControlFrag.this.cur_modify_record_time == 1) {
                        RecordControlFrag.this.radio_two_time.setChecked(true);
                    } else if (RecordControlFrag.this.cur_modify_record_time == 2) {
                        RecordControlFrag.this.radio_three_time.setChecked(true);
                    }
                    RecordControlFrag.this.radio_one_time.setEnabled(true);
                    RecordControlFrag.this.radio_two_time.setEnabled(true);
                    RecordControlFrag.this.radio_three_time.setEnabled(true);
                    RecordControlFrag.this.progressBar_record_time.setVisibility(8);
                    C0568T.showShort(RecordControlFrag.this.mContext, (int) C0291R.string.modify_success);
                    return;
                }
                RecordControlFrag.this.radio_one_time.setEnabled(true);
                RecordControlFrag.this.radio_two_time.setEnabled(true);
                RecordControlFrag.this.radio_three_time.setEnabled(true);
                RecordControlFrag.this.progressBar_record_time.setVisibility(8);
                C0568T.showShort(RecordControlFrag.this.mContext, (int) C0291R.string.operator_error);
            } else if (intent.getAction().equals(P2P.RET_GET_RECORD_PLAN_TIME)) {
                RecordControlFrag.this.time_text.setText(intent.getStringExtra(Values.TIME));
                RecordControlFrag.this.change_plan_time.setEnabled(true);
                RecordControlFrag.this.progressBar_plan_time.setVisibility(8);
                RecordControlFrag.this.time_text.setVisibility(0);
            } else if (intent.getAction().equals(P2P.RET_SET_RECORD_PLAN_TIME)) {
                if (intent.getIntExtra("result", -1) == 0) {
                    RecordControlFrag.this.time_text.setText(RecordControlFrag.this.cur_modify_plan_time);
                    RecordControlFrag.this.change_plan_time.setEnabled(true);
                    RecordControlFrag.this.progressBar_plan_time.setVisibility(8);
                    RecordControlFrag.this.time_text.setVisibility(0);
                    C0568T.showShort(RecordControlFrag.this.mContext, (int) C0291R.string.modify_success);
                    return;
                }
                RecordControlFrag.this.change_plan_time.setEnabled(true);
                RecordControlFrag.this.progressBar_plan_time.setVisibility(8);
                RecordControlFrag.this.time_text.setVisibility(0);
                C0568T.showShort(RecordControlFrag.this.mContext, (int) C0291R.string.operator_error);
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_RECORD_RESOLUTION)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    RecordControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    P2PHandler.getInstance().setRecordResolution(RecordControlFrag.this.contact.contactModel, RecordControlFrag.this.contact.contactId, RecordControlFrag.this.contact.contactPassword, RecordControlFrag.this.cur_modify_record_resolution);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_RECORD_TYPE)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    RecordControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set npc settings record type");
                    P2PHandler.getInstance().setRecordType(RecordControlFrag.this.contact.contactModel, RecordControlFrag.this.contact.contactId, RecordControlFrag.this.contact.contactPassword, RecordControlFrag.this.cur_modify_record_type);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_RECORD_TIME)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    RecordControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set npc settings record time");
                    P2PHandler.getInstance().setRecordType(RecordControlFrag.this.contact.contactModel, RecordControlFrag.this.contact.contactId, RecordControlFrag.this.contact.contactPassword, RecordControlFrag.this.cur_modify_record_type);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_RECORD_PLAN_TIME)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    RecordControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set npc settings record plan time");
                    P2PHandler.getInstance().setRecordPlanTime(RecordControlFrag.this.contact.contactModel, RecordControlFrag.this.contact.contactId, RecordControlFrag.this.contact.contactPassword, RecordControlFrag.this.cur_modify_plan_time);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_REMOTE_RECORD)) {
                state = intent.getIntExtra("state", -1);
                RecordControlFrag.this.progressBar_record.setVisibility(8);
                RecordControlFrag.this.record_img.setVisibility(0);
                RecordControlFrag.this.updateRecord(state);
            } else if (intent.getAction().equals(P2P.RET_SET_REMOTE_RECORD)) {
                state = intent.getIntExtra("state", -1);
                RecordControlFrag.this.isRecordTypeBroadcast = Boolean.valueOf(true);
                P2PHandler.getInstance().getNpcSettings(RecordControlFrag.this.contact.contactModel, RecordControlFrag.this.contact.contactId, RecordControlFrag.this.contact.contactPassword);
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_REMOTE_RECORD)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    RecordControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set remote record");
                    P2PHandler.getInstance().setRemoteRecord(RecordControlFrag.this.contact.contactModel, RecordControlFrag.this.contact.contactId, RecordControlFrag.this.contact.contactPassword, RecordControlFrag.this.last_modify_record);
                }
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mContext = MainControlActivity.mContext;
        this.contact = (Contact) getArguments().getSerializable(ContactDB.TABLE_NAME);
        View view = inflater.inflate(C0291R.layout.fragment_record_control, container, false);
        initComponent(view);
        regFilter();
        showProgress_record_type();
        this.isRecordTypeBroadcast = Boolean.valueOf(true);
        P2PHandler.getInstance().getNpcSettings(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
        return view;
    }

    public void initComponent(View view) {
        this.change_record_resolution = (RelativeLayout) view.findViewById(C0291R.id.change_record_resolution);
        this.record_resolution_radio = (LinearLayout) view.findViewById(C0291R.id.record_resolution_radio);
        this.progressBar_record_resolution = (ProgressBar) view.findViewById(C0291R.id.progressBar_record_resolution);
        this.radio_record_sd = (RadioButton) view.findViewById(C0291R.id.radio_record_sd);
        this.radio_record_hd = (RadioButton) view.findViewById(C0291R.id.radio_record_hd);
        this.change_record_type = (RelativeLayout) view.findViewById(C0291R.id.change_record_type);
        this.record_type_radio = (LinearLayout) view.findViewById(C0291R.id.record_type_radio);
        this.progressBar_record_type = (ProgressBar) view.findViewById(C0291R.id.progressBar_record_type);
        this.radio_one = (RadioButton) view.findViewById(C0291R.id.radio_one);
        this.radio_two = (RadioButton) view.findViewById(C0291R.id.radio_two);
        this.radio_three = (RadioButton) view.findViewById(C0291R.id.radio_three);
        this.change_record_time = (RelativeLayout) view.findViewById(C0291R.id.change_record_time);
        this.record_time_radio = (LinearLayout) view.findViewById(C0291R.id.record_time_radio);
        this.progressBar_record_time = (ProgressBar) view.findViewById(C0291R.id.progressBar_record_time);
        this.radio_one_time = (RadioButton) view.findViewById(C0291R.id.radio_one_time);
        this.radio_two_time = (RadioButton) view.findViewById(C0291R.id.radio_two_time);
        this.radio_three_time = (RadioButton) view.findViewById(C0291R.id.radio_three_time);
        this.bt_set_plantime = (Button) view.findViewById(C0291R.id.bt_set_plantime);
        this.change_plan_time = (RelativeLayout) view.findViewById(C0291R.id.change_plan_time);
        this.change_plan_time_title = (RelativeLayout) view.findViewById(C0291R.id.change_plan_time_title);
        this.progressBar_plan_time = (ProgressBar) view.findViewById(C0291R.id.progressBar_plan_time);
        this.time_picker = (LinearLayout) view.findViewById(C0291R.id.time_picker);
        this.time_text = (TextView) view.findViewById(C0291R.id.time_text);
        initTimePicker(view);
        this.bt_set_plantime.setOnClickListener(this);
        this.radio_record_sd.setOnClickListener(this);
        this.radio_record_hd.setOnClickListener(this);
        this.radio_one.setOnClickListener(this);
        this.radio_two.setOnClickListener(this);
        this.radio_three.setOnClickListener(this);
        this.radio_one_time.setOnClickListener(this);
        this.radio_two_time.setOnClickListener(this);
        this.radio_three_time.setOnClickListener(this);
        this.change_record = (RelativeLayout) view.findViewById(C0291R.id.change_record);
        this.record_img = (ImageView) view.findViewById(C0291R.id.record_img);
        this.record_text = (TextView) view.findViewById(C0291R.id.record_text);
        this.progressBar_record = (ProgressBar) view.findViewById(C0291R.id.progressBar_record);
        this.change_record.setOnClickListener(this);
        if (!P2PValue.HikamDeviceModelList.contains(this.contact.contactModel)) {
            this.change_record_resolution.setVisibility(8);
            this.record_resolution_radio.setVisibility(8);
        }
    }

    public void initTimePicker(View view) {
        this.hour_from = (WheelView) view.findViewById(C0291R.id.hour_from);
        this.hour_from.setViewAdapter(new DateNumericAdapter(this.mContext, 0, 23));
        this.hour_from.setCyclic(true);
        this.minute_from = (WheelView) view.findViewById(C0291R.id.minute_from);
        this.minute_from.setViewAdapter(new DateNumericAdapter(this.mContext, 0, 59));
        this.minute_from.setCyclic(true);
        this.hour_to = (WheelView) view.findViewById(C0291R.id.hour_to);
        this.hour_to.setViewAdapter(new DateNumericAdapter(this.mContext, 0, 23));
        this.hour_to.setCyclic(true);
        this.minute_to = (WheelView) view.findViewById(C0291R.id.minute_to);
        this.minute_to.setViewAdapter(new DateNumericAdapter(this.mContext, 0, 59));
        this.minute_to.setCyclic(true);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.ACK_RET_GET_NPC_SETTINGS);
        filter.addAction(P2P.ACK_RET_SET_RECORD_RESOLUTION);
        filter.addAction(P2P.RET_SET_RECORD_RESOLUTION);
        filter.addAction(P2P.RET_GET_RECORD_RESOLUTION);
        filter.addAction(P2P.ACK_RET_SET_RECORD_TYPE);
        filter.addAction(P2P.RET_SET_RECORD_TYPE);
        filter.addAction(P2P.RET_GET_RECORD_TYPE);
        filter.addAction(P2P.ACK_RET_SET_RECORD_TIME);
        filter.addAction(P2P.RET_SET_RECORD_TIME);
        filter.addAction(P2P.RET_GET_RECORD_TIME);
        filter.addAction(P2P.ACK_RET_SET_RECORD_PLAN_TIME);
        filter.addAction(P2P.RET_SET_RECORD_PLAN_TIME);
        filter.addAction(P2P.RET_GET_RECORD_PLAN_TIME);
        filter.addAction(P2P.ACK_RET_SET_REMOTE_RECORD);
        filter.addAction(P2P.RET_SET_REMOTE_RECORD);
        filter.addAction(P2P.RET_GET_REMOTE_RECORD);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void updateRecord(int state) {
        if (state == 1) {
            this.last_record = 1;
            this.record_img.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
            return;
        }
        this.last_record = 0;
        this.record_img.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
    }

    void updateRecordResolution(int type) {
        if (type == 0) {
            this.radio_record_sd.setChecked(true);
        } else if (type == 1) {
            this.radio_record_hd.setChecked(true);
        }
    }

    void updateRecordType(int type) {
        if (type == 0) {
            this.radio_one.setChecked(true);
            hideRecordTime();
            hidePlanTime();
            showManual();
        } else if (type == 1) {
            this.radio_two.setChecked(true);
            hidePlanTime();
            hideManual();
            showRecordTime();
        } else if (type == 2) {
            this.radio_three.setChecked(true);
            hideRecordTime();
            hideManual();
            showPlanTime();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.bt_set_plantime:
                showProgress_plan_time();
                this.cur_modify_plan_time = Utils.convertPlanTime(this.hour_from.getCurrentItem(), this.minute_from.getCurrentItem(), this.hour_to.getCurrentItem(), this.minute_to.getCurrentItem());
                P2PHandler.getInstance().setRecordPlanTime(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.cur_modify_plan_time);
                return;
            case C0291R.id.change_plan_time:
                showProgress_plan_time();
                this.cur_modify_plan_time = Utils.convertPlanTime(this.hour_from.getCurrentItem(), this.minute_from.getCurrentItem(), this.hour_to.getCurrentItem(), this.minute_to.getCurrentItem());
                P2PHandler.getInstance().setRecordPlanTime(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.cur_modify_plan_time);
                return;
            case C0291R.id.change_record:
                this.progressBar_record.setVisibility(0);
                this.record_img.setVisibility(8);
                if (this.last_record == 1) {
                    this.last_modify_record = 0;
                    P2PHandler.getInstance().setRemoteRecord(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.last_modify_record);
                    return;
                }
                this.last_modify_record = 1;
                P2PHandler.getInstance().setRemoteRecord(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.last_modify_record);
                return;
            case C0291R.id.radio_one:
                this.progressBar_record_type.setVisibility(0);
                this.radio_one.setEnabled(false);
                this.radio_two.setEnabled(false);
                this.radio_three.setEnabled(false);
                this.cur_modify_record_type = 0;
                P2PHandler.getInstance().setRecordType(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.cur_modify_record_type);
                return;
            case C0291R.id.radio_one_time:
                this.progressBar_record_time.setVisibility(0);
                this.radio_one_time.setEnabled(false);
                this.radio_two_time.setEnabled(false);
                this.radio_three_time.setEnabled(false);
                this.cur_modify_record_time = 0;
                P2PHandler.getInstance().setRecordTime(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.cur_modify_record_time);
                return;
            case C0291R.id.radio_record_hd:
                this.progressBar_record_resolution.setVisibility(0);
                this.radio_record_sd.setEnabled(false);
                this.radio_record_hd.setEnabled(false);
                this.cur_modify_record_resolution = 1;
                P2PHandler.getInstance().setRecordResolution(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.cur_modify_record_resolution);
                return;
            case C0291R.id.radio_record_sd:
                this.progressBar_record_resolution.setVisibility(0);
                this.radio_record_sd.setEnabled(false);
                this.radio_record_hd.setEnabled(false);
                this.cur_modify_record_resolution = 0;
                P2PHandler.getInstance().setRecordResolution(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.cur_modify_record_resolution);
                return;
            case C0291R.id.radio_three:
                this.radio_one.setEnabled(false);
                this.radio_two.setEnabled(false);
                this.radio_three.setEnabled(false);
                this.progressBar_record_type.setVisibility(0);
                this.cur_modify_record_type = 2;
                P2PHandler.getInstance().setRecordType(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.cur_modify_record_type);
                return;
            case C0291R.id.radio_three_time:
                this.progressBar_record_time.setVisibility(0);
                this.radio_one_time.setEnabled(false);
                this.radio_two_time.setEnabled(false);
                this.radio_three_time.setEnabled(false);
                this.cur_modify_record_time = 2;
                P2PHandler.getInstance().setRecordTime(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.cur_modify_record_time);
                return;
            case C0291R.id.radio_two:
                this.progressBar_record_type.setVisibility(0);
                this.radio_one.setEnabled(false);
                this.radio_two.setEnabled(false);
                this.radio_three.setEnabled(false);
                this.cur_modify_record_type = 1;
                P2PHandler.getInstance().setRecordType(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.cur_modify_record_type);
                return;
            case C0291R.id.radio_two_time:
                this.progressBar_record_time.setVisibility(0);
                this.radio_one_time.setEnabled(false);
                this.radio_two_time.setEnabled(false);
                this.radio_three_time.setEnabled(false);
                this.cur_modify_record_time = 1;
                P2PHandler.getInstance().setRecordTime(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.cur_modify_record_time);
                return;
            default:
                return;
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.isRegFilter = false;
        }
    }

    public void showRecordResolution() {
        this.change_record_resolution.setBackgroundResource(C0291R.drawable.tiao_bg_up);
        this.progressBar_record_resolution.setVisibility(8);
        this.record_resolution_radio.setVisibility(0);
        this.radio_record_sd.setEnabled(true);
        this.radio_record_hd.setEnabled(true);
    }

    public void showRecordType() {
        this.change_record_type.setBackgroundResource(C0291R.drawable.tiao_bg_up);
        this.progressBar_record_type.setVisibility(8);
        this.record_type_radio.setVisibility(0);
        this.radio_one.setEnabled(true);
        this.radio_two.setEnabled(true);
        this.radio_three.setEnabled(true);
    }

    public void showProgress_record_type() {
        this.change_record_type.setBackgroundResource(C0291R.drawable.tiao_bg_single);
        this.progressBar_record_type.setVisibility(0);
        this.record_type_radio.setVisibility(8);
    }

    public void showRecordTime() {
        this.change_record_time.setVisibility(0);
        this.record_time_radio.setVisibility(0);
        this.progressBar_record_time.setVisibility(8);
    }

    public void showProgress_record_time() {
        this.change_record_time.setVisibility(0);
        this.record_time_radio.setVisibility(0);
        this.progressBar_record_time.setVisibility(0);
    }

    public void showPlanTime() {
        this.time_picker.setVisibility(0);
        this.change_plan_time.setVisibility(0);
        this.change_plan_time_title.setVisibility(0);
        this.progressBar_plan_time.setVisibility(8);
        this.time_text.setVisibility(0);
    }

    public void showProgress_plan_time() {
        this.time_picker.setVisibility(0);
        this.change_plan_time.setVisibility(0);
        this.change_plan_time.setEnabled(false);
        this.progressBar_plan_time.setVisibility(0);
        this.time_text.setVisibility(8);
    }

    public void showManual() {
        this.change_record.setVisibility(0);
    }

    public void hideRecordTime() {
        this.change_record_time.setVisibility(8);
        this.record_time_radio.setVisibility(8);
    }

    public void hidePlanTime() {
        this.change_plan_time_title.setVisibility(8);
        this.change_plan_time.setVisibility(8);
        this.time_picker.setVisibility(8);
    }

    public void hideManual() {
        this.change_record.setVisibility(8);
    }

    public void onDestroy() {
        super.onDestroy();
        Intent it = new Intent();
        it.setAction(Action.CONTROL_BACK);
        this.mContext.sendBroadcast(it);
    }

    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e2) {
            throw new RuntimeException(e2);
        }
    }
}
