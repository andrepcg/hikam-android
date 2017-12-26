package cn.com.streamax.miotp.jni;

import android.util.Log;
import android.view.SurfaceView;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoCallback {
    private static final String TAG = VideoCallback.class.getSimpleName();
    private static ExecutorService exec = Executors.newCachedThreadPool();
    public static SurfaceView sv;
    private static VideoCallback videoCallback;
    private boolean flag = false;

    public static VideoCallback newInstance() {
        if (videoCallback == null) {
            videoCallback = new VideoCallback();
        }
        return videoCallback;
    }

    private VideoCallback() {
    }

    public void invoke(VideoFrameStructure vfs) {
        synchronized (this) {
            if (this.flag) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (sv != null) {
            exec.submit(new ThreadDrawYuv2Surface(sv.getHolder(), vfs));
        } else {
            Log.e(TAG, "sv is null");
        }
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
