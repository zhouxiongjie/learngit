package com.shuangling.software.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.ColumnContentAdapter;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.fragment.ContentFragment;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;


public class ContentActivity extends AppCompatActivity implements Handler.Callback {

    public static final String TAG = "ContentActivity";

    public static final int MSG_UPDATE_LIST = 0x1;

    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.noScriptText)
    TextView noScriptText;
    @BindView(R.id.noData)
    LinearLayout noData;

    private Column mColumn;
    private String mOrderBy = "1";
    private List<ColumnContent> mColumnContents;
    private ColumnContentAdapter mAdapter;
    private Handler mHandler;


    public enum GetContent {
        Refresh,
        LoadMore,
        Normal
    }

    ;

    private RecyclerViewSkeletonScreen mSkeletonScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(MyApplication.getInstance().getCurrentTheme());
        setContentView(R.layout.activity_content);
        ButterKnife.bind(this);
        init();

    }

    private void init() {
        mHandler = new Handler(this);
        mColumn = (Column) getIntent().getSerializableExtra("column");
        activityTitle.setTitleText(mColumn.getName());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.recycleview_divider_drawable));
        recyclerView.addItemDecoration(divider);
        refreshLayout.setPrimaryColorsId(R.color.white, android.R.color.black);
        ((ClassicsHeader) refreshLayout.getRefreshHeader()).setEnableLastTime(false);
        //refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getContent(GetContent.Refresh);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                getContent(GetContent.LoadMore);
            }
        });
        mAdapter = new ColumnContentAdapter(this, null);
        recyclerView.setAdapter(mAdapter);
        getContent(GetContent.Normal);
    }


    public void getContent(final GetContent getContent) {

        if (getContent == GetContent.Normal) {
            mSkeletonScreen =
                    Skeleton.bind(recyclerView)
                            .adapter(mAdapter)
                            .shimmer(false)
                            .angle(20)
                            .frozen(false)
                            .duration(1200)
                            .count(3)
                            .load(R.layout.item_skeleton_content)
                            .show();
        }


        String url = ServerInfo.serviceIP + ServerInfo.getColumnContent + mColumn.getId();
        Map<String, String> params = new HashMap<String, String>();
        params.put("limit", "" + Constant.PAGE_SIZE);
        params.put("sorceType", "0");

        if (getContent == GetContent.Refresh) {
            params.put("operation", "up");
            params.put("update_time", mColumnContents.size() > 0 ? mColumnContents.get(0).getUpdate_time() : "");
        } else if (getContent == GetContent.LoadMore) {
            params.put("operation", "down");
            params.put("update_time", mColumnContents.size() > 0 ? mColumnContents.get(mColumnContents.size() - 1).getUpdate_time() : "");
        }
        //params.put("mobile_source", "app");
        params.put("order_by", mOrderBy);


        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (getContent == GetContent.Refresh) {
                            if (refreshLayout.isRefreshing()) {
                                refreshLayout.finishRefresh();
                            }
                        } else if (getContent == GetContent.LoadMore) {
                            if (refreshLayout.isLoading()) {
                                refreshLayout.finishLoadMore();
                            }
                        } else {
                            mSkeletonScreen.hide();
                        }
                    }
                });


                Message msg = Message.obtain();
                msg.what = MSG_UPDATE_LIST;
                msg.arg1 = getContent.ordinal();
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, IOException exception) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (getContent == GetContent.Refresh) {
                            if (refreshLayout.isRefreshing()) {
                                refreshLayout.finishRefresh();
                            }
                        } else if (getContent == GetContent.LoadMore) {
                            if (refreshLayout.isLoading()) {
                                refreshLayout.finishLoadMore();
                            }
                        } else {
                            mSkeletonScreen.hide();
                        }
                    }
                });

            }
        });

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_LIST:
                String result = (String) msg.obj;
                try {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        if (msg.arg1 == ContentFragment.GetContent.Refresh.ordinal()) {
                            mColumnContents.addAll(0, JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class));
                        } else if (msg.arg1 == ContentFragment.GetContent.LoadMore.ordinal()) {
                            mColumnContents.addAll(JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class));
                        } else {
                            mColumnContents = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class);
                        }

                        if (mColumnContents.size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            noData.setVisibility(View.GONE);
                        }
                        if (mAdapter == null) {
                            mAdapter = new ColumnContentAdapter(this, mColumnContents);

                            mAdapter.setOnItemClickListener(new ColumnContentAdapter.OnItemClickListener() {


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
                            if (msg.arg1 == ContentFragment.GetContent.Refresh.ordinal()) {
                                recyclerView.smoothScrollToPosition(0);
                            }

                        }


                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }
                break;
            default:
                break;

        }
        return false;
    }
}
