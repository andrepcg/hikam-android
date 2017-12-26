package com.p2p.core;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import org.apache.http.HttpStatus;

public class ViewConfiguration {
    private static final int DOUBLE_TAP_SLOP = 100;
    private static final int DOUBLE_TAP_TIMEOUT = 300;
    private static final int EDGE_SLOP = 12;
    private static final int FADING_EDGE_LENGTH = 12;
    private static final int GLOBAL_ACTIONS_KEY_TIMEOUT = 500;
    private static final int JUMP_TAP_TIMEOUT = 500;
    private static final int LONG_PRESS_TIMEOUT = 500;
    @Deprecated
    private static final int MAXIMUM_DRAWING_CACHE_SIZE = 614400;
    private static final int MAXIMUM_FLING_VELOCITY = 4000;
    private static final int MINIMUM_FLING_VELOCITY = 50;
    private static final int PAGING_TOUCH_SLOP = 32;
    private static final int PRESSED_STATE_DURATION = 125;
    private static final int SCROLL_BAR_DEFAULT_DELAY = 300;
    private static final int SCROLL_BAR_FADE_DURATION = 250;
    private static final int SCROLL_BAR_SIZE = 10;
    private static float SCROLL_FRICTION = 0.015f;
    private static final int TAP_TIMEOUT = 115;
    private static final int TOUCH_SLOP = 16;
    private static final int WINDOW_TOUCH_SLOP = 16;
    private static final int ZOOM_CONTROLS_TIMEOUT = 3000;
    private static final SparseArray<ViewConfiguration> sConfigurations = new SparseArray(2);
    private final int mDoubleTapSlop;
    private final int mEdgeSlop;
    private final int mFadingEdgeLength;
    private final int mMaximumDrawingCacheSize;
    private final int mMaximumFlingVelocity;
    private final int mMinimumFlingVelocity;
    private final int mPagingTouchSlop;
    private final int mScrollbarSize;
    private final int mTouchSlop;
    private final int mWindowTouchSlop;

    @Deprecated
    public ViewConfiguration() {
        this.mEdgeSlop = 12;
        this.mFadingEdgeLength = 12;
        this.mMinimumFlingVelocity = 50;
        this.mMaximumFlingVelocity = MAXIMUM_FLING_VELOCITY;
        this.mScrollbarSize = 10;
        this.mTouchSlop = 16;
        this.mPagingTouchSlop = 32;
        this.mDoubleTapSlop = 100;
        this.mWindowTouchSlop = 16;
        this.mMaximumDrawingCacheSize = MAXIMUM_DRAWING_CACHE_SIZE;
    }

    private ViewConfiguration(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float density = metrics.density;
        this.mEdgeSlop = (int) ((density * 12.0f) + 0.5f);
        this.mFadingEdgeLength = (int) ((density * 12.0f) + 0.5f);
        this.mMinimumFlingVelocity = (int) ((50.0f * density) + 0.5f);
        this.mMaximumFlingVelocity = (int) ((4000.0f * density) + 0.5f);
        this.mScrollbarSize = (int) ((10.0f * density) + 0.5f);
        this.mTouchSlop = (int) ((density * 16.0f) + 0.5f);
        this.mPagingTouchSlop = (int) ((32.0f * density) + 0.5f);
        this.mDoubleTapSlop = (int) ((100.0f * density) + 0.5f);
        this.mWindowTouchSlop = (int) ((density * 16.0f) + 0.5f);
        this.mMaximumDrawingCacheSize = (metrics.widthPixels * 4) * metrics.heightPixels;
    }

    public static ViewConfiguration get(Context context) {
        int density = (int) (100.0f * context.getResources().getDisplayMetrics().density);
        ViewConfiguration configuration = (ViewConfiguration) sConfigurations.get(density);
        if (configuration != null) {
            return configuration;
        }
        configuration = new ViewConfiguration(context);
        sConfigurations.put(density, configuration);
        return configuration;
    }

    @Deprecated
    public static int getScrollBarSize() {
        return 10;
    }

    public int getScaledScrollBarSize() {
        return this.mScrollbarSize;
    }

    public static int getScrollBarFadeDuration() {
        return SCROLL_BAR_FADE_DURATION;
    }

    public static int getScrollDefaultDelay() {
        return HttpStatus.SC_MULTIPLE_CHOICES;
    }

    @Deprecated
    public static int getFadingEdgeLength() {
        return 12;
    }

    public int getScaledFadingEdgeLength() {
        return this.mFadingEdgeLength;
    }

    public static int getPressedStateDuration() {
        return PRESSED_STATE_DURATION;
    }

    public static int getLongPressTimeout() {
        return 500;
    }

    public static int getTapTimeout() {
        return TAP_TIMEOUT;
    }

    public static int getJumpTapTimeout() {
        return 500;
    }

    public static int getDoubleTapTimeout() {
        return HttpStatus.SC_MULTIPLE_CHOICES;
    }

    @Deprecated
    public static int getEdgeSlop() {
        return 12;
    }

    public int getScaledEdgeSlop() {
        return this.mEdgeSlop;
    }

    @Deprecated
    public static int getTouchSlop() {
        return 16;
    }

    public int getScaledTouchSlop() {
        return this.mTouchSlop;
    }

    public int getScaledPagingTouchSlop() {
        return this.mPagingTouchSlop;
    }

    @Deprecated
    public static int getDoubleTapSlop() {
        return 100;
    }

    public int getScaledDoubleTapSlop() {
        return this.mDoubleTapSlop;
    }

    @Deprecated
    public static int getWindowTouchSlop() {
        return 16;
    }

    public int getScaledWindowTouchSlop() {
        return this.mWindowTouchSlop;
    }

    @Deprecated
    public static int getMinimumFlingVelocity() {
        return 50;
    }

    public int getScaledMinimumFlingVelocity() {
        return this.mMinimumFlingVelocity;
    }

    @Deprecated
    public static int getMaximumFlingVelocity() {
        return MAXIMUM_FLING_VELOCITY;
    }

    public int getScaledMaximumFlingVelocity() {
        return this.mMaximumFlingVelocity;
    }

    @Deprecated
    public static int getMaximumDrawingCacheSize() {
        return MAXIMUM_DRAWING_CACHE_SIZE;
    }

    public int getScaledMaximumDrawingCacheSize() {
        return this.mMaximumDrawingCacheSize;
    }

    public static long getZoomControlsTimeout() {
        return 3000;
    }

    public static long getGlobalActionKeyTimeout() {
        return 500;
    }

    public static float getScrollFriction() {
        return SCROLL_FRICTION;
    }
}
