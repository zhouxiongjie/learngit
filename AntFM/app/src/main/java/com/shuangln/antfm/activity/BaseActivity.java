package com.shuangln.antfm.activity;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.jaeger.library.StatusBarUtil;
import com.shuangln.antfm.R;
import com.shuangln.antfm.adapter.AudioListAdapter;
import com.shuangln.antfm.fragment.DiscoverFragment;
import com.shuangln.antfm.fragment.IndexFragment;
import com.shuangln.antfm.fragment.PersonalCenterFragment;
import com.shuangln.antfm.fragment.RecommendFragment;
import com.shuangln.antfm.service.AudioPlayerService;
import com.shuangln.antfm.service.IAudioPlayer;
import com.shuangln.antfm.utils.StatusBarManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "BaseActivity";
    public IAudioPlayer mAudioPlayer;
    public WindowManager mWindowManager;
    public View mFloatPlayer;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAudioPlayer=IAudioPlayer.Stub.asInterface(service);

            try{
                int state=mAudioPlayer.getPlayerState();
                if(mAudioPlayer.getPlayerState()==IAliyunVodPlayer.PlayerState.Started.ordinal()
                      ||mAudioPlayer.getPlayerState()==IAliyunVodPlayer.PlayerState.Prepared.ordinal()
                      ||mAudioPlayer.getPlayerState()==IAliyunVodPlayer.PlayerState.Paused.ordinal()){

                    mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                    layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
                    layoutParams.token = getWindow().getDecorView().getWindowToken(); // 必须要
                    mFloatPlayer =LayoutInflater.from(BaseActivity.this).inflate(R.layout.anchor_gridview_item,null,false);
                    mWindowManager.addView(mFloatPlayer, layoutParams);

                }

            }catch (RemoteException e){
                e.printStackTrace();
            }



        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent it = new Intent(this, AudioPlayerService.class);
        bindService(it, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
//        Intent it = new Intent(this, AudioPlayerService.class);
//        bindService(it, mConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if(mWindowManager!=null&&mFloatPlayer!=null){
            mWindowManager.removeViewImmediate(mFloatPlayer);
        }
        unbindService(mConnection);
        super.onDestroy();
    }
}
