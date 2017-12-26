package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.entity.LocalDevice;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.FList;
import java.util.List;

public class ThreeAddContactActivity extends BaseActivity implements OnClickListener {
    BroadcastReceiver br = new C04572();
    ImageView img_back;
    boolean isRegFilter = false;
    RelativeLayout lan_search_btn;
    private RelativeLayout local_device_bar_top;
    private Context mContext;
    RelativeLayout manually_add_btn;
    RelativeLayout qr_code_add_btn;
    RelativeLayout radar_add_btn;
    private TextView text_local_device_count;

    class C04561 implements OnClickListener {
        C04561() {
        }

        public void onClick(View arg0) {
            ThreeAddContactActivity.this.mContext.startActivity(new Intent(ThreeAddContactActivity.this.mContext, LocalDeviceListActivity.class));
        }
    }

    class C04572 extends BroadcastReceiver {
        C04572() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Action.ADD_CONTACT_SUCCESS)) {
                ThreeAddContactActivity.this.finish();
                Log.e("send", "send1");
            } else if (intent.getAction().equals(Action.SETTING_WIFI_SUCCESS)) {
                ThreeAddContactActivity.this.finish();
            } else if (intent.getAction().equals(Action.REFRESH_CONTANTS)) {
                localDevices = FList.getInstance().getSetPasswordLocalDevices();
                if (localDevices.size() > 0) {
                    ThreeAddContactActivity.this.local_device_bar_top.setVisibility(0);
                    ThreeAddContactActivity.this.text_local_device_count.setText("" + localDevices.size());
                    return;
                }
                ThreeAddContactActivity.this.local_device_bar_top.setVisibility(8);
            } else if (intent.getAction().equals(Action.LOCAL_DEVICE_SEARCH_END)) {
                localDevices = FList.getInstance().getSetPasswordLocalDevices();
                if (localDevices.size() > 0) {
                    ThreeAddContactActivity.this.local_device_bar_top.setVisibility(0);
                    ThreeAddContactActivity.this.text_local_device_count.setText("" + localDevices.size());
                    return;
                }
                ThreeAddContactActivity.this.local_device_bar_top.setVisibility(8);
            }
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(C0291R.layout.activity_three_add_contact);
        this.mContext = this;
        initComponent();
        regFilter();
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        Log.e("DPI", " " + outMetrics.densityDpi);
    }

    public void initComponent() {
        this.img_back = (ImageView) findViewById(C0291R.id.img_back);
        this.lan_search_btn = (RelativeLayout) findViewById(C0291R.id.lan_search_btn);
        this.manually_add_btn = (RelativeLayout) findViewById(C0291R.id.manually_add_btn);
        this.qr_code_add_btn = (RelativeLayout) findViewById(C0291R.id.qr_code_add_btn);
        this.radar_add_btn = (RelativeLayout) findViewById(C0291R.id.radar_add_btn);
        this.local_device_bar_top = (RelativeLayout) findViewById(C0291R.id.local_device_bar_top);
        this.text_local_device_count = (TextView) findViewById(C0291R.id.text_local_device_count);
        this.img_back.setOnClickListener(this);
        this.lan_search_btn.setOnClickListener(this);
        this.manually_add_btn.setOnClickListener(this);
        this.qr_code_add_btn.setOnClickListener(this);
        this.radar_add_btn.setOnClickListener(this);
        this.local_device_bar_top.setOnClickListener(new C04561());
        List<LocalDevice> localDevices = FList.getInstance().getSetPasswordLocalDevices();
        if (localDevices.size() > 0) {
            this.local_device_bar_top.setVisibility(0);
            this.text_local_device_count.setText("" + localDevices.size());
            return;
        }
        this.local_device_bar_top.setVisibility(8);
    }

    public void regFilter() {
        this.isRegFilter = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.ADD_CONTACT_SUCCESS);
        filter.addAction(Action.SETTING_WIFI_SUCCESS);
        filter.addAction(Action.REFRESH_CONTANTS);
        filter.addAction(Action.LOCAL_DEVICE_SEARCH_END);
        this.mContext.registerReceiver(this.br, filter);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.img_back:
                finish();
                return;
            case C0291R.id.lan_search_btn:
                this.mContext.startActivity(new Intent(this.mContext, LocalDeviceListActivity.class));
                return;
            case C0291R.id.manually_add_btn:
                this.mContext.startActivity(new Intent(this.mContext, AddContactActivity.class));
                return;
            case C0291R.id.qr_code_add_btn:
                this.mContext.startActivity(new Intent(this.mContext, QRcodeActivity.class));
                return;
            case C0291R.id.radar_add_btn:
                this.mContext.startActivity(new Intent(this.mContext, RadarAddFirstActivity.class));
                return;
            default:
                return;
        }
    }

    public int getActivityInfo() {
        return 48;
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.isRegFilter = false;
            unregisterReceiver(this.br);
        }
    }
}
