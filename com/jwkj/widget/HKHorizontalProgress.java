package com.jwkj.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class HKHorizontalProgress extends View {
    private float drawLength = 0.0f;
    private int max = 100;
    private Paint paint;
    private int progress = 0;
    private int width = 0;

    public HKHorizontalProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.width = getWidth();
        this.drawLength = ((((float) this.progress) * 1.0f) / ((float) this.max)) * ((float) this.width);
        canvas.drawLine(0.0f, 0.0f, this.drawLength, 0.0f, this.paint);
    }

    private void init(Context context) {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setColor(context.getResources().getColor(17170459));
        this.paint.setStrokeWidth(20.0f);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public int getProgress() {
        return this.progress;
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public int getMax() {
        return this.max;
    }
}
