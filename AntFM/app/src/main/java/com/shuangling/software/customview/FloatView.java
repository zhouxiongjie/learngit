package com.shuangling.software.customview;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.shuangling.software.R;
import com.shuangling.software.activity.MainActivity;
import com.shuangling.software.utils.FloatWindowUtil;


public class FloatView extends LinearLayout {

    private Context mContext;
    private boolean mScrolling;
    private float touchDownX;
    private float touchDownY;
    private final int distance = 100;
    private float x2 = 0;
    private float y2 = 0;

    public FloatView(Context context) {
        super(context);
        mContext=context;
        init();
    }

    public FloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        init();
    }

    public FloatView(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        init();
    }

    public FloatView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext=context;
        init();
    }

    public void init(){
        LayoutInflater.from(mContext).inflate(R.layout.float_player, this,true);

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                touchDownX = ev.getX();
                touchDownY = ev.getY();
                mScrolling = false;

                break;
            case MotionEvent.ACTION_MOVE:

                if (Math.abs(touchDownX - ev.getX()) >= ViewConfiguration.get(getContext()).getScaledTouchSlop()||
                        Math.abs(touchDownY - ev.getY()) >= ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    mScrolling = true;
                } else {
                    mScrolling = false;
                }

                break;
            case MotionEvent.ACTION_UP:
                mScrolling = false;
                break;
        }
        return mScrolling;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_OUTSIDE:
                if(mOnActionOutListener!=null){
                    mOnActionOutListener.actionOut();
                }
                Log.d("FloatView","ACTION_OUTSIDE");
                break;
        }
        return true;
    }


    private OnActionOutListener mOnActionOutListener;

    public void addOnActionOutListener(OnActionOutListener actionOutListener){
        mOnActionOutListener=actionOutListener;
    }

    public interface OnActionOutListener {
        void actionOut();
    }

}
