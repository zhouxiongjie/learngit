package com.shuangling.software.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.components.SimpleImmersionFragment;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.R;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.activity.ArticleDetailActivity;
import com.shuangling.software.activity.ArticleDetailActivity02;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.activity.BindPhoneActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.activity.LoginActivity;
import com.shuangling.software.activity.MainActivity;
import com.shuangling.software.activity.NewLoginActivity;
import com.shuangling.software.activity.SpecialDetailActivity;
import com.shuangling.software.activity.VideoDetailActivity;
import com.shuangling.software.activity.WebViewActivity;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.dialog.ShareDialog;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.ShareParameter;
import com.shuangling.software.entity.UpdateInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.MyGlideEngine;
import com.shuangling.software.utils.ServerInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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


public class DiscoverFragment extends SimpleImmersionFragment implements Handler.Callback {

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
    @BindView(R.id.statusBar)
    View statusBar;
    @BindView(R.id.activtyTitle)
    TopTitleBar activtyTitle;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private String mUrl;
    private String mTitle;
    private boolean mShowShare;

    Unbinder unbinder;
    private Handler mHandler;
    private String mJumpUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mHandler = new Handler(this);
        mUrl = getArguments().getString("url");
        mTitle=getArguments().getString("title");
        mShowShare=getArguments().getBoolean("showShare");

        EventBus.getDefault().register(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_descover, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;

    }





    public void jumpTo(String url,String title,boolean showShare) {
        mShowShare=showShare;
        if(mShowShare){
            activtyTitle.setMoreVisibility(true);
        }else {
            activtyTitle.setMoreVisibility(false);
        }

        mUrl = url;
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
        activtyTitle.setTitleText(title);
        webView.loadUrl(url);
    }


    private void init() {
        activtyTitle.setTitleText(mTitle);
        if(mShowShare){
            activtyTitle.setMoreVisibility(true);
        }else {
            activtyTitle.setMoreVisibility(false);
        }
        activtyTitle.setMoreAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//sdk>19才有用

                    String script = "javascript:_getShareEventParmas()";
                    webView.evaluateJavascript(script, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String responseJson) {
                            ShareParameter shareParameter = JSONObject.parseObject(responseJson, ShareParameter.class);

                            if(shareParameter!=null){
                                showShareDialog(shareParameter.getShareTitle(), shareParameter.getShareDesc(), shareParameter.getShareLogo(), shareParameter.getShareUrl());
                            }


                        }
                    });
                }

            }
        });
        TextView tv=activtyTitle.getTitleTextView();
        tv.setTextColor(getResources().getColor(R.color.white));
        activtyTitle.setCanBack(false);
        activtyTitle.setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });
        String url = mUrl;
        url=initUrl(url);
        WebSettings s = webView.getSettings();
        CommonUtils.setWebviewUserAgent(s);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.getSettings().setBlockNetworkImage(false);
        s.setTextZoom(100);
        s.setJavaScriptEnabled(true);       //js
        s.setDomStorageEnabled(true);       //localStorage
//        webView.setWebViewClient(new WebViewClient());


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
                //progressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                //progressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);

                if (url.startsWith(mUrl)) {
                    activtyTitle.setCanBack(false);
                } else {
                    activtyTitle.setCanBack(true);
                }
