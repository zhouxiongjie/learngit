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
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.activity.ArticleDetailActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.activity.SingleAudioDetailActivity;
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
public class RecommendContentGridAdapter extends RecyclerView.Adapter implements View.OnClickListener {

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

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, ColumnContent content);

        void onItemClick(View view, int pos);
    }


    public RecommendContentGridAdapter(Context context, List<ColumnContent> columnContent) {
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

    public boolean hasHeadView() {
        return mHeaderView != null;
    }

    public List<ColumnContent> getData() {
        return this.mColumnContent;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEAD) {
            return new HeadViewHolder(mHeaderView);
//        } else if (viewType == TYPE_AUDIO) {
//            return new AudioViewHolder(inflater.inflate(R.layout.content_audio_item, parent, false));
//        } else if (viewType == TYPE_ALBUM) {
//            return new AlbumViewHolder(inflater.inflate(R.layout.content_album_item, parent, false));
//        } else if (viewType == TYPE_ARTICLE) {
//            return new ArticleViewHolder(inflater.inflate(R.layout.content_article_item, parent, false));
//        } else if (viewType == TYPE_VIDEO) {
//            return new VideoViewHolder(inflater.inflate(R.layout.content_video_item, parent, false));
//        } else if (viewType == TYPE_GALLERIE_ONE) {
//            return new GallerieOneViewHolder(inflater.inflate(R.layout.content_gallerie_one_item, parent, false));
//        } else if (viewType == TYPE_GALLERIE_THREE) {
//            return new GallerieViewThreeHolder(inflater.inflate(R.layout.content_gallerie_three_item, parent, false));
//        } else {
//            return new SpecialViewHolder(inflater.inflate(R.layout.content_special_item, parent, false));
        } else {
            return new ContentViewHolder(inflater.inflate(R.layout.content_recommend_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ColumnContent content;
        if (mHeaderView != null) {
            if (position == 0) {
                content = null;
            } else {
                content = mColumnContent.get(position - 1);
            }

        } else {
            content = mColumnContent.get(position);
        }

        if (getItemViewType(position) == TYPE_HEAD) {

        } else if (getItemViewType(position) == TYPE_AUDIO) {
            ContentViewHolder audioViewHolder = (ContentViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (CommonUtils.getScreenWidth()-CommonUtils.dip2px(30))/3;
                int height = width;
                ImageLoader.showThumb(uri, audioViewHolder.logo, width, height);
            }

            audioViewHolder.title.setText(content.getTitle());
            audioViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, SingleAudioDetailActivity.class);
                    it.putExtra("audioId", content.getId());
                    mContext.startActivity(it);
                }
            });

        } else if (getItemViewType(position) == TYPE_ALBUM) {
            ContentViewHolder albumViewHolder = (ContentViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (CommonUtils.getScreenWidth()-CommonUtils.dip2px(30))/3;
                int height = width;
                ImageLoader.showThumb(uri, albumViewHolder.logo, width, height);
            }
            albumViewHolder.title.setText(content.getTitle());
            albumViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, AlbumDetailActivity.class);
                    it.putExtra("albumId", content.getId());

                    mContext.startActivity(it);
                }
            });

        } else if (getItemViewType(position) == TYPE_ARTICLE) {
            ContentViewHolder articleViewHolder = (ContentViewHolder) holder;
            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (CommonUtils.getScreenWidth()-CommonUtils.dip2px(30))/3;
                int height = width;
                ImageLoader.showThumb(uri, articleViewHolder.logo, width, height);
            }

            articleViewHolder.title.setText(content.getTitle());
            articleViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, ArticleDetailActivity.class);
                    it.putExtra("articleId", content.getId());
                    mContext.startActivity(it);
                }
            });
        } else if (getItemViewType(position) == TYPE_VIDEO) {
            ContentViewHolder videoViewHolder = (ContentViewHolder) holder;


            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (CommonUtils.getScreenWidth()-CommonUtils.dip2px(30))/3;
                int height = width;
                ImageLoader.showThumb(uri, videoViewHolder.logo, width, height);
            }

            videoViewHolder.title.setText(content.getTitle());
        } else if (getItemViewType(position) == TYPE_GALLERIE_ONE) {
            ContentViewHolder gallerieOneViewHolder = (ContentViewHolder) holder;

            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(0))) {
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(0));
                int width = (CommonUtils.getScreenWidth()-CommonUtils.dip2px(30))/3;
                int height = width;
                ImageLoader.showThumb(uri, gallerieOneViewHolder.logo, width, height);
            }
            gallerieOneViewHolder.title.setText(content.getTitle());
            gallerieOneViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, GalleriaActivity.class);
                    it.putExtra("galleriaId", content.getId());
                    mContext.startActivity(it);
                }
            });

        } else if (getItemViewType(position) == TYPE_GALLERIE_THREE) {
            ContentViewHolder gallerieViewThreeHolder = (ContentViewHolder) holder;

            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(0))) {
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(0));
                int width = (CommonUtils.getScreenWidth()-CommonUtils.dip2px(30))/3;
                int height = width;
                ImageLoader.showThumb(uri, gallerieViewThreeHolder.logo, width, height);
            }

            gallerieViewThreeHolder.title.setText(content.getTitle());
            gallerieViewThreeHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, GalleriaActivity.class);
                    it.putExtra("galleriaId", content.getId());
                    mContext.startActivity(it);
                }
            });

        } else {
            ContentViewHolder specialViewHolder = (ContentViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (CommonUtils.getScreenWidth()-CommonUtils.dip2px(30))/3;
                int height = width;
                ImageLoader.showThumb(uri, specialViewHolder.logo, width, height);
            }

            specialViewHolder.title.setText(content.getTitle());
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


    public class ContentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.excellent)
        TextView excellent;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.duration)
        TextView duration;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.organizationLogo)
        SimpleDraweeView organizationLogo;
        @BindView(R.id.organization)
        TextView organization;
        @BindView(R.id.commentNum)
        TextView commentNum;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.root)
        LinearLayout root;

        public ContentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


