package com.shuangling.software.activity;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.player.IPlayer;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.RadioRecommendAdapter;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.customview.MyListView;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.dialog.AudioTimerDialog;
import com.shuangling.software.dialog.RadioListDialog;
import com.shuangling.software.entity.AudioInfo;
import com.shuangling.software.entity.RadioDetail;
import com.shuangling.software.entity.RadioRecommend;
import com.shuangling.software.entity.RadioSet;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.PlayerEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.service.IAudioPlayer;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.FloatWindowUtil;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jake.share.frdialog.dialog.FRDialog;
import cn.jake.share.frdialog.interfaces.FRDialogClickListener;
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
import okhttp3.Response;

@EnableDragToClose()
public class RadioDetailActivity extends AppCompatActivity implements Handler.Callback {


    public static final String TAG = "RadioDetailActivity";

    public static final int MSG_GET_RADIO_DETAIL = 0x00;

    public static final int MSG_GET_RADIO_RECOMMEND = 0x01;

    public static final int MSG_UPDATE_UI = 0x02;

    public static final int MSG_COLLECT_CALLBACK = 0x03;

    public static final int REQUEST_LOGIN = 0x04;

    private static final int SHARE_SUCCESS = 0x5;

    private static final int SHARE_FAILED = 0x6;

    public static final int REQUEST_PERMISSION_CODE = 0x0110;

    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.programName)
    TextView programName;
    @BindView(R.id.anchorName)
    TextView anchorName;
    @BindView(R.id.startTime)
    TextView startTime;
    @BindView(R.id.finishTime)
    TextView finishTime;
    @BindView(R.id.currentTime)
    TextView currentTime;
    @BindView(R.id.endTime)
    TextView endTime;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.list)
    TextView list;
    @BindView(R.id.play)
    FontIconView play;
    @BindView(R.id.previous)
    FontIconView previous;
    @BindView(R.id.next)
    FontIconView next;
    @BindView(R.id.timer)
    TextView timer;
    @BindView(R.id.actionBar)
    RelativeLayout actionBar;
    @BindView(R.id.logo)
    SimpleDraweeView logo;
    @BindView(R.id.radio)
    TextView radio;
    @BindView(R.id.organization)
    TextView organization;
    @BindView(R.id.collect)
    TextView collect;
    @BindView(R.id.listView)
    MyListView listView;
//    @BindView(R.id.collectIcon)
//    FontIconView collectIcon;
//    @BindView(R.id.collectLayout)
//    LinearLayout collectLayout;


    private Handler mHandler;
    //private RadioSet.Radio mRadio;
    private int mRadioId;
    private IAudioPlayer mAudioPlayer;
    private RadioDetail mRadioDetail;
    private RadioRecommendAdapter mRadioRecommendAdapter;

    private Timer mTimer;
    private UpdateTimerTask mUpdateTimerTask;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAudioPlayer = IAudioPlayer.Stub.asInterface(service);
            getRadioDetail();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_radio_detail);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
