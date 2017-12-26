package com.jwkj.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.adapter.InfraredWifiAdapter;
import com.jwkj.global.Constants.Action;

public class WifiListActivity extends BaseActivity implements OnClickListener {
    InfraredWifiAdapter adapter;
    ImageView img_back;
    ListView lv_wifi_list;
    private Context mContext;
    String ssid;
    TextView tv_no_wifi;
    int type;

    class C04621 implements OnItemClickListener {
        C04621() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
            ScanResult result = (ScanResult) WifiListActivity.this.adapter.getItem(arg2);
            WifiListActivity.this.ssid = result.SSID;
            if (result.capabilities.indexOf("WPA") > 0) {
                WifiListActivity.this.type = 2;
            } else if (result.capabilities.indexOf("WEP") > 0) {
                WifiListActivity.this.type = 1;
            } else {
                WifiListActivity.this.type = 0;
            }
            Intent it = new Intent();
            it.setAction(Action.CURRENT_WIFI_NAME);
            it.putExtra("ssid", WifiListActivity.this.ssid);
            it.putExtra("type", WifiListActivity.this.type);
            WifiListActivity.this.mContext.sendBroadcast(it);
            WifiListActivity.this.finish();
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        this.mContext = this;
        setContentView(C0291R.layout.activity_wifi_list);
        initComponent();
    }

    protected void onResume() {
        super.onResume();
        this.adapter.freshWIFIList();
    }

    public void initComponent() {
        this.tv_no_wifi = (TextView) findViewById(C0291R.id.tv_no_wifi);
        this.tv_no_wifi.setOnClickListener(this);
        this.img_back = (ImageView) findViewById(C0291R.id.img_back);
        this.img_back.setOnClickListener(this);
        this.lv_wifi_list = (ListView) findViewById(C0291R.id.lv_wifi_list);
        this.adapter = new InfraredWifiAdapter(this, this.tv_no_wifi);
        this.lv_wifi_list.setAdapter(this.adapter);
        this.lv_wifi_list.setOnItemClickListener(new C04621());
    }

    public int getActivityInfo() {
        return 50;
    }

    public void onClick(View v) {
        if (v.getId() == C0291R.id.img_back) {
            finish();
        } else if (v.getId() == C0291R.id.tv_no_wifi) {
            startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
        }
    }
}
