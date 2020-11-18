package com.shuangling.software.activity;


import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.source.UrlSource;
import com.aliyun.vodplayerview.utils.ScreenUtils;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.api.APICallBack;
import com.shuangling.software.api.APILiving;
import com.shuangling.software.customview.BannerView;
import com.shuangling.software.customview.BannerView1;
import com.shuangling.software.entity.BannerInfo;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.LiveInfo;
import com.shuangling.software.entity.LiveMenu;

import com.shuangling.software.entity.LiveRoomInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.event.MessageEvent;
import com.shuangling.software.fragment.ImgTextFragment;
import com.shuangling.software.fragment.ImgTextLiveFragment;
import com.shuangling.software.fragment.InviteRankFragment;
import com.shuangling.software.fragment.LiveChatFragment;
import com.shuangling.software.network.MyEcho;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;

import net.mrbin99.laravelechoandroid.EchoCallback;
import net.mrbin99.laravelechoandroid.EchoOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


public class LiveDetailActivity extends BaseAudioActivity implements Handler.Callback {


    public static final int MSG_ATTENTION_CALLBACK = 0x07;

    public static final int REQUEST_LOGIN = 0x09;

    private static final int MSG_GET_VIDEO_AUTH = 0xd;


    @BindView(R.id.player_container)
    FrameLayout playerContainer;


