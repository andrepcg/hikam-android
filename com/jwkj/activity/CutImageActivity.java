package com.jwkj.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.hikam.C0291R;
import com.jwkj.data.Contact;
import com.jwkj.data.ContactDB;
import com.jwkj.global.Constants.Image;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.C0568T;
import com.jwkj.utils.ImageUtils;
import java.io.File;

public class CutImageActivity extends BaseActivity implements OnClickListener {
    public static final int TOUCH_EVENT_TYPE_DRAG = 1;
    public static final int TOUCH_EVENT_TYPE_ZOOM = 0;
    ImageView back_btn;
    int bottom_height;
    ImageView header_img;
    int height;
    int initX;
    int initY;
    RelativeLayout layout_bottom;
    ImageView layout_cut;
    Contact mContact;
    Context mContext;
    int mWindowHeight;
    int mWindowWidth;
    Bitmap mainBitmap;
    int maxWidth;
    int minWidth = 150;
    OnTouchListener onTouch = new C03621();
    Button save;
    Bitmap saveBitmap;
    float scale;
    ImageView temp;
    Bitmap tempBitmap;
    int type;
    int width;

    class C03621 implements OnTouchListener {
        float downHeight;
        float downWidth;
        boolean isActive = false;
        int mHeight;
        int mWidth;
        int mode;
        float newDist;
        float oldDist;

        C03621() {
        }

        public boolean onTouch(View view, MotionEvent event) {
            LayoutParams params = (LayoutParams) CutImageActivity.this.layout_cut.getLayoutParams();
            switch (event.getAction()) {
                case 0:
                    this.mode = 1;
                    this.mWidth = params.width;
                    this.mHeight = params.height;
                    this.downWidth = event.getRawX() - ((float) params.x);
                    this.downHeight = event.getRawY() - ((float) params.y);
                    this.isActive = true;
                    break;
                case 2:
                    if (this.mode != 0) {
                        int changeX = 0;
                        int changeY = 0;
                        switch (CutImageActivity.this.type) {
                            case 0:
                                changeX = (int) (event.getRawX() - this.downWidth);
                                if (changeX < CutImageActivity.this.initX) {
                                    changeX = CutImageActivity.this.initX;
                                } else if (changeX > CutImageActivity.this.width - this.mWidth) {
                                    changeX = CutImageActivity.this.width - this.mWidth;
                                }
                                changeY = (int) (event.getRawY() - this.downHeight);
                                if (changeY >= CutImageActivity.this.initY) {
                                    if (changeY > (CutImageActivity.this.height - this.mHeight) + CutImageActivity.this.initY) {
                                        changeY = (CutImageActivity.this.height - this.mHeight) + CutImageActivity.this.initY;
                                        break;
                                    }
                                }
                                changeY = CutImageActivity.this.initY;
                                break;
                                break;
                            case 1:
                                changeX = (int) (event.getRawX() - this.downWidth);
                                if (changeX < CutImageActivity.this.initX) {
                                    changeX = CutImageActivity.this.initX;
                                } else if (changeX > (CutImageActivity.this.width - this.mWidth) + CutImageActivity.this.initX) {
                                    changeX = (CutImageActivity.this.width - this.mWidth) + CutImageActivity.this.initX;
                                }
                                changeY = (int) (event.getRawY() - this.downHeight);
                                if (changeY >= CutImageActivity.this.initY) {
                                    if (changeY > CutImageActivity.this.height - this.mHeight) {
                                        changeY = CutImageActivity.this.height - this.mHeight;
                                        break;
                                    }
                                }
                                changeY = CutImageActivity.this.initY;
                                break;
                                break;
                            case 2:
                                changeX = (int) (event.getRawX() - this.downWidth);
                                if (changeX < CutImageActivity.this.initX) {
                                    changeX = CutImageActivity.this.initX;
                                } else if (changeX > CutImageActivity.this.width - this.mWidth) {
                                    changeX = CutImageActivity.this.width - this.mWidth;
                                }
                                changeY = (int) (event.getRawY() - this.downHeight);
                                if (changeY >= CutImageActivity.this.initY) {
                                    if (changeY > (CutImageActivity.this.height - this.mHeight) + CutImageActivity.this.initY) {
                                        changeY = (CutImageActivity.this.height - this.mHeight) + CutImageActivity.this.initY;
                                        break;
                                    }
                                }
                                changeY = CutImageActivity.this.initY;
                                break;
                                break;
                        }
                        params.x = changeX;
                        params.y = changeY;
                        CutImageActivity.this.layout_cut.setLayoutParams(params);
                        CutImageActivity.this.updateHeader();
                        break;
                    }
                    this.newDist = CutImageActivity.this.spacing(event);
                    if (this.newDist != 0.0f) {
                        CutImageActivity.this.updateCutLayout(this.newDist - this.oldDist);
                        this.oldDist = this.newDist;
                        break;
                    }
                    this.mode = 1;
                    break;
                case 261:
                    this.mode = 0;
                    this.oldDist = CutImageActivity.this.spacing(event);
                    Log.e("my", "ACTION_POINTER_DOWN:" + this.oldDist);
                    break;
            }
            return true;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_cut_image);
        this.mContext = this;
        initCompoment();
        init();
        checkImageSize();
        updateHeader();
        this.mContact = (Contact) getIntent().getSerializableExtra(ContactDB.TABLE_NAME);
    }

