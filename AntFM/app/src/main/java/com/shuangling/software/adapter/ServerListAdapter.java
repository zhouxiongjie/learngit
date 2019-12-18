package com.shuangling.software.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.shuangling.software.R;
import com.shuangling.software.activity.AnchorDetailActivity;
import com.shuangling.software.activity.WebViewBackActivity;
import com.shuangling.software.customview.MyGridView;
import com.shuangling.software.entity.ServerCategory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ServerListAdapter extends BaseExpandableListAdapter {

    private List<ServerCategory> mServerCategory;
    private Context mContext;

    public ServerListAdapter(Context context, List<ServerCategory> serverCategorys) {
        super();
        this.mContext = context;
        this.mServerCategory = serverCategorys;
    }


    public void updateView(List<ServerCategory> serverCategorys) {
        this.mServerCategory = serverCategorys;
    }


    @Override
    public int getGroupCount() {
        return mServerCategory.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public ServerCategory getGroup(int groupPosition) {
        return mServerCategory.get(groupPosition);
    }

    @Override
    public ServerCategory getChild(int groupPosition, int childPosition) {
        return mServerCategory.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return super.getCombinedChildId(groupPosition, childPosition);

    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.server_group_item, parent, false);
        }
        GroupViewHolder vh = new GroupViewHolder(convertView);
        vh.category.setText(getGroup(groupPosition).getTitle());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.server_child_item, parent, false);
        }
        ChildViewHolder vh = new ChildViewHolder(convertView);
        final ServerCategory serverCategory = getChild(groupPosition, childPosition);
        final ServiceGridViewAdapter adapter = new ServiceGridViewAdapter(mContext, serverCategory.getService());
        vh.gridView.setAdapter(adapter);
        vh.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ServerCategory.Service service=serverCategory.getService().get(position);
                Intent it = new Intent(mContext, WebViewBackActivity.class);
                it.putExtra("url", service.getLink_url());
                mContext.startActivity(it);

            }
        });

        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

    public class GroupViewHolder {
        @BindView(R.id.category)
        TextView category;

        GroupViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public class ChildViewHolder {
        @BindView(R.id.gridView)
        MyGridView gridView;

        ChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }




}
