package com.shuangling.software.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.shuangling.software.R;
import com.shuangling.software.service.AudioPlayerService;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectionsGridViewAdapter extends BaseAdapter {

    public static int PAGE_SIZE = 20;

    private Context mContext;
    private int mSelections;
    private int mOrder;
    private List<Integer> mSelect;

    public SelectionsGridViewAdapter(Context context) {
        super();
        this.mContext = context;

    }

    public SelectionsGridViewAdapter(Context context, int selections,int order,List<Integer> select) {
        super();
        this.mContext = context;
        this.mSelections = selections;
        this.mOrder=order;
        mSelect=select;
    }



//    public void updateView(int selections) {
//        this.mSelections = selections;
//        notifyDataSetChanged();
//
//    }

    public void updateView(int order) {
        this.mOrder = order;
        notifyDataSetChanged();

    }




    @Override
    public int getCount() {
        return (this.mSelections + PAGE_SIZE - 1) / PAGE_SIZE;
    }


    @Override
    public List<Integer> getItem(int position) {

        List<Integer> ret = new ArrayList<Integer>();
        for (int i = 0; i < PAGE_SIZE ; i++) {
            if(mOrder==AudioPlayerService.POSITIVE){
                if (PAGE_SIZE * position + i + 1 <= mSelections) {
                    ret.add(PAGE_SIZE * position + i +1 );
                }
            }else{

                if(mSelections-position*PAGE_SIZE-i > 0){
                    ret.add(mSelections-position*PAGE_SIZE-i);
                }

            }

        }
        return ret;
    }


    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.selection_item, parent, false);
        }
        ViewHolder vh = new ViewHolder(convertView);
        List<Integer> list=getItem(position);
        vh.selection.setText(list.get(0).toString()+"-"+list.get(list.size()-1).toString());

        if (vh.selection.getText().equals(mSelect.get(0).toString()+"-"+mSelect.get(list.size()-1).toString())||
                vh.selection.getText().equals(mSelect.get(list.size()-1).toString()+"-"+mSelect.get(0).toString())  ) {
            vh.selection.setSelected(true);
        } else {
            vh.selection.setSelected(false);
        }
        return convertView;
    }





    class ViewHolder {
        @BindView(R.id.selection)
        TextView selection;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
