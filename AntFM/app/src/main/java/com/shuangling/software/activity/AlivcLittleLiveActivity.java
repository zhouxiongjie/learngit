package com.shuangling.software.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.utils.AlicloudTracker;
import com.aliyun.apsara.alivclittlevideo.constants.AlivcLittleHttpConfig;
import com.aliyun.apsara.alivclittlevideo.net.HttpEngine;
import com.aliyun.apsara.alivclittlevideo.net.LittleHttpManager;
import com.aliyun.apsara.alivclittlevideo.net.data.LittleHttpResponse;
import com.aliyun.apsara.alivclittlevideo.net.data.LittleLiveVideoInfo;
import com.aliyun.apsara.alivclittlevideo.net.data.LittleUserInfo;
import com.aliyun.apsara.alivclittlevideo.view.mine.AlivcLittleUserManager;
import com.aliyun.apsara.alivclittlevideo.view.video.AlivcVideoPlayView;
import com.aliyun.apsara.alivclittlevideo.view.video.BaseVideoSourceModel;
import com.aliyun.apsara.alivclittlevideo.view.video.LittleVideoListAdapter;
import com.aliyun.apsara.alivclittlevideo.view.video.videolist.AlivcVideoListView;
import com.aliyun.player.source.VidAuth;
import com.aliyun.player.source.VidSts;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerPortraitView;
import com.ethanhua.skeleton.Skeleton;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.SmallVideoRecyclerViewAdapter;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.LiveInfo;
import com.shuangling.software.entity.ResAuthInfo;
import com.shuangling.software.entity.SmallVideoBean;
import com.shuangling.software.entity.StsInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.entity.VideoDetail;
import com.shuangling.software.fragment.SmallVideoFragment;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ACache;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;


import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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


public class AlivcLittleLiveActivity extends AppCompatActivity {

    public static final int REQUEST_LOGIN = 0x1;


    private AlivcVideoPlayView videoPlayView;
    private Column mColumn;

    private List<ColumnContent> mColumnContents = new ArrayList<>();
    private int mPageIndex = 1;
    private int mVideoPosition = 0;

    private List< SmallVideoBean> mDatas;

    private int mRequestCount = 0;
    private StsInfo mStsInfo;

