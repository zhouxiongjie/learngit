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
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.R;
import com.shuangling.software.activity.CashDetailActivity;
import com.shuangling.software.activity.HistoryActivity;
import com.shuangling.software.adapter.AttentionAdapter;
import com.shuangling.software.adapter.CashIncomeAdapter;
import com.shuangling.software.adapter.TakeCashAdapter;
import com.shuangling.software.entity.Attention;
import com.shuangling.software.entity.TakeCashInfo;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.ServerInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;


public class TakeCashFragment extends Fragment implements Handler.Callback {


    public static final int MSG_UPDATE_LIST = 0x1;

    public static final int MSG_DELETE_HISTORY = 0x2;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;
    @BindView(R.id.noData)
    LinearLayout noData;

    private int mCategory;
    private String mOrganizationId;
    private List<TakeCashInfo> mTakeCashInfos;
    private TakeCashAdapter mAdapter;
    private Handler mHandler;

    public enum GetContent {
        Refresh,
        LoadMore,
        Normal
    }


    private int page = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle args = getArguments();
        mCategory = args.getInt("category");
        mHandler = new Handler(this);
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, null);
        EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //recyclerView.addItemDecoration(new MyItemDecoration());
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
        recyclerView.addItemDecoration(divider);
        //refreshLayout.setPrimaryColorsId(R.color.white, android.R.color.black);
        //((ClassicsHeader) refreshLayout.getRefreshHeader()).setEnableLastTime(false);
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page=1;
                getContent(GetContent.Refresh);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page++;
                getContent(GetContent.LoadMore);
            }
        });

        getContent(GetContent.Normal);
        return view;
    }


    public void getContent(final GetContent getContent) {

        String url = ServerInfo.emc + ServerInfo.takeCashDetail;

        Map<String, String> params = new HashMap<String, String>();

        params.put("page",""+page);
        params.put("page_size",""+Constant.PAGE_SIZE);


        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

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
                            }
                        }catch (Exception e){

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
                            }
                        }catch (Exception e){

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
            case MSG_UPDATE_LIST:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        List<TakeCashInfo> takeCashInfos = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), TakeCashInfo.class);
                        if (msg.arg1 == GetContent.Refresh.ordinal()) {
                            mTakeCashInfos=takeCashInfos;
                            refreshLayout.setEnableLoadMore(true);
                        } else if (msg.arg1 == GetContent.LoadMore.ordinal()) {
                            if(takeCashInfos==null||takeCashInfos.size()==0){
                                refreshLayout.setEnableLoadMore(false);
                                //refreshLayout.finishLoadMoreWithNoMoreData();
                            }
                            mTakeCashInfos.addAll(takeCashInfos);
                        } else {
                            mTakeCashInfos = takeCashInfos;
                        }

                        if (mTakeCashInfos.size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            noData.setVisibility(View.GONE);
                        }

                        if (mAdapter == null) {
                            mAdapter = new TakeCashAdapter(getContext(), mTakeCashInfos);
                            mAdapter.setOnItemClickListener(new TakeCashAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int pos) {
                                    int requestId=mTakeCashInfos.get(pos).getId();
                                    Intent it=new Intent(getContext(),CashDetailActivity.class);
                                    it.putExtra("id",requestId);
                                    startActivity(it);
                                }
                            });
                            //设置添加或删除item时的动画，这里使用默认动画
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            //设置适配器
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.updateView(mTakeCashInfos);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
                break;
            case MSG_DELETE_HISTORY:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show(jsonObject.getString("data"));
                        ((HistoryActivity) getActivity()).cancelEditorMode();
                        getContent(GetContent.Normal);


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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("OnMoneyChanged")) {
            getContent(GetContent.Normal);

        }
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
