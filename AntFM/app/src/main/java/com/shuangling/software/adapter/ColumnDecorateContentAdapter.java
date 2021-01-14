package com.shuangling.software.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.activity.ArticleDetailActivity02;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.activity.LiveDetailActivity;
import com.shuangling.software.activity.SpecialDetailActivity;
import com.shuangling.software.activity.VideoDetailActivity;
import com.shuangling.software.activity.WebViewBackActivity;
import com.shuangling.software.customview.BannerView;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.entity.ColumnContent;
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
public class ColumnDecorateContentAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    //"type": 4,//1 音频 2 专辑 3 文章 4 视频 5专题
    public static final int TYPE_AUDIO = 0;             //音频
    public static final int TYPE_ALBUM = 1;             //专辑
    public static final int TYPE_ARTICLE = 2;           //文章
    public static final int TYPE_ARTICLE_THREE = 3;     //文章
    public static final int TYPE_VIDEO = 4;             //视频
    public static final int TYPE_SPECIAL = 5;           //专题
    public static final int TYPE_GALLERIE_ONE = 6;      //一图集
    public static final int TYPE_GALLERIE_THREE = 7;    //三图集
    public static final int TYPE_ACTIVITY = 8;          //三图集
    public static final int TYPE_LIVE = 9;              //三图集
    public static final int TYPE_HEAD = 10;              //头
    public static final int TYPE_DECORATE = 11;          //装修头
    private Context mContext;
    private List<ColumnContent> mColumnContent;
    private LayoutInflater inflater;
    private View mHeaderView;
    private LinearLayout mDecorateLayout;
    private boolean mIsHot = false;
    private boolean mIsVideo = false;
    private boolean mIsActivity = false;

    public boolean isActivity() {
        return mIsActivity;
    }

    public void setIsActivity(boolean isActivity) {
        this.mIsActivity = isActivity;
    }

    public boolean isIsHot() {
        return mIsHot;
    }

    public void setIsHot(boolean mIsHot) {
        this.mIsHot = mIsHot;
    }

    public boolean isVideo() {
        return mIsVideo;
    }

    public void setIsVideo(boolean isVideo) {
        this.mIsVideo = isVideo;
    }

    public String getOssResize(int width, int height) {
        return "?x-oss-process=image/resize,m_fill,h_" + height + ",w_" + width;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ColumnContent content);

        void onItemClick(View view, int pos);
    }

    public ColumnDecorateContentAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    public ColumnDecorateContentAdapter(Context context, List<ColumnContent> columnContent) {
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

    public void addDecorateLayout(LinearLayout decorateLayout) {
        mDecorateLayout = decorateLayout;
        notifyDataSetChanged();
    }

    public List<ColumnContent> getData() {
        return this.mColumnContent;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            return new HeadViewHolder(mHeaderView);
        } else if (viewType == TYPE_DECORATE) {
            return new DecorateViewHolder(mDecorateLayout);
        } else if (viewType == TYPE_AUDIO) {
            return new AudioViewHolder(inflater.inflate(R.layout.content_audio_item, parent, false));
        } else if (viewType == TYPE_ALBUM) {
            return new AlbumViewHolder(inflater.inflate(R.layout.content_album_item, parent, false));
        } else if (viewType == TYPE_ARTICLE) {
            return new ArticleViewHolder(inflater.inflate(R.layout.content_article_item, parent, false));
        } else if (viewType == TYPE_ARTICLE_THREE) {
            return new ArticleViewThreeHolder(inflater.inflate(R.layout.content_article_three_item, parent, false));
        } else if (viewType == TYPE_VIDEO) {
//            if (isVideo()) {
//                return new VideoViewHolder(inflater.inflate(R.layout.content_video_item_one, parent, false));
//            } else {
            return new VideoViewHolder(inflater.inflate(R.layout.content_video_item, parent, false));
//            }
        } else if (viewType == TYPE_GALLERIE_ONE) {
            return new GallerieOneViewHolder(inflater.inflate(R.layout.content_gallerie_one_item, parent, false));
        } else if (viewType == TYPE_GALLERIE_THREE) {
            return new GallerieViewThreeHolder(inflater.inflate(R.layout.content_gallerie_three_item, parent, false));
        } else if (viewType == TYPE_SPECIAL) {
            return new SpecialViewHolder(inflater.inflate(R.layout.content_special_item, parent, false));
        } else if (viewType == TYPE_ACTIVITY) {
            return new ActivityViewHolder(inflater.inflate(R.layout.content_activity_item, parent, false));
        } else {
            return new LiveViewHolder(inflater.inflate(R.layout.content_live_item, parent, false));
        }
    }

    //    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
