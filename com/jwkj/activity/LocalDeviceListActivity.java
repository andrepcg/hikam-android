package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import com.hikam.C0291R;
import com.jwkj.adapter.LocalDeviceListAdapter;
import com.jwkj.global.Constants.Action;
import com.jwkj.utils.C0568T;
import com.jwkj.widget.NormalDialog;

public class LocalDeviceListActivity extends BaseActivity implements OnClickListener {
    private NormalDialog dialog;
    boolean isRegFilter;
    private LocalDeviceListAdapter mAdapter;
    private ImageView mBack;
    private Context mContext;
    private ListView mList;
    public Runnable mNoDevice = new C03971();
    private BroadcastReceiver mReceiver = new C03982();
    public Handler myhandler = new Handler();

    class C03971 implements Runnable {
        C03971() {
        }

        public void run() {
            if (LocalDeviceListActivity.this.dialog != null && LocalDeviceListActivity.this.dialog.isShowing()) {
                LocalDeviceListActivity.this.dialog.cancel();
                LocalDeviceListActivity.this.dialog = null;
                C0568T.showShort(LocalDeviceListActivity.this.mContext, (int) C0291R.string.no_camera_in_lan);
            }
        }
    }

    class C03982 extends BroadcastReceiver {
        C03982() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Action.ADD_CONTACT_SUCCESS)) {
                if (LocalDeviceListActivity.this.dialog != null && LocalDeviceListActivity.this.dialog.isShowing()) {
                    LocalDeviceListActivity.this.dialog.cancel();
                }
                LocalDeviceListActivity.this.mAdapter.updateData();
                LocalDeviceListActivity.this.finish();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(C0291R.layout.activity_local_device_list);
        initCompent();
        regFilter();
        if (this.mAdapter.getCount() <= 0) {
            this.dialog = new NormalDialog(this.mContext);
            this.dialog.showLoadingDialog2();
            this.dialog.setCanceledOnTouchOutside(false);
        }
        this.myhandler.postDelayed(this.mNoDevice, 30000);
    }

    public void initCompent() {
        this.mBack = (ImageView) findViewById(C0291R.id.back_btn);
        this.mList = (ListView) findViewById(C0291R.id.list_local_device);
        this.mAdapter = new LocalDeviceListAdapter(this);
        this.mList.setAdapter(this.mAdapter);
        this.mBack.setOnClickListener(this);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.ADD_CONTACT_SUCCESS);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            default:
                return;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.isRegFilter = false;
        }
    }

    public int getActivityInfo() {
        return 44;
    }
}
