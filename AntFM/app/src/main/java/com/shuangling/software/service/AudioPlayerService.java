package com.shuangling.software.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.WindowManager;

import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.AliyunVodPlayer;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.shuangling.software.entity.Audio;
import com.shuangling.software.event.PlayerEvent;

import org.greenrobot.eventbus.EventBus;
import java.util.List;
import java.util.Random;

public class AudioPlayerService extends Service {


    public final static int PLAY_CIRCLE = 0x0;                  //单曲循环
    public final static int PLAY_ORDER = 0x1;                   //顺序播放
    public final static int PLAY_RANDOM = 0x2;                  //随机播放
    public final static int PLAY_LOOP = 0x3;                    //循环播放

    public final static int POSITIVE = 0x0;                     //正序排列
    public final static int INVERTED = 0x1;                     //倒叙排列

    public static int sPlayOrder = PLAY_ORDER;
    public static int sSequence = POSITIVE;


    public static enum TimerType {
        Min10,
        Min20,
        Min30,
        Min60,
        PlayThis,
        Cancel;

    }

    public static enum PlaySpeed {
        Speed050,
        Speed075,
        Speed100,
        Speed125,
        Speed150;
    }

    private TimerType mTimerType=TimerType.Cancel;
    private PlaySpeed mPlaySpeed=PlaySpeed.Speed100;
    private AliyunVodPlayer mAliyunVodPlayer;
    private Audio mCurrentAudio;

//    private static AudioPlayerService sService;
//    public static AudioPlayerService getInstance() {
//        return sService;
//    }
    private List<Audio> mAudioList;


    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;

    //录制视频计时器
    class PlayCountDownTimer extends CountDownTimer {

        PlayCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            EventBus.getDefault().post(new PlayerEvent("OnTimerTick",millisUntilFinished));
        }

