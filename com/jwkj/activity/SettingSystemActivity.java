package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.data.SystemDataManager;
import com.jwkj.global.NpcCommon;
import java.util.HashMap;

public class SettingSystemActivity extends BaseActivity implements OnClickListener {
    public static final String ACTION_CHANGEBELL = "com.jwkj.changebell";
    public static final int SET_TYPE_ALLARM_RING = 1;
    public static final int SET_TYPE_COMMING_RING = 0;
    int aRingType;
    int a_muteState;
    RelativeLayout a_mute_btn;
    ImageView a_mute_img;
    int a_openVideoState;
    RelativeLayout a_open_video_btn;
    ImageView a_open_video_img;
    int a_vibrateState;
    RelativeLayout a_vibrate_btn;
    ImageView a_vibrate_img;
    RelativeLayout alarm_set_btn;
    RelativeLayout auto_start_btn;
    ImageView auto_start_icon_img;
    ImageView back_btn;
    int cRingType;
    int c_muteState;
    RelativeLayout c_mute_btn;
    ImageView c_mute_img;
    int c_vibrateState;
    RelativeLayout c_vibrate_btn;
    ImageView c_vibrate_img;
    boolean isAutoStart;
    boolean isShowNotify;
    private Context mContext;
    boolean myreceiverIsReg = false;
    RelativeLayout notify_icon_btn;
    ImageView notify_icon_img;
    MyReceiver receiver;
    TextView selectedARing;
    TextView selectedCRing;
    RelativeLayout set_allarmRing_btn;
    RelativeLayout set_allarmRing_method_btn;
    RelativeLayout set_commingRing_btn;

