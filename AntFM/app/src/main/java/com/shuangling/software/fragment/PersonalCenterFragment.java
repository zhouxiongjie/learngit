package com.shuangling.software.fragment;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.components.SimpleImmersionFragment;
import com.hjq.toast.ToastUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.mylhyl.circledialog.CircleDialog;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.shuangling.software.BuildConfig;
import com.shuangling.software.R;
import com.shuangling.software.activity.AccountAndSecurityActivity;
import com.shuangling.software.activity.AttentionActivity;
import com.shuangling.software.activity.BindPhoneActivity;
import com.shuangling.software.activity.CluesActivity;
import com.shuangling.software.activity.CollectActivity;
import com.shuangling.software.activity.FeedbackActivity;
import com.shuangling.software.activity.HistoryActivity;
import com.shuangling.software.activity.MessageListActivity;
import com.shuangling.software.activity.ModifyUserInfoActivity;
import com.shuangling.software.activity.MyWalletsActivity;
import com.shuangling.software.activity.NewLoginActivity;
import com.shuangling.software.activity.SettingActivity;
import com.shuangling.software.activity.SubscribeActivity;
import com.shuangling.software.activity.WebViewActivity;
import com.shuangling.software.activity.WebViewBackActivity;
import com.shuangling.software.dialog.UpdateDialog;
import com.shuangling.software.entity.UpdateInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.manager.SkinManager;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import okhttp3.Call;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class PersonalCenterFragment extends QMUIFragment/*SimpleImmersionFragment*/ {
    @BindView(R.id.headBg)
    SimpleDraweeView headBg;
    @BindView(R.id.head)
    SimpleDraweeView head;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.attentionNumber)
    TextView attentionNumber;
    @BindView(R.id.subscribeNumber)
    TextView subscribeNumber;
    @BindView(R.id.loginLayout)
    LinearLayout loginLayout;
    //    @BindView(R.id.btn_login)
