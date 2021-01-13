package com.shuangling.software.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.components.SimpleImmersionFragment;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.activity.ArticleDetailActivity02;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.activity.BindPhoneActivity;
import com.shuangling.software.activity.CluesActivity;
import com.shuangling.software.activity.ContentActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.activity.MainActivity;
import com.shuangling.software.activity.NewLoginActivity;
import com.shuangling.software.activity.RadioDetailActivity;
import com.shuangling.software.activity.RadioListActivity;
import com.shuangling.software.activity.SpecialDetailActivity;
import com.shuangling.software.activity.TvDetailActivity;
import com.shuangling.software.activity.VideoDetailActivity;
import com.shuangling.software.activity.WebViewBackActivity;
import com.shuangling.software.adapter.ColumnAlbumContentAdapter;
import com.shuangling.software.adapter.ColumnContentAdapter;
import com.shuangling.software.adapter.ColumnDecorateContentAdapter;
import com.shuangling.software.adapter.ColumnDecorateVideoContentAdapter;
import com.shuangling.software.adapter.MoudleGridViewAdapter;
import com.shuangling.software.customview.BannerView;
import com.shuangling.software.customview.MyGridView;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.BannerInfo;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.DecorModule;
import com.shuangling.software.entity.Station;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ACache;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

public class ColumnFragment extends QMUIFragment/*SimpleImmersionFragment*/ implements Handler.Callback {
    public static final int MSG_UPDATE_LIST = 0x1;
    public static final int MSG_GET_COLUMN_DECORATE = 0x2;
    public static final int MSG_GET_TYPE_CONTENT = 0x3;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    //Unbinder unbinder;
    @BindView(R.id.noData)
    RelativeLayout noData;
    @BindView(R.id.refresh)
    TextView refresh;
    @BindView(R.id.networkError)
    RelativeLayout networkError;
    @BindView(R.id.activtyTitle)
    TopTitleBar activtyTitle;
    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.statusBar)
    View statusBar;
    private Column mColumn;
    private String mOrderBy = "1";
    private List<ColumnContent> mColumnContents = new ArrayList<>();
    private ColumnDecorateContentAdapter mAdapter;
    private Handler mHandler;
    private boolean addInfoStream;
    private boolean hasDecorate = false;

//    @Override
//    public void initImmersionBar() {
//        ImmersionBar.with(this).statusBarView(statusBar).statusBarDarkFont(true).init();
//    }

    public enum GetContent {
        Refresh,
        LoadMore,
        Normal
    }

    private ACache mACache;
    private RecyclerViewSkeletonScreen mSkeletonScreen;
    //private ViewSkeletonScreen mSkeletonScreen;
    private List<RecyclerView> mContentRecyclerView = new ArrayList<>();
    private LinearLayout mDecorateLayout;
    private boolean mShouldHide = false;
    private AtomicInteger mInterfaceNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Bundle args = getArguments();
        mACache = ACache.get(getContext());
        mColumn = (Column) args.getSerializable("Column");
        mHandler = new Handler(this);
        EventBus.getDefault().register(this);
        ClassicsFooter.REFRESH_FOOTER_NOTHING = "没有更多了";
        super.onCreate(savedInstanceState);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_column, null);
