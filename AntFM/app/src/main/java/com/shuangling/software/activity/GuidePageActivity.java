package com.shuangling.software.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.entity.GuidePage;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class GuidePageActivity extends AppCompatActivity implements Handler.Callback {
    public static final String FIRST_RUN = "first_run";
    public static final int MSG_GET_PAGES = 0x1;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.indicator)
    CirclePageIndicator indicator;
    private PagerAdapter mPageAdapter;
    private List<View> mViews = new ArrayList<>();
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏顶部状态栏
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //ImmersionBar.with(this).transparentStatusBar().init();
        //CommonUtils.transparentStatusBar(this);

        //boolean firstRun = SharedPreferencesUtils.getBooleanValue(FIRST_RUN, true);
        //测试
        String lastVersion = SharedPreferencesUtils.getStringValue("lastVersion", null);
        if(!CommonUtils.getVersionName(this).equals(lastVersion)){
            //第一次运行或者更新版本后第一次运行
            setContentView(R.layout.activity_guidepage);
            ButterKnife.bind(this);
            mHandler = new Handler(this);
            guides();
        }else{
            Intent intent = new Intent(this, StartupActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void guides() {
        String url = ServerInfo.serviceIP + ServerInfo.guides;
        Map<String, String> params = new HashMap<>();
        params.put("version", getVersionName());
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_GET_PAGES;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        //SharedPreferencesUtils.putPreferenceTypeValue(FIRST_RUN, SharedPreferencesUtils.PreferenceType.Boolean, "false");
        super.onDestroy();
    }

    public String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_PAGES:
                try {
                    String result = (String) msg.obj;
                    JSONObject jo = JSONObject.parseObject(result);
                    if (jo.getIntValue("code") == 100000 && jo.getJSONObject("data") != null) {
                        GuidePage pages = JSONObject.parseObject(jo.getJSONObject("data").toJSONString(), GuidePage.class);
                        for (int i = 0; pages.getContents() != null && i < pages.getContents().size(); i++) {
                            SimpleDraweeView view = new SimpleDraweeView(this);
                            GenericDraweeHierarchy hierarchy = view.getHierarchy();
                            hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
                            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            if (!TextUtils.isEmpty(pages.getContents().get(i).getImage())) {
                                Uri uri = Uri.parse(pages.getContents().get(i).getImage());
                                ImageLoader.showThumb(uri, view, CommonUtils.getScreenWidth(), CommonUtils.getScreenHeight() - CommonUtils.getNavigationBarHeight(this) - CommonUtils.getStateBarHeight(this));
                            }
                            mViews.add(view);
                            if (i == pages.getContents().size() - 1) {
                                view.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(GuidePageActivity.this, MainActivity.class));
                                        finish();
                                    }
                                });
                            }
                            mPageAdapter = new PagerAdapter() {
                                @Override
                                public void destroyItem(ViewGroup container, int position, Object object) {
                                    container.removeView(mViews.get(position));
                                }

                                @Override
                                public Object instantiateItem(ViewGroup container, int position) {
                                    View view = mViews.get(position);
                                    container.addView(view);
                                    return view;
                                }

                                @Override
                                public boolean isViewFromObject(View arg0, Object arg1) {
                                    return arg0 == arg1;
                                }

                                @Override
                                public int getCount() {
                                    return mViews.size();
                                }
                            };
                            viewPager.setAdapter(mPageAdapter);
                            indicator.setViewPager(viewPager);
                            indicator.setSnap(true);
                            indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                }

                                @Override
                                public void onPageSelected(int position) {
                                }

                                @Override
                                public void onPageScrollStateChanged(int state) {
                                }
                            });
                        }
                    } else {
                        Intent intent = new Intent(this, StartupActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }
}
