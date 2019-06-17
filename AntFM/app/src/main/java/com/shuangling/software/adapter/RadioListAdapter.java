package com.shuangling.software.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.entity.RadioGroup;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RadioListAdapter extends BaseExpandableListAdapter implements View.OnClickListener {

    private List<RadioGroup> mRadioGroups;
    private Context mContext;


    public RadioListAdapter(Context context, List<RadioGroup> list) {
        super();
        this.mRadioGroups = list;
        this.mContext = context;
    }


    public void updateListView(List<RadioGroup> list) {
        this.mRadioGroups = list;
        notifyDataSetChanged();
    }


    @Override
    public int getGroupCount() {
        return mRadioGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mRadioGroups.get(groupPosition).getList().size();
    }

    @Override
    public RadioGroup getGroup(int groupPosition) {
        return mRadioGroups.get(groupPosition);
    }

    @Override
    public RadioGroup.Radio getChild(int groupPosition, int childPosition) {
        return mRadioGroups.get(groupPosition).getList().get(childPosition);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.radio_group_item_two, parent, false);
        }

        GroupViewHolder vh = new GroupViewHolder(convertView);

        vh.label.setText(getGroup(groupPosition).getLabel());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.radio_child_item, parent, false);
        }
        ChildViewHolder childViewHolder=new ChildViewHolder(convertView);
        RadioGroup.Radio radio=getChild(groupPosition,childPosition);
        if (!TextUtils.isEmpty(radio.getLogo())) {
            Uri uri = Uri.parse(radio.getLogo());
            int width = CommonUtils.dip2px(50);
            int height = width;
            ImageLoader.showThumb(uri, childViewHolder.logo, width, height);
        }
        if(radio.getSchedule()!=null)
        childViewHolder.program.setText(radio.getSchedule().getName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }



    @Override
    public void onClick(View v) {

    }


    static class GroupViewHolder {
        @BindView(R.id.label)
        TextView label;
        @BindView(R.id.root)
        RelativeLayout root;

        GroupViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ChildViewHolder {
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.program)
        TextView program;
        @BindView(R.id.root)
        RelativeLayout root;

        ChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
