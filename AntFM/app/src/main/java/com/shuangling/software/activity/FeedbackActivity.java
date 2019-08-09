package com.shuangling.software.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.CitySortAdapter;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.City;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

@EnableDragToClose()
public class FeedbackActivity extends AppCompatActivity implements Handler.Callback {

    public static final String TAG = "CityListActivity";

    public static final int MSG_FEED_BACK = 0x1;
    @BindView(R.id.activtyTitle)
    TopTitleBar activtyTitle;
    @BindView(R.id.suggestion)
    EditText suggestion;
    @BindView(R.id.textNumber)
    TextView textNumber;
    @BindView(R.id.phoneNum)
    EditText phoneNum;
    @BindView(R.id.submit)
    TextView submit;


    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(MyApplication.getInstance().getCurrentTheme());
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        init();
    }


    public void submitSuggestion() {

        String url = ServerInfo.serviceIP + ServerInfo.feedback;

        Map<String,String> params=new HashMap<>();
        params.put("opinion",suggestion.getText().toString());
        params.put("phone",phoneNum.getText().toString());
        params.put("phone_type","");
        params.put("browser","");
        params.put("operation_system","");
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_FEED_BACK;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, IOException exception) {


            }
        });


    }

    private void init() {
        mHandler = new Handler(this);
        phoneNum.setText(""+User.getInstance().getPhone());
        suggestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String sug=s.toString();
                String phoneNumber=phoneNum.getText().toString();
                if(!TextUtils.isEmpty(sug)&&CommonUtils.isMobileNO(phoneNumber)){
                    submit.setActivated(true);
                    submit.setEnabled(true);
                }else {
                    submit.setActivated(false);
                    submit.setEnabled(false);
                }

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
                String phoneNumber=s.toString();
                String sug=suggestion.getText().toString();
                if(!TextUtils.isEmpty(sug)&&CommonUtils.isMobileNO(phoneNumber)){
                    submit.setActivated(true);
                    submit.setEnabled(true);
                }else {
                    submit.setActivated(false);
                    submit.setEnabled(false);
                }
            }
        });

    }


    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case MSG_FEED_BACK:

                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show(jsonObject.getString("msg"));
                        finish();
                    }


                } catch (Exception e) {

                }


                break;
        }
        return false;
    }

    @OnClick(R.id.submit)
    public void onViewClicked() {
        submitSuggestion();
    }



}
