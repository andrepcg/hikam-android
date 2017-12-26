package com.p2p.core;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import com.p2p.core.global.Constants.P2P_WINDOW.Action;

public class P2PView extends SurfaceView {
    static final String TAG = "p2p";
    int deviceType;
    private boolean ignoreResize = false;
    boolean isInitScreen = false;
    Context mContext;
    protected GestureDetector mGestureDetector;
    private HKVideoScale mHKVideoScale;
    private int mHeight;
    MediaPlayer mPlayer;
    Callback mSHCallback = new C06881();
    private VideoScale mVideoScale;
    private int mWidth;

    class C06881 implements Callback {
        C06881() {
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            Log.v(P2PView.TAG, "surfaceChanged()");
            int sdlFormat = -2062217214;
            switch (format) {
                case 1:
                    Log.v(P2PView.TAG, "pixel format RGBA_8888");
                    sdlFormat = -2042224636;
                    break;
                case 2:
                    Log.v(P2PView.TAG, "pixel format RGBX_8888");
                    sdlFormat = -2044321788;
                    break;
                case 3:
                    Log.v(P2PView.TAG, "pixel format RGB_888");
                    sdlFormat = -2045372412;
                    break;
                case 4:
                    Log.v(P2PView.TAG, "pixel format RGB_565");
                    sdlFormat = -2062217214;
                    break;
                case 6:
                    Log.v(P2PView.TAG, "pixel format RGBA_5551");
                    sdlFormat = -2059137022;
                    break;
                case 7:
                    Log.v(P2PView.TAG, "pixel format RGBA_4444");
                    sdlFormat = -2059268094;
                    break;
                case 8:
                    Log.v(P2PView.TAG, "pixel format A_8");
                    break;
                case 9:
                    Log.v(P2PView.TAG, "pixel format L_8");
                    break;
                case 10:
                    Log.v(P2PView.TAG, "pixel format LA_88");
                    break;
                case 11:
                    Log.v(P2PView.TAG, "pixel format RGB_332");
                    sdlFormat = -2079258623;
                    break;
                default:
                    Log.v(P2PView.TAG, "pixel format unknown " + format);
                    break;
            }
            Log.v(P2PView.TAG, "Window size:" + w + "x" + h);
            MediaPlayer mediaPlayer = P2PView.this.mPlayer;
            MediaPlayer.GLNativeResize(w, h, sdlFormat);
            Log.e("surface", w + ":" + h + ":" + P2PView.this.deviceType);
            if (!P2PView.this.ignoreResize) {
                if (P2PView.this.deviceType == 7) {
                    P2PView.this.ignoreResize = true;
                }
                LayoutParams layoutParams;
                if (P2PView.this.deviceType == 7) {
                    P2PView.this.mWidth = (h * 16) / 9;
                    P2PView.this.mHeight = h;
                    if (P2PView.this.mWidth > w) {
                        P2PView.this.mWidth = w;
                        P2PView.this.mHeight = (w * 9) / 16;
                    }
                    layoutParams = P2PView.this.getLayoutParams();
                    layoutParams.width = P2PView.this.mWidth;
                    layoutParams.height = P2PView.this.mHeight;
                    P2PView.this.setLayoutParams(layoutParams);
                } else {
                    P2PView.this.mWidth = (h * 4) / 3;
                    P2PView.this.mHeight = h;
                    if (P2PView.this.mWidth > w) {
                        P2PView.this.mWidth = w;
                        P2PView.this.mHeight = (w * 3) / 4;
                    }
                    layoutParams = P2PView.this.getLayoutParams();
                    layoutParams.width = P2PView.this.mWidth;
                    layoutParams.height = P2PView.this.mHeight;
                    P2PView.this.setLayoutParams(layoutParams);
                }
            }
            if (!P2PView.this.isInitScreen) {
                DisplayMetrics dm = new DisplayMetrics();
                dm = P2PView.this.getResources().getDisplayMetrics();
                int mWindowWidth = dm.widthPixels;
                int mWindowHeight = dm.heightPixels;
                P2PView.this.isInitScreen = true;
                if (P2PView.this.mPlayer != null) {
                    P2PView.this.mPlayer.init(P2PView.this.mWidth, P2PView.this.mHeight, mWindowWidth);
                } else {
                    P2PView.this.mPlayer = MediaPlayer.getInstance();
                    P2PView.this.mPlayer.init(P2PView.this.mWidth, P2PView.this.mHeight, mWindowWidth);
                }
                Intent start = new Intent();
                start.setAction(Action.P2P_WINDOW_READY_TO_START);
                P2PView.this.mContext.sendBroadcast(start);
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            Log.e("oaosj", "surfaceView : holder create .");
            holder.setType(2);
            holder.setKeepScreenOn(true);
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            P2PView.this.release();
        }
    }

