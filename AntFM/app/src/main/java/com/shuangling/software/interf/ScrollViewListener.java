package com.shuangling.software.interf;

import com.shuangling.software.customview.ObservableScrollView;

/**
 * 项目名称：AntFM
 * 创建人：YoungBeam
 * 创建时间：2021/3/23 10:16
 * 类描述：自定义scrollView监听器
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public interface ScrollViewListener {
    void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
}
