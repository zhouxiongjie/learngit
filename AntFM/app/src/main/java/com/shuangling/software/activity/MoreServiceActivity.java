package com.shuangling.software.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.entity.User;
import com.shuangling.software.utils.PreloadWebView;
import com.shuangling.software.utils.ServerInfo;

import butterknife.ButterKnife;

//@EnableDragToClose()
public class MoreServiceActivity extends QMUIActivity/*AppCompatActivity*/ implements Handler.Callback {
    private static final int LOGIN_RESULT = 0x1;
    public static final int MSG_GET_DETAIL = 0x2;
    private static final int SHARE_SUCCESS = 0x3;
    private static final int SHARE_FAILED = 0x4;
//    @BindView(R.id.webView)
    WebView webView;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview/*activity_service_details*/);
//        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this); //
        FrameLayout fl_content = findViewById(R.id.fl_web_container);
        webView = PreloadWebView.getInstance().getWebView(this);
        fl_content.addView(webView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        init();
    }

    private void init() {
        mHandler = new Handler(this);
        String url = ServerInfo.h5IP + "/services";
        if (User.getInstance() == null) {
            if (MainActivity.sCurrentCity != null) {
                url = url + "?app=android&city=" + MainActivity.sCurrentCity.getCode();
            } else {
                url = url + "?app=android";
            }
        } else {
            if (MainActivity.sCurrentCity != null) {
                url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&city=" + MainActivity.sCurrentCity.getCode();
            } else {
                url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
            }
        }
//        WebSettings s = webView.getSettings();
//        CommonUtils.setWebviewUserAgent(s);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        webView.getSettings().setBlockNetworkImage(false);
//        s.setJavaScriptEnabled(true);       //js
//        s.setDomStorageEnabled(true);       //localStorage
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new JsToAndroid(), "clientJS");
        webView.loadUrl(url);
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

        //        @JavascriptInterface
//        public void loginEvent(String str){
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Intent it=new Intent(MoreServiceActivity.this,LoginActivity.class);
//                    startActivityForResult(it,LOGIN_RESULT);
//                }
//            });
//
//
//        }
        @JavascriptInterface
        public void loginEvent(final String bindPhone) {
            //mJumpUrl=null;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(MoreServiceActivity.this, NewLoginActivity.class);
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
            //mJumpUrl=url;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(MoreServiceActivity.this, NewLoginActivity.class);
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
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //showShare();
                    Intent it = new Intent(MoreServiceActivity.this, BindPhoneActivity.class);
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
                    showShare();
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
                        Intent it = new Intent(MoreServiceActivity.this, AudioDetailActivity.class);
                        it.putExtra("audioId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("2")) {
                        Intent it = new Intent(MoreServiceActivity.this, AlbumDetailActivity.class);
                        it.putExtra("albumId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("3")) {
                        Intent it = new Intent(MoreServiceActivity.this, ArticleDetailActivity02.class);
                        it.putExtra("articleId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("4")) {
                        Intent it = new Intent(MoreServiceActivity.this, VideoDetailActivity.class);
                        it.putExtra("videoId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("5")) {
                        Intent it = new Intent(MoreServiceActivity.this, SpecialDetailActivity.class);
                        it.putExtra("specialId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("7")) {
                        Intent it = new Intent(MoreServiceActivity.this, GalleriaActivity.class);
                        it.putExtra("galleriaId", Integer.parseInt(id));
                        startActivity(it);
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_RESULT && resultCode == Activity.RESULT_OK) {
            String url = ServerInfo.h5IP + "/services";
            if (User.getInstance() == null) {
                url = url + "?app=android";
            } else {
                url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
            }
            webView.loadUrl(url);
        }
    }

    private void showShare() {
//        if(mService!=null){
//            OnekeyShare oks = new OnekeyShare();
//            //关闭sso授权
//            oks.disableSSOWhenAuthorize();
//            final Platform qq= ShareSDK.getPlatform(QQ.NAME);
//            if(!qq.isClientValid()){
//                oks.addHiddenPlatform(QQ.NAME);
//            }
//
//            final Platform sina= ShareSDK.getPlatform(SinaWeibo.NAME);
//            if(!sina.isClientValid()){
//                oks.addHiddenPlatform(SinaWeibo.NAME);
//            }
//
//
//            oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
//                //自定义分享的回调想要函数
//                @Override
//                public void onShare(Platform platform,ShareParams paramsToShare) {
//
//                    //点击新浪微博
////                    if(SinaWeibo.NAME.equals(platform.getName())){
////                        //限制微博分享的文字不能超过20
////                        if(mArticle.getArticle().getCovers().size()>0){
////                            paramsToShare.setImageUrl(mArticle.getArticle().getCovers().get(0));
////                        }
////                        paramsToShare.setText(mArticle.getTitle()+ServerInfo.h5IP+ ServerInfo.getArticlePage+mArticleId+"?app=android");
////                    }
////                    else if(QQ.NAME.equals(platform.getName())){
////                        paramsToShare.setTitle(mArticle.getTitle());
////                        if(mArticle.getArticle().getCovers().size()>0){
////                            paramsToShare.setImageUrl(mArticle.getArticle().getCovers().get(0));
////                        }
////                        paramsToShare.setTitleUrl(ServerInfo.h5IP+ ServerInfo.getArticlePage+mArticleId+"?app=android");
////                        paramsToShare.setText(mArticle.getArticle().getContent());
////
////                    }else if(Wechat.NAME.equals(platform.getName())){
////                        paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
////                        paramsToShare.setTitle(mArticle.getTitle());
////                        paramsToShare.setUrl(ServerInfo.h5IP+ ServerInfo.getArticlePage+mArticleId+"?app=android");
////                        if(mArticle.getArticle().getCovers().size()>0){
////                            paramsToShare.setImageUrl(mArticle.getArticle().getCovers().get(0));
////                        }
////                        paramsToShare.setText(mArticle.getArticle().getContent());
////                    }else if(WechatMoments.NAME.equals(platform.getName())){
////                        paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
////                        paramsToShare.setTitle(mArticle.getTitle());
////                        paramsToShare.setUrl(ServerInfo.h5IP+ ServerInfo.getArticlePage+mArticleId+"?app=android");
////                        if(mArticle.getArticle().getCovers().size()>0){
////                            paramsToShare.setImageUrl(mArticle.getArticle().getCovers().get(0));
////                        }
////                    }else if(WechatFavorite.NAME.equals(platform.getName())){
////                        paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
////                        paramsToShare.setTitle(mArticle.getTitle());
////                        paramsToShare.setUrl(ServerInfo.h5IP+ ServerInfo.getArticlePage+mArticleId+"?app=android");
////                        if(mArticle.getArticle().getCovers().size()>0){
////                            paramsToShare.setImageUrl(mArticle.getArticle().getCovers().get(0));
////                        }
////                    }
//
//
//                }
//            });
//            oks.setCallback(new PlatformActionListener() {
//
//                @Override
//                public void onError(Platform arg0, int arg1, Throwable arg2) {
//                    Message msg=Message.obtain();
//                    msg.what=SHARE_FAILED;
//                    msg.obj=arg2.getMessage();
//                    mHandler.sendMessage(msg);
//                }
//
//                @Override
//                public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
//
//                    Message msg=Message.obtain();
//                    msg.what=SHARE_SUCCESS;
//                    mHandler.sendMessage(msg);
//                }
//
//                @Override
//                public void onCancel(Platform arg0, int arg1) {
//
//                }
//            });
//            // 启动分享GUI
//            oks.show(this);
//        }
    }

//    @Override
//    public void onBackPressed() {
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
