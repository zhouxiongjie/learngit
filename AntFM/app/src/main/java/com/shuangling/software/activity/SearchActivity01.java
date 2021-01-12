package com.shuangling.software.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hjq.toast.ToastUtils;
import com.jaeger.library.StatusBarUtil;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.SearchHistoryGridViewAdapter;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.dao.SearchHistoryDaoUtils;
import com.shuangling.software.entity.SearchHistory;
import com.shuangling.software.fragment.SearchListFragment;
import com.shuangling.software.utils.CommonUtils;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//import com.google.android.material.tabs.TabLayout;
//import androidx.fragment.app.Fragment;
//import androidx.appcompat.app.AppCompatActivity;

@EnableDragToClose()
public class SearchActivity01 extends AppCompatActivity {
    public static final String TAG = "SearchActivity";
    private static final int[] searchCategory = new int[]{R.string.all, R.string.article, R.string.video, R.string.little_video, R.string.audio, R.string.album, R.string.photo, R.string.special, R.string.organization, R.string.anchor, R.string.tv, R.string.radio};
    @BindView(R.id.searchCancel)
    TextView searchCancel;
    @BindView(R.id.keyword)
    EditText keyword;
    @BindView(R.id.clean)
    TextView clean;
    //    @BindView(R.id.historyList)
//    LineWrapLayout historyList;
//    @BindView(R.id.scrollView)
//    ScrollView scrollView;
    @BindView(R.id.tabPageIndicator)
    TabLayout tabPageIndicator;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.searchResult)
    LinearLayout searchResult;
    @BindView(R.id.expand)
    FontIconView expand;
    @BindView(R.id.deleteAll)
    TextView deleteAll;
    @BindView(R.id.finish)
    TextView finish;
    @BindView(R.id.historyText)
    TextView historyText;
    @BindView(R.id.gridView)
    GridView gridView;
    @BindView(R.id.cleanLayout)
    LinearLayout cleanLayout;

    private FragmentAdapter mFragmentPagerAdapter;
    private List<String> mHistory = new ArrayList<>();
    private SearchHistoryGridViewAdapter mSearchHistoryGridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search01);
        StatusBarUtil.setTransparent(this);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        init();
    }

    private void updateSearchHistory() {
        //加载历史记录
        List<SearchHistory> history = SearchHistoryDaoUtils.queryAll();
        mHistory.clear();
        for (int i = 0; i < history.size(); i++) {
            mHistory.add(history.get(i).getHistoryString());
        }

        if (mHistory.size() > 10) {
            expand.setVisibility(View.VISIBLE);
        } else {
            expand.setVisibility(View.GONE);
        }

        if (mSearchHistoryGridViewAdapter == null) {
            mSearchHistoryGridViewAdapter = new SearchHistoryGridViewAdapter(this, mHistory);
            gridView.setAdapter(mSearchHistoryGridViewAdapter);
            mSearchHistoryGridViewAdapter.setOnItemClickListener(new SearchHistoryGridViewAdapter.OnItemClickListener() {
                @Override
                public void onDelete(String history) {
                    SearchHistoryDaoUtils.deleteHistory(history);
                    updateSearchHistory();

                }

                @Override
                public void onItemClick(String history) {
                    CommonUtils.hideInput(SearchActivity01.this);
                    keyword.clearFocus();
                    //开始搜索
                    keyword.setText(history);
                    searchResult.setVisibility(View.VISIBLE);
                    mFragmentPagerAdapter.notifyDataSetChanged();
                }
            });
        } else {
            mSearchHistoryGridViewAdapter.updateView(mHistory);
        }
        //historyList.setData(data);
    }

    private void init() {
        mFragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mFragmentPagerAdapter);
        tabPageIndicator.setupWithViewPager(viewPager);
//加载历史记录
        updateSearchHistory();

        historyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchHistoryGridViewAdapter != null && expand.getVisibility() == View.VISIBLE) {
                    if (mSearchHistoryGridViewAdapter.isExpand()) {
                        mSearchHistoryGridViewAdapter.setExpand(false);
                        expand.setText(R.string.arrow_down01);
                    } else {
                        mSearchHistoryGridViewAdapter.setExpand(true);
                        expand.setText(R.string.arrow_up01);
                    }
                }
            }
        });
        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchHistoryGridViewAdapter != null && expand.getVisibility() == View.VISIBLE) {
                    if (mSearchHistoryGridViewAdapter.isExpand()) {
                        mSearchHistoryGridViewAdapter.setExpand(false);
                        expand.setText(R.string.arrow_down01);
                    } else {
                        mSearchHistoryGridViewAdapter.setExpand(true);
                        expand.setText(R.string.arrow_up01);
                    }
                }
            }
        });

        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSearchHistoryGridViewAdapter!=null){
                    mSearchHistoryGridViewAdapter.setIsEditor(true);
                }
                clean.setVisibility(View.GONE);
                cleanLayout.setVisibility(View.VISIBLE);
            }
        });

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchHistoryDaoUtils.cleanAll();
                updateSearchHistory();
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clean.setVisibility(View.VISIBLE);
                cleanLayout.setVisibility(View.GONE);
                if(mSearchHistoryGridViewAdapter!=null){
                    mSearchHistoryGridViewAdapter.setIsEditor(false);
                }
            }
        });


        keyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    CommonUtils.hideInput(SearchActivity01.this);
                    keyword.clearFocus();
                    String str = v.getText().toString();
                    if (str.length() > 0) {
                        //1.添加历史记录
                        //2.返回跟上一个页面搜索关键字
                        SearchHistory searchHistory;
                        List<SearchHistory> list = SearchHistoryDaoUtils.queryAll(str);
                        if (list.size() > 0) {
                            searchHistory = list.get(0);
                            searchHistory.setHistoryString(str);
                            searchHistory.setCreateTime(CommonUtils.getDateTimeString());
                        } else {
                            searchHistory = new SearchHistory();
                            searchHistory.setHistoryString(str);
                            searchHistory.setCreateTime(CommonUtils.getDateTimeString());
                        }
                        SearchHistoryDaoUtils.insertOrReplace(searchHistory);
                        //开始搜索
                        searchResult.setVisibility(View.VISIBLE);
                        mFragmentPagerAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtils.show("请输入搜索关键字");
                    }
                    return true;
                }
                return false;
            }
        });
        keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    searchResult.setVisibility(View.GONE);
                    updateSearchHistory();
                }
            }
        });
        keyword.requestFocus();
    }

    @OnClick({R.id.searchCancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.searchCancel:
                CommonUtils.hideInput(this);
                finish();
                break;
        }
    }

    public class FragmentAdapter extends FragmentStatePagerAdapter {
        private FragmentManager fm;

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public int getCount() {
            return searchCategory.length;
        }

        @Override
        public Fragment getItem(int position) {
            SearchListFragment fragment = new SearchListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("search_type", searchCategory[position]);
            bundle.putString("keyword", keyword.getText().toString());
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getString(searchCategory[position]);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            //得到缓存的fragment
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            //得到tag，这点很重要
//            String fragmentTag = fragment.getTag();
//            FragmentTransaction ft = fm.beginTransaction();
//            //移除旧的fragment
//            ft.remove(fragment);
//            //换成新的fragment
//            fragment = getItem(position);
//            //添加新fragment时必须用前面获得的tag，这点很重要
//            ft.add(container.getId(), fragment, fragmentTag);
//            ft.attach(fragment);
//            ft.commit();
            return fragment;
        }
    }
}
