package com.shuangling.software.activity;

import android.app.Activity;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.player.IPlayer;
import com.aliyun.player.source.VidAuth;
import com.aliyun.vodplayerview.activity.AliyunPlayerSkinActivity;
import com.aliyun.vodplayerview.utils.ScreenUtils;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
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
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.service.IAudioPlayer;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.FloatWindowUtil;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;
import com.shuangling.software.utils.TimeUtil;

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

public class VideoDetailActivity extends BaseAudioActivity implements Handler.Callback {
    public static final int MSG_GET_VIDEO_DETAIL = 0x00;
    public static final int MSG_COLLECT_CALLBACK = 0x01;
    public static final int MSG_UPDATE_STATUS = 0x02;
    public static final int MSG_GET_RELATED_POST = 0x03;
    public static final int MSG_GET_COMMENTS = 0x04;
    public static final int MSG_PRAISE = 0x05;
    public static final int MSG_WRITE_COMMENTS = 0x06;
    public static final int MSG_ATTENTION_CALLBACK = 0x07;
    public static final int MSG_LIKE_CALLBACK = 0x08;
    public static final int REQUEST_LOGIN = 0x09;
    private static final int SHARE_SUCCESS = 0xa;
    private static final int SHARE_FAILED = 0xb;
    private static final int MSG_DELETE_COMMENT = 0xc;
    private static final int MSG_GET_VIDEO_AUTH = 0xd;
    public static final int REQUEST_REPORT = 0xe;
    @BindView(R.id.aliyunVodPlayerView)
    AliyunVodPlayerView aliyunVodPlayerView;
    @BindView(R.id.organizationLogo)
    SimpleDraweeView organizationLogo;
    @BindView(R.id.organization)
    TextView organization;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.attention)
    TextView attention;
    @BindView(R.id.organizationLayout)
    RelativeLayout organizationLayout;
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
    //    @BindView(R.id.scrollView)
//    NestedScrollView scrollView;
    @BindView(R.id.activity_title)
    /*TopTitleBar*/ QMUITopBarLayout activityTitle;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.noData)
    RelativeLayout noData;
    @BindView(R.id.refresh)
    TextView refresh;
    @BindView(R.id.networkError)
    RelativeLayout networkError;
    @BindView(R.id.root)
    RelativeLayout root;
    private Handler mHandler;
    private int mVideoId;
    private VideoDetail mVideoDetail;
    private List<ColumnContent> mPostContents;
    //    private RecommendContentAdapter mAdapter;
    private List<Comment> mComments = new ArrayList<>();
    //    private CommentListAdapter mCommentListAdapter;
    boolean mScrollToTop = false;
    private DialogFragment mCommentDialog;
    private int mNetPlay;
    private int mNeedTipPlay;
    private boolean mNeedResumeAudioPlay = false;
    private VideoRecyclerAdapter mAdapter;

    private int currentPage = 1;
    private Comment currentComment;
    private ViewSkeletonScreen mViewSkeletonScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setmOnServiceConnectionListener(new OnServiceConnectionListener() {
            @Override
            public void onServiceConnection(IAudioPlayer audioPlayer) {
                try {
                    if (audioPlayer.getPlayerState() == IPlayer.started || audioPlayer.getPlayerState() == IPlayer.paused) {
                        audioPlayer.pause();
                        mNeedResumeAudioPlay = true;
                        FloatWindowUtil.getInstance().hideWindow();
                    }
                } catch (RemoteException e) {
                }
            }
        });
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        ImmersionBar.with(this).statusBarDarkFont(true).fitsSystemWindows(true).init();
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this); //
        activityTitle.addLeftImageButton(R.drawable.ic_left, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(view -> { //
            doOnBackPressed();
        });
        activityTitle.addRightImageButton(R.drawable.ic_more, com.qmuiteam.qmui.R.id.right_icon).setOnClickListener(view -> {//
            if (mVideoDetail != null) {
                String url;
                if (User.getInstance() != null) {
                    url = ServerInfo.h5IP + "/videos/" + mVideoId + "?from_user_id=" + User.getInstance().getId() + "&from_url=" + ServerInfo.h5IP + "/videos/" + mVideoId;
                } else {
                    url = ServerInfo.h5IP + "/videos/" + mVideoId + "?from_url=" + ServerInfo.h5IP + "/videos/" + mVideoId;
                }
                showShareDialog(mVideoDetail.getTitle(), mVideoDetail.getDes(), mVideoDetail.getCover(), url);
                //shareTest();
            }
        });
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
//refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));//设置
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new VideoRecyclerAdapter(this);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.recycleview_divider_drawable));
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(mAdapter);
        mNetPlay = SharedPreferencesUtils.getIntValue(SettingActivity.NET_PLAY, 0);
        mNeedTipPlay = SharedPreferencesUtils.getIntValue(SettingActivity.NEED_TIP_PLAY, 0);
        mHandler = new Handler(this);
        mVideoId = getIntent().getIntExtra("videoId", 0);
        initAliyunPlayerView();
        getVideoDetail();
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                currentPage++;
                getComments(1);
            }
        });
    }

    private void initAliyunPlayerView() {
        //保持屏幕敞亮
        aliyunVodPlayerView.setKeepScreenOn(true);
        aliyunVodPlayerView.getPlayerConfig();
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
                if (mVideoDetail.getVideo() != null) {
                    addPlayTimes(mVideoDetail.getVideo().getPost_id());
                }
            }
        });

        aliyunVodPlayerView.setOnCompletionListener(new IPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {

                //aliyunVodPlayerView.reTry();

            }
        });
