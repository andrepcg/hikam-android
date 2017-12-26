package com.jwkj.widget.guide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.activity.BaseActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.entity.LocalDevice;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.FList;
import com.jwkj.utils.Utils;
import com.jwkj.web.HKWebView;
import com.jwkj.widget.TickView;
import com.jwkj.widget.TickView.OnTickListener;
import com.jwkj.widget.stepview.HorizontalStepView;
import com.jwkj.widget.stepview.StepBean;
import java.util.ArrayList;
import java.util.List;

public class ApModeGuideActivity extends BaseActivity implements OnClickListener {
    private ImageView img_back;
    private ImageView img_prepare;
    private boolean isRegFilter = false;
    private BroadcastReceiver mReceiver = new C06381();
    private int progress = 1;
    private int returnweb = 0;
    private RelativeLayout rl_bottom_tools;
    private RelativeLayout rl_goto_wifi;
    private RelativeLayout rl_search;
    private LinearLayout rl_step_five;
    private LinearLayout rl_step_four;
    private RelativeLayout rl_step_one;
    private RelativeLayout rl_step_three;
    private RelativeLayout rl_step_two;
    private Contact saveContact;
    private HorizontalStepView stepView;
    private TickView tickView;
    private TextView tv_a;
    private TextView tv_next;
    private TextView tv_s;
    private TextView tv_search;

