package com.shuangling.software.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.google.android.material.tabs.TabLayout;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.adapter.LiveAwardDetailAdapter;
import com.shuangling.software.customview.MyViewPager;
import com.shuangling.software.entity.AwardGiftInfo;
import com.shuangling.software.entity.AwardInfo;
import com.shuangling.software.entity.LiveRoomInfo01;
import com.shuangling.software.fragment.LiveAwardFragment;
import com.shuangling.software.fragment.LiveAwardRankFragment;
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


/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class LiveAwardDialog extends BaseCircleDialog {


    Unbinder unbinder;


    public static final String[] category = new String[]{"打赏", "排行"};
    @BindView(R.id.tabPageIndicator)
    TabLayout tabPageIndicator;
    @BindView(R.id.viewPager)
    MyViewPager viewPager;
    @BindView(R.id.one)
    LinearLayout one;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noData)
    TextView noData;
    @BindView(R.id.two)
    RelativeLayout two;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.awardTitle)
    TextView awardTitle;

    private LiveRoomInfo01 liveRoomInfo;

    private FragmentAdapter mFragmentPagerAdapter;
    private OnChatEventListener mOnChatEventListener;

    public void setOnChatEventListener(OnChatEventListener onChatEventListener) {
        this.mOnChatEventListener = onChatEventListener;
    }

    public interface OnChatEventListener {
        void sendImage();

        void sendText(String str);
    }


    public static LiveAwardDialog getInstance(LiveRoomInfo01 roomInfo) {
        LiveAwardDialog viewerListDialog = new LiveAwardDialog();
        viewerListDialog.setCanceledBack(true);
        viewerListDialog.setCanceledOnTouchOutside(true);
        viewerListDialog.setGravity(Gravity.BOTTOM);
        viewerListDialog.setWidth(1f);
        viewerListDialog.liveRoomInfo = roomInfo;
        return viewerListDialog;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_live_award, container, false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    private void init() {

        //awardTitle.setTitle("账号与安全");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                two.setVisibility(View.GONE);
                one.setVisibility(View.VISIBLE);
            }
        });
        viewPager.setNoScroll(true);
        mFragmentPagerAdapter = new FragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(mFragmentPagerAdapter);
//        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabPageIndicator, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
//
//            @Override
//            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//                tab.setText(category[position]);
//            }
//
//        });
//        tabLayoutMediator.attach();
        tabPageIndicator.setupWithViewPager(viewPager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public void onRankItemClick(AwardInfo awardInfo) {
        one.setVisibility(View.GONE);
        two.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        awardTitle.setText(awardInfo.getNickname() + "的打赏详情");
        getAwardDetail(awardInfo);
    }

    public class FragmentAdapter extends FragmentStatePagerAdapter {


        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }


        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                LiveAwardFragment fragment = new LiveAwardFragment(LiveAwardDialog.this);
                Bundle bundle = new Bundle();
                bundle.putSerializable("liveRoomInfo", liveRoomInfo);
                fragment.setArguments(bundle);
                return fragment;
            } else {
                LiveAwardRankFragment fragment = new LiveAwardRankFragment(LiveAwardDialog.this);
                Bundle bundle = new Bundle();
                bundle.putSerializable("liveRoomInfo", liveRoomInfo.getRoom_info());
                fragment.setArguments(bundle);
                return fragment;
            }

        }

        @Override
        public int getCount() {
            return category.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return category[position];
        }
    }


    public void getAwardDetail(AwardInfo awardInfo) {
        String url = ServerInfo.live + "/v3/get_gift_ranking_details";
        Map<String, String> params = new HashMap<>();
        params.put("room_id", "" + liveRoomInfo.getRoom_info().getId());
        params.put("user_id", "" + awardInfo.getUser_id());
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        List<AwardGiftInfo> awardInfos = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), AwardGiftInfo.class);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                    LiveAwardDetailAdapter adapter = new LiveAwardDetailAdapter(getContext(), awardInfos);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setVisibility(View.VISIBLE);
//                                    if(awardInfos==null||awardInfos.size()==0){
//                                        noData.setVisibility(View.VISIBLE);
//                                    }
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


}
