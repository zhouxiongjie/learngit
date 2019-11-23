package com.shuangling.software.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
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
import com.alivc.player.AliyunErrorCode;
import com.alivc.player.VcPlayerLog;
import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.AliyunVodPlayer;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.aliyun.vodplayerview.constants.PlayParameter;
import com.aliyun.vodplayerview.utils.NetWatchdog;
import com.aliyun.vodplayerview.view.control.ControlView;
import com.aliyun.vodplayerview.view.interfaces.ViewAction;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.facebook.drawee.view.SimpleDraweeView;
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
    private AliyunVodPlayer mAliyunVodPlayer;
    private boolean mSkip=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_startup);
        ButterKnife.bind(this);

        if (!isTaskRoot()) {
            finish();
            return;
        }

        init();


    }


    private void init() {
        mHandler = new Handler(this);
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                gotoHome();
//            }
//        }, 4000);

        getAdvert();
        verifyUserInfo();
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


    private void gotoHome() {

        startActivity(new Intent(this, MainActivity.class));
        finish();
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
                                            mSkip=true;
                                            Intent it=new Intent(StartupActivity.this, AdvertActivity.class);
                                            it.putExtra("url", advert.getUrl());
                                            it.putExtra("id",advert.getId());
                                            startActivity(it);
                                            finish();
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
                                            mSkip=true;
                                            Intent it=new Intent(StartupActivity.this, AdvertActivity.class);
                                            it.putExtra("url", advert.getUrl());
                                            it.putExtra("id",advert.getId());
                                            startActivity(it);
                                            finish();
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
                    mAliyunVodPlayer.surfaceChanged();
                }

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
        mAliyunVodPlayer = new AliyunVodPlayer(this);
        //设置准备回调
        mAliyunVodPlayer.setOnPreparedListener(new IAliyunVodPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {


            }
        });
        //播放器出错监听
        mAliyunVodPlayer.setOnErrorListener(new IAliyunVodPlayer.OnErrorListener() {
            @Override
            public void onError(int errorCode, int errorEvent, String errorMsg) {

            }
        });
        //请求源过期信息
        mAliyunVodPlayer.setOnTimeExpiredErrorListener(new IAliyunVodPlayer.OnTimeExpiredErrorListener() {
            @Override
            public void onTimeExpiredError() {

            }
        });
        //播放器加载回调
        mAliyunVodPlayer.setOnLoadingListener(new IAliyunVodPlayer.OnLoadingListener() {
            @Override
            public void onLoadStart() {

            }

            @Override
            public void onLoadEnd() {

            }

            @Override
            public void onLoadProgress(int percent) {

            }
        });
        //播放结束
        mAliyunVodPlayer.setOnCompletionListener(new IAliyunVodPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {

            }
        });
        mAliyunVodPlayer.setOnBufferingUpdateListener(new IAliyunVodPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(int percent) {

            }
        });
        //播放信息监听
        mAliyunVodPlayer.setOnInfoListener(new IAliyunVodPlayer.OnInfoListener() {
            @Override
            public void onInfo(int arg0, int arg1) {

            }
        });
        //切换清晰度结果事件
        mAliyunVodPlayer.setOnChangeQualityListener(new IAliyunVodPlayer.OnChangeQualityListener() {
            @Override
            public void onChangeQualitySuccess(String finalQuality) {
                //切换成功后就开始播放

            }

            @Override
            public void onChangeQualityFail(int code, String msg) {
                //失败的话，停止播放，通知上层

            }
        });
        //重播监听
        mAliyunVodPlayer.setOnRePlayListener(new IAliyunVodPlayer.OnRePlayListener() {
            @Override
            public void onReplaySuccess() {
                //重播、重试成功

            }
        });
        //自动播放
        mAliyunVodPlayer.setOnAutoPlayListener(new IAliyunVodPlayer.OnAutoPlayListener() {
            @Override
            public void onAutoPlayStarted() {
                //自动播放开始,需要设置播放状态

            }
        });
        //seek结束事件
        mAliyunVodPlayer.setOnSeekCompleteListener(new IAliyunVodPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete() {

            }
        });
        //PCM原始数据监听
        mAliyunVodPlayer.setOnPcmDataListener(new IAliyunVodPlayer.OnPcmDataListener() {
            @Override
            public void onPcmData(byte[] data, int size) {

            }
        });
        //第一帧显示
        mAliyunVodPlayer.setOnFirstFrameStartListener(new IAliyunVodPlayer.OnFirstFrameStartListener() {
            @Override
            public void onFirstFrameStart() {

            }
        });
        mAliyunVodPlayer.setOnUrlTimeExpiredListener(new IAliyunVodPlayer.OnUrlTimeExpiredListener() {
            @Override
            public void onUrlTimeExpired(String vid, String quality) {

            }
        });
        mAliyunVodPlayer.setDisplay(surface.getHolder());
        mAliyunVodPlayer.setAutoPlay(true);

        AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
        alsb.setSource(url);
        Uri uri = Uri.parse(url);
        if ("rtmp".equals(uri.getScheme())) {
            alsb.setTitle("");
        }
        AliyunLocalSource localSource = alsb.build();
        mAliyunVodPlayer.prepareAsync(localSource);

    }


    @Override
    protected void onStop() {
        super.onStop();

        if (mAliyunVodPlayer != null) {
            IAliyunVodPlayer.PlayerState playerState = mAliyunVodPlayer.getPlayerState();
            if (playerState == IAliyunVodPlayer.PlayerState.Started || mAliyunVodPlayer.isPlaying()) {
                mAliyunVodPlayer.pause();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mAliyunVodPlayer != null) {
            IAliyunVodPlayer.PlayerState playerState = mAliyunVodPlayer.getPlayerState();
            if (playerState == IAliyunVodPlayer.PlayerState.Paused || playerState == IAliyunVodPlayer.PlayerState.Prepared || mAliyunVodPlayer.isPlaying()) {
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
