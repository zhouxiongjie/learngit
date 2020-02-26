package com.shuangling.software.fragment;

import android.content.Intent;
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
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.R;
import com.shuangling.software.activity.AnchorDetailActivity;
import com.shuangling.software.activity.AnchorOrOrganizationDetailActivityH5;
import com.shuangling.software.activity.WebViewActivity;
import com.shuangling.software.adapter.ProgramAnchorAdapter;
import com.shuangling.software.entity.Anchor;
import com.shuangling.software.entity.AnchorOrganizationColumn;
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


public class ProgramAnchorFragment extends Fragment implements Handler.Callback {


    public static final int MSG_UPDATE_LIST = 0x1;


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;
    @BindView(R.id.noData)
    LinearLayout noData;

    private AnchorOrganizationColumn mColumn;
    private String mOrganizationId;
    private List<Anchor> mAnchors = new ArrayList<>();
    private ProgramAnchorAdapter mAdapter;
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
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //refreshLayout.setPrimaryColorsId(R.color.white, android.R.color.black);
        //((ClassicsHeader) refreshLayout.getRefreshHeader()).setEnableLastTime(false);
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


        getContent(GetContent.Normal);
        return view;
    }


    public void getContent(final GetContent getContent) {
        String url = ServerInfo.serviceIP + ServerInfo.getOrganizationAnchor + mOrganizationId;


        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "" + mCurrentPage);
        params.put("page_size", "" + Constant.PAGE_SIZE);


        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {
                try{
                    if (getContent == GetContent.Refresh) {
                        if (refreshLayout.getState() == RefreshState.Refreshing) {
                            refreshLayout.finishRefresh();
                        }
                    } else if (getContent == GetContent.LoadMore) {
                        if (refreshLayout.getState() == RefreshState.Loading) {
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
                        if (refreshLayout.getState() == RefreshState.Refreshing) {
                            refreshLayout.finishRefresh();
                        }
                    } else if (getContent == GetContent.LoadMore) {
                        if (refreshLayout.getState() == RefreshState.Loading) {
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

                        List<Anchor> anchors = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), Anchor.class);
                        if (getContent == GetContent.Normal || getContent == GetContent.Refresh) {
                            mAnchors = anchors;
                        } else {
                            mAnchors.addAll(anchors);
                        }

                        if (mAnchors.size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            noData.setVisibility(View.GONE);
                        }

                        if (anchors.size() < Constant.PAGE_SIZE) {
                            refreshLayout.setEnableLoadMore(false);
                        }

                        if (mAdapter == null) {
                            mAdapter = new ProgramAnchorAdapter(getContext(), mAnchors);

                            mAdapter.setOnItemClickListener(new ProgramAnchorAdapter.OnItemClickListener() {

                                @Override
                                public void onItemClick(int pos) {

//                                    Intent it = new Intent(getContext(), AnchorDetailActivity.class);
//                                    it.putExtra("anchorId", mAnchors.get(pos).getId());
//                                    startActivity(it);
                                    Intent it = new Intent(getContext(), WebViewActivity.class);
                                    it.putExtra("url", ServerInfo.h5HttpsIP+"/anchors/"+mAnchors.get(pos).getId());
                                    startActivity(it);
                                }
                            });
                            //设置添加或删除item时的动画，这里使用默认动画
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            //设置适配器
                            recyclerView.setAdapter(mAdapter);

                        } else {
                            mAdapter.updataView(mAnchors);


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
