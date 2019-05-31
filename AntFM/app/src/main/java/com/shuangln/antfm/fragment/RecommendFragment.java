package com.shuangln.antfm.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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
import com.shuangln.antfm.R;
import com.shuangln.antfm.entity.ColumnInfo;
import com.shuangln.antfm.network.OkHttpCallback;
import com.shuangln.antfm.network.OkHttpUtils;
import com.shuangln.antfm.utils.ServerInfo;

import java.io.IOException;
import java.util.ArrayList;

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
    private ColumnInfo mColumnInfo;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    /**
     * 屏幕宽度
     */
    private int mScreenWidth = 0;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mHandler = new Handler(this);
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
        if (mColumnInfo.getData() != null && mColumnInfo.getData().size() > 0) {
            MyselfFragmentPagerAdapter mAdapetr = new MyselfFragmentPagerAdapter(getChildFragmentManager(), mColumnInfo);
            viewPager.setAdapter(mAdapetr);
            viewPager.addOnPageChangeListener(mPageListener);
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
            viewPager.setCurrentItem(position);

            for (int i = 0; i < columnContent.getChildCount(); i++) {
                View checkView = columnContent.getChildAt(i);
                TextView columnTextView = checkView.findViewById(R.id.text);
                SimpleDraweeView indicator = checkView.findViewById(R.id.indicator);
                boolean ischeck;
                if (i == position) {
                    ischeck = true;
                    indicator.setVisibility(View.VISIBLE);
                    columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                    if (checkView.getLeft() > mScreenWidth / 2) {
                        columnScrollView.scrollTo((int) checkView.getLeft() - mScreenWidth / 2, 0);
                    }
                    mColumnSelectIndex = position;

                } else {
                    ischeck = false;
                    indicator.setVisibility(View.INVISIBLE);
                    columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
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
        OkHttpUtils.get(url, null, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_COLUMN;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, IOException exception) {


            }
        });


    }


    public class MyselfFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private ColumnInfo mColumnInfo;
        private FragmentManager fm;

        public MyselfFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public MyselfFragmentPagerAdapter(FragmentManager fm, ColumnInfo columnInfo) {
            super(fm);
            this.fm = fm;
            mColumnInfo = columnInfo;

        }

        @Override
        public int getCount() {
            return mColumnInfo.getData().size();
        }

        @Override
        public Fragment getItem(int position) {
            Bundle data = new Bundle();
            data.putString("ColumnName", mColumnInfo.getData().get(position).getName());
            data.putInt("ColumnId", mColumnInfo.getData().get(position).getId());
            ContentFragment contentFragment = new ContentFragment();
            contentFragment.setArguments(data);
            return contentFragment;
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
            String fragmentTag = fragment.getTag();
            FragmentTransaction ft = fm.beginTransaction();
//移除旧的fragment
            ft.remove(fragment);
//换成新的fragment
            fragment = getItem(position);
//添加新fragment时必须用前面获得的tag，这点很重要
            ft.add(container.getId(), fragment, fragmentTag);
            ft.attach(fragment);
            ft.commit();
            return fragment;
        }

    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_COLUMN: {
                String result = (String) msg.obj;
                try {
                    mColumnInfo = JSONObject.parseObject(result, ColumnInfo.class);
                    if (mColumnInfo.getCode() == 100000 && mColumnInfo.getData() != null) {
                        int count = mColumnInfo.getData().size();
                        for (int i = 0; i < count; i++) {
                            ColumnInfo.Column column = mColumnInfo.getData().get(i);
                            //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                            params.leftMargin = 20;
//                            params.rightMargin = 20;
                            LayoutInflater inflater = getLayoutInflater();
                            if (inflater == null)
                                return false;
                            View view = inflater.inflate(R.layout.column_txt_layout, columnContent, false);
                            TextView columnTextView = view.findViewById(R.id.text);
                            SimpleDraweeView indicator = view.findViewById(R.id.indicator);
//                            columnTextView.setGravity(Gravity.CENTER);
//                            columnTextView.setPadding(40, 20, 40, 20);

                            columnTextView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                            columnTextView.setText(column.getName());
                            columnTextView.setTextColor(getActivity().getResources().getColorStateList(R.color.column_item_selector));
                            if (mColumnSelectIndex == i) {
                                columnTextView.setSelected(true);
                                columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                                indicator.setVisibility(View.VISIBLE);
                            } else {
                                columnTextView.setSelected(false);
                                columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
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
                                            columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                        } else {
                                            mColumnSelectIndex = i;
                                            columnTextView.setSelected(true);
                                            indicator.setVisibility(View.VISIBLE);
                                            columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                                            viewPager.setCurrentItem(mColumnSelectIndex);
                                        }
                                    }
                                }
                            });
                            columnContent.addView(view, i);
                        }

                        initFragment();
                    } else {

                    }


                } catch (Exception e) {

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
