package com.shuangling.software.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.shuangling.software.interf.ScrollViewListener;

/**
 * 项目名称：AntFM
 * 创建人：Administrator
 * 创建时间：2021/3/23 10:17
 * 类描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class ObservableScrollView extends ScrollView {
    private ScrollViewListener scrollViewListener = null;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }
    //重写滚动方法
    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

}