//        StatusBarUtil.setTransparent(this);
//        StatusBarManager.setImmersiveStatusBar(this, true);
        init();

    }

    private void init() {
        mHandler = new Handler(this);
        EventBus.getDefault().register(this);
        //mRadio = getIntent().getParcelableExtra("Radio");
        mRadioId= getIntent().getIntExtra("radioId",0);
        activityTitle.setMoreAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRadioDetail != null) {
                    showShare(mRadioDetail.getChannel().getName(), mRadioDetail.getChannel().getDes(), mRadioDetail.getChannel().getLogo(), ServerInfo.h5IP + "/radios/" + mRadioDetail.getChannel().getId());
                    //shareTest();
                }

            }
        });
        Intent it = new Intent(this, AudioPlayerService.class);
        bindService(it, mConnection, Context.BIND_AUTO_CREATE);


    }


    private void getRadioDetail() {


        String url = ServerInfo.serviceIP + ServerInfo.getRadioDetail;
        Map<String, String> params = new HashMap<>();
        params.put("channel_id", "" + mRadioId);

        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_RADIO_DETAIL;
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {

            }
        });
    }

    private void addRadioHistory() {


        String url = ServerInfo.serviceIP + ServerInfo.addRadioHistory;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + mRadioId);

        OkHttpUtils.put(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

            }

            @Override
            public void onFailure(Call call, Exception exception) {

            }
        });
    }



    public void updateUI() {
        String url = ServerInfo.serviceIP + ServerInfo.getRadioDetail;
        Map<String, String> params = new HashMap<>();
        params.put("channel_id", "" + mRadioDetail.getChannel().getId());

        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_UPDATE_UI;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }


    public void collect() {
        if(mRadioDetail==null){
            return;
        }
        String url = ServerInfo.serviceIP + ServerInfo.collect;
        Map<String,String> params =new HashMap<>();
        params.put("channel_id",""+mRadioDetail.getChannel().getId());
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_COLLECT_CALLBACK;
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });

    }


    private void getRadioRecommend() {


        String url = ServerInfo.serviceIP + ServerInfo.getRadioRecommend;
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", "" + mRadioDetail.getChannel().getMerchant_id().getId());
        params.put("channel_id", "" + mRadioDetail.getChannel().getId());

        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_RADIO_RECOMMEND;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(PlayerEvent event) {
        if (event.getEventName().equals("OnPrepared")) {
            //1.改变播放按钮的状态
            //2.获取进度时长并显示
            //2.设置定时器更新进度条
//            AudioInfo audioInfo = (AudioInfo) event.getEventObject();
//            mAudioId = audioInfo.getId();
//            getRadioDetail(true);

            try {
                //play.setBackgroundResource(R.drawable.ic_suspended);
                play.setText(getResources().getString(R.string.play_icon_pause));
                endTime.setText(CommonUtils.getShowTime(mAudioPlayer.getDuration()));
                seekBar.setMax((int) (mAudioPlayer.getDuration()));
                seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
                startUpdateTimer();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (event.getEventName().equals("OnTimerTick")) {
            timer.setText(CommonUtils.getShowTime((long) event.getEventObject()));
        } else if (event.getEventName().equals("OnTimerFinish")) {
            timer.setText("定时");
        } else if (event.getEventName().equals("OnTimerCancel")) {
            timer.setText("定时");
        }
    }


    @Override
    protected void onPause() {
        try{
            if(mAudioPlayer.getPlayerState()==IPlayer.started){
                FloatWindowUtil.getInstance().showFloatWindow();
            }
        }catch (RemoteException e){

        }

        super.onPause();
    }

    private void showFloatWindowPermission() {
        FloatWindowUtil.getInstance().addOnPermissionListener(new FloatWindowUtil.OnPermissionListener() {
            @Override
            public void showPermissionDialog() {
                FRDialog dialog = new FRDialog.MDBuilder(RadioDetailActivity.this)
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

    @Override
    protected void onResume() {
//        FloatWindowUtil.getInstance().hideWindow();
//        super.onResume();
        if(!FloatWindowUtil.getInstance().checkFloatWindowPermission()){
            if(MyApplication.getInstance().remindPermission) {
                MyApplication.getInstance().remindPermission=false;
                showFloatWindowPermission();

            }
        }
        FloatWindowUtil.getInstance().hideWindow();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        unbindService(mConnection);
        super.onDestroy();
    }


    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case MSG_GET_RADIO_DETAIL:

                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mRadioDetail = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), RadioDetail.class);


                        AudioInfo audioInfo = new AudioInfo();
                        audioInfo.setId(mRadioDetail.getChannel().getId());
                        audioInfo.setTitle(mRadioDetail.getChannel().getName());
                        audioInfo.setUrl(mRadioDetail.getChannel().getStream());
                        audioInfo.setPublish_at(mRadioDetail.getChannel().getCreated_at());
                        audioInfo.setLogo(mRadioDetail.getChannel().getLogo());
                        audioInfo.setIsRadio(1);
                        mAudioPlayer.playAudio(audioInfo);



                        mAudioPlayer.setPlayerList(null);
                        addRadioHistory();

                        if (!TextUtils.isEmpty(mRadioDetail.getChannel().getLogo())) {
                            Uri uri = Uri.parse(mRadioDetail.getChannel().getLogo());
                            int width = CommonUtils.dip2px(50);
                            int height = width;
                            ImageLoader.showThumb(uri, logo, width, height);
                        }

                        if (mRadioDetail.getIn_play() != null) {

                            programName.setText(mRadioDetail.getIn_play().getName());
                            anchorName.setText(mRadioDetail.getIn_play().getAnchor_name());
                            startTime.setText(mRadioDetail.getIn_play().getStart_time());
                            finishTime.setText(mRadioDetail.getIn_play().getEnd_time());
                        }else{
                            programName.setText("暂无节目");
                            anchorName.setText("暂无主播");
                            startTime.setText("");
                            finishTime.setText("");
                        }
                        activityTitle.setTitleText(mRadioDetail.getChannel().getName());
                        radio.setText(mRadioDetail.getChannel().getName());

                        if (mRadioDetail.getChannel() != null) {
                            if (mRadioDetail.getChannel().getMerchant_id() != null) {
                                organization.setText(mRadioDetail.getChannel().getMerchant_id().getName());
                            }

                            organization.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mRadioDetail.getChannel().getMerchant_id() != null) {
//                                        Intent it = new Intent(RadioDetailActivity.this, OrganizationDetailActivity.class);
//                                        it.putExtra("organizationId", mRadioDetail.getChannel().getMerchant_id().getId());
//                                        startActivity(it);

                                        Intent it = new Intent(RadioDetailActivity.this, WebViewActivity.class);
                                        it.putExtra("url", ServerInfo.h5HttpsIP+"/orgs/"+mRadioDetail.getChannel().getMerchant_id().getId());
                                        startActivity(it);
                                    }
                                }
                            });

                            if (mRadioDetail.getCollection() == 1) {
                                //未收藏
                                collect.setText("收藏");
                                collect.setActivated(true);

                            } else {
                                collect.setText("已收藏");
                                collect.setActivated(false);
                            }

                            collect.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(User.getInstance()!=null){
                                        collect();
                                    }else {
                                        startActivityForResult(new Intent(RadioDetailActivity.this, LoginActivity.class),REQUEST_LOGIN);
                                    }
                                }
                            });


                        }

                        getRadioRecommend();


                    }


                } catch (Exception e) {

                }
                break;
            case MSG_GET_RADIO_RECOMMEND:


                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        final List<RadioRecommend> radioRecommends = JSONArray.parseArray(jsonObject.getJSONArray("data").toJSONString(), RadioRecommend.class);

                        if (mRadioRecommendAdapter == null) {
                            mRadioRecommendAdapter = new RadioRecommendAdapter(this, radioRecommends);
                            listView.setAdapter(mRadioRecommendAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    RadioRecommend radioRecommend=mRadioRecommendAdapter.getItem(position);
                                    mRadioId=radioRecommend.getId();
                                    getRadioDetail();
//                                    getRadioRecommend();


                                }
                            });
                        } else {
                            mRadioRecommendAdapter.updateListView(radioRecommends);
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }
                break;
            case MSG_UPDATE_UI:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mRadioDetail = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), RadioDetail.class);


                        if (!TextUtils.isEmpty(mRadioDetail.getChannel().getLogo())) {
                            Uri uri = Uri.parse(mRadioDetail.getChannel().getLogo());
                            int width = CommonUtils.dip2px(50);
                            int height = width;
                            ImageLoader.showThumb(uri, logo, width, height);
                        }

                        if (mRadioDetail.getIn_play() != null) {
                            programName.setText(mRadioDetail.getIn_play().getName());
                            startTime.setText(mRadioDetail.getIn_play().getStart_time());
                            finishTime.setText(mRadioDetail.getIn_play().getEnd_time());
                            anchorName.setText(mRadioDetail.getIn_play().getAnchor_name());
                        }
                        activityTitle.setTitleText(mRadioDetail.getChannel().getName());
                        radio.setText(mRadioDetail.getChannel().getName());

                        if (mRadioDetail.getChannel() != null) {
                            if (mRadioDetail.getChannel().getMerchant_id() != null) {
                                organization.setText(mRadioDetail.getChannel().getMerchant_id().getName());
                            }

                            if (mRadioDetail.getCollection() == 1) {
                                //未收藏
                                collect.setText("收藏");
                                collect.setActivated(true);

                            } else {
                                collect.setText("已收藏");
                                collect.setActivated(false);
                            }
                        }

                    }


                } catch (Exception e) {

                }
                break;
            case MSG_COLLECT_CALLBACK:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
