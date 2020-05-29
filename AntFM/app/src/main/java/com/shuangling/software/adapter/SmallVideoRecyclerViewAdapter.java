package com.shuangling.software.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by 你是我的 on 2019/1/7.
 */
public class SmallVideoRecyclerViewAdapter extends RecyclerView.Adapter<SmallVideoRecyclerViewAdapter.ViewHolder> {
    Context context;
    List<ColumnContent> smallVideos = new ArrayList<>();
    LayoutInflater layoutInflater;
    OnItemClickListener onItemClickListener;


    public SmallVideoRecyclerViewAdapter(Context context, List<ColumnContent> smallVideos) {
        this.context = context;
        this.smallVideos = smallVideos;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.small_video_recycler_view_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        ColumnContent smallVideo = smallVideos.get(i);
        viewHolder.smallVideoTitle.setText(smallVideo.getTitle());

        if (!TextUtils.isEmpty(smallVideo.getCover())) {
            Uri uri = Uri.parse(smallVideo.getCover());
            int width = CommonUtils.dip2px(100);
            int height = width;
            ImageLoader.showThumb(uri, viewHolder.smallVideoPic, width, height);
        }



        if (smallVideo.getAuthor_info() != null && smallVideo.getAuthor_info().getMerchant() != null
                && !TextUtils.isEmpty(smallVideo.getAuthor_info().getMerchant().getName())) {
            viewHolder.authorName.setText(smallVideo.getAuthor_info().getMerchant().getName());
        }

        if (smallVideo.getAuthor_info() != null && smallVideo.getAuthor_info().getMerchant() != null
                && !TextUtils.isEmpty(smallVideo.getAuthor_info().getMerchant().getLogo())) {
            Uri uri = Uri.parse(smallVideo.getAuthor_info().getMerchant().getLogo());
            int width = CommonUtils.dip2px(16);
            int height = width;
            ImageLoader.showThumb(uri, viewHolder.authorIcon, width, height);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return smallVideos.size();
    }


    public void setData(List<ColumnContent> columnContent) {
        this.smallVideos = columnContent;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView smallVideoPic;
        SimpleDraweeView authorIcon;

        TextView smallVideoTitle;
        TextView authorName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            smallVideoPic = itemView.findViewById(R.id.iv_small_video_face_pic);
            smallVideoTitle = itemView.findViewById(R.id.video_title);
            authorIcon = itemView.findViewById(R.id.iv_author_logo);
            authorName = itemView.findViewById(R.id.tv_author_name);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
