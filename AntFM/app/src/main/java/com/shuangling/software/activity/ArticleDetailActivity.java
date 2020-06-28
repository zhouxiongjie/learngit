package com.shuangling.software.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.player.IPlayer;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;
import com.gyf.immersionbar.ImmersionBar;
import com.mylhyl.circledialog.CircleDialog;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.entity.Article;
import com.shuangling.software.entity.ArticleVoicesInfo;
import com.shuangling.software.entity.Audio;
import com.shuangling.software.entity.AudioInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.FloatWindowUtil;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jake.share.frdialog.dialog.FRDialog;
import cn.jake.share.frdialog.interfaces.FRDialogClickListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
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

import static com.shuangling.software.service.AudioPlayerService.PLAY_ORDER;
import static com.shuangling.software.utils.CommonUtils.NETWORKTYPE_WIFI;

@EnableDragToClose()
public class ArticleDetailActivity extends BaseAudioActivity implements Handler.Callback {

    private static final int LOGIN_RESULT = 0x1;
    public static final int MSG_GET_DETAIL = 0x2;
    private static final int SHARE_SUCCESS = 0x3;
    private static final int SHARE_FAILED = 0x4;
    public static final int MSG_GET_VOICES = 0x5;
    public static final int REQUEST_PERMISSION_CODE = 0x0110;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.root)
    RelativeLayout root;
    private int mArticleId;


    @BindView(R.id.webView)
    WebView webView;

    private Handler mHandler;
    private Article mArticle;

    private ArticleVoicesInfo mArticleVoicesInfo;
    private ViewSkeletonScreen mViewSkeletonScreen;
    private String mJumpUrl;
//    private Runnable runnable=new Runnable() {
//        @Override
//        public void run() {
//            mViewSkeletonScreen.hide();
//        }
//    };

    private boolean firstTime=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_details);
        //CommonUtils.transparentStatusBar(this);
        ImmersionBar.with(this).statusBarDarkFont(true).fitsSystemWindows(true).keyboardEnable(true)  //解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE).init();
        ButterKnife.bind(this);

