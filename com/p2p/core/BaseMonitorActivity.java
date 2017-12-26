package com.p2p.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.p2p.core.GestureDetector.SimpleOnGestureListener;
import com.p2p.core.MediaPlayer.ICapture;
import com.p2p.core.global.Constants.P2P_WINDOW.Action;
import com.p2p.core.utils.MyUtils;
import java.io.IOException;

public abstract class BaseMonitorActivity extends BaseCoreActivity {
    public static int mVideoFrameRate = 15;
    private final int MINX = 50;
    private final int MINY = 25;
    private final int MSG_SHOW_CAPTURERESULT = 2;
    private final int USR_CMD_OPTION_PTZ_TURN_DOWN = 3;
    private final int USR_CMD_OPTION_PTZ_TURN_LEFT = 0;
    private final int USR_CMD_OPTION_PTZ_TURN_RIGHT = 1;
    private final int USR_CMD_OPTION_PTZ_TURN_UP = 2;
    private BroadcastReceiver baseReceiver = new C06761();
    private String callId;
    boolean isBaseRegFilter = false;
    boolean isFullScreen = false;
    private Handler mHandler = new Handler(new C06772());
    public P2PView pView;
    private String pwd;

    class C06761 extends BroadcastReceiver {
        C06761() {
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
                            mPlayer.setDisplay(BaseMonitorActivity.this.pView);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mPlayer.start(BaseMonitorActivity.mVideoFrameRate);
                        BaseMonitorActivity.this.setMute(true);
                    }
                }).start();
            }
        }
    }

    class C06772 implements Callback {
        C06772() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    if (msg.arg1 != 1) {
                        BaseMonitorActivity.this.onCaptureScreenResult(false);
                        break;
                    }
                    BaseMonitorActivity.this.onCaptureScreenResult(true);
                    break;
            }
            return false;
        }
    }

    private class CaptureListener implements ICapture {
        Handler mSubHandler;

        public CaptureListener(Handler handler) {
            this.mSubHandler = handler;
        }

        public void vCaptureResult(int result) {
            Message msg = new Message();
            msg.what = 2;
            msg.arg1 = result;
            this.mSubHandler.sendMessage(msg);
        }
    }

    private class GestureListener extends SimpleOnGestureListener {
        private GestureListener() {
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            BaseMonitorActivity.this.onP2PViewSingleTap();
            Log.e("ee", "onSingleTapConfirmed");
            return super.onSingleTapConfirmed(e);
        }

        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        public boolean onDoubleTap(MotionEvent e) {
            if (BaseMonitorActivity.this.isFullScreen) {
                Log.e("ee", "onDoubleTap pView.halfScreen(); ");
                BaseMonitorActivity.this.isFullScreen = false;
                BaseMonitorActivity.this.pView.halfScreen();
            } else {
                Log.e("ee", "onDoubleTap pView.fullScreen();");
                BaseMonitorActivity.this.isFullScreen = true;
                BaseMonitorActivity.this.pView.fullScreen();
            }
            return super.onDoubleTap(e);
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int id = -1;
            boolean ishorizontal = false;
            Log.e("ee", "onFling");
            if (Math.abs(e2.getX() - e1.getX()) > Math.abs(e2.getY() - e1.getY())) {
                ishorizontal = true;
            }
            float distance;
            if (ishorizontal) {
                distance = e2.getX() - e1.getX();
                if (Math.abs(distance) > ((float) MyUtils.dip2px(BaseMonitorActivity.this, 50))) {
                    id = distance > 0.0f ? 1 : 0;
                }
            } else {
                distance = e2.getY() - e1.getY();
                if (Math.abs(distance) > ((float) MyUtils.dip2px(BaseMonitorActivity.this, 25))) {
                    id = distance > 0.0f ? 2 : 3;
                }
            }
            if (id != -1) {
                MediaPlayer.getInstance().native_p2p_controls(BaseMonitorActivity.this.callId, BaseMonitorActivity.this.pwd, id);
            }
            return true;
        }
    }

    protected abstract void onCaptureScreenResult(boolean z);

    protected abstract void onP2PViewSingleTap();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseRegFilter();
        MediaPlayer.getInstance().setCaptureListener(new CaptureListener(this.mHandler));
    }

    public void initData(String id, String pwd) {
        this.callId = id;
        this.pwd = pwd;
    }

    public void initP2PView(int type) {
        this.pView.setCallBack();
        this.pView.setGestureDetector(new GestureDetector(this, new GestureListener(), null, true));
        this.pView.setDeviceType(type);
    }

    private void baseRegFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.P2P_WINDOW_READY_TO_START);
        registerReceiver(this.baseReceiver, filter);
        this.isBaseRegFilter = true;
    }

    public void setMute(boolean bool) {
        try {
            MediaPlayer.getInstance()._SetMute(bool);
            MediaPlayer.isMute = bool;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void captureScreen() {
        try {
            MediaPlayer.getInstance()._CaptureScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.isBaseRegFilter) {
            unregisterReceiver(this.baseReceiver);
            this.isBaseRegFilter = false;
        }
    }
}
