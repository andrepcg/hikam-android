package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.global.Constants.Action;
import com.jwkj.utils.C0568T;
import com.jwkj.web.HKWebView;
import com.jwkj.widget.guide.IndexGuideActivity;
import java.util.ArrayList;
import java.util.List;

public class QRcodeActivity extends BaseActivity implements OnClickListener {
    BroadcastReceiver br = new C04281();
    Button bt_next;
    Button choose_wifi;
    EditText edit_pwd;
    ImageView img_back;
    boolean isRegFilter = false;
    private Context mContext;
    private int returnweb = 0;
    ImageView showpassword_img;
    boolean showpassword_state = false;
    String ssid;
    TextView tv_ssid;
    int type;

    class C04281 extends BroadcastReceiver {
        C04281() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Action.CURRENT_WIFI_NAME)) {
                QRcodeActivity.this.ssid = intent.getStringExtra("ssid");
                QRcodeActivity.this.type = intent.getIntExtra("type", 0);
                QRcodeActivity.this.tv_ssid.setText(QRcodeActivity.this.ssid);
                Log.e("ssid", QRcodeActivity.this.ssid);
            } else if (intent.getAction().equals(Action.SETTING_WIFI_SUCCESS)) {
                QRcodeActivity.this.finish();
            }
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Window win = getWindow();
        win.addFlags(4718592);
        win.addFlags(2097280);
        this.mContext = this;
        setContentView(C0291R.layout.activity_qr_code);
        this.returnweb = getIntent().getIntExtra("returnweb", 0);
        initComponent();
        currentWifi();
        regFilter();
    }

    public void initComponent() {
        this.tv_ssid = (TextView) findViewById(C0291R.id.tv_ssid);
        this.edit_pwd = (EditText) findViewById(C0291R.id.edit_pwd);
        this.img_back = (ImageView) findViewById(C0291R.id.img_back);
        this.bt_next = (Button) findViewById(C0291R.id.next);
        this.choose_wifi = (Button) findViewById(C0291R.id.choose_wifi);
        this.showpassword_img = (ImageView) findViewById(C0291R.id.showpassword_img);
        this.choose_wifi.setOnClickListener(this);
        this.bt_next.setOnClickListener(this);
        this.img_back.setOnClickListener(this);
        this.showpassword_img.setOnClickListener(this);
    }

    public void regFilter() {
        this.isRegFilter = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.CURRENT_WIFI_NAME);
        filter.addAction(Action.SETTING_WIFI_SUCCESS);
        this.mContext.registerReceiver(this.br, filter);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.choose_wifi:
                startActivity(new Intent(this.mContext, WifiListActivity.class));
                return;
            case C0291R.id.img_back:
                onBack();
                return;
            case C0291R.id.next:
                String wifiPwd = this.edit_pwd.getText().toString();
                if (this.ssid == null) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.please_choose_wireless);
                    return;
                } else if (this.ssid.equals("<unknown ssid>")) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.please_choose_wireless);
                    return;
                } else if (wifiPwd == null || (wifiPwd.length() <= 0 && (this.type == 1 || this.type == 2))) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.please_input_wifi_password);
                    return;
                } else {
                    Intent qr_code = new Intent(this.mContext, CreateQRcodeActivity.class);
                    qr_code.putExtra("ssidname", this.ssid);
                    qr_code.putExtra("wifiPwd", wifiPwd);
                    startActivity(qr_code);
                    return;
                }
            case C0291R.id.showpassword_img:
                changeShowPassword();
                return;
            default:
                return;
        }
    }

    public void onBack() {
        if (this.returnweb == 1) {
            startActivity(new Intent(this, HKWebView.class));
            finish();
            return;
        }
        startActivity(new Intent(this, IndexGuideActivity.class));
        finish();
    }

    public void onBackPressed() {
        onBack();
    }

    public void currentWifi() {
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService("wifi");
        if (manager.isWifiEnabled()) {
            this.ssid = manager.getConnectionInfo().getSSID();
            Log.e("ssid", this.ssid);
            List<ScanResult> datas = new ArrayList();
            WifiManager mWifiManager = (WifiManager) this.mContext.getSystemService("wifi");
            if (mWifiManager.isWifiEnabled()) {
                WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
                mWifiManager.startScan();
                datas = mWifiManager.getScanResults();
                if (this.ssid != null && !"".equals(this.ssid)) {
                    if (this.ssid.charAt(0) == 34) {
                        this.ssid = this.ssid.substring(1, this.ssid.length() - 1);
                    }
                    if (!(this.ssid.equals("<unknown ssid>") || this.ssid.equals("0x"))) {
                        this.tv_ssid.setText(this.ssid);
                        Log.e("ssid", this.ssid);
                    }
                    int i = 0;
                    while (i < datas.size()) {
                        ScanResult result = (ScanResult) datas.get(i);
                        if (!((ScanResult) datas.get(i)).SSID.equals(this.ssid)) {
                            i++;
                        } else if (result.capabilities.indexOf("WPA") > 0) {
                            this.type = 2;
                            return;
                        } else if (result.capabilities.indexOf("WEP") > 0) {
                            this.type = 1;
                            return;
                        } else {
                            this.type = 0;
                            return;
                        }
                    }
                }
            }
        }
    }

    public void changeShowPassword() {
        if (this.showpassword_state) {
            this.showpassword_img.setImageResource(C0291R.drawable.check_off);
            this.showpassword_state = false;
            this.edit_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            return;
        }
        this.showpassword_img.setImageResource(C0291R.drawable.check_on);
        this.showpassword_state = true;
        this.edit_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
    }

    protected void onDestroy() {
        super.onDestroy();
        this.isRegFilter = true;
        this.isRegFilter = false;
        this.mContext.unregisterReceiver(this.br);
    }

    public int getActivityInfo() {
        return 51;
    }
}
