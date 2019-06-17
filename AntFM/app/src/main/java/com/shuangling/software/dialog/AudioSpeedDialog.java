package com.shuangling.software.dialog;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.service.IAudioPlayer;
import com.shuangling.software.service.AudioPlayerService.PlaySpeed;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class AudioSpeedDialog extends BaseCircleDialog {


    @BindView(R.id.speed050)
    RadioButton speed050;
    @BindView(R.id.speed050RL)
    RelativeLayout speed050RL;
    @BindView(R.id.speed075)
    RadioButton speed075;
    @BindView(R.id.speed075RL)
    RelativeLayout speed075RL;
    @BindView(R.id.speed100)
    RadioButton speed100;
    @BindView(R.id.speed100RL)
    RelativeLayout speed100RL;
    @BindView(R.id.speed125)
    RadioButton speed125;
    @BindView(R.id.speed125RL)
    RelativeLayout speed125RL;
    @BindView(R.id.speed150)
    RadioButton speed150;
    @BindView(R.id.speed150RL)
    RelativeLayout speed150RL;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.close)
    TextView close;
    @BindView(R.id.bottom)
    RelativeLayout bottom;
    Unbinder unbinder;
    private IAudioPlayer mAudioPlayer;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAudioPlayer = IAudioPlayer.Stub.asInterface(service);
            try{
                int playSpeed=mAudioPlayer.getPlaySpeed();
                if(AudioPlayerService.PlaySpeed.values()[playSpeed]==PlaySpeed.Speed050){
                    speed050.setChecked(true);
                }else if(AudioPlayerService.PlaySpeed.values()[playSpeed]==PlaySpeed.Speed075){
                    speed075.setChecked(true);
                }else if(AudioPlayerService.PlaySpeed.values()[playSpeed]==PlaySpeed.Speed100){
                    speed100.setChecked(true);
                }else if(AudioPlayerService.PlaySpeed.values()[playSpeed]==PlaySpeed.Speed125){
                    speed125.setChecked(true);
                }else {
                    speed150.setChecked(true);
                }
            }catch (RemoteException e){
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public static AudioSpeedDialog getInstance() {
        AudioSpeedDialog dialogFragment = new AudioSpeedDialog();
        dialogFragment.setCanceledBack(false);
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.BOTTOM);
        dialogFragment.setWidth(1f);
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_audio_speed, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        Intent it = new Intent(getContext(), AudioPlayerService.class);
        getContext().bindService(it, mConnection, Context.BIND_AUTO_CREATE);
        unbinder = ButterKnife.bind(this, rootView);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getContext().unbindService(mConnection);
        unbinder.unbind();
    }



    @OnClick({R.id.speed050RL, R.id.speed075RL, R.id.speed100RL, R.id.speed125RL, R.id.speed150RL, R.id.close})
    public void onViewClicked(View view) {
        try{
            switch (view.getId()) {
                case R.id.speed050RL:
                    mAudioPlayer.setPlaySpeed(PlaySpeed.Speed050.ordinal());
                    dismiss();
                    break;
                case R.id.speed075RL:
                    mAudioPlayer.setPlaySpeed(PlaySpeed.Speed075.ordinal());
                    dismiss();
                    break;
                case R.id.speed100RL:
                    mAudioPlayer.setPlaySpeed(PlaySpeed.Speed100.ordinal());
                    dismiss();
                    break;
                case R.id.speed125RL:
                    mAudioPlayer.setPlaySpeed(PlaySpeed.Speed125.ordinal());
                    dismiss();
                    break;
                case R.id.speed150RL:
                    mAudioPlayer.setPlaySpeed(PlaySpeed.Speed150.ordinal());
                case R.id.close:
                    dismiss();
                    break;
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }

    }
}
