package com.shuangling.software.activity;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.CommentListAdapter;
import com.shuangling.software.adapter.RecommendContentGridAdapter;
import com.shuangling.software.customview.MyListView;
import com.shuangling.software.customview.MyRecyclerView;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.dialog.AudioListDialog;
import com.shuangling.software.dialog.AudioSpeedDialog;
import com.shuangling.software.dialog.AudioTimerDialog;
import com.shuangling.software.entity.Audio;
import com.shuangling.software.entity.AudioDetail;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.Comment;
import com.shuangling.software.event.PlayerEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.service.IAudioPlayer;
import com.shuangling.software.utils.CommonUtils;
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
import okhttp3.Call;
import okhttp3.Response;

@EnableDragToClose()
public class AudioDetailActivity extends AppCompatActivity implements Handler.Callback {

    public static final String TAG = "AlbumDetailActivity";

    public static final int MSG_GET_AUDIO_DETAIL = 0x1;

    public static final int MSG_GET_RELATED_POST  = 0x2;

    public static final int MSG_GET_COMMENTS  = 0x3;

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
    ImageButton play;
    @BindView(R.id.previous)
    ImageButton previous;
    @BindView(R.id.next)
    ImageButton next;
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


    private Handler mHandler;
    private Audio mAudio;
    private int mAudioId;

    private AudioDetail mAudioDetail;
    private IAudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateTimerTask mUpdateTimerTask;
    WindowManager mWindowManager;
    View mFloatPlayer;

    private List<ColumnContent> mPostContents;
    private List<Comment> mComments;
    private CommentListAdapter mCommentListAdapter;
    private RecommendContentGridAdapter mAdapter;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mAudioPlayer = IAudioPlayer.Stub.asInterface(service);
            getAudioDetail();
            getRelatedPosts();
            getComments();

//            try {
//                mAudioPlayer.playAudio(mAudio);
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

    private void init() {
        mHandler = new Handler(this);
        EventBus.getDefault().register(this);
        mAudio = getIntent().getParcelableExtra("Audio");
        //mAudioId = getIntent().getIntExtra("audioId",0);
        Intent it = new Intent(this, AudioPlayerService.class);
        bindService(it, mConnection, Context.BIND_AUTO_CREATE);


    }


    private void getAudioDetail() {


        String url = ServerInfo.serviceIP + ServerInfo.getAudioDetail + mAudio.getPost_audio_id();
        OkHttpUtils.get(url, null, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_AUDIO_DETAIL;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, IOException exception) {


            }
        });
    }


    private void getRelatedPosts() {


        String url = ServerInfo.serviceIP + ServerInfo.getRelatedRecommend;
        Map<String,String> params=new HashMap<>();
        params.put("post_id",""+mAudio.getAudios().get(0).getId());
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
    private void getComments() {


        String url = ServerInfo.serviceIP + ServerInfo.getComentList;
        Map<String,String> params=new HashMap<>();
        params.put("post_id",""+mAudio.getAudios().get(0).getId());
        params.put("page",""+1);
        params.put("page_size",""+Integer.MAX_VALUE);
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




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(PlayerEvent event) {
        if (event.getEventName().equals("OnPrepared")) {
            //1.改变播放按钮的状态
            //2.获取进度时长并显示
            //2.设置定时器更新进度条
            try {
                play.setBackgroundResource(R.drawable.ic_suspended);
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
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        unbindService(mConnection);
        super.onDestroy();
    }

    @OnClick({R.id.play, R.id.previous, R.id.next, R.id.list, R.id.timer, R.id.rate, R.id.subscribe, R.id.writeComment,R.id.commentNumLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.play:
                try {
                    int sta = mAudioPlayer.getPlayerState();
                    if (mAudioPlayer.getPlayerState() == IAliyunVodPlayer.PlayerState.Paused.ordinal()) {
                        mAudioPlayer.start();
                        play.setBackgroundResource(R.drawable.ic_suspended);
                    } else if (mAudioPlayer.getPlayerState() == IAliyunVodPlayer.PlayerState.Started.ordinal()) {
                        mAudioPlayer.pause();
                        play.setBackgroundResource(R.drawable.ic_play);
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
                mWindowManager.removeView(mFloatPlayer);

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
            case R.id.subscribe:
                break;
            case R.id.writeComment:
                break;
            case R.id.commentNumLayout:
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case MSG_GET_AUDIO_DETAIL:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        mAudioDetail = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), AudioDetail.class);

                        if (!TextUtils.isEmpty(mAudioDetail.getAlbum().get(0).getCover())) {
                            Uri uri = Uri.parse(mAudioDetail.getAlbum().get(0).getCover());
                            int width = CommonUtils.dip2px(145);
                            int height = width;
                            ImageLoader.showThumb(uri, logo, width, height);
                            width= (int)getResources().getDimension(R.dimen.article_right_image_width);
                            height = width;
                            ImageLoader.showThumb(uri, albumLogo, width, height);
                        }

                        albumTitle.setText(mAudioDetail.getAlbum().get(0).getTitle());
                        anchorName.setText(mAudioDetail.getAuthor_info().getStaff_name());
                        audioNumber.setText(mAudioDetail.getAlbum().get(0).getAlbums().getCount()+"集");
                        audioTitle.setText(mAudioDetail.getAlbum().get(0).getTitle());
                        subscribe.setText(mAudioDetail.getAlbum().get(0).getIs_sub()==1?"已订阅":"订阅");



                    }


                } catch (Exception e) {

                }


                break;
            case MSG_GET_RELATED_POST:{
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        mPostContents= JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class);
                        if (mAdapter == null) {
                            mAdapter = new RecommendContentGridAdapter(this, mPostContents);

                            mAdapter.setOnItemClickListener(new RecommendContentGridAdapter.OnItemClickListener() {


                                @Override
                                public void onItemClick(View view, ColumnContent content) {

                                }

                                @Override
                                public void onItemClick(View view, int pos) {

                                }
                            });
                            GridLayoutManager manager = new GridLayoutManager(this, 3);
                            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                @Override
                                public int getSpanSize(int position) {
                                    if (position == 0 &&mAdapter.hasHeadView()) {
                                        return 3;
                                    } else {
                                        return 1;
                                    }
                                }
                            });
                            recommend.setLayoutManager(manager);
                            recommend.setItemAnimator(new DefaultItemAnimator());


//                            recommend.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//                            //recyclerView.addItemDecoration(new MyItemDecoration());
//                            recommend.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

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

                    int totalNumber=jsonObject.getJSONObject("data").getInteger("total");
                    commentNum.setText(""+totalNumber);
                    commentNumber.setText(""+totalNumber);

                    mComments= JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), Comment.class);

                    if(mCommentListAdapter==null){
                        mCommentListAdapter=new CommentListAdapter(this,mComments);
                        listView.setAdapter(mCommentListAdapter);
                    }else{
                        mCommentListAdapter.updateView(mComments);
                    }

                }
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


}
