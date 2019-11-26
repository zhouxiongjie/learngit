package com.shuangling.software.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
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
public class ModifyPasswordActivity extends AppCompatActivity implements Handler.Callback {

    public static final String TAG = ModifyPasswordActivity.class.getName();

    private static final int MSG_MODIFY_PASSWORD = 0x01;
    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.oldPassword)
    EditText oldPassword;
    @BindView(R.id.newPassword)
    EditText newPassword;
    @BindView(R.id.repeatPassword)
    EditText repeatPassword;
    @BindView(R.id.save)
    TextView save;
    @BindView(R.id.forgetPassword)
    TextView forgetPassword;

    private DialogFragment mDialogFragment;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modify_password);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);

        init();
    }



    private void init() {
        save.setActivated(true);
        mHandler = new Handler(this);


    }


    private void modifyPassword(String oldPd, String newPd) {

        mDialogFragment=CommonUtils.showLoadingDialog(getSupportFragmentManager());

        String url = ServerInfo.serviceIP + ":" + ServerInfo.resetPassword;
        Map<String, String> params = new HashMap<>();
        params.put("old_password", oldPd);
        params.put("new_password", newPd);
        OkHttpUtils.put(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = mHandler.obtainMessage(MSG_MODIFY_PASSWORD);
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
            }
        });


    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_MODIFY_PASSWORD: {
                mDialogFragment.dismiss();
                String result = (String) msg.obj;
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    ToastUtils.show(jsonObject.getString("msg"));
                    finish();
                } else {
                    ToastUtils.show(jsonObject.getString("msg"));
                }


            }
            break;
            default:
                break;

        }
        return false;
    }


    @OnClick({R.id.save, R.id.forgetPassword})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.save:
                String oldPd = oldPassword.getText().toString();
                String newPd = newPassword.getText().toString();
                String repeatPd = repeatPassword.getText().toString();
                if (TextUtils.isEmpty(oldPd)) {
                    ToastUtils.show( "请输入旧密码");
                    return;
                }
                if (TextUtils.isEmpty(newPd)) {
                    ToastUtils.show( "请输入新密码");
                    return;
                }
                if (TextUtils.isEmpty(repeatPd)) {
                    ToastUtils.show( "请输入确认密码");
                    return;
                }
                if (!newPd.equals(repeatPd)) {
                    ToastUtils.show( "两次输入的密码不一致");
                    return;
                }

                if (newPd.length() < 6 || newPd.length() > 20) {
                    ToastUtils.show( "新密码长度不合法");
                    return;
                }
                modifyPassword(oldPd, newPd);
                break;
            case R.id.forgetPassword:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;
        }
    }
}
