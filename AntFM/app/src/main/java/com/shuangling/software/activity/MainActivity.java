package com.shuangling.software.activity;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.mylhyl.circledialog.CircleDialog;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.dialog.UpdateDialog;
import com.shuangling.software.entity.BottomMenu;
import com.shuangling.software.entity.City;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.UpdateInfo;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.fragment.DiscoverFragment;
import com.shuangling.software.fragment.PersonalCenterFragment;
import com.shuangling.software.fragment.RadioListFragment;
import com.shuangling.software.fragment.RecommendFragment;
import com.shuangling.software.fragment.ServiceFragment;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
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
public class MainActivity extends AppCompatActivity implements AMapLocationListener, Handler.Callback {

    public static final String TAG = "MainActivity";
    public static final int REQUEST_PERMISSION_CODE = 0x0110;
    public static final int MSG_GET_CITY_LIST = 0x1;
    public static final int MSG_GET_UPDATE_INFO = 0x2;
    public static final int MSG_GET_BOTTOM_MENUS = 0x3;

    //    @BindView(R.id.indexIcon)
//    FontIconView indexIcon;
//    @BindView(R.id.index)
//    TextView index;
//    @BindView(R.id.indexLayout)
//    LinearLayout indexLayout;
//    @BindView(R.id.recommendIcon)
//    FontIconView recommendIcon;
//    @BindView(R.id.recommend)
//    TextView recommend;
//    @BindView(R.id.recommendLayout)
//    LinearLayout recommendLayout;
//    @BindView(R.id.discoverIcon)
//    FontIconView discoverIcon;
//    @BindView(R.id.discover)
//    TextView discover;
//    @BindView(R.id.discoverLayout)
//    LinearLayout discoverLayout;
//    @BindView(R.id.personalCenterIcon)
//    FontIconView personalCenterIcon;
//    @BindView(R.id.personalCenter)
//    TextView personalCenter;
//    @BindView(R.id.personalCenterLayout)
//    LinearLayout personalCenterLayout;
    @BindView(R.id.menuContainer)
    LinearLayout menuContainer;


    private Fragment serverFragment;
    private Fragment radioListFragment;
    private Fragment recommendFragment;
    private Fragment discoverFragment;
    private Fragment personalCenterFragment;


    private Handler mHandler;

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


