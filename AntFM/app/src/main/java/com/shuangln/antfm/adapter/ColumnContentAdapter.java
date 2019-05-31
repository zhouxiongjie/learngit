package com.shuangln.antfm.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangln.antfm.R;
import com.shuangln.antfm.activity.AlbumDetailActivity;
import com.shuangln.antfm.activity.ArticleDetailActivity;
import com.shuangln.antfm.entity.ColumnContent;
import com.shuangln.antfm.utils.CommonUtils;
import com.shuangln.antfm.utils.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class ColumnContentAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    //"type": 4,//1 音频 2 专辑 3 文章 4 视频 5专题

    public static final int TYPE_AUDIO = 0;         //音频
    public static final int TYPE_ALBUM = 1;         //专辑
    public static final int TYPE_ARTICLE = 2;        //文章
    public static final int TYPE_VIDEO = 3;          //文章
    public static final int TYPE_SPECIAL = 4;         //文章


    private Context mContext;
    private List<ColumnContent.Content> mColumnContent;
    private LayoutInflater inflater;


    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, ColumnContent.Content content);

        void onItemClick(View view, int pos);
    }


    public ColumnContentAdapter(Context context, List<ColumnContent.Content> columnContent) {
        this.mContext = context;
        this.mColumnContent = columnContent;
        inflater = LayoutInflater.from(mContext);

    }

    public void setData(List<ColumnContent.Content> columnContent) {
        this.mColumnContent = columnContent;

    }

    public List<ColumnContent.Content> getData() {
        return this.mColumnContent;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if (viewType == TYPE_ALBUM) {
            return new AlbumViewHolder(inflater.inflate(R.layout.content_album_item, parent, false));
        } else if (viewType == TYPE_ARTICLE) {
            return new ArticleViewHolder(inflater.inflate(R.layout.content_article_item, parent, false));
        } else if (viewType == TYPE_VIDEO) {
            return new VideoViewHolder(inflater.inflate(R.layout.content_video_item, parent, false));
        } else {
            return new SpecialViewHolder(inflater.inflate(R.layout.content_special_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ColumnContent.Content content=mColumnContent.get(position);
        if(getItemViewType(position) == TYPE_ALBUM){
            AlbumViewHolder albumViewHolder = (AlbumViewHolder)holder;
            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width=(int)mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height=(int)(2*width/3);
                ImageLoader.showThumb(uri,albumViewHolder.logo,width,height);
            }
            albumViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            albumViewHolder.publishTime.setText(content.getUpdate_time());
            albumViewHolder.title.setText(content.getTitle());
            albumViewHolder.commentNum.setText(""+content.getComment()+"评论");
            albumViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it=new Intent(mContext,AlbumDetailActivity.class);
                    it.putExtra("albumId",content.getId());

                    mContext.startActivity(it);
                }
            });

        }else if(getItemViewType(position) == TYPE_ARTICLE){
            ArticleViewHolder articleViewHolder = (ArticleViewHolder)holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width=(int)mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height=(int)(2*width/3);
                ImageLoader.showThumb(uri,articleViewHolder.logo,width,height);
            }
            articleViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            articleViewHolder.publishTime.setText(content.getUpdate_time());
            articleViewHolder.title.setText(content.getTitle());
            articleViewHolder.commentNum.setText(""+content.getComment()+"评论");
            articleViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it=new Intent(mContext,ArticleDetailActivity.class);
                    it.putExtra("articleId",content.getId());
                    mContext.startActivity(it);
                }
            });
        }else if(getItemViewType(position) == TYPE_VIDEO){
            VideoViewHolder videoViewHolder = (VideoViewHolder)holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width=CommonUtils.getScreenWidth()-CommonUtils.dip2px(20);
                int height=(int)(9*width/16);
                ImageLoader.showThumb(uri,videoViewHolder.logo,width,height);
            }
            videoViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            videoViewHolder.publishTime.setText(content.getUpdate_time());
            videoViewHolder.title.setText(content.getTitle());
            videoViewHolder.commentNum.setText(""+content.getComment()+"评论");
        }else {
            SpecialViewHolder specialViewHolder = (SpecialViewHolder)holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width=(int)mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height=(int)(2*width/3);
                ImageLoader.showThumb(uri,specialViewHolder.logo,width,height);
            }
            specialViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            specialViewHolder.publishTime.setText(content.getUpdate_time());
            specialViewHolder.title.setText(content.getTitle());
            specialViewHolder.commentNum.setText(""+content.getComment()+"评论");


        }
    }


    @Override
    public int getItemCount() {

        if (mColumnContent != null) {
            return mColumnContent.size();
        } else {
            return 0;
        }


    }

    @Override
    public void onClick(View v) {

    }


    public class AlbumViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.merchant)
        TextView merchant;
        @BindView(R.id.commentNum)
        TextView commentNum;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.root)
        RelativeLayout root;

        public AlbumViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public class SpecialViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.merchant)
        TextView merchant;
        @BindView(R.id.commentNum)
        TextView commentNum;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.root)
        RelativeLayout root;

        public SpecialViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }




    public class VideoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.duration)
        TextView duration;
        @BindView(R.id.merchant)
        TextView merchant;
        @BindView(R.id.commentNum)
        TextView commentNum;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.root)
        LinearLayout root;

        public VideoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.merchant)
        TextView merchant;
        @BindView(R.id.commentNum)
        TextView commentNum;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.root)
        RelativeLayout root;

        public ArticleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public int getItemViewType(int position) {

        if (mColumnContent.get(position).getType() == 1) {
            return TYPE_AUDIO;
        } else if (mColumnContent.get(position).getType() == 2) {
            return TYPE_ALBUM;
        } else if (mColumnContent.get(position).getType() == 3) {
            return TYPE_ARTICLE;
        } else if (mColumnContent.get(position).getType() == 4) {
            return TYPE_VIDEO;
        } else {
            return TYPE_SPECIAL;
        }

    }



}
