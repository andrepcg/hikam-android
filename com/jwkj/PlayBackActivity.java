package com.jwkj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.hikam.C0291R;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.MyApp;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.PhoneWatcher;
import com.jwkj.utils.PhoneWatcher.OnCommingCallListener;
import com.jwkj.widget.AlarmDialog;
import com.jwkj.widget.AlarmDialog.OnCallingListener;
import com.jwkj.widget.playback.PlayBackManager;
import com.p2p.core.BasePlayBackActivity;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.p2p.core.P2PValue.HikamDeviceModel;
import com.p2p.core.P2PView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

public class PlayBackActivity extends BasePlayBackActivity implements OnClickListener, OnTouchListener, OnSeekBarChangeListener, OnCallingListener {
    PlayBackActivity activity;
    private AlarmDialog alarmDialog;
    String contactId;
    String contactModel;
    String contactPassword;
    RelativeLayout control_bottom;
    String currentVideoPath;
    private int end = -1;
    private long exitTime = 0;
    String fileName;
    private int indicator = -1;
    private boolean isControlShow = true;
    private boolean isMute = true;
    boolean isPause = false;
    boolean isRecording = false;
    boolean isRegFilter = false;
    boolean isReject = false;
    boolean isScroll = false;
    Context mContext;
    private int mCurrentVolume;
    private int mMaxVolume;
    PhoneWatcher mPhoneWatcher;
    private BroadcastReceiver mReceiver = new C03132();
    private ArrayList<String> nameList = null;
    ImageView next;
    TextView nowTime;
    ImageView pause;
    ImageView previous;
    private ArrayList<Integer> rateList = null;
    LinearLayout recView;
    ImageView screenshot;
    private SeekBar seekbar;
    private int start = 0;
    private Date startTime = null;
    ImageView stopVoice;
    private TextView text_record;
    private int time_count = 0;
    private Timer timer;
    TextView totalTime;
    ImageView video_record;

