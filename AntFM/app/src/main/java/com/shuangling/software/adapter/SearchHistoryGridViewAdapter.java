package com.shuangling.software.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shuangling.software.R;
import com.shuangling.software.customview.FontIconView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchHistoryGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mHistory;
    private boolean mIsEditor=false;

    public boolean isEditor() {
        return mIsEditor;
    }

    public void setIsEditor(boolean isEditor) {
        this.mIsEditor = isEditor;
        notifyDataSetChanged();
    }

    public boolean isExpand() {
        return mExpand;
    }

    public void setExpand(boolean expand) {
        this.mExpand = expand;
        notifyDataSetChanged();
    }

    private boolean mExpand=false;

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void onDelete(String history);
        void onItemClick(String history);
    }

    public SearchHistoryGridViewAdapter(Context context) {
        super();
        this.mContext = context;
    }

    public SearchHistoryGridViewAdapter(Context context, List<String> history) {
        super();
        this.mContext = context;
        mHistory = history;
    }

    public void updateView(List<String> history) {
        mHistory = history;
        notifyDataSetChanged();

    }


    @Override
    public int getCount() {
        if (mHistory == null) {
            return 0;
        } else {
            if(mExpand||mIsEditor){
                return mHistory.size();
            }else{
               if(mHistory.size()<=10){
                   return mHistory.size();
               }else{
                   return 10;
               }
            }

        }

    }

    @Override
    public String getItem(int position) {
        return mHistory.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_history_item, parent, false);
        }
        ViewHolder vh = new ViewHolder(convertView);
        String keyword = getItem(position);
        vh.keyword.setText(keyword);
        if(mIsEditor){
            vh.delete.setVisibility(View.VISIBLE);
        }else{
            vh.delete.setVisibility(View.GONE);
        }
        if(position%2==0){
            vh.divide.setVisibility(View.VISIBLE);
        }else{
            vh.divide.setVisibility(View.GONE);
        }
        vh.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mIsEditor){
                    if(onItemClickListener!=null){
                        onItemClickListener.onDelete(keyword);
                    }
                }else{
                    if(onItemClickListener!=null){
                        onItemClickListener.onItemClick(keyword);
                    }
                }


            }
        });

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.keyword)
        TextView keyword;
        @BindView(R.id.delete)
        FontIconView delete;
        @BindView(R.id.divide)
        ImageView divide;
        @BindView(R.id.root)
        LinearLayout root;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
