package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.entity.LocalDevice;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.FList;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonCancelListener;
import java.util.ArrayList;
import java.util.List;

public class RadarAddActivity extends BaseActivity implements OnClickListener {
    private byte AuthModeAutoSwitch = (byte) 2;
    private byte AuthModeOpen = (byte) 0;
    private byte AuthModeShared = (byte) 1;
    private byte AuthModeWPA = (byte) 3;
    private byte AuthModeWPA1PSKWPA2PSK = (byte) 9;
    private byte AuthModeWPA1WPA2 = (byte) 8;
    private byte AuthModeWPA2 = (byte) 6;
    private byte AuthModeWPA2PSK = (byte) 7;
    private byte AuthModeWPANone = (byte) 5;
    private byte AuthModeWPAPSK = (byte) 4;
    ImageView back_btn;
    boolean bool1;
    boolean bool2;
    boolean bool3;
    boolean bool4;
    BroadcastReceiver br = new C04302();
    Button bt_next;
    EditText edit_pwd;
    boolean isRegFilter = false;
    private boolean isWifiOpen = false;
    private RelativeLayout local_device_bar_top;
    private byte mAuthMode;
    private Context mContext;
    private int mFrequency = 0;
    int mLocalIp;
    private RelativeLayout rlPwd;
    ImageView showpassword_img;
    boolean showpassword_state = false;
    String ssid;
    private TextView text_local_device_count;
    TextView tv_ssid;
    int type;

    class C04291 implements OnClickListener {
        C04291() {
        }

        public void onClick(View arg0) {
            RadarAddActivity.this.mContext.startActivity(new Intent(RadarAddActivity.this.mContext, LocalDeviceListActivity.class));
        }
    }

    class C04302 extends BroadcastReceiver {

        class C10881 implements OnButtonCancelListener {
            C10881() {
            }

            public void onClick() {
                RadarAddActivity.this.finish();
            }
        }

