package com.jwkj.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.hikam.C0291R;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.ImageUtils;
import java.io.File;

public class AlarmHeaderView extends ImageView {
    Bitmap tempBitmap;

    public AlarmHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void updateImage(String threeNum, String index) {
        try {
            this.tempBitmap = ImageUtils.getBitmap(new File(Environment.getExternalStorageDirectory().getPath() + "/screenshot/tempHead/alarm/" + NpcCommon.mThreeNum + "/" + index + ".jpg"), 200, 200);
            setImageBitmap(this.tempBitmap);
        } catch (Exception e) {
            this.tempBitmap = BitmapFactory.decodeResource(getResources(), C0291R.drawable.header_icon);
            setImageBitmap(this.tempBitmap);
        }
    }
}
