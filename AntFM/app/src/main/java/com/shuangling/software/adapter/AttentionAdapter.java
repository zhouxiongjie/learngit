package com.shuangling.software.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.entity.Attention;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class AttentionAdapter extends RecyclerView.Adapter implements View.OnClickListener {



    private Context mContext;
    private List<Attention> mAttentions;
    private LayoutInflater inflater;


    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {

        void onItemClick(int pos);
    }


    public AttentionAdapter(Context context, List<Attention> attentions) {
        this.mContext = context;
        this.mAttentions = attentions;
        inflater = LayoutInflater.from(mContext);

    }

    public void updateView(List<Attention> attentions) {
        this.mAttentions = attentions;
        notifyDataSetChanged();

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        return new AttentionViewHolder(inflater.inflate(R.layout.attention_item, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Attention attention = mAttentions.get(position);

        final AttentionViewHolder attentionViewHolder = (AttentionViewHolder) holder;

        if (!TextUtils.isEmpty(attention.getLogo())) {
            Uri uri = Uri.parse(attention.getLogo());
            int width = CommonUtils.dip2px(65);
            int height = width;
            ImageLoader.showThumb(uri, attentionViewHolder.logo, width, height);
        }else {
            ImageLoader.showThumb(attentionViewHolder.logo, R.drawable.head_placeholder);
        }
        attentionViewHolder.title.setText(attention.getName());

        attentionViewHolder.root.setOnClickListener(new View.OnClickListener() {
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

        if (mAttentions != null) {
            return mAttentions.size();
        } else {
            return 0;
        }

    }



    @Override
    public void onClick(View v) {

    }


    public class AttentionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.root)
        RelativeLayout root;

        public AttentionViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    
}
