package com.shuangling.software.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.AnchorDetailActivity;
import com.shuangling.software.entity.Anchor;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.List;

public class AnchorGridViewAdapter extends BaseAdapter implements View.OnClickListener {

	private List<Anchor> mAnchors;
	private Context mContext;

	
	public AnchorGridViewAdapter(Context context, List<Anchor> anchors) {
		super();
		// TODO Auto-generated constructor stub
		this.mAnchors=anchors;
		this.mContext=context;
	}

	

	public void setData(List<Anchor> anchors){
		this.mAnchors=anchors;
	}



	@Override
	public int getCount() {
		if(mAnchors.size()>4){
			return 4;
		}else{
			return mAnchors.size();
		}

	}



	@Override
	public Anchor getItem(int position) {
		return mAnchors.get(position);
	}



	@Override
	public long getItemId(int position) {
		return position;
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if(convertView!=null){
			view=convertView;
		}else{
			view=LayoutInflater.from(mContext).inflate(R.layout.anchor_gridview_item, parent,false);
		}
		ViewHolder vh=new ViewHolder(view);
		Anchor anchor =getItem(position);

		if(!TextUtils.isEmpty(anchor.getLogo())){
			Uri uri = Uri.parse(anchor.getLogo());
			int width=CommonUtils.dip2px(65);
			int height=width;
			ImageLoader.showThumb(uri,vh.anchor,width,height);

		}

		vh.anchorName.setText(anchor.getName());

		view.setTag(anchor);
		view.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {

		Intent it=new Intent(mContext,AnchorDetailActivity.class);
		it.putExtra("anchorId",((Anchor)v.getTag()).getId());
		mContext.startActivity(it);

	}

	private class ViewHolder
	{
		SimpleDraweeView anchor;
		TextView anchorName;


		ViewHolder(View view){
			anchor=view.findViewById(R.id.anchor);
			anchorName=view.findViewById(R.id.anchorName);
		}
	}


	

	

}
