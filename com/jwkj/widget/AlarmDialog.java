package com.jwkj.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
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
import com.jwkj.P2PConnect;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.data.DataManager;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.global.CallingService;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.FList;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.MusicManger;
import com.jwkj.utils.Utils;
import com.tencent.bugly.Bugly;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmDialog extends DialogFragment implements OnClickListener {
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
    BroadcastReceiver br = new C05826();
    TextView chanel_text;
    String device_model;
    NormalDialog dialog;
    int group;
    Handler handler = new Handler(new C05837());
    boolean hasContact = false;
    TextView ignore_btn;
    boolean isAlarm;
    boolean isRegFilter = false;
    boolean isSupport;
    int item;
    LinearLayout layout_area_chanel;
    Context mContext;
    private Handler mHandler = new Handler(new C05848());
    EditText mPassword;
    TextView monitor_btn;
    TextView shield_btn;
    Timer timeOutTimer;
    TextView tv_info;
    boolean viewed = false;

    class C05782 implements AnimationListener {
        C05782() {
        }

        public void onAnimationEnd(Animation arg0) {
            ((InputMethodManager) AlarmDialog.this.alarm_input.getContext().getSystemService("input_method")).toggleSoftInput(0, 2);
        }

        public void onAnimationRepeat(Animation arg0) {
        }

        public void onAnimationStart(Animation arg0) {
        }
    }

    class C05793 extends Thread {
        C05793() {
        }

        public void run() {
            while (AlarmDialog.this.isAlarm) {
                MusicManger.getInstance().Vibrate();
                Utils.sleepThread(100);
            }
            MusicManger.getInstance().stopVibrate();
        }
    }

    class C05804 implements OnTouchListener {
        C05804() {
        }

        public boolean onTouch(View arg0, MotionEvent event) {
            switch (event.getAction()) {
                case 0:
                    AlarmDialog.this.alarm_go.setTextColor(AlarmDialog.this.mContext.getResources().getColor(C0291R.color.text_color_white));
                    break;
                case 1:
                    AlarmDialog.this.alarm_go.setTextColor(AlarmDialog.this.mContext.getResources().getColor(C0291R.color.text_color_gray));
                    break;
            }
            return false;
        }
    }

    class C05826 extends BroadcastReceiver {
        C05826() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Action.CHANGE_ALARM_MESSAGE)) {
                String alarm_id1 = intent.getStringExtra("alarm_id");
                int alarm_type1 = intent.getIntExtra("alarm_type", 0);
                boolean isSupport1 = intent.getBooleanExtra("isSupport", false);
                int group1 = intent.getIntExtra("group", 0);
                int item1 = intent.getIntExtra("item", 0);
                Contact contact = DataManager.findContactByActiveUserAndContactId(AlarmDialog.this.mContext, NpcCommon.mThreeNum, String.valueOf(alarm_id1));
                AlarmDialog.this.alarm_id_text.setText((contact == null ? Utils.showShortDevID(alarm_id1) + "" : contact.contactName) + "(" + Utils.showShortDevID(String.valueOf(alarm_id1)) + ")");
                switch (alarm_type1) {
                    case 1:
                        AlarmDialog.this.alarm_type_text.setText(C0291R.string.allarm_type1);
                        if (isSupport1) {
                            AlarmDialog.this.layout_area_chanel.setVisibility(0);
                            AlarmDialog.this.area_text.setText(AlarmDialog.this.mContext.getResources().getString(C0291R.string.area) + ":" + Utils.getDefenceAreaByGroup(AlarmDialog.this.mContext, group1));
                            AlarmDialog.this.chanel_text.setText(AlarmDialog.this.mContext.getResources().getString(C0291R.string.channel) + ":" + (item1 + 1));
                            return;
                        }
                        return;
                    case 2:
                        AlarmDialog.this.layout_area_chanel.setVisibility(8);
                        AlarmDialog.this.alarm_type_text.setText(C0291R.string.allarm_type2);
                        return;
                    case 3:
                        AlarmDialog.this.layout_area_chanel.setVisibility(8);
                        AlarmDialog.this.alarm_type_text.setText(C0291R.string.allarm_type3);
                        return;
                    case 5:
                        AlarmDialog.this.layout_area_chanel.setVisibility(8);
                        AlarmDialog.this.alarm_type_text.setText(C0291R.string.allarm_type5);
                        return;
                    case 6:
                        AlarmDialog.this.alarm_type_text.setText(C0291R.string.low_voltage_alarm);
                        if (isSupport1) {
                            AlarmDialog.this.layout_area_chanel.setVisibility(0);
                            AlarmDialog.this.area_text.setText(AlarmDialog.this.mContext.getResources().getString(C0291R.string.area) + ":" + Utils.getDefenceAreaByGroup(AlarmDialog.this.mContext, group1));
                            AlarmDialog.this.chanel_text.setText(AlarmDialog.this.mContext.getResources().getString(C0291R.string.channel) + ":" + (item1 + 1));
                            return;
                        }
                        return;
                    case 7:
                        AlarmDialog.this.layout_area_chanel.setVisibility(8);
                        AlarmDialog.this.alarm_type_text.setText(C0291R.string.allarm_type4);
                        return;
                    case 8:
                        AlarmDialog.this.layout_area_chanel.setVisibility(8);
                        AlarmDialog.this.alarm_type_text.setText(C0291R.string.defence);
                        return;
                    case 9:
                        AlarmDialog.this.layout_area_chanel.setVisibility(8);
                        AlarmDialog.this.alarm_type_text.setText(C0291R.string.no_defence);
                        return;
                    case 10:
                        AlarmDialog.this.layout_area_chanel.setVisibility(8);
                        AlarmDialog.this.alarm_type_text.setText(C0291R.string.battery_low_alarm);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    class C05837 implements Callback {
        String[] data;

        C05837() {
        }

        public boolean handleMessage(Message msg) {
            AlarmDialog.this.dismiss();
            String name = "";
            this.data = (String[]) msg.obj;
            Contact contact = FList.getInstance().isContact(this.data[0]);
            if (contact != null) {
                name = contact.contactName;
            }
            Intent reCallIntent = new Intent(AlarmDialog.this.mContext, CallingService.class);
            reCallIntent.setAction(CallingService.MONITOR_RECALL_ACTION);
            reCallIntent.putExtra("callModel", this.data[2]);
            reCallIntent.putExtra("callId", this.data[0]);
            reCallIntent.putExtra(ContactDB.COLUMN_CONTACT_NAME, name);
            reCallIntent.putExtra("password", this.data[1]);
            reCallIntent.putExtra("isOutCall", true);
            reCallIntent.putExtra("type", 1);
            AlarmDialog.this.getActivity().startService(reCallIntent);
            ((OnCallingListener) AlarmDialog.this.getActivity()).onCalling();
            return false;
        }
    }

    class C05848 implements Callback {
        C05848() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    AlarmDialog.this.dismiss();
                    break;
            }
            return false;
        }
    }

    class C05859 extends TimerTask {
        C05859() {
        }

        public void run() {
            if (!AlarmDialog.this.viewed) {
                Message message = new Message();
                message.what = 3;
                AlarmDialog.this.mHandler.sendMessage(message);
            }
        }
    }

    public interface OnCallingListener {
        void onCalling();
    }

    public AlarmDialog(String device_model, String alarm_id, int alarm_type, boolean isSupport, int group, int item) {
        this.device_model = device_model;
        this.alarm_id = alarm_id;
        this.alarm_type = alarm_type;
        this.isSupport = isSupport;
        this.group = group;
        this.item = item;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(1);
        Window win = getActivity().getWindow();
        win.addFlags(4718592);
        win.addFlags(2097280);
        this.mContext = getActivity();
        if (NpcCommon.mThreeNum == null || String.valueOf(this.alarm_id) == null) {
            dismiss();
        }
        Contact contact = FList.getInstance().isContact(this.alarm_id);
        if (contact != null) {
            this.alarm_name = contact.contactName;
        } else {
            this.alarm_name = Utils.showShortDevID(this.alarm_id);
        }
        View view = inflater.inflate(C0291R.layout.activity_alarm, container);
        excuteTimeOutTimer();
        initComponent(view);
        regFilter();
        return view;
    }

    private void openVideo() {
        this.isAlarm = false;
        final Contact contact = FList.getInstance().isContact(this.alarm_id);
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
                    AlarmDialog.this.handler.sendMessage(msg);
                }
            }.start();
        }
        if (!this.hasContact && this.alarm_input.getVisibility() != 0) {
            this.alarm_input.setVisibility(0);
            this.alarm_input.requestFocus();
            Animation anim = AnimationUtils.loadAnimation(this.mContext, C0291R.anim.slide_in_right);
            anim.setAnimationListener(new C05782());
            this.alarm_input.startAnimation(anim);
        }
    }

    public void loadMusicAndVibrate() {
        this.isAlarm = true;
        if (SharedPreferencesManager.getInstance().getAMuteState(MyApp.app) == 1) {
            MusicManger.getInstance().playAlarmMusic();
        }
        if (SharedPreferencesManager.getInstance().getAVibrateState(MyApp.app) == 1) {
            new C05793().start();
        }
        if (SharedPreferencesManager.getInstance().getAOpenVideoState(MyApp.app) == 1) {
            Utils.sleepThread(3000);
            openVideo();
        }
    }

    public void initComponent(View view) {
        this.monitor_btn = (TextView) view.findViewById(C0291R.id.monitor_btn);
        this.ignore_btn = (TextView) view.findViewById(C0291R.id.ignore_btn);
        this.alarm_id_text = (TextView) view.findViewById(C0291R.id.alarm_id_text);
        this.alarm_type_text = (TextView) view.findViewById(C0291R.id.alarm_type_text);
        this.alarm_go = (TextView) view.findViewById(C0291R.id.alarm_go);
        this.tv_info = (TextView) view.findViewById(C0291R.id.tv_info);
        this.alarm_go.setOnTouchListener(new C05804());
        this.alarm_input = (LinearLayout) view.findViewById(C0291R.id.alarm_input);
        this.alarm_img = (ImageView) view.findViewById(C0291R.id.alarm_img);
        this.mPassword = (EditText) view.findViewById(C0291R.id.password);
        this.mPassword.setInputType(2);
        this.mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        final AnimationDrawable anim = (AnimationDrawable) this.alarm_img.getDrawable();
        this.alarm_img.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            public boolean onPreDraw() {
                anim.start();
                return true;
            }
        });
        this.alarm_dialog = (LinearLayout) view.findViewById(C0291R.id.alarm_dialog);
        this.alarm_dialog.startAnimation(AnimationUtils.loadAnimation(this.mContext, C0291R.anim.slide_in_right));
        this.alarmTitle = this.alarm_name + "(" + Utils.showShortDevID(String.valueOf(this.alarm_id)) + ")";
        this.alarm_id_text.setText(this.alarmTitle);
        this.layout_area_chanel = (LinearLayout) view.findViewById(C0291R.id.layout_area_chanel);
        this.area_text = (TextView) view.findViewById(C0291R.id.area_text);
        this.chanel_text = (TextView) view.findViewById(C0291R.id.chanel_text);
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
        getActivity().registerReceiver(this.br, filter);
    }

    public void excuteTimeOutTimer() {
        this.timeOutTimer = new Timer();
        this.timeOutTimer.schedule(new C05859(), (long) this.TIME_OUT);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.alarm_go:
                this.viewed = true;
                final String password = this.mPassword.getText().toString();
                if (password.trim().equals("")) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.input_monitor_pwd);
                    return;
                } else if (password.length() > 9) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.password_length_error);
                    return;
                } else {
                    P2PConnect.vReject("");
                    new Thread() {
                        public void run() {
                            while (P2PConnect.getCurrent_state() != 0) {
                                Utils.sleepThread(500);
                            }
                            Message msg = new Message();
                            msg.obj = new String[]{AlarmDialog.this.alarm_id, password};
                            AlarmDialog.this.handler.sendMessage(msg);
                        }
                    }.start();
                    return;
                }
            case C0291R.id.ignore_btn:
                this.viewed = true;
                C0568T.showShort(this.mContext, this.mContext.getResources().getString(C0291R.string.ignore_alarm_prompt_start) + " " + SharedPreferencesManager.getInstance().getAlarmTimeInterval(this.mContext) + " " + this.mContext.getResources().getString(C0291R.string.ignore_alarm_prompt_end));
                dismiss();
                return;
            case C0291R.id.monitor_btn:
                this.viewed = true;
                if (FList.getInstance().isContact(this.alarm_id) != null) {
                    this.hasContact = true;
                    Message msg = new Message();
                    msg.obj = new String[]{contact.contactId, contact.contactPassword, contact.contactModel, Bugly.SDK_IS_DEV, contact.contactName};
                    this.handler.sendMessage(msg);
                }
                if (!this.hasContact && this.alarm_input.getVisibility() != 0) {
                    this.alarm_input.setVisibility(0);
                    this.alarm_input.requestFocus();
                    Animation anim = AnimationUtils.loadAnimation(this.mContext, C0291R.anim.slide_in_right);
                    anim.setAnimationListener(new AnimationListener() {
                        public void onAnimationEnd(Animation arg0) {
                            ((InputMethodManager) AlarmDialog.this.alarm_input.getContext().getSystemService("input_method")).toggleSoftInput(0, 2);
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

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        P2PConnect.setAlarm(true);
        loadMusicAndVibrate();
    }

    public void onDetach() {
        super.onDetach();
        SharedPreferencesManager.getInstance().putIgnoreAlarmTime(this.mContext, System.currentTimeMillis());
        this.isAlarm = false;
        P2PConnect.vEndAllarm();
    }

    public void onDestroyView() {
        super.onDestroyView();
        MusicManger.getInstance().stop();
        P2PConnect.setAlarm(false);
        if (this.isRegFilter) {
            this.isRegFilter = false;
            this.mContext.unregisterReceiver(this.br);
        }
        if (this.timeOutTimer != null) {
            this.timeOutTimer.cancel();
        }
    }
}
