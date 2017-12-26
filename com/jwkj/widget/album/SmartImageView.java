package com.jwkj.widget.album;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class SmartImageView extends ImageView implements OnGlobalLayoutListener, OnScaleGestureListener, OnTouchListener {
    private boolean isAutoScale;
    private boolean isCanDrag;
    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;
    private GestureDetector mGestureDetector;
    private float mInitScale;
    private int mLastPointCount;
    private float mLastX;
    private float mLastY;
    private Matrix mMatrix;
    private float mMaxScale;
    private float mMidScale;
    private boolean mOnce;
    private ScaleGestureDetector mScaleGestureDetector;
    private int mTouchSlop;

    class C06321 extends SimpleOnGestureListener {
        C06321() {
        }

        public boolean onDoubleTap(MotionEvent e) {
            if (!SmartImageView.this.isAutoScale) {
                if (SmartImageView.this.getScale() < SmartImageView.this.mMidScale) {
                    SmartImageView.this.postDelayed(new AutoScaleRunnable(SmartImageView.this.mMidScale, e.getX(), e.getY()), 16);
                    SmartImageView.this.isAutoScale = true;
                } else {
                    SmartImageView.this.postDelayed(new AutoScaleRunnable(SmartImageView.this.mInitScale, e.getX(), e.getY()), 16);
                    SmartImageView.this.isAutoScale = true;
                }
            }
            return true;
        }
    }

    class AutoScaleRunnable implements Runnable {
        private final float BIGGER = 1.07f;
        private final float SMALL = 0.93f;
        private float targetScale;
        private float tempScale;
        private float f24x;
        private float f25y;

        public AutoScaleRunnable(float targetScale, float x, float y) {
            this.targetScale = targetScale;
            this.f24x = x;
            this.f25y = y;
            if (SmartImageView.this.getScale() < targetScale) {
                this.tempScale = 1.07f;
            }
            if (SmartImageView.this.getScale() > targetScale) {
                this.tempScale = 0.93f;
            }
        }

        public void run() {
            SmartImageView.this.mMatrix.postScale(this.tempScale, this.tempScale, this.f24x, this.f25y);
            SmartImageView.this.checkBorderAndCenterWhenScale();
            SmartImageView.this.setImageMatrix(SmartImageView.this.mMatrix);
            float currentScale = SmartImageView.this.getScale();
            if ((currentScale >= this.targetScale || this.tempScale <= 1.0f) && (currentScale <= this.targetScale || this.tempScale >= 1.0f)) {
                SmartImageView.this.mMatrix.postScale(this.targetScale / currentScale, this.targetScale / currentScale, this.f24x, this.f25y);
                SmartImageView.this.checkBorderAndCenterWhenScale();
                SmartImageView.this.setImageMatrix(SmartImageView.this.mMatrix);
                SmartImageView.this.isAutoScale = false;
                return;
            }
            SmartImageView.this.postDelayed(this, 15);
        }
    }

    public SmartImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);
        setOnTouchListener(this);
        this.mScaleGestureDetector = new ScaleGestureDetector(context, this);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.isCheckTopAndBottom = true;
        this.isCheckLeftAndRight = true;
        this.mGestureDetector = new GestureDetector(context, new C06321());
    }

    public SmartImageView(Context context) {
        this(context, null);
    }

    public SmartImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    public void onGlobalLayout() {
        if (!this.mOnce) {
            int width = getWidth();
            int height = getHeight();
            Drawable drawable = getDrawable();
            int dw = drawable.getIntrinsicWidth();
            int dh = drawable.getIntrinsicHeight();
            float scale = 1.0f;
            if (dw > width && dh < height) {
                scale = (((float) width) * 1.0f) / ((float) dw);
            }
            if (dw < width && dh > height) {
                scale = (((float) height) * 1.0f) / ((float) dh);
            }
            if ((dw > width && dh > height) || (dw < width && dh < height)) {
                scale = Math.min((((float) width) * 1.0f) / ((float) dw), (((float) height) * 1.0f) / ((float) dh));
            }
            this.mInitScale = scale;
            this.mMidScale = 3.0f * scale;
            this.mMaxScale = 5.0f * scale;
            this.mMatrix.postTranslate((float) ((width / 2) - (dw / 2)), (float) ((height / 2) - (dh / 2)));
            this.mMatrix.postScale(scale, scale, (float) (width / 2), (float) (height / 2));
            setImageMatrix(this.mMatrix);
            this.mOnce = true;
        }
    }

    float getScale() {
        float[] values = new float[9];
        this.mMatrix.getValues(values);
        return values[0];
    }

    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();
        if (getDrawable() == null) {
            return true;
        }
        if ((scale < this.mMaxScale && scaleFactor > 1.0f) || (scale > this.mInitScale && scaleFactor < 1.0f)) {
            if (scale * scaleFactor > this.mMaxScale) {
                scaleFactor = this.mMaxScale / scale;
            }
            if (scale * scaleFactor < this.mInitScale) {
                scaleFactor = this.mInitScale / scale;
            }
            this.mMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            checkBorderAndCenterWhenScale();
            setImageMatrix(this.mMatrix);
        }
        return false;
    }

    private void checkBorderAndCenterWhenScale() {
        RectF rect = getMatrixRectf();
        float deltaX = 0.0f;
        float deltaY = 0.0f;
        int width = getWidth();
        int height = getHeight();
        if (rect.width() >= ((float) width)) {
            if (rect.left > 0.0f) {
                deltaX = -rect.left;
            }
            if (rect.right < ((float) width)) {
                deltaX = ((float) width) - rect.right;
            }
        }
        if (rect.height() >= ((float) height)) {
            if (rect.top > 0.0f) {
                deltaY = -rect.top;
            }
            if (rect.bottom < ((float) height)) {
                deltaY = ((float) height) - rect.bottom;
            }
        }
        if (rect.width() < ((float) width)) {
            deltaX = ((((float) width) / 2.0f) - rect.right) + (rect.width() / 2.0f);
        }
        if (rect.height() < ((float) height)) {
            deltaY = ((((float) height) / 2.0f) - rect.bottom) + (rect.height() / 2.0f);
        }
        this.mMatrix.postTranslate(deltaX, deltaY);
    }

    private void checkBorderWhenTranslate() {
        RectF rect = getMatrixRectf();
        float deltaX = 0.0f;
        float deltaY = 0.0f;
        int width = getWidth();
        int height = getHeight();
        if (rect.left > 0.0f && this.isCheckLeftAndRight) {
            deltaX = -rect.left;
        }
        if (rect.right < ((float) width) && this.isCheckLeftAndRight) {
            deltaX = ((float) width) - rect.right;
        }
        if (rect.top > 0.0f && this.isCheckTopAndBottom) {
            deltaY = -rect.top;
        }
        if (rect.bottom < ((float) height) && this.isCheckTopAndBottom) {
            deltaY = ((float) height) - rect.bottom;
        }
        this.mMatrix.postTranslate(deltaX, deltaY);
    }

    private RectF getMatrixRectf() {
        Matrix matrix = this.mMatrix;
        Drawable d = getDrawable();
        RectF rectF = new RectF();
        if (d != null) {
            rectF.set(0.0f, 0.0f, (float) d.getIntrinsicWidth(), (float) d.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }
        return rectF;
    }

    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (!this.mGestureDetector.onTouchEvent(event)) {
            this.mScaleGestureDetector.onTouchEvent(event);
            float x = 0.0f;
            float y = 0.0f;
            int pointerCount = event.getPointerCount();
            for (int i = 0; i < pointerCount; i++) {
                x += event.getX(i);
                y += event.getY(i);
            }
            x /= (float) pointerCount;
            y /= (float) pointerCount;
            if (this.mLastPointCount != pointerCount) {
                this.isCanDrag = false;
                this.mLastX = x;
                this.mLastY = y;
            }
            this.mLastPointCount = pointerCount;
            RectF rectF = getMatrixRectf();
            switch (event.getAction()) {
                case 0:
                    if (rectF.width() > ((float) getWidth()) || rectF.height() > ((float) getHeight())) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    }
                case 1:
                case 3:
                    this.mLastPointCount = 0;
                    getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                case 2:
                    float dx = x - this.mLastX;
                    float dy = y - this.mLastY;
                    if (!this.isCanDrag) {
                        this.isCanDrag = isMoveAction(dx, dy);
                    }
                    if (getDrawable() != null && this.isCanDrag) {
                        if (rectF.width() < ((float) getWidth())) {
                            this.isCheckLeftAndRight = false;
                            dx = 0.0f;
                        } else {
                            this.isCheckLeftAndRight = true;
                        }
                        if (rectF.height() < ((float) getHeight())) {
                            this.isCheckTopAndBottom = false;
                            dy = 0.0f;
                        } else {
                            this.isCheckTopAndBottom = true;
                        }
                        this.mMatrix.postTranslate(dx, dy);
                        checkBorderWhenTranslate();
                        setImageMatrix(this.mMatrix);
                    }
                    this.mLastX = x;
                    this.mLastY = y;
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt((double) ((dx * dx) + (dy * dy))) > ((double) this.mTouchSlop);
    }
}