    float oldX = 0;
    float currentX = 0;
    float oldY = 0;
    float currentY = 0;
    /**
     * 是否重新加载数据
     */
    private boolean isRefreshData = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alivc_little_activity_video_detail);
        initView();
        //loadPlayList(mPageIndex);
       // newGuest();
        mColumnContents.addAll((List<ColumnContent>) getIntent().getSerializableExtra("smallVideos"));
        mVideoPosition = getIntent().getIntExtra("position",0);
        mColumn = (Column) getIntent().getSerializableExtra("Column");
        getSts();
    }
    protected void initView() {
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
                    startActivityForResult(new Intent(AlivcLittleLiveActivity.this, NewLoginActivity.class), REQUEST_LOGIN);
                    return;
                }


                ColumnContent columnContent = mColumnContents.get(position);


                LittleVideoListAdapter.MyHolder holder =  videoPlayView.getPlayPager();
                if(holder != null) {

                    String likes =  holder.getmTvLikes().getText() + "";
                    int nLikes = 0;
                    nLikes = Integer.parseInt(likes);

                    if(columnContent.getIs_like() == 1) {
                        holder.getmFivPrase().setTextColor(Color.parseColor("#ffffff"));
                        praise(position,false);
                        nLikes --;
                        if(nLikes<=0){
                            holder.getmTvLikes().setText("0");
                        }else {
                            holder.getmTvLikes().setText("" + nLikes);
                        }
                        columnContent.setIs_like(0);
                    }else {
                        holder.getmFivPrase().setTextColor(Color.parseColor("#F54C68"));
                        praise(position,true);
                        nLikes ++;
                        holder.getmTvLikes().setText("" + nLikes);
                        columnContent.setIs_like(1);
                    }

                }

            }

            @Override
            public void onShareClick(int position) {


                ColumnContent columnContent =   mColumnContents.get(position);


                //http://www-cms-c.review.slradio.cn/share
                if(columnContent.getVideo() != null){
                    String url =  ServerInfo.h5IP + "/share/" + columnContent.getVideo().getPost_id();
                    Log.d("Share",url);
                    showShare(columnContent.getTitle(),columnContent.getDes(),columnContent.getCover(),url);
                }


            }

            @Override
            public void onCloseClick(int position) {
                out();
            }

            @Override
            public void onCommentClick(int position) {

            }

            @Override
            public void onSendCommentClick(int position) {

            }

            @Override
            public void onSendAttentionClick(int position) {

                if (User.getInstance() == null) {
                    startActivityForResult(new Intent(AlivcLittleLiveActivity.this, NewLoginActivity.class), REQUEST_LOGIN);
                    return;
                }

                LittleVideoListAdapter.MyHolder holder =  videoPlayView.getPlayPager();
                if(holder != null) {

                    if(holder.getmTvAttention().getText() == "已关注") {
                        holder.getmTvAttention().setText("关注");
                        holder.getmTvAttention().setSelected(true);
                        attention(position,false);
                    }else{
                        holder.getmTvAttention().setText("已关注");
                        holder.getmTvAttention().setSelected(false);
                        attention(position,true);
                    }

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

    private static class MyRefreshDataListener implements AlivcVideoListView.OnRefreshDataListener {
        WeakReference<AlivcLittleLiveActivity> weakReference;

        MyRefreshDataListener(AlivcLittleLiveActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onRefresh() {
            Log.e("AlivcLittlePlayer", "onRefresh");

            if (weakReference == null) {
                return;
            }
            AlivcLittleLiveActivity activity = weakReference.get();
            if (activity != null) {
                activity.loadPlayList(activity.mPageIndex = 1);
                activity.isRefreshData = true;
            }
        }

        @Override
        public void onLoadMore() {
            Log.e("AlivcLittlePlayer", "onLoadMore");
            if (weakReference == null) {
                return;
            }
            AlivcLittleLiveActivity activity = weakReference.get();
            if (activity != null) {
                activity.getMoreContent();
                activity.isRefreshData = false;
            }
        }

        @Override
        public void onExitVideo() {

            AlivcLittleLiveActivity activity = weakReference.get();
            if (activity != null) {
               // activity.willExitVideo();
            }

        }


        @Override
        public void onPlayAtPosition(int position) {

            AlivcLittleLiveActivity activity = weakReference.get();
            if (activity != null) {
                activity.onPlayAtPosition(position);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldX = ev.getX();// 点击时的坐标
                oldY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                currentX = ev.getX();// 抬起时的坐标
                currentY = ev.getY();
                handleTouch();
                break;
        }


        return super.dispatchTouchEvent(ev);
    }

    /**
     * 生成新用户
     */
    private void newGuest() {

        LittleUserInfo userInfo = AlivcLittleUserManager.getInstance().getUserInfo(this);

        if(userInfo == null) {
            LittleHttpManager.getInstance().newGuest(
                    new HttpEngine.HttpResponseResultCallback<LittleHttpResponse.LittleUserInfoResponse>() {
                        @Override
                        public void onResponse(boolean result, String message, LittleHttpResponse.LittleUserInfoResponse data) {
                            if (result && data != null && data.data != null) {
                                LittleUserInfo userInfo = data.data;
                                AlivcLittleUserManager.getInstance().setUserInfo(AlivcLittleLiveActivity.this,
                                        userInfo);

                                loadPlayList(mPageIndex);

                            } else {
                               // ToastUtils.show(AlivcLittleLiveActivity.this, message);
                            }
                        }
                    });
        }else{
            loadPlayList(mPageIndex);
        }

    }

    private void loadPlayList(int pageIndex) {
        Log.e("AlivcLittleLiveActivity", "id:" + pageIndex);
        LittleUserInfo userInfo = AlivcLittleUserManager.getInstance().getUserInfo(this);
        if (userInfo == null) {
            Toast.makeText(this, "R.string.alivc_little_no_user:" + com.aliyun.apsara.alivclittlevideo.R.string.alivc_little_tip_no_user, Toast.LENGTH_SHORT)
            .show();
            return;
        }
        LittleHttpManager.getInstance().requestLiveVideoList(userInfo.getToken(), pageIndex, AlivcLittleHttpConfig.DEFAULT_PAGE_SIZE,
        new HttpEngine.HttpResponseResultCallback<LittleHttpResponse.LittleLiveListResponse>() {
            @Override
            public void onResponse(final boolean result, String message,
                                   final LittleHttpResponse.LittleLiveListResponse data) {

                if (result) {
                    mPageIndex++;
                    if (isRefreshData) {
                        videoPlayView.refreshVideoList(data.data.getLiveList());
                    } else {
                        videoPlayView.addMoreData(data.data.getLiveList());
                    }
                    Log.e("AlivcLittleLiveActivity", "isRefreshData:" + isRefreshData);
                } else {
                    if (videoPlayView != null) {
                        videoPlayView.loadFailure();
                    }
                   // ToastUtils.show(AlivcLittleLiveActivity.this, message );
                }

            }
        });
    }


    public void getSts() {

        //String url = "http://api-vms.review.slradio.cn" + ServerInfo.ossSts  ;
        String url =  ServerInfo.vms + ServerInfo.ossSts  ;
        Map<String, String> params = new HashMap<String, String>();
        //OkHttpUtils.getNotAuthorization();
        OkHttpUtils.get(url, params, new OkHttpCallback(AlivcLittleLiveActivity.this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {
                Log.d("OSS",response);
                handleSTS(response);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                Log.d("OSS",exception.getLocalizedMessage());

            }
        });

    }

    public void handleSTS( String result) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                mStsInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), StsInfo.class);
                setData(mColumnContents);
                getVideoDetail(mVideoPosition);

            } else if (jsonObject != null) {
                ToastUtils.show(jsonObject.getString("msg"));
            }
        } catch (Exception e) {

        }

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

        OkHttpUtils.get(url, params, new OkHttpCallback(AlivcLittleLiveActivity.this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                try {
                    String result = response;
                    Log.d("content",result);
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        List<ColumnContent> columnContents=JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class);
                        Iterator<ColumnContent> iterator = columnContents.iterator();
                        while (iterator.hasNext()) {
                            ColumnContent columnContent = iterator.next();
                            if ( columnContent.getType()!=12) {
                                iterator.remove();
                            }
                        }


                        if(columnContents.size()<10){
                            //没有更多
                        }

                        setData(columnContents);
                        mColumnContents.addAll(columnContents);





                    }else{

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


    private void setData(List<ColumnContent> columnContents){
        mDatas = new ArrayList<SmallVideoBean>();
        mRequestCount = columnContents.size() ;
        for(int i = 0; i<columnContents.size(); i++) {
            ColumnContent columnContent = columnContents.get(i);
            if(columnContent.getVideo() != null && mStsInfo != null ){

                VidSts vidSts = new VidSts();
                vidSts.setVid(columnContent.getVideo().getVideo_id());
                vidSts.setAccessKeyId(mStsInfo.getAccessKeyId());
                vidSts.setAccessKeySecret(mStsInfo.getAccessKeySecret());
                vidSts.setSecurityToken(mStsInfo.getSecurityToken());
                vidSts.setRegion("cn-shanghai");
                SmallVideoBean data = new SmallVideoBean();
                data.setSourceId(""  + columnContent.getVideo().getSource_id());
                data.setmVidSts(vidSts);
                data.setIs_like(columnContent.getIs_like());
                data.setTitle(columnContent.getTitle());
                if(columnContent.getAuthor_info() != null && columnContent.getAuthor_info().getMerchant() != null ) {
                    BaseVideoSourceModel.UserBean userBean = new BaseVideoSourceModel.UserBean();
                    userBean.setAvatarUrl(columnContent.getAuthor_info().getMerchant().getLogo());
                    userBean.setUserName(columnContent.getAuthor_info().getMerchant().getName());
                    data.setUser(userBean);
                }


                data.setCoverUrl(columnContent.getCover());
                data.setTitle(columnContent.getTitle());
                mDatas.add(data);
            }
        }

        AlivcLittleLiveActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRefreshData) {
                    videoPlayView.refreshVideoList(mDatas,mVideoPosition);
                } else {
                    videoPlayView.addMoreData(mDatas);
                }
            }
        });

    }



    //通过获取视频详情来获取点赞数，是否关注，
    private void getVideoDetail(int position){

        ColumnContent columnContent= mColumnContents.get(position);
        String url = ServerInfo.serviceIP + ServerInfo.getVideoDetail + columnContent.getId();
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                try{
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                       final VideoDetail videoDetail = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), VideoDetail.class);

                        //是否关注
                        videoDetail.getIs_follow();
                        //点赞数
                        videoDetail.getLike();

                        AlivcLittleLiveActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LittleVideoListAdapter.MyHolder holder =  videoPlayView.getPlayPager();
                                if(holder != null){
                                    if(videoDetail.getIs_follow() == 1) {
                                        holder.getmTvAttention().setText("已关注");
                                        holder.getmTvAttention().setSelected(false);
                                        holder.getmTvAttention().setVisibility(View.VISIBLE);
                                    }else{
                                        holder.getmTvAttention().setText("关注");
                                        holder.getmTvAttention().setSelected(true);
                                        holder.getmTvAttention().setVisibility(View.VISIBLE);
                                    }

                                    if(videoDetail.getIs_likes() == 1) {
                                        holder.getmFivPrase().setTextColor(Color.parseColor("#F54C68"));
                                    }else{
                                        holder.getmFivPrase().setTextColor(Color.parseColor("#ffffff"));
                                    }

                                    holder.getmTvLikes().setText( "" + videoDetail.getLike());
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
    public void attention(int index,final boolean follow) {

        ColumnContent columnContent = mColumnContents.get(index);
        if(columnContent.getAuthor_info() == null){
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



    public void praise(int index,final boolean like) {
        ColumnContent columnContent = mColumnContents.get(index);
        if(columnContent.getAuthor_info() == null){
            ToastUtils.show("无法获取机构信息！");
            return;
        }

        String url = ServerInfo.serviceIP + ServerInfo.like;
        Map<String, String> params = new HashMap<>();
        params.put("id", ""+columnContent.getId());
        if (like) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Log.d("SmallVideo",response);
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







    //获取视频播放权限
    private void getVideoAuth(final ColumnContent columnContent) {

        int sourceId = columnContent.getVideo().getSource_id();
        String url = ServerInfo.vms + "/v1/sources/" + sourceId + "/playAuth";
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                handleGetAuth(columnContent,response);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                mRequestCount -- ;
            }
        });
    }



    public void handleGetAuth( ColumnContent columnContent, String result) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                ResAuthInfo resAuthInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), ResAuthInfo.class);
                VidAuth vidAuth = new VidAuth();
                vidAuth.setPlayAuth(resAuthInfo.getPlay_auth());
                vidAuth.setVid(resAuthInfo.getVideo_id());
                vidAuth.setRegion("cn-shanghai");
                SmallVideoBean data = new SmallVideoBean();
                data.setSourceId(""  + columnContent.getVideo().getSource_id());
                data.setCoverUrl(columnContent.getCover());
                data.setTitle(columnContent.getTitle());
                mDatas.add(data);

                mRequestCount -- ;
                if(mRequestCount == 0) {
                    AlivcLittleLiveActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (isRefreshData) {
                                videoPlayView.refreshVideoList(mDatas);
                            } else {
                                videoPlayView.addMoreData(mDatas);
                            }
                        }
                    });
                }







            } else if (jsonObject != null) {
                ToastUtils.show(jsonObject.getString("msg"));
            }
        } catch (Exception e) {

        }

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

                //shareStatistics(chanel, "" + mVideoDetail.getId(), url);
            }
        });
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Message msg = Message.obtain();
//                msg.what = SHARE_FAILED;
//                msg.obj = arg2.getMessage();
//                mHandler.sendMessage(msg);
            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                Message msg = Message.obtain();
