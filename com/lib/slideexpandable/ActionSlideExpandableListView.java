package com.lib.slideexpandable;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class ActionSlideExpandableListView extends SlideExpandableListView {
    public static OnItemClickListener onItemClickListener;
    private int[] buttonIds = null;
    private OnActionClickListener listener;

    public interface OnActionClickListener {
        void onClick(View view, View view2, int i);
    }

    public interface OnItemClickListener {
        void OnClick(int i, int i2);
    }

    public /* bridge */ /* synthetic */ boolean collapse() {
        return super.collapse();
    }

    public /* bridge */ /* synthetic */ void enableExpandOnItemClick() {
        super.enableExpandOnItemClick();
    }

    public /* bridge */ /* synthetic */ void onRestoreInstanceState(Parcelable parcelable) {
        super.onRestoreInstanceState(parcelable);
    }

    public /* bridge */ /* synthetic */ Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    public ActionSlideExpandableListView(Context context) {
        super(context);
    }

    public ActionSlideExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionSlideExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setItemActionListener(OnActionClickListener listener, int... buttonIds) {
        this.listener = listener;
        this.buttonIds = buttonIds;
    }

    public void setItemClickListener(OnItemClickListener onItemClickListener) {
        onItemClickListener = onItemClickListener;
    }

    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(new WrapperListAdapterImpl(adapter) {
            public View getView(final int position, View view, ViewGroup viewGroup) {
                final View listView = this.wrapped.getView(position, view, viewGroup);
                if (!(ActionSlideExpandableListView.this.buttonIds == null || listView == null)) {
                    for (int id : ActionSlideExpandableListView.this.buttonIds) {
                        View buttonView = listView.findViewById(id);
                        if (buttonView != null) {
                            buttonView.findViewById(id).setOnClickListener(new OnClickListener() {
                                public void onClick(View view) {
                                    if (ActionSlideExpandableListView.this.listener != null) {
                                        ActionSlideExpandableListView.this.listener.onClick(listView, view, position);
                                    }
                                }
                            });
                        }
                    }
                }
                return listView;
            }
        });
    }
}
