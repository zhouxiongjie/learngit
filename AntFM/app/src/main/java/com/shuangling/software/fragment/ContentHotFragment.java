package com.shuangling.software.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.R;
import com.shuangling.software.adapter.ColumnContentAdapter;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ACache;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;


public class ContentHotFragment extends Fragment implements Handler.Callback {


    public static final int MSG_GET_TOP_POST = 0x1;

    public static final int MSG_GET_EXCELLENT_POST = 0x2;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;
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

    private int page = 1;


    private ACache mACache;
    private RecyclerViewSkeletonScreen mSkeletonScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Bundle args = getArguments();
        mACache = ACache.get(getContext());
        mColumn = (Column) args.getSerializable("Column");
        mHandler = new Handler(this);
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_content, null);
        unbinder = ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
        recyclerView.addItemDecoration(divider);
        refreshLayout.setPrimaryColorsId(R.color.white, android.R.color.black);
        ((ClassicsHeader) refreshLayout.getRefreshHeader()).setEnableLastTime(false);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;

                getTopPost(GetContent.Refresh);


            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page++;
                getExcellentPost(GetContent.LoadMore);


            }
        });
        mAdapter = new ColumnContentAdapter(getContext(), null);
        mAdapter.setIsHot(true);
        recyclerView.setAdapter(mAdapter);

        //热门

        String cache = mACache.getAsString(ServerInfo.serviceIP + ServerInfo.topPost);
        if (!TextUtils.isEmpty(cache)) {

            mColumnContents = JSONObject.parseArray(cache, ColumnContent.class);
            String str = mACache.getAsString(ServerInfo.serviceIP + ServerInfo.topPost + "/page");
            if (!TextUtils.isEmpty(str)) {
                page = Integer.parseInt(mACache.getAsString(ServerInfo.serviceIP + ServerInfo.topPost + "/page"));
            }

            if (mAdapter == null) {
                mAdapter = new ColumnContentAdapter(getContext(), mColumnContents);
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
            }

        } else {


            getTopPost(GetContent.Normal);
        }

        //getExcellentPost(GetContent.Normal);


        return view;
    }


    public void getTopPost(final GetContent getContent) {

        if (getContent == GetContent.Normal) {
            mSkeletonScreen =
                    Skeleton.bind(recyclerView)
                            .adapter(mAdapter)
                            .shimmer(false)
                            .angle(20)
                            .frozen(false)
                            .duration(1200)
                            .count(4)
                            .load(R.layout.item_skeleton_content)
                            .show();
        }

        String url = ServerInfo.serviceIP + ServerInfo.topPost;
        Map<String, String> params = new HashMap<>();

        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                Message msg = Message.obtain();
                msg.what = MSG_GET_TOP_POST;
                msg.arg1 = getContent.ordinal();
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, IOException exception) {

                if (getContent == GetContent.Normal || getContent == GetContent.Refresh) {
                    getExcellentPost(getContent);
                }

            }
        });

    }


    public void getExcellentPost(final GetContent getContent) {

        String url = ServerInfo.serviceIP + ServerInfo.marvellousPosts;
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "" + page);
        params.put("pageSize", "" + Constant.PAGE_SIZE);

        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

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
                msg.what = MSG_GET_EXCELLENT_POST;
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case MSG_GET_TOP_POST:
                try {

                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        mColumnContents = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class);

                    }

                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    getExcellentPost(GetContent.values()[msg.arg1]);
                }
                break;
            case MSG_GET_EXCELLENT_POST:
                try {

                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        List<ColumnContent> contents = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class);
                        mColumnContents.addAll(contents);


                        if (mColumnContents.size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            noData.setVisibility(View.GONE);
                        }
                        //加入缓存 前50条数据
                        if (mColumnContents.size()>0&&mColumnContents.size() <= 50) {
                            mACache.put(ServerInfo.serviceIP + ServerInfo.topPost, JSON.toJSONString(mColumnContents));
                            mACache.put(ServerInfo.serviceIP + ServerInfo.topPost + "/page", "" + page);
                        } else {
                            List<ColumnContent> arr = mColumnContents.subList(0, 50);
//                            mACache.put(ServerInfo.serviceIP + ServerInfo.topPost,JSON.toJSONString(arr));
//                            mACache.put(ServerInfo.serviceIP + ServerInfo.topPost+"/page",""+page);
                        }

//                        if(contents.size()<Constant.PAGE_SIZE){
//                            refreshLayout.setEnableLoadMore(false);
//                        }

                        if (mAdapter == null) {
                            mAdapter = new ColumnContentAdapter(getContext(), mColumnContents);
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
                            if (msg.arg1 == GetContent.Refresh.ordinal()) {
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
