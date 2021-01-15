package com.shuangling.software.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.gyf.immersionbar.ImmersionBar;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.activity.ui.WebProgress;
import com.shuangling.software.entity.Special;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.PreloadWebView;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

//@EnableDragToClose()
public class SpecialDetailActivity extends QMUIActivity implements Handler.Callback {
    private static final int LOGIN_RESULT = 0x1;
    public static final int MSG_GET_DETAIL = 0x2;
    private static final int SHARE_SUCCESS = 0x3;
    private static final int SHARE_FAILED = 0x4;
//    @BindView(R.id.progressBar)
    /*ProgressBar*/ WebProgress progressBar;
//    @BindView(R.id.webView)
    WebView webView;
    private Handler mHandler;
    private Special mSpecial;
    private int mSpecialId;
    private String mJumpUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview/*activity_galleria*/);
        //CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        ImmersionBar.with(this).statusBarDarkFont(true).fitsSystemWindows(true).keyboardEnable(true)  //解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE).init();
        FrameLayout fl_content = findViewById(R.id.fl_web_container);
        webView = PreloadWebView.getInstance().getWebView(this);
        fl_content.addView(webView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        View stubView = ((ViewStub) findViewById(R.id.viewStub)).inflate();
        progressBar = stubView.findViewById(R.id.progressBar);
        init();
    }

    private void getSpecialDetail() {
        String url = ServerInfo.serviceIP + ServerInfo.getSpecialDetail + mSpecialId;
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, null, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_GET_DETAIL;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    private void init() {
        mHandler = new Handler(this);
        mSpecialId = getIntent().getIntExtra("specialId", 0);
        getSpecialDetail();
        String url = ServerInfo.h5IP + ServerInfo.getSpecialPage + mSpecialId;
        if (User.getInstance() == null) {
            url = url + "?app=android&multiple=" + CommonUtils.getFontSize();
        } else {
            url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&multiple=" + CommonUtils.getFontSize();
        }
//        WebSettings s = webView.getSettings();
//        CommonUtils.setWebviewUserAgent(s);
//        s.setTextZoom(100);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        webView.getSettings().setBlockNetworkImage(false);
//        s.setJavaScriptEnabled(true);       //js
//        s.setDomStorageEnabled(true);       //localStorage
        webView.setWebViewClient(new WebViewClient() {
            // url拦截
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
                view.loadUrl(url);
                // 相应完成返回true
                return true;
            }

            // 页面开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.show();
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.hide();
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
                //progressBar.setProgress(newProgress);
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
        webView.addJavascriptInterface(new JsToAndroid(), "clientJS");
        webView.loadUrl(url);
        progressBar.setColor("#001CA0FF", "#1CA0FF");
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_DETAIL:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mSpecial = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), Special.class);
                    }
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
            long id = Thread.currentThread().getId();
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
                    Intent it = new Intent(SpecialDetailActivity.this, NewLoginActivity.class);
                    if (bindPhone.equals("0")) {
                        it.putExtra("bindPhone", false);
                    } else {
                        it.putExtra("bindPhone", true);
                    }
                    it.putExtra("jump_url", ServerInfo.h5IP + "/specials/" + mSpecialId);
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
                    Intent it = new Intent(SpecialDetailActivity.this, NewLoginActivity.class);
                    if (bindPhone.equals("0")) {
                        it.putExtra("bindPhone", false);
                    } else {
                        it.putExtra("bindPhone", true);
                    }
                    it.putExtra("jump_url", ServerInfo.h5IP + "/specials/" + mSpecialId);
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
                    Intent it = new Intent(SpecialDetailActivity.this, BindPhoneActivity.class);
                    it.putExtra("hasLogined", true);
                    startActivityForResult(it, LOGIN_RESULT);
                }
            });
        }

        @JavascriptInterface
        public void shareEvent(String str) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    String url;
                    if (User.getInstance() != null) {
                        url = ServerInfo.h5IP + ServerInfo.getSpecialPage + mSpecialId + "?from_user_id=" + User.getInstance().getId() + "&from_url=" + ServerInfo.h5IP + ServerInfo.getSpecialPage + mSpecialId;
                    } else {
                        url = ServerInfo.h5IP + ServerInfo.getSpecialPage + mSpecialId + "?from_url=" + ServerInfo.h5IP + ServerInfo.getSpecialPage + mSpecialId;
                    }
                    showShare(url);
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
                        Intent it = new Intent(SpecialDetailActivity.this, AudioDetailActivity.class);
                        it.putExtra("audioId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("2")) {
                        Intent it = new Intent(SpecialDetailActivity.this, AlbumDetailActivity.class);
                        it.putExtra("albumId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("3")) {
                        Intent it = new Intent(SpecialDetailActivity.this, ArticleDetailActivity02.class);
                        it.putExtra("articleId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("4")) {
                        Intent it = new Intent(SpecialDetailActivity.this, VideoDetailActivity.class);
                        it.putExtra("videoId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("5")) {
                        Intent it = new Intent(SpecialDetailActivity.this, SpecialDetailActivity.class);
                        it.putExtra("specialId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("7")) {
                        Intent it = new Intent(SpecialDetailActivity.this, GalleriaActivity.class);
                        it.putExtra("galleriaId", Integer.parseInt(id));
                        startActivity(it);
                    }
                }
            });
        }

        @JavascriptInterface
        public void channelEvent(final String id, final String type, String title) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (type.equals("1")) {
                        //电台
                        Intent it = new Intent(SpecialDetailActivity.this, RadioDetailActivity.class);
                        //it.putExtra("radioId",radioGroups.get(groupPosition).getList().get(childPosition));
                        it.putExtra("radioId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("2")) {
                        //主播
                        Intent it = new Intent(SpecialDetailActivity.this, WebViewActivity.class);
                        it.putExtra("url", ServerInfo.h5HttpsIP + "/anchors/" + id);
                        startActivity(it);
                    } else if (type.equals("3")) {
                        //机构
                        Intent it = new Intent(SpecialDetailActivity.this, WebViewActivity.class);
                        it.putExtra("url", ServerInfo.h5HttpsIP + "/orgs/" + id);
                        startActivity(it);
                    } else if (type.equals("4")) {
                        //电视台
                        Intent it = new Intent(SpecialDetailActivity.this, TvDetailActivity.class);
                        it.putExtra("radioId", Integer.parseInt(id));
                        startActivity(it);
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_RESULT) {
            String url = ServerInfo.h5IP + ServerInfo.getSpecialPage + mSpecialId;
            if (User.getInstance() == null) {
                url = url + "?app=android" + "&multiple=" + CommonUtils.getFontSize();
            } else {
                //url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
                if (!TextUtils.isEmpty(mJumpUrl)) {
                    url = mJumpUrl + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&multiple=" + CommonUtils.getFontSize();
                } else {
                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&multiple=" + CommonUtils.getFontSize();
                }
                webView.loadUrl(url);
            }
            //webView.loadUrl(url);
        }
    }

    private void showShare(final String url) {
        if (mSpecial != null) {
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
                    String chanel = "1";
                    //点击新浪微博
                    if (SinaWeibo.NAME.equals(platform.getName())) {
                        chanel = "2";
                        //限制微博分享的文字不能超过20
                        if (!TextUtils.isEmpty(mSpecial.getCover())) {
                            paramsToShare.setImageUrl(mSpecial.getCover());
                        }
                        paramsToShare.setText(mSpecial.getTitle() + url);
                    } else if (QQ.NAME.equals(platform.getName())) {
                        chanel = "3";
                        paramsToShare.setTitle(mSpecial.getTitle());
                        if (!TextUtils.isEmpty(mSpecial.getCover())) {
                            paramsToShare.setImageUrl(mSpecial.getCover());
                        }
                        paramsToShare.setTitleUrl(url);
                        paramsToShare.setText(mSpecial.getDes());
                    } else if (Wechat.NAME.equals(platform.getName())) {
                        paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                        paramsToShare.setTitle(mSpecial.getTitle());
                        paramsToShare.setUrl(url);
                        if (!TextUtils.isEmpty(mSpecial.getCover())) {
                            paramsToShare.setImageUrl(mSpecial.getCover());
                        }
                        paramsToShare.setText(mSpecial.getDes());
                    } else if (WechatMoments.NAME.equals(platform.getName())) {
                        paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                        paramsToShare.setTitle(mSpecial.getTitle());
                        paramsToShare.setUrl(url);
                        if (!TextUtils.isEmpty(mSpecial.getCover())) {
                            paramsToShare.setImageUrl(mSpecial.getCover());
                        }
                    } else if (WechatFavorite.NAME.equals(platform.getName())) {
                        paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                        paramsToShare.setTitle(mSpecial.getTitle());
                        paramsToShare.setUrl(url);
                        if (!TextUtils.isEmpty(mSpecial.getCover())) {
                            paramsToShare.setImageUrl(mSpecial.getCover());
                        }
                    }
                    shareStatistics(chanel, "" + mSpecial.getId(), url);
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
}