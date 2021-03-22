package com.shuangling.software.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kcrason.dynamicpagerindicatorlibrary.BasePagerTabView;
import com.shuangling.software.R;

/**
 * 项目名称：My Application
 * 创建人：YoungBean
 * 创建时间：2021/3/16 14:58
 * 类描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class CustomPagerTabView extends BasePagerTabView {
    private TextView mtextView;
    private SimpleDraweeView cornerMark;
    private SimpleDraweeView topIcon;

    public CustomPagerTabView(Context context) {
        super(context);
    }

    public CustomPagerTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPagerTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public TextView getTabTextView() {
        return mtextView;
    }

    public SimpleDraweeView getCornerMark() {
        return cornerMark;
    }

    public SimpleDraweeView getTopIcon() {
        return topIcon;
    }


    @Override
    public View onCreateTabView(Context context) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_view, this, false);
        mtextView = view.findViewById(R.id.title);
        cornerMark = view.findViewById(R.id.corner_mark);
        topIcon = view.findViewById(R.id.top_icon);
        return view;
    }
}
