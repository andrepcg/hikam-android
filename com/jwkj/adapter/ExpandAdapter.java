package com.jwkj.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.jwkj.widget.MyImageView;
import java.util.ArrayList;
import java.util.List;

public class ExpandAdapter extends BaseExpandableListAdapter {
    List<List<String>> child = new ArrayList();
    List<String> group = new ArrayList();
    private Context mContext;

    public ExpandAdapter(Context mContext) {
        this.mContext = mContext;
        addInfo("Andy", new String[]{"male", "138123***", "GuangZhou"});
        addInfo("Fairy", new String[]{"female", "138123***", "GuangZhou"});
    }

    private void addInfo(String g, String[] c) {
        this.group.add(g);
        List<String> childitem = new ArrayList();
        for (Object add : c) {
            childitem.add(add);
        }
        this.child.add(childitem);
    }

    public Object getChild(int arg0, int arg1) {
        return ((List) this.child.get(arg0)).get(arg1);
    }

    public long getChildId(int arg0, int arg1) {
        return (long) arg1;
    }

    public View getChildView(int arg0, int arg1, boolean arg2, View arg3, ViewGroup arg4) {
        return getGenericView((String) ((List) this.child.get(arg0)).get(arg1));
    }

    public int getChildrenCount(int arg0) {
        return ((List) this.child.get(arg0)).size();
    }

    public Object getGroup(int arg0) {
        return this.group.get(arg0);
    }

    public int getGroupCount() {
        return this.group.size();
    }

    public long getGroupId(int arg0) {
        return 0;
    }

    public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {
        String string = (String) this.group.get(arg0);
        return new TextView(this.mContext);
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    public TextView getGenericView(String s) {
        LayoutParams lp = new LayoutParams(-1, MyImageView.IMAGE_WIDTH);
        TextView text = new TextView(this.mContext);
        text.setLayoutParams(lp);
        text.setGravity(19);
        text.setPadding(36, 0, 0, 0);
        text.setText(s);
        return text;
    }
}
