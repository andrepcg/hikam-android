package com.lib.swipelayout;

import android.view.View;
import android.widget.BaseAdapter;

public class SwipeItemAdapterMangerImpl extends SwipeItemMangerImpl {
    protected BaseAdapter mAdapter;

    public SwipeItemAdapterMangerImpl(BaseAdapter adapter) {
        super(adapter);
        this.mAdapter = adapter;
    }

    public void initialize(View target, int position) {
        int resId = getSwipeLayoutId(position);
        OnLayoutListener onLayoutListener = new OnLayoutListener(position);
        SwipeLayout swipeLayout = (SwipeLayout) target.findViewById(resId);
        if (swipeLayout == null) {
            throw new IllegalStateException("can not find SwipeLayout in target view");
        }
        SwipeMemory swipeMemory = new SwipeMemory(position);
        swipeLayout.addSwipeListener(swipeMemory);
        swipeLayout.addOnLayoutListener(onLayoutListener);
        swipeLayout.setTag(resId, new ValueBox(position, swipeMemory, onLayoutListener));
        this.mShownLayouts.add(swipeLayout);
    }

    public void updateConvertView(View target, int position) {
        int resId = getSwipeLayoutId(position);
        SwipeLayout swipeLayout = (SwipeLayout) target.findViewById(resId);
        if (swipeLayout == null) {
            throw new IllegalStateException("can not find SwipeLayout in target view");
        }
        ValueBox valueBox = (ValueBox) swipeLayout.getTag(resId);
        valueBox.swipeMemory.setPosition(position);
        valueBox.onLayoutListener.setPosition(position);
        valueBox.position = position;
    }

    public void bindView(View target, int position) {
    }
}
