package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.hikam.C0291R;
import com.jwkj.adapter.SysMsgAdapter;
import com.jwkj.data.DataManager;
import com.jwkj.data.SysMessage;
import com.jwkj.global.NpcCommon;
import com.lib.slideexpandable.ActionSlideExpandableListView;
import com.lib.slideexpandable.ActionSlideExpandableListView.OnActionClickListener;
import com.lib.slideexpandable.ActionSlideExpandableListView.OnItemClickListener;
import java.util.List;

public class SysMsgActivity extends BaseActivity implements OnClickListener {
    public static final String DELETE_REFESH = "com.jwkj.DELETE_REFESH";
    public static final String REFRESH = "com.jwkj.REFRESH";
    SysMsgAdapter adapter;
    ImageView back;
    boolean isRegReceiver = false;
    ActionSlideExpandableListView list;
    Context mContext;
    public BroadcastReceiver receiver = new C04533();

    class C04533 extends BroadcastReceiver {
        C04533() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(SysMsgActivity.REFRESH)) {
                SysMsgActivity.this.adapter.refresh();
            } else if (intent.getAction().equals(SysMsgActivity.DELETE_REFESH)) {
                List<SysMessage> data = DataManager.findSysMessageByActiveUser(SysMsgActivity.this.mContext, NpcCommon.mThreeNum);
                SysMsgActivity.this.adapter = null;
                SysMsgActivity.this.adapter = new SysMsgAdapter(SysMsgActivity.this.mContext, data);
                SysMsgActivity.this.list.setAdapter(SysMsgActivity.this.adapter);
            }
        }
    }

    class C10901 implements OnItemClickListener {
        C10901() {
        }

        public void OnClick(int position, int type) {
            SysMsgActivity.this.adapter.upDateSysMsg(position, type);
        }
    }

    class C10912 implements OnActionClickListener {
        C10912() {
        }

        public void onClick(View listView, View buttonview, int position) {
            buttonview.getId();
        }
    }

    public void onCreate(Bundle savedData) {
        super.onCreate(savedData);
        setContentView(C0291R.layout.activity_sysmsg);
        this.mContext = this;
        initComponent();
        regFilter();
    }

    public void initComponent() {
        this.list = (ActionSlideExpandableListView) findViewById(C0291R.id.list_sys_msg);
        this.back = (ImageView) findViewById(C0291R.id.back_btn);
        this.back.setOnClickListener(this);
        this.adapter = new SysMsgAdapter(this.mContext, DataManager.findSysMessageByActiveUser(this.mContext, NpcCommon.mThreeNum));
        this.list.setAdapter(this.adapter);
        this.list.setItemClickListener(new C10901());
        this.list.setItemActionListener(new C10912(), C0291R.id.content);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(REFRESH);
        filter.addAction(DELETE_REFESH);
        registerReceiver(this.receiver, filter);
        this.isRegReceiver = true;
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.isRegReceiver) {
            unregisterReceiver(this.receiver);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            default:
                return;
        }
    }

    public int getActivityInfo() {
        return 27;
    }
}
