package com.p2p.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import com.p2p.core.GestureDetector.SimpleOnGestureListener;
import com.p2p.core.global.Constants.P2P_WINDOW.Action;
import java.io.IOException;

public abstract class BaseVideoActivity extends BaseCoreActivity {
    public static int mVideoFrameRate = 15;
    private final int MINX = 50;
    private final int MINY = 25;
    private final int USR_CMD_OPTION_PTZ_TURN_DOWN = 3;
    private final int USR_CMD_OPTION_PTZ_TURN_LEFT = 0;
    private final int USR_CMD_OPTION_PTZ_TURN_RIGHT = 1;
    private final int USR_CMD_OPTION_PTZ_TURN_UP = 2;
    private BroadcastReceiver baseReceiver = new C06811();
    boolean isBaseRegFilter = false;
    boolean isFullScreen = false;
    public P2PView pView;

    class C06811 extends BroadcastReceiver {
        C06811() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Action.P2P_WINDOW_READY_TO_START)) {
                final MediaPlayer mPlayer = MediaPlayer.getInstance();
                new Thread(new Runnable() {
                    public void run() {
                        MediaPlayer.nativeInit(mPlayer);
                        try {
                            mPlayer.setDisplay(BaseVideoActivity.this.pView);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mPlayer.start(BaseVideoActivity.mVideoFrameRate);
                    }
                }).start();
            }
        }
    }

    private class GestureListener extends SimpleOnGestureListener {
        private GestureListener() {
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            BaseVideoActivity.this.onP2PViewSingleTap();
            return super.onSingleTapConfirmed(e);
        }

        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        public boolean onDoubleTap(MotionEvent e) {
            if (BaseVideoActivity.this.isFullScreen) {
                BaseVideoActivity.this.isFullScreen = false;
                BaseVideoActivity.this.pView.halfScreen();
            } else {
                BaseVideoActivity.this.isFullScreen = true;
                BaseVideoActivity.this.pView.fullScreen();
            }
            return super.onDoubleTap(e);
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }
    }

    protected abstract void onP2PViewSingleTap();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseRegFilter();
    }

    public void baseRegFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.P2P_WINDOW_READY_TO_START);
        registerReceiver(this.baseReceiver, filter);
        this.isBaseRegFilter = true;
    }

    public void initP2PView(int type) {
        this.pView.setCallBack();
        this.pView.setGestureDetector(new GestureDetector(this, new GestureListener(), null, true));
        this.pView.setDeviceType(type);
    }

    public void fillCameraData(byte[] data, int length, int PreviewWidth, int PreviewHeight, int isYUV) {
        MediaPlayer.getInstance()._FillVideoRawFrame(data, data.length, PreviewWidth, PreviewHeight, isYUV);
    }

    public void setMute(boolean bool) {
        try {
            MediaPlayer.getInstance()._SetMute(bool);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean closeLocalCamera() {
        if (MediaPlayer.iLocalVideoControl(1) == 1) {
            return true;
        }
        return false;
    }

    public boolean openLocalCamera() {
        if (MediaPlayer.iLocalVideoControl(0) == 1) {
            return true;
        }
        return false;
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.isBaseRegFilter) {
            unregisterReceiver(this.baseReceiver);
            this.isBaseRegFilter = false;
        }
    }
}
