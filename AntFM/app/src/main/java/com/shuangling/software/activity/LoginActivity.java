package com.shuangling.software.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.event.PlayerEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;
import com.youngfeng.snake.annotations.EnableDragToClose;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;

@EnableDragToClose()
public class LoginActivity extends AppCompatActivity implements Handler.Callback, PlatformActionListener {


    private static final int MSG_AUTH_CANCEL = 0;
    private static final int MSG_AUTH_ERROR = 1;
    private static final int MSG_AUTH_COMPLETE = 2;

    private static final int MSG_LOGIN_CALLBACK = 3;
    private static final int MSG_GET_VERIFY_CODE = 4;
    private static final int LOGIN_VERIFY_REQUEST = 5;


    @BindView(R.id.verifyCodeLogin)
    Button verifyCodeLogin;
    @BindView(R.id.accountLogin)
    Button accountLogin;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.weiXin)
    ImageView weiXin;
    @BindView(R.id.qq)
    ImageView qq;
    @BindView(R.id.weiBo)
    ImageView weiBo;
    @BindView(R.id.activtyTitle)
    TopTitleBar activtyTitle;
    @BindView(R.id.head)
    SimpleDraweeView head;

    private List<View> mLoginViews = new ArrayList<View>();
    private Handler mHandler;

    private String mPhoneNumber;
    private DialogFragment mDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mHandler = new Handler(this);

        init();


    }

    private void init() {

        if(MyApplication.getInstance().getStation()!=null&&!TextUtils.isEmpty(MyApplication.getInstance().getStation().getH5_logo())){
            Uri uri = Uri.parse(MyApplication.getInstance().getStation().getH5_logo());
            ImageLoader.showThumb(uri, head, CommonUtils.dip2px(70), CommonUtils.dip2px(70));
        }

        activtyTitle.setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        View verifyCodeLoginView = LayoutInflater.from(this).inflate(R.layout.veritfy_code_login, null);
        final VerifyCodeViewHolder verifyCodeViewHolder = new VerifyCodeViewHolder(verifyCodeLoginView);
        verifyCodeViewHolder.phoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (CommonUtils.isMobileNO(s.toString())) {
                    verifyCodeViewHolder.login.setEnabled(true);
                } else {
                    verifyCodeViewHolder.login.setEnabled(false);
                }

            }
        });

        verifyCodeViewHolder.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneNumber = verifyCodeViewHolder.phoneNum.getText().toString();
                getVerifyCode(mPhoneNumber);
            }
        });


        View accountPwdLoginView = LayoutInflater.from(this).inflate(R.layout.account_password_login, null);
        final AccountViewHolder accountViewHolder = new AccountViewHolder(accountPwdLoginView);
        accountViewHolder.phoneNum.addTextChangedListener(new TextWatcher() {
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
                    if (!TextUtils.isEmpty(accountViewHolder.password.getText().toString())) {
                        accountViewHolder.login.setEnabled(true);
                    } else {
                        accountViewHolder.login.setEnabled(false);
                    }
                }

            }
        });
        accountViewHolder.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = accountViewHolder.phoneNum.getText().toString();
                if (CommonUtils.isMobileNO(phone)) {
                    if (!TextUtils.isEmpty(s.toString())) {
                        accountViewHolder.login.setEnabled(true);
                    } else {
                        accountViewHolder.login.setEnabled(false);
                    }
                }
            }
        });
        accountViewHolder.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountLogin(accountViewHolder.phoneNum.getText().toString(), accountViewHolder.password.getText().toString());

            }
        });
        accountViewHolder.forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });
        mLoginViews.add(verifyCodeLoginView);
        mLoginViews.add(accountPwdLoginView);

        viewPager.setAdapter(new PagerAdapter() {


            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mLoginViews.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = mLoginViews.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return mLoginViews.size();
            }


        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    verifyCodeLogin.setSelected(true);
                    accountLogin.setSelected(false);
                } else {
                    verifyCodeLogin.setSelected(false);
                    accountLogin.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        verifyCodeLogin.setSelected(true);
    }

    @OnClick({R.id.verifyCodeLogin, R.id.accountLogin, R.id.weiXin, R.id.qq, R.id.weiBo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.verifyCodeLogin:
                verifyCodeLogin.setSelected(true);
                accountLogin.setSelected(false);
                viewPager.setCurrentItem(0);
                break;
            case R.id.accountLogin:
                accountLogin.setSelected(true);
                verifyCodeLogin.setSelected(false);
                viewPager.setCurrentItem(1);
                break;
            case R.id.weiXin:
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                authorize(wechat);
                long id = Thread.currentThread().getId();
                Log.i("test", "test");
                break;
            case R.id.qq:
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                authorize(qq);
                break;
            case R.id.weiBo:
                Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
                authorize(sina);
                break;
        }
    }


    //    授权登录
    private void authorize(Platform plat) {
        if (plat == null) {
            return;
        }
        plat.setPlatformActionListener(this);
        // true不使用SSO授权，false使用SSO授权
        plat.SSOSetting(false);
        //获取用户资料
        plat.showUser(null);

    }


    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {

        if (action == Platform.ACTION_USER_INFOR) {
            Message msg = mHandler.obtainMessage(MSG_AUTH_COMPLETE);
            msg.obj = platform;
            mHandler.sendMessage(msg);
        }


    }

    @Override
    public void onError(Platform platform, int action, Throwable throwable) {
        if (action == Platform.ACTION_USER_INFOR) {
            mHandler.sendEmptyMessage(MSG_AUTH_ERROR);
            if (platform != null) {
                if (platform.isAuthValid()) {
                    platform.removeAccount(true);
                }
            }
        }
        throwable.printStackTrace();
    }

    @Override
    public void onCancel(Platform platform, int action) {
        if (action == Platform.ACTION_USER_INFOR) {
            mHandler.sendEmptyMessage(MSG_AUTH_CANCEL);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_AUTH_CANCEL: {
                //取消授权
                ToastUtils.show(R.string.auth_cancel);
            }
            break;
            case MSG_AUTH_ERROR: {
                //授权失败
                ToastUtils.show(R.string.auth_error);
            }
            break;
            case MSG_AUTH_COMPLETE: {
                //授权成功
                ToastUtils.show(R.string.auth_complete);
                Platform platform = (Platform) msg.obj;
                PlatformDb platDB = platform.getDb();//获取数平台数据DB
                //通过DB获取各种数据
                String token = platDB.getToken();
                String gender = platDB.getUserGender();
                String userIcon = platDB.getUserIcon();
                String userId = platDB.getUserId();
                String userName = platDB.getUserName();
                String type;
                if (platform.getName().equals(QQ.NAME)) {
                    type = "QQ";
                } else if (platform.getName().equals(SinaWeibo.NAME)) {
                    type = "WB";
                } else {
                    type = "WX";
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
                        ToastUtils.show("登录成功");
                        setResult(Activity.RESULT_OK);
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
            case MSG_GET_VERIFY_CODE: {
                try {
                    mDialogFragment.dismiss();
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        Intent it = new Intent(this, VerifyCodeLoginActivity.class);
                        it.putExtra("PhoneNumber", mPhoneNumber);
                        startActivityForResult(it, LOGIN_VERIFY_REQUEST);
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

    static class AccountViewHolder {
        @BindView(R.id.countryCode)
        TextView countryCode;
        @BindView(R.id.phoneNum)
        EditText phoneNum;
        @BindView(R.id.password)
        EditText password;
        @BindView(R.id.login)
        Button login;
        @BindView(R.id.forgetPassword)
        TextView forgetPassword;

        AccountViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    static class VerifyCodeViewHolder {
        @BindView(R.id.countryCode)
        TextView countryCode;
        @BindView(R.id.phoneNum)
        EditText phoneNum;
        @BindView(R.id.login)
        Button login;

        VerifyCodeViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    private void accountLogin(String phone, String pwd) {

        mDialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());
        String url = ServerInfo.serviceIP + ServerInfo.login;
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "0");
        params.put("phone", "" + phone);
        params.put("password", pwd);

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
                        mDialogFragment.dismiss();
                    }
                });
                ToastUtils.show("登陆异常");


            }
        });
    }


    private void getVerifyCode(String phone) {
        mDialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());

        String url = ServerInfo.serviceIP + ServerInfo.getVerifyCode;
        Map<String, String> params = new HashMap<String, String>();
        params.put("module", "login");
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
        User.setInstance(null);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == LOGIN_VERIFY_REQUEST && resultCode == RESULT_OK) {
            setResult(Activity.RESULT_OK);
            finish();
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }
}
