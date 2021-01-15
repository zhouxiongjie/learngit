package com.shuangling.software.activity;

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
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
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
public class NewVerifyCodeBindPhoneActivity extends QMUIActivity/*AppCompatActivity*/ implements Handler.Callback {
    private static final int MSG_GET_VERIFY_CODE = 0X00;
    private static final int MSG_WEIXIN_LOGIN_CALLBACK = 0X01;
    private static final int MSG_BIND_PHONE = 0X02;
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
    private DialogFragment mDialogFragment;
    private String weixinUnionid;
    private String weixinOpenid;
    private String weixinNickname;
    private String weixinHeadimgurl;
    private String mJumpUrl;
    private boolean hasLogined;

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
        hasLogined = getIntent().getBooleanExtra("hasLogined", false);
        weixinUnionid = getIntent().getStringExtra("unionid");
        weixinOpenid = getIntent().getStringExtra("openid");
        weixinNickname = getIntent().getStringExtra("nickname");
        weixinHeadimgurl = getIntent().getStringExtra("headimgurl");
        mPhoneNumber = getIntent().getStringExtra("PhoneNumber");
        mJumpUrl = getIntent().getStringExtra("jump_url");
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
                bindPhone(mPhoneNumber, verifyCodeView.getEditContent(), 0);
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
            case MSG_BIND_PHONE:
                try {
                    String result = (String) msg.obj;
                    int support = msg.arg1;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
//EventBus.getDefault().post(new CommonEvent("OnLoginSuccess"));
                        ToastUtils.show("绑定成功");
                        if (User.getInstance() == null) {
                            weixinLogin(weixinNickname, weixinHeadimgurl, weixinOpenid, weixinUnionid);
                        } else {
                            User.getInstance().setPhone(mPhoneNumber);
                            SharedPreferencesUtils.saveUser(User.getInstance());
                            AppManager.finishAllActivity();
                        }
//AppManager.finishAllActivity();
                    } else if (jsonObject != null && jsonObject.getIntValue("code") == 202036) {
                        mDialogFragment.dismiss();
//                        new CircleDialog.Builder()
//                                .setTitle("提示")
//                                .setText("该手机号已绑定其他账号，是否重新绑定此账号？")
//                                .setPositive("确定", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        bindPhone(mPhoneNumber, verifyCodeView.getEditContent(), 1);
//                                    }
//                                })
//                                .setNegative("取消", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        ToastUtils.show("取消绑定");
//                                        finish();
//                                    }
//                                })
//                                .setCanceledOnTouchOutside(false)
//                                .setCancelable(false)
//                                .show(getSupportFragmentManager());

                        new QMUIDialog.MessageDialogBuilder(NewVerifyCodeBindPhoneActivity.this)
                                .setTitle("提示")
                                .setMessage("该手机号已绑定其他账号，是否重新绑定此账号？")
                                .setCanceledOnTouchOutside(false)
                                .setCancelable(false)
                                .setSkinManager(QMUISkinManager.defaultInstance(NewVerifyCodeBindPhoneActivity.this))
                                .addAction("取消", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        ToastUtils.show("取消绑定");
                                        dialog.dismiss();
                                    }
                                })
                                .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        bindPhone(mPhoneNumber, verifyCodeView.getEditContent(), 1);
                                        dialog.dismiss();
                                    }
                                })
                                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
                    } else if (jsonObject != null) {
                        mDialogFragment.dismiss();
                        ToastUtils.show(jsonObject.getString("msg"));
                    }
                } catch (Exception e) {
                    mDialogFragment.dismiss();
                    ToastUtils.show("登录失败，请稍后再试");
                }
                break;
            case MSG_WEIXIN_LOGIN_CALLBACK:
                try {
                    mDialogFragment.dismiss();
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        User user = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), User.class);
                        user.setLogin_type(1);
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
                break;
        }
        return false;
    }

    private void weixinLogin(String nickname, String headimgurl, String openid, String unionid) {
        String url = ServerInfo.serviceIP + ServerInfo.wechatLogin;
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "2");
        params.put("nickname", nickname);
        params.put("headimgurl", headimgurl);
        params.put("openid", openid);
        params.put("unionid", unionid);
        params.put("from_url", SharedPreferencesUtils.getStringValue("from_url", null));
        params.put("from_user_id", SharedPreferencesUtils.getStringValue("from_user_id", null));
        params.put("jump_url", mJumpUrl);
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
                        ToastUtils.show("登录异常");
                    }
                });
            }
        });
    }

    private void getVerifyCode() {
        String url = ServerInfo.serviceIP + ServerInfo.getVerifyCode;
        Map<String, String> params = new HashMap<String, String>();
        params.put("module", "bind_phone");
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

    private void bindPhone(String phone, String verificationCode, final int support) {
        mDialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());
        String url;
        Map<String, String> params = new HashMap<String, String>();
        if (User.getInstance() == null) {
            url = ServerInfo.serviceIP + ServerInfo.weixinBindPhone;
            params.put("type", "2");
            params.put("nickname", weixinNickname);
            params.put("headimgurl", weixinHeadimgurl);
            params.put("openid", weixinOpenid);
            params.put("unionid", weixinUnionid);
            params.put("phone", phone);
            params.put("verification_code", verificationCode);
            params.put("support", "" + support);
        } else {
            url = ServerInfo.serviceIP + ServerInfo.bindPhone;
            params.put("phone", phone);
            params.put("verification_code", verificationCode);
            params.put("support", "" + support);
        }
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = mHandler.obtainMessage(MSG_BIND_PHONE);
                msg.obj = response;
                msg.arg1 = support;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mDialogFragment.dismiss();
                            ToastUtils.show("绑定手机失败，请稍后再试");
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
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        super.doOnBackPressed();
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