//     TextView btn_login;
//    @BindView(R.id.userLayout)
//     RelativeLayout userLayout;
    @BindView(R.id.message)
    LinearLayout message;
    @BindView(R.id.history)
    LinearLayout history;
    @BindView(R.id.collect)
    LinearLayout collect;
    @BindView(R.id.brokeNews)
    LinearLayout brokeNews;
    @BindView(R.id.myPublish)
    LinearLayout myPublish;
    @BindView(R.id.award)
    LinearLayout award;
    @BindView(R.id.wallet)
    LinearLayout wallet;
    @BindView(R.id.reservation)
    LinearLayout reservation;//预约
    @BindView(R.id.tv_release)
    TextView tv_release;//发布
    @BindView(R.id.feedback)
    LinearLayout feedback;
    @BindView(R.id.setting)
    LinearLayout setting;
    //Unbinder unbinder;
    @BindView(R.id.statusBar)
    View statusBar;
    @BindView(R.id.aboutUs)
    LinearLayout aboutUs;
    @BindView(R.id.view_my_head_not_login)
    TextView view_my_head_not_login;
    @BindView(R.id.view_my_head_logined)
    RelativeLayout view_my_head_logined;
    private Handler mHandler;
    private DialogFragment mDialogFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(Looper.getMainLooper());
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_personalcenter, container, false);
//        unbinder = ButterKnife.bind(this, view);
////        if (MyApplication.aboutUsInfo != null && MyApplication.aboutUsInfo.getAbout_us_status() == 1) {//todo 4.0设计稿不显示
////            aboutUs.setVisibility(View.VISIBLE);
////        }
//        return view;
//    }

    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_personalcenter, null);
        ButterKnife.bind(this, rootView);
        CommonUtils.setStatusHeight(getContext(), statusBar);
        QMUIStatusBarHelper.setStatusBarLightMode(getActivity());
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switch (BuildConfig.FLAVOR) {
            case "ltsj":
                wallet.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //unbinder.unbind();
    }

    @OnClick({R.id.history, R.id.collect, R.id.subscribeNumber, R.id.view_my_head_logined, R.id.view_my_head_not_login, R.id.loginLayout, R.id.feedback, R.id.brokeNews, R.id.message, R.id.setting, R.id.attentionNumber, R.id.myPublish, R.id.wallet, R.id.reservation, R.id.tv_release, R.id.award, R.id.aboutUs})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.history:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), HistoryActivity.class));
                } else {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.collect:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), CollectActivity.class));
                } else {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.subscribeNumber:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), SubscribeActivity.class));
                } else {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.accountAndSecurity:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), AccountAndSecurityActivity.class));
                } else {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.view_my_head_logined:
//                if (User.getInstance() != null) {
                startActivity(new Intent(getContext(), ModifyUserInfoActivity.class));
//                } else {
//                    Intent it = new Intent(getContext(), NewLoginActivity.class);
//                    startActivity(it);
//                }
                break;
            case R.id.loginLayout:
                break;
            case R.id.view_my_head_not_login:
                if (User.getInstance() == null) {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.feedback:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), FeedbackActivity.class));
                } else {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.brokeNews:
                if (User.getInstance() == null) {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    startActivity(it);
                } else if (User.getInstance() != null && TextUtils.isEmpty(User.getInstance().getPhone())) {
                    Intent it = new Intent(getContext(), BindPhoneActivity.class);
                    startActivity(it);
                } else {
                    Intent it = new Intent(getContext(), CluesActivity.class);
                    it.putExtra("url", ServerInfo.scs + "/broke-create");
                    startActivity(it);
                }
                break;
            case R.id.myPublish:
                if (User.getInstance() != null) {
                    Intent it = new Intent(getContext(), WebViewActivity.class);
                    it.putExtra("url", ServerInfo.h5HttpsIP + "/publish");
                    it.putExtra("title", "我的发布");
                    startActivity(it);
                } else {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.message:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), MessageListActivity.class));
                } else {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.setting:
                startActivity(new Intent(getContext(), SettingActivity.class));
                break;
            case R.id.attentionNumber:
                startActivity(new Intent(getContext(), AttentionActivity.class));
                break;
            case R.id.checkUpdate:
                getUpdateInfo();
                break;
            case R.id.wallet:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), MyWalletsActivity.class));
                } else {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.reservation://预约 TODO
            case R.id.tv_release:// 发布




                break;
            case R.id.award:
                if (User.getInstance() != null) {
                    Intent it = new Intent(getContext(), WebViewBackActivity.class);
                    it.putExtra("url", ServerInfo.activity + "my-prize");
                    it.putExtra("title", "奖品");
                    startActivity(it);
                } else {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.aboutUs:
                Intent it = new Intent(getContext(), WebViewBackActivity.class);
                it.putExtra("url", ServerInfo.h5IP + "/about");
                it.putExtra("title", "关于我们");
                startActivity(it);
                break;
        }
    }

    public void getUpdateInfo() {
        mDialogFragment = CommonUtils.showLoadingDialog(getFragmentManager());
        String url = ServerInfo.serviceIP + ServerInfo.updateInfoV2;
        Map<String, String> params = new HashMap<>();
        params.put("version", getVersionName());
        params.put("type", "android");
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, final String response) throws IOException {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mDialogFragment.dismiss();
                            JSONObject jsonObject = JSONObject.parseObject(response);
                            if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                                final UpdateInfo updateInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), UpdateInfo.class);
                                if (updateInfo.getNew_version() != null) {
                                    UpdateDialog dialog = UpdateDialog.getInstance(updateInfo.getNew_version().getVersion(), updateInfo.getNew_version().getContent());
                                    dialog.setOnUpdateClickListener(new UpdateDialog.OnUpdateClickListener() {
                                        @Override
                                        public void download() {
                                            if (!TextUtils.isEmpty(updateInfo.getNew_version().getUrl())) {
                                                downloadApk(updateInfo.getNew_version().getUrl());
                                            } else {
                                                ToastUtils.show("下载地址有误");
                                            }
                                        }
                                    });
                                    dialog.showNoUpdate(true);
                                    dialog.show(getFragmentManager(), "UpdateDialog");
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
                try {
                    mDialogFragment.dismiss();
                    ToastUtils.show("请求失败，请稍后再试");
                } catch (Exception e) {
                }
            }
        });
    }

    public String getVersionName() {
        try {
            return getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (User.getInstance() != null) {//已登录
            view_my_head_not_login.setVisibility(View.GONE);
            view_my_head_logined.setVisibility(View.VISIBLE);
//            loginLayout.setVisibility(View.VISIBLE);
//            login.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(User.getInstance().getAvatar())) {
                Uri uri = Uri.parse(User.getInstance().getAvatar());
                int width = CommonUtils.dip2px(50);
                int height = width;
                ImageLoader.showThumb(uri, head, width, height);
            }
            userName.setText(User.getInstance().getNickname());
            updateStatistics();
        } else {//未登录
//            loginLayout.setVisibility(View.GONE);
//            login.setVisibility(View.VISIBLE);
//            ImageLoader.showThumb(head, R.drawable.ic_user4);
            view_my_head_not_login.setVisibility(View.VISIBLE);
            view_my_head_logined.setVisibility(View.GONE);
        }
    }

    public void updateStatistics() {
        String url = ServerInfo.serviceIP + ServerInfo.statistics;
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, null, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                String result = response;
                final JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                attentionNumber.setText("关注 " + jsonObject.getJSONObject("data").getInteger("follows_count"));
                                subscribeNumber.setText("订阅 " + jsonObject.getJSONObject("data").getInteger("subscribe_count"));
                            } catch (Exception e) {
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    public void downloadApk(final String downloadUrl) {
        RxPermissions rxPermissions = new RxPermissions(getActivity());
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
                                    .setTitle("下载")
                                    .setProgressText("已经下载")
                                    .setProgressDrawable(R.drawable.bg_progress_h)
                                    .show(getFragmentManager());
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
                                            String packageName = getContext().getPackageName();
                                            Uri contentUri = FileProvider.getUriForFile(getContext()
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

//    @Override
//    public void initImmersionBar() {
//        ImmersionBar.with(this).statusBarView(statusBar).statusBarDarkFont(true).init();
//    }
}