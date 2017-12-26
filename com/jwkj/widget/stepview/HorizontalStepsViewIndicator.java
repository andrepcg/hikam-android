package com.jwkj.widget.stepview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import com.hikam.C0291R;
import java.util.ArrayList;
import java.util.List;

public class HorizontalStepsViewIndicator extends View {
    private int defaultStepIndicatorNum;
    private Drawable mAttentionIcon;
    private float mCenterY;
    private List<Float> mCircleCenterPointPositionList;
    private float mCircleRadius;
    private int mComplectingPosition;
    private Drawable mCompleteIcon;
    private int mCompletedLineColor;
    private float mCompletedLineHeight;
    private Paint mCompletedPaint;
    private Drawable mDefaultIcon;
    private PathEffect mEffects;
    private float mLeftY;
    private float mLinePadding;
    private OnDrawIndicatorListener mOnDrawListener;
    private Path mPath;
    private float mRightY;
    private List<StepBean> mStepBeanList;
    private int mStepNum;
    private int mUnCompletedLineColor;
    private Paint mUnCompletedPaint;
    private int screenWidth;

    public interface OnDrawIndicatorListener {
        void ondrawIndicator();
    }

    public void setOnDrawListener(OnDrawIndicatorListener onDrawListener) {
        this.mOnDrawListener = onDrawListener;
    }

    public float getCircleRadius() {
        return this.mCircleRadius;
    }

    public HorizontalStepsViewIndicator(Context context) {
        this(context, null);
    }

