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

import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.R;
import com.shuangling.software.adapter.ProgramContentAdapter;
import com.shuangling.software.entity.AnchorOrganizationColumn;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.Constant;
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


public class ProgramContentFragment extends Fragment implements Handler.Callback {


    public static final int MSG_UPDATE_LIST = 0x1;


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;
    @BindView(R.id.noData)
    LinearLayout noData;

    private AnchorOrganizationColumn mColumn;
    private String mAnchorId;
    private String mOrganizationId;
    private List<ColumnContent> mColumnContents = new ArrayList<>();
    private ProgramContentAdapter mAdapter;
    private Handler mHandler;

    public enum GetContent {
        Refresh,
        LoadMore,
        Normal
    }

    ;


    private int mCurrentPage = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle args = getArguments();
        mColumn = (AnchorOrganizationColumn)args.getSerializable("columns");
        mOrganizationId = args.getString("organizationId");
        mAnchorId = args.getString("anchorId");
        mHandler = new Handler(this);
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_content, null);
        unbinder = ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //recyclerView.addItemDecoration(new MyItemDecoration());
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
        recyclerView.addItemDecoration(divider);
        refreshLayout.setPrimaryColorsId(R.color.white, android.R.color.black);
        ((ClassicsHeader) refreshLayout.getRefreshHeader()).setEnableLastTime(false);
        refreshLayout.setEnableRefresh(false);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mCurrentPage = 1;
                getContent(GetContent.Refresh);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                mCurrentPage++;
                getContent(GetContent.LoadMore);
            }
        });
        mAdapter = new ProgramContentAdapter(getContext(), null);
        recyclerView.setAdapter(mAdapter);

        getContent(GetContent.Normal);
        return view;
    }


    public void getContent(final GetContent getContent) {
        String url;
        if (!TextUtils.isEmpty(mAnchorId)) {
            url = ServerInfo.serviceIP + ServerInfo.getPrograms + mAnchorId;
        } else {
            url = ServerInfo.serviceIP + ServerInfo.getPrograms + mOrganizationId;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "" + mCurrentPage);
        params.put("page_size", "" + Constant.PAGE_SIZE);
        if (!TextUtils.isEmpty(mAnchorId)) {
            params.put("type", "0");
        } else {
            params.put("type", "1");
        }
        params.put("post_type", ""+mColumn.getPost_type());
//        if (mCategory.equals(getResources().getString(R.string.album))) {
//            params.put("post_type", "2");
//        } else if (mCategory.equals(getResources().getString(R.string.article))) {
//            params.put("post_type", "3");
//        } else if (mCategory.equals(getResources().getString(R.string.video))) {
//            params.put("post_type", "4");
//        } else if (mCategory.equals(getResources().getString(R.string.special))) {
//            params.put("post_type", "5");
//        } else {
//            params.put("post_type", "7");
//        }


        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {
                try{
                    if (getContent == GetContent.Refresh) {
                        if (refreshLayout.isRefreshing()) {
                            refreshLayout.finishRefresh();
                        }
                    } else if (getContent == GetContent.LoadMore) {
                        if (refreshLayout.isLoading()) {
                            refreshLayout.finishLoadMore();
                        }
                    }
                }catch (Exception e){

                }


                Message msg = Message.obtain();
                msg.what = MSG_UPDATE_LIST;
                msg.arg1 = getContent.ordinal();
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {
                try{
                    if (getContent == GetContent.Refresh) {
                        if (refreshLayout.isRefreshing()) {
                            refreshLayout.finishRefresh();
                        }
                    } else if (getContent == GetContent.LoadMore) {
                        if (refreshLayout.isLoading()) {
                            refreshLayout.finishLoadMore();
                        }
                    }
                }catch (Exception e){

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

        switch (msg.what) {
            case MSG_UPDATE_LIST:

                try {
                    String result = (String) msg.obj;
                    GetContent getContent = GetContent.values()[msg.arg1];
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        List<ColumnContent> columnContents = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), ColumnContent.class);
                        if (getContent == GetContent.Refresh) {
                            mColumnContents = columnContents;
                        } else {
                            mColumnContents.addAll(columnContents);
                        }

                        if (mColumnContents.size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            noData.setVisibility(View.GONE);
                        }

                        if (columnContents.size() < Constant.PAGE_SIZE) {
                            refreshLayout.setEnableLoadMore(false);
                        }

                        if (mAdapter == null) {
                            mAdapter = new ProgramContentAdapter(getContext(), mColumnContents);

                            mAdapter.setOnItemClickListener(new ProgramContentAdapter.OnItemClickListener() {

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
