package com.shuangling.software.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.MessageListAdapter;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.MessageInfo;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

//@EnableDragToClose()
public class MessageListActivity extends QMUIActivity/*AppCompatActivity*/ implements Handler.Callback {
    public static final int MSG_UPDATE_LIST = 0x1;

    public enum GetContent {
        Refresh,
        LoadMore,
        Normal
    }

    @BindView(R.id.activity_title)
    /*TopTitleBar*/ QMUITopBarLayout activityTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.noData)
    LinearLayout noData;
    private Handler mHandler;
    private int mCurrentPage = 1;
    private List<MessageInfo> mMessages;
    private MessageListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
//        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this); //
        activityTitle.addLeftImageButton(R.drawable.ic_left, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(view -> { //
            finish();
        });
        activityTitle.setTitle("消息中心");
        init();
    }

    private void init() {
        mHandler = new Handler(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.recycleview_divider_drawable));
        recyclerView.addItemDecoration(divider);
        //refreshLayout.setPrimaryColorsId(R.color.white, android.R.color.black);
        //((ClassicsHeader) refreshLayout.getRefreshHeader()).setEnableLastTime(false);
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setEnableRefresh(true);
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
    }

    public void getContent(final GetContent getContent) {
        String url = ServerInfo.serviceIP + ServerInfo.messages;
        Map<String, String> params = new HashMap<>();
        params.put("message_type_ids", "1,2");
        params.put("page", "" + mCurrentPage);
        params.put("page_size", "" + Constant.PAGE_SIZE);
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
//                try{
//                    if (getContent == GetContent.Refresh) {
//                        if (refreshLayout.getState() == RefreshState.Refreshing) {
//                            refreshLayout.finishRefresh();
//                        }
//                    } else if (getContent == GetContent.LoadMore) {
//                        if (refreshLayout.getState() == RefreshState.Loading) {
//                            refreshLayout.finishLoadMore();
//                        }
//                    }
//                }catch (Exception e){
//
//                }
                Message msg = Message.obtain();
                msg.what = MSG_UPDATE_LIST;
                msg.arg1 = getContent.ordinal();
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
//                try{
//                    if (getContent == GetContent.Refresh) {
//                        if (refreshLayout.getState() == RefreshState.Refreshing) {
//                            refreshLayout.finishRefresh();
//                        }
//                    } else if (getContent == GetContent.LoadMore) {
//                        if (refreshLayout.getState() == RefreshState.Loading) {
//                            refreshLayout.finishLoadMore();
//                        }
//                    }
//                }catch (Exception e){
//
//                }
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
//                                if (mSkeletonScreen != null) {
//                                    mSkeletonScreen.hide();
//                                }
//                                networkError.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
    }

    public void readMessage(MessageInfo message) {
        String url = ServerInfo.serviceIP + ServerInfo.readMessage + message.getId();
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.put(url, null, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    @Override
    protected void onResume() {
        getContent(GetContent.Normal);
        super.onResume();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_LIST:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        List<MessageInfo> messages = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), MessageInfo.class);
                        if (msg.arg1 == GetContent.Refresh.ordinal()) {
                            mMessages = messages;
                            if (refreshLayout.getState() == RefreshState.Refreshing) {
                                refreshLayout.finishRefresh();
                            }
                        } else if (msg.arg1 == GetContent.LoadMore.ordinal()) {
                            mMessages.addAll(messages);
                            if (refreshLayout.getState() == RefreshState.Loading) {
                                if (messages == null || messages.size() == 0) {
                                    //refreshLayout.setEnableLoadMore(false);
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                } else {
                                    refreshLayout.finishLoadMore();
                                }
                            }
                        } else {
                            mMessages = messages;
                        }
                        if (mMessages.size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            noData.setVisibility(View.GONE);
                        }
                        if (mAdapter == null) {
                            mAdapter = new MessageListAdapter(this, mMessages);
                            mAdapter.setOnItemClickListener(new MessageListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int pos) {
                                    readMessage(mMessages.get(pos));
                                    Intent it = new Intent(MessageListActivity.this, WebViewBackActivity.class);
                                    it.putExtra("url", mMessages.get(pos).getMessage().getContent().getJump_url());
                                    startActivity(it);
                                }
                            });
                            //设置添加或删除item时的动画，这里使用默认动画
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            //设置适配器
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.updateView(mMessages);
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