    public HorizontalStepsViewIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalStepsViewIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.defaultStepIndicatorNum = (int) TypedValue.applyDimension(1, 40.0f, getResources().getDisplayMetrics());
        this.mStepNum = 0;
        this.mUnCompletedLineColor = ContextCompat.getColor(getContext(), C0291R.color.uncompleted_color);
        this.mCompletedLineColor = -1;
        init();
    }

    private void init() {
        this.mStepBeanList = new ArrayList();
        this.mPath = new Path();
        this.mEffects = new DashPathEffect(new float[]{8.0f, 8.0f, 8.0f, 8.0f}, 1.0f);
        this.mCircleCenterPointPositionList = new ArrayList();
        this.mUnCompletedPaint = new Paint();
        this.mCompletedPaint = new Paint();
        this.mUnCompletedPaint.setAntiAlias(true);
        this.mUnCompletedPaint.setColor(this.mUnCompletedLineColor);
        this.mUnCompletedPaint.setStyle(Style.STROKE);
        this.mUnCompletedPaint.setStrokeWidth(2.0f);
        this.mCompletedPaint.setAntiAlias(true);
        this.mCompletedPaint.setColor(this.mCompletedLineColor);
        this.mCompletedPaint.setStyle(Style.STROKE);
        this.mCompletedPaint.setStrokeWidth(2.0f);
        this.mUnCompletedPaint.setPathEffect(this.mEffects);
        this.mCompletedPaint.setStyle(Style.FILL);
        this.mCompletedLineHeight = 0.05f * ((float) this.defaultStepIndicatorNum);
        this.mCircleRadius = 0.28f * ((float) this.defaultStepIndicatorNum);
        this.mLinePadding = 0.85f * ((float) this.defaultStepIndicatorNum);
        this.mCompleteIcon = ContextCompat.getDrawable(getContext(), C0291R.drawable.complted);
        this.mAttentionIcon = ContextCompat.getDrawable(getContext(), C0291R.drawable.attention);
        this.mDefaultIcon = ContextCompat.getDrawable(getContext(), C0291R.drawable.default_icon);
    }

    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = this.defaultStepIndicatorNum * 2;
        if (MeasureSpec.getMode(widthMeasureSpec) != 0) {
            this.screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        int height = this.defaultStepIndicatorNum;
        if (MeasureSpec.getMode(heightMeasureSpec) != 0) {
            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
        }
        setMeasuredDimension((int) (((((float) this.mStepNum) * this.mCircleRadius) * 2.0f) - (((float) (this.mStepNum - 1)) * this.mLinePadding)), height);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mCenterY = 0.5f * ((float) getHeight());
        this.mLeftY = this.mCenterY - (this.mCompletedLineHeight / 2.0f);
        this.mRightY = this.mCenterY + (this.mCompletedLineHeight / 2.0f);
        this.mCircleCenterPointPositionList.clear();
        for (int i = 0; i < this.mStepNum; i++) {
            this.mCircleCenterPointPositionList.add(Float.valueOf(((this.mCircleRadius + (((((float) this.screenWidth) - ((((float) this.mStepNum) * this.mCircleRadius) * 2.0f)) - (((float) (this.mStepNum - 1)) * this.mLinePadding)) / 2.0f)) + ((((float) i) * this.mCircleRadius) * 2.0f)) + (((float) i) * this.mLinePadding)));
        }
        if (this.mOnDrawListener != null) {
            this.mOnDrawListener.ondrawIndicator();
        }
    }

    protected synchronized void onDraw(Canvas canvas) {
        int i;
        super.onDraw(canvas);
        if (this.mOnDrawListener != null) {
            this.mOnDrawListener.ondrawIndicator();
        }
        this.mUnCompletedPaint.setColor(this.mUnCompletedLineColor);
        this.mCompletedPaint.setColor(this.mCompletedLineColor);
        for (i = 0; i < this.mCircleCenterPointPositionList.size() - 1; i++) {
            float preComplectedXPosition = ((Float) this.mCircleCenterPointPositionList.get(i)).floatValue();
            float afterComplectedXPosition = ((Float) this.mCircleCenterPointPositionList.get(i + 1)).floatValue();
            if (i > this.mComplectingPosition || ((StepBean) this.mStepBeanList.get(0)).getState() == -1) {
                this.mPath.moveTo(this.mCircleRadius + preComplectedXPosition, this.mCenterY);
                this.mPath.lineTo(afterComplectedXPosition - this.mCircleRadius, this.mCenterY);
                canvas.drawPath(this.mPath, this.mUnCompletedPaint);
            } else {
                canvas.drawRect((this.mCircleRadius + preComplectedXPosition) - 10.0f, this.mLeftY, (afterComplectedXPosition - this.mCircleRadius) + 10.0f, this.mRightY, this.mCompletedPaint);
            }
        }
        for (i = 0; i < this.mCircleCenterPointPositionList.size(); i++) {
            float currentComplectedXPosition = ((Float) this.mCircleCenterPointPositionList.get(i)).floatValue();
            Rect rect = new Rect((int) (currentComplectedXPosition - this.mCircleRadius), (int) (this.mCenterY - this.mCircleRadius), (int) (this.mCircleRadius + currentComplectedXPosition), (int) (this.mCenterY + this.mCircleRadius));
            StepBean stepsBean = (StepBean) this.mStepBeanList.get(i);
            if (stepsBean.getState() == -1) {
                this.mDefaultIcon.setBounds(rect);
                this.mDefaultIcon.draw(canvas);
            } else if (stepsBean.getState() == 0) {
                this.mCompletedPaint.setColor(-1);
                canvas.drawCircle(currentComplectedXPosition, this.mCenterY, this.mCircleRadius * 1.1f, this.mCompletedPaint);
                this.mAttentionIcon.setBounds(rect);
                this.mAttentionIcon.draw(canvas);
            } else if (stepsBean.getState() == 1) {
                this.mCompleteIcon.setBounds(rect);
                this.mCompleteIcon.draw(canvas);
            }
        }
    }

    public List<Float> getCircleCenterPointPositionList() {
        return this.mCircleCenterPointPositionList;
    }

    public void setStepNum(List<StepBean> stepsBeanList) {
        this.mStepBeanList = stepsBeanList;
        this.mStepNum = this.mStepBeanList.size();
        if (this.mStepBeanList != null && this.mStepBeanList.size() > 0) {
            for (int i = 0; i < this.mStepNum; i++) {
                if (((StepBean) this.mStepBeanList.get(i)).getState() == 1) {
                    this.mComplectingPosition = i;
                }
            }
        }
        requestLayout();
    }

    public void setNext(List<StepBean> stepsBeanList) {
        setStepNum(stepsBeanList);
        invalidate();
    }

    public void setUnCompletedLineColor(int unCompletedLineColor) {
        this.mUnCompletedLineColor = unCompletedLineColor;
    }

    public void setCompletedLineColor(int completedLineColor) {
        this.mCompletedLineColor = completedLineColor;
    }

    public void setDefaultIcon(Drawable defaultIcon) {
        this.mDefaultIcon = defaultIcon;
    }

    public void setCompleteIcon(Drawable completeIcon) {
        this.mCompleteIcon = completeIcon;
    }

    public void setAttentionIcon(Drawable attentionIcon) {
        this.mAttentionIcon = attentionIcon;
    }
}
