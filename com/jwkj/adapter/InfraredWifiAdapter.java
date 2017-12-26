package com.jwkj.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hikam.C0291R;
import java.util.ArrayList;
import java.util.List;
import org.jboss.netty.handler.traffic.AbstractTrafficShapingHandler;

@SuppressLint({"ResourceAsColor"})
public class InfraredWifiAdapter extends BaseAdapter {
    private List<ScanResult> datas = new ArrayList();
    private boolean isConnected;
    private boolean isScan;
    private InfraredWifiAdapter mAdapter;
    private Context mContext;
    private MyHandler mHandler;
    private TextView mPromptView;
    private WifiInfo mWifiInfo;
    private WifiManager mWifiManager;

    public class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            InfraredWifiAdapter.this.mAdapter.notifyDataSetChanged();
        }
    }

    public class WifiThread extends Thread {
        public void run() {
            while (InfraredWifiAdapter.this.isScan) {
                if (InfraredWifiAdapter.this.mWifiManager.isWifiEnabled()) {
                    InfraredWifiAdapter.this.mWifiInfo = InfraredWifiAdapter.this.mWifiManager.getConnectionInfo();
                    InfraredWifiAdapter.this.mWifiManager.startScan();
                    InfraredWifiAdapter.this.datas = InfraredWifiAdapter.this.mWifiManager.getScanResults();
                    InfraredWifiAdapter.this.mHandler.sendEmptyMessage(0);
                    try {
                        Thread.sleep(AbstractTrafficShapingHandler.DEFAULT_MAX_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        InfraredWifiAdapter.this.mHandler.sendEmptyMessage(0);
                        Thread.sleep(1000);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
    }

    public InfraredWifiAdapter(Context context, TextView promptView) {
        this.mContext = context;
        this.mPromptView = promptView;
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mHandler = new MyHandler();
        this.isScan = true;
        this.mAdapter = this;
        new WifiThread().start();
    }

    public int getCount() {
        if (!this.mWifiManager.isWifiEnabled()) {
            this.mPromptView.setVisibility(0);
            return 0;
        } else if (this.datas == null || this.datas.size() == 0) {
            this.mPromptView.setVisibility(0);
            return 0;
        } else {
            this.mPromptView.setVisibility(8);
            return this.datas.size();
        }
    }

    public Object getItem(int arg0) {
        return this.datas.get(arg0);
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int position, View arg1, ViewGroup arg2) {
        int type;
        View view = arg1;
        if (view == null) {
            view = LayoutInflater.from(this.mContext).inflate(C0291R.layout.list_infrared_wifi_item, null);
        }
        TextView name = (TextView) view.findViewById(C0291R.id.text_name);
        ImageView wifi_type = (ImageView) view.findViewById(C0291R.id.wifi_type);
        ScanResult result = (ScanResult) this.datas.get(position);
        if (result.capabilities.indexOf("WPA") > 0) {
            type = 2;
        } else if (result.capabilities.indexOf("WEP") > 0) {
            type = 1;
        } else {
            type = 0;
        }
        if (type == 0) {
            wifi_type.setVisibility(8);
        } else {
            wifi_type.setVisibility(0);
        }
        name.setText(result.SSID);
        return view;
    }

    public void freshWIFIList() {
        new WifiThread().start();
    }

    public void stopScan() {
        this.isScan = false;
    }
}
