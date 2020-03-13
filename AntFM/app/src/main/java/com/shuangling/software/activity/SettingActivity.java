package com.shuangling.software.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.hjq.toast.ToastUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.callback.ConfigItems;
import com.mylhyl.circledialog.callback.ConfigTitle;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.ItemsParams;
import com.mylhyl.circledialog.params.TitleParams;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.dialog.UpdateDialog;
import com.shuangling.software.entity.UpdateInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ACache;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.DataCleanManager;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import okhttp3.Call;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

@EnableDragToClose()
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
    @BindView(R.id.accountAndSecurity)
    RelativeLayout accountAndSecurity;
    @BindView(R.id.checkUpdate)
    RelativeLayout checkUpdate;
    @BindView(R.id.update)
    TextView update;
    @BindView(R.id.fontSize)
    TextView fontSize;
    @BindView(R.id.fontSizeLayout)
    RelativeLayout fontSizeLayout;

    private Handler mHandler;
    private UpdateDialog mUpdateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        init();
    }


    private void init() {

        mHandler = new Handler(getMainLooper());
        version.setText("V" + getVersionName());
        if (MyApplication.getInstance().findNewVerison) {
            Drawable drawableRight = getResources().getDrawable(R.drawable.update_red_circle);
            update.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
        }

        int netLoad = SharedPreferencesUtils.getIntValue(NET_LOAD, 1);
        if (netLoad == 0) {
            netLoadDesc.setText("最佳效果(下载大图)");
        } else {
            netLoadDesc.setText("较省流量(智能下载)");
        }
        float appFontSize = SharedPreferencesUtils.getFloatValue(FontSizeSettingActivity.FONT_SIZE, 1.00f);
        if (appFontSize == 1.00f) {
            fontSize.setText("标准");
        }else if(appFontSize == 1.15f){
            fontSize.setText("大号");
        }else{
            fontSize.setText("特大");
        }
//        else {
//            netLoadDesc.setText("极省流量(不下载图)");
//        }

        int netPlay = SharedPreferencesUtils.getIntValue(NET_PLAY, 0);
        if (netPlay == 0) {
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


    @OnClick({R.id.clearCache, R.id.netLoad, R.id.netPlay, R.id.accountAndSecurity, R.id.checkUpdate,R.id.fontSizeLayout})
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
                final String[] items = {"最佳效果(下载大图)", "较省流量(智能下图)"};
                new CircleDialog.Builder()
                        .configDialog(new ConfigDialog() {
                            @Override
                            public void onConfig(DialogParams params) {
                                //params.backgroundColorPress = Color.CYAN;
                                //增加弹出动画
                                params.animStyle = R.style.dialogWindowAnim;
                            }
                        })
                        .setTitle("非WiFi网络流量")
                        .configTitle(new ConfigTitle() {
                            @Override
                            public void onConfig(TitleParams params) {
                                params.textColor = Color.GRAY;
                                params.textSize = (int) getResources().getDimension(R.dimen.font_size_30px);
                                params.height = 120;
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
                        params.textColor = CommonUtils.getThemeColor(SettingActivity.this);
                        params.textSize = (int) getResources().getDimension(R.dimen.font_size_26px);
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
                                //params.backgroundColorPress = Color.CYAN;
                                //增加弹出动画
                                params.animStyle = R.style.dialogWindowAnim;
                            }
                        })
                        .setTitle("非WiFi网络播放提醒")
                        .configTitle(new ConfigTitle() {
                            @Override
                            public void onConfig(TitleParams params) {
                                params.textColor = Color.GRAY;
                                params.textSize = (int) getResources().getDimension(R.dimen.font_size_30px);
                                params.height = 120;
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
                                params.textColor = CommonUtils.getThemeColor(SettingActivity.this);
                                params.textSize = (int) getResources().getDimension(R.dimen.font_size_26px);
                            }
                        })
                        .setNegative("取消", null)

                        .show(getSupportFragmentManager());
            }
            break;
            case R.id.accountAndSecurity:
                if (User.getInstance() != null) {
                    startActivity(new Intent(this, AccountAndSecurityActivity.class));
                } else {
                    Intent it = new Intent(this, NewLoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.checkUpdate:
                getUpdateInfo();
                break;

            case R.id.fontSizeLayout:
                    startActivity(new Intent(this, FontSizeSettingActivity.class));

                break;
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

    private void clearCache() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearMemoryCaches();
        imagePipeline.clearDiskCaches();
        ACache.get(this).clear();
        SharedPreferencesUtils.putPreferenceTypeValue("custom_column", SharedPreferencesUtils.PreferenceType.String, "");
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
                                cacheSize.setText("0KB");
                            } catch (Exception e) {
                                cacheSize.setText("0KB");
                            }
                        } else {
                            ToastUtils.show("未能获取相关权限，清理失败");
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


    public void getUpdateInfo() {
        final DialogFragment dialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());

        String url = ServerInfo.serviceIP + ServerInfo.updateInfoV2;
        Map<String, String> params = new HashMap<>();
        params.put("version", getVersionName());
        params.put("type", "android");
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, final String response) throws IOException {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialogFragment.dismiss();
                            JSONObject jsonObject = JSONObject.parseObject(response);
                            if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                                final UpdateInfo updateInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), UpdateInfo.class);
                                if (updateInfo.getNew_version() != null) {
                                    mUpdateDialog = UpdateDialog.getInstance(updateInfo.getNew_version().getVersion(), updateInfo.getNew_version().getContent());
                                    mUpdateDialog.setOnUpdateClickListener(new UpdateDialog.OnUpdateClickListener() {
                                        @Override
                                        public void download() {
                                            if (mUpdateDialog != null) {
                                                mUpdateDialog.dismiss();
                                            }
                                            if (!TextUtils.isEmpty(updateInfo.getNew_version().getUrl())) {
                                                downloadApk(updateInfo.getNew_version().getUrl());
                                            } else {
                                                ToastUtils.show("下载地址有误");
                                            }

                                        }
                                    });
                                    mUpdateDialog.showNoUpdate(true);
                                    mUpdateDialog.show(getSupportFragmentManager(), "UpdateDialog");
                                } else {
                                    ToastUtils.show("当前已是最新版本");
                                }

                            } else if (jsonObject != null && jsonObject.getIntValue("code") == -1) {
                                ToastUtils.show("当前已是最新版本");
                            } else if (jsonObject != null && !TextUtils.isEmpty(jsonObject.getString("msg"))) {
                                ToastUtils.show(jsonObject.getString("msg"));
                            }


                        } catch (Exception e) {

                        }
                    }
                });


            }

            @Override
            public void onFailure(Call call, Exception exception) {
                dialogFragment.dismiss();
                ToastUtils.show("请求失败，请稍后再试");

            }
        });
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
                                            Uri contentUri = FileProvider.getUriForFile(SettingActivity.this
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

                        } else {
                            ToastUtils.show("没有文件写权限，请开启该权限");
                        }

                    }
                });

    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
