package com.jwkj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;

public class YearAdapter extends BaseAdapter {
    Context context;
    String[] data;

    class C05041 implements OnTouchListener {
        C05041() {
        }

        public boolean onTouch(View arg0, MotionEvent arg1) {
            return true;
        }
    }

    public YearAdapter(Context context, String[] data) {
        this.context = context;
        this.data = data;
    }

    public int getCount() {
        return Integer.MAX_VALUE;
    }

    public Object getItem(int arg0) {
        return this.data[arg0 % this.data.length];
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int arg0, View arg1, ViewGroup arg2) {
        RelativeLayout view = (RelativeLayout) arg1;
        if (view == null) {
            view = (RelativeLayout) LayoutInflater.from(this.context).inflate(C0291R.layout.list_date_item, null);
            view.setOnTouchListener(new C05041());
        }
        TextView text = (TextView) view.findViewById(C0291R.id.text);
        text.setClickable(false);
        text.setFocusable(false);
        text.setText(this.data[arg0 % this.data.length]);
        return view;
    }
}
