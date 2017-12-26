package com.jwkj.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import com.google.zxing.WriterException;
import com.hikam.C0291R;
import com.jwkj.global.Constants.Action;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.EncodingHandler;
import com.jwkj.utils.UDPHelper;
import com.jwkj.widget.NormalDialog;

public class CreateQRcodeActivity extends BaseActivity implements OnClickListener {
    Button bt_hear;
    Button bt_help;
    Button bt_no_hear;
    ImageView img_back;
    ImageView img_qrcode;
    boolean isReceive = false;
    MulticastLock lock;
    private Context mContext;
    UDPHelper mHelper;
    public Runnable mrunnable = new C03612();
    private Handler myhandler = new Handler();
    String ssidname;
    NormalDialog waitdialog;
    String wifipwd;

    class C03601 extends Handler {
        C03601() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.e("my", "HANDLER_MESSAGE_BIND_ERROR");
                    C0568T.showShort(CreateQRcodeActivity.this.mContext, (int) C0291R.string.port_is_occupied);
                    return;
                case 2:
                    CreateQRcodeActivity.this.isReceive = true;
                    Log.e("my", "HANDLER_MESSAGE_RECEIVE_MSG");
                    CreateQRcodeActivity.this.waitdialog.dismiss();
                    C0568T.showShort(CreateQRcodeActivity.this.mContext, (int) C0291R.string.set_wifi_success);
                    CreateQRcodeActivity.this.mHelper.StopListen();
                    Intent it = new Intent();
                    it.setAction(Action.SETTING_WIFI_SUCCESS);
                    CreateQRcodeActivity.this.mContext.sendBroadcast(it);
                    CreateQRcodeActivity.this.finish();
                    return;
                default:
                    return;
            }
        }
    }

    class C03612 implements Runnable {
        C03612() {
        }

        public void run() {
            if (!CreateQRcodeActivity.this.isReceive) {
                CreateQRcodeActivity.this.waitdialog.dismiss();
                new NormalDialog(CreateQRcodeActivity.this.mContext).showSmartscanFail();
            }
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Window win = getWindow();
        win.addFlags(4718592);
        win.addFlags(2097280);
        this.mContext = this;
        setContentView(C0291R.layout.activity_creat_qr_code);
        this.lock = ((WifiManager) getApplicationContext().getSystemService("wifi")).createMulticastLock("localWifi");
        this.ssidname = getIntent().getStringExtra("ssidname");
        this.wifipwd = getIntent().getStringExtra("wifiPwd");
        initComponent();
        maxVoice();
        qrcode();
        this.lock.acquire();
        this.mHelper = new UDPHelper(9988);
        this.mHelper.setCallBack(new C03601());
    }

    public void initComponent() {
        this.img_qrcode = (ImageView) findViewById(C0291R.id.img_qrcode);
        this.img_back = (ImageView) findViewById(C0291R.id.img_back);
        this.bt_hear = (Button) findViewById(C0291R.id.bt_hear);
        this.bt_no_hear = (Button) findViewById(C0291R.id.bt_no_hear);
        this.bt_help = (Button) findViewById(C0291R.id.bt_help);
        this.img_back.setOnClickListener(this);
        this.bt_hear.setOnClickListener(this);
        this.bt_no_hear.setOnClickListener(this);
        this.bt_help.setOnClickListener(this);
    }

    public void qrcode() {
        try {
            this.img_qrcode.setImageBitmap(EncodingHandler.createQRCode("EnCtYpE_ePyTcNeEsSiD" + this.ssidname + "dIsSeCoDe" + this.wifipwd + "eDoC", ((LayoutParams) this.img_qrcode.getLayoutParams()).width));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void maxVoice() {
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        audioManager.setStreamVolume(3, audioManager.getStreamMaxVolume(3), 0);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.bt_hear:
                this.waitdialog = new NormalDialog(this.mContext);
                this.waitdialog.showWaitConnectionDialog();
                this.mHelper.StartListen();
                this.myhandler.postDelayed(this.mrunnable, 60000);
                return;
            case C0291R.id.bt_help:
                new NormalDialog(this.mContext).showQRcodehelp();
                return;
            case C0291R.id.bt_no_hear:
                finish();
                return;
            case C0291R.id.img_back:
                finish();
                return;
            default:
                return;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        this.mHelper.StopListen();
        this.myhandler.removeCallbacks(this.mrunnable);
        this.lock.release();
    }

    public int getActivityInfo() {
        return 52;
    }
}
