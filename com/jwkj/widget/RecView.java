package com.jwkj.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.internal.view.SupportMenu;
import android.util.AttributeSet;
import android.view.View;
import com.hikam.C0291R;

public class RecView extends View {
    private String REC = "REC";
    private Handler handler;
    private int height;
    private Paint recPaint;
    private boolean showRec = true;
    private Paint textPaint;
    private int width;

    class C06101 implements Runnable {
        C06101() {
        }

        public void run() {
            if (RecView.this.showRec) {
                RecView.this.showRec = false;
            } else {
                RecView.this.showRec = true;
            }
            RecView.this.invalidate();
        }
    }

    public RecView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.recPaint = new Paint();
        this.recPaint.setStyle(Style.FILL);
        this.recPaint.setColor(SupportMenu.CATEGORY_MASK);
        this.recPaint.setAntiAlias(true);
        this.textPaint = new Paint();
        this.textPaint.setStyle(Style.FILL);
        this.textPaint.setColor(getResources().getColor(C0291R.color.color_red));
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTextSize(50.0f);
        this.handler = new Handler();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.showRec) {
            canvas.drawCircle((float) (getHeight() / 2), (float) (getHeight() / 2), (float) (getHeight() / 2), this.recPaint);
        }
        this.handler.postDelayed(new C06101(), 600);
    }
}
