package com.shuangling.software.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.AppManager;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
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
public class BindPhoneActivity extends AppCompatActivity implements Handler.Callback {

    public static final String TAG = BindPhoneActivity.class.getName();

    private static final int MSG_GET_VERIFY_CODE = 1;
    private static final int MSG_WEIXIN_LOGIN_CALLBACK = 2;

    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.phoneNum)
    EditText phoneNum;
    @BindView(R.id.sendCode)
    Button sendCode;
    @BindView(R.id.head)
    SimpleDraweeView head;
    @BindView(R.id.nickname)
    TextView nickname;


    private Handler mHandler;
    private String mPhoneNumber;
    private DialogFragment mDialogFragment;

    private String weixinUnionid;
    private String weixinOpenid;
    private String weixinNickname;
    private String weixinHeadimgurl;

    private boolean hasLogined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        hasLogined = getIntent().getBooleanExtra("hasLogined", false);

        init();
    }


    private void init() {
        activityTitle.getMoreTextView().setTextColor(Color.parseColor("#AFAFB1"));
        weixinUnionid = getIntent().getStringExtra("unionid");
        weixinOpenid = getIntent().getStringExtra("openid");
        weixinNickname = getIntent().getStringExtra("nickname");
        weixinHeadimgurl = getIntent().getStringExtra("headimgurl");

        if (User.getInstance()!=null) {
            activityTitle.setCanBack(true);
            AppManager.clearActivity();
            AppManager.addActivity(this);
            activityTitle.setMoreText("");
            activityTitle.setMoreAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            nickname.setText(User.getInstance().getNickname());
            if (!TextUtils.isEmpty(User.getInstance().getAvatar())) {
                Uri uri = Uri.parse(User.getInstance().getAvatar());
                ImageLoader.showThumb(uri, head, CommonUtils.dip2px(60), CommonUtils.dip2px(60));
            }
        } else {
            activityTitle.setCanBack(false);
            AppManager.addActivity(this);
            activityTitle.setMoreText("跳过");
            activityTitle.setMoreAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    weixinLogin(weixinNickname, weixinHeadimgurl, weixinOpenid, weixinUnionid);
                }
            });

            nickname.setText(weixinNickname);
            if (!TextUtils.isEmpty(weixinHeadimgurl)) {
                Uri uri = Uri.parse(weixinHeadimgurl);
                ImageLoader.showThumb(uri, head, CommonUtils.dip2px(60), CommonUtils.dip2px(60));
            }
        }



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
                } else {
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

                        Intent it = new Intent(this, NewVerifyCodeBindPhoneActivity.class);
                        it.putExtra("hasLogined", hasLogined);
                        it.putExtra("PhoneNumber", mPhoneNumber);
                        it.putExtra("nickname", weixinNickname);
                        it.putExtra("headimgurl", weixinHeadimgurl);
                        it.putExtra("openid", weixinOpenid);
                        it.putExtra("unionid", weixinUnionid);
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

    private void weixinLogin(String nickname, String headimgurl, String openid, String unionid) {
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
        if (User.getInstance()!=null){
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        AppManager.removeActivity(this);
        super.onDestroy();
    }
}
