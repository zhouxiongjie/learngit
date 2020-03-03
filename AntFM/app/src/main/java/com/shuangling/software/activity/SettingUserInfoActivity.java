package com.shuangling.software.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.params.DialogParams;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.entity.OssInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.entity.UserDetail;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.oss.OSSAKSKCredentialProvider;
import com.shuangling.software.oss.OssService;
import com.shuangling.software.utils.AppManager;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.MyGlideEngine;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.youngfeng.snake.annotations.EnableDragToClose;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import okhttp3.Call;

@EnableDragToClose()
public class SettingUserInfoActivity extends AppCompatActivity implements Handler.Callback, OSSCompletedCallback<PutObjectRequest, PutObjectResult> {

    public static final String TAG = SettingUserInfoActivity.class.getName();

    private static final int CHOOSE_PHOTO = 0x0;
    private static final int TACK_PHOTO = 0x1;
    private static final int CUT_OK = 0x02;
    private static final int MSG_UPLOAD_HEAD = 0x03;

    @BindView(R.id.head)
    SimpleDraweeView head;
    @BindView(R.id.nickName)
    EditText nickName;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.eye)
    FontIconView eye;
    @BindView(R.id.confirm)
    Button confirm;


    private File tempFile;
    private Handler mHandler;

    private OssInfo mOssInfo;
    //OSS的上传下载
    private OssService mOssService;
    private Map<String, String> mUserInfo = new HashMap<>();

    private boolean mPasswordVisible=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_userinfo);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        mHandler=new Handler(this);
        AppManager.addActivity(this);
        init();
        getOSSinfo();
    }


    private void init() {
        nickName.setText("");
        if (!TextUtils.isEmpty(User.getInstance().getAvatar())) {
            Uri uri = Uri.parse(User.getInstance().getAvatar());
            ImageLoader.showThumb(uri, head, CommonUtils.dip2px(120), CommonUtils.dip2px(120));
        }


        nickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String nickname = s.toString();
                mUserInfo.put("nickname",nickname);
                String pwd=password.getText().toString();
                if (!TextUtils.isEmpty(nickname)) {
                    if (pwd.length() < 6 || pwd.length() > 20) {
                        confirm.setEnabled(false);
                    } else {
                        confirm.setEnabled(true);
                    }
                }else{
                    confirm.setEnabled(false);
                }


            }
        });


        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String pwd = s.toString();
                String nickname=nickName.getText().toString();
                if (!TextUtils.isEmpty(nickname)) {
                    if (pwd.length() < 6 || pwd.length() > 20) {
                        confirm.setEnabled(false);
                    } else {
                        confirm.setEnabled(true);
                    }
                }else{
                    confirm.setEnabled(false);
                }


            }
        });

    }






    public void modifyUerInfo() {

        String url = ServerInfo.serviceIP + ServerInfo.modifyUserInfo;
        OkHttpUtils.put(url, mUserInfo, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                String result = response;
                final JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    User.getInstance().setAvatar(mUserInfo.get("avatar"));
                    User.getInstance().setNickname(mUserInfo.get("nickname"));
                    SharedPreferencesUtils.saveUser(User.getInstance());
                    setPassword();

                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        ToastUtils.show("修改用户信息失败，请稍后重试");

                    }
                });
            }
        });

    }




    public void setPassword() {

        String url = ServerInfo.serviceIP + ServerInfo.settingPwd;

        Map<String, String> params = new HashMap<>();
        params.put("password",password.getText().toString());
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                String result = response;
                final JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new CommonEvent("OnLoginSuccess"));
                            AppManager.finishAllActivity();
                        }
                    });


                }else{
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.show("设置密码失败，请稍后重试");
                        }
                    });
                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TACK_PHOTO) {
            if (resultCode == RESULT_OK && data != null) {
                //保存图片信息
                String path = data.getStringExtra("path");
                File file = new File(path);
                //clipImage(Uri.fromFile(file));

            } else {
                Toast.makeText(this, "用户取消拍照", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CHOOSE_PHOTO) {

            if (resultCode == RESULT_OK && data != null) {

                List<String> paths = Matisse.obtainPathResult(data);
                //List<Uri> selects = Matisse.obtainResult(data);
                //File file = new File(CommonUtils.getRealFilePath(this, selects.get(0)));
                File file = new File(paths.get(0));
                clipImage(Uri.fromFile(file));

            } else {
                ToastUtils.show("用户取消拍照");
            }

        } else if (requestCode == CUT_OK) {
            if (resultCode == RESULT_OK && data != null) {
                // 获取裁剪的图片数据
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bitmap = extras.getParcelable("data");
                    Random rand = new Random();
                    int randNum = rand.nextInt(1000);
                    tempFile = new File(CommonUtils.getStoragePrivateDirectory(Environment.DIRECTORY_PICTURES), CommonUtils.getCurrentTimeString() + randNum + ".jpg");
                    CommonUtils.saveBitmap(tempFile.getAbsolutePath(), bitmap);
                    //saveBitmapToFile(bitmap);
//
                    // 2.把图片文件file上传到服务器
                    if (mOssInfo != null&&mOssService!=null) {
                        mOssService.asyncUploadFile(mOssInfo.getDir() + tempFile.getName(), tempFile.getAbsolutePath(), null, this);
                    } else {
                        ToastUtils.show("OSS初始化失败,请稍后再试");
                    }

                }
            }

        }
    }


    /**
     * 调用系统的裁剪方法
     */
    private void clipImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        // 数据 uri 代表裁剪哪一张
        intent.setDataAndType(uri, "image/*");
        // 传递数据
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例，这里设置的是正方形（长宽比为1:1）
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        // 你待会裁剪完之后需要获取数据   startActivityForResult
        startActivityForResult(intent, CUT_OK);
    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_UPLOAD_HEAD: {

            }
            break;
            default:
                break;

        }
        return false;
    }




    @OnClick({R.id.head,  R.id.confirm,R.id.eye})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.head: {

//                if (mOssService == null) {
//                    ToastUtils.show("OSS上传服务初始化失败，请稍后再试");
//                    return;
//                }

                RxPermissions rxPermissions = new RxPermissions(SettingUserInfoActivity.this);
                rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean granted) throws Exception {
                                if (granted) {
                                    String packageName = getPackageName();
                                    Matisse.from(SettingUserInfoActivity.this)
                                            .choose(MimeType.of(MimeType.JPEG, MimeType.PNG)) // 选择 mime 的类型
                                            .countable(false)
                                            .maxSelectable(1) // 图片选择的最多数量
                                            .spanCount(4)
                                            .capture(true)
                                            .captureStrategy(new CaptureStrategy(true, packageName + ".fileprovider"))
                                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                            .thumbnailScale(1.0f) // 缩略图的比例
                                            .theme(R.style.Matisse_Zhihu)
                                            .imageEngine(new MyGlideEngine()) // 使用的图片加载引擎
                                            .forResult(CHOOSE_PHOTO); // 设置作为标记的请求码
                                } else {
                                    ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                                }
                            }
                        });

            }
            break;
            case R.id.confirm: {
                modifyUerInfo();

            }
            break;
            case R.id.eye:
                mPasswordVisible=!mPasswordVisible;
                if(mPasswordVisible){
                    eye.setText(R.string.password_visible);
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                }else {
                    eye.setText(R.string.password_invisible);
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                break;

        }
    }


    @Override
    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
        Log.d("PutObject", "UploadSuccess");

        Log.d("ETag", result.getETag());
        Log.d("RequestId", result.getRequestId());

        long upload_end = System.currentTimeMillis();
        mUserInfo.put("avatar",mOssInfo.getHost() + "/" + mOssInfo.getDir() + tempFile.getName());
        //modifyUerInfo("avatar", mOssInfo.getHost() + "/" + mOssInfo.getDir() + tempFile.getName());
        if (!TextUtils.isEmpty(mUserInfo.get("avatar"))) {
            Uri uri = Uri.parse(mUserInfo.get("avatar"));
            ImageLoader.showThumb(uri, head, CommonUtils.dip2px(120), CommonUtils.dip2px(120));

        }

    }

    @Override
    public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
        String info = "";

        if (serviceException != null) {
            // 服务异常
            Log.e("ErrorCode", serviceException.getErrorCode());
            Log.e("RequestId", serviceException.getRequestId());
            Log.e("HostId", serviceException.getHostId());
            Log.e("RawMessage", serviceException.getRawMessage());
            info = serviceException.toString();
        }

    }







    public void getOSSinfo() {

        String url = ServerInfo.serviceIP + ServerInfo.appOss;

        Map<String, String> params = new HashMap<>();


        OkHttpUtils.get(url, null, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                String result = response;
                final JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    mOssInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), OssInfo.class);
                    mOssService = initOSS(mOssInfo.getHost(), mOssInfo.getBucket(), mOssInfo.getAccess_key_id(), mOssInfo.getAccess_key_secret(), mOssInfo.getExpiration(), mOssInfo.getSecurity_token());
                    //mOssService.setCallbackAddress(Config.callbackAddress);
                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });

    }


    public OssService initOSS(String endpoint, String bucket, String accessKey, String accessKeySecret, String expiration,
                              String securityToken) {

//        移动端是不安全环境，不建议直接使用阿里云主账号ak，sk的方式。建议使用STS方式。具体参
//        https://help.aliyun.com/document_detail/31920.html
//        注意：SDK 提供的 PlainTextAKSKCredentialProvider 只建议在测试环境或者用户可以保证阿里云主账号AK，SK安全的前提下使用。具体使用如下
//        主账户使用方式
//        String AK = "******";
//        String SK = "******";
//        credentialProvider = new PlainTextAKSKCredentialProvider(AK,SK)
//        以下是使用STS Sever方式。
//        如果用STS鉴权模式，推荐使用OSSAuthCredentialProvider方式直接访问鉴权应用服务器，token过期后可以自动更新。
//        详见：https://help.aliyun.com/document_detail/31920.html
//        OSSClient的生命周期和应用程序的生命周期保持一致即可。在应用程序启动时创建一个ossClient，在应用程序结束时销毁即可。

        OSSAKSKCredentialProvider oSSAKSKCredentialProvider;
        //使用自己的获取STSToken的类

        oSSAKSKCredentialProvider = new OSSAKSKCredentialProvider(accessKey, accessKeySecret, securityToken, expiration);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSS oss = new OSSClient(getApplicationContext(), endpoint, new OSSStsTokenCredentialProvider(accessKey, accessKeySecret, securityToken), conf);
        OSSLog.enableLog();
        return new OssService(oss, bucket);

    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onDestroy() {
        AppManager.removeActivity(this);
        super.onDestroy();
    }
}
