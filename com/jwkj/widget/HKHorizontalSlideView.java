package com.jwkj.widget;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class HKHorizontalSlideView extends ViewGroup {
    private View mContent;
    private int mContentHeight;
    private int mContentWidth;
    private View mDelete;
    private int mDeleteHeight;
    private int mDeleteWidth;
    private float moveX = 0.0f;
    private float oldX;
    private OnSlideDeleteListener onSlideDeleteListener;
    private float touchSlop;
    private ViewDragHelper viewDragHelper;

    public interface OnSlideDeleteListener {
        void onClose(HKHorizontalSlideView hKHorizontalSlideView);

        void onOpen(HKHorizontalSlideView hKHorizontalSlideView);
    }

    class MyDrawHelper extends Callback {
        MyDrawHelper() {
        }

        public boolean tryCaptureView(View child, int pointerId) {
            return HKHorizontalSlideView.this.mContent == child || HKHorizontalSlideView.this.mDelete == child;
        }

        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == HKHorizontalSlideView.this.mContent) {
                if (left > 0) {
                    return 0;
                }
                if ((-left) > HKHorizontalSlideView.this.mDeleteWidth) {
                    return -HKHorizontalSlideView.this.mDeleteWidth;
                }
            }
            if (child != HKHorizontalSlideView.this.mDelete) {
                return left;
            }
            if (left < HKHorizontalSlideView.this.mContentWidth - HKHorizontalSlideView.this.mDeleteWidth) {
                return HKHorizontalSlideView.this.mContentWidth - HKHorizontalSlideView.this.mDeleteWidth;
            }
            if (left > HKHorizontalSlideView.this.mContentWidth) {
                return HKHorizontalSlideView.this.mContentWidth;
            }
            return left;
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            return super.clampViewPositionVertical(child, top, dy);
        }

        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            HKHorizontalSlideView.this.invalidate();
            if (changedView == HKHorizontalSlideView.this.mContent) {
                HKHorizontalSlideView.this.mDelete.layout(HKHorizontalSlideView.this.mContentWidth + left, 0, (HKHorizontalSlideView.this.mContentWidth + left) + HKHorizontalSlideView.this.mDeleteWidth, HKHorizontalSlideView.this.mDeleteHeight);
                return;
            }
            HKHorizontalSlideView.this.mContent.layout(left - HKHorizontalSlideView.this.mContentWidth, 0, left, HKHorizontalSlideView.this.mContentHeight);
        }

        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if ((-HKHorizontalSlideView.this.mContent.getLeft()) > HKHorizontalSlideView.this.mDeleteWidth / 2) {
                HKHorizontalSlideView.this.isShowDelete(true);
                if (HKHorizontalSlideView.this.onSlideDeleteListener != null) {
                    HKHorizontalSlideView.this.onSlideDeleteListener.onOpen(HKHorizontalSlideView.this);
                }
            } else {
                HKHorizontalSlideView.this.isShowDelete(false);
                if (HKHorizontalSlideView.this.onSlideDeleteListener != null) {
                    HKHorizontalSlideView.this.onSlideDeleteListener.onClose(HKHorizontalSlideView.this);
                }
            }
            super.onViewReleased(releasedChild, xvel, yvel);
        }
    }

    public HKHorizontalSlideView(Context context) {
        super(context);
        this.touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public HKHorizontalSlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public HKHorizontalSlideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setOnSlideDeleteListener(OnSlideDeleteListener onSlideDeleteListener) {
        this.onSlideDeleteListener = onSlideDeleteListener;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mContent = getChildAt(0);
        this.mDelete = getChildAt(1);
        this.viewDragHelper = ViewDragHelper.create(this, new MyDrawHelper());
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.mContent.measure(widthMeasureSpec, heightMeasureSpec);
        LayoutParams layoutParams = this.mDelete.getLayoutParams();
        this.mDelete.measure(MeasureSpec.makeMeasureSpec(layoutParams.width, 1073741824), MeasureSpec.makeMeasureSpec(layoutParams.height, 1073741824));
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.mContentWidth = this.mContent.getMeasuredWidth();
        this.mContentHeight = this.mContent.getMeasuredHeight();
        this.mContent.layout(0, 0, this.mContentWidth, this.mContentHeight);
        this.mDeleteWidth = this.mDelete.getMeasuredWidth();
        this.mDeleteHeight = this.mDelete.getMeasuredHeight();
        this.mDelete.layout(this.mContentWidth, 0, this.mContentWidth + this.mDeleteWidth, this.mContentHeight);
    }

    public void isShowDelete(boolean isShowDelete) {
        if (isShowDelete) {
            this.viewDragHelper.smoothSlideViewTo(this.mContent, -this.mDeleteWidth, 0);
            this.viewDragHelper.smoothSlideViewTo(this.mDelete, this.mContentWidth - this.mDeleteWidth, 0);
        } else {
            this.viewDragHelper.smoothSlideViewTo(this.mContent, 0, 0);
            this.viewDragHelper.smoothSlideViewTo(this.mDelete, this.mContentWidth, 0);
        }
        invalidate();
    }

    public void computeScroll() {
        if (this.viewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        this.viewDragHelper.processTouchEvent(event);
        return true;
    }
}
