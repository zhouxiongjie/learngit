package com.shuangling.software.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.entity.Audio;
import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AudioListAdapter extends BaseAdapter {

    private List<Audio> mAudios;
    private Context mContext;


    public AudioListAdapter(Context context, List<Audio> audios) {
        super();
        // TODO Auto-generated constructor stub
        this.mAudios = audios;
        this.mContext = context;
    }


    public void setData(List<Audio> audios) {
        this.mAudios = audios;
    }


    @Override
    public int getCount() {
        return mAudios.size();
    }


    @Override
    public Audio getItem(int position) {
        return mAudios.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder vh;
        if (convertView != null) {
            view = convertView;
            vh = new ViewHolder(view);;
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.audio_item, parent, false);
            vh = new ViewHolder(view);

        }
        Audio audio=getItem(position);
        vh.title.setText(audio.getAudios().get(0).getTitle());
        vh.publishTime.setText(audio.getAudios().get(0).getPublish_at());
        vh.duration.setText(""+audio.getAudios().get(0).getAudio().getDuration());

        return view;
    }



    class ViewHolder {
        @BindView(R.id.index)
        TextView index;
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
