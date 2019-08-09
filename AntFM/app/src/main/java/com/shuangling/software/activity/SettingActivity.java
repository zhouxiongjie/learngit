package com.shuangling.software.activity;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.callback.ConfigItems;
import com.mylhyl.circledialog.callback.ConfigSubTitle;
import com.mylhyl.circledialog.callback.ConfigTitle;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.ItemsParams;
import com.mylhyl.circledialog.params.SubTitleParams;
import com.mylhyl.circledialog.params.TitleParams;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.DataCleanManager;
import com.shuangling.software.utils.SharedPreferencesUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import java.io.File;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;


public class SettingActivity extends AppCompatActivity {


    public static final String NET_LOAD = "net_load";
    public static final String NET_PLAY = "net_play";
    public static final String NEED_TIP_PLAY = "need_tip_play";

    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.cacheSize)
    TextView cacheSize;
    @BindView(R.id.clearCache)
    RelativeLayout clearCache;
    @BindView(R.id.netLoadDesc)
    TextView netLoadDesc;
    @BindView(R.id.netLoad)
    RelativeLayout netLoad;
    @BindView(R.id.netPlayDesc)
    TextView netPlayDesc;
    @BindView(R.id.netPlay)
    RelativeLayout netPlay;
    @BindView(R.id.version)
    TextView version;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(MyApplication.getInstance().getCurrentTheme());
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        init();
    }


    private void init() {

        version.setText("V"+getVersionName());

        int netLoad=SharedPreferencesUtils.getIntValue(NET_LOAD,0);
        if (netLoad ==0) {
            netLoadDesc.setText("最佳效果(下载大图)");
        } else if (netLoad ==1) {
            netLoadDesc.setText("较省流量(智能下载)");
        }else {
            netLoadDesc.setText("极省流量(不下载图)");
        }

        int netPlay=SharedPreferencesUtils.getIntValue(NET_PLAY,0);
        if (netPlay ==0) {
            netPlayDesc.setText("每次提醒");
        } else {
            netPlayDesc.setText("提醒一次");
        }



        try {
            cacheSize.setText(DataCleanManager.getTotalCacheSize(this));
        } catch (Exception e) {
            cacheSize.setText("0KB");
        }


    }










    @OnClick({R.id.clearCache, R.id.netLoad, R.id.netPlay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.clearCache:
                new CircleDialog.Builder()
                        .setCanceledOnTouchOutside(false)
                        .setCancelable(false)

                        .setText("确定删除所有缓存？")
                        .setNegative("取消", null)
                        .setPositive("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clearCache();

                            }
                        })
                        .show(getSupportFragmentManager());
                break;
            case R.id.netLoad: {
                final String[] items = {"最佳效果(下载大图)", "较省流量(智能下图)", "极省流量(不下载图)"};
                new CircleDialog.Builder()
                        .configDialog(new ConfigDialog() {
                            @Override
                            public void onConfig(DialogParams params) {
                                params.backgroundColorPress = Color.CYAN;
                                //增加弹出动画
                                params.animStyle = R.style.dialogWindowAnim;
                            }
                        })
                        .setTitle("非WiFi网络流量")
                        .configTitle(new ConfigTitle() {
                            @Override
                            public void onConfig(TitleParams params) {
                                params.textColor = Color.GRAY;
                                params.textSize=(int)getResources().getDimension(R.dimen.font_size_30px);
                                params.height=120;
                            }
                        })

                        .setItems(items, new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                SharedPreferencesUtils.putIntValue(NET_LOAD, position);
                                netLoadDesc.setText(items[position]);

                            }
                        }).configItems(new ConfigItems() {
                            @Override
                            public void onConfig(ItemsParams params) {
                                params.textColor=CommonUtils.getThemeColor(SettingActivity.this);
                                params.textSize=(int)getResources().getDimension(R.dimen.font_size_26px);
                            }
                        })
                        .setNegative("取消", null)

                        .show(getSupportFragmentManager());
                }
                break;
            case R.id.netPlay: {
                final String[] items = {"每次提醒", "提醒一次"};
                new CircleDialog.Builder()
                        .configDialog(new ConfigDialog() {
                            @Override
                            public void onConfig(DialogParams params) {
                                params.backgroundColorPress = Color.CYAN;
                                //增加弹出动画
                                params.animStyle = R.style.dialogWindowAnim;
                            }
                        })
                        .setTitle("非WiFi网络播放提醒")
                        .configTitle(new ConfigTitle() {
                            @Override
                            public void onConfig(TitleParams params) {
                                params.textColor = Color.GRAY;
                                params.textSize=(int)getResources().getDimension(R.dimen.font_size_30px);
                                params.height=120;
                            }
                        })

                        .setItems(items, new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                SharedPreferencesUtils.putIntValue(NET_PLAY, position);
                                SharedPreferencesUtils.putIntValue(NEED_TIP_PLAY, 1);
                                netPlayDesc.setText(items[position]);


                            }
                        })
                        .configItems(new ConfigItems() {
                            @Override
                            public void onConfig(ItemsParams params) {
                                params.textColor=CommonUtils.getThemeColor(SettingActivity.this);
                                params.textSize=(int)getResources().getDimension(R.dimen.font_size_26px);
                            }
                        })
                        .setNegative("取消", null)

                        .show(getSupportFragmentManager());
                }
                break;
        }
    }


    public String getVersionName(){
        try{
            return  getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private void clearCache() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearMemoryCaches();
        imagePipeline.clearDiskCaches();

        RxPermissions rxPermissions = new RxPermissions(this);

        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            DataCleanManager.clearAllCache(SettingActivity.this);
                            //递归删除Docment中无用的文件
                            deleteDocmentDir(CommonUtils.getStoragePrivateDirectory(Environment.DIRECTORY_PICTURES));
                            deleteDocmentDir(CommonUtils.getStoragePrivateDirectory(Environment.DIRECTORY_MOVIES));
                            ToastUtils.show("清理完成");

                            try {
                                cacheSize.setText(DataCleanManager.getTotalCacheSize(SettingActivity.this));
                            } catch (Exception e) {
                                cacheSize.setText("0KB");
                            }
                        } else {
                            ToastUtils.show( "未能获取相关权限，清理失败");
                        }
                    }
                });

    }


    public void deleteDocmentDir(String dir) {
        File docmentDir = new File(dir);
        if (!docmentDir.exists()) {
            return;
        }

        File[] files = docmentDir.listFiles();
        for (int i = 0; files != null && i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                CommonUtils.deleteDir(file.getAbsolutePath());
            } else {
                file.delete();
            }
        }
    }
}