    ArrayList<BottomMenuHolder> mMenus=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImmersionBar.with(this).init();
        //ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(true).fitsSystemWindows(true).init();
        ButterKnife.bind(this);
        getBottomMenus();
        mHandler = new Handler(this);
        //showFragment(0);
        //showFloatWindowPermission();
        //getCityList();
        if (MyApplication.getInstance().getStation() != null && MyApplication.getInstance().getStation().getIs_league() == 0) {
            sCurrentCity = new City(Integer.parseInt(MyApplication.getInstance().getStation().getCity_info().getCode()), MyApplication.getInstance().getStation().getCity_info().getName(), "#");
            EventBus.getDefault().post(new CommonEvent("onLocationChanged"));
        } else {
            initLocation();
            startLocation();
        }


    }


    public void switchRecommend(Column column) {
//        settingBackgound(recommend);
//        settingBackgound(recommendIcon);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        if (recommendFragment == null) {
//            recommendFragment = new RecommendFragment();
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("column", column);
//            recommendFragment.setArguments(bundle);
//            transaction.add(R.id.content, recommendFragment);
//        } else {
//
//            transaction.show(recommendFragment);
//            ((RecommendFragment) recommendFragment).switchColumn(column);
//        }
//        hideFragments(transaction);
//        transaction.commit();
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


//    @OnClick({R.id.indexLayout, R.id.recommendLayout, R.id.discoverLayout, R.id.personalCenterLayout})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.indexLayout:
//                showFragment(0);
//                break;
//            case R.id.recommendLayout:
//                showFragment(1);
//                break;
//            case R.id.discoverLayout:
//                showFragment(2);
//                break;
//            case R.id.personalCenterLayout:
//                showFragment(3);
//                break;
//        }
//    }


    private void settingBackgound(TextView table) {
//        table.setSelected(true);
//        if (table != index) {
//            index.setSelected(false);
//        }
//        if (table != recommend) {
//            recommend.setSelected(false);
//        }
//        if (table != discover) {
//            discover.setSelected(false);
//        }
//        if (table != personalCenter) {
//            personalCenter.setSelected(false);
//        }

    }

    private void settingBackgound(FontIconView table) {
//        table.setSelected(true);
//        if (table != indexIcon) {
//            indexIcon.setSelected(false);
//        }
//        if (table != recommendIcon) {
//            recommendIcon.setSelected(false);
//        }
//        if (table != discoverIcon) {
//            discoverIcon.setSelected(false);
//        }
//        if (table != personalCenterIcon) {
//            personalCenterIcon.setSelected(false);
//        }

    }


    private void hideFragments(FragmentTransaction transaction) {


//        if (!index.isSelected()) {
//            if (indexFragment != null) {
//                transaction.hide(indexFragment);
//            }
//        }
//        if (!recommend.isSelected()) {
//            if (recommendFragment != null) {
//                transaction.hide(recommendFragment);
//            }
//
//        }
//        if (!discover.isSelected()) {
//            if (discoverFragment != null) {
//                transaction.hide(discoverFragment);
//            }
//
//        }
//        if (!personalCenter.isSelected()) {
//            if (personalCenterFragment != null) {
//                transaction.hide(personalCenterFragment);
//            }
//        }

    }


    private void showFragment(int order) {
//        if (order == 0) {
//            settingBackgound(index);
//            settingBackgound(indexIcon);
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            if (indexFragment == null) {
//                indexFragment = new IndexFragment();
//                transaction.add(R.id.content, indexFragment);
//            } else {
//                transaction.show(indexFragment);
//            }
//            hideFragments(transaction);
//            transaction.commit();
//        } else if (order == 1) {
//            settingBackgound(recommend);
//            settingBackgound(recommendIcon);
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            if (recommendFragment == null) {
//                recommendFragment = new RecommendFragment();
//                transaction.add(R.id.content, recommendFragment);
//            } else {
//                transaction.show(recommendFragment);
//            }
//            hideFragments(transaction);
//            transaction.commit();
//        } else if (order == 2) {
//            settingBackgound(discover);
//            settingBackgound(discoverIcon);
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            if (discoverFragment == null) {
//                discoverFragment = new DiscoverFragment();
//                transaction.add(R.id.content, discoverFragment);
//            } else {
//                transaction.show(discoverFragment);
//            }
//            hideFragments(transaction);
//            transaction.commit();
//        } else if (order == 3) {
//            settingBackgound(personalCenter);
//            settingBackgound(personalCenterIcon);
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            if (personalCenterFragment == null) {
//                personalCenterFragment = new PersonalCenterFragment();
//                transaction.add(R.id.content, personalCenterFragment);
//            } else {
//                transaction.show(personalCenterFragment);
//            }
//            hideFragments(transaction);
//            transaction.commit();
//        }
    }

    @Override
    protected void onResume() {
        getUpdateInfo();
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
                        if(updateInfo.getNew_version()!=null){
                            MyApplication.getInstance().findNewVerison=true;
                        }
                        if (updateInfo.isSupport()) {
                            UpdateDialog dialog = UpdateDialog.getInstance(updateInfo.getNew_version().getVersion(), updateInfo.getNew_version().getContent());
                            dialog.setOnUpdateClickListener(new UpdateDialog.OnUpdateClickListener() {
                                @Override
                                public void download() {
                                    if(!TextUtils.isEmpty(updateInfo.getNew_version().getUrl())){
                                        downloadApk(updateInfo.getNew_version().getUrl());
                                    }else {
                                        ToastUtils.show("下载地址有误");
                                    }
                                }
                            });
                            dialog.show(getSupportFragmentManager(), "UpdateDialog");
                        }else{
                            String version=SharedPreferencesUtils.getStringValue("version",null);
                            if(TextUtils.isEmpty(version)||(updateInfo.getNew_version()!=null&&!updateInfo.getNew_version().getVersion().equals(version))){
                                UpdateDialog dialog = UpdateDialog.getInstance(updateInfo.getNew_version().getVersion(), updateInfo.getNew_version().getContent());
                                dialog.setOnUpdateClickListener(new UpdateDialog.OnUpdateClickListener() {
                                    @Override
                                    public void download() {
                                        if(!TextUtils.isEmpty(updateInfo.getNew_version().getUrl())){
                                            downloadApk(updateInfo.getNew_version().getUrl());
                                        }else {
                                            ToastUtils.show("下载地址有误");
                                        }

                                    }
                                });
                                dialog.showNoUpdate(true);
                                dialog.show(getSupportFragmentManager(), "UpdateDialog");
                            }
                        }

                    }


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
                            if (bottomMenu.getDisplay()==0) {
                                iterator.remove();
                            }
                        }
                        if(menus!=null&&menus.size()>0){
                           mMenus.clear();


                            for (int i = 0; menus != null && i < menus.size(); i++) {
                                final BottomMenu bottomMenu = menus.get(i);
                                LayoutInflater inflater = LayoutInflater.from(this);
                                View root = inflater.inflate(R.layout.bottom_menu, menuContainer, false);
                                final FontIconView iconView = root.findViewById(R.id.icon);
                                final TextView name = root.findViewById(R.id.name);
                                name.setText(bottomMenu.getName());
                                final BottomMenuHolder bottomMenuHolder=new BottomMenuHolder(root);
                                mMenus.add(bottomMenuHolder);
                                if (bottomMenu.getType() == 1) {
                                    //首页
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_index));
                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //
                                            bottomMenuHolder.name.setSelected(true);
                                            bottomMenuHolder.icon.setSelected(true);
                                            for(int i=0;i<mMenus.size();i++){
                                                BottomMenuHolder holder=mMenus.get(i);
                                                if(holder!=bottomMenuHolder){
                                                    holder.name.setSelected(false);
                                                    holder.icon.setSelected(false);
                                                }
                                            }

                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                            if (recommendFragment == null) {
                                                recommendFragment = new RecommendFragment();
                                                transaction.add(R.id.content, recommendFragment);
                                            } else {
                                                transaction.show(recommendFragment);
                                            }
                                            if(personalCenterFragment!=null&&!personalCenterFragment.isHidden()){
                                                transaction.hide(personalCenterFragment);
                                            }
                                            if(serverFragment!=null&&!serverFragment.isHidden()){
                                                transaction.hide(serverFragment);
                                            }
                                            if(discoverFragment!=null&&!discoverFragment.isHidden()){
                                                transaction.hide(discoverFragment);
                                            }
                                            if(radioListFragment!=null&&!radioListFragment.isHidden()){
                                                transaction.hide(radioListFragment);
                                            }

                                            transaction.commit();
                                        }
                                    });
                                } else if (bottomMenu.getType() == 2) {
                                    //个人中心
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_personal_center));
                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //
                                            bottomMenuHolder.name.setSelected(true);
                                            bottomMenuHolder.icon.setSelected(true);
                                            for(int i=0;i<mMenus.size();i++){
                                                BottomMenuHolder holder=mMenus.get(i);
                                                if(holder!=bottomMenuHolder){
                                                    holder.name.setSelected(false);
                                                    holder.icon.setSelected(false);
                                                }
                                            }

                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                            if (personalCenterFragment == null) {
                                                personalCenterFragment = new PersonalCenterFragment();
                                                transaction.add(R.id.content, personalCenterFragment);
                                            } else {
                                                transaction.show(personalCenterFragment);
                                            }
                                            if(recommendFragment!=null&&!recommendFragment.isHidden()){
                                                transaction.hide(recommendFragment);
                                            }
                                            if(serverFragment!=null&&!serverFragment.isHidden()){
                                                transaction.hide(serverFragment);
                                            }
                                            if(discoverFragment!=null&&!discoverFragment.isHidden()){
                                                transaction.hide(discoverFragment);
                                            }
                                            if(radioListFragment!=null&&!radioListFragment.isHidden()){
                                                transaction.hide(radioListFragment);
                                            }
                                            transaction.commit();
                                        }
                                    });
                                } else if (bottomMenu.getType() == 3) {
                                    //媒体矩阵
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_media_matrix));
                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            bottomMenuHolder.name.setSelected(true);
                                            bottomMenuHolder.icon.setSelected(true);
                                            for(int i=0;i<mMenus.size();i++){
                                                BottomMenuHolder holder=mMenus.get(i);
                                                if(holder!=bottomMenuHolder){
                                                    holder.name.setSelected(false);
                                                    holder.icon.setSelected(false);
                                                }
                                            }

                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                            if (discoverFragment == null) {
                                                discoverFragment = new DiscoverFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("url", ServerInfo.h5HttpsIP + "/gover");
                                                bundle.putString("title",bottomMenu.getName());
                                                discoverFragment.setArguments(bundle);
                                                transaction.add(R.id.content, discoverFragment);
                                            } else {

                                                ((DiscoverFragment)discoverFragment).jumpTo(ServerInfo.h5HttpsIP + "/gover",bottomMenu.getName());
                                                transaction.show(discoverFragment);
                                            }
                                            if(recommendFragment!=null&&!recommendFragment.isHidden()){
                                                transaction.hide(recommendFragment);
                                            }
                                            if(serverFragment!=null&&!serverFragment.isHidden()){
                                                transaction.hide(serverFragment);
                                            }
                                            if(personalCenterFragment!=null&&!personalCenterFragment.isHidden()){
                                                transaction.hide(personalCenterFragment);
                                            }
                                            if(radioListFragment!=null&&!radioListFragment.isHidden()){
                                                transaction.hide(radioListFragment);
                                            }
                                            transaction.commit();


                                            //跳到媒体矩阵
                                        }
                                    });
                                } else if (bottomMenu.getType() == 4) {
                                    //建言咨政
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_suggest_consult));
                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //跳到建言咨政
                                            bottomMenuHolder.name.setSelected(true);
                                            bottomMenuHolder.icon.setSelected(true);
                                            for(int i=0;i<mMenus.size();i++){
                                                BottomMenuHolder holder=mMenus.get(i);
                                                if(holder!=bottomMenuHolder){
                                                    holder.name.setSelected(false);
                                                    holder.icon.setSelected(false);
                                                }
                                            }

                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                            if (discoverFragment == null) {
                                                discoverFragment = new DiscoverFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("url", ServerInfo.h5HttpsIP + "/interact");
                                                bundle.putString("title",bottomMenu.getName());
                                                discoverFragment.setArguments(bundle);
                                                transaction.add(R.id.content, discoverFragment);
                                            } else {

                                                ((DiscoverFragment)discoverFragment).jumpTo(ServerInfo.h5HttpsIP + "/interact",bottomMenu.getName());
                                                transaction.show(discoverFragment);
                                            }
                                            if(recommendFragment!=null&&!recommendFragment.isHidden()){
                                                transaction.hide(recommendFragment);
                                            }
                                            if(serverFragment!=null&&!serverFragment.isHidden()){
                                                transaction.hide(serverFragment);
                                            }
                                            if(personalCenterFragment!=null&&!personalCenterFragment.isHidden()){
                                                transaction.hide(personalCenterFragment);
                                            }
                                            if(radioListFragment!=null&&!radioListFragment.isHidden()){
                                                transaction.hide(radioListFragment);
                                            }
                                            transaction.commit();
                                        }
                                    });
                                } else if (bottomMenu.getType() == 5) {
                                    //办事指南
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_affairs_guide));
                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            bottomMenuHolder.name.setSelected(true);
                                            bottomMenuHolder.icon.setSelected(true);
                                            for(int i=0;i<mMenus.size();i++){
                                                BottomMenuHolder holder=mMenus.get(i);
                                                if(holder!=bottomMenuHolder){
                                                    holder.name.setSelected(false);
                                                    holder.icon.setSelected(false);
                                                }
                                            }

                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                            if (discoverFragment == null) {
                                                discoverFragment = new DiscoverFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("url", ServerInfo.h5HttpsIP + "/guide");
                                                bundle.putString("title",bottomMenu.getName());
                                                discoverFragment.setArguments(bundle);
                                                transaction.add(R.id.content, discoverFragment);
                                            } else {

                                                ((DiscoverFragment)discoverFragment).jumpTo(ServerInfo.h5HttpsIP + "/guide",bottomMenu.getName());
                                                transaction.show(discoverFragment);
                                            }
                                            if(recommendFragment!=null&&!recommendFragment.isHidden()){
                                                transaction.hide(recommendFragment);
                                            }
                                            if(serverFragment!=null&&!serverFragment.isHidden()){
                                                transaction.hide(serverFragment);
                                            }
                                            if(personalCenterFragment!=null&&!personalCenterFragment.isHidden()){
                                                transaction.hide(personalCenterFragment);
                                            }
                                            if(radioListFragment!=null&&!radioListFragment.isHidden()){
                                                transaction.hide(radioListFragment);
                                            }
                                            transaction.commit();
                                        }
                                    });
                                } else if (bottomMenu.getType() == 6) {
                                    //便民服务
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_facilitate_people));
                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            bottomMenuHolder.name.setSelected(true);
                                            bottomMenuHolder.icon.setSelected(true);
                                            for(int i=0;i<mMenus.size();i++){
                                                BottomMenuHolder holder=mMenus.get(i);
                                                if(holder!=bottomMenuHolder){
                                                    holder.name.setSelected(false);
                                                    holder.icon.setSelected(false);
                                                }
                                            }

                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                            if (serverFragment == null) {
                                                serverFragment = new ServiceFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("title",bottomMenu.getName());
                                                serverFragment.setArguments(bundle);
                                                transaction.add(R.id.content, serverFragment);
                                            } else {
                                                transaction.show(serverFragment);
                                            }
                                            if(recommendFragment!=null&&!recommendFragment.isHidden()){
                                                transaction.hide(recommendFragment);
                                            }
                                            if(personalCenterFragment!=null&&!personalCenterFragment.isHidden()){
                                                transaction.hide(personalCenterFragment);
                                            }
                                            if(discoverFragment!=null&&!discoverFragment.isHidden()){
                                                transaction.hide(discoverFragment);
                                            }
                                            if(radioListFragment!=null&&!radioListFragment.isHidden()){
                                                transaction.hide(radioListFragment);
                                            }
                                            transaction.commit();
                                        }
                                    });
                                } else if (bottomMenu.getType() == 7) {
                                    //活动中心
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_activity_center));
                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            bottomMenuHolder.name.setSelected(true);
                                            bottomMenuHolder.icon.setSelected(true);
                                            for(int i=0;i<mMenus.size();i++){
                                                BottomMenuHolder holder=mMenus.get(i);
                                                if(holder!=bottomMenuHolder){
                                                    holder.name.setSelected(false);
                                                    holder.icon.setSelected(false);
                                                }
                                            }

                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                            if (discoverFragment == null) {
                                                discoverFragment = new DiscoverFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("url", ServerInfo.activity + "activity");
                                                bundle.putString("title",bottomMenu.getName());
                                                discoverFragment.setArguments(bundle);
                                                transaction.add(R.id.content, discoverFragment);
                                            } else {

                                                ((DiscoverFragment)discoverFragment).jumpTo(ServerInfo.activity + "activity",bottomMenu.getName());
                                                transaction.show(discoverFragment);
                                            }
                                            if(recommendFragment!=null&&!recommendFragment.isHidden()){
                                                transaction.hide(recommendFragment);
                                            }
                                            if(serverFragment!=null&&!serverFragment.isHidden()){
                                                transaction.hide(serverFragment);
                                            }
                                            if(personalCenterFragment!=null&&!personalCenterFragment.isHidden()){
                                                transaction.hide(personalCenterFragment);
                                            }
                                            if(radioListFragment!=null&&!radioListFragment.isHidden()){
                                                transaction.hide(radioListFragment);
                                            }
                                            transaction.commit();
                                        }
                                    });
                                } else if (bottomMenu.getType() == 8) {
                                    //电视
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_tv));
                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            bottomMenuHolder.name.setSelected(true);
                                            bottomMenuHolder.icon.setSelected(true);
                                            for(int i=0;i<mMenus.size();i++){
                                                BottomMenuHolder holder=mMenus.get(i);
                                                if(holder!=bottomMenuHolder){
                                                    holder.name.setSelected(false);
                                                    holder.icon.setSelected(false);
                                                }
                                            }

                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                            if (radioListFragment == null) {
                                                radioListFragment = new RadioListFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("type","2");
                                                radioListFragment.setArguments(bundle);
                                                transaction.add(R.id.content, radioListFragment);
                                            } else {
                                                ((RadioListFragment)radioListFragment).setType("2");
                                                transaction.show(radioListFragment);
                                            }
                                            if(recommendFragment!=null&&!recommendFragment.isHidden()){
                                                transaction.hide(recommendFragment);
                                            }
                                            if(serverFragment!=null&&!serverFragment.isHidden()){
                                                transaction.hide(serverFragment);
                                            }
                                            if(personalCenterFragment!=null&&!personalCenterFragment.isHidden()){
                                                transaction.hide(personalCenterFragment);
                                            }
                                            if(discoverFragment!=null&&!discoverFragment.isHidden()){
                                                transaction.hide(discoverFragment);
                                            }

                                            transaction.commit();



