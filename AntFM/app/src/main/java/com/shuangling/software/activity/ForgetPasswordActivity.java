package com.shuangling.software.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
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

//@EnableDragToClose()
public class ForgetPasswordActivity extends QMUIActivity/*AppCompatActivity*/ implements Handler.Callback {
    private static final int MSG_GET_VERIFY_CODE = 0X00;
    private static final int MSG_RESET_PASSWORD = 0X01;
    @BindView(R.id.countryCode)
    TextView countryCode;
    @BindView(R.id.phoneNum)
    EditText phoneNum;
    @BindView(R.id.next)
    Button next;
    @BindView(R.id.activity_title)
    /*TopTitleBar*/ QMUITopBarLayout activityTitle;
    @BindView(R.id.verifyCodeLayout)
    LinearLayout verifyCodeLayout;
    @BindView(R.id.tip)
    TextView tip;
    @BindView(R.id.verifyCode)
    EditText verifyCode;
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
    private Handler mHandler;
    private CountDownTimer mCountDownTimer;
    private DialogFragment mDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
//        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this); //
        activityTitle.addLeftImageButton(R.drawable.ic_left, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(view -> { //
            doOnBackPressed();
        });
        activityTitle.setTitle("忘记密码");
        mHandler = new Handler(this);
        init();
    }

    private void init() {
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
        verifyCode.addTextChangedListener(new TextWatcher() {
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
                    if (TextUtils.isEmpty(newPassword.getText().toString())) {
                        resetPassword.setEnabled(false);
                    } else {
                        resetPassword.setEnabled(true);
                    }
                }
            }
        });
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
                    if (TextUtils.isEmpty(verifyCode.getText().toString())) {
                        resetPassword.setEnabled(false);
                    } else {
                        resetPassword.setEnabled(true);
                    }
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
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        modifyPasswordLayout.setVisibility(View.VISIBLE);
                        verifyCodeLayout.setVisibility(View.GONE);
                        //1.倒计时
                        //2.设置提醒文本
                        tip.setText(CommonUtils.tagKeyword("已向手机号" + phoneNum.getText().toString() + "发送短信验证码", phoneNum.getText().toString()));
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
            case MSG_RESET_PASSWORD: {
                try {
                    mDialogFragment.dismiss();
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
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
        }
        return false;
    }

    @OnClick({R.id.next, R.id.resetPassword, R.id.timer})
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
        }
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
                        try {
                            mDialogFragment.dismiss();
                            ToastUtils.show("获取验证码请求异常");
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
    }

    private void resetPassword() {
        mDialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());
        String url = ServerInfo.serviceIP + ServerInfo.resetPassword;
        Map<String, String> params = new HashMap<String, String>();
        params.put("password", newPassword.getText().toString());
        params.put("verification_code", verifyCode.getText().toString());
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
    protected void doOnBackPressed() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        if (modifyPasswordLayout.getVisibility() == View.GONE) {
            super.doOnBackPressed();
        } else {
            modifyPasswordLayout.setVisibility(View.GONE);
            verifyCodeLayout.setVisibility(View.VISIBLE);
        }
    }
}
