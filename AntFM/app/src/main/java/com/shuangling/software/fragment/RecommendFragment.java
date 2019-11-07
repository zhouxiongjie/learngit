package com.shuangling.software.fragment;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.Station;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Response;


public class RecommendFragment extends Fragment implements OnClickListener, Handler.Callback {

    public static final int MSG_GET_COLUMN = 0x1;
    @BindView(R.id.search)
    TextView search;
    @BindView(R.id.columnContent)
    LinearLayout columnContent;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    Unbinder unbinder;
    @BindView(R.id.columnScrollView)
    HorizontalScrollView columnScrollView;
    @BindView(R.id.topBar)
    LinearLayout topBar;

    /**
     * 当前选中的栏目
     */
    private int mColumnSelectIndex = 0;
    private List<Column> mColumns;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    /**
     * 屏幕宽度
     */
    private int mScreenWidth = 0;
    private Handler mHandler;

    private Column mSwitchColumn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScreenWidth = CommonUtils.getScreenWidth();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mSwitchColumn = (Column)bundle.getSerializable("column");
        }
        mHandler = new Handler(this);
    }


    public void switchColumn(Column switchColumn) {

        for(int i=0;i<mColumns.size();i++){
            if(mColumns.get(i).getId()==switchColumn.getId()){
                mColumnSelectIndex =i;
                break;
            }
        }
        viewPager.setCurrentItem(mColumnSelectIndex);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        getRecommendColumns();
        unbinder = ButterKnife.bind(this, view);
        return view;

    }


    private void initFragment() {
        mFragments.clear();
        if (mColumns != null && mColumns.size() > 0) {
            MyselfFragmentPagerAdapter mAdapetr = new MyselfFragmentPagerAdapter(getChildFragmentManager(), mColumns);
            viewPager.setAdapter(mAdapetr);
            viewPager.addOnPageChangeListener(mPageListener);
            //viewPager.setCurrentItem(mColumnSelectIndex);
        }

        if(mSwitchColumn!=null){
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    switchColumn(mSwitchColumn);
                }
            },500);
        }

    }




    public ViewPager.OnPageChangeListener mPageListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            //viewPager.setCurrentItem(position);

            for (int i = 0; i < columnContent.getChildCount(); i++) {
                View checkView = columnContent.getChildAt(i);
                TextView columnTextView = checkView.findViewById(R.id.text);
                SimpleDraweeView indicator = checkView.findViewById(R.id.indicator);
                boolean ischeck;
                if (i == position) {
                    ischeck = true;
                    indicator.setVisibility(View.VISIBLE);
                    columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    if (checkView.getLeft() > mScreenWidth / 2) {
                        columnScrollView.scrollTo((int) checkView.getLeft() - mScreenWidth / 2, 0);
                    }
                    mColumnSelectIndex = position;
//                    Station station=MyApplication.getInstance().getStation();
//                    if(station!=null&&!TextUtils.isEmpty(station.getIcon1())){
//                        ViewGroup.LayoutParams lp=indicator.getLayoutParams();
//                        lp.width=CommonUtils.dip2px(20);
//                        lp.height=lp.width/2;
//                        indicator.setLayoutParams(lp);
//
//                        Uri uri = Uri.parse(station.getIcon1());
//                        int width=CommonUtils.dip2px(20);
//                        int height=width/2;
//                        ImageLoader.showThumb(uri,indicator,width,height);
//                    }else{
//                        ViewGroup.LayoutParams lp=indicator.getLayoutParams();
//                        lp.width=CommonUtils.dip2px(20);
//                        lp.height=CommonUtils.dip2px(2);
//                        indicator.setLayoutParams(lp);
//                        ImageLoader.showThumb(indicator,R.drawable.indicator_line_bg);
//                    }

                } else {
                    ischeck = false;
                    indicator.setVisibility(View.INVISIBLE);
                    columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                }
                columnTextView.setSelected(ischeck);
            }
        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            default:
                break;
        }
    }


    public void getRecommendColumns() {

        String url = ServerInfo.serviceIP + ServerInfo.getRecommendColumns;
        Map<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("page_size", "" + Integer.MAX_VALUE);
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_COLUMN;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });


    }


    public class MyselfFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private List<Column> mColumns;
        private FragmentManager fm;

        public MyselfFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public MyselfFragmentPagerAdapter(FragmentManager fm, List<Column> columns) {
            super(fm);
            this.fm = fm;
            mColumns = columns;

        }

        @Override
        public int getCount() {
            return mColumns.size();
        }

        @Override
        public Fragment getItem(int position) {
            Bundle data = new Bundle();
            data.putSerializable("Column",mColumns.get(position));
            if(mColumns.get(position).getType()==1){
                ContentHotFragment contentFragment = new ContentHotFragment();
                contentFragment.setArguments(data);
                return contentFragment;
            }else{
                ContentFragment contentFragment = new ContentFragment();
                contentFragment.setArguments(data);
                return contentFragment;
            }

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
////移除旧的fragment
//            ft.remove(fragment);
////换成新的fragment
//            fragment = getItem(position);
////添加新fragment时必须用前面获得的tag，这点很重要
//            ft.add(container.getId(), fragment, fragmentTag);
//            ft.attach(fragment);
//            ft.commit();
            return fragment;
        }

    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_COLUMN: {
                String result = (String) msg.obj;
                try {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        JSONObject jo = jsonObject.getJSONObject("data");
                        if (jo.getInteger("total") > 0) {
                            mColumns = JSONObject.parseArray(jo.getJSONArray("data").toJSONString(), Column.class);

                            for (int i = 0; mColumns != null && i < mColumns.size(); i++) {
                                Column column = mColumns.get(i);
                                //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                //params.leftMargin = 20;
                                //params.rightMargin = 20;
                                LayoutInflater inflater = getLayoutInflater();
                                View view = inflater.inflate(R.layout.column_txt_layout, columnContent, false);
                                TextView columnTextView = view.findViewById(R.id.text);
                                SimpleDraweeView indicator = view.findViewById(R.id.indicator);
                                //columnTextView.setGravity(Gravity.CENTER);
                                //columnTextView.setPadding(40, 20, 40, 20);

                                //columnTextView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                                columnTextView.setText(column.getName());
                                columnTextView.setTextColor(getActivity().getResources().getColorStateList(R.color.column_item_selector));




                                if (mColumnSelectIndex == i) {
                                    columnTextView.setSelected(true);
                                    columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                                    columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                                    indicator.setVisibility(View.VISIBLE);
//                                    Station station=MyApplication.getInstance().getStation();
//                                    if(station!=null&&!TextUtils.isEmpty(station.getIcon1())){
//                                        ViewGroup.LayoutParams lp=indicator.getLayoutParams();
//                                        lp.width=CommonUtils.dip2px(20);
//                                        lp.height=lp.width/2;
//                                        indicator.setLayoutParams(lp);
//
//                                        Uri uri = Uri.parse(station.getIcon1());
//                                        int width=CommonUtils.dip2px(20);
//                                        int height=width/2;
//                                        ImageLoader.showThumb(uri,indicator,width,height);
//                                    }else{
//                                        ViewGroup.LayoutParams lp=indicator.getLayoutParams();
//                                        lp.width=CommonUtils.dip2px(20);
//                                        lp.height=CommonUtils.dip2px(2);
//                                        indicator.setLayoutParams(lp);
//                                        ImageLoader.showThumb(indicator,R.drawable.indicator_line_bg);
//                                    }

                                } else {
                                    columnTextView.setSelected(false);
                                    columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                                    columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                                    indicator.setVisibility(View.INVISIBLE);
                                }
                                view.setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        for (int i = 0; i < columnContent.getChildCount(); i++) {
                                            View localView = columnContent.getChildAt(i);
                                            TextView columnTextView = localView.findViewById(R.id.text);
                                            SimpleDraweeView indicator = localView.findViewById(R.id.indicator);
                                            if (localView != v) {
                                                columnTextView.setSelected(false);
                                                indicator.setVisibility(View.INVISIBLE);

                                                columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                                                columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                                            } else {
                                                mColumnSelectIndex = i;
                                                columnTextView.setSelected(true);
                                                indicator.setVisibility(View.VISIBLE);
//                                                Station station=MyApplication.getInstance().getStation();
//                                                if(station!=null&&!TextUtils.isEmpty(station.getIcon1())){
//                                                    Uri uri = Uri.parse(station.getIcon1());
//                                                    int width=CommonUtils.dip2px(15);
//                                                    int height=width;
//                                                    ImageLoader.showThumb(uri,indicator,width,height);
//                                                }
                                                columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                                                columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                                                viewPager.setCurrentItem(mColumnSelectIndex);
                                            }
                                        }
                                    }
                                });
                                columnContent.addView(view, i);
                            }

                            initFragment();
                        }
                    } else {

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