//
        if (mNetPlay == 0) {
            //每次提醒
            aliyunVodPlayerView.setShowFlowTip(true);
        } else {
            //提醒一次
            if (mNeedTipPlay == 1) {
                aliyunVodPlayerView.setShowFlowTip(true);
            } else {
                aliyunVodPlayerView.setShowFlowTip(false);
            }
        }
        aliyunVodPlayerView.setOnModifyFlowTipStatus(new AliyunVodPlayerView.OnModifyFlowTipStatus() {
            @Override
            public void onChangeFlowTipStatus() {
                SharedPreferencesUtils.putIntValue(SettingActivity.NEED_TIP_PLAY, 0);
            }
        });
    }

    private void addPlayTimes(int id) {
        String url = ServerInfo.serviceIP + ServerInfo.addPlayTimes + id;
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.put(url, params, new OkHttpCallback(getApplicationContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Log.i("test", response);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    private void getVideoDetail() {
        String url = ServerInfo.serviceIP + ServerInfo.getVideoDetail + mVideoId;
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_GET_VIDEO_DETAIL;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mViewSkeletonScreen.hide();
                        noData.setVisibility(View.GONE);
                        networkError.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void getVideoAuth(int sourceId) {
        String url = ServerInfo.vms + "/v1/sources/" + sourceId + "/playAuth";
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_GET_VIDEO_AUTH;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    private void getRelatedPosts() {
        String url = ServerInfo.serviceIP + ServerInfo.getRelatedRecommend;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", "" + mVideoId);
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

    //type  0 正常   1 加载更多
    private void getComments(final int type) {
        if (type == 0) {
            currentPage = 1;
        }
        String url = ServerInfo.serviceIP + ServerInfo.getComentList;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", "" + mVideoId);
        params.put("page", "" + currentPage);
        params.put("page_size", "" + Constant.PAGE_SIZE);
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_GET_COMMENTS;
                msg.arg1 = type;
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

    public void collect(final boolean collect) {
        String url = ServerInfo.serviceIP + ServerInfo.collect01;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + mVideoId);
        if (collect) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_COLLECT_CALLBACK;
                msg.arg1 = collect ? 1 : 0;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    public void like(final boolean like) {
        String url = ServerInfo.serviceIP + ServerInfo.like;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + mVideoId);
        if (like) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_LIKE_CALLBACK;
                msg.arg1 = like ? 1 : 0;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    public void attention(final boolean follow) {
        String url = ServerInfo.serviceIP + ServerInfo.attention;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + mVideoDetail.getMerchant_id());
        if (follow) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_ATTENTION_CALLBACK;
                msg.arg1 = follow ? 1 : 0;
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
        params.put("post_id", "" + mVideoId);
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

    private void praise(Comment comment, final View view) {
        String url = ServerInfo.serviceIP + ServerInfo.praise;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + comment.getId());
        currentComment = comment;
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

    public void updateStatus() {
        String url = ServerInfo.serviceIP + ServerInfo.getVideoDetail + mVideoId;
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_UPDATE_STATUS;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    private void setPlaySource(String url) {
//        if ("localSource".equals(PlayParameter.PLAY_PARAM_TYPE)) {
//            AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
//            PlayParameter.PLAY_PARAM_URL = url;
//            alsb.setSource(PlayParameter.PLAY_PARAM_URL);
//            Uri uri = Uri.parse(PlayParameter.PLAY_PARAM_URL);
//            if ("rtmp".equals(uri.getScheme())) {
//                alsb.setTitle("");
//            }
//            AliyunLocalSource localSource = alsb.build();
//            aliyunVodPlayerView.setLocalSource(localSource);
////
//
//        }
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
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) aliyunVodPlayerView
                        .getLayoutParams();
                layoutParams.height = (int) (ScreenUtils.getWidth(this) * 9.0f / 16);
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                ViewGroup.LayoutParams lp = aliyunVodPlayerView.getLayoutParams();
                aliyunVodPlayerView.setLayoutParams(lp);
                bottom.setVisibility(View.VISIBLE);
                activityTitle.setVisibility(View.VISIBLE);
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
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) aliyunVodPlayerView
                        .getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                aliyunVodPlayerView.setLayoutParams(layoutParams);
                bottom.setVisibility(View.GONE);
                activityTitle.setVisibility(View.GONE);
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
        if (aliyunVodPlayerView != null) {
            aliyunVodPlayerView.onDestroy();
            aliyunVodPlayerView = null;
        }
    }

//    @Override
//    public void onBackPressed() {
//        if (mNeedResumeAudioPlay) {
//            FloatWindowUtil.getInstance().visibleWindow();
//        }
//        if (aliyunVodPlayerView != null) {
//            aliyunVodPlayerView.onDestroy();
//            aliyunVodPlayerView = null;
//        }
//        super.onBackPressed();
//    }

    @Override
    protected void doOnBackPressed() {
        if (mNeedResumeAudioPlay) {
            FloatWindowUtil.getInstance().visibleWindow();
        }
        if (aliyunVodPlayerView != null) {
            aliyunVodPlayerView.onDestroy();
            aliyunVodPlayerView = null;
        }
        super.doOnBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updatePlayerViewMode();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_VIDEO_DETAIL:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    mViewSkeletonScreen.hide();
                    networkError.setVisibility(View.GONE);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mVideoDetail = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), VideoDetail.class);
                        noData.setVisibility(View.GONE);
                        if (mVideoDetail.getVideo() != null) {
                            getRelatedPosts();
                            getComments(0);
                            getVideoAuth(mVideoDetail.getVideo().getSource_id());
                            if (mVideoDetail.getAuthor_info() != null && mVideoDetail.getAuthor_info().getMerchant() != null) {
                                if (!TextUtils.isEmpty(mVideoDetail.getAuthor_info().getMerchant().getLogo())) {
                                    Uri uri = Uri.parse(mVideoDetail.getAuthor_info().getMerchant().getLogo());
                                    int width = CommonUtils.dip2px(50);
                                    int height = width;
                                    ImageLoader.showThumb(uri, organizationLogo, width, height);
                                }
                                organization.setText(mVideoDetail.getAuthor_info().getMerchant().getName());
                            }
                            organizationLogo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mVideoDetail.getAuthor_info() != null && mVideoDetail.getAuthor_info().getMerchant() != null) {
                                        Intent it = new Intent(VideoDetailActivity.this, WebViewActivity.class);
                                        it.putExtra("url", ServerInfo.h5HttpsIP + "/orgs/" + mVideoDetail.getAuthor_info().getMerchant().getId());
                                        startActivity(it);
                                    }
                                }
                            });
                            time.setText(TimeUtil.formatDateTime(mVideoDetail.getPublish_at()));
                            if (mVideoDetail.getIs_follow() == 1) {
                                attention.setText("已关注");
                                attention.setActivated(false);
                            } else {
                                attention.setText("关注");
                                attention.setActivated(true);
                            }
                            mAdapter.setVideoDetail(mVideoDetail);
                            mAdapter.setOnPraiseVideo(new VideoRecyclerAdapter.OnPraiseVideo() {
                                @Override
                                public void praiseVideo() {
                                    if (User.getInstance() == null) {
                                        Intent it = new Intent(VideoDetailActivity.this, NewLoginActivity.class);
                                        it.putExtra("jump_url", ServerInfo.h5IP + "/videos/" + mVideoId);
                                        startActivityForResult(it, REQUEST_LOGIN);
                                    } else {
                                        if (mVideoDetail.getIs_likes() == 0) {
                                            like(true);
                                        } else {
                                            like(false);
                                        }
                                    }
                                }
                            });
                            mAdapter.setOnCollectVideo(new VideoRecyclerAdapter.OnCollectVideo() {
                                @Override
                                public void collectVideo() {
                                    if (User.getInstance() == null) {
                                        //startActivityForResult(new Intent(VideoDetailActivity.this, NewLoginActivity.class), REQUEST_LOGIN);
                                        Intent it = new Intent(VideoDetailActivity.this, NewLoginActivity.class);
                                        it.putExtra("jump_url", ServerInfo.h5IP + "/videos/" + mVideoId);
                                        startActivityForResult(it, REQUEST_LOGIN);
                                    } else {
                                        if (mVideoDetail.getIs_collection() == 0) {
                                            collect(true);
                                        } else {
                                            collect(false);
                                        }
                                    }
                                }
                            });
//                        videoTitle.setText(mVideoDetail.getTitle());
////                        playTimes.setText(mVideoDetail.getView() + "次播放");
////                        if (mVideoDetail.getIs_likes() == 0) {
////                            praiseIcon.setTextColor(getResources().getColor(R.color.textColorEleven));
////                        } else {
////                            praiseIcon.setTextColor(CommonUtils.getThemeColor(this));
////                        }
////
////                        if (mVideoDetail.getIs_collection() == 0) {
////                            collectIcon.setTextColor(getResources().getColor(R.color.textColorEleven));
////                            collectText.setText("收藏");
////                        } else {
////                            collectIcon.setTextColor(CommonUtils.getThemeColor(this));
////                            collectText.setText("已收藏");
////                        }
//                        setPlaySource(mVideoDetail.getVideo().getUrl());
//                        if (CommonUtils.getNetWorkType(this) == CommonUtils.NETWORKTYPE_MOBILE) {
//                            if(mNetPlay==0){
//                                //每次提醒
//                                flowLayout.setVisibility(View.VISIBLE);
//                                flow.setText("播放将消耗"+DataCleanManager.getFormatSize(mVideoDetail.getVideo().getSize())+"流量,请注意流量使用");
//
//                            }else {
//                                //提醒一次
//                                if(mNeedTipPlay==1){
//                                    flowLayout.setVisibility(View.VISIBLE);
//                                    flow.setText("播放将消耗"+DataCleanManager.getFormatSize(mVideoDetail.getVideo().getSize())+"流量,请注意流量使用");
//                                }else {
//                                    flowLayout.setVisibility(View.GONE);
//                                    setPlaySource(mVideoDetail.getVideo().getUrl());
//                                }
//
//                            }
//                        } else {
//                            flowLayout.setVisibility(View.GONE);
//                            setPlaySource(mVideoDetail.getVideo().getUrl());
//                        }
//setPlaySource(mVideoDetail.getVideo().getUrl());
                            //setPlaySource(DEFAULT_URL);
                        }
                    } else if (jsonObject != null && jsonObject.getIntValue("code") == 403001) {
                        //资源不存在
                        noData.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                }
                break;
            case MSG_GET_RELATED_POST:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mPostContents = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class);
                        mAdapter.setPostContents(mPostContents);
                        mAdapter.setVideoItemClickListener(new VideoRecyclerAdapter.VideoItemClickListener() {
                            @Override
                            public void onItemClick(ColumnContent content) {
                                mVideoId = content.getId();
                                getVideoDetail();
                                getRelatedPosts();
                                getComments(0);
                            }
                        });
