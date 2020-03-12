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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.activity.ArticleDetailActivity;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.activity.CommentDetailActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.activity.SpecialDetailActivity;
import com.shuangling.software.activity.VideoDetailActivity;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.customview.ReadMoreTextView;
import com.shuangling.software.entity.AudioDetail;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.Comment;
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
public class AudioRecyclerAdapter extends RecyclerView.Adapter implements View.OnClickListener {


    public static final int TYPE_HEAD = 0;                  //顶部点赞，收藏等布局
    //相关推荐
    public static final int TYPE_AUDIO = 1;             //音频
    public static final int TYPE_ALBUM = 2;             //专辑
    public static final int TYPE_ARTICLE = 3;           //文章
    public static final int TYPE_VIDEO = 4;             //视频
    public static final int TYPE_SPECIAL = 5;           //专题
    public static final int TYPE_GALLERIE_ONE = 6;      //一图集
    public static final int TYPE_GALLERIE_THREE = 7;    //三图集

    public static final int TYPE_COMMENT_TOP = 8;           //评论头
    public static final int TYPE_NO_COMMENT =9;            //评论空白页
    public static final int TYPE_COMMENT = 10;               //评论


    private Context mContext;
    private LayoutInflater inflater;
    private HeadViewHolder mHeadViewHolder;
    private View mHeaderView;
    private AudioDetail mAudioDetail;                       //音频详情
    private List<ColumnContent> mPostContents;              //相关推荐
    private List<Comment> mComments;                        //评论列表
    private int mTotalComments;                             //评论总数
    private OnPraise mOnPraise;

    private OnItemReply mOnItemReply;
    private PostItemClickListener mPostItemClickListener;



    public void addHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public HeadViewHolder getHeadViewHolder(){
        return mHeadViewHolder;
    }

    public void setTotalComments(int totalComments) {
        this.mTotalComments = totalComments;
        notifyDataSetChanged();
    }



    public void setAudioDetail(AudioDetail audioDetail) {
        this.mAudioDetail = audioDetail;
        notifyDataSetChanged();
    }

    public void setPostContents(List<ColumnContent> postContents) {
        this.mPostContents = postContents;
        notifyDataSetChanged();
    }

    public void setComments(List<Comment> mComments) {
        this.mComments = mComments;
        notifyDataSetChanged();
    }

    public void setOnPraise(OnPraise onPraise) {
        this.mOnPraise = onPraise;
    }


    public void setOnItemReply(OnItemReply onItemReply) {
        this.mOnItemReply = onItemReply;
    }

    public void setPostItemClickListener(PostItemClickListener itemClickListener) {
        this.mPostItemClickListener = itemClickListener;

    }

    public interface OnItemReply {
        void replyItem(Comment comment);
    }

    public interface OnPraise {
        void praiseItem(Comment comment, View view);

        void deleteItem(Comment comment, View v);
    }


    public interface PostItemClickListener {
        public void onItemClick(ColumnContent content);
    }


