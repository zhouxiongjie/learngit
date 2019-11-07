package com.shuangling.software.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;
import com.youngfeng.snake.annotations.EnableDragToClose;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;


public class StartupActivity extends Activity implements Handler.Callback {

    public static final int MSG_GLOBAL_DECORATE = 0x00;
    @BindView(R.id.background)
    ImageView background;

    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_startup);
        ButterKnife.bind(this);

        if (!isTaskRoot()) {
            finish();
            return;
        }

        init();


    }


    private void init() {
        mHandler = new Handler(this);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoHome();
            }
        }, 4000);

        //initTheme();
        verifyUserInfo();
    }

    public void initTheme() {

        String url = ServerInfo.serviceIP + ServerInfo.globalDecorate;
        Map<String, String> params = new HashMap<String, String>();

        OkHttpUtils.getNotAuthorization(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                Message msg = Message.obtain();
                msg.what = MSG_GLOBAL_DECORATE;
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });


    }


    private void gotoHome() {

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void verifyUserInfo() {
        User.setInstance(SharedPreferencesUtils.getUser());
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        if(SharedPreferencesUtils.getUser()!=null){
            pushService.bindAccount(SharedPreferencesUtils.getUser().getUsername(), new CommonCallback() {
                @Override
                public void onSuccess(String s) {
                    Log.i("bindAccount-onSuccess",s);
                }

                @Override
                public void onFailed(String s, String s1) {
                    Log.i("bindAccount-onFailed",s);
                    Log.i("bindAccount-onFailed",s1);
                }
            });
        }
        EventBus.getDefault().post(new CommonEvent("OnLoginSuccess"));

    }




    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case MSG_GLOBAL_DECORATE:
                try{
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);

                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        JSONObject jo=jsonObject.getJSONObject("data");
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date startTime = format.parse(jo.getString("start_time"));
                        Date endTime = format.parse(jo.getString("end_time"));
                        Date today = new Date();

                        if(today.after(startTime)&&today.before(endTime)){
                            if(Color.parseColor(jo.getString("background_color"))==getResources().getColor(R.color.themeBlue)){
                                MyApplication.getInstance().setCurrentTheme(R.style.AppThemeBlue);
                            }else if(Color.parseColor(jo.getString("background_color"))==getResources().getColor(R.color.themePurple)){
                                MyApplication.getInstance().setCurrentTheme(R.style.AppThemePurple);
                            }else if(Color.parseColor(jo.getString("background_color"))==getResources().getColor(R.color.themeRed)){
                                MyApplication.getInstance().setCurrentTheme(R.style.AppThemeRed);
                            }else if(Color.parseColor(jo.getString("background_color"))==getResources().getColor(R.color.themeGreen)){
                                MyApplication.getInstance().setCurrentTheme(R.style.AppThemeGreen);
                            }else if(Color.parseColor(jo.getString("background_color"))==getResources().getColor(R.color.themeOrange)){
                                MyApplication.getInstance().setCurrentTheme(R.style.AppThemeOrange);
                            }
                            MyApplication.getInstance().setBackgroundImage(jo.getString("background_image"));

                        }

                    }



                }catch (Exception e){
                    e.printStackTrace();
                }


                break;
        }
        return false;
    }
}
