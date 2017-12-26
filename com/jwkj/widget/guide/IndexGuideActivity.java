package com.jwkj.widget.guide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hikam.C0291R;
import com.jwkj.activity.AddContactActivity;
import com.jwkj.activity.BaseActivity;
import com.jwkj.activity.QRcodeActivity;
import com.jwkj.web.HKWebView;

public class IndexGuideActivity extends BaseActivity implements OnClickListener {
    private ImageView img_back;
    private boolean isAlterNative = false;
    private RelativeLayout rl_airlink;
    private RelativeLayout rl_apmode;
    private RelativeLayout rl_exchange;
    private RelativeLayout rl_manual;
    private RelativeLayout rl_smart_scan;
    private TextView tv_exchange;
    private TextView tv_exchange_intro;
    private TextView tv_solution;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_guide_index);
        initComponent();
        regFilter();
    }

    public void onBackPressed() {
        finish();
    }

    private void regFilter() {
    }

    private void initComponent() {
        this.tv_solution = (TextView) findViewById(C0291R.id.tv_solution);
        this.tv_solution.getPaint().setFlags(8);
        this.tv_solution.setOnClickListener(this);
        this.tv_exchange = (TextView) findViewById(C0291R.id.tv_exchange);
        this.tv_exchange_intro = (TextView) findViewById(C0291R.id.tv_exchange_intro);
        this.rl_airlink = (RelativeLayout) findViewById(C0291R.id.rl_airlink);
        this.rl_manual = (RelativeLayout) findViewById(C0291R.id.rl_manual);
        this.rl_exchange = (RelativeLayout) findViewById(C0291R.id.rl_exchange);
        this.rl_smart_scan = (RelativeLayout) findViewById(C0291R.id.rl_smart_scan);
        this.rl_apmode = (RelativeLayout) findViewById(C0291R.id.rl_apmode);
        this.img_back = (ImageView) findViewById(C0291R.id.img_back);
        this.img_back.setOnClickListener(this);
        this.rl_airlink.setOnClickListener(this);
        this.rl_manual.setOnClickListener(this);
        this.rl_apmode.setOnClickListener(this);
        this.rl_smart_scan.setOnClickListener(this);
        this.rl_exchange.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.img_back:
                finish();
                return;
            case C0291R.id.rl_airlink:
                startActivity(new Intent(this, AirLinkGuideActivity.class));
                finish();
                return;
            case C0291R.id.rl_apmode:
                startActivity(new Intent(this, ApModeGuideActivity.class));
                finish();
                return;
            case C0291R.id.rl_exchange:
                if (this.isAlterNative) {
                    showMaster();
                    return;
                } else {
                    showAlterNative();
                    return;
                }
            case C0291R.id.rl_manual:
                startActivity(new Intent(this, AddContactActivity.class));
                finish();
                return;
            case C0291R.id.rl_smart_scan:
                startActivity(new Intent(this, QRcodeActivity.class));
                finish();
                return;
            case C0291R.id.tv_solution:
                startActivity(new Intent(this, HKWebView.class));
                return;
            default:
                return;
        }
    }

    public void showMaster() {
        this.isAlterNative = false;
        this.rl_smart_scan.setVisibility(8);
        this.rl_apmode.setVisibility(8);
        this.tv_exchange.setText("备选方案");
        this.tv_exchange_intro.setText("AP Mode\nSmart Scan");
    }

    public void showAlterNative() {
        this.isAlterNative = true;
        this.rl_smart_scan.setVisibility(0);
        this.rl_apmode.setVisibility(0);
        Animation slide_down_anim = new AlphaAnimation(0.3f, 1.0f);
        slide_down_anim.setDuration(500);
        this.rl_smart_scan.setAnimation(slide_down_anim);
        this.rl_apmode.setAnimation(slide_down_anim);
        slide_down_anim.start();
        this.rl_exchange.setVisibility(8);
    }

    public int getActivityInfo() {
        return 1026;
    }
}
