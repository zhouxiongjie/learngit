package com.shuangling.software.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

public class SolveClickTouchConflictLayout extends LinearLayout {

    public SolveClickTouchConflictLayout(Context context) {
        super(context);
    }

    public SolveClickTouchConflictLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SolveClickTouchConflictLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SolveClickTouchConflictLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private boolean mScrolling;
    private float touchDownX;
    private float touchDownY;
    private final int distance = 100;
    private float x2 = 0;
    private float y2 = 0;

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
}
