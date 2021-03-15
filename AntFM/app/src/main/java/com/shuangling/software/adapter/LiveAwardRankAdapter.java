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
import com.shuangling.software.entity.AwardInfo;
import com.shuangling.software.entity.ChatMessage;
import com.shuangling.software.entity.RewardsInfo;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class LiveAwardRankAdapter extends RecyclerView.Adapter {


    final int TYPE_HEAD = 0;
    final int TYPE_ITEM = 1;

    private LayoutInflater inflater;
    private Context mContext;

    private List<AwardInfo> mAwardInfos;
    private OnItemClick mOnItemClick;

    public interface OnItemClick {
        void ItemClick(AwardInfo awardInfo);
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        mOnItemClick=onItemClick;
    }


    public LiveAwardRankAdapter(Context context, List<AwardInfo> awardInfos) {

        mContext = context;
        mAwardInfos = awardInfos;
        inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        return new ItemViewHolder(inflater.inflate(R.layout.live_award_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ItemViewHolder vh = (ItemViewHolder) holder;
        AwardInfo awardInfo =mAwardInfos.get(position);
        if (!TextUtils.isEmpty(awardInfo.getAvatar())) {
            int width = CommonUtils.dip2px(40);
            int height = width;
            Uri uri = Uri.parse(awardInfo.getAvatar());
            ImageLoader.showThumb(uri, vh.logo, width, height);
        } else {
            ImageLoader.showThumb(vh.logo, R.drawable.ic_user3);
        }
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
        vh.money.setText(awardInfo.getDevote_count() + "积分");

        vh.name.setText(awardInfo.getNickname());
        vh.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClick!=null){
                    mOnItemClick.ItemClick(awardInfo);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(mAwardInfos==null){
            return 0;
        }else {
            return mAwardInfos.size();
        }
    }


//    @Override
//    public int getItemViewType(int position) {
//        return TYPE_ITEM;
//    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.money)
        TextView money;
        @BindView(R.id.root)
        RelativeLayout root;
        @BindView(R.id.order)
        TextView order;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }





}
