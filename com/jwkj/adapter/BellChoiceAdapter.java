package com.jwkj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import java.util.ArrayList;
import java.util.HashMap;

public class BellChoiceAdapter extends BaseAdapter {
    public BellChoiceAdapter ba;
    private ArrayList<HashMap<String, String>> bells;
    public int checkedId = -1;
    private Context context;

    public BellChoiceAdapter(Context context, ArrayList<HashMap<String, String>> bells) {
        this.context = context;
        this.bells = bells;
        this.ba = this;
    }

    public int getCount() {
        if (this.bells == null) {
            return 0;
        }
        return this.bells.size();
    }

    public Object getItem(int arg0) {
        return this.bells.get(arg0);
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int arg0, View view, ViewGroup arg2) {
        RelativeLayout item = (RelativeLayout) view;
        if (item == null) {
            item = (RelativeLayout) LayoutInflater.from(this.context).inflate(C0291R.layout.choice_bell_list_item, null);
            item.setTag(item);
        } else {
            item = (RelativeLayout) item.getTag();
        }
        HashMap<String, String> bellinfo = (HashMap) this.bells.get(arg0);
        RadioButton button = (RadioButton) item.findViewById(C0291R.id.checkButton);
        ((TextView) item.findViewById(C0291R.id.bellName)).setText((CharSequence) bellinfo.get("bellName"));
        if (((String) bellinfo.get("bellId")).equals(String.valueOf(this.checkedId))) {
            button.setChecked(true);
        } else {
            button.setChecked(false);
        }
        return item;
    }

    public void setCheckedId(int id) {
        this.checkedId = id;
    }
}
