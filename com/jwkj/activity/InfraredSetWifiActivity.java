package com.jwkj.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.utils.C0568T;

public class InfraredSetWifiActivity extends BaseActivity implements OnClickListener {
    TextView button1_text;
    TextView button2_text;
    EditText input1;
    EditText input2;
    RelativeLayout layout_edit1;
    RelativeLayout layout_edit2;
    Context mContext;
    String ssid;
    TextView text_ssid;
    int wifiType;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_infrared_set_wifi);
        this.mContext = this;
        this.wifiType = getIntent().getIntExtra("type", 0);
        this.ssid = getIntent().getStringExtra("ssid");
        initComponent();
    }

    public void initComponent() {
        this.layout_edit1 = (RelativeLayout) findViewById(C0291R.id.layout_edit1);
        this.layout_edit2 = (RelativeLayout) findViewById(C0291R.id.layout_edit2);
        this.input1 = (EditText) findViewById(C0291R.id.input1);
        this.input2 = (EditText) findViewById(C0291R.id.input2);
        this.button1_text = (TextView) findViewById(C0291R.id.button1_text);
        this.button2_text = (TextView) findViewById(C0291R.id.button2_text);
        this.text_ssid = (TextView) findViewById(C0291R.id.text_ssid);
        this.text_ssid.setText(this.ssid);
        if (this.wifiType == 1 || this.wifiType == 2) {
            this.layout_edit1.setVisibility(0);
        } else {
            this.layout_edit1.setVisibility(8);
        }
        this.button1_text.setOnClickListener(this);
        this.button2_text.setOnClickListener(this);
    }

    public int getActivityInfo() {
        return 43;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.button1_text:
                if (!((AudioManager) getSystemService("audio")).isWiredHeadsetOn()) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.no_insert_utils);
                }
                String wifiPwd = this.input1.getText().toString();
                String devicePwd = this.input2.getText().toString();
                if (wifiPwd == null || (wifiPwd.length() <= 0 && (this.wifiType == 1 || this.wifiType == 2))) {
                    C0568T.showShort(this.mContext, (int) C0291R.string.input_wifi_pwd);
                    return;
                }
                int iDevPassword;
                if (devicePwd == null || devicePwd.length() <= 0) {
                    iDevPassword = 0;
                } else {
                    try {
                        iDevPassword = Integer.parseInt(devicePwd);
                    } catch (Exception e) {
                        C0568T.showShort(this.mContext, (int) C0291R.string.device_pwd_must_be);
                        return;
                    }
                }
                Intent intent = new Intent();
                intent.setAction("setWifi");
                intent.putExtra("wifiPwd", wifiPwd);
                intent.putExtra("devPwd", iDevPassword);
                this.mContext.sendBroadcast(intent);
                return;
            case C0291R.id.button2_text:
                setResult(1);
                finish();
                return;
            default:
                return;
        }
    }

    public void onBackPressed() {
        Log.e("my", "onBackPressed");
        setResult(1);
        finish();
    }
}
