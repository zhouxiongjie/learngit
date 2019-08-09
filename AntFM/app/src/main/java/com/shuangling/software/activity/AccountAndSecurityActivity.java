package com.shuangling.software.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.User;
import com.youngfeng.snake.annotations.EnableDragToClose;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@EnableDragToClose()
public class AccountAndSecurityActivity extends AppCompatActivity {

    public static final String TAG = AccountAndSecurityActivity.class.getName();
    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.phoneNum)
    TextView phoneNum;
    @BindView(R.id.phoneBind)
    RelativeLayout phoneBind;
    @BindView(R.id.modifyPassword)
    RelativeLayout modifyPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(MyApplication.getInstance().getCurrentTheme());
        setContentView(R.layout.activity_account_and_security);
        ButterKnife.bind(this);
    }



    @Override
    protected void onResume() {
        String phone=""+User.getInstance().getPhone();
        String sub=phone.substring(3,7);
        phoneNum.setText(phone.replace(sub,"****"));

        super.onResume();
    }

    @OnClick({R.id.phoneBind,R.id.modifyPassword})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.phoneBind:
                startActivity(new Intent(this,PhoneBindActivity.class));

                break;
            case R.id.modifyPassword:

                startActivity(new Intent(this,ModifyPasswordActivity.class));
                break;
        }
    }
}
