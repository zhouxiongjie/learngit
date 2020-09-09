package com.shuangling.software.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.entity.ImgTextInfo;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ImgTextGridViewAdapter extends BaseAdapter {

    private List<String> mUrls;
    private Context mContext;

    public ImgTextGridViewAdapter(Context context, List<String> urls) {
        super();
        this.mContext = context;
        this.mUrls = urls;
    }


    @Override
    public int getCount() {
        return mUrls.size();
    }


    @Override
    public String getItem(int position) {
        return mUrls.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.img_item, parent, false);
        }
        ViewHolder vh = new ViewHolder(convertView);
        String url = getItem(position);

        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            int width = CommonUtils.getScreenWidth()-CommonUtils.dip2px(100);
            int height = width;
            ImageLoader.showThumb(uri, vh.logo, width, height);
        }
        return convertView;
    }


    static

    public class ViewHolder {
        @BindView(R.id.logo)
        SimpleDraweeView logo;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
