package com.shuangling.software.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.activity.ui.WebProgress;
import com.shuangling.software.dialog.ShareDialog;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.ShareParameter;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.PreloadWebView;
import com.shuangling.software.utils.ServerInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import okhttp3.Call;

//@EnableDragToClose() //
public class WebViewBackActivity extends /*AppCompatActivity*/QMUIActivity implements Handler.Callback {
    private static final int LOGIN_RESULT = 0x1;
    public static final int MSG_GET_DETAIL = 0x2;
    private static final int SHARE_SUCCESS = 0x3;
    private static final int SHARE_FAILED = 0x4;
    private WebView webView;
    @BindView(R.id.activtyTitle)
    /*TopTitleBar*/ QMUITopBarLayout activtyTitle; //
    WebProgress progressBar;
    private Handler mHandler;
    private String mUrl;
    private String mTitle;
    private int mActivityId;
    private String mJumpUrl;
    private boolean mAddParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_back);
//        CommonUtils.transparentStatusBar(this); //
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this); //
        FrameLayout fl_content = findViewById(R.id.fl_web_container);
        webView = PreloadWebView.getInstance().getWebView(this);
        fl_content.addView(webView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        init();
    }

    private void init() {
        EventBus.getDefault().register(this);
        mUrl = getIntent().getStringExtra("url");
        mTitle = getIntent().getStringExtra("title");
        mAddParams = getIntent().getBooleanExtra("addParams", false);
        if (mAddParams) {//true 内链
            activtyTitle.addLeftImageButton(R.drawable.icon_back, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(view -> { //
                finish();
            });
        }else {//如果是外链，显示进度条、 X
            View stubView = ((ViewStub) findViewById(R.id.viewStub)).inflate();
            progressBar = stubView.findViewById(R.id.progressBar);
            activtyTitle.addLeftImageButton(R.drawable.float_close_icon, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(view -> { //
                finish();
            });
        }
        mActivityId = getIntent().getIntExtra("activityId", -1);
        activtyTitle.setTitle(mTitle);
        String url = initUrl(mUrl);
        if (url.startsWith(ServerInfo.activity)) {
            activtyTitle.addRightImageButton(R.drawable.ic_more, com.qmuiteam.qmui.R.id.right_icon).setOnClickListener(view -> {//
                String script = "javascript:_getShareEventParmas()";
                webView.evaluateJavascript(script, responseJson -> {
                    ShareParameter shareParameter = JSONObject.parseObject(responseJson, ShareParameter.class);
                    if (shareParameter != null) {
                        showShareDialog(shareParameter.getShareTitle(), shareParameter.getShareDesc(), shareParameter.getShareLogo(), shareParameter.getShareUrl());
                    }
                });
            });
        }
        mHandler = new Handler(this);
        webView.setWebViewClient(new WebViewClient() {
            // url拦截
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                if (progressBar != null) progressBar.show();
                return true;
            }

            // 页面开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (progressBar != null) progressBar.show();
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressBar != null) progressBar.hide();
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
//                long id = Thread.currentThread().getId();
//                Log.i("onProgressChanged", "" + newProgress);
                if (newProgress == 100) {
                    if (progressBar != null) progressBar.hide();
                } else {
                    if (progressBar != null) progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            // 设置程序的Title
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (mTitle == null && title != null && !title.startsWith("http")) {
//                    activtyTitle.setTitleText(title);
                    activtyTitle.setTitle(title);
                }
            }
        });
        webView.addJavascriptInterface(new JsToAndroid(), "clientJS");
        webView.loadUrl(url);
        if (progressBar != null) {
            progressBar.show();
            progressBar.setColor("#001CA0FF", "#1CA0FF");
        }
    }

    private void showShareDialog(final String title, final String desc, final String logo, final String url) {
        ShareDialog dialog = ShareDialog.getInstance(false, false);
        dialog.setIsShowPosterButton(false);
        dialog.setIsHideSecondGroup(true);
        dialog.setShareHandler(new ShareDialog.ShareHandler() {
            @Override
            public void onShare(String platform) {
                showShare(platform, title, desc, logo, url);
            }

            @Override
            public void poster() {
            }

            @Override
            public void report() {
            }

            @Override
            public void copyLink() {
//获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData clipData = ClipData.newPlainText("Label", url);
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(clipData);
                ToastUtils.show("复制成功，可以发给朋友们了。");
            }

            @Override
            public void refresh() {
            }

            @Override
            public void collectContent() {
            }
        });
        dialog.show(getSupportFragmentManager(), "ShareDialog");
    }

    private String initUrl(String url) {
        if (mAddParams) {//内链
            if (User.getInstance() == null) {
                if (MainActivity.sCurrentCity != null) {
                    if (url.contains("?")) {
                        url = url + "&app=android&city=" + MainActivity.sCurrentCity.getCode() + "&multiple=" + CommonUtils.getFontSize();
                    } else {
                        url = url + "?app=android&city=" + MainActivity.sCurrentCity.getCode() + "&multiple=" + CommonUtils.getFontSize();
                    }
                } else {
                    if (url.contains("?")) {
                        url = url + "&app=android" + "&multiple=" + CommonUtils.getFontSize();
                    } else {
                        url = url + "?app=android" + "&multiple=" + CommonUtils.getFontSize();
                    }
                }
            } else {
                if (MainActivity.sCurrentCity != null) {
                    if (url.contains("?")) {
                        url = url + "&Authorization=" + User.getInstance().getAuthorization() + "&app=android&city=" + MainActivity.sCurrentCity.getCode() + "&multiple=" + CommonUtils.getFontSize();
                    } else {
                        url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&city=" + MainActivity.sCurrentCity.getCode() + "&multiple=" + CommonUtils.getFontSize();
                    }
                } else {
                    if (url.contains("?")) {
                        url = url + "&Authorization=" + User.getInstance().getAuthorization() + "&app=android" + "&multiple=" + CommonUtils.getFontSize();
                    } else {
                        url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android" + "&multiple=" + CommonUtils.getFontSize();
                    }
                }
            }
            if (mActivityId != -1) {
                url = url + "&qaa_act_id=" + mActivityId + "&multiple=" + CommonUtils.getFontSize();
            }
            return url;
        } else {
            return url;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("OnLoginSuccess") || event.getEventName().equals("OnQuitLogin")) {
            String url = initUrl(mUrl);
            webView.loadUrl(url);
            if (progressBar != null) progressBar.show();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_DETAIL:
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case SHARE_SUCCESS:
                break;
            case SHARE_FAILED:
                break;
        }
        return false;
    }

    private final class JsToAndroid {
        @JavascriptInterface
        public void backEvent(String str) {
//            long id = Thread.currentThread().getId();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }

        @JavascriptInterface
        public void loginEvent(final String bindPhone) {
            mJumpUrl = null;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(WebViewBackActivity.this, NewLoginActivity.class);
                    if (bindPhone.equals("0")) {
                        it.putExtra("bindPhone", false);
                    } else {
                        it.putExtra("bindPhone", true);
                    }
                    startActivityForResult(it, LOGIN_RESULT);
                }
            });
        }

        @JavascriptInterface
        public void loginEvent(final String bindPhone, String url) {
            mJumpUrl = url;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(WebViewBackActivity.this, NewLoginActivity.class);
                    if (bindPhone.equals("0")) {
                        it.putExtra("bindPhone", false);
                    } else {
                        it.putExtra("bindPhone", true);
                    }
                    startActivityForResult(it, LOGIN_RESULT);
                }
            });
        }

        @JavascriptInterface
        public void bindPhoneEvent(final String url) {
            mJumpUrl = url;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //showShare();
                    Intent it = new Intent(WebViewBackActivity.this, BindPhoneActivity.class);
                    it.putExtra("hasLogined", true);
                    startActivityForResult(it, LOGIN_RESULT);
                }
            });
        }

        //        @JavascriptInterface
