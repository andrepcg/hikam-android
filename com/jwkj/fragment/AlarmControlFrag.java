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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.hikam.C0291R;
import com.jwkj.activity.AlarmPushAccountActivity;
import com.jwkj.activity.AlarmTimingActivity;
import com.jwkj.activity.MainControlActivity;
import com.jwkj.activity.ModifyBoundEmailActivity;
import com.jwkj.activity.MotionDetectionActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.entity.Account;
import com.jwkj.global.AccountPersist;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.ScaleRuler;
import com.lib.addBar.AddBar;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.p2p.core.P2PValue.HikamDeviceModel;
import java.lang.reflect.Field;

public class AlarmControlFrag extends BaseFragment implements OnClickListener {
    Account account;
    AddBar addBar;
    RelativeLayout add_alarm_item;
    TextView alarmId_text;
    RelativeLayout alarm_input_switch;
    RelativeLayout alarm_out_switch;
    RelativeLayout alarm_timing_setting;
    ImageView buzzer_img;
    int buzzer_switch;
    LinearLayout buzzer_time;
    RelativeLayout change_buzzer;
    RelativeLayout change_buzzer_prompt;
    RelativeLayout change_email;
    RelativeLayout change_motion;
    RelativeLayout change_pir;
    private Contact contact;
    String curVersion = "";
    int cur_modify_buzzer_state;
    int cur_modify_motion_state;
    boolean current_infrared_state;
    NormalDialog dialog_loading;
    TextView email_text;
    RelativeLayout humanoid_detection;
    RelativeLayout humanoid_detection_prompt;
    ImageView humanoid_img;
    ImageView icon_add_alarm_id;
    ImageView img_alarm_input;
    ImageView img_alarm_out;
    ImageView img_receive_alarm;
    int infrared_switch;
    boolean isHumanDetect = false;
    private boolean isNewAlarmReceiver = false;
    private boolean isOldAlarmReceiver = false;
    boolean isOpenWriedAlarmInput;
    boolean isOpenWriedAlarmOut;
    boolean isReceiveAlarm = true;
    private boolean isRegFilter = false;
    boolean isSurportSMTP = false;
    String[] last_bind_data;
    RelativeLayout layout_alarm_switch;
    RelativeLayout layout_change_pir_prompt;
    private Context mContext;
    private BroadcastReceiver mReceiver = new C05062();
    int max_alarm_count;
    int modify_infrared_state;
    RelativeLayout motion_detection_setting;
    ImageView motion_img;
    int motion_switch;
    TextView motion_text;
    String[] new_data;
    ImageView pir_img;
    ProgressBar progressBar;
    ProgressBar progressBar_alarmId;
    ProgressBar progressBar_alarm_input;
    ProgressBar progressBar_alarm_out;
    ProgressBar progressBar_email;
    ProgressBar progressBar_humanoid;
    ProgressBar progressBar_motion;
    ProgressBar progressBar_motion2;
    ProgressBar progressBar_pir;
    ProgressBar progressBar_receive_alarm;
    RadioButton radio_one;
    RadioButton radio_three;
    RadioButton radio_two;
    RelativeLayout rl_timing_info;
    ScaleRuler scale_ruler;

    class C05051 implements OnSeekBarChangeListener {
        C05051() {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (progress >= 630) {
                seekBar.setProgress(720);
            } else if (progress >= 450) {
                seekBar.setProgress(540);
            } else if (progress >= 270) {
                seekBar.setProgress(360);
            } else if (progress >= 90) {
                seekBar.setProgress(180);
            } else if (progress >= 0) {
                seekBar.setProgress(0);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }
    }

