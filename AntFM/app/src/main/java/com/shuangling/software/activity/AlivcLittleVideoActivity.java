package com.shuangling.software.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.apsara.alivclittlevideo.view.video.AlivcVideoPlayView;
import com.aliyun.apsara.alivclittlevideo.view.video.BaseVideoSourceModel;
import com.aliyun.apsara.alivclittlevideo.view.video.LittleVideoListAdapter;
import com.aliyun.apsara.alivclittlevideo.view.video.videolist.AlivcVideoListView;
import com.aliyun.player.source.VidSts;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.dialog.CommentDialog;
import com.shuangling.software.dialog.ShareDialog;
import com.shuangling.software.dialog.SmallVideoCommentContentBottomDialog;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.SmallVideoBean;
import com.shuangling.software.entity.StsInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.entity.VideoDetail;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.NumberUtil;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

public class AlivcLittleVideoActivity extends AppCompatActivity {
    public static final int REQUEST_LOGIN = 0x1;
    public static final int REQUEST_REPORT = 0xe;
    public static final int PAN_DOWN = 1;
    public static final int PAN_UP = 2;
    public static final int PAN_LEFT = 3;
    public static final int PAN_RIGHT = 4;
    //跳转进入短视频详情的几种方式
    public static final int START_TYPE_NORMAL = 1; //短视频列表点击进入
    public static final int START_TYPE_H5_SCHEME = 2;   //通过H5网页跳转 APP
    public static final int START_TYPE_H5_WEBVIEW = 3;       //通过WebView h5网页调起，关联视频
    public static final int START_TYPE_H5_WEBVIEW_CURRENT = 4;       //通过WebView h5网页调起,当前视频
    private int mStartType = 1;//调用方式
    private AlivcVideoPlayView videoPlayView;
    private Column mColumn;
    private List<ColumnContent> mColumnContents = new ArrayList<>();
    private int mPageIndex = 1;     //当前请求的页
    private int mVideoPosition = 0; //当前视频播放的位置
    private List<SmallVideoBean> mDatas;
    private int mRequestCount = 0;
    private StsInfo mStsInfo = null;
    //是否获取了
    private int mIsRelatedPostsGot = 0;  // -1 请求失败， 0 未请求 1 请求成功
    private int mIsVideoDetailGot = 0;  // -1 请求失败， 0 未请求 1 请求成功
    /*---------- H5 Scheme 调用 App -----------*/
    private int mOrignalId; //原视频的id
    private int mPlayId;  //当前播放的视频id
    float oldX = 0;
    float currentX = 0;
    float oldY = 0;
    float currentY = 0;
    int mLastVideoPosition = 0;
    Boolean mCancelVerticalMove = false;
    /**
     * 是否重新加载数据
     */
    private boolean isRefreshData = true;
    //评论框
    SmallVideoCommentContentBottomDialog mSmallVideoCommentContentBottomDialog;
    CommentDialog mCommentDialog;

