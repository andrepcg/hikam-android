package com.jwkj.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.hikam.C0291R;

@SuppressLint({"SetJavaScriptEnabled"})
public class SysNotifyActivity extends BaseActivity implements OnClickListener {
    ImageView back_btn;
    ProgressBar progress;
    WebView web_panel;
    WebViewClient wvc = new C04541();

    class C04541 extends WebViewClient {
        C04541() {
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            SysNotifyActivity.this.progress.setVisibility(8);
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            SysNotifyActivity.this.progress.setVisibility(0);
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(C0291R.layout.activity_sys_notify);
        initComponent();
    }

    public void initComponent() {
        this.back_btn = (ImageView) findViewById(C0291R.id.back_btn);
        this.progress = (ProgressBar) findViewById(C0291R.id.progress);
        this.web_panel = (WebView) findViewById(C0291R.id.web_panel);
        this.web_panel.getSettings().setJavaScriptEnabled(true);
        this.web_panel.loadUrl("http://www.gwelltimes.com/upg/android/00/00/SysNotify/index.html");
        this.web_panel.setWebViewClient(this.wvc);
        this.back_btn.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0291R.id.back_btn:
                finish();
                return;
            default:
                return;
        }
    }

    public int getActivityInfo() {
        return 28;
    }
}