//        unbinder = ButterKnife.bind(this, view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//        activtyTitle.setTitleText(mColumn.getName());
//        //添加自定义分割线
////        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
////        refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));//设置
////        refreshLayout.setPrimaryColorsId(R.color.white, android.R.color.black);
//        //((ClassicsHeader) refreshLayout.getRefreshHeader()).setEnableLastTime(false);
//        refreshLayout.setEnableLoadMore(false);
////        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
////            @Override
////            public void onRefresh(RefreshLayout refreshlayout) {
////                //getContent(GetContent.Refresh);
////                columnDecorateContent(GetContent.Refresh);
////            }
////        });
//        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(RefreshLayout refreshLayout) {
//                getContent(GetContent.LoadMore);
//            }
//        });
//        mAdapter = new ColumnDecorateContentAdapter(getContext());
//        if (mColumn.getPost_type() == 4) {
//            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//            divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_video_divider_drawable));
//            recyclerView.addItemDecoration(divider);
//            mAdapter.setIsVideo(true);
//        } else if (mColumn.getPost_type() == 9) {
//            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//            divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
//            recyclerView.addItemDecoration(divider);
//            mAdapter.setIsActivity(true);
//        } else {
//            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//            divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
//            recyclerView.addItemDecoration(divider);
//        }
//        recyclerView.setAdapter(mAdapter);
//        if (mColumn.getChildren() != null && mColumn.getChildren().size() > 0) {
//            ViewGroup secondColumn = (ViewGroup) inflater.inflate(R.layout.second_column_layout, recyclerView, false);
//            LinearLayout columnContent = secondColumn.findViewById(R.id.columnContent);
//            for (int i = 0; i < mColumn.getChildren().size(); i++) {
//                final Column column = mColumn.getChildren().get(i);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                params.leftMargin = CommonUtils.dip2px(10);
//                params.rightMargin = CommonUtils.dip2px(10);
//                View secondColumnItem = inflater.inflate(R.layout.second_column_item_layout, secondColumn, false);
//                TextView columnTextView = secondColumnItem.findViewById(R.id.text);
//                SimpleDraweeView indicator = secondColumnItem.findViewById(R.id.logo);
//                columnTextView.setText(column.getName());
//                if (!TextUtils.isEmpty(column.getIcon())) {
//                    Uri uri = Uri.parse(column.getIcon());
//                    int width = CommonUtils.dip2px(20);
//                    int height = width;
//                    ImageLoader.showThumb(uri, indicator, width, height);
//                } else {
//                    indicator.setVisibility(View.GONE);
//                }
//                secondColumnItem.setTag(column);
//                secondColumnItem.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent it = new Intent(getContext(), ContentActivity.class);
//                        it.putExtra("column", column);
//                        startActivity(it);
//                    }
//                });
//                columnContent.addView(secondColumnItem, i, params);
//            }
//            mAdapter.addHeaderView(secondColumn);
//        }
//        /*String cache = mACache.getAsString(ServerInfo.serviceIP + ServerInfo.getColumnContent + mColumn.getId());
//        if (!TextUtils.isEmpty(cache)) {
//mColumnContents = JSONObject.parseArray(cache, ColumnContent.class);
//if (mAdapter == null) {
//                mAdapter = new ColumnDecorateContentAdapter(getContext(), mColumnContents);
//mAdapter.setOnItemClickListener(new ColumnDecorateContentAdapter.OnItemClickListener() {
//@Override
//                    public void onItemClick(View view, ColumnContent content) {
//}
//@Override
//                    public void onItemClick(View view, int pos) {
//}
//                });
//                //设置添加或删除item时的动画，这里使用默认动画
//                recyclerView.setItemAnimator(new DefaultItemAnimator());
//                //设置适配器
//                recyclerView.setAdapter(mAdapter);
//} else {
//                mAdapter.setData(mColumnContents);
//                mAdapter.notifyDataSetChanged();
//}
//} else {
//getContent(GetContent.Normal);
//        }
//*/
//        columnDecorateContent(GetContent.Normal);
//        return view;
//    }

    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_column, null);
        ButterKnife.bind(this, rootView);
        CommonUtils.setStatusHeight(getContext(),statusBar);
        QMUIStatusBarHelper.setStatusBarLightMode(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        activtyTitle.setTitleText(mColumn.getName());

        refreshLayout.setEnableLoadMore(false);

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                getContent(GetContent.LoadMore);
            }
        });
        mAdapter = new ColumnDecorateContentAdapter(getContext());
        if (mColumn.getPost_type() == 4) {
            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_video_divider_drawable));
            recyclerView.addItemDecoration(divider);
            mAdapter.setIsVideo(true);
        } else if (mColumn.getPost_type() == 9) {
            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
            recyclerView.addItemDecoration(divider);
            mAdapter.setIsActivity(true);
        } else {
            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
            recyclerView.addItemDecoration(divider);
        }
        recyclerView.setAdapter(mAdapter);
        if (mColumn.getChildren() != null && mColumn.getChildren().size() > 0) {
            ViewGroup secondColumn = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.second_column_layout, recyclerView, false);
            LinearLayout columnContent = secondColumn.findViewById(R.id.columnContent);
            for (int i = 0; i < mColumn.getChildren().size(); i++) {
                final Column column = mColumn.getChildren().get(i);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.leftMargin = CommonUtils.dip2px(10);
                params.rightMargin = CommonUtils.dip2px(10);
                View secondColumnItem = LayoutInflater.from(getActivity()).inflate(R.layout.second_column_item_layout, secondColumn, false);
                TextView columnTextView = secondColumnItem.findViewById(R.id.text);
                SimpleDraweeView indicator = secondColumnItem.findViewById(R.id.logo);
                columnTextView.setText(column.getName());
                if (!TextUtils.isEmpty(column.getIcon())) {
                    Uri uri = Uri.parse(column.getIcon());
                    int width = CommonUtils.dip2px(20);
                    int height = width;
                    ImageLoader.showThumb(uri, indicator, width, height);
                } else {
                    indicator.setVisibility(View.GONE);
                }
                secondColumnItem.setTag(column);
                secondColumnItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(getContext(), ContentActivity.class);
                        it.putExtra("column", column);
                        startActivity(it);
                    }
                });
                columnContent.addView(secondColumnItem, i, params);
            }
            mAdapter.addHeaderView(secondColumn);
        }

        columnDecorateContent(GetContent.Normal);
        return rootView;
    }

    public void setColumn(Column col) {
        mColumn = col;
        if (activtyTitle == null) {
            return;
        }
        activtyTitle.setTitleText(mColumn.getName());
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                getContent(GetContent.LoadMore);
            }
        });
        mAdapter = new ColumnDecorateContentAdapter(getContext());
        if (mColumn.getPost_type() == 4) {
            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_video_divider_drawable));
            recyclerView.addItemDecoration(divider);
            mAdapter.setIsVideo(true);
        } else if (mColumn.getPost_type() == 9) {
            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
            recyclerView.addItemDecoration(divider);
            mAdapter.setIsActivity(true);
        } else {
            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
            recyclerView.addItemDecoration(divider);
        }
        recyclerView.setAdapter(mAdapter);
        if (mColumn.getChildren() != null && mColumn.getChildren().size() > 0) {
            ViewGroup secondColumn = (ViewGroup) getLayoutInflater().inflate(R.layout.second_column_layout, recyclerView, false);
            LinearLayout columnContent = secondColumn.findViewById(R.id.columnContent);
            for (int i = 0; i < mColumn.getChildren().size(); i++) {
                final Column column = mColumn.getChildren().get(i);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.leftMargin = CommonUtils.dip2px(10);
                params.rightMargin = CommonUtils.dip2px(10);
                View secondColumnItem = getLayoutInflater().inflate(R.layout.second_column_item_layout, secondColumn, false);
                TextView columnTextView = secondColumnItem.findViewById(R.id.text);
                SimpleDraweeView indicator = secondColumnItem.findViewById(R.id.logo);
                columnTextView.setText(column.getName());
                if (!TextUtils.isEmpty(column.getIcon())) {
                    Uri uri = Uri.parse(column.getIcon());
                    int width = CommonUtils.dip2px(20);
                    int height = width;
                    ImageLoader.showThumb(uri, indicator, width, height);
                } else {
                    indicator.setVisibility(View.GONE);
                }
                secondColumnItem.setTag(column);
                secondColumnItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(getContext(), ContentActivity.class);
                        it.putExtra("column", column);
                        startActivity(it);
                    }
                });
                columnContent.addView(secondColumnItem, i, params);
            }
            mAdapter.addHeaderView(secondColumn);
        }
        columnDecorateContent(GetContent.Normal);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("onFontSizeChanged")) {
            columnDecorateContent(GetContent.Normal);
        }
    }

    public void columnDecorateContent(final GetContent getContent) {
        if (getContent == GetContent.Normal) {
            mSkeletonScreen =
                    Skeleton.bind(recyclerView)
                            .adapter(mAdapter)
                            .shimmer(false)
                            .angle(20)
                            .frozen(false)
                            .duration(3000)
                            //.count(4)
                            .load(R.layout.skeleton_content)
                            .show();
//            mSkeletonScreen = Skeleton.bind(root)
//                    .load(R.layout.skeleton_content)
//                    .shimmer(false)
//                    .angle(20)
//                    .duration(1000)
//                    .color(R.color.shimmer_color)
//                    .show();
        }
        String url = ServerInfo.serviceIP + ServerInfo.columnDecorateContent + mColumn.getId();
        Map<String, String> params = new HashMap<String, String>();
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mShouldHide = false;
                            networkError.setVisibility(View.GONE);
                            if (getContent == GetContent.Refresh) {
                                if (refreshLayout.getState() == RefreshState.Refreshing) {
                                    refreshLayout.finishRefresh();
                                }
                            } else if (getContent == GetContent.Normal) {
                                //mSkeletonScreen.hide();
                                mShouldHide = true;
                            }
                        } catch (Exception e) {
                        }
                    }
                });
                Message msg = Message.obtain();
                msg.what = MSG_GET_COLUMN_DECORATE;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mShouldHide = false;
                            if (getContent == GetContent.Refresh) {
                                if (refreshLayout.getState() == RefreshState.Refreshing) {
                                    refreshLayout.finishRefresh();
                                }
                            } else if (getContent == GetContent.Normal) {
                                mSkeletonScreen.hide();
                                networkError.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
    }

    public void getContent(final GetContent getContent) {
//        if (getContent == GetContent.Normal) {
//            mSkeletonScreen =
//                    Skeleton.bind(recyclerView)
//                            .adapter(mAdapter)
//                            .shimmer(false)
//                            .angle(20)
//                            .frozen(false)
//                            .duration(3000)
//                            .count(4)
//                            .load(R.layout.item_skeleton_content)
//                            .show();
//        }
        String url = ServerInfo.serviceIP + ServerInfo.getColumnContent + mColumn.getId();
        Map<String, String> params = new HashMap<String, String>();
        params.put("limit", "" + Constant.PAGE_SIZE);
        params.put("sorce_type", "0");
        if (getContent == GetContent.Refresh) {
            params.put("operation", "up");
            params.put("publish_at", mColumnContents.size() > 0 ? mColumnContents.get(0).getPublish_at() : "");
        } else if (getContent == GetContent.LoadMore) {
            params.put("operation", "down");
            params.put("publish_at", mColumnContents.size() > 0 ? mColumnContents.get(mColumnContents.size() - 1).getPublish_at() : "");
        }
        //params.put("mobile_source", "app");
        params.put("order_by", mOrderBy);
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        try{
//                            if (getContent == GetContent.Refresh) {
//                                if (refreshLayout.getState() == RefreshState.Refreshing) {
//                                    refreshLayout.finishRefresh();
//                                }
//                            } else if (getContent == GetContent.LoadMore) {
//                                if (refreshLayout.getState() == RefreshState.Loading) {
//                                    refreshLayout.finishLoadMore();
//                                }
//                            }
//                        }catch (Exception e){
//
//                        }
//
//                    }
//                });
                Message msg = Message.obtain();
                msg.what = MSG_UPDATE_LIST;
                msg.arg1 = getContent.ordinal();
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (getContent == GetContent.Refresh) {
                                if (refreshLayout.getState() == RefreshState.Refreshing) {
                                    refreshLayout.finishRefresh();
                                }
                            } else if (getContent == GetContent.LoadMore) {
                                if (refreshLayout.getState() == RefreshState.Loading) {
                                    refreshLayout.finishLoadMore();
                                }
                            } else {
                                if (mInterfaceNumber.decrementAndGet() == 0) {
                                    if (mShouldHide) {
                                        mSkeletonScreen.hide();
                                    }
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
    }

    public void getDecorateContent(final int animated, final int orderBy, final int type, String columnId, String contentNumber, final int position) {
        String url = ServerInfo.serviceIP + ServerInfo.indexDecorateContent;
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", columnId);
        params.put("type", "" + type);
        params.put("limit", contentNumber);
        params.put("sorce_type", "0");
        //params.put("city_code", "" + MainActivity.sCurrentCity.getCode());
        params.put("order_by", "" + orderBy);
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                if (mInterfaceNumber.decrementAndGet() == 0) {
                    if (mShouldHide) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mSkeletonScreen.hide();
                            }
                        });
                    }
                }
                Message msg = Message.obtain();
                msg.what = MSG_GET_TYPE_CONTENT;
                Bundle bundle = new Bundle();
                bundle.putInt("type", type);
                bundle.putInt("position", position);
                bundle.putInt("animated", animated);
                msg.setData(bundle);
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                if (mInterfaceNumber.decrementAndGet() == 0) {
                    if (mShouldHide) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mSkeletonScreen.hide();
                            }
                        });
                    }
                }
            }
        });
    }

    @OnClick(R.id.refresh)
    public void onViewClicked() {
        columnDecorateContent(GetContent.Normal);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_LIST:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        List<ColumnContent> columnContents = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class);
                        Iterator<ColumnContent> iterator = columnContents.iterator();
                        while (iterator.hasNext()) {
                            ColumnContent columnContent = iterator.next();
                            if (columnContent.getType() != 1 &&
                                    columnContent.getType() != 2 &&
                                    columnContent.getType() != 3 &&
                                    columnContent.getType() != 4 &&
                                    columnContent.getType() != 5 &&
                                    columnContent.getType() != 7 &&
                                    columnContent.getType() != 9 &&
                                    columnContent.getType() != 10) {
                                iterator.remove();
                            }
                        }
                        if (msg.arg1 == GetContent.Refresh.ordinal()) {
                            mColumnContents.addAll(0, columnContents);
                            if (refreshLayout.getState() == RefreshState.Refreshing) {
                                refreshLayout.finishRefresh();
                            }
                        } else if (msg.arg1 == GetContent.LoadMore.ordinal()) {
                            mColumnContents.addAll(columnContents);
                            if (refreshLayout.getState() == RefreshState.Loading) {
                                if (columnContents == null || columnContents.size() == 0) {
                                    //refreshLayout.setEnableLoadMore(false);
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                } else {
                                    refreshLayout.finishLoadMore();
                                }
                            }
                        } else {
                            mColumnContents = columnContents;
                            if (mInterfaceNumber.decrementAndGet() == 0) {
                                if (mShouldHide) {
                                    mSkeletonScreen.hide();
                                }
                            }
                        }
                        if (mColumnContents.size() == 0 && hasDecorate == false) {
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            noData.setVisibility(View.GONE);
                        }
                        //加入缓存 前10条数据
