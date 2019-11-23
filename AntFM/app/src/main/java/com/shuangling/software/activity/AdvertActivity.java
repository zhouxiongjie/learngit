package com.shuangling.software.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ServerInfo;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class AdvertActivity extends AppCompatActivity  {


    @BindView(R.id.activtyTitle)
    TopTitleBar activtyTitle;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.webView)
    WebView webView;

    private int mAdvertId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 保持Activity处于唤醒状态
        setContentView(R.layout.activity_webview_back);
        ButterKnife.bind(this);
        initData();


    }


    private void initData() {

        String url = getIntent().getStringExtra("url");
        mAdvertId = getIntent().getIntExtra("id",0);
        activtyTitle.setBackListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);       //js
        s.setDomStorageEnabled(true);       //localStorage

//        s.setBuiltInZoomControls(true);
//        s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//        s.setUseWideViewPort(true);
//        s.setLoadWithOverviewMode(true);
//        s.setSavePassword(true);
//        s.setSaveFormData(true);
//        s.setGeolocationEnabled(true);
//        s.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");

//        webView.requestFocus();
        // webView.setScrollBarStyle(0);


        webView.setWebViewClient(new WebViewClient(){
            // url拦截
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            // 页面开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //progressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                //progressBar.setVisibility(View.GONE);
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


        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            // 处理javascript中的alert
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                return super.onJsAlert(view, url, message, result);
            };

            @Override
            // 处理javascript中的confirm
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            };

            @Override
            // 处理javascript中的prompt
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            };

            // 设置网页加载的进度条
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                long id=Thread.currentThread().getId();
                Log.i("onProgressChanged",""+newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(GONE);
                } else {
                    if (progressBar.getVisibility() == GONE)
                        progressBar.setVisibility(VISIBLE);
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
        clickAdert();
    }




    @Override
    protected void onDestroy() {
        webView.destroy();
        super.onDestroy();
    }




    @Override
    public void onBackPressed() {
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
        super.onBackPressed();
    }


    public void clickAdert() {

        String url = ServerInfo.serviceIP + ServerInfo.clickAdvert+mAdvertId;
        Map<String, String> params = new HashMap<>();

        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Log.i("test",response);


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.i("test",exception.getMessage());
            }
        });


    }

}