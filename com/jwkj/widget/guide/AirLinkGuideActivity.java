package com.jwkj.widget.guide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.activity.AddWaitActicity;
import com.jwkj.activity.BaseActivity;
import com.jwkj.global.Constants.Action;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonCancelListener;
import com.jwkj.widget.stepview.HorizontalStepView;
import com.jwkj.widget.stepview.StepBean;
import java.util.ArrayList;
import java.util.List;

public class AirLinkGuideActivity extends BaseActivity implements OnClickListener {
    public static AirLinkGuideActivity instance = null;
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
    boolean bool1;
    boolean bool2;
    boolean bool3;
    boolean bool4;
    BroadcastReceiver br = new C06351();
    private CheckBox cb_pwd;
    private Context context;
    private EditText et_pwd;
    private ImageView img_back;
    private boolean isRegFilter = false;
    private boolean isWifiOpen = false;
    private byte mAuthMode;
    private int mFrequency = 0;
    int mLocalIp;
    private int progress = 1;
    private RelativeLayout rl_bottom_tools;
    private RelativeLayout rl_pwd;
    private LinearLayout rl_step_four;
    private RelativeLayout rl_step_one;
    private RelativeLayout rl_step_three;
    private RelativeLayout rl_step_two;
    boolean showpassword_state = false;
    String ssid;
    private HorizontalStepView stepView;
    private TextView tv;
    private TextView tv_next;
    TextView tv_ssid;
    private TextView tv_step4_intro;
    int type;

    class C06351 extends BroadcastReceiver {

        class C11341 implements OnButtonCancelListener {
            C11341() {
            }

            public void onClick() {
                AirLinkGuideActivity.this.finish();
            }
        }

