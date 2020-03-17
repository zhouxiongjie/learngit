package com.shuangling.software.activity;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.player.IPlayer;
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.AudioRecyclerAdapter;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.dialog.AudioListDialog;
import com.shuangling.software.dialog.AudioSpeedDialog;
import com.shuangling.software.dialog.AudioTimerDialog;
import com.shuangling.software.entity.Audio;
import com.shuangling.software.entity.AudioDetail;
import com.shuangling.software.entity.AudioInfo;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.Comment;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.PlayerEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.service.IAudioPlayer;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.FloatWindowUtil;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;
import com.youngfeng.snake.annotations.EnableDragToClose;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
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

@EnableDragToClose()
public class AudioDetailActivity extends AppCompatActivity implements Handler.Callback, View.OnClickListener {

    public static final String TAG = "AlbumDetailActivity";

    public static final int MSG_GET_AUDIO_DETAIL = 0x1;

    public static final int MSG_GET_AUDIOS_LIST = 0x2;

    public static final int MSG_GET_RELATED_POST = 0x3;

    public static final int MSG_GET_COMMENTS = 0x4;

    public static final int MSG_WRITE_COMMENTS = 0x5;

    public static final int MSG_PRAISE = 0x6;

    public static final int MSG_SUBSCRIBE_CALLBACK = 0x7;

    public static final int REQUEST_LOGIN = 0x8;

    private static final int SHARE_SUCCESS = 0x9;

    private static final int SHARE_FAILED = 0xa;

    public static final int MSG_DELETE_COMMENT = 0xb;

    public static final int REQUEST_BIND_PHONE = 0xc;


