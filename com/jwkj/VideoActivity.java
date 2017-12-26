package com.jwkj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.hikam.C0291R;
import com.jwkj.activity.MainActivity;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.MyApp;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.PhoneWatcher;
import com.jwkj.utils.PhoneWatcher.OnCommingCallListener;
import com.p2p.core.BaseVideoActivity;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PView;
import java.util.List;

public class VideoActivity extends BaseVideoActivity implements OnClickListener {
    public static final int P2P_SURFACE_START_PLAYING_HEIGHT = 240;
    public static final int P2P_SURFACE_START_PLAYING_WIDTH = 320;
    private final int DEFAULT_FRAME_RATE = 15;
    private boolean cameraIsShow = true;
    ImageView close_mike;
    RelativeLayout control_bottom;
    private long exitTime = 0;
    ImageView hungup;
    boolean isControlShow = true;
    boolean isRegFilter = false;
    boolean isReject = false;
    boolean isYV12 = false;
    SurfaceView local_surface_camera;
    Camera mCamera;
    Context mContext;
    private H264Encoder mEncoder;
    SurfaceHolder mHolder;
    boolean mIsCloseMike = false;
    PhoneWatcher mPhoneWatcher;
    private boolean mPreviewRunning = false;
    private BroadcastReceiver mReceiver = new C03212();
    private int mWindowHeight;
    private int mWindowWidth;
    ImageView mask_camera;
    ImageView mask_p2p_view;
    OnTouchListener onTouch = new C03223();
    ImageView switch_camera;
    int type;

