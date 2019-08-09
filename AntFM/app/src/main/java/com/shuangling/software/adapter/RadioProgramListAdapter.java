package com.shuangling.software.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.shuangling.software.R;
import com.shuangling.software.entity.RadioDetail.ProgramListBean;
import com.shuangling.software.entity.RadioDetail.InPlayBean;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RadioProgramListAdapter extends BaseAdapter {

    private List<ProgramListBean> mList;
    private Context mContext;
    private InPlayBean inPlayBean;



    private int mType=0;        //0 电台  1 电视台

    public void setType(int type) {
        this.mType = type;
    }

    public InPlayBean getInPlayBean() {
        return inPlayBean;
    }

    public void setInPlayBean(InPlayBean inPlayBean) {
        this.inPlayBean = inPlayBean;
    }

    public RadioProgramListAdapter(Context context, List<ProgramListBean> list) {
        super();
        this.mList = list;
        this.mContext = context;
    }


    public void setData(List<ProgramListBean> list) {
        this.mList = list;
    }


    @Override
    public int getCount() {
        return mList.size();
    }


    @Override
    public ProgramListBean getItem(int position) {
        return mList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
             if(mType==0){
                 convertView = LayoutInflater.from(mContext).inflate(R.layout.radio_program_item, parent, false);
             }else{
                 convertView = LayoutInflater.from(mContext).inflate(R.layout.tv_program_item, parent, false);
             }

        }
        ViewHolder viewHolder=new ViewHolder(convertView);
        ProgramListBean program = getItem(position);
        viewHolder.program.setText(program.getName());
        viewHolder.anchorName.setText(program.getAnchor_name());
        viewHolder.time.setText(program.getStart_time()+"－"+program.getEnd_time());

        if(inPlayBean!=null&&inPlayBean.getStart_time().equals(program.getStart_time())&&inPlayBean.getEnd_time().equals(program.getEnd_time())){
            viewHolder.live.setVisibility(View.VISIBLE);
            viewHolder.live.setActivated(true);
            viewHolder.program.setSelected(true);
        }else {
            viewHolder.live.setVisibility(View.GONE);
            viewHolder.program.setSelected(false);
        }

        return convertView;
    }



    class ViewHolder {
        @BindView(R.id.program)
        TextView program;
        @BindView(R.id.anchorName)
        TextView anchorName;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.live)
        TextView live;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