        C04302() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Action.ADD_CONTACT_SUCCESS)) {
                RadarAddActivity.this.finish();
            } else if (intent.getAction().equals(Action.RADAR_SET_WIFI_FAILED)) {
                NormalDialog dialog = new NormalDialog(RadarAddActivity.this.mContext);
                dialog.setOnButtonCancelListener(new C10881());
                dialog.showAirlinkFail();
            } else if (intent.getAction().equals(Action.RADAR_SET_WIFI_SUCCESS)) {
                RadarAddActivity.this.finish();
            }
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Window win = getWindow();
        win.addFlags(4718592);
        win.addFlags(2097280);
        this.mContext = this;
        setContentView(C0291R.layout.activity_radar_add);
        initComponent();
        regFilter();
        currentWifi();
    }

    public void initComponent() {
        this.tv_ssid = (TextView) findViewById(C0291R.id.tv_ssid);
        this.edit_pwd = (EditText) findViewById(C0291R.id.edit_pwd);
        this.edit_pwd.setTypeface(Typeface.DEFAULT);
        this.edit_pwd.setTransformationMethod(new PasswordTransformationMethod());
        this.back_btn = (ImageView) findViewById(C0291R.id.back_btn);
        this.bt_next = (Button) findViewById(C0291R.id.next);
        this.rlPwd = (RelativeLayout) findViewById(C0291R.id.layout_pwd);
        this.showpassword_img = (ImageView) findViewById(C0291R.id.showpassword_img);
        this.local_device_bar_top = (RelativeLayout) findViewById(C0291R.id.local_device_bar_top);
        this.text_local_device_count = (TextView) findViewById(C0291R.id.text_local_device_count);
        this.bt_next.setOnClickListener(this);
        this.back_btn.setOnClickListener(this);
        this.showpassword_img.setOnClickListener(this);
        this.local_device_bar_top.setOnClickListener(new C04291());
        List<LocalDevice> localDevices = FList.getInstance().getSetPasswordLocalDevices();
        if (localDevices.size() > 0) {
            this.local_device_bar_top.setVisibility(0);
            this.text_local_device_count.setText("" + localDevices.size());
            return;
        }
        this.local_device_bar_top.setVisibility(8);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.ADD_CONTACT_SUCCESS);
        registerReceiver(this.br, filter);
        this.isRegFilter = true;
    }

    public void currentWifi() {
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService("wifi");
        if (manager.isWifiEnabled()) {
            WifiInfo info = manager.getConnectionInfo();
            this.ssid = info.getSSID();
            this.mLocalIp = info.getIpAddress();
            List<ScanResult> datas = new ArrayList();
            if (manager.isWifiEnabled()) {
                manager.startScan();
                datas = manager.getScanResults();
                if (this.ssid != null && !this.ssid.equals("")) {
                    if (this.ssid.charAt(0) == 34) {
                        this.ssid = this.ssid.substring(1, this.ssid.length() - 1);
                    }
                    if (!(this.ssid.equals("<unknown ssid>") || this.ssid.equals("0x"))) {
                        this.tv_ssid.setText(this.ssid);
                    }
                    for (int i = 0; i < datas.size(); i++) {
                        ScanResult result = (ScanResult) datas.get(i);
                        if (result.SSID.equals(this.ssid)) {
                            if (Utils.isWifiOpen(result)) {
                                this.type = 0;
                                this.isWifiOpen = true;
                                this.rlPwd.setVisibility(8);
                            } else {
                                this.type = 1;
                                this.isWifiOpen = false;
                                this.rlPwd.setVisibility(0);
                            }
                            this.mFrequency = result.frequency;
                            Log.e("Alex debug info", "mFrequency = " + this.mFrequency);
                            this.bool1 = result.capabilities.contains("WPA-PSK");
                            this.bool2 = result.capabilities.contains("WPA2-PSK");
                            this.bool3 = result.capabilities.contains("WPA-EAP");
                            this.bool4 = result.capabilities.contains("WPA2-EAP");
                            if (result.capabilities.contains("WEP")) {
                                this.mAuthMode = this.AuthModeOpen;
                            }
                            if (this.bool1 && this.bool2) {
                                this.mAuthMode = this.AuthModeWPA1PSKWPA2PSK;
                            } else if (this.bool2) {
                                this.mAuthMode = this.AuthModeWPA2PSK;
                            } else if (this.bool1) {
                                this.mAuthMode = this.AuthModeWPAPSK;
                            } else if (this.bool3 && this.bool4) {
                                this.mAuthMode = this.AuthModeWPA1WPA2;
                            } else if (this.bool4) {
                                this.mAuthMode = this.AuthModeWPA2;
                            } else if (this.bool3) {
                                this.mAuthMode = this.AuthModeWPA;
                            } else {
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public int getActivityInfo() {
        return 56;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.next:
                Context context = this.mContext;
                InputMethodManager manager = (InputMethodManager) getSystemService("input_method");
                if (manager != null) {
                    manager.hideSoftInputFromWindow(this.edit_pwd.getWindowToken(), 0);
                }
                String wifiPwd = this.edit_pwd.getText().toString().trim();
                if (this.ssid == null || this.ssid.equals("")) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.please_choose_wireless);
                    return;
                } else if (this.ssid.equals("<unknown ssid>")) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.please_choose_wireless);
                    return;
                } else if (this.isWifiOpen || (wifiPwd != null && (wifiPwd.length() > 0 || !(this.type == 1 || this.type == 2)))) {
                    if (this.mFrequency < 2412 && this.mFrequency > 2484) {
                        NormalDialog dialog = new NormalDialog(this.mContext);
                        this.mContext.getResources().getString(C0291R.string.airlink_wifi_error);
                    }
                    Intent device_network = new Intent(this.mContext, AddWaitActicity.class);
                    device_network.putExtra("ssidname", this.ssid);
                    device_network.putExtra("wifiPwd", wifiPwd);
                    device_network.putExtra("type", this.mAuthMode);
                    device_network.putExtra("LocalIp", this.mLocalIp);
                    device_network.putExtra("isNeedSendWifi", true);
                    startActivity(device_network);
                    finish();
                    return;
                } else {
                    C0568T.showShort(this.mContext, (int) C0291R.string.please_input_wifi_password);
                    return;
                }
            case C0291R.id.showpassword_img:
                changeShowPassword();
                return;
            default:
                return;
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
        if (this.isRegFilter) {
            unregisterReceiver(this.br);
            this.isRegFilter = false;
        }
    }
}