//        mViewSkeletonScreen = Skeleton.bind(root)
//                .load(R.layout.skeleton_article_detail)
//                .shimmer(false)
//                .angle(20)
//                .duration(1000)
//                .color(R.color.shimmer_color)
//                .show();
        init();
    }


    private void getArticleDetail() {

        String url = ServerInfo.serviceIP + ServerInfo.getArticleDetail + mArticleId;
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


    private void articleVoices() {

        String url = ServerInfo.serviceIP + ServerInfo.articleVoices + mArticleId;
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, null, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_VOICES;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }


    private void init() {
        progressBar.setMax(100);
        mHandler = new Handler(this);

        //mHandler.postDelayed(runnable,8000);
        mArticleId = getIntent().getIntExtra("articleId", 0);
        articleVoices();
        getArticleDetail();
        String url = ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId;
        int size = 1;
        int netLoad = SharedPreferencesUtils.getIntValue(SettingActivity.NET_LOAD, 0);
        if (netLoad == 0 || CommonUtils.getNetWorkType(this) == NETWORKTYPE_WIFI) {
            size = 2;
        }

        if (User.getInstance() == null) {
            url = url + "?app=android&size=" + size+"&multiple="+CommonUtils.getFontSize();
        } else {
            url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&size=" + size+"&multiple="+CommonUtils.getFontSize();
        }

        WebSettings s = webView.getSettings();
        CommonUtils.setWebviewUserAgent(s);
        s.setTextZoom(100);
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



//                int w = View.MeasureSpec.makeMeasureSpec(0,
//                        View.MeasureSpec.UNSPECIFIED);
//                int h = View.MeasureSpec.makeMeasureSpec(0,
//                        View.MeasureSpec.UNSPECIFIED);
//                //重新测量
//                view.measure(w, h);
//                mWebViewHeight = view.getHeight();
//                Log.i(TAG, "WEBVIEW高度:" + view.getHeight());


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
//                if (newProgress == 100) {
//                    progressBar.setVisibility(GONE);
//                } else {
//                    if (progressBar.getVisibility() == GONE)
//                        progressBar.setVisibility(VISIBLE);
//                    progressBar.setProgress(newProgress);
//                }
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


    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_DETAIL:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mArticle = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), Article.class);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case SHARE_SUCCESS:
                break;
            case SHARE_FAILED:
                break;
            case MSG_GET_VOICES:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mArticleVoicesInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), ArticleVoicesInfo.class);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            mJumpUrl=null;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(ArticleDetailActivity.this, NewLoginActivity.class);
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
                    Intent it = new Intent(ArticleDetailActivity.this, NewLoginActivity.class);
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
                    Intent it = new Intent(ArticleDetailActivity.this, BindPhoneActivity.class);
                    it.putExtra("hasLogined",true);
                    startActivityForResult(it, LOGIN_RESULT);

                }
            });
        }



        @JavascriptInterface
        public void shareEvent(String str) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    if (mArticle != null) {
                        String logo = "";
                        if (mArticle.getArticle().getCovers().size() > 0) {
                            logo = mArticle.getArticle().getCovers().get(0);
                        }
                        showShare(mArticle.getTitle(), mArticle.getDes(), logo, ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId + "?app=android");
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
                        Intent it = new Intent(ArticleDetailActivity.this, AudioDetailActivity.class);
                        it.putExtra("audioId", Integer.parseInt(id));
                        startActivity(it);

                    } else if (type.equals("2")) {
                        Intent it = new Intent(ArticleDetailActivity.this, AlbumDetailActivity.class);
                        it.putExtra("albumId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("3")) {
                        Intent it = new Intent(ArticleDetailActivity.this, ArticleDetailActivity02.class);
                        it.putExtra("articleId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("4")) {
                        Intent it = new Intent(ArticleDetailActivity.this, VideoDetailActivity.class);
                        it.putExtra("videoId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("5")) {
                        Intent it = new Intent(ArticleDetailActivity.this, SpecialDetailActivity.class);
                        it.putExtra("specialId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("7")) {
                        Intent it = new Intent(ArticleDetailActivity.this, GalleriaActivity.class);
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
                        Intent it=new Intent(ArticleDetailActivity.this,RadioDetailActivity.class);
                        //it.putExtra("radioId",radioGroups.get(groupPosition).getList().get(childPosition));
                        it.putExtra("radioId",Integer.parseInt(id));
                        startActivity(it);

                    }else if (type.equals("2")) {
                        //主播
                        Intent it = new Intent(ArticleDetailActivity.this, WebViewActivity.class);
                        it.putExtra("url", ServerInfo.h5HttpsIP+"/anchors/"+id);
                        startActivity(it);

                    } else if (type.equals("3")) {
                        //机构
                        Intent it = new Intent(ArticleDetailActivity.this, WebViewActivity.class);
                        it.putExtra("url", ServerInfo.h5HttpsIP+"/orgs/"+id);
                        startActivity(it);
                    }else if (type.equals("4")) {
                        //电视台
                        Intent it=new Intent(ArticleDetailActivity.this,TvDetailActivity.class);
                        it.putExtra("radioId",Integer.parseInt(id));
                        startActivity(it);

                    }
                }
            });

        }


        @JavascriptInterface
        public void playArticle(String playOrPause,String id) {
            if(playOrPause.equals("1")){

                if(firstTime==true){
                    firstTime=false;
                    if(mArticleVoicesInfo!=null&&mArticleVoicesInfo.getVoices().size()>0){
                        ArticleVoicesInfo.VoicesBean vb=mArticleVoicesInfo.getVoices().get(0);
                        List<AudioInfo> audioInfos = new ArrayList<>();
                        for (int i = 0; vb != null&&vb.getAudio()!=null&& i < vb.getAudio().size(); i++) {
                            ArticleVoicesInfo.VoicesBean.AudioBean audio = vb.getAudio().get(i);
                            AudioInfo audioInfo = new AudioInfo();
                            audioInfo.setId(audio.getId());
                            audioInfo.setIndex(i + 1);
                            audioInfo.setIsRadio(2);
                            audioInfo.setArticleId(mArticleId);
                            audioInfo.setVideo_id(audio.getVideo_id());
                            audioInfos.add(audioInfo);
                        }
                        try{
                            AudioPlayerService.sPlayOrder=PLAY_ORDER;
                            mAudioPlayer.playAudio(audioInfos.get(0));
                            mAudioPlayer.setPlayerList(audioInfos);
                        }catch (RemoteException e){
                        }
                        if (!FloatWindowUtil.getInstance().checkFloatWindowPermission()) {
                            if (MyApplication.getInstance().remindPermission) {
                                MyApplication.getInstance().remindPermission = false;
                                showFloatWindowPermission();
                            }
                        }
                    }
                }else{
                    try{
                        mAudioPlayer.start();
                    }catch (RemoteException e){

                    }
                }

            }else{
                try{
                    mAudioPlayer.pause();
                }catch (RemoteException e){

                }

            }









//            if (!FloatWindowUtil.getInstance().checkFloatWindowPermission()) {
//                if (MyApplication.getInstance().remindPermission) {
//                    MyApplication.getInstance().remindPermission = false;
//                    showFloatWindowPermission();
//
//                }
//            }
//            FloatWindowUtil.getInstance().hideWindow();



        }

        @JavascriptInterface
        public void bonesEvent(String str) {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    mViewSkeletonScreen.hide();
//                }
//            });

        }


    }

    @Override
    protected void onResume() {
        FloatWindowUtil.getInstance().hideWindow();
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            if(mAudioPlayer!=null){
                if (mAudioPlayer.getPlayerState() == IPlayer.paused ||
                        mAudioPlayer.getPlayerState() == IPlayer.started) {

                    if (FloatWindowUtil.getInstance().checkFloatWindowPermission()) {
                        FloatWindowUtil.getInstance().showFloatWindow();
                    } else {
                        //showFloatWindowPermission();
                    }
                }
            }else{
                Log.e("ArticleDetailActivity","mAudioPlayer==null");
            }
        } catch (Exception e) {

        }
        super.onPause();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LOGIN_RESULT ) {
            String url = ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId;
            int size = 1;
            int netLoad = SharedPreferencesUtils.getIntValue(SettingActivity.NET_LOAD, 0);
            if (netLoad == 0 || CommonUtils.getNetWorkType(this) == NETWORKTYPE_WIFI) {
                size = 2;
            }
            if (User.getInstance() == null) {
                url = url + "?app=android&size=" + size+"&multiple="+CommonUtils.getFontSize();
            } else {
                if(!TextUtils.isEmpty(mJumpUrl)){
                    url = mJumpUrl + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&size=" + size+"&multiple="+CommonUtils.getFontSize();
                }else{
                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&size=" + size+"&multiple="+CommonUtils.getFontSize();
                }
                webView.loadUrl(url);

                //url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&size=" + size;
            }


        }else if (requestCode == REQUEST_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (FloatWindowUtil.getInstance().checkFloatWindowPermission()) {
                    //FloatWindowUtil.getInstance().showFloatWindow();
                } else {
                    //不显示悬浮窗 并提示
                }


            }
        }
    }

    private void showShare(final String title, final String desc, final String logo, final String url) {
        final String cover;
        if (logo.startsWith("http://")) {
            cover = logo.replace("http://", "https://");
        } else {
            cover = logo;
        }
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
            public void onShare(Platform platform, ShareParams paramsToShare) {
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

                shareStatistics(chanel, "" + mArticle.getId(), ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId + "?app=android");
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
//        if(runnable!=null){
//            mHandler.removeCallbacks(runnable);
//        }
        super.onDestroy();
    }


    private void showFloatWindowPermission() {
        FloatWindowUtil.getInstance().addOnPermissionListener(new FloatWindowUtil.OnPermissionListener() {
            @Override
            public void showPermissionDialog() {
                FRDialog dialog = new FRDialog.MDBuilder(ArticleDetailActivity.this)
                        .setTitle("是否显示悬浮播放器")
                        .setMessage("要显示悬浮播放器，需要开启悬浮窗权限")
                        .setPositiveContentAndListener("现在去开启", new FRDialogClickListener() {
                            @Override
                            public boolean onDialogClick(View view) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivityForResult(intent, REQUEST_PERMISSION_CODE);
                                }
                                return true;
                            }
                        }).setNegativeContentAndListener("暂不开启", new FRDialogClickListener() {
                            @Override
                            public boolean onDialogClick(View view) {
                                return true;
                            }
                        }).create();
                dialog.show();
            }
        });
        FloatWindowUtil.getInstance().setPermission();
    }
}
