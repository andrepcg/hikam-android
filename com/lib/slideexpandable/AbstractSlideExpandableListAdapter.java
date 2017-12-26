package com.lib.slideexpandable;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import java.util.BitSet;

public abstract class AbstractSlideExpandableListAdapter extends WrapperListAdapterImpl {
    private int animationDuration = 330;
    private View lastOpen = null;
    private int lastOpenPosition = -1;
    private View lastToggle = null;
    private BitSet openItems = new BitSet();
    private final SparseIntArray viewHeights = new SparseIntArray(10);

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new C06711();
        public int lastOpenPosition;
        public BitSet openItems;

        static class C06711 implements Creator<SavedState> {
            C06711() {
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        SavedState(Parcelable superState) {
            super(superState);
            this.openItems = null;
            this.lastOpenPosition = -1;
        }

        private SavedState(Parcel in) {
            super(in);
            this.openItems = null;
            this.lastOpenPosition = -1;
            in.writeInt(this.lastOpenPosition);
            AbstractSlideExpandableListAdapter.writeBitSet(in, this.openItems);
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            this.lastOpenPosition = out.readInt();
            this.openItems = AbstractSlideExpandableListAdapter.readBitSet(out);
        }
    }

    public abstract View getArrowView(View view);

    public abstract View getExpandToggleButton(View view);

    public abstract View getExpandableView(View view);

    public AbstractSlideExpandableListAdapter(ListAdapter wrapped) {
        super(wrapped);
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        view = this.wrapped.getView(position, view, viewGroup);
        enableFor(view, position);
        return view;
    }

    public int getAnimationDuration() {
        return this.animationDuration;
    }

    public void setAnimationDuration(int duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Duration is less than zero");
        }
        this.animationDuration = duration;
    }

    public boolean isAnyItemExpanded() {
        return this.lastOpenPosition != -1;
    }

    public void enableFor(View parent, int position) {
        View more = getExpandToggleButton(parent);
        View itemToolbar = getExpandableView(parent);
        itemToolbar.measure(parent.getWidth(), parent.getHeight());
        enableFor(more, itemToolbar, position);
    }

    private void enableFor(final View button, final View target, final int position) {
        if (target == this.lastOpen && position != this.lastOpenPosition) {
            this.lastOpen = null;
            this.lastToggle = null;
        }
        if (position == this.lastOpenPosition) {
            this.lastOpen = target;
            this.lastToggle = button;
        }
        if (this.viewHeights.get(position, -1) == -1) {
            this.viewHeights.put(position, target.getMeasuredHeight());
            updateExpandable(target, position);
        } else {
            updateExpandable(target, position);
        }
        button.setOnClickListener(new OnClickListener() {
            public void onClick(final View view) {
                Animation a = target.getAnimation();
                if (a == null || !a.hasStarted() || a.hasEnded()) {
                    int type;
                    target.setAnimation(null);
                    if (target.getVisibility() == 0) {
                        type = 1;
                    } else {
                        type = 0;
                    }
                    if (type == 0) {
                        Log.e("my", "expand");
                        AbstractSlideExpandableListAdapter.this.openItems.set(position, true);
                        if (ActionSlideExpandableListView.onItemClickListener != null) {
                            ActionSlideExpandableListView.onItemClickListener.OnClick(position, 1);
                        }
                    } else {
                        Log.e("my", "collapse");
                        if (ActionSlideExpandableListView.onItemClickListener != null) {
                            ActionSlideExpandableListView.onItemClickListener.OnClick(position, 0);
                        }
                        AbstractSlideExpandableListAdapter.this.openItems.set(position, false);
                    }
                    if (type == 0) {
                        if (!(AbstractSlideExpandableListAdapter.this.lastOpenPosition == -1 || AbstractSlideExpandableListAdapter.this.lastOpenPosition == position)) {
                            if (AbstractSlideExpandableListAdapter.this.lastOpen != null) {
                                AbstractSlideExpandableListAdapter.this.animateView(AbstractSlideExpandableListAdapter.this.lastOpen, 1);
                            }
                            Log.e("my", "collapse222");
                            AbstractSlideExpandableListAdapter.this.openItems.set(AbstractSlideExpandableListAdapter.this.lastOpenPosition, false);
                        }
                        AbstractSlideExpandableListAdapter.this.lastOpen = target;
                        AbstractSlideExpandableListAdapter.this.lastToggle = button;
                        AbstractSlideExpandableListAdapter.this.lastOpenPosition = position;
                    } else if (AbstractSlideExpandableListAdapter.this.lastOpenPosition == position) {
                        AbstractSlideExpandableListAdapter.this.lastOpenPosition = -1;
                    }
                    AbstractSlideExpandableListAdapter.this.animateView(target, type);
                    return;
                }
                a.setAnimationListener(new AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        view.performClick();
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        });
    }

    private void updateExpandable(View target, int position) {
        LayoutParams params = (LayoutParams) target.getLayoutParams();
        if (this.openItems.get(position)) {
            target.setVisibility(0);
            params.bottomMargin = 0;
            return;
        }
        target.setVisibility(8);
        params.bottomMargin = 0 - this.viewHeights.get(position);
    }

    private void animateView(View target, int type) {
        Animation anim = new ExpandCollapseAnimation(target, type);
        anim.setDuration((long) getAnimationDuration());
        target.startAnimation(anim);
    }

    public boolean collapseLastOpen() {
        if (!isAnyItemExpanded()) {
            return false;
        }
        if (this.lastOpen != null) {
            animateView(this.lastOpen, 1);
        }
        this.openItems.set(this.lastOpenPosition, false);
        this.lastOpenPosition = -1;
        return true;
    }

    public Parcelable onSaveInstanceState(Parcelable parcelable) {
        SavedState ss = new SavedState(parcelable);
        ss.lastOpenPosition = this.lastOpenPosition;
        ss.openItems = this.openItems;
        return ss;
    }

    public void onRestoreInstanceState(SavedState state) {
        this.lastOpenPosition = state.lastOpenPosition;
        this.openItems = state.openItems;
    }

    private static BitSet readBitSet(Parcel src) {
        int cardinality = src.readInt();
        BitSet set = new BitSet();
        for (int i = 0; i < cardinality; i++) {
            set.set(src.readInt());
        }
        return set;
    }

    private static void writeBitSet(Parcel dest, BitSet set) {
        int nextSetBit = -1;
        dest.writeInt(set.cardinality());
        while (true) {
            nextSetBit = set.nextSetBit(nextSetBit + 1);
            if (nextSetBit != -1) {
                dest.writeInt(nextSetBit);
            } else {
                return;
            }
        }
    }
}
