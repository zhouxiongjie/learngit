package com.shuangling.software.adapter;

import android.content.Context;
import android.graphics.Color;
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
import com.shuangling.software.entity.InviteInfo;
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
public class LiveInviteListAdapter extends RecyclerView.Adapter {



    private LayoutInflater inflater;
    private Context mContext;

    List<InviteInfo> mInviteInfos = new ArrayList<>();

    public interface OnItemReply {
        void replyItem(ChatMessage message);
    }

    public void setOnItemReply(LiveChatListAdapter.OnItemReply onItemReply) {

    }


    public LiveInviteListAdapter(Context context, List<InviteInfo> viewers) {

        mContext = context;
        mInviteInfos=viewers;
        inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        return new InviteInfoViewHolder(inflater.inflate(R.layout.live_hot_item, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        InviteInfoViewHolder vh=(InviteInfoViewHolder)viewHolder;
        InviteInfo inviteInfo=mInviteInfos.get(position);
        vh.order.setText(""+(position+1));
        if(position==0){
            vh.order.setTextColor(Color.parseColor("#FF2935"));
        }else if(position==1){
            vh.order.setTextColor(Color.parseColor("#FF6421"));
        }else if(position==2){
            vh.order.setTextColor(Color.parseColor("#F3AF00"));
        }else {
            vh.order.setTextColor(Color.parseColor("#B0B0B4"));
        }
        vh.hotIndex.setText(""+inviteInfo.getNum()+"人");
        vh.name.setText(inviteInfo.getInviter()!=null?inviteInfo.getInviter().getNickname():"");

        if (inviteInfo.getInviter()!=null&&!TextUtils.isEmpty(inviteInfo.getInviter().getAvatar())) {
            Uri uri = Uri.parse(inviteInfo.getInviter().getAvatar());
            int width = CommonUtils.dip2px(40);
            int height = width;
            ImageLoader.showThumb(uri, vh.logo, width, height);
        } else {
            ImageLoader.showThumb(vh.logo, R.drawable.ic_user3);
        }

    }

    @Override
    public int getItemCount() {
        if (mInviteInfos==null||mInviteInfos.size() == 0) {
            return 0;
        } else {
            return mInviteInfos.size();
        }
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    static class InviteInfoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.order)
        TextView order;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.root)
        RelativeLayout root;
        @BindView(R.id.hotIndex)
        TextView hotIndex;

        InviteInfoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