//    public class AlbumViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.logo)
//        SimpleDraweeView logo;
//        @BindView(R.id.title)
//        TextView title;
//        @BindView(R.id.merchant)
//        TextView merchant;
//        @BindView(R.id.commentNum)
//        TextView commentNum;
//        @BindView(R.id.publishTime)
//        TextView publishTime;
//        @BindView(R.id.root)
//        LinearLayout root;
//        @BindView(R.id.excellent)
//        TextView excellent;
//
//        public AlbumViewHolder(View view) {
//            super(view);
//            ButterKnife.bind(this, view);
//        }
//    }
//
//
//    public class SpecialViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.logo)
//        SimpleDraweeView logo;
//        @BindView(R.id.title)
//        TextView title;
//        @BindView(R.id.merchant)
//        TextView merchant;
//        @BindView(R.id.commentNum)
//        TextView commentNum;
//        @BindView(R.id.publishTime)
//        TextView publishTime;
//        @BindView(R.id.root)
//        LinearLayout root;
//        @BindView(R.id.excellent)
//        TextView excellent;
//
//        public SpecialViewHolder(View view) {
//            super(view);
//            ButterKnife.bind(this, view);
//
//        }
//    }
//
//
//    public class VideoViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.title)
//        TextView title;
//        @BindView(R.id.logo)
//        SimpleDraweeView logo;
//        @BindView(R.id.duration)
//        TextView duration;
//        @BindView(R.id.commentNum)
//        TextView commentNum;
//        @BindView(R.id.publishTime)
//        TextView publishTime;
//        @BindView(R.id.root)
//        LinearLayout root;
//        @BindView(R.id.excellent)
//        TextView excellent;
//        @BindView(R.id.organization)
//        TextView organization;
//        @BindView(R.id.organizationLogo)
//        SimpleDraweeView organizationLogo;
//
//
//        public VideoViewHolder(View view) {
//            super(view);
//            ButterKnife.bind(this, view);
//        }
//    }
//
//
//    public class GallerieOneViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.logo)
//        SimpleDraweeView logo;
//        @BindView(R.id.count)
//        TextView count;
//        @BindView(R.id.title)
//        TextView title;
//        @BindView(R.id.merchant)
//        TextView merchant;
//        @BindView(R.id.commentNum)
//        TextView commentNum;
//        @BindView(R.id.publishTime)
//        TextView publishTime;
//        @BindView(R.id.root)
//        LinearLayout root;
//        @BindView(R.id.excellent)
//        TextView excellent;
//
//        public GallerieOneViewHolder(View view) {
//            super(view);
//            ButterKnife.bind(this, view);
//        }
//    }
//
//
//    public class GallerieViewThreeHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.title)
//        TextView title;
//        @BindView(R.id.pic1)
//        SimpleDraweeView pic1;
//        @BindView(R.id.pic2)
//        SimpleDraweeView pic2;
//        @BindView(R.id.pic3)
//        SimpleDraweeView pic3;
//        @BindView(R.id.count)
//        TextView count;
//        @BindView(R.id.merchant)
//        TextView merchant;
//        @BindView(R.id.commentNum)
//        TextView commentNum;
//        @BindView(R.id.publishTime)
//        TextView publishTime;
//        @BindView(R.id.root)
//        LinearLayout root;
//        @BindView(R.id.excellent)
//        TextView excellent;
//
//        public GallerieViewThreeHolder(View view) {
//            super(view);
//            ButterKnife.bind(this, view);
//        }
//    }
//
//
//    public class ArticleViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.logo)
//        SimpleDraweeView logo;
//        @BindView(R.id.title)
//        TextView title;
//        @BindView(R.id.merchant)
//        TextView merchant;
//        @BindView(R.id.commentNum)
//        TextView commentNum;
//        @BindView(R.id.publishTime)
//        TextView publishTime;
//        @BindView(R.id.root)
//        LinearLayout root;
//        @BindView(R.id.excellent)
//        TextView excellent;
//
//        public ArticleViewHolder(View view) {
//            super(view);
//            ButterKnife.bind(this, view);
//        }
//    }


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
