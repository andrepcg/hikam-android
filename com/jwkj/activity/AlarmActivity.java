package com.jwkj.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.CallActivity;
import com.jwkj.P2PConnect;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.data.DataManager;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.FList;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.MusicManger;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.tencent.bugly.Bugly;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmActivity extends Activity implements OnClickListener {
    public static final int USER_HASNOTVIEWED = 3;
    private int TIME_OUT = 20000;
    String alarmTitle;
    LinearLayout alarm_dialog;
    TextView alarm_go;
    String alarm_id;
    TextView alarm_id_text;
    ImageView alarm_img;
    LinearLayout alarm_input;
    String alarm_name;
    int alarm_type;
    TextView alarm_type_text;
    TextView area_text;
    BroadcastReceiver br = new C03426();
    TextView chanel_text;
    String device_model;
    NormalDialog dialog;
    int group;
    Handler handler = new Handler(new C03437());
    boolean hasContact = false;
    TextView ignore_btn;
    boolean isAlarm;
    boolean isRegFilter = false;
    boolean isSupport;
    int item;
    LinearLayout layout_area_chanel;
    Context mContext;
    private Handler mHandler = new Handler(new C03448());
    EditText mPassword;
    TextView monitor_btn;
    TextView shield_btn;
    Timer timeOutTimer;
    TextView tv_info;
    boolean viewed = false;

    class C03382 implements AnimationListener {
        C03382() {
        }

        public void onAnimationEnd(Animation arg0) {
            ((InputMethodManager) AlarmActivity.this.alarm_input.getContext().getSystemService("input_method")).toggleSoftInput(0, 2);
        }

        public void onAnimationRepeat(Animation arg0) {
        }

        public void onAnimationStart(Animation arg0) {
        }
    }

    class C03393 extends Thread {
        C03393() {
        }

        public void run() {
            while (AlarmActivity.this.isAlarm) {
                MusicManger.getInstance().Vibrate();
                Utils.sleepThread(100);
            }
            MusicManger.getInstance().stopVibrate();
        }
    }

    class C03404 implements OnTouchListener {
        C03404() {
        }

        public boolean onTouch(View arg0, MotionEvent event) {
            switch (event.getAction()) {
                case 0:
                    AlarmActivity.this.alarm_go.setTextColor(AlarmActivity.this.mContext.getResources().getColor(C0291R.color.text_color_white));
                    break;
                case 1:
                    AlarmActivity.this.alarm_go.setTextColor(AlarmActivity.this.mContext.getResources().getColor(C0291R.color.text_color_gray));
                    break;
            }
            return false;
        }
    }

    class C03426 extends BroadcastReceiver {
        C03426() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Action.CHANGE_ALARM_MESSAGE)) {
                String alarm_id1 = intent.getStringExtra("alarm_id");
                int alarm_type1 = intent.getIntExtra("alarm_type", 0);
                boolean isSupport1 = intent.getBooleanExtra("isSupport", false);
                int group1 = intent.getIntExtra("group", 0);
                int item1 = intent.getIntExtra("item", 0);
                Contact contact = DataManager.findContactByActiveUserAndContactId(AlarmActivity.this.mContext, NpcCommon.mThreeNum, String.valueOf(alarm_id1));
                AlarmActivity.this.alarm_id_text.setText((contact == null ? Utils.showShortDevID(alarm_id1) + "" : contact.contactName) + "(" + Utils.showShortDevID(String.valueOf(alarm_id1)) + ")");
                Log.e("few", "alarm_type1 :" + alarm_type1);
                switch (alarm_type1) {
                    case 1:
                        AlarmActivity.this.alarm_type_text.setText(C0291R.string.allarm_type1);
                        if (isSupport1) {
                            AlarmActivity.this.layout_area_chanel.setVisibility(0);
                            AlarmActivity.this.area_text.setText(AlarmActivity.this.mContext.getResources().getString(C0291R.string.area) + ":" + Utils.getDefenceAreaByGroup(AlarmActivity.this.mContext, group1));
                            AlarmActivity.this.chanel_text.setText(AlarmActivity.this.mContext.getResources().getString(C0291R.string.channel) + ":" + (item1 + 1));
                            return;
                        }
                        return;
                    case 2:
                        AlarmActivity.this.layout_area_chanel.setVisibility(8);
                        AlarmActivity.this.alarm_type_text.setText(C0291R.string.allarm_type2);
                        return;
                    case 3:
                        AlarmActivity.this.layout_area_chanel.setVisibility(8);
                        AlarmActivity.this.alarm_type_text.setText(C0291R.string.allarm_type3);
                        return;
                    case 5:
                        AlarmActivity.this.layout_area_chanel.setVisibility(8);
                        AlarmActivity.this.alarm_type_text.setText(C0291R.string.allarm_type5);
                        return;
                    case 6:
                        AlarmActivity.this.alarm_type_text.setText(C0291R.string.low_voltage_alarm);
                        if (isSupport1) {
                            AlarmActivity.this.layout_area_chanel.setVisibility(0);
                            AlarmActivity.this.area_text.setText(AlarmActivity.this.mContext.getResources().getString(C0291R.string.area) + ":" + Utils.getDefenceAreaByGroup(AlarmActivity.this.mContext, group1));
                            AlarmActivity.this.chanel_text.setText(AlarmActivity.this.mContext.getResources().getString(C0291R.string.channel) + ":" + (item1 + 1));
                            return;
                        }
                        return;
                    case 7:
                        AlarmActivity.this.layout_area_chanel.setVisibility(8);
                        AlarmActivity.this.alarm_type_text.setText(C0291R.string.allarm_type4);
                        return;
                    case 8:
                        AlarmActivity.this.layout_area_chanel.setVisibility(8);
                        AlarmActivity.this.alarm_type_text.setText(C0291R.string.defence);
                        return;
                    case 9:
                        AlarmActivity.this.layout_area_chanel.setVisibility(8);
                        AlarmActivity.this.alarm_type_text.setText(C0291R.string.no_defence);
                        return;
                    case 10:
                        AlarmActivity.this.layout_area_chanel.setVisibility(8);
                        AlarmActivity.this.alarm_type_text.setText(C0291R.string.battery_low_alarm);
                        return;
                    case 31:
                        AlarmActivity.this.layout_area_chanel.setVisibility(8);
                        AlarmActivity.this.alarm_type_text.setText(C0291R.string.humanoid_detection);
                        return;
                    case 32:
                        AlarmActivity.this.layout_area_chanel.setVisibility(8);
                        AlarmActivity.this.alarm_type_text.setText(C0291R.string.humanoid_detection_fallback);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    class C03437 implements Callback {
        C03437() {
        }

        public boolean handleMessage(Message msg) {
            AlarmActivity.this.finish();
            String[] data = (String[]) msg.obj;
            Intent monitor = new Intent();
            monitor.setClass(AlarmActivity.this.mContext, CallActivity.class);
            monitor.putExtra("callId", data[0]);
            monitor.putExtra("password", data[1]);
            monitor.putExtra("callModel", data[2]);
            monitor.putExtra("isAlarmTrigger", data[3]);
            monitor.putExtra(ContactDB.COLUMN_CONTACT_NAME, data[4]);
            monitor.putExtra("isOutCall", true);
            monitor.putExtra("type", 1);
            AlarmActivity.this.startActivity(monitor);
            return false;
        }
    }

    class C03448 implements Callback {
        C03448() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    AlarmActivity.this.finish();
                    break;
            }
            return false;
        }
    }

    class C03459 extends TimerTask {
        C03459() {
        }

        public void run() {
            if (!AlarmActivity.this.viewed) {
                Message message = new Message();
                message.what = 3;
                AlarmActivity.this.mHandler.sendMessage(message);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        if (P2PConnect.isPlaying() && getRequestedOrientation() != 0) {
            setRequestedOrientation(0);
        }
        Window win = getWindow();
        win.addFlags(4718592);
        win.addFlags(2097280);
        this.mContext = this;
        this.device_model = getIntent().getStringExtra("device_model");
        this.alarm_id = getIntent().getStringExtra("alarm_id");
        this.alarm_type = getIntent().getIntExtra("alarm_type", 0);
        this.isSupport = getIntent().getBooleanExtra("isSupport", false);
        this.group = getIntent().getIntExtra("group", 0);
        this.item = getIntent().getIntExtra("item", 0);
        if (NpcCommon.mThreeNum == null || String.valueOf(this.alarm_id) == null) {
            finish();
            return;
        }
        Contact contact = DataManager.findContactByActiveUserAndContactId(this.mContext, NpcCommon.mThreeNum, String.valueOf(this.alarm_id));
        if (contact != null) {
            this.alarm_name = contact.contactName;
        } else {
            this.alarm_name = Utils.showShortDevID(this.alarm_id);
        }
        setContentView(C0291R.layout.activity_alarm);
        excuteTimeOutTimer();
        initComponent();
        regFilter();
    }

    private void openVideo() {
        this.isAlarm = false;
        final Contact contact = FList.getInstance().isContact(String.valueOf(this.alarm_id));
        if (contact != null) {
            this.hasContact = true;
            P2PConnect.vReject("");
            new Thread() {
                public void run() {
                    while (P2PConnect.getCurrent_state() != 0) {
                        Utils.sleepThread(500);
                    }
                    Message msg = new Message();
                    msg.obj = new String[]{contact.contactId, contact.contactPassword, contact.contactModel, "true", contact.contactName};
                    AlarmActivity.this.handler.sendMessage(msg);
                }
            }.start();
        }
        if (!this.hasContact && this.alarm_input.getVisibility() != 0) {
            this.alarm_input.setVisibility(0);
            this.alarm_input.requestFocus();
            Animation anim = AnimationUtils.loadAnimation(this.mContext, C0291R.anim.slide_in_right);
            anim.setAnimationListener(new C03382());
            this.alarm_input.startAnimation(anim);
        }
    }

    public void loadMusicAndVibrate() {
        this.isAlarm = true;
        if (SharedPreferencesManager.getInstance().getAMuteState(MyApp.app) == 1) {
            MusicManger.getInstance().playAlarmMusic();
        }
        if (SharedPreferencesManager.getInstance().getAVibrateState(MyApp.app) == 1) {
            new C03393().start();
        }
        if (SharedPreferencesManager.getInstance().getAOpenVideoState(MyApp.app) == 1) {
            Utils.sleepThread(3000);
            openVideo();
        }
    }

    public void initComponent() {
        this.monitor_btn = (TextView) findViewById(C0291R.id.monitor_btn);
        this.ignore_btn = (TextView) findViewById(C0291R.id.ignore_btn);
        this.alarm_id_text = (TextView) findViewById(C0291R.id.alarm_id_text);
        this.alarm_type_text = (TextView) findViewById(C0291R.id.alarm_type_text);
        this.alarm_go = (TextView) findViewById(C0291R.id.alarm_go);
        this.tv_info = (TextView) findViewById(C0291R.id.tv_info);
        this.alarm_go.setOnTouchListener(new C03404());
        this.alarm_input = (LinearLayout) findViewById(C0291R.id.alarm_input);
        this.alarm_img = (ImageView) findViewById(C0291R.id.alarm_img);
        this.mPassword = (EditText) findViewById(C0291R.id.password);
        this.mPassword.setInputType(2);
        this.mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        final AnimationDrawable anim = (AnimationDrawable) this.alarm_img.getDrawable();
        this.alarm_img.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            public boolean onPreDraw() {
                anim.start();
                return true;
            }
        });
        this.alarm_dialog = (LinearLayout) findViewById(C0291R.id.alarm_dialog);
        this.alarm_dialog.startAnimation(AnimationUtils.loadAnimation(this.mContext, C0291R.anim.slide_in_right));
        this.alarmTitle = this.alarm_name + "(" + Utils.showShortDevID(String.valueOf(this.alarm_id)) + ")";
        this.alarm_id_text.setText(this.alarmTitle);
        this.layout_area_chanel = (LinearLayout) findViewById(C0291R.id.layout_area_chanel);
        this.area_text = (TextView) findViewById(C0291R.id.area_text);
        this.chanel_text = (TextView) findViewById(C0291R.id.chanel_text);
        switch (this.alarm_type) {
            case 1:
                this.tv_info.setText(C0291R.string.alarm_info);
                this.alarm_type_text.setText(C0291R.string.allarm_type1);
                if (this.isSupport) {
                    this.layout_area_chanel.setVisibility(0);
                    this.area_text.setText(this.mContext.getResources().getString(C0291R.string.area) + ":" + Utils.getDefenceAreaByGroup(this.mContext, this.group));
                    this.chanel_text.setText(this.mContext.getResources().getString(C0291R.string.channel) + ":" + (this.item + 1));
                    break;
                }
                break;
            case 2:
                this.tv_info.setText(C0291R.string.alarm_info);
                this.alarm_type_text.setText(C0291R.string.allarm_type2);
                break;
            case 3:
                this.tv_info.setText(C0291R.string.alarm_info);
                this.alarm_type_text.setText(C0291R.string.allarm_type3);
                break;
            case 5:
                this.tv_info.setText(C0291R.string.alarm_info);
                this.alarm_type_text.setText(C0291R.string.allarm_type5);
                break;
            case 6:
                this.tv_info.setText(C0291R.string.alarm_info);
                this.alarm_type_text.setText(C0291R.string.low_voltage_alarm);
                if (this.isSupport) {
                    this.layout_area_chanel.setVisibility(0);
                    this.area_text.setText(this.mContext.getResources().getString(C0291R.string.area) + ":" + Utils.getDefenceAreaByGroup(this.mContext, this.group));
                    this.chanel_text.setText(this.mContext.getResources().getString(C0291R.string.channel) + ":" + (this.item + 1));
                    break;
                }
                break;
            case 7:
                this.tv_info.setText(C0291R.string.alarm_info);
                this.alarm_type_text.setText(C0291R.string.allarm_type4);
                break;
            case 8:
                this.tv_info.setText(C0291R.string.alarm_info);
                this.alarm_type_text.setText(C0291R.string.defence);
                break;
            case 9:
                this.tv_info.setText(C0291R.string.alarm_info);
                this.alarm_type_text.setText(C0291R.string.no_defence);
                break;
            case 10:
                this.tv_info.setText(C0291R.string.alarm_info);
                this.alarm_type_text.setText(C0291R.string.battery_low_alarm);
                break;
            case 13:
                this.alarm_type_text.setText(C0291R.string.door_bell);
                this.tv_info.setText(C0291R.string.visitor_messge);
                break;
            case 31:
                this.alarm_type_text.setText(C0291R.string.humanoid_detection);
                this.tv_info.setText(C0291R.string.alarm_info);
                break;
            case 32:
                this.alarm_type_text.setText(C0291R.string.humanoid_detection_fallback);
                this.tv_info.setText(C0291R.string.alarm_info);
                break;
        }
        this.alarm_go.setOnClickListener(this);
        this.monitor_btn.setOnClickListener(this);
        this.ignore_btn.setOnClickListener(this);
    }

    public void regFilter() {
        this.isRegFilter = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.CHANGE_ALARM_MESSAGE);
        registerReceiver(this.br, filter);
    }

    public void excuteTimeOutTimer() {
        this.timeOutTimer = new Timer();
        this.timeOutTimer.schedule(new C03459(), (long) this.TIME_OUT);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.alarm_go:
                this.viewed = true;
                final String password = this.mPassword.getText().toString();
                if (password.trim().equals("")) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.input_monitor_pwd);
                    return;
                }
                P2PConnect.vReject("");
                new Thread() {
                    public void run() {
                        while (P2PConnect.getCurrent_state() != 0) {
                            Utils.sleepThread(500);
                        }
                        Message msg = new Message();
                        msg.obj = new String[]{String.valueOf(AlarmActivity.this.alarm_id), password, "", Bugly.SDK_IS_DEV, ""};
                        AlarmActivity.this.handler.sendMessage(msg);
                    }
                }.start();
                return;
            case C0291R.id.ignore_btn:
                this.viewed = true;
                C0568T.showShort(this.mContext, this.mContext.getResources().getString(C0291R.string.ignore_alarm_prompt_start) + " " + SharedPreferencesManager.getInstance().getAlarmTimeInterval(this.mContext) + " " + this.mContext.getResources().getString(C0291R.string.ignore_alarm_prompt_end));
                finish();
                return;
            case C0291R.id.monitor_btn:
                this.viewed = true;
                final Contact contact = FList.getInstance().isContact(String.valueOf(this.alarm_id));
                if (contact != null) {
                    this.hasContact = true;
                    P2PConnect.vReject("");
                    new Thread() {
                        public void run() {
                            while (P2PConnect.getCurrent_state() != 0) {
                                Utils.sleepThread(500);
                            }
                            Message msg = new Message();
                            msg.obj = new String[]{contact.contactId, contact.contactPassword, contact.contactModel, Bugly.SDK_IS_DEV, contact.contactName};
                            AlarmActivity.this.handler.sendMessage(msg);
                        }
                    }.start();
                }
                if (!this.hasContact && this.alarm_input.getVisibility() != 0) {
                    this.alarm_input.setVisibility(0);
                    this.alarm_input.requestFocus();
                    Animation anim = AnimationUtils.loadAnimation(this.mContext, C0291R.anim.slide_in_right);
                    anim.setAnimationListener(new AnimationListener() {
                        public void onAnimationEnd(Animation arg0) {
                            ((InputMethodManager) AlarmActivity.this.alarm_input.getContext().getSystemService("input_method")).toggleSoftInput(0, 2);
                        }

                        public void onAnimationRepeat(Animation arg0) {
                        }

                        public void onAnimationStart(Animation arg0) {
                        }
                    });
                    this.alarm_input.startAnimation(anim);
                    return;
                }
                return;
            default:
                return;
        }
    }

    protected void onStop() {
        super.onStop();
        SharedPreferencesManager.getInstance().putIgnoreAlarmTime(this.mContext, System.currentTimeMillis());
        this.isAlarm = false;
        P2PConnect.vEndAllarm();
    }

    protected void onResume() {
        super.onResume();
        P2PConnect.setAlarm(true);
        loadMusicAndVibrate();
    }

    protected void onPause() {
        super.onPause();
        MusicManger.getInstance().stop();
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onDestroy() {
        super.onDestroy();
        P2PConnect.setAlarm(false);
        if (this.isRegFilter) {
            this.isRegFilter = false;
            this.mContext.unregisterReceiver(this.br);
        }
        if (this.timeOutTimer != null) {
            this.timeOutTimer.cancel();
        }
    }

    protected void onRestart() {
        super.onRestart();
    }
}
