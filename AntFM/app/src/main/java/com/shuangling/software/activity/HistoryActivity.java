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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.fragment.HistoryFragment;
import com.shuangling.software.fragment.HistoryRadioFragment;
import com.shuangling.software.utils.CommonUtils;
import com.youngfeng.snake.annotations.EnableDragToClose;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@EnableDragToClose()
public class HistoryActivity extends AppCompatActivity {

    public static final String TAG = "HistoryActivity";

    public static final int MSG_FEED_BACK = 0x1;

    public static final int[] category = new int[]{R.string.radio, R.string.tv, R.string.article, R.string.audio, R.string.video, R.string.special, R.string.photo};
    @BindView(R.id.activtyTitle)
    TopTitleBar activtyTitle;
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

        setContentView(R.layout.activity_history);
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
                mCurrentItem=i;
                if(activtyTitle.getMoreText().equals("取消")){
                    activtyTitle.setMoreText("删除");
                    setEditorMode(false);
                    actionLayout.setVisibility(View.GONE);
                    EventBus.getDefault().post("historyModeChange");
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        activtyTitle.setMoreAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activtyTitle.getMoreText().equals("删除")){
                    activtyTitle.setMoreText("取消");
                    setEditorMode(true);
                    actionLayout.setVisibility(View.VISIBLE);
                    EventBus.getDefault().post("historyModeChange");
                }else {
                    activtyTitle.setMoreText("删除");
                    setEditorMode(false);
                    actionLayout.setVisibility(View.GONE);
                    EventBus.getDefault().post("historyModeChange");
                }
            }
        });
    }

    public void cancelEditorMode(){
        activtyTitle.setMoreText("删除");
        setEditorMode(false);
        actionLayout.setVisibility(View.GONE);
        EventBus.getDefault().post("historyModeChange");
    }


    @OnClick({R.id.selectAll, R.id.delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.selectAll:
                EventBus.getDefault().post("historySelectAll");
                break;
            case R.id.delete:
                EventBus.getDefault().post("historySelectDelete");
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

            if (category[position] == R.string.radio || category[position] == R.string.tv) {
                HistoryRadioFragment fragment = new HistoryRadioFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("category", category[position]);
                fragment.setArguments(bundle);
                return fragment;
            } else {
                HistoryFragment fragment = new HistoryFragment();
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
