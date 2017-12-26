package com.jwkj.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;
import android.widget.PopupWindow;

public class WrapPopupWindow extends PopupWindow {
    public WrapPopupWindow(Context context) {
        super(context);
    }

    public WrapPopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void showAsDropDown(View anchor) {
        if (VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            setHeight(anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom);
        }
        super.showAsDropDown(anchor);
    }
}
