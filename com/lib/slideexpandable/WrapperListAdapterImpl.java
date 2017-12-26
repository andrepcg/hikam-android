package com.lib.slideexpandable;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

public abstract class WrapperListAdapterImpl extends BaseAdapter implements WrapperListAdapter {
    protected ListAdapter wrapped;

    public WrapperListAdapterImpl(ListAdapter wrapped) {
        this.wrapped = wrapped;
    }

    public ListAdapter getWrappedAdapter() {
        return this.wrapped;
    }

    public boolean areAllItemsEnabled() {
        return this.wrapped.areAllItemsEnabled();
    }

    public boolean isEnabled(int i) {
        return this.wrapped.isEnabled(i);
    }

    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        this.wrapped.registerDataSetObserver(dataSetObserver);
    }

    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        this.wrapped.unregisterDataSetObserver(dataSetObserver);
    }

    public int getCount() {
        return this.wrapped.getCount();
    }

    public Object getItem(int i) {
        return this.wrapped.getItem(i);
    }

    public long getItemId(int i) {
        return this.wrapped.getItemId(i);
    }

    public boolean hasStableIds() {
        return this.wrapped.hasStableIds();
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        return this.wrapped.getView(position, view, viewGroup);
    }

    public int getItemViewType(int i) {
        return this.wrapped.getItemViewType(i);
    }

    public int getViewTypeCount() {
        return this.wrapped.getViewTypeCount();
    }

    public boolean isEmpty() {
        return this.wrapped.isEmpty();
    }
}
