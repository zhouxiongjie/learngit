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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.service.AudioPlayerService.PlaySpeed;
import com.shuangling.software.service.IAudioPlayer;

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
    ImageView speed050;
    @BindView(R.id.speed050RL)
    RelativeLayout speed050RL;
    @BindView(R.id.speed075)
    ImageView speed075;
    @BindView(R.id.speed075RL)
    RelativeLayout speed075RL;
    @BindView(R.id.speed100)
    ImageView speed100;
    @BindView(R.id.speed100RL)
    RelativeLayout speed100RL;
    @BindView(R.id.speed125)
    ImageView speed125;
    @BindView(R.id.speed125RL)
    RelativeLayout speed125RL;
    @BindView(R.id.speed150)
    ImageView speed150;
    @BindView(R.id.speed150RL)
    RelativeLayout speed150RL;
    @BindView(R.id.close)
    FontIconView close;
    @BindView(R.id.speed050Text)
    TextView speed050Text;
    @BindView(R.id.speed075Text)
    TextView speed075Text;
    @BindView(R.id.speed100Text)
    TextView speed100Text;
    @BindView(R.id.speed125Text)
    TextView speed125Text;
    @BindView(R.id.speed150Text)
    TextView speed150Text;
    Unbinder unbinder;

    private IAudioPlayer mAudioPlayer;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAudioPlayer = IAudioPlayer.Stub.asInterface(service);
            try {

                long id=Thread.currentThread().getId();
                int playSpeed = mAudioPlayer.getPlaySpeed();
                if (PlaySpeed.values()[playSpeed] == PlaySpeed.Speed050) {
                    speed050.setVisibility(View.VISIBLE);
                    speed050Text.setSelected(true);
                } else if (PlaySpeed.values()[playSpeed] == PlaySpeed.Speed075) {
                    speed075.setVisibility(View.VISIBLE);
                    speed075Text.setSelected(true);
                } else if (PlaySpeed.values()[playSpeed] == PlaySpeed.Speed100) {
                    speed100.setVisibility(View.VISIBLE);
                    speed100Text.setSelected(true);
                } else if (PlaySpeed.values()[playSpeed] == PlaySpeed.Speed125) {
                    speed125.setVisibility(View.VISIBLE);
                    speed125Text.setSelected(true);
                } else {
                    speed150.setVisibility(View.VISIBLE);
                    speed150Text.setSelected(true);
                }
            } catch (RemoteException e) {
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
        try {
            switch (view.getId()) {
                case R.id.speed050RL:
                    mAudioPlayer.setPlaySpeed(PlaySpeed.Speed050.ordinal());

                    setSelect(PlaySpeed.Speed050.ordinal());
                    break;
                case R.id.speed075RL:
                    mAudioPlayer.setPlaySpeed(PlaySpeed.Speed075.ordinal());
                    setSelect(PlaySpeed.Speed075.ordinal());
                    break;
                case R.id.speed100RL:
                    mAudioPlayer.setPlaySpeed(PlaySpeed.Speed100.ordinal());
                    setSelect(PlaySpeed.Speed100.ordinal());
                    break;
                case R.id.speed125RL:
                    mAudioPlayer.setPlaySpeed(PlaySpeed.Speed125.ordinal());
                    setSelect(PlaySpeed.Speed125.ordinal());
                    break;
                case R.id.speed150RL:
                    mAudioPlayer.setPlaySpeed(PlaySpeed.Speed150.ordinal());
                    setSelect(PlaySpeed.Speed150.ordinal());
                    break;
                case R.id.close:
                    dismiss();
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    public void setSelect(int speed){
        if(speed==PlaySpeed.Speed050.ordinal()){
            speed050Text.setSelected(true);
            speed075Text.setSelected(false);
            speed100Text.setSelected(false);
            speed125Text.setSelected(false);
            speed150Text.setSelected(false);
            speed050.setVisibility(View.VISIBLE);
            speed075.setVisibility(View.GONE);
            speed100.setVisibility(View.GONE);
            speed125.setVisibility(View.GONE);
            speed150.setVisibility(View.GONE);


        }else if(speed==PlaySpeed.Speed075.ordinal()){
            speed050Text.setSelected(false);
            speed075Text.setSelected(true);
            speed100Text.setSelected(false);
            speed125Text.setSelected(false);
            speed150Text.setSelected(false);
            speed050.setVisibility(View.GONE);
            speed075.setVisibility(View.VISIBLE);
            speed100.setVisibility(View.GONE);
            speed125.setVisibility(View.GONE);
            speed150.setVisibility(View.GONE);
        }else if(speed==PlaySpeed.Speed100.ordinal()){
            speed050Text.setSelected(false);
            speed075Text.setSelected(false);
            speed100Text.setSelected(true);
            speed125Text.setSelected(false);
            speed150Text.setSelected(false);
            speed050.setVisibility(View.GONE);
            speed075.setVisibility(View.GONE);
            speed100.setVisibility(View.VISIBLE);
            speed125.setVisibility(View.GONE);
            speed150.setVisibility(View.GONE);

        }else if(speed==PlaySpeed.Speed125.ordinal()){
            speed050Text.setSelected(false);
            speed075Text.setSelected(false);
            speed100Text.setSelected(false);
            speed125Text.setSelected(true);
            speed150Text.setSelected(false);
            speed050.setVisibility(View.GONE);
            speed075.setVisibility(View.GONE);
            speed100.setVisibility(View.GONE);
            speed125.setVisibility(View.VISIBLE);
            speed150.setVisibility(View.GONE);

        }else if(speed==PlaySpeed.Speed150.ordinal()){
            speed050Text.setSelected(false);
            speed075Text.setSelected(false);
            speed100Text.setSelected(false);
            speed125Text.setSelected(false);
            speed150Text.setSelected(true);
            speed050.setVisibility(View.GONE);
            speed075.setVisibility(View.GONE);
            speed100.setVisibility(View.GONE);
            speed125.setVisibility(View.GONE);
            speed150.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }
}
