package com.shuangling.software.customview;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.kcrason.dynamicpagerindicatorlibrary.BasePagerTabView;
import com.kcrason.dynamicpagerindicatorlibrary.DynamicPagerIndicator;
import com.shuangling.software.entity.Column;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：My Application
 * 创建人：YoungBean
 * 创建时间：2021/3/16 15:33
 * 类描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class CustomPagerIndicator extends DynamicPagerIndicator {
    private List<Column> mColumns = new ArrayList<>();
    private Column column;
    private CustomPagerTabView customPagerTabView;

    public CustomPagerIndicator(Context context) {
        super(context);
    }

    public CustomPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setColumns(List<Column> columns) {
        mColumns = columns;
    }

    @Override
    public BasePagerTabView createTabView(Context context, PagerAdapter pagerAdapter, int position) {
        customPagerTabView = new CustomPagerTabView(getContext());
        if (column == null) {
            column = new Column();
        }
        if (mColumns.size() > 0) {
            column = mColumns.get(position);
            switch (column.getDisplay_effect_type()) {
                case 0://标题
                    customPagerTabView.getCornerMark().setVisibility(GONE);
                    break;
                case 1://标题加角标
                    customPagerTabView.getCornerMark().setVisibility(VISIBLE);
                    ImageLoader.showThumb(Uri.parse(column.getDisplay_effect()), customPagerTabView.getCornerMark(), CommonUtils.dip2px(22.5f), CommonUtils.dip2px(13.8f));
                    break;
                case 2://大图
                    customPagerTabView.getTopIcon().setVisibility(VISIBLE);
                    customPagerTabView.getTabTextView().setVisibility(GONE);
                    customPagerTabView.getCornerMark().setVisibility(GONE);
                    Uri uri = Uri.parse(column.getDisplay_effect());
                    //设置Fresco支持gif动态图片
                    DraweeController controller = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
                    //将Fresco管理者设置使用
                    customPagerTabView.getTopIcon().setController(controller);
                    break;
            }
        }
        return customPagerTabView;
    }

    public void setBigIcon(int position,CustomPagerTabView customPagerTabView){
        this.customPagerTabView = customPagerTabView;
        ViewGroup.LayoutParams params = customPagerTabView.getTopIcon().getLayoutParams();
        if (mColumns.get(position).getDisplay_effect_type() == 2){
            params.height = CommonUtils.dip2px(30);
            params.width = CommonUtils.dip2px(51);
            customPagerTabView.getTopIcon().setLayoutParams(params);
        }
    }

    public void setNormalIcon(int position,CustomPagerTabView customPagerTabView){
        this.customPagerTabView = customPagerTabView;
        ViewGroup.LayoutParams params = customPagerTabView.getTopIcon().getLayoutParams();
        if (mColumns.get(position).getDisplay_effect_type() == 2){
            params.height = CommonUtils.dip2px(22);
            params.width = CommonUtils.dip2px(37.5f);
            customPagerTabView.getTopIcon().setLayoutParams(params);
        }
    }


}