    @BindView(R.id.aliyunVodPlayerView)
    AliyunVodPlayerView aliyunVodPlayerView;
    @BindView(R.id.attention)
    TextView attention;
    @BindView(R.id.tabPageIndicator)
    TabLayout tabPageIndicator;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.adverts)
    RelativeLayout adverts;
    @BindView(R.id.root)
    LinearLayout root;

    @BindView(R.id.audit)
    TextView auditTextView;


    @BindView(R.id.status)
    TextView statusTextView;

    @BindView(R.id.cover)
    SimpleDraweeView liveCover;


    @BindView(R.id.host_header)
    SimpleDraweeView hostHeader;


    @BindView(R.id.host_leave)
    FrameLayout hostLeaveLayout;






    private MyEcho echo;
    private int mRoomId;
    private String mStreamName;
    private String mUrl;
    private boolean mVerify;

    private List<LiveMenu> mMenus;
    private FragmentAdapter mFragmentPagerAdapter;

    public LiveRoomInfo getLiveRoomInfo() {
        return mLiveRoomInfo;
    }

    private LiveRoomInfo mLiveRoomInfo;
    private boolean mHasInChannel = false;
    public int mType;

    public boolean canGrabRedPacket=false;
    public boolean hasInvite=false;
    private ViewSkeletonScreen mViewSkeletonScreen;
    private Handler mHandler = new Handler();
    private Timer mTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(MyApplication.getInstance().getCurrentTheme());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_detail);
        //CommonUtils.setTransparentStatusBar(this);
        //ImmersionBar.with(this).transparentBar().titleBar(activityTitle).init();
        //ImmersionBar.with(this).statusBarDarkFont(true).fitsSystemWindows(true).init();
        ImmersionBar.with(this).statusBarDarkFont(true).fitsSystemWindows(true).keyboardEnable(true)  //解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING).init();
        //ImmersionBar.with(this).statusBarDarkFont(true);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        init();


    }

    private void init() {

         mViewSkeletonScreen = Skeleton.bind(root)
                .load(R.layout.skeleton_video_detail)
                .shimmer(false)
                .angle(20)
                .duration(1000)
                .color(R.color.shimmer_color)
                .show();
        mVerify = getIntent().getBooleanExtra("verify",true);
        mStreamName = getIntent().getStringExtra("streamName");
        mRoomId = getIntent().getIntExtra("roomId", 0);
        mUrl = getIntent().getStringExtra("url");
        mType = getIntent().getIntExtra("type",4);

        hostLeaveLayout.setVisibility(View.GONE);

        initAliyunPlayerView();
        getLiveDetail();
        getAdvertises();


        //getDetail();

        //joinChannel();
    }



    private void getMenus() {
        String url = ServerInfo.live + "/v2/get_room_menus_c";
        Map<String, String> params = new HashMap<>();
        params.put("room_id", "" + mRoomId);

        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mMenus = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), LiveMenu.class);

                        Iterator<LiveMenu> iterator = mMenus.iterator();
                        while (iterator.hasNext()) {
                            LiveMenu liveMenu = iterator.next();
                            if(liveMenu.getShowtype()==14&&liveMenu.getUsing()==1){
                                hasInvite=true;
                            }
                            if ((liveMenu.getUsing()!=1)||
                                    (liveMenu.getShowtype() != 1&&
                                    liveMenu.getShowtype() != 2&&
                                    liveMenu.getShowtype()!=11&&
                                    liveMenu.getShowtype()!=14)) {
                                if(liveMenu.getShowtype()==6&&liveMenu.getUsing()==1){
                                    canGrabRedPacket=true;
                                }

                                iterator.remove();
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initFragment();
                            }
                        });


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test", exception.toString());

            }
        });

    }


    private void getLiveDetail(){
        auditTextView.setText("");
        APILiving.getRoomDetail(this, mStreamName, new APICallBack<LiveRoomInfo>() {
            @Override
            public void onSuccess(LiveRoomInfo liveRoomInfo) {
                auditTextView.setText(liveRoomInfo.getAudit() + "人");
                mLiveRoomInfo = liveRoomInfo;

                setLiveStatus(liveRoomInfo.getState());

                Uri uri = Uri.parse(liveRoomInfo.getCover_url());
                int width = CommonUtils.dip2px(375);
                int height = CommonUtils.dip2px(200);
                ImageLoader.showThumb(uri, liveCover, width, height);

                uri = Uri.parse(liveRoomInfo.getLogo());
                width = CommonUtils.dip2px(65);
                height = CommonUtils.dip2px(65);
                ImageLoader.showThumb(uri, hostHeader, width, height);
                EventBus.getDefault().post(new CommonEvent("liveRoomInfo"));
                getMenus();

                if(mLiveRoomInfo!=null&&mLiveRoomInfo.getEntry_mode()==2&&mVerify){
                    //口令观看
                    Intent it=new Intent(LiveDetailActivity.this,PassPhraseActivity.class);
                    it.putExtra("LiveRoomInfo",mLiveRoomInfo);
                    startActivity(it);
                    finish();
                }else{
                    getAuthKey();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            getUsersCount(liveRoomInfo.getId() + "");
                            mViewSkeletonScreen.hide();
                        }catch (Exception e) { }
                    }
                });

            }
            @Override
            public void onFail(String error) {

                mViewSkeletonScreen.hide();
            }
        });
    }

    
    private void getUsersCount(String roomId){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    APILiving.getLiveUsers(LiveDetailActivity.this, roomId, new APICallBack<String>() {
                        @Override
                        public void onSuccess(String count) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        auditTextView.setText(count + "人");
                                    }catch (Exception e) { }
                                }
                            });
                        }
                        @Override
                        public void onFail(String error) {
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mTimer.schedule(task,0,5000);
    }



    private void setLiveStatus(int status){
        switch (status) {
            case 1:
                statusTextView.setText("未开始");
                liveCover.setVisibility(View.VISIBLE);
                aliyunVodPlayerView.pause();
                aliyunVodPlayerView.setVisibility(View.GONE);
                break;
            case 2:
                statusTextView.setText("直播");
                liveCover.setVisibility(View.GONE);
                break;
            case 3:
                statusTextView.setText("已结束");
                liveCover.setVisibility(View.VISIBLE);
                aliyunVodPlayerView.pause();
                aliyunVodPlayerView.setVisibility(View.GONE);
                break;
        }
    }

    private void getAuthKey() {
        String url = ServerInfo.live + ServerInfo.getRtsAuthKey;
        Map<String, String> params = new HashMap<>();
        params.put("room_id", "" + mRoomId);
        params.put("type", "play");
        //params.put("suffix",".m3u8");
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        final String key = jsonObject.getString("data");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //setPlaySource(mUrl + "?" + key);
                                    setPlaySource(mLiveRoomInfo.getRts_pull_url() + "?" + key);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test", exception.toString());

            }
        });

    }


    private void getAdvertises() {
        String url = ServerInfo.live + ServerInfo.getAdvertises;
        Map<String, String> params = new HashMap<>();
        params.put("room_id", "" + mRoomId);

        //params.put("suffix",".m3u8");
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        JSONArray ja=jsonObject.getJSONArray("data");
                        List<BannerView.Banner> banners = new ArrayList<>();
                        for(int i=0;ja!=null&&i<ja.size();i++){

                            BannerInfo banner = new BannerInfo();
                            banner.setLogo(ja.getJSONObject(i).getString("pic_url"));
                            banner.setUrl(ja.getJSONObject(i).getString("url"));
                            banners.add(banner);

                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                BannerView1 bannerView = new BannerView1(getContext());
                                int width = CommonUtils.getScreenWidth();
                                int height = (int)(width / 5.36);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
//                                params.leftMargin = CommonUtils.dip2px(20);
//                                params.rightMargin = CommonUtils.dip2px(20);
//                                params.bottomMargin = CommonUtils.dip2px(10);
//                                params.topMargin = CommonUtils.dip2px(10);
                                adverts.addView(bannerView, params);

                                bannerView.setData(banners);
                                bannerView.setOnItemClickListener(new BannerView1.OnItemClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        BannerInfo banner = (BannerInfo) view.getTag();
                                        String url = banner.getUrl();
                                        String title = banner.getTitle();
                                        jumpTo(url, title);


                                    }
                                });
                            }
                        });

