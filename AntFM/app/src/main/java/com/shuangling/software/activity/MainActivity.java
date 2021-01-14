package com.shuangling.software.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hjq.toast.ToastUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.mylhyl.circledialog.CircleDialog;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.arch.QMUIFragmentPagerAdapter;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIViewPager;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.dialog.InformationDialog;
import com.shuangling.software.dialog.NotificationDialog;
import com.shuangling.software.dialog.UpdateDialog;
import com.shuangling.software.entity.BottomMenu;
import com.shuangling.software.entity.City;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.UpdateInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.fragment.ColumnFragment;
import com.shuangling.software.fragment.DiscoverFragment;
import com.shuangling.software.fragment.PersonalCenterFragment;
import com.shuangling.software.fragment.PersonalCenterFragment01;
import com.shuangling.software.fragment.RadioListFragment;
import com.shuangling.software.fragment.RecommendFragment;
import com.shuangling.software.fragment.ServiceFragment;
import com.shuangling.software.manager.SkinManager;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.FloatWindowUtil;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.youngfeng.snake.annotations.EnableDragToClose;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jake.share.frdialog.dialog.FRDialog;
import cn.jake.share.frdialog.interfaces.FRDialogClickListener;
import io.reactivex.functions.Consumer;
import okhttp3.Call;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

@EnableDragToClose()
public class MainActivity extends QMUIActivity implements AMapLocationListener, Handler.Callback {
    public static final String TAG = "MainActivity";
    public static final int REQUEST_PERMISSION_CODE = 0x0110;
    private static final int REQUEST_EXTERNAL_STORAGE = 0x0111;
    //    private static final int REQUEST_EXTERNAL_STORAGE2 = 0x0112;
    public static final int MSG_GET_CITY_LIST = 0x1;
    public static final int MSG_GET_UPDATE_INFO = 0x2;
    public static final int MSG_GET_BOTTOM_MENUS = 0x3;

    @BindView(R.id.menuContainer)
    LinearLayout menuContainer;
    @BindView(R.id.pager)
    QMUIViewPager mViewPager;

    //    private Fragment serverFragment;
//    private Fragment radioListFragment;
//    private Fragment recommendFragment;
//    private Fragment discoverFragment;
//    private Fragment personalCenterFragment;
//    private Fragment columnFragment;
    private Handler mHandler;
    public static final String[] PERMISSION_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    public static City sCurrentCity;   //默认城市长沙
    public static List<City> sCityList;
    private static long lastClickTime;
    private static final int MIN_CLICK_DELAY_TIME = 2000;
    private String mApkDownloadUrl;
    private ArrayList<BottomMenuHolder> mMenus = new ArrayList<>();
    private List<BottomMenu> mBottomMenus;
    private UpdateDialog mUpdateDialog;
    public static boolean firstRun;


