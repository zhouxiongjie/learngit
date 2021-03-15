package com.shuangling.software.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.R;
import com.shuangling.software.adapter.LiveAwardRankAdapter;
import com.shuangling.software.adapter.LiveRewardAdapter;
import com.shuangling.software.dialog.LiveAwardDialog;
import com.shuangling.software.entity.AwardInfo;
import com.shuangling.software.entity.LiveRoomInfo01;
import com.shuangling.software.entity.RewardsInfo;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
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

public class LiveAwardRankFragment extends Fragment {


    Unbinder unbinder;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noData)
    TextView noData;


    private LiveRoomInfo01.RoomInfoBean liveRoomInfo;

    private LiveAwardDialog mDialog;
    public LiveAwardRankFragment(LiveAwardDialog dialog){
        mDialog=dialog;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle args = getArguments();
        liveRoomInfo = (LiveRoomInfo01.RoomInfoBean) args.getSerializable("liveRoomInfo");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.fragment_award_rank, null);
        unbinder = ButterKnife.bind(this, view);
        getRankList();
        return view;
    }



    public void getRankList() {
        String url = ServerInfo.live + "/v3/get_gift_ranking";
        Map<String, String> params = new HashMap<>();
        params.put("room_id",""+liveRoomInfo.getId());
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        List<AwardInfo> awardInfos = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), AwardInfo.class);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                    LiveAwardRankAdapter adapter=new LiveAwardRankAdapter(getContext(), awardInfos);
                                    adapter.setOnItemClick(new LiveAwardRankAdapter.OnItemClick() {
                                        @Override
                                        public void ItemClick(AwardInfo awardInfo) {
                                            mDialog.onRankItemClick(awardInfo);
                                        }
                                    });
                                    recyclerView.setAdapter(adapter);
                                    if(awardInfos==null||awardInfos.size()==0){
                                        noData.setVisibility(View.VISIBLE);
                                    }
                                } catch (Exception e) {

                                }

                            }
                        });


                    } else {
                        //ToastUtils.show("获取观众列表失败");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test", exception.toString());

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(String str) {

    }

//    @OnClick({R.id.send, R.id.sub, R.id.add})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.send:
//
//                break;
//            case R.id.sub:
//
//                break;
//            case R.id.add:
//
//                break;
//        }
//    }



}
