package com.p2p.core;

import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.jwkj.activity.ImageBrowser;

public class HKVideoScale {
    private static final float MAX_SCALE = 2.0f;
    static final String TAG = "HKVideoScale";
    private Rect curRect = null;
    public float mHeight;
    public float mWidth;
    private int mode;
    private float oldDist;
    private float oldScale;
    private float oldX;
    private float oldY;
    private Rect oriRect = null;
    private P2PView pView;
    private RelativeLayout relView;
    private float scaleValue = 1.0f;
    private float touchCenterX;
    private float touchCenterY;

    public HKVideoScale(P2PView view, RelativeLayout relView) {
        this.pView = view;
        this.relView = relView;
    }

    public void onTouchEvent(MotionEvent event) {
        switch (event.getAction() & 255) {
            case 0:
                this.mode = 1;
                this.oldX = event.getX();
                this.oldY = event.getY();
                if (this.oriRect == null) {
                    this.oriRect = new Rect(this.pView.getLeft(), this.pView.getTop(), this.pView.getRight(), this.pView.getBottom());
                    this.mWidth = (float) this.oriRect.width();
                    this.mHeight = (float) this.oriRect.height();
                }
                if (this.curRect == null) {
                    this.curRect = new Rect(this.pView.getLeft(), this.pView.getTop(), this.pView.getRight(), this.pView.getBottom());
                    return;
                }
                return;
            case 1:
                this.mode = 0;
                return;
            case 2:
                Log.e(TAG, "ACTION_MOVE " + this.mode);
                if (this.mode >= 2) {
                    float v;
                    float dd = spacing(event) - this.oldDist;
                    DisplayMetrics dm = new DisplayMetrics();
                    float mWindowWidth = (float) this.pView.getResources().getDisplayMetrics().widthPixels;
                    if (dd > 0.0f) {
                        v = 1.0f + ((dd / mWindowWidth) * 8.0f);
                    } else {
                        v = 1.0f + ((dd / mWindowWidth) * MAX_SCALE);
                    }
                    float scaleNext = this.oldScale * v;
                    if (scaleNext < 1.0f) {
                        scaleNext = 1.0f;
                    } else if (scaleNext > MAX_SCALE) {
                        scaleNext = MAX_SCALE;
                    }
                    resizeRect(scaleNext, this.touchCenterX, this.touchCenterY);
                    return;
                } else if (this.mode == 1) {
                    moveRect(event);
                    return;
                } else {
                    return;
                }
            case 5:
                this.oldDist = spacing(event);
                this.touchCenterX = centerX(event);
                this.touchCenterY = centerY(event);
                this.oldScale = this.scaleValue;
                this.mode++;
                return;
            case 6:
                this.mode = -1;
                return;
            default:
                return;
        }
    }

    private void moveRect(MotionEvent event) {
        float dx = event.getX() - this.oldX;
        float dy = event.getY() - this.oldY;
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dy < 3.0f) {
                dy = 0.0f;
            }
        } else if (dx < 3.0f) {
            dx = 0.0f;
        }
        int leftT = (int) (((float) this.curRect.left) + (dx / MAX_SCALE));
        int topT = (int) (((float) this.curRect.top) + (dy / MAX_SCALE));
        int rightT = (int) (((float) leftT) + this.mWidth);
        int bottomT = (int) (((float) topT) + this.mHeight);
        if (leftT <= this.oriRect.left && rightT >= this.oriRect.right) {
            this.curRect.left = leftT;
            this.curRect.right = rightT;
        }
        if (topT <= this.oriRect.top && bottomT >= this.oriRect.bottom) {
            this.curRect.top = topT;
            this.curRect.bottom = bottomT;
        }
        P2pJni.P2PClientSdkMoveView(this.curRect.left, this.curRect.top, (int) this.mWidth, (int) this.mHeight);
    }

    private void resizeRect(float newScaleValue, float x, float y) {
        if (this.scaleValue >= 1.0f && this.scaleValue <= MAX_SCALE) {
            int width = this.oriRect.right - this.oriRect.left;
            int height = this.oriRect.bottom - this.oriRect.top;
            this.mWidth = ((float) width) * newScaleValue;
            this.mHeight = ((float) height) * newScaleValue;
            if (this.pView.deviceType == 7) {
                this.mHeight = (this.mWidth * 9.0f) / 16.0f;
            } else {
                this.mHeight = (this.mWidth * 3.0f) / ImageBrowser.SCALE_MAX;
            }
            int left = (int) ((-(this.mWidth - ((float) width))) / MAX_SCALE);
            int top = (int) ((-(this.mHeight - ((float) height))) / MAX_SCALE);
            int right = (int) (this.mWidth - ((float) left));
            int bottom = (int) (this.mHeight - ((float) top));
            this.scaleValue = newScaleValue;
            if (left > this.oriRect.left) {
                left = this.oriRect.left;
                right = (int) (((float) left) + this.mWidth);
            }
            if (top > this.oriRect.top) {
                top = this.oriRect.top;
                bottom = (int) (((float) top) + this.mHeight);
            }
            if (right < this.oriRect.right) {
                right = this.oriRect.right;
                left = (int) (((float) right) - this.mWidth);
            }
            if (bottom < this.oriRect.bottom) {
                bottom = this.oriRect.bottom;
                top = (int) (((float) bottom) - this.mHeight);
            }
            this.curRect.left = left;
            this.curRect.right = right;
            this.curRect.top = top;
            this.curRect.bottom = bottom;
            P2pJni.P2PClientSdkResizeView(this.curRect.left, this.curRect.top, (int) this.mWidth, (int) this.mHeight);
            Log.e("few", "resize:[ " + this.curRect.left + " - " + this.curRect.top + " - " + this.curRect.right + " - " + this.curRect.height() + "]");
        }
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt((double) ((x * x) + (y * y)));
    }

    private float centerX(MotionEvent event) {
        return (event.getX(0) + event.getX(1)) / MAX_SCALE;
    }

    private float centerY(MotionEvent event) {
        return (event.getY(0) + event.getY(1)) / MAX_SCALE;
    }
}
