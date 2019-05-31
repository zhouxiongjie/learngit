package com.shuangln.antfm.customview;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.shuangln.antfm.R;
import com.shuangln.antfm.utils.ImageLoader;
import com.viewpagerindicator.CirclePageIndicator;
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

	private int mWidth;
	private int mHeight;

	public abstract static class Banner
	{
		public abstract String getLogo();
		public abstract String getTitle();
	}


	public interface OnItemClickListener
	{
		public void onClick(View view);
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

	private void init(Context context){
		mContext = context;
		mInflater=LayoutInflater.from(context);
		mInflater.inflate(R.layout.advertisement_layout, this, true);
		mAutoScrollViewPager =  findViewById(R.id.autoScrollViewPager);
		mAutoViewPageIndicator = findViewById(R.id.autoPageIndicator);
		mAdvertDesc = findViewById(R.id.advertDesc);

		mAutoScrollViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mWidth=mAutoScrollViewPager.getWidth();
				mHeight=mAutoScrollViewPager.getHeight();
			}
		});
	}


	public void setData(List<T> datas) {
		mData=datas;
		if (datas.size() > 0) {
			setVisibility(View.VISIBLE);
		}else{
			setVisibility(View.GONE);
			return;
		}
		mAutoViews.clear();

		for(int i=0;i<mData.size();i++){
			final T data=mData.get(i);
			SimpleDraweeView view=new SimpleDraweeView(mContext);
			GenericDraweeHierarchy hierarchy = view.getHierarchy();
			hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);
			RoundingParams rp = new RoundingParams();
			rp.setRoundAsCircle(false);
			rp.setCornersRadius(10);
			hierarchy.setRoundingParams(rp);
			view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			view.setTag(data);

			if(!TextUtils.isEmpty(data.getLogo())){

				Uri uri = Uri.parse(data.getLogo());
				ImageLoader.showThumb(uri, view, mWidth, mHeight);
			}
			mAutoViews.add(view);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(mClickListener!=null){
						mClickListener.onClick(v);
					}

				}
			});

			mAutoViewPageAdapter = new PagerAdapter()
			{

				@Override
				public void destroyItem(ViewGroup container, int position, Object object)
				{
					container.removeView(mAutoViews.get(position));
				}

				@Override
				public Object instantiateItem(ViewGroup container, int position)
				{
					View view = mAutoViews.get(position);
					container.addView(view);
					return view;
				}

				@Override
				public boolean isViewFromObject(View arg0, Object arg1)
				{
					return arg0 == arg1;
				}

				@Override
				public int getCount()
				{
					return mAutoViews.size();
				}
			};
			mAutoScrollViewPager.setAdapter(mAutoViewPageAdapter);
			mAutoViewPageIndicator.setViewPager(mAutoScrollViewPager);
			mAutoViewPageIndicator.setSnap(true);
			mAutoViewPageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				@Override
				public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
					T adv =(T) mAutoViews.get(position).getTag();
					mAdvertDesc.setText(adv.getTitle());
				}

				@Override
				public void onPageSelected(int position) {
					T adv =(T) mAutoViews.get(position).getTag();
					mAdvertDesc.setText(adv.getTitle());
				}

				@Override
				public void onPageScrollStateChanged(int state) {

				}
			});
			mAutoScrollViewPager.startAutoScroll();
			mAutoViewPageIndicator.setCurrentItem(0);
		}
	}


}
