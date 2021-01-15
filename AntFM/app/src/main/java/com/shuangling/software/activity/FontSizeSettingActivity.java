package com.shuangling.software.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.SharedPreferencesUtils;
import com.youngfeng.snake.annotations.EnableDragToClose;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//@EnableDragToClose()
public class FontSizeSettingActivity extends QMUIActivity/*AppCompatActivity*/ {
    public static final String FONT_SIZE = "font_size";
    @BindView(R.id.activity_title)
    /*TopTitleBar*/ QMUITopBarLayout activityTitle;
    @BindView(R.id.standard)
    RadioButton standard;
    @BindView(R.id.standardLayout)
    RelativeLayout standardLayout;
    @BindView(R.id.large)
    RadioButton large;
    @BindView(R.id.largeLayout)
    RelativeLayout largeLayout;
    @BindView(R.id.superLarge)
    RadioButton superLarge;
    @BindView(R.id.superLargeLayout)
    RelativeLayout superLargeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_size_setting);
//        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this); //
        activityTitle.addLeftImageButton(R.drawable.ic_left, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(view -> { //
            finish();
        });
        activityTitle.setTitle("字体大小");
        init();
    }

    private void init() {
        float fontSize = SharedPreferencesUtils.getFloatValue(FONT_SIZE, 1.00f);
        if (fontSize == 1.00f) {
            standard.setVisibility(View.VISIBLE);
        } else if (fontSize == 1.15f) {
            large.setVisibility(View.VISIBLE);
        } else {
            superLarge.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.standardLayout, R.id.largeLayout, R.id.superLargeLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.standardLayout: {
                SharedPreferencesUtils.putPreferenceTypeValue(FONT_SIZE, SharedPreferencesUtils.PreferenceType.Float, "1.00");
                standard.setVisibility(View.VISIBLE);
                large.setVisibility(View.GONE);
                superLarge.setVisibility(View.GONE);
                CommonUtils.setFontSize(getResources());
                EventBus.getDefault().post(new CommonEvent("onFontSizeChanged"));
//                Resources res=getResources();
//                float appFontSize = 1.00f;
//                float systemFontSize = res.getConfiguration().fontScale;
//                float mixFontSize=appFontSize*systemFontSize;
//
//                float max = (appFontSize > systemFontSize) ? appFontSize : systemFontSize;
//                float fontSize=Math.min(mixFontSize,max);
//                res.getConfiguration().fontScale=fontSize;
//                res.updateConfiguration(res.getConfiguration(), res.getDisplayMetrics());
//                Configuration configuration = getResources().getConfiguration();
//                configuration.fontScale = (float) 1;
//                //0.85 小, 1 标准大小, 1.15 大，1.3 超大 ，1.45 特大
//                DisplayMetrics metrics = new DisplayMetrics();
//                getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                metrics.scaledDensity = configuration.fontScale * metrics.density;
//                getBaseContext().getResources().updateConfiguration(configuration, metrics);
            }
            break;
            case R.id.largeLayout: {
                SharedPreferencesUtils.putPreferenceTypeValue(FONT_SIZE, SharedPreferencesUtils.PreferenceType.Float, "1.15");
                standard.setVisibility(View.GONE);
                large.setVisibility(View.VISIBLE);
                superLarge.setVisibility(View.GONE);
                CommonUtils.setFontSize(getResources());
                EventBus.getDefault().post(new CommonEvent("onFontSizeChanged"));
            }
            break;
            case R.id.superLargeLayout: {
                SharedPreferencesUtils.putPreferenceTypeValue(FONT_SIZE, SharedPreferencesUtils.PreferenceType.Float, "1.30");
                standard.setVisibility(View.GONE);
                large.setVisibility(View.GONE);
                superLarge.setVisibility(View.VISIBLE);
                CommonUtils.setFontSize(getResources());
                EventBus.getDefault().post(new CommonEvent("onFontSizeChanged"));
            }
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
