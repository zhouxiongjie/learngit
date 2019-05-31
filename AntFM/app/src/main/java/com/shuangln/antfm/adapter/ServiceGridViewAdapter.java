package com.shuangln.antfm.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangln.antfm.R;
import com.shuangln.antfm.entity.Anchor;
import com.shuangln.antfm.entity.LocalService;
import com.shuangln.antfm.utils.CommonUtils;
import com.shuangln.antfm.utils.ImageLoader;

import java.util.List;

public class ServiceGridViewAdapter extends BaseAdapter implements View.OnClickListener {

	private LocalService mService;
	private Context mContext;


	public ServiceGridViewAdapter(Context context, LocalService service) {
		super();
		// TODO Auto-generated constructor stub
		this.mService=service;
		this.mContext=context;
	}

	

	public void setData(LocalService service){
		this.mService=service;
	}



	@Override
	public int getCount() {
		if(mService.getData().size()>4){
			return 4;
		}else{
			return mService.getData().size();
		}

	}



	@Override
	public LocalService.Service getItem(int position) {
		return mService.getData().get(position);
	}



	@Override
	public long getItemId(int position) {
		return position;
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder vh;
		if(convertView!=null){
			view=convertView;
			vh=(ViewHolder)view.getTag();
		}else{
			vh=new ViewHolder();
			view=LayoutInflater.from(mContext).inflate(R.layout.service_gridview_item, parent,false);
			vh.service=view.findViewById(R.id.service);
			vh.serviceName=view.findViewById(R.id.serviceName);

		}
		LocalService.Service service =getItem(position);

		if(!TextUtils.isEmpty(service.getCover())){
			Uri uri = Uri.parse(service.getCover());
			int width=CommonUtils.dip2px(65);
			int height=width;
			ImageLoader.showThumb(uri,vh.service,width,height);

		}

		vh.serviceName.setText(service.getTitle());

		view.setTag(vh);
		view.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {

	}

	private class ViewHolder
	{
		SimpleDraweeView service;
		TextView serviceName;
	}
	

	

}
