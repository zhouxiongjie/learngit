package com.shuangling.software.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.OssInfo;
import com.shuangling.software.entity.User;
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

import java.io.File;
import java.io.IOException;
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
public class BindPhoneActivity extends AppCompatActivity implements Handler.Callback{

    public static final String TAG = BindPhoneActivity.class.getName();

    private static final int MSG_GET_VERIFY_CODE = 1;
    private static final int MSG_WEIXIN_LOGIN_CALLBACK = 2;

    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.phoneNum)
    EditText phoneNum;
    @BindView(R.id.sendCode)
    Button sendCode;



    private Handler mHandler;
    private String mPhoneNumber;
    private DialogFragment mDialogFragment;

    private String weixinUnionid;
    private String weixinOpenid;
    private String weixinNickname;
    private String weixinHeadimgurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        AppManager.addActivity(this);
        init();

    }


    private void init() {
        weixinUnionid=getIntent().getStringExtra("unionid");
        weixinOpenid=getIntent().getStringExtra("openid");
        weixinNickname=getIntent().getStringExtra("nickname");
        weixinHeadimgurl=getIntent().getStringExtra("headimgurl");

        activityTitle.setMoreAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weixinLogin(weixinNickname,weixinHeadimgurl,weixinOpenid,weixinUnionid);
            }
        });

        phoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPhoneNumber = s.toString();
                if (CommonUtils.isMobileNO(mPhoneNumber)) {
                    sendCode.setEnabled(true);
                }else {
                    sendCode.setEnabled(false);
                }
            }
        });

    }





    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_VERIFY_CODE: {
                try {
                    mDialogFragment.dismiss();
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
//                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
//                        Intent it = new Intent(this, VerifyCodeLoginActivity.class);
//                        it.putExtra("PhoneNumber", mPhoneNumber);
//                        startActivityForResult(it, LOGIN_VERIFY_REQUEST);
//                    } else if (jsonObject != null) {
//                        ToastUtils.show(jsonObject.getString("msg"));
//                    }


                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        Intent it=new Intent(this,NewVerifyCodeBindPhoneActivity.class);
                        it.putExtra("PhoneNumber",mPhoneNumber);
                        it.putExtra("nickname",weixinNickname);
                        it.putExtra("headimgurl",weixinHeadimgurl);
                        it.putExtra("openid",weixinOpenid);
                        it.putExtra("unionid",weixinUnionid);
                        startActivity(it);

                    } else if (jsonObject != null) {
                        ToastUtils.show(jsonObject.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
            break;
            case MSG_WEIXIN_LOGIN_CALLBACK: {
                try {
                    mDialogFragment.dismiss();
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        User user = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), User.class);
                        User.setInstance(user);
                        SharedPreferencesUtils.saveUser(user);
                        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
                        pushService.bindAccount(user.getUsername(), new CommonCallback() {
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



                        ToastUtils.show("登录成功");
                        setResult(RESULT_OK);
                        EventBus.getDefault().post(new CommonEvent("OnLoginSuccess"));
                        AppManager.finishAllActivity();
                        //finish();




                    } else {
                        ToastUtils.show(jsonObject.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            default:
                break;

        }
        return false;
    }


    @OnClick({R.id.sendCode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sendCode:
                getVerifyCode(mPhoneNumber);
            break;

        }
    }
    private void weixinLogin(String nickname, String headimgurl,String openid, String unionid) {
        mDialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());
        String url = ServerInfo.serviceIP + ServerInfo.wechatLogin;
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "2");
        params.put("nickname", nickname);
        params.put("headimgurl", headimgurl);
        params.put("openid", openid);
        params.put("unionid", unionid);
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = mHandler.obtainMessage(MSG_WEIXIN_LOGIN_CALLBACK);
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDialogFragment.dismiss();
                        ToastUtils.show("登陆异常");
                    }
                });



            }
        });
    }

    private void getVerifyCode(String phone) {
        mDialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());

        String url = ServerInfo.serviceIP + ServerInfo.getVerifyCode;
        Map<String, String> params = new HashMap<String, String>();
        params.put("module", "bind_phone");
        params.put("phone", phone);

        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = mHandler.obtainMessage(MSG_GET_VERIFY_CODE);
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDialogFragment.dismiss();
                    }
                });

                ToastUtils.show("获取验证码请求异常");


            }
        });
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
