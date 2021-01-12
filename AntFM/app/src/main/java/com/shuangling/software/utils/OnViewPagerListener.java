package com.shuangling.software.utils;
/**
 * Created by 你是我的 on 2019/1/5.
 */
public interface OnViewPagerListener {
    // 初始化完成
    void onInitComplete();
// 释放的监听
    void onPageRelease(boolean isNext, int position);
// 选中的监听以及判断是否滑动到底部
    void onPageSelected(int position, boolean isBottom);
void onPageSelected(int position);
// 到最顶部时，右滑退出监听
void onPageSelected(int position, int totalPage);
    void exitVideo();
}
