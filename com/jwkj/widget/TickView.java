package com.jwkj.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.hikam.C0291R;

public class TickView extends View {
    public Runnable animRunnable = new C06141();
    private Handler handler = new Handler();
    private int height;
    private int indicator;
    private boolean isFirstIn = true;
    private boolean isTicking = false;
    private Paint maskPaint;
    private OnTickListener onTickListener;
    private Path path;
    private Paint tickPaint;
    private int width;

    class C06141 implements Runnable {
        C06141() {
        }

        public void run() {
            if (TickView.this.indicator <= TickView.this.width) {
                TickView.this.indicator = TickView.this.indicator + 1;
                TickView.this.invalidate();
                return;
            }
            TickView.this.isTicking = false;
            if (TickView.this.onTickListener != null) {
                TickView.this.onTickListener.tickDone();
            }
        }
    }

    public interface OnTickListener {
        void tickDone();
    }

    public void setOnTickListener(OnTickListener onTickListener) {
        this.onTickListener = onTickListener;
    }

    public TickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initComponent(context);
    }

    private void initComponent(Context context) {
        this.tickPaint = new Paint();
        this.tickPaint.setAntiAlias(true);
        this.tickPaint.setStrokeWidth(10.0f);
        this.tickPaint.setStyle(Style.STROKE);
        this.tickPaint.setColor(context.getResources().getColor(C0291R.color.blue));
        this.maskPaint = new Paint();
        this.maskPaint.setAntiAlias(true);
        this.maskPaint.setStyle(Style.FILL);
        this.maskPaint.setColor(context.getResources().getColor(C0291R.color.text_color_white));
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.isFirstIn) {
            this.width = getWidth();
            this.height = getHeight();
            this.path = new Path();
            this.path.moveTo((float) (this.width - this.height), (float) (this.height / 2));
            this.path.lineTo((float) (this.width - (this.height / 2)), (float) this.height);
            this.path.lineTo((float) this.width, 0.0f);
            this.indicator = this.width - this.height;
            this.isFirstIn = false;
        }
        canvas.drawPath(this.path, this.tickPaint);
        canvas.drawRect((float) (this.indicator - 2), 0.0f, (float) this.width, (float) this.height, this.maskPaint);
        if (this.isTicking) {
            this.handler.postDelayed(this.animRunnable, 10);
        }
    }

    public void startTick() {
        this.isTicking = true;
        this.handler.postDelayed(this.animRunnable, 5);
    }
}
