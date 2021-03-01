package com.shuangling.software.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.hjq.toast.ToastUtils;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.activity.ui.WebProgress;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.MyGlideEngine;
import com.shuangling.software.utils.PreloadWebView;
import com.shuangling.software.utils.ServerInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

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

public class CluesActivity extends QMUIActivity/*AppCompatActivity*/ implements Handler.Callback, AMapLocationListener {
    private static final int LOGIN_RESULT = 0x1;
    public static final int MSG_GET_DETAIL = 0x2;
    private static final int SHARE_SUCCESS = 0x3;
    private static final int SHARE_FAILED = 0x4;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 2;
    //    @BindView(R.id.webView)
    WebView webView;
    //    @BindView(R.id.progressBar)
    /*ProgressBar*/ WebProgress progressBar;

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    private Handler mHandler;
    private String mUrl;
    private AMapLocationClient mlocationClient;
    private LatLng mlocation;
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout./*activity_webview*/activity_clues);
        //CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        FrameLayout fl_content = findViewById(R.id.fl_web_container);
        webView = PreloadWebView.getInstance().getWebView(this);
        fl_content.addView(webView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        View stubView = ((ViewStub) findViewById(R.id.viewStub)).inflate();
        progressBar = stubView.findViewById(R.id.progressBar);
        progressBar.setColor(CommonUtils.getTranslucentThemeColor(this),CommonUtils.getThemeColor(this));
        init();
    }

    private void initTopBar() {
        mTopBar.addLeftImageButton(R.drawable.ic_left, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //overridePendingTransition(R.anim.slide_still, R.anim.slide_out_right);
            }
        });

        mTopBar.setTitle("报料");
    }


    private void init() {
        initTopBar();
        mUrl = getIntent().getStringExtra("url");
        mUrl = mUrl + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&phone=" + User.getInstance().getPhone() + "&name=" + User.getInstance().getNickname();
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
                return true;
            }

            // 页面开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (progressBar != null) progressBar.show();
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
                long id = Thread.currentThread().getId();
//                Log.i("onProgressChanged", "" + newProgress);
                if (newProgress == 100) {
//                    progressBar.setVisibility(GONE);
                    if (progressBar != null) progressBar.hide();
                } else {
//                    if (progressBar.getVisibility() == GONE)
//                        progressBar.setVisibility(VISIBLE);
                    if (progressBar != null) progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            // 设置程序的Title
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override//webview
            public void onGeolocationPermissionsShowPrompt(String origin, android.webkit.GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

//            @Override//x5
//            public void onGeolocationPermissionsShowPrompt(String s, GeolocationPermissionsCallback geolocationPermissionsCallback) {
//                geolocationPermissionsCallback.invoke(s, true, false);
//                super.onGeolocationPermissionsShowPrompt(s, geolocationPermissionsCallback);
//            }

            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            // For Lollipop 5.0+ Devices
            @Override
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
                uploadMessage = filePathCallback;
                RxPermissions rxPermissions = new RxPermissions(CluesActivity.this);
                rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean granted) throws Exception {
                                if (granted) {
                                    String packageName = getPackageName();
                                    Matisse.from(CluesActivity.this)
                                            .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.MP4)) // 选择 mime 的类型
                                            .countable(false)
                                            .maxSelectable(1) // 图片选择的最多数量
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
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }
        });
        webView.addJavascriptInterface(new JsToAndroid(), "clientJS");
//initLocation();
        requestpermissions();
//webView.loadUrl(mUrl);
    }

    private void requestpermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(needPermissions)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            //获得定位权限 开始定位
                            //startLocation();
                        } else {
                            //未获得定位权限 直接加载
                            //webView.loadUrl(mUrl);
                        }
                        webView.loadUrl(mUrl);
                        if (progressBar != null) progressBar.show();
                    }
                });
    }

    /**
     * 初始化定位，设置回调监听
     */
    private void initLocation() {
//初始化client
        mlocationClient = new AMapLocationClient(this.getApplicationContext());
        // 设置定位监听
        mlocationClient.setLocationListener(this);
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(needPermissions)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            //获得定位权限 开始定位
                            startLocation();
                        } else {
                            //未获得定位权限 直接加载
                            webView.loadUrl(mUrl);
                            if (progressBar != null) progressBar.show();
                        }
                    }
                });
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            mlocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            String mAddress = aMapLocation.getAddress();
            String url = String.format("%s&lat=%f&lon=%f&addr=%s", mUrl, mlocation.latitude, mlocation.longitude, mAddress);
            webView.loadUrl(url);
            if (progressBar != null) progressBar.show();
        } else {
            ToastUtils.show("获取定位失败,请确认是否开启GPS定位!");
            webView.loadUrl(mUrl);
            if (progressBar != null) progressBar.show();
        }
    }
