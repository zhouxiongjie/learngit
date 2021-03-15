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
import com.shuangling.software.entity.AwardGiftInfo;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class LiveAwardDetailAdapter extends RecyclerView.Adapter {


    private LayoutInflater inflater;
    private Context mContext;

    private List<AwardGiftInfo> mAwardGiftInfos;
    private OnItemClick mOnItemClick;

    public interface OnItemClick {
        void ItemClick(AwardGiftInfo AwardGiftInfo);
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        mOnItemClick = onItemClick;
    }


    public LiveAwardDetailAdapter(Context context, List<AwardGiftInfo> awardGiftInfos) {

        mContext = context;
        mAwardGiftInfos = awardGiftInfos;
        inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        return new ItemViewHolder(inflater.inflate(R.layout.live_award_detail_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ItemViewHolder vh = (ItemViewHolder) holder;
        AwardGiftInfo awardGiftInfo = mAwardGiftInfos.get(position);
        if (awardGiftInfo.getProps()!=null&&!TextUtils.isEmpty(awardGiftInfo.getProps().getIcon_url())) {
            int width = CommonUtils.dip2px(40);
            int height = width;
            Uri uri = Uri.parse(awardGiftInfo.getProps().getIcon_url());
            ImageLoader.showThumb(uri, vh.logo, width, height);
        } else {
            ImageLoader.showThumb(vh.logo, R.drawable.ic_user3);
        }

        vh.money.setText(String.format("%.2f", (float) awardGiftInfo.getPrice()/ 100) + "元");

        vh.number.setText(awardGiftInfo.getAmount()+"个");


    }

    @Override
    public int getItemCount() {
        if (mAwardGiftInfos == null) {
            return 0;
        } else {
            return mAwardGiftInfos.size();
        }
    }


//    @Override
//    public int getItemViewType(int position) {
//        return TYPE_ITEM;
//    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.order)
        TextView order;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.number)
        TextView number;
        @BindView(R.id.money)
        TextView money;
        @BindView(R.id.root)
        RelativeLayout root;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }



}
