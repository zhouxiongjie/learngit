package com.shuangling.software.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.fragment.AttentionFragment;
import com.shuangling.software.utils.CommonUtils;
import com.youngfeng.snake.annotations.EnableDragToClose;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@EnableDragToClose()
public class MyWalletsActivity extends AppCompatActivity {

    public static final String TAG = "MyWalletsActivity";


    public static final int[] category = new int[]{R.string.income, R.string.cash_record};
    @BindView(R.id.activtyTitle)
    TopTitleBar activtyTitle;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_wallets);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        init();
    }


    private void init() {
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

    }



    @OnClick({R.id.cash})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cash:
                //EventBus.getDefault().post("historySelectAll");
                break;

        }
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


            AttentionFragment fragment = new AttentionFragment();
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
