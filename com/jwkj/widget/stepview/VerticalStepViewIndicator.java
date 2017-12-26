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
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import com.hikam.C0291R;
import java.util.ArrayList;
import java.util.List;

public class VerticalStepViewIndicator extends View {
    private final String TAG_NAME;
    private int defaultStepIndicatorNum;
    private Drawable mAttentionIcon;
    private float mCenterX;
    private List<Float> mCircleCenterPointPositionList;
    private float mCircleRadius;
    private int mComplectingPosition;
    private Drawable mCompleteIcon;
    private int mCompletedLineColor;
    private float mCompletedLineHeight;
    private Paint mCompletedPaint;
    private Drawable mDefaultIcon;
    private PathEffect mEffects;
    private int mHeight;
    private boolean mIsReverseDraw;
    private float mLeftY;
    private float mLinePadding;
    private OnDrawIndicatorListener mOnDrawListener;
    private Path mPath;
    private Rect mRect;
    private float mRightY;
    private int mStepNum;
    private int mUnCompletedLineColor;
    private Paint mUnCompletedPaint;

    public interface OnDrawIndicatorListener {
        void ondrawIndicator();
    }

    public void setOnDrawListener(OnDrawIndicatorListener onDrawListener) {
        this.mOnDrawListener = onDrawListener;
    }

    public float getCircleRadius() {
        return this.mCircleRadius;
    }

    public VerticalStepViewIndicator(Context context) {
        this(context, null);
    }

    public VerticalStepViewIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalStepViewIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.TAG_NAME = getClass().getSimpleName();
        this.defaultStepIndicatorNum = (int) TypedValue.applyDimension(1, 40.0f, getResources().getDisplayMetrics());
        this.mStepNum = 0;
        this.mUnCompletedLineColor = ContextCompat.getColor(getContext(), C0291R.color.uncompleted_color);
        this.mCompletedLineColor = -1;
        init();
    }

    private void init() {
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
        this.mIsReverseDraw = true;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(this.TAG_NAME, "onMeasure");
        int width = this.defaultStepIndicatorNum;
        this.mHeight = 0;
        if (this.mStepNum > 0) {
            this.mHeight = (int) ((((float) (getPaddingTop() + getPaddingBottom())) + ((this.mCircleRadius * 2.0f) * ((float) this.mStepNum))) + (((float) (this.mStepNum - 1)) * this.mLinePadding));
        }
        if (MeasureSpec.getMode(widthMeasureSpec) != 0) {
            width = Math.min(width, MeasureSpec.getSize(widthMeasureSpec));
        }
        setMeasuredDimension(width, this.mHeight);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i(this.TAG_NAME, "onSizeChanged");
        this.mCenterX = (float) (getWidth() / 2);
        this.mLeftY = this.mCenterX - (this.mCompletedLineHeight / 2.0f);
        this.mRightY = this.mCenterX + (this.mCompletedLineHeight / 2.0f);
        for (int i = 0; i < this.mStepNum; i++) {
            if (this.mIsReverseDraw) {
                this.mCircleCenterPointPositionList.add(Float.valueOf(((float) this.mHeight) - ((this.mCircleRadius + ((((float) i) * this.mCircleRadius) * 2.0f)) + (((float) i) * this.mLinePadding))));
            } else {
                this.mCircleCenterPointPositionList.add(Float.valueOf((this.mCircleRadius + ((((float) i) * this.mCircleRadius) * 2.0f)) + (((float) i) * this.mLinePadding)));
            }
        }
        if (this.mOnDrawListener != null) {
            this.mOnDrawListener.ondrawIndicator();
        }
    }

    protected void onDraw(Canvas canvas) {
        int i;
        super.onDraw(canvas);
        Log.i(this.TAG_NAME, "onDraw");
        if (this.mOnDrawListener != null) {
            this.mOnDrawListener.ondrawIndicator();
        }
        this.mUnCompletedPaint.setColor(this.mUnCompletedLineColor);
        this.mCompletedPaint.setColor(this.mCompletedLineColor);
        for (i = 0; i < this.mCircleCenterPointPositionList.size() - 1; i++) {
            float preComplectedXPosition = ((Float) this.mCircleCenterPointPositionList.get(i)).floatValue();
            float afterComplectedXPosition = ((Float) this.mCircleCenterPointPositionList.get(i + 1)).floatValue();
            if (i < this.mComplectingPosition) {
                if (this.mIsReverseDraw) {
                    canvas.drawRect(this.mLeftY, (this.mCircleRadius + afterComplectedXPosition) - 10.0f, this.mRightY, (preComplectedXPosition - this.mCircleRadius) + 10.0f, this.mCompletedPaint);
                } else {
                    canvas.drawRect(this.mLeftY, (this.mCircleRadius + preComplectedXPosition) - 10.0f, this.mRightY, (afterComplectedXPosition - this.mCircleRadius) + 10.0f, this.mCompletedPaint);
                }
            } else if (this.mIsReverseDraw) {
                this.mPath.moveTo(this.mCenterX, this.mCircleRadius + afterComplectedXPosition);
                this.mPath.lineTo(this.mCenterX, preComplectedXPosition - this.mCircleRadius);
                canvas.drawPath(this.mPath, this.mUnCompletedPaint);
            } else {
                this.mPath.moveTo(this.mCenterX, this.mCircleRadius + preComplectedXPosition);
                this.mPath.lineTo(this.mCenterX, afterComplectedXPosition - this.mCircleRadius);
                canvas.drawPath(this.mPath, this.mUnCompletedPaint);
            }
        }
        for (i = 0; i < this.mCircleCenterPointPositionList.size(); i++) {
            float currentComplectedXPosition = ((Float) this.mCircleCenterPointPositionList.get(i)).floatValue();
            this.mRect = new Rect((int) (this.mCenterX - this.mCircleRadius), (int) (currentComplectedXPosition - this.mCircleRadius), (int) (this.mCenterX + this.mCircleRadius), (int) (this.mCircleRadius + currentComplectedXPosition));
            if (i < this.mComplectingPosition) {
                this.mCompleteIcon.setBounds(this.mRect);
                this.mCompleteIcon.draw(canvas);
            } else if (i != this.mComplectingPosition || this.mCircleCenterPointPositionList.size() == 1) {
                this.mDefaultIcon.setBounds(this.mRect);
                this.mDefaultIcon.draw(canvas);
            } else {
                this.mCompletedPaint.setColor(-1);
                canvas.drawCircle(this.mCenterX, currentComplectedXPosition, this.mCircleRadius * 1.1f, this.mCompletedPaint);
                this.mAttentionIcon.setBounds(this.mRect);
                this.mAttentionIcon.draw(canvas);
            }
        }
    }

    public List<Float> getCircleCenterPointPositionList() {
        return this.mCircleCenterPointPositionList;
    }

    public void setStepNum(int stepNum) {
        this.mStepNum = stepNum;
        requestLayout();
    }

    public void setIndicatorLinePaddingProportion(float linePaddingProportion) {
        this.mLinePadding = ((float) this.defaultStepIndicatorNum) * linePaddingProportion;
    }

    public void setComplectingPosition(int complectingPosition) {
        this.mComplectingPosition = complectingPosition;
        requestLayout();
    }

    public void setUnCompletedLineColor(int unCompletedLineColor) {
        this.mUnCompletedLineColor = unCompletedLineColor;
    }

    public void setCompletedLineColor(int completedLineColor) {
        this.mCompletedLineColor = completedLineColor;
    }

    public void reverseDraw(boolean isReverseDraw) {
        this.mIsReverseDraw = isReverseDraw;
        invalidate();
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