    class C05062 extends BroadcastReceiver {
        C05062() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(P2P.RET_GET_BIND_ALARM_ID)) {
                AlarmControlFrag.this.showImg_receive_alarm();
                String[] data = intent.getStringArrayExtra("data");
                int max_count = intent.getIntExtra("max_count", 0);
                AlarmControlFrag.this.last_bind_data = data;
                String oldContactId = "000000";
                Account account = AccountPersist.getInstance().getActiveAccountInfo(AlarmControlFrag.this.getActivity());
                if (account != null) {
                    oldContactId = account.three_number2;
                }
                AlarmControlFrag.this.max_alarm_count = max_count;
                AlarmControlFrag.this.showAlarmIdState();
                AlarmControlFrag.this.layout_alarm_switch.setClickable(true);
                AlarmControlFrag.this.add_alarm_item.setClickable(true);
                int count = 0;
                int i = 0;
                while (i < data.length) {
                    if (data[i].equals(NpcCommon.mThreeNum)) {
                        Log.e("alarm check:", "tag isNewAlarmReceiver");
                        AlarmControlFrag.this.isNewAlarmReceiver = true;
                    }
                    if (data[i].equals(oldContactId)) {
                        Log.e("alarm check:", "tag isOldAlarmReceiver");
                        AlarmControlFrag.this.isOldAlarmReceiver = true;
                    }
                    if (data[i].equals(NpcCommon.mThreeNum) || data[i].equals(oldContactId)) {
                        AlarmControlFrag.this.img_receive_alarm.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
                        AlarmControlFrag.this.isReceiveAlarm = false;
                        Log.e("alarm check:", "position " + i + " ,data" + data[i] + "ThreeNum:" + NpcCommon.mThreeNum);
                        count++;
                    }
                    i++;
                }
                if (count == 0) {
                    AlarmControlFrag.this.img_receive_alarm.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
                    AlarmControlFrag.this.isReceiveAlarm = true;
                }
            } else if (intent.getAction().equals(P2P.RET_SET_BIND_ALARM_ID)) {
                result = intent.getIntExtra("result", -1);
                if (AlarmControlFrag.this.dialog_loading != null && AlarmControlFrag.this.dialog_loading.isShowing()) {
                    AlarmControlFrag.this.dialog_loading.dismiss();
                    AlarmControlFrag.this.dialog_loading = null;
                }
                if (result == 0) {
                    P2PHandler.getInstance().getBindAlarmId(NpcCommon.mThreeNum, AlarmControlFrag.this.contact.contactModel, AlarmControlFrag.this.contact.contactId, AlarmControlFrag.this.contact.contactPassword, AlarmControlFrag.this.account.three_number, AlarmControlFrag.this.account.three_number2);
                    C0568T.showShort(AlarmControlFrag.this.mContext, (int) C0291R.string.modify_success);
                } else if (AlarmControlFrag.this.getIsRun()) {
                    C0568T.showShort(AlarmControlFrag.this.mContext, (int) C0291R.string.operator_error);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_ALARM_EMAIL)) {
                email = intent.getStringExtra("email");
                if (email.equals("") || email.equals("0")) {
                    AlarmControlFrag.this.email_text.setText(C0291R.string.unbound);
                } else {
                    AlarmControlFrag.this.email_text.setText(email);
                }
                AlarmControlFrag.this.showEmailState();
                result = intent.getIntExtra("result", 0);
                if ((result & 2) == 0) {
                    AlarmControlFrag.this.isSurportSMTP = false;
                } else if ((result & 2) == 2) {
                    AlarmControlFrag.this.isSurportSMTP = true;
                } else {
                    AlarmControlFrag.this.isSurportSMTP = false;
                    Log.i("alex print", "isSurportSMTP------>failed");
                }
                Log.i("alex print", "isSurportSMTP------>" + AlarmControlFrag.this.isSurportSMTP);
            } else if (intent.getAction().equals(P2P.RET_GET_ALARM_EMAIL_WITHSMTP)) {
                email = intent.getStringExtra("email");
                result = intent.getIntExtra("result", 0);
                if ((result & 2) == 0) {
                    AlarmControlFrag.this.isSurportSMTP = false;
                } else if ((result & 2) == 2) {
                    AlarmControlFrag.this.isSurportSMTP = true;
                } else {
                    AlarmControlFrag.this.isSurportSMTP = false;
                    Log.i("alex print", "isSurportSMTP------>failed");
                }
                Log.i("alex print", "isSurportSMTP------>" + AlarmControlFrag.this.isSurportSMTP);
                if (email.equals("") || email.equals("0")) {
                    AlarmControlFrag.this.email_text.setText(C0291R.string.unbound);
                } else {
                    AlarmControlFrag.this.email_text.setText(email);
                }
                AlarmControlFrag.this.showEmailState();
            } else if (intent.getAction().equals(P2P.RET_SET_ALARM_EMAIL)) {
                P2PHandler.getInstance().getAlarmEmail(AlarmControlFrag.this.contact.contactModel, AlarmControlFrag.this.contact.contactId, AlarmControlFrag.this.contact.contactPassword);
            } else if (intent.getAction().equals(P2P.RET_GET_MOTION)) {
                state = intent.getIntExtra("motionState", -1);
                if (intent.getIntExtra("sensitivity", -1) != -1 || !P2PValue.HikamDeviceModelList.contains(AlarmControlFrag.this.contact.contactModel)) {
                    AlarmControlFrag.this.progressBar_motion2.setVisibility(8);
                    if (state == 1) {
                        AlarmControlFrag.this.motion_text.setText(C0291R.string.motion_on);
                        AlarmControlFrag.this.motion_switch = 1;
                        AlarmControlFrag.this.motion_img.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
                        AlarmControlFrag.this.scale_ruler.open();
                    } else {
                        AlarmControlFrag.this.motion_text.setText(C0291R.string.motion_off);
                        AlarmControlFrag.this.motion_switch = 0;
                        AlarmControlFrag.this.motion_img.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
                        AlarmControlFrag.this.scale_ruler.close();
                    }
                    AlarmControlFrag.this.showMotionState();
                }
            } else if (intent.getAction().equals(P2P.RET_SET_MOTION)) {
                if (intent.getIntExtra("result", -1) == 0) {
                    if (AlarmControlFrag.this.cur_modify_motion_state == 1) {
                        AlarmControlFrag.this.motion_switch = 1;
                        AlarmControlFrag.this.motion_img.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
                        AlarmControlFrag.this.scale_ruler.open();
                    } else {
                        AlarmControlFrag.this.motion_switch = 0;
                        AlarmControlFrag.this.motion_img.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
                        AlarmControlFrag.this.scale_ruler.close();
                    }
                    AlarmControlFrag.this.showMotionState();
                    C0568T.showShort(AlarmControlFrag.this.mContext, (int) C0291R.string.modify_success);
                    return;
                }
                AlarmControlFrag.this.showMotionState();
                C0568T.showShort(AlarmControlFrag.this.mContext, (int) C0291R.string.operator_error);
            } else if (intent.getAction().equals(P2P.RET_GET_BUZZER)) {
                AlarmControlFrag.this.updateBuzzer(intent.getIntExtra("buzzerState", -1));
                AlarmControlFrag.this.showBuzzerTime();
            } else if (intent.getAction().equals("com.hikam.RET_SET_BUZZER")) {
                if (intent.getIntExtra("result", -1) == 0) {
                    AlarmControlFrag.this.updateBuzzer(AlarmControlFrag.this.cur_modify_buzzer_state);
                    AlarmControlFrag.this.showBuzzerTime();
                    C0568T.showShort(AlarmControlFrag.this.mContext, (int) C0291R.string.modify_success);
                    return;
                }
                AlarmControlFrag.this.showBuzzerTime();
                C0568T.showShort(AlarmControlFrag.this.mContext, (int) C0291R.string.operator_error);
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_NPC_SETTINGS)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    AlarmControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:get npc settings");
                    P2PHandler.getInstance().getNpcSettings(AlarmControlFrag.this.contact.contactModel, AlarmControlFrag.this.contact.contactId, AlarmControlFrag.this.contact.contactPassword);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_BIND_ALARM_ID)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    if (AlarmControlFrag.this.dialog_loading != null && AlarmControlFrag.this.dialog_loading.isShowing()) {
                        AlarmControlFrag.this.dialog_loading.dismiss();
                        AlarmControlFrag.this.dialog_loading = null;
                    }
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    AlarmControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set alarm bind id");
                    P2PHandler.getInstance().setBindAlarmId(AlarmControlFrag.this.contact.contactModel, AlarmControlFrag.this.contact.contactId, AlarmControlFrag.this.contact.contactPassword, AlarmControlFrag.this.new_data.length, AlarmControlFrag.this.new_data, NpcCommon.mThreeNum);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_BIND_ALARM_ID)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    AlarmControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:get alarm bind id");
                    P2PHandler.getInstance().getBindAlarmId(NpcCommon.mThreeNum, AlarmControlFrag.this.contact.contactModel, AlarmControlFrag.this.contact.contactId, AlarmControlFrag.this.contact.contactPassword, AlarmControlFrag.this.account.three_number, AlarmControlFrag.this.account.three_number2);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_ALARM_EMAIL)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    AlarmControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:get alarm email");
                    P2PHandler.getInstance().getAlarmEmail(AlarmControlFrag.this.contact.contactModel, AlarmControlFrag.this.contact.contactId, AlarmControlFrag.this.contact.contactPassword);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_DEVICE_INFO)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    AlarmControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:get device info");
                    P2PHandler.getInstance().getDeviceVersion(AlarmControlFrag.this.contact.contactModel, AlarmControlFrag.this.contact.contactId, AlarmControlFrag.this.contact.contactPassword);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_DEVICE_INFO) || intent.getAction().equals(P2P.RET_GET_DEVICE_INFO2)) {
                AlarmControlFrag.this.curVersion = intent.getStringExtra("cur_version");
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_MOTION)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    AlarmControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set npc settings motion");
                    P2PHandler.getInstance().setMotion(AlarmControlFrag.this.contact.contactModel, AlarmControlFrag.this.contact.contactId, AlarmControlFrag.this.contact.contactPassword, AlarmControlFrag.this.cur_modify_motion_state, 0);
                }
            } else if (intent.getAction().equals("com.hikam.RET_SET_BUZZER")) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    AlarmControlFrag.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set npc settings buzzer");
                    P2PHandler.getInstance().setBuzzer(AlarmControlFrag.this.contact.contactModel, AlarmControlFrag.this.contact.contactId, AlarmControlFrag.this.contact.contactPassword, AlarmControlFrag.this.cur_modify_buzzer_state);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_INFRARED_SWITCH)) {
                if (!P2PValue.HikamDeviceModelList.contains(AlarmControlFrag.this.contact.contactModel)) {
                    state = intent.getIntExtra("state", -1);
                    if (state == 1) {
                        AlarmControlFrag.this.change_pir.setVisibility(0);
                        AlarmControlFrag.this.layout_change_pir_prompt.setVisibility(0);
                        AlarmControlFrag.this.current_infrared_state = false;
                        AlarmControlFrag.this.pir_img.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
                    } else if (state == 0) {
                        AlarmControlFrag.this.change_pir.setVisibility(0);
                        AlarmControlFrag.this.layout_change_pir_prompt.setVisibility(0);
                        AlarmControlFrag.this.current_infrared_state = true;
                        AlarmControlFrag.this.pir_img.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
                    }
                    AlarmControlFrag.this.showImg_infrared_switch();
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_INFRARED_SWITCH)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9998) {
                    if (AlarmControlFrag.this.current_infrared_state) {
                        P2PHandler.getInstance().setInfraredSwitch(AlarmControlFrag.this.contact.contactId, AlarmControlFrag.this.contact.contactPassword, 1);
                    } else {
                        P2PHandler.getInstance().setInfraredSwitch(AlarmControlFrag.this.contact.contactId, AlarmControlFrag.this.contact.contactPassword, 0);
                    }
                } else if (result == 9997) {
                    if (AlarmControlFrag.this.current_infrared_state) {
                        AlarmControlFrag.this.current_infrared_state = false;
                        AlarmControlFrag.this.pir_img.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
                    } else {
                        AlarmControlFrag.this.current_infrared_state = true;
                        AlarmControlFrag.this.pir_img.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
                    }
                    AlarmControlFrag.this.showImg_infrared_switch();
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_ALARM_PUSH_STATUS)) {
                result = intent.getIntExtra("result", -1);
                if (AlarmControlFrag.this.dialog_loading != null && AlarmControlFrag.this.dialog_loading.isShowing()) {
                    AlarmControlFrag.this.dialog_loading.dismiss();
                    AlarmControlFrag.this.dialog_loading = null;
                }
                if (result == 0) {
                    P2PHandler.getInstance().getBindAlarmId(NpcCommon.mThreeNum, AlarmControlFrag.this.contact.contactModel, AlarmControlFrag.this.contact.contactId, AlarmControlFrag.this.contact.contactPassword, AlarmControlFrag.this.account.three_number, AlarmControlFrag.this.account.three_number2);
                    C0568T.showShort(AlarmControlFrag.this.mContext, (int) C0291R.string.modify_success);
                } else if (AlarmControlFrag.this.getIsRun()) {
                    C0568T.showShort(AlarmControlFrag.this.mContext, (int) C0291R.string.operator_error);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_ALARM_PUSH_STATUS)) {
                int onoff = intent.getIntExtra("onoff", 0);
                Log.e("oaosj", "ACK_RET_GET_ALARM_PUSH_STATUS" + onoff + " str " + intent.getStringExtra("account"));
                if (onoff == 1) {
                    AlarmControlFrag.this.img_receive_alarm.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
                    AlarmControlFrag.this.isReceiveAlarm = false;
                } else if (onoff == 0) {
                    AlarmControlFrag.this.img_receive_alarm.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
                    AlarmControlFrag.this.isReceiveAlarm = true;
                }
                AlarmControlFrag.this.showImg_receive_alarm();
                AlarmControlFrag.this.layout_alarm_switch.setClickable(true);
                AlarmControlFrag.this.add_alarm_item.setClickable(true);
            } else if (intent.getAction().equals(P2P.RET_SET_HUMAN_DETECT)) {
                result = intent.getIntExtra("result", -1);
                if (AlarmControlFrag.this.isHumanDetect) {
                    AlarmControlFrag.this.showHumanOp(1);
                } else {
                    AlarmControlFrag.this.showHumanOp(0);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_HUMAN_DETECT)) {
                if (intent.getIntExtra("enable", 0) == 0) {
                    AlarmControlFrag.this.showHumanOp(1);
                } else {
                    AlarmControlFrag.this.showHumanOp(0);
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
        View view = inflater.inflate(C0291R.layout.fragment_alarm_control, container, false);
        initComponent(view);
        regFilter();
        showProgress();
        showProgress_motion();
        showProgress_email();
        this.account = AccountPersist.getInstance().getActiveAccountInfo(getActivity());
        if (this.account == null) {
            this.account = new Account();
        }
        P2PHandler.getInstance().getBindAlarmId(NpcCommon.mThreeNum, this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.account.three_number, this.account.three_number2);
        P2PHandler.getInstance().getAlarmEmail(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
        P2PHandler.getInstance().getDeviceVersion(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
        P2PHandler.getInstance().GetHumanDetect(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
        P2pJni.P2PClientSdkGetSiren(this.contact.contactId, this.contact.contactPassword, 900000);
        return view;
    }

    public void onResume() {
        super.onResume();
        P2PHandler.getInstance().getNpcSettings(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword);
    }

    public void initComponent(View view) {
        this.humanoid_detection_prompt = (RelativeLayout) view.findViewById(C0291R.id.humanoid_detection_prompt);
        this.motion_text = (TextView) view.findViewById(C0291R.id.motion_text);
        this.progressBar_motion2 = (ProgressBar) view.findViewById(C0291R.id.progressBar_motion2);
        this.humanoid_img = (ImageView) view.findViewById(C0291R.id.humanoid_img);
        this.progressBar_humanoid = (ProgressBar) view.findViewById(C0291R.id.humanoid_progressBar);
        this.humanoid_detection = (RelativeLayout) view.findViewById(C0291R.id.humanoid_detection);
        this.change_buzzer = (RelativeLayout) view.findViewById(C0291R.id.change_buzzer);
        this.buzzer_img = (ImageView) view.findViewById(C0291R.id.buzzer_img);
        this.progressBar = (ProgressBar) view.findViewById(C0291R.id.progressBar);
        this.buzzer_time = (LinearLayout) view.findViewById(C0291R.id.buzzer_time);
        this.change_buzzer_prompt = (RelativeLayout) view.findViewById(C0291R.id.change_buzzer_prompt);
        this.change_motion = (RelativeLayout) view.findViewById(C0291R.id.change_motion);
        this.motion_img = (ImageView) view.findViewById(C0291R.id.motion_img);
        this.progressBar_motion = (ProgressBar) view.findViewById(C0291R.id.progressBar_motion);
        this.radio_one = (RadioButton) view.findViewById(C0291R.id.radio_one);
        this.radio_two = (RadioButton) view.findViewById(C0291R.id.radio_two);
        this.radio_three = (RadioButton) view.findViewById(C0291R.id.radio_three);
        this.change_email = (RelativeLayout) view.findViewById(C0291R.id.change_email);
        this.email_text = (TextView) view.findViewById(C0291R.id.email_text);
        this.progressBar_email = (ProgressBar) view.findViewById(C0291R.id.progressBar_email);
        this.add_alarm_item = (RelativeLayout) view.findViewById(C0291R.id.add_alarm_item);
        this.motion_detection_setting = (RelativeLayout) view.findViewById(C0291R.id.motion_detection_setting);
        this.alarm_timing_setting = (RelativeLayout) view.findViewById(C0291R.id.alarm_timing_setting);
        this.rl_timing_info = (RelativeLayout) view.findViewById(C0291R.id.rl_timing_info);
        this.change_pir = (RelativeLayout) view.findViewById(C0291R.id.change_pir);
        this.layout_change_pir_prompt = (RelativeLayout) view.findViewById(C0291R.id.change_pir_prompt);
        this.pir_img = (ImageView) view.findViewById(C0291R.id.pir_img);
        this.progressBar_pir = (ProgressBar) view.findViewById(C0291R.id.progressBar_pir);
        this.img_receive_alarm = (ImageView) view.findViewById(C0291R.id.img_receive_alarm);
        this.layout_alarm_switch = (RelativeLayout) view.findViewById(C0291R.id.layout_alarm_switch);
        this.progressBar_receive_alarm = (ProgressBar) view.findViewById(C0291R.id.progressBar_receive_alarm);
        this.scale_ruler = (ScaleRuler) view.findViewById(C0291R.id.scaleRuler);
        this.scale_ruler.setProgress(360);
        this.scale_ruler.setOnSeekBarChangeListener(new C05051());
        this.add_alarm_item.setOnClickListener(this);
        this.motion_detection_setting.setOnClickListener(this);
        this.alarm_timing_setting.setOnClickListener(this);
        this.change_email.setOnClickListener(this);
        this.change_motion.setOnClickListener(this);
        this.change_buzzer.setOnClickListener(this);
        this.radio_one.setOnClickListener(this);
        this.radio_two.setOnClickListener(this);
        this.radio_three.setOnClickListener(this);
        this.change_pir.setOnClickListener(this);
        this.layout_alarm_switch.setOnClickListener(this);
        this.layout_alarm_switch.setClickable(false);
        this.add_alarm_item.setClickable(false);
        this.humanoid_detection.setOnClickListener(this);
        if (NpcCommon.mThreeNum.equals("517400")) {
            this.layout_alarm_switch.setVisibility(8);
        }
        if (P2PValue.HikamDeviceModelList.contains(this.contact.contactModel)) {
            this.humanoid_detection.setVisibility(0);
            this.humanoid_detection_prompt.setVisibility(0);
        } else {
            this.motion_detection_setting.setVisibility(8);
            this.change_motion.setVisibility(0);
            this.alarm_timing_setting.setVisibility(8);
            this.rl_timing_info.setVisibility(8);
        }
        if (P2PValue.HikamDeviceModelList.contains(this.contact.contactModel) && !this.contact.contactModel.equals(HikamDeviceModel.Q3)) {
            this.change_buzzer.setVisibility(8);
            this.change_buzzer_prompt.setVisibility(8);
        }
    }

    public void regFilter() {
        MainControlActivity.isCancelCheck = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.RET_SET_HUMAN_DETECT);
        filter.addAction(P2P.RET_GET_HUMAN_DETECT);
        filter.addAction(P2P.ACK_RET_SET_ALARM_PUSH_STATUS);
        filter.addAction(P2P.ACK_RET_GET_ALARM_PUSH_STATUS);
        filter.addAction(P2P.ACK_RET_GET_NPC_SETTINGS);
        filter.addAction(P2P.ACK_RET_SET_BIND_ALARM_ID);
        filter.addAction(P2P.ACK_RET_GET_BIND_ALARM_ID);
        filter.addAction(P2P.RET_SET_BIND_ALARM_ID);
        filter.addAction(P2P.RET_GET_BIND_ALARM_ID);
        filter.addAction(P2P.ACK_RET_GET_ALARM_EMAIL);
        filter.addAction(P2P.RET_SET_ALARM_EMAIL);
        filter.addAction(P2P.RET_GET_ALARM_EMAIL);
        filter.addAction(P2P.RET_GET_ALARM_EMAIL_WITHSMTP);
        filter.addAction(P2P.ACK_RET_SET_MOTION);
        filter.addAction(P2P.RET_SET_MOTION);
        filter.addAction(P2P.RET_GET_MOTION);
        filter.addAction("com.hikam.RET_SET_BUZZER");
        filter.addAction("com.hikam.RET_SET_BUZZER");
        filter.addAction(P2P.RET_GET_BUZZER);
        filter.addAction(P2P.RET_GET_INFRARED_SWITCH);
        filter.addAction(P2P.ACK_RET_SET_INFRARED_SWITCH);
        filter.addAction(P2P.RET_GET_WIRED_ALARM_INPUT);
        filter.addAction(P2P.RET_GET_WIRED_ALARM_OUT);
        filter.addAction(P2P.ACK_RET_SET_WIRED_ALARM_INPUT);
        filter.addAction(P2P.ACK_RET_SET_WIRED_ALARM_OUT);
        filter.addAction(P2P.ACK_RET_GET_DEVICE_INFO);
        filter.addAction(P2P.RET_GET_DEVICE_INFO);
        filter.addAction(P2P.RET_GET_DEVICE_INFO2);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.add_alarm_item:
                Intent it = new Intent(this.mContext, AlarmPushAccountActivity.class);
                it.putExtra(ContactDB.COLUMN_CONTACT_MODEL, this.contact.contactModel);
                it.putExtra(ContactDB.COLUMN_CONTACT_ID, this.contact.contactId);
                it.putExtra(ContactDB.COLUMN_CONTACT_PASSWORD, this.contact.contactPassword);
                startActivity(it);
                return;
            case C0291R.id.alarm_timing_setting:
                Intent alarmIntent = new Intent(this.mContext, AlarmTimingActivity.class);
                alarmIntent.putExtra(ContactDB.COLUMN_CONTACT_MODEL, this.contact.contactModel);
                alarmIntent.putExtra(ContactDB.COLUMN_CONTACT_ID, this.contact.contactId);
                alarmIntent.putExtra(ContactDB.COLUMN_CONTACT_PASSWORD, this.contact.contactPassword);
                startActivity(alarmIntent);
                return;
            case C0291R.id.change_buzzer:
                showProgress();
                if (this.buzzer_switch != 0) {
                    this.cur_modify_buzzer_state = 0;
                } else {
                    this.cur_modify_buzzer_state = 1;
                }
                P2PHandler.getInstance().setBuzzer(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.cur_modify_buzzer_state);
                return;
            case C0291R.id.change_email:
                Intent modify_email = new Intent(this.mContext, ModifyBoundEmailActivity.class);
                modify_email.putExtra(ContactDB.TABLE_NAME, this.contact);
                modify_email.putExtra("email", this.email_text.getText().toString());
                modify_email.putExtra("curVersion", this.curVersion);
                modify_email.putExtra("isSurportSMTP", this.isSurportSMTP);
                this.mContext.startActivity(modify_email);
                return;
            case C0291R.id.change_motion:
                showProgress_motion();
                if (this.motion_switch != 0) {
                    this.cur_modify_motion_state = 0;
                    P2PHandler.getInstance().setMotion(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.cur_modify_motion_state, 0);
                    return;
                }
                this.cur_modify_motion_state = 1;
                P2PHandler.getInstance().setMotion(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.cur_modify_motion_state, 0);
                return;
            case C0291R.id.change_pir:
                showProgress_infrares_switch();
                if (this.current_infrared_state) {
                    this.modify_infrared_state = 1;
                    P2PHandler.getInstance().setInfraredSwitch(this.contact.contactId, this.contact.contactPassword, this.modify_infrared_state);
                    return;
                }
                this.modify_infrared_state = 0;
                P2PHandler.getInstance().setInfraredSwitch(this.contact.contactId, this.contact.contactPassword, this.modify_infrared_state);
                return;
            case C0291R.id.humanoid_detection:
                if (this.isHumanDetect) {
                    P2PHandler.getInstance().SetHumanDetect(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, 0);
                    showHumanOp(2);
                    return;
                }
                P2PHandler.getInstance().SetHumanDetect(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, 1);
                showHumanOp(2);
                return;
            case C0291R.id.layout_alarm_switch:
                String target;
                showProgress_receive_alarm();
                Account account = AccountPersist.getInstance().getActiveAccountInfo(getActivity());
                if (P2PValue.HikamDeviceModelList.contains(this.contact.contactModel)) {
                    target = NpcCommon.mThreeNum;
                } else {
                    target = account.three_number2;
                }
                int i;
                if (this.isReceiveAlarm) {
                    System.out.println("last_bind_data.length=" + this.last_bind_data.length + " max_alarm_count=" + this.max_alarm_count);
                    if (this.last_bind_data.length > this.max_alarm_count) {
                        C0568T.showShort(this.mContext, (int) C0291R.string.alarm_push_limit);
                        showImg_receive_alarm();
                        return;
                    }
                    this.new_data = new String[(this.last_bind_data.length + 1)];
                    for (i = 0; i < this.last_bind_data.length; i++) {
                        this.new_data[i] = this.last_bind_data[i];
                    }
                    this.new_data[this.new_data.length - 1] = target;
                    this.last_bind_data = this.new_data;
                    P2PHandler.getInstance().setBindAlarmId(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.new_data.length, this.new_data, NpcCommon.mThreeNum);
                    return;
                }
                int count;
                boolean isNewServer = false;
                if (P2PValue.HikamDeviceModelList.contains(this.contact.contactModel)) {
                    isNewServer = true;
                }
                if (this.isNewAlarmReceiver && isNewServer) {
                    Log.e("few", "isNewAlarmReceiver");
                    this.new_data = new String[(this.last_bind_data.length - 1)];
                    count = 0;
                    for (i = 0; i < this.last_bind_data.length; i++) {
                        if (!this.last_bind_data[i].equals(NpcCommon.mThreeNum)) {
                            this.new_data[count] = this.last_bind_data[i];
                            count++;
                        }
                    }
                    if (this.new_data.length == 0) {
                        this.new_data = new String[]{"0"};
                    }
                    this.last_bind_data = this.new_data;
                    P2PHandler.getInstance().setBindAlarmId(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.new_data.length, this.new_data, NpcCommon.mThreeNum);
                }
                if (this.isOldAlarmReceiver) {
                    Log.e("few", "isOldAlarmReceiver");
                    this.new_data = new String[(this.last_bind_data.length - 1)];
                    count = 0;
                    for (i = 0; i < this.last_bind_data.length; i++) {
                        if (!this.last_bind_data[i].equals(account.three_number2)) {
                            this.new_data[count] = this.last_bind_data[i];
                            count++;
                        }
                    }
                    if (this.new_data.length == 0) {
                        this.new_data = new String[]{"0"};
                    }
                    this.last_bind_data = this.new_data;
                    P2PHandler.getInstance().setBindAlarmId(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.new_data.length, this.new_data, NpcCommon.mThreeNum2);
                    return;
                }
                return;
            case C0291R.id.motion_detection_setting:
                Intent motionIntent = new Intent(this.mContext, MotionDetectionActivity.class);
                motionIntent.putExtra(ContactDB.COLUMN_CONTACT_MODEL, this.contact.contactModel);
                motionIntent.putExtra(ContactDB.COLUMN_CONTACT_ID, this.contact.contactId);
                motionIntent.putExtra(ContactDB.COLUMN_CONTACT_PASSWORD, this.contact.contactPassword);
                startActivity(motionIntent);
                return;
            case C0291R.id.radio_one:
                showProgress();
                this.cur_modify_buzzer_state = 1;
                P2PHandler.getInstance().setBuzzer(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.cur_modify_buzzer_state);
                return;
            case C0291R.id.radio_three:
                showProgress();
                this.cur_modify_buzzer_state = 3;
                P2PHandler.getInstance().setBuzzer(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.cur_modify_buzzer_state);
                return;
            case C0291R.id.radio_two:
                showProgress();
                this.cur_modify_buzzer_state = 2;
                P2PHandler.getInstance().setBuzzer(this.contact.contactModel, this.contact.contactId, this.contact.contactPassword, this.cur_modify_buzzer_state);
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
        MainControlActivity.isCancelCheck = false;
    }

    public void updateBuzzer(int state) {
        if (state == 1) {
            this.buzzer_switch = 1;
            this.buzzer_img.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
            this.change_buzzer.setBackgroundResource(C0291R.drawable.tiao_bg_up);
            this.buzzer_time.setVisibility(0);
            this.radio_one.setChecked(true);
        } else if (state == 2) {
            this.buzzer_switch = 2;
            this.buzzer_img.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
            this.change_buzzer.setBackgroundResource(C0291R.drawable.tiao_bg_up);
            this.buzzer_time.setVisibility(0);
            this.radio_two.setChecked(true);
        } else if (state == 3) {
            this.buzzer_switch = 3;
            this.buzzer_img.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
            this.change_buzzer.setBackgroundResource(C0291R.drawable.tiao_bg_up);
            this.buzzer_time.setVisibility(0);
            this.radio_three.setChecked(true);
        } else if (state == 0) {
            this.buzzer_switch = 0;
            this.buzzer_img.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
            this.change_buzzer.setBackgroundResource(C0291R.drawable.tiao_bg_single);
            this.buzzer_time.setVisibility(8);
        } else if (!P2PValue.HikamDeviceModelList.contains(this.contact.contactModel)) {
            this.buzzer_switch = 0;
            this.buzzer_img.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
            this.change_buzzer.setBackgroundResource(C0291R.drawable.tiao_bg_single);
            this.buzzer_time.setVisibility(8);
        }
    }

    public void showProgress() {
        this.progressBar.setVisibility(0);
        this.buzzer_img.setVisibility(8);
        this.change_buzzer.setEnabled(false);
        this.radio_one.setEnabled(false);
        this.radio_two.setEnabled(false);
        this.radio_three.setEnabled(false);
    }

    public void showBuzzerTime() {
        this.progressBar.setVisibility(8);
        this.buzzer_img.setVisibility(0);
        this.change_buzzer.setEnabled(true);
        this.radio_one.setEnabled(true);
        this.radio_two.setEnabled(true);
        this.radio_three.setEnabled(true);
    }

    public void showMotionState() {
        this.progressBar_motion.setVisibility(8);
        this.motion_img.setVisibility(0);
        this.change_motion.setEnabled(true);
    }

    public void showProgress_motion() {
        this.progressBar_motion.setVisibility(0);
        this.motion_img.setVisibility(8);
        this.change_motion.setEnabled(false);
    }

    public void showEmailState() {
        this.progressBar_email.setVisibility(8);
        this.email_text.setVisibility(0);
        this.change_email.setEnabled(true);
    }

    public void showProgress_email() {
        this.progressBar_email.setVisibility(0);
        this.email_text.setVisibility(8);
        this.change_email.setEnabled(false);
    }

    public void showAlarmIdState() {
    }

    public void showProgress_alarmId() {
    }

    public void showProgress_infrares_switch() {
        this.progressBar_pir.setVisibility(0);
        this.pir_img.setVisibility(8);
    }

    public void showImg_infrared_switch() {
        ProgressBar progressBar = this.progressBar_pir;
        ProgressBar progressBar2 = this.progressBar;
        progressBar.setVisibility(8);
        this.pir_img.setVisibility(0);
    }

    public void showHumanOp(int op) {
        switch (op) {
            case 0:
                this.isHumanDetect = true;
                this.progressBar_humanoid.setVisibility(8);
                this.humanoid_img.setBackgroundResource(C0291R.drawable.ic_checkbox_on);
                this.humanoid_img.setVisibility(0);
                this.humanoid_detection.setClickable(true);
                return;
            case 1:
                this.isHumanDetect = false;
                this.progressBar_humanoid.setVisibility(8);
                this.humanoid_img.setBackgroundResource(C0291R.drawable.ic_checkbox_off);
                this.humanoid_img.setVisibility(0);
                this.humanoid_detection.setClickable(true);
                return;
            case 2:
                this.progressBar_humanoid.setVisibility(0);
                this.humanoid_img.setVisibility(8);
                this.humanoid_detection.setClickable(false);
                return;
            default:
                return;
        }
    }

    public void showProgress_receive_alarm() {
        this.progressBar_receive_alarm.setVisibility(0);
        ImageView imageView = this.img_receive_alarm;
        ProgressBar progressBar = this.progressBar;
        imageView.setVisibility(8);
    }

    public void showImg_receive_alarm() {
        this.progressBar_receive_alarm.setVisibility(8);
        ImageView imageView = this.img_receive_alarm;
        ProgressBar progressBar = this.progressBar;
        imageView.setVisibility(0);
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
