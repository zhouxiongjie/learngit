package com.shuangling.software.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hjq.toast.ToastUtils;
import com.jaeger.library.StatusBarUtil;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.LineWrapLayout;
import com.shuangling.software.dao.SearchHistoryDaoUtils;
import com.shuangling.software.entity.SearchHistory;
import com.shuangling.software.fragment.SearchListFragment;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.StatusBarManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SearchActivity extends AppCompatActivity {

    public static final String TAG = "SearchActivity";

    private static final String[] searchCategory = new String[]{"全部", "音频", "资讯", "专辑", "电台","主播","视频","电视台","专题"};

    @BindView(R.id.searchCancel)
    TextView searchCancel;
    @BindView(R.id.keyword)
    EditText keyword;
    @BindView(R.id.clean)
    TextView clean;
    @BindView(R.id.historyList)
    LineWrapLayout historyList;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.tabPageIndicator)
    TabLayout tabPageIndicator;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.searchResult)
    LinearLayout searchResult;

    private FragmentAdapter mFragmentPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setTheme(MyApplication.getInstance().getCurrentTheme());
        setContentView(R.layout.activity_search);
        StatusBarUtil.setTransparent(this);
        StatusBarManager.setImmersiveStatusBar(this, true);
        ButterKnife.bind(this);

        init();

    }


    private void updateSearchHistory(){
        //加载历史记录
        List<SearchHistory> history =SearchHistoryDaoUtils.queryAll();
        List<String> data = new ArrayList<String>();
        for(int i=0;i<history.size();i++){
            data.add(history.get(i).getHistoryString()) ;
        }
        historyList.setData(data);
    }

    private void init() {
        mFragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mFragmentPagerAdapter);
        tabPageIndicator.setupWithViewPager(viewPager);

        //加载历史记录
        updateSearchHistory();
        historyList.setOnItemClickListener(new LineWrapLayout.OnItemClickListener() {
            @Override
            public void onClick(View view) {
                String keyword=((TextView) view).getText().toString();
                //开始搜索
                searchResult.setVisibility(View.VISIBLE);
                mFragmentPagerAdapter.notifyDataSetChanged();

            }
        });


        keyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String str=v.getText().toString();
                    if(str.length()>0){
                        //1.添加历史记录
                        //2.返回跟上一个页面搜索关键字
                        SearchHistory searchHistory;


                        List<SearchHistory> list=SearchHistoryDaoUtils.queryAll(str);
                        if(list.size()>0){
                            searchHistory=list.get(0);
                            searchHistory.setHistoryString(str);
                            searchHistory.setCreateTime(CommonUtils.getDateTimeString());
                        }else{
                            searchHistory=new SearchHistory();
                            searchHistory.setHistoryString(str);
                            searchHistory.setCreateTime(CommonUtils.getDateTimeString());
                        }
                        SearchHistoryDaoUtils.insertOrReplace(searchHistory);
                        //开始搜索
                        searchResult.setVisibility(View.VISIBLE);
                        mFragmentPagerAdapter.notifyDataSetChanged();

                    }else{
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
                if(TextUtils.isEmpty(s.toString())){
                    searchResult.setVisibility(View.GONE);
                    updateSearchHistory();
                }
            }
        });
    }


    @OnClick({R.id.searchCancel, R.id.clean})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.searchCancel:
                finish();
                break;
            case R.id.clean:
                SearchHistoryDaoUtils.cleanAll();

                List<SearchHistory> history =SearchHistoryDaoUtils.queryAll();
                List<String> data = new ArrayList<String>();
                for(int i=0;i<history.size();i++){
                    data.add(history.get(i).getHistoryString()) ;
                }
                historyList.setData(data);
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
            bundle.putInt("search_type", position);
            bundle.putString("keyword", keyword.getText().toString());
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return searchCategory[position];
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
