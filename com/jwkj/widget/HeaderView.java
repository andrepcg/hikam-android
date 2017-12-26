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

public class HeaderView extends ImageView {
    Bitmap tempBitmap;

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void updateImage(String threeNum, boolean isGray) {
        try {
            this.tempBitmap = ImageUtils.getBitmap(new File(Environment.getExternalStorageDirectory().getPath() + "/screenshot/tempHead/" + NpcCommon.mThreeNum + "/" + threeNum + ".jpg"), 200, 200);
            setImageBitmap(this.tempBitmap);
        } catch (Exception e) {
            this.tempBitmap = BitmapFactory.decodeResource(getResources(), C0291R.drawable.heard_icon_1);
            setImageBitmap(this.tempBitmap);
        }
    }
}
