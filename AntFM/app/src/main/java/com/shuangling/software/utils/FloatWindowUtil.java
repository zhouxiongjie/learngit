package com.shuangling.software.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aliyun.player.IPlayer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.activity.AnchorOrOrganizationDetailActivityH5;
import com.shuangling.software.activity.ArticleDetailActivity;
import com.shuangling.software.activity.ArticleDetailActivity02;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.activity.RadioDetailActivity;
import com.shuangling.software.customview.FloatView;
import com.shuangling.software.customview.ProgressCircleImageView;
import com.shuangling.software.entity.AudioInfo;
import com.shuangling.software.event.PlayerEvent;
import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.service.IAudioPlayer;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.Timer;
import java.util.TimerTask;
import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by zxj
 * Date：2018/9/29 下午4:29
 * Desc：
 */
public class FloatWindowUtil {

    public static final int REQUEST_PERMISSION_CODE = 0x0110;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private FloatView mView;
    private Point point = new Point();
    private int statusBarHeight = 0;
    private Context mContext;

    private ProgressCircleImageView mProgressCircleImageView;
    private LinearLayout mRoot;
    private LinearLayout mControllerLayout;
    private ImageView mPlay;
    private ImageView mNext;
    private ImageView mClose;

    private Timer mTimer;
    private UpdateTimerTask mUpdateTimerTask;
    private Handler mHandler=new Handler(Looper.getMainLooper());

    private OnPermissionListener mOnPermissionListener;
    public void addOnPermissionListener(OnPermissionListener permissionListener){
        mOnPermissionListener=permissionListener;
    }

    private IAudioPlayer mAudioPlayer;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAudioPlayer = IAudioPlayer.Stub.asInterface(service);
            initPlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private FloatWindowUtil() {
        mContext=MyApplication.getInstance();
    }

    private static class SingletonInstance {
        @SuppressLint("StaticFieldLeak")
        private static final FloatWindowUtil INSTANCE = new FloatWindowUtil();
    }

    public static FloatWindowUtil getInstance() {
        return SingletonInstance.INSTANCE;
    }


    public void setPermission(){
        if (checkFloatWindowPermission()) {
        } else {
            if(mOnPermissionListener!=null){
                mOnPermissionListener.showPermissionDialog();
            }
        }
    }

    public void showFloatWindow(){

        if (checkFloatWindowPermission()) {
            showWindow();
        }else{

        }
    }





