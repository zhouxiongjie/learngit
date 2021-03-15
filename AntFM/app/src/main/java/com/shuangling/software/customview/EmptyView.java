package com.shuangling.software.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.skin.QMUISkinHelper;
import com.qmuiteam.qmui.skin.QMUISkinValueBuilder;
import com.qmuiteam.qmui.skin.annotation.QMUISkinChangeNotAdapted;
import com.qmuiteam.qmui.widget.QMUILoadingView;
import com.shuangling.software.R;

public class EmptyView extends RelativeLayout {
    private LinearLayout noNetwork;
    private LinearLayout serverError;
    private TextView refresh;
    protected TextView goBack;

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.empty_view, this, true);
        noNetwork = findViewById(R.id.noNetwork);
        serverError = findViewById(R.id.serverError);
        refresh = findViewById(R.id.refresh);
        goBack = findViewById(R.id.goBack);
    }

//    /**
//     * 显示emptyView
//     *
//     * @param loading               是否要显示loading
//     * @param titleText             标题的文字，不需要则传null
//     * @param detailText            详情文字，不需要则传null
//     * @param buttonText            按钮的文字，不需要按钮则传null
//     * @param onButtonClickListener 按钮的onClick监听，不需要则传null
//     */
//    public void show(boolean loading, String titleText, String detailText, String buttonText, OnClickListener onButtonClickListener) {
//        setLoadingShowing(loading);
//        setTitleText(titleText);
//        setDetailText(detailText);
//        setButton(buttonText, onButtonClickListener);
//        show();
//    }
//
//    /**
//     * 用于显示emptyView并且只显示loading的情况，此时title、detail、button都被隐藏
//     *
//     * @param loading 是否显示loading
//     */
//    public void show(boolean loading) {
//        setLoadingShowing(loading);
//        setTitleText(null);
//        setDetailText(null);
//        setButton(null, null);
//        show();
//    }
//
//    /**
//     * 用于显示纯文本的简单调用方法，此时loading、button均被隐藏
//     *
//     * @param titleText  标题的文字，不需要则传null
//     * @param detailText 详情文字，不需要则传null
//     */
//    public void show(String titleText, String detailText) {
//        setLoadingShowing(false);
//        setTitleText(titleText);
//        setDetailText(detailText);
//        setButton(null, null);
//        show();
//    }
//
//    /**
//     * 显示emptyView，不建议直接使用，建议调用带参数的show()方法，方便控制所有子View的显示/隐藏
//     */
//    public void show() {
//        setVisibility(VISIBLE);
//    }
//
//    /**
//     * 隐藏emptyView
//     */
//    public void hide() {
//        setVisibility(GONE);
//        setLoadingShowing(false);
//        setTitleText(null);
//        setDetailText(null);
//        setButton(null, null);
//    }
//
//    public boolean isShowing() {
//        return getVisibility() == VISIBLE;
//    }
//
//    public boolean isLoading() {
//        return mLoadingView.getVisibility() == VISIBLE;
//    }
//
//    public void setLoadingShowing(boolean show) {
//        mLoadingView.setVisibility(show ? VISIBLE : GONE);
//    }
//
//    public void setTitleText(String text) {
//        mTitleTextView.setText(text);
//        mTitleTextView.setVisibility(text != null ? VISIBLE : GONE);
//    }
//
//    public void setDetailText(String text) {
//        mDetailTextView.setText(text);
//        mDetailTextView.setVisibility(text != null ? VISIBLE : GONE);
//    }
//
//    @QMUISkinChangeNotAdapted
//    public void setTitleColor(int color) {
//        mTitleTextView.setTextColor(color);
//    }
//
//    @QMUISkinChangeNotAdapted
//    public void setDetailColor(int color) {
//        mDetailTextView.setTextColor(color);
//    }
//
//    public void setTitleSkinValue(QMUISkinValueBuilder builder) {
//        QMUISkinHelper.setSkinValue(mTitleTextView, builder);
//    }
//
//    public void setDetailSkinValue(QMUISkinValueBuilder builder) {
//        QMUISkinHelper.setSkinValue(mDetailTextView, builder);
//    }
//
//    public void setLoadingSkinValue(QMUISkinValueBuilder builder) {
//        QMUISkinHelper.setSkinValue(mLoadingView, builder);
//    }
//
//    public void setBtnSkinValue(QMUISkinValueBuilder builder) {
//        QMUISkinHelper.setSkinValue(mButton, builder);
//    }
//
//    public void setButton(String text, OnClickListener onClickListener) {
//        mButton.setText(text);
//        mButton.setVisibility(text != null ? VISIBLE : GONE);
//        mButton.setOnClickListener(onClickListener);
//    }
}
