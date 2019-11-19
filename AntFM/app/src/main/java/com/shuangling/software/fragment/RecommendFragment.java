package com.shuangling.software.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.activity.CityListActivity;
import com.shuangling.software.activity.MainActivity;
import com.shuangling.software.activity.SearchActivity;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.Weather;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;


public class RecommendFragment extends Fragment implements Handler.Callback {

    public static final int MSG_GET_COLUMN = 0x1;
    public static final int MSG_GET_CITY_WEATHER = 0x2;
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
    @BindView(R.id.city)
    TextView city;
    @BindView(R.id.divide)
    TextView divide;
    @BindView(R.id.temperature)
    TextView temperature;
    @BindView(R.id.weather)
    TextView weather;
    @BindView(R.id.weatherLayout)
    RelativeLayout weatherLayout;
    @BindView(R.id.logo1)
    SimpleDraweeView logo1;

    /**
     * 当前选中的栏目
     */
    private int mColumnSelectIndex = 0;
    public List<Column> mColumns;
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
            mSwitchColumn = (Column) bundle.getSerializable("column");
        }
        mHandler = new Handler(this);
        EventBus.getDefault().register(this);
    }


    public void switchColumn(Column switchColumn) {

        for (int i = 0; i < mColumns.size(); i++) {
            if (mColumns.get(i).getId() == switchColumn.getId()) {
                mColumnSelectIndex = i;
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
        if (MyApplication.getInstance().getStation() != null && MyApplication.getInstance().getStation().getIs_league() == 0) {
            //city.setCompoundDrawables(null, null, null, null);
            weatherLayout.setVisibility(View.GONE);
            logo1.setVisibility(View.VISIBLE);
            if (MyApplication.getInstance().getStation() != null && !TextUtils.isEmpty(MyApplication.getInstance().getStation().getLogo1())){
                Uri uri = Uri.parse(MyApplication.getInstance().getStation().getLogo1());
                ImageLoader.showThumb(uri,logo1,CommonUtils.dip2px(110),CommonUtils.dip2px(18));
            }
        }
        if (MainActivity.sCurrentCity != null) {
            city.setText(MainActivity.sCurrentCity.getName());
        }
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

        if (mSwitchColumn != null) {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    switchColumn(mSwitchColumn);
                }
            }, 500);
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
            if (position == 0) {
                if (MyApplication.getInstance().getStation() != null && MyApplication.getInstance().getStation().getIs_league() == 0) {
                    weatherLayout.setVisibility(View.GONE);
                    logo1.setVisibility(View.VISIBLE);
                }else{
                    weatherLayout.setVisibility(View.VISIBLE);
                    logo1.setVisibility(View.GONE);
                }
            } else {
                weatherLayout.setVisibility(View.GONE);
                logo1.setVisibility(View.GONE);
            }

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


    @OnClick({R.id.city, R.id.search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.city:
                if (MyApplication.getInstance().getStation() != null && MyApplication.getInstance().getStation().getIs_league() == 1) {
                    //固定
                    startActivity(new Intent(getContext(), CityListActivity.class));
                }

                break;
            case R.id.search:
                startActivity(new Intent(getContext(), SearchActivity.class));

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


    public void weather() {

        String url = ServerInfo.serviceIP + ServerInfo.weather;
        Map<String, String> params = new HashMap<String, String>();
        params.put("location", "" + MainActivity.sCurrentCity.getCode() + "00");


        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                Message msg = Message.obtain();
                msg.what = MSG_GET_CITY_WEATHER;
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
        if (event.getEventName().equals("onLocationChanged")) {
            city.setText(MainActivity.sCurrentCity.getName());
            weather();

        }
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
            data.putSerializable("Column", mColumns.get(position));
            if (mColumns.get(position).getType() == -1) {
                //首页
                IndexFragment indexFragment = new IndexFragment();
                indexFragment.setArguments(data);
                return indexFragment;
            } else if (mColumns.get(position).getType() == 1) {
                ContentHotFragment contentFragment = new ContentHotFragment();
                contentFragment.setArguments(data);
                return contentFragment;
            } else {
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

                            Column col = new Column();
                            col.setName("首页");
                            col.setType(-1);
                            if (mColumns != null) {
                                mColumns.add(0, col);
                            } else {
                                mColumns = new ArrayList<>();
                                mColumns.add(col);
                            }
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
            case MSG_GET_CITY_WEATHER:
                try {
                    String result = (String) msg.obj;
                    JSONObject jo = JSONObject.parseObject(result);
                    if (jo.getIntValue("code") == 100000 && jo.getJSONObject("data") != null) {
                        Weather wea = JSONObject.parseObject(jo.getJSONObject("data").toJSONString(), Weather.class);
                        if (wea.getWeather() != null && wea.getWeather().getHeWeather6() != null && wea.getWeather().getHeWeather6().get(0) != null && wea.getWeather().getHeWeather6().get(0).getNow() != null) {
                            temperature.setText(wea.getWeather().getHeWeather6().get(0).getNow().getTmp() + "℃");
                            weather.setText(wea.getWeather().getHeWeather6().get(0).getNow().getCond_txt());
                        }

                    }
                } catch (Exception e) {

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

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