    class C06381 extends BroadcastReceiver {
        C06381() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Action.LOCAL_DEVICE_SEARCH_END)) {
                List<LocalDevice> localDevices = FList.getInstance().getLocalDevices();
                if (localDevices.size() > 0) {
                    final LocalDevice device = (LocalDevice) localDevices.get(0);
                    ApModeGuideActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            ApModeGuideActivity.this.tv_search.setText(ApModeGuideActivity.this.getString(C0291R.string.ap_find_device) + Utils.showShortDevID(device.getContactId()));
                            ApModeGuideActivity.this.saveContact = new Contact();
                            ApModeGuideActivity.this.saveContact.contactId = device.getContactId();
                            ApModeGuideActivity.this.saveContact.contactModel = device.getContactModel();
                            ApModeGuideActivity.this.saveContact.contactPassword = "123";
                            ApModeGuideActivity.this.tickView.startTick();
                        }
                    });
                    return;
                }
                ApModeGuideActivity.this.tv_search.setText(C0291R.string.ap_not_find_device);
            }
        }
    }

    class C11352 implements OnTickListener {
        C11352() {
        }

        public void tickDone() {
            if (ApModeGuideActivity.this.isRegFilter) {
                ApModeGuideActivity.this.isRegFilter = false;
                ApModeGuideActivity.this.unregisterReceiver(ApModeGuideActivity.this.mReceiver);
            }
            Intent intent = new Intent(ApModeGuideActivity.this, GuideConnectActivity.class);
            intent.putExtra("isApMode", true);
            intent.putExtra(ContactDB.TABLE_NAME, ApModeGuideActivity.this.saveContact);
            ApModeGuideActivity.this.startActivity(intent);
            ApModeGuideActivity.this.finish();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_guide_apmode);
        this.returnweb = getIntent().getIntExtra("returnweb", 0);
        initComponent();
        getWifiList();
    }

    private void reg() {
        if (!this.isRegFilter) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Action.LOCAL_DEVICE_SEARCH_END);
            registerReceiver(this.mReceiver, filter);
            this.isRegFilter = true;
        }
    }

    public void unReq() {
        if (this.isRegFilter) {
            this.isRegFilter = false;
            unregisterReceiver(this.mReceiver);
        }
    }

    private void initComponent() {
        this.tickView = (TickView) findViewById(C0291R.id.tickview);
        this.tickView.setOnTickListener(new C11352());
        this.img_prepare = (ImageView) findViewById(C0291R.id.img_prepare);
        this.tv_a = (TextView) findViewById(C0291R.id.tv_a);
        this.tv_s = (TextView) findViewById(C0291R.id.tv_s);
        this.tv_a.setOnClickListener(this);
        this.tv_s.setOnClickListener(this);
        this.img_back = (ImageView) findViewById(C0291R.id.img_back);
        this.img_back.setOnClickListener(this);
        this.tv_next = (TextView) findViewById(C0291R.id.tv_next);
        this.tv_search = (TextView) findViewById(C0291R.id.tv_search);
        this.rl_step_one = (RelativeLayout) findViewById(C0291R.id.rl_step_one);
        this.rl_step_two = (RelativeLayout) findViewById(C0291R.id.rl_step_two);
        this.rl_step_three = (RelativeLayout) findViewById(C0291R.id.rl_step_three);
        this.rl_search = (RelativeLayout) findViewById(C0291R.id.rl_search);
        this.rl_step_four = (LinearLayout) findViewById(C0291R.id.rl_step_four);
        this.rl_step_five = (LinearLayout) findViewById(C0291R.id.rl_step_five);
        this.rl_bottom_tools = (RelativeLayout) findViewById(C0291R.id.rl_bottom_tools);
        this.rl_bottom_tools.setOnClickListener(this);
        this.rl_goto_wifi = (RelativeLayout) findViewById(C0291R.id.rl_goto_wifi);
        this.rl_goto_wifi.setOnClickListener(this);
        this.stepView = (HorizontalStepView) findViewById(C0291R.id.step_view);
        List<StepBean> stepList = new ArrayList();
        StepBean stepBean0 = new StepBean("介绍", -1);
        StepBean stepBean1 = new StepBean("相机设置", -1);
        StepBean stepBean2 = new StepBean("手机设置", -1);
        StepBean stepBean3 = new StepBean("链接结果", -1);
        StepBean stepBean5 = new StepBean("密码设置", -1);
        StepBean stepBean6 = new StepBean("wifi链接", -1);
        StepBean stepBean8 = new StepBean("完成", -1);
        stepList.add(stepBean0);
        stepList.add(stepBean1);
        stepList.add(stepBean2);
        stepList.add(stepBean3);
        this.stepView.setStepViewTexts(stepList).setTextSize(12);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.img_back:
                showPre();
                return;
            case C0291R.id.rl_bottom_tools:
                if (this.progress == 4) {
                    showPre();
                    return;
                } else {
                    showNext();
                    return;
                }
            case C0291R.id.rl_goto_wifi:
                startActivity(new Intent("android.settings.WIFI_SETTINGS"));
                return;
            case C0291R.id.tv_a:
                this.tv_a.setBackgroundColor(getResources().getColor(C0291R.color.colorPrimary));
                this.tv_a.setTextColor(getResources().getColor(17170443));
                this.tv_s.setBackgroundColor(getResources().getColor(17170445));
                this.tv_s.setTextColor(getResources().getColor(17170444));
                this.img_prepare.setVisibility(8);
                this.img_prepare.setImageResource(C0291R.drawable.reset_copy);
                this.img_prepare.setVisibility(0);
                return;
            case C0291R.id.tv_s:
                this.tv_s.setBackgroundColor(getResources().getColor(C0291R.color.colorPrimary));
                this.tv_s.setTextColor(getResources().getColor(17170443));
                this.tv_a.setBackgroundColor(getResources().getColor(17170445));
                this.tv_a.setTextColor(getResources().getColor(17170444));
                this.img_prepare.setVisibility(8);
                this.img_prepare.setImageResource(C0291R.drawable.ap_reset);
                this.img_prepare.setVisibility(0);
                return;
            default:
                return;
        }
    }

    public void onBackPressed() {
        showPre();
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
            case 5:
                showStepFive();
                return;
            default:
                return;
        }
    }

    public void showPre() {
        int wanna;
        if (this.progress == 5) {
            wanna = this.progress - 2;
        } else {
            wanna = this.progress - 1;
        }
        if (wanna >= 1) {
            show(wanna);
            this.progress = wanna;
        } else if (this.returnweb == 1) {
            startActivity(new Intent(this, HKWebView.class));
            finish();
        } else {
            startActivity(new Intent(this, IndexGuideActivity.class));
            finish();
        }
    }

    public void showNext() {
        if (this.progress == 3 && isHikamWifi()) {
            this.progress++;
        }
        int wanna = this.progress + 1;
        if (wanna <= 5) {
            this.progress = wanna;
            show(this.progress);
        }
    }

    public void showStepOne() {
        this.progress = 1;
        dismiss();
        unReq();
        this.rl_step_one.setVisibility(0);
        this.rl_bottom_tools.setVisibility(0);
        this.tv_next.setText(C0291R.string.continues);
    }

    public void showStepTwo() {
        this.progress = 2;
        dismiss();
        unReq();
        this.rl_step_two.setVisibility(0);
        this.rl_bottom_tools.setVisibility(0);
        this.tv_next.setText(C0291R.string.continues);
    }

    public void showStepThree() {
        this.progress = 3;
        dismiss();
        unReq();
        this.rl_step_three.setVisibility(0);
        this.rl_bottom_tools.setVisibility(0);
        this.tv_next.setText(C0291R.string.continues);
    }

    public void showStepFour() {
        this.progress = 4;
        dismiss();
        unReq();
        this.rl_step_four.setVisibility(0);
        this.rl_bottom_tools.setVisibility(0);
        this.tv_next.setText(C0291R.string.back);
    }

    public void showStepFive() {
        this.progress = 5;
        dismiss();
        searchCamera();
        reg();
        this.rl_step_five.setVisibility(0);
        this.rl_bottom_tools.setVisibility(8);
    }

    private void searchCamera() {
        FList.getInstance().searchLocalDevice();
    }

    public void dismiss() {
        this.rl_step_one.setVisibility(8);
        this.rl_step_two.setVisibility(8);
        this.rl_step_three.setVisibility(8);
        this.rl_step_four.setVisibility(8);
        this.rl_step_five.setVisibility(8);
    }

    public void getWifiList() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService("wifi");
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        List<ScanResult> listResult = wifiManager.getScanResults();
        if (listResult != null && listResult.size() > 0) {
            for (ScanResult item : listResult) {
                Log.e("f", "wifi msg :" + item.SSID);
            }
        }
    }

    public boolean isHikamWifi() {
        String wifiName = ((WifiManager) getApplicationContext().getSystemService("wifi")).getConnectionInfo().getSSID().trim().toString();
        Log.e("ffew", "wifi wifi name :" + wifiName);
        if (wifiName.substring(1).startsWith("Hikam") || wifiName.substring(1).startsWith("GW_AP") || wifiName.substring(1).startsWith("Hi")) {
            Log.e("few", "wifi true");
            return true;
        }
        Log.e("few", "wifi false");
        return false;
    }

    protected void onDestroy() {
        super.onDestroy();
        unReq();
    }

    public int getActivityInfo() {
        return 1024;
    }
}
