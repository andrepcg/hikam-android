package com.jwkj.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

public class CompatLinearLayout extends LinearLayout {
    private boolean isNeedToScroll = false;
    private float moveX = 0.0f;
    private float oldX;
    private float touchSlop;

    public CompatLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        return super.onInterceptTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
