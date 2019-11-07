package com.shuangling.software.activity;

import android.app.Activity;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.Anchor;
import com.shuangling.software.entity.Station;
import com.shuangling.software.entity.User;
import com.shuangling.software.fragment.ProgramContentFragment;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

@EnableDragToClose()
public class AnchorDetailActivity extends BaseActivity implements Handler.Callback {

    public static final String TAG = "AnchorDetailActivity";

    public static final int MSG_GET_ANCHOR_DETAIL = 0x1;

    public static final int MSG_GET_RECOMMEND_ANCHOR = 0x2;

    public static final int MSG_ATTENTION_CALLBACK = 0x3;

    public static final int MSG_ATTENTION_OTHER_CALLBACK = 0x4;

    public static final int REQUEST_LOGIN = 0x5;

    private static final int[] category = new int[]{R.string.album, R.string.article, R.string.video, R.string.special, R.string.photo};

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
    @BindView(R.id.anchorsLayout)
    LinearLayout anchorsLayout;
    @BindView(R.id.anchorLogo)
    SimpleDraweeView anchorLogo;
    @BindView(R.id.anchors)
    LinearLayout anchors;
    @BindView(R.id.authentication)
    TextView authentication;


    private int mAnchorId;
    private FragmentAdapter mFragmentPagerAdapter;
    private Anchor mAnchor;
    private List<Anchor> mAnchors;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_anchor_detail);
        ButterKnife.bind(this);
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
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_ANCHOR_DETAIL;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }


    private void getRecommendAnchor() {

        String url = ServerInfo.serviceIP + ServerInfo.getRecommendAnchor + mAnchorId;
        Map<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("page_size", "" + Constant.PAGE_SIZE);
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_RECOMMEND_ANCHOR;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }


    public void attention(final boolean attention) {

        String url = ServerInfo.serviceIP + ServerInfo.attention;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + mAnchor.getId());
        if (attention) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }

        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_ATTENTION_CALLBACK;
                msg.arg1 = attention ? 1 : 0;
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });

    }


    public void attention(Anchor anchor, final boolean follow, final View view) {

        String url = ServerInfo.serviceIP + ServerInfo.attention;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + anchor.getId());
        if (follow) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }

        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_ATTENTION_OTHER_CALLBACK;
                msg.arg1 = follow ? 1 : 0;
                Bundle bundle = new Bundle();
                bundle.putString("response", response);
                msg.setData(bundle);
                msg.obj = view;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


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
                        authentication.setText(mAnchor.getMerchant().getName()+"官方主播");
                        activityTitle.setTitleText(mAnchor.getName());
                        if (mAnchor.getOthers() != null) {
                            count.setText("" + mAnchor.getOthers().getCount());
                            follows.setText("" + mAnchor.getOthers().getFollows());
                            likes.setText("" + mAnchor.getOthers().getLikes());
                        }

                        if (!TextUtils.isEmpty(mAnchor.getDes())) {
                            desc.setText(mAnchor.getDes());
                        } else {
                            desc.setText("暂无简介");
                        }

                        organization.setText(mAnchor.getMerchant().getName());

                        if (mAnchor.getIs_follow() == 0) {
                            attention.setActivated(true);
                            more.setActivated(true);
                            attention.setText("关注");
                        } else {
                            attention.setActivated(false);
                            more.setActivated(false);
                            attention.setText("已关注");
                        }

                        attention.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (User.getInstance() == null) {
                                    startActivityForResult(new Intent(AnchorDetailActivity.this, LoginActivity.class), REQUEST_LOGIN);
                                } else {
                                    attention(mAnchor.getIs_follow() == 0);
                                }
                            }
                        });
                        more.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (anchors.getVisibility() == View.GONE) {
                                    getRecommendAnchor();
                                } else {
                                    anchorsLayout.removeAllViews();
                                    anchors.setVisibility(View.GONE);
                                    more.setImageResource(R.drawable.anchor_more_down_selector);

                                }

                            }
                        });


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case MSG_GET_RECOMMEND_ANCHOR:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        mAnchors = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), Anchor.class);
                        if (mAnchors.size() > 0) {
                            more.setImageResource(R.drawable.anchor_more_up_selector);
                            anchorsLayout.removeAllViews();
                            anchors.setVisibility(View.VISIBLE);
                            Station station = MyApplication.getInstance().getStation();
                            if (station != null && !TextUtils.isEmpty(station.getIcon2())) {
                                Uri uri = Uri.parse(station.getIcon2());
                                int width = CommonUtils.dip2px(15);
                                int height = width;
                                ImageLoader.showThumb(uri, anchorLogo, width, height);
                            } else {
                                anchorLogo.setVisibility(View.GONE);
                            }


                            for (int i = 0; i < mAnchors.size(); i++) {
                                final Anchor anchor = mAnchors.get(i);

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonUtils.dip2px(120), LinearLayout.LayoutParams.WRAP_CONTENT);
                                int marginLeft;
                                if (i == 0) {
                                    marginLeft = CommonUtils.dip2px(20);
                                } else {
                                    marginLeft = CommonUtils.dip2px(10);
                                }
                                int margin = CommonUtils.dip2px(10);
                                params.setMargins(marginLeft, margin, margin, margin);
                                View anchorView = LayoutInflater.from(this).inflate(R.layout.anchor_item_layout, anchorsLayout, false);
                                TextView anchorName = anchorView.findViewById(R.id.anchorName);
                                SimpleDraweeView logo = anchorView.findViewById(R.id.logo);
                                TextView desc = anchorView.findViewById(R.id.desc);
                                desc.setVisibility(View.GONE);
                                TextView attention = anchorView.findViewById(R.id.attention);
                                if (anchor.getIs_follow() == 0) {
                                    attention.setActivated(true);
                                } else {
                                    attention.setActivated(false);
                                }

                                anchorView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent it = new Intent(AnchorDetailActivity.this, AnchorDetailActivity.class);
                                        it.putExtra("anchorId", anchor.getId());
                                        startActivity(it);
                                    }
                                });
                                attention.setTag(anchor);
                                attention.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (User.getInstance() == null) {
                                            startActivityForResult(new Intent(AnchorDetailActivity.this, LoginActivity.class), REQUEST_LOGIN);
                                        } else {
                                            attention(anchor, anchor.getIs_follow() == 0, v);
                                        }

                                    }
                                });

                                if (!TextUtils.isEmpty(anchor.getLogo())) {
                                    Uri uri = Uri.parse(anchor.getLogo());
                                    int width = CommonUtils.dip2px(65);
                                    int height = width;
                                    ImageLoader.showThumb(uri, logo, width, height);
                                }
                                anchorName.setText(anchor.getName());
                                desc.setText(anchor.getDes());

                                anchorsLayout.addView(anchorView, i, params);
                            }
                            //更多

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonUtils.dip2px(120), LinearLayout.LayoutParams.WRAP_CONTENT);
                            int margin = CommonUtils.dip2px(5);
                            params.setMargins(margin, margin, margin, margin);
                            params.gravity = Gravity.CENTER_VERTICAL;
                            View more = LayoutInflater.from(this).inflate(R.layout.more_item_layout, anchorsLayout, false);

                            more.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it = new Intent(AnchorDetailActivity.this, MoreAnchorOrOrganizationActivity.class);
                                    it.putExtra("type", 2);
                                    it.putExtra("orderBy", 1);
                                    startActivity(it);

                                }
                            });

                            anchorsLayout.addView(more, params);
                        }


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_ATTENTION_CALLBACK:
                try {
                    String result = (String) msg.obj;
                    boolean follow = msg.arg1 == 1 ? true : false;

                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show(jsonObject.getString("msg"));

                        if (follow) {
                            attention.setText("已关注");
                            attention.setActivated(false);
                            more.setActivated(false);
                            mAnchor.setIs_follow(1);

                            if (anchors.getVisibility() == View.GONE) {
                                getRecommendAnchor();
                            }


                        } else {
                            attention.setText("关注");
                            attention.setActivated(true);
                            more.setActivated(true);
                            mAnchor.setIs_follow(0);
                            if (anchors.getVisibility() == View.VISIBLE) {
                                anchorsLayout.removeAllViews();
                                anchors.setVisibility(View.GONE);
                                more.setImageResource(R.drawable.anchor_more_down_selector);
                            }

                        }


                    }


                } catch (Exception e) {

                }
                break;
            case MSG_ATTENTION_OTHER_CALLBACK:
                try {
                    String result = msg.getData().getString("response");
                    boolean follow = msg.arg1 == 1 ? true : false;

                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show(jsonObject.getString("msg"));
                        TextView attention = (TextView) msg.obj;
                        Anchor anchor = (Anchor) attention.getTag();
                        if (follow) {
                            attention.setText("已关注");
                            attention.setActivated(false);
                            anchor.setIs_follow(1);
                        } else {
                            attention.setText("关注");
                            attention.setActivated(true);
                            anchor.setIs_follow(0);

                        }

                    }


                } catch (Exception e) {

                }
                break;
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.attention, R.id.more, R.id.organizationLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.attention:
                break;
            case R.id.more:
                break;
            case R.id.organizationLayout:
                Intent it = new Intent(this, OrganizationDetailActivity.class);
                it.putExtra("organizationId", mAnchor.getMerchant().getId());
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


            ProgramContentFragment fragment = new ProgramContentFragment();
            Bundle bundle = new Bundle();
            bundle.putString("anchorId", "" + mAnchorId);
            bundle.putString("category", getResources().getString(category[position]));
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getString(category[position]);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK) {
            getAnchorDetail();
            if (anchors.getVisibility() == View.VISIBLE) {
                getRecommendAnchor();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
