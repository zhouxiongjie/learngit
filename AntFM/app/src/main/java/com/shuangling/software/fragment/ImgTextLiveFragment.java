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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.shuangling.software.R;
import com.shuangling.software.adapter.ColumnDecorateContentAdapter;
import com.shuangling.software.adapter.ImgTextAdapter;
import com.shuangling.software.entity.ImgTextInfo;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.GetContentMode;
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


public class ImgTextLiveFragment extends Fragment implements Handler.Callback {


    public static final int MSG_UPDATE_LIST = 0x1;

    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.noScriptText)
    TextView noScriptText;
    @BindView(R.id.noData)
    RelativeLayout noData;
    @BindView(R.id.desc)
    TextView desc;
    @BindView(R.id.refresh)
    TextView refresh;
    @BindView(R.id.networkError)
    RelativeLayout networkError;
    @BindView(R.id.root)
    RelativeLayout root;
    private Handler mHandler;

    private String mStreamName;
    private int mRoomId;
    private int mCurrentPage = 1;
    private List<ImgTextInfo> mImgTextInfos = new ArrayList<>();

    private ImgTextAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mHandler = new Handler(this);
        Bundle args = getArguments();

        mStreamName = args.getString("streamName");
        mRoomId = args.getInt("roomId");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_img_text_live, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        init();
        return view;

    }


    private void init() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //添加自定义分割线
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(true);

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                mCurrentPage++;
                getImgText(GetContentMode.LoadMore);
            }
        });
        mAdapter = new ImgTextAdapter(getActivity());
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(mAdapter);

        getImgText(GetContentMode.Normal);

    }


    private void getImgText(GetContentMode mode) {
        String url = ServerInfo.live + "/v3/pictexts";
        Map<String, String> params = new HashMap<>();
        params.put("room_id", "" + mRoomId);
        params.put("page", "" + mCurrentPage);
        params.put("page_size", "" + Constant.PAGE_SIZE);

        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_UPDATE_LIST;
                msg.arg1 = mode.ordinal();
                msg.obj = response;
                mHandler.sendMessage(msg);




            }

            @Override
            public void onFailure(Call call, Exception exception) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            if (mode == GetContentMode.Refresh) {
                                if (refreshLayout.getState() == RefreshState.Refreshing) {
                                    refreshLayout.finishRefresh();
                                }
                            } else if (mode == GetContentMode.LoadMore) {
                                if (refreshLayout.getState() == RefreshState.Loading) {
                                    refreshLayout.finishLoadMore();
                                }
                            } else {

                                //networkError.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {

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
                try {

                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);

                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        List<ImgTextInfo> imgTextInfos = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), ImgTextInfo.class);

                        for(int i=0;imgTextInfos!=null&&i<imgTextInfos.size();i++){

                            ImgTextInfo imgTextInfo=imgTextInfos.get(i);
                            String message=imgTextInfo.getMsg();
                            JSONObject jo = JSONObject.parseObject(message);
                            imgTextInfo.setContents(jo.getString("contents"));

                            JSONArray ja=jo.getJSONArray("imgArray");
                            List<String> imgArray=new ArrayList<>();
                            for(int j=0;ja!=null&&j<ja.size();j++){
                                imgArray.add(ja.getString(j));
                            }
                            imgTextInfo.setImgArray(imgArray);
                        }




                        if (msg.arg1 == GetContentMode.Refresh.ordinal()) {
                            mImgTextInfos = imgTextInfos;
                            if (refreshLayout.getState() == RefreshState.Refreshing) {
                                refreshLayout.finishRefresh();
                            }

                        } else if (msg.arg1 == GetContentMode.LoadMore.ordinal()) {
                            mImgTextInfos.addAll(imgTextInfos);
                            if (refreshLayout.getState() == RefreshState.Loading) {
                                if (imgTextInfos == null || imgTextInfos.size() == 0) {
                                    //refreshLayout.setEnableLoadMore(false);
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                } else {
                                    refreshLayout.finishLoadMore();
                                }

                            }

                        } else {

                            mImgTextInfos = imgTextInfos;
                        }


                        if (mImgTextInfos.size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            noData.setVisibility(View.GONE);
                        }


                        if (mAdapter == null) {
                            mAdapter = new ImgTextAdapter(getActivity(), mImgTextInfos);
                            mAdapter.setOnItemClickListener(new ImgTextAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int pos) {

                                }
                            });
                            //设置添加或删除item时的动画，这里使用默认动画
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            //设置适配器
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.updateView(mImgTextInfos);

                        }
                    } else {
                        if (msg.arg1 == GetContentMode.Refresh.ordinal()) {
                            if (refreshLayout.getState() == RefreshState.Refreshing) {
                                refreshLayout.finishRefresh();
                            }
                        } else if (msg.arg1 == GetContentMode.LoadMore.ordinal()) {
                            if (refreshLayout.getState() == RefreshState.Loading) {
                                refreshLayout.finishLoadMore();
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
        return false;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("ImgTextLiveUpdate")) {
            mCurrentPage = 1;
            getImgText(GetContentMode.Normal);
        }
    }

    @Override
    public void onDestroyView() {

        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