//                if(!webView.canGoBack()){
//
//                }else{
//                    activtyTitle.setCanBack(true);
//
//                }

            }

            // WebView加载的所有资源url
            @Override
            public void onLoadResource(WebView view, String url) {
//                if(url.startsWith(ServerInfo.serviceIP + "/v1/services")){
//                    activtyTitle.setCanBack(true);
//                }else if(url.startsWith(ServerInfo.h5IP + "/find")){
//                    activtyTitle.setCanBack(false);
//                }
//                if(!webView.canGoBack()){
//                    activtyTitle.setCanBack(true);
//                }else{
//                    activtyTitle.setCanBack(false);
//                }
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

            ;

            @Override
            // 处理javascript中的confirm
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            ;

            @Override
            // 处理javascript中的prompt
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            ;

            // 设置网页加载的进度条
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                long id = Thread.currentThread().getId();
                Log.i("onProgressChanged", "" + newProgress);
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
            public boolean onShowFileChooser(WebView mWebView, final ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;


                RxPermissions rxPermissions = new RxPermissions(getActivity());
                rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean granted) throws Exception {
                                if (granted) {
                                    String packageName = getContext().getPackageName();
                                    Matisse.from(DiscoverFragment.this)
                                            .choose(MimeType.of(MimeType.JPEG, MimeType.PNG)) // 选择 mime 的类型
                                            .countable(false)
                                            .maxSelectable(9) // 图片选择的最多数量
                                            .spanCount(4)
                                            .capture(true)
                                            .captureStrategy(new CaptureStrategy(true, packageName + ".fileprovider"))
                                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                            .thumbnailScale(1.0f) // 缩略图的比例
                                            .theme(R.style.Matisse_Zhihu)
                                            .imageEngine(new MyGlideEngine()) // 使用的图片加载引擎
                                            .forResult(REQUEST_SELECT_FILE); // 设置作为标记的请求码
                                } else {
                                    ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                                }
                            }
                        });

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



    private  void showShareDialog(final String title, final String desc, final String logo, final String url) {

        ShareDialog dialog = ShareDialog.getInstance(false,false);
        dialog.setIsShowPosterButton(false);
        dialog.setIsHideSecondGroup(true);
        dialog.setShareHandler(new ShareDialog.ShareHandler() {
            @Override
            public void onShare(String platform) {

                showShare(platform,title, desc, logo, url);

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
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
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
        dialog.show(getChildFragmentManager(), "ShareDialog");
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

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).statusBarView(statusBar).init();
    }

    private final class JsToAndroid {

        @JavascriptInterface
        public void backEvent(String str) {
            long id = Thread.currentThread().getId();
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                }
            });

        }

        @JavascriptInterface
        public void loginEvent(final String bindPhone) {
            mJumpUrl=null;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
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
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
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
                    Intent it = new Intent(getContext(), BindPhoneActivity.class);
                    it.putExtra("hasLogined",true);
                    startActivityForResult(it, LOGIN_RESULT);

                }
            });
        }





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
        public void pageEvent(final String id, final String type, final String title) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //1音频、2专辑、3文章、4视频、5专题、7图集
                    if (type.equals("1")) {
                        Intent it = new Intent(getContext(), AudioDetailActivity.class);
                        it.putExtra("audioId", Integer.parseInt(id));
                        startActivity(it);

                    } else if (type.equals("2")) {
                        Intent it = new Intent(getContext(), AlbumDetailActivity.class);
                        it.putExtra("albumId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("3")) {
                        Intent it = new Intent(getContext(), ArticleDetailActivity02.class);
                        it.putExtra("articleId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("4")) {
                        Intent it = new Intent(getContext(), VideoDetailActivity.class);
                        it.putExtra("videoId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("5")) {
                        Intent it = new Intent(getContext(), SpecialDetailActivity.class);
                        it.putExtra("specialId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("7")) {
                        Intent it = new Intent(getContext(), GalleriaActivity.class);
                        it.putExtra("galleriaId", Integer.parseInt(id));
                        startActivity(it);
                    }
                }
            });

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("OnLoginSuccess")||event.getEventName().equals("OnQuitLogin")) {
            String url=initUrl(mUrl);
            webView.loadUrl(url);
        }else if(event.getEventName().equals("onFontSizeChanged")){
            String url=initUrl(mUrl);
            webView.loadUrl(url);
        }
    }




    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
//            String url = mUrl;
//            if (User.getInstance() == null) {
//                if (MainActivity.sCurrentCity != null) {
//                    url = url + "?app=android&city=" + MainActivity.sCurrentCity.getCode();
//                } else {
//                    url = url + "?app=android";
//                }
//            } else {
//                if (MainActivity.sCurrentCity != null) {
//                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&city=" + MainActivity.sCurrentCity.getCode();
//                } else {
//                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
//                }
//
//            }
//            webView.loadUrl(url);
            webView.clearHistory();
//            activtyTitle.setCanBack(false);
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }


    @Override
    public void onDestroyView() {
        webView.destroy();
        unbinder.unbind();
        super.onDestroyView();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_RESULT) {
//            String url = webView.getUrl();
//            if (url.indexOf("?") > 0) {
//                url = url.substring(0, url.indexOf("?"));
//            }
//            if (User.getInstance() == null) {
//                url = url + "?app=android";
//            } else {
//
//                if(!TextUtils.isEmpty(mJumpUrl)){
//                    url = mJumpUrl + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
//                }else{
//                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
//                }
//
//                //url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
//                webView.loadUrl(url);
//            }

//            String url=initUrl(mUrl);
//            if(!TextUtils.isEmpty(mJumpUrl)){
//                url = mJumpUrl + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android"+"&multiple="+CommonUtils.getFontSize();
//            }
//            //url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
//            webView.loadUrl(url);


            String url=initUrl(mUrl);
            if(User.getInstance()!=null){
                if(!TextUtils.isEmpty(mJumpUrl)){
                    url = mJumpUrl + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android"+"&multiple="+CommonUtils.getFontSize();
                }
                webView.loadUrl(url);
            }


        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE && resultCode == Activity.RESULT_OK && data != null) {
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
            Uri result = data == null || resultCode != Activity.RESULT_OK ? null : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }


