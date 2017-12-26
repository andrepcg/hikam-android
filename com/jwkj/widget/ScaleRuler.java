package com.jwkj.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;
import com.hikam.C0291R;

public class ScaleRuler extends SeekBar {
    private float[] cursors = new float[this.scaleNum];
    private boolean isFirstIn = true;
    private Paint paint = new Paint();
    private LinearGradient pressShader;
    private int scaleNum = 5;
    private int sensitivity = 0;
    private Paint textPaint = new Paint();
    private float thumbIndex = 0.0f;
    private Paint thumbPaint = new Paint();
    private LinearGradient unPressShader;
    private float f21x;

    public ScaleRuler(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.textPaint.setTextSize(20.0f);
        this.paint.setAntiAlias(true);
        this.paint.setColor(-1);
        this.paint.setStrokeWidth(1.0f);
        this.thumbPaint.setAntiAlias(true);
        this.thumbPaint.setColor(-16776961);
        setProgressDrawable(getContext().getResources().getDrawable(C0291R.drawable.widget_scale_ruler_close));
        setMax(720);
        setThumb(null);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < this.scaleNum; i++) {
            canvas.drawLine(this.cursors[i], (float) getHeight(), this.cursors[i], 0.0f, this.paint);
            if (i == 4) {
                canvas.drawText((i + 1) + "", this.cursors[i] - 16.0f, (float) getHeight(), this.textPaint);
            } else if (i == 0) {
                canvas.drawText((i + 1) + "", this.cursors[i] + 2.0f, (float) getHeight(), this.textPaint);
            } else {
                canvas.drawText((i + 1) + "", this.cursors[i] - 8.0f, (float) getHeight(), this.textPaint);
            }
        }
        canvas.drawRect(this.thumbIndex, 10.0f, 20.0f + this.thumbIndex, (float) (getHeight() - 21), this.thumbPaint);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.f21x = (((float) getWidth()) * 1.0f) / ((float) (this.scaleNum - 1));
        for (int i = 0; i < this.scaleNum; i++) {
            this.cursors[i] = this.f21x * ((float) i);
        }
        if (this.isFirstIn) {
            drawThumb(this.cursors[2]);
            this.isFirstIn = false;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                getParent().requestDisallowInterceptTouchEvent(true);
                drawThumb(event.getX());
                break;
            case 1:
                int progress = getProgress();
                if (progress >= 630) {
                    setRulerProgress(720);
                    drawThumb(this.cursors[4]);
                } else if (progress >= 450) {
                    setRulerProgress(540);
                    drawThumb(this.cursors[3]);
                } else if (progress >= 270) {
                    setRulerProgress(360);
                    drawThumb(this.cursors[2]);
                } else if (progress >= 90) {
                    setRulerProgress(180);
                    drawThumb(this.cursors[1]);
                } else if (progress >= 0) {
                    setRulerProgress(0);
                    drawThumb(this.cursors[0]);
                }
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case 2:
                drawThumb(event.getX());
                break;
        }
        return super.onTouchEvent(event);
    }

    public void drawThumb(float x2) {
        this.thumbIndex = x2;
        invalidate();
    }

    private void changeShader(boolean isPress) {
        if (isPress) {
            this.thumbPaint.setColor(-16776961);
        } else {
            this.thumbPaint.setColor(-7829368);
        }
        invalidate();
    }

    public void setRulerProgress(int progress) {
        setProgress(progress);
        if (progress >= 630) {
            drawThumb(this.cursors[4]);
            Log.e("ScaleRuler", "ScaleRuler cursors[4] :" + this.cursors[4]);
            this.sensitivity = 5;
        } else if (progress >= 450) {
            drawThumb(this.cursors[3]);
            Log.e("ScaleRuler", "ScaleRuler cursors[3] :" + this.cursors[3]);
            this.sensitivity = 4;
        } else if (progress >= 270) {
            drawThumb(this.cursors[2]);
            Log.e("ScaleRuler", "ScaleRuler cursors[2] :" + this.cursors[2]);
            this.sensitivity = 3;
        } else if (progress >= 90) {
            drawThumb(this.cursors[1]);
            Log.e("ScaleRuler", "ScaleRuler cursors[1] :" + this.cursors[1]);
            this.sensitivity = 2;
        } else if (progress >= 0) {
            drawThumb(this.cursors[0]);
            Log.e("ScaleRuler", "ScaleRuler cursors[0] :" + this.cursors[0]);
            this.sensitivity = 1;
        }
    }

    public int getSensitivity() {
        return this.sensitivity;
    }

    public void setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity;
        switch (sensitivity) {
            case 1:
                setRulerProgress(0);
                return;
            case 2:
                setRulerProgress(180);
                return;
            case 3:
                setRulerProgress(360);
                return;
            case 4:
                setRulerProgress(540);
                return;
            case 5:
                setRulerProgress(720);
                return;
            default:
                return;
        }
    }

    public void close() {
        changeShader(false);
        setProgressDrawable(getContext().getResources().getDrawable(C0291R.drawable.widget_scale_ruler_close));
    }

    public void open() {
        changeShader(true);
        setProgressDrawable(getContext().getResources().getDrawable(C0291R.drawable.widget_scale_ruler));
    }
}
