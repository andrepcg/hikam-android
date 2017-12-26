package com.jwkj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.hikam.C0291R;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.MyApp;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.PhoneWatcher;
import com.jwkj.utils.PhoneWatcher.OnCommingCallListener;
import com.jwkj.widget.AlarmDialog;
import com.jwkj.widget.AlarmDialog.OnCallingListener;
import com.p2p.core.BasePlayBackActivity;
import com.p2p.core.MediaPlayer;
import com.p2p.core.P2PValue;
import com.p2p.core.P2PView;
import java.io.IOException;
import java.util.Date;

public class ShortAVActivity extends BasePlayBackActivity implements OnClickListener, OnTouchListener, OnSeekBarChangeListener, OnCallingListener {
    ShortAVActivity activity;
    private AlarmDialog alarmDialog;
    String contactId;
    String contactModel;
    String contactPassword;
    RelativeLayout control_bottom;
    private int end = -1;
    private long exitTime = 0;
    String fileName;
    private int indicator = -1;
    private boolean isControlShow = true;
    boolean isPause = false;
    boolean isRegFilter = false;
    boolean isReject = false;
    boolean isScroll = false;
    private AudioManager mAudioManager = null;
    Context mContext;
    private int mCurrentVolume;
    private boolean mIsCloseVoice = false;
    private int mMaxVolume;
    PhoneWatcher mPhoneWatcher;
    private BroadcastReceiver mReceiver = new C03203();
    ImageView next;
    TextView nowTime;
    ImageView pause;
    ImageView previous;
    private SeekBar seekbar;
    private int start = 0;
    private Date startTime = null;
    ImageView stopVoice;
    TextView totalTime;

