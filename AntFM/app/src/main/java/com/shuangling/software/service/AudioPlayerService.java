package com.shuangling.software.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.WindowManager;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.UrlSource;
import com.aliyun.player.source.VidAuth;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.entity.AudioInfo;
import com.shuangling.software.entity.ResAuthInfo;
import com.shuangling.software.event.PlayerEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ServerInfo;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;

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
    private AliPlayer mAliyunVodPlayer;
    
    private AudioInfo mCurrentAudio;
    private List<AudioInfo> mAudioList;

    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;

    private int mPlayerState = IPlayer.idle;
    private  long mCurrentPosition;

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


        }


        //设置播放列表
//        @Override
//        public void setPlayerList(List<Audio> list) throws RemoteException {
//            mAudioList=list;
//        }

        @Override
        public void setPlayerList(List<AudioInfo> list) throws RemoteException {
            mAudioList=list;
        }

        //获取播放列表
//        @Override
//        public List<Audio> getPlayerList() throws RemoteException{
//            return mAudioList;
//        }

        @Override
        public List<AudioInfo> getPlayerList() throws RemoteException{
            return mAudioList;
        }

        //获取当前播放音频
        @Override
        public AudioInfo getCurrentAudio(){
            return mCurrentAudio;
        }

        //如果要播放的和当前正在播放的是同一个音频，则跳过
