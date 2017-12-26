package com.p2p.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.p2p.core.GestureDetector.SimpleOnGestureListener;
import com.p2p.core.global.Constants.P2P_WINDOW.Action;
import java.io.IOException;

public abstract class BasePlayBackActivity extends BaseCoreActivity {
    private static final int JUMP = 4;
    private static final int NAMEPLAY = 7;
    private static final int NEXT = 5;
    private static final int PAUSE = 2;
    private static final int PREVIOUS = 6;
    private static final int START = 3;
    private static int mVideoFrameRate = 15;
    private BroadcastReceiver baseReceiver = new C06791();
    boolean isBaseRegFilter = false;
    boolean isFullScreen = false;
    public P2PView pView;

    class C06791 extends BroadcastReceiver {
        C06791() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Action.P2P_WINDOW_READY_TO_START)) {
                final MediaPlayer mPlayer = MediaPlayer.getInstance();
                new Thread(new Runnable() {
                    public void run() {
                        if (P2PValue.HikamDeviceModelList.contains(MediaPlayer.callModel)) {
                            P2pJni.P2PClientSdkInitDecoder();
                        } else {
                            MediaPlayer.nativeInit(mPlayer);
                        }
                        try {
                            mPlayer.setDisplay(BasePlayBackActivity.this.pView);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mPlayer.start(BasePlayBackActivity.mVideoFrameRate);
                    }
                }).start();
            }
        }
    }

    private class GestureListener extends SimpleOnGestureListener {
        private GestureListener() {
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            BasePlayBackActivity.this.onP2PViewSingleTap();
            return super.onSingleTapConfirmed(e);
        }

        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        public boolean onDoubleTap(MotionEvent e) {
            if (BasePlayBackActivity.this.isFullScreen) {
                BasePlayBackActivity.this.isFullScreen = false;
                BasePlayBackActivity.this.pView.halfScreen();
            } else {
                BasePlayBackActivity.this.isFullScreen = true;
                BasePlayBackActivity.this.pView.fullScreen();
            }
            return super.onDoubleTap(e);
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
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

    public void onDestroy() {
        super.onDestroy();
        if (this.isBaseRegFilter) {
            unregisterReceiver(this.baseReceiver);
            this.isBaseRegFilter = false;
        }
    }

    public void pausePlayBack() {
        MediaPlayer.iRecFilePlayingControl(2, 0, "test".getBytes());
    }

    public void startPlayBack() {
        MediaPlayer.iRecFilePlayingControl(3, 0, "test".getBytes());
    }

    public boolean previous(String filename) {
        if (MediaPlayer.iRecFilePlayingControl(6, 0, filename.getBytes()) == 0) {
            return false;
        }
        return true;
    }

    public boolean next(String filename) {
        if (MediaPlayer.iRecFilePlayingControl(5, 0, filename.getBytes()) == 0) {
            return false;
        }
        return true;
    }

    public void jump(int value) {
        MediaPlayer.iRecFilePlayingControl(4, value, "test".getBytes());
    }
}
