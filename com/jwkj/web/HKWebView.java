package com.jwkj.web;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import com.hikam.C0291R;
import com.jwkj.activity.BaseActivity;
import com.jwkj.activity.QRcodeActivity;
import com.jwkj.widget.HKHorizontalProgress;
import com.jwkj.widget.guide.ApModeGuideActivity;
import java.util.Locale;

@SuppressLint({"JavascriptInterface"})
public class HKWebView extends BaseActivity {
    private ImageView img_back;
    private HKHorizontalProgress progressBar;
    private WebView webView;

    class C05711 implements OnClickListener {
        C05711() {
        }

        public void onClick(View v) {
            HKWebView.this.finish();
        }
    }

    class C05722 extends WebViewClient {
        C05722() {
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    class C05733 extends WebChromeClient {
        C05733() {
        }

        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                HKWebView.this.progressBar.setProgress(0);
            } else if (newProgress > 80) {
                HKWebView.this.progressBar.setProgress(0);
            } else {
                HKWebView.this.progressBar.setProgress(newProgress);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0291R.layout.activity_webview);
        initComponent();
    }

    private void initComponent() {
        this.img_back = (ImageView) findViewById(C0291R.id.img_back);
        this.img_back.setOnClickListener(new C05711());
        this.progressBar = (HKHorizontalProgress) findViewById(C0291R.id.progressBar);
        this.webView = (WebView) findViewById(C0291R.id.webview);
        String locale = Locale.getDefault().getLanguage();
        if ("zh".equals(locale)) {
            this.webView.loadUrl("file:///android_asset/solution.html");
        } else if ("de".equals(locale)) {
            this.webView.loadUrl("file:///android_asset/solution_de.html");
        } else {
            this.webView.loadUrl("file:///android_asset/solution_en.html");
        }
        this.webView.setWebViewClient(new C05722());
        this.webView.getSettings().setCacheMode(1);
        this.webView.setWebChromeClient(new C05733());
        WebSettings webSettings = this.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(1);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        this.webView.addJavascriptInterface(this, "Hikam_Android");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            if (this.webView.canGoBack()) {
                this.webView.goBack();
                return true;
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @JavascriptInterface
    public void startSmartScan() {
        Intent intent = new Intent(this, QRcodeActivity.class);
        intent.putExtra("returnweb", 1);
        startActivity(intent);
        finish();
    }

    @JavascriptInterface
    public void startAPMode() {
        Intent intent = new Intent(this, ApModeGuideActivity.class);
        intent.putExtra("returnweb", 1);
        startActivity(intent);
        finish();
    }

    public int getActivityInfo() {
        return 0;
    }
}
