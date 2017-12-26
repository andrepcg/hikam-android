package com.p2p.core;

import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import com.jwkj.activity.ImageBrowser;

public class VideoScale {
    private static final float MAX_SCALE = 2.0f;
    static final String TAG = "VideoScale";
    private Rect curRect = new Rect();
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

    public VideoScale(P2PView view, RelativeLayout relView) {
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
                    Log.d("p2p Scale", "scaleNext: " + scaleNext);
                    layoutByScale(scaleNext, this.touchCenterX, this.touchCenterY);
                    return;
                } else if (this.mode == 1) {
                    int left;
                    int right;
                    int top;
                    int bottom;
                    float dx = event.getX() - this.oldX;
                    float dy = event.getY() - this.oldY;
                    int leftT = (int) (((float) this.pView.getLeft()) + dx);
                    int topT = (int) (((float) this.pView.getTop()) + dy);
                    int rightT = (int) (((float) this.pView.getRight()) + dx);
                    int bottomT = (int) (((float) this.pView.getBottom()) + dy);
                    if (leftT > this.oriRect.left || rightT < this.oriRect.right) {
                        left = this.pView.getLeft();
                        right = this.pView.getRight();
                    } else {
                        left = leftT;
                        right = rightT;
                    }
                    if (topT > this.oriRect.top || bottomT < this.oriRect.bottom) {
                        top = this.pView.getTop();
                        bottom = this.pView.getBottom();
                    } else {
                        top = topT;
                        bottom = bottomT;
                    }
                    this.pView.layout(left, top, right, bottom);
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

    private void scaleVideo(float newScaleValue, float x, float y) {
        float height = (float) this.oriRect.height();
        this.mWidth = (((float) this.oriRect.width()) / 3.0f) * newScaleValue;
        this.mHeight = (height / 3.0f) * newScaleValue;
        if (this.pView.deviceType == 7) {
            this.mHeight = (this.mWidth * 9.0f) / 16.0f;
        } else {
            this.mHeight = (this.mWidth * 3.0f) / ImageBrowser.SCALE_MAX;
        }
        this.scaleValue = newScaleValue;
        MediaPlayer mediaPlayer = this.pView.mPlayer;
        MediaPlayer.changeScreenSize((int) this.mWidth, (int) this.mHeight, 0, newScaleValue);
    }

    private void layoutByScale(float newScaleValue, float x, float y) {
        if (this.scaleValue >= 1.0f && this.scaleValue <= MAX_SCALE) {
            int height = this.oriRect.bottom - this.oriRect.top;
            this.mWidth = ((float) (this.oriRect.right - this.oriRect.left)) * newScaleValue;
            this.mHeight = ((float) height) * newScaleValue;
            if (this.pView.deviceType == 7) {
                this.mHeight = (this.mWidth * 9.0f) / 16.0f;
            } else {
                this.mHeight = (this.mWidth * 3.0f) / ImageBrowser.SCALE_MAX;
            }
            int left = (int) (x - (((x - ((float) this.pView.getLeft())) / ((float) this.pView.getWidth())) * this.mWidth));
            int top = (int) (y - (((y - ((float) this.pView.getTop())) / ((float) this.pView.getHeight())) * this.mHeight));
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
            int l = left;
            int t = top;
            int r = right;
            int b = bottom;
            Log.e(TAG, "ChangeScreenSize mWidth:" + ((int) this.mWidth) + " mHeight:" + ((int) this.mHeight) + " scale:" + newScaleValue);
            MediaPlayer mediaPlayer = this.pView.mPlayer;
            MediaPlayer.changeScreenSize((int) this.mWidth, (int) this.mHeight, 1, newScaleValue);
            Log.e(TAG, "layout left:" + left + " top:" + top + " right:" + right + " bottom:" + bottom);
            this.pView.layout(left, top, right, bottom);
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
