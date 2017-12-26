package com.jwkj.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.hikam.C0291R;
import java.util.ArrayList;
import java.util.List;

public class StarLinesView extends View {
    private static final float STAR_RADIUS = 15.0f;
    private boolean MOVE_MODE = false;
    private List<StarPoint> backupList;
    private Paint dashLinePaint;
    private float moveX = 0.0f;
    private float moveY = 0.0f;
    private Paint paint;
    private Path path;
    private List<StarPoint> pointList;
    private Paint shadowPaint;
    private float startX;
    private float startY;

    public class StarPoint {
        private float f22x;
        private float f23y;

        public StarPoint(float x, float y) {
            this.f22x = x;
            this.f23y = y;
        }
    }

    public StarLinesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setLayerType(1, null);
        this.pointList = new ArrayList();
        this.backupList = new ArrayList();
        this.paint = new Paint();
        this.paint.setColor(getResources().getColor(C0291R.color.paint_star));
        this.paint.setAntiAlias(true);
        this.paint.setStrokeWidth(10.0f);
        DashPathEffect effect = new DashPathEffect(new float[]{20.0f, 10.0f}, 0.0f);
        this.dashLinePaint = new Paint();
        this.dashLinePaint.setColor(getResources().getColor(C0291R.color.paint_star));
        this.dashLinePaint.setAntiAlias(true);
        this.dashLinePaint.setPathEffect(effect);
        this.dashLinePaint.setStrokeWidth(10.0f);
        this.shadowPaint = new Paint();
        this.shadowPaint.setColor(getResources().getColor(C0291R.color.gray));
        this.shadowPaint.setAntiAlias(true);
        this.shadowPaint.setAlpha(50);
        this.path = new Path();
    }

    protected void onDraw(Canvas canvas) {
        int i;
        super.onDraw(canvas);
        if (this.pointList.size() != 0) {
            canvas.drawCircle(this.moveX + ((StarPoint) this.pointList.get(0)).f22x, ((StarPoint) this.pointList.get(0)).f23y + this.moveY, STAR_RADIUS, this.paint);
        }
        for (i = 1; i < getPointSize(); i++) {
            drawStarLine(canvas, (StarPoint) this.pointList.get(i - 1), (StarPoint) this.pointList.get(i));
        }
        if (this.pointList.size() > 2) {
            Canvas canvas2 = canvas;
            canvas2.drawLine(this.moveX + ((StarPoint) this.pointList.get(0)).f22x, this.moveY + ((StarPoint) this.pointList.get(0)).f23y, this.moveX + ((StarPoint) this.pointList.get(this.pointList.size() - 1)).f22x, this.moveY + ((StarPoint) this.pointList.get(this.pointList.size() - 1)).f23y, this.dashLinePaint);
        }
        this.path.reset();
        for (i = 0; i < getPointSize(); i++) {
            if (i == 0) {
                this.path.moveTo(((StarPoint) this.pointList.get(0)).f22x, ((StarPoint) this.pointList.get(0)).f23y);
            } else {
                this.path.lineTo(((StarPoint) this.pointList.get(i)).f22x, ((StarPoint) this.pointList.get(i)).f23y);
            }
        }
        canvas.drawPath(this.path, this.shadowPaint);
    }

    private void drawStarLine(Canvas canvas, StarPoint startPoint, StarPoint endPoint) {
        Canvas canvas2 = canvas;
        canvas2.drawLine(this.moveX + startPoint.f22x, this.moveY + startPoint.f23y, this.moveX + endPoint.f22x, this.moveY + endPoint.f23y, this.paint);
        canvas.drawCircle(endPoint.f22x + this.moveX, endPoint.f23y + this.moveY, STAR_RADIUS, this.paint);
    }

    private int getPointSize() {
        return this.pointList.size();
    }

    private int getBackupPointSize() {
        return this.backupList.size();
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                Log.e("few", "down");
                this.startX = event.getX();
                this.startY = event.getY();
                break;
            case 1:
                if (!this.MOVE_MODE) {
                    Log.e("few", "up not move");
                    this.pointList.add(new StarPoint(event.getX(), event.getY()));
                    invalidate();
                    break;
                }
                Log.e("few", "up move");
                for (StarPoint starPoint : this.pointList) {
                    starPoint.f22x = starPoint.f22x + this.moveX;
                    starPoint.f23y = starPoint.f23y + this.moveY;
                }
                this.moveX = 0.0f;
                this.moveY = 0.0f;
                this.MOVE_MODE = false;
                break;
            case 2:
                Log.e("few", "move");
                this.moveX = event.getX() - this.startX;
                this.moveY = event.getY() - this.startY;
                if (((double) Math.abs(this.moveX)) > 0.5d || ((double) Math.abs(this.moveY)) > 0.5d) {
                    this.MOVE_MODE = true;
                    invalidate();
                    break;
                }
        }
        return true;
    }

    public synchronized void reset() {
        this.pointList.clear();
        this.backupList.clear();
        invalidate();
    }

    public synchronized void pop() {
        int pointSize = this.pointList.size();
        int backupSize = this.backupList.size();
        if (pointSize != 0) {
            this.backupList.add(this.pointList.get(pointSize - 1));
            this.pointList.remove(pointSize - 1);
            invalidate();
        }
    }

    public synchronized void backup() {
        int pointSize = this.pointList.size();
        int backupSize = this.backupList.size();
        if (backupSize != 0) {
            this.pointList.add(this.backupList.get(backupSize - 1));
            this.backupList.remove(backupSize - 1);
            invalidate();
        }
    }

    public void getRect() {
    }
}
