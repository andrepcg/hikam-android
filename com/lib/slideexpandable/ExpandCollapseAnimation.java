package com.lib.slideexpandable;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;

public class ExpandCollapseAnimation extends Animation {
    public static final int COLLAPSE = 1;
    public static final int EXPAND = 0;
    private View mAnimatedView;
    private int mEndHeight = this.mAnimatedView.getMeasuredHeight();
    private LayoutParams mLayoutParams;
    private int mType;

    public ExpandCollapseAnimation(View view, int type) {
        this.mAnimatedView = view;
        this.mLayoutParams = (LayoutParams) view.getLayoutParams();
        this.mType = type;
        if (this.mType == 0) {
            this.mLayoutParams.bottomMargin = -this.mEndHeight;
        } else {
            this.mLayoutParams.bottomMargin = 0;
        }
        view.setVisibility(0);
    }

    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (interpolatedTime < 1.0f) {
            if (this.mType == 0) {
                this.mLayoutParams.bottomMargin = (-this.mEndHeight) + ((int) (((float) this.mEndHeight) * interpolatedTime));
            } else {
                this.mLayoutParams.bottomMargin = -((int) (((float) this.mEndHeight) * interpolatedTime));
            }
            Log.d("ExpandCollapseAnimation", "anim height " + this.mLayoutParams.bottomMargin);
            this.mAnimatedView.requestLayout();
        } else if (this.mType == 0) {
            this.mLayoutParams.bottomMargin = 0;
            this.mAnimatedView.requestLayout();
        } else {
            this.mLayoutParams.bottomMargin = -this.mEndHeight;
            this.mAnimatedView.setVisibility(8);
            this.mAnimatedView.requestLayout();
        }
    }
}
