package com.shuangling.software;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.hjq.toast.ToastUtils;
import com.mob.MobSDK;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.shuangling.software.dao.DaoMaster;
import com.shuangling.software.dao.DaoSession;
import com.shuangling.software.service.AudioPlayerService;

public class MyApplication extends MultiDexApplication {

	public static MyApplication sInstance;
    private static DaoSession sDaoSession;

    private int currentTheme=R.style.AppThemeBlue;
    private String backgroundImage;
	@Override
	public void onCreate() {

		super.onCreate();
		sInstance = this;
		Fresco.initialize(this);
		MobSDK.init(this);
		ToastUtils.init(this);
        setupDatabase();
		Intent it = new Intent(this, AudioPlayerService.class);
		startService(it);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            int activitys=0;
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activitys++;
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                activitys--;
                if(activitys==0){
                    Intent it = new Intent(MyApplication.this, AudioPlayerService.class);
                    stopService(it);
                }

            }
        });
        //setTheme(R.style.AppThemeRed);
	}




	public static MyApplication getInstance() {
		// TODO Auto-generated method stub
		return sInstance;
	}



    public int getCurrentTheme() {
        return currentTheme;
    }

    public void setCurrentTheme(int currentTheme) {
        this.currentTheme = currentTheme;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }




    /**
     * 配置数据库
     */
    private void setupDatabase() {
        //创建数据库shop.db"
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "ltsj.db", null);
        //获取可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取Dao对象管理者
        sDaoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoInstant() {
        return sDaoSession;
    }




}
