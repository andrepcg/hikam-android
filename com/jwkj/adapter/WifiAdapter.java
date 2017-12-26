package com.jwkj.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.fragment.NetControlFrag;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import com.jwkj.widget.guide.ApModeGuideDoneActivity;

public class WifiAdapter extends BaseAdapter {
    ApModeGuideDoneActivity aga;
    int iCount;
    int iCurrentId;
    int[] iStrength;
    int[] iType;
    Context mContext;
    String[] names;
    NetControlFrag ncf;

    public WifiAdapter(Context context, NetControlFrag ncf) {
        this.mContext = context;
        this.iCount = 0;
        this.ncf = ncf;
    }

    public WifiAdapter(Context context, int flag, ApModeGuideDoneActivity aga) {
        this.mContext = context;
        this.iCount = 0;
        this.aga = aga;
    }

    public WifiAdapter(Context context, int iCount, int[] iType, int[] iStrength, String[] names) {
        this.mContext = context;
        this.iCount = iCount;
        this.iType = iType;
        this.iStrength = iStrength;
        this.names = names;
    }

    public int getCount() {
        return this.iCount;
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(final int position, View arg1, ViewGroup arg2) {
        Log.e("datas", this.iType[position] + " " + this.names[position]);
        View view = arg1;
        if (view == null) {
            view = LayoutInflater.from(this.mContext).inflate(C0291R.layout.list_wifi_item, null);
        }
        TextView name = (TextView) view.findViewById(C0291R.id.name);
        ImageView wifi_strength = (ImageView) view.findViewById(C0291R.id.wifi_strength);
        ImageView choose_img = (ImageView) view.findViewById(C0291R.id.choose_img);
        ImageView wifi_type = (ImageView) view.findViewById(C0291R.id.wifi_type);
        if (this.iType[position] == 0) {
            wifi_type.setVisibility(8);
            Log.e("gone", this.iType[position] + " " + this.names[position]);
        } else {
            wifi_type.setVisibility(0);
        }
        try {
            name.setText(this.names[position]);
        } catch (Exception e) {
            name.setText("");
        }
        if (position == this.iCurrentId) {
            choose_img.setVisibility(0);
        } else {
            choose_img.setVisibility(8);
        }
        switch (this.iStrength[position]) {
            case 0:
                wifi_strength.setImageResource(C0291R.drawable.ic_strength1);
                break;
            case 1:
                wifi_strength.setImageResource(C0291R.drawable.ic_strength2);
                break;
            case 2:
                wifi_strength.setImageResource(C0291R.drawable.ic_strength3);
                break;
            case 3:
                wifi_strength.setImageResource(C0291R.drawable.ic_strength4);
                break;
            case 4:
                wifi_strength.setImageResource(C0291R.drawable.ic_strength5);
                break;
        }
        view.setOnClickListener(new OnClickListener() {

            class C11031 implements OnButtonOkListener {
                C11031() {
                }

                public void onClick() {
                    if (WifiAdapter.this.ncf != null) {
                        boolean nowNotify = false;
                        if (WifiAdapter.this.iCurrentId < 0) {
                            nowNotify = true;
                        } else if (!WifiAdapter.this.names[position].equals(WifiAdapter.this.names[WifiAdapter.this.iCurrentId])) {
                            nowNotify = true;
                        }
                        WifiAdapter.this.ncf.showModfyWifi(WifiAdapter.this.iType[position], WifiAdapter.this.names[position], nowNotify);
                        return;
                    }
                    WifiAdapter.this.aga.showModfyWifi(WifiAdapter.this.iType[position], WifiAdapter.this.names[position]);
                }
            }

            public void onClick(View arg0) {
                NormalDialog dialog = new NormalDialog(WifiAdapter.this.mContext, WifiAdapter.this.mContext.getResources().getString(C0291R.string.warning), WifiAdapter.this.mContext.getResources().getString(C0291R.string.modify_net_warning), WifiAdapter.this.mContext.getResources().getString(C0291R.string.change), WifiAdapter.this.mContext.getResources().getString(C0291R.string.cancel));
                dialog.setOnButtonOkListener(new C11031());
                dialog.showNormalDialog();
                dialog.setCanceledOnTouchOutside(false);
            }
        });
        return view;
    }

    public void updateData(int iCurrentId, int iCount, int[] iType, int[] iStrength, String[] names) {
        this.iCurrentId = iCurrentId;
        this.iCount = iCount;
        this.iType = iType;
        this.iStrength = iStrength;
        this.names = names;
        notifyDataSetChanged();
    }
}
