package com.jwkj.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import com.hikam.C0291R;
import com.jwkj.adapter.AboutPagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends BaseActivity implements OnClickListener {
    ImageView back;
    int current_pager = 0;
    ImageView img_one;
    ImageView img_three;
    ImageView img_two;
    ViewPager pager;

    class C10701 implements OnPageChangeListener {
        C10701() {
        }

        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    AboutActivity.this.img_one.setImageResource(C0291R.drawable.about_bottom_p);
                    AboutActivity.this.img_two.setImageResource(C0291R.drawable.about_bottom);
                    AboutActivity.this.img_three.setImageResource(C0291R.drawable.about_bottom);
                    return;
                case 1:
                    AboutActivity.this.img_one.setImageResource(C0291R.drawable.about_bottom);
                    AboutActivity.this.img_two.setImageResource(C0291R.drawable.about_bottom_p);
                    AboutActivity.this.img_three.setImageResource(C0291R.drawable.about_bottom);
                    return;
                case 2:
                    AboutActivity.this.img_one.setImageResource(C0291R.drawable.about_bottom);
                    AboutActivity.this.img_two.setImageResource(C0291R.drawable.about_bottom);
                    AboutActivity.this.img_three.setImageResource(C0291R.drawable.about_bottom_p);
                    return;
                default:
                    return;
            }
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(C0291R.layout.activity_about);
        initComponent();
        loadPager();
    }

    public void initComponent() {
        this.img_one = (ImageView) findViewById(C0291R.id.img_one);
        this.img_two = (ImageView) findViewById(C0291R.id.img_two);
        this.img_three = (ImageView) findViewById(C0291R.id.img_three);
        this.back = (ImageView) findViewById(C0291R.id.back_btn);
        this.pager = (ViewPager) findViewById(C0291R.id.about_pager);
        this.back.setOnClickListener(this);
    }

    public void loadPager() {
        List<View> views = new ArrayList();
        ImageView one_bg = new ImageView(this);
        one_bg.setLayoutParams(new LayoutParams(-1, -1));
        one_bg.setScaleType(ScaleType.FIT_CENTER);
        one_bg.setBackgroundResource(C0291R.drawable.about_one);
        views.add(one_bg);
        ImageView two_bg = new ImageView(this);
        two_bg.setLayoutParams(new LayoutParams(-1, -1));
        two_bg.setScaleType(ScaleType.FIT_CENTER);
        two_bg.setBackgroundResource(C0291R.drawable.about_two);
        views.add(two_bg);
        ImageView three_bg = new ImageView(this);
        three_bg.setLayoutParams(new LayoutParams(-1, -1));
        three_bg.setScaleType(ScaleType.FIT_CENTER);
        three_bg.setBackgroundResource(C0291R.drawable.about_three);
        views.add(three_bg);
        this.pager.setAdapter(new AboutPagerAdapter(views));
        this.pager.setOnPageChangeListener(new C10701());
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
        return 4;
    }
}
