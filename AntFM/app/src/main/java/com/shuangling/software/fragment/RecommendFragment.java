package com.shuangling.software.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.components.SimpleImmersionFragment;
import com.kcrason.dynamicpagerindicatorlibrary.DynamicPagerIndicator;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.activity.ArticleDetailActivity;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.activity.CityListActivity;
import com.shuangling.software.activity.CluesActivity;
import com.shuangling.software.activity.ContentActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.activity.LoginActivity;
import com.shuangling.software.activity.MainActivity;
import com.shuangling.software.activity.RadioDetailActivity;
import com.shuangling.software.activity.RadioListActivity;
import com.shuangling.software.activity.SearchActivity;
import com.shuangling.software.activity.SpecialDetailActivity;
import com.shuangling.software.activity.TvDetailActivity;
import com.shuangling.software.activity.VideoDetailActivity;
import com.shuangling.software.activity.WebViewActivity;
import com.shuangling.software.activity.WebViewBackActivity;
import com.shuangling.software.dialog.CustomColumnDialog;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.User;
import com.shuangling.software.entity.Weather;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;


public class RecommendFragment extends SimpleImmersionFragment implements Handler.Callback {

    public static final int MSG_GET_COLUMN = 0x1;
    public static final int MSG_GET_CITY_WEATHER = 0x2;
    @BindView(R.id.search)
    TextView search;
//    @BindView(R.id.columnContent)
//    LinearLayout columnContent;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    Unbinder unbinder;
//    @BindView(R.id.columnScrollView)
//    HorizontalScrollView columnScrollView;
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
    @BindView(R.id.customColumn)
    ImageView customColumn;
    @BindView(R.id.topBackground)
    SimpleDraweeView topBackground;
    @BindView(R.id.topBar)
    RelativeLayout topBar;
    @BindView(R.id.statusBar)
    View statusBar;
    @BindView(R.id.pagerIndicator)
    DynamicPagerIndicator pagerIndicator;


    /**
     * 当前选中的栏目
     */
    private int mColumnSelectIndex = 0;
    public List<Column> mColumns;
    public List<Column> mRemoteColumns;

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    /**
     * 屏幕宽度
     */
    private int mScreenWidth = 0;

    private Handler mHandler;

