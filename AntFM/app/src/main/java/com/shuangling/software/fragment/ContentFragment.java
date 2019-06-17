package com.shuangling.software.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.R;
import com.shuangling.software.adapter.ColumnContentAdapter;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Response;


public class ContentFragment extends Fragment implements Handler.Callback{


    public static final int MSG_UPDATE_LIST = 0x1;

    public static final int MSG_GET_TOP_POST = 0x2;

    public static final int MSG_GET_EXCELLENT_POST = 0x2;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;

    private Column mColumn;
    private String mOrderBy="1";
    private List<ColumnContent> mColumnContents;
    private ColumnContentAdapter mAdapter;
    private Handler mHandler;

    public enum GetContent{
        Refresh,
        LoadMore,
        Normal
    };


    private int page=1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Bundle args = getArguments();
        mColumn = (Column)args.getSerializable("Column");
        mHandler=new Handler(this);
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_content, null);
        unbinder = ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //recyclerView.addItemDecoration(new MyItemDecoration());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        refreshLayout.setPrimaryColorsId(R.color.white, android.R.color.black);
        ((ClassicsHeader) refreshLayout.getRefreshHeader()).setEnableLastTime(false);
        //refreshLayout.setEnableLoadMore(false);
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
        mAdapter=new ColumnContentAdapter(getContext(),null);
        recyclerView.setAdapter(mAdapter);

        if(mColumn.getChildren()!=null&&mColumn.getChildren().size()>0){

            ViewGroup secondColumn = (ViewGroup) inflater.inflate(R.layout.second_column_layout, recyclerView, false);
            LinearLayout columnContent=secondColumn.findViewById(R.id.columnContent);
            for (int i = 0;i < mColumn.getChildren().size(); i++) {
                Column column = mColumn.getChildren().get(i);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.leftMargin = CommonUtils.dip2px(10);
                params.rightMargin = CommonUtils.dip2px(10);
                View secondColumnItem = inflater.inflate(R.layout.second_column_item_layout, secondColumn, false);
                TextView columnTextView = secondColumnItem.findViewById(R.id.text);
                SimpleDraweeView indicator = secondColumnItem.findViewById(R.id.logo);
                columnTextView.setText(column.getName());
                if (!TextUtils.isEmpty(column.getIcon())) {
                    Uri uri = Uri.parse(column.getIcon());
                    int width = CommonUtils.dip2px(20);
                    int height = width;
                    ImageLoader.showThumb(uri, indicator, width, height);
                }else{
                    indicator.setVisibility(View.GONE);
                }
                secondColumnItem.setTag(column);
                secondColumnItem.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                    }
                });
                columnContent.addView(secondColumnItem, i,params);
            }

            mAdapter.addHeaderView(secondColumn);

        }
        getContent(GetContent.Normal);
        return view;
    }


    public void getContent(final GetContent getContent) {

        String url = ServerInfo.serviceIP + ServerInfo.getColumnContent + mColumn.getId();
        Map<String, String> params = new HashMap<String, String>();
        params.put("limit", "" + Constant.PAGE_SIZE);
        params.put("sorceType", "0");

        if(getContent==GetContent.Refresh){
            params.put("operation", "up");
            params.put("update_time", mColumnContents.size()>0?mColumnContents.get(0).getUpdate_time():"");
        }else if(getContent==GetContent.LoadMore){
            params.put("operation", "down");
            params.put("update_time", mColumnContents.size()>0?mColumnContents.get(mColumnContents.size()-1).getUpdate_time():"");
        }
        //params.put("mobile_source", "app");
        params.put("order_by", mOrderBy);


        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if(getContent==GetContent.Refresh){
                    if(refreshLayout.isRefreshing()){
                        refreshLayout.finishRefresh();
                    }
                }else if(getContent==GetContent.LoadMore){
                    if(refreshLayout.isLoading()){
                        refreshLayout.finishLoadMore();
                    }
                }

                Message msg = Message.obtain();
                msg.what = MSG_UPDATE_LIST;
                msg.arg1=getContent.ordinal();
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, IOException exception) {

                if(getContent==GetContent.Refresh){
                    if(refreshLayout.isRefreshing()){
                        refreshLayout.finishRefresh();
                    }
                }else if(getContent==GetContent.LoadMore){
                    if(refreshLayout.isLoading()){
                        refreshLayout.finishLoadMore();
                    }
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

        switch (msg.what){
            case MSG_UPDATE_LIST:
                String result = (String) msg.obj;
                try {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        if(msg.arg1==GetContent.Refresh.ordinal()){
                            mColumnContents.addAll(0,JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class));
                        }else if(msg.arg1==GetContent.LoadMore.ordinal()){
                            mColumnContents.addAll(JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class));
                        }else {
                            mColumnContents= JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class);
                        }
                        if (mAdapter == null) {
                            mAdapter = new ColumnContentAdapter(getContext(), mColumnContents);
                            mAdapter.setOnItemClickListener(new ColumnContentAdapter.OnItemClickListener() {


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
                            if(msg.arg1==GetContent.Refresh.ordinal()){
                                recyclerView.smoothScrollToPosition(0);
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
