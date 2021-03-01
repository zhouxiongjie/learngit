package com.shuangling.software.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.fastjson.JSONObject;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.activity.HistoryActivity;
import com.shuangling.software.adapter.LiveRewardAdapter;
import com.shuangling.software.customview.MyViewPager;
import com.shuangling.software.entity.LiveRoomInfo01;
import com.shuangling.software.entity.RewardsInfo;
import com.shuangling.software.fragment.HistoryFragment;
import com.shuangling.software.fragment.HistoryRadioFragment;
import com.shuangling.software.fragment.LiveAwardFragment;
import com.shuangling.software.fragment.LiveAwardRankFragment;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ServerInfo;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;
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
    @BindView(R.id.tabPageIndicator)
    TabLayout tabPageIndicator;
    @BindView(R.id.viewPager)
    MyViewPager viewPager;

    public static final String[] category = new String[]{"打赏","排行"};

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




    public class FragmentAdapter extends FragmentStatePagerAdapter {


        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }


        @NonNull
        @Override
        public Fragment getItem(int position) {
            if(position==0){
                LiveAwardFragment fragment = new LiveAwardFragment(LiveAwardDialog.this);
                Bundle bundle = new Bundle();
                bundle.putSerializable("liveRoomInfo", liveRoomInfo);
                fragment.setArguments(bundle);
                return fragment;
            }else {
                LiveAwardRankFragment fragment = new LiveAwardRankFragment();
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


}
