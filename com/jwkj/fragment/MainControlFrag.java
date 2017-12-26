package com.jwkj.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.hikam.C0291R;
import com.jwkj.activity.DeviceUpdateActivity;
import com.jwkj.activity.MainControlActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.Action;
import com.p2p.core.P2PValue;
import com.p2p.core.P2PValue.HikamDeviceModel;
import java.lang.reflect.Field;

public class MainControlFrag extends BaseFragment implements OnClickListener {
    RelativeLayout alarm_control;
    RelativeLayout chekc_device_update;
    RelativeLayout defenceArea_control;
    RelativeLayout device_control;
    private Contact mContact;
    private Context mContext;
    RelativeLayout name_control;
    RelativeLayout net_control;
    RelativeLayout record_control;
    RelativeLayout remote_control;
    RelativeLayout sd_card_control;
    RelativeLayout security_control;
    RelativeLayout time_contrl;
    RelativeLayout video_control;
    private boolean wrongPwd = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mContext = MainControlActivity.mContext;
        this.mContact = (Contact) getArguments().getSerializable(ContactDB.TABLE_NAME);
        this.wrongPwd = getArguments().getBoolean("wrongPwd", false);
        View view = inflater.inflate(C0291R.layout.fragment_control_main, container, false);
        initComponent(view);
        return view;
    }

    public void initComponent(View view) {
        this.name_control = (RelativeLayout) view.findViewById(C0291R.id.name_control);
        this.time_contrl = (RelativeLayout) view.findViewById(C0291R.id.time_control);
        this.remote_control = (RelativeLayout) view.findViewById(C0291R.id.remote_control);
        this.alarm_control = (RelativeLayout) view.findViewById(C0291R.id.alarm_control);
        this.video_control = (RelativeLayout) view.findViewById(C0291R.id.video_control);
        this.record_control = (RelativeLayout) view.findViewById(C0291R.id.record_control);
        this.security_control = (RelativeLayout) view.findViewById(C0291R.id.security_control);
        this.net_control = (RelativeLayout) view.findViewById(C0291R.id.net_control);
        this.defenceArea_control = (RelativeLayout) view.findViewById(C0291R.id.defenceArea_control);
        this.chekc_device_update = (RelativeLayout) view.findViewById(C0291R.id.check_device_update);
        this.sd_card_control = (RelativeLayout) view.findViewById(C0291R.id.sd_card_control);
        this.device_control = (RelativeLayout) view.findViewById(C0291R.id.device_control);
        this.name_control.setOnClickListener(this);
        this.defenceArea_control.setOnClickListener(this);
        this.net_control.setOnClickListener(this);
        this.security_control.setOnClickListener(this);
        this.record_control.setOnClickListener(this);
        this.video_control.setOnClickListener(this);
        this.time_contrl.setOnClickListener(this);
        this.remote_control.setOnClickListener(this);
        this.alarm_control.setOnClickListener(this);
        this.chekc_device_update.setOnClickListener(this);
        this.sd_card_control.setOnClickListener(this);
        this.device_control.setOnClickListener(this);
        if (this.wrongPwd) {
            disableViewsWhileWrongPwd();
        }
        if (this.mContact.contactType == 3) {
            view.findViewById(C0291R.id.control_main_frame).setVisibility(8);
        } else if (this.mContact.contactType == 2) {
            this.chekc_device_update.setVisibility(8);
            this.sd_card_control.setBackgroundResource(C0291R.drawable.tiao_bg_bottom);
        } else {
            this.chekc_device_update.setVisibility(0);
            this.defenceArea_control.setBackgroundResource(C0291R.drawable.tiao_bg_center);
        }
        if (P2PValue.HikamDeviceModelList.contains(this.mContact.contactModel) || HikamDeviceModel.Q5.equals(this.mContact.contactModel)) {
            this.device_control.setVisibility(0);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.alarm_control:
                Intent go_alarm_control = new Intent();
                go_alarm_control.setAction(Action.REPLACE_ALARM_CONTROL);
                go_alarm_control.putExtra("isEnforce", true);
                this.mContext.sendBroadcast(go_alarm_control);
                return;
            case C0291R.id.check_device_update:
                Intent check_update = new Intent(this.mContext, DeviceUpdateActivity.class);
                check_update.putExtra(ContactDB.TABLE_NAME, this.mContact);
                this.mContext.startActivity(check_update);
                return;
            case C0291R.id.defenceArea_control:
                Intent go_da_control = new Intent();
                go_da_control.setAction(Action.REPLACE_DEFENCE_AREA_CONTROL);
                go_da_control.putExtra("isEnforce", true);
                this.mContext.sendBroadcast(go_da_control);
                return;
            case C0291R.id.device_control:
                Intent go_device_control = new Intent();
                go_device_control.setAction(Action.REPLACE_DEVICE_CONTROL);
                go_device_control.putExtra("isEnforce", true);
                this.mContext.sendBroadcast(go_device_control);
                return;
            case C0291R.id.name_control:
                Intent go_name_control = new Intent();
                go_name_control.setAction(Action.REPLACE_SETTING_NAME);
                go_name_control.putExtra("isEnforce", true);
                this.mContext.sendBroadcast(go_name_control);
                return;
            case C0291R.id.net_control:
                Intent go_net_control = new Intent();
                go_net_control.setAction(Action.REPLACE_NET_CONTROL);
                go_net_control.putExtra("isEnforce", true);
                this.mContext.sendBroadcast(go_net_control);
                return;
            case C0291R.id.record_control:
                Intent go_record_control = new Intent();
                go_record_control.setAction(Action.REPLACE_RECORD_CONTROL);
                go_record_control.putExtra("isEnforce", true);
                this.mContext.sendBroadcast(go_record_control);
                return;
            case C0291R.id.remote_control:
                Intent go_remote_control = new Intent();
                go_remote_control.setAction(Action.REPLACE_REMOTE_CONTROL);
                go_remote_control.putExtra("isEnforce", true);
                this.mContext.sendBroadcast(go_remote_control);
                return;
            case C0291R.id.sd_card_control:
                Intent go_sd_card_control = new Intent();
                go_sd_card_control.setAction(Action.REPLACE_SD_CARD_CONTROL);
                go_sd_card_control.putExtra("isEnforce", true);
                this.mContext.sendBroadcast(go_sd_card_control);
                return;
            case C0291R.id.security_control:
                Intent go_security_control = new Intent();
                go_security_control.setAction(Action.REPLACE_SECURITY_CONTROL);
                go_security_control.putExtra("isEnforce", true);
                this.mContext.sendBroadcast(go_security_control);
                return;
            case C0291R.id.time_control:
                Intent go_time_control = new Intent();
                go_time_control.setAction(Action.REPLACE_SETTING_TIME);
                go_time_control.putExtra("isEnforce", true);
                this.mContext.sendBroadcast(go_time_control);
                return;
            case C0291R.id.video_control:
                Intent go_video_control = new Intent();
                go_video_control.setAction(Action.REPLACE_VIDEO_CONTROL);
                go_video_control.putExtra("isEnforce", true);
                this.mContext.sendBroadcast(go_video_control);
                return;
            default:
                return;
        }
    }

    private void disableViewsWhileWrongPwd() {
        this.defenceArea_control.setEnabled(false);
        this.net_control.setEnabled(false);
        this.security_control.setEnabled(false);
        this.record_control.setEnabled(false);
        this.video_control.setEnabled(false);
        this.time_contrl.setEnabled(false);
        this.remote_control.setEnabled(false);
        this.alarm_control.setEnabled(false);
        this.chekc_device_update.setEnabled(false);
        this.sd_card_control.setEnabled(false);
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