//        public void shareEvent(final String str) {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//
//                    showShare(str);
//                }
//            });
//
//
//        }
        @JavascriptInterface
        public void shareEvent(final String id, final String title, final String des, final String url, final String logo) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showShare(id, title, des, url, logo);
                }
            });
        }

        @JavascriptInterface
        public void pageEvent(final String id, final String type, final String title) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //1音频、2专辑、3文章、4视频、5专题、7图集
                    if (type.equals("1")) {
                        Intent it = new Intent(WebViewBackActivity.this, AudioDetailActivity.class);
                        it.putExtra("audioId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("2")) {
                        Intent it = new Intent(WebViewBackActivity.this, AlbumDetailActivity.class);
                        it.putExtra("albumId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("3")) {
                        Intent it = new Intent(WebViewBackActivity.this, ArticleDetailActivity02.class);
                        it.putExtra("articleId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("4")) {
                        Intent it = new Intent(WebViewBackActivity.this, VideoDetailActivity.class);
                        it.putExtra("videoId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("5")) {
                        Intent it = new Intent(WebViewBackActivity.this, SpecialDetailActivity.class);
                        it.putExtra("specialId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("7")) {
                        Intent it = new Intent(WebViewBackActivity.this, GalleriaActivity.class);
                        it.putExtra("galleriaId", Integer.parseInt(id));
                        startActivity(it);
                    }
                }
            });
        }

        @JavascriptInterface
        public void cateEvent(final String typeId, final String columnId, final String name) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(WebViewBackActivity.this, ContentActivity.class);
                    Column column = new Column();
                    column.setName(name);
                    column.setId(Integer.parseInt(columnId));
                    it.putExtra("column", column);
                    startActivity(it);
                }
            });
        }

        @JavascriptInterface
        public void confirmToBrowser(final String url) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (Exception e) {
                    }
                }
            });
        }
    }

    private void showShare(final String id) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        final Platform qq = ShareSDK.getPlatform(QQ.NAME);
        if (!qq.isClientValid()) {
            oks.addHiddenPlatform(QQ.NAME);
        }
        final Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
        if (!sina.isClientValid()) {
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            //自定义分享的回调想要函数
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
//点击新浪微博
                String chanel = "1";
                if (SinaWeibo.NAME.equals(platform.getName())) {
                    //限制微博分享的文字不能超过20
                    chanel = "2";
                    paramsToShare.setText("刺激好玩的活动" + ServerInfo.activity + "qaa/game-result/" + id);
                } else if (QQ.NAME.equals(platform.getName())) {
                    chanel = "3";
//                        paramsToShare.setTitle(mArticle.getTitle());
//                        if(mArticle.getArticle().getCovers().size()>0){
//                            paramsToShare.setImageUrl(mArticle.getArticle().getCovers().get(0));
//                        }
                    paramsToShare.setTitleUrl(ServerInfo.activity + "qaa/game-result/" + id);
                    //paramsToShare.setText(mArticle.getArticle().getContent());
                } else if (Wechat.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
//                        paramsToShare.setTitle(mArticle.getTitle());
                    paramsToShare.setUrl(ServerInfo.activity + "qaa/game-result/" + id);
//                        if(mArticle.getArticle().getCovers().size()>0){
//                            paramsToShare.setImageUrl(mArticle.getArticle().getCovers().get(0));
//                        }
//                        paramsToShare.setText(mArticle.getArticle().getContent());
                } else if (WechatMoments.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
//                        paramsToShare.setTitle(mArticle.getTitle());
                    paramsToShare.setUrl(ServerInfo.activity + "qaa/game-result/" + id);
//                        if(mArticle.getArticle().getCovers().size()>0){
//                            paramsToShare.setImageUrl(mArticle.getArticle().getCovers().get(0));
//                        }
                } else if (WechatFavorite.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
//                        paramsToShare.setTitle(mArticle.getTitle());
                    paramsToShare.setUrl(ServerInfo.activity + "qaa/game-result/" + id);
//                        if(mArticle.getArticle().getCovers().size()>0){
//                            paramsToShare.setImageUrl(mArticle.getArticle().getCovers().get(0));
//                        }
                }
                shareStatistics(chanel, "" + id, ServerInfo.activity + "qaa/game-result/" + id);
            }
        });
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Message msg = Message.obtain();
                msg.what = SHARE_FAILED;
                msg.obj = arg2.getMessage();
                mHandler.sendMessage(msg);
            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                Message msg = Message.obtain();
                msg.what = SHARE_SUCCESS;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
            }
        });
        // 启动分享GUI
        oks.show(this);
    }

    private void showShare(String platform, final String title, final String desc, final String logo, final String url) {
        final String cover;
        if (logo.startsWith("http://")) {
            cover = logo.replace("http://", "https://");
        } else {
            cover = logo;
        }
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setPlatform(platform);
        final Platform qq = ShareSDK.getPlatform(QQ.NAME);
        if (!qq.isClientValid()) {
            oks.addHiddenPlatform(QQ.NAME);
        }
        final Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
        if (!sina.isClientValid()) {
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            //自定义分享的回调想要函数
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                //点击新浪微博
                String chanel = "1";
                if (SinaWeibo.NAME.equals(platform.getName())) {
                    chanel = "2";
                    //限制微博分享的文字不能超过20
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                    paramsToShare.setText(title + url);
                } else if (QQ.NAME.equals(platform.getName())) {
                    chanel = "3";
                    paramsToShare.setTitle(title);
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                    paramsToShare.setTitleUrl(url);
                    paramsToShare.setText(desc);
                } else if (Wechat.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(url);
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                    paramsToShare.setText(desc);
                    Log.d("ShareSDK", paramsToShare.toMap().toString());
                } else if (WechatMoments.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(url);
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                } else if (WechatFavorite.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(url);
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                }
//shareStatistics(chanel, "" + mArticle.getId(), ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId + "?app=android");
            }
        });
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Message msg = Message.obtain();
                msg.what = SHARE_FAILED;
                msg.obj = arg2.getMessage();
                mHandler.sendMessage(msg);
            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                Message msg = Message.obtain();
                msg.what = SHARE_SUCCESS;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
            }
        });
        // 启动分享GUI
        oks.show(this);
    }

