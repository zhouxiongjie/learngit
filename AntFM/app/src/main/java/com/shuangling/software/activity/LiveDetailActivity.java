package com.shuangling.software.activity;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.player.IPlayer;
import com.aliyun.player.source.VidAuth;
import com.aliyun.vodplayerview.utils.ScreenUtils;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.VideoRecyclerAdapter;
import com.shuangling.software.dialog.ShareDialog;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.Comment;
import com.shuangling.software.entity.ResAuthInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.entity.VideoDetail;
import com.shuangling.software.network.MyEcho;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.service.IAudioPlayer;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.FloatWindowUtil;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.QNAppServer;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;
import com.shuangling.software.utils.TimeUtil;

import net.mrbin99.laravelechoandroid.EchoCallback;
import net.mrbin99.laravelechoandroid.EchoOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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


public class LiveDetailActivity extends BaseAudioActivity implements Handler.Callback {


    public static final int MSG_ATTENTION_CALLBACK = 0x07;

    public static final int REQUEST_LOGIN = 0x09;

    private static final int MSG_GET_VIDEO_AUTH = 0xd;

    @BindView(R.id.aliyunVodPlayerView)
    AliyunVodPlayerView aliyunVodPlayerView;
    @BindView(R.id.attention)
    TextView attention;
    @BindView(R.id.tabPageIndicator)
    TabLayout tabPageIndicator;


