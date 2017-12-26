package com.jwkj.widget.album;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

public class AlbumSwipeBackLayout extends RelativeLayout {
    private float moveY = 0.0f;
    private float oldY = 0.0f;
    private OnViewPagerDragListener onViewPagerDragListener;
    private float per = 0.0f;
    private RelativeLayout self;
    private float touchSlop;
    private ViewPager viewPager;

    class C06271 implements AnimatorUpdateListener {
        C06271() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            AlbumSwipeBackLayout.this.viewPager.setTranslationY((float) ((Integer) animation.getAnimatedValue()).intValue());
        }
    }

    class C06282 implements AnimatorUpdateListener {
        C06282() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            float value = ((Float) animation.getAnimatedValue()).floatValue();
            AlbumSwipeBackLayout.this.viewPager.setAlpha(value);
            AlbumSwipeBackLayout.this.self.setAlpha(value);
        }
    }

    class C06293 implements AnimatorUpdateListener {
        C06293() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            AlbumSwipeBackLayout.this.viewPager.setTranslationY((float) ((Integer) animation.getAnimatedValue()).intValue());
        }
    }

    class C06304 implements AnimatorUpdateListener {
        C06304() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            float value = ((Float) animation.getAnimatedValue()).floatValue();
            AlbumSwipeBackLayout.this.viewPager.setAlpha(value);
            AlbumSwipeBackLayout.this.self.setAlpha(value);
        }
    }

    class C06315 implements AnimatorListener {
        C06315() {
        }

        public void onAnimationStart(Animator animation) {
        }

        public void onAnimationRepeat(Animator animation) {
        }

        public void onAnimationEnd(Animator animation) {
            AlbumSwipeBackLayout.this.self.setVisibility(8);
            AlbumSwipeBackLayout.this.viewPager.setVisibility(0);
            AlbumSwipeBackLayout.this.self.setAlpha(1.0f);
            AlbumSwipeBackLayout.this.viewPager.setAlpha(1.0f);
            AlbumSwipeBackLayout.this.viewPager.setTranslationY(0.0f);
            AlbumSwipeBackLayout.this.self.setTranslationY(0.0f);
        }

        public void onAnimationCancel(Animator animation) {
        }
    }

    public interface OnViewPagerDragListener {
        void onRelease(float f);
    }

    public void setOnViewPagerDragListener(OnViewPagerDragListener onViewPagerDragListener) {
        this.onViewPagerDragListener = onViewPagerDragListener;
    }

    public AlbumSwipeBackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.viewPager = (ViewPager) getChildAt(0);
        this.self = this;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                this.oldY = (float) ((int) event.getY());
                break;
            case 1:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case 2:
                getParent().requestDisallowInterceptTouchEvent(true);
                if (Math.abs(((float) ((int) event.getY())) - this.oldY) > this.touchSlop) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                this.oldY = event.getY();
                break;
            case 1:
                if (this.moveY >= 100.0f) {
                    destoryView();
                    if (this.onViewPagerDragListener != null) {
                        this.onViewPagerDragListener.onRelease(this.per);
                        break;
                    }
                }
                recoverView();
                break;
                break;
            case 2:
                this.moveY = event.getY() - this.oldY;
                this.viewPager.setTranslationY(this.moveY);
                this.per = 1.0f - ((Math.abs(this.moveY) * 2.0f) / ((float) getHeight()));
                setAlpha(this.per);
                this.viewPager.setAlpha(this.per);
                Log.e("few", "per:" + this.per);
                break;
        }
        return super.onTouchEvent(event);
    }

    public void recoverView() {
        ValueAnimator transYAnim = ValueAnimator.ofInt(new int[]{(int) this.moveY, 0});
        transYAnim.setDuration(500);
        transYAnim.setTarget(this.viewPager);
        transYAnim.addUpdateListener(new C06271());
        ValueAnimator alphaAnim = ValueAnimator.ofFloat(new float[]{this.per, 1.0f});
        alphaAnim.setDuration(500);
        alphaAnim.setTarget(this.viewPager);
        alphaAnim.addUpdateListener(new C06282());
        AnimatorSet set = new AnimatorSet();
        set.playTogether(new Animator[]{transYAnim, alphaAnim});
        set.start();
    }

    public void destoryView() {
        ValueAnimator transYAnim = ValueAnimator.ofInt(new int[]{(int) this.moveY, getHeight()});
        transYAnim.setDuration(500);
        transYAnim.setTarget(this.viewPager);
        transYAnim.addUpdateListener(new C06293());
        ValueAnimator alphaAnim = ValueAnimator.ofFloat(new float[]{this.per, 0.0f});
        alphaAnim.setDuration(500);
        alphaAnim.setTarget(this.viewPager);
        alphaAnim.addUpdateListener(new C06304());
        AnimatorSet set = new AnimatorSet();
        set.playTogether(new Animator[]{transYAnim, alphaAnim});
        set.addListener(new C06315());
        set.start();
    }
}
