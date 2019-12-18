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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.ArrowRectangleView;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.customview.PopupMenu;
import com.shuangling.software.customview.ReadMoreTextViewWithIcon;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.AnchorOrganizationColumn;
import com.shuangling.software.entity.LiveInfo;
import com.shuangling.software.entity.Organization;
import com.shuangling.software.entity.OrganizationMenus;
import com.shuangling.software.entity.Station;
import com.shuangling.software.entity.User;
import com.shuangling.software.fragment.ProgramAnchorFragment;
import com.shuangling.software.fragment.ProgramContentFragment;
import com.shuangling.software.fragment.ProgramRadioFragment;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

@EnableDragToClose()
public class OrganizationDetailActivity extends BaseActivity implements Handler.Callback {

    public static final String TAG = "AnchorDetailActivity";

    public static final int MSG_GET_ORGANIZATION_DETAIL = 0x1;

    public static final int MSG_GET_RECOMMEND_ORGANIZATION = 0x2;

    public static final int MSG_ATTENTION_CALLBACK = 0x3;

    public static final int MSG_ATTENTION_OTHER_CALLBACK = 0x4;

    public static final int MSG_GET_ORGANIZATION_MENUS = 0x5;

    public static final int REQUEST_LOGIN = 0x6;

    public static final int MSG_GET_ANCHOR_COLUMN = 0x7;

    public static final int MSG_GET_ORGANIZATION_LIVE=0x8;