    private QMUISkinManager.OnSkinChangeListener mOnSkinChangeListener = new QMUISkinManager.OnSkinChangeListener() {
        @Override
        public void onSkinChange(QMUISkinManager skinManager, int oldSkin, int newSkin) {
            if (newSkin == SkinManager.SKIN_BLUE || newSkin == SkinManager.SKIN_PURPLE ||
                    newSkin == SkinManager.SKIN_RED || newSkin == SkinManager.SKIN_GREEN ||
                    newSkin == SkinManager.SKIN_ORANGE) {
                QMUIStatusBarHelper.setStatusBarLightMode(MainActivity.this);
            } else {
                QMUIStatusBarHelper.setStatusBarDarkMode(MainActivity.this);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //QMUISkinManager manager=getSkinManager();
        QMUISkinManager skinManager = QMUISkinManager.defaultInstance(this);
       // setSkinManager(skinManager);
        mOnSkinChangeListener.onSkinChange(skinManager, -1, skinManager.getCurrentSkin());
        //ImmersionBar.with(this).init();
        //ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(true).fitsSystemWindows(true).init();
        ButterKnife.bind(this);
        getBottomMenus();
        mHandler = new Handler(this);
        //showFragment(0);
        //showFloatWindowPermission();
        //getCityList();
        if (MyApplication.getInstance().getStation() != null && MyApplication.getInstance().getStation().getIs_league() == 0) {
            try {
                sCurrentCity = new City(Integer.parseInt(MyApplication.getInstance().getStation().getCity_info().getCode()), MyApplication.getInstance().getStation().getCity_info().getName(), "#");
                EventBus.getDefault().post(new CommonEvent("onLocationChanged"));
            } catch (Exception e) {
            }
        } else {
            initLocation();
            startLocation();
        }
        getUpdateInfo();
        Intent it = getIntent();
        if (null != it) {
            Uri uri = it.getData();
            if (null != uri) {
//                ToastUtils.show("|scheme-"+ uri.getScheme()
//                        + "|type-" + uri.getQueryParameter("type")
//                        + "|id-" + uri.getQueryParameter("id"));
                verifyUserInfo();
                String type = uri.getQueryParameter("type");
                String fromUrl = uri.getQueryParameter("from_url");
                String fromUserId = uri.getQueryParameter("from_user_id");
                String post_type = uri.getQueryParameter("post_type");
//保存fromUrl fromUserId到SharedPreferences
                SharedPreferencesUtils.putPreferenceTypeValue("from_url", SharedPreferencesUtils.PreferenceType.String, fromUrl);
                SharedPreferencesUtils.putPreferenceTypeValue("from_user_id", SharedPreferencesUtils.PreferenceType.String, fromUserId);
                if (type == null) {
                    return;
                }
                if (type.equals("1")) {
                    //音频
                    Intent intent = new Intent(this, AudioDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("audioId", Integer.parseInt(uri.getQueryParameter("id")));
                    startActivity(intent);
                } else if (type.equals("2")) {
                    //专辑
                    Intent intent = new Intent(this, AlbumDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("albumId", Integer.parseInt(uri.getQueryParameter("id")));
                    startActivity(intent);
                } else if (type.equals("3")) {
                    //文章
                    Intent intent = new Intent(this, ArticleDetailActivity02.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("articleId", Integer.parseInt(uri.getQueryParameter("id")));
                    startActivity(intent);
                } else if (type.equals("4")) {
                    //视频
                    Intent intent = new Intent(this, VideoDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("videoId", Integer.parseInt(uri.getQueryParameter("id")));
                    startActivity(intent);
                } else if (type.equals("5")) {
                    //专题
                    Intent intent = new Intent(this, SpecialDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("specialId", Integer.parseInt(uri.getQueryParameter("id")));
                    startActivity(intent);
                } else if (type.equals("6")) {
                    //图集
                    Intent intent = new Intent(this, GalleriaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("galleriaId", Integer.parseInt(uri.getQueryParameter("id")));
                    startActivity(intent);
                } else if (type.equals("98")) {
                    //机构
                    Intent intent = new Intent(this, WebViewBackActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    it.putExtra("url", ServerInfo.h5HttpsIP + "/orgs/" + Integer.parseInt(uri.getQueryParameter("id")));
                    startActivity(it);
                } else if (type.equals("99")) {
                    //主播
                    Intent intent = new Intent(this, WebViewBackActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    it.putExtra("url", ServerInfo.h5HttpsIP + "/anchors/" + Integer.parseInt(uri.getQueryParameter("id")));
                    startActivity(it);
                } else if (type.equals("12")) {//短视频
                    String original_id = uri.getQueryParameter("original_id");
                    String play_id = uri.getQueryParameter("play_id");
                    String id = uri.getQueryParameter("play_id");
                    if (id == null) {
                        //播放的是关联视频
                        if (original_id == null || play_id == null) {
                            return;
                        }
                    } else {
                        //播放的是当前视频
                        original_id = id;
                        play_id = id;
                    }
                    Intent intent = new Intent(this, AlivcLittleVideoActivity.class);
                    if (id == null) {
                        intent.putExtra("startType", AlivcLittleVideoActivity.START_TYPE_H5_SCHEME);
                    } else {
                        intent.putExtra("startType", AlivcLittleVideoActivity.START_TYPE_H5_WEBVIEW_CURRENT);
                    }
                    intent.putExtra("original_id", Integer.parseInt(original_id));
                    intent.putExtra("play_id", Integer.parseInt(play_id));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }
        startAudioService();
//        int permission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, PERMISSION_STORAGE, REQUEST_EXTERNAL_STORAGE2);
//        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (getSkinManager() != null) {
            getSkinManager().addSkinChangeListener(mOnSkinChangeListener);
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        if (getSkinManager() != null) {
            getSkinManager().removeSkinChangeListener(mOnSkinChangeListener);
        }
    }
    @Override
    protected void onNewIntent(Intent it) {
        super.onNewIntent(it);
        if (null != it) {
            Uri uri = it.getData();
            if (null != uri) {
//                ToastUtils.show("|scheme-"+ uri.getScheme()
//                        + "|type-" + uri.getQueryParameter("type")
//                        + "|id-" + uri.getQueryParameter("id"));
                String type = uri.getQueryParameter("type");
                String fromUrl = uri.getQueryParameter("from_url");
                String fromUserId = uri.getQueryParameter("from_user_id");
                String post_type = uri.getQueryParameter("post_type");
                //保存fromUrl fromUserId到SharedPreferences
                SharedPreferencesUtils.putPreferenceTypeValue("from_url", SharedPreferencesUtils.PreferenceType.String, fromUrl);
                SharedPreferencesUtils.putPreferenceTypeValue("from_user_id", SharedPreferencesUtils.PreferenceType.String, fromUserId);
                if (type == null) {
                    return;
                }
                if (type.equals("1")) {
                    //音频
                    Intent intent = new Intent(this, AudioDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("audioId", Integer.parseInt(uri.getQueryParameter("id")));
                    startActivity(intent);
                } else if (type.equals("2")) {
                    //专辑
                    Intent intent = new Intent(this, AlbumDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("albumId", Integer.parseInt(uri.getQueryParameter("id")));
                    startActivity(intent);
                } else if (type.equals("3")) {
                    //文章
                    Intent intent = new Intent(this, ArticleDetailActivity02.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("articleId", Integer.parseInt(uri.getQueryParameter("id")));
                    startActivity(intent);
                } else if (type.equals("4")) {
                    //视频
                    Intent intent = new Intent(this, VideoDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("videoId", Integer.parseInt(uri.getQueryParameter("id")));
                    startActivity(intent);
                } else if (type.equals("5")) {
                    //专题
                    Intent intent = new Intent(this, SpecialDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("specialId", Integer.parseInt(uri.getQueryParameter("id")));
                    startActivity(intent);
                } else if (type.equals("6")) {
                    //图集
                    Intent intent = new Intent(this, GalleriaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("galleriaId", Integer.parseInt(uri.getQueryParameter("id")));
                    startActivity(intent);
                } else if (type.equals("12")) {
                    String original_id = uri.getQueryParameter("original_id");
                    String play_id = uri.getQueryParameter("play_id");
                    String id = uri.getQueryParameter("id");
                    Intent intent = new Intent(this, AlivcLittleVideoActivity.class);
                    if (id == null) {
                        //播放的是关联视频
                        if (original_id == null || play_id == null) {
                            return;
                        }
                    } else {
                        //播放的是当前视频
                        if (original_id == null || play_id == null) {
                            original_id = id;
                            play_id = id;
                            intent.putExtra("startType", AlivcLittleVideoActivity.START_TYPE_H5_WEBVIEW_CURRENT);
                        } else {
                            intent.putExtra("startType", AlivcLittleVideoActivity.START_TYPE_H5_SCHEME);
                        }
                    }
                    intent.putExtra("original_id", Integer.parseInt(original_id));
                    intent.putExtra("play_id", Integer.parseInt(play_id));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }
    }

    private void verifyUserInfo() {
        User.setInstance(SharedPreferencesUtils.getUser());
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        if (SharedPreferencesUtils.getUser() != null) {
            pushService.bindAccount(SharedPreferencesUtils.getUser().getUsername(), new CommonCallback() {
                @Override
                public void onSuccess(String s) {
                    Log.i("bindAccount-onSuccess", s);
                }

                @Override
                public void onFailed(String s, String s1) {
                    Log.i("bindAccount-onFailed", s);
                    Log.i("bindAccount-onFailed", s1);
                }
            });
        }
        EventBus.getDefault().post(new CommonEvent("OnLoginSuccess"));
    }

    public void switchRecommend(Column column) {
//        mMenus.get(0).name.setSelected(true);
//        mMenus.get(0).icon.setSelected(true);
//        for (int i = 1; i < mMenus.size(); i++) {
//            BottomMenuHolder holder = mMenus.get(i);
//            holder.name.setSelected(false);
//            holder.icon.setSelected(false);
//        }
        for (int i = 0; i < mBottomMenus.size(); i++) {
            if (mBottomMenus.get(i).getType() == 1) {
                if (mViewPager != null && mViewPager.getAdapter().getCount() >= i + 1) {
                    mViewPager.setCurrentItem(i);
                    PagerAdapter f = mViewPager.getAdapter();
                    RecommendFragment recommendFragment = (RecommendFragment) f.instantiateItem(mViewPager, i);
                    recommendFragment.switchColumn(column);
                }
                break;

            }
        }


//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        if (recommendFragment == null) {
//            recommendFragment = new RecommendFragment();
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("column", column);
//            recommendFragment.setArguments(bundle);
//            transaction.add(R.id.content, recommendFragment);
//        } else {
//            transaction.show(recommendFragment);
//            ((RecommendFragment) recommendFragment).switchColumn(column);
//        }
//        if (personalCenterFragment != null && !personalCenterFragment.isHidden()) {
//            transaction.hide(personalCenterFragment);
//        }
//        if (serverFragment != null && !serverFragment.isHidden()) {
//            transaction.hide(serverFragment);
//        }
//        if (discoverFragment != null && !discoverFragment.isHidden()) {
//            transaction.hide(discoverFragment);
//        }
//        if (radioListFragment != null && !radioListFragment.isHidden()) {
//            transaction.hide(radioListFragment);
//        }
//        if (columnFragment != null && !columnFragment.isHidden()) {
//            transaction.hide(columnFragment);
//        }
//        transaction.commitAllowingStateLoss();
    }

    /**
     * 初始化定位，设置回调监听
     */
    private void initLocation() {
        //初始化client
        mLocationClient = new AMapLocationClient(this.getApplicationContext());
        // 设置定位监听
        mLocationClient.setLocationListener(this);
    }

    private void startLocation() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(needPermissions)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            if (isLocServiceEnable(MainActivity.this)) {
                                //checkWifiSetting();
                                //设置定位参数
                                mLocationClient.setLocationOption(getOption());
                                // 启动定位
                                mLocationClient.startLocation();
                            } else {
                                ToastUtils.show("定位失败,和位置相关的功能可能无法使用");
                                sCurrentCity = new City(4301, "长沙市", "C");
                                EventBus.getDefault().post(new CommonEvent("onLocationChanged"));
                            }
//checkWifiSetting();
                            //设置定位参数
//                            mLocationClient.setLocationOption(getOption());
//                            // 启动定位
//                            mLocationClient.startLocation();
                        } else {
                            ToastUtils.show("未能获取定位相关权限，和定位相关的功能可能无法使用");
                            sCurrentCity = new City(4301, "长沙市", "C");
                            EventBus.getDefault().post(new CommonEvent("onLocationChanged"));
                        }
                    }
                });
    }

    /**
     * 设置定位参数
     *
     * @return 定位参数类
     */
    private AMapLocationClientOption getOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setLocationCacheEnable(false);//设置是否返回缓存中位置，默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        return mOption;
    }

    /**
     * 检查wifi，并提示用户开启wifi
     */
    private void checkWifiSetting() {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("开启WIFI模块会提升定位准确性");
        builder.setPositiveButton("去开启", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("不了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showFloatWindowPermission() {
        FloatWindowUtil.getInstance().addOnPermissionListener(new FloatWindowUtil.OnPermissionListener() {
            @Override
            public void showPermissionDialog() {
                FRDialog dialog = new FRDialog.MDBuilder(MainActivity.this)
                        .setTitle("悬浮窗权限")
                        .setMessage("您的手机没有授予悬浮窗权限，请开启后再试")
                        .setPositiveContentAndListener("现在去开启", new FRDialogClickListener() {
                            @Override
                            public boolean onDialogClick(View view) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivityForResult(intent, REQUEST_PERMISSION_CODE);
                                }
                                return true;
                            }
                        }).setNegativeContentAndListener("暂不开启", new FRDialogClickListener() {
                            @Override
                            public boolean onDialogClick(View view) {
                                return true;
                            }
                        }).create();
                dialog.show();
            }
        });
        FloatWindowUtil.getInstance().setPermission();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (FloatWindowUtil.getInstance().checkFloatWindowPermission()) {
                    //FloatWindowUtil02.getInstance().showFloatWindow();
                } else {
                    //不显示悬浮窗 并提示
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        long id = Thread.currentThread().getId();
        int error = aMapLocation.getErrorCode();
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            String cityCode = aMapLocation.getAdCode();
            cityCode = cityCode.substring(0, 4);
//            for (int i = 0; i < sCityList.size(); i++) {
//                if (cityCode.equals(sCityList.get(i).getCode())) {
//                    if (sCurrentCity == null) {
//                        sCurrentCity = sCityList.get(i);
//                        //通知城市已改变
//                    }
//
//                    break;
//                }
//            }
//            if (sCurrentCity == null) {
            sCurrentCity = new City(Integer.parseInt(cityCode), aMapLocation.getCity(), "#");
            EventBus.getDefault().post(new CommonEvent("onLocationChanged"));
//            }else{
//                EventBus.getDefault().post(new CommonEvent("onLocationChanged"));
//            }
//            aMapLocation.getCountry();//国家信息
//            aMapLocation.getProvince();//省信息
//            aMapLocation.getCity();//城市信息
//            aMapLocation.getDistrict();//城区信息
//            aMapLocation.getStreet();//街道信息
//            aMapLocation.getStreetNum();//街道门牌号信息
//            aMapLocation.getCityCode();//城市编码
//            aMapLocation.getAdCode();//地区编码
//            aMapLocation.getAoiName();//获取当前定位点的AOI信息
        } else {
            String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
            Log.e("AmapErr", errText);
            ToastUtils.show("定位失败");
            sCurrentCity = new City(4301, "长沙市", "C");
            EventBus.getDefault().post(new CommonEvent("onLocationChanged"));
        }
    }

    public void getCityList() {
        String url = ServerInfo.serviceIP + ServerInfo.getCityList;
        OkHttpUtils.get(url, null, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_GET_CITY_LIST;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    public void getBottomMenus() {
        String url = ServerInfo.serviceIP + ServerInfo.bottomMenus;
        Map<String, String> params = new HashMap<>();
        params.put("version", "v" + getVersionName());
        params.put("type", "android");
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_GET_BOTTOM_MENUS;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    public void getUpdateInfo() {
        String url = ServerInfo.serviceIP + ServerInfo.updateInfoV2;
        Map<String, String> params = new HashMap<>();
        params.put("version", getVersionName());
        params.put("type", "android");
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_GET_UPDATE_INFO;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                String lastVersion = SharedPreferencesUtils.getStringValue("lastVersion", null);
                if (TextUtils.isEmpty(lastVersion) || !lastVersion.equals(getVersionName())) {
                    /**
                     * 如果第一次使用或者更新后第一次使用，则弹出通知对话框
                     */
                    SharedPreferencesUtils.putPreferenceTypeValue("lastVersion", SharedPreferencesUtils.PreferenceType.String, getVersionName());
                    NotificationManagerCompat manager = NotificationManagerCompat.from(MainActivity.this);
                    boolean pushMessage = manager.areNotificationsEnabled();
//boolean pushMessage = sp.getBoolean(PUSH_MESSAGE, true);
                    if (!pushMessage) {
                        NotificationDialog.getInstance().setOnOkClickListener(new NotificationDialog.OnOkClickListener() {
                            @Override
                            public void openNoticafition() {
                                Intent localIntent = new Intent();
                                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                if (Build.VERSION.SDK_INT >= 9) {
                                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                } else if (Build.VERSION.SDK_INT <= 8) {
                                    localIntent.setAction(Intent.ACTION_VIEW);
                                    localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                                    localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
                                }
                                startActivity(localIntent);
                            }
                        }).show(getSupportFragmentManager(), "NotificationDialog");
                    }
                }
                if (TextUtils.isEmpty(lastVersion)) {
                    InformationDialog.getInstance().show(getSupportFragmentManager(), "InformationDialog");
                }
//                if(firstRun){
//
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            InformationDialog.getInstance().show(getSupportFragmentManager(), "InformationDialog");
//
//                            NotificationManagerCompat manager = NotificationManagerCompat.from(MainActivity.this);
//                            boolean pushMessage = manager.areNotificationsEnabled();
//
//                            //boolean pushMessage = sp.getBoolean(PUSH_MESSAGE, true);
//                            if (!pushMessage) {
//                                NotificationDialog.getInstance().setOnOkClickListener(new NotificationDialog.OnOkClickListener() {
//                                    @Override
//                                    public void openNoticafition() {
//                                        Intent localIntent = new Intent();
//                                        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        if (Build.VERSION.SDK_INT >= 9) {
//                                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//                                            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
//                                        } else if (Build.VERSION.SDK_INT <= 8) {
//                                            localIntent.setAction(Intent.ACTION_VIEW);
//                                            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
//                                            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
//                                        }
//                                        startActivity(localIntent);
//                                    }
//                                }).show(getSupportFragmentManager(), "NotificationDialog");
//                            }
//
//                        }
//                    });
//
//
//
//                }
            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_CITY_LIST:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        JSONObject jo = jsonObject.getJSONObject("data");
                        sCityList = new ArrayList<>();
                        for (char i = 'A'; i <= 'Z'; i++) {
                            if (jo.containsKey("" + i)) {
                                sCityList.addAll(JSONArray.parseArray(jo.getJSONArray("" + i).toJSONString(), City.class));
                            }
                        }
                    }
                    initLocation();
                    startLocation();
                } catch (Exception e) {
                }
                break;
            case MSG_GET_UPDATE_INFO:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        final UpdateInfo updateInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), UpdateInfo.class);
                        if (updateInfo.getNew_version() != null) {
                            MyApplication.getInstance().findNewVerison = true;
                        }
                        if (updateInfo.isSupport()) {
                            mUpdateDialog = UpdateDialog.getInstance(updateInfo.getNew_version().getVersion(), updateInfo.getNew_version().getContent());
                            mUpdateDialog.setOnUpdateClickListener(new UpdateDialog.OnUpdateClickListener() {
                                @Override
                                public void download() {
                                    if (!TextUtils.isEmpty(updateInfo.getNew_version().getUrl())) {
                                        RxPermissions rxPermissions = new RxPermissions(MainActivity.this);
                                        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                .subscribe(new Consumer<Boolean>() {
                                                    @Override
                                                    public void accept(Boolean granted) throws Exception {
                                                        try {
                                                            if (granted) {
                                                                if (mUpdateDialog != null) {
                                                                    mUpdateDialog.dismiss();
                                                                }
                                                                downloadApk(updateInfo.getNew_version().getUrl());
                                                            } else {
                                                                ToastUtils.show("没有文件写权限，请开启该权限");
                                                            }
                                                        } catch (Exception e) {
                                                        }
                                                    }
                                                });
                                    } else {
                                        ToastUtils.show("下载地址有误");
                                    }
                                }
                            });
                            mUpdateDialog.show(getSupportFragmentManager(), "UpdateDialog");
                        } else {
                            String version = SharedPreferencesUtils.getStringValue("version", null);
                            if (TextUtils.isEmpty(version) || (updateInfo.getNew_version() != null && !updateInfo.getNew_version().getVersion().equals(version))) {
                                mUpdateDialog = UpdateDialog.getInstance(updateInfo.getNew_version().getVersion(), updateInfo.getNew_version().getContent());
                                mUpdateDialog.setOnUpdateClickListener(new UpdateDialog.OnUpdateClickListener() {
                                    @Override
                                    public void download() {
                                        if (mUpdateDialog != null) {
                                            mUpdateDialog.dismiss();
                                        }
                                        if (!TextUtils.isEmpty(updateInfo.getNew_version().getUrl())) {
                                            RxPermissions rxPermissions = new RxPermissions(MainActivity.this);
                                            rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                    .subscribe(new Consumer<Boolean>() {
                                                        @Override
                                                        public void accept(Boolean granted) throws Exception {
                                                            if (granted) {
                                                                downloadApk(updateInfo.getNew_version().getUrl());
                                                            } else {
                                                                ToastUtils.show("没有文件写权限，请开启该权限");
                                                            }
                                                        }
                                                    });
                                        } else {
                                            ToastUtils.show("下载地址有误");
                                        }
                                    }
                                });
                                mUpdateDialog.showNoUpdate(true);
                                mUpdateDialog.show(getSupportFragmentManager(), "UpdateDialog");
                            }
                        }
                    }
                    String lastVersion = SharedPreferencesUtils.getStringValue("lastVersion", null);
                    if (TextUtils.isEmpty(lastVersion) || !lastVersion.equals(getVersionName())) {
                        /**
                         * 如果第一次使用或者更新后第一次使用，则弹出通知对话框
                         */
                        SharedPreferencesUtils.putPreferenceTypeValue("lastVersion", SharedPreferencesUtils.PreferenceType.String, getVersionName());
                        NotificationManagerCompat manager = NotificationManagerCompat.from(MainActivity.this);
                        boolean pushMessage = manager.areNotificationsEnabled();
//boolean pushMessage = sp.getBoolean(PUSH_MESSAGE, true);
                        if (!pushMessage) {
                            NotificationDialog.getInstance().setOnOkClickListener(new NotificationDialog.OnOkClickListener() {
                                @Override
                                public void openNoticafition() {
                                    Intent localIntent = new Intent();
                                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    if (Build.VERSION.SDK_INT >= 9) {
                                        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                        localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                    } else if (Build.VERSION.SDK_INT <= 8) {
                                        localIntent.setAction(Intent.ACTION_VIEW);
                                        localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                                        localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
                                    }
                                    startActivity(localIntent);
                                }
                            }).show(getSupportFragmentManager(), "NotificationDialog");
                        }
                    }
                    if (TextUtils.isEmpty(lastVersion)) {
                        InformationDialog.getInstance().show(getSupportFragmentManager(), "InformationDialog");
                    }
//                    if(firstRun){
//                        InformationDialog.getInstance().show(getSupportFragmentManager(), "InformationDialog");;
//
//
//
//                    }
                } catch (Exception e) {
                }
                break;
            case MSG_GET_BOTTOM_MENUS:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        List<BottomMenu> menus = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), BottomMenu.class);
                        Iterator<BottomMenu> iterator = menus.iterator();
                        while (iterator.hasNext()) {
                            BottomMenu bottomMenu = iterator.next();
                            if (bottomMenu.getDisplay() == 0) {
                                iterator.remove();
                            }
                        }
                        if (menus != null && menus.size() > 0) {
                            mMenus.clear();
                            for (int i = 0; menus != null && i < menus.size(); i++) {
                                final BottomMenu bottomMenu = menus.get(i);
                                LayoutInflater inflater = LayoutInflater.from(this);
                                View root = inflater.inflate(R.layout.bottom_menu, menuContainer, false);

                                final BottomMenuHolder bottomMenuHolder = new BottomMenuHolder(root);
                                bottomMenuHolder.name.setText(bottomMenu.getName());
                                if (bottomMenu.getType() == 1) {
                                    //首页
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_index));
                                } else if (bottomMenu.getType() == 2) {
                                    //个人中心
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_personal_center));
                                } else if (bottomMenu.getType() == 3) {
                                    //媒体矩阵
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_media_matrix));
                                } else if (bottomMenu.getType() == 4) {
                                    //建言咨政
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_suggest_consult));
                                } else if (bottomMenu.getType() == 5) {
                                    //办事指南
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_affairs_guide));
                                } else if (bottomMenu.getType() == 6) {
                                    //便民服务
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_facilitate_people));
                                } else if (bottomMenu.getType() == 7) {
                                    //活动中心
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_activity_center));
                                } else if (bottomMenu.getType() == 8) {
                                    //电视
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_tv));
                                } else if (bottomMenu.getType() == 9) {
                                    //电台
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_radio));
                                } else if (bottomMenu.getType() == 10) {
                                    //资讯分类
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_news));
                                }
                                bottomMenuHolder.root.setTag(i);
                                bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        bottomMenuHolder.name.setSelected(true);
                                        bottomMenuHolder.icon.setSelected(true);
                                        for (int i = 0; i < mMenus.size(); i++) {
                                            BottomMenuHolder holder = mMenus.get(i);
                                            if (holder != bottomMenuHolder) {
                                                holder.name.setSelected(false);
                                                holder.icon.setSelected(false);
                                            }
                                        }

                                        int index = (int) v.getTag();
                                        if (mViewPager != null && mViewPager.getAdapter().getCount() >= index + 1) {
                                            mViewPager.setCurrentItem(index);
                                        }


                                    }
                                });


                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.weight = 1;
                                menuContainer.addView(root, params);

                                mMenus.add(bottomMenuHolder);
