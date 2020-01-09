package com.shuangling.software.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.entity.CashIncomeInfo;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.TimeUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class CashIncomeAdapter extends RecyclerView.Adapter implements View.OnClickListener {


    private Context mContext;
    private List<CashIncomeInfo> mCashIncomeInfos;
    private LayoutInflater inflater;


    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {

        void onItemClick(int pos);
    }


    public CashIncomeAdapter(Context context, List<CashIncomeInfo> cashIncomeInfos) {
        this.mContext = context;
        this.mCashIncomeInfos = cashIncomeInfos;
        inflater = LayoutInflater.from(mContext);

    }

    public void updateView(List<CashIncomeInfo> cashIncomeInfos) {
        this.mCashIncomeInfos = cashIncomeInfos;
        notifyDataSetChanged();

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        return new CashIncomeViewHolder(inflater.inflate(R.layout.cash_income_item, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final CashIncomeInfo cashIncomeInfo = mCashIncomeInfos.get(position);

        final CashIncomeViewHolder attentionViewHolder = (CashIncomeViewHolder) holder;

        attentionViewHolder.remark.setText(cashIncomeInfo.getRemark());
        attentionViewHolder.time.setText(TimeUtil.formatDateTime(cashIncomeInfo.getUpdated_at()));
        if(cashIncomeInfo.getType()==1){
            attentionViewHolder.money.setText("+"+String.format("%.2f",(float)cashIncomeInfo.getMoney()/100));
        }else{
            attentionViewHolder.money.setText("-"+String.format("%.2f",(float)cashIncomeInfo.getMoney()/100));
        }
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

        if (mCashIncomeInfos != null) {
            return mCashIncomeInfos.size();
        } else {
            return 0;
        }

    }


    @Override
    public void onClick(View v) {

    }


    public class CashIncomeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.remark)
        TextView remark;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.money)
        TextView money;
        @BindView(R.id.root)
        RelativeLayout root;

        public CashIncomeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }



}
