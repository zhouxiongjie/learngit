package com.shuangling.software.adapter;

import android.content.Context;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.shuangling.software.R;
import com.shuangling.software.customview.AudioJumpView;
import com.shuangling.software.entity.AudioInfo;
import com.shuangling.software.service.IAudioPlayer;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.TimeUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AudioListAdapter extends BaseAdapter {

    private List<AudioInfo> mAudios;
    private Context mContext;
    private IAudioPlayer audioPlayer;

    public AudioListAdapter(Context context, List<AudioInfo> audios, IAudioPlayer audioPlayer) {
        super();
        // TODO Auto-generated constructor stub
        this.mAudios = audios;
        this.mContext = context;
        this.audioPlayer=audioPlayer;
    }


    public void updateView(List<AudioInfo> audios) {
        this.mAudios = audios;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (mAudios == null) {
            return 0;
        }
        return mAudios.size();
    }


    @Override
    public AudioInfo getItem(int position) {
        return mAudios.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.audio_item, parent, false);

        }
        ViewHolder vh = new ViewHolder(convertView);
        AudioInfo audio = getItem(position);
        vh.index.setText("" + audio.getIndex());
        vh.title.setText(audio.getTitle());
        vh.publishTime.setText(TimeUtil.formatDateTime(audio.getPublish_at()));
        vh.duration.setText(CommonUtils.getShowTime((long) Float.parseFloat(audio.getDuration()) * 1000));
        try{
            if (audioPlayer.getCurrentAudio()!=null&&audioPlayer.getCurrentAudio().getId()==audio.getId()){
                vh.index.setVisibility(View.GONE);
                vh.audioJump.setVisibility(View.VISIBLE);
            }else{
                vh.index.setVisibility(View.VISIBLE);
                vh.audioJump.setVisibility(View.GONE);
            }
        }catch (RemoteException e){

        }

        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.index)
        TextView index;
        @BindView(R.id.audioJump)
        AudioJumpView audioJump;
        @BindView(R.id.frameLayout)
        FrameLayout frameLayout;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.duration)
        TextView duration;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
