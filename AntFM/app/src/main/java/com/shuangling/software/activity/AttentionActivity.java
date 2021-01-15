package com.shuangling.software.activity;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
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

//@EnableDragToClose()
public class AttentionActivity extends QMUIActivity/*AppCompatActivity*/ {
    public static final String TAG = "HistoryActivity";
    public static final int[] category = new int[]{R.string.organization, R.string.anchor};
    @BindView(R.id.activtyTitle)
    /*TopTitleBar*/ QMUITopBarLayout activtyTitle;
    @BindView(R.id.tabPageIndicator)
    TabLayout tabPageIndicator;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.selectAll)
    TextView selectAll;
    @BindView(R.id.delete)
    TextView delete;
    @BindView(R.id.actionLayout)
    LinearLayout actionLayout;
    private FragmentAdapter mFragmentPagerAdapter;
    public int mCurrentItem;
    private boolean mEditorMode = false;

    public boolean isEditorMode() {
        return mEditorMode;
    }

    public void setEditorMode(boolean ditorMode) {
        this.mEditorMode = ditorMode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);
//        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this); //
        init();
    }

    private void init() {
        activtyTitle.addLeftImageButton(R.drawable.ic_left, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(view -> { //
            finish();
        });
        activtyTitle.setTitle("我的关注");
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

    public void cancelEditorMode() {
        setEditorMode(false);
        actionLayout.setVisibility(View.GONE);
        EventBus.getDefault().post("historyModeChange");
    }

    @OnClick({R.id.selectAll, R.id.delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.selectAll:
                //EventBus.getDefault().post("historySelectAll");
                break;
            case R.id.delete:
                //EventBus.getDefault().post("historySelectDelete");
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