    private Column mSwitchColumn;
    private MyselfFragmentPagerAdapter mMyselfFragmentPagerAdapter;

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
        int columnIndex = 0;
        for (int i = 0; mColumns != null && i < mColumns.size(); i++) {
            if (mColumns.get(i).getId() == switchColumn.getId()) {
                columnIndex = i;
                break;
            }
        }
        viewPager.setCurrentItem(columnIndex);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recommend, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        if (!TextUtils.isEmpty(MyApplication.getInstance().getBackgroundImage())) {
            Uri uri = Uri.parse(MyApplication.getInstance().getBackgroundImage());
            topBackground.setImageURI(uri);
        }
        if (TextUtils.isEmpty(SharedPreferencesUtils.getStringValue("custom_column", null))) {
            getRecommendColumns(0);
        } else {
            mColumns = JSONObject.parseArray(SharedPreferencesUtils.getStringValue("custom_column", null), Column.class);
//            for (int i = 0; mColumns != null && i < mColumns.size(); i++) {
//                Column column = mColumns.get(i);
//                View view = inflater.inflate(R.layout.column_txt_layout, columnContent, false);
//                TextView columnTextView = view.findViewById(R.id.text);
//                //SimpleDraweeView indicator = view.findViewById(R.id.indicator);
//                columnTextView.setText(column.getName());
//                columnTextView.setTextColor(getActivity().getResources().getColorStateList(R.color.column_item_selector));
//
//
//                if (mColumnSelectIndex == i) {
//                    columnTextView.setSelected(true);
//                    columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
//                    columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//                    //indicator.setVisibility(View.VISIBLE);
//
//                } else {
//                    columnTextView.setSelected(false);
//                    columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//                    columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//                    //indicator.setVisibility(View.INVISIBLE);
//                }
//                view.setOnClickListener(new OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        for (int i = 0; i < columnContent.getChildCount(); i++) {
//                            View localView = columnContent.getChildAt(i);
//                            TextView columnTextView = localView.findViewById(R.id.text);
//                            //SimpleDraweeView indicator = localView.findViewById(R.id.indicator);
//                            if (localView != v) {
//                                columnTextView.setSelected(false);
//                                //indicator.setVisibility(View.INVISIBLE);
//
//                                columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//                                columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//                            } else {
//                                //mColumnSelectIndex = i;
//                                columnTextView.setSelected(true);
//                                //indicator.setVisibility(View.VISIBLE);
//                                columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
//                                columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//
//                                viewPager.setCurrentItem(i);
//                            }
//                        }
//                    }
//                });
//                columnContent.addView(view, i);
//            }
            initFragment();
            getRecommendColumns(1);


        }


//        if (!TextUtils.isEmpty(MyApplication.getInstance().getBackgroundImage())) {
//            Uri uri = Uri.parse(MyApplication.getInstance().getBackgroundImage());
//        }
        if (MyApplication.getInstance().getStation() != null && MyApplication.getInstance().getStation().getIs_league() == 0) {
            //city.setCompoundDrawables(null, null, null, null);
            weatherLayout.setVisibility(View.GONE);
            logo1.setVisibility(View.VISIBLE);
            if (MyApplication.getInstance().getStation() != null && !TextUtils.isEmpty(MyApplication.getInstance().getStation().getLogo1())) {
                Uri uri = Uri.parse(MyApplication.getInstance().getStation().getLogo1());
                ImageLoader.showThumb(uri, logo1, CommonUtils.dip2px(110), CommonUtils.dip2px(18));
            }
        }
        if (MainActivity.sCurrentCity != null) {
            city.setText(MainActivity.sCurrentCity.getName());
        }

//        topBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//
//                int height = topBar.getHeight();
//                Log.i("topBarHeight",""+height);
//                if(height==mTopBarHeight){
//                    topBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    ViewGroup.LayoutParams lp = topBackground.getLayoutParams();
//                    lp.height = height;
//                    topBackground.setLayoutParams(lp);
//                }
//                mTopBarHeight=height;
//
//            }
//        });
//        topBar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                int height = topBar.getHeight();
//                return false;
//            }
//        });
        return rootView;

    }


    private void initFragment() {
        mFragments.clear();
        if (mColumns != null && mColumns.size() > 0) {
            mMyselfFragmentPagerAdapter = new MyselfFragmentPagerAdapter(getChildFragmentManager(), mColumns);
            viewPager.setAdapter(mMyselfFragmentPagerAdapter);
            viewPager.addOnPageChangeListener(mPageListener);
            if(pagerIndicator.getViewPager()==null){
                pagerIndicator.setViewPager(viewPager);
            }
            pagerIndicator.updateIndicator(true);
            //viewPager.setCurrentItem(mColumnSelectIndex);

        }

        if (mSwitchColumn != null) {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    switchColumn(mSwitchColumn);
                }
            }, 500);
        } else {
            if (MyApplication.getInstance().getStation() != null && MyApplication.getInstance().getStation().getCategory() == 2) {
//                mHandler.postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                    }
//                }, 500);
                //mColumnSelectIndex = 1;
                viewPager.setCurrentItem(1);
            }
        }

    }


    public ViewPager.OnPageChangeListener mPageListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

            if(arg0==mColumnSelectIndex){

            }else{

            }


        }

        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub

            if (MyApplication.getInstance().getStation() != null && MyApplication.getInstance().getStation().getIs_league() == 0) {
                weatherLayout.setVisibility(View.GONE);
                logo1.setVisibility(View.VISIBLE);
            } else {
                weatherLayout.setVisibility(View.VISIBLE);
                logo1.setVisibility(View.GONE);
            }

            int prePosition = mColumnSelectIndex;
