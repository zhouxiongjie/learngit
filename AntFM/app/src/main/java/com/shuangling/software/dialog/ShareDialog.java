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
    @BindView(R.id.layout_second_group)
    LinearLayout secondGroupLayout;

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
    @BindView(R.id.reportBtn)
    ImageView reportBtn;
    @BindView(R.id.reportText)
    TextView reportText;
    Unbinder unbinder1;
    @BindView(R.id.refresh)
    LinearLayout refresh;
    @BindView(R.id.report)
    LinearLayout report;
    private float mAppFontSize;
    Unbinder unbinder;

    private boolean mIsCollected;
    private boolean mIsReported;

    private boolean mIsShowPosterButton = false;
    private boolean mIsShowFontSize = true;


    private boolean mIsShowRefresh = true;
    private boolean mIsShowCollect = true;
    private boolean mIsShowReport = true;
    private boolean mIsShowCopyLink = true;

    private boolean mIsHideSecondGroup = false;


    public interface ShareHandler {
        void onShare(String platform);

        void copyLink();

        void refresh();

        void collectContent();

        void poster();

        void report();
    }


    public boolean isIsShowPosterButton() {
        return mIsShowPosterButton;
    }

    public void setIsShowPosterButton(boolean mIsShowPosterButton) {
        this.mIsShowPosterButton = mIsShowPosterButton;
    }


    public boolean isIsShowFontSize() {
        return mIsShowFontSize;
    }

    public void setIsShowFontSize(boolean mIsShowFontSize) {
        this.mIsShowFontSize = mIsShowFontSize;
    }

    public boolean isIsShowRefresh() {
        return mIsShowRefresh;
    }

    public void setIsShowRefresh(boolean mIsShowRefresh) {
        this.mIsShowRefresh = mIsShowRefresh;
    }

    public boolean isIsShowCollect() {
        return mIsShowCollect;
    }

    public void setIsShowCollect(boolean mIsShowCollect) {
        this.mIsShowCollect = mIsShowCollect;
    }

    public boolean isIsShowReport() {
        return mIsShowReport;
    }

    public void setIsShowReport(boolean mIsShowReport) {
        this.mIsShowReport = mIsShowReport;
    }

    public boolean isIsShowCopyLink() {
        return mIsShowCopyLink;
    }

    public void setIsShowCopyLink(boolean mIsShowCopyLink) {
        this.mIsShowCopyLink = mIsShowCopyLink;
    }


    public boolean isIsHideSecondGroup() {
        return mIsHideSecondGroup;
    }

    public void setIsHideSecondGroup(boolean mIsHideSecondGroup) {
        this.mIsHideSecondGroup = mIsHideSecondGroup;
    }

    public void setShareHandler(ShareHandler shareHandler) {
        this.mShareHandler = shareHandler;
    }

    private ShareHandler mShareHandler;


    public static ShareDialog getInstance(boolean isCollected, boolean isReported) {
        ShareDialog dialogFragment = new ShareDialog();
        dialogFragment.setCanceledBack(true);
        dialogFragment.setCanceledOnTouchOutside(true);
        dialogFragment.setGravity(Gravity.BOTTOM);
        dialogFragment.setWidth(1f);
        dialogFragment.mIsCollected = isCollected;
        dialogFragment.mIsReported = isReported;
        dialogFragment.mIsShowPosterButton = false;
        dialogFragment.mIsHideSecondGroup = false;
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
        } else {
            collectBtn.setSelected(false);
        }

        if (mIsReported) {
            reportBtn.setSelected(true);
            reportText.setText("已举报");
        } else {
            reportBtn.setSelected(false);
            reportText.setText("举报");
        }

        if(mIsShowFontSize){
            fontSize.setVisibility(View.VISIBLE);
        } else {
            fontSize.setVisibility(View.GONE);
        }


        if (mIsShowPosterButton) {
            poster.setVisibility(View.VISIBLE);
        } else {
            poster.setVisibility(View.GONE);
        }

        if (mIsShowRefresh) {
            refresh.setVisibility(View.VISIBLE);
        } else {
            refresh.setVisibility(View.GONE);
        }

        if (mIsShowCollect) {
            collect.setVisibility(View.VISIBLE);
        } else {
            collect.setVisibility(View.GONE);
        }

        if (mIsShowReport) {
            report.setVisibility(View.VISIBLE);
        } else {
            report.setVisibility(View.GONE);
        }

        if (mIsShowCopyLink) {
            copyLink.setVisibility(View.VISIBLE);
        } else {
            copyLink.setVisibility(View.GONE);
        }



        if (mIsHideSecondGroup) {
            secondGroupLayout.setVisibility(View.GONE);
        } else {
            secondGroupLayout.setVisibility(View.VISIBLE);
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


    @OnClick({R.id.post, R.id.weiXin, R.id.weiXinCollect, R.id.weiXinFriends, R.id.weibo, R.id.qq, R.id.qqZone, R.id.fontSize, R.id.fontSizeBar, R.id.cancel, R.id.copyLink, R.id.collect, R.id.report, R.id.refresh})
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

            case R.id.report:
                if (!mIsReported) {
                    if (mShareHandler != null) {
                        mShareHandler.report();
                    }
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
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }
}
