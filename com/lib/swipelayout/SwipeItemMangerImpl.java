package com.lib.swipelayout;

import android.view.View;
import android.widget.BaseAdapter;
import com.lib.swipelayout.Attributes.Mode;
import com.lib.swipelayout.SwipeLayout.OnLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class SwipeItemMangerImpl implements SwipeItemMangerInterface {
    public final int INVALID_POSITION = -1;
    protected BaseAdapter mBaseAdapter;
    protected int mOpenPosition = -1;
    protected Set<Integer> mOpenPositions = new HashSet();
    protected Set<SwipeLayout> mShownLayouts = new HashSet();
    private Mode mode = Mode.Single;

    class ValueBox {
        OnLayoutListener onLayoutListener;
        int position;
        SwipeMemory swipeMemory;

        ValueBox(int position, SwipeMemory swipeMemory, OnLayoutListener onLayoutListener) {
            this.swipeMemory = swipeMemory;
            this.onLayoutListener = onLayoutListener;
            this.position = position;
        }
    }

    class OnLayoutListener implements OnLayout {
        private int position;

        OnLayoutListener(int position) {
            this.position = position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void onLayout(SwipeLayout v) {
            if (SwipeItemMangerImpl.this.isOpen(this.position)) {
                v.open(false, false);
            } else {
                v.close(false, false);
            }
        }
    }

    class SwipeMemory extends SimpleSwipeListener {
        private int position;

        SwipeMemory(int position) {
            this.position = position;
        }

        public void onClose(SwipeLayout layout) {
            if (SwipeItemMangerImpl.this.mode == Mode.Multiple) {
                SwipeItemMangerImpl.this.mOpenPositions.remove(Integer.valueOf(this.position));
            } else {
                SwipeItemMangerImpl.this.mOpenPosition = -1;
            }
        }

        public void onStartOpen(SwipeLayout layout) {
            if (SwipeItemMangerImpl.this.mode == Mode.Single) {
                SwipeItemMangerImpl.this.closeAllExcept(layout);
            }
        }

        public void onOpen(SwipeLayout layout) {
            if (SwipeItemMangerImpl.this.mode == Mode.Multiple) {
                SwipeItemMangerImpl.this.mOpenPositions.add(Integer.valueOf(this.position));
                return;
            }
            SwipeItemMangerImpl.this.closeAllExcept(layout);
            SwipeItemMangerImpl.this.mOpenPosition = this.position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

    public abstract void bindView(View view, int i);

    public abstract void initialize(View view, int i);

    public abstract void updateConvertView(View view, int i);

    public SwipeItemMangerImpl(BaseAdapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("Adapter can not be null");
        } else if (adapter instanceof SwipeItemMangerInterface) {
            this.mBaseAdapter = adapter;
        } else {
            throw new IllegalArgumentException("adapter should implement the SwipeAdapterInterface");
        }
    }

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        this.mOpenPositions.clear();
        this.mShownLayouts.clear();
        this.mOpenPosition = -1;
    }

    public int getSwipeLayoutId(int position) {
        if (this.mBaseAdapter != null) {
            return ((SwipeAdapterInterface) this.mBaseAdapter).getSwipeLayoutResourceId(position);
        }
        return -1;
    }

    public void openItem(int position) {
        if (this.mode != Mode.Multiple) {
            this.mOpenPosition = position;
        } else if (!this.mOpenPositions.contains(Integer.valueOf(position))) {
            this.mOpenPositions.add(Integer.valueOf(position));
        }
        if (this.mBaseAdapter != null) {
            this.mBaseAdapter.notifyDataSetChanged();
        }
    }

    public void closeItem(int position) {
        if (this.mode == Mode.Multiple) {
            this.mOpenPositions.remove(Integer.valueOf(position));
        } else if (this.mOpenPosition == position) {
            this.mOpenPosition = -1;
        }
        if (this.mBaseAdapter != null) {
            this.mBaseAdapter.notifyDataSetChanged();
        }
    }

    public void closeAllExcept(SwipeLayout layout) {
        for (SwipeLayout s : this.mShownLayouts) {
            if (s != layout) {
                s.close();
            }
        }
    }

    public void closeAllItems() {
        if (this.mode == Mode.Multiple) {
            this.mOpenPositions.clear();
        } else {
            this.mOpenPosition = -1;
        }
        for (SwipeLayout s : this.mShownLayouts) {
            s.close();
        }
    }

    public void removeShownLayouts(SwipeLayout layout) {
        this.mShownLayouts.remove(layout);
    }

    public List<Integer> getOpenItems() {
        if (this.mode == Mode.Multiple) {
            return new ArrayList(this.mOpenPositions);
        }
        return Arrays.asList(new Integer[]{Integer.valueOf(this.mOpenPosition)});
    }

    public List<SwipeLayout> getOpenLayouts() {
        return new ArrayList(this.mShownLayouts);
    }

    public boolean isOpen(int position) {
        if (this.mode == Mode.Multiple) {
            return this.mOpenPositions.contains(Integer.valueOf(position));
        }
        return this.mOpenPosition == position;
    }
}