//            for (int i = 0; i < columnContent.getChildCount(); i++) {
//                View checkView = columnContent.getChildAt(i);
//                final TextView columnTextView = checkView.findViewById(R.id.text);
//                //SimpleDraweeView indicator = checkView.findViewById(R.id.indicator);
//                boolean ischeck;
//                if (i == position) {
//                    ischeck = true;
//                    //indicator.setVisibility(View.VISIBLE);
////                    columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
//                    columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//
//                    ValueAnimator animator = ValueAnimator.ofInt(15, 22).setDuration(300);
//                    animator.setInterpolator(new LinearInterpolator());
//                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                        @Override
//                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                            int textSize = (int) valueAnimator.getAnimatedValue();
//                            columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
//                        }
//                    });
//                    animator.addListener(new Animator.AnimatorListener() {
//                        @Override
//                        public void onAnimationStart(Animator animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            animation.cancel();
//                        }
//
//                        @Override
//                        public void onAnimationCancel(Animator animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animator animation) {
//
//                        }
//                    });
//                    animator.start();
//
//
////                    if (checkView.getLeft() > mScreenWidth / 2) {
////                        columnScrollView.scrollTo((int) checkView.getLeft() - mScreenWidth / 2, 0);
////                    } else {
////                        columnScrollView.scrollTo(0, 0);
////                    }
//                    mColumnSelectIndex = position;
////                    int a=checkView.getLeft();
////                    int b=columnScrollView.getScrollX();
////                    int c=columnTextView.getWidth();
////                    float pos=checkView.getLeft()-columnScrollView.getScrollX()+checkView.getWidth()/2;
////                    indicator.setX(pos);
//
//
//                } else {
//                    ischeck = false;
//                    //indicator.setVisibility(View.INVISIBLE);
//
//                    if (i == prePosition) {
//                        ValueAnimator animator = ValueAnimator.ofInt(22, 15).setDuration(300);
//                        animator.setInterpolator(new LinearInterpolator());
//                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                            @Override
//                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                                int textSize = (int) valueAnimator.getAnimatedValue();
//                                columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
//                            }
//                        });
//                        animator.addListener(new Animator.AnimatorListener() {
//                            @Override
//                            public void onAnimationStart(Animator animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                animation.cancel();
//                                columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//                            }
//
//                            @Override
//                            public void onAnimationCancel(Animator animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animator animation) {
//
//                            }
//                        });
//                        animator.start();
//                    } else {
//                        columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//                        columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//                    }
//                }
//                columnTextView.setSelected(ischeck);
//            }
        }
    };


    @OnClick({R.id.city, R.id.search, R.id.customColumn})
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
            case R.id.customColumn:
                if (mRemoteColumns != null) {
                    List<Column> columns = new ArrayList<>();
                    columns.addAll(mRemoteColumns);
                    CustomColumnDialog dialog = CustomColumnDialog.getInstance(columns, mColumns.get(mColumnSelectIndex));
                    dialog.setOnCloseClickListener(new CustomColumnDialog.OnCloseClickListener() {
                        @Override
                        public void close() {
                            mColumns = JSONObject.parseArray(SharedPreferencesUtils.getStringValue("custom_column", null), Column.class);
                            mColumnSelectIndex = 0;
//                            columnContent.removeAllViews();
//                            LayoutInflater inflater = getLayoutInflater();
//                            for (int i = 0; mColumns != null && i < mColumns.size(); i++) {
//                                Column column = mColumns.get(i);
//                                View view = inflater.inflate(R.layout.column_txt_layout, columnContent, false);
//                                TextView columnTextView = view.findViewById(R.id.text);
//                                //SimpleDraweeView indicator = view.findViewById(R.id.indicator);
//                                columnTextView.setText(column.getName());
//                                columnTextView.setTextColor(getActivity().getResources().getColorStateList(R.color.column_item_selector));
//                                if (mColumnSelectIndex == i) {
//                                    columnTextView.setSelected(true);
//                                    columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
//                                    columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//                                    //indicator.setVisibility(View.VISIBLE);
//                                } else {
//                                    columnTextView.setSelected(false);
//                                    columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//                                    columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//                                    //indicator.setVisibility(View.INVISIBLE);
//                                }
//                                view.setOnClickListener(new OnClickListener() {
//
//                                    @Override
//                                    public void onClick(View v) {
//                                        for (int i = 0; i < columnContent.getChildCount(); i++) {
//                                            View localView = columnContent.getChildAt(i);
//                                            TextView columnTextView = localView.findViewById(R.id.text);
//                                            //SimpleDraweeView indicator = localView.findViewById(R.id.indicator);
//                                            if (localView != v) {
//                                                columnTextView.setSelected(false);
//                                                //indicator.setVisibility(View.INVISIBLE);
//
//                                                columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//                                                columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//                                            } else {
//                                                //mColumnSelectIndex = i;
//                                                columnTextView.setSelected(true);
//                                                //indicator.setVisibility(View.VISIBLE);
//                                                columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
//                                                columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//
//                                                viewPager.setCurrentItem(i);
//                                            }
//                                        }
//                                    }
//                                });
//                                columnContent.addView(view, i);
//                            }
                            initFragment();
                            //mMyselfFragmentPagerAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void switchClo(Column col, boolean initial) {
                            if (initial) {
                                mColumns = JSONObject.parseArray(SharedPreferencesUtils.getStringValue("custom_column", null), Column.class);
                                mColumnSelectIndex = 0;
//                                columnContent.removeAllViews();
//                                LayoutInflater inflater = getLayoutInflater();
//                                for (int i = 0; mColumns != null && i < mColumns.size(); i++) {
//                                    Column column = mColumns.get(i);
//                                    View view = inflater.inflate(R.layout.column_txt_layout, columnContent, false);
//                                    TextView columnTextView = view.findViewById(R.id.text);
//                                    //SimpleDraweeView indicator = view.findViewById(R.id.indicator);
//                                    columnTextView.setText(column.getName());
//                                    columnTextView.setTextColor(getActivity().getResources().getColorStateList(R.color.column_item_selector));
//                                    if (mColumnSelectIndex == i) {
//                                        columnTextView.setSelected(true);
//                                        columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
//                                        columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//                                        //indicator.setVisibility(View.VISIBLE);
//                                    } else {
//                                        columnTextView.setSelected(false);
//                                        columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//                                        columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//                                        //indicator.setVisibility(View.INVISIBLE);
//                                    }
//                                    view.setOnClickListener(new OnClickListener() {
//
//                                        @Override
//                                        public void onClick(View v) {
//                                            for (int i = 0; i < columnContent.getChildCount(); i++) {
//                                                View localView = columnContent.getChildAt(i);
//                                                TextView columnTextView = localView.findViewById(R.id.text);
//                                                //SimpleDraweeView indicator = localView.findViewById(R.id.indicator);
//                                                if (localView != v) {
//                                                    columnTextView.setSelected(false);
//                                                    //indicator.setVisibility(View.INVISIBLE);
//
//                                                    columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//                                                    columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//                                                } else {
//                                                    //mColumnSelectIndex = i;
//                                                    columnTextView.setSelected(true);
//                                                    //indicator.setVisibility(View.VISIBLE);
//                                                    columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
//                                                    columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//
//                                                    viewPager.setCurrentItem(i);
//                                                }
//                                            }
//                                        }
//                                    });
//                                    columnContent.addView(view, i);
//                                }
                                mSwitchColumn = col;
                                initFragment();
                            } else {
                                switchColumn(col);
                            }

                        }
                    });
                    dialog.show(getFragmentManager(), "AudioListDialog");
                }
                break;
        }
    }


    public void getRecommendColumns(final int useLocal) {

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
                msg.arg1 = useLocal;
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
            if(city!=null){
                city.setText(MainActivity.sCurrentCity.getName());
            }

            weather();

        }
    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).keyboardEnable(true).statusBarView(statusBar).init();
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

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mColumns.get(position).getName();
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

                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    int useLocal = msg.arg1;
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        JSONObject jo = jsonObject.getJSONObject("data");
                        if (jo.getInteger("total") > 0) {
                            mRemoteColumns = JSONObject.parseArray(jo.getJSONArray("data").toJSONString(), Column.class);


                            Column col = new Column();
                            col.setName("首页");
                            col.setType(-1);
                            if (mRemoteColumns != null) {
                                mRemoteColumns.add(0, col);
                            } else {
                                mRemoteColumns = new ArrayList<>();
                                mRemoteColumns.add(col);
                            }

                            if (useLocal == 1) {
                                String customColumn = SharedPreferencesUtils.getStringValue("custom_column", "");
                                List<Column> customColumns = JSONObject.parseArray(customColumn, Column.class);

                                List<Column> tempColumns = new ArrayList<>();
                                List<Integer> columnIds = new ArrayList<>();
                                for (int i = 0; i < customColumns.size(); i++) {
                                    columnIds.add(customColumns.get(i).getId());
                                }
                                Iterator<Column> iterator = mRemoteColumns.iterator();
                                while (iterator.hasNext()) {
                                    Column column = iterator.next();
                                    if (columnIds.contains(column.getId())) {
                                        tempColumns.add(column);

                                    }
                                }

                                String custom_column = JSON.toJSONString(tempColumns);
                                if (!custom_column.equals(customColumn)) {
                                    SharedPreferencesUtils.putPreferenceTypeValue("custom_column", SharedPreferencesUtils.PreferenceType.String, custom_column);
                                }
                                return true;
                            }
                            mColumns = mRemoteColumns;
                            if (mColumns != null && mColumns.size() > 8) {
                                mColumns = mColumns.subList(0, 8);
                            }
//                            for (int i = 0; mColumns != null && i < mColumns.size(); i++) {
//                                Column column = mColumns.get(i);
//                                //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                                //params.leftMargin = 20;
//                                //params.rightMargin = 20;
//                                LayoutInflater inflater = getLayoutInflater();
//                                View view = inflater.inflate(R.layout.column_txt_layout, columnContent, false);
//                                TextView columnTextView = view.findViewById(R.id.text);
//                                //SimpleDraweeView indicator = view.findViewById(R.id.indicator);
//                                //columnTextView.setGravity(Gravity.CENTER);
//                                //columnTextView.setPadding(40, 20, 40, 20);
//
//                                //columnTextView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
//                                columnTextView.setText(column.getName());
//                                columnTextView.setTextColor(getActivity().getResources().getColorStateList(R.color.column_item_selector));
//
//
//                                if (mColumnSelectIndex == i) {
//                                    columnTextView.setSelected(true);
//                                    columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
//                                    columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//
//                                } else {
//                                    columnTextView.setSelected(false);
//                                    columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//                                    columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//                                    //indicator.setVisibility(View.INVISIBLE);
//                                }
//                                view.setOnClickListener(new OnClickListener() {
//
//                                    @Override
//                                    public void onClick(View v) {
//                                        for (int i = 0; i < columnContent.getChildCount(); i++) {
//                                            View localView = columnContent.getChildAt(i);
//                                            TextView columnTextView = localView.findViewById(R.id.text);
//                                            //SimpleDraweeView indicator = localView.findViewById(R.id.indicator);
//                                            if (localView != v) {
//                                                columnTextView.setSelected(false);
//                                                //indicator.setVisibility(View.INVISIBLE);
//
//                                                columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//                                                columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//                                            } else {
//                                                //mColumnSelectIndex = i;
//                                                columnTextView.setSelected(true);
//                                                columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
//                                                columnTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//
//                                                viewPager.setCurrentItem(i);
//                                            }
//                                        }
//                                    }
//                                });
//                                columnContent.addView(view, i);
//                            }

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


    public void jumpTo(String url, String title) {
        if (url.startsWith(ServerInfo.h5IP + "/tv") || url.startsWith(ServerInfo.h5HttpsIP + "/tv")) {
            Intent it = new Intent(getContext(), RadioListActivity.class);
            it.putExtra("type", "2");
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/radios/") || url.startsWith(ServerInfo.h5HttpsIP + "/radios/")) {
            String radioId = url.substring(url.lastIndexOf("/") + 1);
            Intent it = new Intent(getContext(), TvDetailActivity.class);
            it.putExtra("radioId", Integer.parseInt(radioId));
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/radios") || url.startsWith(ServerInfo.h5HttpsIP + "/radios")) {
            Intent it = new Intent(getContext(), RadioListActivity.class);
            it.putExtra("type", "1");
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/radios/") || url.startsWith(ServerInfo.h5HttpsIP + "/radios/")) {
            String radioId = url.substring(url.lastIndexOf("/") + 1);
            Intent it = new Intent(getContext(), RadioDetailActivity.class);
            it.putExtra("radioId", Integer.parseInt(radioId));
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/gover") || url.startsWith(ServerInfo.h5HttpsIP + "/gover")) {
            Intent it = new Intent(getContext(), WebViewActivity.class);
            it.putExtra("url", url);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/dj") || url.startsWith(ServerInfo.h5HttpsIP + "/dj")) {
            Intent it = new Intent(getContext(), WebViewActivity.class);
            it.putExtra("url", url);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/interact") || url.startsWith(ServerInfo.h5HttpsIP + "/interact")) {
            Intent it = new Intent(getContext(), WebViewActivity.class);
            it.putExtra("url", url);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/guide") || url.startsWith(ServerInfo.h5HttpsIP + "/guide")) {
            Intent it = new Intent(getContext(), WebViewActivity.class);
            it.putExtra("url", url);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/cates/") || url.startsWith(ServerInfo.h5HttpsIP + "/cates/")) {
            //跳转栏目
            String columnid = url.substring(url.lastIndexOf("/") + 1);
            Column column = new Column();
            column.setId(Integer.parseInt(columnid));
            switchColumn(column);

        } else if (url.startsWith(ServerInfo.h5IP + "/specials") || url.startsWith(ServerInfo.h5HttpsIP + "/specials")) {
            //跳转热门
            int columnid = 0;

            for (int i = 0; i < mColumns.size(); i++) {
                if (mColumns.get(i).getType() == 1) {
                    columnid = mColumns.get(i).getId();
                    break;
                }
            }
            Column column = new Column();
            column.setId(columnid);
            switchColumn(column);

        } else if (url.startsWith(ServerInfo.h5IP + "/orgs/") || url.startsWith(ServerInfo.h5HttpsIP + "/orgs/")) {
            String organizationId = url.substring(url.lastIndexOf("/") + 1);
//            Intent it = new Intent(getContext(), OrganizationDetailActivity.class);
//            it.putExtra("organizationId", Integer.parseInt(organizationId));
//            startActivity(it);

            Intent it = new Intent(getContext(), WebViewActivity.class);
            it.putExtra("url", ServerInfo.h5HttpsIP + "/orgs/" + organizationId);
            startActivity(it);

        } else if (url.startsWith(ServerInfo.h5IP + "/anchors/") || url.startsWith(ServerInfo.h5HttpsIP + "/anchors/")) {
            String anchorId = url.substring(url.lastIndexOf("/") + 1);
//            Intent it = new Intent(getContext(), AnchorDetailActivity.class);
//            it.putExtra("anchorId", Integer.parseInt(anchorId));
//            startActivity(it);
            Intent it = new Intent(getContext(), WebViewActivity.class);
            it.putExtra("url", ServerInfo.h5HttpsIP + "/anchors/" + anchorId);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/atlas/") || url.startsWith(ServerInfo.h5HttpsIP + "/atlas/")) {
            String galleriaId = url.substring(url.lastIndexOf("/") + 1);
            Intent it = new Intent(getContext(), GalleriaActivity.class);
            it.putExtra("galleriaId", Integer.parseInt(galleriaId));
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/albums/") || url.startsWith(ServerInfo.h5HttpsIP + "/albums/")) {
            String albumId = url.substring(url.lastIndexOf("/") + 1);
            Intent it = new Intent(getContext(), AlbumDetailActivity.class);
            it.putExtra("albumId", Integer.parseInt(albumId));
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/audios/") || url.startsWith(ServerInfo.h5HttpsIP + "/audios/")) {
            String audioId = url.substring(url.lastIndexOf("/") + 1);
            Intent it = new Intent(getContext(), AudioDetailActivity.class);
            it.putExtra("audioId", Integer.parseInt(audioId));
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/posts/") || url.startsWith(ServerInfo.h5HttpsIP + "/posts/")) {
            String articleId = url.substring(url.lastIndexOf("/") + 1);
            Intent it = new Intent(getContext(), ArticleDetailActivity.class);
            it.putExtra("articleId", Integer.parseInt(articleId));
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/specials/") || url.startsWith(ServerInfo.h5HttpsIP + "/specials/")) {
            String specialId = url.substring(url.lastIndexOf("/") + 1);
            Intent it = new Intent(getContext(), SpecialDetailActivity.class);
            it.putExtra("specialId", Integer.parseInt(specialId));
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/videos/") || url.startsWith(ServerInfo.h5HttpsIP + "/videos/")) {
            String videoId = url.substring(url.lastIndexOf("/") + 1);
            Intent it = new Intent(getContext(), VideoDetailActivity.class);
            it.putExtra("videoId", Integer.parseInt(videoId));
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/subcates/") || url.startsWith(ServerInfo.h5IP + "/subcates/")) {
            String columnid = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("?"));
            Column column = new Column();
            column.setId(Integer.parseInt(columnid));
            column.setName(url.substring(url.lastIndexOf("=") + 1));
            Intent it = new Intent(getContext(), ContentActivity.class);
            it.putExtra("column", column);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.scs + "/broke-create")) {
            if (User.getInstance() != null) {
                Intent it = new Intent(getContext(), CluesActivity.class);
                it.putExtra("url", ServerInfo.scs + "/broke-create");
                startActivity(it);
            } else {
                Intent it = new Intent(getContext(), LoginActivity.class);
                startActivity(it);
            }

        } else if (url.startsWith(ServerInfo.h5IP + "/invitation-post") || url.startsWith(ServerInfo.h5HttpsIP + "/invitation-post")) {
            Intent it = new Intent(getContext(), WebViewActivity.class);
            it.putExtra("url", url);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/actrank") || url.startsWith(ServerInfo.h5HttpsIP + "/actrank")) {
            Intent it = new Intent(getContext(), WebViewActivity.class);
            it.putExtra("url", url);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/wish") || url.startsWith(ServerInfo.h5HttpsIP + "/wish")) {
            Intent it = new Intent(getContext(), WebViewActivity.class);
            it.putExtra("url", url);
            startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/actlist") || url.startsWith(ServerInfo.h5HttpsIP + "/actlist")) {
            Intent it = new Intent(getContext(), WebViewActivity.class);
            it.putExtra("url", url);
            startActivity(it);
        } else {
            Intent it = new Intent(getContext(), WebViewBackActivity.class);
            it.putExtra("url", url);
            it.putExtra("title", title);
            startActivity(it);
        }
    }
}
