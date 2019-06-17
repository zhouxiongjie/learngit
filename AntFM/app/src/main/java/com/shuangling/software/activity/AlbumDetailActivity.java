package com.shuangling.software.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jaeger.library.StatusBarUtil;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.MyFragmentPagerAdapter;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.Album;
import com.shuangling.software.fragment.AlbumAudiosFragment;
import com.shuangling.software.fragment.AlbumIntroduceFragment;
import com.shuangling.software.fragment.AlbumRecommendFragment;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.StatusBarManager;
import java.io.IOException;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;


public class AlbumDetailActivity extends BaseActivity implements Handler.Callback {

    public static final String TAG = "AlbumDetailActivity";

    public static final int MSG_GET_ALBUM_DETAIL = 0x1;

    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.logo)
    SimpleDraweeView logo;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.head)
    SimpleDraweeView head;
    @BindView(R.id.subscribe)
    TextView subscribe;

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.albumTitle)
    TextView albumTitle;
    @BindView(R.id.indicator1)
    SimpleDraweeView indicator1;
    @BindView(R.id.indicator2)
    SimpleDraweeView indicator2;
    @BindView(R.id.indicator3)
    SimpleDraweeView indicator3;
    @BindView(R.id.introduction)
    LinearLayout introduction;
    @BindView(R.id.program)
    LinearLayout program;
    @BindView(R.id.recommend)
    LinearLayout recommend;

    private Handler mHandler;

    private int mAlbumId;
    private Album mAlbum;
    private ArrayList<Fragment> mFragments = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(MyApplication.getInstance().getCurrentTheme());
        setContentView(R.layout.activity_album_detail);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        StatusBarManager.setImmersiveStatusBar(this, true);
        mHandler = new Handler(this);



        getAlbumDetail();

    }

    private void getAlbumDetail() {
        mAlbumId = getIntent().getIntExtra("albumId", 0);

        String url = ServerInfo.serviceIP + ServerInfo.getAlbumDetail + mAlbumId;
        OkHttpUtils.get(url, null, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_ALBUM_DETAIL;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, IOException exception) {


            }
        });
    }


    @OnClick({R.id.subscribe, R.id.introduction, R.id.program, R.id.recommend})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.subscribe:
                break;
            case R.id.introduction:
                selectPage(0);
                break;
            case R.id.program:
                selectPage(1);
                break;
            case R.id.recommend:
                selectPage(2);
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_ALBUM_DETAIL:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        mAlbum = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), Album.class);

                        if (!TextUtils.isEmpty(mAlbum.getCover())) {
                            Uri uri = Uri.parse(mAlbum.getCover());
                            int width = (int) getResources().getDimension(R.dimen.article_right_image_width);
                            int height = width;
                            ImageLoader.showThumb(uri, logo, width, height);
                        }
                        if (mAlbum.getAuthor_info() != null && mAlbum.getAuthor_info().getMerchant() != null) {
                            if (!TextUtils.isEmpty(mAlbum.getAuthor_info().getMerchant().getName())) {
                                name.setText(mAlbum.getAuthor_info().getMerchant().getName());
                            }
                            if (!TextUtils.isEmpty(mAlbum.getAuthor_info().getMerchant().getLogo())) {
                                Uri uri = Uri.parse(mAlbum.getAuthor_info().getMerchant().getLogo());
                                int width = CommonUtils.dip2px(30);
                                int height = width;
                                ImageLoader.showThumb(uri, head, width, height);
                            }
                        }
                        if (mAlbum.getIs_sub() == 1) {
                            subscribe.setText(getResources().getString(R.string.has_subscribe));
                        } else {
                            subscribe.setText(getResources().getString(R.string.subscribe));
                        }
                        albumTitle.setText(mAlbum.getTitle());
                        initFragment();
                        selectPage(0);

                    }


                } catch (Exception e) {

                }


                break;
        }
        return false;
    }


    private void initFragment() {
        mFragments.clear();
        AlbumIntroduceFragment introduceFragment = new AlbumIntroduceFragment();
        Bundle introduceData = new Bundle();
        introduceData.putString("introduction", mAlbum.getDes());
        introduceFragment.setArguments(introduceData);

        AlbumAudiosFragment audiosFragment = new AlbumAudiosFragment();
        Bundle audiosData = new Bundle();
        audiosData.putInt("albumId", mAlbum.getId());
        audiosFragment.setArguments(audiosData);

        AlbumRecommendFragment recommendFragment = new AlbumRecommendFragment();
        Bundle recommendData = new Bundle();
        recommendData.putInt("albumId", mAlbum.getId());
        recommendFragment.setArguments(recommendData);


        mFragments.add(introduceFragment);
        mFragments.add(audiosFragment);
        mFragments.add(recommendFragment);
        MyFragmentPagerAdapter mAdapetr = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        viewPager.setAdapter(mAdapetr);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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


    private void selectPage(int page) {
        viewPager.setCurrentItem(page);
        if (page == 0) {
            introduction.setSelected(true);
            program.setSelected(false);
            recommend.setSelected(false);
            indicator1.setVisibility(View.VISIBLE);
            indicator2.setVisibility(View.INVISIBLE);
            indicator3.setVisibility(View.INVISIBLE);
        } else if (page == 1) {
            introduction.setSelected(false);
            program.setSelected(true);
            recommend.setSelected(false);
            indicator1.setVisibility(View.INVISIBLE);
            indicator2.setVisibility(View.VISIBLE);
            indicator3.setVisibility(View.INVISIBLE);
        } else {
            introduction.setSelected(false);
            program.setSelected(false);
            recommend.setSelected(true);
            indicator1.setVisibility(View.INVISIBLE);
            indicator2.setVisibility(View.INVISIBLE);
            indicator3.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
