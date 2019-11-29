package com.shuangling.software.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.gyf.immersionbar.ImmersionBar;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@EnableDragToClose()
public class WebViewBackActivity extends AppCompatActivity implements Handler.Callback {

    private static final int LOGIN_RESULT = 0x1;
    public static final int MSG_GET_DETAIL = 0x2;
    private static final int SHARE_SUCCESS = 0x3;
    private static final int SHARE_FAILED = 0x4;


    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.activtyTitle)
    TopTitleBar activtyTitle;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Handler mHandler;
    private String mUrl;
    private String mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview_back);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        init();
    }


    private void init() {
        mUrl = getIntent().getStringExtra("url");
        mTitle= getIntent().getStringExtra("title");
        activtyTitle.setTitleText(mTitle);
        if (User.getInstance() == null) {
            if (MainActivity.sCurrentCity != null) {
                mUrl = mUrl + "?app=android&city=" + MainActivity.sCurrentCity.getCode();
            } else {
                mUrl = mUrl + "?app=android";
            }

        } else {
            if (MainActivity.sCurrentCity != null) {
                mUrl = mUrl + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&city=" + MainActivity.sCurrentCity.getCode();
            } else {
                mUrl = mUrl + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
            }

        }

        mHandler = new Handler(this);
        WebSettings s = webView.getSettings();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.getSettings().setBlockNetworkImage(false);
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
        webView.addJavascriptInterface(new JsToAndroid(), "clientJS");
        webView.loadUrl(mUrl);


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
            long id = Thread.currentThread().getId();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });

        }

        @JavascriptInterface
        public void loginEvent(String str) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(WebViewBackActivity.this, LoginActivity.class);
                    startActivityForResult(it, LOGIN_RESULT);
                }
            });


        }


        @JavascriptInterface
        public void shareEvent(final String str) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    showShare(str);
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
                        Intent it = new Intent(WebViewBackActivity.this, WebViewBackActivity.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LOGIN_RESULT && resultCode == Activity.RESULT_OK) {
            String url = webView.getUrl();
            if (User.getInstance() == null) {
                url = url + "?app=android";
            } else {
                url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
            }
            webView.loadUrl(url);

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
                String chanel="1";
                if (SinaWeibo.NAME.equals(platform.getName())) {
                    //限制微博分享的文字不能超过20
                    chanel="2";
                    paramsToShare.setText("刺激好玩的活动" + ServerInfo.activity + "qaa/game-result/" + id);
                } else if (QQ.NAME.equals(platform.getName())) {
                    chanel="3";
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
                shareStatistics(chanel,""+id,ServerInfo.activity + "qaa/game-result/" + id);

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


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }

    }

    public void shareStatistics(String channel,String postId,String shardUrl) {

        String url = ServerInfo.serviceIP + ServerInfo.shareStatistics;
        Map<String, String> params = new HashMap<>();
        if(User.getInstance()!=null){
            params.put("user_id", ""+User.getInstance().getId());
        }
        params.put("channel", channel);
        params.put("post_id", postId);
        params.put("source_type", "3");
        params.put("type", "1");
        params.put("shard_url", shardUrl);
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {
                Log.i("test",response);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                Log.i("test",exception.toString());

            }
        });

    }
}
