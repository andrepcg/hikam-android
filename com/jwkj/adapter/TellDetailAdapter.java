package com.jwkj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.data.NearlyTell;
import com.jwkj.utils.Utils;
import java.util.List;

public class TellDetailAdapter extends BaseAdapter {
    Context context;
    List<NearlyTell> list;

    public TellDetailAdapter(Context context, List<NearlyTell> list) {
        this.context = context;
        this.list = list;
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int arg0, View arg1, ViewGroup arg2) {
        View view = arg1;
        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(C0291R.layout.list_tell_detail_item, null);
        }
        ImageView callState = (ImageView) view.findViewById(C0291R.id.callState);
        TextView tellDate = (TextView) view.findViewById(C0291R.id.tellDate);
        ImageView callType = (ImageView) view.findViewById(C0291R.id.call_type);
        NearlyTell nearlyTell = (NearlyTell) this.list.get(arg0);
        switch (nearlyTell.tellState) {
            case 0:
                callState.setImageResource(C0291R.drawable.call_in_reject);
                break;
            case 1:
                callState.setImageResource(C0291R.drawable.call_in_accept);
                break;
            case 2:
                callState.setImageResource(C0291R.drawable.call_out_reject);
                break;
            case 3:
                callState.setImageResource(C0291R.drawable.call_out_accept);
                break;
        }
        tellDate.setText(Utils.getFormatTellDate(this.context, nearlyTell.tellTime));
        if (nearlyTell.tellType == 0) {
            callType.setBackgroundResource(C0291R.drawable.call);
        } else if (nearlyTell.tellType == 1) {
            callType.setBackgroundResource(C0291R.drawable.monitore);
        }
        return view;
    }

    public void updateData(List<NearlyTell> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