//                                        Intent it=new Intent(MainActivity.this,RadioListActivity.class);
//                                        it.putExtra("type","2");
//                                        startActivity(it);
                                        }
                                    });
                                } else if (bottomMenu.getType() == 9) {
                                    //电台
                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_radio));
                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            bottomMenuHolder.name.setSelected(true);
                                            bottomMenuHolder.icon.setSelected(true);
                                            for(int i=0;i<mMenus.size();i++){
                                                BottomMenuHolder holder=mMenus.get(i);
                                                if(holder!=bottomMenuHolder){
                                                    holder.name.setSelected(false);
                                                    holder.icon.setSelected(false);
                                                }
                                            }

                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                            if (radioListFragment == null) {
                                                radioListFragment = new RadioListFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("type","1");
                                                radioListFragment.setArguments(bundle);
                                                transaction.add(R.id.content, radioListFragment);
                                            } else {
                                                ((RadioListFragment)radioListFragment).setType("1");
                                                transaction.show(radioListFragment);
                                            }
                                            if(recommendFragment!=null&&!recommendFragment.isHidden()){
                                                transaction.hide(recommendFragment);
                                            }
                                            if(serverFragment!=null&&!serverFragment.isHidden()){
                                                transaction.hide(serverFragment);
                                            }
                                            if(personalCenterFragment!=null&&!personalCenterFragment.isHidden()){
                                                transaction.hide(personalCenterFragment);
                                            }
                                            if(discoverFragment!=null&&!discoverFragment.isHidden()){
                                                transaction.hide(discoverFragment);
                                            }

                                            transaction.commit();
