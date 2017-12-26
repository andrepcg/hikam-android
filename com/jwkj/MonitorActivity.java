package com.jwkj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.streamax.miotp.jni.ThreadDrawYuv2Surface;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.hikam.C0291R;
import com.jwkj.activity.MainActivity;
import com.jwkj.data.ContactDB;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import com.jwkj.thread.CycleNpcSettingThread;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.ImageUtils;
import com.jwkj.utils.PhoneWatcher;
import com.jwkj.utils.PhoneWatcher.OnCommingCallListener;
import com.jwkj.widget.AlarmDialog;
import com.jwkj.widget.AlarmDialog.OnCallingListener;
import com.p2p.core.BaseMonitorActivity;
import com.p2p.core.HKVideoScale;
import com.p2p.core.MediaPlayer;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.p2p.core.P2PValue.HikamDeviceModel;
import com.p2p.core.P2PView;
import com.p2p.core.VideoScale;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.jboss.netty.handler.codec.rtsp.RtspHeaders.Values;

public class MonitorActivity extends BaseMonitorActivity implements OnClickListener, OnTouchListener, OnCallingListener {
    public static boolean isPlaying = false;
    public static boolean isWifi = true;
    int USR_CMD_CAR_DIR_CTL = 7;
    private final int USR_CMD_OPTION_CAR_TURN_BACK = 3;
    private final int USR_CMD_OPTION_CAR_TURN_FORWARD = 2;
    private final int USR_CMD_OPTION_CAR_TURN_LEFT = 0;
    private final int USR_CMD_OPTION_CAR_TURN_RIGHT = 1;
    private AlarmDialog alarmDialog;
    Button bt_bottom;
    Button bt_left;
    Button bt_right;
    Button bt_top;
    String callId;
    String callModel;
    ImageView close_voice;
    String contactName;
    RelativeLayout control_bottom;
    LinearLayout control_top;
    int cur_modify_record_type;
    String currentVideoPath;
    int current_video_mode = 5;
    private CycleNpcSettingThread cycleNpcSettingThread = null;
    int device_timezone_index = -1;
    ImageView down;
    private long exitTime = 0;
    private boolean first = true;
    ImageView hungup;
    ImageView img_reverse;
    String isAlarmTrigger;
    boolean isControlShow = true;
    boolean isHD = false;
    boolean isMute = false;
    boolean isOpenSteerWheel = true;
    private boolean isRecording = false;
    boolean isRegFilter = false;
    boolean isReject = false;
    int last_modify_record;
    int last_record;
    RelativeLayout layout_steering_wheel;
    LinearLayout layout_voice_state;
    ImageView left;
    Context mContext;
    public Handler mHandler = new Handler(new C02974());
    PhoneWatcher mPhoneWatcher;
    private BroadcastReceiver mReceiver = new C02985();
    ImageView manual_record;
    private Handler mhandler = new Handler();
    Runnable mrunnable = new C02996();
    public Handler myhandler = new Handler();
    Runnable myrunnable = new C03007();
    String password;
    LinearLayout recView;
    ImageView right;
    ImageView screenshot;
    private SimpleDateFormat sdf_device_time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    ImageView send_voice;
    ImageView steering_wheel;
    TextView text_number;
    TextView text_record;
    private int time_count = 0;
    private TurnBackThread turnBackThread = null;
    private TurnForwardThread turnForwardThread = null;
    private TurnLeftThread turnLeftThread = null;
    private TurnRightThread turnRightThread = null;
    int type;
    ImageView up;
    TextView video_mode_hd;
    TextView video_mode_ld;
    TextView video_mode_sd;
    ImageView video_record;
    ImageView voice_state;

    class C02963 implements OnTouchListener {
        C02963() {
        }

        public boolean onTouch(View arg0, MotionEvent event) {
            switch (event.getAction()) {
                case 0:
                    MonitorActivity.this.send_voice.setImageResource(C0291R.drawable.paly_mic_n);
                    MonitorActivity.this.layout_voice_state.setVisibility(0);
                    MonitorActivity.this.setMute(false);
                    if (!P2PValue.HikamDeviceModelList.contains(MonitorActivity.this.callModel)) {
                        return true;
                    }
                    P2pJni.P2PClientSdkSetMuteFalse(MonitorActivity.this.callId);
                    return true;
                case 1:
                    MonitorActivity.this.send_voice.setImageResource(C0291R.drawable.paly_mic_p);
                    MonitorActivity.this.mhandler.postDelayed(MonitorActivity.this.mrunnable, 1000);
                    return true;
                default:
                    return false;
            }
        }
    }

    class C02974 implements Callback {
        C02974() {
        }

        public boolean handleMessage(Message msg) {
            MonitorActivity.this.pView.updateScreenOrientation();
            return false;
        }
    }

