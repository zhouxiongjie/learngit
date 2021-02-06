package com.shuangling.software.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.entity.ChatMessage;
import com.shuangling.software.entity.Viewer;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class LiveViewerListAdapter extends RecyclerView.Adapter {



    private LayoutInflater inflater;
    private Context mContext;

    List<Viewer> mViewers = new ArrayList<>();

    public interface OnItemReply {
        void replyItem(ChatMessage message);
    }

    public void setOnItemReply(LiveChatListAdapter.OnItemReply onItemReply) {

    }


    public LiveViewerListAdapter(Context context, List<Viewer> viewers) {

        mContext = context;
        mViewers=viewers;
        inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        return new ViewerViewHolder(inflater.inflate(R.layout.live_viewer_item, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        ViewerViewHolder vh=(ViewerViewHolder)viewHolder;
        Viewer viewer=mViewers.get(position);
        vh.order.setText(""+(position+1));
        vh.name.setText(viewer.getNick_name());

        if (!TextUtils.isEmpty(viewer.getAvatar())) {
            Uri uri = Uri.parse(viewer.getAvatar());
            int width = CommonUtils.dip2px(40);
            int height = width;
            ImageLoader.showThumb(uri, vh.logo, width, height);
        } else {
            ImageLoader.showThumb(vh.logo, R.drawable.ic_user3);
        }

    }

    @Override
    public int getItemCount() {
        if (mViewers==null||mViewers.size() == 0) {
            return 0;
        } else {
            return mViewers.size();
        }
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    static class ViewerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.order)
        TextView order;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.root)
        RelativeLayout root;


        ViewerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
