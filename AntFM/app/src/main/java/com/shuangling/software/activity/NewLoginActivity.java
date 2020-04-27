package com.shuangling.software.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
public class NewLoginActivity extends AppCompatActivity implements Handler.Callback, PlatformActionListener {


    private static final int MSG_AUTH_CANCEL = 0;
    private static final int MSG_AUTH_ERROR = 1;
    private static final int MSG_AUTH_COMPLETE = 2;

    private static final int MSG_LOGIN_CALLBACK = 3;
    private static final int MSG_GET_VERIFY_CODE = 4;
    private static final int LOGIN_VERIFY_REQUEST = 5;
    private static final int MSG_VERIFY_CODE_LOGIN_CALLBACK = 6;
    private static final int MSG_IS_USER_EXIST = 0X07;

    @BindView(R.id.activtyTitle)
    TopTitleBar activtyTitle;
    @BindView(R.id.phoneNum)
    EditText phoneNum;
    @BindView(R.id.sendCode)
    Button sendCode;
    @BindView(R.id.passwordLogin)
    TextView passwordLogin;
    @BindView(R.id.weiXin)
    ImageView weiXin;
    @BindView(R.id.head)
    SimpleDraweeView head;
    @BindView(R.id.useProtocol)
    TextView useProtocol;
    @BindView(R.id.secretProtocol)
    TextView secretProtocol;
    @BindView(R.id.and)
    TextView and;
    @BindView(R.id.protocol)
    LinearLayout protocol;


    private List<View> mLoginViews = new ArrayList<View>();
    private Handler mHandler;

    private String mPhoneNumber;
    private DialogFragment mDialogFragment;
    //private boolean mUserExist;
    //微信信息
    private String weixinUnionid;
    private String weixinOpenid;
    private String weixinNickname;
    private String weixinHeadimgurl;

    private boolean bindPhone;

