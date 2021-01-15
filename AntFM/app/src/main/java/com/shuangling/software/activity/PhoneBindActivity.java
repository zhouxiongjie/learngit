package com.shuangling.software.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
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

//@EnableDragToClose()
public class PhoneBindActivity extends QMUIActivity/*AppCompatActivity*/ implements Handler.Callback {
    public static final String TAG = PhoneBindActivity.class.getName();
    private static final int MSG_GET_VERIFY_CODE = 4;

    @BindView(R.id.activity_title)
    /*TopTitleBar*/ QMUITopBarLayout activityTitle;
    @BindView(R.id.phoneNum)
    TextView phoneNum;
    @BindView(R.id.changePhone)
    TextView changePhone;
    @BindView(R.id.root)
    LinearLayout root;
    private DialogFragment mDialogFragment;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_bind);
//        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this); //
        activityTitle.addLeftImageButton(R.drawable.ic_left, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(view -> { //
            finish();
        });
        activityTitle.setTitle("手机号绑定");
        init();
    }

    private void init() {
        mHandler = new Handler(this);
        String phone = "" + User.getInstance().getPhone();
        String sub = phone.substring(3, 7);
        phoneNum.setText(phone.replace(sub, "****"));
    }

    private void getVerifyCode(String phone) {
        mDialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());
        String url = ServerInfo.serviceIP + ServerInfo.getVerifyCode;
        Map<String, String> params = new HashMap<String, String>();
        params.put("module", "update_phone");
        params.put("phone", phone);
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = mHandler.obtainMessage(MSG_GET_VERIFY_CODE);
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(final Call call, Exception exception) {
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
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_VERIFY_CODE: {
                try {
                    mDialogFragment.dismiss();
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        Intent it = new Intent(this, VerifyCodeLoginActivity.class);
                        it.putExtra("PhoneNumber", "" + User.getInstance().getPhone());

                        it.putExtra("PageCategory", VerifyCodeLoginActivity.PageCategory.VerifyBindPhone.ordinal());
                        startActivity(it);
                        finish();
                    } else if (jsonObject != null) {
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
    @OnClick(R.id.changePhone)
    public void onViewClicked() {
        getVerifyCode("" + User.getInstance().getPhone());
    }
}
