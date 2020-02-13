package com.shuangling.software.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.entity.HistoryRadio;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class HistoryRadioAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    //"type": 4,//1 音频 2 专辑 3 文章 4 视频 5专题

    public static final int TYPE_AUDIO = 0;             //音频
    public static final int TYPE_ARTICLE = 2;           //文章
    public static final int TYPE_VIDEO = 3;             //视频
    public static final int TYPE_SPECIAL = 4;           //专题
    public static final int TYPE_RADIO = 5;             //电台
    public static final int TYPE_GALLERIE = 7;          //图集


    private Context mContext;
    private List<HistoryRadio> mHistorys;
    private LayoutInflater inflater;
    private boolean mEditorMode = false;
    private int[] mItemSelected;

    public void setEditorMode(boolean editorMode) {
        if (editorMode) {
            mItemSelected = new int[mHistorys.size()];
            resetItemSelected();
        }
        this.mEditorMode = editorMode;
        notifyDataSetChanged();
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {

        void onItemClick(int pos);
    }


    public HistoryRadioAdapter(Context context, List<HistoryRadio> historys) {
        this.mContext = context;
        this.mHistorys = historys;
        inflater = LayoutInflater.from(mContext);

    }

    public void setData(List<HistoryRadio> historys) {
        this.mHistorys = historys;

    }

    public List<HistoryRadio> getData() {
        return mHistorys;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RadioViewHolder(inflater.inflate(R.layout.history_radio_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final HistoryRadio content = mHistorys.get(position);

        final RadioViewHolder radioViewHolder = (RadioViewHolder) holder;
        if(content.getRadio()!=null){
            if (!TextUtils.isEmpty(content.getRadio().getLogo())) {
                Uri uri = Uri.parse(content.getRadio().getLogo());
                int width = CommonUtils.dip2px(70);
                int height = width;
                ImageLoader.showThumb(uri, radioViewHolder.logo, width, height);
            }
            radioViewHolder.title.setText(content.getRadio().getName());
            radioViewHolder.program.setText(content.getRadio().getDes());
        }

        if (mEditorMode) {
            radioViewHolder.checkBox.setVisibility(View.VISIBLE);
            if (mItemSelected[position] == 1) {
                radioViewHolder.checkBox.setChecked(true);
            } else {
                radioViewHolder.checkBox.setChecked(false);
            }
        } else {
            radioViewHolder.checkBox.setVisibility(View.GONE);
        }

        radioViewHolder.time.setText(content.getUpdated_at());
        radioViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditorMode) {
                    if (radioViewHolder.checkBox.isChecked()) {
                        radioViewHolder.checkBox.setChecked(false);
                        mItemSelected[position] = 0;
                    } else {
                        radioViewHolder.checkBox.setChecked(true);
                        mItemSelected[position] = 1;
                    }

                } else {
                    if(onItemClickListener!=null){
                        onItemClickListener.onItemClick(position);
                    }
                }

            }
        });


    }


    @Override
    public int getItemCount() {

        if (mHistorys != null) {
            return mHistorys.size();
        } else {
            return 0;
        }

    }

    @Override
    public void onClick(View v) {

    }


    public class RadioViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.checkBox)
        CheckBox checkBox;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.program)
        TextView program;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.root)
        LinearLayout root;

        public RadioViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public int getItemViewType(int position) {

        return TYPE_RADIO;

    }


    public void resetItemSelected() {
        for (int i = 0; i < mItemSelected.length; i++) {
            mItemSelected[i] = 0;
        }
    }

    public void selectAllItem() {
        for (int i = 0; i < mItemSelected.length; i++) {
            mItemSelected[i] = 1;
        }
        notifyDataSetChanged();
    }

    public int[] getItemSelected() {
        return mItemSelected;
    }


}
