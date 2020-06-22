package com.shuangling.software.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.vodplayerview.utils.ScreenUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.activity.AlivcLittleVideoActivity;
import com.shuangling.software.activity.ArticleDetailActivity02;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.activity.RadioDetailActivity;
import com.shuangling.software.activity.SpecialDetailActivity;
import com.shuangling.software.activity.TvDetailActivity;
import com.shuangling.software.activity.VideoDetailActivity;
import com.shuangling.software.activity.WebViewActivity;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.SearchResult;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.NumberUtil;
import com.shuangling.software.utils.ServerInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class SearchListAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    //"type": 4,//1 音频 2 专辑 3 文章 4 视频 5专题

    public static final int TYPE_AUDIO = 0;             //音频
    public static final int TYPE_ALBUM = 1;             //专辑
    public static final int TYPE_ARTICLE = 2;           //文章
    public static final int TYPE_VIDEO = 3;             //视频
    public static final int TYPE_SPECIAL = 4;           //专题
    public static final int TYPE_RADIO = 5;             //电台
    public static final int TYPE_ANCHOR = 6;            //主播
    public static final int TYPE_TV = 7;                //电视台
    public static final int TYPE_GALLERIE = 8;          //图集
    public static final int TYPE_ORGANIZATION = 9;       //机构
    public static final int TYPE_LITTLE_VIDEO = 11;     //短视频
    private Context mContext;
    private List<SearchResult> mSearchResults;
    private LayoutInflater inflater;

    private int mSearchType; //当前正在搜索的类型 由于 视频 /短视频 两个其实是同一台，加一个搜索参数区分

    public void setSearchType(int searchType) {
        this.mSearchType = searchType;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, SearchResult content);

        void onItemClick(int pos);
    }


    public SearchListAdapter(Context context, List<SearchResult> searchResults) {
        this.mContext = context;
        this.mSearchResults = searchResults;
        inflater = LayoutInflater.from(mContext);

    }

    public void setData(List<SearchResult> searchResults) {
        this.mSearchResults = searchResults;

    }

    public List<SearchResult> getData() {
        return this.mSearchResults;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if (viewType == TYPE_AUDIO) {
            return new AudioViewHolder(inflater.inflate(R.layout.search_audio_item, parent, false));
        } else if (viewType == TYPE_ALBUM) {
            return new AlbumViewHolder(inflater.inflate(R.layout.search_album_item, parent, false));
        } else if (viewType == TYPE_ARTICLE) {
            return new ArticleViewHolder(inflater.inflate(R.layout.search_article_item, parent, false));
        } else if (viewType == TYPE_VIDEO) {
            return new VideoViewHolder(inflater.inflate(R.layout.search_video_item, parent, false));
        } else if (viewType == TYPE_LITTLE_VIDEO) {
            if (mSearchType != R.string.little_video) {
                //综合搜索显示的是视频的视图
                return new VideoViewHolder(inflater.inflate(R.layout.search_video_item, parent, false));
            }else{
                return new LittleVideoViewHolder(inflater.inflate(R.layout.small_video_recycler_view_item, parent, false));
            }

        } else if (viewType == TYPE_SPECIAL) {
            return new SpecialViewHolder(inflater.inflate(R.layout.search_special_item, parent, false));
        } else if (viewType == TYPE_RADIO) {
            return new RadioViewHolder(inflater.inflate(R.layout.search_radio_item, parent, false));
        } else if (viewType == TYPE_ANCHOR) {
            return new AnchorViewHolder(inflater.inflate(R.layout.search_anchor_item, parent, false));
        } else if (viewType == TYPE_TV) {
            return new TvViewHolder(inflater.inflate(R.layout.search_radio_item, parent, false));
        } else if (viewType == TYPE_GALLERIE) {
            return new GallerieViewHolder(inflater.inflate(R.layout.history_gallerie_item, parent, false));
        } else {
            return new OrganizationViewHolder(inflater.inflate(R.layout.search_anchor_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final SearchResult content = mSearchResults.get(position);
        int itemViewType = getItemViewType(position);
        if (itemViewType == TYPE_AUDIO) {
            AudioViewHolder audioViewHolder = (AudioViewHolder) holder;
            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.dip2px(70);
                int height = width;
                ImageLoader.showThumb(uri, audioViewHolder.logo, width, height);
            }
            //audioViewHolder.name.setText(content.getName());
            audioViewHolder.duration.setText(CommonUtils.getShowTime((long) Float.parseFloat(content.getDuration())));
            audioViewHolder.title.setText(content.getTitle());
            audioViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, AudioDetailActivity.class);
                    it.putExtra("audioId", content.getId());
                    mContext.startActivity(it);
                }
            });

        } else if (itemViewType == TYPE_ALBUM) {
            AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;
            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.dip2px(70);
                int height = width;
                ImageLoader.showThumb(uri, albumViewHolder.logo, width, height);
            }
            albumViewHolder.title.setText(content.getTitle());
            albumViewHolder.name.setText(content.getDes());
            albumViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, AlbumDetailActivity.class);
                    it.putExtra("albumId", content.getId());
                    mContext.startActivity(it);
                }
            });

        } else if (itemViewType == TYPE_ARTICLE) {
            ArticleViewHolder articleViewHolder = (ArticleViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.dip2px(70);
                int height = width;
                ImageLoader.showThumb(uri, articleViewHolder.logo, width, height);
            }
            articleViewHolder.title.setText(content.getTitle());
            articleViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, ArticleDetailActivity02.class);
                    it.putExtra("articleId", content.getId());
                    mContext.startActivity(it);
                }
            });
        } else if (itemViewType == TYPE_VIDEO) {

            //视频
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.dip2px(70);
                int height = width;
                ImageLoader.showThumb(uri, videoViewHolder.logo, width, height);
            }
            videoViewHolder.title.setText(content.getTitle());
            if (!TextUtils.isEmpty(content.getDuration())) {
                videoViewHolder.duration.setText(CommonUtils.getShowTime((long) Float.parseFloat(content.getDuration())));
            } else {
                videoViewHolder.duration.setText("00:00");
            }
            videoViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, VideoDetailActivity.class);
                    it.putExtra("videoId", content.getId());
                    mContext.startActivity(it);
                }
            });



        }else if (itemViewType == TYPE_LITTLE_VIDEO) { //短视频
            //当前搜索的是全部，显示为视频单元
            if (mSearchType != R.string.little_video) {
                //视频
                VideoViewHolder videoViewHolder = (VideoViewHolder) holder;

                if (!TextUtils.isEmpty(content.getCover())) {
                    Uri uri = Uri.parse(content.getCover());
                    int width = CommonUtils.dip2px(70);
                    int height = width;
                    ImageLoader.showThumb(uri, videoViewHolder.logo, width, height);
                }
                videoViewHolder.title.setText(content.getTitle());
                if (!TextUtils.isEmpty(content.getDuration())) {
                    videoViewHolder.duration.setText(CommonUtils.getShowTime((long) Float.parseFloat(content.getDuration())));
                } else {
                    videoViewHolder.duration.setText("00:00");
                }
                videoViewHolder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, AlivcLittleVideoActivity.class);
                        intent.putExtra("startType",  AlivcLittleVideoActivity.START_TYPE_H5_WEBVIEW_CURRENT);
                        intent.putExtra("original_id",  content.getId());
                        intent.putExtra("play_id",  content.getId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);

                    }
                });
            } else {//选的是短视频分类

                LittleVideoViewHolder videoViewHolder = (LittleVideoViewHolder) holder;

                if (!TextUtils.isEmpty(content.getCover())) {
                    Uri uri = Uri.parse(content.getCover());
                    int width = CommonUtils.dip2px(160);
                    int height = width;
                    ImageLoader.showThumb(uri, videoViewHolder.mIvSmallVideoFacePic, width, height);
                }
                videoViewHolder.mVideoTitle.setText(content.getTitle());

                //作者信息
                if(content.getMerchant() != null){
                    if (!TextUtils.isEmpty(content.getMerchant().getLogo())) {
                        Uri uri = Uri.parse(content.getMerchant().getLogo());
                        int width = CommonUtils.dip2px(20);
                        int height = width;
                        ImageLoader.showThumb(uri, videoViewHolder.mIvAuthorLogo, width, height);
                    }
                    videoViewHolder.mTvAuthorName.setText(content.getMerchant().getName());
                }

                //播放次数
                if(content.getPlays()>0 ) {
                    videoViewHolder.mTvViews.setText(NumberUtil.formatNum(content.getPlays() + "",false)+ "次播放");
                    videoViewHolder.mTvViews.setVisibility(View.VISIBLE);
                }else{
                    videoViewHolder.mTvViews.setText( "");
                    videoViewHolder.mTvViews.setVisibility(View.GONE);
                }

                videoViewHolder.mIvSmallVideoFacePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<ColumnContent> columnContents = new ArrayList<ColumnContent>();
                        for(int i=0;i<mSearchResults.size();i++){
                            SearchResult content = mSearchResults.get(i);
                            ColumnContent columnContent = new ColumnContent();
                            columnContent.setId(content.getId());
                            columnContent.setTitle(content.getTitle());
                            columnContent.setCover(content.getCover());
                            columnContent.setView(content.getPlays());
                            ColumnContent.AuthorInfoBean.MerchantBean merchantBean = new ColumnContent.AuthorInfoBean.MerchantBean();
                            ColumnContent.VideoBean videoBean = new ColumnContent.VideoBean();
                            videoBean.setVideo_id(content.getVideo_id());
                            videoBean.setDuration(content.getDuration());
                            columnContent.setVideo(videoBean);
                            ColumnContent.AuthorInfoBean authorInfoBean = new ColumnContent.AuthorInfoBean();
                            if(content.getMerchant() != null){
                                merchantBean.setId(content.getMerchant().getId());
                                merchantBean.setName(content.getMerchant().getName());
                                merchantBean.setLogo(content.getMerchant().getLogo());
                            }
                            authorInfoBean.setMerchant(merchantBean);
                            columnContent.setAuthor_info(authorInfoBean);
                            columnContents.add(columnContent);
                        }

                        Intent intent = new Intent(mContext, AlivcLittleVideoActivity.class);
                        intent.putExtra("startType",  AlivcLittleVideoActivity.START_TYPE_H5_WEBVIEW);
                        intent.putExtra("littleVideos", (Serializable) columnContents);
                        intent.putExtra("position",  position);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);

                    }
                });

            }
        } else if (itemViewType == TYPE_SPECIAL) {
            SpecialViewHolder specialViewHolder = (SpecialViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.dip2px(70);
                int height = width;
                ImageLoader.showThumb(uri, specialViewHolder.logo, width, height);
            }
            specialViewHolder.merchant.setText(content.getName());
            specialViewHolder.title.setText(content.getTitle());
            specialViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, SpecialDetailActivity.class);
                    it.putExtra("specialId", content.getId());
                    mContext.startActivity(it);
                }
            });


        } else if (itemViewType == TYPE_RADIO) {
            RadioViewHolder radioViewHolder = (RadioViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.dip2px(70);
                int height = width;
                ImageLoader.showThumb(uri, radioViewHolder.logo, width, height);
            }
            radioViewHolder.title.setText(content.getName());
            radioViewHolder.name.setText("正在直播:" + content.getDes());
            radioViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent it = new Intent(mContext, RadioDetailActivity.class);
                    it.putExtra("radioId", content.getId());
                    mContext.startActivity(it);
                }
            });


        } else if (itemViewType == TYPE_ANCHOR) {
            AnchorViewHolder anchorViewHolder = (AnchorViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.dip2px(70);
                int height = width;
                ImageLoader.showThumb(uri, anchorViewHolder.logo, width, height);
            }

            anchorViewHolder.title.setText(content.getName());
            anchorViewHolder.fanNum.setText(content.getFollows() + "粉丝");
            if (content.getIs_follow() == 0) {
                anchorViewHolder.attention.setActivated(true);
                anchorViewHolder.attention.setText("关注");
            } else {
                anchorViewHolder.attention.setActivated(false);
                anchorViewHolder.attention.setText("已关注");
            }
            anchorViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent it = new Intent(mContext, AnchorDetailActivity.class);
//                    it.putExtra("anchorId", content.getId());
//                    mContext.startActivity(it);
                    Intent it = new Intent(mContext, WebViewActivity.class);
                    it.putExtra("url", ServerInfo.h5HttpsIP + "/anchors/" + content.getId());
                    mContext.startActivity(it);
                }
            });

        } else if (itemViewType == TYPE_GALLERIE) {
            final GallerieViewHolder gallerieViewHolder = (GallerieViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, gallerieViewHolder.logo, width, height);
            }


            gallerieViewHolder.title.setText(content.getTitle());
            gallerieViewHolder.timePrefix.setVisibility(View.INVISIBLE);
            gallerieViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent it = new Intent(mContext, GalleriaActivity.class);
                    it.putExtra("galleriaId", content.getId());
                    mContext.startActivity(it);
                }
            });

        } else if (itemViewType == TYPE_TV) {
            TvViewHolder tvViewHolder = (TvViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.dip2px(70);
                int height = width;
                ImageLoader.showThumb(uri, tvViewHolder.logo, width, height);
            }

            tvViewHolder.title.setText(content.getName());
            tvViewHolder.name.setText("正在直播:" + content.getDes());
            tvViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, TvDetailActivity.class);
                    it.putExtra("radioId", content.getId());
                    mContext.startActivity(it);
                }
            });

        } else {
            OrganizationViewHolder organizationViewHolder = (OrganizationViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.dip2px(70);
                int height = width;
                ImageLoader.showThumb(uri, organizationViewHolder.logo, width, height);
            }

            organizationViewHolder.title.setText(content.getName());
            organizationViewHolder.fanNum.setText(content.getFollows() + "粉丝");
            if (content.getIs_follow() == 0) {
                organizationViewHolder.attention.setActivated(true);
                organizationViewHolder.attention.setText("关注");
            } else {
                organizationViewHolder.attention.setActivated(false);
                organizationViewHolder.attention.setText("已关注");
            }
            organizationViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent it = new Intent(mContext, OrganizationDetailActivity.class);
//                    it.putExtra("organizationId", content.getId());
//                    mContext.startActivity(it);

                    Intent it = new Intent(mContext, WebViewActivity.class);
                    it.putExtra("url", ServerInfo.h5HttpsIP + "/orgs/" + content.getId());
                    mContext.startActivity(it);
                }
            });

        }
    }


    @Override
    public int getItemCount() {

        if (mSearchResults != null) {
            return mSearchResults.size();
        } else {
            return 0;
        }


    }

    @Override
    public void onClick(View v) {

    }


    public class AudioViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.duration)
        TextView duration;
        @BindView(R.id.root)
        RelativeLayout root;

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
        @BindView(R.id.name)
        TextView name;
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
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.duration)
        TextView duration;
        @BindView(R.id.root)
        RelativeLayout root;

        public VideoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    //短视频
    public class LittleVideoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_small_video_face_pic)
        SimpleDraweeView mIvSmallVideoFacePic;
        @BindView(R.id.video_title)
        TextView mVideoTitle;
        @BindView(R.id.iv_author_logo)
        SimpleDraweeView mIvAuthorLogo;
        @BindView(R.id.tv_author_name)
        TextView mTvAuthorName;

        @BindView(R.id.tv_view)
        TextView mTvViews;

        public LittleVideoViewHolder(View view) {
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

    public class RadioViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.root)
        RelativeLayout root;

        public RadioViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public class TvViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.root)
        RelativeLayout root;

        public TvViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public class GallerieViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.checkBox)
        CheckBox checkBox;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.logoLayout)
        RelativeLayout logoLayout;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.root)
        LinearLayout root;
        @BindView(R.id.timePrefix)
        TextView timePrefix;


        public GallerieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public class AnchorViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.fanNum)
        TextView fanNum;
        @BindView(R.id.attention)
        TextView attention;
        @BindView(R.id.root)
        RelativeLayout root;

        public AnchorViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public class OrganizationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.fanNum)
        TextView fanNum;
        @BindView(R.id.attention)
        TextView attention;
        @BindView(R.id.root)
        RelativeLayout root;

        public OrganizationViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public int getItemViewType(int position) {

        if (mSearchResults.get(position).getSearch_type() == 1) {
            return TYPE_AUDIO;
        } else if (mSearchResults.get(position).getSearch_type() == 2) {
            return TYPE_ARTICLE;
        } else if (mSearchResults.get(position).getSearch_type() == 3) {
            return TYPE_ALBUM;
        } else if (mSearchResults.get(position).getSearch_type() == 4) {
            return TYPE_RADIO;
        } else if (mSearchResults.get(position).getSearch_type() == 5) {
            return TYPE_ANCHOR;
        } else if (mSearchResults.get(position).getSearch_type() == 6) {
            return TYPE_VIDEO;
        } else if (mSearchResults.get(position).getSearch_type() == 11) {
            return TYPE_LITTLE_VIDEO;
        } else if (mSearchResults.get(position).getSearch_type() == 7) {
            return TYPE_TV;
        } else if (mSearchResults.get(position).getSearch_type() == 8) {
            return TYPE_SPECIAL;
        } else if (mSearchResults.get(position).getSearch_type() == 9) {
            return TYPE_GALLERIE;
        } else {
            return TYPE_ORGANIZATION;
        }

    }


}
