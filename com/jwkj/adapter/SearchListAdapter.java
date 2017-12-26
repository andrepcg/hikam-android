package com.jwkj.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.activity.SearchListActivity;
import com.jwkj.global.Constants.Action;
import com.jwkj.utils.LanguageComparator_CN;
import com.jwkj.utils.PinYinSort;
import java.util.Collections;

public class SearchListAdapter extends BaseExpandableListAdapter {
    private PinYinSort assort = new PinYinSort();
    private LanguageComparator_CN cnSort = new LanguageComparator_CN();
    private Context context;
    private String[] data;
    private LayoutInflater inflater;

    public SearchListAdapter(Context context, String[] data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
        sort();
    }

    private void sort() {
        for (String str : this.data) {
            this.assort.getHashList().add(str);
        }
        this.assort.getHashList().sortKeyComparator(this.cnSort);
        int length = this.assort.getHashList().size();
        for (int i = 0; i < length; i++) {
            Collections.sort(this.assort.getHashList().getValueListIndex(i), this.cnSort);
        }
    }

    public Object getChild(int group, int child) {
        return this.assort.getHashList().getValueIndex(group, child);
    }

    public long getChildId(int group, int child) {
        return (long) child;
    }

    public View getChildView(int group, int child, boolean arg2, View contentView, ViewGroup arg4) {
        if (contentView == null) {
            contentView = this.inflater.inflate(C0291R.layout.list_searchlist_item, null);
        }
        TextView name = (TextView) contentView.findViewById(C0291R.id.name);
        TextView count = (TextView) contentView.findViewById(C0291R.id.county_count);
        final String[] info = ((String) this.assort.getHashList().getValueIndex(group, child)).split(":");
        name.setText(info[0]);
        count.setText(info[1]);
        contentView.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent i = new Intent(Action.ACTION_COUNTRY_CHOOSE);
                i.putExtra("info", info);
                SearchListAdapter.this.context.sendBroadcast(i);
                ((SearchListActivity) SearchListAdapter.this.context).finish();
            }
        });
        return contentView;
    }

    public int getChildrenCount(int group) {
        return this.assort.getHashList().getValueListIndex(group).size();
    }

    public Object getGroup(int group) {
        return this.assort.getHashList().getValueListIndex(group);
    }

    public int getGroupCount() {
        return this.assort.getHashList().size();
    }

    public long getGroupId(int group) {
        return (long) group;
    }

    public View getGroupView(int group, boolean arg1, View contentView, ViewGroup arg3) {
        if (contentView == null) {
            contentView = this.inflater.inflate(C0291R.layout.title_search_list, null);
            contentView.setClickable(true);
        }
        ((TextView) contentView.findViewById(C0291R.id.name)).setText(this.assort.getFirstChar((String) this.assort.getHashList().getValueIndex(group, 0)));
        return contentView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    public PinYinSort getAssort() {
        return this.assort;
    }
}
