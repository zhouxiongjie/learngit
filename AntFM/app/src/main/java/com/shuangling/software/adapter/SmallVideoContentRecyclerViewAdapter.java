package com.shuangling.software.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.player.source.VidAuth;
import com.aliyun.vodplayerview.utils.ScreenUtils;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerPortraitView;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.R;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.ResAuthInfo;
import com.shuangling.software.entity.SmallVideo;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Created by 你是我的 on 2019/1/7.
 */

// 小视频内容recyclerview 适配器
public class SmallVideoContentRecyclerViewAdapter extends
        RecyclerView.Adapter<SmallVideoContentRecyclerViewAdapter.ViewHolder>
        implements View.OnClickListener , Handler.Callback{


    private static final int MSG_GET_VIDEO_AUTH = 0xd;
    private Handler mHandler;

    private List<ColumnContent> smallVideos = new ArrayList<>();
    private HashMap<String,ResAuthInfo> resAuthInfoMap = new HashMap<>();
    private Context context;
    private LayoutInflater layoutInflater;
    private OnClickListener onClickListener;
    ColumnContent smallVideo;

    public SmallVideoContentRecyclerViewAdapter(List<ColumnContent> smallVideos, Context context) {
        this.smallVideos = smallVideos;
        this.context = context;
        mHandler = new Handler(this);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.small_video_content_view_pager_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        smallVideo = smallVideos.get(i);
        if (!TextUtils.isEmpty(smallVideo.getCover())) {
            viewHolder.aliyunVodPlayerView.setCoverUri(smallVideo.getCover());
        }
        //初始化播放器
        viewHolder.aliyunVodPlayerView.setKeepScreenOn(true);
        String sdDir = CommonUtils.getStoragePrivateDirectory(Environment.DIRECTORY_MOVIES);
        viewHolder.aliyunVodPlayerView.setPlayingCache(false, sdDir, 60 * 60 /*时长, s */, 300 /*大小，MB*/);
        viewHolder.aliyunVodPlayerView.setCirclePlay(true);
        viewHolder.aliyunVodPlayerView.setAutoPlay(false);
       //viewHolder.aliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        viewHolder.aliyunVodPlayerView.setBackBtnVisiable(View.INVISIBLE);
        viewHolder.aliyunVodPlayerView.seekTo(0);
        //viewHolder.aliyunVodPlayerView.setLockPortraitMode();

        if(smallVideo.getVideo() != null ){
            setVideo(smallVideo.getVideo().getSource_id(),viewHolder);
        }

        viewHolder.tvSmallVideoTitle.setText(smallVideo.getTitle());
        viewHolder.ivExitSmallVideoContent.setOnClickListener(this);
        viewHolder.ivSmallVideoShare.setOnClickListener(this);
        viewHolder.ivSmallVideoShareMore.setOnClickListener(this);
        viewHolder.layoutSmallVideoComment.setOnClickListener(this);
        viewHolder.ivSmallVideoComment.setOnClickListener(this);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        holder.getAliyunVodPlayerView().setAutoPlay(false);
        holder.getAliyunVodPlayerView().onStop();
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return smallVideos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.video_view)
        AliyunVodPlayerPortraitView aliyunVodPlayerView;
        @BindView(R.id.iv_thumb)
        SimpleDraweeView ivThumb;
        @BindView(R.id.iv_play)
        ImageView ivPlay;
        @BindView(R.id.check_box_like)
        CheckBox checkBoxLike;
        @BindView(R.id.iv_small_video_share_more)
        ImageView ivSmallVideoShareMore;
        @BindView(R.id.iv_small_video_comment)
        ImageView ivSmallVideoComment;
        @BindView(R.id.iv_small_video_share)
        ImageView ivSmallVideoShare;
        @BindView(R.id.iv_exit_small_video_content)
        ImageView ivExitSmallVideoContent;
        @BindView(R.id.tv_small_video_content_title)
        TextView tvSmallVideoTitle;
        @BindView(R.id.layout_small_video_comment)
        LinearLayout layoutSmallVideoComment;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }


        public AliyunVodPlayerPortraitView getAliyunVodPlayerView() {
            return aliyunVodPlayerView;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_exit_small_video_content:
                onClickListener.onExitClick();
                break;
            case R.id.iv_small_video_share:
            case R.id.iv_small_video_share_more:
                onClickListener.onShareClick(smallVideo);
                break;
            case R.id.layout_small_video_comment:
                onClickListener.onCommentAndInputClick();
                break;
            case R.id.iv_small_video_comment:
                onClickListener.onCommentClick();
                break;
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onExitClick();// 退出

        void onShareClick(ColumnContent smallVideo);// 分享

        void onCommentClick();// 显示评论

        void onCommentAndInputClick();// 显示评论和软键盘
    }



    private void setVideo(int sourceId, final ViewHolder viewHolder) {

        String key = "" + sourceId;
        ResAuthInfo resAuthInfo = resAuthInfoMap.get(key);
        if(resAuthInfo != null){
            VidAuth vidAuth = new VidAuth();
            vidAuth.setPlayAuth(resAuthInfo.getPlay_auth());
            vidAuth.setVid(resAuthInfo.getVideo_id());
            vidAuth.setRegion("cn-shanghai");
            viewHolder.aliyunVodPlayerView.setAuthInfo(vidAuth);
        }else{
            getVideoAuth(sourceId,viewHolder);
        }
    }

    //获取视频播放权限
    private void getVideoAuth(final int sourceId, final ViewHolder viewHolder) {

        String url = ServerInfo.vms + "/v1/sources/" + sourceId + "/playAuth";
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, params, new OkHttpCallback(this.context) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                handleGetAuth(sourceId,response,viewHolder);
            }

            @Override
            public void onFailure(Call call, Exception exception) {

            }
        });
    }



    public void handleGetAuth(int sourceId,String result,ViewHolder viewHolder) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                ResAuthInfo resAuthInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), ResAuthInfo.class);
                VidAuth vidAuth = new VidAuth();
                vidAuth.setPlayAuth(resAuthInfo.getPlay_auth());
                vidAuth.setVid(resAuthInfo.getVideo_id());
                vidAuth.setRegion("cn-shanghai");
                viewHolder.aliyunVodPlayerView.setAuthInfo(vidAuth);
               // viewHolder.aliyunVodPlayerView.start();
                resAuthInfoMap.put(""+sourceId,resAuthInfo);
            } else if (jsonObject != null) {
                ToastUtils.show(jsonObject.getString("msg"));
            }
        } catch (Exception e) {

        }

    }


    @Override
    public boolean handleMessage(Message msg) {


        switch (msg.what) {
            case MSG_GET_VIDEO_AUTH:

                break;
        }



        return  true;
    }
}
