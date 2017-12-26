package com.jwkj.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import com.hikam.C0291R;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.global.MyApp;

public class SettingPushActivity extends BaseActivity {
    private ImageView btn_back;
    private RelativeLayout demo_notification;
    private LinearLayout demo_popup;
    private Context mContext;
    private RadioButton rb_notification;
    private RadioButton rb_popup;
    private RelativeLayout rl_notification;
    private RelativeLayout rl_popup;
    private int state;

    class C04401 implements OnClickListener {
        C04401() {
        }

        public void onClick(View v) {
            SettingPushActivity.this.state = 1;
            SettingPushActivity.this.showPopup();
            SettingPushActivity.this.rb_popup.setChecked(true);
            SettingPushActivity.this.rb_notification.setChecked(false);
            SharedPreferencesManager.getInstance().putAlarmPushMethod(SettingPushActivity.this.state, SettingPushActivity.this.mContext);
        }
    }

    class C04412 implements OnClickListener {
        C04412() {
        }

        public void onClick(View v) {
            SettingPushActivity.this.state = 2;
            SettingPushActivity.this.showNotification();
            SettingPushActivity.this.rb_popup.setChecked(false);
            SettingPushActivity.this.rb_notification.setChecked(true);
            SharedPreferencesManager.getInstance().putAlarmPushMethod(SettingPushActivity.this.state, SettingPushActivity.this.mContext);
        }
    }

    class C04423 implements OnClickListener {
        C04423() {
        }

        public void onClick(View v) {
            SettingPushActivity.this.finish();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.set_push_method);
        this.mContext = this;
        init();
        setListener();
    }

    private void showPopup() {
        Animation animOut = AnimationUtils.loadAnimation(this, C0291R.anim.scale_out);
        Animation animIn = AnimationUtils.loadAnimation(this, C0291R.anim.scale_in);
        animIn.setFillAfter(true);
        animOut.setFillAfter(true);
        this.demo_popup.startAnimation(animIn);
        this.demo_popup.setVisibility(0);
        this.demo_notification.startAnimation(animOut);
        this.demo_notification.setVisibility(8);
    }

    private void showNotification() {
        Animation animOut = AnimationUtils.loadAnimation(this, C0291R.anim.scale_out);
        Animation animIn = AnimationUtils.loadAnimation(this, C0291R.anim.scale_in);
        animIn.setFillAfter(true);
        animOut.setFillAfter(true);
        this.demo_notification.startAnimation(animIn);
        this.demo_notification.setVisibility(0);
        this.demo_popup.startAnimation(animOut);
        this.demo_popup.setVisibility(8);
    }

    private void init() {
        this.btn_back = (ImageView) findViewById(C0291R.id.back_btn);
        this.rb_popup = (RadioButton) findViewById(C0291R.id.rb_popup);
        this.rb_notification = (RadioButton) findViewById(C0291R.id.rb_notification);
        this.rl_popup = (RelativeLayout) findViewById(C0291R.id.set_method_popup);
        this.rl_notification = (RelativeLayout) findViewById(C0291R.id.set_method_notification);
        this.demo_popup = (LinearLayout) findViewById(C0291R.id.demo_popup);
        this.demo_notification = (RelativeLayout) findViewById(C0291R.id.demo_notification);
        MyApp.PUSH_METHOD = SharedPreferencesManager.getInstance().getAlarmPushMethod(MyApp.app);
        switch (MyApp.PUSH_METHOD) {
            case 1:
                this.state = 1;
                showPopup();
                this.rb_popup.setChecked(true);
                this.rb_notification.setChecked(false);
                return;
            case 2:
                this.state = 2;
                showNotification();
                this.rb_popup.setChecked(false);
                this.rb_notification.setChecked(true);
                return;
            default:
                return;
        }
    }

    private void setListener() {
        this.rl_popup.setOnClickListener(new C04401());
        this.rl_notification.setOnClickListener(new C04412());
        this.btn_back.setOnClickListener(new C04423());
    }

    public int getActivityInfo() {
        return 0;
    }
}
