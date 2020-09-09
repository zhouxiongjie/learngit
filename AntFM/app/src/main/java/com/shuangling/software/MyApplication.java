package com.shuangling.software;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.hjq.toast.ToastUtils;
import com.liulishuo.filedownloader.FileDownloader;
import com.mob.MobSDK;
import com.previewlibrary.ZoomMediaLoader;
import com.qiniu.droid.rtc.QNLogLevel;
import com.qiniu.droid.rtc.QNRTCEnv;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.shuangling.software.activity.FontSizeSettingActivity;
import com.shuangling.software.customview.MyClassicFooter;
import com.shuangling.software.customview.MyClassicHeader;
import com.shuangling.software.dao.DaoMaster;
import com.shuangling.software.dao.DaoSession;
import com.shuangling.software.entity.Station;
import com.shuangling.software.network.ElnImageDownloaderFetcher;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.CrashHandler;
import com.shuangling.software.utils.FloatWindowUtil;
import com.shuangling.software.utils.MyImageLoader;
import com.shuangling.software.utils.MyToastStyle;
import com.shuangling.software.utils.ServerInfo;

import com.youngfeng.snake.Snake;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;
import okhttp3.Call;


public class MyApplication extends MultiDexApplication {


    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.white,R.color.textColorThree);//全局设置主题颜色
//                int size=CommonUtils.dp2sp(16);
                MyClassicHeader mch=new MyClassicHeader(context);
                mch.setEnableLastTime(false);
                mch.getTitleView().setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
                return mch;//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                layout.setPrimaryColorsId(R.color.white,R.color.textColorThree);
                layout.setEnableScrollContentWhenLoaded(true);
                MyClassicFooter mcf=new MyClassicFooter(context);
                mcf.getTitleView().setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
                return  mcf.setFinishDuration(0);
            }
        });
    }


    public static final String TAG=MyApplication.class.getName();




	public static MyApplication sInstance;
    private static DaoSession sDaoSession;

    private int currentTheme=R.style.AppThemeBlue;
    private Station station;
    private String backgroundImage;
    private boolean needResume=false;

    public String useProtocolTitle;
    public String secretProtocolTitle;

    public boolean remindPermission=true;
    public boolean findNewVerison=false;

    //private static int articleVoiceStatus;

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
        QNRTCEnv.setLogLevel(QNLogLevel.INFO);
        /**
         * init must be called before any other func
         */
        QNRTCEnv.init(getApplicationContext());
        QNRTCEnv.setLogFileEnabled(true);
        ZoomMediaLoader.getInstance().init(new MyImageLoader());
        Constant.SYSTEM_FONT_SCALE = getResources().getConfiguration().fontScale;
//        OkHttpClient okHttpClient=OkHttpUtils.okHttpClient;
//        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
//                .newBuilder(this, okHttpClient)
//                .build();
//
//        Fresco.initialize(this, config);
//        Fresco.initialize(this);
        Sentry.init("http://a31a66f6b5ee4bd4ad7ef75899bfd28f@47.94.104.239:9000/7", new AndroidSentryClientFactory(this));
      //  Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());



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

        getApiDomain();

        getUseProtocol();
	}



    //设置字体为默认大小，不随系统字体大小改而改变
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        if (newConfig.fontScale != 1)//非默认值
//            getResources();
//        super.onConfigurationChanged(newConfig);
//    }


//    @Override
//    public Resources getResources() {
//        return CommonUtils.setFontSize(this);
//    }



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
                        String start=jo.getString("start_time");
                        String end=jo.getString("end_time");
                        if(TextUtils.isEmpty(start)&&TextUtils.isEmpty(end)){
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
                        }else{
                            Date startTime = format.parse(start);
                            Date endTime = format.parse(end);
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



    public void getApiDomain() {

        String url = ServerInfo.live + "/v1/api_domain";
        Map<String, String> params = new HashMap<String, String>();

        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                try{

                    JSONObject jsonObject = JSONObject.parseObject(response);

                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        ServerInfo.echo_server=jsonObject.getJSONObject("data").getString("echo_server");
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

        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

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


//    public void articleVoiceConfigs(){
//        String url = ServerInfo.serviceIP + ServerInfo.articleVoiceConfigs;
//        Map<String, String> params = new HashMap<String, String>();
//
//        OkHttpUtils.getNotAuthorization(url, params, new OkHttpCallback(this) {
//
//            @Override
//            public void onResponse(Call call, String response) throws IOException {
//
//
//                try{
//
//                    JSONObject jsonObject = JSONObject.parseObject(response);
//
//                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
//
//                        articleVoiceStatus=jsonObject.getJSONObject("data").getIntValue("status");
//
//                    }
//
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call call, Exception exception) {
//
//
//            }
//        });
//    }



    private void getUseProtocol() {

        String url = ServerInfo.serviceIP + ServerInfo.useProtocol;
        Map<String, String> params = new HashMap<String, String>();

        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        if(jsonObject.getJSONObject("data")!=null){
                            useProtocolTitle = jsonObject.getJSONObject("data").getString("title");
                        }
                    }
                } catch (Exception e) {

                }
                getSecretProtocol();
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                getSecretProtocol();
            }
        });
    }


    private void getSecretProtocol() {

        String url = ServerInfo.serviceIP + ServerInfo.clauses;
        Map<String, String> params = new HashMap<String, String>();
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        if(jsonObject.getJSONObject("data")!=null){
                            secretProtocolTitle = jsonObject.getJSONObject("data").getString("title");
                        }
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {

            }
        });
    }



}
