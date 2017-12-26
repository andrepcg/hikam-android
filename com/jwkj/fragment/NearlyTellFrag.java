package com.jwkj.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import com.hikam.C0291R;
import com.jwkj.activity.MainActivity;
import com.jwkj.adapter.NearlyTellAdapter;
import com.jwkj.data.DataManager;
import com.jwkj.data.NearlyTell;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.NpcCommon;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class NearlyTellFrag extends BaseFragment implements OnClickListener {
    NormalDialog dialog;
    private boolean isRegFilter = false;
    private NearlyTellAdapter mAdapter;
    private ImageView mClearBtn = null;
    private Context mContext;
    private ListView mListView;
    BroadcastReceiver mReceiver = new C05351();
    private List<NearlyTell> nearlyTellList = new ArrayList();

    class C05351 extends BroadcastReceiver {
        C05351() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Action.ACTION_REFRESH_NEARLY_TELL)) {
                NearlyTellFrag.this.getData();
                NearlyTellFrag.this.updateListView();
            }
        }
    }

    class C11112 implements OnButtonOkListener {
        C11112() {
        }

        public void onClick() {
            DataManager.clearNearlyTell(NearlyTellFrag.this.mContext, NpcCommon.mThreeNum);
            Intent refreshContans = new Intent();
            refreshContans.setAction(Action.ACTION_REFRESH_NEARLY_TELL);
            NearlyTellFrag.this.mContext.sendBroadcast(refreshContans);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(C0291R.layout.fragment_neally, container, false);
        Log.e("my", "createNearlyTellFrag");
        this.mContext = MainActivity.mContext;
        initComponent(view);
        regFilter();
        return view;
    }

    public void initComponent(View view) {
        this.mListView = (ListView) view.findViewById(C0291R.id.list);
        this.mClearBtn = (ImageView) view.findViewById(C0291R.id.clear);
        this.mClearBtn.setOnClickListener(this);
        getData();
        this.mAdapter = new NearlyTellAdapter(this.mContext, this.nearlyTellList);
        this.mListView.setAdapter(this.mAdapter);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.ACTION_REFRESH_NEARLY_TELL);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    private void getData() {
        this.nearlyTellList = DataManager.findNearlyTellByActiveUser(this.mContext, NpcCommon.mThreeNum);
        ArrayList<String> array = new ArrayList();
        HashMap<String, NearlyTell> data = new HashMap();
        for (int i = 0; i < this.nearlyTellList.size(); i++) {
            NearlyTell nearlyTell = (NearlyTell) this.nearlyTellList.get(i);
            Timestamp userTime = new Timestamp(Long.parseLong(nearlyTell.tellTime));
            if (array.contains(nearlyTell.tellId)) {
                NearlyTell u = (NearlyTell) data.get(nearlyTell.tellId);
                if (new Timestamp(Long.parseLong(u.tellTime)).before(userTime)) {
                    u.tellTime = nearlyTell.tellTime;
                    u.tellType = nearlyTell.tellType;
                }
                u.count++;
            } else {
                nearlyTell.count = 1;
                data.put(nearlyTell.tellId, nearlyTell);
                array.add(nearlyTell.tellId);
            }
        }
        this.nearlyTellList.clear();
        for (NearlyTell nearlyTell2 : data.values()) {
            this.nearlyTellList.add(nearlyTell2);
        }
        Collections.sort(this.nearlyTellList);
    }

    private void updateListView() {
        if (this.mAdapter != null) {
            this.mAdapter.updateData(this.nearlyTellList);
            this.mAdapter.notifyDataSetChanged();
            return;
        }
        this.mAdapter = new NearlyTellAdapter(this.mContext, this.nearlyTellList);
        this.mListView.setAdapter(this.mAdapter);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.clear:
                if (this.dialog == null || !this.dialog.isShowing()) {
                    this.dialog = new NormalDialog(this.mContext, this.mContext.getResources().getString(C0291R.string.delete_call_records), this.mContext.getResources().getString(C0291R.string.clear_confirm), this.mContext.getResources().getString(C0291R.string.clear), this.mContext.getResources().getString(C0291R.string.cancel));
                    this.dialog.setOnButtonOkListener(new C11112());
                    this.dialog.showDialog();
                    return;
                }
                Log.e("my", "isShowing");
                return;
            default:
                return;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.isRegFilter = false;
            this.mContext.unregisterReceiver(this.mReceiver);
        }
    }
}
