package com.shuangling.software.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.entity.LiveRoomInfo01;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GiftGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<LiveRoomInfo01.PropsBean> mGifts;

    public GiftGridAdapter(Context context, List<LiveRoomInfo01.PropsBean> gifts) {
        this.mContext = context;
        this.mGifts = gifts;
    }

    @Override
    public int getCount() {
        return null == mGifts ? 0 : mGifts.size();
    }

    @Override
    public LiveRoomInfo01.PropsBean getItem(int position) {
        return mGifts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            convertView = View.inflate(mContext, R.layout.gift_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LiveRoomInfo01.PropsBean gift=getItem(position);


        if (!TextUtils.isEmpty(gift.getIcon_url())) {
            Uri uri = Uri.parse(gift.getIcon_url());
            ImageLoader.showThumb(uri, holder.logo, CommonUtils.dip2px(50), CommonUtils.dip2px(50));
        }
        holder.giftName.setText(gift.getName());
        holder.giftPrice.setText(String.format("%.2f", (float) gift.getPrice() / 100)+"元");

        return convertView;
    }

    public class ViewHolder {
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.giftName)
        TextView giftName;
        @BindView(R.id.giftPrice)
        TextView giftPrice;
        @BindView(R.id.bg)
        LinearLayout bg;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}