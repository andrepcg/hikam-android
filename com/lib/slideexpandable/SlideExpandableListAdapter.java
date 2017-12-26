package com.lib.slideexpandable;

import android.view.View;
import android.widget.ListAdapter;
import com.hikam.C0291R;

public class SlideExpandableListAdapter extends AbstractSlideExpandableListAdapter {
    private int expandable_view_id;
    private int toggle_button_id;

    public SlideExpandableListAdapter(ListAdapter wrapped, int toggle_button_id, int expandable_view_id) {
        super(wrapped);
        this.toggle_button_id = toggle_button_id;
        this.expandable_view_id = expandable_view_id;
    }

    public SlideExpandableListAdapter(ListAdapter wrapped) {
        this(wrapped, C0291R.id.expandable_toggle_button, C0291R.id.expandable);
    }

    public View getExpandToggleButton(View parent) {
        return parent.findViewById(this.toggle_button_id);
    }

    public View getExpandableView(View parent) {
        return parent.findViewById(this.expandable_view_id);
    }

    public View getArrowView(View parent) {
        return null;
    }
}