    class C03132 extends BroadcastReceiver {
        C03132() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(P2P.RET_GET_PLAYBACK_FILES)) {
                PlayBackActivity.this.updataList((String[]) intent.getCharSequenceArrayExtra("recordList"), intent.getIntArrayExtra("rateList"));
            } else if (intent.getAction().equals(P2P.P2P_REJECT)) {
                PlayBackActivity.this.reject();
            } else if (intent.getAction().equalsIgnoreCase("android.intent.action.SCREEN_OFF")) {
                PlayBackActivity.this.reject();
                PlayBackListActivity.IS_SCREEN_OFF = true;
            } else if (intent.getAction().equals(P2P.PLAYBACK_CHANGE_SEEK)) {
                if (!PlayBackActivity.this.isScroll) {
                    int max = intent.getIntExtra("max", 0);
                    int current = intent.getIntExtra("current", 0);
                    PlayBackActivity.this.seekbar.setMax(max);
                    PlayBackActivity.this.seekbar.setProgress(current);
                    PlayBackActivity.this.nowTime.setText(PlayBackActivity.this.convertTime(current));
                    PlayBackActivity.this.totalTime.setText(PlayBackActivity.this.convertTime(max));
                    if (max == current) {
                        PlayBackActivity.this.stopRecord();
                    }
                }
            } else if (intent.getAction().equals(P2P.PLAYBACK_CHANGE_STATE)) {
                int state = intent.getIntExtra("state", 0);
                Log.e("playback", "haowuPLAYBACK_CHANGE_STATE state : " + state);
                switch (state) {
                    case 0:
                        PlayBackActivity.this.isPause = true;
                        PlayBackActivity.this.pause.setImageResource(C0291R.drawable.playing_start);
                        return;
                    case 1:
                        PlayBackActivity.this.isPause = true;
                        PlayBackActivity.this.pause.setImageResource(C0291R.drawable.playing_start);
                        return;
                    case 2:
                        PlayBackActivity.this.isPause = false;
                        PlayBackActivity.this.pause.setImageResource(C0291R.drawable.playing_pause);
                        return;
                    default:
                        return;
                }
            } else if (intent.getAction().equals(Action.DIALOG_ALARM_PUSH)) {
                PlayBackActivity.this.alarmDialog = new AlarmDialog(intent.getStringExtra("device_model"), intent.getStringExtra("alarm_id"), intent.getIntExtra("alarm_type", 0), intent.getBooleanExtra("isSupport", false), intent.getIntExtra("group", 0), intent.getIntExtra("item", 0));
                PlayBackActivity.this.alarmDialog.show(PlayBackActivity.this.getSupportFragmentManager(), "alarm");
            }
        }
    }

    class C03153 implements Runnable {
        C03153() {
        }

        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final int ret = P2pJni.P2PClientSdkVideoRecordMuxer(PlayBackActivity.this.currentVideoPath);
            PlayBackActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (ret == 0) {
                        C0568T.showShort(PlayBackActivity.this.mContext, (int) C0291R.string.record_success);
                    } else {
                        C0568T.showShort(PlayBackActivity.this.mContext, (int) C0291R.string.record_failed);
                    }
                }
            });
        }
    }

    class C03164 implements Runnable {
        C03164() {
        }

        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int ret = P2pJni.P2PClientSdkVideoRecordMuxer(PlayBackActivity.this.currentVideoPath);
        }
    }

    class C10661 implements OnCommingCallListener {
        C10661() {
        }

        public void onCommingCall() {
            PlayBackActivity.this.reject();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        P2PConnect.setPlaying(true);
        P2PConnect.IS_LIVE_STATE = true;
        Window win = getWindow();
        win.addFlags(4718592);
        win.addFlags(2097280);
        setContentView(C0291R.layout.p2p_playback);
        this.fileName = getIntent().getStringExtra("fileName");
        this.contactModel = getIntent().getStringExtra(ContactDB.COLUMN_CONTACT_MODEL);
        this.contactId = getIntent().getStringExtra(ContactDB.COLUMN_CONTACT_ID);
        this.contactPassword = getIntent().getStringExtra(ContactDB.COLUMN_CONTACT_PASSWORD);
        this.rateList = getIntent().getIntegerArrayListExtra("rateList");
        this.nameList = getIntent().getStringArrayListExtra("nameList");
        this.indicator = getIntent().getIntExtra("indicator", -1);
        this.startTime = (Date) getIntent().getSerializableExtra("startTime");
        this.end = this.nameList.size() - 1;
        this.mContext = this;
        this.activity = this;
        initComponent();
        regFilter();
        startWatcher();
    }

    public void onHomePressed() {
        super.onHomePressed();
        reject();
    }

    private void startWatcher() {
        this.mPhoneWatcher = new PhoneWatcher(this.mContext);
        this.mPhoneWatcher.setOnCommingCallListener(new C10661());
        this.mPhoneWatcher.startWatcher();
    }

    private void initComponent() {
        this.pView = (P2PView) findViewById(C0291R.id.pView);
        initP2PView(P2PConnect.getCurrentDeviceType());
        this.control_bottom = (RelativeLayout) findViewById(C0291R.id.control_bottom);
        this.stopVoice = (ImageView) findViewById(C0291R.id.close_voice);
        this.previous = (ImageView) findViewById(C0291R.id.previous);
        this.pause = (ImageView) findViewById(C0291R.id.pause);
        this.next = (ImageView) findViewById(C0291R.id.next);
        this.seekbar = (SeekBar) findViewById(C0291R.id.seek_bar);
        this.nowTime = (TextView) findViewById(C0291R.id.nowTime);
        this.totalTime = (TextView) findViewById(C0291R.id.totalTime);
        this.text_record = (TextView) findViewById(C0291R.id.text_record);
        this.video_record = (ImageView) findViewById(C0291R.id.video_record);
        this.screenshot = (ImageView) findViewById(C0291R.id.screenshot);
        this.video_record.setOnClickListener(this);
        this.screenshot.setOnClickListener(this);
        this.stopVoice.setOnClickListener(this);
        this.control_bottom.setOnTouchListener(this);
        this.previous.setOnClickListener(this);
        this.pause.setOnClickListener(this);
        this.next.setOnClickListener(this);
        this.seekbar.setOnSeekBarChangeListener(this);
        this.recView = (LinearLayout) findViewById(C0291R.id.view_rec);
        if (!P2PValue.HikamDeviceModelList.contains(this.contactModel)) {
            this.video_record.setVisibility(8);
            this.screenshot.setVisibility(8);
        }
    }

    private void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.P2P_REJECT);
        filter.addAction(P2P.PLAYBACK_CHANGE_SEEK);
        filter.addAction(P2P.PLAYBACK_CHANGE_STATE);
        filter.addAction("android.intent.action.SCREEN_OFF");
        filter.addAction(Action.DIALOG_ALARM_PUSH);
        filter.addAction(P2P.RET_GET_PLAYBACK_FILES);
        registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void updataList(String[] names, int[] rates) {
        Log.e("TGA", "拿到新数据 size" + names.length);
        int len = names.length;
        if (len == 0) {
            C0568T.showShort(this.mContext, (int) C0291R.string.no_next_file);
            return;
        }
        for (int i = 0; i < len; i++) {
            this.nameList.add(names[i]);
            this.rateList.add(Integer.valueOf(rates[i]));
        }
        this.end += len;
        int next = this.indicator + 1;
        if (next <= this.end) {
            Log.e("TGA", "再播放");
            P2pJni.P2PClientSdkPlayBackNext(this.contactId, 5, 0, (String) this.nameList.get(this.indicator), (String) this.nameList.get(next), this.contactPassword);
            this.indicator++;
            return;
        }
        C0568T.showShort(this.mContext, (int) C0291R.string.no_next_file);
    }

    public void reject() {
        if (this.isRecording) {
            stopRecordWithFinish();
        }
        if (this.alarmDialog != null) {
            this.alarmDialog.dismiss();
        }
        if (!this.isReject) {
            this.isReject = true;
            finish();
        }
        if (P2PValue.HikamDeviceModelList.contains(this.contactModel)) {
            P2pJni.P2PClientSdkStopPlayback(this.contactId);
            P2PHandler.getInstance().reject();
        }
        P2PConnect.IS_LIVE_STATE = false;
    }

    public void onBackPressed() {
        reject();
        Log.e("few", "onBackPressed");
    }

    public void onStop() {
        super.onStop();
        if (this.isRegFilter) {
            this.isRegFilter = false;
            unregisterReceiver(this.mReceiver);
        }
    }

    protected void onStart() {
        super.onStart();
        if (!this.isRegFilter) {
            regFilter();
        }
    }

    protected void onPause() {
        super.onPause();
        reject();
    }

    public void onDestroy() {
        if (!P2PConnect.getIsAlarm()) {
            if (this.isRegFilter) {
                this.mContext.unregisterReceiver(this.mReceiver);
                this.isRegFilter = false;
            }
            if (this.mPhoneWatcher != null) {
                this.mPhoneWatcher.stopWatcher();
            }
        }
        P2PConnect.setPlaying(false);
        super.onDestroy();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.close_voice:
                if (P2pJni.P2PMediaGetMute() == 1) {
                    openAudio();
                    return;
                } else {
                    closeAudio();
                    return;
                }
            case C0291R.id.next:
                if (P2PValue.HikamDeviceModelList.contains(this.contactModel)) {
                    int next = this.indicator + 1;
                    if (next <= this.end) {
                        switchRecord();
                        P2pJni.P2PClientSdkPlayBackNext(this.contactId, 5, 0, (String) this.nameList.get(this.indicator), (String) this.nameList.get(next), this.contactPassword);
                        this.fileName = (String) this.nameList.get(next);
                        this.indicator++;
                        return;
                    } else if (this.startTime == null) {
                        C0568T.showShort(this.mContext, (int) C0291R.string.no_next_file);
                        return;
                    } else {
                        switchRecord();
                        Contact contact = new Contact();
                        contact.contactId = this.contactId;
                        contact.contactPassword = this.contactPassword;
                        contact.contactModel = this.contactModel;
                        PlayBackManager.getInstance().searchNextPager(this.startTime, PlayBackManager.getInstance().getDataByTimeString((String) this.nameList.get(this.indicator)), contact);
                        return;
                    }
                } else if (!next(this.fileName)) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.no_next_file);
                    return;
                } else {
                    return;
                }
            case C0291R.id.pause:
                if (P2PValue.HikamDeviceModelList.contains(this.contactModel)) {
                    if (this.isPause) {
                        P2pJni.P2PClientSdkPlaybackControl(this.contactId, 3, 0, this.fileName, this.contactPassword);
                        return;
                    }
                    switchRecord();
                    P2pJni.P2PClientSdkPlaybackControl(this.contactId, 2, 0, this.fileName, this.contactPassword);
                    return;
                } else if (this.isPause) {
                    startPlayBack();
                    return;
                } else {
                    pausePlayBack();
                    return;
                }
            case C0291R.id.previous:
                if (P2PValue.HikamDeviceModelList.contains(this.contactModel)) {
                    int pre = this.indicator - 1;
                    if (pre >= this.start) {
                        switchRecord();
                        P2pJni.P2PClientSdkPlayBackNext(this.contactId, 5, 0, (String) this.nameList.get(this.indicator), (String) this.nameList.get(pre), this.contactPassword);
                        this.fileName = (String) this.nameList.get(pre);
                        this.indicator--;
                        return;
                    }
                    C0568T.showShort(this.mContext, (int) C0291R.string.no_previous_file);
                    return;
                } else if (!previous(this.fileName)) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.no_previous_file);
                    return;
                } else {
                    return;
                }
            case C0291R.id.screenshot:
                P2pJni.P2PClientSdkCaptureScreen(Environment.getExternalStorageDirectory().getPath() + "/screenshot/" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".jpg");
                C0568T.showShort(this.mContext, (int) C0291R.string.capture_success);
                return;
            case C0291R.id.video_record:
                if (this.isPause) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.open_first);
                    return;
                } else if (this.isRecording) {
                    stopRecord();
                    return;
                } else {
                    startRecord();
                    return;
                }
            default:
                return;
        }
    }

    public void changeControl() {
        if (this.isControlShow) {
            this.isControlShow = false;
            Animation anim2 = AnimationUtils.loadAnimation(this, C0291R.anim.slide_out_top);
            anim2.setDuration(300);
            this.control_bottom.startAnimation(anim2);
            this.control_bottom.setVisibility(8);
            return;
        }
        this.isControlShow = true;
        this.control_bottom.setVisibility(0);
        anim2 = AnimationUtils.loadAnimation(this, C0291R.anim.slide_in_bottom);
        anim2.setDuration(300);
        this.control_bottom.startAnimation(anim2);
    }

    public boolean onTouch(View arg0, MotionEvent event) {
        switch (arg0.getId()) {
            case C0291R.id.control_bottom:
                return true;
            default:
                return false;
        }
    }

    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        this.nowTime.setText(convertTime(arg1));
    }

    public void onStartTrackingTouch(SeekBar arg0) {
        Log.e("playback", "onStartTrackingTouch");
        this.isScroll = true;
        stopRecord();
    }

    public void onStopTrackingTouch(SeekBar arg0) {
        Log.e("playback", "onStopTrackingTouch arg0.getProgress():" + arg0.getProgress());
        if (P2PValue.HikamDeviceModelList.contains(this.contactModel)) {
            P2pJni.P2PClientSdkPlaybackDragPos(this.contactId, arg0.getProgress(), this.fileName, this.contactPassword);
            if (this.isPause) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                P2pJni.P2PClientSdkPlaybackDragPos(this.contactId, arg0.getProgress() + 1, this.fileName, this.contactPassword);
            }
        } else {
            jump(arg0.getProgress());
        }
        this.isScroll = false;
    }

    public String convertTime(int time) {
        int hour = time / 3600;
        int minute = (time / 60) - (hour * 60);
        int second = (time - ((hour * 60) * 60)) - (minute * 60);
        String hour_str = hour + "";
        String minute_str = minute + "";
        String second_str = second + "";
        if (minute < 10) {
            minute_str = "0" + minute;
        }
        if (second < 10) {
            second_str = "0" + second;
        }
        return hour_str + ":" + minute_str + ":" + second_str;
    }

    public int getActivityInfo() {
        return 33;
    }

    protected void onP2PViewSingleTap() {
        changeControl();
    }

    protected void onGoBack() {
        MyApp.app.showNotification();
    }

    protected void onGoFront() {
        MyApp.app.hideNotification();
    }

    protected void onExit() {
        MyApp.app.hideNotification();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || event.getAction() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        if (System.currentTimeMillis() - this.exitTime > 2000) {
            C0568T.showShort(this.mContext, (int) C0291R.string.Press_again_exit);
            this.exitTime = System.currentTimeMillis();
            Log.e("few", "onKeyDown 1");
        } else {
            Log.e("few", "onKeyDown 2");
            reject();
        }
        return true;
    }

    public void onCalling() {
        reject();
    }

    public void startRecord() {
        if (!this.isRecording) {
            this.video_record.setImageResource(C0291R.drawable.record_on);
            this.recView.setVisibility(0);
            this.isRecording = true;
            if (P2PValue.HikamDeviceModelList.contains(this.contactModel)) {
                String time_stamp = String.valueOf(System.currentTimeMillis());
                P2pJni.P2PClientSdkVideoRecordStart(time_stamp);
                StringBuilder sb = new StringBuilder();
                sb.append("/storage/emulated/0/hikam_record/hk_camera_");
                sb.append(time_stamp);
                this.currentVideoPath = sb.toString();
                if (HikamDeviceModel.Q5.equals(this.contactModel)) {
                    SharedPreferencesManager.getInstance().setRecordResolution(this, this.currentVideoPath, 960);
                } else {
                    SharedPreferencesManager.getInstance().setRecordResolution(this, this.currentVideoPath, 720);
                }
            }
        }
    }

    public void stopRecord() {
        if (this.isRecording) {
            this.video_record.setImageResource(C0291R.drawable.record_off);
            this.isRecording = false;
            this.recView.setVisibility(8);
            if (P2PValue.HikamDeviceModelList.contains(this.contactModel)) {
                P2pJni.P2PClientSdkVideoRecordFinish();
                new Thread(new C03153()).start();
            }
        }
    }

    public void stopRecordWithFinish() {
        if (P2PValue.HikamDeviceModelList.contains(this.contactModel)) {
            P2pJni.P2PClientSdkVideoRecordFinish();
            new Thread(new C03164()).start();
        }
    }

    public void switchRecord() {
        if (this.isRecording) {
            stopRecord();
            C0568T.showShort(this.mContext, (int) C0291R.string.record_switch_tips2);
        }
    }

    private void closeAudio() {
        P2pJni.P2PMediaSetMute(1);
        this.stopVoice.setImageResource(C0291R.drawable.btn_call_sound_out_s);
    }

    private void openAudio() {
        P2pJni.P2PMediaSetMute(0);
        this.stopVoice.setImageResource(C0291R.drawable.play_volume_p);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() != 25 && event.getKeyCode() != 24) {
            return super.dispatchKeyEvent(event);
        }
        if (((AudioManager) getSystemService("audio")).getStreamVolume(3) == 0) {
            closeAudio();
        } else {
            openAudio();
        }
        return super.dispatchKeyEvent(event);
    }

    protected void onResume() {
        super.onResume();
    }
}