    public AudioRecyclerAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }


    public void updateView(List<Comment> comments) {
        this.mComments = comments;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEAD) {
            mHeadViewHolder= new HeadViewHolder(mHeaderView);
            return mHeadViewHolder;
        } else if (viewType == TYPE_AUDIO) {
            return new AudioViewHolder(inflater.inflate(R.layout.content_audio_item, parent, false));
        } else if (viewType == TYPE_ALBUM) {
            return new AlbumViewHolder(inflater.inflate(R.layout.content_album_item, parent, false));
        } else if (viewType == TYPE_ARTICLE) {
            return new ArticleViewHolder(inflater.inflate(R.layout.content_article_item, parent, false));
        } else if (viewType == TYPE_VIDEO) {
            return new VideoViewHolder(inflater.inflate(R.layout.recommend_video_item, parent, false));
        } else if (viewType == TYPE_GALLERIE_ONE) {
            return new GallerieOneViewHolder(inflater.inflate(R.layout.content_gallerie_one_item, parent, false));
        } else if (viewType == TYPE_GALLERIE_THREE) {
            return new GallerieViewThreeHolder(inflater.inflate(R.layout.content_gallerie_three_item, parent, false));
        } else if(viewType == TYPE_SPECIAL){
            return new SpecialViewHolder(inflater.inflate(R.layout.content_special_item, parent, false));
        } else if (viewType == TYPE_COMMENT_TOP) {
            return new CommentTopViewHolder(inflater.inflate(R.layout.video_comment_top, parent, false));
        } else if (viewType == TYPE_NO_COMMENT) {
            return new NoCommentViewHolder(inflater.inflate(R.layout.no_data_layout, parent, false));
        } else {
            return new CommentViewHolder(inflater.inflate(R.layout.comment_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        int type=getItemViewType(position);
        if ( type== TYPE_HEAD) {

        } else if (type == TYPE_AUDIO) {
            final ColumnContent content = mPostContents.get(position-1);
            AudioViewHolder audioViewHolder = (AudioViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.dip2px(100);
                int height = width;
                ImageLoader.showThumb(uri, audioViewHolder.logo, width, height);
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

        } else if (type == TYPE_ALBUM) {
            final ColumnContent content = mPostContents.get(position-1);
            final AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, albumViewHolder.logo, width, height);
            }
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                albumViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }

            albumViewHolder.title.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    ViewTreeObserver obs = albumViewHolder.title.getViewTreeObserver();
                    obs.removeOnPreDrawListener(this);
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
            });
            if(content.getAlbums().getStatus()==1){
                //已完结
                albumViewHolder.title.setText(CommonUtils.tagKeyword("完~"+content.getTitle(),"完~",CommonUtils.getThemeColor(mContext)));
            }else{
                albumViewHolder.title.setText(content.getTitle());
            }
            albumViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            albumViewHolder.count.setText("" + content.getAlbums().getCount() + "集");
            albumViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, AlbumDetailActivity.class);
                    it.putExtra("albumId", content.getId());

                    mContext.startActivity(it);
                }
            });

        } else if (type == TYPE_ARTICLE) {
            final ColumnContent content = mPostContents.get(position-1);
            final ArticleViewHolder articleViewHolder = (ArticleViewHolder) holder;


            if (!TextUtils.isEmpty(content.getCover())) {
                articleViewHolder.logo.setVisibility(View.VISIBLE);
                Uri uri = Uri.parse(content.getCover());
                int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, articleViewHolder.logo, width, height);
            }else {
                articleViewHolder.logo.setVisibility(View.GONE);
            }

            articleViewHolder.title.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
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

            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                articleViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }

            articleViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
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
        } else if (type == TYPE_VIDEO) {

            final ColumnContent content = mPostContents.get(position-1);
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, videoViewHolder.logo, width, height);
            }

            videoViewHolder.title.setText(content.getTitle());
            videoViewHolder.playTimes.setText(content.getView()+"次播放");
            videoViewHolder.duration.setText(CommonUtils.getShowTime((long) Float.parseFloat(content.getVideo().getDuration()) * 1000));

            videoViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, VideoDetailActivity.class);
                    it.putExtra("videoId", content.getId());
                    mContext.startActivity(it);
                }
            });

        } else if (type == TYPE_GALLERIE_ONE) {

            final ColumnContent content = mPostContents.get(position-1);
            GallerieOneViewHolder gallerieOneViewHolder = (GallerieOneViewHolder) holder;

            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(0))) {
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(0));
                int width = CommonUtils.getScreenWidth();
                int height = (int) (9f * width / 16f);
                ImageLoader.showThumb(uri, gallerieOneViewHolder.logo, width, height);
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

        } else if (type == TYPE_GALLERIE_THREE) {
            final ColumnContent content = mPostContents.get(position-1);
            GallerieViewThreeHolder gallerieViewThreeHolder = (GallerieViewThreeHolder) holder;


            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(0))) {
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(0));
                int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(30)) / 3;
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, gallerieViewThreeHolder.pic1, width, height);
            }
            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(1))) {
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(1));
                int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(30)) / 3;
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, gallerieViewThreeHolder.pic2, width, height);
            }
            if (!TextUtils.isEmpty(content.getGallerie().getCovers().get(2))) {
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(2));
                int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(30)) / 3;
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, gallerieViewThreeHolder.pic3, width, height);
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

        } else if(type == TYPE_SPECIAL){
            final ColumnContent content = mPostContents.get(position-1);
            final SpecialViewHolder specialViewHolder = (SpecialViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, specialViewHolder.logo, width, height);
            }


            specialViewHolder.title.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    ViewTreeObserver obs = specialViewHolder.title.getViewTreeObserver();
                    obs.removeOnPreDrawListener(this);

                    if(specialViewHolder.logo.getVisibility()==View.GONE){
                        Log.i("test","articleViewHolder.logo=GONE");
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
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                specialViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }

            specialViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
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
        else if (type == TYPE_COMMENT_TOP) {
            CommentTopViewHolder viewHolder = (CommentTopViewHolder) holder;
            if (mTotalComments == 0) {
                viewHolder.commentNum.setText("");
            } else {
                viewHolder.commentNum.setText("(" + mTotalComments + ")");
            }


        } else if (type == TYPE_NO_COMMENT) {


        } else {
            final CommentViewHolder viewHolder = (CommentViewHolder) holder;
            int pos = mPostContents != null ? position - 2 - mPostContents.size() : position - 2;
            final Comment comment = mComments.get(pos);
            if (!TextUtils.isEmpty(comment.getUser().getAvatar())) {
                Uri uri = Uri.parse(comment.getUser().getAvatar());
                int width = CommonUtils.dip2px(40);
                int height = width;
                ImageLoader.showThumb(uri, viewHolder.head, width, height);
            }else{
                ImageLoader.showThumb(viewHolder.head,R.drawable.ic_user1);
            }
            viewHolder.account.setText(comment.getUser().getNickname());
            viewHolder.time.setText(comment.getCreated_at());


            viewHolder.praiseSum.setText("" + comment.getLike_count());
            if (comment.getFabulous() == 1) {
                //viewHolder.praise.setTextColor(CommonUtils.getThemeColor(mContext));
                viewHolder.praiseSum.setActivated(false);
            } else {
                //viewHolder.praise.setTextColor(mContext.getResources().getColor(R.color.textColorThree));
                viewHolder.praiseSum.setActivated(true);
            }

            if (comment.getDelete() == 0) {
                //不可删除
                viewHolder.delete.setVisibility(View.GONE);
            } else {
                viewHolder.delete.setVisibility(View.VISIBLE);
            }
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnPraise != null) {
                        mOnPraise.deleteItem(comment, v);
                    }
                }
            });

            viewHolder.comments.setText(comment.getText().getContent());


            if (comment.getComment_count() > 0) {
                viewHolder.reply.setText(comment.getComment_count() + "回复");
                viewHolder.reply.setBackgroundResource(R.drawable.write_comment_bg);
            } else {
                viewHolder.reply.setText("回复");
                viewHolder.reply.setBackgroundResource(R.color.transparent);
            }

            viewHolder.reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, CommentDetailActivity.class);
                    it.putExtra("commentId", comment.getId());
                    mContext.startActivity(it);
                }
            });

            viewHolder.praiseSum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnPraise != null) {
                        mOnPraise.praiseItem(comment, v);
                    }
                }
            });
        }
    }


    @Override
    public void onClick(View v) {

    }


    public class HeadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.logo)
        public SimpleDraweeView logo;
        @BindView(R.id.audioTitle)
        public TextView audioTitle;
        @BindView(R.id.organization)
        public TextView organization;
        @BindView(R.id.currentTime)
        public TextView currentTime;
        @BindView(R.id.endTime)
        public TextView endTime;
        @BindView(R.id.seekBar)
        public SeekBar seekBar;
        @BindView(R.id.play)
        public FontIconView play;
        @BindView(R.id.previous)
        public FontIconView previous;
        @BindView(R.id.next)
        public FontIconView next;
        @BindView(R.id.actionBar)
        public RelativeLayout actionBar;
        @BindView(R.id.list)
        public TextView list;
        @BindView(R.id.timer)
        public TextView timer;
        @BindView(R.id.rate)
        public TextView rate;
        @BindView(R.id.albumLogo)
        public SimpleDraweeView albumLogo;
        @BindView(R.id.albumTitle)
        public TextView albumTitle;
        @BindView(R.id.anchorName)
        public TextView anchorName;
        @BindView(R.id.audioNumber)
        public TextView audioNumber;
        @BindView(R.id.subscribe)
        public TextView subscribe;

        HeadViewHolder(View view) {
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

        public AudioViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.excellent)
        RelativeLayout excellent;
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


    public class AlbumViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.merchant)
        TextView merchant;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.root)
        LinearLayout root;
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
        @BindView(R.id.layout)
        LinearLayout layout;
        public SpecialViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }


    public class VideoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.excellent)
        TextView excellent;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.duration)
        TextView duration;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.playTimes)
        TextView playTimes;
        @BindView(R.id.root)
        LinearLayout root;


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
        RelativeLayout excellent;

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
        RelativeLayout excellent;

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
        RelativeLayout excellent;
        @BindView(R.id.layout)
        LinearLayout layout;

        public ArticleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    static class CommentTopViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.commentNum)
        TextView commentNum;
        @BindView(R.id.commentLayout)
        LinearLayout commentLayout;

        CommentTopViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    static class NoCommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.noScriptText)
        TextView noScriptText;
        @BindView(R.id.noData)
        LinearLayout noData;

        NoCommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    class CommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.head)
        SimpleDraweeView head;
        //        @BindView(R.id.praise)
