package com.shuangling.software;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

//import com.alibaba.sdk.android.push.CloudPushService;
//import com.alibaba.sdk.android.push.CommonCallback;
//import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.hjq.toast.IToastStyle;
import com.hjq.toast.ToastUtils;
import com.hjq.toast.style.ToastBlackStyle;
import com.liulishuo.filedownloader.FileDownloader;
import com.mob.MobSDK;
import com.shuangling.software.dao.DaoMaster;
import com.shuangling.software.dao.DaoSession;
import com.shuangling.software.entity.Album;
import com.shuangling.software.entity.OssInfo;
import com.shuangling.software.entity.Station;
import com.shuangling.software.network.ElnImageDownloaderFetcher;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.oss.OSSAKSKCredentialProvider;
import com.shuangling.software.oss.OssService;
import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.utils.FloatWindowUtil;
import com.shuangling.software.utils.MyToastStyle;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.Snake;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;

import static com.taobao.accs.client.AccsConfig.build;

public class MyApplication extends MultiDexApplication {

    public static final String TAG=MyApplication.class.getName();

	public static MyApplication sInstance;
    private static DaoSession sDaoSession;

    private int currentTheme=R.style.AppThemeBlue;
    private Station station;
    private String backgroundImage;
    private boolean needResume=false;

    public Station getStation() {
        return station;
    }

    public void setStation( Station station ){
        this.station=station;
    }

	@Override
	public void onCreate() {

		super.onCreate();
		sInstance = this;

//        OkHttpClient okHttpClient=OkHttpUtils.okHttpClient;
//        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
//                .newBuilder(this, okHttpClient)
//                .build();
//
//        Fresco.initialize(this, config);
//        Fresco.initialize(this);
        initFresco();

		MobSDK.init(this);
		ToastUtils.init(this);
        ToastUtils.initStyle(new MyToastStyle());
        // 对Snake进行初始化
        Snake.init(this);
        FileDownloader.setup(this);
        initPushService(this);
        // android 7.0系统解决拍照的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }


        setupDatabase();
		Intent it = new Intent(this, AudioPlayerService.class);
		startService(it);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            int activitys=0;
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (activitys == 0) {
                    //后台切换到前台
                    Log.v("onActivityStarted", "App切到前台");
                    if(needResume){
                        FloatWindowUtil.getInstance().visibleWindow();
                    }
                }
                activitys++;
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                activitys--;
                if (activitys == 0) { //前台切换到后台
                    Log.v("onActivityStopped", "App切到后台");
                    if(FloatWindowUtil.getInstance().isVisible()){
                        FloatWindowUtil.getInstance().hideWindow();
                        needResume=true;

                    }

                }

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
//                activitys--;
//                if(activitys==0){
//                    Intent it = new Intent(MyApplication.this, AudioPlayerService.class);
//                    stopService(it);
//                }

            }
        });

        initTheme();

        initStation();

	}


    private void initFresco(){
//        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder()
//                .setBaseDirectoryPath(new File(MultiCard.getInstance(this).getRootDir()))
//                .setBaseDirectoryName("image_cache")
//                .setMaxCacheSize(50 * ByteConstants.MB)
//                .setMaxCacheSizeOnLowDiskSpace(10 * ByteConstants.MB)
//                .setMaxCacheSizeOnVeryLowDiskSpace(2 * ByteConstants.MB)
//                .build();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setNetworkFetcher(new ElnImageDownloaderFetcher())
                .build();
        Fresco.initialize(this, config);
    }


	public static MyApplication getInstance() {
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


    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private void initPushService(final Context applicationContext) {
        initNotificationChannel();
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {

                String deviceId=pushService.getDeviceId();
                Log.i(TAG, "init cloudchannel success");
                //setConsoleText("init cloudchannel success");
            }
            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
                //setConsoleText("init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
    }


    public void initNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = "1";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "FmChannel";
            // 用户可以看到的通知渠道的描述
            String description = "FmChannel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    public void initTheme() {

        String url = ServerInfo.serviceIP + ServerInfo.globalDecorate;
        Map<String, String> params = new HashMap<String, String>();

        OkHttpUtils.getNotAuthorization(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                try{

                    JSONObject jsonObject = JSONObject.parseObject(response);

                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        JSONObject jo=jsonObject.getJSONObject("data");
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date startTime = format.parse(jo.getString("start_time"));
                        Date endTime = format.parse(jo.getString("end_time"));
                        Date today = new Date();

                        if(today.after(startTime)&&today.before(endTime)){
                            if(Color.parseColor(jo.getString("background_color"))==getResources().getColor(R.color.themeBlue)){
                                MyApplication.getInstance().setCurrentTheme(R.style.AppThemeBlue);
                                MyApplication.getInstance().setTheme(R.style.AppThemeBlue);
                            }else if(Color.parseColor(jo.getString("background_color"))==getResources().getColor(R.color.themePurple)){
                                MyApplication.getInstance().setCurrentTheme(R.style.AppThemePurple);
                                MyApplication.getInstance().setTheme(R.style.AppThemePurple);
                            }else if(Color.parseColor(jo.getString("background_color"))==getResources().getColor(R.color.themeRed)){
                                MyApplication.getInstance().setCurrentTheme(R.style.AppThemeRed);
                                MyApplication.getInstance().setTheme(R.style.AppThemeRed);
                            }else if(Color.parseColor(jo.getString("background_color"))==getResources().getColor(R.color.themeGreen)){
                                MyApplication.getInstance().setCurrentTheme(R.style.AppThemeGreen);
                                MyApplication.getInstance().setTheme(R.style.AppThemeGreen);
                            }else if(Color.parseColor(jo.getString("background_color"))==getResources().getColor(R.color.themeOrange)){
                                MyApplication.getInstance().setCurrentTheme(R.style.AppThemeOrange);
                                MyApplication.getInstance().setTheme(R.style.AppThemeOrange);
                            }
                            MyApplication.getInstance().setBackgroundImage(jo.getString("background_image"));

                        }

                    }



                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });


    }


    public void initStation(){
        String url = ServerInfo.serviceIP + ServerInfo.getStationInfo;
        Map<String, String> params = new HashMap<String, String>();

        OkHttpUtils.getNotAuthorization(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                try{

                    JSONObject jsonObject = JSONObject.parseObject(response);

                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        station = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), Station.class);

                    }



                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }


}