//                        if (mColumnContents.size()>0&&mColumnContents.size() <= 10) {
//                            mACache.put(ServerInfo.serviceIP + ServerInfo.getColumnContent + mColumn.getId(), JSON.toJSONString(mColumnContents));
//                        } else if(mColumnContents.size() > 10) {
//                            List<ColumnContent> arr = mColumnContents.subList(0, 10);
//                            mACache.put(ServerInfo.serviceIP + ServerInfo.getColumnContent + mColumn.getId(), JSON.toJSONString(arr));
//                        }
                        if (mAdapter == null) {
                            mAdapter = new ColumnDecorateContentAdapter(getContext(), mColumnContents);
                            mAdapter.setOnItemClickListener(new ColumnDecorateContentAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, ColumnContent content) {
                                }

                                @Override
                                public void onItemClick(View view, int pos) {
                                }
                            });
                            //设置添加或删除item时的动画，这里使用默认动画
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            //设置适配器
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.setData(mColumnContents);
                            mAdapter.notifyDataSetChanged();
                            if (msg.arg1 == GetContent.Refresh.ordinal()) {
                                recyclerView.smoothScrollToPosition(0);
                            }
                        }
                    } else {
                        if (msg.arg1 == GetContent.Refresh.ordinal()) {
                            if (refreshLayout.getState() == RefreshState.Refreshing) {
                                refreshLayout.finishRefresh();
                            }
                        } else if (msg.arg1 == GetContent.LoadMore.ordinal()) {
                            if (refreshLayout.getState() == RefreshState.Loading) {
                                refreshLayout.finishLoadMore();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (mInterfaceNumber.decrementAndGet() == 0) {
                        if (mShouldHide) {
                            mSkeletonScreen.hide();
                        }
                    }
                }
                break;
            case MSG_GET_COLUMN_DECORATE:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        JSONArray modules = jsonObject.getJSONObject("data").getJSONArray("modules");
                        addInfoStream = true;
                        mInterfaceNumber = new AtomicInteger(1);
                        if (modules != null) {
                            List<DecorModule> decorModules = JSONObject.parseArray(modules.toJSONString(), DecorModule.class);
                            Iterator<DecorModule> iterator = decorModules.iterator();
                            while (iterator.hasNext()) {
                                DecorModule decorModule = iterator.next();
                                if ((decorModule.getType() == 1 && decorModule.getStatus() == 0) || (decorModule.getType() == 2 && decorModule.getStatus() == 0)) {
                                    iterator.remove();
                                }
                            }
                            if (decorModules.size() > 0) {
                                hasDecorate = true;
                                mContentRecyclerView.clear();
                                mDecorateLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.content_decorate_layout, recyclerView, false);
                                for (int i = 0; i < decorModules.size(); i++) {
                                    final DecorModule module = decorModules.get(i);
                                    //模块类型1轮播图，2金刚区，3主播，4生活服务，5机构，6资讯
                                    if (module.getType() == 1) {
                                        //轮播图
                                        BannerView bannerView = new BannerView(getContext());
                                        int width = CommonUtils.getScreenWidth() - CommonUtils.dip2px(40);
                                        int height = 10 * width / 23;
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
                                        params.leftMargin = CommonUtils.dip2px(20);
                                        params.rightMargin = CommonUtils.dip2px(20);
                                        params.bottomMargin = CommonUtils.dip2px(10);
                                        params.topMargin = CommonUtils.dip2px(10);
                                        mDecorateLayout.addView(bannerView, params);
                                        List<BannerView.Banner> banners = new ArrayList<>();
                                        for (int j = 0; module.getContents() != null && j < module.getContents().size(); j++) {
                                            BannerInfo banner = new BannerInfo();
                                            banner.setLogo(module.getContents().get(j).getCover());
                                            banner.setUrl(module.getContents().get(j).getSource_url());
                                            banners.add(banner);
                                        }
                                        bannerView.setData(banners);
                                        bannerView.setOnItemClickListener(new BannerView.OnItemClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                BannerInfo banner = (BannerInfo) view.getTag();
                                                String url = banner.getUrl();
                                                String title = banner.getTitle();
                                                CommonUtils.jumpTo(getActivity(),url, title);
                                            }
                                        });
                                    } else if (module.getType() == 2) {
                                        //金刚区
                                        final int cols = module.getCols();
                                        final int flip = module.getPage_animated();
                                        if (flip == 1) {
                                            //翻页
                                            final ViewPager moduleViewPager = new ViewPager(getContext());
                                            moduleViewPager.setBackgroundResource(R.color.white);
                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, CommonUtils.dip2px(100));
                                            mDecorateLayout.addView(moduleViewPager, params);
                                            PagerAdapter modulePageAdapter = new PagerAdapter() {
                                                @Override
                                                public int getItemPosition(Object object) {
                                                    return POSITION_NONE;
                                                }

                                                @Override
                                                public void destroyItem(ViewGroup container, int position, Object object) {
                                                    container.removeView(container.findViewWithTag(position));
                                                }

                                                @Override
                                                public Object instantiateItem(ViewGroup container, int position) {
                                                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                                                    final View v = inflater.inflate(R.layout.index_module_item, moduleViewPager, false);
                                                    MyGridView gv = v.findViewById(R.id.gridView);
                                                    gv.setNumColumns(cols);
                                                    List<DecorModule.ContentsBean> contents = new ArrayList<>();
                                                    for (int i = position * cols * 2; i < (position + 1) * cols * 2 && i < module.getContents().size(); i++) {
                                                        contents.add(module.getContents().get(i));
                                                    }
                                                    final MoudleGridViewAdapter adapter = new MoudleGridViewAdapter(getActivity(), contents);
                                                    gv.setAdapter(adapter);
                                                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                            DecorModule.ContentsBean cb = adapter.getItem(position);
                                                            String url = cb.getSource_url();
                                                            String title = cb.getTitle();
                                                            CommonUtils.jumpTo(getActivity(),url, title);
                                                        }
                                                    });
                                                    v.setTag(position);
                                                    container.addView(v);
                                                    if (position == 0) {
                                                        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                                            @Override
                                                            public void onGlobalLayout() {
                                                                int height = v.getHeight();
                                                                ViewGroup.LayoutParams lp = moduleViewPager.getLayoutParams();
                                                                lp.height = height;
                                                                moduleViewPager.setLayoutParams(lp);
                                                            }
                                                        });
                                                    }
                                                    return v;
                                                }

                                                @Override
                                                public boolean isViewFromObject(View arg0, Object arg1) {
                                                    return arg0 == arg1;
                                                }

                                                @Override
                                                public int getCount() {
                                                    return (module.getContents().size() + cols * 2 - 1) / (cols * 2);
                                                }
                                            };
                                            moduleViewPager.setAdapter(modulePageAdapter);
                                            ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
                                                @Override
                                                public void onPageScrolled(int i, float v, int i1) {
                                                }

                                                @Override
                                                public void onPageSelected(int i) {
                                                }

                                                @Override
                                                public void onPageScrollStateChanged(int i) {
                                                }
                                            };
                                            moduleViewPager.addOnPageChangeListener(pageChangeListener);
                                        } else {
                                            //连续
                                            LayoutInflater inflater = LayoutInflater.from(getContext());
                                            View root = inflater.inflate(R.layout.index_module_layout, mDecorateLayout, false);
                                            LinearLayout moduleLayout = root.findViewById(R.id.moduleLayout);
                                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            mDecorateLayout.addView(root, lp);
                                            for (int j = 0; j < module.getContents().size(); j++) {
                                                final DecorModule.ContentsBean content = module.getContents().get(j);
                                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonUtils.getScreenWidth() / cols, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                params.gravity = Gravity.CENTER;
                                                params.topMargin = CommonUtils.dip2px(20);
                                                params.bottomMargin = CommonUtils.dip2px(20);
                                                View anchorView = LayoutInflater.from(getContext()).inflate(R.layout.anchor_gridview_item, moduleLayout, false);
                                                TextView anchorName = anchorView.findViewById(R.id.anchorName);
                                                SimpleDraweeView anchor = anchorView.findViewById(R.id.anchor);
                                                if (!TextUtils.isEmpty(content.getCover())) {
                                                    Uri uri = Uri.parse(content.getCover());
                                                    int width = CommonUtils.dip2px(45);
                                                    int height = width;
                                                    ImageLoader.showThumb(uri, anchor, width, height);
                                                }
                                                anchorName.setText(content.getTitle());
                                                anchorView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        String url = content.getSource_url();
                                                        String title = content.getTitle();
                                                        CommonUtils.jumpTo(getActivity(),url, title);
                                                    }
                                                });
                                                moduleLayout.addView(anchorView, j, params);
                                            }
                                        }
                                    } else if (module.getType() == 15) {
                                        addInfoStream = false;
                                        //视频
                                        LayoutInflater inflater = LayoutInflater.from(getContext());
                                        View clolumnLayout = inflater.inflate(R.layout.index_column_item, mDecorateLayout, false);
                                        SimpleDraweeView logo = clolumnLayout.findViewById(R.id.logo);
                                        TextView column = clolumnLayout.findViewById(R.id.column);
                                        TextView more = clolumnLayout.findViewById(R.id.more);
                                        ImageView divider = clolumnLayout.findViewById(R.id.divider);
                                        more.setVisibility(View.GONE);
                                        column.setText(module.getTitle());
                                        Station station = MyApplication.getInstance().getStation();
                                        if (station != null && !TextUtils.isEmpty(station.getIcon3())) {
                                            Uri uri = Uri.parse(station.getIcon3());
                                            int width = CommonUtils.dip2px(9);
                                            int height = width * 2;
                                            ImageLoader.showThumb(uri, logo, width, height);
                                        } else {
                                            logo.setVisibility(View.GONE);
                                        }
                                        String[] ids = module.getData_source_id().split(",");
                                        final String columnId = ids[ids.length - 1];
                                        more.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                            }
                                        });
                                        RecyclerView recyclerView = clolumnLayout.findViewById(R.id.recyclerView);
                                        mContentRecyclerView.add(recyclerView);
                                        if (mDecorateLayout.getChildCount() == 0) {
                                            divider.setVisibility(View.GONE);
                                        }
                                        mDecorateLayout.addView(clolumnLayout);
                                        mInterfaceNumber.incrementAndGet();
                                        getDecorateContent(module.getAnimated(), module.getOrder_by(), 4, columnId, "" + module.getContent_number(), mContentRecyclerView.size() - 1);
                                    } else if (module.getType() == 16) {
                                        //音频
                                        addInfoStream = false;
                                        LayoutInflater inflater = LayoutInflater.from(getContext());
                                        View clolumnLayout = inflater.inflate(R.layout.index_column_item, mDecorateLayout, false);
                                        SimpleDraweeView logo = clolumnLayout.findViewById(R.id.logo);
                                        TextView column = clolumnLayout.findViewById(R.id.column);
                                        TextView more = clolumnLayout.findViewById(R.id.more);
                                        ImageView divider = clolumnLayout.findViewById(R.id.divider);
                                        more.setVisibility(View.GONE);
                                        column.setText(module.getTitle());
                                        Station station = MyApplication.getInstance().getStation();
                                        if (station != null && !TextUtils.isEmpty(station.getIcon3())) {
                                            Uri uri = Uri.parse(station.getIcon3());
                                            int width = CommonUtils.dip2px(9);
                                            int height = width * 2;
                                            ImageLoader.showThumb(uri, logo, width, height);
                                        } else {
                                            logo.setVisibility(View.GONE);
                                        }
                                        String[] ids = module.getData_source_id().split(",");
                                        final String columnId = ids[ids.length - 1];
                                        more.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                            }
                                        });
                                        RecyclerView recyclerView = clolumnLayout.findViewById(R.id.recyclerView);
                                        mContentRecyclerView.add(recyclerView);
                                        if (mDecorateLayout.getChildCount() == 0) {
                                            divider.setVisibility(View.GONE);
                                        }
                                        mDecorateLayout.addView(clolumnLayout);
                                        mInterfaceNumber.incrementAndGet();
                                        getDecorateContent(module.getAnimated(), module.getOrder_by(), 2, columnId, "" + module.getContent_number(), mContentRecyclerView.size() - 1);
                                    } else if (module.getType() == 17) {
                                        //文章
                                        addInfoStream = false;
                                        LayoutInflater inflater = LayoutInflater.from(getContext());
                                        View clolumnLayout = inflater.inflate(R.layout.index_column_item, mDecorateLayout, false);
                                        SimpleDraweeView logo = clolumnLayout.findViewById(R.id.logo);
                                        TextView column = clolumnLayout.findViewById(R.id.column);
                                        TextView more = clolumnLayout.findViewById(R.id.more);
                                        ImageView divider = clolumnLayout.findViewById(R.id.divider);
                                        more.setVisibility(View.GONE);
                                        column.setText(module.getTitle());
                                        Station station = MyApplication.getInstance().getStation();
                                        if (station != null && !TextUtils.isEmpty(station.getIcon3())) {
                                            Uri uri = Uri.parse(station.getIcon3());
                                            int width = CommonUtils.dip2px(9);
                                            int height = width * 2;
                                            ImageLoader.showThumb(uri, logo, width, height);
                                        } else {
                                            logo.setVisibility(View.GONE);
                                        }
                                        String[] ids = module.getData_source_id().split(",");
                                        final String columnId = ids[ids.length - 1];
                                        more.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                            }
                                        });
                                        RecyclerView recyclerView = clolumnLayout.findViewById(R.id.recyclerView);
                                        mContentRecyclerView.add(recyclerView);
                                        if (mDecorateLayout.getChildCount() == 0) {
                                            divider.setVisibility(View.GONE);
                                        }
                                        mDecorateLayout.addView(clolumnLayout);
                                        mInterfaceNumber.incrementAndGet();
                                        getDecorateContent(module.getAnimated(), module.getOrder_by(), 3, columnId, "" + module.getContent_number(), mContentRecyclerView.size() - 1);
                                    } else if (module.getType() == 18) {
                                        //图集
                                        addInfoStream = false;
                                        LayoutInflater inflater = LayoutInflater.from(getContext());
                                        View clolumnLayout = inflater.inflate(R.layout.index_column_item, mDecorateLayout, false);
                                        SimpleDraweeView logo = clolumnLayout.findViewById(R.id.logo);
                                        TextView column = clolumnLayout.findViewById(R.id.column);
                                        TextView more = clolumnLayout.findViewById(R.id.more);
                                        ImageView divider = clolumnLayout.findViewById(R.id.divider);
                                        more.setVisibility(View.GONE);
                                        column.setText(module.getTitle());
                                        Station station = MyApplication.getInstance().getStation();
                                        if (station != null && !TextUtils.isEmpty(station.getIcon3())) {
                                            Uri uri = Uri.parse(station.getIcon3());
                                            int width = CommonUtils.dip2px(9);
                                            int height = width * 2;
                                            ImageLoader.showThumb(uri, logo, width, height);
                                        } else {
                                            logo.setVisibility(View.GONE);
                                        }
                                        String[] ids = module.getData_source_id().split(",");
                                        final String columnId = ids[ids.length - 1];
                                        more.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                            }
                                        });
                                        RecyclerView recyclerView = clolumnLayout.findViewById(R.id.recyclerView);
                                        mContentRecyclerView.add(recyclerView);
                                        if (mDecorateLayout.getChildCount() == 0) {
                                            divider.setVisibility(View.GONE);
                                        }
                                        mDecorateLayout.addView(clolumnLayout);
                                        mInterfaceNumber.incrementAndGet();
                                        getDecorateContent(module.getAnimated(), module.getOrder_by(), 7, columnId, "" + module.getContent_number(), mContentRecyclerView.size() - 1);
                                    }
                                }
                                mAdapter.addDecorateLayout(mDecorateLayout);
                            }
                        }
                        if (addInfoStream == true) {
                            getContent(GetContent.Normal);
                            refreshLayout.setEnableLoadMore(true);
                            refreshLayout.setOnRefreshListener(new OnRefreshListener() {
                                @Override
                                public void onRefresh(RefreshLayout refreshlayout) {
                                    //getContent(GetContent.Refresh);
                                    getContent(GetContent.Refresh);
                                }
                            });
                        } else {
                            if (mInterfaceNumber.decrementAndGet() == 0) {
                                if (mShouldHide) {
                                    mSkeletonScreen.hide();
                                }
                            }
                            refreshLayout.setEnableLoadMore(false);
                            refreshLayout.setOnRefreshListener(new OnRefreshListener() {
                                @Override
                                public void onRefresh(RefreshLayout refreshlayout) {
                                    //getContent(GetContent.Refresh);
                                    columnDecorateContent(GetContent.Refresh);
                                }
                            });
                        }
                    } else {
                        if (mShouldHide) {
                            mSkeletonScreen.hide();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_GET_TYPE_CONTENT: {
                try {
                    String result = (String) msg.obj;
                    JSONObject jo = JSONObject.parseObject(result);
                    int position = msg.getData().getInt("position");
                    int animated = msg.getData().getInt("animated");
                    int type = msg.getData().getInt("type");
                    if (type == 4) {
                        if (jo.getIntValue("code") == 100000 && jo.getJSONArray("data") != null) {
                            List<ColumnContent> columnContents = JSONObject.parseArray(jo.getJSONArray("data").toJSONString(), ColumnContent.class);
                            if (animated == 3) {
                                RecyclerView recyclerView = mContentRecyclerView.get(position);
                                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                                lp.leftMargin = CommonUtils.dip2px(5);
                                lp.rightMargin = CommonUtils.dip2px(5);
                                recyclerView.setLayoutParams(lp);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                                divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
                                recyclerView.addItemDecoration(divider);
                                ColumnDecorateVideoContentAdapter adapter = new ColumnDecorateVideoContentAdapter(getContext(), recyclerView, columnContents);
                                recyclerView.setAdapter(adapter);
                            } else if (animated == 4) {
                                RecyclerView recyclerView = mContentRecyclerView.get(position);
                                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                                lp.leftMargin = CommonUtils.dip2px(5);
                                lp.rightMargin = CommonUtils.dip2px(5);
                                recyclerView.setLayoutParams(lp);
                                GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
                                recyclerView.setLayoutManager(manager);
                                DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                                divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
                                recyclerView.addItemDecoration(divider);
                                final ColumnDecorateVideoContentAdapter adapter = new ColumnDecorateVideoContentAdapter(getContext(), recyclerView, columnContents);
                                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        int size = adapter.getItemCount();
                                        if ((position + 1) % 2 == 0) {
                                            return 1;
                                        } else if ((position + 1) == size) {
                                            return 2;
                                        } else {
                                            return 1;
                                        }
                                    }
                                });
                                recyclerView.setAdapter(adapter);
                            } else if (animated == 7) {
                                //1+4
                                RecyclerView recyclerView = mContentRecyclerView.get(position);
                                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                                lp.leftMargin = CommonUtils.dip2px(5);
                                lp.rightMargin = CommonUtils.dip2px(5);
                                recyclerView.setLayoutParams(lp);
                                GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
                                recyclerView.setLayoutManager(manager);
                                DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                                divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
                                recyclerView.addItemDecoration(divider);
                                final ColumnDecorateVideoContentAdapter adapter = new ColumnDecorateVideoContentAdapter(getContext(), recyclerView, columnContents);
                                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        int size = adapter.getItemCount();
                                        int page = size / 5;
                                        if (page > 0 && (position + 1) <= 5 * page) {
                                            if ((position + 1) % 5 == 1) {
                                                return 2;
                                            } else {
                                                return 1;
                                            }
                                        } else {
                                            if (size % 5 == 1) {
                                                if ((position + 1) % 5 == 1) {
                                                    return 2;
                                                }
                                            } else if (size % 5 == 2) {
                                                if ((position + 1) % 5 == 1) {
                                                    return 2;
                                                } else if ((position + 1) % 5 == 2) {
                                                    return 2;
                                                }
                                            } else if (size % 5 == 3) {
                                                if ((position + 1) % 5 == 1) {
                                                    return 2;
                                                } else if ((position + 1) % 5 == 2) {
                                                    return 1;
                                                } else if ((position + 1) % 5 == 3) {
                                                    return 1;
                                                }
                                            } else if (size % 5 == 4) {
                                                if ((position + 1) % 5 == 1) {
                                                    return 2;
                                                } else if ((position + 1) % 5 == 2) {
                                                    return 1;
                                                } else if ((position + 1) % 5 == 3) {
                                                    return 1;
                                                } else if ((position + 1) % 5 == 4) {
                                                    return 2;
                                                }
                                            }
                                            return 2;
                                        }
                                    }
                                });
                                recyclerView.setAdapter(adapter);
                            } else if (animated == 8) {
                                RecyclerView recyclerView = mContentRecyclerView.get(position);
                                LayoutInflater inflater = LayoutInflater.from(getContext());
                                View anchorLayout = inflater.inflate(R.layout.index_scrollview_column_layout, null, false);
                                LinearLayout videoLayout = anchorLayout.findViewById(R.id.contentLayout);
                                for (int i = 0; i < columnContents.size(); i++) {
                                    final ColumnContent content = columnContents.get(i);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((CommonUtils.getScreenWidth() - CommonUtils.dip2px(10)) * 2 / 5, LinearLayout.LayoutParams.WRAP_CONTENT);
//                                int margin = CommonUtils.dip2px(10);
//                                params.setMargins(margin, margin, margin, margin);
                                    View anchorView = LayoutInflater.from(getContext()).inflate(R.layout.scrollview_video_item_layout, videoLayout, false);
                                    TextView videoTitle = anchorView.findViewById(R.id.title);
                                    SimpleDraweeView logo = anchorView.findViewById(R.id.logo);
                                    TextView duration = anchorView.findViewById(R.id.duration);
                                    videoTitle.setText(content.getTitle());
                                    if (content.getVideo() != null && !TextUtils.isEmpty(content.getVideo().getDuration())) {
                                        duration.setText(CommonUtils.getShowTime((long) Float.parseFloat(content.getVideo().getDuration()) * 1000));
                                    } else {
                                        duration.setText("00:00");
                                    }
                                    if (!TextUtils.isEmpty(content.getCover())) {
                                        Uri uri = Uri.parse(content.getCover());
                                        int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(10)) * 2 / 5;
                                        int height = (int) (2f * width / 3f);
                                        ImageLoader.showThumb(uri, logo, width, height);
                                    }
                                    anchorView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent it = new Intent(getContext(), VideoDetailActivity.class);
                                            it.putExtra("videoId", content.getId());
                                            startActivity(it);
                                        }
                                    });
                                    videoLayout.addView(anchorView, params);
                                }
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                                divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
                                recyclerView.addItemDecoration(divider);
                                ColumnContentAdapter adapter = new ColumnContentAdapter(getContext(), recyclerView);
                                adapter.addHeaderView(anchorLayout);
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    } else if (type == 3) {
                        //文章
                        if (jo.getIntValue("code") == 100000 && jo.getJSONArray("data") != null) {
                            List<ColumnContent> columnContents = JSONObject.parseArray(jo.getJSONArray("data").toJSONString(), ColumnContent.class);
                            RecyclerView recyclerView = mContentRecyclerView.get(position);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                            divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
                            recyclerView.addItemDecoration(divider);
                            ColumnContentAdapter adapter = new ColumnContentAdapter(getContext(), recyclerView, columnContents);
                            recyclerView.setAdapter(adapter);
                        }
                    } else if (type == 7) {
                        //图集
                        if (jo.getIntValue("code") == 100000 && jo.getJSONArray("data") != null) {
                            List<ColumnContent> columnContents = JSONObject.parseArray(jo.getJSONArray("data").toJSONString(), ColumnContent.class);
                            RecyclerView recyclerView = mContentRecyclerView.get(position);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                            divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
                            recyclerView.addItemDecoration(divider);
                            ColumnContentAdapter adapter = new ColumnContentAdapter(getContext(), recyclerView, columnContents);
                            recyclerView.setAdapter(adapter);
                        }
                    } else if (type == 2) {
                        //音频
                        if (jo.getIntValue("code") == 100000 && jo.getJSONArray("data") != null) {
                            List<ColumnContent> columnContents = JSONObject.parseArray(jo.getJSONArray("data").toJSONString(), ColumnContent.class);
                            if (animated == 8) {
                                //单行
                                RecyclerView recyclerView = mContentRecyclerView.get(position);
                                LayoutInflater inflater = LayoutInflater.from(getContext());
                                View anchorLayout = inflater.inflate(R.layout.index_scrollview_column_layout, null, false);
                                LinearLayout albumLayout = anchorLayout.findViewById(R.id.contentLayout);
                                for (int i = 0; i < columnContents.size(); i++) {
                                    final ColumnContent content = columnContents.get(i);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((CommonUtils.getScreenWidth() - CommonUtils.dip2px(10)) * 2 / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
//                                int margin = CommonUtils.dip2px(10);
//                                params.setMargins(margin, margin, margin, margin);
                                    View anchorView = LayoutInflater.from(getContext()).inflate(R.layout.scrollview_album_item_layout, albumLayout, false);
                                    TextView videoTitle = anchorView.findViewById(R.id.title);
                                    SimpleDraweeView logo = anchorView.findViewById(R.id.logo);
                                    videoTitle.setText(content.getTitle());
                                    if (!TextUtils.isEmpty(content.getCover())) {
                                        Uri uri = Uri.parse(content.getCover());
                                        int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(10)) * 2 / 7;
                                        int height = width;
                                        ImageLoader.showThumb(uri, logo, width, height);
                                    }
                                    anchorView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent it = new Intent(getContext(), AlbumDetailActivity.class);
                                            it.putExtra("albumId", content.getId());
                                            startActivity(it);
                                        }
                                    });
                                    albumLayout.addView(anchorView, params);
                                }
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                                divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
                                recyclerView.addItemDecoration(divider);
                                ColumnContentAdapter adapter = new ColumnContentAdapter(getContext(), recyclerView);
                                adapter.addHeaderView(anchorLayout);
                                recyclerView.setAdapter(adapter);
                            } else if (animated == 5) {
                                //三图
                                RecyclerView recyclerView = mContentRecyclerView.get(position);
                                GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
                                recyclerView.setLayoutManager(manager);
                                DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                                divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
                                recyclerView.addItemDecoration(divider);
                                final ColumnAlbumContentAdapter adapter = new ColumnAlbumContentAdapter(getContext(), columnContents);
                                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        int size = adapter.getItemCount();
                                        int page = size / 3;
                                        if (page > 0 && (position + 1) <= 3 * page) {
                                            return 1;
                                        } else {
                                            return 3;
                                        }
                                    }
                                });
                                adapter.setMode(1);
                                recyclerView.setAdapter(adapter);
                            } else if (animated == 9) {
                                //三图+5图
                                RecyclerView recyclerView = mContentRecyclerView.get(position);
                                GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
                                recyclerView.setLayoutManager(manager);
                                DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                                divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
                                recyclerView.addItemDecoration(divider);
                                final ColumnAlbumContentAdapter adapter = new ColumnAlbumContentAdapter(getContext(), columnContents);
                                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        int size = adapter.getItemCount();
                                        int page = size / 8;
                                        if (page > 0 && (position + 1) <= 8 * page) {
                                            if ((position + 1) % 8 > 0 && (position + 1) % 8 <= 3) {
                                                return 1;
                                            } else {
                                                return 3;
                                            }
                                        } else {
                                            if (size % 8 == 1 || size % 8 == 2) {
                                                return 3;
                                            } else if (size % 8 == 3) {
                                                return 1;
                                            } else {
                                                if ((position + 1) % 8 <= 3 && (position + 1) % 8 > 0) {
                                                    return 1;
                                                } else {
                                                    return 3;
                                                }
                                            }
                                        }
                                    }
                                });
                                adapter.setMode(2);
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
            break;
            default:
                break;
        }
        return false;
    }

    public void jumpTo(String url, String title) {
        if (url.startsWith(ServerInfo.h5IP + "/tv") || url.startsWith(ServerInfo.h5HttpsIP + "/tv")) {
            Intent it = new Intent(getContext(), RadioListActivity.class);
            it.putExtra("type", "2");
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/lives/") || url.startsWith(ServerInfo.h5HttpsIP + "/lives/")) {
            String radioId = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Intent it = new Intent(getContext(), TvDetailActivity.class);
            it.putExtra("radioId", Integer.parseInt(radioId));
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/radios/") || url.startsWith(ServerInfo.h5HttpsIP + "/radios/")) {
            String radioId = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Intent it = new Intent(getContext(), RadioDetailActivity.class);
            it.putExtra("radioId", Integer.parseInt(radioId));
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/radios") || url.startsWith(ServerInfo.h5HttpsIP + "/radios")) {
            Intent it = new Intent(getContext(), RadioListActivity.class);
            it.putExtra("type", "1");
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/gover") || url.startsWith(ServerInfo.h5HttpsIP + "/gover")) {
            Intent it = new Intent(getContext(), WebViewBackActivity.class);
            it.putExtra("title", title);
            it.putExtra("url", url);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/dj") || url.startsWith(ServerInfo.h5HttpsIP + "/dj")) {
            Intent it = new Intent(getContext(), WebViewBackActivity.class);
            it.putExtra("title", title);
            it.putExtra("url", url);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/interact") || url.startsWith(ServerInfo.h5HttpsIP + "/interact")) {
            Intent it = new Intent(getContext(), WebViewBackActivity.class);
            it.putExtra("title", title);
            it.putExtra("url", url);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/guide") || url.startsWith(ServerInfo.h5HttpsIP + "/guide")) {
            Intent it = new Intent(getContext(), WebViewBackActivity.class);
            it.putExtra("title", title);
            it.putExtra("url", url);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/cates/") || url.startsWith(ServerInfo.h5HttpsIP + "/cates/")) {
            //跳转栏目
            String columnid = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Column column = new Column();
            column.setId(Integer.parseInt(columnid));
            ((MainActivity) getActivity()).switchRecommend(column);
        } else if (url.startsWith(ServerInfo.h5IP + "/specials") || url.startsWith(ServerInfo.h5HttpsIP + "/specials")) {
            //跳转热门
            Column column = null;
            List<Column> allColumns = JSONObject.parseArray(SharedPreferencesUtils.getStringValue("all_column", null), Column.class);
            for (int i = 0; allColumns != null && i < allColumns.size(); i++) {
                if (allColumns.get(i).getType() == 1) {
                    column = allColumns.get(i);
                    break;
                }
            }
            if (column != null) {
                ((MainActivity) getActivity()).switchRecommend(column);
            }
        } else if (url.startsWith(ServerInfo.h5IP + "/orgs/") || url.startsWith(ServerInfo.h5HttpsIP + "/orgs/")) {
            String organizationId = url.substring(url.lastIndexOf("/") + 1);
//            Intent it = new Intent(getContext(), OrganizationDetailActivity.class);
//            it.putExtra("organizationId", Integer.parseInt(organizationId));
//            startActivity(it);
            Intent it = new Intent(getContext(), WebViewBackActivity.class);
            it.putExtra("title", title);
            it.putExtra("url", ServerInfo.h5HttpsIP + "/orgs/" + organizationId);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/anchors/") || url.startsWith(ServerInfo.h5HttpsIP + "/anchors/")) {
            String anchorId = url.substring(url.lastIndexOf("/") + 1);
//            Intent it = new Intent(getContext(), AnchorDetailActivity.class);
//            it.putExtra("anchorId", Integer.parseInt(anchorId));
//            startActivity(it);
            Intent it = new Intent(getContext(), WebViewBackActivity.class);
            it.putExtra("url", ServerInfo.h5HttpsIP + "/anchors/" + anchorId);
            it.putExtra("title", title);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/atlas/") || url.startsWith(ServerInfo.h5HttpsIP + "/atlas/")) {
            String galleriaId = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Intent it = new Intent(getContext(), GalleriaActivity.class);
            it.putExtra("galleriaId", Integer.parseInt(galleriaId));
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/albums/") || url.startsWith(ServerInfo.h5HttpsIP + "/albums/")) {
            String albumId = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Intent it = new Intent(getContext(), AlbumDetailActivity.class);
            it.putExtra("albumId", Integer.parseInt(albumId));
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/audios/") || url.startsWith(ServerInfo.h5HttpsIP + "/audios/")) {
            String audioId = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Intent it = new Intent(getContext(), AudioDetailActivity.class);
            it.putExtra("audioId", Integer.parseInt(audioId));
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/posts/") || url.startsWith(ServerInfo.h5HttpsIP + "/posts/")) {
            String articleId = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Intent it = new Intent(getContext(), ArticleDetailActivity02.class);
            it.putExtra("articleId", Integer.parseInt(articleId));
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/specials/") || url.startsWith(ServerInfo.h5HttpsIP + "/specials/")) {
            String specialId = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Intent it = new Intent(getContext(), SpecialDetailActivity.class);
            it.putExtra("specialId", Integer.parseInt(specialId));
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/videos/") || url.startsWith(ServerInfo.h5HttpsIP + "/videos/")) {
            String videoId = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Intent it = new Intent(getContext(), VideoDetailActivity.class);
            it.putExtra("videoId", Integer.parseInt(videoId));
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/subcates/") || url.startsWith(ServerInfo.h5IP + "/subcates/")) {
            String columnid = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Column column = new Column();
            column.setId(Integer.parseInt(columnid));
            column.setName(url.substring(url.lastIndexOf("=") + 1));
            Intent it = new Intent(getContext(), ContentActivity.class);
            it.putExtra("column", column);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.scs + "/broke-create")) {
            if (User.getInstance() == null) {
                Intent it = new Intent(getContext(), NewLoginActivity.class);
                startActivity(it);
            } else if (User.getInstance() != null && TextUtils.isEmpty(User.getInstance().getPhone())) {
                Intent it = new Intent(getContext(), BindPhoneActivity.class);
                //it.putExtra("hasLogined",true);
                startActivity(it);
            } else {
                Intent it = new Intent(getContext(), CluesActivity.class);
                it.putExtra("url", ServerInfo.scs + "/broke-create");
                startActivity(it);
            }
        } else if (url.startsWith(ServerInfo.h5IP + "/invitation-post") || url.startsWith(ServerInfo.h5HttpsIP + "/invitation-post")) {
            Intent it = new Intent(getContext(), WebViewBackActivity.class);
            it.putExtra("url", url);
            it.putExtra("title", title);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/actrank") || url.startsWith(ServerInfo.h5HttpsIP + "/actrank")) {
            Intent it = new Intent(getContext(), WebViewBackActivity.class);
            it.putExtra("url", url);
            it.putExtra("title", title);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/wish") || url.startsWith(ServerInfo.h5HttpsIP + "/wish")) {
            Intent it = new Intent(getContext(), WebViewBackActivity.class);
            it.putExtra("url", url);
            it.putExtra("title", title);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/actlist") || url.startsWith(ServerInfo.h5HttpsIP + "/actlist")) {
            Intent it = new Intent(getContext(), WebViewBackActivity.class);
            it.putExtra("url", url);
            it.putExtra("title", title);
            startActivity(it);
        } else {
            Intent it = new Intent(getContext(), WebViewBackActivity.class);
            it.putExtra("url", url);
            it.putExtra("title", title);
            startActivity(it);
        }
    }
}
