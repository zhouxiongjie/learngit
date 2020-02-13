package com.shuangling.software.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gyf.immersionbar.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.AnchorListAdapter;
import com.shuangling.software.adapter.AttentionAdapter;
import com.shuangling.software.adapter.SubscribeAdapter;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.Anchor;
import com.shuangling.software.entity.Subscribe;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.Constant;
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
public class MoreAnchorOrOrganizationActivity extends AppCompatActivity implements Handler.Callback {

    public static final int MSG_UPDATE_LIST = 0x1;



    public enum GetContent{
        Refresh,
        LoadMore,
        Normal
    };

    @BindView(R.id.activtyTitle)
    TopTitleBar activtyTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private int mCurrentPage=1;
    private Handler mHandler;
    private List<Anchor> mAnchors=new ArrayList<>();
    private AnchorListAdapter mAdapter;
    private int mType;
    private int mOrderBy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_subscribe);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mHandler=new Handler(this);
        mType=getIntent().getIntExtra("type",1);
        mOrderBy=getIntent().getIntExtra("orderBy",1);
        if(mType==1){
            activtyTitle.setTitleText("更多机构");
        }else{
            activtyTitle.setTitleText("更多主播");
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.recycleview_divider_drawable));
        recyclerView.addItemDecoration(divider);
        ((ClassicsHeader) refreshLayout.getRefreshHeader()).setEnableLastTime(false);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mCurrentPage=1;
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
    }


    public void getContent(final GetContent getContent) {

        String url = ServerInfo.serviceIP + ServerInfo.getCityAnchors;
        Map<String, String> params = new HashMap<String, String>();
        params.put("city_code", "" + MainActivity.sCurrentCity.getCode());
        params.put("order_by", ""+mOrderBy);
        params.put("page", ""+mCurrentPage);
        params.put("page_size", "" + Constant.PAGE_SIZE);
        params.put("type", ""+mType);
        params.put("mode", "one");



        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

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
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case MSG_UPDATE_LIST:
                try {
                    String result = (String) msg.obj;
                    GetContent getContent=GetContent.values()[msg.arg1];
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        List<Anchor> anchors=JSONArray.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), Anchor.class);
                        if(getContent==GetContent.Normal){
                            mAnchors = anchors;
                        }else{
                            mAnchors.addAll(anchors);
                        }

                        if(anchors!=null&&anchors.size()==Constant.PAGE_SIZE){
                            refreshLayout.setEnableLoadMore(true);
                        }else {
                            refreshLayout.setEnableLoadMore(true);
                        }


                        if (mAdapter == null) {
                            mAdapter = new AnchorListAdapter(this, mAnchors);
                            mAdapter.setOnItemClickListener(new AnchorListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int pos) {
                                    if(mType==1){
//                                        Intent it = new Intent(MoreAnchorOrOrganizationActivity.this, OrganizationDetailActivity.class);
//                                        it.putExtra("organizationId", mAnchors.get(pos).getId());
//                                        startActivity(it);

                                        Intent it = new Intent(MoreAnchorOrOrganizationActivity.this, WebViewActivity.class);
                                        it.putExtra("url", ServerInfo.h5HttpsIP+"/orgs/"+mAnchors.get(pos).getId());
                                        startActivity(it);

                                    }else {
//                                        Intent it = new Intent(MoreAnchorOrOrganizationActivity.this, AnchorDetailActivity.class);
//                                        it.putExtra("anchorId", mAnchors.get(pos).getId());
//                                        startActivity(it);

                                        Intent it = new Intent(MoreAnchorOrOrganizationActivity.this, WebViewActivity.class);
                                        it.putExtra("url", ServerInfo.h5HttpsIP+"/anchors/"+mAnchors.get(pos).getId());
                                        startActivity(it);
                                    }
                                }
                            });
                            //设置添加或删除item时的动画，这里使用默认动画
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            //设置适配器
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.updateView(mAnchors);
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
