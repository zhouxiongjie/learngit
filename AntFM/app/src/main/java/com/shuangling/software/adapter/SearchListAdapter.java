package com.shuangling.software.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.activity.AnchorDetailActivity;
import com.shuangling.software.activity.ArticleDetailActivity;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.activity.RadioDetailActivity;
import com.shuangling.software.activity.SpecialDetailActivity;
import com.shuangling.software.activity.TvDetailActivity;
import com.shuangling.software.activity.VideoDetailActivity;
import com.shuangling.software.entity.SearchResult;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
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


    private Context mContext;
    private List<SearchResult> mSearchResults;
    private LayoutInflater inflater;


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
        } else if (viewType == TYPE_SPECIAL) {
            return new SpecialViewHolder(inflater.inflate(R.layout.search_special_item, parent, false));
        } else if (viewType == TYPE_RADIO) {
            return new RadioViewHolder(inflater.inflate(R.layout.search_radio_item, parent, false));
        } else if (viewType == TYPE_ANCHOR) {
            return new AnchorViewHolder(inflater.inflate(R.layout.search_anchor_item, parent, false));
        } else {
            return new TvViewHolder(inflater.inflate(R.layout.search_radio_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final SearchResult content = mSearchResults.get(position);
        if (getItemViewType(position) == TYPE_AUDIO) {
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
                    Intent it = new Intent(mContext,AudioDetailActivity.class);
                    it.putExtra("audioId", content.getId());
                    mContext.startActivity(it);
                }
            });

        } else if (getItemViewType(position) == TYPE_ALBUM) {
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

        } else if (getItemViewType(position) == TYPE_ARTICLE) {
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
                    Intent it = new Intent(mContext, ArticleDetailActivity.class);
                    it.putExtra("articleId", content.getId());
                    mContext.startActivity(it);
                }
            });
        } else if (getItemViewType(position) == TYPE_VIDEO) {
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.dip2px(70);
                int height = width;
                ImageLoader.showThumb(uri, videoViewHolder.logo, width, height);
            }
            videoViewHolder.title.setText(content.getTitle());
            if(!TextUtils.isEmpty(content.getDuration())){
                videoViewHolder.duration.setText(CommonUtils.getShowTime((long) Float.parseFloat(content.getDuration())));
            }else{
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

        } else if (getItemViewType(position) == TYPE_SPECIAL) {
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


        } else if (getItemViewType(position) == TYPE_RADIO) {
            RadioViewHolder radioViewHolder = (RadioViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.dip2px(70);
                int height = width;
                ImageLoader.showThumb(uri, radioViewHolder.logo, width, height);
            }
            radioViewHolder.title.setText(content.getName());
            radioViewHolder.name.setText("正在直播:"+content.getDes());
            radioViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent it=new Intent(mContext,RadioDetailActivity.class);
                    it.putExtra("radioId",content.getId());
                    mContext.startActivity(it);
                }
            });


        } else if (getItemViewType(position) == TYPE_ANCHOR) {
            AnchorViewHolder anchorViewHolder = (AnchorViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.dip2px(70);
                int height = width;
                ImageLoader.showThumb(uri, anchorViewHolder.logo, width, height);
            }

            anchorViewHolder.title.setText(content.getName());
            anchorViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it=new Intent(mContext,AnchorDetailActivity.class);
                    it.putExtra("anchorId",content.getId());
                    mContext.startActivity(it);
                }
            });

        } else {
            TvViewHolder tvViewHolder = (TvViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.dip2px(70);
                int height = width;
                ImageLoader.showThumb(uri, tvViewHolder.logo, width, height);
            }

            tvViewHolder.title.setText(content.getName());
            tvViewHolder.name.setText("正在直播:"+content.getDes());
            tvViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it=new Intent(mContext,TvDetailActivity.class);
                    it.putExtra("radioId",content.getId());
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


    public class AnchorViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.root)
        RelativeLayout root;

        public AnchorViewHolder(View view) {
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
        } else if (mSearchResults.get(position).getSearch_type() == 7) {
            return TYPE_TV;
        } else {
            return TYPE_SPECIAL;
        }

    }


}
