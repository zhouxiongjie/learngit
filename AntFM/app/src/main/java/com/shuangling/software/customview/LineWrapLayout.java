package com.shuangling.software.customview;

/**
 * Created by Administrator on 2017-08-03.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shuangling.software.R;
import com.shuangling.software.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;


public class LineWrapLayout extends ViewGroup {
    private Context context;
    private int bgId = 0;//textview背景ID
    private int magin = 20;//每个VIEW之间的间距
    private List<List<View>> mAllChildViews = new ArrayList<List<View>>();//所有子控件
    private List<Integer> mLineHeight = new ArrayList<Integer>();//每一行的高度

    public interface OnItemClickListener//点击事件接口
    {
        public void onClick(View view);
    }

    private OnItemClickListener clickListener;

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public LineWrapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LineWrapLayout);
        magin = a.getInt(R.styleable.LineWrapLayout_magin, 15);
        bgId = a.getResourceId(R.styleable.LineWrapLayout_itemBg, R.drawable.search_record_bg);
    }

    /**
     * 关键字数据
     *
     * @param data
     */
    public void setData(List<String> data) {
        Log.i("AAA", "setData:" + data.size());
        this.removeAllViews();
        for (int i = 0; i < data.size(); i++) {
            TextView textView = new TextView(context);
            textView.setText(data.get(i));
            textView.setTextColor(getResources().getColorStateList(R.color.textColorTwo));//字体颜色
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);//字体、提示字体大小
            textView.setPadding(CommonUtils.dip2px(20),CommonUtils.dip2px(20),CommonUtils.dip2px(20),CommonUtils.dip2px(20));
            if (bgId != 0)
                textView.setBackgroundResource(bgId);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(v);
                }
            });
            this.addView(textView);
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //取得控件的宽高
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        //测量模式
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int mWidth = 0;
        int mHeight = 0;

        int lineWidth = 0;
        int lineHeight = 0;

        int mCount = getChildCount();
        for (int i = 0; i < mCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();
            // 当前子控件占据的宽度和高度+子控件之间的间距
            int childWidth = child.getMeasuredWidth() + magin + lp.leftMargin
                    + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + magin + lp.topMargin
                    + lp.bottomMargin;
            int temp = lineWidth + childWidth;
            if (temp <= sizeWidth) {
                //当新加的子控件宽度+当前行所有子控件之和还小于父控件宽度
                //说明当前行还可以添加
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            } else {
                //否则的话就要增加一行了
                mWidth = Math.max(lineWidth, mWidth);// 取最大的
                lineWidth = childWidth; // 重新开启新行，开始记录
                // 加上当前高度，
                mHeight += lineHeight;
                // 开启记录下一行的高度
                lineHeight = childHeight;
            }
        }
        //加上最后一行的高度
        mHeight += lineHeight;
        mWidth = Math.max(mWidth, lineWidth);
        mHeight = Math.max(mHeight, lineHeight);

        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth
                : mWidth, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight
                : mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllChildViews.clear();
        mLineHeight.clear();
        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;
        // 每一行所有的childView
        List<View> lineViews = new ArrayList<View>();
        int mCount = getChildCount();
        Log.i("AAA", "mCount:" + mCount);
        // 遍历所有
        for (int i = 0; i < mCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            int temp = childWidth + magin + lp.leftMargin + lp.rightMargin + lineWidth;

            // 如果不需要换行
            if (temp <= width) {
                lineWidth = temp;
                lineHeight = Math.max(lineHeight, childHeight + magin + lp.topMargin
                        + lp.bottomMargin);
                lineViews.add(child);
            } else {
                // 记录这一行的高度
                mLineHeight.add(lineHeight);
                // 将当前行的View保存，然后new 一个List保存下一行的child
                mAllChildViews.add(lineViews);
                lineViews = new ArrayList<View>();
                lineViews.add(child);
                lineHeight = childHeight + magin + lp.topMargin + lp.bottomMargin;
                lineWidth = childWidth + magin + lp.leftMargin + lp.rightMargin;
            }
        }
        // 将最后一行加入队列
        mLineHeight.add(lineHeight);
        mAllChildViews.add(lineViews);


        int left = 0;
        int top = 0;
        // 总行数
        int count = mAllChildViews.size();
        for (int i = 0; i < count; i++) {
            lineViews = mAllChildViews.get(i);
            lineHeight = mLineHeight.get(i);
            // 遍历当前行所有的View
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                MarginLayoutParams lp = (MarginLayoutParams) child
                        .getLayoutParams();

                int lc = left + magin + lp.leftMargin;
                int tc = top + magin + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                child.layout(lc, tc, rc, bc);

                left += child.getMeasuredWidth() + lp.rightMargin + lp.leftMargin
                        + magin;
            }
            left = 0;
            top += lineHeight;
        }

    }

}