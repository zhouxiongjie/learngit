package com.shuangling.software.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.hjq.toast.ToastUtils;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.VerifyCodeView;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.AppManager;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

//@EnableDragToClose()
public class NewVerifyCodeLoginActivity extends QMUIActivity/*AppCompatActivity*/ implements Handler.Callback {
    private static final int MSG_GET_VERIFY_CODE = 0X00;
    private static final int MSG_LOGIN_CALLBACK = 0X01;
    private static final int MSG_VERIFY_PHONE = 0X02;
    private static final int MSG_IS_USER_EXIST = 0X03;
    @BindView(R.id.activity_title)
    /*TopTitleBar*/ QMUITopBarLayout activityTitle;
    @BindView(R.id.phoneNum)
    TextView phoneNum;
    @BindView(R.id.timer)
    TextView timer;
    @BindView(R.id.verifyCodeView)
    VerifyCodeView verifyCodeView;
    private Handler mHandler;
    private CountDownTimer mCountDownTimer;
    private String mPhoneNumber;
    private boolean mUserExist = false;
    private DialogFragment mDialogFragment;
    private String mJumpUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_verify_code_login);
//        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this); //
        activityTitle.addLeftImageButton(R.drawable.ic_left, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(view -> { //
            doOnBackPressed();
        });
        mHandler = new Handler(this);
        AppManager.addActivity(this);
        init();
    }

    private void init() {
        mJumpUrl = getIntent().getStringExtra("jump_url");
        mPhoneNumber = getIntent().getStringExtra("PhoneNumber");
        phoneNum.setText(mPhoneNumber);
        mCountDownTimer = new CountDownTimer(60 * 1000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText("重新发送" + "(" + millisUntilFinished / 1000 + ")");
                timer.setEnabled(false);
            }

            @Override
            public void onFinish() {
                timer.setText("重新发送");
                timer.setEnabled(true);
            }
        };
        mCountDownTimer.start();
        verifyCodeView.setInputCompleteListener(new VerifyCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                //ToastUtils.show("inputComplete: " + verifyCodeView.getEditContent());
//                if(mUserExist){
//                    ToastUtils.show("登录成功");
//
//                }
                isUserExist();
            }

            @Override
            public void invalidContent() {
            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_VERIFY_CODE: {
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mCountDownTimer = new CountDownTimer(60 * 1000, 500) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                timer.setText("(" + millisUntilFinished / 1000 + ")重新发送");
                                timer.setEnabled(false);
                            }

                            @Override
                            public void onFinish() {
                                timer.setText("重新发送");
                                timer.setEnabled(true);
                            }
                        };
                        mCountDownTimer.start();
                    } else if (jsonObject != null) {
                        ToastUtils.show(jsonObject.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case MSG_LOGIN_CALLBACK: {
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
                        if (mUserExist) {
                            ToastUtils.show("登录成功");
                            setResult(RESULT_OK);
                            EventBus.getDefault().post(new CommonEvent("OnLoginSuccess"));
                            AppManager.finishAllActivity();
                            //finish();
                        } else {
                            Intent it = new Intent(this, SettingUserInfoActivity.class);
                            startActivity(it);
                        }
                    } else {
                        ToastUtils.show(jsonObject.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case MSG_VERIFY_PHONE: {
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        startActivity(new Intent(this, NewPhoneBindActivity.class));
                        finish();
                    } else {
                        ToastUtils.show(jsonObject.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case MSG_IS_USER_EXIST:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mUserExist = true;
                        verifyCodeLogin(verifyCodeView.getEditContent());
                    } else if (jsonObject != null && jsonObject.getIntValue("code") == 202004) {
                        mUserExist = false;
                        verifyCodeLogin(verifyCodeView.getEditContent());
                    } else {
                        mDialogFragment.dismiss();
                        ToastUtils.show("登录失败，请稍后再试");
                    }
                } catch (Exception e) {
                    try {
                        mDialogFragment.dismiss();
                        ToastUtils.show("登录失败，请稍后再试");
                    } catch (Exception ex) {
                    }
                }
                break;
        }
        return false;
    }

    private void getVerifyCode() {
        String url = ServerInfo.serviceIP + ServerInfo.getVerifyCode;
        Map<String, String> params = new HashMap<String, String>();
        params.put("module", "login");
        params.put("phone", mPhoneNumber);
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = mHandler.obtainMessage(MSG_GET_VERIFY_CODE);
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                ToastUtils.show("获取验证码请求异常");
            }
        });
    }

    private void isUserExist() {
        mDialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());
        String url = ServerInfo.serviceIP + ServerInfo.isUserExist;
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "9");
        params.put("unionid", mPhoneNumber);
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = mHandler.obtainMessage(MSG_IS_USER_EXIST);
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mDialogFragment.dismiss();
                            ToastUtils.show("登录失败，请稍后再试");
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
    }

    private void verifyCodeLogin(String verifyCode) {
        String url = ServerInfo.serviceIP + ServerInfo.login;
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "1");
        params.put("phone", mPhoneNumber);
        params.put("verification_code", verifyCode);
        params.put("from_url", SharedPreferencesUtils.getStringValue("from_url", null));
        params.put("from_user_id", SharedPreferencesUtils.getStringValue("from_user_id", null));
        params.put("jump_url", mJumpUrl);
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = mHandler.obtainMessage(MSG_LOGIN_CALLBACK);
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mDialogFragment.dismiss();
                            ToastUtils.show("登录失败，请稍后再试");
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
    }

    //    private void verifyPhone() {
//
//        String url = ServerInfo.serviceIP + ServerInfo.getVerifyCode;
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("module", "update_phone");
//        params.put("phone", mPhoneNumber);
//        params.put("verification_code", verifyCode.getText().toString());
//
//        OkHttpUtils.put(url, params, new OkHttpCallback(this) {
//
//            @Override
//            public void onResponse(Call call, String response) throws IOException {
//
//                Message msg = mHandler.obtainMessage(MSG_VERIFY_PHONE);
//                msg.obj = response;
//                mHandler.sendMessage(msg);
//
//            }
//
//            @Override
//            public void onFailure(Call call, Exception exception) {
//
//                ToastUtils.show("IO异常");
//
//            }
//        });
//    }

    @Override
    protected void doOnBackPressed() {
        super.doOnBackPressed();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @OnClick({R.id.timer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.timer:
                getVerifyCode();
                break;
//            case R.id.login:
//                if (mPageCategory == PageCategory.VerifyBindPhone) {
//                    verifyPhone();
//                } else {
//                    verifyCodeLogin();
//                }
//
//                break;
        }
    }

    @Override
    protected void onDestroy() {
        AppManager.removeActivity(this);
        super.onDestroy();
    }
}