    class ClearScreenThread extends Thread {
        SurfaceHolder sfHolder;

        public ClearScreenThread(SurfaceHolder sfHolder) {
            this.sfHolder = sfHolder;
        }

        public void run() {
            super.run();
            synchronized (this.sfHolder) {
                Canvas canvas = this.sfHolder.lockCanvas();
                canvas.drawColor(ViewCompat.MEASURED_STATE_MASK);
                this.sfHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public P2PView(Context context) {
        super(context);
        this.mContext = context;
        this.mPlayer = MediaPlayer.getInstance();
    }

    public void restart() {
        setVisibility(8);
        setVisibility(0);
    }

    public P2PView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mPlayer = MediaPlayer.getInstance();
    }

    public void updateScreenOrientation() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        int mWindowWidth = dm.widthPixels;
        int mWindowHeight = dm.heightPixels;
        Log.e("my", "xWidth:" + mWindowWidth + " xHeight:" + mWindowHeight);
        if (this.deviceType == 7) {
            this.mWidth = (mWindowHeight * 16) / 9;
            this.mHeight = mWindowHeight;
            if (this.mWidth > mWindowWidth) {
                this.mWidth = mWindowWidth;
                this.mHeight = (mWindowWidth * 9) / 16;
            }
            LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = this.mWidth * 3;
            layoutParams.height = this.mHeight * 3;
            setLayoutParams(layoutParams);
            MediaPlayer mediaPlayer = this.mPlayer;
            MediaPlayer.changeScreenSize(this.mWidth, this.mHeight, 1, 1.0f);
            return;
        }
        this.mWidth = (mWindowHeight * 4) / 3;
        this.mHeight = mWindowHeight;
        if (this.mWidth > mWindowWidth) {
            this.mWidth = mWindowWidth;
            this.mHeight = (mWindowWidth * 3) / 4;
        }
        layoutParams = getLayoutParams();
        layoutParams.width = this.mWidth;
        layoutParams.height = this.mHeight;
        setLayoutParams(layoutParams);
        mediaPlayer = this.mPlayer;
        MediaPlayer.changeScreenSize(this.mWidth, this.mHeight, 0, 1.0f);
    }

    public void setCallBack() {
        MediaPlayer.setEglView(this);
        getHolder().addCallback(this.mSHCallback);
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        this.mGestureDetector = gestureDetector;
    }

    public void setVideoScale(VideoScale vScale) {
        this.mVideoScale = vScale;
    }

    public void setHKVideoScale(HKVideoScale vScale) {
        this.mHKVideoScale = vScale;
    }

    public void setDeviceType(int type) {
        this.deviceType = type;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mGestureDetector != null) {
            Log.d(TAG, "onTouchEvent");
            this.mGestureDetector.onTouchEvent(event);
        }
        if (this.mVideoScale != null) {
            this.mVideoScale.onTouchEvent(event);
            return true;
        } else if (this.mHKVideoScale == null) {
            return super.onTouchEvent(event);
        } else {
            this.mHKVideoScale.onTouchEvent(event);
            return true;
        }
    }

    public void fullScreen() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        int mWindowWidth = dm.widthPixels;
        int mWindowHeight = dm.heightPixels;
        if (this.deviceType != 7) {
            this.mWidth = mWindowWidth;
            this.mHeight = mWindowHeight;
            LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = this.mWidth;
            layoutParams.height = this.mHeight;
            setLayoutParams(layoutParams);
            if (this.mPlayer != null) {
                MediaPlayer mediaPlayer = this.mPlayer;
                MediaPlayer.changeScreenSize(this.mWidth, this.mHeight, 1, 1.0f);
            }
        }
    }

    public void halfScreen() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        int mWindowWidth = dm.widthPixels;
        int mWindowHeight = dm.heightPixels;
        if (this.deviceType != 7) {
            this.mWidth = (mWindowHeight * 4) / 3;
            this.mHeight = mWindowHeight;
            if (this.mWidth > mWindowWidth) {
                this.mWidth = mWindowWidth;
                this.mHeight = (mWindowWidth * 3) / 4;
            }
            LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = this.mWidth;
            layoutParams.height = this.mHeight;
            setLayoutParams(layoutParams);
            if (this.mPlayer != null) {
                MediaPlayer mediaPlayer = this.mPlayer;
                MediaPlayer.changeScreenSize(this.mWidth, this.mHeight, 0, 1.0f);
            }
        }
    }

    public synchronized void release() {
        Log.d(TAG, "releasing player");
        if (this.mPlayer != null) {
            this.mPlayer.stop();
            this.mPlayer.release();
            this.mPlayer = null;
        }
        MediaPlayer.ReleaseOpenGL();
    }
}