//
//        if(payloads.isEmpty()){
//            onBindViewHolder(holder, position);
//        } else {
//            onBindItemHolder(holder, position);
//        }
//
//    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ColumnContent content;
        if (mHeaderView != null) {
            if (position == 0) {
                content = null;
            } else if (mDecorateLayout != null) {
                if (position == 1) {
                    content = null;
                } else {
                    content = mColumnContent.get(position - 2);
                }
            } else {
                content = mColumnContent.get(position - 1);
            }
        } else {
            if (mDecorateLayout != null) {
                if (position == 0) {
                    content = null;
                } else {
                    content = mColumnContent.get(position - 1);
                }
            } else {
                content = mColumnContent.get(position);
            }
        }
        int itemViewType = getItemViewType(position);
        if (itemViewType == TYPE_HEAD) {
        } else if (itemViewType == TYPE_DECORATE) {
        } else if (itemViewType == TYPE_AUDIO) {
            AudioViewHolder audioViewHolder = (AudioViewHolder) holder;
            audioViewHolder.excellent.setVisibility(View.GONE);
            if (content.getTop() != null) {
                audioViewHolder.top.setVisibility(View.VISIBLE);
            } else {
                audioViewHolder.top.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(content.getCover())) {
                int width = CommonUtils.dip2px(100);
                int height = width;
                Uri uri = Uri.parse(content.getCover() + getOssResize(width, height));
                ImageLoader.showThumb(uri, audioViewHolder.logo, width, height);
            } else {
                ImageLoader.showThumb(audioViewHolder.logo, R.drawable.article_placeholder);
            }
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                audioViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }
            audioViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            audioViewHolder.title.setText(content.getTitle());
            //audioViewHolder.commentNum.setText(""+content.getComment()+"评论");
            audioViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, AudioDetailActivity.class);
                    it.putExtra("audioId", content.getId());
                    mContext.startActivity(it);
                }
            });
        } else if (itemViewType == TYPE_ALBUM) {
            final AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;
            albumViewHolder.excellent.setVisibility(View.GONE);
            if (content.getTop() != null) {
                albumViewHolder.top.setVisibility(View.VISIBLE);
            } else {
                albumViewHolder.top.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(content.getCover())) {
                int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height = (int) (2f * width / 3f);
                Uri uri = Uri.parse(content.getCover() + getOssResize(width, height));
                ImageLoader.showThumb(uri, albumViewHolder.logo, width, height);
            }
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                albumViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }
            if (albumViewHolder.title.getTag() != null) {
                ViewTreeObserver.OnPreDrawListener listener = (ViewTreeObserver.OnPreDrawListener) albumViewHolder.title.getTag();
                albumViewHolder.title.getViewTreeObserver().removeOnPreDrawListener(listener);
            }
//            ViewTreeObserver.OnPreDrawListener listener = new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    Log.i("test", "albumViewHolder onPreDraw");
//                    Log.i("test", content.getTitle());
//                    ViewTreeObserver obs = albumViewHolder.title.getViewTreeObserver();
//                    obs.removeOnPreDrawListener(this);
//                    albumViewHolder.title.setTag(null);
////                    Log.i("test_width",""+albumViewHolder.layout.getWidth());
////                    Log.i("test_title",""+content.getTitle());
////                    Log.i("test_title_width",""+albumViewHolder.title.getWidth());
//
//                    if (albumViewHolder.title.getLineCount() > 2) {
////                        Log.i("test","getLineCount>2");
//                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
//                        layoutParams.topMargin = CommonUtils.dip2px(5);
//                        layoutParams.addRule(RelativeLayout.BELOW, R.id.logo);
//                        albumViewHolder.layout.setLayoutParams(layoutParams);
//                    } else {
//
//
//                        if (albumViewHolder.layout.getWidth() > albumViewHolder.title.getWidth()) {
////                            Log.i("test","layout.getWidth()>title.getWidth()");
//                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
//                            layoutParams.topMargin = CommonUtils.dip2px(5);
//                            layoutParams.addRule(RelativeLayout.BELOW, R.id.logo);
//                            albumViewHolder.layout.setLayoutParams(layoutParams);
//
//                        } else {
////                            Log.i("test","layout.getWidth()<title.getWidth()");
//                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
//                            layoutParams.topMargin = CommonUtils.dip2px(5);
//                            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.logo);
//                            albumViewHolder.layout.setLayoutParams(layoutParams);
//                        }
//
//                    }
//                    return true;
//                }
//            };
//            albumViewHolder.title.setTag(listener);
//            albumViewHolder.title.getViewTreeObserver().addOnPreDrawListener(listener);
            albumViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            if (content.getAlbums() != null && content.getAlbums().getStatus() == 1) {
                //已完结
                albumViewHolder.title.setText(CommonUtils.tagKeyword("完~" + content.getTitle(), "完~", CommonUtils.getThemeColor(mContext)));
            } else {
                albumViewHolder.title.setText(content.getTitle());
            }
            if (content.getAlbums() != null) {
                albumViewHolder.count.setText("" + content.getAlbums().getCount() + "集");
            }
            CommonUtils.setReadsAndComment(albumViewHolder.commentNum, content.getComment(), content.getView());
            albumViewHolder.title.post(new Runnable() {
                @Override
                public void run() {
                    int titleWidth = albumViewHolder.title.getWidth();
                    int layoutWidth = albumViewHolder.layout.getWidth();
                    int lineCount = albumViewHolder.title.getLineCount();
                    if (lineCount > 2) {
//                            Log.i("test","getLineCount>2");
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                        layoutParams.topMargin = CommonUtils.dip2px(5);
                        layoutParams.removeRule(RelativeLayout.ALIGN_BOTTOM);
                        layoutParams.addRule(RelativeLayout.BELOW, R.id.logo);
                        layoutParams.addRule(RelativeLayout.BELOW, R.id.title);
                        albumViewHolder.layout.setLayoutParams(layoutParams);
                    } else {
                        if (layoutWidth > titleWidth) {
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                            layoutParams.topMargin = CommonUtils.dip2px(5);
                            layoutParams.removeRule(RelativeLayout.ALIGN_BOTTOM);
                            layoutParams.addRule(RelativeLayout.BELOW, R.id.logo);
                            albumViewHolder.layout.setLayoutParams(layoutParams);
                        } else {
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                            layoutParams.topMargin = CommonUtils.dip2px(5);
                            layoutParams.removeRule(RelativeLayout.BELOW);
                            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.logo);
                            albumViewHolder.layout.setLayoutParams(layoutParams);
                        }
                    }
                }
            });
            albumViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, AlbumDetailActivity.class);
                    it.putExtra("albumId", content.getId());
                    mContext.startActivity(it);
                }
            });
        } else if (itemViewType == TYPE_ARTICLE) {
            final ArticleViewHolder articleViewHolder = (ArticleViewHolder) holder;
            articleViewHolder.excellent.setVisibility(View.GONE);
            if (content.getTop() != null) {
                articleViewHolder.top.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(content.getCover())) {
                    articleViewHolder.logo.setVisibility(View.VISIBLE);
                    int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                    int height = (int) (2f * width / 3f);
                    Uri uri = Uri.parse(content.getCover() + getOssResize(width, height));
                    ImageLoader.showThumb(uri, articleViewHolder.logo, width, height);
                } else {
                    articleViewHolder.logo.setVisibility(View.GONE);
                }
            } else {
                articleViewHolder.top.setVisibility(View.GONE);
                if (content.getArticle() != null && content.getArticle().getCovers().size() > 0 && !TextUtils.isEmpty(content.getArticle().getCovers().get(0))) {
                    articleViewHolder.logo.setVisibility(View.VISIBLE);
                    int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                    int height = (int) (2f * width / 3f);
                    Uri uri = Uri.parse(content.getArticle().getCovers().get(0) + getOssResize(width, height));
                    ImageLoader.showThumb(uri, articleViewHolder.logo, width, height);
                } else {
                    articleViewHolder.logo.setVisibility(View.GONE);
                }
            }
