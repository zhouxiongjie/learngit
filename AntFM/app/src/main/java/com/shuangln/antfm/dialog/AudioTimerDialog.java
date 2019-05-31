package com.shuangln.antfm.dialog;

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
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangln.antfm.R;
import com.shuangln.antfm.service.AudioPlayerService;
import com.shuangln.antfm.service.AudioPlayerService.TimerType;
import com.shuangln.antfm.service.IAudioPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class AudioTimerDialog extends BaseCircleDialog  {


    @BindView(R.id.min10)
    RadioButton min10;
    @BindView(R.id.min10RL)
    RelativeLayout min10RL;
    @BindView(R.id.min20)
    RadioButton min20;
    @BindView(R.id.min20RL)
    RelativeLayout min20RL;
    @BindView(R.id.min30)
    RadioButton min30;
    @BindView(R.id.min30RL)
    RelativeLayout min30RL;
    @BindView(R.id.min60)
    RadioButton min60;
    @BindView(R.id.min60RL)
    RelativeLayout min60RL;
    @BindView(R.id.playThis)
    RadioButton playThis;
    @BindView(R.id.playThisRL)
    RelativeLayout playThisRL;
    @BindView(R.id.noOpen)
    RadioButton noOpen;
    @BindView(R.id.noOpenRL)
    RelativeLayout noOpenRL;
    @BindView(R.id.close)
    TextView close;
    Unbinder unbinder;
    @BindView(R.id.bottom)
    RelativeLayout bottom;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    private IAudioPlayer mAudioPlayer;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAudioPlayer = IAudioPlayer.Stub.asInterface(service);
            try {
                int timerType=mAudioPlayer.getTimerType();
                if(TimerType.values()[timerType]==TimerType.Min10){
                    min10.setChecked(true);
                }else if(TimerType.values()[timerType]==TimerType.Min20){
                    min20.setChecked(true);
                }else if(TimerType.values()[timerType]==TimerType.Min30){
                    min30.setChecked(true);
                }else if(TimerType.values()[timerType]==TimerType.Min60){
                    min60.setChecked(true);
                }else if(TimerType.values()[timerType]==TimerType.PlayThis){
                    playThis.setChecked(true);
                }else {
                    noOpen.setChecked(true);
                }


            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public static AudioTimerDialog getInstance() {
        AudioTimerDialog dialogFragment = new AudioTimerDialog();
        dialogFragment.setCanceledBack(false);
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.BOTTOM);
        dialogFragment.setWidth(1f);
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_audio_timer, container, false);

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

    @OnClick({R.id.min10RL, R.id.min20RL, R.id.min30RL, R.id.min60RL, R.id.playThisRL, R.id.noOpenRL, R.id.close})
    public void onViewClicked(View view) {
        try{
            switch (view.getId()) {

                case R.id.min10RL:
                    mAudioPlayer.setTimerType(TimerType.Min10.ordinal());
                    dismiss();
                    break;
                case R.id.min20RL:
                    mAudioPlayer.setTimerType(TimerType.Min20.ordinal());
                    dismiss();
                    break;
                case R.id.min30RL:
                    mAudioPlayer.setTimerType(TimerType.Min30.ordinal());
                    dismiss();
                    break;
                case R.id.min60RL:
                    mAudioPlayer.setTimerType(TimerType.Min60.ordinal());
                    dismiss();
                    break;
                case R.id.playThisRL:
                    mAudioPlayer.setTimerType(TimerType.PlayThis.ordinal());
                    dismiss();
                    break;
                case R.id.noOpenRL:
                    mAudioPlayer.setTimerType(TimerType.Cancel.ordinal());
                case R.id.close:
                    dismiss();
                    break;
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }

    }




}
