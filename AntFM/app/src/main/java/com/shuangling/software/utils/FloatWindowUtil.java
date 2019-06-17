package com.shuangling.software.utils;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.ProgressCircleImageView;

import cn.jake.share.frdialog.dialog.FRDialog;
import cn.jake.share.frdialog.interfaces.FRDialogClickListener;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by manji
 * Date：2018/9/29 下午4:29
 * Desc：
 */
public class FloatWindowUtil{

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private View mView;
    private Point point = new Point();
    private int statusBarHeight = 0;

    private OnPermissionListener mOnPermissionListener;

    public void addOnPermissionListener(OnPermissionListener permissionListener){
        mOnPermissionListener=permissionListener;
    }

    private FloatWindowUtil() {
    }

    private static class SingletonInstance {
        @SuppressLint("StaticFieldLeak")
        private static final FloatWindowUtil INSTANCE = new FloatWindowUtil();
    }

    public static FloatWindowUtil getInstance() {
        return SingletonInstance.INSTANCE;
    }


    public void showFloatWindow(Context context){

        if (checkFloatWindowPermission(context)) {
            showWindow(context);
        } else {
            if(mOnPermissionListener!=null){
                mOnPermissionListener.showPermissionDialog();
            }
        }
    }


    public static boolean checkFloatWindowPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    @SuppressLint("CheckResult")
    private void showWindow(Context context) {
        if (null == mWindowManager && null == mView) {
            mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            mView = LayoutInflater.from(context).inflate(R.layout.float_player, null);
            mWindowManager.getDefaultDisplay().getSize(point);
            initListener(context);
            mLayoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }
            mLayoutParams.format = PixelFormat.RGBA_8888;                           //窗口透明
            mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;        //窗口位置
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            // 可以修改View的初始位置
            mLayoutParams.x = point.x-mView.getMeasuredWidth();
            mLayoutParams.y = point.y/2;
            mWindowManager.addView(mView, mLayoutParams);
        }
    }

    public void dismissWindow() {
        if (mWindowManager != null && mView != null) {
            mWindowManager.removeViewImmediate(mView);
        }
    }

    private void initListener(final Context context) {

        ProgressCircleImageView progressCircleImageView=mView.findViewById(R.id.progressImageView);
        progressCircleImageView.setTimer(30*1000,800);
        ProgressCircleImageView.PlayCountDownTimer timer=progressCircleImageView.getTimer();
        timer.start();

        final int mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
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
                        mLayoutParams.y = (int) (event.getRawY() - startY - statusBarHeight);
                        //mLayoutParams.y = (int) (event.getRawY() );
                        updateViewLayout();   //更新mView 的位置
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (isPerformClick) {
                            //mView.performClick();
                        }
                        //判断mView是在Window中的位置，以中间为界
                        if (mLayoutParams.x + mView.getMeasuredWidth() / 2 >= mWindowManager.getDefaultDisplay().getWidth() / 2) {
                            finalMoveX = mWindowManager.getDefaultDisplay().getWidth() - mView.getMeasuredWidth();
                        } else {
                            finalMoveX = 0;
                        }
                        stickToSide();
                        return !isPerformClick;
                }
                return false;
            }

            private void stickToSide() {
                ValueAnimator animator = ValueAnimator.ofInt(mLayoutParams.x, finalMoveX).setDuration(Math.abs(mLayoutParams.x - finalMoveX));
                animator.setInterpolator(new BounceInterpolator());
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

    public void hideWindow() {
        if (mView != null) {
            mView.setVisibility(View.GONE);
        }
    }

    public void visibleWindow() {
        if (mView != null) {
            mView.setVisibility(View.VISIBLE);
        }
    }

    public interface OnPermissionListener {
        void showPermissionDialog();
    }
}