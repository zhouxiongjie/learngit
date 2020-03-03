package com.shuangling.software.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.customview.VerifyCodeView;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

@EnableDragToClose()
public class NewForgetPasswordActivity extends AppCompatActivity implements Handler.Callback {

    private static final int MSG_GET_VERIFY_CODE = 0X00;
    private static final int MSG_RESET_PASSWORD = 0X01;

    private static final int MSG_VERIFY_VERIFY_CODE = 0X02;
    @BindView(R.id.countryCode)
    TextView countryCode;
    @BindView(R.id.phoneNum)
    EditText phoneNum;
    @BindView(R.id.next)
    Button next;
    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.verifyCodeLayout)
    LinearLayout verifyCodeLayout;
//    @BindView(R.id.tip)
//    TextView tip;
//    @BindView(R.id.verifyCode)
//    EditText verifyCode;
    @BindView(R.id.timer)
    TextView timer;
    @BindView(R.id.newPassword)
    EditText newPassword;
    @BindView(R.id.passwordTip)
    TextView passwordTip;
    @BindView(R.id.resetPassword)
    Button resetPassword;
    @BindView(R.id.modifyPasswordLayout)
    LinearLayout modifyPasswordLayout;
    @BindView(R.id.phoneNum01)
    TextView phoneNum01;
    @BindView(R.id.verifyCodeView)
    VerifyCodeView verifyCodeView;
    @BindView(R.id.inputVerifyCodeLayout)
    LinearLayout inputVerifyCodeLayout;
    @BindView(R.id.eye)
    FontIconView eye;
    private Handler mHandler;

    private CountDownTimer mCountDownTimer;
    private DialogFragment mDialogFragment;

    private boolean mPasswordVisible=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_forget_password);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        init();
    }

    private void init() {
        activityTitle.setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                String phone = s.toString();
                if (CommonUtils.isMobileNO(phone)) {
                    next.setEnabled(true);
                } else {
                    next.setEnabled(false);

                }
            }
        });


//        verifyCode.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (TextUtils.isEmpty(s.toString())) {
//                    resetPassword.setEnabled(false);
//                } else {
//                    if (TextUtils.isEmpty(newPassword.getText().toString())) {
//                        resetPassword.setEnabled(false);
//                    } else {
//                        resetPassword.setEnabled(true);
//                    }
//
//                }
//            }
//        });

        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    resetPassword.setEnabled(false);
                } else {
//                    if (TextUtils.isEmpty(verifyCode.getText().toString())) {
//                        resetPassword.setEnabled(false);
//                    } else {
                    resetPassword.setEnabled(true);
//                    }

                }
            }
        });

        verifyCodeView.setInputCompleteListener(new VerifyCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                //ToastUtils.show("inputComplete: " + verifyCodeView.getEditContent());
//                if(mUserExist){
//                    ToastUtils.show("登录成功");
//
//                }
                //验证手机验证码
                verifyVerifyCode(phoneNum.getText().toString(),verifyCodeView.getEditContent());

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
                    mDialogFragment.dismiss();
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        inputVerifyCodeLayout.setVisibility(View.VISIBLE);
                        //modifyPasswordLayout.setVisibility(View.VISIBLE);
                        verifyCodeLayout.setVisibility(View.GONE);
                        phoneNum01.setText(phoneNum.getText().toString());

                        //1.倒计时
                        //2.设置提醒文本
                        //tip.setText(CommonUtils.tagKeyword("已向手机号" + phoneNum.getText().toString() + "发送短信验证码", phoneNum.getText().toString()));
                        mCountDownTimer = new CountDownTimer(60 * 1000, 500) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                timer.setText("重新发送(" + millisUntilFinished / 1000 + ")");
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
            case MSG_RESET_PASSWORD: {
                try {
                    mDialogFragment.dismiss();
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show("新密码已设置，请重新登录");
                        startActivity(new Intent(this, MainActivity.class));
                        finish();

                    } else if (jsonObject != null) {
                        ToastUtils.show(jsonObject.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;

            case MSG_VERIFY_VERIFY_CODE: {
                try {
                    mDialogFragment.dismiss();
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        inputVerifyCodeLayout.setVisibility(View.GONE);
                        modifyPasswordLayout.setVisibility(View.VISIBLE);
                        //verifyCodeLayout.setVisibility(View.GONE);

                    } else if (jsonObject != null) {
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


    @OnClick({R.id.next, R.id.resetPassword, R.id.timer,R.id.eye})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next:
                getVerifyCode(phoneNum.getText().toString());
                break;
            case R.id.resetPassword:
                resetPassword();
                break;
            case R.id.timer:
                getVerifyCode(phoneNum.getText().toString());
                break;
            case R.id.eye:
                mPasswordVisible=!mPasswordVisible;
                if(mPasswordVisible){
                    eye.setText(R.string.password_visible);
                    newPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                }else {
                    eye.setText(R.string.password_invisible);
                    newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                break;
        }
    }

    private void verifyVerifyCode(String phone,String verifyCode) {

        mDialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());
        String url = ServerInfo.serviceIP + ServerInfo.getVerifyCode;
        Map<String, String> params = new HashMap<String, String>();
        params.put("module", "reset_password");
        params.put("phone", phone);
        params.put("verification_code",verifyCode);

        OkHttpUtils.put(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = mHandler.obtainMessage(MSG_VERIFY_VERIFY_CODE);
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
                ToastUtils.show("服务请求异常");


            }
        });
    }


    private void getVerifyCode(String phone) {

        mDialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());
        String url = ServerInfo.serviceIP + ServerInfo.getVerifyCode;
        Map<String, String> params = new HashMap<String, String>();
        params.put("module", "reset_password");
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


    private void resetPassword() {
        mDialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());
        String url = ServerInfo.serviceIP + ServerInfo.resetPassword;
        Map<String, String> params = new HashMap<String, String>();
        params.put("password", newPassword.getText().toString());
        params.put("verification_code", verifyCodeView.getEditContent());
        params.put("phone", phoneNum.getText().toString());

        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = mHandler.obtainMessage(MSG_RESET_PASSWORD);
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
                ToastUtils.show("重置密码请求异常");


            }
        });
    }


    @Override
    public void onBackPressed() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        if (modifyPasswordLayout.getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            modifyPasswordLayout.setVisibility(View.GONE);
            verifyCodeLayout.setVisibility(View.VISIBLE);
        }
    }


}
