package com.shuangling.software.adapter;

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
import com.shuangling.software.R;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.activity.ArticleDetailActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.activity.SpecialDetailActivity;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class ColumnContentAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    //"type": 4,//1 音频 2 专辑 3 文章 4 视频 5专题

    public static final int TYPE_AUDIO = 0;             //音频
    public static final int TYPE_ALBUM = 1;             //专辑
    public static final int TYPE_ARTICLE = 2;           //文章
    public static final int TYPE_VIDEO = 3;             //视频
    public static final int TYPE_SPECIAL = 4;           //专题
    public static final int TYPE_GALLERIE_ONE = 5;      //一图集
    public static final int TYPE_GALLERIE_THREE = 6;    //三图集
    public static final int TYPE_HEAD = 7;              //头

    private Context mContext;
    private List<ColumnContent> mColumnContent;
    private LayoutInflater inflater;
    private View mHeaderView;
    private boolean mIsHot=false;

    public boolean isIsHot() {
        return mIsHot;
    }

    public void setIsHot(boolean mIsHot) {
        this.mIsHot = mIsHot;
    }


    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, ColumnContent content);

        void onItemClick(View view, int pos);
    }


    public ColumnContentAdapter(Context context, List<ColumnContent> columnContent) {
        this.mContext = context;
        this.mColumnContent = columnContent;
        inflater = LayoutInflater.from(mContext);

    }

    public void setData(List<ColumnContent> columnContent) {
        this.mColumnContent = columnContent;

    }


    public void addHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public List<ColumnContent> getData() {
        return this.mColumnContent;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEAD) {
            return new HeadViewHolder(mHeaderView);
        } else if (viewType == TYPE_AUDIO) {
            return new AudioViewHolder(inflater.inflate(R.layout.content_audio_item, parent, false));
        } else if (viewType == TYPE_ALBUM) {
            return new AlbumViewHolder(inflater.inflate(R.layout.content_album_item, parent, false));
        } else if (viewType == TYPE_ARTICLE) {
            return new ArticleViewHolder(inflater.inflate(R.layout.content_article_item, parent, false));
        } else if (viewType == TYPE_VIDEO) {
            return new VideoViewHolder(inflater.inflate(R.layout.content_video_item, parent, false));
        } else if (viewType == TYPE_GALLERIE_ONE) {
            return new GallerieOneViewHolder(inflater.inflate(R.layout.content_gallerie_one_item, parent, false));
        } else if (viewType == TYPE_GALLERIE_THREE) {
            return new GallerieViewThreeHolder(inflater.inflate(R.layout.content_gallerie_three_item, parent, false));
        } else {
            return new SpecialViewHolder(inflater.inflate(R.layout.content_special_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ColumnContent content;
        boolean showExcellent=false;
        if (mHeaderView != null) {
            if (position == 0) {
                content = null;
            } else {
                content = mColumnContent.get(position - 1);
                if(isIsHot()){
                    if(position-2>0){
                        if(mColumnContent.get(position-2).getTop()!=null){
                            showExcellent=true;
                        }
                    }
                }
            }

        } else {
            content = mColumnContent.get(position);
            if(isIsHot()){
                if(position-1>0){
                    if(mColumnContent.get(position-1).getTop()!=null){
                        showExcellent=true;
                    }
                }
            }
        }

        if (getItemViewType(position) == TYPE_HEAD) {

        } else if (getItemViewType(position) == TYPE_AUDIO) {
            AudioViewHolder audioViewHolder = (AudioViewHolder) holder;
            if(showExcellent){
                audioViewHolder.excellent.setVisibility(View.VISIBLE);
            }else{
                audioViewHolder.excellent.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.dip2px(100);
                int height = width;
                ImageLoader.showThumb(uri, audioViewHolder.logo, width, height);
            }
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                audioViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }

            //audioViewHolder.publishTime.setText(content.getUpdate_time());
            audioViewHolder.title.setText(content.getTitle());
            //audioViewHolder.commentNum.setText(""+content.getComment()+"评论");
            audioViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent it=new Intent(mContext,AudioDetailActivity.class);
//                    it.putExtra("Audio",content.geta());
//
//                    mContext.startActivity(it);
                }
            });

        } else if (getItemViewType(position) == TYPE_ALBUM) {
            AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;
            if(showExcellent){
                albumViewHolder.excellent.setVisibility(View.VISIBLE);
            }else{
                albumViewHolder.excellent.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, albumViewHolder.logo, width, height);
            }
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                albumViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }

            albumViewHolder.publishTime.setText(content.getUpdate_time());
            albumViewHolder.title.setText(content.getTitle());
            albumViewHolder.commentNum.setText("" + content.getComment() + "评论");
            albumViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, AlbumDetailActivity.class);
                    it.putExtra("albumId", content.getId());

                    mContext.startActivity(it);
                }
            });

        } else if (getItemViewType(position) == TYPE_ARTICLE) {
            ArticleViewHolder articleViewHolder = (ArticleViewHolder) holder;
            if(showExcellent){
                articleViewHolder.excellent.setVisibility(View.VISIBLE);
            }else{
                articleViewHolder.excellent.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, articleViewHolder.logo, width, height);
            }
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                articleViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }

            articleViewHolder.publishTime.setText(content.getUpdate_time());
            articleViewHolder.title.setText(content.getTitle());
            articleViewHolder.commentNum.setText("" + content.getComment() + "评论");
            articleViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, ArticleDetailActivity.class);
                    it.putExtra("articleId", content.getId());
                    mContext.startActivity(it);
                }
            });
        } else if (getItemViewType(position) == TYPE_VIDEO) {
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;

            if(showExcellent){
                videoViewHolder.excellent.setVisibility(View.VISIBLE);
            }else{
                videoViewHolder.excellent.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.getScreenWidth() - CommonUtils.dip2px(20);
                int height = (int) (9f * width / 16f);
                ImageLoader.showThumb(uri, videoViewHolder.logo, width, height);
            }
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                videoViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }
            videoViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            videoViewHolder.publishTime.setText(content.getUpdate_time());
            videoViewHolder.title.setText(content.getTitle());
            videoViewHolder.commentNum.setText("" + content.getComment() + "评论");
        } else if(getItemViewType(position) == TYPE_GALLERIE_ONE) {
            GallerieOneViewHolder gallerieOneViewHolder = (GallerieOneViewHolder) holder;

            if(showExcellent){
                gallerieOneViewHolder.excellent.setVisibility(View.VISIBLE);
            }else{
                gallerieOneViewHolder.excellent.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(0))) {
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(0));
                int width = CommonUtils.getScreenWidth();
                int height = (int) (9f * width / 16f);
                ImageLoader.showThumb(uri, gallerieOneViewHolder.logo, width, height);
            }
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                gallerieOneViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }
            gallerieOneViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            gallerieOneViewHolder.publishTime.setText(content.getUpdate_time());
            gallerieOneViewHolder.title.setText(content.getTitle());
            gallerieOneViewHolder.commentNum.setText("" + content.getComment() + "评论");
            gallerieOneViewHolder.count.setText(content.getGallerie().getCount()+"图");
            gallerieOneViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, GalleriaActivity.class);
                    it.putExtra("galleriaId", content.getId());
                    mContext.startActivity(it);
                }
            });

        }else if(getItemViewType(position) == TYPE_GALLERIE_THREE) {
            GallerieViewThreeHolder gallerieViewThreeHolder = (GallerieViewThreeHolder) holder;

            if(showExcellent){
                gallerieViewThreeHolder.excellent.setVisibility(View.VISIBLE);
            }else{
                gallerieViewThreeHolder.excellent.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(0))) {
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(0));
                int width = (CommonUtils.getScreenWidth()-CommonUtils.dip2px(30))/3;
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, gallerieViewThreeHolder.pic1, width, height);
            }
            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(1))) {
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(1));
                int width = (CommonUtils.getScreenWidth()-CommonUtils.dip2px(30))/3;
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, gallerieViewThreeHolder.pic2, width, height);
            }
            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(2))) {
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(2));
                int width = (CommonUtils.getScreenWidth()-CommonUtils.dip2px(30))/3;
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, gallerieViewThreeHolder.pic3, width, height);
            }
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                gallerieViewThreeHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }
            gallerieViewThreeHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            gallerieViewThreeHolder.publishTime.setText(content.getUpdate_time());
            gallerieViewThreeHolder.title.setText(content.getTitle());
            gallerieViewThreeHolder.commentNum.setText("" + content.getComment() + "评论");
            gallerieViewThreeHolder.count.setText(content.getGallerie().getCount()+"图");
            gallerieViewThreeHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, GalleriaActivity.class);
                    it.putExtra("galleriaId", content.getId());
                    mContext.startActivity(it);
                }
            });

        }else {
            SpecialViewHolder specialViewHolder = (SpecialViewHolder) holder;
            if(showExcellent){
                specialViewHolder.excellent.setVisibility(View.VISIBLE);
            }else{
                specialViewHolder.excellent.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, specialViewHolder.logo, width, height);
            }
            specialViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            specialViewHolder.publishTime.setText(content.getUpdate_time());
            specialViewHolder.title.setText(content.getTitle());
            specialViewHolder.commentNum.setText("" + content.getComment() + "评论");
            specialViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, SpecialDetailActivity.class);
                    it.putExtra("specialId", content.getId());

                    mContext.startActivity(it);
                }
            });

        }
    }


    @Override
    public void onClick(View v) {

    }


    public class HeadViewHolder extends RecyclerView.ViewHolder {


        public HeadViewHolder(View view) {
            super(view);

        }
    }


    public class AudioViewHolder extends RecyclerView.ViewHolder {

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
        LinearLayout root;
        @BindView(R.id.excellent)
        TextView excellent;

        public AudioViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
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
        LinearLayout root;
        @BindView(R.id.excellent)
        TextView excellent;

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
        LinearLayout root;
        @BindView(R.id.excellent)
        TextView excellent;

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
        @BindView(R.id.excellent)
        TextView excellent;

        public VideoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public class GallerieOneViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.merchant)
        TextView merchant;
        @BindView(R.id.commentNum)
        TextView commentNum;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.root)
        LinearLayout root;
        @BindView(R.id.excellent)
        TextView excellent;

        public GallerieOneViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public class GallerieViewThreeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.pic1)
        SimpleDraweeView pic1;
        @BindView(R.id.pic2)
        SimpleDraweeView pic2;
        @BindView(R.id.pic3)
        SimpleDraweeView pic3;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.merchant)
        TextView merchant;
        @BindView(R.id.commentNum)
        TextView commentNum;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.root)
        LinearLayout root;
        @BindView(R.id.excellent)
        TextView excellent;

        public GallerieViewThreeHolder(View view) {
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
        LinearLayout root;
        @BindView(R.id.excellent)
        TextView excellent;

        public ArticleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public int getItemCount() {
        if (mHeaderView != null) {
            if (mColumnContent != null) {
                return mColumnContent.size() + 1;
            } else {
                return 1;
            }
        } else {
            if (mColumnContent != null) {
                return mColumnContent.size();
            } else {
                return 0;
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView != null) {
            if (position == 0) {
                return TYPE_HEAD;
            } else {
                if (mColumnContent.get(position - 1).getType() == 1) {
                    return TYPE_AUDIO;
                } else if (mColumnContent.get(position - 1).getType() == 2) {
                    return TYPE_ALBUM;
                } else if (mColumnContent.get(position - 1).getType() == 3) {
                    return TYPE_ARTICLE;
                } else if (mColumnContent.get(position - 1).getType() == 4) {
                    return TYPE_VIDEO;
                } else if (mColumnContent.get(position - 1).getType() == 5) {
                    return TYPE_SPECIAL;
                } else {
                    if (mColumnContent.get(position - 1).getGallerie().getType() == 1) {
                        return TYPE_GALLERIE_ONE;
                    } else if (mColumnContent.get(position - 1).getGallerie().getType() == 2) {
                        return TYPE_GALLERIE_THREE;
                    } else {
                        if (mColumnContent.get(position - 1).getGallerie().getCovers().size() < 3) {
                            return TYPE_GALLERIE_ONE;
                        } else {
                            return TYPE_GALLERIE_THREE;
                        }
                    }

                }
            }


        } else {
            if (mColumnContent.get(position).getType() == 1) {
                return TYPE_AUDIO;
            } else if (mColumnContent.get(position).getType() == 2) {
                return TYPE_ALBUM;
            } else if (mColumnContent.get(position).getType() == 3) {
                return TYPE_ARTICLE;
            } else if (mColumnContent.get(position).getType() == 4) {
                return TYPE_VIDEO;
            } else if (mColumnContent.get(position).getType() == 5) {
                return TYPE_SPECIAL;
            } else {
                if (mColumnContent.get(position).getGallerie().getType() == 1) {
                    return TYPE_GALLERIE_ONE;
                } else if (mColumnContent.get(position).getGallerie().getType() == 2) {
                    return TYPE_GALLERIE_THREE;
                } else {
                    if (mColumnContent.get(position).getGallerie().getCovers().size() < 3) {
                        return TYPE_GALLERIE_ONE;
                    } else {
                        return TYPE_GALLERIE_THREE;
                    }
                }

            }
        }


    }


}
