package com.shuangling.software.activity;

import android.app.Activity;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.AppManager;
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
public class NewAccountPasswordLoginActivity extends AppCompatActivity implements Handler.Callback {

    private static final int MSG_LOGIN_CALLBACK = 0X01;
    private static final int MSG_VERIFY_PHONE = 0X02;
    @BindView(R.id.activtyTitle)
    TopTitleBar activtyTitle;
    @BindView(R.id.phoneNum)
    EditText phoneNum;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.eye)
    FontIconView eye;
    @BindView(R.id.passwordLayout)
    RelativeLayout passwordLayout;
    @BindView(R.id.forgetPassword)
    TextView forgetPassword;
    @BindView(R.id.verifyCodeLogin)
    TextView verifyCodeLogin;
    @BindView(R.id.login)
    Button login;


    private Handler mHandler;
    private boolean mPasswordVisible=false;

    private DialogFragment mDialogFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account_password_login);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        init();
    }

    private void init() {

        AppManager.addActivity(this);
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
                    if (!TextUtils.isEmpty(password.getText().toString())) {
                        login.setEnabled(true);
                    } else {
                        login.setEnabled(false);
                    }
                }else{
                    login.setEnabled(false);
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
                String phone = phoneNum.getText().toString();
                if (CommonUtils.isMobileNO(phone)) {
                    if (!TextUtils.isEmpty(s.toString())) {
                        login.setEnabled(true);
                    } else {
                        login.setEnabled(false);
                    }
                }else{
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

            case MSG_LOGIN_CALLBACK: {
                try {
                    mDialogFragment.dismiss();
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        User user = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), User.class);
                        user.setLogin_type(0);
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
                        setResult(Activity.RESULT_OK);
                        EventBus.getDefault().post(new CommonEvent("OnLoginSuccess"));
                        //finish();
                        AppManager.finishAllActivity();
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








    private void accountLogin(String phone, String pwd) {

        mDialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());
        String url = ServerInfo.serviceIP + ServerInfo.login;
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "0");
        params.put("phone", "" + phone);
        params.put("password", pwd);
        params.put("from_url", SharedPreferencesUtils.getStringValue("from_url",null));
        params.put("from_user_id", SharedPreferencesUtils.getStringValue("from_user_id",null));

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
                        try{
                            mDialogFragment.dismiss();
                            ToastUtils.show("登录异常");
                        }catch (Exception e){

                        }

                    }
                });



            }
        });
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }


    @OnClick({ R.id.login,R.id.eye,R.id.verifyCodeLogin,R.id.forgetPassword})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.login:
                CommonUtils.hideInput(this);
                accountLogin(phoneNum.getText().toString(), password.getText().toString());

                break;
            case R.id.eye:
                mPasswordVisible=!mPasswordVisible;
                if(mPasswordVisible){
                    eye.setText(R.string.password_visible);
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    if(!TextUtils.isEmpty(password.getText().toString())){
                        password.setSelection(password.getText().toString().length());//将光标移至文字末尾
                    }

                }else {
                    eye.setText(R.string.password_invisible);
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    if(!TextUtils.isEmpty(password.getText().toString())){
                        password.setSelection(password.getText().toString().length());//将光标移至文字末尾
                    }
                }
                break;
            case R.id.verifyCodeLogin:
                onBackPressed();
                break;
            case R.id.forgetPassword:
                startActivity(new Intent(this, NewForgetPasswordActivity.class));

                break;
        }
    }


    @Override
    protected void onDestroy() {
        AppManager.removeActivity(this);
        super.onDestroy();
    }
}
