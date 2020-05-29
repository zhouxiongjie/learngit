package com.aliyun.apsara.alivclittlevideo.view.video;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aliyun.apsara.alivclittlevideo.R;
import com.aliyun.apsara.alivclittlevideo.net.data.LittleMineVideoInfo;
import com.aliyun.apsara.alivclittlevideo.sts.StsTokenInfo;
import com.aliyun.apsara.alivclittlevideo.view.video.videolist.AlivcVideoListView;
import com.aliyun.apsara.alivclittlevideo.view.video.videolist.IVideoSourceModel;
import com.aliyun.apsara.alivclittlevideo.view.video.videolist.OnTimeExpiredErrorListener;

import com.aliyun.downloader.AliMediaDownloader;
import com.aliyun.player.IPlayer;
import com.aliyun.player.source.StsInfo;


import java.util.ArrayList;
import java.util.List;
import com.aliyun.apsara.alivclittlevideo.utils.DensityUtils;


/**
 * 播放界面, 负责initPlayerSDK以及各种view
 *
 * @author xlx
 */
public class AlivcVideoPlayView extends FrameLayout {


    private static final String TAG = "AlivcVideoPlayView";
    private Context context;
    private AlivcVideoListView videoListView;

    private LittleVideoListAdapter.OnItemBtnClick onItemBtnClick;

    public void playVideoAtPostion(int position){
        videoListView.playVideoAtPosition(position);
    }

    public void setOnItemBtnClick(LittleVideoListAdapter.OnItemBtnClick onItemBtnClick) {
        this.onItemBtnClick = onItemBtnClick;
        mVideoAdapter.setItemBtnClick(onItemBtnClick);
    }

    /**
     * 刷新数据listener (下拉刷新和上拉加载)
     */
    private AlivcVideoListView.OnRefreshDataListener onRefreshDataListener;
    /**
     * 视频缓冲加载view
     */
    private LoadingView mLoadingView;



    /**
     * 视频删除点击事件
     */
    private OnVideoDeleteListener mOutOnVideoDeleteListener;
    private LittleVideoListAdapter mVideoAdapter;

    public AlivcVideoPlayView(@NonNull Context context) {
        this(context, null);
    }

