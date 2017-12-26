package com.jwkj.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class RotateLayout extends FrameLayout {
    private Matrix mForward;
    private Matrix mReverse;
    private float[] mTemp;

    public RotateLayout(Context context) {
        super(context);
        this.mForward = new Matrix();
        this.mReverse = new Matrix();
        this.mTemp = new float[2];
    }

    public RotateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mForward = new Matrix();
        this.mReverse = new Matrix();
        this.mTemp = new float[2];
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    protected void dispatchDraw(Canvas canvas) {
        canvas.rotate(-90.0f, (float) (-getHeight()), 0.0f);
        this.mForward = canvas.getMatrix();
        this.mForward.invert(this.mReverse);
        canvas.save();
        canvas.setMatrix(this.mForward);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        float[] temp = this.mTemp;
        temp[0] = event.getX();
        temp[1] = event.getY();
        this.mReverse.mapPoints(temp);
        event.setLocation(temp[0], temp[1]);
        return super.dispatchTouchEvent(event);
    }
}
