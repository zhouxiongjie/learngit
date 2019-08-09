package com.shuangling.software.activity;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.CommentListAdapter;
import com.shuangling.software.adapter.RecommendContentAdapter;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.customview.MyListView;
import com.shuangling.software.customview.MyRecyclerView;
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
import com.shuangling.software.utils.FloatWindowUtil;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
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
public class SingleAudioDetailActivity extends AppCompatActivity implements Handler.Callback {

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

    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.divideOne)
    ImageView divideOne;
    @BindView(R.id.logo)
    SimpleDraweeView logo;
    @BindView(R.id.audioTitle)
    TextView audioTitle;
    @BindView(R.id.currentTime)
    TextView currentTime;
    @BindView(R.id.endTime)
    TextView endTime;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.play)
    FontIconView play;
    @BindView(R.id.previous)
    FontIconView previous;
    @BindView(R.id.next)
    FontIconView next;
    @BindView(R.id.actionBar)
    RelativeLayout actionBar;
    @BindView(R.id.list)
    TextView list;
    @BindView(R.id.timer)
    TextView timer;
    @BindView(R.id.rate)
    TextView rate;
    @BindView(R.id.albumLogo)
    SimpleDraweeView albumLogo;
    @BindView(R.id.albumTitle)
    TextView albumTitle;
    @BindView(R.id.anchorName)
    TextView anchorName;
    @BindView(R.id.audioNumber)
    TextView audioNumber;
    @BindView(R.id.subscribe)
    TextView subscribe;
    @BindView(R.id.recommend)
    MyRecyclerView recommend;
    @BindView(R.id.commentNum)
    TextView commentNum;
    @BindView(R.id.listView)
    MyListView listView;
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
    @BindView(R.id.commentLayout)
    LinearLayout commentLayout;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.noScriptText)
    TextView noScriptText;
    @BindView(R.id.noData)
    LinearLayout noData;
    @BindView(R.id.organization)
    TextView organization;


    private Handler mHandler;
    private int mAudioId;
    private AudioDetail mAudioDetail;
    private AudioInfo mAudioInfo;

    private IAudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateTimerTask mUpdateTimerTask;


    private List<ColumnContent> mPostContents;
    private List<Comment> mComments;
    private CommentListAdapter mCommentListAdapter;
    //private RecommendContentGridAdapter mAdapter;
    private RecommendContentAdapter mAdapter;
    boolean mScrollToTop = false;

    private DialogFragment mCommentDialog;
    private ViewSkeletonScreen mViewSkeletonScreen;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mAudioPlayer = IAudioPlayer.Stub.asInterface(service);

            try {
                int speed = mAudioPlayer.getPlaySpeed();
                if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed050) {
                    rate.setText("0.5x");
                } else if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed075) {
                    rate.setText("0.75x");
                } else if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed100) {
                    rate.setText("倍速");
                } else if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed125) {
                    rate.setText("1.25x");
                } else if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed150) {
                    rate.setText("1.5x");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            getAudioDetail(false);
            //getRelatedPosts();
            getComments();

//            try {
//                //mAudioPlayer.playAudio(mAudio);
//
//
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(MyApplication.getInstance().getCurrentTheme());
        setContentView(R.layout.activity_audio_detail);
        ButterKnife.bind(this);
        init();

    }


    @Override
    protected void onNewIntent(Intent intent) {

        mAudioId = intent.getIntExtra("audioId", 0);
        getAudioDetail(false);

        getComments();
        super.onNewIntent(intent);
    }

    private void init() {
        mHandler = new Handler(this);
        EventBus.getDefault().register(this);
        //mAudio = getIntent().getParcelableExtra("Audio");
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
        Intent it = new Intent(this, AudioPlayerService.class);
        bindService(it, mConnection, Context.BIND_AUTO_CREATE);


    }


    private void getAudioDetail(final boolean isOnPrepared) {


        if (!isOnPrepared) {

            mViewSkeletonScreen = Skeleton.bind(scrollView)
                    .shimmer(false)
                    .angle(20)
                    .duration(3000)
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
            public void onFailure(Call call, IOException exception) {
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
            public void onFailure(Call call, IOException exception) {


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
            public void onFailure(Call call, IOException exception) {


            }
        });
    }

    private void getComments() {
        String url = ServerInfo.serviceIP + ServerInfo.getComentList;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", "" + mAudioId);
        params.put("page", "" + 1);
        params.put("page_size", "" + Integer.MAX_VALUE);
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_COMMENTS;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, IOException exception) {


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
            public void onFailure(Call call, IOException exception) {


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
            public void onFailure(Call call, IOException exception) {


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
            public void onFailure(Call call, IOException exception) {


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
            public void onFailure(Call call, IOException exception) {


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
            getComments();
            //1.改变播放按钮的状态
            //2.获取进度时长并显示
            //2.设置定时器更新进度条
            try {

                play.setText(R.string.play_icon_pause);

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
        } else if (event.getEventName().equals("SpeedChanged")) {

            try {
                int speed = mAudioPlayer.getPlaySpeed();
                if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed050) {
                    rate.setText("0.5x");
                } else if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed075) {
                    rate.setText("0.75x");
                } else if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed100) {
                    rate.setText("倍速");
                } else if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed125) {
                    rate.setText("1.25x");
                } else if (AudioPlayerService.PlaySpeed.values()[speed] == AudioPlayerService.PlaySpeed.Speed150) {
                    rate.setText("1.5x");
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

    @OnClick({R.id.play, R.id.previous, R.id.next, R.id.list, R.id.timer, R.id.rate, R.id.writeComment, R.id.commentNumLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.play:
                try {
                    int sta = mAudioPlayer.getPlayerState();
                    if (mAudioPlayer.getPlayerState() == IAliyunVodPlayer.PlayerState.Paused.ordinal()) {
                        mAudioPlayer.start();
                        //play.setBackgroundResource(R.drawable.ic_suspended);
                        play.setText(R.string.play_icon_pause);
                    } else if (mAudioPlayer.getPlayerState() == IAliyunVodPlayer.PlayerState.Started.ordinal()) {
                        mAudioPlayer.pause();
                        //play.setBackgroundResource(R.drawable.ic_play);
                        play.setText(R.string.play_icon_play);
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

            case R.id.writeComment: {

                if (User.getInstance() == null) {
                    startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
                } else {
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
                    scrollView.smoothScrollTo(0, 0);
                } else {
                    mScrollToTop = true;
                    scrollView.smoothScrollTo(0, commentLayout.getTop());
                }

//                }else{
//
//                }


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
                            mAudioInfo.setIsRadio(0);
                            mAudioPlayer.playAudio(mAudioInfo);

                            getAlbumAudios("" + mAudioDetail.getAlbum().get(0).getId());
                        }
                        getRelatedPosts();

                        if (!TextUtils.isEmpty(mAudioDetail.getAlbum().get(0).getCover())) {
                            Uri uri = Uri.parse(mAudioDetail.getAlbum().get(0).getCover());
                            int width = CommonUtils.dip2px(145);
                            int height = width;
                            ImageLoader.showThumb(uri, logo, width, height);
                            width = (int) getResources().getDimension(R.dimen.article_right_image_width);
                            height = width;
                            ImageLoader.showThumb(uri, albumLogo, width, height);
                        }
                        organization.setText(mAudioDetail.getAlbum().get(0).getMerchant().getName());
                        albumTitle.setText(mAudioDetail.getAlbum().get(0).getTitle());
                        anchorName.setText(mAudioDetail.getAuthor_info().getStaff_name());
                        audioNumber.setText(mAudioDetail.getAlbum().get(0).getAlbums().getCount() + "集");
                        audioTitle.setText(mAudioDetail.getTitle());
                        if (mAudioDetail.getAlbum().get(0).getIs_sub() == 1) {
                            subscribe.setText(getResources().getString(R.string.has_subscribe));
                            subscribe.setActivated(false);
                        } else {
                            subscribe.setText(getResources().getString(R.string.subscribe));
                            subscribe.setActivated(true);
                        }
                        subscribe.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (User.getInstance() == null) {
                                    startActivityForResult(new Intent(SingleAudioDetailActivity.this, LoginActivity.class), REQUEST_LOGIN);
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
                        if (mAdapter == null) {
                            //mAdapter = new RecommendContentGridAdapter(this, mPostContents);
                            mAdapter = new RecommendContentAdapter(this, mPostContents);
                            mAdapter.setOnItemClickListener(new RecommendContentAdapter.ItemClickListener() {


                                @Override
                                public void onItemClick(int position) {

                                }

                            });
//                            GridLayoutManager manager = new GridLayoutManager(this, 3);
//                            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                                @Override
//                                public int getSpanSize(int position) {
//                                    if (position == 0 && mAdapter.hasHeadView()) {
//                                        return 3;
//                                    } else {
//                                        return 1;
//                                    }
//                                }
//                            });

                            recommend.setItemAnimator(new DefaultItemAnimator());


                            recommend.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                            DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
                            divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.recycleview_divider_drawable));
                            recommend.addItemDecoration(divider);
                            //设置添加或删除item时的动画，这里使用默认动画


                            //设置适配器
                            recommend.setAdapter(mAdapter);

                        } else {
                            mAdapter.setData(mPostContents);
                            mAdapter.notifyDataSetChanged();

                        }


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
                    if (totalNumber == 0) {
                        commentNum.setText("");
                        noData.setVisibility(View.VISIBLE);
                    } else {
                        commentNum.setText("("+totalNumber+")");
                        noData.setVisibility(View.GONE);
                    }

                    commentNumber.setText("" + totalNumber);

                    mComments = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), Comment.class);

                    if (mCommentListAdapter == null) {
                        mCommentListAdapter = new CommentListAdapter(this, mComments);
                        mCommentListAdapter.setOnPraise(new CommentListAdapter.OnPraise() {
                            @Override
                            public void praiseItem(Comment comment, View v) {
                                if (User.getInstance() != null) {
                                    praise("" + comment.getId(), v);
                                } else {
                                    startActivityForResult(new Intent(SingleAudioDetailActivity.this, LoginActivity.class), REQUEST_LOGIN);
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
                        listView.setAdapter(mCommentListAdapter);
                    } else {
                        mCommentListAdapter.updateView(mComments);
                    }

                }
            }
            break;
            case MSG_DELETE_COMMENT:
                try {

                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show(jsonObject.getString("msg"));
                        getComments();
                    }

                } catch (Exception e) {

                }
                break;
            case MSG_WRITE_COMMENTS: {
                String result = (String) msg.obj;
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                    ToastUtils.show("发表成功");
                    getComments();

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
                            subscribe.setText("已订阅");
                            subscribe.setActivated(false);
                            mAudioDetail.getAlbum().get(0).setIs_sub(1);
                        } else {
                            subscribe.setText("订阅");
                            subscribe.setActivated(true);
                            mAudioDetail.getAlbum().get(0).setIs_sub(0);

                        }


                    }


                } catch (Exception e) {

                }
                break;
        }
        return false;
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
                        if (mAudioPlayer.getPlayerState() == IAliyunVodPlayer.PlayerState.Started.ordinal()) {
                            currentTime.setText(CommonUtils.getShowTime(mAudioPlayer.getCurrentPosition()));
                            seekBar.setProgress((int) (mAudioPlayer.getCurrentPosition()));
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
        FloatWindowUtil.getInstance().showFloatWindow();
        super.onPause();
    }


    @Override
    protected void onResume() {
        FloatWindowUtil.getInstance().hideWindow();
        super.onResume();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK) {
            getAudioDetail(true);
            getComments();
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
                if (SinaWeibo.NAME.equals(platform.getName())) {
                    //限制微博分享的文字不能超过20
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                    paramsToShare.setText(title + url);
                } else if (QQ.NAME.equals(platform.getName())) {
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
}
