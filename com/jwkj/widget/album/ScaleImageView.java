package com.jwkj.widget.album;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ScaleImageView extends ImageView {
    private static final int DOUBLE_CLICK_TIME = 400;
    private float DIFF_HOR;
    private float DIFF_VER;
    private float DIFF_ZOOM;
    private float dis1;
    private float dis2;
    private int pointNum = 0;
    private long time = 0;
    private float x1;
    private float x2;
    private float y1;
    private float y2 = 0.0f;

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & 255) {
            case 0:
                if (this.pointNum > 1) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                this.pointNum++;
                this.x1 = event.getX();
                if (this.time != 0) {
                    long sencondTime = System.currentTimeMillis();
                    if (sencondTime - this.time >= 400) {
                        this.time = sencondTime;
                        Log.e("few", "enter -failed" + this.time);
                        break;
                    }
                    recoveryView();
                    this.time = 0;
                    Log.e("few", "enter --ok" + this.time);
                    break;
                }
                this.time = System.currentTimeMillis();
                Log.e("few", "enter --" + this.time);
                break;
            case 1:
                getParent().requestDisallowInterceptTouchEvent(false);
                this.pointNum--;
                break;
            case 2:
                if (this.pointNum > 1) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (this.pointNum == 1 && this.DIFF_ZOOM != 0.0f) {
                    this.x2 = event.getX();
                    float cao = this.DIFF_ZOOM * ((float) getWidth());
                    Log.e("oaosj", "---" + cao + "---");
                    Log.e("oaosj", "---" + this.DIFF_HOR + "---");
                    if (this.DIFF_HOR >= cao || this.DIFF_HOR <= (-cao)) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        this.DIFF_HOR += this.x2 - this.x1;
                        setTranslationX(this.DIFF_HOR);
                    }
                }
                if (this.pointNum > 1) {
                    this.x1 = event.getX(0);
                    this.x2 = event.getX(1);
                    this.y1 = event.getY(0);
                    this.y2 = event.getY(1);
                    this.dis2 = (float) Math.sqrt(Math.pow((double) (this.x1 - this.x2), 2.0d) + Math.pow((double) (this.y1 - this.y2), 2.0d));
                    Log.e("oaosj", "ACTION_MOVE -- " + this.dis2 + " -- " + (this.dis2 - this.dis1));
                    this.DIFF_ZOOM += (this.dis2 - this.dis1) / ((float) getWidth());
                    setScaleX(1.0f + this.DIFF_ZOOM);
                    setScaleY(1.0f + this.DIFF_ZOOM);
                    break;
                }
                break;
            case 5:
                if (this.pointNum > 1) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                this.pointNum++;
                if (this.pointNum > 1) {
                    this.x1 = event.getX(0);
                    this.x2 = event.getX(1);
                    this.y1 = event.getY(0);
                    this.y2 = event.getY(1);
                    this.dis1 = (float) Math.sqrt(Math.pow((double) (this.x1 - this.x2), 2.0d) + Math.pow((double) (this.y1 - this.y2), 2.0d));
                    break;
                }
                break;
            case 6:
                if (this.pointNum > 1) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                this.pointNum--;
                break;
        }
        return true;
    }

    public void recoveryView() {
        this.DIFF_HOR = 0.0f;
        if (this.DIFF_ZOOM == 0.0f) {
            this.DIFF_ZOOM = 0.5f;
            setScaleX(this.DIFF_ZOOM + 1.0f);
            setScaleY(this.DIFF_ZOOM + 1.0f);
        } else {
            this.DIFF_ZOOM = 0.0f;
            setScaleX(this.DIFF_ZOOM + 1.0f);
            setScaleY(this.DIFF_ZOOM + 1.0f);
        }
        setTranslationX(this.DIFF_HOR);
    }
}