        C06351() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Action.RADAR_SET_WIFI_FAILED)) {
                AirLinkGuideActivity.this.show(1);
                NormalDialog dialog = new NormalDialog(AirLinkGuideActivity.this.context);
                dialog.setOnButtonCancelListener(new C11341());
                dialog.showAirlinkFail();
            }
        }
    }

    class C06362 implements OnCheckedChangeListener {
        C06362() {
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                AirLinkGuideActivity.this.et_pwd.setInputType(145);
                return;
            }
            AirLinkGuideActivity.this.et_pwd.setInputType(128);
            AirLinkGuideActivity.this.et_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(C0291R.layout.activity_guide_airlink);
        initComponent();
        this.context = this;
    }

    protected void onResume() {
        super.onResume();
        reqFilt();
    }

    private void reqFilt() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.RADAR_SET_WIFI_FAILED);
        registerReceiver(this.br, filter);
        this.isRegFilter = true;
    }

    private void initComponent() {
        this.tv_ssid = (TextView) findViewById(C0291R.id.tv_ssid);
        this.et_pwd = (EditText) findViewById(C0291R.id.et_pwd);
        this.rl_pwd = (RelativeLayout) findViewById(C0291R.id.layout_pwd);
        this.rl_step_one = (RelativeLayout) findViewById(C0291R.id.rl_step_one);
        this.rl_step_two = (RelativeLayout) findViewById(C0291R.id.rl_step_two);
        this.rl_step_three = (RelativeLayout) findViewById(C0291R.id.rl_step_three);
        this.rl_step_four = (LinearLayout) findViewById(C0291R.id.rl_step_four);
        this.rl_bottom_tools = (RelativeLayout) findViewById(C0291R.id.rl_bottom_tools);
        this.rl_bottom_tools.setOnClickListener(this);
        this.tv_next = (TextView) findViewById(C0291R.id.tv_next);
        this.tv_step4_intro = (TextView) findViewById(C0291R.id.tv_step4_intro);
        this.tv_step4_intro.setText(Html.fromHtml(getResources().getString(C0291R.string.guide_airlink_step4_intro)));
        this.tv_step4_intro.setOnClickListener(this);
        this.img_back = (ImageView) findViewById(C0291R.id.img_back);
        this.img_back.setOnClickListener(this);
        this.stepView = (HorizontalStepView) findViewById(C0291R.id.step_view);
        List<StepBean> stepList = new ArrayList();
        StepBean stepBean0 = new StepBean("介绍", -1);
        StepBean stepBean1 = new StepBean("准备", -1);
        stepList.add(stepBean0);
        stepList.add(stepBean1);
        this.stepView.setStepViewTexts(stepList).setTextSize(12);
        this.cb_pwd = (CheckBox) findViewById(C0291R.id.cb_pwd);
        this.cb_pwd.setOnCheckedChangeListener(new C06362());
    }

    public void onBackPressed() {
        showPre();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.img_back:
                showPre();
                return;
            case C0291R.id.rl_bottom_tools:
                showNext();
                return;
            case C0291R.id.tv_step4_intro:
                this.ssid = "";
                this.tv_ssid.setText(getResources().getString(C0291R.string.please_choose_wireless));
                show(1);
                return;
            default:
                return;
        }
    }

    public int getActivityInfo() {
        return 105;
    }

    public void show(int progress) {
        this.stepView.nextStep(progress - 1);
        switch (progress) {
            case 1:
                showStepOne();
                return;
            case 2:
                showStepTwo();
                return;
            case 3:
                showStepThree();
                return;
            case 4:
                showStepFour();
                return;
            default:
                return;
        }
    }

    public void showPre() {
        int wanna = this.progress - 1;
        if (wanna >= 1) {
            show(wanna);
            this.progress = wanna;
            return;
        }
        startActivity(new Intent(this, IndexGuideActivity.class));
        finish();
    }

    public void showNext() {
        int wanna = this.progress + 1;
        if (wanna == 5) {
            String wifiPwd = this.et_pwd.getText().toString().trim();
            if (this.ssid == null || this.ssid.equals("")) {
                C0568T.showShort((Context) this, (int) C0291R.string.please_choose_wireless);
                return;
            } else if (this.ssid.equals("<unknown ssid>")) {
                C0568T.showShort((Context) this, (int) C0291R.string.please_choose_wireless);
                return;
            } else if (this.isWifiOpen || (wifiPwd != null && (wifiPwd.length() > 0 || !(this.type == 1 || this.type == 2)))) {
                if (this.mFrequency < 2412 && this.mFrequency > 2484) {
                    NormalDialog dialog = new NormalDialog(this);
                    getResources().getString(C0291R.string.airlink_wifi_error);
                }
                Intent device_network = new Intent(this, AddWaitActicity.class);
                device_network.putExtra("ssidname", this.ssid);
                device_network.putExtra("wifiPwd", wifiPwd);
                device_network.putExtra("type", this.mAuthMode);
                device_network.putExtra("LocalIp", this.mLocalIp);
                device_network.putExtra("isNeedSendWifi", true);
                startActivity(device_network);
                return;
            } else {
                C0568T.showShort((Context) this, (int) C0291R.string.please_input_wifi_password);
                return;
            }
        }
        this.progress = wanna;
        show(this.progress);
    }

    public void showStepOne() {
        this.progress = 1;
        dismiss();
        this.rl_step_one.setVisibility(0);
        this.tv_next.setText(C0291R.string.continues);
    }

    public void showStepTwo() {
        this.progress = 2;
        dismiss();
        this.rl_step_two.setVisibility(0);
        this.tv_next.setText(C0291R.string.continues);
    }

    public void showStepThree() {
        this.progress = 3;
        dismiss();
        this.rl_step_three.setVisibility(0);
        this.tv_next.setText(C0291R.string.continues);
    }

    public void showStepFour() {
        this.progress = 4;
        currentWifi();
        dismiss();
        this.rl_step_four.setVisibility(0);
        this.tv_next.setText(C0291R.string.start_airlink);
    }

    public void dismiss() {
        this.rl_step_one.setVisibility(8);
        this.rl_step_two.setVisibility(8);
        this.rl_step_three.setVisibility(8);
        this.rl_step_four.setVisibility(8);
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
                                this.rl_pwd.setVisibility(8);
                            } else {
                                this.type = 1;
                                this.isWifiOpen = false;
                                this.rl_pwd.setVisibility(0);
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

    protected void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            unregisterReceiver(this.br);
            this.isRegFilter = false;
        }
    }
}
