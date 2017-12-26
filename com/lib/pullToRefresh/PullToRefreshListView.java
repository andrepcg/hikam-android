package com.lib.pullToRefresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.lib.pullToRefresh.PullToRefreshBase.AnimationStyle;
import com.lib.pullToRefresh.PullToRefreshBase.Mode;
import com.lib.pullToRefresh.PullToRefreshBase.Orientation;
import com.lib.pullToRefresh.PullToRefreshBase.State;

public class PullToRefreshListView extends PullToRefreshAdapterViewBase<ListView> {
    private LoadingLayout mFooterLoading;
    private LoadingLayout mHeaderLoading;
    private boolean mListViewExtrasEnabled;
    private FrameLayout mLvFooterLoadingFrame;

    protected class InternalListView extends ListView implements EmptyViewMethodAccessor {
        private boolean mAddedLvFooter = false;

        public InternalListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        protected void dispatchDraw(Canvas canvas) {
            try {
                super.dispatchDraw(canvas);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        public boolean dispatchTouchEvent(MotionEvent ev) {
            try {
                return super.dispatchTouchEvent(ev);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return false;
            }
        }

        public void setAdapter(ListAdapter adapter) {
            if (!(PullToRefreshListView.this.mLvFooterLoadingFrame == null || this.mAddedLvFooter)) {
                addFooterView(PullToRefreshListView.this.mLvFooterLoadingFrame, null, false);
                this.mAddedLvFooter = true;
            }
            super.setAdapter(adapter);
        }

        public void setEmptyView(View emptyView) {
            PullToRefreshListView.this.setEmptyView(emptyView);
        }

        public void setEmptyViewInternal(View emptyView) {
            super.setEmptyView(emptyView);
        }
    }

    @TargetApi(9)
    final class InternalListViewSDK9 extends InternalListView {
        public InternalListViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
            boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
            OverscrollHelper.overScrollBy(PullToRefreshListView.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);
            return returnValue;
        }
    }

    public PullToRefreshListView(Context context) {
        super(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshListView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshListView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    protected void onRefreshing(boolean doScroll) {
        ListAdapter adapter = ((ListView) this.mRefreshableView).getAdapter();
        if (!this.mListViewExtrasEnabled || !getShowViewWhileRefreshing() || adapter == null || adapter.isEmpty()) {
            super.onRefreshing(doScroll);
            return;
        }
        LoadingLayout origLoading;
        LoadingLayout listViewLoading;
        LoadingLayout oppositeListViewLoading;
        int selection;
        int scrollToY;
        super.onRefreshing(false);
        switch (getCurrentMode()) {
            case MANUAL_REFRESH_ONLY:
            case PULL_FROM_END:
                origLoading = getFooterLayout();
                listViewLoading = this.mFooterLoading;
                oppositeListViewLoading = this.mHeaderLoading;
                selection = ((ListView) this.mRefreshableView).getCount() - 1;
                scrollToY = getScrollY() - getFooterSize();
                break;
            default:
                origLoading = getHeaderLayout();
                listViewLoading = this.mHeaderLoading;
                oppositeListViewLoading = this.mFooterLoading;
                selection = 0;
                scrollToY = getScrollY() + getHeaderSize();
                break;
        }
        origLoading.reset();
        origLoading.hideAllViews();
        oppositeListViewLoading.setVisibility(8);
        listViewLoading.setVisibility(0);
        listViewLoading.refreshing();
        if (doScroll) {
            disableLoadingLayoutVisibilityChanges();
            setHeaderScroll(scrollToY);
            ((ListView) this.mRefreshableView).setSelection(selection);
            smoothScrollTo(0);
        }
    }

    protected void onReset() {
        boolean scrollLvToEdge = true;
        if (this.mListViewExtrasEnabled) {
            LoadingLayout originalLoadingLayout;
            LoadingLayout listViewLoadingLayout;
            int selection;
            int scrollToHeight;
            switch (getCurrentMode()) {
                case MANUAL_REFRESH_ONLY:
                case PULL_FROM_END:
                    originalLoadingLayout = getFooterLayout();
                    listViewLoadingLayout = this.mFooterLoading;
                    selection = ((ListView) this.mRefreshableView).getCount() - 1;
                    scrollToHeight = getFooterSize();
                    if (Math.abs(((ListView) this.mRefreshableView).getLastVisiblePosition() - selection) > 1) {
                        scrollLvToEdge = false;
                    }
                    break;
                default:
                    originalLoadingLayout = getHeaderLayout();
                    listViewLoadingLayout = this.mHeaderLoading;
                    scrollToHeight = -getHeaderSize();
                    selection = 0;
                    if (Math.abs(((ListView) this.mRefreshableView).getFirstVisiblePosition() - 0) > 1) {
                        scrollLvToEdge = false;
                        break;
                    }
                    break;
            }
            if (listViewLoadingLayout.getVisibility() == 0) {
                originalLoadingLayout.showInvisibleViews();
                listViewLoadingLayout.setVisibility(8);
                if (scrollLvToEdge && getState() != State.MANUAL_REFRESHING) {
                    ((ListView) this.mRefreshableView).setSelection(selection);
                    setHeaderScroll(scrollToHeight);
                }
            }
            super.onReset();
            return;
        }
        super.onReset();
    }

    protected LoadingLayoutProxy createLoadingLayoutProxy(boolean includeStart, boolean includeEnd) {
        LoadingLayoutProxy proxy = super.createLoadingLayoutProxy(includeStart, includeEnd);
        if (this.mListViewExtrasEnabled) {
            Mode mode = getMode();
            if (includeStart && mode.showHeaderLoadingLayout()) {
                proxy.addLayout(this.mHeaderLoading);
            }
            if (includeEnd && mode.showFooterLoadingLayout()) {
                proxy.addLayout(this.mFooterLoading);
            }
        }
        return proxy;
    }

    protected ListView createListView(Context context, AttributeSet attrs) {
        if (VERSION.SDK_INT >= 9) {
            return new InternalListViewSDK9(context, attrs);
        }
        return new InternalListView(context, attrs);
    }

    protected ListView createRefreshableView(Context context, AttributeSet attrs) {
        ListView lv = createListView(context, attrs);
        lv.setId(16908298);
        return lv;
    }

    protected void handleStyledAttributes(TypedArray a) {
        super.handleStyledAttributes(a);
        this.mListViewExtrasEnabled = a.getBoolean(11, true);
        if (this.mListViewExtrasEnabled) {
            LayoutParams lp = new LayoutParams(-1, -2, 1);
            FrameLayout frame = new FrameLayout(getContext());
            this.mHeaderLoading = createLoadingLayout(getContext(), Mode.PULL_FROM_START, a);
            this.mHeaderLoading.setVisibility(8);
            frame.addView(this.mHeaderLoading, lp);
            ((ListView) this.mRefreshableView).addHeaderView(frame, null, false);
            this.mLvFooterLoadingFrame = new FrameLayout(getContext());
            this.mFooterLoading = createLoadingLayout(getContext(), Mode.PULL_FROM_END, a);
            this.mFooterLoading.setVisibility(8);
            this.mLvFooterLoadingFrame.addView(this.mFooterLoading, lp);
            if (!a.hasValue(16)) {
                setScrollingWhileRefreshingEnabled(true);
            }
        }
    }
}
