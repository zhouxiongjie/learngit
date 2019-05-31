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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangln.antfm.R;
import com.shuangln.antfm.activity.AudioDetailActivity;
import com.shuangln.antfm.adapter.AudioListAdapter;
import com.shuangln.antfm.entity.Audio;
import com.shuangln.antfm.service.AudioPlayerService;
import com.shuangln.antfm.service.IAudioPlayer;


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

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAudioPlayer=IAudioPlayer.Stub.asInterface(service);

            try{
                mAdapter = new AudioListAdapter(getContext(),mAudioPlayer.getPlayerList());
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    }
                });
                listView.setAdapter(mAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try{
                            mAudioPlayer.playAudio(mAdapter.getItem(position));

                        }catch (RemoteException e){
                            e.printStackTrace();
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
        Intent it = new Intent(getContext(), AudioPlayerService.class);
        getContext().bindService(it, mConnection, Context.BIND_AUTO_CREATE);


        close=view.findViewById(R.id.close);
        playOrder.setOnClickListener(this);
        showOrder.setOnClickListener(this);
        close.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.playOrder) {

        } else if(v.getId() == R.id.showOrder){

        }else if(v.getId() == R.id.close){
            dismiss();
        }
    }


    @Override
    public void onDestroy() {
        getContext().unbindService(mConnection);
        super.onDestroy();
    }
}