    private String mUseProtocolTitle;
    private String mClauseTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login);
        AppManager.clearActivity();
        AppManager.addActivity(this);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        mHandler = new Handler(this);

        init();


    }

    private void init() {
        //getUseProtocol();

        bindPhone = getIntent().getBooleanExtra("bindPhone", false);
        if (MyApplication.getInstance().getStation() != null && !TextUtils.isEmpty(MyApplication.getInstance().getStation().getH5_logo())) {
            Uri uri = Uri.parse(MyApplication.getInstance().getStation().getH5_logo());
            ImageLoader.showThumb(uri, head, CommonUtils.dip2px(75), CommonUtils.dip2px(75));
        }

        activtyTitle.setBackListener(new View.OnClickListener() {
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
                mPhoneNumber = s.toString();
                if (CommonUtils.isMobileNO(mPhoneNumber)) {
                    sendCode.setEnabled(true);
                } else {
                    sendCode.setEnabled(false);
                }
            }
        });


        if (!TextUtils.isEmpty(MyApplication.getInstance().useProtocolTitle)||!TextUtils.isEmpty(MyApplication.getInstance().secretProtocolTitle)){
            protocol.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(MyApplication.getInstance().useProtocolTitle)){
                useProtocol.setText("《"+MyApplication.getInstance().useProtocolTitle+"》");
            }
            if(!TextUtils.isEmpty(MyApplication.getInstance().secretProtocolTitle)){
                secretProtocol.setText("《"+MyApplication.getInstance().secretProtocolTitle+"》");
            }

            if(!TextUtils.isEmpty(MyApplication.getInstance().useProtocolTitle)&&!TextUtils.isEmpty(MyApplication.getInstance().secretProtocolTitle)){
                and.setVisibility(View.VISIBLE);

            }else{
                and.setVisibility(View.GONE);
            }
        }else{
            protocol.setVisibility(View.INVISIBLE);
        }

    }

    @OnClick({R.id.sendCode, R.id.passwordLogin, R.id.weiXin, R.id.qq, R.id.weiBo, R.id.useProtocol, R.id.secretProtocol})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.sendCode:
                getVerifyCode(mPhoneNumber);
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
            case R.id.passwordLogin:
                startActivity(new Intent(this, NewAccountPasswordLoginActivity.class));
                break;
            case R.id.useProtocol: {
                Intent it = new Intent(this, WebViewActivity.class);
                it.putExtra("url", ServerInfo.h5HttpsIP + "/qulity-info?type=2");
                startActivity(it);
            }

            break;
            case R.id.secretProtocol: {
                Intent it = new Intent(this, WebViewActivity.class);
                it.putExtra("url", ServerInfo.h5HttpsIP + "/qulity-info?type=1");
                startActivity(it);
            }
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
                weixinUnionid = platDB.get("unionid");
                weixinOpenid = platDB.get("openid");
                weixinNickname = platDB.get("nickname");
                weixinHeadimgurl = platDB.get("icon");
                String type;
                if (platform.getName().equals(QQ.NAME)) {
                    type = "QQ";
                } else if (platform.getName().equals(SinaWeibo.NAME)) {
                    type = "WB";
                } else {
                    type = "WX";
                }

                long id = Thread.currentThread().getId();
                isUserExist(weixinUnionid);


            }
            break;
            case MSG_LOGIN_CALLBACK: {
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

                        if(bindPhone==true&&TextUtils.isEmpty(User.getInstance().getPhone())){

                            Intent it = new Intent(this, BindPhoneActivity.class);
                            it.putExtra("nickname", weixinNickname);
                            it.putExtra("headimgurl", weixinHeadimgurl);
                            it.putExtra("openid", weixinOpenid);
                            it.putExtra("unionid", weixinUnionid);
                            startActivity(it);

                            ToastUtils.show("登录成功");
                            setResult(RESULT_OK);
                            EventBus.getDefault().post(new CommonEvent("OnLoginSuccess"));
                            AppManager.finishAllActivity();

                        }else{
                            ToastUtils.show("登录成功");
                            setResult(RESULT_OK);
                            EventBus.getDefault().post(new CommonEvent("OnLoginSuccess"));
                            AppManager.finishAllActivity();
                        }

                        //finish();


                    } else {
                        ToastUtils.show(jsonObject.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case MSG_VERIFY_CODE_LOGIN_CALLBACK: {
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

                        Intent it = new Intent(this, NewVerifyCodeLoginActivity.class);
                        it.putExtra("PhoneNumber", mPhoneNumber);
                        startActivity(it);


//                        mCountDownTimer = new CountDownTimer(60 * 1000, 500) {
//                            @Override
//                            public void onTick(long millisUntilFinished) {
//                                verifyCodeViewHolder.timer.setText("(" + millisUntilFinished / 1000 + ")重新发送");
//                                verifyCodeViewHolder.timer.setEnabled(false);
//                            }
//
//                            @Override
//                            public void onFinish() {
//                                verifyCodeViewHolder.timer.setText("重新发送");
//                                verifyCodeViewHolder.timer.setEnabled(true);
//                            }
//                        };
//                        mCountDownTimer.start();

                    } else if (jsonObject != null) {
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
                        //mUserExist=true;
                        weixinLogin(weixinNickname, weixinHeadimgurl, weixinOpenid, weixinUnionid);

                    } else if ((jsonObject != null && jsonObject.getIntValue("code") == 202004)) {
                        //mUserExist=false;
                        //weixinLogin(weixinNickname,weixinHeadimgurl,weixinOpenid,weixinUnionid);
                        Intent it = new Intent(this, BindPhoneActivity.class);
                        it.putExtra("nickname", weixinNickname);
                        it.putExtra("headimgurl", weixinHeadimgurl);
                        it.putExtra("openid", weixinOpenid);
                        it.putExtra("unionid", weixinUnionid);
                        startActivity(it);

                    } else {
                        mDialogFragment.dismiss();
                        ToastUtils.show("登录失败，请稍后再试");
                    }
                } catch (Exception e) {
                    try{
                        mDialogFragment.dismiss();
                        ToastUtils.show("登录失败，请稍后再试");
                    }catch (Exception ex){

                    }




                }
                break;
        }
        return false;
    }

    private void isUserExist(String unionid) {
        mDialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());
        String url = ServerInfo.serviceIP + ServerInfo.isUserExist;
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "0");
        params.put("unionid", unionid);

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

    private void weixinLogin(String nickname, String headimgurl, String openid, String unionid) {

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


    private void verifyCodeLogin(String phone, String verifyCode) {

        String url = ServerInfo.serviceIP + ServerInfo.login;
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "1");
        params.put("phone", phone);
        params.put("verification_code", verifyCode);

        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = mHandler.obtainMessage(MSG_VERIFY_CODE_LOGIN_CALLBACK);
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {

                ToastUtils.show("登录异常");


            }
        });
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
                        try{
                            mDialogFragment.dismiss();
                            ToastUtils.show("获取验证码请求异常");
                        }catch (Exception e){

                        }

                    }
                });




            }
        });
    }


    private void getUseProtocol() {

        String url = ServerInfo.serviceIP + ServerInfo.useProtocol;
        Map<String, String> params = new HashMap<String, String>();

        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mUseProtocolTitle = jsonObject.getJSONObject("data").getString("title");
                    }
                } catch (Exception e) {

                }

                getClauses();

            }

            @Override
            public void onFailure(Call call, Exception exception) {
                getClauses();
            }
        });
    }


    private void getClauses() {

        String url = ServerInfo.serviceIP + ServerInfo.clauses;
        Map<String, String> params = new HashMap<String, String>();

        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        if(jsonObject.getJSONObject("data")!=null){
                            mClauseTitle = jsonObject.getJSONObject("data").getString("title");
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(mUseProtocolTitle)||!TextUtils.isEmpty(mClauseTitle)){
                                    protocol.setVisibility(View.VISIBLE);
                                    if(!TextUtils.isEmpty(mUseProtocolTitle)){
                                        useProtocol.setText("《"+mUseProtocolTitle+"》");
                                    }
                                    if(!TextUtils.isEmpty(mClauseTitle)){
                                        secretProtocol.setText("《"+mClauseTitle+"》");
                                    }

                                    if(!TextUtils.isEmpty(mUseProtocolTitle)&&!TextUtils.isEmpty(mClauseTitle)){
                                        and.setVisibility(View.VISIBLE);

                                    }else{
                                        and.setVisibility(View.GONE);
                                    }
                                }else{
                                    protocol.setVisibility(View.INVISIBLE);
                                }
                            }
                        });


                    }
                } catch (Exception e) {

                    Log.e("test",e.getMessage());
                }

            }

            @Override
            public void onFailure(Call call, Exception exception) {


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


    @Override
    protected void onDestroy() {
        AppManager.removeActivity(this);
        super.onDestroy();
    }
}
