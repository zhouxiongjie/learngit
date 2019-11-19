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
                        Advert advert = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), Advert.class);

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
                            }

                            mCountDownTimer = new CountDownTimer(advert.getLength() * 1000, 500) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    timer.setText("跳过" + millisUntilFinished / 1000 + "s");

                                }

                                @Override
                                public void onFinish() {
                                    gotoHome();
                                }
                            };
                            mCountDownTimer.start();
                        }

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
                if(mCountDownTimer!=null){
                    mCountDownTimer.cancel();
                    gotoHome();
                }
                break;
        }
    }
}
