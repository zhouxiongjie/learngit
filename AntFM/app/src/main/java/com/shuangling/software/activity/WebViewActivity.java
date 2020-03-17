package com.shuangling.software.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.MyGlideEngine;
import com.shuangling.software.utils.ServerInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.youngfeng.snake.annotations.EnableDragToClose;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
import io.reactivex.functions.Consumer;
import okhttp3.Call;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@EnableDragToClose()
public class WebViewActivity extends AppCompatActivity implements Handler.Callback {

    private static final int LOGIN_RESULT = 0x1;
    public static final int MSG_GET_DETAIL = 0x2;
    private static final int SHARE_SUCCESS = 0x3;
    private static final int SHARE_FAILED = 0x4;

    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 2;

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Handler mHandler;
    private String mUrl;
    private int mActivityId;
    private String mJumpUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        init();
    }


    private void init() {
        EventBus.getDefault().register(this);
        mUrl = getIntent().getStringExtra("url");
        mActivityId= getIntent().getIntExtra("activityId",-1);
        String url=mUrl;
        url=initUrl(url);


        mHandler = new Handler(this);
        WebSettings s = webView.getSettings();
        CommonUtils.setWebviewUserAgent(s);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.getSettings().setBlockNetworkImage(false);
        s.setTextZoom(100);
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


            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor

            protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }


            // For Lollipop 5.0+ Devices
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, final ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;


                RxPermissions rxPermissions = new RxPermissions(WebViewActivity.this);
                rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean granted) throws Exception {
                                if(granted){
                                    String packageName = getPackageName();
                                    Matisse.from(WebViewActivity.this)
                                            .choose(MimeType.of(MimeType.JPEG,MimeType.PNG)) // 选择 mime 的类型
                                            .countable(false)
                                            .maxSelectable(9) // 图片选择的最多数量
                                            .spanCount(4)
                                            .capture(true)
                                            .captureStrategy(new CaptureStrategy(true,packageName+".fileprovider"))
                                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                            .thumbnailScale(1.0f) // 缩略图的比例
                                            .theme(R.style.Matisse_Zhihu)
                                            .imageEngine(new MyGlideEngine()) // 使用的图片加载引擎
                                            .forResult(REQUEST_SELECT_FILE); // 设置作为标记的请求码
                                }else{
                                    ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                                }
                            }
                        });



//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                //startActivityForResult(intent, REQUEST_SELECT_FILE);
//                //Intent intent = fileChooserParams.createIntent();
//                try {
//                    startActivityForResult(intent, REQUEST_SELECT_FILE);
//                } catch (ActivityNotFoundException e) {
//                    uploadMessage = null;
//                    Toast.makeText(getBaseContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
//                    return false;
//                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }


            protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }
        });
        webView.addJavascriptInterface(new JsToAndroid(), "clientJS");
        webView.loadUrl(url);


    }



    private String initUrl(String url){
        if (User.getInstance() == null) {
            if (MainActivity.sCurrentCity != null) {
                if(url.contains("?")){
                    url = url + "&app=android&city=" + MainActivity.sCurrentCity.getCode();
                }else{
                    url = url + "?app=android&city=" + MainActivity.sCurrentCity.getCode();
                }
            } else {
                if(url.contains("?")){
                    url = url + "&app=android";
                }else{
                    url = url + "?app=android";
                }
            }


        } else {
            if (MainActivity.sCurrentCity != null) {
                if(url.contains("?")){
                    url = url + "&Authorization=" + User.getInstance().getAuthorization() + "&app=android&city=" + MainActivity.sCurrentCity.getCode();
                }else{
                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&city=" + MainActivity.sCurrentCity.getCode();
                }
            } else {
                if(url.contains("?")){
                    url = url + "&Authorization=" + User.getInstance().getAuthorization() + "&app=android";
                }else{
                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
                }
            }
        }
        if(mActivityId!=-1){
            url=url+"&qaa_act_id="+mActivityId;
        }

        return url;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("OnLoginSuccess")||event.getEventName().equals("OnQuitLogin")) {
            String url=mUrl;
            url=initUrl(mUrl);
            webView.loadUrl(url);
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
            long id = Thread.currentThread().getId();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });

        }