//                msg.what = SHARE_SUCCESS;
//                mHandler.sendMessage(msg);


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
    public void onPlayAtPosition(int position){
        mVideoPosition = position;
        Log.d("SmallVideo","position = " + position);
        getVideoDetail(position);

    }




    //判断上下滑动
    public void handleTouch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



                if(Math.abs(currentX- oldX) < Math.abs(currentY- oldY)){
                    handleVerticalTouch();
                }else{
                   if(mVideoPosition == 0) {
                       //当前是第一页
                       handleHorizontalTouch();
                   }
                }


            }
        }).start();
    }


    //垂直方向
    public void handleVerticalTouch() {
        if (currentY - oldY > 200) {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    }

    //水平方向
    public void handleHorizontalTouch(){
        if (currentX - oldX > 200) {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    }

//    public void willExitVideo(){
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(200);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if (currentX - oldX > 200) {
//                    Message message = new Message();
//                    message.what = 1;
//                    handler.sendMessage(message);
//                }
//            }
//        }).start();
//    }

    // 退出动画
    public void out() {

        Intent intent = new Intent();
        intent.putExtra("smallVideos", (Serializable) mColumnContents);
        intent.putExtra("position",  mVideoPosition);
        setResult(RESULT_OK, intent);

        finish();
        overridePendingTransition(0, R.anim.out);
    }


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            out();
            return false;
        }
    });


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_LOGIN && resultCode == RESULT_OK) {
           getVideoDetail(mVideoPosition);
        }

    }

}
