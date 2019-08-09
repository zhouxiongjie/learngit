package com.shuangling.software.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.service.IAudioPlayer;



public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "BaseActivity";
    public IAudioPlayer mAudioPlayer;



    public OnServiceConnectionListener mOnServiceConnectionListener;

    public void setmOnServiceConnectionListener(OnServiceConnectionListener mOnServiceConnectionListener) {
        this.mOnServiceConnectionListener = mOnServiceConnectionListener;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAudioPlayer=IAudioPlayer.Stub.asInterface(service);
            if(mOnServiceConnectionListener!=null){
                mOnServiceConnectionListener.onServiceConnection(mAudioPlayer);
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(MyApplication.getInstance().getCurrentTheme());
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
        unbindService(mConnection);
        super.onDestroy();
    }


    public interface OnServiceConnectionListener {

        void onServiceConnection(IAudioPlayer audioPlayer);
    }
}