    public AlivcVideoPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        initPlayListView();
        initLoadingView();
    }
    private Dialog mDownloadDialog;

    private TextView mTvProgress;
    private FrameLayout mDownloadContent;





    /**
     * 下载sdk核心类
     */
    private AliMediaDownloader mDownloadManager;

    private int mClickPosition;

    /**
     * 初始化视频列表
     */
    private void initPlayListView() {
        videoListView = new AlivcVideoListView(context);
        //创建adapter，需要继承BaseVideoListAdapter
        mVideoAdapter = new LittleVideoListAdapter(context);
        //给AlivcVideoListView实例化对象添加adapter
        videoListView.setAdapter(mVideoAdapter);
        videoListView.setVisibility(VISIBLE);
        //设置sdk播放器实例化对象数量
        videoListView.setPlayerCount(3);


        //设置下拉、上拉监听进行加载数据
        videoListView.setOnRefreshDataListener(new AlivcVideoListView.OnRefreshDataListener() {
            @Override
            public void onRefresh() {
                if (onRefreshDataListener != null) {
                    onRefreshDataListener.onRefresh();
                }
            }

            @Override
            public void onLoadMore() {
                if (onRefreshDataListener != null) {
                    onRefreshDataListener.onLoadMore();
                }
            }

            @Override
            public void onExitVideo() {
                if (onRefreshDataListener != null) {
                    onRefreshDataListener.onExitVideo();
                }
            }

            @Override
            public void onPlayAtPosition(int position) {
                if (onRefreshDataListener != null) {
                    onRefreshDataListener.onPlayAtPosition(position);
                }
            }
        });
        //设置视频缓冲监听
        videoListView.setLoadingListener(new IPlayer.OnLoadingStatusListener() {
            @Override
            public void onLoadingBegin() {
                mLoadingView.start();
            }

            @Override
            public void onLoadingEnd() {
                mLoadingView.cancle();
            }

            @Override
            public void onLoadingProgress(int var1, float var2) {

            }
        });
        //设置鉴权过期监听，刷新鉴权信息
        videoListView.setTimeExpiredErrorListener(new OnTimeExpiredErrorListener() {

            @Override
            public void onTimeExpiredError() {
                if (mStsInfoExpiredListener != null) {
                    mStsInfoExpiredListener.onTimeExpired();
                }
            }
        });
        //添加到布局中
        addSubView(videoListView);
    }

    /**
     * 播放、下载、上传过程中鉴权过期时提供的回调消息
     */
    private OnStsInfoExpiredListener mStsInfoExpiredListener;

    public void setOnStsInfoExpiredListener(
        OnStsInfoExpiredListener mTimeExpiredErrorListener) {
        this.mStsInfoExpiredListener = mTimeExpiredErrorListener;
    }





    private void initLoadingView() {
        mLoadingView = new LoadingView(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                5);
        params.setMargins(0, 0, 0, DensityUtils.dip2px(getContext(), 4));
        params.gravity = Gravity.BOTTOM;
        addView(mLoadingView, params);
    }

    /**
     * addSubView 添加子view到布局中
     *
     * @param view 子view
     */
    private void addSubView(View view) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        addView(view, params);
    }

    /**
     * 刷新视频列表数据
     *
     * @param datas
     */
    public void refreshVideoList(List<? extends BaseVideoSourceModel> datas) {
        List<IVideoSourceModel> videoList = new ArrayList<>();
        videoList.addAll(datas);
        videoListView.refreshData(videoList);
        //取消加载loading
        mLoadingView.cancle();

    }

    /**
     * 刷新视频列表数据
     *
     * @param datas
     * @param position
     */
    public void refreshVideoList(List<? extends BaseVideoSourceModel> datas, int position) {
        List<IVideoSourceModel> videoList = new ArrayList<>();
        videoList.addAll(datas);
        videoListView.refreshData(videoList, position);
        //取消加载loading
        mLoadingView.cancle();
    }



    /**
     * 添加更多视频
     *
     * @param datas
     */
    public void addMoreData(List<? extends BaseVideoSourceModel> datas) {
        List<IVideoSourceModel> videoList = new ArrayList<>();
        videoList.addAll(datas);
        videoListView.addMoreData(videoList);
        //取消加载loading
        mLoadingView.cancle();
    }



    /**
     * 设置下拉刷新数据listener
     *
     * @param listener OnRefreshDataListener
     */
    public void setOnRefreshDataListener(AlivcVideoListView.OnRefreshDataListener listener) {
        this.onRefreshDataListener = listener;
    }

    public void onStart() {

    }

    public void onResume() {
        videoListView.setOnBackground(false);

    }

    public void onStop() {
        mLoadingView.cancle();
    }

    public void onPause() {

        videoListView.setOnBackground(true);

    }

    public void onDestroy() {
        context = null;
        if (mDownloadManager != null) {
            mDownloadManager.setOnCompletionListener(null);
            mDownloadManager.setOnErrorListener(null);
            mDownloadManager.setOnProgressListener(null);
            mDownloadManager.setOnPreparedListener(null);
            mDownloadManager.release();
            mDownloadManager = null;
        }
    }

    /**
     * 视频列表获取失败
     */
    public void loadFailure() {
        mLoadingView.cancle();
        videoListView.loadFailure();
    }



    private FragmentActivity mActivity;

    private FragmentManager getFragmentManager() {
        FragmentManager fm = null;
        if (mActivity != null) {
            fm = mActivity.getSupportFragmentManager();
        } else {
            Context mContext = getContext();
            if (mContext instanceof FragmentActivity) {
                fm = ((FragmentActivity)mContext).getSupportFragmentManager();
            }
        }
        return fm;
    }

    /**
     * 刷新sts信息
     *
     * @param tokenInfo
     */
    public void refreshStsInfo(StsTokenInfo tokenInfo) {
        if(videoListView != null){
            String currentUid = videoListView.getCurrentUid();
            if(!TextUtils.isEmpty(currentUid) && tokenInfo != null){
                StsInfo stsInfo = new StsInfo();
                stsInfo.setAccessKeyId(tokenInfo.getAccessKeyId());
                stsInfo.setAccessKeySecret(tokenInfo.getAccessKeySecret());
                stsInfo.setSecurityToken(tokenInfo.getSecurityToken());
                videoListView.moveTo(currentUid,stsInfo);
            }
        }
    }

    /**
     * 删除按钮点击listener
     */
    public interface OnVideoDeleteListener {
        /**
         * 删除视频
         *
         * @param videoId 视频id
         */
        void onDeleteClick(LittleMineVideoInfo.VideoListBean videoId);
    }

    public void setOnVideoDeleteListener(
        OnVideoDeleteListener mOutOnVideoDeleteListener) {
        this.mOutOnVideoDeleteListener = mOutOnVideoDeleteListener;
    }
    /**
     * 移除当前播放的视频
     */
    public void removeCurrentPlayVideo() {
        videoListView.removeCurrentPlayVideo();
    }


    public  LittleVideoListAdapter.MyHolder getPlayPager() {
       return videoListView.getPlayPager();
    }

}