    public boolean checkFloatWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(mContext);
        }
        return true;
    }



    @SuppressLint("CheckResult")
    private void showWindow() {
        if (null == mWindowManager && null == mView) {
            mWindowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
            mView = new FloatView(mContext);
            mView.setElevation(CommonUtils.dip2px(5));
            mProgressCircleImageView=mView.findViewById(R.id.progressImageView);
            mRoot=mView.findViewById(R.id.root);
            mControllerLayout=mView.findViewById(R.id.controllerLayout);
            mPlay=mView.findViewById(R.id.play);
            mNext=mView.findViewById(R.id.next);
            mClose=mView.findViewById(R.id.close);
            //mProgressCircleImageView.setDisableCircularTransformation(true);
            Glide.with(mContext).load(R.drawable.player_dynamic).into(mProgressCircleImageView);
            mWindowManager.getDefaultDisplay().getSize(point);

            mLayoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }
            mLayoutParams.format = PixelFormat.RGBA_8888;                               //窗口透明
            mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;                         //窗口位置
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            // 可以修改View的初始位置
            int width=mView.getMeasuredWidth();
            mLayoutParams.x = mWindowManager.getDefaultDisplay().getWidth()-CommonUtils.dip2px(50);
            mLayoutParams.y = point.y/2;
            mWindowManager.addView(mView, mLayoutParams);
            mView.addOnActionOutListener(new FloatView.OnActionOutListener() {
                @Override
                public void actionOut() {
                    shrinkWindow();
                }
            });

            EventBus.getDefault().register(this);
            Intent it = new Intent(mContext, AudioPlayerService.class);
            mContext.bindService(it, mConnection, Context.BIND_AUTO_CREATE);

        }else{
            visibleWindow();
        }
    }



    public void dismissWindow() {
        try{
            if (mWindowManager != null && mView != null) {
                mWindowManager.removeViewImmediate(mView);
                mAudioPlayer.stop();
                EventBus.getDefault().unregister(this);
                mContext.unbindService(mConnection);
                mView=null;
                mWindowManager=null;
            }
        }catch (RemoteException e){

        }

    }

    private void initPlayer() {
        try{
            mProgressCircleImageView.setDuration(mAudioPlayer.getDuration());
            mProgressCircleImageView.updateProgress(mAudioPlayer.getCurrentPosition());
            if(mAudioPlayer.getPlayerState()==IPlayer.started){
                mPlay.setImageResource(R.drawable.float_pause_icon);
                Glide.with(mContext).load(R.drawable.player_dynamic).into(mProgressCircleImageView);
                startUpdateTimer();
            }else{
                mPlay.setImageResource(R.drawable.float_play_icon);
                Glide.with(mContext).load(R.drawable.player_static).into(mProgressCircleImageView);
            }
        }catch (RemoteException e){

        }

        mProgressCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mControllerLayout.getVisibility()==View.GONE){

                    if(mLayoutParams.x>CommonUtils.dip2px(10)){
                        //右边
                        ValueAnimator animator = ValueAnimator.ofInt(CommonUtils.dip2px(40), CommonUtils.dip2px(198)).setDuration(100);
                        animator.setInterpolator(new LinearInterpolator());
                        mControllerLayout.setVisibility(View.VISIBLE);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                mLayoutParams.width = (int) valueAnimator.getAnimatedValue();
                                mLayoutParams.x=point.x-mLayoutParams.width-CommonUtils.dip2px(10);
                                updateViewLayout();
                            }
                        });
                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                animation.cancel();
                                LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)mProgressCircleImageView.getLayoutParams();
                                lp.leftMargin=10;
                                lp.width=CommonUtils.dip2px(30);
                                lp.height=lp.width;
                                mProgressCircleImageView.setLayoutParams(lp);

                                try{
                                    String logo=mAudioPlayer.getCurrentAudio()!=null?mAudioPlayer.getCurrentAudio().getLogo():null;
                                    if(!TextUtils.isEmpty(logo)){
                                        //mProgressCircleImageView.setDisableCircularTransformation(false);
                                        Glide.with(mContext).load(logo).into(mProgressCircleImageView);
                                    }else{
                                        //mProgressCircleImageView.setDisableCircularTransformation(false);
                                        if(mAudioPlayer.getPlayerState()==IPlayer.started){
                                            Glide.with(mContext).load(R.drawable.player_dynamic).into(mProgressCircleImageView);
                                        }else {
                                            Glide.with(mContext).load(R.drawable.player_static).into(mProgressCircleImageView);
                                        }

                                    }

                                }catch (RemoteException e){

                                }
                                //Glide.with(mContext).load(R.drawable.player_gif).diskCacheStrategy(DiskCacheStrategy.ALL).into(mProgressCircleImageView);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        animator.start();

                    }else{
                        //左边
                        ValueAnimator animator = ValueAnimator.ofInt(CommonUtils.dip2px(40), CommonUtils.dip2px(198)).setDuration(100);
                        animator.setInterpolator(new LinearInterpolator());
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                mLayoutParams.width = (int) valueAnimator.getAnimatedValue();
                                updateViewLayout();
                            }
                        });
                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                mControllerLayout.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {

                                LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)mProgressCircleImageView.getLayoutParams();
                                lp.leftMargin=10;
                                lp.width=CommonUtils.dip2px(30);
                                lp.height=lp.width;
                                mProgressCircleImageView.setLayoutParams(lp);
                                try{
                                    String logo=mAudioPlayer.getCurrentAudio()!=null?mAudioPlayer.getCurrentAudio().getLogo():null;
                                    if(!TextUtils.isEmpty(logo)){
                                        //mProgressCircleImageView.setDisableCircularTransformation(false);
                                        Glide.with(mContext).load(logo).into(mProgressCircleImageView);
                                    }else{
                                        //mProgressCircleImageView.setDisableCircularTransformation(false);
                                        if(mAudioPlayer.getPlayerState()==IPlayer.started){
                                            Glide.with(mContext).load(R.drawable.player_dynamic).into(mProgressCircleImageView);
                                        }else {
                                            Glide.with(mContext).load(R.drawable.player_static).into(mProgressCircleImageView);
                                        }
                                    }

                                }catch (RemoteException e){

                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        animator.start();
                    }




                }else {
                    try{
                        AudioInfo audio=mAudioPlayer.getCurrentAudio();
                        if(audio.getIsRadio()==0){
                            Intent it=new Intent(mContext,AudioDetailActivity.class);
                            it.putExtra("audioId",audio.getId());
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                            mContext.startActivity(it);
                        }else if(audio.getIsRadio()==1){
                            Intent it=new Intent(mContext,RadioDetailActivity.class);
                            it.putExtra("radioId",audio.getId());
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                            mContext.startActivity(it);
                        }else {
                            Intent it = new Intent(mContext, ArticleDetailActivity02.class);
                            it.putExtra("articleId", audio.getArticleId());
                            mContext.startActivity(it);
                        }

                    }catch (RemoteException e){

                    }
                }

            }
        });


        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    int sta = mAudioPlayer.getPlayerState();
                    if (mAudioPlayer.getPlayerState() == IPlayer.paused) {
                        mAudioPlayer.start();
                        mPlay.setImageResource(R.drawable.float_pause_icon);

                    } else if (mAudioPlayer.getPlayerState() == IPlayer.started) {
                        mAudioPlayer.pause();
                        mPlay.setImageResource(R.drawable.float_play_icon);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mAudioPlayer.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismissWindow();
            }
        });





        final int mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        //设置触摸滑动事件
        mView.setOnTouchListener(new View.OnTouchListener() {
            int startX, startY;  //起始点
            boolean isPerformClick;  //是否点击
            int finalMoveX;  //最后通过动画将mView的X轴坐标移动到finalMoveX


            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("click", "onTouch: " + event.getAction());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        isPerformClick = true;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //判断是CLICK还是MOVE
                        //只要移动过，就认为不是点击
                        if (Math.abs(startX - event.getX()) >= mTouchSlop || Math.abs(startY - event.getY()) >= mTouchSlop) {
                            isPerformClick = false;
                        }

                        //mLayoutParams.x = (int) (event.getRawX());
                        mLayoutParams.x = (int) (event.getRawX() - startX);
                        //这里修复了刚开始移动的时候，悬浮窗的y坐标是不正确的，要减去状态栏的高度，可以将这个去掉运行体验一下
                        if(event.getRawY()>CommonUtils.dip2px(100)&&event.getRawY()<mWindowManager.getDefaultDisplay().getHeight()-CommonUtils.dip2px(100)){
                            mLayoutParams.y = (int) (event.getRawY() - startY - statusBarHeight);
                            //mLayoutParams.y = (int) (event.getRawY() );

                        }
                        updateViewLayout();//更新mView 的位置
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (isPerformClick) {
                            //mView.performClick();
                        }
                        //判断mView是在Window中的位置，以中间为界
                        if (mLayoutParams.x + mView.getMeasuredWidth() / 2 >= mWindowManager.getDefaultDisplay().getWidth() / 2) {
                            finalMoveX = mWindowManager.getDefaultDisplay().getWidth() - mView.getMeasuredWidth()-CommonUtils.dip2px(10);
                        } else {
                            finalMoveX = CommonUtils.dip2px(10);
                        }
                        stickToSide();
                        return !isPerformClick;
                }
                return false;
            }

            private void stickToSide() {
                ValueAnimator animator = ValueAnimator.ofInt(mLayoutParams.x, finalMoveX).setDuration(Math.abs(mLayoutParams.x - finalMoveX)/5);
                //animator.setInterpolator(new BounceInterpolator());
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        mLayoutParams.x = (int) valueAnimator.getAnimatedValue();
                        updateViewLayout();
                    }
                });
                animator.start();
            }
        });
    }


    private void updateViewLayout() {
        if (null != mView && null != mLayoutParams) {
            mWindowManager.updateViewLayout(mView, mLayoutParams);
            Log.i("mLayoutParams","x="+mLayoutParams.x+",y="+mLayoutParams.y);
        }
    }

    public void shrinkWindow() {


        if(mControllerLayout.getVisibility()==View.VISIBLE){

            ValueAnimator animator = ValueAnimator.ofInt(CommonUtils.dip2px(198), CommonUtils.dip2px(40)).setDuration(100);
            if(mLayoutParams.x >CommonUtils.dip2px(10)){
                //右边
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        mLayoutParams.width = (int) valueAnimator.getAnimatedValue();
                        mLayoutParams.x =point.x-mLayoutParams.width-CommonUtils.dip2px(10);
                        updateViewLayout();
                    }
                });
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animation.cancel();
                        mControllerLayout.setVisibility(View.GONE);
                        LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)mProgressCircleImageView.getLayoutParams();
                        lp.leftMargin=0;
                        lp.width=CommonUtils.dip2px(40);
                        lp.height=lp.width;
                        mProgressCircleImageView.setLayoutParams(lp);
                        mLayoutParams.x =point.x-CommonUtils.dip2px(50);
                        mLayoutParams.width =CommonUtils.dip2px(40);
                        updateViewLayout();
                        try{
                            if(mAudioPlayer.getPlayerState()==IPlayer.started){
                                Glide.with(mContext).load(R.drawable.player_dynamic).into(mProgressCircleImageView);
                            }else{
                                Glide.with(mContext).load(R.drawable.player_static).into(mProgressCircleImageView);
                            }
                        }catch (RemoteException e){

                        }


                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animator.start();
            }else{
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        mLayoutParams.width = (int) valueAnimator.getAnimatedValue();
                        updateViewLayout();
                    }
                });
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animation.cancel();
                        mControllerLayout.setVisibility(View.GONE);
                        LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)mProgressCircleImageView.getLayoutParams();
                        lp.leftMargin=0;
                        lp.width=CommonUtils.dip2px(40);
                        lp.height=lp.width;
                        mProgressCircleImageView.setLayoutParams(lp);
                        mLayoutParams.x =CommonUtils.dip2px(10);
                        mLayoutParams.width =CommonUtils.dip2px(40);
                        updateViewLayout();
                        try{
                            if(mAudioPlayer.getPlayerState()==IPlayer.started){
                                Glide.with(mContext).load(R.drawable.player_dynamic).into(mProgressCircleImageView);
                            }else{
                                Glide.with(mContext).load(R.drawable.player_static).into(mProgressCircleImageView);
                            }
                        }catch (RemoteException e){

                        }


                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animator.start();
            }


        }

    }

    public void hideWindow() {
        if (mView != null) {
            mView.setVisibility(View.GONE);
        }
    }


    public boolean isVisible(){
        if (mView != null) {
            return mView.getVisibility()==View.VISIBLE;
        }
        return false;
    }

    public void visibleWindow() {
        if (mView != null) {
            mView.setVisibility(View.VISIBLE);
        }
    }

    public interface OnPermissionListener {
        void showPermissionDialog();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(PlayerEvent event) {
        if (event.getEventName().equals("OnPrepared")) {

            //1.改变播放按钮的状态
            //2.获取进度时长并显示
            //2.设置定时器更新进度条
            try{
                mProgressCircleImageView.setDuration(mAudioPlayer.getDuration());
                mPlay.setImageResource(R.drawable.float_pause_icon);
                if(mControllerLayout.getVisibility()==View.GONE){
                    Glide.with(mContext).load(R.drawable.player_dynamic).into(mProgressCircleImageView);
                }
                startUpdateTimer();
            }catch (RemoteException e){

            }


        } else if (event.getEventName().equals("OnTimerTick")) {

        } else if (event.getEventName().equals("OnTimerFinish")) {

        }else if (event.getEventName().equals("OnCompleted")) {
            if(mControllerLayout.getVisibility()==View.GONE){
                Glide.with(mContext).load(R.drawable.player_static).into(mProgressCircleImageView);
            }
            mPlay.setImageResource(R.drawable.float_play_icon);

        }else if (event.getEventName().equals("OnTimerCancel")) {

        }else if(event.getEventName().equals("OnPause")){

            try{
                mPlay.setImageResource(R.drawable.float_play_icon);
                cancelUpdateTimer();
                if(mControllerLayout.getVisibility()==View.GONE){
                    Glide.with(mContext).load(R.drawable.player_static).into(mProgressCircleImageView);
                }else{
                    String logo=mAudioPlayer.getCurrentAudio()!=null?mAudioPlayer.getCurrentAudio().getLogo():null;
                    if(!TextUtils.isEmpty(logo)){
                        //mProgressCircleImageView.setDisableCircularTransformation(false);
                        Glide.with(mContext).load(logo).into(mProgressCircleImageView);
                    }else{
                        //mProgressCircleImageView.setDisableCircularTransformation(false);

                        Glide.with(mContext).load(R.drawable.player_static).into(mProgressCircleImageView);


                    }
                }
            }catch (RemoteException e){

            }

        }else if(event.getEventName().equals("OnStart")){
            try{
                mProgressCircleImageView.setDuration(mAudioPlayer.getDuration());
                mPlay.setImageResource(R.drawable.float_pause_icon);
                if(mControllerLayout.getVisibility()==View.GONE){
                    Glide.with(mContext).load(R.drawable.player_dynamic).into(mProgressCircleImageView);
                }else{
                    String logo=mAudioPlayer.getCurrentAudio()!=null?mAudioPlayer.getCurrentAudio().getLogo():null;
                    if(!TextUtils.isEmpty(logo)){
                        //mProgressCircleImageView.setDisableCircularTransformation(false);
                        Glide.with(mContext).load(logo).into(mProgressCircleImageView);
                    }else{
                        //mProgressCircleImageView.setDisableCircularTransformation(false);

                        Glide.with(mContext).load(R.drawable.player_dynamic).into(mProgressCircleImageView);


                    }


                }
                startUpdateTimer();
            }catch (RemoteException e){

            }

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

    public class UpdateTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                //1.更新当前时间
                //2.更新当前进度条
                if (mAudioPlayer.getPlayerState() == IPlayer.started) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                mProgressCircleImageView.updateProgress(mAudioPlayer.getCurrentPosition());
                            }catch (RemoteException e){

                            }

                        }
                    });

                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }




        }
    }



}