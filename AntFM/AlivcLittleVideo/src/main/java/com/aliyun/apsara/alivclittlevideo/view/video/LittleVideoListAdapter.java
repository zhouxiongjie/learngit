package com.aliyun.apsara.alivclittlevideo.view.video;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aliyun.apsara.alivclittlevideo.R;
import com.aliyun.apsara.alivclittlevideo.net.data.LittleLiveVideoInfo;
import com.aliyun.apsara.alivclittlevideo.net.data.LittleMineVideoInfo;
import com.aliyun.apsara.alivclittlevideo.utils.DensityUtils;
import com.aliyun.apsara.alivclittlevideo.utils.ImageLoader;
import com.aliyun.apsara.alivclittlevideo.view.video.videolist.BaseVideoListAdapter;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 视频列表的adapter
 * @author xlx
 */
public class LittleVideoListAdapter extends BaseVideoListAdapter<LittleVideoListAdapter.MyHolder,BaseVideoSourceModel> {
    public static final String TAG = LittleVideoListAdapter.class.getSimpleName();
    private OnItemBtnClick mItemBtnClick;
    public LittleVideoListAdapter(Context context) {
        super(context);
    }


    @NonNull
    @Override
    public LittleVideoListAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_view_pager, viewGroup, false);
        return new MyHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, final int position) {
        super.onBindViewHolder(myHolder, position);
        myHolder.mIvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemBtnClick!=null){
                    mItemBtnClick.onDownloadClick(position);
                }
            }
        });
        myHolder.mFivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemBtnClick!=null){
                    mItemBtnClick.onCloseClick(position);
                }
            }
        });
        myHolder.mIvShareMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemBtnClick!=null){
                    mItemBtnClick.onShareClick(position);
                }
            }
        });
        myHolder.mFivPrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemBtnClick!=null){
                    mItemBtnClick.onPraiseClick(position);
                }
            }
        });
        myHolder.mFivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemBtnClick!=null){
                    mItemBtnClick.onCommentClick(position);
                }
            }
        });
        myHolder.mFivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemBtnClick!=null){
                    mItemBtnClick.onShareClick(position);
                }
            }
        });
        myHolder.mTvSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemBtnClick!=null){
                    mItemBtnClick.onSendCommentClick(position);
                }
            }
        });
        myHolder.mTvAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemBtnClick!=null){
                    mItemBtnClick.onSendAttentionClick(position);
                }
            }
        });
        myHolder.mTvSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemBtnClick!=null){
                    mItemBtnClick.onSendCommentClick(position);
                }
            }
        });


        //myHolder.mVideoInfoView.setVideoInfo(list.get(position));

        BaseVideoSourceModel video = list.get(position);
        if (video instanceof LittleMineVideoInfo.VideoListBean){
            if ("success".equals(((LittleMineVideoInfo.VideoListBean)video).getNarrowTranscodeStatus())){
                myHolder.mIvNarrow.setVisibility(View.VISIBLE);
            }else {
                myHolder.mIvNarrow.setVisibility(View.GONE);
            }
            myHolder.mIvLive.setVisibility(View.GONE);
        }else if (video instanceof LittleLiveVideoInfo.LiveListBean){
            myHolder.mIvNarrow.setVisibility(View.GONE);
            myHolder.mIvDownload.setVisibility(View.GONE);
            myHolder.mIvLive.setVisibility(View.VISIBLE);
        }


        myHolder.mTvVideoTitle.setText(video.getTitle());
        if(video.getUser()!=null) {
            myHolder.mTvAuthorName.setText(video.getUser().getUserName());
                Uri uri = Uri.parse(video.getUser().getAvatarUrl());
                int width = 76;
                int height = width;
                ImageLoader.showThumb(uri, myHolder.mSdvAvatar, width, height);
        }
//
//
//                    && !TextUtils.isEmpty(smallVideo.getAuthor_info().getMerchant().getLogo())) {
//                Uri uri = Uri.parse(smallVideo.getAuthor_info().getMerchant().getLogo());
//                int width = CommonUtils.dip2px(16);
//                int height = width;
//                ImageLoader.showThumb(uri, myHolder, width, height);
//
//        }


    }

    public final class  MyHolder extends BaseVideoListAdapter.BaseHolder {
        private ImageView thumb;
        public FrameLayout playerView;
        private ImageView mIvDownload;
        private ViewGroup mRootView;
        private VideoInfoView mVideoInfoView;
        private ImageView mIvNarrow;
        private ImageView mIvLive;
        private ImageView mPlayIconImageView;

        private FontIconView mFivClose;//关闭
        private ImageView mIvShareMore;//更多分享
        private FontIconView mFivPrase;//点赞
        private TextView mTvLikes; //点赞数
        private FontIconView mFivComment;//查看评论
        private FontIconView mFivShare;//分享
        private TextView mTvSendComment;//发评论
        private TextView mTvAttention;//关注
        private TextView mTvVideoTitle;//标题
        private TextView mTvAuthorName;//作者名字
        private SimpleDraweeView mSdvAvatar; //作者的头像
        private ProgressBar mPlayProgressBar; //播放进度


        public FontIconView getmFivPrase() {
            return mFivPrase;
        }

        public TextView getmTvAttention() {
            return mTvAttention;
        }

        public TextView getmTvLikes() {
            return mTvLikes;
        }

        MyHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG,"new PlayerManager");
            thumb = itemView.findViewById(R.id.img_thumb);
            playerView = itemView.findViewById(R.id.player_view);
            mIvDownload = itemView.findViewById(R.id.iv_download);
            mRootView = itemView.findViewById(R.id.root_view);
            mVideoInfoView = itemView.findViewById(R.id.content_view);
            mIvNarrow = itemView.findViewById(R.id.iv_narrow);
            mIvLive = itemView.findViewById(R.id.iv_live);
            mPlayIconImageView = itemView.findViewById(R.id.iv_play_icon);

            mIvShareMore = itemView.findViewById(R.id.iv_share_more);
            mFivPrase = itemView.findViewById(R.id.fiv_prase);
            mTvLikes =  itemView.findViewById(R.id.tv_likes);
            mFivComment = itemView.findViewById(R.id.fiv_comment);
            mFivShare = itemView.findViewById(R.id.fiv_share);
            mFivClose = itemView.findViewById(R.id.fiv_close);
            mTvSendComment = itemView.findViewById(R.id.tv_send_comment);
            mTvAttention = itemView.findViewById(R.id.tv_attention);
            mTvVideoTitle = itemView.findViewById(R.id.tv_video_title);
            mTvAuthorName = itemView.findViewById(R.id.tv_author_name);
            mSdvAvatar =  itemView.findViewById(R.id.iv_author_logo);
            mPlayProgressBar = itemView.findViewById(R.id.pb_play_bar);
        }

        @Override
        public ImageView getCoverView() {
            return thumb;
        }

        @Override
        public ViewGroup getContainerView() {
            return mRootView;
        }

        @Override
        public ImageView getPlayIcon(){
            return mPlayIconImageView;
        }

        @Override
        public ProgressBar getPlayProgressBar() {
            return mPlayProgressBar;
        }
    }
    public interface OnItemBtnClick{
        void onDownloadClick(int position);

        //点赞
        void onPraiseClick(int position);
        //分享
        void onShareClick(int position);
        //关闭
        void onCloseClick(int position);
        //评论
        void onCommentClick(int position);
        //发评论
        void onSendCommentClick(int position);
        //关注
        void onSendAttentionClick(int position);


    }

    public void setItemBtnClick(
        OnItemBtnClick mItemBtnClick) {
        this.mItemBtnClick = mItemBtnClick;
    }


}