    private MyEcho echo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(MyApplication.getInstance().getCurrentTheme());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_detail);
        //CommonUtils.setTransparentStatusBar(this);
        //ImmersionBar.with(this).transparentBar().titleBar(activityTitle).init();
        ImmersionBar.with(this).statusBarDarkFont(true).fitsSystemWindows(true).init();
        //ImmersionBar.with(this).statusBarDarkFont(true);
        ButterKnife.bind(this);
        init();


    }

    private void init() {

        initAliyunPlayerView();


        getMenus("359");

        //getRoomToken("359");

        joinChannel("359");
    }

    private void getMenus(String roomId) {
        String url = ServerInfo.live + "/v2/get_room_menus_c";
        Map<String, String> params = new HashMap<>();
        params.put("room_id",roomId);

        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try{

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {


                    }

                }catch (Exception e){

                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test",exception.toString());

            }
        });

    }


    private void initAliyunPlayerView() {
        //保持屏幕敞亮

        aliyunVodPlayerView.setKeepScreenOn(true);
        String sdDir = CommonUtils.getStoragePrivateDirectory(Environment.DIRECTORY_MOVIES);
        aliyunVodPlayerView.setPlayingCache(false, sdDir, 60 * 60 /*时长, s */, 300 /*大小，MB*/);
        aliyunVodPlayerView.setTheme(AliyunVodPlayerView.Theme.Blue);
        //aliyunVodPlayerView.setCirclePlay(true);
        aliyunVodPlayerView.setAutoPlay(true);
//        aliyunVodPlayerView.setReferer(ServerInfo.h5IP);
        aliyunVodPlayerView.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                //准备完成触发


            }
        });

        aliyunVodPlayerView.setOnModifyFlowTipStatus(new AliyunVodPlayerView.OnModifyFlowTipStatus() {
            @Override
            public void onChangeFlowTipStatus() {
                SharedPreferencesUtils.putIntValue(SettingActivity.NEED_TIP_PLAY, 0);
            }
        });


    }


    private void joinChannel(String roomId){
        EchoOptions options = new EchoOptions();
        options.host = "http://echo-live.review.slradio.cn";
        options.headers.put("Authorization", User.getInstance().getAuthorization());
        echo = new MyEcho(options);
        echo.connect(new EchoCallback() {
            @Override
            public void call(Object... args) {
                Log.i("test", "Success connect");
            }
        }, new EchoCallback() {
            @Override
            public void call(Object... args) {
                // Error connect
                Log.i("test", "Error connect");
            }
        });


        echo.privateChannel("room.chat." +roomId)
                .listen("VideoChatEvent_"+"user_"+User.getInstance().getId(), new EchoCallback() {
                    @Override
                    public void call(Object... args) {
                        // Event thrown.
                        Log.i("test", "args");

                        ToastUtils.show(args.toString());

                    }
                });
    }




    private void getRoomToken(String roomId) {
        String url = "http://api-live.review.slradio.cn" + "/v4/get_room_token_c";
        Map<String, String> params = new HashMap<>();
        params.put("userId","user_"+User.getInstance().getId());
        params.put("roomId",roomId);
        params.put("permission","user");
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try{

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        String roomToken = jsonObject.getString("data");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (TextUtils.isEmpty(roomToken)) {
                                    return;
                                }
                                Intent intent = new Intent(LiveDetailActivity.this, RoomActivity.class);
                                intent.putExtra(RoomActivity.EXTRA_ROOM_ID, roomId);
                                intent.putExtra(RoomActivity.EXTRA_ROOM_TOKEN, roomToken);
                                intent.putExtra(RoomActivity.EXTRA_USER_ID, "user_"+User.getInstance().getId());
                                startActivity(intent);
                            }
                        });

                    }

                }catch (Exception e){

                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test",exception.toString());

            }
        });
    }












    private void updatePlayerViewMode() {
        if (aliyunVodPlayerView != null) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                //转为竖屏了。
                //显示状态栏
                //                if (!isStrangePhone()) {
                //                    getSupportActionBar().show();
                //                }

                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                aliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                //设置view的布局，宽高之类
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) aliyunVodPlayerView
                        .getLayoutParams();
                layoutParams.height = (int) (ScreenUtils.getWidth(this) * 9.0f / 16);
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                ViewGroup.LayoutParams lp = aliyunVodPlayerView.getLayoutParams();
                aliyunVodPlayerView.setLayoutParams(lp);


                aliyunVodPlayerView.setBackBtnVisiable(View.INVISIBLE);
                ImmersionBar.with(this).statusBarDarkFont(true).fitsSystemWindows(true).init();
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //转到横屏了。
                //隐藏状态栏
                if (!isStrangePhone()) {
                    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    aliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }

                //设置view的布局，宽高
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) aliyunVodPlayerView
                        .getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                aliyunVodPlayerView.setLayoutParams(layoutParams);

                aliyunVodPlayerView.setBackBtnVisiable(View.VISIBLE);
                ImmersionBar.with(this).statusBarDarkFont(true).fitsSystemWindows(false).init();
            }

        }
    }


    private boolean isStrangePhone() {
        boolean strangePhone = "mx5".equalsIgnoreCase(Build.DEVICE)
                || "Redmi Note2".equalsIgnoreCase(Build.DEVICE)
                || "Z00A_1".equalsIgnoreCase(Build.DEVICE)
                || "hwH60-L02".equalsIgnoreCase(Build.DEVICE)
                || "hermes".equalsIgnoreCase(Build.DEVICE)
                || ("V4".equalsIgnoreCase(Build.DEVICE) && "Meitu".equalsIgnoreCase(Build.MANUFACTURER))
                || ("m1metal".equalsIgnoreCase(Build.DEVICE) && "Meizu".equalsIgnoreCase(Build.MANUFACTURER));

        return strangePhone;
    }


    @Override
    protected void onResume() {
        super.onResume();
        updatePlayerViewMode();
        if (aliyunVodPlayerView != null) {
            aliyunVodPlayerView.setAutoPlay(true);
            aliyunVodPlayerView.onResume();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (aliyunVodPlayerView != null) {
            aliyunVodPlayerView.setAutoPlay(false);
            aliyunVodPlayerView.onStop();
        }

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (echo != null) {
            echo.disconnect();
        }

        if (aliyunVodPlayerView != null) {
            aliyunVodPlayerView.onDestroy();
            aliyunVodPlayerView = null;
        }
    }

    @Override
    public void onBackPressed() {

        if (aliyunVodPlayerView != null) {
            aliyunVodPlayerView.onDestroy();
            aliyunVodPlayerView = null;
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updatePlayerViewMode();
    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {

            case MSG_ATTENTION_CALLBACK:

                break;


        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_LOGIN && resultCode == RESULT_OK) {
        }

    }

    @OnClick({R.id.attention})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.attention:
                break;


        }
    }


}