        @Override
        public void onFinish() {
            mAliyunVodPlayer.stop();
            mTimerType=TimerType.Cancel;
            EventBus.getDefault().post(new PlayerEvent("OnTimerFinish",null));
            mCurrentAudio=null;
        }

    }


    private Binder mBinder=new IAudioPlayer.Stub() {



        private PlayCountDownTimer countDownTimer;

        //显示悬浮窗口
        @Override
        public void showFloatPlayer(){
            if(mWindowManager==null){
                mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                mLayoutParams = new WindowManager.LayoutParams();
            }


//            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
//            mLayoutParams.token = activity.getWindow().getDecorView().getWindowToken();             //必须要
//            mWindowManager.addView(view, layoutParams);
        }


        //设置播放列表
        @Override
        public void setPlayerList(List<Audio> list) throws RemoteException {
            mAudioList=list;
        }

        //获取播放列表
        @Override
        public List<Audio> getPlayerList() throws RemoteException{
            return mAudioList;
        }

        //获取当前播放音频
        @Override
        public Audio getCurrentAudio(){
            return mCurrentAudio;
        }

        //如果要播放的和当前正在播放的是同一个音频，则跳过
        @Override
        public void playAudio(Audio audio) throws RemoteException{
            if (mCurrentAudio == null) {
                mCurrentAudio = audio;
                AliyunLocalSource.AliyunLocalSourceBuilder asb = new AliyunLocalSource.AliyunLocalSourceBuilder();
                asb.setSource(audio.getAudios().get(0).getAudio().getUrl());
                //aliyunVodPlayer.setLocalSource(asb.build());
                AliyunLocalSource mLocalSource = asb.build();
                mAliyunVodPlayer.prepareAsync(mLocalSource);
            } else {
                if (mCurrentAudio.getPost_audio_id() == audio.getPost_audio_id()) {
                    //如果处于暂停状态，则开始播放


                    //如果正在播放，则暂停播放
                } else {
                    mCurrentAudio = audio;
                    AliyunLocalSource.AliyunLocalSourceBuilder asb = new AliyunLocalSource.AliyunLocalSourceBuilder();
                    asb.setSource(audio.getAudios().get(0).getAudio().getUrl());
                    //aliyunVodPlayer.setLocalSource(asb.build());
                    AliyunLocalSource mLocalSource = asb.build();
                    mAliyunVodPlayer.prepareAsync(mLocalSource);
                }
            }

        }

        //设置播放器定时类型
        @Override
        public void setTimerType(int type){
            mTimerType=TimerType.values()[type];
            if(countDownTimer!=null){
                countDownTimer.cancel();
                EventBus.getDefault().post(new PlayerEvent("OnTimerCancel",null));
            }
            if(mTimerType==TimerType.Min10){
                countDownTimer=new PlayCountDownTimer(10*60000,500);
                countDownTimer.start();
            }else if(mTimerType==TimerType.Min20){
                countDownTimer=new PlayCountDownTimer(20*60000,500);
                countDownTimer.start();
            }else if(mTimerType==TimerType.Min30){
                countDownTimer=new PlayCountDownTimer(30*60000,500);
                countDownTimer.start();
            }else if(mTimerType==TimerType.Min60){
                countDownTimer=new PlayCountDownTimer(60*60000,500);
                countDownTimer.start();
            }else if(mTimerType==TimerType.PlayThis){
                countDownTimer=new PlayCountDownTimer(mAliyunVodPlayer.getDuration()-mAliyunVodPlayer.getCurrentPosition(),500);
                countDownTimer.start();
            }
        }


        @Override
        public int getTimerType(){
            return mTimerType.ordinal();
        }

        //获取播放器状态
        @Override
        public int getPlayerState() {
            int i=mAliyunVodPlayer.getPlayerState().ordinal();
            return mAliyunVodPlayer.getPlayerState().ordinal();
        }

        //开始播放
        @Override
        public void start() throws RemoteException{
            mAliyunVodPlayer.start();
        }

        //停止播放，在开始播放之后调用
        @Override
        public void stop() throws RemoteException{
            mAliyunVodPlayer.stop();
        }

        //暂停播放
        @Override
        public void pause() throws RemoteException{
            mAliyunVodPlayer.pause();
        }

        //重播，播放上一次的url
        @Override
        public void replay() throws RemoteException{
            mAliyunVodPlayer.replay();
        }

        //Seek，跳转到指定时间点的视频画面，时间单位为秒
        @Override
        public void seekTo(int ms) throws RemoteException{
            mAliyunVodPlayer.seekTo(ms);
        }

        //循环播放
        @Override
        public void setCirclePlay(boolean circle) throws RemoteException{
            mAliyunVodPlayer.setCirclePlay(true);
        }

        //视频设置清晰度   IAliyunVodPlayer.QualityValue.QUALITY_LOW
        @Override
        public void changeQuality(String quality) throws RemoteException{
            mAliyunVodPlayer.changeQuality(quality);
        }

        //获取播放的当前时间，单位为秒
        @Override
        public long getCurrentPosition() throws RemoteException{
            return mAliyunVodPlayer.getCurrentPosition();
        }

        //获取视频的总时长，单位为秒
        @Override
        public long getDuration() throws RemoteException{
            return mAliyunVodPlayer.getDuration();
        }

        //倍数播放支持0.5~2倍的设置，支持音频变速不变调
        @Override
        public void setPlaySpeed(int speed) throws RemoteException {
            mPlaySpeed=PlaySpeed.values()[speed];
            if(PlaySpeed.values()[speed]==PlaySpeed.Speed050){
                mAliyunVodPlayer.setPlaySpeed(0.5f);
            }else if(PlaySpeed.values()[speed]==PlaySpeed.Speed075){
                mAliyunVodPlayer.setPlaySpeed(0.75f);
            }else if(PlaySpeed.values()[speed]==PlaySpeed.Speed100){
                mAliyunVodPlayer.setPlaySpeed(1.0f);
            }else if(PlaySpeed.values()[speed]==PlaySpeed.Speed125){
                mAliyunVodPlayer.setPlaySpeed(1.25f);
            }else if(PlaySpeed.values()[speed]==PlaySpeed.Speed150){
                mAliyunVodPlayer.setPlaySpeed(1.5f);
            }

        }
        //倍数播放支持0.5~2倍的设置，支持音频变速不变调
        @Override
        public int getPlaySpeed() throws RemoteException {
            return mPlaySpeed.ordinal();
        }




        //设置自动播放，设置后调用prepare之后会自动开始播放，无需调start接口
        @Override
        public void setAutoPlay(boolean autoPlay) throws RemoteException {
            mAliyunVodPlayer.setAutoPlay(autoPlay);
        }

        //设置播放器音量（系统音量），值为0~100
        @Override
        public void setVolume(int volume) throws RemoteException{
            mAliyunVodPlayer.setVolume(volume);
        }

        //设置为静音
        @Override
        public void setMuteMode(boolean bMute) throws RemoteException{
            mAliyunVodPlayer.setMuteMode(bMute);
        }



        //设置亮度（系统亮度），值为0~100
        @Override
        public void setScreenBrightness(int brightness) throws RemoteException{
            mAliyunVodPlayer.setScreenBrightness(brightness);
        }


        //下一首
        @Override
        public void next() throws RemoteException{

            if(sPlayOrder==PLAY_CIRCLE){
                playAudio(mCurrentAudio);

            }else if(sPlayOrder==PLAY_ORDER){

                for (int i = 0; mAudioList!=null&&i < mAudioList.size(); i++) {
                    Audio pi = mAudioList.get(i);
                    if (pi.getId()==mCurrentAudio.getId()) {
                        if (i < mAudioList.size() - 1) {
                            i++;
                            playAudio(mAudioList.get(i));
                        }
                        break;
                    }
                }

            }else if(sPlayOrder==PLAY_RANDOM){
                Random random = new Random();
                int rand = random.nextInt(mAudioList.size() - 1);
                playAudio(mAudioList.get(rand));
            }else{
                for (int i = 0; mAudioList!=null&&i < mAudioList.size(); i++) {
                    Audio pi = mAudioList.get(i);
                    if (pi.getId()==mCurrentAudio.getId()) {
                        if (i ==mAudioList.size()-1) {
                            i=0;
                        }else{
                            i++;
                        }
                        playAudio(mAudioList.get(i));
                        break;
                    }
                }
            }
        }


        //上一首
        @Override
        public void previous() throws RemoteException{
            if(sPlayOrder==PLAY_CIRCLE){
                playAudio(mCurrentAudio);
            }else if(sPlayOrder==PLAY_ORDER){
                for (int i = 0; mAudioList!=null&&i < mAudioList.size(); i++) {
                    Audio pi = mAudioList.get(i);
                    if (pi.getId()==mCurrentAudio.getId()) {
                        if (i >0) {
                            i--;
                            playAudio(mAudioList.get(i));
                        }
                        break;
                    }
                }
            }else if(sPlayOrder==PLAY_RANDOM){
                Random random = new Random();
                int rand = random.nextInt(mAudioList.size() - 1);
                playAudio(mAudioList.get(rand));
            }else{
                //LOOP
                for (int i = 0; mAudioList!=null&&i < mAudioList.size(); i++) {
                    Audio pi = mAudioList.get(i);
                    if (pi.getId()==mCurrentAudio.getId()) {
                        if (i ==0) {
                            i=mAudioList.size()-1;
                        }else{
                            i--;
                        }
                        playAudio(mAudioList.get(i));
                        break;
                    }
                }
            }
        }




    };




    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mAliyunVodPlayer = new AliyunVodPlayer(this);
        mAliyunVodPlayer.setAutoPlay(true);
        mAliyunVodPlayer.setOnPreparedListener(new IAliyunVodPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                //准备完成触发
                EventBus.getDefault().post(new PlayerEvent("OnPrepared",mCurrentAudio));

            }
        });
        mAliyunVodPlayer.setOnFirstFrameStartListener(new IAliyunVodPlayer.OnFirstFrameStartListener() {
            @Override
            public void onFirstFrameStart() {
                //首帧显示触发
            }
        });
        mAliyunVodPlayer.setOnErrorListener(new IAliyunVodPlayer.OnErrorListener() {
            @Override
            public void onError(int arg0, int arg1, String msg) {
                //出错时处理，查看接口文档中的错误码和错误消息
            }
        });
        mAliyunVodPlayer.setOnCompletionListener(new IAliyunVodPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                //播放正常完成时触发
                try {
                    ((IAudioPlayer.Stub)mBinder).next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        mAliyunVodPlayer.setOnSeekCompleteListener(new IAliyunVodPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete() {
                //seek完成时触发
            }
        });
        mAliyunVodPlayer.setOnStoppedListner(new IAliyunVodPlayer.OnStoppedListener() {
            @Override
            public void onStopped() {
                //使用stop功能时触发
            }
        });
        mAliyunVodPlayer.setOnChangeQualityListener(new IAliyunVodPlayer.OnChangeQualityListener() {
            @Override
            public void onChangeQualitySuccess(String finalQuality) {
                //视频清晰度切换成功后触发
            }

            @Override
            public void onChangeQualityFail(int code, String msg) {
                //视频清晰度切换失败时触发
            }
        });
        mAliyunVodPlayer.setOnCircleStartListener(new IAliyunVodPlayer.OnCircleStartListener() {
            @Override
            public void onCircleStart() {
                //循环播放开始
            }
        });

    }


    @Override
    public void onDestroy() {
        mAliyunVodPlayer.stop();
        super.onDestroy();
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