//                                if (bottomMenu.getType() == 1) {
//                                    //首页
//                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_index));
//                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            //
//                                            bottomMenuHolder.name.setSelected(true);
//                                            bottomMenuHolder.icon.setSelected(true);
//                                            for (int i = 0; i < mMenus.size(); i++) {
//                                                BottomMenuHolder holder = mMenus.get(i);
//                                                if (holder != bottomMenuHolder) {
//                                                    holder.name.setSelected(false);
//                                                    holder.icon.setSelected(false);
//                                                }
//                                            }
//                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                                            if (recommendFragment == null) {
//                                                recommendFragment = new RecommendFragment();
//                                                transaction.add(R.id.content, recommendFragment);
//                                            } else {
//                                                transaction.show(recommendFragment);
//                                            }
//                                            if (personalCenterFragment != null && !personalCenterFragment.isHidden()) {
//                                                transaction.hide(personalCenterFragment);
//                                            }
//                                            if (serverFragment != null && !serverFragment.isHidden()) {
//                                                transaction.hide(serverFragment);
//                                            }
//                                            if (discoverFragment != null && !discoverFragment.isHidden()) {
//                                                transaction.hide(discoverFragment);
//                                            }
//                                            if (radioListFragment != null && !radioListFragment.isHidden()) {
//                                                transaction.hide(radioListFragment);
//                                            }
//                                            if (columnFragment != null && !columnFragment.isHidden()) {
//                                                transaction.hide(columnFragment);
//                                            }
//                                            transaction.commitAllowingStateLoss();
//                                        }
//                                    });
//                                } else if (bottomMenu.getType() == 2) {
//                                    //个人中心
//                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_personal_center));
//                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            bottomMenuHolder.name.setSelected(true);
//                                            bottomMenuHolder.icon.setSelected(true);
//                                            for (int i = 0; i < mMenus.size(); i++) {
//                                                BottomMenuHolder holder = mMenus.get(i);
//                                                if (holder != bottomMenuHolder) {
//                                                    holder.name.setSelected(false);
//                                                    holder.icon.setSelected(false);
//                                                }
//                                            }
//                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                                            if (personalCenterFragment == null) {
////                                                personalCenterFragment = new PersonalCenterFragment01();
//                                                personalCenterFragment = new PersonalCenterFragment();// todo 4.0版重写‘我的’页面
//                                                transaction.add(R.id.content, personalCenterFragment);
//                                            } else {
//                                                transaction.show(personalCenterFragment);
//                                            }
//                                            if (recommendFragment != null && !recommendFragment.isHidden()) {
//                                                transaction.hide(recommendFragment);
//                                            }
//                                            if (serverFragment != null && !serverFragment.isHidden()) {
//                                                transaction.hide(serverFragment);
//                                            }
//                                            if (discoverFragment != null && !discoverFragment.isHidden()) {
//                                                transaction.hide(discoverFragment);
//                                            }
//                                            if (radioListFragment != null && !radioListFragment.isHidden()) {
//                                                transaction.hide(radioListFragment);
//                                            }
//                                            if (columnFragment != null && !columnFragment.isHidden()) {
//                                                transaction.hide(columnFragment);
//                                            }
//                                            transaction.commitAllowingStateLoss();
//                                        }
//                                    });
//                                } else if (bottomMenu.getType() == 3) {
//                                    //媒体矩阵
//                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_media_matrix));
//                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            bottomMenuHolder.name.setSelected(true);
//                                            bottomMenuHolder.icon.setSelected(true);
//                                            for (int i = 0; i < mMenus.size(); i++) {
//                                                BottomMenuHolder holder = mMenus.get(i);
//                                                if (holder != bottomMenuHolder) {
//                                                    holder.name.setSelected(false);
//                                                    holder.icon.setSelected(false);
//                                                }
//                                            }
//                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                                            if (discoverFragment == null) {
//                                                discoverFragment = new DiscoverFragment();
//                                                Bundle bundle = new Bundle();
//                                                bundle.putString("url", ServerInfo.h5HttpsIP + "/gover");
//                                                bundle.putString("title", bottomMenu.getName());
//                                                discoverFragment.setArguments(bundle);
//                                                transaction.add(R.id.content, discoverFragment);
//                                            } else {
//                                                ((DiscoverFragment) discoverFragment).jumpTo(ServerInfo.h5HttpsIP + "/gover", bottomMenu.getName(), false);
//                                                transaction.show(discoverFragment);
//                                            }
//                                            if (recommendFragment != null && !recommendFragment.isHidden()) {
//                                                transaction.hide(recommendFragment);
//                                            }
//                                            if (serverFragment != null && !serverFragment.isHidden()) {
//                                                transaction.hide(serverFragment);
//                                            }
//                                            if (personalCenterFragment != null && !personalCenterFragment.isHidden()) {
//                                                transaction.hide(personalCenterFragment);
//                                            }
//                                            if (radioListFragment != null && !radioListFragment.isHidden()) {
//                                                transaction.hide(radioListFragment);
//                                            }
//                                            if (columnFragment != null && !columnFragment.isHidden()) {
//                                                transaction.hide(columnFragment);
//                                            }
//                                            transaction.commitAllowingStateLoss();
////跳到媒体矩阵
//                                        }
//                                    });
//                                } else if (bottomMenu.getType() == 4) {
//                                    //建言咨政
//                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_suggest_consult));
//                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            //跳到建言咨政
//                                            bottomMenuHolder.name.setSelected(true);
//                                            bottomMenuHolder.icon.setSelected(true);
//                                            for (int i = 0; i < mMenus.size(); i++) {
//                                                BottomMenuHolder holder = mMenus.get(i);
//                                                if (holder != bottomMenuHolder) {
//                                                    holder.name.setSelected(false);
//                                                    holder.icon.setSelected(false);
//                                                }
//                                            }
//                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                                            if (discoverFragment == null) {
//                                                discoverFragment = new DiscoverFragment();
//                                                Bundle bundle = new Bundle();
//                                                bundle.putString("url", ServerInfo.h5HttpsIP + "/interact");
//                                                bundle.putString("title", bottomMenu.getName());
//                                                discoverFragment.setArguments(bundle);
//                                                transaction.add(R.id.content, discoverFragment);
//                                            } else {
//                                                ((DiscoverFragment) discoverFragment).jumpTo(ServerInfo.h5HttpsIP + "/interact", bottomMenu.getName(), false);
//                                                transaction.show(discoverFragment);
//                                            }
//                                            if (recommendFragment != null && !recommendFragment.isHidden()) {
//                                                transaction.hide(recommendFragment);
//                                            }
//                                            if (serverFragment != null && !serverFragment.isHidden()) {
//                                                transaction.hide(serverFragment);
//                                            }
//                                            if (personalCenterFragment != null && !personalCenterFragment.isHidden()) {
//                                                transaction.hide(personalCenterFragment);
//                                            }
//                                            if (radioListFragment != null && !radioListFragment.isHidden()) {
//                                                transaction.hide(radioListFragment);
//                                            }
//                                            if (columnFragment != null && !columnFragment.isHidden()) {
//                                                transaction.hide(columnFragment);
//                                            }
//                                            transaction.commitAllowingStateLoss();
//                                        }
//                                    });
//                                } else if (bottomMenu.getType() == 5) {
//                                    //办事指南
//                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_affairs_guide));
//                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            bottomMenuHolder.name.setSelected(true);
//                                            bottomMenuHolder.icon.setSelected(true);
//                                            for (int i = 0; i < mMenus.size(); i++) {
//                                                BottomMenuHolder holder = mMenus.get(i);
//                                                if (holder != bottomMenuHolder) {
//                                                    holder.name.setSelected(false);
//                                                    holder.icon.setSelected(false);
//                                                }
//                                            }
//                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                                            if (discoverFragment == null) {
//                                                discoverFragment = new DiscoverFragment();
//                                                Bundle bundle = new Bundle();
//                                                bundle.putString("url", ServerInfo.h5HttpsIP + "/guide");
//                                                bundle.putString("title", bottomMenu.getName());
//                                                discoverFragment.setArguments(bundle);
//                                                transaction.add(R.id.content, discoverFragment);
//                                            } else {
//                                                ((DiscoverFragment) discoverFragment).jumpTo(ServerInfo.h5HttpsIP + "/guide", bottomMenu.getName(), false);
//                                                transaction.show(discoverFragment);
//                                            }
//                                            if (recommendFragment != null && !recommendFragment.isHidden()) {
//                                                transaction.hide(recommendFragment);
//                                            }
//                                            if (serverFragment != null && !serverFragment.isHidden()) {
//                                                transaction.hide(serverFragment);
//                                            }
//                                            if (personalCenterFragment != null && !personalCenterFragment.isHidden()) {
//                                                transaction.hide(personalCenterFragment);
//                                            }
//                                            if (radioListFragment != null && !radioListFragment.isHidden()) {
//                                                transaction.hide(radioListFragment);
//                                            }
//                                            if (columnFragment != null && !columnFragment.isHidden()) {
//                                                transaction.hide(columnFragment);
//                                            }
//                                            transaction.commitAllowingStateLoss();
//                                        }
//                                    });
//                                } else if (bottomMenu.getType() == 6) {
//                                    //便民服务
//                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_facilitate_people));
//                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            bottomMenuHolder.name.setSelected(true);
//                                            bottomMenuHolder.icon.setSelected(true);
//                                            for (int i = 0; i < mMenus.size(); i++) {
//                                                BottomMenuHolder holder = mMenus.get(i);
//                                                if (holder != bottomMenuHolder) {
//                                                    holder.name.setSelected(false);
//                                                    holder.icon.setSelected(false);
//                                                }
//                                            }
//                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                                            if (serverFragment == null) {
//                                                serverFragment = new ServiceFragment();
//                                                Bundle bundle = new Bundle();
//                                                bundle.putString("title", bottomMenu.getName());
//                                                serverFragment.setArguments(bundle);
//                                                transaction.add(R.id.content, serverFragment);
//                                            } else {
//                                                transaction.show(serverFragment);
//                                            }
//                                            if (recommendFragment != null && !recommendFragment.isHidden()) {
//                                                transaction.hide(recommendFragment);
//                                            }
//                                            if (personalCenterFragment != null && !personalCenterFragment.isHidden()) {
//                                                transaction.hide(personalCenterFragment);
//                                            }
//                                            if (discoverFragment != null && !discoverFragment.isHidden()) {
//                                                transaction.hide(discoverFragment);
//                                            }
//                                            if (radioListFragment != null && !radioListFragment.isHidden()) {
//                                                transaction.hide(radioListFragment);
//                                            }
//                                            if (columnFragment != null && !columnFragment.isHidden()) {
//                                                transaction.hide(columnFragment);
//                                            }
//                                            transaction.commitAllowingStateLoss();
//                                        }
//                                    });
//                                } else if (bottomMenu.getType() == 7) {
//                                    //活动中心
//                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_activity_center));
//                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            bottomMenuHolder.name.setSelected(true);
//                                            bottomMenuHolder.icon.setSelected(true);
//                                            for (int i = 0; i < mMenus.size(); i++) {
//                                                BottomMenuHolder holder = mMenus.get(i);
//                                                if (holder != bottomMenuHolder) {
//                                                    holder.name.setSelected(false);
//                                                    holder.icon.setSelected(false);
//                                                }
//                                            }
//                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                                            if (discoverFragment == null) {
//                                                discoverFragment = new DiscoverFragment();
//                                                Bundle bundle = new Bundle();
//                                                bundle.putString("url", ServerInfo.activity + "activity");
//                                                bundle.putString("title", bottomMenu.getName());
//                                                bundle.putBoolean("showShare", true);
//                                                discoverFragment.setArguments(bundle);
//                                                transaction.add(R.id.content, discoverFragment);
//                                            } else {
//                                                ((DiscoverFragment) discoverFragment).jumpTo(ServerInfo.activity + "activity", bottomMenu.getName(), true);
//                                                transaction.show(discoverFragment);
//                                            }
//                                            if (recommendFragment != null && !recommendFragment.isHidden()) {
//                                                transaction.hide(recommendFragment);
//                                            }
//                                            if (serverFragment != null && !serverFragment.isHidden()) {
//                                                transaction.hide(serverFragment);
//                                            }
//                                            if (personalCenterFragment != null && !personalCenterFragment.isHidden()) {
//                                                transaction.hide(personalCenterFragment);
//                                            }
//                                            if (radioListFragment != null && !radioListFragment.isHidden()) {
//                                                transaction.hide(radioListFragment);
//                                            }
//                                            if (columnFragment != null && !columnFragment.isHidden()) {
//                                                transaction.hide(columnFragment);
//                                            }
//                                            transaction.commitAllowingStateLoss();
//                                        }
//                                    });
//                                } else if (bottomMenu.getType() == 8) {
//                                    //电视
//                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_tv));
//                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            synchronized (MainActivity.this) {
//                                                bottomMenuHolder.name.setSelected(true);
//                                                bottomMenuHolder.icon.setSelected(true);
//                                                for (int i = 0; i < mMenus.size(); i++) {
//                                                    BottomMenuHolder holder = mMenus.get(i);
//                                                    if (holder != bottomMenuHolder) {
//                                                        holder.name.setSelected(false);
//                                                        holder.icon.setSelected(false);
//                                                    }
//                                                }
//                                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                                                if (radioListFragment == null) {
//                                                    radioListFragment = new RadioListFragment();
//                                                    Bundle bundle = new Bundle();
//                                                    bundle.putString("type", "2");
//                                                    radioListFragment.setArguments(bundle);
//                                                    transaction.add(R.id.content, radioListFragment);
//                                                } else {
//                                                    ((RadioListFragment) radioListFragment).setType("2");
//                                                    transaction.show(radioListFragment);
//                                                }
//                                                if (recommendFragment != null && !recommendFragment.isHidden()) {
//                                                    transaction.hide(recommendFragment);
//                                                }
//                                                if (serverFragment != null && !serverFragment.isHidden()) {
//                                                    transaction.hide(serverFragment);
//                                                }
//                                                if (personalCenterFragment != null && !personalCenterFragment.isHidden()) {
//                                                    transaction.hide(personalCenterFragment);
//                                                }
//                                                if (discoverFragment != null && !discoverFragment.isHidden()) {
//                                                    transaction.hide(discoverFragment);
//                                                }
//                                                if (columnFragment != null && !columnFragment.isHidden()) {
//                                                    transaction.hide(columnFragment);
//                                                }
//                                                transaction.commitAllowingStateLoss();
//                                            }
//                                        }
//                                    });
//                                } else if (bottomMenu.getType() == 9) {
//                                    //电台
//                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_radio));
//                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            synchronized (MainActivity.this) {
//                                                bottomMenuHolder.name.setSelected(true);
//                                                bottomMenuHolder.icon.setSelected(true);
//                                                for (int i = 0; i < mMenus.size(); i++) {
//                                                    BottomMenuHolder holder = mMenus.get(i);
//                                                    if (holder != bottomMenuHolder) {
//                                                        holder.name.setSelected(false);
//                                                        holder.icon.setSelected(false);
//                                                    }
//                                                }
//                                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                                                if (radioListFragment == null) {
//                                                    radioListFragment = new RadioListFragment();
//                                                    Bundle bundle = new Bundle();
//                                                    bundle.putString("type", "1");
//                                                    radioListFragment.setArguments(bundle);
//                                                    transaction.add(R.id.content, radioListFragment);
//                                                } else {
//                                                    ((RadioListFragment) radioListFragment).setType("1");
//                                                    transaction.show(radioListFragment);
//                                                }
//                                                if (recommendFragment != null && !recommendFragment.isHidden()) {
//                                                    transaction.hide(recommendFragment);
//                                                }
//                                                if (serverFragment != null && !serverFragment.isHidden()) {
//                                                    transaction.hide(serverFragment);
//                                                }
//                                                if (personalCenterFragment != null && !personalCenterFragment.isHidden()) {
//                                                    transaction.hide(personalCenterFragment);
//                                                }
//                                                if (discoverFragment != null && !discoverFragment.isHidden()) {
//                                                    transaction.hide(discoverFragment);
//                                                }
//                                                if (columnFragment != null && !columnFragment.isHidden()) {
//                                                    transaction.hide(columnFragment);
//                                                }
//                                                transaction.commitAllowingStateLoss();
//                                            }
//
//                                        }
//                                    });
//                                } else if (bottomMenu.getType() == 10) {
//                                    //资讯分类
//                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_news));
//                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            Column column = null;
//                                            int columnId = Integer.parseInt(bottomMenu.getSource_id());
//                                            List<Column> allColumns = JSONObject.parseArray(SharedPreferencesUtils.getStringValue("all_column", null), Column.class);
//                                            for (int i = 0; allColumns != null && i < allColumns.size(); i++) {
//                                                if (allColumns.get(i).getId() == columnId) {
//                                                    column = allColumns.get(i);
//                                                    break;
//                                                }
//                                            }
//                                            if (column == null) {
//                                                column = new Column();
//                                                column.setId(Integer.parseInt(bottomMenu.getSource_id()));
//                                                column.setName(bottomMenu.getName());
//                                            }
//
//                                            bottomMenuHolder.name.setSelected(true);
//                                            bottomMenuHolder.icon.setSelected(true);
//                                            for (int i = 0; i < mMenus.size(); i++) {
//                                                BottomMenuHolder holder = mMenus.get(i);
//                                                if (holder != bottomMenuHolder) {
//                                                    holder.name.setSelected(false);
//                                                    holder.icon.setSelected(false);
//                                                }
//                                            }
//                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                                            if (columnFragment == null) {
//                                                columnFragment = new ColumnFragment();
//                                                Bundle bundle = new Bundle();
//                                                bundle.putSerializable("Column", column);
//                                                columnFragment.setArguments(bundle);
//                                                transaction.add(R.id.content, columnFragment);
//                                            } else {
//                                                ((ColumnFragment) columnFragment).setColumn(column);
//                                                transaction.show(columnFragment);
//                                            }
//                                            if (recommendFragment != null && !recommendFragment.isHidden()) {
//                                                transaction.hide(recommendFragment);
//                                            }
//                                            if (personalCenterFragment != null && !personalCenterFragment.isHidden()) {
//                                                transaction.hide(personalCenterFragment);
//                                            }
//                                            if (discoverFragment != null && !discoverFragment.isHidden()) {
//                                                transaction.hide(discoverFragment);
//                                            }
//                                            if (serverFragment != null && !serverFragment.isHidden()) {
//                                                transaction.hide(serverFragment);
//                                            }
//                                            if (radioListFragment != null && !radioListFragment.isHidden()) {
//                                                transaction.hide(radioListFragment);
//                                            }
//                                            transaction.commitAllowingStateLoss();
////                                            Column column=new Column();
////                                            column.setId(Integer.parseInt(bottomMenu.getSource_id()));
////                                            column.setName(bottomMenu.getName());
////                                            ((RecommendFragment)recommendFragment).switchColumn(column);
//                                        }
//                                    });
//                                }
//                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                                params.weight = 1;
//                                menuContainer.addView(root, params);
                            }

                            initPagers(menus);
                            if (mMenus.size() > 0) {
                                mMenus.get(0).root.performClick();
                            }
                        }
                    }
                } catch (Exception e) {
                }
                break;
        }
        return false;
    }

    @Override
    public void doOnBackPressed() {
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            ToastUtils.show("再按一次退出应用");
            lastClickTime = curClickTime;
        } else {
            lastClickTime = curClickTime;
            super.doOnBackPressed();
            //ComponentActivity.onBack
            //finish();
        }
    }

    public String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void downloadApk(final String downloadUrl) {
//        mApkDownloadUrl=downloadUrl;
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            int permission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            if (permission != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(MainActivity.this, PERMISSION_STORAGE, REQUEST_EXTERNAL_STORAGE);
//            } else {
//                //下载安装apk
//                downloadInstallApk();
//            }
//        }else{
//            //下载安装apk
//            downloadInstallApk();
//        }
        File file = new File(CommonUtils.getStoragePublicDirectory(DIRECTORY_DOWNLOADS) + File.separator + "ltsj.apk");
        if (file.exists()) {
            file.delete();
        }
        final CircleDialog.Builder builder = new CircleDialog.Builder();
        final DialogFragment dialogFragment = builder
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
//                                    .configDialog(params -> params.backgroundColor = Color.CYAN)
                .setTitle("下载")
                .setProgressText("已经下载")
                //.setProgressText("已经下载%s了")
                .setProgressDrawable(R.drawable.bg_progress_h)
                //.setNegative("取消", v -> timer.cancel())
                .show(getSupportFragmentManager());
//                            TimerTask timerTask = new TimerTask() {
//                                final int max = 222;
//                                int progress = 0;
//
//                                @Override
//                                public void run() {
//                                    progress++;
//                                    if (progress >= max) {
//                                        MainActivity.this.runOnUiThread(() -> {
//                                            builder.setProgressText("下载完成").refresh();
//                                            timer.cancel();
//                                        });
//                                    } else {
//                                        builder.setProgress(max, progress).refresh();
//                                    }
//                                }
//                            };
//                            timer.schedule(timerTask, 0, 50);
        final FileDownloadListener downloadListener = new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                Log.i("test", "pending");
            }

            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                Log.i("test", "connected");
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                //StyledDialog.updateProgress(dialog, (int)((long)soFarBytes * 100 / (long)totalBytes), 100, "素材下载中...", true);
                builder.setProgress(totalBytes, soFarBytes).create();
            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {
                try {
                    dialogFragment.dismiss();
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    File file = new File(CommonUtils.getStoragePublicDirectory(DIRECTORY_DOWNLOADS) + File.separator + "ltsj.apk");
                    boolean exist = file.exists();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        String packageName = getPackageName();
                        Uri contentUri = FileProvider.getUriForFile(MainActivity.this
                                , packageName + ".fileprovider"
                                , file);
                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    } else {
                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    }
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                Log.i("test", ex.toString());
            }

            @Override
            protected void completed(BaseDownloadTask task) {
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                Log.i("test", e.toString());
            }

            @Override
            protected void warn(BaseDownloadTask task) {
            }
        };
        final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(downloadListener);
        final List<BaseDownloadTask> tasks = new ArrayList<>();
        tasks.add(FileDownloader.getImpl().create(downloadUrl).setPath(CommonUtils.getStoragePublicDirectory(DIRECTORY_DOWNLOADS) + File.separator + "ltsj.apk"));
        //queueSet.setCallbackProgressMinInterval(200);
        //queueSet.disableCallbackProgressTimes();
        // 由于是队列任务, 这里是我们假设了现在不需要每个任务都回调`FileDownloadListener#progress`, 我们只关系每个任务是否完成, 所以这里这样设置可以很有效的减少ipc.
        // 所有任务在下载失败的时候都自动重试一次
        queueSet.setAutoRetryTimes(1);
        // 串行执行该任务队列
        queueSet.downloadSequentially(tasks);
        //queueSet.downloadTogether(tasks);
        queueSet.start();
    }

    public void downloadInstallApk() {
        File file = new File(CommonUtils.getStoragePublicDirectory(DIRECTORY_DOWNLOADS) + File.separator + "ltsj.apk");
        if (file.exists()) {
            file.delete();
        }
        final CircleDialog.Builder builder = new CircleDialog.Builder();
        final DialogFragment dialogFragment = builder
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
//                                    .configDialog(params -> params.backgroundColor = Color.CYAN)
                .setTitle("下载")
                .setProgressText("已经下载")
                //.setProgressText("已经下载%s了")
                .setProgressDrawable(R.drawable.bg_progress_h)
                //.setNegative("取消", v -> timer.cancel())
                .show(getSupportFragmentManager());
//                            TimerTask timerTask = new TimerTask() {
//                                final int max = 222;
//                                int progress = 0;
//
//                                @Override
//                                public void run() {
//                                    progress++;
//                                    if (progress >= max) {
//                                        MainActivity.this.runOnUiThread(() -> {
//                                            builder.setProgressText("下载完成").refresh();
//                                            timer.cancel();
//                                        });
//                                    } else {
//                                        builder.setProgress(max, progress).refresh();
//                                    }
//                                }
//                            };
//                            timer.schedule(timerTask, 0, 50);
        final FileDownloadListener downloadListener = new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                Log.i("test", "pending");
            }

            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                Log.i("test", "connected");
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                //StyledDialog.updateProgress(dialog, (int)((long)soFarBytes * 100 / (long)totalBytes), 100, "素材下载中...", true);
                builder.setProgress(totalBytes, soFarBytes).create();
            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {
                try {
                    dialogFragment.dismiss();
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    File file = new File(CommonUtils.getStoragePublicDirectory(DIRECTORY_DOWNLOADS) + File.separator + "ltsj.apk");
                    boolean exist = file.exists();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        String packageName = getPackageName();
                        Uri contentUri = FileProvider.getUriForFile(MainActivity.this
                                , packageName + ".fileprovider"
                                , file);
                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    } else {
                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    }
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                Log.i("test", ex.toString());
            }

            @Override
            protected void completed(BaseDownloadTask task) {
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                Log.i("test", e.toString());
            }

            @Override
            protected void warn(BaseDownloadTask task) {
            }
        };
        final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(downloadListener);
        final List<BaseDownloadTask> tasks = new ArrayList<>();
        tasks.add(FileDownloader.getImpl().create(mApkDownloadUrl).setPath(CommonUtils.getStoragePublicDirectory(DIRECTORY_DOWNLOADS) + File.separator + "ltsj.apk"));
        //queueSet.setCallbackProgressMinInterval(200);
        //queueSet.disableCallbackProgressTimes();
        // 由于是队列任务, 这里是我们假设了现在不需要每个任务都回调`FileDownloadListener#progress`, 我们只关系每个任务是否完成, 所以这里这样设置可以很有效的减少ipc.
        // 所有任务在下载失败的时候都自动重试一次
        queueSet.setAutoRetryTimes(1);
        // 串行执行该任务队列
        queueSet.downloadSequentially(tasks);
        //queueSet.downloadTogether(tasks);
        queueSet.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadInstallApk();
                } else {
                    // Permission Denied
                    ToastUtils.show("没有sd卡读写权限, 无法下载");
                }
                break;