    public static final int REQUEST_PERMISSION_CODE = 0x0110;

    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.divideOne)
    ImageView divideOne;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.writeComment)
    TextView writeComment;
    @BindView(R.id.commentsIcon)
    ImageView commentsIcon;
    @BindView(R.id.commentNumber)
    TextView commentNumber;
    @BindView(R.id.commentNumLayout)
    FrameLayout commentNumLayout;
    @BindView(R.id.bottom)
    LinearLayout bottom;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;


    private int mNetPlay;
    private int mNeedTipPlay;

    private Handler mHandler;
    private int mAudioId;
    private AudioDetail mAudioDetail;
    private AudioInfo mAudioInfo;

    private IAudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateTimerTask mUpdateTimerTask;
    private List<ColumnContent> mPostContents;
    private List<Comment> mComments;
    boolean mScrollToTop = false;
    private DialogFragment mCommentDialog;
    private RecyclerViewSkeletonScreen mViewSkeletonScreen;
    private AudioRecyclerAdapter mAdapter;

    private HeadViewHolder mHeadViewHolder;
    private int currentPage=1;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mAudioPlayer = IAudioPlayer.Stub.asInterface(service);
            mHeadViewHolder.play.setOnClickListener(AudioDetailActivity.this);
            mHeadViewHolder.previous.setOnClickListener(AudioDetailActivity.this);
            mHeadViewHolder.next.setOnClickListener(AudioDetailActivity.this);
            mHeadViewHolder.list.setOnClickListener(AudioDetailActivity.this);
            mHeadViewHolder.timer.setOnClickListener(AudioDetailActivity.this);
            mHeadViewHolder.rate.setOnClickListener(AudioDetailActivity.this);
            try {
                int speed = mAudioPlayer.getPlaySpeed();
                if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed050) {
                    mHeadViewHolder.rate.setText("0.5x");
                } else if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed075) {
                    mHeadViewHolder.rate.setText("0.75x");
                } else if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed100) {
                    mHeadViewHolder.rate.setText("倍速");
                } else if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed125) {
                    mHeadViewHolder.rate.setText("1.25x");
                } else if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed150) {
                    mHeadViewHolder.rate.setText("1.5x");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            getAudioDetail(false);
            getComments(0);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_audio_detail);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        init();

    }


    @Override
    protected void onNewIntent(Intent intent) {

        mAudioId = intent.getIntExtra("audioId", 0);
        mNetPlay = SharedPreferencesUtils.getIntValue(SettingActivity.NET_PLAY, 0);
        mNeedTipPlay = SharedPreferencesUtils.getIntValue(SettingActivity.NEED_TIP_PLAY, 0);
        getAudioDetail(false);

        getComments(0);
        super.onNewIntent(intent);
    }


    public class HeadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.logo)
        public SimpleDraweeView logo;
        @BindView(R.id.audioTitle)
        public TextView audioTitle;
        @BindView(R.id.organization)
        public TextView organization;
        @BindView(R.id.currentTime)
        public TextView currentTime;
        @BindView(R.id.endTime)
        public TextView endTime;
        @BindView(R.id.seekBar)
        public SeekBar seekBar;
        @BindView(R.id.play)
        public FontIconView play;
        @BindView(R.id.previous)
        public FontIconView previous;
        @BindView(R.id.next)
        public FontIconView next;
        @BindView(R.id.actionBar)
        public RelativeLayout actionBar;
        @BindView(R.id.list)
        public TextView list;
        @BindView(R.id.timer)
        public TextView timer;
        @BindView(R.id.rate)
        public TextView rate;
        @BindView(R.id.albumLogo)
        public SimpleDraweeView albumLogo;
        @BindView(R.id.albumTitle)
        public TextView albumTitle;
        @BindView(R.id.anchorName)
        public TextView anchorName;
        @BindView(R.id.audioNumber)
        public TextView audioNumber;
        @BindView(R.id.subscribe)
        public TextView subscribe;

        HeadViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void init() {
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));//设置
        mNetPlay = SharedPreferencesUtils.getIntValue(SettingActivity.NET_PLAY, 0);
        mNeedTipPlay = SharedPreferencesUtils.getIntValue(SettingActivity.NEED_TIP_PLAY, 0);
        mHandler = new Handler(this);
        EventBus.getDefault().register(this);

        mAudioId = getIntent().getIntExtra("audioId", 0);
        activityTitle.setMoreAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAudioDetail != null) {
                    showShare(mAudioDetail.getAlbum().get(0).getTitle(), mAudioDetail.getTitle(), mAudioDetail.getAlbum().get(0).getCover(), ServerInfo.h5IP + "/audios/" + mAudioId);
                    //shareTest();
                }
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new AudioRecyclerAdapter(this);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.recycleview_divider_drawable));
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(mAdapter);
        ViewGroup headView = (ViewGroup) getLayoutInflater().inflate(R.layout.audio_top_layout, recyclerView, false);
        mHeadViewHolder = new HeadViewHolder(headView);
        mAdapter.addHeaderView(headView);

        Intent it = new Intent(this, AudioPlayerService.class);
        bindService(it, mConnection, Context.BIND_AUTO_CREATE);

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                currentPage++;
                getComments(1);
            }
        });
    }


    private void getAudioDetail(final boolean isOnPrepared) {


        if (!isOnPrepared) {

            mViewSkeletonScreen =
                    Skeleton.bind(recyclerView)
                            .adapter(mAdapter)
                            .shimmer(false)
                            .angle(20)
                            .frozen(false)
                            .duration(3000)
                            .count(1)
                            .load(R.layout.skeleton_audio_detail)
                            .show();


        }

        String url = ServerInfo.serviceIP + ServerInfo.getAudioDetail + mAudioId;
        OkHttpUtils.get(url, null, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_AUDIO_DETAIL;
                msg.arg1 = isOnPrepared ? 1 : 0;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mViewSkeletonScreen.hide();
                    }
                });

            }
        });
    }


    private void getRelatedPosts() {
        String url = ServerInfo.serviceIP + ServerInfo.getRelatedRecommend;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", "" + mAudioDetail.getAlbum().get(0).getId());
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_RELATED_POST;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }


    private void getAlbumAudios(String albumId) {


        String url = ServerInfo.serviceIP + ServerInfo.getAlbumAudios + albumId;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageSize", "" + Integer.MAX_VALUE);

        params.put("page", "" + 1);
        params.put("sort", "0");

        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_AUDIOS_LIST;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }

    //type  0 正常   1 加载更多
    private void getComments(final int type) {
        if(type==0){
            currentPage=1;
        }
        String url = ServerInfo.serviceIP + ServerInfo.getComentList;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", "" + mAudioId);
        params.put("page", "" + currentPage);
        params.put("page_size", "" + Constant.PAGE_SIZE);
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_COMMENTS;
                msg.arg1=type;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }


    private void praise(String commentId, final View view) {
        String url = ServerInfo.serviceIP + ServerInfo.praise;
        Map<String, String> params = new HashMap<>();
        params.put("id", commentId);

        OkHttpUtils.put(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_PRAISE;
                Bundle bundle = new Bundle();
                bundle.putString("response", response);
                msg.setData(bundle);
                msg.obj = view;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }


    public void subscribe(final boolean subscribe) {


        String url = ServerInfo.serviceIP + ServerInfo.subscribes;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + mAudioDetail.getAlbum().get(0).getId());
        if (subscribe) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }

        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_SUBSCRIBE_CALLBACK;
                msg.arg1 = subscribe ? 1 : 0;
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });

    }


    private void deleteComment(Comment comment) {


        String url = ServerInfo.serviceIP + ServerInfo.getComentList;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + comment.getId());

        OkHttpUtils.delete(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_DELETE_COMMENT;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }

    private void writeComments(String content, String parentId, String topCommentId) {
        String url = ServerInfo.serviceIP + ServerInfo.getComentList;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", "" + mAudioId);
        params.put("type", "1");
        params.put("content", content);
        params.put("parent_id", parentId);
        params.put("base_comment_id", topCommentId);
        params.put("source_type", "3");
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_WRITE_COMMENTS;
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

            AudioInfo audioInfo = (AudioInfo) event.getEventObject();
            mAudioId = audioInfo.getId();
            getAudioDetail(true);
            //getRelatedPosts();
            getComments(0);
            //1.改变播放按钮的状态
            //2.获取进度时长并显示
            //2.设置定时器更新进度条
            try {

                mHeadViewHolder.play.setText(R.string.play_icon_pause);
                mHeadViewHolder.endTime.setText(CommonUtils.getShowTime(mAudioPlayer.getDuration()));
                mHeadViewHolder.seekBar.setMax((int) (mAudioPlayer.getDuration()));
                mHeadViewHolder.seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
                startUpdateTimer();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (event.getEventName().equals("OnTimerTick")) {
            mHeadViewHolder.timer.setText(CommonUtils.getShowTime((long) event.getEventObject()));
        } else if (event.getEventName().equals("OnTimerFinish")) {
            mHeadViewHolder.timer.setText("定时");
        } else if (event.getEventName().equals("OnTimerCancel")) {
            mHeadViewHolder.timer.setText("定时");
        } else if (event.getEventName().equals("SpeedChanged")) {

            try {
                int speed = mAudioPlayer.getPlaySpeed();
                if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed050) {
                    mHeadViewHolder.rate.setText("0.5x");
                } else if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed075) {
                    mHeadViewHolder.rate.setText("0.75x");
                } else if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed100) {
                    mHeadViewHolder.rate.setText("倍速");
                } else if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed125) {
                    mHeadViewHolder.rate.setText("1.25x");
                } else if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed150) {
                    mHeadViewHolder.rate.setText("1.5x");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        unbindService(mConnection);
        super.onDestroy();
    }

    @OnClick({R.id.writeComment, R.id.commentNumLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {


            case R.id.writeComment: {

                if (User.getInstance() == null) {

                    Intent it = new Intent(this, NewLoginActivity.class);
                    it.putExtra("bindPhone",true);
                    startActivityForResult(it, REQUEST_LOGIN);
                }else if (User.getInstance() !=null&&TextUtils.isEmpty(User.getInstance().getPhone())) {

                    Intent it = new Intent(this, BindPhoneActivity.class);
                    //it.putExtra("hasLogined",true);
                    startActivity(it);
                }else {
                    mCommentDialog = new CircleDialog.Builder()
                            .setCanceledOnTouchOutside(false)
                            .setCancelable(true)
                            .setInputManualClose(true)
                            .setTitle("发表评论")
//                        .setSubTitle("发表评论")
                            .setInputHint("写评论")
//                        .setInputText("默认文本")
                            .autoInputShowKeyboard()
                            .setInputCounter(200)
                            .configInput(new ConfigInput() {
                                @Override
                                public void onConfig(InputParams params) {
                                    params.styleText = Typeface.NORMAL;
                                }
                            })
                            .setNegative("取消", null)
                            .setPositiveInput("发表", new OnInputClickListener() {
                                @Override
                                public void onClick(String text, View v) {
                                    if (TextUtils.isEmpty(text)) {
                                        ToastUtils.show("请输入内容");
                                    } else {
                                        mCommentDialog.dismiss();
                                        //发送评论
                                        writeComments(text, "0", "0");

                                    }
                                }
                            })
                            .show(getSupportFragmentManager());
                }

            }
            break;
            case R.id.commentNumLayout:
                if (mScrollToTop) {
                    mScrollToTop = false;
                    recyclerView.smoothScrollToPosition(0);
                } else {
                    mScrollToTop = true;
                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    llm.scrollToPositionWithOffset(mPostContents != null ? 1 + mPostContents.size() : 1, 0);
                    llm.setStackFromEnd(false);
                }
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case MSG_GET_AUDIO_DETAIL:
                try {
                    String result = (String) msg.obj;
                    boolean isOnPrepared = (msg.arg1 == 1 ? true : false);
                    if (!isOnPrepared) {
                        mViewSkeletonScreen.hide();
                    }
                    JSONObject jsonObject = JSONObject.parseObject(result);

                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        mAudioDetail = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), AudioDetail.class);

                        if (!isOnPrepared) {
                            mAudioInfo = new AudioInfo();
                            mAudioInfo.setId(mAudioDetail.getId());
                            mAudioInfo.setTitle(mAudioDetail.getTitle());
                            mAudioInfo.setUrl(mAudioDetail.getAudio().getUrl());
                            mAudioInfo.setDuration(mAudioDetail.getAudio().getDuration());
                            mAudioInfo.setPublish_at(mAudioDetail.getAlbum().get(0).getPublish_at());
                            mAudioInfo.setLogo(mAudioDetail.getAlbum().get(0).getCover());
                            mAudioInfo.setSourceId(mAudioDetail.getAudio().getSource_id());
                            mAudioInfo.setIsRadio(0);


                            //判断网络环境
                            if (CommonUtils.getNetWorkType(this) == CommonUtils.NETWORKTYPE_MOBILE) {
                                if (mNetPlay == 0) {
                                    //每次提醒
                                    new CircleDialog.Builder()
                                            .setCanceledOnTouchOutside(false)
                                            .setCancelable(false)

                                            .setText("当前非WiFi环境，是否使用流量播放")
                                            .setNegative("暂停播放", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    try {
                                                        mAudioPlayer.stop();
                                                    } catch (RemoteException e) {

                                                    }
                                                }
                                            })
                                            .setPositive("继续播放", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    try {
                                                        mAudioPlayer.playAudio(mAudioInfo);
                                                    } catch (RemoteException e) {

                                                    }

                                                }
                                            })
                                            .show(getSupportFragmentManager());
                                } else {
                                    //提醒一次
                                    if (mNeedTipPlay == 1) {
                                        new CircleDialog.Builder()
                                                .setCanceledOnTouchOutside(false)
                                                .setCancelable(false)

                                                .setText("当前非WiFi环境，是否使用流量播放")
                                                .setNegative("暂停播放", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        try {
                                                            mAudioPlayer.stop();
                                                        } catch (RemoteException e) {

                                                        }
                                                    }
                                                })
                                                .setPositive("继续播放", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        try {
                                                            mAudioPlayer.playAudio(mAudioInfo);
                                                        } catch (RemoteException e) {

                                                        }

                                                    }
                                                })
                                                .show(getSupportFragmentManager());
                                    } else {
                                        mAudioPlayer.playAudio(mAudioInfo);
                                    }
                                }

                            } else {
                                mAudioPlayer.playAudio(mAudioInfo);
                            }


                            getAlbumAudios("" + mAudioDetail.getAlbum().get(0).getId());
                        }
                        getRelatedPosts();

                        if (!TextUtils.isEmpty(mAudioDetail.getAlbum().get(0).getCover())) {
                            Uri uri = Uri.parse(mAudioDetail.getAlbum().get(0).getCover());
                            int width = CommonUtils.dip2px(145);
                            int height = width;
                            ImageLoader.showThumb(uri, mHeadViewHolder.logo, width, height);
                            width = (int) getResources().getDimension(R.dimen.article_right_image_width);
                            height = width;
                            ImageLoader.showThumb(uri, mHeadViewHolder.albumLogo, width, height);
                        }
                        mHeadViewHolder.organization.setText(mAudioDetail.getAlbum().get(0).getMerchant().getName());
                        mHeadViewHolder.albumTitle.setText(mAudioDetail.getAlbum().get(0).getTitle());
                        mHeadViewHolder.anchorName.setText(mAudioDetail.getAuthor_info().getStaff_name());
                        mHeadViewHolder.audioNumber.setText(mAudioDetail.getAlbum().get(0).getAlbums().getCount() + "集");
                        mHeadViewHolder.audioTitle.setText(mAudioDetail.getTitle());
                        if (mAudioDetail.getAlbum().get(0).getIs_sub() == 1) {
                            mHeadViewHolder.subscribe.setText(getResources().getString(R.string.has_subscribe));
                            mHeadViewHolder.subscribe.setActivated(false);
                        } else {
                            mHeadViewHolder.subscribe.setText(getResources().getString(R.string.subscribe));
                            mHeadViewHolder.subscribe.setActivated(true);
                        }
                        mHeadViewHolder.subscribe.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (User.getInstance() == null) {
                                    startActivityForResult(new Intent(AudioDetailActivity.this, NewLoginActivity.class), REQUEST_LOGIN);
                                } else {
                                    subscribe(mAudioDetail.getAlbum().get(0).getIs_sub() == 0);
                                }
                            }
                        });


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case MSG_GET_AUDIOS_LIST: {

                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        JSONObject jo = jsonObject.getJSONObject("data");
                        if (jo.getInteger("total") > 0) {
                            List<Audio> audios = JSONObject.parseArray(jo.getJSONArray("data").toJSONString(), Audio.class);
                            List<AudioInfo> audioInfos = new ArrayList<>();
                            for (int i = 0; audios != null && i < audios.size(); i++) {
                                Audio audio = audios.get(i);
                                AudioInfo audioInfo = new AudioInfo();
                                audioInfo.setId(audio.getAudios().get(0).getId());
                                audioInfo.setIndex(i + 1);
                                audioInfo.setTitle(audio.getAudios().get(0).getTitle());
                                audioInfo.setUrl(audio.getAudios().get(0).getAudio().getUrl());
                                audioInfo.setDuration("" + audio.getAudios().get(0).getAudio().getDuration());
                                audioInfo.setPublish_at(audio.getAudios().get(0).getPublish_at());
                                audioInfo.setLogo(mAudioDetail.getAlbum().get(0).getCover());
                                audioInfo.setSourceId(audio.getAudios().get(0).getAudio().getSource_id());
                                audioInfo.setIsRadio(0);
                                audioInfos.add(audioInfo);
                            }

                            mAudioPlayer.setPlayerList(audioInfos);

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case MSG_GET_RELATED_POST: {
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mPostContents = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class);
                        mAdapter.setPostContents(mPostContents);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case MSG_GET_COMMENTS: {
                String result = (String) msg.obj;
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                    int totalNumber = jsonObject.getJSONObject("data").getInteger("total");
                    mAdapter.setTotalComments(totalNumber);
                    commentNumber.setText("" + totalNumber);

                    List<Comment> comments = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), Comment.class);

                    if (msg.arg1 == 0) {
                        mComments=comments;
//                        if(comments==null||comments.size()==0){
//                            refreshLayout.setEnableLoadMore(false);
//                        }else{
//                            refreshLayout.setEnableLoadMore(true);
//                        }

                    } else if (msg.arg1 == 1) {

                        if (refreshLayout.getState() == RefreshState.Loading) {
//                            if(comments==null||comments.size()==0){
//                                refreshLayout.setEnableLoadMore(false);
//                                //refreshLayout.finishLoadMoreWithNoMoreData();
//                            }else{
//                                refreshLayout.finishLoadMore();
//                            }

                            if(comments==null||comments.size()==0){
                                //refreshLayout.setEnableLoadMore(false);
                                refreshLayout.finishLoadMoreWithNoMoreData();
                            }else{
                                refreshLayout.finishLoadMore();
                            }
                        }

                        mComments.addAll(comments);
                    }


                    if(mComments==null||mComments.size()==0){
                        refreshLayout.setEnableLoadMore(false);
                    }
                    mAdapter.setComments(mComments);

                    mAdapter.setOnPraise(new AudioRecyclerAdapter.OnPraise() {
                        @Override
                        public void praiseItem(Comment comment, View v) {
                            if (User.getInstance() != null) {
                                praise("" + comment.getId(), v);
                            } else {
                                startActivityForResult(new Intent(AudioDetailActivity.this, NewLoginActivity.class), REQUEST_LOGIN);
                            }

                        }

                        @Override
                        public void deleteItem(final Comment comment, View v) {
                            new CircleDialog.Builder()
                                    .setCanceledOnTouchOutside(false)
                                    .setCancelable(false)

                                    .setText("确认删除此评论?")
                                    .setNegative("取消", null)
                                    .setPositive("确定", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            deleteComment(comment);

                                        }
                                    })
                                    .show(getSupportFragmentManager());
                        }
                    });
                }
            }
            break;
            case MSG_DELETE_COMMENT:
                try {

                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show("删除成功");
                        getComments(0);
                    }

                } catch (Exception e) {

                }
                break;
            case MSG_WRITE_COMMENTS: {
                String result = (String) msg.obj;
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                    ToastUtils.show("发表成功");
                    getComments(0);

                }
            }
            break;
            case MSG_PRAISE:
                try {

                    String result = msg.getData().getString("response");
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        TextView textView = (TextView) msg.obj;
                        if (jsonObject.getInteger("data") == 1) {
                            //取消点赞
                            textView.setActivated(true);
                            textView.setText("" + (Integer.parseInt(textView.getText().toString()) - 1));
                        } else {
                            //点赞成功
                            textView.setActivated(false);
                            textView.setText("" + (Integer.parseInt(textView.getText().toString()) + 1));

                        }
                        //getComments();

                    }

                } catch (Exception e) {

                }
                break;
            case MSG_SUBSCRIBE_CALLBACK:
                try {
                    String result = (String) msg.obj;
                    boolean sub = msg.arg1 == 1 ? true : false;

                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show(jsonObject.getString("msg"));
                        if (sub) {
                            mHeadViewHolder.subscribe.setText("已订阅");
                            mHeadViewHolder.subscribe.setActivated(false);
                            mAudioDetail.getAlbum().get(0).setIs_sub(1);
                        } else {
                            mHeadViewHolder.subscribe.setText("订阅");
                            mHeadViewHolder.subscribe.setActivated(true);
                            mAudioDetail.getAlbum().get(0).setIs_sub(0);

                        }


                    }


                } catch (Exception e) {

                }
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                try {
                    int sta = mAudioPlayer.getPlayerState();
                    if (mAudioPlayer.getPlayerState() == IPlayer.paused) {
                        mAudioPlayer.start();
                        mHeadViewHolder.play.setText(R.string.play_icon_pause);
                    } else if (mAudioPlayer.getPlayerState() == IPlayer.started) {
                        mAudioPlayer.pause();
                        mHeadViewHolder.play.setText(R.string.play_icon_play);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.previous:
                try {
                    mAudioPlayer.previous();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.next:
                try {
                    mAudioPlayer.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.list:

                AudioListDialog.getInstance().show(getSupportFragmentManager(), "AudioListDialog");
                break;
            case R.id.timer:
                AudioTimerDialog.getInstance().show(getSupportFragmentManager(), "AudioTimerDialog");
                break;
            case R.id.rate:
                AudioSpeedDialog.getInstance().show(getSupportFragmentManager(), "AudioSpeedDialog");
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
                        if (mAudioPlayer.getPlayerState() == IPlayer.started) {
                            mHeadViewHolder.currentTime.setText(CommonUtils.getShowTime(mAudioPlayer.getCurrentPosition()));
                            mHeadViewHolder.seekBar.setProgress((int) (mAudioPlayer.getCurrentPosition()));
                        }


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
            Log.i("test", "onStartTrackingTouch");
            cancelUpdateTimer();

        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i("test", "onStartTrackingTouch");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startUpdateTimer();
                }
            }, 2000);

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


    @Override
    protected void onPause() {
        try {
            if (mAudioPlayer.getPlayerState() == IPlayer.paused ||
                    mAudioPlayer.getPlayerState() == IPlayer.started) {

                if (FloatWindowUtil.getInstance().checkFloatWindowPermission()) {
                    FloatWindowUtil.getInstance().showFloatWindow();
                } else {
                    //showFloatWindowPermission();

                }


            }
        } catch (RemoteException e) {

        }
        super.onPause();
    }


    private void showFloatWindowPermission() {
        FloatWindowUtil.getInstance().addOnPermissionListener(new FloatWindowUtil.OnPermissionListener() {
            @Override
            public void showPermissionDialog() {
                FRDialog dialog = new FRDialog.MDBuilder(AudioDetailActivity.this)
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

        if (!FloatWindowUtil.getInstance().checkFloatWindowPermission()) {
            if (MyApplication.getInstance().remindPermission) {
                MyApplication.getInstance().remindPermission = false;
                showFloatWindowPermission();

            }
        }
        FloatWindowUtil.getInstance().hideWindow();
        super.onResume();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK) {
            getAudioDetail(true);
            getComments(0);
        } else if (requestCode == REQUEST_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (FloatWindowUtil.getInstance().checkFloatWindowPermission()) {
                    //FloatWindowUtil.getInstance().showFloatWindow();
                } else {
                    //不显示悬浮窗 并提示
                }


            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                //点击新浪微博
                String chanel = "1";
                if (SinaWeibo.NAME.equals(platform.getName())) {
                    //限制微博分享的文字不能超过20
                    chanel = "2";
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
                shareStatistics(chanel, "" + mAudioDetail.getId(), url);
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
                shareStatistics(chanel, "" + mAudioDetail.getId(), url);

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

}
