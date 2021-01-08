package com.shuangling.software.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.ColumnDecorateContentAdapter;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.DecorModule;
import com.shuangling.software.entity.User;
import com.shuangling.software.fragment.ContentFragment;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.GetContentMode;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

@EnableDragToClose()
public class MoreActivity extends QMUIFragmentActivity implements Handler.Callback {
    public static final String TAG = "ContentActivity";
    public static final int MSG_UPDATE_LIST = 0x1;
    public static final int MSG_GET_TYPE_CONTENT = 0x3;
    //    @BindView(R.id.activity_title)
//    TopTitleBar activityTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.noScriptText)
    TextView noScriptText;
    @BindView(R.id.noData)
    RelativeLayout noData;
    @BindView(R.id.topbar)
    QMUITopBarLayout topbar;
    private Column mColumn;
    private DecorModule mDecorModule;
    //private String mType;       //分类 1 音频 2专辑 3 文章 4视频 5专题 7图集
    //private String mTitle;      //更多的标题
    private int mCurrentPage = 1;
    private List<ColumnContent> mColumnContents = new ArrayList<>();
    private ColumnDecorateContentAdapter mAdapter;
    private Handler mHandler;

    private RecyclerViewSkeletonScreen mSkeletonScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        topbar.addLeftImageButton(R.drawable.ic_left,R.id.qmui_topbar_item_left_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        QMUIStatusBarHelper.setStatusBarLightMode(this );


        mHandler = new Handler(this);
        mColumn = (Column) getIntent().getSerializableExtra("column");
        mDecorModule = (DecorModule) getIntent().getSerializableExtra("decorModule");
        //activityTitle.setTitleText(mDecorModule.getTitle());
        topbar.setTitle(mDecorModule.getTitle());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.recycleview_divider_drawable));
        recyclerView.addItemDecoration(divider);
        // refreshLayout.setPrimaryColorsId(R.color.white, android.R.color.black);
        //((ClassicsHeader) refreshLayout.getRefreshHeader()).setEnableLastTime(false);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mCurrentPage = 1;
                getContent(GetContentMode.Refresh);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                mCurrentPage++;
                getContent(GetContentMode.LoadMore);
            }
        });
        mAdapter = new ColumnDecorateContentAdapter(this, null);
        recyclerView.setAdapter(mAdapter);
        getContent(GetContentMode.Normal);

        //columnDecorateContent(GetContent.Normal);
    }

    public void getContent(final GetContentMode getContent) {
        if (getContent == GetContentMode.Normal) {
            mSkeletonScreen =
                    Skeleton.bind(recyclerView)
                            .adapter(mAdapter)
                            .shimmer(false)
                            .angle(20)
                            .frozen(false)
                            .duration(1200)
                            .count(3)
                            .load(R.layout.item_skeleton_content)
                            .show();
        }
        String url = ServerInfo.serviceIP + "/v2/renovation_posts";
        Map<String, String> params = new HashMap<String, String>();
        params.put("page_size", "" + Constant.PAGE_SIZE);
        params.put("page", "" + mCurrentPage);

        //params.put("mobile_source", "app");
        params.put("id", "" + mColumn.getId());
        params.put("sorce_type", "0");
        params.put("order_by", "" + mDecorModule.getOrder_by());

        if (mDecorModule.getType() == 15) {
            //视频
            params.put("type", "4");
        } else if (mDecorModule.getType() == 16) {
            //音频
            params.put("type", "2");
        } else if (mDecorModule.getType() == 17) {
            //文章
            params.put("type", "3");
        } else if (mDecorModule.getType() == 18) {
            //图集
            params.put("type", "7");
        } else {
            //专题
            params.put("type", "5");
        }


        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        try{
//                            if (getContent == GetContentMode.Refresh) {
//                                if (refreshLayout.getState() == RefreshState.Refreshing) {
//                                    refreshLayout.finishRefresh();
//                                    refreshLayout.resetNoMoreData();
//                                }
//                            } else if (getContent == GetContentMode.LoadMore) {
//                                if (refreshLayout.getState() == RefreshState.Loading) {
//                                    refreshLayout.finishLoadMore();
//                                }
//                            }else {
//                                mSkeletonScreen.hide();
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
                            if (getContent == GetContentMode.Refresh) {
                                if (refreshLayout.getState() == RefreshState.Refreshing) {
                                    refreshLayout.finishRefresh();
                                    refreshLayout.resetNoMoreData();
                                }
                            } else if (getContent == GetContentMode.LoadMore) {
                                if (refreshLayout.getState() == RefreshState.Loading) {
                                    refreshLayout.finishLoadMore();
                                }
                            } else {
                                mSkeletonScreen.hide();
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
                        List<ColumnContent> columnContents = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), ColumnContent.class);
                        if (msg.arg1 == GetContentMode.Refresh.ordinal()) {
                            mColumnContents.addAll(0, columnContents);
                            refreshLayout.finishRefresh();
                        } else if (msg.arg1 == GetContentMode.LoadMore.ordinal()) {
                            mColumnContents.addAll(columnContents);
                            if (refreshLayout.getState() == RefreshState.Loading) {
                                if (columnContents == null || columnContents.size() == 0) {
                                    //refreshLayout.setEnableLoadMore(false);
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                } else {
                                    refreshLayout.finishLoadMore();
                                }
                            }
                        } else {
                            if(mSkeletonScreen!=null){
                                mSkeletonScreen.hide();
                            }
                            mColumnContents = columnContents;
                        }
                        if (mColumnContents.size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            noData.setVisibility(View.GONE);
                        }
                        if (mAdapter == null) {
                            mAdapter = new ColumnDecorateContentAdapter(this, mColumnContents);
                            mAdapter.setOnItemClickListener(new ColumnDecorateContentAdapter.OnItemClickListener() {
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
                            if (msg.arg1 == ContentFragment.GetContent.Refresh.ordinal()) {
                                recyclerView.smoothScrollToPosition(0);
                            }
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
            default:
                break;
        }
        return false;
    }


}
