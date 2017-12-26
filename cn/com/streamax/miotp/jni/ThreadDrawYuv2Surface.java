package cn.com.streamax.miotp.jni;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.SurfaceHolder;

public class ThreadDrawYuv2Surface implements Runnable {
    public static Bitmap srcBtm;
    private Canvas f9c;
    private SurfaceHolder holder;
    private Matrix matrix = new Matrix();
    private Paint f10p = new Paint();
    private VideoFrameStructure vfs;

    public ThreadDrawYuv2Surface(SurfaceHolder holder, VideoFrameStructure vfs) {
        this.holder = holder;
        this.vfs = vfs;
    }

    public void run() {
        this.matrix.setScale(((float) this.holder.getSurfaceFrame().width()) / ((float) this.vfs.width), ((float) this.holder.getSurfaceFrame().height()) / ((float) this.vfs.height));
        srcBtm = Bitmap.createBitmap(this.vfs.pRgb, this.vfs.width, this.vfs.height, Config.RGB_565);
        if (this.holder != null) {
            this.f9c = this.holder.lockCanvas();
            if (this.f9c != null) {
                this.f9c.drawBitmap(srcBtm, this.matrix, this.f10p);
                this.holder.unlockCanvasAndPost(this.f9c);
            }
        }
    }
}
