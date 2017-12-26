package com.lib.slideexpandable;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

class SlideExpandableListView extends ListView {
    private SlideExpandableListAdapter adapter;

    class C06731 implements OnItemClickListener {
        C06731() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ((SlideExpandableListAdapter) SlideExpandableListView.this.getAdapter()).getExpandToggleButton(view).performClick();
        }
    }

    public SlideExpandableListView(Context context) {
        super(context);
    }

    public SlideExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlideExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean collapse() {
        if (this.adapter != null) {
            return this.adapter.collapseLastOpen();
        }
        return false;
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = new SlideExpandableListAdapter(adapter);
        super.setAdapter(this.adapter);
    }

    public void enableExpandOnItemClick() {
        setOnItemClickListener(new C06731());
    }

    public Parcelable onSaveInstanceState() {
        return this.adapter.onSaveInstanceState(super.onSaveInstanceState());
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState ss = (SavedState) state;
            super.onRestoreInstanceState(ss.getSuperState());
            this.adapter.onRestoreInstanceState(ss);
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
