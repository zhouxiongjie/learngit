package com.shuangling.software.customview;

import android.graphics.Rect;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.shuangling.software.utils.CommonUtils;

import java.util.Random;

public class AudioJumpView extends View {
    private int columnNum = 5;
    private Random mRandom;
    private Paint mPaint;
    private int mColor;

    public AudioJumpView(Context context) {
        this(context, null);
    }

    public AudioJumpView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AudioJumpView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mColor = CommonUtils.getThemeColor(context);
        init();
    }

    private void init() {
        mRandom = new Random();
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();// 获取对应高度
        int width = getWidth(); // 获取对应宽度
        int columnWidth = width / (columnNum * 2) - 1;
        for (int i = 0; i < columnNum; i++) {
            Rect rect = new Rect(columnWidth * i * 2, mRandom.nextInt(height), columnWidth * (1 + i * 2), height);
            canvas.drawRect(rect, mPaint);
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        }, 300);
    }
}
