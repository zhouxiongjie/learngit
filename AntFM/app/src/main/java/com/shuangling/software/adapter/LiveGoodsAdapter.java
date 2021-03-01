package com.shuangling.software.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.entity.LiveGoodsInfo;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class LiveGoodsAdapter extends RecyclerView.Adapter implements View.OnClickListener {


    private Context mContext;
    private LiveGoodsInfo mLiveGoodsInfo;
    private LayoutInflater inflater;


    private OnItemClickListener onItemClickListener;


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {

        void onItemClick(LiveGoodsInfo.DataBean seletedGoods);

        void onItemDeleteClick(LiveGoodsInfo.DataBean seletedGoods);
    }


    public LiveGoodsAdapter(Context context, LiveGoodsInfo liveGoodsInfo) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
        mLiveGoodsInfo = liveGoodsInfo;
    }


//    public  void appendDataList(List<LLSelectedGoods.DataBean> dataList) {
//        mSelectedGoods.addAll(dataList);
//        this.notifyDataSetChanged();
//    }
//
//
//    public  void deleteData(LLSelectedGoods.DataBean dataBean) {
//        mSelectedGoods.remove(dataBean);
//        this.notifyDataSetChanged();
//    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GoodsViewHolder(inflater.inflate(R.layout.item_live_goods, parent, false));
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        GoodsViewHolder viewHolder = (GoodsViewHolder) holder;

        final LiveGoodsInfo.DataBean goodsBean = mLiveGoodsInfo.getData().get(position);
        String pictureUrl = goodsBean.getMerchant_goods() != null ? goodsBean.getMerchant_goods().getPict_url() : "";
        if (!TextUtils.isEmpty(pictureUrl)) {
            Uri uri = Uri.parse(pictureUrl);
            ImageLoader.showThumb(uri, viewHolder.picture, CommonUtils.dip2px(100), CommonUtils.dip2px(100));
        }

        //点击图片
        viewHolder.see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(goodsBean);
                }
            }
        });

        if (position < 9) {
            viewHolder.sort.setText("0" + (position + 1));
        } else {
            viewHolder.sort.setText("" + (position + 1));
        }

        if (goodsBean.getIn_explanation() == 1) {
            viewHolder.explain.setVisibility(View.VISIBLE);
        }else{
            viewHolder.explain.setVisibility(View.GONE);
        }

        if (goodsBean.getMerchant_goods() != null) {
            viewHolder.goodsName.setText(goodsBean.getMerchant_goods().getGoods_name());
            viewHolder.price.setText("￥" + goodsBean.getMerchant_goods().getAfter_coupon_price());
            switch (goodsBean.getMerchant_goods().getSource()) {
                case 0: {
                    viewHolder.source.setText("自定义");
                }
                break;
                case 1:
                    viewHolder.source.setText("来自淘宝");
                    break;
                case 2:
                    viewHolder.source.setText("来自京东");
                    break;
                case 3:
                    viewHolder.source.setText("来自拼多多");
                    break;
                default:
                    break;

            }
        }


    }


    @Override
    public int getItemCount() {
        if (mLiveGoodsInfo == null || mLiveGoodsInfo.getData() == null) {
            return 0;
        } else {
            return mLiveGoodsInfo.getData().size();
        }

    }


    @Override
    public void onClick(View v) {

    }


    public class GoodsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.picture)
        SimpleDraweeView picture;
        @BindView(R.id.sort)
        TextView sort;
        @BindView(R.id.goods_name)
        TextView goodsName;
        @BindView(R.id.source)
        TextView source;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.see)
        TextView see;
        @BindView(R.id.explain)
        TextView explain;

        public GoodsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
