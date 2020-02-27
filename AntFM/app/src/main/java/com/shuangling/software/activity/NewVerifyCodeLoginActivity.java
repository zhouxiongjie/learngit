package com.shuangling.software.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.customview.VerifyCodeView;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;
import com.youngfeng.snake.annotations.EnableDragToClose;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

@EnableDragToClose()
public class NewVerifyCodeLoginActivity extends AppCompatActivity implements Handler.Callback {

    private static final int MSG_GET_VERIFY_CODE = 0X00;
    private static final int MSG_LOGIN_CALLBACK = 0X01;
    private static final int MSG_VERIFY_PHONE = 0X02;
    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.phoneNum)
    TextView phoneNum;
    @BindView(R.id.timer)
    TextView timer;
    @BindView(R.id.verifyCodeView)
    VerifyCodeView verifyCodeView;


    private Handler mHandler;

    private CountDownTimer mCountDownTimer;
    private String mPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_verify_code_login);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        init();
    }

    private void init() {

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
        activityTitle.setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        verifyCodeView.setInputCompleteListener(new VerifyCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                ToastUtils.show("inputComplete: " + verifyCodeView.getEditContent());


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
                        finish();
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


    private void verifyCodeLogin(String verifyCode) {

        String url = ServerInfo.serviceIP + ServerInfo.login;
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "1");
        params.put("phone", mPhoneNumber);
        params.put("verification_code", verifyCode);

        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = mHandler.obtainMessage(MSG_LOGIN_CALLBACK);
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {

                ToastUtils.show("登陆异常");


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
    public void onBackPressed() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        super.onBackPressed();

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
}
