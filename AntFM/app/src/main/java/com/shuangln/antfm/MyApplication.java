package com.shuangln.antfm;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

public class MyApplication extends MultiDexApplication {

	public static MyApplication sInstance;

	static {
		//设置全局的Header构建器
		SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
			@Override
			public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
				layout.setPrimaryColorsId(R.color.white, android.R.color.black);//全局设置主题颜色
				return new ClassicsHeader(context);
				//setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
			}
		});
		//设置全局的Footer构建器
//		SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
//			@Override
//			public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
//				//指定为经典Footer，默认是 BallPulseFooter
//				return new ClassicsFooter(context).setDrawableSize(20);
//			}
//		});
	}


	@Override
	public void onCreate() {

		super.onCreate();
		sInstance = this;
		Fresco.initialize(this);
	}




	public static MyApplication getInstance() {
		// TODO Auto-generated method stub
		return sInstance;
	}




}
