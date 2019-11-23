package com.shuangling.software.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuangling.software.R;
import com.shuangling.software.entity.Column;
import com.shuangling.software.utils.CommonUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyColumnGridViewAdapter extends BaseAdapter {

    private List<Column> mColumns;
    private Column mCurrenColumn;
    private Context mContext;


    public void setIsEditor(boolean isEditor) {
        this.mIsEditor = isEditor;
        notifyDataSetChanged();
    }

    private boolean mIsEditor;


    public MyColumnGridViewAdapter(Context context, List<Column> columns,Column column) {
        super();
        this.mColumns = columns;
        this.mContext = context;
        this.mCurrenColumn=column;
    }


    public void setData(List<Column> columns) {
        this.mColumns = columns;
    }


    @Override
    public int getCount() {
        return mColumns.size();
    }


    @Override
    public Column getItem(int position) {
        return mColumns.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.column_gridview_item, parent, false);
        }
        ViewHolder vh = new ViewHolder(convertView);
        Column column = getItem(position);
        vh.columnName.setText(column.getName());
        if(column.getType()==1||column.getType()==-1){
            vh.columnName.setTextColor(Color.parseColor("#dcdcdc"));
        }else{
            vh.columnName.setTextColor(Color.parseColor("#222222"));
        }
        if(column.getId()==mCurrenColumn.getId()){
            vh.columnName.setTextColor(CommonUtils.getThemeColor(mContext));
        }

        if (mIsEditor&&column.getType()!=1&&column.getType()!=-1) {
            vh.delete.setVisibility(View.VISIBLE);
        }else{
            vh.delete.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }


    /**
     * 根据图片的名称获取对应的资源id
     *
     * @param resourceName
     * @return
     */
    public int getDrawResourceID(String resourceName) {
        Resources res = mContext.getResources();
        int picid = res.getIdentifier(resourceName, "drawable", mContext.getPackageName());
        return picid;
    }


    static class ViewHolder {
        @BindView(R.id.columnName)
        TextView columnName;
        @BindView(R.id.delete)
        ImageView delete;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
