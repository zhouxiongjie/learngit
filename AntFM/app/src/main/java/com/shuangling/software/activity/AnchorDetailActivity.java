package com.shuangling.software.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jaeger.library.StatusBarUtil;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.Anchor;
import com.shuangling.software.fragment.SearchListFragment;
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


public class AnchorDetailActivity extends BaseActivity implements Handler.Callback {

    public static final String TAG = "AnchorDetailActivity";

    private static final String[] category = new String[]{"专辑",  "资讯", "视频","专题"};

    public static final int MSG_GET_ANCHOR_DETAIL = 0x1;
    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.logo)
    SimpleDraweeView logo;
    @BindView(R.id.attention)
    TextView attention;
    @BindView(R.id.more)
    ImageView more;
    @BindView(R.id.tabPageIndicator)
    TabLayout tabPageIndicator;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.count)
    TextView count;
    @BindView(R.id.follows)
    TextView follows;
    @BindView(R.id.likes)
    TextView likes;
    @BindView(R.id.desc)
    TextView desc;
    @BindView(R.id.organization)
    TextView organization;
    @BindView(R.id.organizationLayout)
    LinearLayout organizationLayout;

    private int mAnchorId;
    private FragmentAdapter mFragmentPagerAdapter;
    private Anchor mAnchor;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(MyApplication.getInstance().getCurrentTheme());
        setContentView(R.layout.activity_anchor_detail);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        StatusBarManager.setImmersiveStatusBar(this, true);
        mHandler = new Handler(this);

        init();

        getAnchorDetail();

    }

    private void init() {
        mFragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mFragmentPagerAdapter);
        tabPageIndicator.setupWithViewPager(viewPager);
    }

    private void getAnchorDetail() {
        mAnchorId = getIntent().getIntExtra("anchorId", 0);

        String url = ServerInfo.serviceIP + ServerInfo.anchorDetail + mAnchorId;
        Map<String, String> params = new HashMap<>();
        params.put("type", "0");
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_ANCHOR_DETAIL;
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
            case MSG_GET_ANCHOR_DETAIL:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        mAnchor = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), Anchor.class);
                        if (!TextUtils.isEmpty(mAnchor.getLogo())) {
                            Uri uri = Uri.parse(mAnchor.getLogo());
                            int width = CommonUtils.dip2px(90);
                            int height = width;
                            ImageLoader.showThumb(uri, logo, width, height);
                        }

                        activityTitle.setTitleText(mAnchor.getName());
                        count.setText(""+mAnchor.getOthers().getCount());
                        follows.setText(""+mAnchor.getOthers().getFollows());
                        likes.setText(""+mAnchor.getOthers().getLikes());
                        desc.setText(mAnchor.getDes());
                        organization.setText(mAnchor.getMerchant().getName());



                    }


                } catch (Exception e) {
                    e.printStackTrace();
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

    @OnClick({R.id.attention, R.id.more,R.id.organizationLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.attention:
                break;
            case R.id.more:
                break;
            case R.id.organizationLayout:
                Intent it=new Intent(this,OrganizationDetailActivity.class);
                it.putExtra("organizationId",mAnchor.getMerchant().getId());
                startActivity(it);
                break;
        }
    }




    public class FragmentAdapter extends FragmentStatePagerAdapter {

        private FragmentManager fm;

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }


        @Override
        public int getCount() {
            return category.length;
        }

        @Override
        public Fragment getItem(int position) {


            SearchListFragment fragment = new SearchListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("category", category[position]);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return category[position];
        }



        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            //得到缓存的fragment
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            //得到tag，这点很重要
//            String fragmentTag = fragment.getTag();
//            FragmentTransaction ft = fm.beginTransaction();
//            //移除旧的fragment
//            ft.remove(fragment);
//            //换成新的fragment
//            fragment = getItem(position);
//            //添加新fragment时必须用前面获得的tag，这点很重要
//            ft.add(container.getId(), fragment, fragmentTag);
//            ft.attach(fragment);
//            ft.commit();
            return fragment;
        }

    }
}
