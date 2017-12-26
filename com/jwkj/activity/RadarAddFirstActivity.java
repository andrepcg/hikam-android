package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.entity.LocalDevice;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.FList;
import com.jwkj.widget.NormalDialog;
import java.util.List;

public class RadarAddFirstActivity extends BaseActivity {
    ImageView back;
    BroadcastReceiver br = new C04344();
    boolean isRegFilter = false;
    private RelativeLayout local_device_bar_top;
    private Context mContext;
    Button next;
    private TextView text_local_device_count;

    class C04311 implements OnClickListener {
        C04311() {
        }

        public void onClick(View arg0) {
            Intent it = new Intent();
            it.setClass(RadarAddFirstActivity.this.mContext, RadarAddActivity.class);
            RadarAddFirstActivity.this.startActivity(it);
        }
    }

    class C04322 implements OnClickListener {
        C04322() {
        }

        public void onClick(View arg0) {
            RadarAddFirstActivity.this.finish();
        }
    }

    class C04333 implements OnClickListener {
        C04333() {
        }

        public void onClick(View arg0) {
            RadarAddFirstActivity.this.mContext.startActivity(new Intent(RadarAddFirstActivity.this.mContext, LocalDeviceListActivity.class));
        }
    }

    class C04344 extends BroadcastReceiver {
        C04344() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Action.ADD_CONTACT_SUCCESS)) {
                RadarAddFirstActivity.this.finish();
            } else if (intent.getAction().equals(Action.RADAR_SET_WIFI_FAILED)) {
                new NormalDialog(RadarAddFirstActivity.this.mContext).showAirlinkFail();
            } else if (intent.getAction().equals(Action.RADAR_SET_WIFI_SUCCESS)) {
                RadarAddFirstActivity.this.finish();
            } else if (intent.getAction().equals(Action.REFRESH_CONTANTS)) {
                localDevices = FList.getInstance().getSetPasswordLocalDevices();
                if (localDevices.size() > 0) {
                    RadarAddFirstActivity.this.local_device_bar_top.setVisibility(0);
                    RadarAddFirstActivity.this.text_local_device_count.setText("" + localDevices.size());
                    return;
                }
                RadarAddFirstActivity.this.local_device_bar_top.setVisibility(8);
            } else if (intent.getAction().equals(Action.LOCAL_DEVICE_SEARCH_END)) {
                localDevices = FList.getInstance().getSetPasswordLocalDevices();
                if (localDevices.size() > 0) {
                    RadarAddFirstActivity.this.local_device_bar_top.setVisibility(0);
                    RadarAddFirstActivity.this.text_local_device_count.setText("" + localDevices.size());
                    return;
                }
                RadarAddFirstActivity.this.local_device_bar_top.setVisibility(8);
            }
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(C0291R.layout.activity_radar_add_first);
        this.mContext = this;
        this.next = (Button) findViewById(C0291R.id.next);
        this.back = (ImageView) findViewById(C0291R.id.back_btn);
        this.local_device_bar_top = (RelativeLayout) findViewById(C0291R.id.local_device_bar_top);
        this.text_local_device_count = (TextView) findViewById(C0291R.id.text_local_device_count);
        this.next.setOnClickListener(new C04311());
        this.back.setOnClickListener(new C04322());
        this.local_device_bar_top.setOnClickListener(new C04333());
        regFilter();
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
        filter.addAction(Action.RADAR_SET_WIFI_FAILED);
        filter.addAction(Action.RADAR_SET_WIFI_SUCCESS);
        filter.addAction(Action.REFRESH_CONTANTS);
        filter.addAction(Action.LOCAL_DEVICE_SEARCH_END);
        filter.addAction(Action.ADD_CONTACT_SUCCESS);
        registerReceiver(this.br, filter);
        this.isRegFilter = true;
    }

    public int getActivityInfo() {
        return 55;
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            unregisterReceiver(this.br);
            this.isRegFilter = false;
        }
    }
}
