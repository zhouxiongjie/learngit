package com.shuangling.software.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.UrlSource;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.entity.Advert;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


public class StartupActivity extends Activity implements Handler.Callback {

    public static final int MSG_GET_ADVERT = 0x00;
    @BindView(R.id.background)
    ImageView background;
    @BindView(R.id.logo)
    SimpleDraweeView logo;
    @BindView(R.id.surface)
    SurfaceView surface;
    @BindView(R.id.timer)
    TextView timer;

    private Handler mHandler;
    private CountDownTimer mCountDownTimer;
    //播放器
    private AliPlayer mAliyunVodPlayer;
    private boolean mSkip=false;

    private int mPlayerState = IPlayer.idle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏顶部状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_startup);
        ImmersionBar.with(this).transparentStatusBar().init();
        ButterKnife.bind(this);

        if (!isTaskRoot()) {
            finish();
            return;
        }

        init();


    }


    private void init() {
        mHandler = new Handler(this);

        getAdvert();
        verifyUserInfo();
        //缓存文章详情资源
//        WebView webView=new WebView(this);
//        webView.setWebViewClient(new WebViewClient() {
//            // url拦截
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
//                view.loadUrl(url);
//                // 相应完成返回true
//                return true;
//
//            }
//
//            // 页面开始加载
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                //progressBar.setVisibility(View.VISIBLE);
//                super.onPageStarted(view, url, favicon);
//            }
//
//            // 页面加载完成
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                //progressBar.setVisibility(View.GONE);
//                super.onPageFinished(view, url);
//            }
//
//            // WebView加载的所有资源url
//            @Override
//            public void onLoadResource(WebView view, String url) {
//                super.onLoadResource(view, url);
//            }
//
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
////				view.loadData(errorHtml, "text/html; charset=UTF-8", null);
//                super.onReceivedError(view, errorCode, description, failingUrl);
//            }
//
//        });
//
//
//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            // 处理javascript中的alert
//            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
//                return super.onJsAlert(view, url, message, result);
//            }
//
//            ;
//
//            @Override
//            // 处理javascript中的confirm
//            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
//                return super.onJsConfirm(view, url, message, result);
//            }
//
//            ;
//
//            @Override
//            // 处理javascript中的prompt
//            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
//                return super.onJsPrompt(view, url, message, defaultValue, result);
//            }
//
//            ;
//
//            // 设置网页加载的进度条
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                super.onProgressChanged(view, newProgress);
//            }
//
//            // 设置程序的Title
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//                super.onReceivedTitle(view, title);
//            }
//        });
//        webView.loadUrl(ServerInfo.h5IP + ServerInfo.getArticlePage + 0);
    }

    public void getAdvert() {

        String url = ServerInfo.serviceIP + ServerInfo.startAdvert;
        Map<String, String> params = new HashMap<String, String>();

        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                Message msg = Message.obtain();
                msg.what = MSG_GET_ADVERT;
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        timer.setVisibility(View.GONE);
                    }
                });

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gotoHome();
                    }
                }, 4000);

            }
        });


    }


    public void updateStatistics() {

        String url = ServerInfo.serviceIP + ServerInfo.statistics;

        Map<String, String> params = new HashMap<>();


        OkHttpUtils.get(url, null, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                String result = response;
                final JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });

    }


    private void gotoHome() {

        startActivity(new Intent(this, MainActivity.class));
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },200);

    }

    private void verifyUserInfo() {
        User.setInstance(SharedPreferencesUtils.getUser());
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        if (SharedPreferencesUtils.getUser() != null) {
            pushService.bindAccount(SharedPreferencesUtils.getUser().getUsername(), new CommonCallback() {
                @Override
                public void onSuccess(String s) {
                    Log.i("bindAccount-onSuccess", s);
                }

                @Override
                public void onFailed(String s, String s1) {
                    Log.i("bindAccount-onFailed", s);
                    Log.i("bindAccount-onFailed", s1);
                }
            });
            updateStatistics();
        }
        EventBus.getDefault().post(new CommonEvent("OnLoginSuccess"));

    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_ADVERT:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        JSONObject jo=jsonObject.getJSONObject("data");
                        if(jo!=null){
                            final Advert advert = JSONObject.parseObject(jo.toJSONString(), Advert.class);

                            if (advert != null) {
                                if (advert.getType() == 1) {
                                    //图片
                                    surface.setVisibility(View.GONE);
                                    logo.setVisibility(View.VISIBLE);

                                    if (!TextUtils.isEmpty(advert.getContent())) {
                                        Uri uri = Uri.parse(advert.getContent());
                                        int width = CommonUtils.getScreenWidth();
                                        int height = CommonUtils.getScreenHeight() - CommonUtils.dip2px(90);
                                        ImageLoader.showThumb(uri, logo, width, height);
                                    }
                                    logo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(advert.getSkip()==1){
                                                mSkip=true;
                                                Intent it=new Intent(StartupActivity.this, AdvertActivity.class);
                                                it.putExtra("url", advert.getUrl());
                                                it.putExtra("id",advert.getId());
                                                startActivity(it);
                                                finish();
                                            }

                                        }
                                    });
                                }else{
                                    //视频
                                    surface.setVisibility(View.VISIBLE);
                                    logo.setVisibility(View.GONE);
                                    if (!TextUtils.isEmpty(advert.getContent())){
                                        initAliVcPlayer(advert.getContent());
                                    }
                                    surface.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(advert.getSkip()==1){
                                                mSkip=true;
                                                Intent it=new Intent(StartupActivity.this, AdvertActivity.class);
                                                it.putExtra("url", advert.getUrl());
                                                it.putExtra("id",advert.getId());
                                                startActivity(it);
                                                finish();
                                            }

                                        }
                                    });

                                }
                                timer.setVisibility(View.VISIBLE);
                                mCountDownTimer = new CountDownTimer(advert.getLength() * 1000, 500) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        if(millisUntilFinished / 1000==0){
                                            timer.setText("跳过1s");
                                        }else{
                                            timer.setText("跳过" + millisUntilFinished / 1000 + "s");
                                        }


                                    }

                                    @Override
                                    public void onFinish() {
                                        if(mSkip==false){
                                            gotoHome();
                                        }

                                    }
                                };
                                mCountDownTimer.start();
                            }else{
                                timer.setVisibility(View.GONE);

                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        gotoHome();
                                    }
                                }, 4000);
                            }
                        }else{
                            timer.setVisibility(View.GONE);

                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    gotoHome();
                                }
                            }, 4000);
                        }


                    }else{
                        timer.setVisibility(View.GONE);

                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                gotoHome();
                            }
                        }, 4000);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
        }
        return false;
    }

    @OnClick({R.id.logo, R.id.surface, R.id.timer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.logo:
                break;
            case R.id.surface:
                break;
            case R.id.timer:
                mSkip=true;
                if(mCountDownTimer!=null){
                    mCountDownTimer.cancel();
                    gotoHome();
                }
                break;
        }
    }


    /**
     * 初始化播放器
     */
    private void initAliVcPlayer(String url) {

        SurfaceHolder holder = surface.getHolder();
        //增加surfaceView的监听
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if(mAliyunVodPlayer!=null){
                    mAliyunVodPlayer.setDisplay(surfaceHolder);
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width,
                                       int height) {
                if(mAliyunVodPlayer!=null){
                    mAliyunVodPlayer.redraw();
                }

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
        mAliyunVodPlayer = AliPlayerFactory.createAliPlayer(getApplicationContext());
        //设置准备回调
        mAliyunVodPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {


            }
        });
        mAliyunVodPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FILL);
        //播放器出错监听
        mAliyunVodPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {

            }

        });

        //播放器加载回调
        mAliyunVodPlayer.setOnLoadingStatusListener(new IPlayer.OnLoadingStatusListener() {
            @Override
            public void onLoadingBegin() {

            }

            @Override
            public void onLoadingProgress(int i, float v) {

            }

            @Override
            public void onLoadingEnd() {

            }

        });
        //播放结束
        mAliyunVodPlayer.setOnCompletionListener(new IPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {

            }
        });
        mAliyunVodPlayer.setOnInfoListener(new IPlayer.OnInfoListener() {
            @Override
            public void onInfo(InfoBean infoBean) {

            }

        });
        //播放信息监听
        mAliyunVodPlayer.setOnInfoListener(new IPlayer.OnInfoListener() {
            @Override
            public void onInfo(InfoBean infoBean) {

            }

        });
        //切换清晰度结果事件
        mAliyunVodPlayer.setOnTrackChangedListener(new IPlayer.OnTrackChangedListener() {
            @Override
            public void onChangedSuccess(TrackInfo trackInfo) {

            }

            @Override
            public void onChangedFail(TrackInfo trackInfo, ErrorInfo errorInfo) {

            }


        });
        mAliyunVodPlayer.setOnStateChangedListener(new IPlayer.OnStateChangedListener() {
            @Override
            public void onStateChanged(int i) {
                mPlayerState = i;
            }
        });


        //seek结束事件
        mAliyunVodPlayer.setOnSeekCompleteListener(new IPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete() {

            }
        });

        //第一帧显示
        mAliyunVodPlayer.setOnRenderingStartListener(new IPlayer.OnRenderingStartListener() {
            @Override
            public void onRenderingStart() {

            }

        });

        mAliyunVodPlayer.setDisplay(surface.getHolder());
        mAliyunVodPlayer.setAutoPlay(true);

//        AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
//        alsb.setSource(url);
//        Uri uri = Uri.parse(url);
//        if ("rtmp".equals(uri.getScheme())) {
//            alsb.setTitle("");
//        }
//        AliyunLocalSource localSource = alsb.build();
//        mAliyunVodPlayer.prepareAsync(localSource);

        UrlSource urlSource = new UrlSource();
        urlSource.setUri(url);
        //urlSource.setTitle(title);

        mAliyunVodPlayer.setDataSource(urlSource);
        mAliyunVodPlayer.prepare();


    }


    @Override
    protected void onStop() {
        super.onStop();

        if (mAliyunVodPlayer != null) {

            if (mPlayerState == IPlayer.started || mPlayerState == IPlayer.prepared) {
                mAliyunVodPlayer.pause();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mAliyunVodPlayer != null) {


            if (mPlayerState == IPlayer.paused || mPlayerState == IPlayer.prepared) {
                mAliyunVodPlayer.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mAliyunVodPlayer != null) {
            mAliyunVodPlayer.stop();
            mAliyunVodPlayer.release();
            mAliyunVodPlayer = null;
        }
        super.onDestroy();
    }
}
