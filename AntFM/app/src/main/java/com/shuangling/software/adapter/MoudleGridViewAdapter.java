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
import com.shuangling.software.entity.DecorModule.ContentsBean;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.List;

public class MoudleGridViewAdapter extends BaseAdapter implements View.OnClickListener {
    private List<ContentsBean> mMoudles;
    private Context mContext;

    public MoudleGridViewAdapter(Context context, List<ContentsBean> moudles) {
        super();
        this.mMoudles = moudles;
        this.mContext = context;
    }

    public void setData(List<ContentsBean> moudles) {
        this.mMoudles = moudles;
    }

    @Override
    public int getCount() {
//		if(mMoudles.size()>4){
//			return 4;
//		}else{
        return mMoudles.size();
//		}
    }

    @Override
    public ContentsBean getItem(int position) {
        return mMoudles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.anchor_gridview_item, parent, false);
        }
        ViewHolder vh = new ViewHolder(convertView);
        ContentsBean module = getItem(position);
        if (!TextUtils.isEmpty(module.getCover())) {
            Uri uri = Uri.parse(module.getCover());
            int width = CommonUtils.dip2px(45);
            int height = width;
            ImageLoader.showThumb(uri, vh.anchor, width, height);
        }
        vh.anchorName.setText(module.getTitle());
        convertView.setTag(module);
        //convertView.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {
//		Intent it=new Intent(mContext,AnchorDetailActivity.class);
//		it.putExtra("anchorId",((ContentsBean)v.getTag()).getId());
//		mContext.startActivity(it);
    }

    private class ViewHolder {
        SimpleDraweeView anchor;
        TextView anchorName;

        ViewHolder(View view) {
            anchor = view.findViewById(R.id.anchor);
            anchorName = view.findViewById(R.id.anchorName);
        }
    }
}
