package com.shuangling.software.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.entity.RadioSet;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RadioListAdapter extends BaseExpandableListAdapter implements View.OnClickListener {

    private List<RadioSet> mRadioGroups;
    private Context mContext;



    private boolean showLogo=false;
    public void setShowLogo(boolean showLogo) {
        this.showLogo = showLogo;

    }

    public RadioListAdapter(Context context, List<RadioSet> list) {
        super();
        this.mRadioGroups = list;
        this.mContext = context;
    }


    public void updateListView(List<RadioSet> list) {
        this.mRadioGroups = list;
        notifyDataSetChanged();
    }


    @Override
    public int getGroupCount() {
        if(mRadioGroups!=null){
            return mRadioGroups.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(groupPosition==mRadioGroups.size()-1){
            //最后一个分类增加空白条目
            return mRadioGroups.get(groupPosition).getList().size()+1;
        }else{
            return mRadioGroups.get(groupPosition).getList().size();
        }

    }

    @Override
    public RadioSet getGroup(int groupPosition) {
        return mRadioGroups.get(groupPosition);
    }

    @Override
    public RadioSet.Radio getChild(int groupPosition, int childPosition) {
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

        if(groupPosition==0&&showLogo){
            vh.logo.setVisibility(View.VISIBLE);
            String logo="http://fcgtvb-cdn.fcgtvb.com/cms/customer/static/dist/img/radio-cover-bg@2x.png?x-oss-process=image/resize,w_500,h_200";
            Uri uri = Uri.parse(logo);
            int width = 500;
            int height = 200;
            ImageLoader.showThumb(uri, vh.logo, width, height);

        }else{
            vh.logo.setVisibility(View.GONE);
        }

        vh.label.setText(getGroup(groupPosition).getLabel());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if(groupPosition==mRadioGroups.size()-1&&isLastChild){
            //
            convertView=new View(mContext);
            convertView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,parent.getHeight()-CommonUtils.dip2px(100)));
            convertView.setVisibility(View.INVISIBLE);
        }else{
            if (convertView == null||!(convertView instanceof RelativeLayout)) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.radio_child_item, parent, false);
            }
            ChildViewHolder childViewHolder=new ChildViewHolder(convertView);
            RadioSet.Radio radio=getChild(groupPosition,childPosition);
            if (!TextUtils.isEmpty(radio.getLogo())) {
                Uri uri = Uri.parse(radio.getLogo());
                int width = CommonUtils.dip2px(50);
                int height = width;
                ImageLoader.showThumb(uri, childViewHolder.logo, width, height);
            }
            if(radio.getSchedule()!=null&&radio.getSchedule().getProgram()!=null){
                childViewHolder.program.setText(radio.getSchedule().getProgram().getName());
                childViewHolder.living.setVisibility(View.VISIBLE);
            }else{
                childViewHolder.program.setText("");
                childViewHolder.living.setVisibility(View.GONE);
            }
            childViewHolder.title.setText(radio.getName());

        }


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
        LinearLayout root;
        @BindView(R.id.logo)
        SimpleDraweeView logo;

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
        @BindView(R.id.living)
        TextView living;

        ChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
