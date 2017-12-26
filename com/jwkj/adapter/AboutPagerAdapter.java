package com.jwkj.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class AboutPagerAdapter extends PagerAdapter {
    private List<View> views;

    public AboutPagerAdapter(List<View> views) {
        this.views = views;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) this.views.get(position));
    }

    public int getCount() {
        return this.views.size();
    }

    public Object instantiateItem(ViewGroup container, int position) {
        container.addView((View) this.views.get(position), 0);
        return this.views.get(position);
    }

    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
}