//                        if (mAdapter == null) {
//                            mAdapter = new RecommendContentAdapter(this, mPostContents);
//
//                            mAdapter.setOnItemClickListener(new RecommendContentAdapter.ItemClickListener() {
//                                @Override
//                                public void onItemClick(int position) {
//                                    mVideoId = mPostContents.get(position).getId();
//                                    getVideoDetail();
//                                    getRelatedPosts();
//                                    getComments();
//                                }
//                            });
//
//
//                            recommend.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//                            DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
//                            divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.recycleview_divider_drawable));
//                            recommend.addItemDecoration(divider);
//
//                            //设置添加或删除item时的动画，这里使用默认动画
//
//
//                            //设置适配器
//                            recommend.setAdapter(mAdapter);
//
//
//                        } else {
//                            mAdapter.setData(mPostContents);
//                            mAdapter.notifyDataSetChanged();
//
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_COLLECT_CALLBACK:
                try {
                    String result = (String) msg.obj;
                    boolean collect = msg.arg1 == 1 ? true : false;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show(jsonObject.getString("msg"));
                        if (collect) {
                            mVideoDetail.setIs_collection(1);
//                            collectIcon.setTextColor(CommonUtils.getThemeColor(this));
//                            collectText.setText("已收藏");
                        } else {
                            mVideoDetail.setIs_collection(0);
//                            collectIcon.setTextColor(getResources().getColor(R.color.textColorEleven));
//                            collectText.setText("收藏");
                        }
                        mAdapter.setVideoDetail(mVideoDetail);
                    }
                } catch (Exception e) {
                }
                break;
            case MSG_ATTENTION_CALLBACK:
                try {
                    String result = (String) msg.obj;
                    boolean follow = msg.arg1 == 1 ? true : false;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show(jsonObject.getString("msg"));
                        if (follow) {
                            attention.setText("已关注");
                            mVideoDetail.setIs_follow(1);
                            attention.setActivated(false);
                        } else {
                            attention.setText("关注");
                            mVideoDetail.setIs_follow(0);
                            attention.setActivated(true);
                        }
                        mAdapter.setVideoDetail(mVideoDetail);
                    }
                } catch (Exception e) {
                }
                break;
            case MSG_GET_COMMENTS:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        int totalNumber = jsonObject.getJSONObject("data").getInteger("total");
                        //commentNum.setText("(" + totalNumber+")");
                        mAdapter.setTotalComments(totalNumber);
                        commentNumber.setText("" + totalNumber);
                        List<Comment> comments = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), Comment.class);
                        if (msg.arg1 == 0) {
                            mComments = comments;
//                            if(comments==null||comments.size()==0){
//                                refreshLayout.setEnableLoadMore(false);
//                            }else{
//                                refreshLayout.setEnableLoadMore(true);
//                            }
                        } else if (msg.arg1 == 1) {
                            if (refreshLayout.getState() == RefreshState.Loading) {
//                                if(comments==null||comments.size()==0){
//                                    refreshLayout.setEnableLoadMore(false);
//                                    //refreshLayout.finishLoadMoreWithNoMoreData();
//                                }else{
//                                    refreshLayout.finishLoadMore();
//                                }
                                if (comments == null || comments.size() == 0) {
                                    //refreshLayout.setEnableLoadMore(false);
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                } else {
                                    refreshLayout.finishLoadMore();
                                }
                            }
                            mComments.addAll(comments);
                        }
                        if (mComments == null || mComments.size() == 0) {
                            refreshLayout.setEnableLoadMore(false);
                        }
                        mAdapter.setComments(mComments);
                        mAdapter.setOnPraise(new VideoRecyclerAdapter.OnPraise() {
                            @Override
                            public void praiseItem(Comment comment, View view) {
                                if (User.getInstance() != null) {
                                    praise(comment, view);
                                } else {
//                                    Intent it = new Intent(VideoDetailActivity.this, NewLoginActivity.class);
//                                    startActivityForResult(it, REQUEST_LOGIN);
                                    Intent it = new Intent(VideoDetailActivity.this, NewLoginActivity.class);
                                    it.putExtra("jump_url", ServerInfo.h5IP + "/videos/" + mVideoId);
                                    startActivityForResult(it, REQUEST_LOGIN);
                                }
                            }

                            @Override
                            public void deleteItem(final Comment comment, View v) {
//                                new CircleDialog.Builder()
//                                        .setCanceledOnTouchOutside(false)
//                                        .setCancelable(false)
//                                        .setText("确认删除此评论?")
//                                        .setNegative("取消", null)
//                                        .setPositive("确定", new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                deleteComment(comment);
//                                            }
//                                        })
//                                        .show(getSupportFragmentManager());

                                new QMUIDialog.MessageDialogBuilder(VideoDetailActivity.this)
                                        //.setTitle("提示")
                                        .setMessage("确认删除此评论？")
                                        .setCanceledOnTouchOutside(false)
                                        .setCancelable(false)
                                        .setSkinManager(QMUISkinManager.defaultInstance(VideoDetailActivity.this))
                                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                                            @Override
                                            public void onClick(QMUIDialog dialog, int index) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                                            @Override
                                            public void onClick(QMUIDialog dialog, int index) {
                                                deleteComment(comment);
                                                dialog.dismiss();
                                            }
                                        })
                                        .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
                            }
                        });
                        mAdapter.setOnItemReply(new VideoRecyclerAdapter.OnItemReply() {
                            @Override
                            public void replyItem(final Comment comment) {
                                if (User.getInstance() == null) {
                                    Intent it = new Intent(VideoDetailActivity.this, NewLoginActivity.class);
                                    it.putExtra("bindPhone", true);
                                    it.putExtra("jump_url", ServerInfo.h5IP + "/videos/" + mVideoId);
                                    startActivityForResult(it, REQUEST_LOGIN);
                                } else if (User.getInstance() != null && TextUtils.isEmpty(User.getInstance().getPhone())) {
                                    Intent it = new Intent(VideoDetailActivity.this, BindPhoneActivity.class);
                                    //it.putExtra("hasLogined",true);
                                    startActivity(it);
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
                                            .configPositive(new ConfigButton() {
                                                @Override
                                                public void onConfig(ButtonParams params) {
                                                    //按钮字体颜色
                                                    params.textColor = CommonUtils.getThemeColor(VideoDetailActivity.this);
                                                }
                                            })
                                            .setPositiveInput("发表", new OnInputClickListener() {
                                                @Override
                                                public void onClick(String text, View v) {
                                                    if (TextUtils.isEmpty(text)) {
                                                        ToastUtils.show("请输入内容");
                                                    } else {
                                                        CommonUtils.closeInputMethod(VideoDetailActivity.this);
                                                        //发送评论
                                                        writeComments(text, "" + comment.getId(), "" + comment.getId());
                                                        mCommentDialog.dismiss();
                                                    }
                                                }
                                            })
                                            .show(getSupportFragmentManager());
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
            case MSG_UPDATE_STATUS:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mVideoDetail = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), VideoDetail.class);
                        if (mVideoDetail.getAuthor_info() != null && mVideoDetail.getAuthor_info().getMerchant() != null) {
                            if (!TextUtils.isEmpty(mVideoDetail.getAuthor_info().getMerchant().getLogo())) {
                                Uri uri = Uri.parse(mVideoDetail.getAuthor_info().getMerchant().getLogo());
                                int width = CommonUtils.dip2px(50);
                                int height = width;
                                ImageLoader.showThumb(uri, organizationLogo, width, height);
                            }
                            organization.setText(mVideoDetail.getAuthor_info().getMerchant().getName());
                        }
                        time.setText(mVideoDetail.getPublish_at());
                        if (mVideoDetail.getIs_follow() == 1) {
                            attention.setText("已关注");
                            attention.setActivated(false);
                        } else {
                            attention.setText("关注");
                            attention.setActivated(true);
                        }
//                        videoTitle.setText(mVideoDetail.getTitle());
//                        playTimes.setText(mVideoDetail.getView() + "次播放");
//                        if (mVideoDetail.getIs_likes() == 0) {
//                            praiseIcon.setTextColor(getResources().getColor(R.color.textColorEleven));
//                        } else {
//                            praiseIcon.setTextColor(CommonUtils.getThemeColor(this));
//                        }
//
//                        if (mVideoDetail.getIs_collection() == 0) {
//                            collectIcon.setTextColor(getResources().getColor(R.color.textColorEleven));
//                        } else {
//                            collectIcon.setTextColor(CommonUtils.getThemeColor(this));
//                        }
                        mAdapter.setVideoDetail(mVideoDetail);
                    }
                } catch (Exception e) {
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
                            int num = Integer.parseInt(textView.getText().toString()) - 1;
                            currentComment.setLike_count(num);
                            currentComment.setFabulous(0);
                            textView.setText("" + num);
                        } else {
                            //点赞成功
                            textView.setActivated(false);
                            int num = Integer.parseInt(textView.getText().toString()) + 1;
                            textView.setText("" + num);
                            currentComment.setLike_count(num);
                            currentComment.setFabulous(1);
                        }
                        //getComments();
                    }
                } catch (Exception e) {
                }
                break;
            case MSG_WRITE_COMMENTS:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show("发表成功");
                        getComments(0);
                    }
                } catch (Exception e) {
                }
                break;
            case MSG_LIKE_CALLBACK:
                try {
                    String result = (String) msg.obj;
                    boolean like = msg.arg1 == 1 ? true : false;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show(jsonObject.getString("msg"));
                        if (like) {
                            //praiseIcon.setTextColor(CommonUtils.getThemeColor(this));
                            mVideoDetail.setIs_likes(1);
                            mVideoDetail.setLike(mVideoDetail.getLike() + 1);
                        } else {
                            //praiseIcon.setTextColor(getResources().getColor(R.color.textColorEleven));
                            mVideoDetail.setIs_likes(0);
                            mVideoDetail.setLike(mVideoDetail.getLike() - 1);
                        }
                        mAdapter.setVideoDetail(mVideoDetail);
                    }
                } catch (Exception e) {
                }
                break;
            case MSG_GET_VIDEO_AUTH:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ResAuthInfo resAuthInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), ResAuthInfo.class);
                        VidAuth vidAuth = new VidAuth();
                        vidAuth.setPlayAuth(resAuthInfo.getPlay_auth());
                        vidAuth.setVid(resAuthInfo.getVideo_id());
                        vidAuth.setRegion("cn-shanghai");
                        aliyunVodPlayerView.setAuthInfo(vidAuth);
                    } else if (jsonObject != null) {
                        //ToastUtils.show(jsonObject.getString("msg"));
                        ToastUtils.show("播放失败");

                    }
                } catch (Exception e) {
                }
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN && resultCode == RESULT_OK) {
            updateStatus();
            getComments(0);
        } else if (requestCode == REQUEST_REPORT && resultCode == Activity.RESULT_OK) {
            if (mVideoDetail != null) {
                mVideoDetail.setIs_user_report(1);
            }
        }
    }

    @OnClick({R.id.attention, R.id.writeComment, R.id.commentNumLayout, R.id.refresh})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.attention:
                if (User.getInstance() == null) {
                    //startActivityForResult(new Intent(this, NewLoginActivity.class), REQUEST_LOGIN);
                    Intent it = new Intent(VideoDetailActivity.this, NewLoginActivity.class);
                    it.putExtra("jump_url", ServerInfo.h5IP + "/videos/" + mVideoId);
                    startActivityForResult(it, REQUEST_LOGIN);
                } else {
                    if (mVideoDetail != null && mVideoDetail.getIs_follow() == 0) {
                        attention(true);
                    } else if (mVideoDetail != null) {
                        attention(false);
                    }
                }
                break;
