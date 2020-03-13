package com.shuangling.software.utils;

import android.view.Gravity;

import com.hjq.toast.IToastStyle;

public class MyToastStyle implements IToastStyle {

    @Override
    public int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    public int getXOffset() {
        return 0;
    }

    @Override
    public int getYOffset() {
        return 0;
    }

    @Override
    public int getZ() {
        return CommonUtils.dip2px(5);
    }

    @Override
    public int getCornerRadius() {
        return CommonUtils.dip2px(5);
    }

    @Override
    public int getBackgroundColor() {
        return 0X88000000;
    }

    @Override
    public int getTextColor() {
        return 0XEEFFFFFF;
    }

    @Override
    public float getTextSize() {
        //int size=CommonUtils.dip2px(14);
        return CommonUtils.dip2px(14);
    }

    @Override
    public int getMaxLines() {
        return 2;
    }

    @Override
    public int getPaddingStart() {
        return CommonUtils.dip2px(10);
    }



    @Override
    public int getPaddingTop() {
        return CommonUtils.dip2px(10);
    }

    @Override
    public int getPaddingEnd() {
        return getPaddingStart();
    }


    @Override
    public int getPaddingBottom() {
        return getPaddingTop();
    }
}
