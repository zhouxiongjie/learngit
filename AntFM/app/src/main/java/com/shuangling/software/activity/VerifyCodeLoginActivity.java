package com.shuangling.software.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;


public class VerifyCodeLoginActivity extends AppCompatActivity implements Handler.Callback {

    private static final int MSG_GET_VERIFY_CODE = 0X00;
    private static final int MSG_LOGIN_CALLBACK = 0X01;


    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.head)
    SimpleDraweeView head;
    @BindView(R.id.verifyCode)
    EditText verifyCode;
    @BindView(R.id.timer)
    TextView timer;
    @BindView(R.id.login)
    Button login;
    @BindView(R.id.verifyCodeLayout)
    LinearLayout verifyCodeLayout;

    private Handler mHandler;

    private CountDownTimer mCountDownTimer;
    private String mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(MyApplication.getInstance().getCurrentTheme());
        setContentView(R.layout.activity_verify_code_login);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        init();
    }

    private void init() {
        mPhoneNumber=getIntent().getStringExtra("PhoneNumber");
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
        activityTitle.setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        verifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    login.setEnabled(true);
                } else {
                    login.setEnabled(false);

                }
            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();

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
                        startActivity(new Intent(this,MainActivity.class));
                        finish();
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
        params.put("module", "reset_password");
        params.put("phone", mPhoneNumber);

        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Message msg = mHandler.obtainMessage(MSG_GET_VERIFY_CODE);
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, IOException exception) {

                ToastUtils.show("获取验证码请求异常");


            }
        });
    }



    private void verifyCodeLogin() {

        String url = ServerInfo.serviceIP + ServerInfo.login;
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "1");
        params.put("phone", mPhoneNumber);
        params.put("verification_code", verifyCode.getText().toString());

        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Message msg = mHandler.obtainMessage(MSG_LOGIN_CALLBACK);
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, IOException exception) {

                ToastUtils.show("登陆异常");


            }
        });
    }





    @Override
    public void onBackPressed() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        super.onBackPressed();

    }


    @OnClick({R.id.timer, R.id.login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.timer:
                getVerifyCode();
                break;
            case R.id.login:
                verifyCodeLogin();
                break;
        }
    }
}
