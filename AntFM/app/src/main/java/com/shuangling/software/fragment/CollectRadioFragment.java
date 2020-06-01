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
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.R;
import com.shuangling.software.activity.RadioDetailActivity;
import com.shuangling.software.activity.TvDetailActivity;
import com.shuangling.software.adapter.CollectRadioAdapter;
import com.shuangling.software.entity.Collect;
import com.shuangling.software.entity.CollectRadio;
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
import butterknife.Unbinder;
import okhttp3.Call;


public class CollectRadioFragment extends Fragment implements Handler.Callback {


    public static final int MSG_UPDATE_LIST = 0x1;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;
    @BindView(R.id.noData)
    RelativeLayout noData;

    private int mCategory;
    private List<CollectRadio> mCollects=new ArrayList<>();
    private CollectRadioAdapter mAdapter;
    private Handler mHandler;
    private RecyclerViewSkeletonScreen mSkeletonScreen;
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
        View view = inflater.inflate(R.layout.fragment_collect, null);
        unbinder = ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
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
                mCurrentPage ++;
                getContent(GetContent.LoadMore);
            }
        });
        mAdapter = new CollectRadioAdapter(getContext(), null);
        mAdapter.setOnItemClickListener(new CollectRadioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                if (mCategory == R.string.radio) {

                    Intent it = new Intent(getContext(), RadioDetailActivity.class);
                    it.putExtra("radioId", mCollects.get(pos).getChannel_id());
                    startActivity(it);
                } else {
                    Intent it = new Intent(getContext(), TvDetailActivity.class);
                    it.putExtra("radioId", mCollects.get(pos).getChannel_id());
                    startActivity(it);
                }
            }
        });
        //设置添加或删除item时的动画，这里使用默认动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置适配器
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


        String url = ServerInfo.serviceIP + ServerInfo.myRadioCollect;

        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "" + mCurrentPage);
        params.put("page_size", "" + Constant.PAGE_SIZE);

        if (mCategory == R.string.radio) {
            params.put("type", "1");
        } else {
            params.put("type", "2");
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

                        try{
                            if (getContent == GetContent.Refresh) {
                                if (refreshLayout.getState() == RefreshState.Refreshing) {
                                    refreshLayout.finishRefresh();
                                }
                            } else if (getContent == GetContent.LoadMore) {
                                if (refreshLayout.getState() == RefreshState.Loading) {
                                    refreshLayout.finishLoadMore();
                                }
                            }else{
                                if(mSkeletonScreen!=null){
                                    mSkeletonScreen.hide();
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
        EventBus.getDefault().unregister(this);
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
                        List<CollectRadio> collects = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), CollectRadio.class);

//                        if (getContent == GetContent.Refresh) {
//                            refreshLayout.setEnableLoadMore(true);
//                            mCollects = collects;
//                        } else if(getContent==GetContent.LoadMore){
//                            if(collects==null||collects.size()==0){
//                                refreshLayout.setEnableLoadMore(false);
//                                //refreshLayout.finishLoadMoreWithNoMoreData();
//                            }
//                            mCollects.addAll(collects);
//
//                        }else{
//                            refreshLayout.setEnableLoadMore(true);
//                            mCollects.addAll(collects);
//                        }

                        if (msg.arg1 == GetContent.Refresh.ordinal()) {
                            mCollects = collects;
                            if (refreshLayout.getState() == RefreshState.Refreshing) {
                                refreshLayout.finishRefresh();
                            }

                        } else if (msg.arg1 == GetContent.LoadMore.ordinal()) {
                            mCollects.addAll(collects);
                            if (refreshLayout.getState() == RefreshState.Loading) {
                                if(collects==null||collects.size()==0){
                                    //refreshLayout.setEnableLoadMore(false);
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                }else{
                                    refreshLayout.finishLoadMore();
                                }

                            }

                        } else {
                            if(mSkeletonScreen!=null){
                                mSkeletonScreen.hide();
                            }
                            mCollects=collects;
                        }


                        if (mCollects.size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            noData.setVisibility(View.GONE);
                        }


                        if (mAdapter == null) {
                            mAdapter = new CollectRadioAdapter(getContext(), mCollects);
                            mAdapter.setOnItemClickListener(new CollectRadioAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int pos) {
                                    if (mCategory == R.string.radio) {

                                        Intent it = new Intent(getContext(), RadioDetailActivity.class);
                                        it.putExtra("radioId", mCollects.get(pos).getChannel_id());
                                        startActivity(it);
                                    } else {
                                        Intent it = new Intent(getContext(), TvDetailActivity.class);
                                        it.putExtra("radioId", mCollects.get(pos).getChannel_id());
                                        startActivity(it);
                                    }
                                }
                            });
                            //设置添加或删除item时的动画，这里使用默认动画
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            //设置适配器
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.setData(mCollects);
                            mAdapter.notifyDataSetChanged();
                        }
                    }else{
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
            default:
                break;

        }
        return false;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(String str) {

    }
}
