package com.jwkj.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import com.jwkj.utils.ImageResizer;

public class MyImageView extends ImageView {
    public static final int IMAGE_HEIGHT = 90;
    public static final int IMAGE_WIDTH = 120;
    private Bitmap mBitmap;
    private String mPath;

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility != 8) {
            if (this.mBitmap != null) {
                this.mBitmap.recycle();
                this.mBitmap = null;
            }
            if (this.mPath != null) {
                ImageResizer resizer = new ImageResizer();
                this.mBitmap = ImageResizer.resize(this.mPath, 120.0f, 90.0f);
                setImageBitmap(this.mBitmap);
            }
        } else if (this.mBitmap != null) {
            this.mBitmap.recycle();
            this.mBitmap = null;
        }
        Log.e("my", "onWindowVisibilityChanged:" + visibility);
    }

    public void setImageFilePath(String path) {
        this.mPath = path;
        if (this.mBitmap != null) {
            this.mBitmap.recycle();
            this.mBitmap = null;
        }
        ImageResizer resizer = new ImageResizer();
        this.mBitmap = ImageResizer.resize(this.mPath, 120.0f, 90.0f);
        setImageBitmap(this.mBitmap);
    }
}
