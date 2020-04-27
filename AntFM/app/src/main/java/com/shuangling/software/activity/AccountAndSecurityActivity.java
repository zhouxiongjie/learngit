package com.shuangling.software.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.BaseModel;
import com.shuangling.software.entity.User;
import com.shuangling.software.entity.UserThirdPlatformModel;
import com.shuangling.software.entity.UserThirdPlatformModel.DataBean;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jake.share.frdialog.dialog.FRDialog;
import cn.jake.share.frdialog.interfaces.FRDialogClickListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;

@EnableDragToClose()
public class AccountAndSecurityActivity extends AppCompatActivity implements PlatformActionListener {

    public static final String TAG = AccountAndSecurityActivity.class.getName();
    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.phoneNum)
    TextView phoneNum;
    @BindView(R.id.phoneBind)
    RelativeLayout phoneBind;
    @BindView(R.id.modifyPassword)
    RelativeLayout modifyPassword;
    @BindView(R.id.bind_phone_text)
    TextView bindPhoneText;
    @BindView(R.id.wechat_bind)
    TextView wechatBindText;
    @BindView(R.id.wechat_layout)
    RelativeLayout wechatLayout;

    private boolean wechatBind = false;

    Handler handler = new Handler();

    private DialogFragment mDialogFragment;

    private List<DataBean> platformList = new ArrayList<>();
    //    如果存在手机号码 那就是绑定手机了  就存在修改密码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_and_security);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
    }

    private void setUserInfo() {
        String phone = User.getInstance().getPhone();
        if (TextUtils.isEmpty(phone)) {
            modifyPassword.setVisibility(View.GONE);
            bindPhoneText.setText("绑定手机号");
            phoneNum.setText("去绑定");
            phoneNum.setTextColor(CommonUtils.getThemeColor(this));

        } else {
            modifyPassword.setVisibility(View.VISIBLE);
            bindPhoneText.setText("更换手机号");

            String sub = phone.substring(3, 7);
            phoneNum.setText(phone.replace(sub, "****"));
            phoneNum.setTextColor(getResources().getColor(R.color.text_gray));
        }


        if ((platformList != null && platformList.size() > 0) || wechatBind){
            wechatBindText.setText("已绑定");
            wechatBindText.setTextColor(getResources().getColor(R.color.text_gray));
        }else {
            wechatBindText.setText("尚未绑定");
            wechatBindText.setTextColor(CommonUtils.getThemeColor(this));
        }
    }

    private void getThirdPlatformInfo(){
        String url = ServerInfo.serviceIP + ServerInfo.getThirdPlatformInfo;

        OkHttpUtils.get(url, null, new OkHttpCallback(this) {
            @Override
            public void onFailure(Call call, Exception e) {

            }

            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {
                    UserThirdPlatformModel platformModel = JSON.parseObject(response, UserThirdPlatformModel.class);
                    if (platformModel.getCode() == 100000){
                        platformList = platformModel.getData();
                    }

                }catch (Exception e){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.show("获取用户信息失败");
                        }
                    });
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setUserInfo();
                    }
                });
            }
        });

    }

    private void unbundWechat(){

        if (mDialogFragment != null) mDialogFragment.dismiss();
        mDialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());
        String url = ServerInfo.serviceIP + ServerInfo.getThirdPlatformInfo;
        Map<String, String> params = new HashMap<>();
        params.put("type","0");

        OkHttpUtils.delete(url, params, new OkHttpCallback(this) {
            @Override
            public void onFailure(Call call, Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            mDialogFragment.dismiss();
                            ToastUtils.show("解绑失败,请稍后再试");
                        }catch (Exception e){

                        }

                    }
                });
            }

            @Override
            public void onResponse(Call call, String response) throws IOException {
                try {
                    final BaseModel result = JSON.parseObject(response, BaseModel.class);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            try{
                                mDialogFragment.dismiss();
                                if (result.getCode() == 100000) {
                                    ToastUtils.show("解绑成功");
                                    if (platformList.size() > 0) platformList.remove(0);

                                    wechatBind = false;
                                    setUserInfo();
                                }
                            }catch (Exception e){

                            }


                        }
                    });


                } catch (Exception e) {
                    mDialogFragment.dismiss();
                }
            }
        });
    }

    private void getWechatInfo(){
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.setPlatformActionListener(this);
        // true不使用SSO授权，false使用SSO授权
        wechat.SSOSetting(false);
        //获取用户资料
        wechat.showUser(null);
    }

    private void bindWechat(final Platform platform , String support){

        PlatformDb platDB = platform.getDb();//获取数平台数据DB
        //通过DB获取各种数据
        String weixinUnionid = platDB.get("unionid");
        String weixinOpenid = platDB.get("openid");
        String weixinNickname = platDB.get("nickname");
        String weixinHeadimgurl = platDB.get("icon");

        String url = ServerInfo.serviceIP + ServerInfo.bindWechat;
        Map<String, String> params = new HashMap<>();
        params.put("type","2");
        params.put("nickname",weixinNickname);
        params.put("headimgurl",weixinHeadimgurl);
        params.put("openid",weixinOpenid);
        params.put("unionid",weixinUnionid);
        params.put("support",support);

        if (mDialogFragment != null) mDialogFragment.dismiss();
        mDialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());

        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onFailure(Call call, Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            mDialogFragment.dismiss();
                            ToastUtils.show("绑定失败,请稍后再试");
                            Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                            wechat.removeAccount(true);
                        }catch (Exception e){

                        }

                    }
                });
            }

            @Override
            public void onResponse(Call call, String response) throws IOException {
                try {
                    final BaseModel result = JSON.parseObject(response,BaseModel.class);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                mDialogFragment.dismiss();
                                if (result.getCode() == 100000){
                                    wechatBind = true;
                                    setUserInfo();
                                    ToastUtils.show("绑定成功");
                                    Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                                    wechat.removeAccount(true);
                                }else {
                                    forceBind(platform);
                                }
                            }catch (Exception e){

                            }


                        }
                    });

                }catch (Exception e){
                    try {
                        mDialogFragment.dismiss();
                        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                        wechat.removeAccount(true);
                    }catch (Exception ex){

                    }

                }
            }
        });
    }

    private void forceBind(final Platform platform){
        new CircleDialog.Builder()
                .setTitle("提示")
                .setText("该第三方账号已经绑定其他账号，是否重新绑定此账号？")
                .setPositive("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bindWechat(platform,"1");
                    }
                })
                .setNegative("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.show("取消绑定");
                    }
                })
                .setCanceledOnTouchOutside(false)
                .setCancelable(false)
                .show(getSupportFragmentManager());
    }

    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {

        final Platform platform1 = platform;

        handler.post(new Runnable() {
            @Override
            public void run() {
                bindWechat(platform1,"0");
            }
        });

    }

    @Override
    public void onError(Platform platform, int action, Throwable throwable) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.show("授权失败");
            }
        });
    }

    @Override
    public void onCancel(Platform platform, int action) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.show("取消授权");
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
//        String phone = "" + User.getInstance().getPhone();
//        String sub = phone.substring(3, 7);
//        phoneNum.setText(phone.replace(sub, "****"));

        setUserInfo();
        getThirdPlatformInfo();

    }



    FRDialog dialog;
    private void wechatLayoutClick(){
        String phone = User.getInstance().getPhone();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show("当前账号为唯一登录账号，无法进行解绑。");
            return;
        }

        if (platformList.size() > 0 || wechatBind){//解绑
             dialog = new FRDialog.CommonBuilder(this)
                     .setContentView(R.layout.unbundling_platform_layout)
                     .setFullWidth()
                     .setFromBottom()
                     .setOnClickListener(R.id.confirm_button, new FRDialogClickListener() {
                         @Override
                         public boolean onDialogClick(View view) {
                             //去解绑
                             unbundWechat();
                             return true;
                         }
                     })
                     .setOnClickListener(R.id.cancel_button, new FRDialogClickListener() {
                         @Override
                         public boolean onDialogClick(View view) {
//                             dialog.dismiss();
                             return true;
                         }
                     })
                     .show();

        }else {//绑定

            getWechatInfo();
        }
    }

    private void phoneLayoutClick(){
        String phone = User.getInstance().getPhone();
        if (TextUtils.isEmpty(phone)) {
            Intent intent = new Intent(this, BindPhoneActivity.class);
            intent.putExtra("hasLogined",true);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, PhoneBindActivity.class);
            startActivity(intent);
        }
    }

    @OnClick({R.id.phoneBind, R.id.modifyPassword, R.id.wechat_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.phoneBind:
                phoneLayoutClick();
                break;
            case R.id.modifyPassword:
                startActivity(new Intent(this, ModifyPasswordActivity.class));
                break;
            case R.id.wechat_layout:
                wechatLayoutClick();
                break;
        }
    }

}
