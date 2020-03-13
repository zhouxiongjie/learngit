package com.shuangling.software.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.service.IAudioPlayer;
import com.shuangling.software.utils.SharedPreferencesUtils;


public class BaseActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Resources getResources() {
        //获取到resources对象
        Resources res=super.getResources();
        float appFontSize = SharedPreferencesUtils.getFloatValue(FontSizeSettingActivity.FONT_SIZE, 1.00f);
        float systemFontSize = res.getConfiguration().fontScale;
        float mixFontSize=appFontSize*systemFontSize;

        float max = (appFontSize > systemFontSize) ? appFontSize : systemFontSize;
        float fontSize=Math.min(mixFontSize,max);
        res.getConfiguration().fontScale=fontSize;
        res.updateConfiguration(res.getConfiguration(), res.getDisplayMetrics());
        return res;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //if (newConfig.fontScale != 1)//非默认值
        getResources();
        super.onConfigurationChanged(newConfig);
    }

}