//            articleViewHolder.title.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    Log.i("test", "articleViewHolder onPreDraw");
//                    Log.i("test", content.getTitle());
//                    ViewTreeObserver obs = articleViewHolder.title.getViewTreeObserver();
//                    obs.removeOnPreDrawListener(this);
//
//                    if (articleViewHolder.logo.getVisibility() == View.GONE) {
////                            Log.i("test","articleViewHolder.logo=GONE");
//                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
//                        layoutParams.topMargin = CommonUtils.dip2px(10);
//                        layoutParams.addRule(RelativeLayout.BELOW, R.id.title);
//                        articleViewHolder.layout.setLayoutParams(layoutParams);
//
//                    } else {
//                        if (articleViewHolder.title.getLineCount() > 2) {
////                            Log.i("test","getLineCount>2");
//                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
//                            layoutParams.topMargin = CommonUtils.dip2px(5);
//                            layoutParams.addRule(RelativeLayout.BELOW, R.id.logo);
//                            articleViewHolder.layout.setLayoutParams(layoutParams);
//                        } else {
//
//                            if (articleViewHolder.layout.getWidth() > articleViewHolder.title.getWidth()) {
////                                Log.i("test","layout.getWidth()>title.getWidth()");
//                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
//                                layoutParams.topMargin = CommonUtils.dip2px(5);
//                                layoutParams.addRule(RelativeLayout.BELOW, R.id.logo);
//                                articleViewHolder.layout.setLayoutParams(layoutParams);
//
//                            } else {
////                                Log.i("test","layout.getWidth()<title.getWidth()");
//                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
//                                layoutParams.topMargin = CommonUtils.dip2px(5);
//                                layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.logo);
//                                articleViewHolder.layout.setLayoutParams(layoutParams);
//                            }
//
//                        }
//                    }
//
//
//                    return true;
//                }
//            });
            articleViewHolder.title.setText(content.getTitle());
            articleViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            CommonUtils.setReadsAndComment(articleViewHolder.commentNum, content.getComment(), content.getView());
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                articleViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }
            if (articleViewHolder.logo.getVisibility() == View.GONE) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                layoutParams.removeRule(RelativeLayout.ALIGN_BOTTOM);
                layoutParams.topMargin = CommonUtils.dip2px(10);
                layoutParams.addRule(RelativeLayout.BELOW, R.id.title);
                articleViewHolder.layout.setLayoutParams(layoutParams);
            } else {
                articleViewHolder.title.post(new Runnable() {
                    @Override
                    public void run() {
                        int titleWidth = articleViewHolder.title.getWidth();
                        int layoutWidth = articleViewHolder.layout.getWidth();
                        int lineCount = articleViewHolder.title.getLineCount();
                        if (lineCount > 2) {
//                            Log.i("test","getLineCount>2");
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                            layoutParams.topMargin = CommonUtils.dip2px(5);
                            layoutParams.removeRule(RelativeLayout.ALIGN_BOTTOM);
                            layoutParams.addRule(RelativeLayout.BELOW, R.id.logo);
                            layoutParams.addRule(RelativeLayout.BELOW, R.id.title);
                            articleViewHolder.layout.setLayoutParams(layoutParams);
                        } else {
                            if (layoutWidth > titleWidth) {
                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                                layoutParams.topMargin = CommonUtils.dip2px(5);
                                layoutParams.removeRule(RelativeLayout.ALIGN_BOTTOM);
                                layoutParams.addRule(RelativeLayout.BELOW, R.id.logo);
                                articleViewHolder.layout.setLayoutParams(layoutParams);
                            } else {
                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                                layoutParams.topMargin = CommonUtils.dip2px(5);
                                layoutParams.removeRule(RelativeLayout.BELOW);
                                layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.logo);
                                articleViewHolder.layout.setLayoutParams(layoutParams);
                            }
                        }
                    }
                });
            }
            articleViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, ArticleDetailActivity02.class);
                    it.putExtra("articleId", content.getId());
                    mContext.startActivity(it);
                }
            });
        } else if (itemViewType == TYPE_ARTICLE_THREE) {
            final ArticleViewThreeHolder articleViewHolder = (ArticleViewThreeHolder) holder;
            articleViewHolder.excellent.setVisibility(View.GONE);
            if (content.getTop() != null) {
                articleViewHolder.top.setVisibility(View.VISIBLE);
            } else {
                articleViewHolder.top.setVisibility(View.GONE);
            }
            if (content.getArticle().getCovers() != null && content.getArticle().getCovers().size() > 0 && !TextUtils.isEmpty(content.getArticle().getCovers().get(0))) {
                int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(30)) / 3;
                int height = (int) (2f * width / 3f);
                Uri uri = Uri.parse(content.getArticle().getCovers().get(0) + getOssResize(width, height));
                ImageLoader.showThumb(uri, articleViewHolder.pic1, width, height);
            } else {
                ImageLoader.showThumb(articleViewHolder.pic1, R.drawable.video_placeholder);
            }
            if (!TextUtils.isEmpty(content.getArticle().getCovers().get(1))) {
                int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(30)) / 3;
                int height = (int) (2f * width / 3f);
                Uri uri = Uri.parse(content.getArticle().getCovers().get(1) + getOssResize(width, height));
                ImageLoader.showThumb(uri, articleViewHolder.pic2, width, height);
            } else {
                ImageLoader.showThumb(articleViewHolder.pic2, R.drawable.video_placeholder);
            }
            if (!TextUtils.isEmpty(content.getArticle().getCovers().get(2))) {
                int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(30)) / 3;
                int height = (int) (2f * width / 3f);
                Uri uri = Uri.parse(content.getArticle().getCovers().get(2) + getOssResize(width, height));
                ImageLoader.showThumb(uri, articleViewHolder.pic3, width, height);
            } else {
                ImageLoader.showThumb(articleViewHolder.pic3, R.drawable.video_placeholder);
            }
            articleViewHolder.title.setText(content.getTitle());
            articleViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            CommonUtils.setReadsAndComment(articleViewHolder.commentNum, content.getComment(), content.getView());
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                articleViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }
            articleViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, ArticleDetailActivity02.class);
                    it.putExtra("articleId", content.getId());
                    mContext.startActivity(it);
                }
            });
        } else if (itemViewType == TYPE_VIDEO) {
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
            videoViewHolder.excellent.setVisibility(View.GONE);
            if (content.getTop() != null) {
                videoViewHolder.top.setVisibility(View.VISIBLE);
            } else {
                videoViewHolder.top.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(content.getCover())) {
                int width = CommonUtils.getScreenWidth() - CommonUtils.dip2px(20);
                int height = (int) (9f * width / 16f);
                Uri uri = Uri.parse(content.getCover() + getOssResize(width, height));
                ImageLoader.showThumb(uri, videoViewHolder.logo, width, height);
            } else {
                ImageLoader.showThumb(videoViewHolder.logo, R.drawable.video_placeholder);
            }
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                videoViewHolder.organization.setText(content.getAuthor_info().getMerchant().getName());
//                if (isVideo()) {
//                    if (!TextUtils.isEmpty(content.getAuthor_info().getMerchant().getLogo())) {
//                        Uri uri = Uri.parse(content.getAuthor_info().getMerchant().getLogo());
//                        int width = CommonUtils.dip2px(35);
//                        int height = width;
//                        ImageLoader.showThumb(uri, videoViewHolder.organizationLogo, width, height);
//                    }
//                }
            }
            if (content.getVideo() != null && !TextUtils.isEmpty(content.getVideo().getDuration())) {
                videoViewHolder.duration.setText(CommonUtils.getShowTime((long) Float.parseFloat(content.getVideo().getDuration()) * 1000));
            } else {
                videoViewHolder.duration.setText("00:00");
            }
            videoViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            videoViewHolder.title.setText(content.getTitle());
            CommonUtils.setReadsAndComment(videoViewHolder.commentNum, content.getComment(), content.getView());
            videoViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, VideoDetailActivity.class);
                    it.putExtra("videoId", content.getId());
                    mContext.startActivity(it);
                }
            });
        } else if (itemViewType == TYPE_GALLERIE_ONE) {
            GallerieOneViewHolder gallerieOneViewHolder = (GallerieOneViewHolder) holder;
            gallerieOneViewHolder.excellent.setVisibility(View.GONE);
            if (content.getTop() != null) {
                gallerieOneViewHolder.top.setVisibility(View.VISIBLE);
            } else {
                gallerieOneViewHolder.top.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(0))) {
                int width = CommonUtils.getScreenWidth();
                int height = (int) (9f * width / 16f);
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(0) + getOssResize(width, height));
                ImageLoader.showThumb(uri, gallerieOneViewHolder.logo, width, height);
            } else {
                ImageLoader.showThumb(gallerieOneViewHolder.logo, R.drawable.video_placeholder);
            }
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                gallerieOneViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }
            gallerieOneViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            gallerieOneViewHolder.title.setText(content.getTitle());
            CommonUtils.setReadsAndComment(gallerieOneViewHolder.commentNum, content.getComment(), content.getView());
            //gallerieOneViewHolder.count.setText(content.getGallerie().getCount() + "图");
            gallerieOneViewHolder.count.setText(content.getGallerie().getCount() + "");
            gallerieOneViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, GalleriaActivity.class);
                    it.putExtra("galleriaId", content.getId());
                    mContext.startActivity(it);
                }
            });
        } else if (itemViewType == TYPE_GALLERIE_THREE) {
            GallerieViewThreeHolder gallerieViewThreeHolder = (GallerieViewThreeHolder) holder;
            gallerieViewThreeHolder.excellent.setVisibility(View.GONE);
            if (content.getTop() != null) {
                gallerieViewThreeHolder.top.setVisibility(View.VISIBLE);
            } else {
                gallerieViewThreeHolder.top.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(0))) {
                int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(30)) / 3;
                int height = (int) (2f * width / 3f);
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(0) + getOssResize(width, height));
                ImageLoader.showThumb(uri, gallerieViewThreeHolder.pic1, width, height);
            } else {
                ImageLoader.showThumb(gallerieViewThreeHolder.pic1, R.drawable.video_placeholder);
            }
            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(1))) {
                int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(30)) / 3;
                int height = (int) (2f * width / 3f);
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(1) + getOssResize(width, height));
                ImageLoader.showThumb(uri, gallerieViewThreeHolder.pic2, width, height);
            } else {
                ImageLoader.showThumb(gallerieViewThreeHolder.pic2, R.drawable.video_placeholder);
            }
            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(2))) {
                int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(30)) / 3;
                int height = (int) (2f * width / 3f);
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(2) + getOssResize(width, height));
                ImageLoader.showThumb(uri, gallerieViewThreeHolder.pic3, width, height);
            } else {
                ImageLoader.showThumb(gallerieViewThreeHolder.pic3, R.drawable.video_placeholder);
            }
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                gallerieViewThreeHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }
            gallerieViewThreeHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            gallerieViewThreeHolder.title.setText(content.getTitle());
            CommonUtils.setReadsAndComment(gallerieViewThreeHolder.commentNum, content.getComment(), content.getView());
            //gallerieViewThreeHolder.count.setText(content.getGallerie().getCount() + "图");
            gallerieViewThreeHolder.count.setText(content.getGallerie().getCount() + "");
            gallerieViewThreeHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, GalleriaActivity.class);
                    it.putExtra("galleriaId", content.getId());
                    mContext.startActivity(it);
                }
            });
        } else if (itemViewType == TYPE_SPECIAL) {
            final SpecialViewHolder specialViewHolder = (SpecialViewHolder) holder;
            specialViewHolder.excellent.setVisibility(View.GONE);
            if (content.getTop() != null) {
                specialViewHolder.top.setVisibility(View.VISIBLE);
                specialViewHolder.specialIcon.setVisibility(View.GONE);
            } else {
                specialViewHolder.top.setVisibility(View.GONE);
                specialViewHolder.specialIcon.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(content.getCover())) {
                int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height = (int) (2f * width / 3f);
                Uri uri = Uri.parse(content.getCover() + getOssResize(width, height));
                ImageLoader.showThumb(uri, specialViewHolder.logo, width, height);
            } else {
                ImageLoader.showThumb(specialViewHolder.logo, R.drawable.article_placeholder);
            }
//            specialViewHolder.title.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    Log.i("test", "specialViewHolder onPreDraw");
//                    Log.i("test", content.getTitle());
//                    ViewTreeObserver obs = specialViewHolder.title.getViewTreeObserver();
//                    obs.removeOnPreDrawListener(this);
//
//                    if (specialViewHolder.logo.getVisibility() == View.GONE) {
//
//                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
//                        layoutParams.topMargin = CommonUtils.dip2px(10);
//                        layoutParams.addRule(RelativeLayout.BELOW, R.id.title);
//                        specialViewHolder.layout.setLayoutParams(layoutParams);
//
//                    } else {
//                        if (specialViewHolder.title.getLineCount() > 2) {
//                            Log.i("test", "getLineCount>2");
//                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
//                            layoutParams.topMargin = CommonUtils.dip2px(5);
//                            layoutParams.addRule(RelativeLayout.BELOW, R.id.logo);
//                            specialViewHolder.layout.setLayoutParams(layoutParams);
//                        } else {
//
//                            if (specialViewHolder.layout.getWidth() > specialViewHolder.title.getWidth()) {
//                                Log.i("test", "layout.getWidth()>title.getWidth()");
//                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
//                                layoutParams.topMargin = CommonUtils.dip2px(5);
//                                layoutParams.addRule(RelativeLayout.BELOW, R.id.logo);
//                                specialViewHolder.layout.setLayoutParams(layoutParams);
//
//                            } else {
//                                Log.i("test", "layout.getWidth()<title.getWidth()");
//                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
//                                layoutParams.topMargin = CommonUtils.dip2px(5);
//                                layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.logo);
//                                specialViewHolder.layout.setLayoutParams(layoutParams);
//                            }
//
//                        }
//                    }
//
//
//                    return true;
//                }
//            });
            specialViewHolder.title.setText(content.getTitle());
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                specialViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }
            specialViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            CommonUtils.setReadsAndComment(specialViewHolder.commentNum, content.getComment(), content.getView());
            specialViewHolder.title.post(new Runnable() {
                @Override
                public void run() {
                    int titleWidth = specialViewHolder.title.getWidth();
                    int layoutWidth = specialViewHolder.layout.getWidth();
                    int lineCount = specialViewHolder.title.getLineCount();
                    if (lineCount > 2) {
//                            Log.i("test","getLineCount>2");
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                        layoutParams.topMargin = CommonUtils.dip2px(5);
                        layoutParams.removeRule(RelativeLayout.ALIGN_BOTTOM);
                        layoutParams.addRule(RelativeLayout.BELOW, R.id.logo);
                        layoutParams.addRule(RelativeLayout.BELOW, R.id.title);
                        specialViewHolder.layout.setLayoutParams(layoutParams);
                    } else {
                        if (layoutWidth > titleWidth) {
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                            layoutParams.topMargin = CommonUtils.dip2px(5);
                            layoutParams.removeRule(RelativeLayout.ALIGN_BOTTOM);
                            layoutParams.addRule(RelativeLayout.BELOW, R.id.logo);
                            specialViewHolder.layout.setLayoutParams(layoutParams);
                        } else {
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                            layoutParams.topMargin = CommonUtils.dip2px(5);
                            layoutParams.removeRule(RelativeLayout.BELOW);
                            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.logo);
                            specialViewHolder.layout.setLayoutParams(layoutParams);
                        }
                    }
                }
            });
            specialViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, SpecialDetailActivity.class);
                    it.putExtra("specialId", content.getId());
                    mContext.startActivity(it);
                }
            });
        } else if (itemViewType == TYPE_ACTIVITY) {
            final ActivityViewHolder activityViewHolder = (ActivityViewHolder) holder;
            if (content.getTop() != null) {
                activityViewHolder.top.setVisibility(View.VISIBLE);
            } else {
                activityViewHolder.top.setVisibility(View.GONE);
            }
            if (isActivity()) {
                activityViewHolder.activity.setVisibility(View.GONE);
            } else {
                activityViewHolder.activity.setVisibility(View.VISIBLE);
            }
            activityViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            if (content.getActivitiy().getType() == 1) {
                GenericDraweeHierarchy hierarchy = activityViewHolder.logo.getHierarchy();
                hierarchy.setPlaceholderImage(R.drawable.activity_sign_up_logo);
            } else if (content.getActivitiy().getType() == 2) {
                GenericDraweeHierarchy hierarchy = activityViewHolder.logo.getHierarchy();
                hierarchy.setPlaceholderImage(R.drawable.activity_answer_logo);
            } else if (content.getActivitiy().getType() == 3) {
                GenericDraweeHierarchy hierarchy = activityViewHolder.logo.getHierarchy();
                hierarchy.setPlaceholderImage(R.drawable.activity_vote_logo);
            } else if (content.getActivitiy().getType() == 4) {
                GenericDraweeHierarchy hierarchy = activityViewHolder.logo.getHierarchy();
                hierarchy.setPlaceholderImage(R.drawable.activity_luck_logo);
            } else if (content.getActivitiy().getType() == 5) {
                GenericDraweeHierarchy hierarchy = activityViewHolder.logo.getHierarchy();
                hierarchy.setPlaceholderImage(R.drawable.activity_marathon_logo);
            } else if (content.getActivitiy().getType() == 6) {
                GenericDraweeHierarchy hierarchy = activityViewHolder.logo.getHierarchy();
                hierarchy.setPlaceholderImage(R.drawable.activity_interact_logo);
            }
            if (!TextUtils.isEmpty(content.getCover())) {
                int width = CommonUtils.getScreenWidth();
                int height = (int) (9f * width / 16f);
                Uri uri = Uri.parse(content.getCover() + getOssResize(width, height));
                ImageLoader.showThumb(uri, activityViewHolder.logo, width, height);
            } else {
                ImageLoader.showThumb(activityViewHolder.logo, R.drawable.video_placeholder);
            }
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                activityViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }
            activityViewHolder.title.setText(content.getTitle());
            if (content.getActivitiy() != null) {
                if (content.getActivitiy().getJoin_count() > 0) {
                    activityViewHolder.commentNum.setText(content.getActivitiy().getJoin_count() + "人参与");
                } else {
                    activityViewHolder.commentNum.setText("");
                }
            }
            activityViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, WebViewBackActivity.class);
                    it.putExtra("url", content.getActivitiy().getUrl());
                    it.putExtra("title", content.getTitle());
                    if (content.getActivitiy().getType() == 2) {
                        it.putExtra("activityId", content.getActivitiy().getId());
                    }
                    mContext.startActivity(it);
                }
            });
        } else if (itemViewType == TYPE_LIVE) {
            final LiveViewHolder liveViewHolder = (LiveViewHolder) holder;
            if (content.getTop() != null) {
                liveViewHolder.top.setVisibility(View.VISIBLE);
            } else {
                liveViewHolder.top.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(content.getCover())) {
                int width = CommonUtils.getScreenWidth();
                int height = (int) (9f * width / 16f);
                Uri uri = Uri.parse(content.getCover() + getOssResize(width, height));
                ImageLoader.showThumb(uri, liveViewHolder.logo, width, height);
            } else {
                ImageLoader.showThumb(liveViewHolder.logo, R.drawable.video_placeholder);
            }
            Glide.with(mContext).load(R.drawable.wave).into(liveViewHolder.statusIcon);
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                liveViewHolder.organization.setText(content.getAuthor_info().getMerchant().getName());
            }
            liveViewHolder.title.setText(content.getTitle());
            liveViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            liveViewHolder.popularity.setVisibility(View.GONE);
            if (content.getLive() != null) {
                liveViewHolder.popularity.setText(content.getLive().getPopularity() + "人气");
                if (content.getLive().getType() == 1) {
                    liveViewHolder.type.setText("网络");
                    liveViewHolder.typeIcon.setText(R.string.live_network);
                } else if (content.getLive().getType() == 2) {
                    liveViewHolder.type.setText("电台");
                    liveViewHolder.typeIcon.setText(R.string.live_radio);
                } else if (content.getLive().getType() == 3) {
                    liveViewHolder.type.setText("电视");
                    liveViewHolder.typeIcon.setText(R.string.live_tv);
                } else if (content.getLive().getType() == 4) {
                    liveViewHolder.type.setText("教育");
                    liveViewHolder.typeIcon.setText(R.string.live_shop);
                }
            }
            liveViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent it = new Intent(mContext, WebViewBackActivity.class);
