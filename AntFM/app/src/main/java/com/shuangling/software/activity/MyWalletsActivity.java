package com.shuangling.software.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSONObject;
import com.google.android.material.tabs.TabLayout;
import com.hjq.toast.ToastUtils;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.entity.AccountInfo;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.fragment.CashIncomeFragment;
import com.shuangling.software.fragment.TakeCashFragment;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ServerInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

//@EnableDragToClose()
public class MyWalletsActivity extends QMUIActivity/*AppCompatActivity*/ implements Handler.Callback {
    public static final String TAG = "MyWalletsActivity";
    private static final int MSG_ACCOUNT_DETAIL = 0;
    public static final int[] category = new int[]{R.string.income, R.string.cash_record};
    @BindView(R.id.activity_title)
    /*TopTitleBar*/ QMUITopBarLayout activity_title;
    @BindView(R.id.money)
    TextView money;
    @BindView(R.id.cash)
    TextView cash;
    @BindView(R.id.tabPageIndicator)
    TabLayout tabPageIndicator;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private FragmentAdapter mFragmentPagerAdapter;
    public int mCurrentItem;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallets);
//        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this); //
        activity_title.addLeftImageButton(R.drawable.ic_left, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(view -> { //
            finish();
        });
        activity_title.setTitle("我的钱包");
        init();
    }

    private void init() {
        mHandler = new Handler(this);
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
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        getAccountDetail();
    }

    private void getAccountDetail() {
        String url = ServerInfo.emc + ServerInfo.accountDetail;
        Map<String, String> params = new HashMap<String, String>();
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = mHandler.obtainMessage(MSG_ACCOUNT_DETAIL);
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("OnMoneyChanged")) {
            getAccountDetail();
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
                        final AccountInfo accountInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), AccountInfo.class);
                        if (accountInfo != null) {
                            money.setText(String.format("%.2f", (float) accountInfo.getFree_balance() / 100));
                            cash.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it = new Intent(MyWalletsActivity.this, CashActivity.class);
                                    it.putExtra("money", accountInfo.getFree_balance());
                                    startActivity(it);
                                }
                            });
                        }
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
            if (position == 0) {
                CashIncomeFragment fragment = new CashIncomeFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("category", category[position]);
                fragment.setArguments(bundle);
                return fragment;
            } else {
                TakeCashFragment fragment = new TakeCashFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("category", category[position]);
                fragment.setArguments(bundle);
                return fragment;
            }
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
