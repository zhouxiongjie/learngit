package com.shuangling.software.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.shuangling.software.activity.MyWalletsActivity;
import com.shuangling.software.activity.NewLoginActivity;
import com.shuangling.software.entity.ChatMessage;
import com.shuangling.software.entity.RedPacketDetailInfo;
import com.shuangling.software.entity.RewardsInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.entity.Viewer;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class LiveRewardAdapter extends RecyclerView.Adapter {


    final int TYPE_HEAD = 0;
    final int TYPE_ITEM = 1;

    private LayoutInflater inflater;
    private Context mContext;

    private RewardsInfo mRewardsInfo;

    public interface OnItemReply {
        void replyItem(ChatMessage message);
    }

    public void setOnItemReply(LiveChatListAdapter.OnItemReply onItemReply) {

    }


    public LiveRewardAdapter(Context context, RewardsInfo rewardsInfo) {

        mContext = context;
        mRewardsInfo = rewardsInfo;
        inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if (viewType == TYPE_HEAD) {
            return new HeadViewHolder(inflater.inflate(R.layout.live_reward_head_item, viewGroup, false));
        } else {
            return new ItemViewHolder(inflater.inflate(R.layout.live_reward_item, viewGroup, false));
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int itemViewType = getItemViewType(position);
        if (itemViewType == TYPE_HEAD) {
            HeadViewHolder vh = (HeadViewHolder) holder;
            vh.keyword.setText(mRewardsInfo.getKeyword());
        } else {
            ItemViewHolder vh = (ItemViewHolder) holder;
            RewardsInfo.AwardsBean awardsBean =mRewardsInfo.getAwards().get(position-1);
            if (!TextUtils.isEmpty(awardsBean.getAvatar())) {
                int width = CommonUtils.dip2px(40);
                int height = width;
                Uri uri = Uri.parse(awardsBean.getAvatar());
                ImageLoader.showThumb(uri, vh.logo, width, height);
            } else {
                ImageLoader.showThumb(vh.logo, R.drawable.ic_user3);
            }
            vh.money.setText(String.format("%.2f", (float) awardsBean.getMoney() / 100) + "元");

            vh.name.setText(awardsBean.getNickname());
        }

    }

    @Override
    public int getItemCount() {
        if (mRewardsInfo.getAwards()==null||mRewardsInfo.getAwards().size() == 0) {
            return 0;
        } else {
            return mRewardsInfo.getAwards().size() + 1;
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        } else {
            return TYPE_ITEM;
        }
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.money)
        TextView money;
        @BindView(R.id.root)
        RelativeLayout root;


        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    static class HeadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.keyword)
        TextView keyword;
        @BindView(R.id.root)
        RelativeLayout root;

        HeadViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
