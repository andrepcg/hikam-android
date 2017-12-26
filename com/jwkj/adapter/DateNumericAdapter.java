package com.jwkj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.wheel.widget.AbstractWheelAdapter;

public class DateNumericAdapter extends AbstractWheelAdapter {
    Context context;
    int end;
    int start;

    public DateNumericAdapter(Context context, int start, int end) {
        this.context = context;
        this.start = start;
        this.end = end;
    }

    public int getItemsCount() {
        return (this.end - this.start) + 1;
    }

    public View getItem(int index, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(C0291R.layout.list_wheel_date_item, null);
        }
        TextView text = (TextView) view.findViewById(C0291R.id.text);
        int i = this.start + index;
        if (i < 0 || i >= 10) {
            text.setText(String.valueOf(this.start + index));
        } else {
            text.setText("0" + String.valueOf(this.start + index));
        }
        return view;
    }
}
