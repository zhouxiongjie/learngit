package com.shuangling.software.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.shuangling.software.R;
import com.shuangling.software.utils.CommonUtils;

/**
 * Created by rome753 on 2017/3/10
 */

public class TextRatingBar extends View implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener {

    //paddingLeft
    private int mLeft;
    //paddingTop
    private int mTop;
    //当前rating
    private int mRating;
    //总raring数
    private int mCount;
    //rating文字
    private String[] texts = {"标准","大","特大"};
    //相邻raring的距离
    private int mUnitSize;
    //bar到底部的距离
    private int mYOffset;
    //小竖条的一半长度
    private int mMarkSize;

    Paint paint = new Paint();

    private GestureDetector mGestureDetector;

    public TextRatingBar(Context context) {
        this(context, null);
    }

    public TextRatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector=new GestureDetector(getContext(),this);
        mGestureDetector.setIsLongpressEnabled(false);
        setLongClickable(true);
        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
        mCount = 3;
        mRating = 0;
        mMarkSize = 10;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //int widthMS=MeasureSpec.makeMeasureSpec(CommonUtils.getScreenWidth(),MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i("test", getMeasuredWidth() + " " + getMeasuredHeight());
        mLeft = (getPaddingLeft()+getPaddingRight())/2;
        mTop = getPaddingTop();
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int mw=getMeasuredWidth();
        int barWidth = mw - 2 * mLeft;
        mUnitSize = barWidth/(mCount - 1);
        mYOffset = getMeasuredHeight() - getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setStrokeWidth(4);
        paint.setColor(Color.GRAY);
        canvas.drawLine(mLeft,mYOffset,mLeft+(mCount-1)*mUnitSize,mYOffset,paint);
        //canvas.drawLine(mLeft,mYOffset,mLeft+mRating*mUnitSize,mYOffset,paint);
        for(int i=0;i<mCount;i++){
            //paint.setColor(Color.GRAY);
            canvas.drawLine(mLeft+i*mUnitSize,mYOffset-mMarkSize,mLeft+i*mUnitSize,mYOffset+2,paint);
            //paint.setColor(mRating == i ? Color.BLACK : Color.BLACK);
            paint.setTextSize(getResources().getDimension(R.dimen.font_size_30px));
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(texts[i],mLeft+i*mUnitSize,mTop,paint);
        }
        //paint.setColor(Color.GRAY);
        //canvas.drawLine(mLeft+mRating*mUnitSize,mYOffset,mLeft+(mCount-1)*mUnitSize,mYOffset,paint);
        canvas.drawCircle(mLeft+mRating*mUnitSize,mYOffset,20,paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);

//        boolean consume=mGestureDetector.onTouchEvent(event);
//        return consume;

//        Log.i("TextRatingBar",""+event.getAction());
//
//        if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){
//            float x = event.getX();
//            for(int i=0;i<mCount;i++){
//                float distance = mLeft+i*mUnitSize - x;
//                if(Math.abs(distance) < 100){
//                    setRating(i);
//                    if(onRatingListener != null){
//                        onRatingListener.onRating(mRating);
//                    }
//                    break;
//                }
//            }
//        }
//        return true;

    }

    public int getRating() {
        return mRating;

    }

    public void setRating(int rating) {
        mRating = rating;
        invalidate();
    }

    private OnRatingListener onRatingListener;

    public void setOnRatingListener(OnRatingListener onRatingListener) {
        this.onRatingListener = onRatingListener;
    }




    public interface OnRatingListener{
        void onRating(int rating);
    }



    @Override
    public boolean onDown(MotionEvent e) {

        Log.i("TextRatingBar","onDown");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.i("TextRatingBar","onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i("TextRatingBar","onSingleTapUp");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.i("TextRatingBar","onScroll");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.i("TextRatingBar","onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.i("TextRatingBar","onFling");
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.i("TextRatingBar","onSingleTapConfirmed");


        float x = e.getX();
        for(int i=0;i<mCount;i++){
            float distance = mLeft+i*mUnitSize - x;
            if(Math.abs(distance) < 100){
                setRating(i);
                if(onRatingListener != null){
                    onRatingListener.onRating(mRating);
                }
                break;
            }
        }
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.i("TextRatingBar","onDoubleTap");
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.i("TextRatingBar","onDoubleTapEvent");
        return false;
    }


}