//                        ToastUtils.show(jsonObject.getString("msg"));
//                        updateUI();
                        if (jsonObject.getInteger("data") == 1) {
                            collect.setActivated(true);
                            collect.setText("收藏");
                            mRadioDetail.setCollection(1);
                            ToastUtils.show("取消收藏成功");
                        } else {
                            collect.setActivated(false);
                            collect.setText("已收藏");
                            mRadioDetail.setCollection(2);
                            ToastUtils.show("收藏成功");
                        }

                    }


                } catch (Exception e) {

                }
                break;
        }
        return false;
    }

    @OnClick({R.id.list, R.id.play, R.id.previous, R.id.next, R.id.timer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.list:

                RadioListDialog.getInstance(mRadioDetail).show(getSupportFragmentManager(), "RadioListDialog");

                break;
            case R.id.play:
                try {
                    if (mAudioPlayer.getPlayerState() == IPlayer.paused) {
                        mAudioPlayer.start();
                        //play.setBackgroundResource(R.drawable.ic_suspended);
                        play.setText(R.string.play_icon_pause);
                    } else if (mAudioPlayer.getPlayerState() == IPlayer.started) {
                        mAudioPlayer.pause();
                        //play.setBackgroundResource(R.drawable.ic_play);
                        play.setText(R.string.play_icon_play);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.previous:
                break;
            case R.id.next:
                break;
            case R.id.timer:
                AudioTimerDialog.getInstance().show(getSupportFragmentManager(), "AudioTimerDialog");
                break;

        }
    }



    public class UpdateTimerTask extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        //1.更新当前时间
                        //2.更新当前进度条
                        currentTime.setText(CommonUtils.getShowTime(mAudioPlayer.getCurrentPosition()));
                        seekBar.setProgress((int) (mAudioPlayer.getCurrentPosition()));

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }


                }
            });

        }
    }


    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // TODO Auto-generated method stub
            if (progress >= 0) {
                // 如果是用户手动拖动控件，则设置视频跳转。
                if (fromUser) {
                    try {
                        mAudioPlayer.seekTo(progress);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }

            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        public void onStopTrackingTouch(SeekBar seekBar) {

        }

    }


    public void startUpdateTimer() {

        cancelUpdateTimer();
        mTimer = new Timer();
        mUpdateTimerTask = new UpdateTimerTask();
        mTimer.schedule(mUpdateTimerTask, 0, 500);
    }

    public void cancelUpdateTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mUpdateTimerTask != null) {
            mUpdateTimerTask.cancel();
        }
    }

    private void showShare(final String title, final String desc, final String logo, final String url) {
        final String cover;
        if(logo.startsWith("http://")){
            cover=logo.replace("http://","https://");
        }else{
            cover=logo;
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
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                //点击新浪微博
                String chanel="1";
                if (SinaWeibo.NAME.equals(platform.getName())) {
                    chanel="2";
                    //限制微博分享的文字不能超过20
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                    paramsToShare.setText(title + url);
                } else if (QQ.NAME.equals(platform.getName())) {
                    chanel="3";
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
                shareStatistics(chanel,""+mRadioDetail.getChannel().getId(),url);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_LOGIN&&resultCode==RESULT_OK){
            updateUI();
        }
        //super.onActivityResult(requestCode, resultCode, data);
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

}
