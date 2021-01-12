package com.shuangling.software.fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.R;
import com.shuangling.software.activity.FeedbackDetailActivity;
import com.shuangling.software.activity.HistoryActivity;
import com.shuangling.software.adapter.FeedbackListAdapter;
import com.shuangling.software.entity.FeedBackInfo;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.ServerInfo;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
public class FeedbackHistoryFragment extends Fragment implements Handler.Callback {
public static final int MSG_UPDATE_LIST = 0x1;
public static final int MSG_DELETE_HISTORY = 0x2;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;
    @BindView(R.id.noData)
    RelativeLayout noData;
private int mCategory;
    private String mOrganizationId;
    private List<FeedBackInfo> mFeedBackInfos;
    private FeedbackListAdapter mAdapter;
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
        View view = inflater.inflate(R.layout.fragment_feedback_history, null);
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
String url = ServerInfo.serviceIP + "/v1/my_feed_backs";
Map<String, String> params = new HashMap<String, String>();
params.put("page",""+page);
        params.put("page_size",""+Constant.PAGE_SIZE);
        if(mCategory==R.string.reply){
            params.put("mode","1");
        }else {
            params.put("mode","0");
        }
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
                        List<FeedBackInfo> feedBackInfos = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), FeedBackInfo.class);
if(mCategory==R.string.reply){
//                            for(int i=0;feedBackInfos!=null&&i<feedBackInfos.size();i++){
//                                if(feedBackInfos.get(i).getIs_user_read()==0){
//                                    EventBus.getDefault().post(new MessageEvent("hasNoRead",null));
//                                }
//                            }
}
if (msg.arg1 == GetContent.Refresh.ordinal()) {
                            mFeedBackInfos=feedBackInfos;
                            refreshLayout.setEnableLoadMore(true);
                        } else if (msg.arg1 == GetContent.LoadMore.ordinal()) {
                            if(feedBackInfos==null||feedBackInfos.size()==0){
                                refreshLayout.setEnableLoadMore(false);
                                //refreshLayout.finishLoadMoreWithNoMoreData();
                            }
                            mFeedBackInfos.addAll(feedBackInfos);
                        } else {
                            mFeedBackInfos = feedBackInfos;
                        }
if (mFeedBackInfos.size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            noData.setVisibility(View.GONE);
                        }
if (mAdapter == null) {
                            mAdapter = new FeedbackListAdapter(getContext(), mFeedBackInfos);
                            mAdapter.setOnItemClickListener(new FeedbackListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int pos) {
                                    Intent it=new Intent(getContext(),FeedbackDetailActivity.class);
                                    it.putExtra("id",mFeedBackInfos.get(pos).getId());
                                    startActivity(it);
                                }
                            });
                            //设置添加或删除item时的动画，这里使用默认动画
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            //设置适配器
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.updateView(mFeedBackInfos);
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
}
