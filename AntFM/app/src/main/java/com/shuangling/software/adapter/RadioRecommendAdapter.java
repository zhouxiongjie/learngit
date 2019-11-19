package com.shuangling.software.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.entity.RadioRecommend;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RadioRecommendAdapter extends BaseAdapter {
    private List<RadioRecommend> list;
    private RadioRecommend selected;
    private Context mContext;

    public RadioRecommend getSelected() {
        return selected;
    }

    public void setSelected(RadioRecommend selected) {
        this.selected = selected;
        notifyDataSetChanged();
    }


    public RadioRecommendAdapter(Context mContext, List<RadioRecommend> list) {
        this.mContext = mContext;
        this.list = list;
    }


    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<RadioRecommend> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public RadioRecommend getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.radio_recommend_item, viewGroup, false);
        }
        ViewHolder viewHolder = new ViewHolder(view);
        RadioRecommend radioRecommend=getItem(position);
        if (!TextUtils.isEmpty(radioRecommend.getLogo())) {
            Uri uri = Uri.parse(radioRecommend.getLogo());
            int width = CommonUtils.dip2px(65);
            int height = width;
            ImageLoader.showThumb(uri, viewHolder.logo, width, height);
        }

        if(radioRecommend.getSchedule()!=null&&radioRecommend.getSchedule().getProgram()!=null){
            viewHolder.program.setVisibility(View.VISIBLE);
        }else{
            viewHolder.program.setVisibility(View.GONE);
        }
        viewHolder.title.setText(radioRecommend.getName());
        if(radioRecommend.getMerchant_id()!=null){
            viewHolder.organization.setText(radioRecommend.getMerchant_id().getName());
        }
        return view;

    }



    class ViewHolder {
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.program)
        TextView program;
        @BindView(R.id.organization)
        TextView organization;
        @BindView(R.id.play)
        FontIconView play;
        @BindView(R.id.root)
        RelativeLayout root;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