    public void initCompoment() {
        this.temp = (ImageView) findViewById(C0291R.id.temp);
        this.header_img = (ImageView) findViewById(C0291R.id.header_img);
        this.save = (Button) findViewById(C0291R.id.save);
        this.back_btn = (ImageView) findViewById(C0291R.id.back_btn);
        this.layout_bottom = (RelativeLayout) findViewById(C0291R.id.layout_bottom);
        this.layout_cut = (ImageView) findViewById(C0291R.id.layout_cut);
        this.layout_cut.setOnTouchListener(this.onTouch);
        this.back_btn.setOnClickListener(this);
        this.save.setOnClickListener(this);
    }

    public void init() {
        try {
            this.mainBitmap = ImageUtils.getBitmap(new File("/sdcard/2cu/temp"), 500, 500);
            DisplayMetrics dm = new DisplayMetrics();
            dm = getResources().getDisplayMetrics();
            this.mWindowWidth = dm.widthPixels;
            this.mWindowHeight = dm.heightPixels;
            LayoutParams params1 = (LayoutParams) this.temp.getLayoutParams();
            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) this.layout_bottom.getLayoutParams();
            LayoutParams params3 = (LayoutParams) this.layout_cut.getLayoutParams();
            float x1 = (float) this.mainBitmap.getWidth();
            float y1 = (float) this.mainBitmap.getHeight();
            float x2;
            float y2;
            if (x1 > y1) {
                x2 = (float) this.mWindowWidth;
                y2 = (x2 * y1) / x1;
                if (y2 < ((float) this.mWindowHeight)) {
                    this.scale = x1 / x2;
                    this.width = (int) x2;
                    this.height = (int) y2;
                    this.initX = 0;
                    this.initY = ((this.mWindowHeight - params2.height) / 2) - (this.height / 2);
                    params1.width = this.width;
                    params1.height = this.height;
                    params1.x = this.initX;
                    params1.y = this.initY;
                    this.temp.setLayoutParams(params1);
                    this.temp.setImageBitmap(this.mainBitmap);
                    params3.x = this.initX;
                    params3.y = this.initY;
                    params3.width = this.height;
                    params3.height = this.height;
                    this.maxWidth = this.height;
                    this.layout_cut.setLayoutParams(params3);
                    this.type = 0;
                }
            } else if (x1 < y1) {
                y2 = (float) (this.mWindowHeight - params2.height);
                x2 = (x1 * y2) / y1;
                if (x2 >= ((float) this.mWindowWidth)) {
                    x2 = (float) this.mWindowWidth;
                    y2 = (x2 * y1) / x1;
                    this.scale = x1 / x2;
                    this.width = (int) x2;
                    this.height = (int) y2;
                    this.initX = 0;
                    this.initY = ((this.mWindowHeight - params2.height) / 2) - (this.height / 2);
                    params1.width = this.width;
                    params1.height = this.height;
                    params1.x = this.initX;
                    params1.y = this.initY;
                    this.temp.setLayoutParams(params1);
                    this.temp.setImageBitmap(this.mainBitmap);
                    params3.x = this.initX;
                    params3.y = this.initY;
                    params3.width = this.width;
                    params3.height = this.width;
                    this.maxWidth = this.width;
                    this.layout_cut.setLayoutParams(params3);
                    this.type = 0;
                    return;
                }
                this.scale = y1 / y2;
                this.width = (int) x2;
                this.height = (int) y2;
                this.initX = (this.mWindowWidth / 2) - (this.width / 2);
                this.initY = 0;
                params1.width = this.width;
                params1.height = this.height;
                params1.x = this.initX;
                params1.y = this.initY;
                this.temp.setLayoutParams(params1);
                this.temp.setImageBitmap(this.mainBitmap);
                params3.x = this.initX;
                params3.y = this.initY;
                params3.width = this.width;
                params3.height = this.width;
                this.maxWidth = this.width;
                this.layout_cut.setLayoutParams(params3);
                this.type = 1;
            } else {
                x2 = (float) this.mWindowWidth;
                y2 = (float) this.mWindowWidth;
                this.scale = x1 / x2;
                this.width = (int) x2;
                this.height = (int) y2;
                this.initX = 0;
                this.initY = ((this.mWindowHeight - params2.height) / 2) - (this.height / 2);
                params1.width = this.width;
                params1.height = this.height;
                params1.x = this.initX;
                params1.y = this.initY;
                this.temp.setLayoutParams(params1);
                this.temp.setImageBitmap(this.mainBitmap);
                params3.x = this.initX;
                params3.y = this.initY;
                params3.width = this.width - 1;
                params3.height = this.width - 1;
                this.maxWidth = this.width - 1;
                this.layout_cut.setLayoutParams(params3);
                this.type = 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float spacing(MotionEvent event) {
        try {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt((double) ((x * x) + (y * y)));
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    void updateCutLayout(float value) {
        LayoutParams params = (LayoutParams) this.layout_cut.getLayoutParams();
        if (((float) params.width) + value > ((float) this.maxWidth)) {
            params.width = this.maxWidth;
            params.height = this.maxWidth;
        } else if (((float) params.width) + value < ((float) this.minWidth)) {
            params.width = this.minWidth;
            params.height = this.minWidth;
        } else {
            params.width = (int) (((float) params.width) + value);
            params.height = (int) (((float) params.height) + value);
        }
        this.layout_cut.setLayoutParams(params);
    }

    void checkImageSize() {
        this.tempBitmap = Bitmap.createBitmap(this.mainBitmap, 0, 0, (int) (((float) this.minWidth) * this.scale), (int) (((float) this.minWidth) * this.scale));
        if (this.tempBitmap.getWidth() < 32) {
            setResult(0);
            C0568T.showShort(this.mContext, (int) C0291R.string.image_size_too_small);
            finish();
        }
    }

    void updateHeader() {
        LayoutParams params = (LayoutParams) this.layout_cut.getLayoutParams();
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;
        switch (this.type) {
            case 0:
                x = (int) (((float) params.x) * this.scale);
                y = (int) (((float) (params.y - this.initY)) * this.scale);
                width = (int) (((float) params.width) * this.scale);
                height = (int) (((float) params.height) * this.scale);
                break;
            case 1:
                x = (int) (((float) (params.x - this.initX)) * this.scale);
                y = (int) (((float) params.y) * this.scale);
                width = (int) (((float) params.width) * this.scale);
                height = (int) (((float) params.height) * this.scale);
                Log.e("my", x + ":" + y + ":" + width + ":" + height);
                Log.e("my", this.mainBitmap.getWidth() + ":" + this.mainBitmap.getHeight());
                break;
            case 2:
                x = (int) (((float) params.x) * this.scale);
                y = (int) (((float) (params.y - this.initY)) * this.scale);
                width = (int) (((float) params.width) * this.scale);
                height = (int) (((float) params.height) * this.scale);
                break;
        }
        try {
            this.tempBitmap = Bitmap.createBitmap(this.mainBitmap, x, y, width, height);
            this.saveBitmap = Bitmap.createBitmap(this.tempBitmap, 0, 0, this.tempBitmap.getWidth(), this.tempBitmap.getHeight());
            Log.e("my", this.tempBitmap.getWidth() + "");
            Log.e("my", ImageUtils.getScaleRounded(this.tempBitmap.getWidth()) + "");
            this.tempBitmap = ImageUtils.roundCorners(this.tempBitmap, (float) ImageUtils.getScaleRounded(this.tempBitmap.getWidth()));
            this.header_img.setImageBitmap(this.tempBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            case C0291R.id.save:
                setResult(1);
                ImageUtils.saveImg(this.saveBitmap, Image.USER_HEADER_PATH + NpcCommon.mThreeNum + "/" + this.mContact.contactId + "/", Image.USER_HEADER_FILE_NAME);
                this.saveBitmap = ImageUtils.grayBitmap(this.saveBitmap);
                ImageUtils.saveImg(this.saveBitmap, Image.USER_HEADER_PATH + NpcCommon.mThreeNum + "/" + this.mContact.contactId + "/", Image.USER_GRAY_HEADER_FILE_NAME);
                finish();
                return;
            default:
                return;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (!(this.mainBitmap == null || this.mainBitmap.isRecycled())) {
            this.mainBitmap.recycle();
        }
        if (!(this.tempBitmap == null || this.tempBitmap.isRecycled())) {
            this.tempBitmap.recycle();
        }
        if (this.saveBitmap != null && !this.saveBitmap.isRecycled()) {
            this.saveBitmap.recycle();
        }
    }

    public int getActivityInfo() {
        return 38;
    }
}
