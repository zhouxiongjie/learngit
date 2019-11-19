package com.shuangling.software.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hjq.toast.ToastUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.mylhyl.circledialog.CircleDialog;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.activity.AccountAndSecurityActivity;
import com.shuangling.software.activity.AttentionActivity;
import com.shuangling.software.activity.CluesActivity;
import com.shuangling.software.activity.CollectActivity;
import com.shuangling.software.activity.FeedbackActivity;
import com.shuangling.software.activity.HistoryActivity;
import com.shuangling.software.activity.LoginActivity;
import com.shuangling.software.activity.MainActivity;
import com.shuangling.software.activity.MessageListActivity;
import com.shuangling.software.activity.ModifyUserInfoActivity;
import com.shuangling.software.activity.SettingActivity;
import com.shuangling.software.activity.SubscribeActivity;
import com.shuangling.software.dialog.UpdateDialog;
import com.shuangling.software.entity.UpdateInfo;
import com.shuangling.software.entity.User;
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


public class PersonalCenterFragment extends Fragment {


    public static final int MSG_UPDATE_STATUS = 0x01;
    public static final int MSG_GET_UPDATE_INFO = 0x2;

    @BindView(R.id.top)
    RelativeLayout top;
    @BindView(R.id.history)
    RelativeLayout history;
    @BindView(R.id.collect)
    RelativeLayout collect;
    @BindView(R.id.subscribe)
    RelativeLayout subscribe;
    @BindView(R.id.accountAndSecurity)
    RelativeLayout accountAndSecurity;
    //    @BindView(R.id.content)
//    LinearLayout content;
    @BindView(R.id.head)
    SimpleDraweeView head;
    @BindView(R.id.headBg)
    SimpleDraweeView headBg;
    @BindView(R.id.loginLayout)
    LinearLayout loginLayout;
    @BindView(R.id.login)
    TextView login;
    Unbinder unbinder;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.attentionNumber)
    TextView attentionNumber;
    @BindView(R.id.userLayout)
    RelativeLayout userLayout;
    @BindView(R.id.feedback)
    RelativeLayout feedback;
    @BindView(R.id.brokeNews)
    RelativeLayout brokeNews;
    @BindView(R.id.messageNumber)
    TextView messageNumber;
    @BindView(R.id.messageLayout)
    FrameLayout messageLayout;
    @BindView(R.id.setting)
    RelativeLayout setting;
    @BindView(R.id.update)
    TextView update;
    @BindView(R.id.checkUpdate)
    RelativeLayout checkUpdate;

    private Handler mHandler;
    private DialogFragment mDialogFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mHandler = new Handler(Looper.getMainLooper());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personalcenter, container, false);

        unbinder = ButterKnife.bind(this, view);
        if(MyApplication.getInstance().findNewVerison){
            update.setText("发现新版本");
            Drawable drawableRight = getResources().getDrawable(R.drawable.update_red_circle);
            update.setCompoundDrawablesWithIntrinsicBounds(null,null, drawableRight, null);
        }
        return view;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.history, R.id.collect, R.id.subscribe, R.id.accountAndSecurity, R.id.headBg, R.id.login, R.id.loginLayout, R.id.feedback, R.id.brokeNews, R.id.messageLayout, R.id.setting, R.id.attentionNumber,R.id.checkUpdate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.history:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), HistoryActivity.class));
                } else {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.collect:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), CollectActivity.class));
                } else {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.subscribe:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), SubscribeActivity.class));
                } else {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.accountAndSecurity:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), AccountAndSecurityActivity.class));
                } else {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.headBg:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), ModifyUserInfoActivity.class));
                } else {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.loginLayout:
                break;
            case R.id.login:
                if (User.getInstance() == null) {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }

                break;
            case R.id.feedback:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), FeedbackActivity.class));
                } else {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.brokeNews:
                if (User.getInstance() != null) {
                    Intent it = new Intent(getContext(), CluesActivity.class);
                    it.putExtra("url", ServerInfo.scs + "/broke-create");
                    startActivity(it);
                } else {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.messageLayout:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), MessageListActivity.class));
                } else {
                    Intent it = new Intent(getContext(), LoginActivity.class);
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
        }
    }


    public void getUpdateInfo() {
        mDialogFragment = CommonUtils.showLoadingDialog(getFragmentManager());

        String url = ServerInfo.serviceIP + ServerInfo.updateInfo;
        Map<String, String> params = new HashMap<>();
        params.put("version", "v" + getVersionName());
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
                               if (updateInfo.getNew_version()!=null) {
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
                                   dialog.show(getFragmentManager(), "UpdateDialog");
                               }else{
                                   ToastUtils.show("当前已是最新版本");
                               }

                           }


                       } catch (Exception e) {

                       }
                   }
               });


            }

            @Override
            public void onFailure(Call call, Exception exception) {
                mDialogFragment.dismiss();
                ToastUtils.show("请求失败，请稍后再试");

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

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_LOGIN) {
//            if (User.getInstance() != null) {
//                loginLayout.setVisibility(View.VISIBLE);
//                login.setVisibility(View.GONE);
//                if (!TextUtils.isEmpty(User.getInstance().getAvatar())) {
//                    Uri uri = Uri.parse(User.getInstance().getAvatar());
//                    int width = CommonUtils.dip2px(50);
//                    int height = width;
//                    ImageLoader.showThumb(uri, head, width, height);
//                }
//                userName.setText(User.getInstance().getUsername());
//                attentionNumber.setText("" + User.getInstance().getStatus());
//            } else {
//                loginLayout.setVisibility(View.GONE);
//                login.setVisibility(View.VISIBLE);
//                ImageLoader.showThumb(head, R.drawable.ic_user3);
//            }
//        }
//    }


    @Override
    public void onResume() {
        super.onResume();
        if (User.getInstance() != null) {
            loginLayout.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(User.getInstance().getAvatar())) {
                Uri uri = Uri.parse(User.getInstance().getAvatar());
                int width = CommonUtils.dip2px(50);
                int height = width;
                ImageLoader.showThumb(uri, head, width, height);
            }
            userName.setText(User.getInstance().getNickname());
            //attentionNumber.setText("关注:" + User.getInstance().getStatus());
            updateStatistics();
        } else {
            loginLayout.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
            ImageLoader.showThumb(head, R.drawable.ic_user4);
            messageNumber.setVisibility(View.GONE);
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
                            attentionNumber.setText("关注:" + jsonObject.getJSONObject("data").getInteger("follows_count"));
                            messageNumber.setText("" + jsonObject.getJSONObject("data").getInteger("not_read_num"));
                            if (jsonObject.getJSONObject("data").getInteger("not_read_num") > 0) {
                                messageNumber.setVisibility(View.VISIBLE);
                            } else {
                                messageNumber.setVisibility(View.GONE);
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
//                                    .configDialog(params -> params.backgroundColor = Color.CYAN)
                                    .setTitle("下载")
                                    .setProgressText("已经下载")
                                    //.setProgressText("已经下载%s了")
                                    .setProgressDrawable(R.drawable.bg_progress_h)
                                    //.setNegative("取消", v -> timer.cancel())
                                    .show(getFragmentManager());
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


}
