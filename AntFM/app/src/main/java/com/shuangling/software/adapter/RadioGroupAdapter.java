package com.shuangling.software.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shuangling.software.R;
import com.shuangling.software.entity.RadioGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RadioGroupAdapter extends BaseAdapter {
    private List<RadioGroup> list;
    private RadioGroup selected;
    private Context mContext;

    public RadioGroup getSelected() {
        return selected;
    }

    public void setSelected(RadioGroup selected) {
        this.selected = selected;
        notifyDataSetChanged();
    }


    public RadioGroupAdapter(Context mContext, List<RadioGroup> list) {
        this.mContext = mContext;
        this.list = list;
    }



    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<RadioGroup> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public RadioGroup getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        final RadioGroup listBean = list.get(position);
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.radio_group_item_one, null);

        }
        viewHolder = new ViewHolder(view);
        viewHolder.label.setText(listBean.getLabel());
        if(listBean==selected){
            viewHolder.label.setSelected(true);
        }else{
            viewHolder.label.setSelected(false);
        }

        return view;

    }




    class ViewHolder {
        @BindView(R.id.label)
        TextView label;
        @BindView(R.id.root)
        RelativeLayout root;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
