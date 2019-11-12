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
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.activity.ArticleDetailActivity;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.activity.SpecialDetailActivity;
import com.shuangling.software.activity.VideoDetailActivity;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.Station;
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
public class IndexDecorVideoContentAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    //"type": 4,//1 音频 2 专辑 3 文章 4 视频 5专题

    public static final int TYPE_AUDIO = 0;             //音频
    public static final int TYPE_ALBUM = 1;             //专辑
    public static final int TYPE_ARTICLE = 2;           //文章
    public static final int TYPE_ARTICLE_THREE = 3;     //文章
    public static final int TYPE_VIDEO = 4;             //视频
    public static final int TYPE_SPECIAL = 5;           //专题
    public static final int TYPE_GALLERIE_ONE = 6;      //一图集
    public static final int TYPE_GALLERIE_THREE = 7;    //三图集
    public static final int TYPE_HEAD = 8;              //头

    private Context mContext;
    private List<ColumnContent> mColumnContent;
    private LayoutInflater inflater;
    private View mHeaderView;
    private boolean mIsHot = false;

    private boolean mIsVideo = false;

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

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, ColumnContent content);

        void onItemClick(View view, int pos);
    }


    public IndexDecorVideoContentAdapter(Context context, List<ColumnContent> columnContent) {
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
        }  else if (viewType == TYPE_ARTICLE_THREE) {
            return new ArticleViewThreeHolder(inflater.inflate(R.layout.content_article_three_item, parent, false));
        } else if (viewType == TYPE_VIDEO) {
            if (isVideo()) {
                return new VideoViewHolder(inflater.inflate(R.layout.content_video_item_one, parent, false));
            } else {
                return new VideoViewHolder(inflater.inflate(R.layout.content_video_item, parent, false));
            }

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
        boolean showExcellent = false;
        if (mHeaderView != null) {
            if (position == 0) {
                content = null;
            } else {
                content = mColumnContent.get(position - 1);
//                if (isIsHot()) {
//                    if (position - 2 > 0) {
//                        if (mColumnContent.get(position - 2).getTop() != null) {
//                            showExcellent = true;
//                        }
//                    }
//                }
            }

        } else {
            content = mColumnContent.get(position);
//            if (isIsHot()) {
//                if (position - 1 > 0) {
//                    if (mColumnContent.get(position - 1).getTop() != null) {
//                        showExcellent = true;
//                    }
//                }
//            }
        }

        int itemViewType=getItemViewType(position);
        if ( itemViewType== TYPE_HEAD) {

        } else if (itemViewType == TYPE_AUDIO) {
            AudioViewHolder audioViewHolder = (AudioViewHolder) holder;
            if (showExcellent) {
                audioViewHolder.excellent.setVisibility(View.VISIBLE);
                Station station=MyApplication.getInstance().getStation();
                if(station!=null&&!TextUtils.isEmpty(station.getIcon2())){
                    Uri uri = Uri.parse(station.getIcon2());
                    int width=CommonUtils.dip2px(15);
                    int height=width;
                    ImageLoader.showThumb(uri,audioViewHolder.excellentLogo,width,height);
                }else{
                    audioViewHolder.excellentLogo.setVisibility(View.GONE);
                }

            } else {
                audioViewHolder.excellent.setVisibility(View.GONE);
            }

            if(content.getTop()!=null){
                audioViewHolder.top.setVisibility(View.VISIBLE);
            }else{
                audioViewHolder.top.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.dip2px(100);
                int height = width;
                ImageLoader.showThumb(uri, audioViewHolder.logo, width, height);
            }else{
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
            if (showExcellent) {
                albumViewHolder.excellent.setVisibility(View.VISIBLE);
                Station station=MyApplication.getInstance().getStation();
                if(station!=null&&!TextUtils.isEmpty(station.getIcon2())){
                    Uri uri = Uri.parse(station.getIcon2());
                    int width=CommonUtils.dip2px(15);
                    int height=width;
                    ImageLoader.showThumb(uri,albumViewHolder.excellentLogo,width,height);
                }else{
                    albumViewHolder.excellentLogo.setVisibility(View.GONE);
                }
            } else {
                albumViewHolder.excellent.setVisibility(View.GONE);
            }

            if(content.getTop()!=null){
                albumViewHolder.top.setVisibility(View.VISIBLE);
            }else{
                albumViewHolder.top.setVisibility(View.GONE);
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

            if(albumViewHolder.title.getTag()!=null){
                ViewTreeObserver.OnPreDrawListener listener =(ViewTreeObserver.OnPreDrawListener)albumViewHolder.title.getTag();
                albumViewHolder.title.getViewTreeObserver().removeOnPreDrawListener(listener);
            }

            ViewTreeObserver.OnPreDrawListener listener=new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    Log.i("test","albumViewHolder onPreDraw");
                    Log.i("test",content.getTitle());
                    ViewTreeObserver obs = albumViewHolder.title.getViewTreeObserver();
                    obs.removeOnPreDrawListener(this);
                    albumViewHolder.title.setTag(null);
//                    Log.i("test_width",""+albumViewHolder.layout.getWidth());
//                    Log.i("test_title",""+content.getTitle());
//                    Log.i("test_title_width",""+albumViewHolder.title.getWidth());

                    if(albumViewHolder.title.getLineCount()>2){
//                        Log.i("test","getLineCount>2");
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                        layoutParams.topMargin = CommonUtils.dip2px(5);
                        layoutParams.addRule(RelativeLayout.BELOW,R.id.logo);
                        albumViewHolder.layout.setLayoutParams(layoutParams);
                    } else{


                        if(albumViewHolder.layout.getWidth()>albumViewHolder.title.getWidth()){
//                            Log.i("test","layout.getWidth()>title.getWidth()");
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                            layoutParams.topMargin = CommonUtils.dip2px(5);
                            layoutParams.addRule(RelativeLayout.BELOW,R.id.logo);
                            albumViewHolder.layout.setLayoutParams(layoutParams);

                        }else{
//                            Log.i("test","layout.getWidth()<title.getWidth()");
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                            layoutParams.topMargin = CommonUtils.dip2px(5);
                            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.logo);
                            albumViewHolder.layout.setLayoutParams(layoutParams);
                        }

                    }
                    return true;
                }
            };
            albumViewHolder.title.setTag(listener);
            albumViewHolder.title.getViewTreeObserver().addOnPreDrawListener(listener);
            albumViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            if(content.getAlbums().getStatus()==1){
                //已完结
                albumViewHolder.title.setText(CommonUtils.tagKeyword("完~"+content.getTitle(),"完~",CommonUtils.getThemeColor(mContext)));
            }else{
                albumViewHolder.title.setText(content.getTitle());
            }

            albumViewHolder.count.setText("" + content.getAlbums().getCount() + "集");
            albumViewHolder.commentNum.setText(content.getComment()+"评论");
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

            if (showExcellent) {
                articleViewHolder.excellent.setVisibility(View.VISIBLE);
                Station station=MyApplication.getInstance().getStation();
                if(station!=null&&!TextUtils.isEmpty(station.getIcon2())){
                    Uri uri = Uri.parse(station.getIcon2());
                    int width=CommonUtils.dip2px(15);
                    int height=width;
                    ImageLoader.showThumb(uri,articleViewHolder.excellentLogo,width,height);
                }else{
                    articleViewHolder.excellentLogo.setVisibility(View.GONE);
                }
            } else {
                articleViewHolder.excellent.setVisibility(View.GONE);
            }
            if(content.getTop()!=null){
                articleViewHolder.top.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(content.getCover())) {
                    articleViewHolder.logo.setVisibility(View.VISIBLE);
                    Uri uri = Uri.parse(content.getCover());
                    int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                    int height = (int) (2f * width / 3f);
                    ImageLoader.showThumb(uri, articleViewHolder.logo, width, height);
                }else{
                    articleViewHolder.logo.setVisibility(View.GONE);
                }
            }else{
                articleViewHolder.top.setVisibility(View.GONE);
                if (content.getArticle().getCovers().size()>0&&!TextUtils.isEmpty(content.getArticle().getCovers().get(0))) {
                    articleViewHolder.logo.setVisibility(View.VISIBLE);
                    Uri uri = Uri.parse(content.getArticle().getCovers().get(0));
                    int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                    int height = (int) (2f * width / 3f);
                    ImageLoader.showThumb(uri, articleViewHolder.logo, width, height);
                }else{
                    articleViewHolder.logo.setVisibility(View.GONE);
                }
            }


            articleViewHolder.title.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    Log.i("test","articleViewHolder onPreDraw");
                    Log.i("test",content.getTitle());
                    ViewTreeObserver obs = articleViewHolder.title.getViewTreeObserver();
                    obs.removeOnPreDrawListener(this);

                    if(articleViewHolder.logo.getVisibility()==View.GONE){
//                            Log.i("test","articleViewHolder.logo=GONE");
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                            layoutParams.topMargin = CommonUtils.dip2px(10);
                            layoutParams.addRule(RelativeLayout.BELOW,R.id.title);
                            articleViewHolder.layout.setLayoutParams(layoutParams);

                    }else{
                        if(articleViewHolder.title.getLineCount()>2){
//                            Log.i("test","getLineCount>2");
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                            layoutParams.topMargin = CommonUtils.dip2px(5);
                            layoutParams.addRule(RelativeLayout.BELOW,R.id.logo);
                            articleViewHolder.layout.setLayoutParams(layoutParams);
                        } else{

                            if(articleViewHolder.layout.getWidth()>articleViewHolder.title.getWidth()){
//                                Log.i("test","layout.getWidth()>title.getWidth()");
                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                                layoutParams.topMargin = CommonUtils.dip2px(5);
                                layoutParams.addRule(RelativeLayout.BELOW,R.id.logo);
                                articleViewHolder.layout.setLayoutParams(layoutParams);

                            }else{
//                                Log.i("test","layout.getWidth()<title.getWidth()");
                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                                layoutParams.topMargin = CommonUtils.dip2px(5);
                                layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.logo);
                                articleViewHolder.layout.setLayoutParams(layoutParams);
                            }

                        }
                    }


                    return true;
                }
            });


            articleViewHolder.title.setText(content.getTitle());

            articleViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            articleViewHolder.commentNum.setText("" + content.getComment() + "评论");
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                articleViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }


            articleViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, ArticleDetailActivity.class);
                    it.putExtra("articleId", content.getId());
                    mContext.startActivity(it);
                }
            });
        }  else if (itemViewType == TYPE_ARTICLE_THREE) {
            final ArticleViewThreeHolder articleViewHolder = (ArticleViewThreeHolder) holder;

            if (showExcellent) {
                articleViewHolder.excellent.setVisibility(View.VISIBLE);
                Station station=MyApplication.getInstance().getStation();
                if(station!=null&&!TextUtils.isEmpty(station.getIcon2())){
                    Uri uri = Uri.parse(station.getIcon2());
                    int width=CommonUtils.dip2px(15);
                    int height=width;
                    ImageLoader.showThumb(uri,articleViewHolder.excellentLogo,width,height);
                }else{
                    articleViewHolder.excellentLogo.setVisibility(View.GONE);
                }
            } else {
                articleViewHolder.excellent.setVisibility(View.GONE);
            }
            if(content.getTop()!=null){
                articleViewHolder.top.setVisibility(View.VISIBLE);
            }else{
                articleViewHolder.top.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(content.getArticle().getCovers().get(0))) {
                Uri uri = Uri.parse(content.getArticle().getCovers().get(0));
                int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(30)) / 3;
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, articleViewHolder.pic1, width, height);
            }else{
                ImageLoader.showThumb(articleViewHolder.pic1, R.drawable.video_placeholder);
            }
            if (!TextUtils.isEmpty(content.getArticle().getCovers().get(1))) {
                Uri uri = Uri.parse(content.getArticle().getCovers().get(1));
                int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(30)) / 3;
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, articleViewHolder.pic2, width, height);
            }else{
                ImageLoader.showThumb(articleViewHolder.pic2, R.drawable.video_placeholder);
            }
            if (!TextUtils.isEmpty(content.getArticle().getCovers().get(2))) {
                Uri uri = Uri.parse(content.getArticle().getCovers().get(2));
                int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(30)) / 3;
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, articleViewHolder.pic3, width, height);
            }else{
                ImageLoader.showThumb(articleViewHolder.pic3, R.drawable.video_placeholder);
            }



            articleViewHolder.title.setText(content.getTitle());

            articleViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            articleViewHolder.commentNum.setText("" + content.getComment() + "评论");
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                articleViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }


            articleViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, ArticleDetailActivity.class);
                    it.putExtra("articleId", content.getId());
                    mContext.startActivity(it);
                }
            });
        }else if (itemViewType == TYPE_VIDEO) {
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;

            if (showExcellent) {
                videoViewHolder.excellent.setVisibility(View.VISIBLE);
                Station station=MyApplication.getInstance().getStation();
                if(station!=null&&!TextUtils.isEmpty(station.getIcon2())){
                    Uri uri = Uri.parse(station.getIcon2());
                    int width=CommonUtils.dip2px(15);
                    int height=width;
                    ImageLoader.showThumb(uri,videoViewHolder.excellentLogo,width,height);
                }else{
                    videoViewHolder.excellentLogo.setVisibility(View.GONE);
                }
            } else {
                videoViewHolder.excellent.setVisibility(View.GONE);
            }
            if(content.getTop()!=null){
                videoViewHolder.top.setVisibility(View.VISIBLE);
            }else{
                videoViewHolder.top.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.getScreenWidth() - CommonUtils.dip2px(20);
                int height = (int) (9f * width / 16f);
                ImageLoader.showThumb(uri, videoViewHolder.logo, width, height);
            }else{
                ImageLoader.showThumb(videoViewHolder.logo, R.drawable.video_placeholder);
            }
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                videoViewHolder.organization.setText(content.getAuthor_info().getMerchant().getName());
                if (isVideo()) {
                    if (!TextUtils.isEmpty(content.getAuthor_info().getMerchant().getLogo())) {
                        Uri uri = Uri.parse(content.getAuthor_info().getMerchant().getLogo());
                        int width = CommonUtils.dip2px(35);
                        int height = width;
                        ImageLoader.showThumb(uri, videoViewHolder.organizationLogo, width, height);
                    }
                }
            }
            if(!TextUtils.isEmpty(content.getVideo().getDuration())){
                videoViewHolder.duration.setText(CommonUtils.getShowTime((long) Float.parseFloat(content.getVideo().getDuration()) * 1000));
            }else{
                videoViewHolder.duration.setText("00:00");
            }
            videoViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            videoViewHolder.title.setText(content.getTitle());
            videoViewHolder.commentNum.setText( content.getComment()+"评论" );
            videoViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, VideoDetailActivity.class);
                    it.putExtra("videoId", content.getId());
                    mContext.startActivity(it);
                }
            });
        } else if (itemViewType== TYPE_GALLERIE_ONE) {
            GallerieOneViewHolder gallerieOneViewHolder = (GallerieOneViewHolder) holder;

            if (showExcellent) {
                gallerieOneViewHolder.excellent.setVisibility(View.VISIBLE);
                Station station=MyApplication.getInstance().getStation();
                if(station!=null&&!TextUtils.isEmpty(station.getIcon2())){
                    Uri uri = Uri.parse(station.getIcon2());
                    int width=CommonUtils.dip2px(15);
                    int height=width;
                    ImageLoader.showThumb(uri,gallerieOneViewHolder.excellentLogo,width,height);
                }else{
                    gallerieOneViewHolder.excellentLogo.setVisibility(View.GONE);
                }
            } else {
                gallerieOneViewHolder.excellent.setVisibility(View.GONE);
            }
            if(content.getTop()!=null){
                gallerieOneViewHolder.top.setVisibility(View.VISIBLE);
            }else{
                gallerieOneViewHolder.top.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(0))) {
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(0));
                int width = CommonUtils.getScreenWidth();
                int height = (int) (9f * width / 16f);
                ImageLoader.showThumb(uri, gallerieOneViewHolder.logo, width, height);
            }else{
                ImageLoader.showThumb(gallerieOneViewHolder.logo, R.drawable.video_placeholder);
            }
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                gallerieOneViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }
            gallerieOneViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            gallerieOneViewHolder.title.setText(content.getTitle());
            gallerieOneViewHolder.commentNum.setText("" + content.getComment() + "评论");
            gallerieOneViewHolder.count.setText(content.getGallerie().getCount() + "图");
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

            if (showExcellent) {
                gallerieViewThreeHolder.excellent.setVisibility(View.VISIBLE);
                Station station=MyApplication.getInstance().getStation();
                if(station!=null&&!TextUtils.isEmpty(station.getIcon2())){
                    Uri uri = Uri.parse(station.getIcon2());
                    int width=CommonUtils.dip2px(15);
                    int height=width;
                    ImageLoader.showThumb(uri,gallerieViewThreeHolder.excellentLogo,width,height);
                }else{
                    gallerieViewThreeHolder.excellentLogo.setVisibility(View.GONE);
                }
            } else {
                gallerieViewThreeHolder.excellent.setVisibility(View.GONE);
            }
            if(content.getTop()!=null){
                gallerieViewThreeHolder.top.setVisibility(View.VISIBLE);
            }else{
                gallerieViewThreeHolder.top.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(0))) {
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(0));
                int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(30)) / 3;
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, gallerieViewThreeHolder.pic1, width, height);
            }else{
                ImageLoader.showThumb(gallerieViewThreeHolder.pic1, R.drawable.video_placeholder);
            }
            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(1))) {
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(1));
                int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(30)) / 3;
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, gallerieViewThreeHolder.pic2, width, height);
            }else{
                ImageLoader.showThumb(gallerieViewThreeHolder.pic2, R.drawable.video_placeholder);
            }
            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(2))) {
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(2));
                int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(30)) / 3;
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, gallerieViewThreeHolder.pic3, width, height);
            }else{
                ImageLoader.showThumb(gallerieViewThreeHolder.pic3, R.drawable.video_placeholder);
            }
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                gallerieViewThreeHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }
            gallerieViewThreeHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            gallerieViewThreeHolder.title.setText(content.getTitle());
            gallerieViewThreeHolder.commentNum.setText("" + content.getComment() + "评论");
            gallerieViewThreeHolder.count.setText(content.getGallerie().getCount() + "图");
            gallerieViewThreeHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, GalleriaActivity.class);
                    it.putExtra("galleriaId", content.getId());
                    mContext.startActivity(it);
                }
            });

        } else {
            final SpecialViewHolder specialViewHolder = (SpecialViewHolder) holder;
            if (showExcellent) {
                specialViewHolder.excellent.setVisibility(View.VISIBLE);
                Station station=MyApplication.getInstance().getStation();
                if(station!=null&&!TextUtils.isEmpty(station.getIcon2())){
                    Uri uri = Uri.parse(station.getIcon2());
                    int width=CommonUtils.dip2px(15);
                    int height=width;
                    ImageLoader.showThumb(uri,specialViewHolder.excellentLogo,width,height);
                }else{
                    specialViewHolder.excellentLogo.setVisibility(View.GONE);
                }
            } else {
                specialViewHolder.excellent.setVisibility(View.GONE);
            }

            if(content.getTop()!=null){
                specialViewHolder.top.setVisibility(View.VISIBLE);
                specialViewHolder.specialIcon.setVisibility(View.GONE);
            }else{
                specialViewHolder.top.setVisibility(View.GONE);
                specialViewHolder.specialIcon.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, specialViewHolder.logo, width, height);
            }else{
                ImageLoader.showThumb(specialViewHolder.logo, R.drawable.article_placeholder);
            }

            specialViewHolder.title.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    Log.i("test","specialViewHolder onPreDraw");
                    Log.i("test",content.getTitle());
                    ViewTreeObserver obs = specialViewHolder.title.getViewTreeObserver();
                    obs.removeOnPreDrawListener(this);

                    if(specialViewHolder.logo.getVisibility()==View.GONE){

                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                        layoutParams.topMargin = CommonUtils.dip2px(10);
                        layoutParams.addRule(RelativeLayout.BELOW,R.id.title);
                        specialViewHolder.layout.setLayoutParams(layoutParams);

                    }else{
                        if(specialViewHolder.title.getLineCount()>2){
                            Log.i("test","getLineCount>2");
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                            layoutParams.topMargin = CommonUtils.dip2px(5);
                            layoutParams.addRule(RelativeLayout.BELOW,R.id.logo);
                            specialViewHolder.layout.setLayoutParams(layoutParams);
                        } else{

                            if(specialViewHolder.layout.getWidth()>specialViewHolder.title.getWidth()){
                                Log.i("test","layout.getWidth()>title.getWidth()");
                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                                layoutParams.topMargin = CommonUtils.dip2px(5);
                                layoutParams.addRule(RelativeLayout.BELOW,R.id.logo);
                                specialViewHolder.layout.setLayoutParams(layoutParams);

                            }else{
                                Log.i("test","layout.getWidth()<title.getWidth()");
                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                                layoutParams.topMargin = CommonUtils.dip2px(5);
                                layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.logo);
                                specialViewHolder.layout.setLayoutParams(layoutParams);
                            }

                        }
                    }


                    return true;
                }
            });
            specialViewHolder.title.setText(content.getTitle());
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                specialViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }
            specialViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));

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
                    if (mColumnContent.get(position - 1).getArticle().getType() == 1) {
                        return TYPE_ARTICLE;
                    } else if (mColumnContent.get(position - 1).getArticle().getType() == 2) {
                        return TYPE_ARTICLE_THREE;
                    } else {
                        if (mColumnContent.get(position - 1).getArticle().getCovers().size() >2) {
                            return TYPE_ARTICLE_THREE;
                        } else {
                            return TYPE_ARTICLE;
                        }
                    }
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
                if(mColumnContent.get(position).getTop()!=null){
                    return TYPE_ARTICLE;
                }else{
                    if (mColumnContent.get(position).getArticle().getType() == 1) {
                        return TYPE_ARTICLE;
                    } else if (mColumnContent.get(position).getArticle().getType() == 2) {
                        return TYPE_ARTICLE_THREE;
                    } else {
                        if (mColumnContent.get(position).getArticle().getCovers().size() >2) {
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
