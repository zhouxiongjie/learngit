package com.shuangling.software.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.User;
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
import okhttp3.Response;

@EnableDragToClose()
public class NewPhoneBindActivity extends AppCompatActivity implements Handler.Callback {

    public static final String TAG = NewPhoneBindActivity.class.getName();

    private static final int MSG_GET_VERIFY_CODE = 0x00;
    private static final int MSG_GET_BIND_PHONE = 0x01;
    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.tip)
    TextView tip;
    @BindView(R.id.phoneNum)
    EditText phoneNum;
    @BindView(R.id.verifyCode)
    EditText verifyCode;
    @BindView(R.id.timer)
    TextView timer;
    @BindView(R.id.phoneBind)
    Button phoneBind;


    private DialogFragment mDialogFragment;

    private Handler mHandler;
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_phone_bind);
        ButterKnife.bind(this);

        init();
    }


    private void init() {
        mHandler = new Handler(this);

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
                    timer.setEnabled(true);
                } else {
                    timer.setEnabled(false);

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
                String verifyCode = s.toString();
                if (!TextUtils.isEmpty(verifyCode)) {
                    phoneBind.setEnabled(true);
                }else{
                    phoneBind.setEnabled(false);
                }

            }
        });
    }


    private void getVerifyCode() {
        mDialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());

        String url = ServerInfo.serviceIP + ServerInfo.getVerifyCode;
        Map<String, String> params = new HashMap<String, String>();
        params.put("module", "update_phone");
        params.put("phone", phoneNum.getText().toString());

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
                        ToastUtils.show("IO异常");
                    }
                });


            }
        });
    }


    private void bindPhone() {
        mDialogFragment=CommonUtils.showLoadingDialog(getSupportFragmentManager());
        String url = ServerInfo.serviceIP + ServerInfo.modifyPhone;
        Map<String, String> params = new HashMap<String, String>();
        params.put("verification_code", verifyCode.getText().toString());
        params.put("phone", phoneNum.getText().toString());

        OkHttpUtils.put(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = mHandler.obtainMessage(MSG_GET_BIND_PHONE);
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDialogFragment.dismiss();
                        ToastUtils.show("IO异常");
                    }
                });



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
            case MSG_GET_BIND_PHONE:
                try {
                    mDialogFragment.dismiss();
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        User.getInstance().setPhone(phoneNum.getText().toString());
                        finish();
                    } else if (jsonObject != null) {
                        ToastUtils.show(jsonObject.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;

        }
        return false;
    }


    @OnClick({R.id.timer, R.id.phoneBind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.timer:
                getVerifyCode();
                break;
            case R.id.phoneBind:
                bindPhone();
                break;
        }
    }
}
