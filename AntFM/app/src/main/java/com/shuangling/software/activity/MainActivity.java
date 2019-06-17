package com.shuangling.software.activity;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.entity.City;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.fragment.DiscoverFragment;
import com.shuangling.software.fragment.IndexFragment;
import com.shuangling.software.fragment.PersonalCenterFragment;
import com.shuangling.software.fragment.RecommendFragment;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.FloatWindowUtil;
import com.shuangling.software.utils.ServerInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jake.share.frdialog.dialog.FRDialog;
import cn.jake.share.frdialog.interfaces.FRDialogClickListener;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements AMapLocationListener, Handler.Callback {

    public static final String TAG = "MainActivity";
    public static final int REQUEST_PERMISSION_CODE = 0x0110;
    public static final int MSG_GET_CITY_LIST = 0x1;
    @BindView(R.id.indexIcon)
    FontIconView indexIcon;
    @BindView(R.id.index)
    TextView index;
    @BindView(R.id.indexLayout)
    LinearLayout indexLayout;
    @BindView(R.id.recommendIcon)
    FontIconView recommendIcon;
    @BindView(R.id.recommend)
    TextView recommend;
    @BindView(R.id.recommendLayout)
    LinearLayout recommendLayout;
    @BindView(R.id.discoverIcon)
    FontIconView discoverIcon;
    @BindView(R.id.discover)
    TextView discover;
    @BindView(R.id.discoverLayout)
    LinearLayout discoverLayout;
    @BindView(R.id.personalCenterIcon)
    FontIconView personalCenterIcon;
    @BindView(R.id.personalCenter)
    TextView personalCenter;
    @BindView(R.id.personalCenterLayout)
    LinearLayout personalCenterLayout;

//    @BindView(R.id.index)
//    TextView index;
//    @BindView(R.id.recommend)
//    TextView recommend;
//    @BindView(R.id.discover)
//    TextView discover;
//    @BindView(R.id.personalCenter)
//    TextView personalCenter;
//    @BindView(R.id.content)
//    FrameLayout content;


    private Fragment indexFragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(MyApplication.getInstance().getCurrentTheme());
        setContentView(R.layout.activity_main);
//        StatusBarUtil.setTransparent(this);
//        StatusBarManager.setImmersiveStatusBar(this, true);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        showFragment(0);
        showFloatWindow();
        getCityList();

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
                            checkWifiSetting();
                            //设置定位参数
                            mLocationClient.setLocationOption(getOption());
                            // 启动定位
                            mLocationClient.startLocation();
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

    private void showFloatWindow() {
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
        FloatWindowUtil.getInstance().showFloatWindow(this);
    }




    @OnClick({R.id.indexLayout, R.id.recommendLayout, R.id.discoverLayout, R.id.personalCenterLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.indexLayout:
                showFragment(0);
                break;
            case R.id.recommendLayout:
                showFragment(1);
                break;
            case R.id.discoverLayout:
                showFragment(2);
                break;
            case R.id.personalCenterLayout:
                showFragment(3);
                break;
        }
    }


    private void settingBackgound(TextView table) {
        table.setSelected(true);
        if (table != index) {
            index.setSelected(false);
        }
        if (table != recommend) {
            recommend.setSelected(false);
        }
        if (table != discover) {
            discover.setSelected(false);
        }
        if (table != personalCenter) {
            personalCenter.setSelected(false);
        }

    }

    private void settingBackgound(FontIconView table) {
        table.setSelected(true);
        if (table != indexIcon) {
            indexIcon.setSelected(false);
        }
        if (table != recommendIcon) {
            recommendIcon.setSelected(false);
        }
        if (table != discoverIcon) {
            discoverIcon.setSelected(false);
        }
        if (table != personalCenterIcon) {
            personalCenterIcon.setSelected(false);
        }

    }


    private void hideFragments(FragmentTransaction transaction) {

        if (!index.isSelected()) {
            if (indexFragment != null) {
                transaction.hide(indexFragment);
            }
        }
        if (!recommend.isSelected()) {
            if (recommendFragment != null) {
                transaction.hide(recommendFragment);
            }

        }
        if (!discover.isSelected()) {
            if (discoverFragment != null) {
                transaction.hide(discoverFragment);
            }

        }
        if (!personalCenter.isSelected()) {
            if (personalCenterFragment != null) {
                transaction.hide(personalCenterFragment);
            }
        }

    }


    private void showFragment(int order) {
        if (order == 0) {
            settingBackgound(index);
            settingBackgound(indexIcon);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (indexFragment == null) {
                indexFragment = new IndexFragment();
                transaction.add(R.id.content, indexFragment);
            } else {
                transaction.show(indexFragment);
            }
            hideFragments(transaction);
            transaction.commit();
        } else if (order == 1) {
            settingBackgound(recommend);
            settingBackgound(recommendIcon);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (recommendFragment == null) {
                recommendFragment = new RecommendFragment();
                transaction.add(R.id.content, recommendFragment);
            } else {
                transaction.show(recommendFragment);
            }
            hideFragments(transaction);
            transaction.commit();
        } else if (order == 2) {
            settingBackgound(discover);
            settingBackgound(discoverIcon);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (discoverFragment == null) {
                discoverFragment = new DiscoverFragment();
                transaction.add(R.id.content, discoverFragment);
            } else {
                transaction.show(discoverFragment);
            }
            hideFragments(transaction);
            transaction.commit();
        } else if (order == 3) {
            settingBackgound(personalCenter);
            settingBackgound(personalCenterIcon);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (personalCenterFragment == null) {
                personalCenterFragment = new PersonalCenterFragment();
                transaction.add(R.id.content, personalCenterFragment);
            } else {
                transaction.show(personalCenterFragment);
            }
            hideFragments(transaction);
            transaction.commit();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (FloatWindowUtil.checkFloatWindowPermission(this)) {
                    FloatWindowUtil.getInstance().showFloatWindow(this);
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

            String cityCode = aMapLocation.getCityCode();

            for (int i = 0; i < sCityList.size(); i++) {
                if (cityCode.equals(sCityList.get(i).getCode())) {
                    if (sCurrentCity == null) {
                        sCurrentCity = sCityList.get(i);
                        //通知城市已改变
                        EventBus.getDefault().post(new CommonEvent("onLocationChanged"));

                    }

                    break;
                }
            }
            if (sCurrentCity == null) {
                sCurrentCity = new City(4301, "长沙市", "C");
                EventBus.getDefault().post(new CommonEvent("onLocationChanged"));
            }


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
            public void onResponse(Call call, Response response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_CITY_LIST;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, IOException exception) {


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
        }
        return false;
    }


}
