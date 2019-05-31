package com.shuangln.antfm.activity;


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
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jaeger.library.StatusBarUtil;
import com.mylhyl.circledialog.CircleDialog;
import com.shuangln.antfm.R;
import com.shuangln.antfm.customview.TopTitleBar;
import com.shuangln.antfm.dialog.AudioListDialog;
import com.shuangln.antfm.dialog.AudioSpeedDialog;
import com.shuangln.antfm.dialog.AudioTimerDialog;
import com.shuangln.antfm.entity.Audio;
import com.shuangln.antfm.entity.PlayerEvent;
import com.shuangln.antfm.service.AudioPlayerService;
import com.shuangln.antfm.service.IAudioPlayer;
import com.shuangln.antfm.utils.CommonUtils;
import com.shuangln.antfm.utils.ImageLoader;
import com.shuangln.antfm.utils.StatusBarManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AudioDetailActivity extends AppCompatActivity implements Handler.Callback{

    public static final String TAG = "AlbumDetailActivity";
    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
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
    @BindView(R.id.head)
    SimpleDraweeView head;
    @BindView(R.id.subscribe)
    TextView subscribe;
    @BindView(R.id.anchorHead)
    SimpleDraweeView anchorHead;
    @BindView(R.id.anchorName)
    TextView anchorName;
    @BindView(R.id.attentionNumber)
    TextView attentionNumber;
    @BindView(R.id.attention)
    TextView attention;


    private Handler mHandler;
    private Audio mAudio;
    private IAudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateTimerTask mUpdateTimerTask;
    WindowManager mWindowManager;
    View mFloatPlayer;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAudioPlayer=IAudioPlayer.Stub.asInterface(service);
            try{
                mAudioPlayer.playAudio(mAudio);


            }catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_detail);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        StatusBarManager.setImmersiveStatusBar(this, true);
        init();

    }

    private void init() {
        mHandler=new Handler(this);
        EventBus.getDefault().register(this);
        mAudio = getIntent().getParcelableExtra("Audio");

        Intent it = new Intent(this, AudioPlayerService.class);
        bindService(it, mConnection, Context.BIND_AUTO_CREATE);


        if (!TextUtils.isEmpty(mAudio.getAlbum().get(0).getCover())) {
            Uri uri = Uri.parse(mAudio.getAlbum().get(0).getCover());
            int width = CommonUtils.dip2px(145);
            int height = width;
            ImageLoader.showThumb(uri, logo, width, height);
        }


        audioTitle.setText(mAudio.getAudios().get(0).getTitle());


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(PlayerEvent event) {
        if(event.getEventName().equals("OnPrepared")){
            //1.改变播放按钮的状态
            //2.获取进度时长并显示
            //2.设置定时器更新进度条
            try{
                play.setBackgroundResource(R.drawable.ic_suspended);
                endTime.setText(CommonUtils.getShowTime(mAudioPlayer.getDuration()));
                seekBar.setMax((int)(mAudioPlayer.getDuration()));
                seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
                startUpdateTimer();
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }else if(event.getEventName().equals("OnTimerTick")){
            timer.setText(CommonUtils.getShowTime((long)event.getEventObject()));
        }else if(event.getEventName().equals("OnTimerFinish")){
            timer.setText("定时");
        }else if(event.getEventName().equals("OnTimerCancel")){
            timer.setText("定时");
        }
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        unbindService(mConnection);
        super.onDestroy();
    }

    @OnClick({R.id.play, R.id.previous, R.id.next, R.id.list, R.id.timer, R.id.rate, R.id.subscribe, R.id.attention})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.play:
                try{
                    int sta=mAudioPlayer.getPlayerState();
                    if(mAudioPlayer.getPlayerState()==IAliyunVodPlayer.PlayerState.Paused.ordinal()){
                        mAudioPlayer.start();
                        play.setBackgroundResource(R.drawable.ic_suspended);
                    }else if(mAudioPlayer.getPlayerState()==IAliyunVodPlayer.PlayerState.Started.ordinal()){
                        mAudioPlayer.pause();
                        play.setBackgroundResource(R.drawable.ic_play);
                    }
                }catch (RemoteException e){
                    e.printStackTrace();
                }

                break;
            case R.id.previous:
//                try{
//                    List<Audio> audioList= mAudioPlayer.getPlayerList();
//                    Audio currentAudio=mAudioPlayer.getCurrentAudio();
//                    for(int i=0;i<audioList.size();i++){
//                        if(audioList.get(i).getPost_audio_id()==currentAudio.getPost_audio_id()){
//                            if(AudioPlayerService.sSequence==AudioPlayerService.POSITIVE){
//                                i--;
//                                if(i<0){
//                                    i=audioList.size()-1;
//                                }
//                                mAudioPlayer.playAudio(audioList.get(i));
//                                break;
//                            }else{
//                                i++;
//
//                            }
//                        }
//                    }
//                }catch (RemoteException e){
//                    e.printStackTrace();
//                }
                mWindowManager.removeView(mFloatPlayer);

                break;
            case R.id.next:
                mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
                layoutParams.token = getWindow().getDecorView().getWindowToken(); // 必须要
                mFloatPlayer =LayoutInflater.from(AudioDetailActivity.this).inflate(R.layout.anchor_gridview_item,null,false);
                mWindowManager.addView(mFloatPlayer, layoutParams);
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
            case R.id.attention:
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }


    public class UpdateTimerTask extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try{
                        //1.更新当前时间
                        //2.更新当前进度条
                        currentTime.setText(CommonUtils.getShowTime(mAudioPlayer.getCurrentPosition()));
                        seekBar.setProgress((int)(mAudioPlayer.getCurrentPosition()));

                    }catch (RemoteException e){
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
                    try{
                        mAudioPlayer.seekTo(progress);
                    }catch (RemoteException e){
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