//        @JavascriptInterface
//        public void loginEvent(String str) {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Intent it = new Intent(WebViewActivity.this, LoginActivity.class);
//                    startActivityForResult(it, LOGIN_RESULT);
//                }
//            });
//
//
//        }
        @JavascriptInterface
        public void loginEvent(final String bindPhone) {
            mJumpUrl=null;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(WebViewActivity.this, NewLoginActivity.class);
                    if(bindPhone.equals("0")){
                        it.putExtra("bindPhone",false);
                    }else{
                        it.putExtra("bindPhone",true);
                    }
                    startActivityForResult(it, LOGIN_RESULT);
                }
            });


        }



        @JavascriptInterface
        public void loginEvent(final String bindPhone,String url) {
            mJumpUrl=url;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(WebViewActivity.this, NewLoginActivity.class);
                    if(bindPhone.equals("0")){
                        it.putExtra("bindPhone",false);
                    }else{
                        it.putExtra("bindPhone",true);
                    }
                    startActivityForResult(it, LOGIN_RESULT);
                }
            });

        }

        @JavascriptInterface
        public void bindPhoneEvent(final String url) {
            mJumpUrl=url;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //showShare();
                    Intent it = new Intent(WebViewActivity.this, BindPhoneActivity.class);
                    it.putExtra("hasLogined",true);
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
        public void shareEvent(final String id, final String title, final String des,final String url, final String logo) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    showShare(id, title, des,url, logo);
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
                        Intent it=new Intent(WebViewActivity.this,RadioDetailActivity.class);
                        //it.putExtra("radioId",radioGroups.get(groupPosition).getList().get(childPosition));
                        it.putExtra("radioId",Integer.parseInt(id));
                        startActivity(it);

                    }else if (type.equals("2")) {
                        //主播
                        Intent it = new Intent(WebViewActivity.this, WebViewActivity.class);
                        it.putExtra("url", ServerInfo.h5HttpsIP+"/anchors/"+id);
                        startActivity(it);

                    } else if (type.equals("3")) {
                        //机构
                        Intent it = new Intent(WebViewActivity.this, WebViewActivity.class);
                        it.putExtra("url", ServerInfo.h5HttpsIP+"/orgs/"+id);
                        startActivity(it);
                    }else if (type.equals("4")) {
                        //电视台
                        Intent it=new Intent(WebViewActivity.this,TvDetailActivity.class);
                        it.putExtra("radioId",Integer.parseInt(id));
                        startActivity(it);

                    }
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
                        Intent it = new Intent(WebViewActivity.this, AudioDetailActivity.class);
                        it.putExtra("audioId", Integer.parseInt(id));
                        startActivity(it);

                    } else if (type.equals("2")) {
                        Intent it = new Intent(WebViewActivity.this, AlbumDetailActivity.class);
                        it.putExtra("albumId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("3")) {
                        Intent it = new Intent(WebViewActivity.this, ArticleDetailActivity.class);
                        it.putExtra("articleId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("4")) {
                        Intent it = new Intent(WebViewActivity.this, VideoDetailActivity.class);
                        it.putExtra("videoId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("5")) {
                        Intent it = new Intent(WebViewActivity.this, SpecialDetailActivity.class);
                        it.putExtra("specialId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("7")) {
                        Intent it = new Intent(WebViewActivity.this, GalleriaActivity.class);
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

                    Intent it = new Intent(WebViewActivity.this, ContentActivity.class);
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

//        if (requestCode == LOGIN_RESULT) {
//            String url = webView.getUrl();
//            if (url.indexOf("?") > 0) {
//                url = url.substring(0, url.indexOf("?"));
//            }
//            if (User.getInstance() == null) {
//                url = url + "?app=android"+"&multiple="+CommonUtils.getFontSize();
//            } else {
//
//                if(!TextUtils.isEmpty(mJumpUrl)){
//                    url = mJumpUrl + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android"+"&multiple="+CommonUtils.getFontSize();
//                }else{
//                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android"+"&multiple="+CommonUtils.getFontSize();
//                }
//                //url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
//                webView.loadUrl(url);
//            }
//
//        }
        if (requestCode == LOGIN_RESULT) {

            String url=initUrl(mUrl);
            if(User.getInstance()!=null){
                if(!TextUtils.isEmpty(mJumpUrl)){
                    url = mJumpUrl + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
                }
                webView.loadUrl(url);
            }
            

        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE&&resultCode == RESULT_OK && data != null) {
                if (uploadMessage == null)
                    return;

                //List<String> paths=Matisse.obtainPathResult(data);
                List<Uri> selects = Matisse.obtainResult(data);
                //File file = new File(CommonUtils.getRealFilePath(this, selects.get(0)));
                Uri[] urls = selects.toArray(new Uri[selects.size()]);
                uploadMessage.onReceiveValue(urls);
                //uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = data == null || resultCode != MainActivity.RESULT_OK ? null : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
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
                String chanel="1";
                //点击新浪微博
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



    private void showShare(final String id, final String title, final String des,final String url, final String logo) {


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
                    //限制微博分享的文字不能超过20
                    chanel = "2";
                    //paramsToShare.setText(title + ServerInfo.activity + "qaa/game-result/" + id);
                    paramsToShare.setText(title + url);
                } else if (QQ.NAME.equals(platform.getName())) {
                    chanel = "3";
                    paramsToShare.setTitle(title);
                    if (!TextUtils.isEmpty(logo)) {
                        paramsToShare.setImageUrl(logo);
                    }
                    paramsToShare.setTitleUrl(url);
                    paramsToShare.setText(des);

                } else if (Wechat.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(url);
                    if (!TextUtils.isEmpty(logo)) {
                        paramsToShare.setImageUrl(logo);
                    }
                    paramsToShare.setText(des);
                } else if (WechatMoments.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(url);
                    if (!TextUtils.isEmpty(logo)) {
                        paramsToShare.setImageUrl(logo);
                    }
                } else if (WechatFavorite.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(url);
                    if (!TextUtils.isEmpty(logo)) {
                        paramsToShare.setImageUrl(logo);
                    }
                }
                shareStatistics(chanel, "" + id, url);

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





    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
