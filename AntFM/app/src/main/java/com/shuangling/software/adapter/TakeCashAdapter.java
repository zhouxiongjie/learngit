package com.shuangling.software.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shuangling.software.R;
import com.shuangling.software.entity.TakeCashInfo;
import com.shuangling.software.utils.TimeUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class TakeCashAdapter extends RecyclerView.Adapter implements View.OnClickListener {


    private Context mContext;
    private List<TakeCashInfo> mTakeCashInfos;
    private LayoutInflater inflater;


    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {

        void onItemClick(int pos);
    }


    public TakeCashAdapter(Context context, List<TakeCashInfo> takeCashInfos) {
        this.mContext = context;
        this.mTakeCashInfos = takeCashInfos;
        inflater = LayoutInflater.from(mContext);

    }

    public void updateView(List<TakeCashInfo> takeCashInfos) {
        this.mTakeCashInfos = takeCashInfos;
        notifyDataSetChanged();

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        return new TakeCashViewHolder(inflater.inflate(R.layout.take_cash_item, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final TakeCashInfo takeCashInfo = mTakeCashInfos.get(position);

        final TakeCashViewHolder attentionViewHolder = (TakeCashViewHolder) holder;

        attentionViewHolder.remark.setText("支付宝提现");
        attentionViewHolder.time.setText(TimeUtil.formatDateTime(takeCashInfo.getUpdated_at()));
        if (takeCashInfo.getStatus() == 1||takeCashInfo.getStatus() == 2) {
            attentionViewHolder.status.setBackgroundResource(R.drawable.process_bg);
            attentionViewHolder.status.setTextColor(Color.parseColor("#EB8C10"));
            attentionViewHolder.status.setText("处理中");
        } else if(takeCashInfo.getStatus() == 3) {
            attentionViewHolder.status.setBackgroundResource(R.drawable.process_success_bg);
            attentionViewHolder.status.setTextColor(Color.parseColor("#4690FF"));
            attentionViewHolder.status.setText("已到账");
        }else if(takeCashInfo.getStatus() == 4){
            attentionViewHolder.status.setBackgroundResource(R.drawable.process_fail_bg);
            attentionViewHolder.status.setTextColor(Color.parseColor("#FF5028"));
            attentionViewHolder.status.setText("失败");
        }
        attentionViewHolder.money.setText(String.format("%.2f",(float)takeCashInfo.getMoney()/100));
        attentionViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });

    }


    @Override
    public int getItemCount() {

        if (mTakeCashInfos != null) {
            return mTakeCashInfos.size();
        } else {
            return 0;
        }

    }


    @Override
    public void onClick(View v) {

    }


    public class TakeCashViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.remark)
        TextView remark;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.money)
        TextView money;
        @BindView(R.id.root)
        RelativeLayout root;

        public TakeCashViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