/**
 * 开始定位
 */
    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    public static boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        //if (gps || network) {
        if (gps) {
            return true;
        }
        return false;
    }

    private void startLocation() {
        mlocationClient.setLocationOption(getOption());
        // 启动定位
        mlocationClient.startLocation();
    }

    /**
     * 设置定位参数
     *
     * @return 定位参数类
     */
    private AMapLocationClientOption getOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setLocationCacheEnable(false);//设置是否返回缓存中位置，默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        //设置定位间隔,单位毫秒,默认为2000ms
        //mOption.setInterval(4000);
        return mOption;
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
//                    Intent it = new Intent(CluesActivity.this, LoginActivity.class);
//                    startActivityForResult(it, LOGIN_RESULT);
//                }
//            });
//        }
        @JavascriptInterface
        public void loginEvent(final String bindPhone) {
            //mJumpUrl=null;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(CluesActivity.this, NewLoginActivity.class);
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
            // mJumpUrl=url;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(CluesActivity.this, NewLoginActivity.class);
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
                    Intent it = new Intent(CluesActivity.this, BindPhoneActivity.class);
                    it.putExtra("hasLogined", true);
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
                        Intent it = new Intent(CluesActivity.this, AudioDetailActivity.class);
                        it.putExtra("audioId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("2")) {
                        Intent it = new Intent(CluesActivity.this, AlbumDetailActivity.class);
                        it.putExtra("albumId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("3")) {
                        Intent it = new Intent(CluesActivity.this, ArticleDetailActivity02.class);
                        it.putExtra("articleId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("4")) {
                        Intent it = new Intent(CluesActivity.this, VideoDetailActivity.class);
                        it.putExtra("videoId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("5")) {
                        Intent it = new Intent(CluesActivity.this, SpecialDetailActivity.class);
                        it.putExtra("specialId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("7")) {
                        Intent it = new Intent(CluesActivity.this, GalleriaActivity.class);
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
                    Intent it = new Intent(CluesActivity.this, ContentActivity.class);
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
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE && resultCode == RESULT_OK) {
                if (uploadMessage == null)
                    return;
                List<String> paths = Matisse.obtainPathResult(data);
                //List<Uri> selects = Matisse.obtainResult(data);
                //File file = new File(CommonUtils.getRealFilePath(this, selects.get(0)));
                //Uri[] urls = selects.toArray(new Uri[selects.size()]);
                Uri uri = Uri.fromFile(new File(paths.get(0)));
                uploadMessage.onReceiveValue(new Uri[]{uri});
//uploadMessage.onReceiveValue(urls);
                //uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                uploadMessage = null;
            } else if (requestCode == REQUEST_SELECT_FILE && resultCode == RESULT_CANCELED) {
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                uploadMessage = null;
                return;
            }
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
                if (SinaWeibo.NAME.equals(platform.getName())) {
                    //限制微博分享的文字不能超过20
                    paramsToShare.setText("刺激好玩的活动" + ServerInfo.activity + "qaa/game-result/" + id);
                } else if (QQ.NAME.equals(platform.getName())) {
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
//                Message msg = Message.obtain();
//                msg.what = SHARE_SUCCESS;
//                mHandler.sendMessage(msg);
                String chanel;
                if (SinaWeibo.NAME.equals(arg0.getName())) {
                    chanel = "2";
                } else if (QQ.NAME.equals(arg0.getName())) {
                    chanel = "3";
                } else {
                    chanel = "1";
                }
                shareStatistics(chanel, "" + id, ServerInfo.activity + "qaa/game-result/" + id);
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
            }
        });
        // 启动分享GUI
        oks.show(this);
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
//在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
//mlocationClient.stopLocation();
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
