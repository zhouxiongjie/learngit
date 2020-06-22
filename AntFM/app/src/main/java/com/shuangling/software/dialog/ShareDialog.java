package com.shuangling.software.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.activity.FontSizeSettingActivity;
import com.shuangling.software.customview.SlideSelectView;
import com.shuangling.software.customview.TextRatingBar;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;


/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class ShareDialog extends BaseCircleDialog {

    public static final String FONT_SIZE = "font_size";

    @BindView(R.id.post)
    LinearLayout poster; //海报
    @BindView(R.id.weiXin)
    LinearLayout weiXin;
    @BindView(R.id.weiXinFriends)
    LinearLayout weiXinFriends;
    @BindView(R.id.weiXinCollect)
    LinearLayout weiXinCollect;
    @BindView(R.id.qq)
    LinearLayout qq;
    @BindView(R.id.qqZone)
    LinearLayout qqZone;
    @BindView(R.id.weibo)
    LinearLayout weibo;
    @BindView(R.id.fontSize)
    LinearLayout fontSize;

    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.fontSizeBar)
    SlideSelectView fontSizeBar;
    @BindView(R.id.selectionsLayout)
    LinearLayout selectionsLayout;
    @BindView(R.id.collect)
    LinearLayout collect;
    @BindView(R.id.copyLink)
    LinearLayout copyLink;
    @BindView(R.id.otherLayout)
    HorizontalScrollView otherLayout;
    @BindView(R.id.collectBtn)
    ImageView collectBtn;
    private float mAppFontSize;
    Unbinder unbinder;

    private boolean mIsCollected;

    private boolean mIsShowPosterButton = false;

    public interface ShareHandler {
        void onShare(String platform);

        void copyLink();

        void refresh();

        void collectContent();

        void poster();
    }


    public boolean isIsShowPosterButton() {
        return mIsShowPosterButton;
    }

    public void setIsShowPosterButton(boolean mIsShowPosterButton) {
        this.mIsShowPosterButton = mIsShowPosterButton;
    }

    public void setShareHandler(ShareHandler shareHandler) {
        this.mShareHandler = shareHandler;
    }

    private ShareHandler mShareHandler;


    public static ShareDialog getInstance(boolean isCollected) {
        ShareDialog dialogFragment = new ShareDialog();
        dialogFragment.setCanceledBack(true);
        dialogFragment.setCanceledOnTouchOutside(true);
        dialogFragment.setGravity(Gravity.BOTTOM);
        dialogFragment.setWidth(1f);
        dialogFragment.mIsCollected = isCollected;
        dialogFragment.mIsShowPosterButton = false;
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_share, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        unbinder = ButterKnife.bind(this, rootView);

        fontSizeBar.setOnSelectListener(new SlideSelectView.onSelectListener() {
            @Override
            public void onSelect(int rating) {
                mAppFontSize = SharedPreferencesUtils.getFloatValue(FontSizeSettingActivity.FONT_SIZE, 1.00f);
                if (mAppFontSize == 1.00f && rating != 0) {
                    if (rating == 1) {
                        SharedPreferencesUtils.putPreferenceTypeValue(FONT_SIZE, SharedPreferencesUtils.PreferenceType.Float, "1.15");
                        CommonUtils.setFontSize(getResources());
                        EventBus.getDefault().post(new CommonEvent("onFontSizeChanged"));
                    } else {
                        SharedPreferencesUtils.putPreferenceTypeValue(FONT_SIZE, SharedPreferencesUtils.PreferenceType.Float, "1.30");
                        CommonUtils.setFontSize(getResources());
                        EventBus.getDefault().post(new CommonEvent("onFontSizeChanged"));
                    }
                } else if (mAppFontSize == 1.15f && rating != 1) {
                    if (rating == 0) {
                        SharedPreferencesUtils.putPreferenceTypeValue(FONT_SIZE, SharedPreferencesUtils.PreferenceType.Float, "1.00");
                        CommonUtils.setFontSize(getResources());
                        EventBus.getDefault().post(new CommonEvent("onFontSizeChanged"));
                    } else {
                        SharedPreferencesUtils.putPreferenceTypeValue(FONT_SIZE, SharedPreferencesUtils.PreferenceType.Float, "1.30");
                        CommonUtils.setFontSize(getResources());
                        EventBus.getDefault().post(new CommonEvent("onFontSizeChanged"));
                    }
                } else if (mAppFontSize == 1.30f && rating != 2) {
                    if (rating == 0) {
                        SharedPreferencesUtils.putPreferenceTypeValue(FONT_SIZE, SharedPreferencesUtils.PreferenceType.Float, "1.00");
                        CommonUtils.setFontSize(getResources());
                        EventBus.getDefault().post(new CommonEvent("onFontSizeChanged"));
                    } else {
                        SharedPreferencesUtils.putPreferenceTypeValue(FONT_SIZE, SharedPreferencesUtils.PreferenceType.Float, "1.15");
                        CommonUtils.setFontSize(getResources());
                        EventBus.getDefault().post(new CommonEvent("onFontSizeChanged"));
                    }
                }
            }
        });

        mAppFontSize = SharedPreferencesUtils.getFloatValue(FontSizeSettingActivity.FONT_SIZE, 1.00f);
        if (mAppFontSize == 1.00f) {
            fontSizeBar.setCurrentPosition(0);
        } else if (mAppFontSize == 1.15f) {
            fontSizeBar.setCurrentPosition(1);
        } else {
            fontSizeBar.setCurrentPosition(2);
        }


        if (mIsCollected) {
            collectBtn.setSelected(true);
        }else{
            collectBtn.setSelected(false);
        }

        if(mIsShowPosterButton){
            poster.setVisibility(View.VISIBLE);
        }else{
            poster.setVisibility(View.GONE);
        }

//        fontSizeBar.setOnRatingListener(new TextRatingBar.OnRatingListener() {
//            @Override
//            public void onRating(int rating) {
//                if(rating==0){
//                    //标准
//                    SharedPreferencesUtils.putPreferenceTypeValue(FONT_SIZE, SharedPreferencesUtils.PreferenceType.Float, "1.00");
//                    CommonUtils.setFontSize(getResources());
//                    EventBus.getDefault().post(new CommonEvent("onFontSizeChanged"));
//                }else if(rating==1){
//                    //大
//                    SharedPreferencesUtils.putPreferenceTypeValue(FONT_SIZE, SharedPreferencesUtils.PreferenceType.Float, "1.15");
//                    CommonUtils.setFontSize(getResources());
//                    EventBus.getDefault().post(new CommonEvent("onFontSizeChanged"));
//                }else{
//                    //特大
//                    SharedPreferencesUtils.putPreferenceTypeValue(FONT_SIZE, SharedPreferencesUtils.PreferenceType.Float, "1.30");
//                    CommonUtils.setFontSize(getResources());
//                    EventBus.getDefault().post(new CommonEvent("onFontSizeChanged"));
//                }
//            }
//        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.post,R.id.weiXin, R.id.weiXinCollect, R.id.weiXinFriends, R.id.weibo, R.id.qq, R.id.qqZone, R.id.fontSize, R.id.fontSizeBar, R.id.cancel, R.id.copyLink, R.id.collect, R.id.refresh})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.post:
                if (mShareHandler != null) {
                    mShareHandler.poster();
                }
                dismiss();

                break;
            case R.id.weiXin:
                if (mShareHandler != null) {
                    mShareHandler.onShare(Wechat.NAME);
                }
                dismiss();

                break;
            case R.id.weiXinFriends:
                if (mShareHandler != null) {
                    mShareHandler.onShare(WechatMoments.NAME);
                }
                dismiss();
                break;
            case R.id.weiXinCollect:
                if (mShareHandler != null) {
                    mShareHandler.onShare(WechatFavorite.NAME);
                }
                dismiss();

                break;
            case R.id.weibo:
                if (mShareHandler != null) {
                    mShareHandler.onShare(SinaWeibo.NAME);
                }
                dismiss();
                break;
            case R.id.qq:
                if (mShareHandler != null) {
                    mShareHandler.onShare(QQ.NAME);
                }
                dismiss();
                break;
            case R.id.qqZone:
                if (mShareHandler != null) {
                    mShareHandler.onShare(QQ.NAME);
                }
                dismiss();
                break;
            case R.id.fontSize:
                otherLayout.setVisibility(View.GONE);
                fontSizeBar.setVisibility(View.VISIBLE);
                cancel.setText("返回");
                break;
            case R.id.fontSizeBar:


                break;

            case R.id.copyLink:
                if (mShareHandler != null) {
                    mShareHandler.copyLink();
                }

                break;
            case R.id.collect:
                if (mShareHandler != null) {
                    mShareHandler.collectContent();
                }
                dismiss();

                break;

            case R.id.refresh:
                if (mShareHandler != null) {
                    mShareHandler.refresh();
                }

                break;

            case R.id.cancel:
                if (cancel.getText().toString().equals("返回")) {
                    otherLayout.setVisibility(View.VISIBLE);
                    fontSizeBar.setVisibility(View.GONE);
                    cancel.setText("取消");

                } else {
                    dismiss();
                }
                break;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }
}
