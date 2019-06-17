package com.shuangling.software.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jaeger.library.StatusBarUtil;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.Organization;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.StatusBarManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;


public class OrganizationDetailActivity extends BaseActivity implements Handler.Callback {

    public static final String TAG = "AnchorDetailActivity";

    private static final String[] category = new String[]{"电台","主播","资讯","专辑","视频"};

    public static final int MSG_GET_ORGANIZATION_DETAIL = 0x1;
    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.logo)
    SimpleDraweeView logo;
    @BindView(R.id.count)
    TextView count;
    @BindView(R.id.follows)
    TextView follows;
    @BindView(R.id.likes)
    TextView likes;
    @BindView(R.id.attention)
    TextView attention;
    @BindView(R.id.more)
    ImageView more;
    @BindView(R.id.desc)
    TextView desc;
    @BindView(R.id.tabPageIndicator)
    TabLayout tabPageIndicator;
    @BindView(R.id.viewPager)
    ViewPager viewPager;


    private int mOrganizationId;
    private Organization mOrganization;

    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(MyApplication.getInstance().getCurrentTheme());
        setContentView(R.layout.activity_organization_detail);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        StatusBarManager.setImmersiveStatusBar(this, true);
        mHandler = new Handler(this);


        getAlbumDetail();

    }

    private void getAlbumDetail() {
        mOrganizationId = getIntent().getIntExtra("organizationId", 0);

        String url = ServerInfo.serviceIP + ServerInfo.anchorDetail + mOrganizationId;
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_ORGANIZATION_DETAIL;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, IOException exception) {


            }
        });
    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_ORGANIZATION_DETAIL:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        mOrganization = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), Organization.class);
                        if (!TextUtils.isEmpty(mOrganization.getLogo())) {
                            Uri uri = Uri.parse(mOrganization.getLogo());
                            int width = CommonUtils.dip2px(90);
                            int height = width;
                            ImageLoader.showThumb(uri, logo, width, height);
                        }

                        activityTitle.setTitleText(mOrganization.getName());
                        count.setText("" + mOrganization.getOthers().getCount());
                        follows.setText("" + mOrganization.getOthers().getFollows());
                        likes.setText("" + mOrganization.getOthers().getLikes());
                        desc.setText(mOrganization.getDes());



                    }


                } catch (Exception e) {

                }


                break;
        }
        return false;
    }


    private void initFragment() {
//        mFragments.clear();
//        AlbumIntroduceFragment introduceFragment = new AlbumIntroduceFragment();
//        Bundle introduceData = new Bundle();
//        introduceData.putString("introduction", mAlbum.getDes());
//        introduceFragment.setArguments(introduceData);
//
//        AlbumAudiosFragment audiosFragment = new AlbumAudiosFragment();
//        Bundle audiosData = new Bundle();
//        audiosData.putInt("albumId", mAlbum.getId());
//        audiosFragment.setArguments(audiosData);
//
//        AlbumRecommendFragment recommendFragment = new AlbumRecommendFragment();
//        Bundle recommendData = new Bundle();
//        recommendData.putInt("albumId", mAlbum.getId());
//        recommendFragment.setArguments(recommendData);
//
//
//        mFragments.add(introduceFragment);
//        mFragments.add(audiosFragment);
//        mFragments.add(recommendFragment);
//        MyFragmentPagerAdapter mAdapetr = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
//        viewPager.setAdapter(mAdapetr);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @OnClick({R.id.attention, R.id.more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.attention:
                break;
            case R.id.more:
                break;
        }
    }
}
