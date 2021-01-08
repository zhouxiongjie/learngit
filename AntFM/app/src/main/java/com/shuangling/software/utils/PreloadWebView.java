package com.shuangling.software.utils;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.os.Build;
import android.os.Looper;
import android.os.MessageQueue;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.shuangling.software.MyApplication;

import java.util.Stack;

public class PreloadWebView {
    private PreloadWebView() {
    }

    private static final int CACHED_WEBVIEW_MAX_NUM = 2;
    private static final Stack<WebView> mCachedWebViewStack = new Stack<>();

    public static PreloadWebView getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final PreloadWebView INSTANCE = new PreloadWebView();
    }

    /**
     * 创建WebView实例
     * 用了applicationContext
     */
    public void preload() {
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {//CPU空闲的时候会调用
            @Override
            public boolean queueIdle() {//返回true就是单次回调后不删除，下次进入空闲时继续回调该方法，false只回调单次
//                L.d("webview preload:" + mCachedWebViewStack.size());
                if (mCachedWebViewStack.size() < CACHED_WEBVIEW_MAX_NUM) {
                    mCachedWebViewStack.push(createWebView());
                }
                return false;
            }
        });
    }

    /**
     * 从缓存池中获取合适的WebView
     *
     * @param context activity context
     * @return WebView
     */
    public WebView getWebView(Context context) {
        // 为空，直接返回新实例
        if (mCachedWebViewStack == null || mCachedWebViewStack.isEmpty()) {
            WebView web = createWebView();
            MutableContextWrapper contextWrapper = (MutableContextWrapper) web.getContext();
            contextWrapper.setBaseContext(context);
            return web;
        }
        WebView webView = mCachedWebViewStack.pop();
        // webView不为空，则开始使用预创建的WebView,并且替换Context
        MutableContextWrapper contextWrapper = (MutableContextWrapper) webView.getContext();
        contextWrapper.setBaseContext(context);
        return webView;
    }

    private WebView createWebView() {
        WebView webview = new WebView(new MutableContextWrapper(MyApplication.getInstance()));
        WebSettings s = webview.getSettings();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        CommonUtils.setWebviewUserAgent(s);
        s.setJavaScriptEnabled(true);
        s.setTextZoom(100);// 设置网页字体不跟随系统字体发生改变
        s.setDomStorageEnabled(true);  //开启 DOM storage API 功能
//        webview.loadDataWithBaseURL("file:///android_asset/article/?item_id=0&token=0", getHtml(), "text/html", "utf-8", "file:///android_asset/article/?item_id=0&token=0");
        return webview;
    }

//    public static void loadBaseHtml(WebView webView) {
//        if (webView == null) {
//            return;
//        }
//        webView.loadDataWithBaseURL("file:///android_asset/article/?item_id=0&token=0", getHtml(), "text/html", "utf-8", "file:///android_asset/article/?item_id=0&token=0");
//    }

//    private static String getHtml() {
//        return "<!DOCTYPE html>\n" +
//                "<html>\n" +
//                "<head>\n" +
//                "<meta charset=\"utf-8\">\n" +
//                "<meta name=\"viewport\" content=\"initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">\n" +
//                "<link rel=\"stylesheet\" type=\"text/css\" href=\"" +
//                "file:///android_asset/article/css/android.css" +
//                "\">\n</head>\n" +
//                "<body class=\"font_m\"><header></header><article></article><footer></footer>" +
//                "<script type=\"text/javascript\" src=\"" +
//                "file:///android_asset/article/js/lib.js" +
//                "\"></script>" +
//                "<script type=\"text/javascript\" src=\"" +
//                "file:///android_asset/article/js/android.js" +
//                "\" ></script>\n" +
//                "</body>\n" +
//                "</html>\n";
//    }
}