//            case R.id.praiseLayout:
//                if (User.getInstance() == null) {
//                    startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
//                } else {
//                    if (mVideoDetail.getIs_likes() == 0) {
//                        like(true);
//                    } else {
//                        like(false);
//                    }
//                }
//
//                break;
//            case R.id.collectLayout:
//
//                if (User.getInstance() == null) {
//                    startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
//                } else {
//                    if (mVideoDetail.getIs_collection() == 0) {
//                        collect(true);
//                    } else {
//                        collect(false);
//                    }
//                }
//                break;
            case R.id.writeComment:
//                if (User.getInstance() == null) {
//                    Intent it = new Intent(this, LoginActivity.class);
//                    startActivityForResult(it, REQUEST_LOGIN);
//                }
                if (mVideoDetail == null) {
                    return;
                }
                if (User.getInstance() == null) {
//                    Intent it = new Intent(this, NewLoginActivity.class);
//                    startActivityForResult(it, REQUEST_LOGIN);
                    Intent it = new Intent(VideoDetailActivity.this, NewLoginActivity.class);
                    it.putExtra("jump_url", ServerInfo.h5IP + "/videos/" + mVideoId);
                    startActivityForResult(it, REQUEST_LOGIN);
                } else if (User.getInstance() != null && TextUtils.isEmpty(User.getInstance().getPhone())) {
                    Intent it = new Intent(this, BindPhoneActivity.class);
                    //it.putExtra("hasLogined",true);
                    startActivity(it);
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
                            .configPositive(new ConfigButton() {
                                @Override
                                public void onConfig(ButtonParams params) {
                                    //按钮字体颜色
                                    params.textColor = CommonUtils.getThemeColor(VideoDetailActivity.this);
                                }
                            })
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
                break;
            case R.id.commentNumLayout:
                if (mScrollToTop) {
                    mScrollToTop = false;
                    //scrollView.smoothScrollTo(0, 0);
                    recyclerView.smoothScrollToPosition(0);
                } else {
                    mScrollToTop = true;
                    //scrollView.smoothScrollTo(0, commentLayout.getTop());
//                    recyclerView.smoothScrollToPosition(mPostContents!=null?2+mPostContents.size():2);
                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    llm.scrollToPositionWithOffset(mPostContents != null ? 1 + mPostContents.size() : 1, 0);
                    llm.setStackFromEnd(false);
                }
                break;
            case R.id.refresh:
                getVideoDetail();
                break;
        }
    }

    private void showShareDialog(final String title, final String desc, final String logo, final String url) {
        ShareDialog dialog = ShareDialog.getInstance(false, mVideoDetail.getIs_user_report() == 0 ? false : true);
        dialog.setIsHideSecondGroup(false);
        dialog.setIsShowPosterButton(false);
        dialog.setIsShowReport(true);
        dialog.setIsShowCollect(false);
        dialog.setIsShowCopyLink(false);
        dialog.setIsShowFontSize(false);
        dialog.setIsShowRefresh(false);
        dialog.setShareHandler(new ShareDialog.ShareHandler() {
            @Override
            public void onShare(String platform) {
                showShare(platform, title, desc, logo, url);
            }

            @Override
            public void poster() {
            }

            @Override
            public void report() {
                if (User.getInstance() == null) {
                    Intent it = new Intent(VideoDetailActivity.this, NewLoginActivity.class);
                    startActivityForResult(it, REQUEST_LOGIN);
                } else {
                    Intent it = new Intent(VideoDetailActivity.this, ReportActivity.class);
                    it.putExtra("id", "" + mVideoDetail.getId());
                    startActivityForResult(it, REQUEST_REPORT);
                }
            }

            @Override
            public void copyLink() {
//获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData clipData = ClipData.newPlainText("Label", url);
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(clipData);
                ToastUtils.show("复制成功，可以发给朋友们了。");
            }

            @Override
            public void refresh() {
            }

            @Override
            public void collectContent() {
            }
        });
        dialog.show(getSupportFragmentManager(), "ShareDialog");
    }

    private void showShare(String platform, final String title, final String desc, final String logo, final String url) {
        final String cover;
        if (logo.startsWith("http://")) {
            cover = logo.replace("http://", "https://");
        } else {
            cover = logo;
        }
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setPlatform(platform);
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
                shareStatistics(chanel, "" + mVideoDetail.getId(), ServerInfo.h5IP + ServerInfo.getArticlePage + mVideoDetail.getId() + "?app=android");
            }
        });
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
            }
        });
        // 启动分享GUI
        oks.show(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //解决某些手机上锁屏之后会出现标题栏的问题。
        updatePlayerViewMode();
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
