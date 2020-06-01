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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.R;
import com.shuangling.software.activity.HistoryActivity;
import com.shuangling.software.adapter.HistoryAdapter;
import com.shuangling.software.entity.History;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.ServerInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;


public class HistoryFragment extends Fragment implements Handler.Callback {


    public static final int MSG_UPDATE_LIST = 0x1;

    public static final int MSG_DELETE_HISTORY = 0x2;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;
    @BindView(R.id.noData)
    RelativeLayout noData;
    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.refresh)
    TextView refresh;
    @BindView(R.id.networkError)
    RelativeLayout networkError;

    private int mCategory;
    private String mOrganizationId;
    private List<History> mHistorys = new ArrayList<>();

    private RecyclerViewSkeletonScreen mSkeletonScreen;
    private HistoryAdapter mAdapter;
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
        mCategory = args.getInt("category");
        mHandler = new Handler(this);
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.fragment_history, null);
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
        mAdapter = new HistoryAdapter(getContext(), null);
        recyclerView.setAdapter(mAdapter);

        getContent(GetContent.Normal);
        return view;
    }


    public void getContent(final GetContent getContent) {


        if (getContent == GetContent.Normal) {
            mSkeletonScreen =
                    Skeleton.bind(recyclerView)
                            .adapter(mAdapter)
                            .shimmer(false)
                            .angle(20)
                            .frozen(false)
                            .duration(3000)
                            .count(10)
                            .load(R.layout.item_skeleton_content)
                            .show();
        }

        String url = ServerInfo.serviceIP + ServerInfo.history;

        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "" + mCurrentPage);
        params.put("page_size", "" + Constant.PAGE_SIZE);

        if (mCategory == R.string.audio) {
            params.put("type", "1");
        } else if (mCategory == R.string.article) {
            params.put("type", "3");
        } else if (mCategory == R.string.video) {
            params.put("type", "4");
        } else if (mCategory == R.string.special) {
            params.put("type", "5");
        } else {
            params.put("type", "7");
        }


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
                                if (mSkeletonScreen != null) {
                                    mSkeletonScreen.hide();
                                }
                                networkError.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {

                        }

                    }
                });

            }
        });

    }


    private void deleteHistory(ArrayList<Integer> delete) {
        String url = ServerInfo.serviceIP + ServerInfo.history;
        Map<String, String> params = new HashMap<String, String>();

        if (mCategory == R.string.audio) {
            params.put("type", "1");
        } else if (mCategory == R.string.article) {
            params.put("type", "3");
        } else if (mCategory == R.string.video) {
            params.put("type", "4");
        } else if (mCategory == R.string.special) {
            params.put("type", "5");
        } else {
            params.put("type", "7");
        }

        for (int i = 0; i < delete.size(); i++) {
            params.put("ids[" + i + "]", "" + delete.get(i).toString());
        }

        OkHttpUtils.delete(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_DELETE_HISTORY;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception e) {

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }


    @OnClick(R.id.refresh)
    public void onViewClicked() {
        getContent(GetContent.Normal);
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case MSG_UPDATE_LIST:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    networkError.setVisibility(View.GONE);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        List<History> histories = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), History.class);


                        if (msg.arg1 == GetContent.Refresh.ordinal()) {
                            mHistorys = histories;
                            if (refreshLayout.getState() == RefreshState.Refreshing) {
                                refreshLayout.finishRefresh();
                            }

                        } else if (msg.arg1 == GetContent.LoadMore.ordinal()) {
                            mHistorys.addAll(histories);
                            if (refreshLayout.getState() == RefreshState.Loading) {
                                if (histories == null || histories.size() == 0) {
                                    //refreshLayout.setEnableLoadMore(false);
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                } else {
                                    refreshLayout.finishLoadMore();
                                }

                            }

                        } else {
                            if (mSkeletonScreen != null) {
                                mSkeletonScreen.hide();
                            }
                            mHistorys = histories;
                        }


                        if (mHistorys.size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            noData.setVisibility(View.GONE);
                        }


                        if (mAdapter == null) {
                            mAdapter = new HistoryAdapter(getContext(), mHistorys);
                            mAdapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int pos) {

                                }
                            });
                            //设置添加或删除item时的动画，这里使用默认动画
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            //设置适配器
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.setData(mHistorys);

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
    public void getEventBus(String str) {
        if (str.equals("historyModeChange")) {
            if (mAdapter != null) {
                mAdapter.setEditorMode(((HistoryActivity) getActivity()).isEditorMode());
            }
        } else if (str.equals("historySelectAll")) {
            if (mAdapter != null) {
                mAdapter.selectAllItem();
            }

        } else if (str.equals("historySelectDelete")) {
            int currentItem = ((HistoryActivity) getActivity()).mCurrentItem;
            if (HistoryActivity.category[currentItem] == mCategory) {

                new CircleDialog.Builder()
                        .setCanceledOnTouchOutside(false)
                        .setCancelable(false)

                        .setText("确认删除历史记录吗?")
                        .setNegative("取消", null)
                        .setPositive("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int itemSelect[] = mAdapter.getItemSelected();
                                ArrayList<Integer> ids = new ArrayList<>();
                                for (int i = 0; i < itemSelect.length; i++) {
                                    if (itemSelect[i] == 1) {
                                        ids.add(mAdapter.getData().get(i).getPost_id());
                                    }

                                }
                                deleteHistory(ids);

                            }
                        })
                        .show(getChildFragmentManager());
            }
        }
    }
}
