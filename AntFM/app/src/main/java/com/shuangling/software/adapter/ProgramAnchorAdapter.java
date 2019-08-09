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
import com.shuangling.software.entity.Anchor;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class ProgramAnchorAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    //"type": 4,//1 音频 2 专辑 3 文章 4 视频 5专题

    public static final int TYPE_ARTICLE = 2;           //文章
    public static final int TYPE_VIDEO = 3;             //视频
    public static final int TYPE_SPECIAL = 4;           //专题
    public static final int TYPE_RADIO = 5;             //电台
    public static final int TYPE_GALLERIE = 7;          //图集


    private Context mContext;
    private List<Anchor> mAnchors;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {

        void onItemClick(int pos);
    }


    public ProgramAnchorAdapter(Context context, List<Anchor> anchors) {
        this.mContext = context;
        this.mAnchors = anchors;
        inflater = LayoutInflater.from(mContext);

    }

    public void updataView(List<Anchor> anchors) {
        this.mAnchors = anchors;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new RadioViewHolder(inflater.inflate(R.layout.organization_anchor_item, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Anchor content = mAnchors.get(position);

        final RadioViewHolder articleViewHolder = (RadioViewHolder) holder;

        if (!TextUtils.isEmpty(content.getLogo())) {
            Uri uri = Uri.parse(content.getLogo());
            int width = CommonUtils.dip2px(65);
            int height = width;
            ImageLoader.showThumb(uri, articleViewHolder.logo, width, height);
        } else {
            ImageLoader.showThumb(articleViewHolder.logo, R.drawable.article_placeholder);
        }

        articleViewHolder.title.setText(content.getName());
        articleViewHolder.attentionNumber.setText(""+content.getFollows());

        articleViewHolder.root.setOnClickListener(new View.OnClickListener() {
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

        if (mAnchors != null) {
            return mAnchors.size();
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
        @BindView(R.id.attentionNumber)
        TextView attentionNumber;
        @BindView(R.id.root)
        RelativeLayout root;

        RadioViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
