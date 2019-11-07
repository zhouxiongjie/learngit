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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.R;
import com.shuangling.software.adapter.SearchListAdapter;
import com.shuangling.software.entity.SearchResult;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Response;


public class RadioProgramListFragment extends Fragment implements Handler.Callback {

    public static final int MSG_UPDATE_LIST = 0x1;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;

    private Handler mHandler;
    private String mKeyword;
    private int mSearchType;
    private int mCurrentPage = 1;

    private List<SearchResult> mSearchList=new ArrayList<>();
    private SearchListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mKeyword = args.getString("keyword");
        mSearchType = args.getInt("search_type");
        mHandler=new Handler(this);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        unbinder = ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //recyclerView.addItemDecoration(new MyItemDecoration());
        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycleview_divider_drawable));
        recyclerView.addItemDecoration(divider);
        refreshLayout.setPrimaryColorsId(R.color.white, android.R.color.black);
        ((ClassicsHeader) refreshLayout.getRefreshHeader()).setEnableLastTime(false);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getContent();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
            }
        });

        return view;

    }


    @Override
    public void onResume() {
        getContent();
        super.onResume();
    }

    public void getContent() {

        String url = ServerInfo.serviceIP + ServerInfo.search;
        Map<String, String> params = new HashMap<String, String>();
        params.put("search_type", "" + mSearchType);

        params.put("query", mKeyword);
        params.put("page", ""+mCurrentPage);


        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                if(refreshLayout.isRefreshing()){
                    refreshLayout.finishRefresh();
                }

                Message msg = Message.obtain();
                msg.what = MSG_UPDATE_LIST;
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {

                if(refreshLayout.isRefreshing()){
                    refreshLayout.finishRefresh();
                }
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
        switch (msg.what){
            case MSG_UPDATE_LIST:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        int pages=jsonObject.getJSONObject("data").getInteger("pages");
                        if(pages==mCurrentPage){
                            refreshLayout.setEnableLoadMore(false);
                        }else {
                            refreshLayout.setEnableLoadMore(true);
                        }

                        mSearchList.addAll(JSONArray.parseArray(jsonObject.getJSONObject("data").getJSONArray("list").toJSONString(), SearchResult.class));
                        if (mAdapter == null) {
                            mAdapter = new SearchListAdapter(getContext(), mSearchList);
                            mAdapter.setOnItemClickListener(new SearchListAdapter.OnItemClickListener() {


                                @Override
                                public void onItemClick(View view, SearchResult content) {

                                }

                                @Override
                                public void onItemClick( int pos) {

                                }
                            });
                            //设置添加或删除item时的动画，这里使用默认动画
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            //设置适配器
                            recyclerView.setAdapter(mAdapter);

                        } else {
                            mAdapter.setData(mSearchList);
                            mAdapter.notifyDataSetChanged();
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }
}