//                        BannerView bannerView = new BannerView(getContext());
//                        int width = CommonUtils.getScreenWidth() - CommonUtils.dip2px(40);
//                        int height = 10 * width / 23;
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
//                        params.leftMargin = CommonUtils.dip2px(20);
//                        params.rightMargin = CommonUtils.dip2px(20);
//                        params.bottomMargin = CommonUtils.dip2px(10);
//                        params.topMargin = CommonUtils.dip2px(10);
//                        mDecorateLayout.addView(bannerView, params);
//                        List<BannerView.Banner> banners = new ArrayList<>();
//                        for (int j = 0; module.getContents() != null && j < module.getContents().size(); j++) {
//                            BannerInfo banner = new BannerInfo();
//                            banner.setLogo(module.getContents().get(j).getCover());
//                            banner.setUrl(module.getContents().get(j).getSource_url());
//                            banners.add(banner);
//
//                        }
//                        bannerView.setData(banners);
//                        bannerView.setOnItemClickListener(new BannerView.OnItemClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                BannerInfo banner = (BannerInfo) view.getTag();
//
//                                String url = banner.getUrl();
//                                String title = banner.getTitle();
//                                jumpTo(url, title);
//
//
//                            }
//                        });


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test", exception.toString());

            }
        });

    }

    private void initFragment() {

        try {
            if (mMenus != null && mMenus.size() > 0) {

                mFragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), mMenus);
                viewPager.setAdapter(mFragmentPagerAdapter);
                tabPageIndicator.setupWithViewPager(viewPager);
                viewPager.setOffscreenPageLimit(20);

                if (mMenus.size() > 5) {
                    tabPageIndicator.setTabMode(TabLayout.MODE_SCROLLABLE);
                } else {
                    tabPageIndicator.setTabMode(TabLayout.MODE_FIXED);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void initAliyunPlayerView() {
        //保持屏幕敞亮

        aliyunVodPlayerView.setKeepScreenOn(true);
        String sdDir = CommonUtils.getStoragePrivateDirectory(Environment.DIRECTORY_MOVIES);
        aliyunVodPlayerView.setPlayingCache(false, sdDir, 60 * 60 /*时长, s */, 300 /*大小，MB*/);
        aliyunVodPlayerView.setTheme(AliyunVodPlayerView.Theme.Blue);
        //aliyunVodPlayerView.setCirclePlay(true);
        //aliyunVodPlayerView.setAutoPlay(true);
//        aliyunVodPlayerView.setReferer(ServerInfo.h5IP);
        aliyunVodPlayerView.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                //准备完成触发


            }
        });

        aliyunVodPlayerView.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                aliyunVodPlayerView.pause();
                aliyunVodPlayerView.setVisibility(View.GONE);
                liveCover.setVisibility(View.VISIBLE);
                hostLeaveLayout.setVisibility(View.VISIBLE);
            }
        });

        aliyunVodPlayerView.setOnModifyFlowTipStatus(new AliyunVodPlayerView.OnModifyFlowTipStatus() {
            @Override
            public void onChangeFlowTipStatus() {
                SharedPreferencesUtils.putIntValue(SettingActivity.NEED_TIP_PLAY, 0);
            }
        });


    }


    private void setPlaySource(String url) {

        UrlSource urlSource = new UrlSource();
        urlSource.setUri(url);
        aliyunVodPlayerView.setLocalSource(urlSource);

    }


    public void joinChannel() {

        if (mHasInChannel) {
            return;
        }
        mHasInChannel = true;
        EchoOptions options = new EchoOptions();
        options.host = ServerInfo.echo_server;
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


        echo.channel("room.chat.common." + mRoomId)
                .listen("InteractCommonEvent", new EchoCallback() {
                    @Override
                    public void call(Object... args) {
                        // Event thrown.
                        Log.i("test", "args");

                        try {
//                            ToastUtils.show(args[1].toString());
                            Log.i("message", args[1].toString());
                            JSONObject jo = JSONObject.parseObject(args[1].toString());
                            if (jo.getInteger("roomID") == mRoomId) {
                                jo = jo.getJSONObject("msg");
                                if (jo.getString("type").equals("3")) {
                                    //同意或者拒绝连麦申请
                                    if (jo.getJSONObject("data") != null && jo.getJSONObject("data").getString("allowed").equals("1") && jo.getJSONObject("data").getString("id").equals("" + User.getInstance().getId())) {
                                        //同意连麦
                                        getRoomToken("" + mRoomId);

                                    } else if (jo.getJSONObject("data") != null && jo.getJSONObject("data").getString("allowed").equals("0") && jo.getJSONObject("data").getString("id").equals("" + User.getInstance().getId())) {
                                        new CircleDialog.Builder()
                                                .setCanceledOnTouchOutside(false)
                                                .setCancelable(false)
                                                .setText("您的连麦请求被拒绝")
                                                .setPositive("确定", null)
                                                .show(getSupportFragmentManager());

                                    }
                                } else {
                                    EventBus.getDefault().post(new MessageEvent("message", jo.toString()));
                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });
    }


    //取消连麦
    public void cancelInteract(String roomId) {
        String url = ServerInfo.live + "/v4/user_cancel_apply_interact";
        Map<String, String> params = new HashMap<>();
        params.put("userId", "" + User.getInstance().getId());
        params.put("roomId", roomId);
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {


                        //getRoomToken(""+mRoomId);


//                        String roomToken = jsonObject.getString("data");
//
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (TextUtils.isEmpty(roomToken)) {
//                                    return;
//                                }
//                                Intent intent = new Intent(getContext(), RoomActivity.class);
//                                intent.putExtra(RoomActivity.EXTRA_ROOM_ID, roomId);
//                                intent.putExtra(RoomActivity.EXTRA_ROOM_TOKEN, roomToken);
//                                intent.putExtra(RoomActivity.EXTRA_USER_ID, "user_"+User.getInstance().getId());
//                                startActivity(intent);
//                            }
//                        });

                    } else if (jsonObject != null) {
                        ToastUtils.show(jsonObject.getString("msg"));
                    } else {

                        ToastUtils.show("取消连麦失败");
                    }

                } catch (Exception e) {

                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test", exception.toString());

            }
        });
    }


    public void leaveChannel() {
        if (echo != null && mHasInChannel) {
            echo.leave("room.chat.common." + mRoomId);
            mHasInChannel = false;
        }


    }


    private void getRoomToken(final String roomId) {
        String url = ServerInfo.live + "/v4/get_room_token_c";
        Map<String, String> params = new HashMap<>();
        params.put("userId", "user_" + User.getInstance().getId());
        params.put("roomId", roomId);
        params.put("permission", "user");
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        final String roomToken = jsonObject.getString("data");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (TextUtils.isEmpty(roomToken)) {
                                    return;
                                }
                                Intent intent = new Intent(LiveDetailActivity.this, RoomActivity.class);
                                intent.putExtra(RoomActivity.EXTRA_ROOM_ID, roomId);
                                intent.putExtra(RoomActivity.EXTRA_ROOM_TOKEN, roomToken);
                                intent.putExtra(RoomActivity.EXTRA_USER_ID, "user_" + User.getInstance().getId());
                                intent.putExtra("streamName", mStreamName);
                                startActivity(intent);
                            }
                        });

                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test", exception.toString());

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
                LinearLayout.LayoutParams layoutParams = (
                        LinearLayout.LayoutParams) playerContainer
                        .getLayoutParams();
                layoutParams.height = (int) (ScreenUtils.getWidth(this) * 9.0f / 16);
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                //ViewGroup.LayoutParams lp = playerContainer.getLayoutParams();
                playerContainer.setLayoutParams(layoutParams);
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
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) playerContainer
                        .getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                playerContainer.setLayoutParams(layoutParams);

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

        mTimer.cancel();
        mHandler = null;

        if (echo != null) {
            echo.disconnect();
        }

        if (aliyunVodPlayerView != null) {
            aliyunVodPlayerView.onDestroy();
            aliyunVodPlayerView = null;
        }
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(MessageEvent event) {
        if (event.getEventName().equals("kickedOut")) {
            new CircleDialog.Builder()
                    .setText("您已经被主持人移除连麦")
                    .setPositive("我知道了", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    })

                    .show(getSupportFragmentManager());
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("modifyMenu")) {
            getMenus();
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
        super.onActivityResult(requestCode, resultCode, data);


    }

    @OnClick({R.id.attention})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.attention:


                break;


        }
    }


    public class FragmentAdapter extends FragmentStatePagerAdapter {

        private FragmentManager fm;
        private List<LiveMenu> mMenus;

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public FragmentAdapter(FragmentManager fm, List<LiveMenu> menus) {
            super(fm);
            this.fm = fm;
            mMenus = menus;

        }

        @Override
        public int getCount() {
            return mMenus.size();
        }

        @Override
        public Fragment getItem(int position) {

            if (mMenus.get(position).getShowtype() == 1) {
                //聊天
                LiveChatFragment liveChatFragment = new LiveChatFragment();
                Bundle bundle = new Bundle();
                bundle.putString("streamName", mStreamName);
                bundle.putInt("roomId", mRoomId);
                bundle.putSerializable("LiveRoomInfo",mLiveRoomInfo);
                liveChatFragment.setArguments(bundle);
                return liveChatFragment;

            }else if(mMenus.get(position).getShowtype() == 2){
                //图文
                ImgTextFragment imgTextFragment = new ImgTextFragment();
                Bundle bundle = new Bundle();
                bundle.putString("streamName", mStreamName);
                bundle.putInt("roomId", mRoomId);
                imgTextFragment.setArguments(bundle);
                return imgTextFragment;
            }else if(mMenus.get(position).getShowtype() == 14){
                //邀请榜
                InviteRankFragment inviteRankFragment = new InviteRankFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("LiveRoomInfo",mLiveRoomInfo);
                inviteRankFragment.setArguments(bundle);
                return inviteRankFragment;
            }else{
                ImgTextLiveFragment imgTextLiveFragment = new ImgTextLiveFragment();
                Bundle bundle = new Bundle();
                bundle.putString("streamName", mStreamName);
                bundle.putInt("roomId", mRoomId);
                imgTextLiveFragment.setArguments(bundle);
                return imgTextLiveFragment;
            }



        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mMenus.get(position).getMenu_name();
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            //得到缓存的fragment
            Fragment fragment = (Fragment) super.instantiateItem(container, position);

            return fragment;
        }

    }


    public void jumpTo(String url,String title){
        if(url.startsWith(ServerInfo.h5IP+"/tv")||url.startsWith(ServerInfo.h5HttpsIP+"/tv")){
            Intent it=new Intent(this,RadioListActivity.class);
            it.putExtra("type","2");
            startActivity(it);
        }else if(url.startsWith(ServerInfo.h5IP+"/lives/")||url.startsWith(ServerInfo.h5HttpsIP+"/lives/")){
            String radioId=CommonUtils.getQuantity(url.substring(url.lastIndexOf("/")+1));
            Intent it=new Intent(this,TvDetailActivity.class);
            it.putExtra("radioId",Integer.parseInt(radioId));
            startActivity(it);
        }else if(url.startsWith(ServerInfo.h5IP+"/radios/")||url.startsWith(ServerInfo.h5HttpsIP+"/radios/")){
            String radioId=CommonUtils.getQuantity(url.substring(url.lastIndexOf("/")+1));
            Intent it=new Intent(this,RadioDetailActivity.class);
            it.putExtra("radioId",Integer.parseInt(radioId));
            startActivity(it);
        }else if(url.startsWith(ServerInfo.h5IP+"/radios")||url.startsWith(ServerInfo.h5HttpsIP+"/radios")){
            Intent it=new Intent(this,RadioListActivity.class);
            it.putExtra("type","1");
            startActivity(it);
        }else if(url.startsWith(ServerInfo.h5IP+"/gover")||url.startsWith(ServerInfo.h5HttpsIP+"/gover")){
            Intent it=new Intent(this,WebViewBackActivity.class);
            it.putExtra("title",title);
            it.putExtra("url",url);
            startActivity(it);
        }else if(url.startsWith(ServerInfo.h5IP+"/dj")||url.startsWith(ServerInfo.h5HttpsIP+"/dj")){
            Intent it=new Intent(this,WebViewBackActivity.class);
            it.putExtra("title",title);
            it.putExtra("url",url);
            startActivity(it);
        }else if(url.startsWith(ServerInfo.h5IP+"/interact")||url.startsWith(ServerInfo.h5HttpsIP+"/interact")){
            Intent it=new Intent(this,WebViewBackActivity.class);
            it.putExtra("title",title);
            it.putExtra("url",url);
            startActivity(it);
        }else if(url.startsWith(ServerInfo.h5IP+"/guide")||url.startsWith(ServerInfo.h5HttpsIP+"/guide")){
            Intent it=new Intent(this,WebViewBackActivity.class);
            it.putExtra("title",title);
            it.putExtra("url",url);
            startActivity(it);
        }else if(url.startsWith(ServerInfo.h5IP+"/cates/")||url.startsWith(ServerInfo.h5HttpsIP+"/cates/")){
            //跳转栏目
            Intent it=new Intent(this,WebViewBackActivity.class);
            it.putExtra("url",url);
            it.putExtra("title",title);
            startActivity(it);

        }else if(url.startsWith(ServerInfo.h5IP+"/specials")||url.startsWith(ServerInfo.h5HttpsIP+"/specials")){
            //跳转热门
            Intent it=new Intent(this,WebViewBackActivity.class);
            it.putExtra("url",url);
            it.putExtra("title",title);
            startActivity(it);

        }else if(url.startsWith(ServerInfo.h5IP+"/orgs/")||url.startsWith(ServerInfo.h5HttpsIP+"/orgs/")){
            String organizationId=url.substring(url.lastIndexOf("/")+1);
//            Intent it = new Intent(this, OrganizationDetailActivity.class);
//            it.putExtra("organizationId", Integer.parseInt(organizationId));
//            startActivity(it);
            Intent it = new Intent(this, WebViewBackActivity.class);
            it.putExtra("url", ServerInfo.h5HttpsIP+"/orgs/"+organizationId);
            it.putExtra("title",title);
            startActivity(it);

        }else if(url.startsWith(ServerInfo.h5IP+"/anchors/")||url.startsWith(ServerInfo.h5HttpsIP+"/anchors/")){
            String anchorId=url.substring(url.lastIndexOf("/")+1);
//            Intent it = new Intent(this, AnchorDetailActivity.class);
//            it.putExtra("anchorId", Integer.parseInt(anchorId));
//            startActivity(it);

            Intent it = new Intent(this, WebViewBackActivity.class);
            it.putExtra("url", ServerInfo.h5HttpsIP+"/anchors/"+anchorId);
            it.putExtra("title",title);
            startActivity(it);
        }else if(url.startsWith(ServerInfo.h5IP+"/atlas/")||url.startsWith(ServerInfo.h5HttpsIP+"/atlas/")){
            String galleriaId=CommonUtils.getQuantity(url.substring(url.lastIndexOf("/")+1));
            Intent it = new Intent(this, GalleriaActivity.class);
            it.putExtra("galleriaId", Integer.parseInt(galleriaId));
            startActivity(it);
        }else if(url.startsWith(ServerInfo.h5IP+"/albums/")||url.startsWith(ServerInfo.h5HttpsIP+"/albums/")){
            String albumId=CommonUtils.getQuantity(url.substring(url.lastIndexOf("/")+1));
            Intent it = new Intent(this, AlbumDetailActivity.class);
            it.putExtra("albumId", Integer.parseInt(albumId));
            startActivity(it);
        }else if(url.startsWith(ServerInfo.h5IP+"/audios/")||url.startsWith(ServerInfo.h5HttpsIP+"/audios/")){
            String audioId=CommonUtils.getQuantity(url.substring(url.lastIndexOf("/")+1));
            Intent it = new Intent(this, AudioDetailActivity.class);
            it.putExtra("audioId", Integer.parseInt(audioId));
            startActivity(it);
        }else if(url.startsWith(ServerInfo.h5IP+"/posts/")||url.startsWith(ServerInfo.h5HttpsIP+"/posts/")){
            String articleId=CommonUtils.getQuantity(url.substring(url.lastIndexOf("/")+1));
            Intent it = new Intent(this, ArticleDetailActivity.class);
            it.putExtra("articleId", Integer.parseInt(articleId));
            startActivity(it);
        }else if(url.startsWith(ServerInfo.h5IP+"/specials/")||url.startsWith(ServerInfo.h5HttpsIP+"/specials/")){
            String specialId=CommonUtils.getQuantity(url.substring(url.lastIndexOf("/")+1));
            Intent it = new Intent(this, SpecialDetailActivity.class);
            it.putExtra("specialId", Integer.parseInt(specialId));
            startActivity(it);
        }else if(url.startsWith(ServerInfo.h5IP+"/videos/")||url.startsWith(ServerInfo.h5HttpsIP+"/videos/")){
            String videoId=CommonUtils.getQuantity(url.substring(url.lastIndexOf("/")+1));
            Intent it = new Intent(this, VideoDetailActivity.class);
            it.putExtra("videoId",Integer.parseInt(videoId));
            startActivity(it);
        }else if(url.startsWith(ServerInfo.h5IP+"/subcates/")||url.startsWith(ServerInfo.h5IP+"/subcates/")){
            String columnid=CommonUtils.getQuantity(url.substring(url.lastIndexOf("/")+1));
            Column column=new Column();
            column.setId(Integer.parseInt(columnid));
            column.setName(url.substring(url.lastIndexOf("=")+1));
            Intent it = new Intent(this, ContentActivity.class);
            it.putExtra("column", column);
            startActivity(it);
        }else if (url.startsWith(ServerInfo.scs + "/broke-create")) {
            if (User.getInstance() == null) {
                Intent it = new Intent(this, NewLoginActivity.class);
                startActivity(it);
            }else if (User.getInstance() !=null&&TextUtils.isEmpty(User.getInstance().getPhone())) {
                Intent it = new Intent(this, BindPhoneActivity.class);
                //it.putExtra("hasLogined",true);
                startActivity(it);
            }
            else {
                Intent it = new Intent(this, CluesActivity.class);
                it.putExtra("url", ServerInfo.scs + "/broke-create");
                startActivity(it);
            }
        }else if (url.startsWith(ServerInfo.h5IP + "/invitation-post") || url.startsWith(ServerInfo.h5HttpsIP + "/invitation-post")) {
            Intent it = new Intent(this, WebViewBackActivity.class);
            it.putExtra("url", url);
            it.putExtra("title",title);
            startActivity(it);
        }else if (url.startsWith(ServerInfo.h5IP + "/actrank") || url.startsWith(ServerInfo.h5HttpsIP + "/actrank")) {
            Intent it = new Intent(this, WebViewBackActivity.class);
            it.putExtra("url", url);
            it.putExtra("title",title);
            startActivity(it);
        }else if (url.startsWith(ServerInfo.h5IP + "/wish") || url.startsWith(ServerInfo.h5HttpsIP + "/wish")) {
            Intent it = new Intent(this, WebViewBackActivity.class);
            it.putExtra("url", url);
            it.putExtra("title",title);
            startActivity(it);
        }else if (url.startsWith(ServerInfo.h5IP + "/actlist") || url.startsWith(ServerInfo.h5HttpsIP + "/actlist")) {
            Intent it = new Intent(this, WebViewBackActivity.class);
            it.putExtra("url", url);
            it.putExtra("title",title);
            startActivity(it);
        }else {
            Intent it=new Intent(this,WebViewBackActivity.class);
            it.putExtra("url",url);
            it.putExtra("title",title);
            startActivity(it);
        }
    }


}