//                    it.putExtra("url", content.getLive().getUrl());
//                    it.putExtra("title", content.getTitle());
//                    mContext.startActivity(it);
                    if (content.getLive() == null) {
                        com.hjq.toast.ToastUtils.show("数据异常");
                        return;
                    }
                    String url = content.getLive().getUrl();
                    if (content.getLive().getType() == 4 || content.getLive().getType() == 1) {
                        if (!TextUtils.isEmpty(url)) {
                            String streamName = url.substring(url.lastIndexOf("=") + 1);
                            Intent it = new Intent(mContext, LiveDetailActivity.class);
                            it.putExtra("streamName", streamName);
                            it.putExtra("roomId", content.getLive().getRoom_id());
                            //it.putExtra("url",content.getLive().getHls_play_url());
                            it.putExtra("url", content.getLive().getRtmp_play_url());
                            it.putExtra("type", content.getLive().getType());
                            mContext.startActivity(it);
                        }
                    } else {
                        Intent it = new Intent(mContext, WebViewBackActivity.class);
                        it.putExtra("url", content.getLive().getUrl());
                        it.putExtra("title", content.getTitle());
                        mContext.startActivity(it);
                    }
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

    public class DecorateViewHolder extends RecyclerView.ViewHolder {
        public DecorateViewHolder(View view) {
            super(view);
        }
    }

    public class BannerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.banner)
        BannerView banner;
        @BindView(R.id.root)
        RelativeLayout root;

        public BannerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.top)
        TextView top;
        @BindView(R.id.merchant)
        TextView merchant;
        @BindView(R.id.commentNum)
        TextView commentNum;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.root)
        LinearLayout root;
        @BindView(R.id.excellent)
        RelativeLayout excellent;
        @BindView(R.id.excellentLogo)
        SimpleDraweeView excellentLogo;

        public AudioViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.excellent)
        RelativeLayout excellent;
        @BindView(R.id.excellentLogo)
        SimpleDraweeView excellentLogo;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.top)
        TextView top;
        @BindView(R.id.merchant)
        TextView merchant;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.root)
        LinearLayout root;
        @BindView(R.id.commentNum)
        TextView commentNum;
        @BindView(R.id.layout)
        LinearLayout layout;

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
        @BindView(R.id.top)
        TextView top;
        @BindView(R.id.specialIcon)
        TextView specialIcon;
        @BindView(R.id.merchant)
        TextView merchant;
        @BindView(R.id.commentNum)
        TextView commentNum;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.root)
        LinearLayout root;
        @BindView(R.id.excellent)
        RelativeLayout excellent;
        @BindView(R.id.excellentLogo)
        SimpleDraweeView excellentLogo;
        @BindView(R.id.layout)
        LinearLayout layout;

        public SpecialViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.excellentLogo)
        SimpleDraweeView excellentLogo;
        @BindView(R.id.excellent)
        RelativeLayout excellent;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.top)
        TextView top;
        @BindView(R.id.activity)
        TextView activity;
        @BindView(R.id.merchant)
        TextView merchant;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.commentNum)
        TextView commentNum;
        @BindView(R.id.root)
        LinearLayout root;

        public ActivityViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class LiveViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.excellentLogo)
        SimpleDraweeView excellentLogo;
        @BindView(R.id.excellent)
        RelativeLayout excellent;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.playIcon)
        ImageView playIcon;
        @BindView(R.id.statusIcon)
        ImageView statusIcon;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.statusLayout)
        LinearLayout statusLayout;
        @BindView(R.id.popularity)
        TextView popularity;
        @BindView(R.id.typeIcon)
        FontIconView typeIcon;
        @BindView(R.id.type)
        TextView type;
        @BindView(R.id.organizationLogo)
        SimpleDraweeView organizationLogo;
        @BindView(R.id.top)
        TextView top;
        @BindView(R.id.organization)
        TextView organization;
        @BindView(R.id.commentNum)
        TextView commentNum;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.root)
        LinearLayout root;

        public LiveViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.top)
        TextView top;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.duration)
        TextView duration;
        @BindView(R.id.commentNum)
        TextView commentNum;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.root)
        LinearLayout root;
        @BindView(R.id.excellent)
        RelativeLayout excellent;
        @BindView(R.id.excellentLogo)
        SimpleDraweeView excellentLogo;
        @BindView(R.id.organization)
        TextView organization;
        @BindView(R.id.organizationLogo)
        SimpleDraweeView organizationLogo;

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
        @BindView(R.id.top)
        TextView top;
        @BindView(R.id.merchant)
        TextView merchant;
        @BindView(R.id.commentNum)
        TextView commentNum;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.root)
        LinearLayout root;
        @BindView(R.id.excellent)
        RelativeLayout excellent;
        @BindView(R.id.excellentLogo)
        SimpleDraweeView excellentLogo;

        public GallerieOneViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class GallerieViewThreeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.top)
        TextView top;
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
        RelativeLayout excellent;
        @BindView(R.id.excellentLogo)
        SimpleDraweeView excellentLogo;

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
        @BindView(R.id.top)
        TextView top;
        @BindView(R.id.merchant)
        TextView merchant;
        @BindView(R.id.commentNum)
        TextView commentNum;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.root)
        LinearLayout root;
        @BindView(R.id.excellent)
        RelativeLayout excellent;
        @BindView(R.id.excellentLogo)
        SimpleDraweeView excellentLogo;
        @BindView(R.id.layout)
        LinearLayout layout;

        public ArticleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class ArticleViewThreeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.top)
        TextView top;
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
        RelativeLayout excellent;
        @BindView(R.id.excellentLogo)
        SimpleDraweeView excellentLogo;

        public ArticleViewThreeHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mHeaderView != null) {
            count++;
        }
        if (mDecorateLayout != null) {
            count++;
        }
        if (mColumnContent != null) {
            count += mColumnContent.size();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView != null) {
            if (position == 0) {
                return TYPE_HEAD;
            } else if (mDecorateLayout != null) {
                if (position == 1) {
                    return TYPE_DECORATE;
                } else {
                    if (mColumnContent.get(position - 2).getType() == 1) {
                        return TYPE_AUDIO;
                    } else if (mColumnContent.get(position - 2).getType() == 2) {
                        return TYPE_ALBUM;
                    } else if (mColumnContent.get(position - 2).getType() == 3) {
                        if (mColumnContent.get(position - 2).getArticle() == null) {
                            return TYPE_ARTICLE;
                        } else if (mColumnContent.get(position - 2).getArticle().getType() == 1) {
                            return TYPE_ARTICLE;
                        } else if (mColumnContent.get(position - 2).getArticle().getType() == 2) {
                            return TYPE_ARTICLE_THREE;
                        } else {
                            if (mColumnContent.get(position - 2).getArticle().getCovers().size() > 2) {
                                return TYPE_ARTICLE_THREE;
                            } else {
                                return TYPE_ARTICLE;
                            }
                        }
                    } else if (mColumnContent.get(position - 2).getType() == 4) {
                        return TYPE_VIDEO;
                    } else if (mColumnContent.get(position - 2).getType() == 5) {
                        return TYPE_SPECIAL;
                    } else if (mColumnContent.get(position - 2).getType() == 7) {
                        if (mColumnContent.get(position - 2).getGallerie().getType() == 1) {
                            return TYPE_GALLERIE_ONE;
                        } else if (mColumnContent.get(position - 2).getGallerie().getType() == 2) {
                            return TYPE_GALLERIE_THREE;
                        } else {
                            if (mColumnContent.get(position - 2).getGallerie().getCovers().size() < 3) {
                                return TYPE_GALLERIE_ONE;
                            } else {
                                return TYPE_GALLERIE_THREE;
                            }
                        }
                    } else if (mColumnContent.get(position - 2).getType() == 9) {
                        return TYPE_ACTIVITY;
                    } else {
                        return TYPE_LIVE;
                    }
                }
            } else {
                if (mColumnContent.get(position - 1).getType() == 1) {
                    return TYPE_AUDIO;
                } else if (mColumnContent.get(position - 1).getType() == 2) {
                    return TYPE_ALBUM;
                } else if (mColumnContent.get(position - 1).getType() == 3) {
                    if (mColumnContent.get(position - 1).getArticle() == null) {
                        return TYPE_ARTICLE;
                    } else if (mColumnContent.get(position - 1).getArticle().getType() == 1) {
                        return TYPE_ARTICLE;
                    } else if (mColumnContent.get(position - 1).getArticle().getType() == 2) {
                        return TYPE_ARTICLE_THREE;
                    } else {
                        if (mColumnContent.get(position - 1).getArticle().getCovers().size() > 2) {
                            return TYPE_ARTICLE_THREE;
                        } else {
                            return TYPE_ARTICLE;
                        }
                    }
                } else if (mColumnContent.get(position - 1).getType() == 4) {
                    return TYPE_VIDEO;
                } else if (mColumnContent.get(position - 1).getType() == 5) {
                    return TYPE_SPECIAL;
                } else if (mColumnContent.get(position - 1).getType() == 7) {
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
                } else if (mColumnContent.get(position - 1).getType() == 9) {
                    return TYPE_ACTIVITY;
                } else {
                    return TYPE_LIVE;
                }
            }
        } else {
            if (mDecorateLayout != null) {
                if (position == 0) {
                    return TYPE_DECORATE;
                } else {
                    if (mColumnContent.get(position - 1).getType() == 1) {
                        return TYPE_AUDIO;
                    } else if (mColumnContent.get(position - 1).getType() == 2) {
                        return TYPE_ALBUM;
                    } else if (mColumnContent.get(position - 1).getType() == 3) {
                        if (mColumnContent.get(position - 1).getTop() != null) {
                            return TYPE_ARTICLE;
                        } else {
                            if (mColumnContent.get(position - 1).getArticle() == null) {
                                return TYPE_ARTICLE;
                            } else if (mColumnContent.get(position - 1).getArticle().getType() == 1) {
                                return TYPE_ARTICLE;
                            } else if (mColumnContent.get(position - 1).getArticle().getType() == 2) {
                                return TYPE_ARTICLE_THREE;
                            } else {
                                if (mColumnContent.get(position - 1).getArticle().getCovers().size() > 2) {
                                    return TYPE_ARTICLE_THREE;
                                } else {
                                    return TYPE_ARTICLE;
                                }
                            }
                        }
                    } else if (mColumnContent.get(position - 1).getType() == 4) {
                        return TYPE_VIDEO;
                    } else if (mColumnContent.get(position - 1).getType() == 5) {
                        return TYPE_SPECIAL;
                    } else if (mColumnContent.get(position - 1).getType() == 7) {
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
                    } else if (mColumnContent.get(position - 1).getType() == 9) {
                        return TYPE_ACTIVITY;
                    } else {
                        return TYPE_LIVE;
                    }
                }
            } else {
                if (mColumnContent.get(position).getType() == 1) {
                    return TYPE_AUDIO;
                } else if (mColumnContent.get(position).getType() == 2) {
                    return TYPE_ALBUM;
                } else if (mColumnContent.get(position).getType() == 3) {
                    if (mColumnContent.get(position).getTop() != null) {
                        return TYPE_ARTICLE;
                    } else {
                        if (mColumnContent.get(position).getArticle() == null) {
                            return TYPE_ARTICLE;
                        } else if (mColumnContent.get(position).getArticle().getType() == 1) {
                            return TYPE_ARTICLE;
                        } else if (mColumnContent.get(position).getArticle().getType() == 2) {
                            return TYPE_ARTICLE_THREE;
                        } else {
                            if (mColumnContent.get(position).getArticle().getCovers().size() > 2) {
                                return TYPE_ARTICLE_THREE;
                            } else {
                                return TYPE_ARTICLE;
                            }
                        }
                    }
                } else if (mColumnContent.get(position).getType() == 4) {
                    return TYPE_VIDEO;
                } else if (mColumnContent.get(position).getType() == 5) {
                    return TYPE_SPECIAL;
                } else if (mColumnContent.get(position).getType() == 7) {
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
                } else if (mColumnContent.get(position).getType() == 9) {
                    return TYPE_ACTIVITY;
                } else {
                    return TYPE_LIVE;
                }
            }
        }
    }
}
