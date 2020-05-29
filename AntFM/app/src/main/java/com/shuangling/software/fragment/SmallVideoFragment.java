package com.shuangling.software.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.player.source.VidAuth;
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.R;
import com.shuangling.software.activity.AlivcLittleLiveActivity;
import com.shuangling.software.activity.SmallVideoContentActivity;
import com.shuangling.software.adapter.SmallVideoRecyclerViewAdapter;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.ResAuthInfo;
import com.shuangling.software.entity.SmallVideo;
import com.shuangling.software.entity.SmallVideoBean;
import com.shuangling.software.entity.StsInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ACache;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.ServerInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;


public class SmallVideoFragment extends Fragment implements Handler.Callback {


    public static final int MSG_UPDATE_LIST = 0x1;
    public static final int MSG_GET_COLUMN_DECORATE = 0x2;
    public static final int MSG_GET_TYPE_CONTENT = 0x3;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;
    @BindView(R.id.noData)
    LinearLayout noData;

    private Column mColumn;
    private String mOrderBy = "1";
    private StsInfo mStsInfo;
    private List<ColumnContent> mColumnContents=new ArrayList<>();
    private SmallVideoRecyclerViewAdapter mAdapter;
    private Handler mHandler;
    private boolean addInfoStream;
    private boolean hasDecorate=false;
    public enum GetContent {
        Refresh,
        LoadMore,
        Normal
    }

    private ACache mACache;
    private RecyclerViewSkeletonScreen mSkeletonScreen;
    private List<RecyclerView> mContentRecyclerView = new ArrayList<>();
    private LinearLayout mDecorateLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Bundle args = getArguments();
        mACache = ACache.get(getContext());
        mColumn = (Column) args.getSerializable("Column");
        mHandler = new Handler(this);
        EventBus.getDefault().register(this);
        ClassicsFooter.REFRESH_FOOTER_NOTHING="没有更多了";
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_small_video, null);
        unbinder = ButterKnife.bind(this, view);

        //添加自定义分割线

        refreshLayout.setEnableLoadMore(true);
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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        mAdapter = new SmallVideoRecyclerViewAdapter(getActivity(),mColumnContents);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new SmallVideoRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

//                List<SmallVideo> currentList = new ArrayList<>();
//                Intent intent = new Intent(getContext(), SmallVideoContentActivity.class);
//                intent.putExtra("smallVideos", (Serializable) mColumnContents);
//                intent.putExtra("position",  position);
//                startActivity(intent);


                Intent intent = new Intent(getContext(), AlivcLittleLiveActivity.class);
                intent.putExtra("smallVideos", (Serializable) mColumnContents);
                intent.putExtra("position",  position);
             //   intent.putExtra("sts", (Serializable)  mStsInfo);
                startActivity(intent);

            }

        });


       // columnDecorateContent(GetContent.Normal);

        getContent(GetContent.Normal);
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if(event.getEventName().equals("onFontSizeChanged")){
          //  columnDecorateContent(GetContent.Normal);
        }
    }

    public void columnDecorateContent(final GetContent getContent) {

        if (getContent == GetContent.Normal) {
            mSkeletonScreen =
                    Skeleton.bind(recyclerView)
                            .shimmer(false)
                            .frozen(false)
                            .duration(3000)
                            .color(R.color.shimmer_color)
                            //.count(2)
                            .load(R.layout.small_video_recycler_view_item)
                            .show();
        }
        String url = ServerInfo.serviceIP + ServerInfo.columnDecorateContent + mColumn.getId();
        Map<String, String> params = new HashMap<String, String>();

        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if (getContent == GetContent.Refresh) {
                                if (refreshLayout.getState() == RefreshState.Refreshing) {
                                    refreshLayout.finishRefresh();
                                }
                            } else {
                                mSkeletonScreen.hide();
                            }
                        }catch (Exception e){

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

                        try{
                            if (getContent == GetContent.Refresh) {
                                if (refreshLayout.getState() == RefreshState.Refreshing) {
                                    refreshLayout.finishRefresh();
                                }
                            } else {
                                mSkeletonScreen.hide();
                            }
                        }catch (Exception e){

                        }

                    }
                });

            }
        });

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
                            .color(R.color.shimmer_color)
                            .load(R.layout.small_video_recycler_view_item_skeleton)
                            .show();
        }

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
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if (getContent == GetContent.Refresh) {
                                if (refreshLayout.getState() == RefreshState.Refreshing) {
                                    refreshLayout.finishRefresh();
                                }
                            } else {
                                mSkeletonScreen.hide();
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
        });

    }



    public void getDecorateContent(final int animated, final int orderBy, final int type, String columnId, String contentNumber, final int position) {

        String url = ServerInfo.serviceIP + ServerInfo.indexDecorateContent ;
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", columnId);
        params.put("type", ""+type);
        params.put("limit", contentNumber);
        params.put("sorce_type", "0");
        //params.put("city_code", "" + MainActivity.sCurrentCity.getCode());
        params.put("order_by", ""+orderBy);


        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                Message msg = Message.obtain();
                msg.what = MSG_GET_TYPE_CONTENT;
                Bundle bundle=new Bundle();
                bundle.putInt("type",type);
                bundle.putInt("position",position);
                bundle.putInt("animated",animated);
                msg.setData(bundle);
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
                    Log.d("content",result);
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        List<ColumnContent> columnContents=JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class);
                        Iterator<ColumnContent> iterator = columnContents.iterator();
                        while (iterator.hasNext()) {
                            ColumnContent columnContent = iterator.next();
                            if ( columnContent.getType()!=12) {
                                iterator.remove();
                            }
                        }


                        if (msg.arg1 == GetContent.Refresh.ordinal()) {
                            //mColumnContents.clear();
                            mColumnContents.addAll(0, columnContents);
                            if (refreshLayout.getState() == RefreshState.Refreshing) {
                                refreshLayout.finishRefresh();
                            }

                        } else if (msg.arg1 == GetContent.LoadMore.ordinal()) {
                            mColumnContents.addAll(columnContents);
                            if (refreshLayout.getState() == RefreshState.Loading) {
                                if(columnContents==null||columnContents.size()==0){
                                    //refreshLayout.setEnableLoadMore(false);
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                }else{
                                    refreshLayout.finishLoadMore();
                                }

                            }


                        } else {
                            mColumnContents.addAll(columnContents);
                        }

                        if (mColumnContents.size() == 0&&hasDecorate==false) {
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
                            mAdapter = new SmallVideoRecyclerViewAdapter(getContext(), mColumnContents);
                            //设置添加或删除item时的动画，这里使用默认动画
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            //设置适配器
                            recyclerView.setAdapter(mAdapter);

                        } else {
                            //mAdapter.setData(mColumnContents);
                            mAdapter.notifyDataSetChanged();
                            if (msg.arg1 == GetContent.Refresh.ordinal()) {
                                recyclerView.smoothScrollToPosition(0);
                            }

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
}