    HashMap<Integer, Long> lastClickTimeMap = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alivc_little_activity_video_detail);
        initView();
        onLoad();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        isRefreshData = true;
        onLoad();
    }

    private void onLoad() {
        mStartType = getIntent().getIntExtra("startType", 1);
        if (mStartType == START_TYPE_NORMAL) {
            //普通模式调用
            mColumnContents.addAll((List<ColumnContent>) getIntent().getSerializableExtra("littleVideos"));
            mVideoPosition = getIntent().getIntExtra("position", 0);
            mColumn = (Column) getIntent().getSerializableExtra("Column");
        } else if (mStartType == START_TYPE_H5_SCHEME) {
            //H5 Scheme 调用
            mOrignalId = getIntent().getIntExtra("original_id", 0);
            mPlayId = getIntent().getIntExtra("play_id", 0);
        } else if (mStartType == START_TYPE_H5_WEBVIEW_CURRENT) {
            //H5 Scheme 调用
            mOrignalId = getIntent().getIntExtra("original_id", 0);
            mPlayId = getIntent().getIntExtra("play_id", 0);
        } else if (mStartType == START_TYPE_H5_WEBVIEW) {
            //普通模式调用
            mColumnContents.addAll((List<ColumnContent>) getIntent().getSerializableExtra("littleVideos"));
            mVideoPosition = getIntent().getIntExtra("position", 0);
        }
        requestData();
    }

    protected void initView() {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        videoPlayView = findViewById(R.id.video_play_detail_view);
        findViewById(R.id.fl_video_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        videoPlayView.setOnRefreshDataListener(new MyRefreshDataListener(this));
        videoPlayView.setOnItemBtnClick(new LittleVideoListAdapter.OnItemBtnClick() {
            @Override
            public void onDownloadClick(int position) {
            }

            @Override
            public void onPraiseClick(int position) {
                if (User.getInstance() == null) {
                    startActivityForResult(new Intent(AlivcLittleVideoActivity.this, NewLoginActivity.class), REQUEST_LOGIN);
                    return;
                }
                ColumnContent columnContent = mColumnContents.get(position);
                LittleVideoListAdapter.MyHolder holder = videoPlayView.getPlayPager();
                if (holder != null) {


                    String likes = holder.getmTvLikes().getText() + "";
                    int nLikes = 0;
                    if (!likes.equals("点赞")) {
                        nLikes = Integer.parseInt(likes);
                    }
                    Long lastClickTime = lastClickTimeMap.get(position);
                    if(lastClickTime == null || System.currentTimeMillis() - lastClickTime> 1000) { // 防抖
                        if (columnContent.getIs_like() == 1) {
                            holder.getmFivPrase().setTextColor(Color.parseColor("#ffffff"));
                            praise(position, false);
                            nLikes--;
                            if (nLikes <= 0) {
                                holder.getmTvLikes().setText("点赞");
                            } else {
                                holder.getmTvLikes().setText("" + nLikes);
                            }
                            columnContent.setIs_like(0);
                        } else {
                            holder.getmFivPrase().setTextColor(Color.parseColor("#F54C68"));
                            praise(position, true);
                            nLikes++;
                            holder.getmTvLikes().setText("" + nLikes);
                            columnContent.setIs_like(1);
                        }

                    }else{
                        if(columnContent.getIs_like() == 1){
                            int[] itemPosition = new int[2];
                            int[] superLikePosition = new int[2];
                            holder.getmFivPrase().getLocationOnScreen(itemPosition);
                            holder.getSuperLikeLayout().getLocationOnScreen(superLikePosition);
                            int x = itemPosition[0] + holder.getmFivPrase().getWidth() / 2;
                            int y = (itemPosition[1] - superLikePosition[1]) + holder.getmFivPrase().getHeight() / 2;
                            holder.getSuperLikeLayout().launch(x, y);
                        }else{
                            holder.getmFivPrase().setTextColor(Color.parseColor("#F54C68"));
                            praise(position, true);
                            nLikes++;
                            holder.getmTvLikes().setText("" + nLikes);
                            columnContent.setIs_like(1);
                        }
                    }
                    lastClickTimeMap.put(position, System.currentTimeMillis());




                }
            }

            @Override
            public void onShareClick(int position) {
                ColumnContent columnContent = mColumnContents.get(position);
                //http://www-cms-c.review.slradio.cn/share
                if (columnContent.getVideo() != null) {
                    String url = "";
                    if (User.getInstance() != null) {
                        url = ServerInfo.h5IP + "/share/" + columnContent.getVideo().getPost_id() + "?from_user_id=" + User.getInstance().getId();
                    } else {
                        url = ServerInfo.h5IP + "/share/" + columnContent.getVideo().getPost_id() + "?from_url=" + ServerInfo.h5IP + "/share/" + columnContent.getVideo().getPost_id();
                    }
                    Log.d("Share", url);
                    showShareDialog(columnContent.getTitle(), columnContent.getDes(), columnContent.getCover(), url, columnContent.getIs_user_report());
                }
            }

            //关闭按钮
            @Override
            public void onCloseClick(int position) {
                out();
            }

            //查看评论
            @Override
            public void onCommentClick(int position) {
                onCommentButtonClick();
            }

            //发评论
            @Override
            public void onSendCommentClick(int position) {
                onCommentAndInputClick();
            }

            //点击关注/已关注
            @Override
            public void onSendAttentionClick(int position) {
                if (User.getInstance() == null) {
                    startActivityForResult(new Intent(AlivcLittleVideoActivity.this, NewLoginActivity.class), REQUEST_LOGIN);
                    return;
                }
                LittleVideoListAdapter.MyHolder holder = videoPlayView.getPlayPager();
                if (holder != null) {
                    if (holder.getmTvAttention().getText() == "已关注") {
                        holder.getmTvAttention().setText("关注");
                        holder.getmTvAttention().setTextColor(Color.parseColor("#FFFFFF"));
                        holder.getmTvAttention().setSelected(true);
                        attention(position, false);
                    } else {
                        holder.getmTvAttention().setText("已关注");
                        holder.getmTvAttention().setTextColor(Color.parseColor("#999999"));
                        holder.getmTvAttention().setSelected(false);
                        attention(position, true);
                    }
                }
            }

            @Override
            public void onHeadClick(int position) {
                ColumnContent columnContent = mColumnContents.get(position);
                if (columnContent.getAuthor_info() != null && columnContent.getAuthor_info().getMerchant() != null) {
                    Intent it = new Intent(AlivcLittleVideoActivity.this, WebViewActivity.class);
                    it.putExtra("url", ServerInfo.h5HttpsIP + "/orgs/" + columnContent.getAuthor_info().getMerchant().getId());
                    startActivity(it);
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
            //getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
            //getWindow().setNavigationBarColor(Color.BLUE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayView.onPause();
    }

    @Override
    protected void onDestroy() {
        if (videoPlayView != null) {
            videoPlayView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        out();
        //super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN && resultCode == RESULT_OK) {
            getVideoDetail(mVideoPosition);
        } else if (requestCode == REQUEST_REPORT && resultCode == Activity.RESULT_OK) {
            ColumnContent columnContent = mColumnContents.get(mVideoPosition);
            columnContent.setIs_user_report(1);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Boolean result;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldX = ev.getX();// 点击时的坐标
                oldY = ev.getY();
                mLastVideoPosition = mVideoPosition;
                mCancelVerticalMove = false;
                break;
            case MotionEvent.ACTION_UP:
                currentX = ev.getX();// 抬起时的坐标
                currentY = ev.getY();
                if (Math.abs(currentX - oldX) > 50) {
                    mCancelVerticalMove = true;
                }
                handleTouch();
                break;
            case MotionEvent.ACTION_MOVE:
//                index = ev.findPointerIndex(mActivePointerId);
//                int secondaryIndex = MotionEventCompat.findPointerIndex(event,mSecondaryPointerId);
//                final float x = MotionEventCompat.getX(event,index);
//                final float y = MotionEventCompat.getY(event,index);
//                final float secondX = MotionEventCompat.getX(event,secondaryIndex);
//                final float secondY = MotionEventCompat.getY(event,secondaryIndex);
                currentX = ev.getX();//移动时操过200
                currentY = ev.getY();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private static class MyRefreshDataListener implements AlivcVideoListView.OnRefreshDataListener {
        WeakReference<AlivcLittleVideoActivity> weakReference;

        MyRefreshDataListener(AlivcLittleVideoActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onRefresh() {
            Log.e("AlivcLittlePlayer", "onRefresh");
            if (weakReference == null) {
                return;
            }
            AlivcLittleVideoActivity activity = weakReference.get();
            if (activity != null) {
                activity.isRefreshData = true;
            }
        }

        @Override
        public void onLoadMore() {
            Log.e("AlivcLittlePlayer", "onLoadMore");
            if (weakReference == null) {
                return;
            }
            AlivcLittleVideoActivity activity = weakReference.get();
            if (activity != null) {
                if (activity.mStartType == AlivcLittleVideoActivity.START_TYPE_NORMAL) {
                    activity.getMoreContent();
                    activity.isRefreshData = false;
                }
            }
        }

        @Override
        public void onExitVideo() {
            AlivcLittleVideoActivity activity = weakReference.get();
            if (activity != null) {
                // activity.willExitVideo();
            }
        }

        @Override
        public void onSTSExpired() {
            AlivcLittleVideoActivity activity = weakReference.get();
            if (activity != null) {
                activity.getSts();
            }
        }

        @Override
        public void onPlayAtPosition(int position) {
            AlivcLittleVideoActivity activity = weakReference.get();
            if (activity != null) {
                activity.onPlayAtPosition(position);
            }
        }
    }

    //<editor-fold desc="数据请求相关">
    //请求数据
    private void requestData() {
        if (mStartType == START_TYPE_NORMAL) {
            getSts();
        } else if (mStartType == START_TYPE_H5_SCHEME) {
            getSts();
            getRelatedPosts();
        } else if (mStartType == START_TYPE_H5_WEBVIEW) {
            getSts();
        } else if (mStartType == START_TYPE_H5_WEBVIEW_CURRENT) {
            getSts();
            getVideoDetail(mOrignalId + "");
        }
    }

    //处理请求结果
    private void handleRequestFinish() {
        if (mStartType == START_TYPE_NORMAL) {
            if (mStsInfo != null) {
                setData(mColumnContents);
                getVideoDetail(mVideoPosition);
            }
        } else if (mStartType == START_TYPE_H5_SCHEME) {
            if (mStsInfo != null && mIsRelatedPostsGot != 0) {
                mVideoPosition = getThePosition();
                setData(mColumnContents);
                getVideoDetail(mVideoPosition);
            }
        } else if (mStartType == START_TYPE_H5_WEBVIEW) {
            if (mStsInfo != null) {
                setData(mColumnContents);
                getVideoDetail(mVideoPosition);
            }
        } else if (mStartType == START_TYPE_H5_WEBVIEW_CURRENT) {
            if (mStsInfo != null && mIsVideoDetailGot != 0) {
                setData(mColumnContents);
            }
        }
    }

    //获取当前播放视频所在的位置
    private int getThePosition() {
        int position = 0;
        for (int i = 0; i < mColumnContents.size(); i++) {
            ColumnContent columnContent = mColumnContents.get(i);
            if (mPlayId == columnContent.getId()) {
                position = i;
                break;
            }
        }
        return position;
    }

    //获取Sts授权
    public void getSts() {
        //String url = "http://api-vms.review.slradio.cn" + ServerInfo.ossSts  ;
        String url = ServerInfo.vms + ServerInfo.ossSts;
        Map<String, String> params = new HashMap<String, String>();
        OkHttpUtils.get(url, params, new OkHttpCallback(AlivcLittleVideoActivity.this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mStsInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), StsInfo.class);
                        com.aliyun.player.source.StsInfo stsInfo = new com.aliyun.player.source.StsInfo();
                        stsInfo.setAccessKeyId(mStsInfo.getAccessKeyId());
                        stsInfo.setAccessKeySecret(mStsInfo.getAccessKeySecret());
                        stsInfo.setRegion("cn-shanghai");
                        stsInfo.setSecurityToken(mStsInfo.getSecurityToken());
                        videoPlayView.setStsInfo(stsInfo);
                        handleRequestFinish();
                    } else if (jsonObject != null) {
                        ToastUtils.show(jsonObject.getString("msg"));
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                Log.d("OSS", exception.getLocalizedMessage());
            }
        });
    }

    //获取更多数据
    public void getMoreContent() {
        isRefreshData = false;
        String url = ServerInfo.serviceIP + ServerInfo.getColumnContent + mColumn.getId();
        Map<String, String> params = new HashMap<String, String>();
        params.put("limit", "" + Constant.PAGE_SIZE);
        params.put("sorce_type", "0");
        params.put("operation", "down");
        params.put("publish_at", mColumnContents.size() > 0 ? mColumnContents.get(mColumnContents.size() - 1).getPublish_at() : "");
        //params.put("mobile_source", "app");
        params.put("order_by", "1");
        OkHttpUtils.get(url, params, new OkHttpCallback(AlivcLittleVideoActivity.this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                try {
                    String result = response;
                    Log.d("content", result);
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        List<ColumnContent> columnContents = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class);
                        Iterator<ColumnContent> iterator = columnContents.iterator();
                        while (iterator.hasNext()) {
                            ColumnContent columnContent = iterator.next();
                            if (columnContent.getType() != 12) {
                                iterator.remove();
                            }
                        }
                        if (columnContents.size() < 10) {
                            //没有更多
                        }
                        setData(columnContents);
                        mColumnContents.addAll(columnContents);
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    //获取关联的数据
    private void getRelatedPosts() {
        String url = ServerInfo.serviceIP + ServerInfo.getRelatedRecommend;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", "" + mOrignalId);
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                mIsRelatedPostsGot = -1;
                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        List<ColumnContent> datas = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class);
                        mColumnContents.addAll(datas);
                        mIsRelatedPostsGot = 1;
                    }
                    handleRequestFinish();
                } catch (Exception e) {
                    e.printStackTrace();
                    handleRequestFinish();
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    private void setData(List<ColumnContent> columnContents) {
        mDatas = new ArrayList<SmallVideoBean>();
        for (int i = 0; i < columnContents.size(); i++) {
            ColumnContent columnContent = columnContents.get(i);
            if (columnContent.getVideo() != null && mStsInfo != null) {
                VidSts vidSts = new VidSts();
                vidSts.setVid(columnContent.getVideo().getVideo_id());
                vidSts.setAccessKeyId(mStsInfo.getAccessKeyId());
                vidSts.setAccessKeySecret(mStsInfo.getAccessKeySecret());
                vidSts.setSecurityToken(mStsInfo.getSecurityToken());
                vidSts.setRegion("cn-shanghai");
                SmallVideoBean data = new SmallVideoBean();
                data.setSourceId(columnContent.getId() + "");
                data.setmVidSts(vidSts);
                data.setIs_like(columnContent.getIs_like());
                data.setTitle(columnContent.getTitle());
                data.setView(columnContent.getView());
                data.setComment(columnContent.getComment());
                if (columnContent.getAuthor_info() != null && columnContent.getAuthor_info().getMerchant() != null) {
                    BaseVideoSourceModel.UserBean userBean = new BaseVideoSourceModel.UserBean();
                    userBean.setAvatarUrl(columnContent.getAuthor_info().getMerchant().getLogo());
                    userBean.setUserName(columnContent.getAuthor_info().getMerchant().getName());
                    userBean.setType(columnContent.getAuthor_info().getMerchant().getType());
                    data.setUser(userBean);
                }
                data.setCoverUrl(columnContent.getCover());
                data.setTitle(columnContent.getTitle());
                mDatas.add(data);
            }
        }
        AlivcLittleVideoActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRefreshData) {
                    videoPlayView.refreshVideoList(mDatas, mVideoPosition);
                } else {
                    videoPlayView.addMoreData(mDatas);
                }
            }
        });
    }

    //通过获取视频详情来获取点赞数，是否关注，
    private void getVideoDetail(int position) {
        if (position >= mColumnContents.size()) {
            return;
        }
        ColumnContent columnContent = mColumnContents.get(position);
        addPlayTimes(columnContent.getId());
        String url = ServerInfo.serviceIP + ServerInfo.getVideoDetail + columnContent.getId();
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        final VideoDetail videoDetail = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), VideoDetail.class);
//是否关注
                        videoDetail.getIs_follow();
                        //点赞数
                        videoDetail.getLike();
                        AlivcLittleVideoActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LittleVideoListAdapter.MyHolder holder = videoPlayView.getPlayPager();
                                if (holder != null) {
                                    if (videoDetail.getIs_follow() == 1) {
                                        holder.getmTvAttention().setText("已关注");
                                        holder.getmTvAttention().setSelected(false);
                                        holder.getmTvAttention().setTextColor(Color.parseColor("#999999"));
                                        holder.getmTvAttention().setVisibility(View.VISIBLE);
                                    } else {
                                        holder.getmTvAttention().setText("关注");
                                        holder.getmTvAttention().setSelected(true);
                                        holder.getmTvAttention().setTextColor(Color.parseColor("#FFFFFF"));
                                        holder.getmTvAttention().setVisibility(View.VISIBLE);
                                    }
                                    if (videoDetail.getIs_likes() == 1) {
                                        holder.getmFivPrase().setTextColor(Color.parseColor("#F54C68"));
                                    } else {
                                        holder.getmFivPrase().setTextColor(Color.parseColor("#ffffff"));
                                    }
                                    ColumnContent columnContent = mColumnContents.get(mVideoPosition);
                                    if (videoDetail.getComment() > columnContent.getComment()) {
                                        columnContent.setComment(videoDetail.getComment());
                                    } else {
                                        videoDetail.setComment(columnContent.getComment());
                                    }
                                    if (videoDetail.getComment() > 0) {
                                        holder.getmTvComment().setText("" + videoDetail.getComment());
                                    } else {
                                        holder.getmTvComment().setText("点评");
                                    }
                                    if (videoDetail.getLike() > 0) {
                                        holder.getmTvLikes().setText(NumberUtil.formatNum("" + videoDetail.getLike(), false));
                                    } else {
                                        holder.getmTvLikes().setText("点赞");
                                    }
                                    columnContent.setIs_user_report(videoDetail.getIs_user_report());
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    private void getVideoDetail(String videoId) {
        String url = ServerInfo.serviceIP + ServerInfo.getVideoDetail + videoId;
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                mIsVideoDetailGot = -1;
                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        final ColumnContent columnContent = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), ColumnContent.class);
                        mColumnContents.add(columnContent);
                        mIsVideoDetailGot = 1;
                        handleRequestFinish();
                        final VideoDetail videoDetail = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), VideoDetail.class);
