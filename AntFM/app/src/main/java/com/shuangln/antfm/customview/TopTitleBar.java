package com.shuangln.antfm.customview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shuangln.antfm.R;

/**
 * 标题控件
 */
public class TopTitleBar extends RelativeLayout {

    private String titleText;
    private boolean canBack;
    private String backText;
    private String moreText;
    private int moreImg;
    private TextView tvMore;


    public TopTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.top_title_bar, this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopTitleBar, 0, 0);
        try {
            titleText = ta.getString(R.styleable.TopTitleBar_titleText);
            canBack = ta.getBoolean(R.styleable.TopTitleBar_canBack, false);
            backText = ta.getString(R.styleable.TopTitleBar_backText);
            moreImg = ta.getResourceId(R.styleable.TopTitleBar_moreImg, 0);
            moreText = ta.getString(R.styleable.TopTitleBar_moreText);
            setUpView(context);
        } finally {
            ta.recycle();
        }
    }

    private void setUpView(Context context){
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(titleText);
        LinearLayout backBtn = (LinearLayout) findViewById(R.id.title_back);
        backBtn.setVisibility(canBack ? VISIBLE : INVISIBLE);
        if (canBack){
            TextView tvBack = (TextView) findViewById(R.id.txt_back);
            tvBack.setText(backText);
            backBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) getContext()).finish();
                }
            });
        }
        if (moreImg != 0){
            ImageView moreImgView = (ImageView) findViewById(R.id.img_more);
            moreImgView.setImageDrawable(ContextCompat.getDrawable(context,moreImg));
        }
        tvMore = (TextView) findViewById(R.id.txt_more);
        tvMore.setText(moreText);
    }


    /**
     * 标题控件
     *
     * @param titleText 设置标题文案
     */
    public void setTitleText(String titleText){
        this.titleText = titleText;
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(titleText);
    }

    /**
     * 标题更多按钮
     *
     * @param img 设置更多按钮
     */
    public void setMoreImg(int img){
        moreImg = img;
        ImageView moreImgView = (ImageView) findViewById(R.id.img_more);
        moreImgView.setImageDrawable(getContext().getResources().getDrawable(moreImg));
    }


    /**
     * 设置更多按钮事件
     *
     * @param listener 事件监听
     */
    public void setMoreImgAction(OnClickListener listener){
        ImageView moreImgView = (ImageView) findViewById(R.id.img_more);
        moreImgView.setOnClickListener(listener);
    }



    /**
     * 设置更多按钮事件
     *
     * @param listener 事件监听
     */
    public void setMoreTextAction(OnClickListener listener){
        tvMore.setOnClickListener(listener);
    }


    /**
     * 设置更多文字内容
     * @param text 更多文本
     */
    public void setMoreTextContext(String text){
        tvMore.setText(text);
    }



    /**
     * 设置返回按钮事件
     *
     * @param listener 事件监听
     */
    public void setBackListener(OnClickListener listener){
        if (canBack){
            LinearLayout backBtn = (LinearLayout) findViewById(R.id.title_back);
            backBtn.setOnClickListener(listener);
        }
    }



}
