package com.shuangling.software.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

public class MyClassicFooter extends ClassicsFooter {

    public MyClassicFooter(Context context) {
        super(context, null);
    }

    public MyClassicFooter(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    public TextView getTitleView(){
        return mTitleText;
    }
}
