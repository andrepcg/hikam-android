package com.jwkj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import com.hikam.C0291R;
import com.jwkj.global.Constants;

public class ForgetPassword extends Activity implements OnClickListener {
    public ImageView back;
    public WebView wv;

    class C03821 extends WebViewClient {
        C03821() {
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.forgetpassword);
        this.back = (ImageView) findViewById(C0291R.id.back_btn);
        this.back.setOnClickListener(this);
        this.wv = (WebView) findViewById(C0291R.id.webView1);
        this.wv.getSettings().setJavaScriptEnabled(true);
        this.wv.loadUrl(Constants.FORGET_PASSWORD_URL);
        this.wv.setWebViewClient(new C03821());
    }

    public void onClick(View v) {
        if (v.getId() == C0291R.id.back_btn) {
            finish();
        }
    }
}
