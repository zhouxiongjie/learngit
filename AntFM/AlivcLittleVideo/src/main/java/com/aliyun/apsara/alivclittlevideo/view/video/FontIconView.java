package com.aliyun.apsara.alivclittlevideo.view.video;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class FontIconView extends AppCompatTextView {


    public FontIconView(Context context) {
        super(context);
        init(context);
    }
    public FontIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public FontIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context) {
//        设置字体图标
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"littlevideoiconfont.ttf"));
    }
}