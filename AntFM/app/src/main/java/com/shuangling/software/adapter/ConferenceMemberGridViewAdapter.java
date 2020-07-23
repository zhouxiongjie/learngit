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
import com.shuangling.software.activity.ui.UserTrackView;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.List;

public class ConferenceMemberGridViewAdapter extends BaseAdapter {

	private List<UserTrackView> mUserTrackViews;
	private Context mContext;


	public ConferenceMemberGridViewAdapter(Context context, List<UserTrackView> userTrackViews) {
		super();
		this.mUserTrackViews=userTrackViews;
		this.mContext=context;
	}

	

	public void setData(List<UserTrackView> userTrackViews){
		this.mUserTrackViews=userTrackViews;
	}



	@Override
	public int getCount() {
		if(mUserTrackViews==null){
			return 0;
		}else {
			return mUserTrackViews.size();
		}


	}



	@Override
	public UserTrackView getItem(int position) {
		return mUserTrackViews.get(position);
	}



	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		return mUserTrackViews.get(position);

	}


}
