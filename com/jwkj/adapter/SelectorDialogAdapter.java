package com.jwkj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hikam.C0291R;

public class SelectorDialogAdapter extends BaseAdapter {
    String[] data;
    Context mContext;

    public SelectorDialogAdapter(Context context, String[] data) {
        this.mContext = context;
        this.data = data;
    }

    public int getCount() {
        return this.data.length;
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int position, View arg1, ViewGroup arg2) {
        View view = arg1;
        if (view == null) {
            view = LayoutInflater.from(this.mContext).inflate(C0291R.layout.list_selector_dialog_item, null);
        }
        ((TextView) view.findViewById(C0291R.id.name)).setText(this.data[position]);
        return view;
    }
}
