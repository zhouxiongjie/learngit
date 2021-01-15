package com.shuangling.software.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hjq.toast.ToastUtils;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.event.MessageEvent;
import com.shuangling.software.fragment.FeedbackHistoryFragment;
import com.shuangling.software.utils.CommonUtils;
import com.youngfeng.snake.annotations.EnableDragToClose;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

//@EnableDragToClose()
public class FeedbackHistoryActivity extends QMUIActivity/*AppCompatActivity*/ implements Handler.Callback {
    public static final String TAG = "MyWalletsActivity";
    private static final int MSG_ACCOUNT_DETAIL = 0;
    public static final int[] category = new int[]{R.string.reply, R.string.pending_reply};
    @BindView(R.id.activtyTitle)
    /*TopTitleBar*/ QMUITopBarLayout activtyTitle;
    @BindView(R.id.tabPageIndicator)
    TabLayout tabPageIndicator;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private SimpleDraweeView noRead;
    private FragmentAdapter mFragmentPagerAdapter;
    public int mCurrentItem;
//    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_history);
//        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this); //
        activtyTitle.addLeftImageButton(R.drawable.ic_left, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(view -> { //
            finish();
        });
        activtyTitle.setTitle("反馈记录");
        init();
    }

    private void init() {
//        mHandler = new Handler(this);
        EventBus.getDefault().register(this);
        mFragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mFragmentPagerAdapter);
        tabPageIndicator.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                mCurrentItem = i;
                if (mCurrentItem == 0) {
//                    mAuditPending.setSelected(true);
//                    mOnceMore.setVisibility(View.GONE);
                } else {
//                    mAuditPending.setSelected(false);
//                    mOnceMore.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
//setupTabOneIcon();
    }

    private void setupTabOneIcon() {
        tabPageIndicator.getTabAt(0).setCustomView(R.layout.feedback_reply_table);
        noRead = tabPageIndicator.getTabAt(0).getCustomView().findViewById(R.id.noRead);
        noRead.setVisibility(View.GONE);
        //mNoReadNumber.setText("4");
    }

    @Subscribe
    public void getEventBus(MessageEvent msg) {
        if (msg.getEventName().equals("hasNoRead")) {
            //noRead.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("OnMoneyChanged")) {
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_ACCOUNT_DETAIL:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    } else if (jsonObject != null) {
                        ToastUtils.show(jsonObject.getString("msg"));
                    }
                } catch (Exception e) {
                }
                break;
        }
        return false;
    }

    public class FragmentAdapter extends FragmentStatePagerAdapter {
        //private FragmentManager fm;
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
            //this.fm = fm;
        }

        @Override
        public int getCount() {
            return category.length;
        }

        @Override
        public Fragment getItem(int position) {
            FeedbackHistoryFragment fragment = new FeedbackHistoryFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("category", category[position]);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getString(category[position]);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            return fragment;
        }
    }
}