    class C03212 extends BroadcastReceiver {
        C03212() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(P2P.P2P_REJECT)) {
                VideoActivity.this.reject();
            } else if (intent.getAction().equals(P2P.P2P_CHANGE_IMAGE_TRANSFER)) {
                int state = intent.getIntExtra("state", -1);
                if (state == 0) {
                    VideoActivity.this.mask_p2p_view.setVisibility(8);
                } else if (state == 1) {
                    VideoActivity.this.mask_p2p_view.setVisibility(0);
                } else if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                    VideoActivity.this.reject();
                }
            }
        }
    }

    class C03223 implements OnTouchListener {
        float downHeight;
        long downTime;
        float downWidth;
        boolean isActive = false;
        int mHeight;
        int mWidth;

        C03223() {
        }

        public boolean onTouch(View view, MotionEvent event) {
            int y = (int) event.getY();
            LayoutParams params1 = (LayoutParams) VideoActivity.this.local_surface_camera.getLayoutParams();
            LayoutParams params2 = (LayoutParams) VideoActivity.this.mask_camera.getLayoutParams();
            switch (event.getAction()) {
                case 0:
                    this.downTime = System.currentTimeMillis();
                    this.mWidth = params1.width;
                    this.mHeight = params1.height;
                    this.downWidth = event.getRawX() - ((float) params1.x);
                    this.downHeight = event.getRawY() - ((float) params1.y);
                    this.isActive = true;
                    break;
                case 1:
                    this.isActive = false;
                    if (System.currentTimeMillis() - this.downTime < 100) {
                        if (!VideoActivity.this.cameraIsShow) {
                            if (VideoActivity.this.openLocalCamera()) {
                                VideoActivity.this.cameraIsShow = true;
                                VideoActivity.this.mask_camera.setVisibility(8);
                                VideoActivity.this.local_surface_camera.setVisibility(0);
                                break;
                            }
                        } else if (VideoActivity.this.closeLocalCamera()) {
                            VideoActivity.this.cameraIsShow = false;
                            VideoActivity.this.mask_camera.setVisibility(0);
                            VideoActivity.this.local_surface_camera.setVisibility(8);
                            break;
                        }
                    }
                    break;
                case 2:
                    Log.e("my", "rawxy:" + event.getRawX() + ":" + event.getRawY());
                    int changeX = (int) (event.getRawX() - this.downWidth);
                    if (changeX < 0) {
                        changeX = 0;
                    } else if (changeX > VideoActivity.this.mWindowWidth - this.mWidth) {
                        changeX = VideoActivity.this.mWindowWidth - this.mWidth;
                    }
                    int changeY = (int) (event.getRawY() - this.downHeight);
                    if (changeY < 0) {
                        changeY = 0;
                    } else if (changeY > VideoActivity.this.mWindowHeight - this.mHeight) {
                        changeY = VideoActivity.this.mWindowHeight - this.mHeight;
                    }
                    params1.x = changeX;
                    params1.y = changeY;
                    VideoActivity.this.local_surface_camera.setLayoutParams(params1);
                    params2.x = changeX;
                    params2.y = changeY;
                    VideoActivity.this.mask_camera.setLayoutParams(params2);
                    break;
            }
            return true;
        }
    }

    class H264Encoder implements PreviewCallback {
        private boolean mIsWritingData = false;

        public H264Encoder(int width, int height) {
        }

        public void onPreviewFrame(byte[] data, Camera camera) {
            if (data != null && !this.mIsWritingData) {
                this.mIsWritingData = true;
                Parameters p = camera.getParameters();
                Log.e("debug", p.getPreviewSize().width + ":::::" + p.getPreviewSize().height);
                if (VideoActivity.this.cameraIsShow) {
                    if (VideoActivity.this.isYV12) {
                        VideoActivity.this.fillCameraData(data, data.length, p.getPreviewSize().width, p.getPreviewSize().height, 1);
                    } else {
                        VideoActivity.this.fillCameraData(data, data.length, p.getPreviewSize().width, p.getPreviewSize().height, 0);
                    }
                }
                this.mIsWritingData = false;
            }
        }
    }

    class LocalCameraCallBack implements Callback, PictureCallback {
        LocalCameraCallBack() {
        }

        public void onPictureTaken(byte[] arg0, Camera arg1) {
        }

        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
            if (VideoActivity.this.mPreviewRunning) {
                VideoActivity.this.mCamera.stopPreview();
            }
            try {
                if (VideoActivity.this.mCamera != null) {
                    Parameters parameters = VideoActivity.this.mCamera.getParameters();
                    List<Integer> LRates = parameters.getSupportedPreviewFrameRates();
                    int iFrameRateTmp = 1;
                    for (int i = 0; i < LRates.size(); i++) {
                        int iDiff;
                        int iNewRate = ((Integer) LRates.get(i)).intValue();
                        if (iFrameRateTmp > 15) {
                            iDiff = iFrameRateTmp - 15;
                        } else {
                            iDiff = 15 - iFrameRateTmp;
                        }
                        if (iDiff == 0) {
                            break;
                        }
                        if (iNewRate <= 15 && 15 - iNewRate < iDiff) {
                            iFrameRateTmp = iNewRate;
                        } else if (iNewRate > 15 && iNewRate - 15 < iDiff) {
                            iFrameRateTmp = iNewRate;
                        }
                    }
                    if (iFrameRateTmp > 7 || iFrameRateTmp < 22) {
                        BaseVideoActivity.mVideoFrameRate = iFrameRateTmp;
                        parameters.setPreviewFrameRate(BaseVideoActivity.mVideoFrameRate);
                    }
                    for (Size size : parameters.getSupportedPreviewSizes()) {
                        Log.e("debug", size.width + ":" + size.height);
                    }
                    parameters.setPreviewSize(320, 240);
                    parameters.set("orientation", "landscape");
                    VideoActivity.this.setFormat(parameters);
                    VideoActivity.this.mCamera.setDisplayOrientation(0);
                    VideoActivity.this.mCamera.setParameters(parameters);
                    VideoActivity.this.mEncoder = new H264Encoder(VideoActivity.this.mCamera.getParameters().getPreviewSize().width, VideoActivity.this.mCamera.getParameters().getPreviewSize().height);
                    VideoActivity.this.mCamera.setPreviewCallback(VideoActivity.this.mEncoder);
                    VideoActivity.this.mCamera.startPreview();
                    VideoActivity.this.mPreviewRunning = true;
                }
            } catch (Exception e) {
                C0568T.showShort(VideoActivity.this.mContext, (int) C0291R.string.camera_error);
                VideoActivity.this.releaseCamera();
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            try {
                VideoActivity.this.mCamera = XCamera.open();
                VideoActivity.this.mCamera.setPreviewDisplay(VideoActivity.this.mHolder);
            } catch (Exception e) {
                if (VideoActivity.this.mCamera != null) {
                    VideoActivity.this.releaseCamera();
                }
            }
        }

        public void surfaceDestroyed(SurfaceHolder arg0) {
            VideoActivity.this.releaseCamera();
        }
    }

    class C10691 implements OnCommingCallListener {
        C10691() {
        }

        public void onCommingCall() {
            VideoActivity.this.reject();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        P2PConnect.setPlaying(true);
        Window win = getWindow();
        win.addFlags(4718592);
        win.addFlags(2097280);
        setContentView(C0291R.layout.p2p_video);
        this.type = getIntent().getIntExtra("type", -1);
        this.mContext = this;
        initComponent();
        regFilter();
        startWatcher();
        openCamera();
    }

    public void initComponent() {
        this.pView = (P2PView) findViewById(C0291R.id.pView);
        initP2PView(P2PConnect.getCurrentDeviceType());
        this.switch_camera = (ImageView) findViewById(C0291R.id.switch_camera);
        this.hungup = (ImageView) findViewById(C0291R.id.hungup);
        this.close_mike = (ImageView) findViewById(C0291R.id.close_mike);
        this.local_surface_camera = (SurfaceView) findViewById(C0291R.id.local_surface_camera);
        this.mask_camera = (ImageView) findViewById(C0291R.id.mask_camera);
        this.mask_p2p_view = (ImageView) findViewById(C0291R.id.mask_p2p_view);
        this.control_bottom = (RelativeLayout) findViewById(C0291R.id.control_bottom);
        this.mask_camera.setOnTouchListener(this.onTouch);
        this.local_surface_camera.setOnTouchListener(this.onTouch);
        this.switch_camera.setOnClickListener(this);
        this.hungup.setOnClickListener(this);
        this.close_mike.setOnClickListener(this);
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        this.mWindowWidth = dm.widthPixels;
        this.mWindowHeight = dm.heightPixels;
        LayoutParams params1 = (LayoutParams) this.local_surface_camera.getLayoutParams();
        LayoutParams params2 = (LayoutParams) this.mask_camera.getLayoutParams();
        params1.x = this.mWindowWidth - params1.width;
        this.local_surface_camera.setLayoutParams(params1);
        params2.x = this.mWindowWidth - params2.width;
        this.mask_camera.setLayoutParams(params2);
    }

    public void onHomePressed() {
        super.onHomePressed();
        reject();
    }

    private void startWatcher() {
        this.mPhoneWatcher = new PhoneWatcher(this.mContext);
        this.mPhoneWatcher.setOnCommingCallListener(new C10691());
        this.mPhoneWatcher.startWatcher();
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P.P2P_REJECT);
        filter.addAction(P2P.P2P_CHANGE_IMAGE_TRANSFER);
        filter.addAction("android.intent.action.SCREEN_OFF");
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.isRegFilter = true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.close_mike:
                if (this.mIsCloseMike) {
                    this.close_mike.setImageResource(C0291R.drawable.btn_no_sound);
                    setMute(false);
                    this.mIsCloseMike = false;
                    return;
                }
                this.close_mike.setImageResource(C0291R.drawable.btn_no_sound_s);
                setMute(true);
                this.mIsCloseMike = true;
                return;
            case C0291R.id.hungup:
                reject();
                return;
            case C0291R.id.local_surface_camera:
                if (this.cameraIsShow && closeLocalCamera()) {
                    Log.e("my", "close camera");
                    this.cameraIsShow = false;
                    this.mask_camera.setVisibility(0);
                    this.local_surface_camera.setVisibility(8);
                    return;
                }
                return;
            case C0291R.id.mask_camera:
                if (!this.cameraIsShow && openLocalCamera()) {
                    Log.e("my", "open camera");
                    this.cameraIsShow = true;
                    this.mask_camera.setVisibility(8);
                    this.local_surface_camera.setVisibility(0);
                    return;
                }
                return;
            case C0291R.id.switch_camera:
                swtichCamera();
                return;
            default:
                return;
        }
    }

    public void changeControl() {
        if (this.isControlShow) {
            this.isControlShow = false;
            Animation anim2 = AnimationUtils.loadAnimation(this, 17432577);
            anim2.setDuration(200);
            this.control_bottom.startAnimation(anim2);
            this.control_bottom.setVisibility(8);
            return;
        }
        this.isControlShow = true;
        this.control_bottom.setVisibility(0);
        anim2 = AnimationUtils.loadAnimation(this, 17432576);
        anim2.setDuration(200);
        this.control_bottom.startAnimation(anim2);
    }

    private void swtichCamera() {
        try {
            if (Camera.getNumberOfCameras() >= 2) {
                releaseCamera();
                this.mCamera = XCamera.switchCamera();
                if (this.mCamera != null) {
                    this.mCamera.setPreviewDisplay(this.mHolder);
                    Parameters parameters = this.mCamera.getParameters();
                    List<Integer> LRates = parameters.getSupportedPreviewFrameRates();
                    int iFrameRateTmp = 1;
                    for (int i = 0; i < LRates.size(); i++) {
                        int iDiff;
                        int iNewRate = ((Integer) LRates.get(i)).intValue();
                        if (iFrameRateTmp > 15) {
                            iDiff = iFrameRateTmp - 15;
                        } else {
                            iDiff = 15 - iFrameRateTmp;
                        }
                        if (iDiff == 0) {
                            break;
                        }
                        if (iNewRate <= 15 && 15 - iNewRate < iDiff) {
                            iFrameRateTmp = iNewRate;
                        } else if (iNewRate > 15 && iNewRate - 15 < iDiff) {
                            iFrameRateTmp = iNewRate;
                        }
                    }
                    if (iFrameRateTmp > 7 || iFrameRateTmp < 22) {
                        mVideoFrameRate = iFrameRateTmp;
                        parameters.setPreviewFrameRate(mVideoFrameRate);
                    }
                    parameters.setPreviewSize(320, 240);
                    parameters.set("orientation", "landscape");
                    setFormat(parameters);
                    this.mCamera.setDisplayOrientation(0);
                    this.mCamera.setParameters(parameters);
                    this.mEncoder = new H264Encoder(this.mCamera.getParameters().getPreviewSize().width, this.mCamera.getParameters().getPreviewSize().height);
                    this.mCamera.setPreviewCallback(this.mEncoder);
                    this.mCamera.startPreview();
                    this.mPreviewRunning = true;
                }
            }
        } catch (Exception e) {
            releaseCamera();
        }
    }

    private void setFormat(Parameters p) {
        List<Integer> supportList = p.getSupportedPreviewFormats();
        if (supportList != null && supportList.size() != 0) {
            int i;
            for (i = 0; i < supportList.size(); i++) {
                Log.e("my", ((Integer) supportList.get(i)).intValue() + "");
            }
            i = 0;
            while (i < supportList.size()) {
                int format = ((Integer) supportList.get(i)).intValue();
                if (17 == format) {
                    p.setPreviewFormat(17);
                    this.isYV12 = false;
                    return;
                } else if (842094169 == format) {
                    p.setPreviewFormat(842094169);
                    this.isYV12 = true;
                    return;
                } else {
                    i++;
                }
            }
        }
    }

    public void onBackPressed() {
        reject();
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.isRegFilter) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.isRegFilter = false;
        }
        if (this.mPhoneWatcher != null) {
            this.mPhoneWatcher.stopWatcher();
        }
        P2PConnect.setPlaying(false);
        if (!activity_stack.containsKey(Integer.valueOf(1))) {
            startActivity(new Intent(this, MainActivity.class));
        }
        Intent refreshContans = new Intent();
        refreshContans.setAction(Action.REFRESH_CONTANTS);
        this.mContext.sendBroadcast(refreshContans);
    }

    public void openCamera() {
        this.mHolder = this.local_surface_camera.getHolder();
        this.mHolder.addCallback(new LocalCameraCallBack());
        this.mHolder.setType(3);
        this.local_surface_camera.setZOrderOnTop(true);
    }

    public synchronized void releaseCamera() {
        if (this.mCamera != null) {
            Log.e("p2p", "releaseCamera");
            this.mCamera.setPreviewCallback(null);
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
        }
    }

    public void reject() {
        if (!this.isReject) {
            this.isReject = true;
            P2PHandler.getInstance().reject();
            releaseCamera();
            finish();
        }
    }

    public int getActivityInfo() {
        return 34;
    }

    protected void onP2PViewSingleTap() {
        changeControl();
    }

    protected void onGoBack() {
        MyApp.app.showNotification();
    }

    protected void onGoFront() {
        MyApp.app.hideNotification();
    }

    protected void onExit() {
        MyApp.app.hideNotification();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || event.getAction() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        if (System.currentTimeMillis() - this.exitTime > 2000) {
            Toast.makeText(getApplicationContext(), C0291R.string.Press_again, 0).show();
            C0568T.showShort(this.mContext, (int) C0291R.string.Press_again);
            this.exitTime = System.currentTimeMillis();
        } else {
            reject();
        }
        return true;
    }
}