//                                        Intent it=new Intent(MainActivity.this,RadioListActivity.class);
//                                        it.putExtra("type","1");
//                                        startActivity(it);
                                        }
                                    });
                                } else if (bottomMenu.getType() == 10) {
                                    //资讯分类

                                    bottomMenuHolder.icon.setText(getResources().getString(R.string.menus_news));
                                    bottomMenuHolder.root.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            bottomMenuHolder.name.setSelected(true);
                                            bottomMenuHolder.icon.setSelected(true);
                                            for(int i=0;i<mMenus.size();i++){
                                                BottomMenuHolder holder=mMenus.get(i);
                                                if(holder!=bottomMenuHolder){
                                                    holder.name.setSelected(false);
                                                    holder.icon.setSelected(false);
                                                }
                                            }


                                            bottomMenuHolder.name.setSelected(true);
                                            bottomMenuHolder.icon.setSelected(true);
                                            for(int i=0;i<mMenus.size();i++){
                                                BottomMenuHolder holder=mMenus.get(i);
                                                if(holder!=bottomMenuHolder){
                                                    holder.name.setSelected(false);
                                                    holder.icon.setSelected(false);
                                                }
                                            }

                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                            if (recommendFragment == null) {
                                                recommendFragment = new RecommendFragment();
                                                transaction.add(R.id.content, recommendFragment);
                                            } else {
                                                transaction.show(recommendFragment);
                                            }
                                            if(personalCenterFragment!=null&&!personalCenterFragment.isHidden()){
                                                transaction.hide(personalCenterFragment);
                                            }
                                            if(discoverFragment!=null&&!discoverFragment.isHidden()){
                                                transaction.hide(discoverFragment);
                                            }

                                            if(serverFragment!=null&&!serverFragment.isHidden()){
                                                transaction.hide(serverFragment);
                                            }
                                            if(radioListFragment!=null&&!radioListFragment.isHidden()){
                                                transaction.hide(radioListFragment);
                                            }


                                            transaction.commit();





                                            Column column=new Column();
                                            column.setId(Integer.parseInt(bottomMenu.getSource_id()));
                                            column.setName(bottomMenu.getName());
                                            ((RecommendFragment)recommendFragment).switchColumn(column);
                                        }
                                    });
                                }
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.weight = 1;
                                menuContainer.addView(root, params);

                            }

                            if(mMenus.size()>0){
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
    public void onBackPressed() {
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            ToastUtils.show("再按一次退出应用");
            lastClickTime = curClickTime;
        } else {
            lastClickTime = curClickTime;
            super.onBackPressed();
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


        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {

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

                    }
                });

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
}
