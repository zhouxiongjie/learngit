package com.shuangling.software.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.shuangling.software.R;
import com.shuangling.software.adapter.RecommendContentAdapter;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class AlbumIntroduceFragment extends Fragment implements Handler.Callback {
    public static final int MSG_GET_RELATED_POST = 0x1;
    //    @BindView(R.id.introduction)
//    TextView introduction;
    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private RecommendContentAdapter mAdapter;
    private String mIntroduction;
    private int mAlbumId;
    private Handler mHandler;
    private List<ColumnContent> mColumnContents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mHandler = new Handler(this);
        mIntroduction = getArguments().getString("introduction");
        mAlbumId = getArguments().getInt("albumId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_introduce, container, false);
        unbinder = ButterKnife.bind(this, view);
//        GridLayoutManager manager = new GridLayoutManager(getContext(), 3);
//        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                if (position == 0 &&mAdapter.hasHeadView()) {
//                    return 3;
//                } else {
//                    return 1;
//                }
//            }
//        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
        recyclerView.addItemDecoration(divider);
        mAdapter = new RecommendContentAdapter(getContext(), null);
        recyclerView.setAdapter(mAdapter);
        ViewGroup secondColumn = (ViewGroup) inflater.inflate(R.layout.album_introduce_top, recyclerView, false);
        TextView introduction = secondColumn.findViewById(R.id.introduction);
        introduction.setText(mIntroduction);
        mAdapter.addHeaderView(secondColumn);
        getRelatedPosts();
        return view;
    }

    private void getRelatedPosts() {
        String url = ServerInfo.serviceIP + ServerInfo.getRelatedRecommend;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", "" + mAlbumId);
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_GET_RELATED_POST;
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
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_RELATED_POST: {
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mColumnContents = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class);
                        if (mAdapter == null) {
                            mAdapter = new RecommendContentAdapter(getContext(), mColumnContents);
                            mAdapter.setOnItemClickListener(new RecommendContentAdapter.ItemClickListener() {
                                @Override
                                public void onItemClick(int pos) {
                                }
                            });
                            //设置添加或删除item时的动画，这里使用默认动画
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            //设置适配器
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.setData(mColumnContents);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
        }
        return false;
    }
}
