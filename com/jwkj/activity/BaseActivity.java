package com.jwkj.activity;

import com.jwkj.global.MyApp;
import com.p2p.core.BaseCoreActivity;

public abstract class BaseActivity extends BaseCoreActivity {
    public abstract int getActivityInfo();

    protected void onGoBack() {
        MyApp.app.showNotification();
    }

    protected void onGoFront() {
        MyApp.app.hideNotification();
    }

    protected void onExit() {
        MyApp.app.hideNotification();
    }
}