    class C02985 extends BroadcastReceiver {
        C02985() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(P2P.P2P_REJECT)) {
                MonitorActivity.this.reject();
            } else if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                MonitorActivity.this.reject();
            } else if (intent.getAction().equals(P2P.P2P_MONITOR_NUMBER_CHANGE)) {
                if (intent.getIntExtra("number", -1) != -1) {
                    MonitorActivity.this.text_number.setText(MonitorActivity.this.mContext.getResources().getString(C0291R.string.monitor_number) + P2PConnect.getNumber());
                }
            } else if (intent.getAction().equals(P2P.P2P_RESOLUTION_CHANGE)) {
                int mode = intent.getIntExtra(Values.MODE, -1);
                if (mode != -1) {
                    MonitorActivity.this.current_video_mode = mode;
                    MonitorActivity.this.updateVideoModeText(MonitorActivity.this.current_video_mode);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_GET_NPC_SETTINGS)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    MonitorActivity.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:get npc settings");
                    P2PHandler.getInstance().getNpcSettings(MonitorActivity.this.callModel, MonitorActivity.this.callId, MonitorActivity.this.password);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_RECORD_TYPE)) {
                intent.getIntExtra("type", -1);
            } else if (intent.getAction().equals(P2P.RET_GET_REMOTE_RECORD)) {
                state = intent.getIntExtra("state", -1);
                Log.e("alex", "state: " + state);
                if (state == 1) {
                    MonitorActivity.this.last_record = 1;
                    MonitorActivity.this.manual_record.setImageResource(C0291R.drawable.record_on);
                    return;
                }
                MonitorActivity.this.last_record = 0;
                MonitorActivity.this.manual_record.setImageResource(C0291R.drawable.record_off);
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_RECORD_TYPE)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    MonitorActivity.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set npc settings record type");
                    P2PHandler.getInstance().setRecordType(MonitorActivity.this.callModel, MonitorActivity.this.callId, MonitorActivity.this.password, MonitorActivity.this.cur_modify_record_type);
                }
            } else if (intent.getAction().equals(P2P.RET_SET_RECORD_TYPE)) {
                if (intent.getIntExtra("result", -1) == 0) {
                    C0568T.showShort(MonitorActivity.this.mContext, C0291R.string.modify_success);
                } else {
                    C0568T.showShort(MonitorActivity.this.mContext, C0291R.string.operator_error);
                }
            } else if (intent.getAction().equals(P2P.ACK_RET_SET_REMOTE_RECORD)) {
                result = intent.getIntExtra("result", -1);
                if (result == 9999) {
                    i = new Intent();
                    i.setAction(Action.CONTROL_SETTING_PWD_ERROR);
                    MonitorActivity.this.mContext.sendBroadcast(i);
                } else if (result == 9998) {
                    Log.e("my", "net error resend:set remote record");
                    P2PHandler.getInstance().setRemoteRecord(MonitorActivity.this.callModel, MonitorActivity.this.callId, MonitorActivity.this.password, MonitorActivity.this.last_modify_record);
                }
            } else if (intent.getAction().equals(P2P.RET_SET_REMOTE_RECORD)) {
                state = intent.getIntExtra("state", -1);
                P2PHandler.getInstance().getNpcSettings(MonitorActivity.this.callModel, MonitorActivity.this.callId, MonitorActivity.this.password);
            } else if (intent.getAction().equals(P2P.RET_GET_INFRARED_SWITCH)) {
                if (HikamDeviceModel.Q3.equals(MonitorActivity.this.callModel)) {
                    MonitorActivity.this.steering_wheel.setVisibility(8);
                } else if (HikamDeviceModel.Q5.equals(MonitorActivity.this.callModel)) {
                    MonitorActivity.this.steering_wheel.setVisibility(8);
                } else if (HikamDeviceModel.Q7.equals(MonitorActivity.this.callModel)) {
                    MonitorActivity.this.steering_wheel.setVisibility(8);
                } else {
                    MonitorActivity.this.steering_wheel.setVisibility(0);
                }
            } else if (intent.getAction().equals(P2P.RET_GET_TIME_ZONE)) {
                MonitorActivity.this.device_timezone_index = intent.getIntExtra("state", -1);
                Log.e("alex", "-------------------------device timezone index = " + MonitorActivity.this.device_timezone_index);
            } else if (intent.getAction().equals(P2P.RET_GET_TIME)) {
                String device_time = intent.getStringExtra(Values.TIME);
                Calendar system_calendar = Calendar.getInstance();
                Log.e("alex", "-------------------------device time = " + device_time);
                Calendar device_calendar = Calendar.getInstance();
                Date device_date = null;
                try {
                    device_date = MonitorActivity.this.sdf_device_time.parse(device_time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                device_calendar.setTime(device_date);
                int device_hour = device_calendar.get(11);
                int device_minute = device_calendar.get(12);
                int system_hour = system_calendar.get(11);
                if (Math.abs(device_minute - system_calendar.get(12)) >= 1 && MonitorActivity.this.device_timezone_index >= 0) {
                    int[] iArr = new int[24];
                    int device_timezone_offset = ((new int[]{-11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}[MonitorActivity.this.device_timezone_index] * 1000) * 60) * 60;
                    long newLongTime = system_calendar.getTimeInMillis();
                    if (TimeZone.getDefault().inDaylightTime(new Date())) {
                        newLongTime = system_calendar.getTimeInMillis() - ((long) ((TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings()) - device_timezone_offset));
                    } else {
                        newLongTime = system_calendar.getTimeInMillis() - ((long) (TimeZone.getDefault().getRawOffset() - device_timezone_offset));
                    }
                    device_calendar.setTimeInMillis(newLongTime);
                    String new_device_time = MonitorActivity.this.sdf_device_time.format(device_calendar.getTime());
                    Log.e("alex", "-------------------------new device time = " + new_device_time);
                    P2PHandler.getInstance().setDeviceTime(MonitorActivity.this.callModel, MonitorActivity.this.callId, MonitorActivity.this.password, new_device_time);
                }
                MonitorActivity.this.finish();
            } else if (intent.getAction().equals(Action.ACTION_NETWORK_CHANGE)) {
                NetworkInfo networkInfo = ((ConnectivityManager) MonitorActivity.this.mContext.getSystemService("connectivity")).getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isAvailable()) {
                    Log.e("MonitorActivity", "Network disconnection");
                    MonitorActivity.isPlaying = false;
                } else if (networkInfo.isConnected()) {
                    Log.e("MonitorActivity", "Network connection " + networkInfo.getState().toString());
                    if (!MonitorActivity.isPlaying) {
                        Log.e("MonitorActivity", "Network reconnection - Not Playing" + networkInfo.getState().toString());
                        MonitorActivity.this.reject();
                    }
                } else {
                    Log.e("MonitorActivity", "Network connecting " + networkInfo.getState().toString());
                }
            } else if (intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {
                WifiManager wifiManager = (WifiManager) MonitorActivity.this.mContext.getSystemService("wifi");
                int wifiState = intent.getIntExtra("wifi_state", 1);
                if (wifiState == 1) {
                    MonitorActivity.isWifi = false;
                    Log.e("MonitorActivity", "Network wifi disconnection");
                } else if (wifiState == 0) {
                    Log.e("MonitorActivity", "Network  wifi connecting");
                } else if (wifiState == 3) {
                    if (!MonitorActivity.isWifi) {
                        Log.e("MonitorActivity", "Network  wifi WIFI_STATE_ENABLED no wifi");
                        MonitorActivity.this.reject();
                    }
                    Log.e("MonitorActivity", "Network  wifi WIFI_STATE_ENABLED");
                    MonitorActivity.isWifi = true;
                }
            } else if (intent.getAction().equals(Action.DIALOG_ALARM_PUSH)) {
                String device_model = intent.getStringExtra("device_model");
                String alarm_id = intent.getStringExtra("alarm_id");
                int alarm_type = intent.getIntExtra("alarm_type", 0);
                boolean isSupport = intent.getBooleanExtra("isSupport", false);
                int group = intent.getIntExtra("group", 0);
                int item = intent.getIntExtra("item", 0);
                MonitorActivity.this.alarmDialog = new AlarmDialog(device_model, alarm_id, alarm_type, isSupport, group, item);
                MonitorActivity.this.alarmDialog.show(MonitorActivity.this.getSupportFragmentManager(), "alarm");
            } else if (intent.getAction().equals(P2P.RET_LOW_WARNING)) {
                C0568T.showShort(MonitorActivity.this.mContext, C0291R.string.net_warning);
            }
        }
    }

    class C02996 implements Runnable {
        C02996() {
        }

        public void run() {
            MonitorActivity.this.layout_voice_state.setVisibility(8);
            MonitorActivity.this.setMute(true);
            if (P2PValue.HikamDeviceModelList.contains(MonitorActivity.this.callModel)) {
                P2pJni.P2PClientSdkSetMuteTrue(MonitorActivity.this.callId);
            }
        }
    }

    class C03007 implements Runnable {
        C03007() {
        }

        public void run() {
            MonitorActivity.this.reject();
        }
    }

    class C03018 implements Runnable {
        C03018() {
        }

        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                ((AudioManager) MonitorActivity.this.getSystemService("audio")).setStreamMute(3, false);
            } catch (NullPointerException e2) {
            }
        }
    }

    class C03039 implements Runnable {
        C03039() {
        }

        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final int ret = P2pJni.P2PClientSdkVideoRecordMuxer(MonitorActivity.this.currentVideoPath);
            MonitorActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (ret == 0) {
                        C0568T.showShort(MonitorActivity.this.mContext, (int) C0291R.string.record_success);
                    } else {
                        C0568T.showShort(MonitorActivity.this.mContext, (int) C0291R.string.record_failed);
                    }
                }
            });
        }
    }

    private class TurnBackThread extends Thread {
        private boolean running;

        public final void run() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Incorrect nodes count for selectOther: B:11:0x0027 in [B:10:0x0023, B:11:0x0027, B:15:0x0015, B:16:0x0015]
	at jadx.core.utils.BlockUtils.selectOther(BlockUtils.java:53)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:64)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
            /*
            r2 = this;
            r0 = 1;
            r2.running = r0;
        L_0x0003:
            r0 = java.lang.Thread.interrupted();	 Catch:{ InterruptedException -> 0x000d, all -> 0x001e }
            if (r0 != 0) goto L_0x0016;	 Catch:{ InterruptedException -> 0x000d, all -> 0x001e }
        L_0x0009:
            r2.taskBody();	 Catch:{ InterruptedException -> 0x000d, all -> 0x001e }
            goto L_0x0003;
        L_0x000d:
            r0 = move-exception;
            r0 = r2.running;
            if (r0 != 0) goto L_0x0015;
        L_0x0012:
            r2.cleanUp();
        L_0x0015:
            return;
        L_0x0016:
            r0 = r2.running;
            if (r0 != 0) goto L_0x0015;
        L_0x001a:
            r2.cleanUp();
            goto L_0x0015;
        L_0x001e:
            r0 = move-exception;
            r1 = r2.running;
            if (r1 != 0) goto L_0x0027;
        L_0x0023:
            r2.cleanUp();
            goto L_0x0015;
        L_0x0027:
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.jwkj.MonitorActivity.TurnBackThread.run():void");
        }

        private TurnBackThread() {
            this.running = false;
        }

        private void cleanUp() {
        }

        private void taskBody() throws InterruptedException {
            MediaPlayer.getInstance().native_p2p_controls(MonitorActivity.this.callId, MonitorActivity.this.password, 3);
            Thread.sleep(200);
        }

        public void terminate() {
            this.running = false;
            interrupt();
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class TurnForwardThread extends Thread {
        private boolean running;

        public final void run() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Incorrect nodes count for selectOther: B:11:0x0027 in [B:10:0x0023, B:11:0x0027, B:15:0x0015, B:16:0x0015]
	at jadx.core.utils.BlockUtils.selectOther(BlockUtils.java:53)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:64)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
            /*
            r2 = this;
            r0 = 1;
            r2.running = r0;
        L_0x0003:
            r0 = java.lang.Thread.interrupted();	 Catch:{ InterruptedException -> 0x000d, all -> 0x001e }
            if (r0 != 0) goto L_0x0016;	 Catch:{ InterruptedException -> 0x000d, all -> 0x001e }
        L_0x0009:
            r2.taskBody();	 Catch:{ InterruptedException -> 0x000d, all -> 0x001e }
            goto L_0x0003;
        L_0x000d:
            r0 = move-exception;
            r0 = r2.running;
            if (r0 != 0) goto L_0x0015;
        L_0x0012:
            r2.cleanUp();
        L_0x0015:
            return;
        L_0x0016:
            r0 = r2.running;
            if (r0 != 0) goto L_0x0015;
        L_0x001a:
            r2.cleanUp();
            goto L_0x0015;
        L_0x001e:
            r0 = move-exception;
            r1 = r2.running;
            if (r1 != 0) goto L_0x0027;
        L_0x0023:
            r2.cleanUp();
            goto L_0x0015;
        L_0x0027:
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.jwkj.MonitorActivity.TurnForwardThread.run():void");
        }

        private TurnForwardThread() {
            this.running = false;
        }

        private void cleanUp() {
        }

        private void taskBody() throws InterruptedException {
            MediaPlayer.getInstance().native_p2p_controls(MonitorActivity.this.callId, MonitorActivity.this.password, 2);
            Thread.sleep(200);
        }

        public void terminate() {
            this.running = false;
            interrupt();
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class TurnLeftThread extends Thread {
        private boolean running;

        public final void run() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Incorrect nodes count for selectOther: B:11:0x0027 in [B:10:0x0023, B:11:0x0027, B:15:0x0015, B:16:0x0015]
	at jadx.core.utils.BlockUtils.selectOther(BlockUtils.java:53)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:64)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
            /*
            r2 = this;
            r0 = 1;
            r2.running = r0;
        L_0x0003:
            r0 = java.lang.Thread.interrupted();	 Catch:{ InterruptedException -> 0x000d, all -> 0x001e }
            if (r0 != 0) goto L_0x0016;	 Catch:{ InterruptedException -> 0x000d, all -> 0x001e }
        L_0x0009:
            r2.taskBody();	 Catch:{ InterruptedException -> 0x000d, all -> 0x001e }
            goto L_0x0003;
        L_0x000d:
            r0 = move-exception;
            r0 = r2.running;
            if (r0 != 0) goto L_0x0015;
        L_0x0012:
            r2.cleanUp();
        L_0x0015:
            return;
        L_0x0016:
            r0 = r2.running;
            if (r0 != 0) goto L_0x0015;
        L_0x001a:
            r2.cleanUp();
            goto L_0x0015;
        L_0x001e:
            r0 = move-exception;
            r1 = r2.running;
            if (r1 != 0) goto L_0x0027;
        L_0x0023:
            r2.cleanUp();
            goto L_0x0015;
        L_0x0027:
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.jwkj.MonitorActivity.TurnLeftThread.run():void");
        }

        private TurnLeftThread() {
            this.running = false;
        }

        private void cleanUp() {
        }

        private void taskBody() throws InterruptedException {
            MediaPlayer.getInstance().native_p2p_controls(MonitorActivity.this.callId, MonitorActivity.this.password, 0);
            Thread.sleep(200);
        }

        public void terminate() {
            this.running = false;
            interrupt();
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class TurnRightThread extends Thread {
        private boolean running;

        public final void run() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Incorrect nodes count for selectOther: B:11:0x0027 in [B:10:0x0023, B:11:0x0027, B:15:0x0015, B:16:0x0015]
	at jadx.core.utils.BlockUtils.selectOther(BlockUtils.java:53)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:64)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
            /*
            r2 = this;
            r0 = 1;
            r2.running = r0;
        L_0x0003:
            r0 = java.lang.Thread.interrupted();	 Catch:{ InterruptedException -> 0x000d, all -> 0x001e }
            if (r0 != 0) goto L_0x0016;	 Catch:{ InterruptedException -> 0x000d, all -> 0x001e }
        L_0x0009:
            r2.taskBody();	 Catch:{ InterruptedException -> 0x000d, all -> 0x001e }
            goto L_0x0003;
        L_0x000d:
            r0 = move-exception;
            r0 = r2.running;
            if (r0 != 0) goto L_0x0015;
        L_0x0012:
            r2.cleanUp();
        L_0x0015:
            return;
        L_0x0016:
            r0 = r2.running;
            if (r0 != 0) goto L_0x0015;
        L_0x001a:
            r2.cleanUp();
            goto L_0x0015;
        L_0x001e:
            r0 = move-exception;
            r1 = r2.running;
            if (r1 != 0) goto L_0x0027;
        L_0x0023:
            r2.cleanUp();
            goto L_0x0015;
        L_0x0027:
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.jwkj.MonitorActivity.TurnRightThread.run():void");
        }

        private TurnRightThread() {
            this.running = false;
        }

        private void cleanUp() {
        }

        private void taskBody() throws InterruptedException {
            MediaPlayer.getInstance().native_p2p_controls(MonitorActivity.this.callId, MonitorActivity.this.password, 1);
            Thread.sleep(200);
        }

        public void terminate() {
            this.running = false;
            interrupt();
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class C10651 implements OnCommingCallListener {
        C10651() {
        }

        public void onCommingCall() {
            MonitorActivity.this.reject();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        P2PConnect.setPlaying(true);
        isPlaying = true;
        P2PConnect.IS_LIVE_STATE = true;
        Window win = getWindow();
        win.addFlags(4718592);
        win.addFlags(2097280);
        setContentView(C0291R.layout.p2p_monitor);
        this.type = getIntent().getIntExtra("type", -1);
        this.callId = getIntent().getStringExtra("callId");
        this.password = getIntent().getStringExtra("password");
        this.callModel = getIntent().getStringExtra("callModel");
        this.isAlarmTrigger = getIntent().getStringExtra("isAlarmTrigger");
        this.contactName = getIntent().getStringExtra(ContactDB.COLUMN_CONTACT_NAME);
        this.mContext = this;
        initData(this.callId, this.password);
        initComponent();
        regFilter();
        startWatcher();
        if (HikamDeviceModel.Q8.equals(this.callModel)) {
            this.steering_wheel.setVisibility(0);
        }
        if (P2PValue.HikamDeviceModelList.contains(this.callModel)) {
            this.cycleNpcSettingThread = new CycleNpcSettingThread(this.callModel, this.callId, this.password);
            this.cycleNpcSettingThread.startGetNpcSettings();
            this.video_record.setVisibility(0);
        } else {
            openAudio2();
            P2PHandler.getInstance().getNpcSettings(this.callModel, this.callId, this.password);
        }
        if ("true".equals(this.isAlarmTrigger)) {
            this.myhandler.postDelayed(this.myrunnable, 60000);
        }
        Log.e("test", "test onCreate");
    }

    public void onHomePressed() {
        super.onHomePressed();
        reject();
    }

    private void startWatcher() {
        this.mPhoneWatcher = new PhoneWatcher(this.mContext);
        this.mPhoneWatcher.setOnCommingCallListener(new C10651());
        this.mPhoneWatcher.startWatcher();
    }

    public void initComponent() {
        this.pView = (P2PView) findViewById(C0291R.id.pView);
        if (P2PValue.HikamDeviceModelList.contains(this.callModel)) {
            this.pView.setHKVideoScale(new HKVideoScale(this.pView, null));
        } else {
            this.pView.setVideoScale(new VideoScale(this.pView, null));
        }
        initP2PView(P2PConnect.getCurrentDeviceType());
        setMute(true);
        this.recView = (LinearLayout) findViewById(C0291R.id.view_rec);
        this.screenshot = (ImageView) findViewById(C0291R.id.screenshot);
        this.hungup = (ImageView) findViewById(C0291R.id.hungup);
        this.close_voice = (ImageView) findViewById(C0291R.id.close_voice);
        this.steering_wheel = (ImageView) findViewById(C0291R.id.steering_wheel);
        this.control_bottom = (RelativeLayout) findViewById(C0291R.id.control_bottom);
        this.control_top = (LinearLayout) findViewById(C0291R.id.control_top);
        this.layout_voice_state = (LinearLayout) findViewById(C0291R.id.layout_voice_state);
        this.send_voice = (ImageView) findViewById(C0291R.id.send_voice);
        this.voice_state = (ImageView) findViewById(C0291R.id.voice_state);
        this.manual_record = (ImageView) findViewById(C0291R.id.manual_record);
        this.video_record = (ImageView) findViewById(C0291R.id.video_record);
        this.video_mode_hd = (TextView) findViewById(C0291R.id.video_mode_hd);
        this.video_mode_sd = (TextView) findViewById(C0291R.id.video_mode_sd);
        this.video_mode_ld = (TextView) findViewById(C0291R.id.video_mode_ld);
        this.text_record = (TextView) findViewById(C0291R.id.text_record);
        this.layout_steering_wheel = (RelativeLayout) findViewById(C0291R.id.layout_steering_wheel);
        this.up = (ImageView) findViewById(C0291R.id.top_btn);
        this.down = (ImageView) findViewById(C0291R.id.bottom_btn);
        this.left = (ImageView) findViewById(C0291R.id.left_btn);
        this.right = (ImageView) findViewById(C0291R.id.right_btn);
        this.text_number = (TextView) findViewById(C0291R.id.text_number);
        this.text_number.setText(getResources().getString(C0291R.string.monitor_number) + P2PConnect.getNumber());
        P2PHandler.getInstance().setVideoMode(this.password, this.callModel, this.current_video_mode);
        updateVideoModeText(this.current_video_mode);
        if (P2PValue.HikamDeviceModelList.contains(this.callModel)) {
            this.video_mode_ld.setVisibility(8);
        }
        if (P2PConnect.getCurrentDeviceType() == 7) {
            this.video_mode_hd.setVisibility(0);
        } else {
            this.video_mode_hd.setVisibility(8);
        }
        final AnimationDrawable anim = (AnimationDrawable) this.voice_state.getDrawable();
        this.voice_state.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            public boolean onPreDraw() {
                anim.start();
                return true;
            }
        });
        this.send_voice.setOnTouchListener(new C02963());
        this.screenshot.setOnClickListener(this);
        this.hungup.setOnClickListener(this);
        this.close_voice.setOnClickListener(this);
        this.video_mode_hd.setOnClickListener(this);
        this.video_mode_sd.setOnClickListener(this);
        this.video_mode_ld.setOnClickListener(this);
        this.steering_wheel.setOnClickListener(this);
        this.manual_record.setOnClickListener(this);
        this.video_record.setOnClickListener(this);
        this.up.setOnTouchListener(this);
        this.down.setOnTouchListener(this);
        this.left.setOnTouchListener(this);
        this.right.setOnTouchListener(this);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.ACTION_NETWORK_CHANGE);
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction(P2P.P2P_REJECT);
        filter.addAction(P2P.P2P_MONITOR_NUMBER_CHANGE);
        filter.addAction(P2P.P2P_RESOLUTION_CHANGE);
        filter.addAction("android.intent.action.SCREEN_OFF");
        filter.addAction(P2P.ACK_RET_GET_NPC_SETTINGS);
        filter.addAction(P2P.ACK_RET_SET_RECORD_TYPE);
        filter.addAction(P2P.RET_SET_RECORD_TYPE);
        filter.addAction(P2P.RET_GET_RECORD_TYPE);
        filter.addAction(P2P.ACK_RET_SET_REMOTE_RECORD);
        filter.addAction(P2P.RET_SET_REMOTE_RECORD);
        filter.addAction(P2P.RET_GET_REMOTE_RECORD);
        filter.addAction(P2P.RET_GET_INFRARED_SWITCH);
        filter.addAction(P2P.RET_GET_TIME);
        filter.addAction(P2P.RET_GET_TIME_ZONE);
        filter.addAction(Action.DIALOG_ALARM_PUSH);
        filter.addAction(P2P.RET_LOW_WARNING);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("my", "onConfigurationChanged:" + newConfig);
        if (newConfig.orientation == 2) {
            this.mHandler.sendEmptyMessageDelayed(0, 500);
        } else {
            this.mHandler.sendEmptyMessageDelayed(0, 500);
        }
    }

    public void changeControl() {
        if (this.isControlShow) {
            this.isControlShow = false;
            Animation anim2 = AnimationUtils.loadAnimation(this, 17432577);
            anim2.setDuration(200);
            this.control_bottom.startAnimation(anim2);
            this.control_bottom.setVisibility(8);
            this.control_top.startAnimation(anim2);
            this.control_top.setVisibility(8);
            this.layout_steering_wheel.setVisibility(8);
            return;
        }
        this.isControlShow = true;
        this.control_bottom.setVisibility(0);
        this.control_top.setVisibility(0);
        anim2 = AnimationUtils.loadAnimation(this, 17432576);
        anim2.setDuration(200);
        this.control_bottom.startAnimation(anim2);
        this.control_top.startAnimation(anim2);
        if (this.isOpenSteerWheel) {
            this.layout_steering_wheel.setVisibility(8);
        } else {
            this.layout_steering_wheel.setVisibility(0);
        }
    }

    public void updateVideoModeText(int mode) {
        if (mode == 7) {
            this.video_mode_hd.setTextColor(this.mContext.getResources().getColor(C0291R.color.text_color_blue));
            this.video_mode_sd.setTextColor(this.mContext.getResources().getColor(C0291R.color.text_color_white));
            this.video_mode_ld.setTextColor(this.mContext.getResources().getColor(C0291R.color.text_color_white));
        } else if (mode == 5) {
            this.video_mode_hd.setTextColor(this.mContext.getResources().getColor(C0291R.color.text_color_white));
            this.video_mode_sd.setTextColor(this.mContext.getResources().getColor(C0291R.color.text_color_blue));
            this.video_mode_ld.setTextColor(this.mContext.getResources().getColor(C0291R.color.text_color_white));
        } else if (mode == 6) {
            this.video_mode_hd.setTextColor(this.mContext.getResources().getColor(C0291R.color.text_color_white));
            this.video_mode_sd.setTextColor(this.mContext.getResources().getColor(C0291R.color.text_color_white));
            this.video_mode_ld.setTextColor(this.mContext.getResources().getColor(C0291R.color.text_color_blue));
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.bottom_btn:
                MediaPlayer.getInstance().native_p2p_controls(this.callId, this.password, 2);
                Log.e("few", "fewjiofjewiojfoiewjofijewiofjioewjiofjew");
                return;
            case C0291R.id.close_voice:
                if (P2PValue.HikamDeviceModelList.contains(this.callModel)) {
                    if (P2pJni.P2PMediaGetMute() == 1) {
                        openAudio();
                        return;
                    } else {
                        closeAudio();
                        return;
                    }
                } else if (this.isMute) {
                    openAudio2();
                    return;
                } else {
                    closeAudio2();
                    return;
                }
            case C0291R.id.hungup:
                reject();
                return;
            case C0291R.id.left_btn:
                MediaPlayer.getInstance().native_p2p_controls(this.callId, this.password, 0);
                return;
            case C0291R.id.manual_record:
                if (this.last_record == 1) {
                    this.last_modify_record = 0;
                    P2PHandler.getInstance().setRemoteRecord(this.callModel, this.callId, this.password, this.last_modify_record);
                } else {
                    this.last_modify_record = 1;
                    P2PHandler.getInstance().setRemoteRecord(this.callModel, this.callId, this.password, this.last_modify_record);
                }
                this.cur_modify_record_type = 0;
                P2PHandler.getInstance().setRecordType(this.callModel, this.callId, this.password, this.cur_modify_record_type);
                return;
            case C0291R.id.right_btn:
                MediaPlayer.getInstance().native_p2p_controls(this.callId, this.password, 1);
                return;
            case C0291R.id.screenshot:
                if (P2PValue.HikamDeviceModelList.contains(this.callModel)) {
                    P2pJni.P2PClientSdkCaptureScreen(Environment.getExternalStorageDirectory().getPath() + "/screenshot/" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".jpg");
                    C0568T.showShort(this.mContext, (int) C0291R.string.capture_success);
                    return;
                }
                captureScreen();
                return;
            case C0291R.id.steering_wheel:
                if (this.isOpenSteerWheel) {
                    this.isOpenSteerWheel = false;
                    this.layout_steering_wheel.setVisibility(0);
                    this.steering_wheel.setImageResource(C0291R.drawable.b_play_controller1_d);
                    return;
                }
                this.isOpenSteerWheel = true;
                this.layout_steering_wheel.setVisibility(8);
                this.steering_wheel.setImageResource(C0291R.drawable.b_play_controller1_n);
                return;
            case C0291R.id.top_btn:
                MediaPlayer.getInstance().native_p2p_controls(this.callId, this.password, 3);
                Log.e("few", "fewjiofjewiojfoiewjofijewiofjioewjiofjew11");
                return;
            case C0291R.id.video_mode_hd:
                if (this.current_video_mode != 7) {
                    switchRecord();
                    this.current_video_mode = 7;
                    P2PHandler.getInstance().setVideoMode(this.password, this.callModel, 7);
                    updateVideoModeText(this.current_video_mode);
                    return;
                }
                return;
            case C0291R.id.video_mode_ld:
                if (this.current_video_mode != 6) {
                    switchRecord();
                    this.current_video_mode = 6;
                    P2PHandler.getInstance().setVideoMode(this.password, this.callModel, 6);
                    updateVideoModeText(this.current_video_mode);
                    return;
                }
                return;
            case C0291R.id.video_mode_sd:
                if (this.current_video_mode != 5) {
                    switchRecord();
                    this.current_video_mode = 5;
                    P2PHandler.getInstance().setVideoMode(this.password, this.callModel, 5);
                    updateVideoModeText(this.current_video_mode);
                    return;
                }
                return;
            case C0291R.id.video_record:
                if (this.isRecording) {
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

    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case C0291R.id.bottom_btn:
                if (event.getAction() == 1) {
                    this.turnForwardThread.terminate();
                }
                if (event.getAction() == 0) {
                    this.turnForwardThread = new TurnForwardThread();
                    this.turnForwardThread.start();
                    break;
                }
                break;
            case C0291R.id.left_btn:
                if (event.getAction() == 1) {
                    this.turnLeftThread.terminate();
                }
                if (event.getAction() == 0) {
                    this.turnLeftThread = new TurnLeftThread();
                    this.turnLeftThread.start();
                    break;
                }
                break;
            case C0291R.id.right_btn:
                if (event.getAction() == 1) {
                    this.turnRightThread.terminate();
                }
                if (event.getAction() == 0) {
                    this.turnRightThread = new TurnRightThread();
                    this.turnRightThread.start();
                    break;
                }
                break;
            case C0291R.id.top_btn:
                if (event.getAction() == 1) {
                    this.turnBackThread.terminate();
                }
                if (event.getAction() == 0) {
                    this.turnBackThread = new TurnBackThread();
                    this.turnBackThread.start();
                    break;
                }
                break;
        }
        return true;
    }

    public void onBackPressed() {
        reject();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.e("test", "test onDestroy");
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.isRegFilter = false;
        }
        if (this.mPhoneWatcher != null) {
            this.mPhoneWatcher.stopWatcher();
        }
        P2PConnect.setPlaying(false);
        if (!activity_stack.containsKey(Integer.valueOf(1))) {
            startActivity(new Intent(this, MainActivity.class));
        }
        if ("true".equals(this.isAlarmTrigger)) {
            this.myhandler.removeCallbacks(this.myrunnable);
        }
        if (P2PValue.HikamDeviceModelList.contains(this.callModel) && this.cycleNpcSettingThread != null) {
            this.cycleNpcSettingThread.stopGetNpcSettings();
        }
        Intent refreshContans = new Intent();
        refreshContans.setAction(Action.REFRESH_CONTANTS);
        this.mContext.sendBroadcast(refreshContans);
    }

    public int getActivityInfo() {
        return 35;
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

    protected void onCaptureScreenResult(boolean isSuccess) {
        if (isSuccess) {
            C0568T.showShort(this.mContext, (int) C0291R.string.capture_success);
        } else {
            C0568T.showShort(this.mContext, (int) C0291R.string.capture_failed);
        }
    }

    public void reject() {
        Log.e("gew", "-0-reject");
        if (!this.isReject) {
            this.isReject = true;
            isPlaying = false;
            P2PConnect.IS_LIVE_STATE = false;
            if (this.alarmDialog != null) {
                this.alarmDialog.dismiss();
            }
            if (P2PValue.HikamDeviceModelList.contains(this.callModel)) {
                Bitmap bitmap = ThreadDrawYuv2Surface.srcBtm;
                if (bitmap != null) {
                    ImageUtils.saveImg(bitmap, Environment.getExternalStorageDirectory().getPath() + "/screenshot/tempHead/" + NpcCommon.mThreeNum, this.callId + ".jpg");
                }
                if (!P2PConnect.getIsAlarm()) {
                    MediaPlayer.getInstance().p2p_close_stream();
                }
                P2PHandler.getInstance().reject();
                finish();
            } else {
                P2PHandler.getInstance().getDeviceTime(this.callModel, this.callId, this.password);
                P2PHandler.getInstance().reject();
            }
            if (this.isRecording) {
                stopRecordWithFinish();
            }
            if (this.isMute) {
                new Thread(new C03018()).start();
            }
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        Bundle bundle = new Bundle();
        bundle.putInt("current_video_mode", this.current_video_mode);
        outState.putBundle("bundle", bundle);
        super.onSaveInstanceState(outState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Bundle bundle = savedInstanceState.getBundle("bundle");
            int storeMode = 5;
            if (bundle != null) {
                storeMode = bundle.getInt("current_video_mode");
            }
            this.current_video_mode = storeMode;
            P2PHandler.getInstance().setVideoMode(this.password, this.callModel, this.current_video_mode);
            updateVideoModeText(this.current_video_mode);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || event.getAction() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        if (System.currentTimeMillis() - this.exitTime > 2000) {
            C0568T.showShort(this.mContext, (int) C0291R.string.press_again_monitor);
            this.exitTime = System.currentTimeMillis();
        } else {
            reject();
        }
        return true;
    }

    public void onCalling() {
        MediaPlayer.getInstance().p2p_close_stream();
        reject();
    }

    public void startRecord() {
        this.time_count = 0;
        this.video_record.setImageResource(C0291R.drawable.record_on);
        this.recView.setVisibility(0);
        this.isRecording = true;
        if (P2PValue.HikamDeviceModelList.contains(this.callModel)) {
            String time_stamp = String.valueOf(System.currentTimeMillis());
            P2pJni.P2PClientSdkVideoRecordStart(time_stamp);
            StringBuilder sb = new StringBuilder();
            sb.append("/storage/emulated/0/hikam_record/hk_camera_");
            sb.append(time_stamp);
            this.currentVideoPath = sb.toString();
            if (HikamDeviceModel.Q5.equals(this.callModel) && this.current_video_mode == 7) {
                SharedPreferencesManager.getInstance().setRecordResolution(this, this.currentVideoPath, 960);
            } else {
                SharedPreferencesManager.getInstance().setRecordResolution(this, this.currentVideoPath, 720);
            }
        }
    }

    public void stopRecord() {
        this.video_record.setImageResource(C0291R.drawable.record_off);
        this.isRecording = false;
        this.recView.setVisibility(8);
        if (P2PValue.HikamDeviceModelList.contains(this.callModel)) {
            P2pJni.P2PClientSdkVideoRecordFinish();
            new Thread(new C03039()).start();
        }
    }

    public void stopRecordWithFinish() {
        if (P2PValue.HikamDeviceModelList.contains(this.callModel)) {
            P2pJni.P2PClientSdkVideoRecordFinish();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int ret = P2pJni.P2PClientSdkVideoRecordMuxer(MonitorActivity.this.currentVideoPath);
                }
            }).start();
        }
    }

    public void switchRecord() {
        if (this.isRecording) {
            stopRecord();
            C0568T.showShort(this.mContext, (int) C0291R.string.record_switch_tips);
        }
    }

    private void closeAudio() {
        P2pJni.P2PMediaSetMute(1);
        this.close_voice.setImageResource(C0291R.drawable.btn_call_sound_out_s);
    }

    private void openAudio() {
        P2pJni.P2PMediaSetMute(0);
        this.close_voice.setImageResource(C0291R.drawable.play_volume_p);
    }

    private void closeAudio2() {
        this.isMute = true;
        ((AudioManager) getSystemService("audio")).setStreamVolume(3, 0, 0);
        this.close_voice.setImageResource(C0291R.drawable.btn_call_sound_out_s);
    }

    private void openAudio2() {
        this.isMute = false;
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        audioManager.setStreamVolume(3, (audioManager.getStreamMaxVolume(3) * 8) / 10, 0);
        this.close_voice.setImageResource(C0291R.drawable.play_volume_p);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() != 25 && event.getKeyCode() != 24) {
            return super.dispatchKeyEvent(event);
        }
        if (((AudioManager) getSystemService("audio")).getStreamVolume(3) == 0) {
            this.isMute = true;
            closeAudio();
        } else {
            this.isMute = false;
            openAudio();
        }
        return super.dispatchKeyEvent(event);
    }
}
