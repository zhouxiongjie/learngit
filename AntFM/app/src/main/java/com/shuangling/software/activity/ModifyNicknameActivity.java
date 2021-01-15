package com.shuangling.software.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
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
import okhttp3.Call;

//@EnableDragToClose()
public class ModifyNicknameActivity extends QMUIActivity/*AppCompatActivity*/ {
    public static final String TAG = ModifyNicknameActivity.class.getName();
    @BindView(R.id.activity_title)
    /*TopTitleBar*/ QMUITopBarLayout activityTitle;
    @BindView(R.id.nickName)
    EditText nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nickname);
//        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this); //
        activityTitle.addLeftImageButton(R.drawable.ic_left, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(view -> { //
            finish();
        });
        activityTitle.setTitle("修改昵称");
        init();
    }

    private void init() {
        nickName.setText("" + User.getInstance().getNickname());
        if (!TextUtils.isEmpty(User.getInstance().getNickname())) {
            nickName.setSelection(User.getInstance().getNickname().length());//将光标移至文字末尾
        }
//        activityTitle.setMoreAction(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!TextUtils.isEmpty(nickName.getText().toString().trim())) {
//                    modifyUerInfo("nickname", nickName.getText().toString().trim());
//                } else {
//                    ToastUtils.show("没有输入名字,请重新填写");
//                }
//            }
//        });
        activityTitle.addRightTextButton("保存",com.qmuiteam.qmui.R.id.right_icon).setOnClickListener(view -> {
            if (!TextUtils.isEmpty(nickName.getText().toString().trim())) {
                    modifyUerInfo("nickname", nickName.getText().toString().trim());
                } else {
                    ToastUtils.show("没有输入名字,请重新填写");
                }
        });
    }

    public void modifyUerInfo(final String key, final String value) {
        String url = ServerInfo.serviceIP + ServerInfo.modifyUserInfo;
        Map<String, String> params = new HashMap<>();
        params.put(key, value);
        OkHttpUtils.put(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                String result = response;
                final JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    if (key.equals("nickname")) {
                        User.getInstance().setNickname(value);
                        SharedPreferencesUtils.saveUser(User.getInstance());
                        //清理缓存
                        EventBus.getDefault().post(new CommonEvent("updateNickname"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }
                } else if (jsonObject != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.show(jsonObject.getString("msg"));
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.show("修改用户信息失败");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }
}