    public class MyReceiver extends BroadcastReceiver {
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(SettingSystemActivity.ACTION_CHANGEBELL)) {
                SettingSystemActivity.this.initSelectMusicName();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_system_set);
        this.c_vibrateState = SharedPreferencesManager.getInstance().getCVibrateState(this);
        this.c_muteState = SharedPreferencesManager.getInstance().getCMuteState(this);
        this.a_vibrateState = SharedPreferencesManager.getInstance().getAVibrateState(this);
        this.a_openVideoState = SharedPreferencesManager.getInstance().getAOpenVideoState(this);
        this.a_muteState = SharedPreferencesManager.getInstance().getAMuteState(this);
        this.isShowNotify = SharedPreferencesManager.getInstance().getIsShowNotify(this);
        this.isAutoStart = SharedPreferencesManager.getInstance().getIsAutoStart(this, NpcCommon.mThreeNum);
        initCompent();
        initBtnState();
        initSelectMusicName();
        this.mContext = this;
        registerMonitor();
    }

    public void initCompent() {
        this.back_btn = (ImageView) findViewById(C0291R.id.back_btn);
        this.c_vibrate_btn = (RelativeLayout) findViewById(C0291R.id.c_vibrate_btn);
        this.c_mute_btn = (RelativeLayout) findViewById(C0291R.id.c_mute_btn);
        this.a_vibrate_btn = (RelativeLayout) findViewById(C0291R.id.a_vibrate_btn);
        this.a_open_video_btn = (RelativeLayout) findViewById(C0291R.id.a_open_video_btn);
        this.a_mute_btn = (RelativeLayout) findViewById(C0291R.id.a_mute_btn);
        this.c_vibrate_img = (ImageView) findViewById(C0291R.id.c_vibrate_img);
        this.c_mute_img = (ImageView) findViewById(C0291R.id.c_mute_img);
        this.a_vibrate_img = (ImageView) findViewById(C0291R.id.a_vibrate_img);
        this.a_open_video_img = (ImageView) findViewById(C0291R.id.a_open_video_img);
        this.a_mute_img = (ImageView) findViewById(C0291R.id.a_mute_img);
        this.set_commingRing_btn = (RelativeLayout) findViewById(C0291R.id.set_commingRing);
        this.set_allarmRing_btn = (RelativeLayout) findViewById(C0291R.id.set_allarmRing);
        this.set_allarmRing_method_btn = (RelativeLayout) findViewById(C0291R.id.set_allarmRing_method);
        this.selectedCRing = (TextView) findViewById(C0291R.id.selectedCommingRing);
        this.selectedARing = (TextView) findViewById(C0291R.id.selectAllarmRing);
        this.notify_icon_btn = (RelativeLayout) findViewById(C0291R.id.notify_icon_btn);
        this.notify_icon_img = (ImageView) findViewById(C0291R.id.notify_icon_img);
        this.auto_start_btn = (RelativeLayout) findViewById(C0291R.id.auto_start_btn);
        this.auto_start_icon_img = (ImageView) findViewById(C0291R.id.auto_start_icon_img);
        this.alarm_set_btn = (RelativeLayout) findViewById(C0291R.id.alarm_set_btn);
        this.auto_start_btn.setOnClickListener(this);
        this.alarm_set_btn.setOnClickListener(this);
        this.notify_icon_btn.setOnClickListener(this);
        this.c_vibrate_btn.setOnClickListener(this);
        this.c_mute_btn.setOnClickListener(this);
        this.a_vibrate_btn.setOnClickListener(this);
        this.a_open_video_btn.setOnClickListener(this);
        this.a_mute_btn.setOnClickListener(this);
        this.set_commingRing_btn.setOnClickListener(this);
        this.set_allarmRing_btn.setOnClickListener(this);
        this.set_allarmRing_method_btn.setOnClickListener(this);
        this.back_btn.setOnClickListener(this);
    }

    public void initBtnState() {
        if (this.c_vibrateState == 0) {
            this.c_vibrate_img.setImageResource(C0291R.drawable.check_off);
        } else {
            this.c_vibrate_img.setImageResource(C0291R.drawable.check_on);
        }
        if (this.c_muteState == 0) {
            this.c_mute_img.setImageResource(C0291R.drawable.check_off);
        } else {
            this.c_mute_img.setImageResource(C0291R.drawable.check_on);
        }
        if (this.a_vibrateState == 0) {
            this.a_vibrate_img.setImageResource(C0291R.drawable.check_off);
        } else {
            this.a_vibrate_img.setImageResource(C0291R.drawable.check_on);
        }
        if (this.a_openVideoState == 0) {
            this.a_open_video_img.setImageResource(C0291R.drawable.check_off);
        } else {
            this.a_open_video_img.setImageResource(C0291R.drawable.check_on);
        }
        if (this.a_muteState == 0) {
            this.a_mute_img.setImageResource(C0291R.drawable.check_off);
        } else {
            this.a_mute_img.setImageResource(C0291R.drawable.check_on);
        }
        if (this.isShowNotify) {
            this.notify_icon_img.setImageResource(C0291R.drawable.ic_checkbox_on);
        } else {
            this.notify_icon_img.setImageResource(C0291R.drawable.ic_checkbox_off);
        }
        if (this.isAutoStart) {
            this.auto_start_icon_img.setImageResource(C0291R.drawable.ic_checkbox_on);
        } else {
            this.auto_start_icon_img.setImageResource(C0291R.drawable.ic_checkbox_off);
        }
    }

    public void initSelectMusicName() {
        HashMap<String, String> data;
        if (SharedPreferencesManager.getInstance().getCBellType(this) == 0) {
            data = SystemDataManager.getInstance().findSystemBellById(this, SharedPreferencesManager.getInstance().getCSystemBellId(this));
            if (data != null) {
                this.selectedCRing.setText((CharSequence) data.get("bellName"));
            }
        } else {
            data = SystemDataManager.getInstance().findSdBellById(this, SharedPreferencesManager.getInstance().getCSdBellId(this));
            if (data != null) {
                this.selectedCRing.setText((CharSequence) data.get("bellName"));
            }
        }
        if (SharedPreferencesManager.getInstance().getABellType(this) == 0) {
            data = SystemDataManager.getInstance().findSystemBellById(this, SharedPreferencesManager.getInstance().getASystemBellId(this));
            if (data != null) {
                this.selectedARing.setText((CharSequence) data.get("bellName"));
                return;
            }
            return;
        }
        data = SystemDataManager.getInstance().findSdBellById(this, SharedPreferencesManager.getInstance().getASdBellId(this));
        if (data != null) {
            this.selectedARing.setText((CharSequence) data.get("bellName"));
        }
    }

    public void registerMonitor() {
        this.myreceiverIsReg = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CHANGEBELL);
        this.receiver = new MyReceiver();
        registerReceiver(this.receiver, filter);
    }

    public void changeCVibrate() {
        if (this.c_vibrateState == 0) {
            this.c_vibrate_img.setImageResource(C0291R.drawable.check_on);
            this.c_vibrateState = 1;
            SharedPreferencesManager.getInstance().putCVibrateState(this.c_vibrateState, this);
            return;
        }
        this.c_vibrate_img.setImageResource(C0291R.drawable.check_off);
        this.c_vibrateState = 0;
        SharedPreferencesManager.getInstance().putCVibrateState(this.c_vibrateState, this);
    }

    public void changeCMute() {
        if (this.c_muteState == 0) {
            this.c_mute_img.setImageResource(C0291R.drawable.check_on);
            this.c_muteState = 1;
            SharedPreferencesManager.getInstance().putCMuteState(this.c_muteState, this);
            return;
        }
        this.c_mute_img.setImageResource(C0291R.drawable.check_off);
        this.c_muteState = 0;
        SharedPreferencesManager.getInstance().putCMuteState(this.c_muteState, this);
    }

    public void changeAVibrate() {
        if (this.a_vibrateState == 0) {
            this.a_vibrate_img.setImageResource(C0291R.drawable.check_on);
            this.a_vibrateState = 1;
            SharedPreferencesManager.getInstance().putAVibrateState(this.a_vibrateState, this);
            return;
        }
        this.a_vibrate_img.setImageResource(C0291R.drawable.check_off);
        this.a_vibrateState = 0;
        SharedPreferencesManager.getInstance().putAVibrateState(this.a_vibrateState, this);
    }

    public void changeAOpenVideo() {
        if (this.a_openVideoState == 0) {
            this.a_open_video_img.setImageResource(C0291R.drawable.check_on);
            this.a_openVideoState = 1;
            SharedPreferencesManager.getInstance().putAOpenVideoState(this.a_openVideoState, this);
            return;
        }
        this.a_open_video_img.setImageResource(C0291R.drawable.check_off);
        this.a_openVideoState = 0;
        SharedPreferencesManager.getInstance().putAOpenVideoState(this.a_openVideoState, this);
    }

    public void changeAMute() {
        if (this.a_muteState == 0) {
            this.a_mute_img.setImageResource(C0291R.drawable.check_on);
            this.a_muteState = 1;
            SharedPreferencesManager.getInstance().putAMuteState(this.a_muteState, this);
            return;
        }
        this.a_mute_img.setImageResource(C0291R.drawable.check_off);
        this.a_muteState = 0;
        SharedPreferencesManager.getInstance().putAMuteState(this.a_muteState, this);
    }

    public void changeIsShowNotifyIcon() {
        if (this.isShowNotify) {
            this.isShowNotify = false;
            this.notify_icon_img.setImageResource(C0291R.drawable.ic_checkbox_off);
            SharedPreferencesManager.getInstance().putIsShowNotify(this, this.isShowNotify);
            return;
        }
        this.isShowNotify = true;
        this.notify_icon_img.setImageResource(C0291R.drawable.ic_checkbox_on);
        SharedPreferencesManager.getInstance().putIsShowNotify(this, this.isShowNotify);
    }

    public void changeIsAutoStartIcon() {
        if (this.isAutoStart) {
            this.isAutoStart = false;
            this.auto_start_icon_img.setImageResource(C0291R.drawable.ic_checkbox_off);
            SharedPreferencesManager.getInstance().putIsAutoStart(this, this.isAutoStart);
            return;
        }
        this.isAutoStart = true;
        this.auto_start_icon_img.setImageResource(C0291R.drawable.ic_checkbox_on);
        SharedPreferencesManager.getInstance().putIsAutoStart(this, this.isAutoStart);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.a_mute_btn:
                changeAMute();
                return;
            case C0291R.id.a_open_video_btn:
                changeAOpenVideo();
                return;
            case C0291R.id.a_vibrate_btn:
                changeAVibrate();
                return;
            case C0291R.id.alarm_set_btn:
                startActivity(new Intent(this, AlarmSetActivity.class));
                return;
            case C0291R.id.auto_start_btn:
                changeIsAutoStartIcon();
                return;
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.c_mute_btn:
                changeCMute();
                return;
            case C0291R.id.c_vibrate_btn:
                changeCVibrate();
                return;
            case C0291R.id.notify_icon_btn:
                changeIsShowNotifyIcon();
                return;
            case C0291R.id.set_allarmRing:
                Intent set_allarm_bellRing = new Intent(this, SettingBellRingActivity.class);
                set_allarm_bellRing.putExtra("type", 1);
                startActivity(set_allarm_bellRing);
                return;
            case C0291R.id.set_allarmRing_method:
                startActivity(new Intent(this, SettingPushActivity.class));
                return;
            case C0291R.id.set_commingRing:
                Intent set_comming_bellRing = new Intent(this, SettingBellRingActivity.class);
                set_comming_bellRing.putExtra("type", 0);
                startActivity(set_comming_bellRing);
                return;
            default:
                return;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.myreceiverIsReg) {
            unregisterReceiver(this.receiver);
        }
    }

    public int getActivityInfo() {
        return 26;
    }
}