//    private void showShare(final String id, final String title, final String des,final String url, final String logo) {
//
//
//        OnekeyShare oks = new OnekeyShare();
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//        final Platform qq = ShareSDK.getPlatform(QQ.NAME);
//        if (!qq.isClientValid()) {
//            oks.addHiddenPlatform(QQ.NAME);
//        }
//
//        final Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
//        if (!sina.isClientValid()) {
//            oks.addHiddenPlatform(SinaWeibo.NAME);
//        }
//
//
//        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
//            //自定义分享的回调想要函数
//            @Override
//            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
//                String chanel = "1";
//                //点击新浪微博
//                if (SinaWeibo.NAME.equals(platform.getName())) {
//                    //限制微博分享的文字不能超过20
//                    chanel = "2";
//                    //paramsToShare.setText(title + ServerInfo.activity + "qaa/game-result/" + id);
//                    paramsToShare.setText(title + url);
//                } else if (QQ.NAME.equals(platform.getName())) {
//                    chanel = "3";
//                    paramsToShare.setTitle(title);
//                    if (!TextUtils.isEmpty(logo)) {
//                        paramsToShare.setImageUrl(logo);
//                    }
//                    paramsToShare.setTitleUrl(url);
//                    paramsToShare.setText(des);
//
//                } else if (Wechat.NAME.equals(platform.getName())) {
//                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
//                    paramsToShare.setTitle(title);
//                    paramsToShare.setUrl(url);
//                    if (!TextUtils.isEmpty(logo)) {
//                        paramsToShare.setImageUrl(logo);
//                    }
//                    paramsToShare.setText(des);
//                } else if (WechatMoments.NAME.equals(platform.getName())) {
//                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
//                    paramsToShare.setTitle(title);
//                    paramsToShare.setUrl(url);
//                    if (!TextUtils.isEmpty(logo)) {
//                        paramsToShare.setImageUrl(logo);
//                    }
//                } else if (WechatFavorite.NAME.equals(platform.getName())) {
//                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
//                    paramsToShare.setTitle(title);
//                    paramsToShare.setUrl(url);
//                    if (!TextUtils.isEmpty(logo)) {
//                        paramsToShare.setImageUrl(logo);
//                    }
//                }
//                shareStatistics(chanel, "" + id, url);
//
//            }
//        });
//        oks.setCallback(new PlatformActionListener() {
//
//            @Override
//            public void onError(Platform arg0, int arg1, Throwable arg2) {
//                Message msg = Message.obtain();
//                msg.what = SHARE_FAILED;
//                msg.obj = arg2.getMessage();
//                mHandler.sendMessage(msg);
//            }
//
//            @Override
//            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
//
//                Message msg = Message.obtain();
//                msg.what = SHARE_SUCCESS;
//                mHandler.sendMessage(msg);
//
//            }
//
//            @Override
//            public void onCancel(Platform arg0, int arg1) {
//
//            }
//        });
//        // 启动分享GUI
//        oks.show(getContext());
//
//    }


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
        oks.show(getContext());
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
        OkHttpUtils.post(url, params, new OkHttpCallback(getContext()) {

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


    private String initUrl(String url){
        if (User.getInstance() == null) {
            if (MainActivity.sCurrentCity != null) {
                if(url.contains("?")){
                    url = url + "&app=android&city=" + MainActivity.sCurrentCity.getCode()+"&multiple="+CommonUtils.getFontSize();
                }else{
                    url = url + "?app=android&city=" + MainActivity.sCurrentCity.getCode()+"&multiple="+CommonUtils.getFontSize();
                }
            } else {
                if(url.contains("?")){
                    url = url + "&app=android"+"&multiple="+CommonUtils.getFontSize();
                }else{
                    url = url + "?app=android"+"&multiple="+CommonUtils.getFontSize();
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


        return url;
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
