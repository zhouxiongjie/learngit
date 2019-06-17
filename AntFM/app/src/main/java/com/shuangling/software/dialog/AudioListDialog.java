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
import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.adapter.AudioListAdapter;
import com.shuangling.software.entity.Audio;
import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.service.IAudioPlayer;
import com.shuangling.software.utils.CommonUtils;

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

        if(AudioPlayerService.sPlayOrder==PLAY_CIRCLE){
            Drawable drawableLeft = getResources().getDrawable(R.drawable.play_circle);
            playOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
            //playOrder.setCompoundDrawablePadding(CommonUtils.dip2px(10));
        }else if(AudioPlayerService.sPlayOrder==PLAY_ORDER){
            Drawable drawableLeft = getResources().getDrawable(R.drawable.play_order);
            playOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
            //playOrder.setCompoundDrawablePadding(CommonUtils.dip2px(10));
        }else if(AudioPlayerService.sPlayOrder==PLAY_RANDOM){
            Drawable drawableLeft = getResources().getDrawable(R.drawable.play_random);
            playOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
            //playOrder.setCompoundDrawablePadding(CommonUtils.dip2px(10));
        }else {
            Drawable drawableLeft = getResources().getDrawable(R.drawable.play_loop);
            playOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
            //playOrder.setCompoundDrawablePadding(CommonUtils.dip2px(10));
        }

        if(AudioPlayerService.sSequence==POSITIVE){
            Drawable drawableLeft = getResources().getDrawable(R.drawable.order_asc);
            playOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
        }else {
            Drawable drawableLeft = getResources().getDrawable(R.drawable.order_dec);
            playOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
        }

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
                //playOrder.setCompoundDrawablePadding(CommonUtils.dip2px(10));
            }else if(AudioPlayerService.sPlayOrder==PLAY_ORDER){
                AudioPlayerService.sPlayOrder=PLAY_RANDOM;
                Drawable drawableLeft = getResources().getDrawable(R.drawable.play_random);
                playOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
                //playOrder.setCompoundDrawablePadding(CommonUtils.dip2px(10));
            }else if(AudioPlayerService.sPlayOrder==PLAY_RANDOM){
                AudioPlayerService.sPlayOrder=PLAY_LOOP;
                Drawable drawableLeft = getResources().getDrawable(R.drawable.play_loop);
                playOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
                //playOrder.setCompoundDrawablePadding(CommonUtils.dip2px(10));
            }else {
                AudioPlayerService.sPlayOrder=PLAY_CIRCLE;
                Drawable drawableLeft = getResources().getDrawable(R.drawable.play_circle);
                playOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
                //playOrder.setCompoundDrawablePadding(CommonUtils.dip2px(10));
            }


        } else if(v.getId() == R.id.showOrder){

            if(AudioPlayerService.sSequence==POSITIVE){
                AudioPlayerService.sSequence=INVERTED;
                Drawable drawableLeft = getResources().getDrawable(R.drawable.order_dec);
                showOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
                //playOrder.setCompoundDrawablePadding(CommonUtils.dip2px(10));
            }else{
                AudioPlayerService.sSequence=POSITIVE;
                Drawable drawableLeft = getResources().getDrawable(R.drawable.order_asc);
                showOrder.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null, null, null);
                //playOrder.setCompoundDrawablePadding(CommonUtils.dip2px(10));
            }

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