//是否关注
                        videoDetail.getIs_follow();
                        //点赞数
                        videoDetail.getLike();
                        AlivcLittleVideoActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LittleVideoListAdapter.MyHolder holder = videoPlayView.getPlayPager();
                                if (holder != null) {
                                    if (videoDetail.getIs_follow() == 1) {
                                        holder.getmTvAttention().setText("已关注");
                                        holder.getmTvAttention().setSelected(false);
                                        holder.getmTvAttention().setTextColor(Color.parseColor("#999999"));
                                        holder.getmTvAttention().setVisibility(View.VISIBLE);
                                    } else {
                                        holder.getmTvAttention().setText("关注");
                                        holder.getmTvAttention().setSelected(true);
                                        holder.getmTvAttention().setTextColor(Color.parseColor("#FFFFFF"));
                                        holder.getmTvAttention().setVisibility(View.VISIBLE);
                                    }
                                    if (videoDetail.getIs_likes() == 1) {
                                        holder.getmFivPrase().setTextColor(Color.parseColor("#F54C68"));
                                    } else {
                                        holder.getmFivPrase().setTextColor(Color.parseColor("#ffffff"));
                                    }
                                    holder.getmTvLikes().setText("" + videoDetail.getLike());
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    //关注，取消关注
    public void attention(int index, final boolean follow) {
        ColumnContent columnContent = mColumnContents.get(index);
        if (columnContent.getAuthor_info() == null) {
            ToastUtils.show("无法获取机构信息！");
            return;
        }
        String url = ServerInfo.serviceIP + ServerInfo.attention;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + columnContent.getAuthor_info().getMerchant_id());
        if (follow) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    public void praise(int index, final boolean like) {
        ColumnContent columnContent = mColumnContents.get(index);
        if (columnContent.getAuthor_info() == null) {
            ToastUtils.show("无法获取机构信息！");
            return;
        }
        String url = ServerInfo.serviceIP + ServerInfo.like;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + columnContent.getId());
        if (like) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Log.d("SmallVideo", response);
//            Message msg = Message.obtain();
//            msg.what = MSG_PRAISE;
//            Bundle bundle = new Bundle();
//            bundle.putString("response", response);
//            msg.setData(bundle);
//            msg.obj = view;
//
//            mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
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

    private void writeComments(String content, String parentId, String topCommentId, int postId) {
        String url = ServerInfo.serviceIP + ServerInfo.getComentList;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", "" + postId);
        params.put("type", "1");
        params.put("content", content);
        params.put("parent_id", parentId);
        params.put("base_comment_id", topCommentId);
        params.put("source_type", "3");
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                try {
                    String result = response;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show("发表成功");
                        ColumnContent columnContent = mColumnContents.get(mVideoPosition);
                        columnContent.setComment(columnContent.getComment() + 1);
                        getVideoDetail(mVideoPosition);
                    } else {
                        String message = jsonObject.getString("msg");
                        if (message != null) {
                            ToastUtils.show("发表失败" + message);
                        } else {
                            ToastUtils.show("发表失败");
                        }
                    }
                } catch (Exception e) {
                    ToastUtils.show("发表失败");
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                ToastUtils.show("发表失败，网络错误");
            }
        });
    }

    //</editor-fold>
