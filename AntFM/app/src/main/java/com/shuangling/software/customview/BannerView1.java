package com.shuangling.software.customview;

import android.content.Context;
import android.net.Uri;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class BannerView1<T extends BannerView.Banner> extends RelativeLayout {
    private OnItemClickListener mClickListener;
    private Context mContext;
    private LayoutInflater mInflater;
    private AutoScrollViewPager mAutoScrollViewPager;
    private CirclePageIndicator mAutoViewPageIndicator;
    private TextView mAdvertDesc;
    private List<T> mData = new ArrayList<T>();
    private List<View> mAutoViews = new ArrayList<View>();
    private PagerAdapter mAutoViewPageAdapter;
    private int mCurponsition;//记录当前显示的索引
    private int mWidth;
    private int mHeight;

    public interface OnItemClickListener {
        public void onClick(View view);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public BannerView1(Context context) {
        super(context);
        init(context);
    }

    public BannerView1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public BannerView1(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        init(context);
    }

    public BannerView1(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.advertisement_layout1, this, true);
        mAutoScrollViewPager = findViewById(R.id.autoScrollViewPager);
        mAutoViewPageIndicator = findViewById(R.id.autoPageIndicator);
        mAdvertDesc = findViewById(R.id.advertDesc);
        mAutoScrollViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mWidth = mAutoScrollViewPager.getWidth();
                mHeight = mAutoScrollViewPager.getHeight();
            }
        });
    }

    public void setData(List<T> datas) {
        if (datas.size() > 0) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
            return;
        }
        mData = new ArrayList<>();
        mData.addAll(datas);
        mData.add(0, datas.get(datas.size() - 1));
        mData.add(datas.get(0));
        mAutoViews.clear();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int i = 0; i < mData.size(); i++) {
            final T data = mData.get(i);
            View view = inflater.inflate(R.layout.advertisement_item, this, false);
            SimpleDraweeView simpleDraweeView = view.findViewById(R.id.logo);
            int width = CommonUtils.getScreenWidth();
            int height = (int) (width / 5.36);
            if (!TextUtils.isEmpty(data.getLogo())) {
                Uri uri = Uri.parse(data.getLogo() + CommonUtils.getOssResize(width, height));
                ImageLoader.showThumb(uri, simpleDraweeView, width, height);
            }
            mAutoViews.add(view);
            view.setTag(data);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onClick(v);
                    }
                }
            });
            mAutoViewPageAdapter = new PagerAdapter() {
                @Override
                public void destroyItem(ViewGroup container, int position, Object object) {
                    container.removeView(mAutoViews.get(position));
                }

                @Override
                public Object instantiateItem(ViewGroup container, int position) {
                    View view = mAutoViews.get(position);
                    container.addView(view);
                    return view;
                }

                @Override
                public boolean isViewFromObject(View arg0, Object arg1) {
                    return arg0 == arg1;
                }

                @Override
                public int getCount() {
                    return mAutoViews.size();
                }
            };
            mAutoScrollViewPager.setAdapter(mAutoViewPageAdapter);
            mAutoViewPageIndicator.setViewPager(mAutoScrollViewPager);
            mAutoViewPageIndicator.setSnap(true);
            mAutoViewPageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    T adv = (T) mAutoViews.get(position).getTag();
                    mAdvertDesc.setText(adv.getTitle());
                }

                @Override
                public void onPageSelected(int position) {
                    mCurponsition = position;//记录当前显示的索引
                    T adv = (T) mAutoViews.get(position).getTag();
                    mAdvertDesc.setText(adv.getTitle());
                    if (mCurponsition == 0) {
                        mAutoScrollViewPager.setCurrentItem(mAutoViews.size() - 2, false);//切换，不要动画效果
                    } else if (mCurponsition == mAutoViews.size() - 1) {
                        mAutoScrollViewPager.setCurrentItem(1, false);//切换，不要动画效果
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    //验证当前的滑动是否结束
//                    if (state == ViewPager.SCROLL_STATE_IDLE) {
//                        if (mCurponsition == 0){
//                            mAutoScrollViewPager.setCurrentItem(mAutoViews.size()-2, false);//切换，不要动画效果
//                        } else if (mCurponsition ==mAutoViews.size()-1) {
//                            mAutoScrollViewPager.setCurrentItem(1, false);//切换，不要动画效果
//                        }
//                    }
                }
            });
            mAutoScrollViewPager.startAutoScroll();
            mAutoViewPageIndicator.setCurrentItem(1);
        }
    }

    private void measureView(View v) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        if (lp == null) {
            return;
        }
        int width;
        int height;
        if (lp.width > 0 || lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            // xml文件中设置了该view的准确宽度值，例如android:layout_width="150dp"
            width = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
        } else {
            // xml文件中使用wrap_content设定该view宽度，例如android:layout_width="wrap_content"
            width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        if (lp.height > 0 || lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            // xml文件中设置了该view的准确高度值，例如android:layout_height="50dp"
            height = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
        } else {
            // xml文件中使用wrap_content设定该view高度，例如android:layout_height="wrap_content"
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        v.measure(width, height);
    }
}