    private static final int[] category = new int[]{R.string.radio, R.string.anchor, R.string.article, R.string.album, R.string.video, R.string.special, R.string.photo};
    //private static final int[] category = new int[]{ R.string.article,R.string.album, R.string.video, R.string.special, R.string.photo};

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
    ReadMoreTextViewWithIcon desc;
    @BindView(R.id.tabPageIndicator)
    TabLayout tabPageIndicator;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.anchorLogo)
    SimpleDraweeView anchorLogo;
    @BindView(R.id.anchorsLayout)
    LinearLayout anchorsLayout;
    @BindView(R.id.anchors)
    LinearLayout anchors;
    @BindView(R.id.menus)
    LinearLayout menus;
    @BindView(R.id.authentication)
    TextView authentication;
    @BindView(R.id.noData)
    LinearLayout noData;
    @BindView(R.id.layout)
    LinearLayout layout;

    private FragmentAdapter mFragmentPagerAdapter;

    private int mOrganizationId;
    private Organization mOrganization;

    private List<Organization> mOrganizations;
    private List<OrganizationMenus> mOrganizationMenus;
    public List<AnchorOrganizationColumn> mColumns;
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    private Handler mHandler;

    private PopupMenu mPopupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_organization_detail);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        mHandler = new Handler(this);

        init();
        getOrganizationDetail();
        getOrganizationMenus();
        getOrganizationLive();

    }


    private void init() {
        mOrganizationId = getIntent().getIntExtra("organizationId", 0);
        getColumn();
//        mFragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(mFragmentPagerAdapter);
//        tabPageIndicator.setupWithViewPager(viewPager);
    }


    private void initFragment() {
        mFragments.clear();
        if (mColumns != null && mColumns.size() > 0) {

            mFragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), mColumns);
            viewPager.setAdapter(mFragmentPagerAdapter);
            tabPageIndicator.setupWithViewPager(viewPager);

            if (mColumns.size() > 5) {
                tabPageIndicator.setTabMode(TabLayout.MODE_SCROLLABLE);
            } else {
                tabPageIndicator.setTabMode(TabLayout.MODE_FIXED);
            }
        }

    }


    private void getColumn() {

        String url = ServerInfo.serviceIP + ServerInfo.getAnchorOrOrganizationColumn + mOrganizationId;
        Map<String, String> params = new HashMap<>();
        params.put("mode","all");
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_ANCHOR_COLUMN;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }


    private void getOrganizationLive() {


        String url = ServerInfo.serviceIP + ServerInfo.getAnchorOrOrganizationLive + mOrganizationId;
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_ORGANIZATION_LIVE;
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }

    private void getOrganizationDetail() {


        String url = ServerInfo.serviceIP + ServerInfo.anchorDetail + mOrganizationId;
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params.put("mode","all");
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_ORGANIZATION_DETAIL;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }


    private void getOrganizationMenus() {
        mOrganizationId = getIntent().getIntExtra("organizationId", 0);

        String url = ServerInfo.serviceIP + ServerInfo.getOrganizationMenus + mOrganizationId;
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_ORGANIZATION_MENUS;
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
        params.put("id", "" + mOrganization.getId());
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


    public void attention(Organization organization, final boolean follow, final View view) {

        String url = ServerInfo.serviceIP + ServerInfo.attention;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + organization.getId());
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


    private void getRecommendOrganization() {

        String url = ServerInfo.serviceIP + ServerInfo.getRecommendAnchor + mOrganizationId;
        Map<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("page_size", "" + Constant.PAGE_SIZE);
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_RECOMMEND_ORGANIZATION;
                msg.obj = response;
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
                        authentication.setText(mOrganization.getName() + "官方账号");
                        activityTitle.setTitleText(mOrganization.getName());
                        if (mOrganization.getOthers() != null) {
                            count.setText("" + mOrganization.getOthers().getCount());
                            follows.setText("" + mOrganization.getOthers().getFollows());
                            likes.setText("" + mOrganization.getOthers().getLikes());
                        }

                        if (!TextUtils.isEmpty(mOrganization.getDes())) {
                            desc.setText(mOrganization.getDes());
                        } else {
                            desc.setText("暂无简介");
                        }

                        if (mOrganization.getIs_follow() == 0) {
                            attention.setText("关注");
                            attention.setActivated(true);
                            more.setActivated(true);
                        } else {
                            attention.setActivated(false);
                            attention.setText("已关注");
                            more.setActivated(false);
                        }

                        attention.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (User.getInstance() == null) {
                                    startActivityForResult(new Intent(OrganizationDetailActivity.this, LoginActivity.class), REQUEST_LOGIN);
                                } else {
                                    attention(mOrganization.getIs_follow() == 0);
                                }
                            }
                        });

                        more.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (anchors.getVisibility() == View.GONE) {
                                    getRecommendOrganization();
                                } else {
                                    anchorsLayout.removeAllViews();
                                    anchors.setVisibility(View.GONE);
                                    more.setImageResource(R.drawable.anchor_more_down_selector);

                                }

                            }
                        });


                    }


                } catch (Exception e) {

                }


                break;
            case MSG_GET_ORGANIZATION_MENUS:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        mOrganizationMenus = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), OrganizationMenus.class);

                        if (mOrganizationMenus.size() > 0 && mOrganizationMenus.get(0).getContent() != null) {

                            for (int i = 0; i < mOrganizationMenus.get(0).getContent().size(); i++) {

                                final OrganizationMenus.ContentBean contentBean = mOrganizationMenus.get(0).getContent().get(i);

                                LayoutInflater inflater = getLayoutInflater();
                                View view = inflater.inflate(R.layout.menu_item, menus, false);
                                TextView menu = view.findViewById(R.id.menu);
                                ImageView divide = view.findViewById(R.id.divide);
                                if (i == mOrganizationMenus.get(0).getContent().size() - 1) {
                                    divide.setVisibility(View.INVISIBLE);
                                }
                                menu.setText(contentBean.getName());

                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (contentBean.getAction() == 1) {
                                            //打开二级菜单
                                            if (contentBean.getChilds() != null && contentBean.getChilds().size() > 0) {
                                                //
                                                ArrowRectangleView menuLayout = (ArrowRectangleView) getLayoutInflater().inflate(R.layout.level_two_menu, null);

                                                for (int j = 0; j < contentBean.getChilds().size(); j++) {
                                                    TextView textView = new TextView(OrganizationDetailActivity.this);
                                                    int paddingLeftRight = CommonUtils.dip2px(10);
                                                    int paddingTopBottom = CommonUtils.dip2px(5);
                                                    textView.setPadding(paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingTopBottom);
                                                    textView.setMinWidth(CommonUtils.dip2px(60));
                                                    textView.setGravity(Gravity.CENTER);
                                                    textView.setTextColor(getResources().getColor(R.color.textColorTwo));
                                                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                                                    textView.setText(contentBean.getChilds().get(j).getName());
                                                    textView.setTag(contentBean.getChilds().get(j));
                                                    menuLayout.addView(textView, new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                                }

                                                mPopupMenu = new PopupMenu((ViewGroup) menuLayout);
                                                mPopupMenu.setMenuItemBackgroundColor(0xffb1df83);
                                                mPopupMenu.setMenuItemHoverBackgroundColor(0x22000000);
                                                mPopupMenu.setOnMenuItemSelectedListener(new PopupMenu.OnMenuItemSelectedListener() {
                                                    @Override
                                                    public void onMenuItemSelected(View menuItem) {
                                                        OrganizationMenus.ContentBean.ChildsBean levelTwoMenu = (OrganizationMenus.ContentBean.ChildsBean) menuItem.getTag();
                                                        Intent it = new Intent(OrganizationDetailActivity.this, WebViewActivity.class);
                                                        it.putExtra("url", levelTwoMenu.getUrl());
                                                        startActivity(it);
                                                    }
                                                });
                                                if (mPopupMenu.isShowing()) {
                                                    mPopupMenu.dismiss();
                                                } else {
                                                    // based on bottom-left, need take menu width and menu icon width into account
                                                    //mPopupMenu.show(menus, (int) 0, (int)0);


                                                    menuLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                                                    int measuredWidth = menuLayout.getMeasuredWidth();
                                                    int measuredHeight = menuLayout.getMeasuredHeight();

                                                    int[] location = new int[2];
                                                    v.getLocationOnScreen(location);
                                                    //popupWindow.showAsDropDown(v);//在v的下面
                                                    //显示在上方
                                                    mPopupMenu.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + v.getWidth() / 2 - measuredWidth / 2, location[1] - measuredHeight);
                                                    //显示在正上方
                                                    //popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - measuredWidth / 2, location[1]-measuredHeight);
                                                    //显示在左方
                                                    //popupWindow.showAtLocation(v,Gravity.NO_GRAVITY,location[0]-popupWindow.getWidth(),location[1]);
                                                    //显示在下方
                                                    //popupWindow.showAtLocation(v,Gravity.NO_GRAVITY,location[0]+v.getWidth(),location[1]);
                                                    mPopupMenu.setAnimationStyle(android.R.style.Animation_Translucent);//设置动画


                                                }

                                            }

                                        } else {
                                            //跳转链接
                                            Intent it = new Intent(OrganizationDetailActivity.this, WebViewActivity.class);
                                            it.putExtra("url", contentBean.getUrl());
                                            startActivity(it);
                                        }
                                    }
                                });
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                                lp.weight = 1;
                                menus.addView(view, lp);


                            }

                        }

                    }


                } catch (Exception e) {

                }
                break;

            case MSG_GET_RECOMMEND_ORGANIZATION:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        mOrganizations = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), Organization.class);
                        if (mOrganizations.size() > 0) {
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


                            for (int i = 0; i < mOrganizations.size(); i++) {
                                final Organization organization = mOrganizations.get(i);

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
                                ImageView authenticationLogo = anchorView.findViewById(R.id.authenticationLogo);
                                authenticationLogo.setBackgroundResource(R.drawable.ic_org_authentication);
                                TextView desc = anchorView.findViewById(R.id.desc);
                                desc.setVisibility(View.GONE);
                                TextView attention = anchorView.findViewById(R.id.attention);
                                if (organization.getIs_follow() == 0) {
                                    attention.setActivated(true);
                                } else {
                                    attention.setActivated(false);
                                }

                                anchorView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent it = new Intent(OrganizationDetailActivity.this, OrganizationDetailActivity.class);
                                        it.putExtra("organizationId", organization.getId());
                                        startActivity(it);
                                    }
                                });
                                attention.setTag(organization);
                                attention.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (User.getInstance() == null) {
                                            startActivityForResult(new Intent(OrganizationDetailActivity.this, LoginActivity.class), REQUEST_LOGIN);
                                        } else {
                                            attention(organization, organization.getIs_follow() == 0, v);
                                        }

                                    }
                                });

                                if (!TextUtils.isEmpty(organization.getLogo())) {
                                    Uri uri = Uri.parse(organization.getLogo());
                                    int width = CommonUtils.dip2px(65);
                                    int height = width;
                                    ImageLoader.showThumb(uri, logo, width, height);
                                }
                                anchorName.setText(organization.getName());
                                desc.setText(organization.getDes());

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
                                    Intent it = new Intent(OrganizationDetailActivity.this, MoreAnchorOrOrganizationActivity.class);
                                    it.putExtra("type", 1);
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
                            mOrganization.setIs_follow(1);

                            if (anchors.getVisibility() == View.GONE) {
                                getRecommendOrganization();
                            }
                        } else {
                            attention.setText("关注");
                            attention.setActivated(true);
                            more.setActivated(true);
                            mOrganization.setIs_follow(0);
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
                        Organization organization = (Organization) attention.getTag();
                        if (follow) {
                            attention.setText("已关注");
                            attention.setActivated(false);
                            organization.setIs_follow(1);
                        } else {
                            attention.setText("关注");
                            attention.setActivated(true);
                            organization.setIs_follow(0);

                        }

                    }


                } catch (Exception e) {

                }
                break;
            case MSG_GET_ANCHOR_COLUMN:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        mColumns = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), AnchorOrganizationColumn.class);


                        if (mColumns == null || mColumns.size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            noData.setVisibility(View.GONE);
                            initFragment();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_GET_ORGANIZATION_LIVE:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        final LiveInfo liveInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), LiveInfo.class);

                        if (liveInfo != null) {
                            LayoutInflater inflater = LayoutInflater.from(this);
                            View root = inflater.inflate(R.layout.anchor_or_organization_live_item, layout, false);

                            ViewHolder vh = new ViewHolder(root);

                            if (!TextUtils.isEmpty(liveInfo.getCover())) {
                                Uri uri = Uri.parse(liveInfo.getCover());
                                int width = CommonUtils.getScreenWidth();
                                int height = (int) (9f * width / 16f);
                                ImageLoader.showThumb(uri, vh.logo, width, height);
                            } else {
                                ImageLoader.showThumb(vh.logo, R.drawable.video_placeholder);
                            }
                            Glide.with(this).load(R.drawable.wave).diskCacheStrategy(DiskCacheStrategy.ALL).into(vh.statusIcon);

                            vh.title.setText(liveInfo.getTitle());
                            if (liveInfo.getLive() != null) {
                                vh.popularity.setText(liveInfo.getLive().getPopularity() + "人气");
                                if (liveInfo.getLive().getType() == 1) {
                                    vh.type.setText("网络");
                                    vh.typeIcon.setText(R.string.live_network);
                                } else if (liveInfo.getLive().getType() == 2) {
                                    vh.type.setText("电台");
                                    vh.typeIcon.setText(R.string.live_radio);
                                } else if (liveInfo.getLive().getType() == 3) {
                                    vh.type.setText("电视");
                                    vh.typeIcon.setText(R.string.live_tv);
                                } else if (liveInfo.getLive().getType() == 4) {
                                    vh.type.setText("电商");
                                    vh.typeIcon.setText(R.string.live_shop);
                                }

                            }

                            vh.root.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it = new Intent(OrganizationDetailActivity.this, WebViewBackActivity.class);
                                    it.putExtra("url", liveInfo.getLive().getUrl());
                                    startActivity(it);
                                }
                            });

                            layout.addView(root,4);
                        }


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
        }
        return false;
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


    public class FragmentAdapter extends FragmentStatePagerAdapter {

        private FragmentManager fm;
        private List<AnchorOrganizationColumn> mColumns;

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public FragmentAdapter(FragmentManager fm, List<AnchorOrganizationColumn> columns) {
            super(fm);
            this.fm = fm;
            mColumns = columns;

        }

        @Override
        public int getCount() {
            return mColumns.size();
        }

        @Override
        public Fragment getItem(int position) {

            if (mColumns.get(position).getType() == 2 || mColumns.get(position).getType() == 6) {
                //电台
                ProgramRadioFragment fragment = new ProgramRadioFragment();
                Bundle bundle = new Bundle();
                bundle.putString("organizationId", "" + mOrganizationId);
                bundle.putSerializable("columns", mColumns.get(position));
                fragment.setArguments(bundle);
                return fragment;
            } else if (mColumns.get(position).getType() == 3) {
                //主播
                ProgramAnchorFragment fragment = new ProgramAnchorFragment();
                Bundle bundle = new Bundle();
                bundle.putString("organizationId", "" + mOrganizationId);
                bundle.putSerializable("columns", mColumns.get(position));
                fragment.setArguments(bundle);
                return fragment;
            } else {
                ProgramContentFragment fragment = new ProgramContentFragment();
                Bundle bundle = new Bundle();
                bundle.putString("organizationId", "" + mOrganizationId);
                bundle.putSerializable("columns", mColumns.get(position));
                fragment.setArguments(bundle);
                return fragment;
            }


        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mColumns.get(position).getName();
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            //得到缓存的fragment
            Fragment fragment = (Fragment) super.instantiateItem(container, position);

            return fragment;
        }

    }


    class ViewHolder {
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.playIcon)
        ImageView playIcon;
        @BindView(R.id.statusIcon)
        ImageView statusIcon;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.statusLayout)
        LinearLayout statusLayout;
        @BindView(R.id.popularity)
        TextView popularity;
        @BindView(R.id.typeIcon)
        FontIconView typeIcon;
        @BindView(R.id.type)
        TextView type;
        @BindView(R.id.root)
        LinearLayout root;
        @BindView(R.id.title)
        TextView title;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
