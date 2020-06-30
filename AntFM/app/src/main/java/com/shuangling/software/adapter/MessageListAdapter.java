package com.shuangling.software.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.entity.MessageInfo;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.TimeUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class MessageListAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    //"type": 4,//1 音频 2 专辑 3 文章 4 视频 5专题

    public static final int TYPE_ALBUM = 1;             //专辑


    private Context mContext;
    private List<MessageInfo> mMessages;
    private LayoutInflater inflater;


    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {

        void onItemClick(int pos);
    }


    public MessageListAdapter(Context context, List<MessageInfo> messages) {
        this.mContext = context;
        this.mMessages = messages;
        inflater = LayoutInflater.from(mContext);

    }

    public void updateView(List<MessageInfo> messages) {
        this.mMessages = messages;
        notifyDataSetChanged();

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new MessageViewHolder(inflater.inflate(R.layout.message_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MessageInfo message = mMessages.get(position);

        final MessageViewHolder albumViewHolder = (MessageViewHolder) holder;


        albumViewHolder.title.setText("【"+message.getMessage().getContent().getTitle()+"】");
        albumViewHolder.time.setText(TimeUtil.formatDateTime(message.getCreated_at()));
        albumViewHolder.description.setText(message.getMessage().getContent().getDescription());
        albumViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(position);
                }
            }
        });


    }


    @Override
    public int getItemCount() {

        if (mMessages != null) {
            return mMessages.size();
        } else {
            return 0;
        }

    }


    @Override
    public int getItemViewType(int position) {
        return TYPE_ALBUM;
    }

    @Override
    public void onClick(View v) {

    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.root)
        LinearLayout root;
        @BindView(R.id.isRead)
        SimpleDraweeView isRead;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.description)
        TextView description;

        public MessageViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }



}
