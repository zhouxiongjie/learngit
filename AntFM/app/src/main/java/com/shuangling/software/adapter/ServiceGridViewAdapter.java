package com.shuangling.software.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.customview.MyGridView;
import com.shuangling.software.entity.ServerCategory.Service;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ServiceGridViewAdapter extends BaseAdapter {

    private List<Service> mServices;
    private Context mContext;

    public ServiceGridViewAdapter(Context context, List<Service> services) {
        super();
        this.mContext = context;
        this.mServices = services;
    }


    @Override
    public int getCount() {
        return mServices.size();
    }


    @Override
    public Service getItem(int position) {
        return mServices.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.server_item, parent, false);
        }
        ViewHolder vh=new ViewHolder(convertView);
        Service service = getItem(position);
        vh.name.setText(service.getTitle());
        if (!TextUtils.isEmpty(service.getCover())) {
            Uri uri = Uri.parse(service.getCover());
            int width = CommonUtils.dip2px(35);
            int height = width;
            ImageLoader.showThumb(uri, vh.logo, width, height);
        }
        return convertView;
    }



    public class ViewHolder {
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.name)
        TextView name;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
