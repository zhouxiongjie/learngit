package com.shuangln.antfm.fragment;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangln.antfm.R;
import com.shuangln.antfm.adapter.ColumnContentAdapter;
import com.shuangln.antfm.customview.MyItemDecoration;
import com.shuangln.antfm.entity.ColumnContent;
import com.shuangln.antfm.entity.ColumnInfo;
import com.shuangln.antfm.network.OkHttpCallback;
import com.shuangln.antfm.network.OkHttpUtils;
import com.shuangln.antfm.utils.Constant;
import com.shuangln.antfm.utils.ServerInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Response;


public class ContentFragment extends Fragment implements Handler.Callback{


    public static final int MSG_UPDATE_LIST = 0x1;
    public static final int MSG_REFRESH_COMPLETE = 0x2;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;

    private String mColumnName;
    private int mColumnId;
    private int currentPage = 0;
    private int totalPage = 1;

    private ColumnContent mColumnContent;
    private ColumnContentAdapter mAdapter;
    private Handler mHandler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Bundle args = getArguments();
        mColumnName = args.getString("ColumnName");
        mColumnId = args.getInt("ColumnId");
        mHandler=new Handler(this);
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_content, null);
        unbinder = ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //recyclerView.addItemDecoration(new MyItemDecoration());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        refreshLayout.setPrimaryColorsId(R.color.white, android.R.color.black);
        ((ClassicsHeader) refreshLayout.getRefreshHeader()).setEnableLastTime(false);
        refreshLayout.setEnableLoadMore(false);
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


    public void getContent() {

        String url = ServerInfo.serviceIP + ServerInfo.getColumnContent + mColumnId;
        Map<String, String> params = new HashMap<String, String>();
        params.put("limit", "" + Constant.PAGE_SIZE);
        if (mColumnName.equals(getString(R.string.city))) {
            params.put("sorceType", "1");
            params.put("cityCode", "1");

        } else {
            params.put("sorceType", "0");
        }
        params.put("update_time", "");
        params.put("operation", "");


        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if(refreshLayout.isRefreshing()){
                    refreshLayout.finishRefresh();
                }

                Message msg = Message.obtain();
                msg.what = MSG_UPDATE_LIST;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, IOException exception) {

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
                String result = (String) msg.obj;
                try {
                    mColumnContent = JSONObject.parseObject(result, ColumnContent.class);
                    if (mColumnContent.getCode() == 100000 && mColumnContent.getData() != null) {

                        if(getContext()==null){
                            return false;
                        }
                        if (mAdapter == null) {
                            mAdapter = new ColumnContentAdapter(getContext(), mColumnContent.getData());
                            mAdapter.setOnItemClickListener(new ColumnContentAdapter.OnItemClickListener() {


                                @Override
                                public void onItemClick(View view, ColumnContent.Content content) {

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
                            mAdapter.setData(mColumnContent.getData());
                            mAdapter.notifyDataSetChanged();
                        }

                    }


                } catch (Exception e) {

                }
                break;
            default:
                break;

        }
        return false;
    }
}