//        @Override
//        public void playAudio(Audio audio) throws RemoteException{
//            if (mCurrentAudio == null) {
//                mCurrentAudio = audio;
//                AliyunLocalSource.AliyunLocalSourceBuilder asb = new AliyunLocalSource.AliyunLocalSourceBuilder();
//                asb.setSource(audio.getAudios().get(0).getAudio().getUrl());
//                //aliyunVodPlayer.setLocalSource(asb.build());
//                AliyunLocalSource mLocalSource = asb.build();
//                mAliyunVodPlayer.prepareAsync(mLocalSource);
//            } else {
//                if (mCurrentAudio.getPost_audio_id() == audio.getPost_audio_id()) {
//                    //如果处于暂停状态，则开始播放
//
//
//                    //如果正在播放，则暂停播放
//                } else {
//                    mCurrentAudio = audio;
//                    AliyunLocalSource.AliyunLocalSourceBuilder asb = new AliyunLocalSource.AliyunLocalSourceBuilder();
//                    asb.setSource(audio.getAudios().get(0).getAudio().getUrl());
//                    //aliyunVodPlayer.setLocalSource(asb.build());
//                    AliyunLocalSource mLocalSource = asb.build();
//                    mAliyunVodPlayer.prepareAsync(mLocalSource);
//                }
//            }
//
//        }


        @Override
        public void playAudio (AudioInfo audioInfo) throws RemoteException{
            if (mCurrentAudio == null) {
                mCurrentAudio = audioInfo;
                if(audioInfo.getIsRadio()==1){

                    UrlSource urlSource = new UrlSource();
                    urlSource.setUri(audioInfo.getUrl());
                    urlSource.setTitle(audioInfo.getTitle());
                    mAliyunVodPlayer.setDataSource(urlSource);
                    mAliyunVodPlayer.prepare();
                }else{
                    getAudioAuth(audioInfo.getSourceId());
                }

            } else {
                if (mCurrentAudio.getId() == audioInfo.getId()) {
                    //如果处于暂停状态，则开始播放
                    if(mPlayerState!=IPlayer.prepared&&
                            mPlayerState!=IPlayer.started&&
                            mPlayerState!=IPlayer.paused){
                        mCurrentAudio = audioInfo;
                        if(audioInfo.getIsRadio()==1){

                            UrlSource urlSource = new UrlSource();
                            urlSource.setUri(audioInfo.getUrl());
                            urlSource.setTitle(audioInfo.getTitle());
                            mAliyunVodPlayer.setDataSource(urlSource);
                            mAliyunVodPlayer.prepare();
                        }else{
                            getAudioAuth(audioInfo.getSourceId());
                        }

                    }else{
                        EventBus.getDefault().post(new PlayerEvent("OnPrepared",mCurrentAudio));
                    }

                    //如果正在播放，则暂停播放
                } else {
                    mCurrentAudio = audioInfo;
                    if(audioInfo.getIsRadio()==1){

                        UrlSource urlSource = new UrlSource();
                        urlSource.setUri(audioInfo.getUrl());
                        urlSource.setTitle(audioInfo.getTitle());
                        mAliyunVodPlayer.setDataSource(urlSource);
                        mAliyunVodPlayer.prepare();
                    }else{
                        getAudioAuth(audioInfo.getSourceId());
                    }
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
                countDownTimer=new PlayCountDownTimer(mAliyunVodPlayer.getDuration()-mCurrentPosition,500);
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
            return mPlayerState;
        }

        //开始播放
        @Override
        public void start() throws RemoteException{
            EventBus.getDefault().post(new PlayerEvent("OnStart",mCurrentAudio));
            mAliyunVodPlayer.start();
        }

        //停止播放，在开始播放之后调用
        @Override
        public void stop() throws RemoteException{
            EventBus.getDefault().post(new PlayerEvent("OnStop",mCurrentAudio));
            mAliyunVodPlayer.stop();
        }

        //暂停播放
        @Override
        public void pause() throws RemoteException{
            EventBus.getDefault().post(new PlayerEvent("OnPause",mCurrentAudio));
            mAliyunVodPlayer.pause();
        }

        //重播，播放上一次的url
        @Override
        public void replay() throws RemoteException{
            mAliyunVodPlayer.prepare();
        }

        //Seek，跳转到指定时间点的视频画面，时间单位为秒
        @Override
        public void seekTo(int ms) throws RemoteException{
            mAliyunVodPlayer.seekTo(ms);
        }

        //循环播放
        @Override
        public void setCirclePlay(boolean circle) throws RemoteException{
            mAliyunVodPlayer.setLoop(true);
        }

        //视频设置清晰度   IPlayer.QualityValue.QUALITY_LOW
        @Override
        public void changeQuality(String quality) throws RemoteException{
            //mAliyunVodPlayer.changeQuality(quality);
        }

        //获取播放的当前时间，单位为秒
        @Override
        public long getCurrentPosition() throws RemoteException{
            return mCurrentPosition;
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
                mAliyunVodPlayer.setSpeed(0.5f);
            }else if(PlaySpeed.values()[speed]==PlaySpeed.Speed075){
                mAliyunVodPlayer.setSpeed(0.75f);
            }else if(PlaySpeed.values()[speed]==PlaySpeed.Speed100){
                mAliyunVodPlayer.setSpeed(1.0f);
            }else if(PlaySpeed.values()[speed]==PlaySpeed.Speed125){
                mAliyunVodPlayer.setSpeed(1.25f);
            }else if(PlaySpeed.values()[speed]==PlaySpeed.Speed150){
                mAliyunVodPlayer.setSpeed(1.5f);
            }
            EventBus.getDefault().post(new PlayerEvent("SpeedChanged",mPlaySpeed));

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
            mAliyunVodPlayer.setMute(bMute);
        }



        //设置亮度（系统亮度），值为0~100
        @Override
        public void setScreenBrightness(int brightness) throws RemoteException{
            //mAliyunVodPlayer.setScreenBrightness(brightness);
        }


        //下一首
        @Override
        public void next() throws RemoteException{

//            if(sPlayOrder==PLAY_CIRCLE){
//                playAudio(mCurrentAudio);
//            }
            if(sPlayOrder==PLAY_CIRCLE||sPlayOrder==PLAY_ORDER){

                if(sSequence==POSITIVE){
                    for (int i = 0; mAudioList!=null&&i < mAudioList.size(); i++) {
                        AudioInfo pi = mAudioList.get(i);
                        if (pi.getId()==mCurrentAudio.getId()) {
                            if (i < mAudioList.size() - 1) {
                                i++;
                                playAudio(mAudioList.get(i));
                            }
                            break;
                        }
                    }
                }else{
                    for (int i = 0; mAudioList!=null&&i < mAudioList.size(); i++) {
                        AudioInfo pi = mAudioList.get(i);
                        if (pi.getId()==mCurrentAudio.getId()) {
                            if (i >0) {
                                i--;
                                playAudio(mAudioList.get(i));
                            }
                            break;
                        }
                    }
                }


            }else if(sPlayOrder==PLAY_RANDOM){
                Random random = new Random();
                int rand = random.nextInt(mAudioList.size() - 1);
                playAudio(mAudioList.get(rand));
            }else{
                if(sSequence==POSITIVE){
                    for (int i = 0; mAudioList!=null&&i < mAudioList.size(); i++) {
                        AudioInfo pi = mAudioList.get(i);
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
                }else {
                    for (int i = 0; mAudioList!=null&&i < mAudioList.size(); i++) {
                        AudioInfo pi = mAudioList.get(i);
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
        }


        //上一首
        @Override
        public void previous() throws RemoteException{
            if(sPlayOrder==PLAY_CIRCLE){
                playAudio(mCurrentAudio);
            }else if(sPlayOrder==PLAY_ORDER){

                if(sSequence==POSITIVE){
                    for (int i = 0; mAudioList!=null&&i < mAudioList.size(); i++) {
                        AudioInfo pi = mAudioList.get(i);
                        if (pi.getId()==mCurrentAudio.getId()) {
                            if (i >0) {
                                i--;
                                playAudio(mAudioList.get(i));
                            }
                            break;
                        }
                    }
                }else{
                    for (int i = 0; mAudioList!=null&&i < mAudioList.size(); i++) {
                        AudioInfo pi = mAudioList.get(i);
                        if (pi.getId()==mCurrentAudio.getId()) {
                            if (i <mAudioList.size()-1) {
                                i++;
                                playAudio(mAudioList.get(i));
                            }
                            break;
                        }
                    }
                }

            }else if(sPlayOrder==PLAY_RANDOM){
                Random random = new Random();
                int rand = random.nextInt(mAudioList.size() - 1);
                playAudio(mAudioList.get(rand));
            }else{
                //LOOP
                if(sSequence==POSITIVE){
                    for (int i = 0; mAudioList!=null&&i < mAudioList.size(); i++) {
                        AudioInfo pi = mAudioList.get(i);
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
                }else {
                    for (int i = 0; mAudioList!=null&&i < mAudioList.size(); i++) {
                        AudioInfo pi = mAudioList.get(i);
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
        }




    };




    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mAliyunVodPlayer = AliPlayerFactory.createAliPlayer(getApplicationContext());
        mAliyunVodPlayer.setAutoPlay(true);
        //mAliyunVodPlayer.setReferer(ServerInfo.h5IP);
        mAliyunVodPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                //准备完成触发
                EventBus.getDefault().post(new PlayerEvent("OnPrepared",mCurrentAudio));

                addPlayTimes(mCurrentAudio.getId());

            }
        });
        mAliyunVodPlayer.setOnRenderingStartListener(new IPlayer.OnRenderingStartListener() {
            @Override
            public void onRenderingStart() {

            }

        });
        mAliyunVodPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {

                if(errorInfo!=null&&errorInfo.getMsg()!=null){
                    ToastUtils.show(errorInfo.getMsg());
                }
            }

        });
        mAliyunVodPlayer.setOnCompletionListener(new IPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                //播放正常完成时触发
                EventBus.getDefault().post(new PlayerEvent("OnCompleted",mCurrentAudio));

            }
        });
        mAliyunVodPlayer.setOnSeekCompleteListener(new IPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete() {
                //seek完成时触发
            }
        });
        mAliyunVodPlayer.setOnStateChangedListener(new IPlayer.OnStateChangedListener() {
            @Override
            public void onStateChanged(int i) {
                mPlayerState=i;
                if(mPlayerState==IPlayer.completion){
                    try {
                        if(sPlayOrder==PLAY_CIRCLE){
                            ((IAudioPlayer.Stub)mBinder).playAudio(mCurrentAudio);
                        }else{
                            ((IAudioPlayer.Stub)mBinder).next();
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            }


        });
        mAliyunVodPlayer.setOnTrackChangedListener(new IPlayer.OnTrackChangedListener() {
            @Override
            public void onChangedSuccess(TrackInfo trackInfo) {

            }

            @Override
            public void onChangedFail(TrackInfo trackInfo, ErrorInfo errorInfo) {

            }

        });
        mAliyunVodPlayer.setOnInfoListener(new IPlayer.OnInfoListener() {
            @Override
            public void onInfo(InfoBean infoBean) {
                if(infoBean.getCode() == InfoCode.LoopingStart){
                    //循环开始
                }else if(infoBean.getCode() == InfoCode.CurrentPosition){
                    mCurrentPosition=infoBean.getExtraValue();
                }
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


    private void getAudioAuth(int sourceId) {
        String url = ServerInfo.vms + "/v1/sources/" +sourceId+ "/playAuth";
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, params, new OkHttpCallback(getApplicationContext()) {


            @Override
            public void onResponse(Call call, String response) throws IOException {

                JSONObject jsonObject = JSONObject.parseObject(response);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                    ResAuthInfo resAuthInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), ResAuthInfo.class);

                    VidAuth vidAuth = new VidAuth();
                    vidAuth.setPlayAuth(resAuthInfo.getPlay_auth());
                    vidAuth.setVid(resAuthInfo.getVideo_id());
                    vidAuth.setRegion("cn-shanghai");

                    mAliyunVodPlayer.setDataSource(vidAuth);
                    mAliyunVodPlayer.prepare();

                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {

            }
        });
    }

    private void addPlayTimes(int id) {
        String url = ServerInfo.serviceIP + ServerInfo.addPlayTimes+id;
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, params, new OkHttpCallback(getApplicationContext()) {


            @Override
            public void onResponse(Call call, String response) throws IOException {

            }

            @Override
            public void onFailure(Call call, Exception exception) {

            }
        });
    }
}
