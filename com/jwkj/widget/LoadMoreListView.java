package com.jwkj.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.hikam.C0291R;

public class LoadMoreListView extends ListView implements OnScrollListener {
    private static final int STATE_CLOSE = 2;
    private static final int STATE_FINISH = 1;
    private static final int STATE_LOADING = 0;
    private int LOAD_TIMEOUT = 5000;
    private int current_state = 1;
    private RelativeLayout fv_loadclose;
    private RelativeLayout fv_loadstart;
    private Handler handler;
    private OnLoadMoreListener onLoadMoreListener;
    private RelativeLayout rl_footview;

    class C05861 implements Runnable {
        C05861() {
        }

        public void run() {
            if (LoadMoreListView.this.current_state == 0) {
                LoadMoreListView.this.loadMoreFinish();
            }
        }
    }

    public interface OnLoadMoreListener {
        void onLoadFinish();

        void onLoadStart();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.rl_footview = (RelativeLayout) LayoutInflater.from(context).inflate(C0291R.layout.widget_footview, null);
        addFooterView(this.rl_footview);
        this.fv_loadstart = (RelativeLayout) this.rl_footview.getChildAt(0);
        this.fv_loadclose = (RelativeLayout) this.rl_footview.getChildAt(1);
        this.handler = new Handler();
        setOnScrollListener(this);
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount > visibleItemCount && this.current_state == 1 && firstVisibleItem >= totalItemCount - visibleItemCount) {
            loadMoreStart();
        }
    }

    public void setTimeOut(int second) throws Exception {
        if (this.current_state == 1) {
            this.LOAD_TIMEOUT = second * 1000;
            return;
        }
        throw new Exception("不要在加载的过程中改变超时时间！");
    }

    public void loadMoreStart() {
        this.fv_loadstart.setVisibility(0);
        this.fv_loadclose.setVisibility(8);
        this.current_state = 0;
        if (this.onLoadMoreListener != null) {
            this.onLoadMoreListener.onLoadStart();
        }
        this.handler.postDelayed(new C05861(), (long) this.LOAD_TIMEOUT);
    }

    public void loadMoreFinish() {
        this.fv_loadstart.setVisibility(8);
        this.fv_loadclose.setVisibility(8);
        this.current_state = 1;
        this.handler.removeCallbacksAndMessages(null);
    }

    public void loadClose() {
        this.fv_loadstart.setVisibility(8);
        this.fv_loadclose.setVisibility(0);
        this.current_state = 2;
        this.handler.removeCallbacksAndMessages(null);
    }

    public void loadOpen() {
        this.fv_loadclose.setVisibility(8);
        this.current_state = 1;
    }

    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    public Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