    class C03203 extends BroadcastReceiver {
        C03203() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(P2P.P2P_REJECT)) {
                ShortAVActivity.this.reject();
            } else if (intent.getAction().equalsIgnoreCase("android.intent.action.SCREEN_OFF")) {
                ShortAVActivity.this.reject();
                PlayBackListActivity.IS_SCREEN_OFF = true;
            } else if (intent.getAction().equals(P2P.PLAYBACK_CHANGE_SEEK)) {
                if (!ShortAVActivity.this.isScroll) {
                    int max = intent.getIntExtra("max", 0);
                    int current = intent.getIntExtra("current", 0);
                    ShortAVActivity.this.seekbar.setMax(max);
                    ShortAVActivity.this.seekbar.setProgress(current);
                    ShortAVActivity.this.nowTime.setText(ShortAVActivity.this.convertTime(current));
                    ShortAVActivity.this.totalTime.setText(ShortAVActivity.this.convertTime(max));
                }
            } else if (intent.getAction().equals(P2P.PLAYBACK_CHANGE_STATE)) {
                switch (intent.getIntExtra("state", 0)) {
                    case 0:
                        ShortAVActivity.this.isPause = true;
                        ShortAVActivity.this.pause.setImageResource(C0291R.drawable.playing_start);
                        return;
                    case 1:
                        ShortAVActivity.this.isPause = true;
                        ShortAVActivity.this.pause.setImageResource(C0291R.drawable.playing_start);
                        return;
                    case 2:
                        ShortAVActivity.this.isPause = false;
                        ShortAVActivity.this.pause.setImageResource(C0291R.drawable.playing_pause);
                        return;
                    default:
                        return;
                }
            } else if (intent.getAction().equals(Action.DIALOG_ALARM_PUSH)) {
                ShortAVActivity.this.alarmDialog = new AlarmDialog(intent.getStringExtra("device_model"), intent.getStringExtra("alarm_id"), intent.getIntExtra("alarm_type", 0), intent.getBooleanExtra("isSupport", false), intent.getIntExtra("group", 0), intent.getIntExtra("item", 0));
                ShortAVActivity.this.alarmDialog.show(ShortAVActivity.this.getSupportFragmentManager(), "alarm");
            }
        }
    }

    class C10681 implements OnCommingCallListener {
        C10681() {
        }

        public void onCommingCall() {
            ShortAVActivity.this.reject();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        P2PConnect.setPlaying(true);
        P2PConnect.IS_LIVE_STATE = true;
        Window win = getWindow();
        win.addFlags(4718592);
        win.addFlags(2097280);
        setContentView(C0291R.layout.activity_short_av);
        this.contactModel = getIntent().getStringExtra(ContactDB.COLUMN_CONTACT_MODEL);
        this.contactId = getIntent().getStringExtra(ContactDB.COLUMN_CONTACT_ID);
        this.contactPassword = getIntent().getStringExtra(ContactDB.COLUMN_CONTACT_PASSWORD);
        this.mContext = this;
        this.activity = this;
        initComponent();
        regFilter();
        startWatcher();
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) getSystemService("audio");
        }
        this.mCurrentVolume = this.mAudioManager.getStreamVolume(3);
        this.mMaxVolume = this.mAudioManager.getStreamMaxVolume(3);
    }

    public void onHomePressed() {
        super.onHomePressed();
        reject();
    }

    private void startWatcher() {
        this.mPhoneWatcher = new PhoneWatcher(this.mContext);
        this.mPhoneWatcher.setOnCommingCallListener(new C10681());
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
        this.stopVoice.setOnClickListener(this);
        this.control_bottom.setOnTouchListener(this);
        this.previous.setOnClickListener(this);
        this.pause.setOnClickListener(this);
        this.next.setOnClickListener(this);
        this.seekbar.setOnSeekBarChangeListener(this);
        final MediaPlayer mPlayer = MediaPlayer.getInstance();
        new Thread(new Runnable() {
            public void run() {
                if (P2PValue.HikamDeviceModelList.contains(MediaPlayer.callModel)) {
                    P2pJni.P2PClientSdkInitDecoder();
                } else {
                    MediaPlayer.nativeInit(mPlayer);
                }
                try {
                    mPlayer.setDisplay(ShortAVActivity.this.pView);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mPlayer.start(15);
            }
        }).start();
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

    public void reject() {
        if (this.alarmDialog != null) {
            this.alarmDialog.dismiss();
        }
        if (!this.isReject) {
            this.isReject = true;
            finish();
        }
        if (P2PValue.HikamDeviceModelList.contains(this.contactModel)) {
            Log.e("fw", "channel -0-Backpress--------------");
            MediaPlayer.getInstance().p2p_close_stream();
        }
        P2PConnect.IS_LIVE_STATE = false;
    }

    public void onBackPressed() {
        Log.e("fw", "-0-Backpress--------------");
        reject();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == 0 && event.getKeyCode() == 24) {
            this.mCurrentVolume++;
            if (this.mCurrentVolume > this.mMaxVolume) {
                this.mCurrentVolume = this.mMaxVolume;
            }
            if (this.mCurrentVolume == 0) {
                return false;
            }
            this.mIsCloseVoice = false;
            this.stopVoice.setImageResource(C0291R.drawable.btn_call_sound_out);
            return false;
        } else if (event.getAction() != 0 || event.getKeyCode() != 25) {
            return super.dispatchKeyEvent(event);
        } else {
            this.mCurrentVolume--;
            if (this.mCurrentVolume < 0) {
                this.mCurrentVolume = 0;
            }
            if (this.mCurrentVolume != 0) {
                return false;
            }
            this.mIsCloseVoice = true;
            this.stopVoice.setImageResource(C0291R.drawable.btn_call_sound_out_s);
            return false;
        }
    }

    public void onStop() {
        super.onStop();
        Log.e("few", "哦哦哦 activity stop");
        if (this.isRegFilter) {
            this.isRegFilter = false;
            unregisterReceiver(this.mReceiver);
        }
    }

    protected void onStart() {
        super.onStart();
        Log.e("few", "哦哦哦 activity start");
        if (!this.isRegFilter) {
            regFilter();
        }
    }

    protected void onPause() {
        super.onPause();
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.isRegFilter = false;
        }
    }

    public void onDestroy() {
        if (!P2PConnect.getIsAlarm()) {
            if (this.mAudioManager != null) {
                this.mAudioManager.setStreamVolume(3, this.mCurrentVolume, 0);
            }
            if (this.isRegFilter) {
                this.mContext.unregisterReceiver(this.mReceiver);
                Log.e("few", "1111111111111fanzhuce");
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
                if (this.mIsCloseVoice) {
                    this.mIsCloseVoice = false;
                    this.stopVoice.setImageResource(C0291R.drawable.btn_call_sound_out);
                    if (this.mCurrentVolume == 0) {
                        this.mCurrentVolume = 1;
                    }
                    if (this.mAudioManager != null) {
                        this.mAudioManager.setStreamVolume(3, this.mCurrentVolume, 0);
                        return;
                    }
                    return;
                }
                this.mIsCloseVoice = true;
                this.stopVoice.setImageResource(C0291R.drawable.btn_call_sound_out_s);
                if (this.mAudioManager != null) {
                    this.mAudioManager.setStreamVolume(3, 0, 0);
                    return;
                }
                return;
            case C0291R.id.pause:
                if (P2PValue.HikamDeviceModelList.contains(this.contactModel)) {
                    if (this.isPause) {
                        P2pJni.P2PClientSdkPlaybackControl(this.contactId, 3, 0, this.fileName, this.contactPassword);
                        return;
                    } else {
                        P2pJni.P2PClientSdkPlaybackControl(this.contactId, 2, 0, this.fileName, this.contactPassword);
                        return;
                    }
                } else if (this.isPause) {
                    startPlayBack();
                    return;
                } else {
                    pausePlayBack();
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
        } else {
            reject();
        }
        return true;
    }

    public void onCalling() {
        reject();
    }
}