//    @Override
//    public void onBackPressed() {//todo
//        if (webView.canGoBack()) {
//            webView.goBack();
//        } else {
//            super.onBackPressed();
//        }
//    }


    @Override
    protected void doOnBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.doOnBackPressed();
        }
    }

    public void shareStatistics(String channel, String postId, String shardUrl) {
        String url = ServerInfo.serviceIP + ServerInfo.shareStatistics;
        Map<String, String> params = new HashMap<>();
        if (User.getInstance() != null) {
            params.put("user_id", "" + User.getInstance().getId());
        }
        params.put("channel", channel);
        params.put("post_id", postId);
        params.put("source_type", "3");
        params.put("type", "1");
        params.put("shard_url", shardUrl);
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Log.i("test", response);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                Log.i("test", exception.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        if (webView != null) {
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_RESULT) {
//            String url=initUrl(mUrl);
//
//            if(!TextUtils.isEmpty(mJumpUrl)){
//                url = mJumpUrl + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android"+"&multiple="+CommonUtils.getFontSize();
//            }
//            //url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
//            webView.loadUrl(url);
            String url = initUrl(mUrl);
            if (User.getInstance() != null) {
                if (!TextUtils.isEmpty(mJumpUrl)) {
                    url = mJumpUrl + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android" + "&multiple=" + CommonUtils.getFontSize();
                }
                webView.loadUrl(url);
                progressBar.show();
            }
        }
    }
}
