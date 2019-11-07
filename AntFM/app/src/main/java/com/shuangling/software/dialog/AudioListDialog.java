package com.shuangling.software.dialog;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.mylhyl.circledialog.CircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.activity.SettingActivity;
import com.shuangling.software.adapter.AudioListAdapter;
import com.shuangling.software.entity.Audio;
import com.shuangling.software.entity.AudioInfo;
import com.shuangling.software.event.PlayerEvent;
import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.service.IAudioPlayer;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.shuangling.software.service.AudioPlayerService.INVERTED;
import static com.shuangling.software.service.AudioPlayerService.PLAY_CIRCLE;
import static com.shuangling.software.service.AudioPlayerService.PLAY_LOOP;
import static com.shuangling.software.service.AudioPlayerService.PLAY_ORDER;
import static com.shuangling.software.service.AudioPlayerService.PLAY_RANDOM;
import static com.shuangling.software.service.AudioPlayerService.POSITIVE;


/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class AudioListDialog extends BaseCircleDialog implements View.OnClickListener {


    private TextView playOrder;

    private TextView showOrder;

    private ListView listView;

    private TextView close;

    private AudioListAdapter mAdapter;
    private IAudioPlayer mAudioPlayer;
    private int mNetPlay;
    private int mNeedTipPlay;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAudioPlayer=IAudioPlayer.Stub.asInterface(service);

            try{
                List<AudioInfo> audios=mAudioPlayer.getPlayerList();
                List<AudioInfo> newAudios = new ArrayList<>();
                if(AudioPlayerService.sSequence==INVERTED){
                    for (int i = audios.size()-1; i >=0; i--) {
                        newAudios.add(audios.get(i));
                    }

                }else{
                    newAudios=audios;
                }
                mAdapter = new AudioListAdapter(getContext(),newAudios,mAudioPlayer);

                listView.setAdapter(mAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        //判断网络环境
                        if (CommonUtils.getNetWorkType(getContext()) == CommonUtils.NETWORKTYPE_MOBILE) {
                            if(mNetPlay==0){
                                //每次提醒
                                new CircleDialog.Builder()
                                        .setCanceledOnTouchOutside(false)
                                        .setCancelable(false)

                                        .setText("当前非WiFi环境，是否使用流量播放")
                                        .setNegative("暂停播放", null)
                                        .setPositive("继续播放", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try{
                                                    mAudioPlayer.playAudio(mAdapter.getItem(position));
                                                }catch (RemoteException e){

                                                }

                                            }
                                        })
                                        .show(getFragmentManager());
                            }else{
                                //提醒一次
                                if(mNeedTipPlay==1) {
                                    new CircleDialog.Builder()
                                            .setCanceledOnTouchOutside(false)
                                            .setCancelable(false)

                                            .setText("当前非WiFi环境，是否使用流量播放")
                                            .setNegative("暂停播放", null)
                                            .setPositive("继续播放", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    try{
                                                        mAudioPlayer.playAudio(mAdapter.getItem(position));
                                                    }catch (RemoteException e){

                                                    }

                                                }
                                            })
                                            .show(getFragmentManager());
                                }else{
                                    try{
                                        mAudioPlayer.playAudio(mAdapter.getItem(position));

                                    }catch (RemoteException e){
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }else{
                            try{
                                mAudioPlayer.playAudio(mAdapter.getItem(position));

                            }catch (RemoteException e){
                                e.printStackTrace();
                            }
                        }


                    }
                });
            }catch (RemoteException e){
                e.printStackTrace();
            }



        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public static AudioListDialog getInstance() {
        AudioListDialog dialogFragment = new AudioListDialog();
        dialogFragment.setCanceledBack(false);
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.BOTTOM);
        dialogFragment.setWidth(1f);
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_audio_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        playOrder=view.findViewById(R.id.playOrder);
        showOrder=view.findViewById(R.id.showOrder);
        listView=view.findViewById(R.id.listView);
        mNetPlay=SharedPreferencesUtils.getIntValue(SettingActivity.NET_PLAY,0);
        mNeedTipPlay=SharedPreferencesUtils.getIntValue(SettingActivity.NEED_TIP_PLAY,0);
        Intent it = new Intent(getContext(), AudioPlayerService.class);
        getContext().bindService(it, mConnection, Context.BIND_AUTO_CREATE);

        if(AudioPlayerService.sPlayOrder==PLAY_CIRCLE){
            Drawable drawableLeft = getResources().getDrawable(R.drawable.play_circle);
            playOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
            playOrder.setText(R.string.play_circle);
        }else if(AudioPlayerService.sPlayOrder==PLAY_ORDER){
            Drawable drawableLeft = getResources().getDrawable(R.drawable.play_order);
            playOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
            playOrder.setText(R.string.play_order);
        }else if(AudioPlayerService.sPlayOrder==PLAY_RANDOM){
            Drawable drawableLeft = getResources().getDrawable(R.drawable.play_random);
            playOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
            playOrder.setText(R.string.play_random);
        }else {
            Drawable drawableLeft = getResources().getDrawable(R.drawable.play_loop);
            playOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
            playOrder.setText(R.string.play_loop);
        }

        if(AudioPlayerService.sSequence==POSITIVE){
            Drawable drawableLeft = getResources().getDrawable(R.drawable.order_asc);
            showOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
            showOrder.setText(R.string.order_positive);

        }else {
            Drawable drawableLeft = getResources().getDrawable(R.drawable.order_dec);
            showOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
            showOrder.setText(R.string.order_inverted);
        }
        EventBus.getDefault().register(this);
        close=view.findViewById(R.id.close);
        playOrder.setOnClickListener(this);
        showOrder.setOnClickListener(this);
        close.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.playOrder) {

            if(AudioPlayerService.sPlayOrder==PLAY_CIRCLE){
                AudioPlayerService.sPlayOrder=PLAY_ORDER;
                Drawable drawableLeft = getResources().getDrawable(R.drawable.play_order);
                playOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
                playOrder.setText(R.string.play_order);
            }else if(AudioPlayerService.sPlayOrder==PLAY_ORDER){
                AudioPlayerService.sPlayOrder=PLAY_RANDOM;
                Drawable drawableLeft = getResources().getDrawable(R.drawable.play_random);
                playOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
                playOrder.setText(R.string.play_random);
            }else if(AudioPlayerService.sPlayOrder==PLAY_RANDOM){
                AudioPlayerService.sPlayOrder=PLAY_LOOP;
                Drawable drawableLeft = getResources().getDrawable(R.drawable.play_loop);
                playOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
                playOrder.setText(R.string.play_loop);
            }else {
                AudioPlayerService.sPlayOrder=PLAY_CIRCLE;
                Drawable drawableLeft = getResources().getDrawable(R.drawable.play_circle);
                playOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
                playOrder.setText(R.string.play_circle);
            }


        } else if(v.getId() == R.id.showOrder){

            if(AudioPlayerService.sSequence==POSITIVE){
                AudioPlayerService.sSequence=INVERTED;
                Drawable drawableLeft = getResources().getDrawable(R.drawable.order_dec);
                showOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
                showOrder.setText(R.string.order_inverted);
                try{
                    List<AudioInfo> audios=mAudioPlayer.getPlayerList();
                    List<AudioInfo> newAudios = new ArrayList<>();
                    if(AudioPlayerService.sSequence==INVERTED){
                        for (int i = audios.size()-1; i >=0; i--) {
                            newAudios.add(audios.get(i));
                        }

                    }else{
                        newAudios=audios;
                    }
                    mAdapter.updateView(newAudios);
                }catch (RemoteException e){

                }



            }else{
                AudioPlayerService.sSequence=POSITIVE;
                Drawable drawableLeft = getResources().getDrawable(R.drawable.order_asc);
                showOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
                showOrder.setText(R.string.order_positive);
                try{
                    List<AudioInfo> audios=mAudioPlayer.getPlayerList();
                    mAdapter.updateView(audios);
                }catch (RemoteException e){

                }
            }

        }else if(v.getId() == R.id.close){
            dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(PlayerEvent event) {
        if (event.getEventName().equals("OnPrepared")) {

            mAdapter.notifyDataSetChanged();


        } else if(event.getEventName().equals("OnPause")){
            mAdapter.notifyDataSetChanged();
        }else if(event.getEventName().equals("OnStart")){
            mAdapter.notifyDataSetChanged();

        }
    }


    @Override
    public void onDestroyView() {
        getContext().unbindService(mConnection);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
}
