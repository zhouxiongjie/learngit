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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.ArticleDetailActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.activity.RadioDetailActivity;
import com.shuangling.software.activity.RadioListActivity;
import com.shuangling.software.activity.SpecialDetailActivity;
import com.shuangling.software.activity.TvDetailActivity;
import com.shuangling.software.activity.VideoDetailActivity;
import com.shuangling.software.entity.CollectRadio;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class CollectRadioAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    //"type": 4,//1 音频 2 专辑 3 文章 4 视频 5专题
    
    public static final int TYPE_ARTICLE = 2;           //文章
    public static final int TYPE_VIDEO = 3;             //视频
    public static final int TYPE_SPECIAL = 4;           //专题
    public static final int TYPE_RADIO = 5;             //电台
    public static final int TYPE_GALLERIE = 7;          //图集


    private Context mContext;
    private List<CollectRadio> mCollects;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {

        void onItemClick(int pos);
    }


    public CollectRadioAdapter(Context context, List<CollectRadio> collects) {
        this.mContext = context;
        this.mCollects = collects;
        inflater = LayoutInflater.from(mContext);

    }

    public void setData(List<CollectRadio> collects) {
        this.mCollects = collects;

    }

    public List<CollectRadio> getData() {
        return mCollects;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new RadioViewHolder(inflater.inflate(R.layout.radio_child_item, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final CollectRadio content = mCollects.get(position);

        final RadioViewHolder articleViewHolder = (RadioViewHolder) holder;

        if (!TextUtils.isEmpty(content.getRadio().get(0).getLogo())) {
            Uri uri = Uri.parse(content.getRadio().get(0).getLogo());
            int width = CommonUtils.dip2px(50);
            int height = width;
            ImageLoader.showThumb(uri, articleViewHolder.logo, width, height);
        }else{
            ImageLoader.showThumb(articleViewHolder.logo, R.drawable.article_placeholder);
        }

        articleViewHolder.title.setText(content.getRadio().get(0).getName());
        if(content.getRadio().get(0).getSchedule()!=null&&content.getRadio().get(0).getSchedule().getProgram()!=null
                &&!TextUtils.isEmpty(content.getRadio().get(0).getSchedule().getProgram().getName())){
            articleViewHolder.program.setText(content.getRadio().get(0).getSchedule().getProgram().getName());
        }else{
            articleViewHolder.program.setText("暂无直播");
        }

        articleViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(position);
                }
            }
        });

    }


    @Override
    public int getItemCount() {

        if (mCollects != null) {
            return mCollects.size();
        } else {
            return 0;
        }

    }

    @Override
    public void onClick(View v) {

    }








    static class RadioViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.program)
        TextView program;
        @BindView(R.id.root)
        RelativeLayout root;

        RadioViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