//<editor-fold desc="Function">
    private void showShareDialog(final String title, final String desc, final String logo, final String url, int is_user_report) {
        ShareDialog dialog = ShareDialog.getInstance(false, is_user_report == 1);

        dialog.setIsShowReport(true);
        dialog.setIsShowCopyLink(true);
        dialog.setShareHandler(new ShareDialog.ShareHandler() {
            @Override
            public void onShare(String platform) {
                showShare(platform, title, desc, logo, url);
            }


            @Override
            public void report() {
                //举报
                ColumnContent columnContent = mColumnContents.get(mVideoPosition);
                if (User.getInstance() == null) {
                    Intent it = new Intent(AlivcLittleVideoActivity.this, NewLoginActivity.class);
                    startActivityForResult(it, REQUEST_LOGIN);
                } else {
                    Intent it = new Intent(AlivcLittleVideoActivity.this, ReportActivity.class);
                    it.putExtra("id", "" + columnContent.getId());
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
                ColumnContent columnContent = mColumnContents.get(mVideoPosition);
                shareStatistics(chanel, "" + columnContent.getId(), ServerInfo.h5IP + "/share/" + columnContent.getId() + "?app=android");
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

    //当前播放视频的位置
    public void onPlayAtPosition(int position) {
        mVideoPosition = position;
        Log.d("SmallVideo", "position = " + position);
        getVideoDetail(position);
    }

    //判断上下滑动
    public void handleTouch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (Math.abs(currentX - oldX) < Math.abs(currentY - oldY)) {
                    handleVerticalTouch();
                } else {
                    if (mVideoPosition == 0) {
                        //当前是第一页
                        handleHorizontalTouch();
                    }
                }
                Looper.loop();
            }
        }).start();
    }

    //垂直方向
    public void handleVerticalTouch() {
        if (mCancelVerticalMove == true) {
            return;
        }
        if (mVideoPosition != mLastVideoPosition) {
            return;
        }
        if (currentY - oldY > 200) {//从上往下
            Message message = new Message();
            message.what = PAN_DOWN;
            handler.sendMessage(message);
        } else if (currentY - oldY < -200) {//从下往上滑
            Message message = new Message();
            message.what = PAN_UP;
            handler.sendMessage(message);
        }
    }

    //水平方向
    public void handleHorizontalTouch() {
        if (currentX - oldX > 200) {
            Message message = new Message();
            message.what = PAN_RIGHT;
            handler.sendMessage(message);
        }
    }

    // 退出动画
    public void out() {
        Intent intent = new Intent();
        intent.putExtra("littleVideos", (Serializable) mColumnContents);
        intent.putExtra("position", mVideoPosition);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(0, R.anim.out);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == PAN_DOWN || msg.what == PAN_RIGHT) {
                out();
            } else if (msg.what == PAN_UP) {
                onCommentButtonClick();
            }
            return false;
        }
    });

    //评论
    public void onCommentButtonClick() {
        ColumnContent columnContent = mColumnContents.get(mVideoPosition);
        mSmallVideoCommentContentBottomDialog =
                new SmallVideoCommentContentBottomDialog();
        mSmallVideoCommentContentBottomDialog.setVideoId(columnContent.getId());
        mSmallVideoCommentContentBottomDialog.show(getSupportFragmentManager(), "dialog");
    }

    //评论并且输入
    public void onCommentAndInputClick() {
        final ColumnContent columnContent = mColumnContents.get(mVideoPosition);
        //判断是否登录
        if (User.getInstance() == null) {
            Intent it = new Intent(AlivcLittleVideoActivity.this, NewLoginActivity.class);
            it.putExtra("bindPhone", true);
            it.putExtra("jump_url", ServerInfo.h5IP + "/videos/" + columnContent.getId());
            startActivityForResult(it, REQUEST_LOGIN);
        } else if (User.getInstance() != null && TextUtils.isEmpty(User.getInstance().getPhone())) {
            Intent it = new Intent(AlivcLittleVideoActivity.this, BindPhoneActivity.class);
            startActivity(it);
        } else {
            if (columnContent.getComment() > 0) {
                mSmallVideoCommentContentBottomDialog =
                        new SmallVideoCommentContentBottomDialog();
                mSmallVideoCommentContentBottomDialog.setVideoId(columnContent.getId());
                mSmallVideoCommentContentBottomDialog.show(getSupportFragmentManager(), "DialogAndInput");
            } else {
                mCommentDialog = new CommentDialog();
                mCommentDialog.show(getSupportFragmentManager(), "smallVideosoftInput");
                mCommentDialog.setSendListener(new CommentDialog.SendListener() {
                    @Override
                    public void sendComment(String inputText) {
                        writeComments(inputText, "" + "0", "" + "0", columnContent.getId());
                        mCommentDialog.dismiss();
                    }
                }, null);
                mCommentDialog.setDismissListener(new CommentDialog.DismissListener() {
                    @Override
                    public void onDismiss() {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.hideInput(AlivcLittleVideoActivity.this);
                            }
                        }, 50);
                    }
                });
            }
        }
    }
//</editor-fold>
}
