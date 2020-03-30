package com.shuangling.software.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.header.ClassicsHeader;

public class MyClassicHeader extends ClassicsHeader {

    public MyClassicHeader(Context context) {
        super(context, null);
    }

    public MyClassicHeader(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    public TextView getTitleView(){
        return mTitleText;
    }
}
