package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.hikam.C0291R;
import com.jwkj.adapter.IpcListAdapter;
import com.jwkj.global.Constants.Action;
import com.jwkj.utils.Utils;
import com.jwkj.widget.MyPwindow;
import com.p2p.shake.ShakeManager;
import java.net.InetAddress;
import java.util.ArrayList;

public class ShakeActivity extends BaseActivity implements OnClickListener {
    public static final int SHAKING_END = 291;
    private static final int SPEED_SHRESHOLD = 2000;
    private static final int UPTATE_INTERVAL_TIME = 70;
    private IpcListAdapter adapter;
    AnimationDrawable anim_shaking;
    private ImageView back_btn;
    boolean isRegFilter = false;
    boolean isShaking = false;
    private long lastUpdateTime;
    private float lastX;
    private float lastY;
    private float lastZ;
    private ListView list_ipc;
    private Context mContext;
    private Handler mHandler = new Handler(new C04463());
    BroadcastReceiver mReceiver = new C04452();
    private View parent;
    private MyPwindow pwindow;
    private Sensor sensor;
    private SensorManager sensorManager;
    private SensorEventListener shakeListener;
    private RelativeLayout shake_frame;
    private ImageView shaking_img;
    private Vibrator vibrator;
    WakeLock wakeLock = null;

    class C04441 implements SensorEventListener {
        C04441() {
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            if (!ShakeActivity.this.isShaking) {
                long currentUpdateTime = System.currentTimeMillis();
                long timeInterval = currentUpdateTime - ShakeActivity.this.lastUpdateTime;
                if (timeInterval >= 70) {
                    ShakeActivity.this.lastUpdateTime = currentUpdateTime;
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];
                    float deltaX = x - ShakeActivity.this.lastX;
                    float deltaY = y - ShakeActivity.this.lastY;
                    float deltaZ = z - ShakeActivity.this.lastZ;
                    ShakeActivity.this.lastX = x;
                    ShakeActivity.this.lastY = y;
                    ShakeActivity.this.lastZ = z;
                    double speed = (Math.sqrt((double) (((deltaX * deltaX) + (deltaY * deltaY)) + (deltaZ * deltaZ))) / ((double) timeInterval)) * 10000.0d;
                    Log.v("my", "speed:" + speed);
                    if (speed >= 2000.0d) {
                        ShakeActivity.this.adapter.clear();
                        ShakeActivity.this.isShaking = true;
                        ShakeActivity.this.startAnim();
                        ShakeActivity.this.vibrator.vibrate(new long[]{500, 200, 500, 200}, -1);
                    }
                }
            }
        }
    }

    class C04452 extends BroadcastReceiver {
        C04452() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Action.UPDATE_DEVICE_FALG) && ShakeActivity.this.adapter != null) {
                ShakeActivity.this.adapter.updateFlag(intent.getStringExtra("threeNum"), true);
            }
        }
    }

    class C04463 implements Callback {
        C04463() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 17:
                    if (ShakeActivity.this.pwindow != null) {
                        ShakeActivity.this.pwindow.dismiss();
                    }
                    ShakeActivity.this.isShaking = false;
                    ShakeActivity.this.adapter.closeAnim();
                    break;
                case 18:
                    if (ShakeActivity.this.isShaking) {
                        Bundle bundle = msg.getData();
                        String id = bundle.getString("id");
                        String name = bundle.getString(HttpPostBodyUtil.NAME);
                        int flag = bundle.getInt("flag", 1);
                        int type = bundle.getInt("type", 7);
                        InetAddress address = (InetAddress) bundle.getSerializable("address");
                        Log.e("ward shaking", id);
                        ShakeActivity.this.adapter.updateData(id, address, name, flag, type);
                        break;
                    }
                    break;
                case ShakeActivity.SHAKING_END /*291*/:
                    if (ShakeActivity.this.isRegFilter) {
                        ShakeActivity.this.anim_shaking.stop();
                        ShakeManager.getInstance().setHandler(ShakeActivity.this.mHandler);
                        ShakeManager.getInstance().shaking();
                        ShakeManager.getInstance().setSearchTime(5000);
                        ShakeActivity.this.pwindow = new MyPwindow(ShakeActivity.this.mContext, ShakeActivity.this.parent);
                        ShakeActivity.this.pwindow.setContentText(ShakeActivity.this.mContext.getResources().getString(C0291R.string.search_ipc));
                        ShakeActivity.this.pwindow.showToast();
                        break;
                    }
                    break;
            }
            return false;
        }
    }

    class C04474 extends Thread {
        C04474() {
        }

        public void run() {
            Utils.sleepThread(1500);
            Message msg = new Message();
            msg.what = ShakeActivity.SHAKING_END;
            ShakeActivity.this.mHandler.sendMessage(msg);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.fragment_discover);
        this.mContext = this;
        Context context = this.mContext;
        Context context2 = this.mContext;
        this.vibrator = (Vibrator) context.getSystemService("vibrator");
        regFilter();
        initComponent();
    }

    public void initComponent() {
        this.parent = findViewById(C0291R.id.main);
        this.shake_frame = (RelativeLayout) findViewById(C0291R.id.layout_shake);
        this.shaking_img = (ImageView) findViewById(C0291R.id.shaking_img);
        this.anim_shaking = (AnimationDrawable) this.shaking_img.getDrawable();
        this.list_ipc = (ListView) findViewById(C0291R.id.list_ipc);
        this.back_btn = (ImageView) findViewById(C0291R.id.back_btn);
        this.back_btn.setOnClickListener(this);
        this.adapter = new IpcListAdapter(this.mContext, new ArrayList());
        this.list_ipc.setAdapter(this.adapter);
        this.sensorManager = (SensorManager) this.mContext.getSystemService("sensor");
        if (this.sensorManager != null) {
            this.sensor = this.sensorManager.getDefaultSensor(1);
        }
        if (this.sensor != null) {
            this.shakeListener = new C04441();
            this.sensorManager.registerListener(this.shakeListener, this.sensor, 1);
        }
    }

    public void regFilter() {
        this.isRegFilter = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.UPDATE_DEVICE_FALG);
        this.mContext.registerReceiver(this.mReceiver, filter);
    }

    public void startAnim() {
        this.anim_shaking.start();
        new C04474().start();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            default:
                return;
        }
    }

    public void onPause() {
        super.onPause();
        if (this.shakeListener != null) {
            this.sensorManager.unregisterListener(this.shakeListener);
        }
        releaseWakeLock();
    }

    public void onResume() {
        super.onResume();
        acquireWakeLock();
        if (this.sensor != null && this.shakeListener != null) {
            this.sensorManager.registerListener(this.shakeListener, this.sensor, 1);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.isRegFilter = false;
            this.mContext.unregisterReceiver(this.mReceiver);
        }
        if (this.shakeListener != null) {
            this.sensorManager.unregisterListener(this.shakeListener);
        }
        this.isShaking = false;
        ShakeManager.getInstance().stopShaking();
        if (this.pwindow != null) {
            this.pwindow.dismiss();
        }
    }

    private void acquireWakeLock() {
        if (this.wakeLock == null) {
            this.wakeLock = ((PowerManager) this.mContext.getSystemService("power")).newWakeLock(6, getClass().getCanonicalName());
            this.wakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (this.wakeLock != null && this.wakeLock.isHeld()) {
            this.wakeLock.release();
            this.wakeLock = null;
        }
    }

    public int getActivityInfo() {
        return 41;
    }
}
