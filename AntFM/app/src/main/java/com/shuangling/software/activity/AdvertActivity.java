package com.shuangling.software.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.shuangling.software.R;
import com.shuangling.software.activity.ui.WebProgress;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.PreloadWebView;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

//@EnableDragToClose()
public class AdvertActivity extends QMUIActivity/*AppCompatActivity*/ {
    @BindView(R.id.activtyTitle)
    /*TopTitleBar*/ QMUITopBarLayout activtyTitle;
    WebProgress progressBar;
    WebView webView;
    private int mAdvertId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 保持Activity处于唤醒状态
        setContentView(R.layout.activity_webview_back);
//        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this); //
        FrameLayout fl_content = findViewById(R.id.fl_web_container);
        webView = PreloadWebView.getInstance().getWebView(this);
        fl_content.addView(webView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        View stubView = ((ViewStub) findViewById(R.id.viewStub)).inflate();
        progressBar = stubView.findViewById(R.id.progressBar);
        initData();
    }

    private void initData() {
        String url = getIntent().getStringExtra("url");
        mAdvertId = getIntent().getIntExtra("id", 0);
        activtyTitle.addLeftImageButton(R.drawable.ic_left, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(view -> { //
            doOnBackPressed();
        });
        webView.setWebViewClient(new WebViewClient() {
            // url拦截
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                progressBar.show();
                return true;
            }

            // 页面开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.show();
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.hide();
                super.onPageFinished(view, url);
            }

            // WebView加载的所有资源url
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//				view.loadData(errorHtml, "text/html; charset=UTF-8", null);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            // 处理javascript中的alert
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            // 处理javascript中的confirm
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            // 处理javascript中的prompt
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            // 设置网页加载的进度条
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                long id = Thread.currentThread().getId();
                Log.i("onProgressChanged", "" + newProgress);
                if (newProgress == 100) {
                    progressBar.hide();
                } else {
                    progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            // 设置程序的Title
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
        webView.loadUrl(url);
        progressBar.show();
        progressBar.setColor("#001CA0FF", "#1CA0FF");
        clickAdert();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

//    @Override
//    public void onBackPressed() {
//        Intent it = new Intent(this, MainActivity.class);
//        startActivity(it);
//        super.onBackPressed();
//    }

    @Override
    protected void doOnBackPressed() {
        super.doOnBackPressed();
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
    }

    public void clickAdert() {
        String url = ServerInfo.serviceIP + ServerInfo.clickAdvert + mAdvertId;
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Log.i("test", response);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                Log.i("test", exception.getMessage());
            }
        });
    }
}