//            case REQUEST_EXTERNAL_STORAGE2:
//                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    finish();
//                }
//                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        //if (gps || network) {
        if (gps) {
            return true;
        }
        return false;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    static class BottomMenuHolder {
        @BindView(R.id.icon)
        FontIconView icon;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.root)
        LinearLayout root;

        BottomMenuHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void showInformationTip() {
    }

    private void startAudioService() {
        if (AudioPlayerService.isRunning) {
            Intent it = new Intent(this, AudioPlayerService.class);
            startService(it);
        }
    }

    @Override
    public Resources getResources() {
        //return CommonUtils.setFontSize(this);
        return CommonUtils.setFontSize(super.getResources());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //if (newConfig.fontScale != 1)//非默认值
        if (newConfig.fontScale != Constant.SYSTEM_FONT_SCALE) {
            Constant.SYSTEM_FONT_SCALE = newConfig.fontScale;
            CommonUtils.setFontSize(getResources());
            EventBus.getDefault().post(new CommonEvent("onFontSizeChanged"));
        }
        if ((newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            // 假设 SKIN_2 为 Dark Mode 下的 skin
            SkinManager.changeSkin(SkinManager.SKIN_DARK_PURPLE);
        } else if (SkinManager.getCurrentSkin() == SkinManager.SKIN_DARK_PURPLE) {
            SkinManager.changeSkin(SkinManager.SKIN_PURPLE);
        }


        super.onConfigurationChanged(newConfig);
    }


    private void initPagers(List<BottomMenu> menus) {
        mBottomMenus = menus;
        QMUIFragmentPagerAdapter pagerAdapter = new QMUIFragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public QMUIFragment createFragment(int position) {
                BottomMenu bottomMenu = mBottomMenus.get(position);
                //switch (position+1) {
                switch (bottomMenu.getType()) {

                    case 1:
                        //首页
                        return new RecommendFragment();
                    case 2:
                        //个人中心
                        return new PersonalCenterFragment01();
                    case 3: {
                        //媒体矩阵
                        DiscoverFragment discoverFragment = new DiscoverFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("url", ServerInfo.h5HttpsIP + "/gover");
                        bundle.putString("title", bottomMenu.getName());
                        discoverFragment.setArguments(bundle);
                        return discoverFragment;
                    }
                    case 4: {
                        //建言咨政
                        DiscoverFragment discoverFragment = new DiscoverFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("url", ServerInfo.h5HttpsIP + "/interact");
                        bundle.putString("title", bottomMenu.getName());
                        discoverFragment.setArguments(bundle);
                        return discoverFragment;
                    }


                    case 5: {
                        //办事指南
                        DiscoverFragment discoverFragment = new DiscoverFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("url", ServerInfo.h5HttpsIP + "/guide");
                        bundle.putString("title", bottomMenu.getName());
                        discoverFragment.setArguments(bundle);
                        return discoverFragment;
                    }

                    case 6: {
                        //便民服务
                        ServiceFragment serverFragment = new ServiceFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("title", bottomMenu.getName());
                        serverFragment.setArguments(bundle);
                        return serverFragment;
                    }
                    case 7: {
                        //活动中心
                        DiscoverFragment discoverFragment = new DiscoverFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("url", ServerInfo.activity + "activity");
                        bundle.putString("title", bottomMenu.getName());
                        bundle.putBoolean("showShare", true);
                        discoverFragment.setArguments(bundle);
                    }
                    case 8: {
                        //电视
                        RadioListFragment radioListFragment = new RadioListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "2");
                        radioListFragment.setArguments(bundle);
                        return radioListFragment;
                    }

                    case 9: {
                        //电台
                        RadioListFragment radioListFragment = new RadioListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "1");
                        radioListFragment.setArguments(bundle);
                        return radioListFragment;
                    }

                    default: {
                        //资讯分类 10
                        Column column = null;
                        int columnId = Integer.parseInt(bottomMenu.getSource_id());
                        List<Column> allColumns = JSONObject.parseArray(SharedPreferencesUtils.getStringValue("all_column", null), Column.class);
                        for (int i = 0; allColumns != null && i < allColumns.size(); i++) {
                            if (allColumns.get(i).getId() == columnId) {
                                column = allColumns.get(i);
                                break;
                            }
                        }
                        if (column == null) {
                            column = new Column();
                            column.setId(Integer.parseInt(bottomMenu.getSource_id()));
                            column.setName(bottomMenu.getName());
                        }

                        ColumnFragment columnFragment = new ColumnFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Column", column);
                        columnFragment.setArguments(bundle);
                        return columnFragment;
                    }

                }

            }

            @Override
            public int getCount() {
                return mBottomMenus.size();
                //return 5;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "TabSegment";
                    case 1:
                        return "CTopBar";
                    case 2:
                        return "IViewPager";
                    case 3:
                    default:
                        return "ViewPager";
                }
            }
        };
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setEnableLoop(false);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mMenus != null && mMenus.size() >= position + 1) {
                    for (int i = 0; i < mMenus.size(); i++) {
                        BottomMenuHolder bottomMenu = mMenus.get(i);
                        if (position == i) {
                            bottomMenu.name.setSelected(true);
                            bottomMenu.icon.setSelected(true);
                        } else {
                            bottomMenu.name.setSelected(false);
                            bottomMenu.icon.setSelected(false);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //mTabSegment.setupWithViewPager(mViewPager);
    }
}
