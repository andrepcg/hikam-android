package com.jwkj.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.os.StrictMode.VmPolicy;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.UDPHelper;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.guide.AirLinkGuideActivity;
import com.jwkj.widget.guide.GuideConnectActivity;
import com.mediatek.elian.ElianNative;
import com.p2p.core.P2PValue;
import com.zmodo.wifi.DevInfoStructure;
import com.zmodo.wifi.SmartLinkAPI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AddWaitActicity extends BaseActivity implements OnClickListener {
    private long TimeOut;
    private ProgressBar bar;
    NormalDialog dialog;
    ElianNative elain;
    private boolean isNeedSendWifi = true;
    boolean isReceive = false;
    boolean isRegFilter = false;
    private boolean isSendWifiStop = true;
    private boolean isTimerCancel = true;
    private ImageView ivBacke;
    MulticastLock lock;
    private Context mContext;
    boolean mDone = true;
    public UDPHelper mHelper;
    int mLocalIp;
    Thread mThread = null;
    private Timer mTimer;
    public Runnable mrunnable = new C03367();
    public Handler myhandler = new Handler();
    String pwd;
    private Contact saveContact = null;
    private Handler sendWifiHandler = new Handler(new C03334());
    String ssid;
    int status = 0;
    public Runnable stopRunnable = new C03345();
    int sysLang;
    private int time;
    private TextView tv_info;
    private TextView tv_title;
    byte type;

    class C03301 extends Handler {
        C03301() {
        }

        public void handleMessage(Message msg) {
            if (msg.what == 273) {
                AddWaitActicity.this.bar.setProgress(AddWaitActicity.this.status);
            }
        }
    }

    class C03323 extends TimerTask {
        C03323() {
        }

        public void run() {
            if (AddWaitActicity.this.time < 5) {
                AddWaitActicity.this.sendWifiHandler.sendEmptyMessage(0);
            } else {
                AddWaitActicity.this.sendWifiHandler.sendEmptyMessage(1);
            }
        }
    }

    class C03334 implements Callback {
        C03334() {
        }

        public boolean handleMessage(Message arg0) {
            switch (arg0.what) {
                case 0:
                    AddWaitActicity.this.time = AddWaitActicity.this.time + 1;
                    AddWaitActicity.this.sendWifi();
                    break;
                case 1:
                    AddWaitActicity.this.cancleTimer();
                    break;
                case 2:
                    AddWaitActicity.this.stopSendWifi();
                    break;
            }
            return false;
        }
    }

    class C03345 implements Runnable {
        C03345() {
        }

        public void run() {
            AddWaitActicity.this.sendWifiHandler.sendEmptyMessage(2);
        }
    }

    class C03356 extends Handler {
        C03356() {
        }

        public void handleMessage(Message msg) {
            Bundle bundle;
            Intent it;
            FList flist;
            String contactId;
            String frag;
            String ipFlag;
            Contact saveContact;
            Intent add_device;
            switch (msg.what) {
                case 1:
                    Log.e("my", "HANDLER_MESSAGE_BIND_ERROR");
                    C0568T.showShort(AddWaitActicity.this.mContext, (int) C0291R.string.port_is_occupied);
                    break;
                case 2:
                    AddWaitActicity.this.isReceive = true;
                    Log.e("my", "HANDLER_MESSAGE_RECEIVE_MSG");
                    C0568T.showShort(AddWaitActicity.this.mContext, (int) C0291R.string.set_wifi_success);
                    AddWaitActicity.this.mHelper.StopListen();
                    bundle = msg.getData();
                    it = new Intent();
                    it.setAction(Action.RADAR_SET_WIFI_SUCCESS);
                    AddWaitActicity.this.sendBroadcast(it);
                    flist = FList.getInstance();
                    flist.updateOnlineState();
                    flist.searchLocalDevice();
                    flist.updataCameraCover();
                    contactId = bundle.getString(ContactDB.COLUMN_CONTACT_ID);
                    frag = bundle.getString("frag");
                    ipFlag = bundle.getString("ipFlag");
                    saveContact = new Contact();
                    saveContact.contactId = contactId;
                    saveContact.activeUser = NpcCommon.mThreeNum;
                    add_device = new Intent(AddWaitActicity.this.mContext, AddContactNextActivity.class);
                    add_device.putExtra(ContactDB.TABLE_NAME, saveContact);
                    if (Integer.parseInt(frag) == 0) {
                        add_device.putExtra("isCreatePassword", true);
                    } else {
                        add_device.putExtra("isCreatePassword", false);
                    }
                    add_device.putExtra("isfactory", true);
                    add_device.putExtra("ipFlag", ipFlag);
                    AddWaitActicity.this.startActivity(add_device);
                    AddWaitActicity.this.finish();
                    AirLinkGuideActivity.instance.finish();
                    break;
                case 3:
                    AddWaitActicity.this.isReceive = true;
                    Log.e("my", "HANDLER_MESSAGE_RECEIVE_HIKAM_MSG");
                    C0568T.showShort(AddWaitActicity.this.mContext, (int) C0291R.string.set_wifi_success);
                    AddWaitActicity.this.mHelper.StopListen();
                    bundle = msg.getData();
                    it = new Intent();
                    it.setAction(Action.RADAR_SET_WIFI_SUCCESS);
                    AddWaitActicity.this.sendBroadcast(it);
                    flist = FList.getInstance();
                    flist.updateOnlineState();
                    flist.searchLocalDevice();
                    flist.updataCameraCover();
                    String contactModel = bundle.getString(ContactDB.COLUMN_CONTACT_MODEL);
                    contactId = bundle.getString(ContactDB.COLUMN_CONTACT_ID);
                    frag = bundle.getString("frag");
                    ipFlag = bundle.getString("ipFlag");
                    saveContact = new Contact();
                    saveContact.contactId = contactId;
                    saveContact.activeUser = NpcCommon.mThreeNum;
                    saveContact.contactModel = contactModel;
                    add_device = new Intent(AddWaitActicity.this.mContext, GuideConnectActivity.class);
                    add_device.putExtra(ContactDB.TABLE_NAME, saveContact);
                    if (Integer.parseInt(frag) == 0) {
                        add_device.putExtra("isCreatePassword", true);
                    } else {
                        add_device.putExtra("isCreatePassword", false);
                    }
                    add_device.putExtra("isfactory", true);
                    add_device.putExtra("ipFlag", ipFlag);
                    AddWaitActicity.this.startActivity(add_device);
                    AddWaitActicity.this.finish();
                    AirLinkGuideActivity.instance.finish();
                    break;
            }
            AddWaitActicity.this.cancleTimer();
        }
    }

    class C03367 implements Runnable {
        C03367() {
        }

        public void run() {
            if (!AddWaitActicity.this.isReceive) {
                if (AddWaitActicity.this.isNeedSendWifi) {
                    Intent it = new Intent();
                    it.setAction(Action.RADAR_SET_WIFI_FAILED);
                    AddWaitActicity.this.sendBroadcast(it);
                    AddWaitActicity.this.finish();
                    return;
                }
                AddWaitActicity.this.finish();
            }
        }
    }

    static {
        System.loadLibrary("SmartLink");
        System.loadLibrary("elianjni");
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Window win = getWindow();
        win.addFlags(4718592);
        win.addFlags(2097280);
        setContentView(C0291R.layout.activity_add_waite);
        this.mContext = this;
        StrictMode.setThreadPolicy(new Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
        this.lock = ((WifiManager) getApplicationContext().getSystemService("wifi")).createMulticastLock("localWifi");
        this.ssid = getIntent().getStringExtra("ssidname");
        this.pwd = getIntent().getStringExtra("wifiPwd");
        this.type = getIntent().getByteExtra("type", (byte) -1);
        this.mLocalIp = getIntent().getIntExtra("LocalIp", -1);
        this.isNeedSendWifi = getIntent().getBooleanExtra("isNeedSendWifi", true);
        initUI();
        if (this.isNeedSendWifi) {
            this.TimeOut = 110000;
            excuteTimer();
        } else {
            this.TimeOut = 60000;
            this.tv_title.setText(getResources().getString(C0291R.string.qr_code_add_device));
        }
        this.lock.acquire();
        this.mHelper = new UDPHelper(9988);
        listen();
        this.myhandler.postDelayed(this.mrunnable, this.TimeOut);
        this.mHelper.StartListen();
        if (Locale.getDefault().getLanguage().endsWith("zh")) {
            this.sysLang = 1;
        } else {
            this.sysLang = 2;
        }
        this.bar = (ProgressBar) findViewById(C0291R.id.bar);
        final Handler mHandler = new C03301();
        new Thread() {
            public void run() {
                while (AddWaitActicity.this.status < 100) {
                    AddWaitActicity addWaitActicity = AddWaitActicity.this;
                    addWaitActicity.status++;
                    try {
                        Thread.sleep(AddWaitActicity.this.TimeOut / 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message m = new Message();
                    m.what = 273;
                    mHandler.sendMessage(m);
                }
            }
        }.start();
    }

    private void excuteTimer() {
        this.mTimer = new Timer();
        this.mTimer.schedule(new C03323(), 0, 30000);
        this.isTimerCancel = false;
    }

    private void cancleTimer() {
        Log.i("dxsnewTimer", "第" + this.time + "次停止计时器时间:" + getTime());
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.isTimerCancel = true;
        }
    }

    private void sendWifi() {
        Log.i("dxsnewTimer", "第" + this.time + "次发包时间:" + getTime());
        if (this.elain == null) {
            this.elain = new ElianNative();
        }
        if (!(this.ssid == null || "".equals(this.ssid))) {
            this.elain.InitSmartConnection(null, 1, 1);
            this.elain.StartSmartConnection(this.ssid, this.pwd, "", this.type);
            Log.e("wifi_mesg", "ssidname=" + this.ssid + "--wifipwd=" + this.pwd + "--type=" + this.type);
            this.isSendWifiStop = false;
            SmartLinkAPI.SmartLinkStartup();
            SmartLinkAPI.SmartLinkRun(this.ssid, this.pwd, this.sysLang);
        }
        this.sendWifiHandler.postDelayed(this.stopRunnable, 20000);
    }

    private void stopSendWifi() {
        Log.i("dxsnewTimer", "第" + this.time + "次停止发包时间:" + getTime());
        if (this.elain != null) {
            this.elain.StopSmartConnection();
            this.isSendWifiStop = true;
        }
        SmartLinkAPI.SmartLinkStop();
        SmartLinkAPI.SmartLinkClearup();
    }

    private void initUI() {
        this.ivBacke = (ImageView) findViewById(C0291R.id.img_back);
        this.tv_title = (TextView) findViewById(C0291R.id.title);
        this.ivBacke.setOnClickListener(this);
        this.tv_info = (TextView) findViewById(C0291R.id.tv_info);
    }

    void listen() {
        this.mHelper.setCallBack(new C03356());
    }

    public void callback(DevInfoStructure data) {
        SmartLinkAPI.RcveSrvStop();
        Intent it = new Intent();
        it.setAction(Action.RADAR_SET_WIFI_SUCCESS);
        sendBroadcast(it);
        FList flist = FList.getInstance();
        flist.updateOnlineState();
        flist.searchLocalDevice();
        flist.updataCameraCover();
        Contact saveContact = new Contact();
        saveContact.contactId = new String(data.devid);
        if (P2PValue.HikamDeviceModelList.contains(new String(data.devtype))) {
            saveContact.contactModel = new String(data.devtype);
        }
        saveContact.activeUser = NpcCommon.mThreeNum;
        Intent add_device = new Intent(this.mContext, AddContactNextActivity.class);
        add_device.putExtra(ContactDB.TABLE_NAME, saveContact);
        add_device.putExtra("isCreatePassword", false);
        add_device.putExtra("isfactory", true);
        startActivity(add_device);
        finish();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.img_back:
                finish();
                return;
            default:
                return;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        this.myhandler.removeCallbacks(this.mrunnable);
        this.sendWifiHandler.removeCallbacks(this.stopRunnable);
        this.mHelper.StopListen();
        if (!this.isSendWifiStop) {
            stopSendWifi();
        }
        if (!this.isTimerCancel) {
            cancleTimer();
        }
        this.lock.release();
    }

    public int getActivityInfo() {
        return 58;
    }

    private String getTime() {
        return new SimpleDateFormat("HH-mm-ss").format(new Date());
    }
}
