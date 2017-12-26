package com.jwkj.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;
import com.hikam.C0291R;
import com.jwkj.adapter.ImageBrowserAdapter;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.Utils;
import java.io.File;
import java.io.FileFilter;

public class ImageBrowser extends BaseActivity implements OnClickListener {
    public static final float SCALE_MAX = 4.0f;
    ImageBrowserAdapter adapter;
    ImageView back;
    Context context;
    ImageView curImageView;
    String curPath;
    View curView;
    File[] files;
    private float initScale = 1.0f;
    int length;
    GridView list;
    private AlertDialog mDeleteDialog;
    ScaleGestureDetector mScaleGestureDetector = null;
    private final Matrix mScaleMatrix = new Matrix();
    Bitmap mTempBitmap;
    private final float[] matrixValues = new float[9];
    int screenHeight;
    int screenWidth;
    int selectedItem;
    ImageSwitcher switcher;

    class C03831 implements FileFilter {
        C03831() {
        }

        public boolean accept(File pathname) {
            if (pathname.getName().endsWith(".jpg")) {
                return true;
            }
            return false;
        }
    }

    class C03842 implements OnClickListener {
        C03842() {
        }

        public void onClick(View view) {
            String path = Utils.getPathFromUri(ImageBrowser.this.context, Uri.parse(Media.insertImage(ImageBrowser.this.context.getContentResolver(), ImageBrowser.this.mTempBitmap, "", "")));
            C0568T.showShort(ImageBrowser.this.context, (int) C0291R.string.saveto_success);
            ImageBrowser.this.context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse("file://" + path)));
        }
    }

    class C03853 implements ViewFactory {
        C03853() {
        }

        public View makeView() {
            ImageBrowser.this.curImageView = new ImageView(ImageBrowser.this.context);
            ImageBrowser.this.curImageView.setScaleType(ScaleType.FIT_CENTER);
            ImageBrowser.this.curImageView.setLayoutParams(new LayoutParams(-1, -1));
            Log.e("my", Runtime.getRuntime().totalMemory() + "");
            return ImageBrowser.this.curImageView;
        }
    }

    class C03864 implements OnGestureListener {
        C03864() {
        }

        public boolean onDown(MotionEvent arg0) {
            return false;
        }

        public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
            float distance = arg0.getRawX() - arg1.getRawX();
            ImageBrowser imageBrowser;
            int i;
            if (distance > 0.0f && Math.abs(distance) > 30.0f) {
                imageBrowser = ImageBrowser.this;
                i = imageBrowser.selectedItem + 1;
                imageBrowser.selectedItem = i;
                if (i < ImageBrowser.this.files.length) {
                    ImageBrowser.this.switcher.setInAnimation(AnimationUtils.loadAnimation(ImageBrowser.this.context, C0291R.anim.slide_in_right_100));
                    ImageBrowser.this.switcher.setOutAnimation(AnimationUtils.loadAnimation(ImageBrowser.this.context, C0291R.anim.slide_out_left_100));
                    ImageBrowser.this.curPath = ImageBrowser.this.files[ImageBrowser.this.selectedItem].getPath();
                    ImageBrowser.this.mTempBitmap = BitmapFactory.decodeFile(ImageBrowser.this.curPath);
                    ImageBrowser.this.curImageView.setImageBitmap(ImageBrowser.this.mTempBitmap);
                    ImageBrowser.this.curImageView.setScaleType(ScaleType.FIT_CENTER);
                    ImageBrowser.this.curImageView.invalidate();
                    ImageBrowser.this.switcher.setImageDrawable(new BitmapDrawable(ImageBrowser.this.mTempBitmap));
                } else {
                    ImageBrowser.this.selectedItem = ImageBrowser.this.files.length - 1;
                }
                Log.e("my", Runtime.getRuntime().totalMemory() + "");
            } else if (distance < 0.0f && Math.abs(distance) > 30.0f) {
                imageBrowser = ImageBrowser.this;
                i = imageBrowser.selectedItem - 1;
                imageBrowser.selectedItem = i;
                if (i >= 0) {
                    ImageBrowser.this.switcher.setInAnimation(AnimationUtils.loadAnimation(ImageBrowser.this.context, C0291R.anim.slide_in_left_100));
                    ImageBrowser.this.switcher.setOutAnimation(AnimationUtils.loadAnimation(ImageBrowser.this.context, C0291R.anim.slide_out_right_100));
                    ImageBrowser.this.curPath = ImageBrowser.this.files[ImageBrowser.this.selectedItem].getPath();
                    ImageBrowser.this.mTempBitmap = BitmapFactory.decodeFile(ImageBrowser.this.curPath);
                    ImageBrowser.this.curImageView.setImageBitmap(ImageBrowser.this.mTempBitmap);
                    ImageBrowser.this.curImageView.setScaleType(ScaleType.FIT_CENTER);
                    ImageBrowser.this.curImageView.invalidate();
                    ImageBrowser.this.switcher.setImageDrawable(new BitmapDrawable(ImageBrowser.this.mTempBitmap));
                } else {
                    ImageBrowser.this.selectedItem = 0;
                }
                Log.e("my", Runtime.getRuntime().totalMemory() + "");
            }
            return true;
        }

        public void onLongPress(MotionEvent arg0) {
        }

        public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
            return false;
        }

        public void onShowPress(MotionEvent arg0) {
        }

        public boolean onSingleTapUp(MotionEvent arg0) {
            return false;
        }
    }

    public class ScaleGestureListener implements OnScaleGestureListener {
        public boolean onScale(ScaleGestureDetector detector) {
            float scale = ImageBrowser.this.getScale();
            float scaleFactor = detector.getScaleFactor();
            if ((scale < ImageBrowser.SCALE_MAX && scaleFactor > 1.0f) || (scale > ImageBrowser.this.initScale && scaleFactor < 1.0f)) {
                if (scaleFactor * scale < ImageBrowser.this.initScale) {
                    scaleFactor = ImageBrowser.this.initScale / scale;
                }
                if (scaleFactor * scale > ImageBrowser.SCALE_MAX) {
                    scaleFactor = ImageBrowser.SCALE_MAX / scale;
                }
                ImageBrowser.this.mScaleMatrix.postScale(scaleFactor, scaleFactor);
                Bitmap bmp = BitmapFactory.decodeFile(ImageBrowser.this.curPath);
                ImageBrowser.this.curImageView.setImageBitmap(Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), ImageBrowser.this.mScaleMatrix, true));
                ImageBrowser.this.curImageView.setScaleType(ScaleType.MATRIX);
                ImageBrowser.this.curImageView.invalidate();
            }
            return true;
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.image_browser);
        this.context = this;
        if (this.files == null) {
            this.files = new File[0];
        }
        this.screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        this.screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        initComponent();
    }

    public void initComponent() {
        this.list = (GridView) findViewById(C0291R.id.list_grid);
        this.back = (ImageView) findViewById(C0291R.id.back_btn);
        DisplayMetrics dm = new DisplayMetrics();
        this.mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureListener());
        this.adapter = new ImageBrowserAdapter(this);
        this.list.setAdapter(this.adapter);
        this.back.setOnClickListener(this);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LayoutParams params = (LayoutParams) this.curView.getLayoutParams();
        int ori = getResources().getConfiguration().orientation;
        if (ori == 2) {
            params.width = this.screenHeight;
            params.height = this.screenWidth;
        } else if (ori == 1) {
            params.width = this.screenWidth;
            params.height = this.screenHeight;
        }
        this.curView.setLayoutParams(params);
    }

    public void createGalleryDialog(int position) {
        setRequestedOrientation(4);
        LayoutInflater factor = (LayoutInflater) this.context.getSystemService("layout_inflater");
        this.curView = LayoutInflater.from(this.context).inflate(C0291R.layout.dialog_gallery, null);
        TextView saveto = (TextView) this.curView.findViewById(C0291R.id.tv_saveto);
        this.files = new File(Environment.getExternalStorageDirectory().getPath() + "/screenshot").listFiles(new C03831());
        this.curPath = this.files[position].getPath();
        this.mTempBitmap = BitmapFactory.decodeFile(this.curPath);
        saveto.setOnClickListener(new C03842());
        this.selectedItem = position;
        this.switcher = (ImageSwitcher) this.curView.findViewById(C0291R.id.img_container);
        this.switcher.setFactory(new C03853());
        this.switcher.setImageDrawable(new BitmapDrawable(this.mTempBitmap));
        final GestureDetector gd = new GestureDetector(new C03864());
        this.switcher.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                gd.onTouchEvent(arg1);
                return true;
            }
        });
        this.mDeleteDialog = new Builder(this.context, C0291R.style.hikamDialog).create();
        this.mDeleteDialog.show();
        this.mDeleteDialog.setContentView(this.curView);
        LayoutParams layout = new LayoutParams(-1, -1);
        LayoutParams params = (LayoutParams) this.curView.getLayoutParams();
        int ori = getResources().getConfiguration().orientation;
        if (ori == 2) {
            params.width = this.screenHeight;
            params.height = this.screenWidth;
        } else if (ori == 1) {
            params.width = this.screenWidth;
            params.height = this.screenHeight;
        }
        this.curView.setLayoutParams(params);
    }

    public final float getScale() {
        this.mScaleMatrix.getValues(this.matrixValues);
        return this.matrixValues[0];
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            default:
                return;
        }
    }

    public int getActivityInfo() {
        return 3;
    }
}
