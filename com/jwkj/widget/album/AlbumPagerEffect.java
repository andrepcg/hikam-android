package com.jwkj.widget.album;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class AlbumPagerEffect implements PageTransformer {
    private static final float MIN_ALPHA = 0.5f;
    private static final float MIN_SCALE = 0.85f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();
        if (position < -1.0f) {
            view.setAlpha(0.0f);
        } else if (position <= 1.0f) {
            float scaleFactor = Math.max(MIN_SCALE, 1.0f - Math.abs(position));
            float vertMargin = (((float) pageHeight) * (1.0f - scaleFactor)) / 2.0f;
            float horzMargin = (((float) pageWidth) * (1.0f - scaleFactor)) / 2.0f;
            if (position < 0.0f) {
                view.setTranslationX(horzMargin - (vertMargin / 2.0f));
            } else {
                view.setTranslationX((-horzMargin) + (vertMargin / 2.0f));
            }
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            view.setAlpha((((scaleFactor - MIN_SCALE) / 0.14999998f) * MIN_ALPHA) + MIN_ALPHA);
        } else {
            view.setAlpha(0.0f);
        }
    }
}