//        FontIconView praise;
        @BindView(R.id.praiseSum)
        TextView praiseSum;
        @BindView(R.id.delete)
        ImageView delete;
        @BindView(R.id.account)
        TextView account;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.comments)
        ReadMoreTextView comments;
        @BindView(R.id.reply)
        TextView reply;

        CommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public int getItemCount() {

        if(mHeaderView == null) {
            return 0;
        }else{
            //音频详情头 +1
            //评论头 +1
            //相关推荐size
            //评论size
            int count = 2;
            if (mPostContents != null) {
                count += mPostContents.size();
            }
            if (mComments != null && mComments.size() > 0) {
                count += mComments.size();
            } else {
                count += 1;
            }
            return count;
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        } else {
            if (mPostContents != null) {
                if (position <= mPostContents.size()) {
                    if (mPostContents.get(position - 1).getType() == 1) {
                        return TYPE_AUDIO;
                    } else if (mPostContents.get(position - 1).getType() == 2) {
                        return TYPE_ALBUM;
                    } else if (mPostContents.get(position - 1).getType() == 3) {
                        return TYPE_ARTICLE;
                    } else if (mPostContents.get(position - 1).getType() == 4) {
                        return TYPE_VIDEO;
                    } else if (mPostContents.get(position - 1).getType() == 5) {
                        return TYPE_SPECIAL;
                    } else {
                        if (mPostContents.get(position - 1).getGallerie().getType() == 1) {
                            return TYPE_GALLERIE_ONE;
                        } else if (mPostContents.get(position - 1).getGallerie().getType() == 2) {
                            return TYPE_GALLERIE_THREE;
                        } else {
                            if (mPostContents.get(position).getGallerie().getCovers().size() < 3) {
                                return TYPE_GALLERIE_ONE;
                            } else {
                                return TYPE_GALLERIE_THREE;
                            }
                        }

                    }

                } else {
                    if (position == mPostContents.size() + 1) {
                        return TYPE_COMMENT_TOP;
                    } else if (position == mPostContents.size() + 2) {
                        if (mComments == null || mComments.size() == 0) {
                            return TYPE_NO_COMMENT;
                        } else {
                            return TYPE_COMMENT;
                        }
                    } else {
                        return TYPE_COMMENT;
                    }
                }
            } else {
                if (position == 1) {
                    return TYPE_COMMENT_TOP;
                } else if (position == 2) {
                    if (mComments == null || mComments.size() == 0) {
                        return TYPE_NO_COMMENT;
                    } else {
                        return TYPE_COMMENT;
                    }
                } else {
                    return TYPE_COMMENT;
                }
            }
        }

    }


    static class ViewHolder {


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
