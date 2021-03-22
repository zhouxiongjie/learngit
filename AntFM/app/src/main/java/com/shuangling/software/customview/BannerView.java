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

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.entity.BannerColorInfo;
import com.shuangling.software.interf.LoadImageInterface;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class BannerView<T extends BannerView.Banner> extends RelativeLayout {
    private OnItemClickListener mClickListener;
    private Context mContext;
    private LayoutInflater mInflater;
    private AutoScrollViewPager mAutoScrollViewPager;
    private CirclePageIndicator mAutoViewPageIndicator;
    private TextView mAdvertDesc;
    private List<T> mData = new ArrayList<T>();
    private List<View> mAutoViews = new ArrayList<View>();
    private PagerAdapter mAutoViewPageAdapter;
    //定义监听接口，接受外部传过来的监听对象
    private OnPageChangeListener banner_onPageChangeListener;
    private LoadImageInterface loadImageInterface;
    private List<BannerColorInfo> colorList;
    private int mCurponsition;//记录当前显示的索引
    private int mWidth;
    private int mHeight;
    private int mMode = 2;        //1大图，2卡片

    public void setLoadImageInterface(LoadImageInterface loadImageInterface, List<BannerColorInfo> colorList) {
        this.loadImageInterface = loadImageInterface;
        this.colorList = colorList;
    }

    public int getmMode() {
        return mMode;
    }

    public void setmMode(int mMode) {
        this.mMode = mMode;
    }

    public abstract static class Banner {
        public abstract String getLogo();

        public abstract String getTitle();

        public abstract String getUrl();
    }

    public interface OnItemClickListener {
        public void onClick(View view);
    }

    // 外部对象通过该接口监听内部viewpager的三种状态，同时获取参数
    public interface OnPageChangeListener {
        public void onPageScrolleStateChange(int state);

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        public void onPageSelected(int position);
    }

    //外部调用该方法，传入监听对象
    public void addOnPageChangedListener(OnPageChangeListener onPageChangeListener) {
        this.banner_onPageChangeListener = onPageChangeListener;
    }


    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public BannerView(Context context) {
        super(context);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.advertisement_layout, this, true);
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
        for (int i = 0; i < mData.size(); i++) {
            final T data = mData.get(i);

            View banner = mInflater.inflate(R.layout.banner_item, mAutoScrollViewPager, false);

            SimpleDraweeView logo = banner.findViewById(R.id.logo);
            SimpleDraweeView mask = banner.findViewById(R.id.mask);
            if (TextUtils.isEmpty(data.getTitle())) {
                mask.setVisibility(GONE);
            } else {
                mask.setVisibility(VISIBLE);
            }
            if (mMode == 1) {
                //大图模式
                GenericDraweeHierarchy hierarchy = logo.getHierarchy();
//                hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
//                logo.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                banner.setTag(data);
                int width = CommonUtils.getScreenWidth();
                int height = 14 * width / 25;
                if (!TextUtils.isEmpty(data.getLogo())) {
                    Uri uri = Uri.parse(data.getLogo() + CommonUtils.getOssResize(width, height));
                    if (loadImageInterface != null) {
                        loadImageInterface.getBitmap(Uri.parse(data.getLogo()));
                    }
                    ImageLoader.showThumb(uri, logo, width, height);
                }


            } else {
                //卡片模式
                GenericDraweeHierarchy hierarchy = logo.getHierarchy();

                GenericDraweeHierarchy hierarchy01 = mask.getHierarchy();
                hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
                RoundingParams rp = new RoundingParams();
                rp.setRoundAsCircle(false);
                rp.setCornersRadius(10);
                hierarchy.setRoundingParams(rp);

                hierarchy01.setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
                RoundingParams rp01 = new RoundingParams();
                rp01.setRoundAsCircle(false);
                rp01.setCornersRadius(10);
                hierarchy01.setRoundingParams(rp01);
//                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                banner.setTag(data);
                int width = CommonUtils.getScreenWidth() - CommonUtils.dip2px(40);
                int height = 10 * width / 23;
                if (!TextUtils.isEmpty(data.getLogo())) {
                    Uri uri = Uri.parse(data.getLogo() + CommonUtils.getOssResize(width, height));
                    if (loadImageInterface != null) {
                        loadImageInterface.getBitmap(Uri.parse(data.getLogo()));
                    }
                    ImageLoader.showThumb(uri, logo, width, height);
                }
            }
            mAutoViews.add(banner);
            banner.setOnClickListener(new OnClickListener() {
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
                    if (banner_onPageChangeListener != null) {
                        banner_onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }
                    T adv = (T) mAutoViews.get(position).getTag();
                    mAdvertDesc.setText(adv.getTitle());
                    //mAdvertDesc.setText("抗击疫情我们在一起，坚决打赢疫情防控的人民战争坚决打赢疫情防控的人民战争");

                }

                @Override
                public void onPageSelected(int position) {
                    if (banner_onPageChangeListener != null) {
                        banner_onPageChangeListener.onPageSelected(position);
                    }
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
                    if (banner_onPageChangeListener != null) {
                        banner_onPageChangeListener.onPageScrolleStateChange(state);
                    }
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
