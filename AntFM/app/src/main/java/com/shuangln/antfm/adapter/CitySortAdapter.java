package com.shuangln.antfm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.shuangln.antfm.R;
import com.shuangln.antfm.entity.City;
import java.util.List;


public class CitySortAdapter extends BaseAdapter implements SectionIndexer {
    private List<City> list = null;
    private Context mContext;

    public CitySortAdapter(Context mContext, List<City> list) {
        this.mContext = mContext;
        this.list = list;
    }




    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<City> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public City getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final City listBean = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.city_with_letters_item, null);
            viewHolder.letters=view.findViewById(R.id.letters);
            viewHolder.name=view.findViewById(R.id.name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //根据position获取分类的首字母的char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(section)){
            viewHolder.letters.setVisibility(View.VISIBLE);
            viewHolder.letters.setText(listBean.getInitials());
        }else{
            viewHolder.letters.setVisibility(View.GONE);
        }
        viewHolder.name.setText(listBean.getName());

        return view;

    }



    final static class ViewHolder {
        TextView letters;
        TextView name;

    }


    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getInitials().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getInitials();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }


    @Override
    public Object[] getSections() {
        return null;
    }
}
