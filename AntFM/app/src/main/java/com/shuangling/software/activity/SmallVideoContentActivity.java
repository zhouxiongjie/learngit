package com.shuangling.software.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.player.source.VidAuth;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerPortraitView;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.hjq.toast.ToastUtils;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.shuangling.software.R;
import com.shuangling.software.adapter.SmallVideoContentRecyclerViewAdapter;
import com.shuangling.software.dialog.SmallVideoCommentContentBottomDialog;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.ResAuthInfo;
import com.shuangling.software.entity.SmallVideo;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.MyLayoutManger;
import com.shuangling.software.utils.OnViewPagerListener;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

// 类似viewpager的小视频内容 活动
public class SmallVideoContentActivity extends QMUIActivity {
    RecyclerView recyclerView;
    SmallVideoContentRecyclerViewAdapter smallVideoContentRecyclerViewAdapter;
    List<ColumnContent> smallVideos = new ArrayList<>();
    List<AliyunVodPlayerPortraitView> playerViews = new ArrayList<AliyunVodPlayerPortraitView>();
    MyLayoutManger myLayoutManger;
    float oldX = 0;
    float currentX = 0;
    int videoPosition = 0; // 当前点击的视频位置
    SmallVideoCommentContentBottomDialog smallVideoCommentContentBottomDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small_video_content);
        smallVideos.addAll((List<ColumnContent>) getIntent().getSerializableExtra("littleVideos"));
        videoPosition = getIntent().getIntExtra("position", 0);
        initView();
        initListener();
    }

    @Override
    protected void onDestroy() {
        for (int i = playerViews.size() - 1; i > -1; i--) {
            AliyunVodPlayerPortraitView playerPortraitView = playerViews.get(i);
            playerPortraitView.onDestroy();
            playerViews.remove(i);
        }
        super.onDestroy();
    }

    private void initListener() {
        myLayoutManger.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                int index = 0;
                if (isNext) {// 有下一个则释放当前视频
                    index = 0;
                } else {
                    index = 1;
                }
                releaseVideo(index);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onPageSelected(int position, boolean isBottom) {
                playVideo(position);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageSelected(int position, int totalPage) {
            }

            @Override
            public void exitVideo() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (currentX - oldX > 200) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }).start();
            }
        });
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler_view_small_video_content);
        /*myLayoutManger = new MyLayoutManger(this,OrientationHelper.VERTICAL,false);*/// 上下滑动
        myLayoutManger = new MyLayoutManger(this, OrientationHelper.HORIZONTAL, false);// 左右滑动
        smallVideoContentRecyclerViewAdapter = new SmallVideoContentRecyclerViewAdapter(smallVideos, this);
        recyclerView.setLayoutManager(myLayoutManger);
        recyclerView.setAdapter(smallVideoContentRecyclerViewAdapter);
        recyclerView.scrollToPosition(videoPosition);
        smallVideoContentRecyclerViewAdapter.setOnClickListener(new SmallVideoContentRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onExitClick() {
                out();
            }

            @Override
            public void onShareClick(ColumnContent smallVideo) {
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.putExtra(Intent.EXTRA_TEXT, smallVideo.getSmallVideoUrl()
//                );
//                intent.setType("text/plain");
//                startActivity(Intent.createChooser(intent, "share"));
            }

            @Override
            public void onCommentClick() {
                smallVideoCommentContentBottomDialog =
                        new SmallVideoCommentContentBottomDialog();
                smallVideoCommentContentBottomDialog.show(getSupportFragmentManager(), "dialog");
            }

            @Override
            public void onCommentAndInputClick() {
                smallVideoCommentContentBottomDialog =
                        new SmallVideoCommentContentBottomDialog();
                smallVideoCommentContentBottomDialog.show(getSupportFragmentManager(), "DialogAndInput");
            }
        });
    }

    private void releaseVideo(int index) {
        View itemView = recyclerView.getChildAt(index);
        final AliyunVodPlayerPortraitView videoView = itemView.findViewById(R.id.video_view);
        final ImageView imgThumb = itemView.findViewById(R.id.iv_thumb);
        final ImageView imgPaly = itemView.findViewById(R.id.iv_play);
        videoView.setAutoPlay(false);
        videoView.pause();
        videoView.onStop();
        imgThumb.animate().alpha(1).start();
        imgPaly.animate().alpha(0f).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void playVideo(int position) {
        View itemView = recyclerView.getChildAt(0);
        final AliyunVodPlayerPortraitView videoView = itemView.findViewById(R.id.video_view);
        playerViews.add(videoView);
        final ImageView imgThumb = itemView.findViewById(R.id.iv_thumb);
        final ImageView imgPaly = itemView.findViewById(R.id.iv_play);
// videoView.reInit();
//if(videoView.setAuthInfo();)
        if (videoView.getAuthoInfo() != null) {
            videoView.setAutoPlay(true);
            videoView.start();
        } else {
            ColumnContent columnContent = smallVideos.get(position);
            if (columnContent.getVideo() != null) {
                getVideoAuth(columnContent.getVideo().getSource_id(), videoView);
            }
        }
        imgPaly.setOnClickListener(new View.OnClickListener() {
            boolean isPalying = true;

            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    imgPaly.animate().alpha(1).start();
                    videoView.animate().alpha(1f).start();
                    videoView.pause();
                    isPalying = false;
                } else {
                    imgPaly.animate().alpha(0).start();
                    videoView.start();
                    isPalying = true;
                }
            }
        });
    }

    //获取视频播放权限
    private void getVideoAuth(final int sourceId, final AliyunVodPlayerPortraitView aliyunVodPlayerView) {
        String url = ServerInfo.vms + "/v1/sources/" + sourceId + "/playAuth";
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                handleGetAuth(sourceId, response, aliyunVodPlayerView);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    public void handleGetAuth(int sourceId, String result, AliyunVodPlayerPortraitView aliyunVodPlayerView) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                ResAuthInfo resAuthInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), ResAuthInfo.class);
                VidAuth vidAuth = new VidAuth();
                vidAuth.setPlayAuth(resAuthInfo.getPlay_auth());
                vidAuth.setVid(resAuthInfo.getVideo_id());
                vidAuth.setRegion("cn-shanghai");
                aliyunVodPlayerView.setAuthInfo(vidAuth);
                aliyunVodPlayerView.setAutoPlay(true);
            } else if (jsonObject != null) {
                ToastUtils.show(jsonObject.getString("msg"));
            }
        } catch (Exception e) {
        }
    }

    // 退出动画
    public void out() {
        finish();
        overridePendingTransition(0, R.anim.out);
    }

    // 拦截 获取 滑动距离
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldX = ev.getX();// 点击时的坐标
                break;
            case MotionEvent.ACTION_UP:
                currentX = ev.getX();// 抬起时的坐标
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            out();
            return false;
        }
    });
}
