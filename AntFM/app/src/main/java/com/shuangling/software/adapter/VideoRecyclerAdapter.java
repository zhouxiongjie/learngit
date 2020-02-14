package com.shuangling.software.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.CommentDetailActivity;
import com.shuangling.software.customview.ReadMoreTextView;
import com.shuangling.software.customview.ReadMoreTextViewWithIcon;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.Comment;
import com.shuangling.software.entity.VideoDetail;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class VideoRecyclerAdapter extends RecyclerView.Adapter implements View.OnClickListener {


    public static final int TYPE_HEAD = 0;                  //顶部点赞，收藏等布局
    public static final int TYPE_POST_VIDEO = 1;            //推荐视频
    public static final int TYPE_COMMENT_TOP = 2;           //评论头
    public static final int TYPE_NO_COMMENT = 3;            //评论空白页
    public static final int TYPE_COMMENT = 4;               //评论


    private Context mContext;
    private LayoutInflater inflater;
    private VideoDetail mVideoDetail;                       //视频详情
    private List<ColumnContent> mPostContents;              //相关推荐
    private List<Comment> mComments;                        //评论列表
    private int mTotalComments;
    private OnPraise mOnPraise;
    private OnPraiseVideo mOnPraiseVideo;
    private OnCollectVideo mOnCollectVideo;
    private OnItemReply mOnItemReply;
    private VideoItemClickListener mVideoItemClickListener;


    public void setTotalComments(int totalComments) {
        this.mTotalComments = totalComments;
        notifyDataSetChanged();
    }

    public void setVideoDetail(VideoDetail mVideoDetail) {
        this.mVideoDetail = mVideoDetail;
        notifyDataSetChanged();
    }

    public void setPostContents(List<ColumnContent> mPostContents) {
        this.mPostContents = mPostContents;
        notifyDataSetChanged();
    }

    public void setComments(List<Comment> mComments) {
        this.mComments = mComments;
        notifyDataSetChanged();
    }

    public void setOnPraise(OnPraise onPraise) {
        this.mOnPraise = onPraise;
    }

    public void setOnPraiseVideo(OnPraiseVideo onPraiseVideo) {
        this.mOnPraiseVideo = onPraiseVideo;
    }

    public void setOnCollectVideo(OnCollectVideo onCollectVideo) {
        this.mOnCollectVideo = onCollectVideo;
    }

    public void setOnItemReply(OnItemReply onItemReply) {
        this.mOnItemReply = onItemReply;
    }

    public void setVideoItemClickListener(VideoItemClickListener itemClickListener) {
        this.mVideoItemClickListener = itemClickListener;

    }

    public interface OnItemReply {
        void replyItem(Comment comment);
    }

    public interface OnPraise {
        void praiseItem(Comment comment, View view);

        void deleteItem(Comment comment, View v);
    }

    public interface OnPraiseVideo {
        void praiseVideo();
    }

    public interface OnCollectVideo {
        void collectVideo();
    }

    public interface VideoItemClickListener {
        public void onItemClick(ColumnContent content);
    }


    public VideoRecyclerAdapter(Context context) {
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
            return new HeadViewHolder(inflater.inflate(R.layout.video_praise_collect_layout, parent, false));
        } else if (viewType == TYPE_POST_VIDEO) {
            return new VideoViewHolder(inflater.inflate(R.layout.recommend_video_item, parent, false));
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


        if (getItemViewType(position) == TYPE_HEAD) {
            final HeadViewHolder viewHolder = (HeadViewHolder) holder;
            if (mVideoDetail != null) {
                viewHolder.videoTitle.setText(mVideoDetail.getTitle());
                viewHolder.reads.setText(""+mVideoDetail.getView());
                viewHolder.playTimes.setText("简介:" + mVideoDetail.getDes());
                viewHolder.playTimes.setVisibility(View.GONE);
                viewHolder.showMore.setTag(false);
                viewHolder.showMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean extend=(boolean)v.getTag();
                        extend=!extend;
                        v.setTag(extend);
                        if(extend){
                            viewHolder.videoTitle.setMaxLines(Integer.MAX_VALUE);
                            viewHolder.playTimes.setVisibility(View.VISIBLE);
                            viewHolder.showMore.setImageResource(R.drawable.more_up);
                        }else{
                            viewHolder.playTimes.setVisibility(View.GONE);
                            viewHolder.videoTitle.setMaxLines(1);
                            viewHolder.showMore.setImageResource(R.drawable.more_down);
                        }
                    }
                });
//                viewHolder.videoTitle.setOnCollapseOrExpande(new ReadMoreTextViewWithIcon.OnCollapseOrExpande() {
//                    @Override
//                    public void textCollapseOrExpanded(boolean collapse) {
//                        if (collapse) {
//                            viewHolder.playTimes.setVisibility(View.GONE);
//                        } else {
//                            viewHolder.playTimes.setVisibility(View.VISIBLE);
//                        }
//                    }
//                });
                if (mVideoDetail.getIs_likes() == 0) {
                    viewHolder.praiseSum.setActivated(true);
                } else {
                    viewHolder.praiseSum.setActivated(false);
                }
                viewHolder.praiseSum.setText("" + mVideoDetail.getLike());
                if (mVideoDetail.getIs_collection() == 0) {
                    viewHolder.collect.setActivated(true);
                    viewHolder.collect.setText("收藏");
                } else {
                    viewHolder.collect.setActivated(false);
                    viewHolder.collect.setText("已收藏");
                }

                viewHolder.praiseLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnPraiseVideo != null) {
                            mOnPraiseVideo.praiseVideo();
                        }

                    }
                });

                viewHolder.collectLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnCollectVideo != null) {
                            mOnCollectVideo.collectVideo();
                        }

                    }
                });
            }

        } else if (getItemViewType(position) == TYPE_POST_VIDEO) {


            final ColumnContent content = mPostContents.get(position - 1);
            VideoViewHolder viewHolder = (VideoViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, viewHolder.logo, width, height);
            }

            viewHolder.title.setText(content.getTitle());
            if (content.getVideo() != null) {
                viewHolder.playTimes.setText(content.getView() + "次播放");
                viewHolder.duration.setText(CommonUtils.getShowTime((long) Float.parseFloat(content.getVideo().getDuration()) * 1000));

                viewHolder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mVideoItemClickListener != null) {
                            mVideoItemClickListener.onItemClick(content);
                        }
                    }
                });
            }


        } else if (getItemViewType(position) == TYPE_COMMENT_TOP) {
            CommentTopViewHolder viewHolder = (CommentTopViewHolder) holder;
            if (mTotalComments == 0) {
                viewHolder.commentNum.setText("");
            } else {
                viewHolder.commentNum.setText("(" + mTotalComments + ")");
            }


        } else if (getItemViewType(position) == TYPE_NO_COMMENT) {


        } else {
            final CommentViewHolder viewHolder = (CommentViewHolder) holder;
            int pos = mPostContents != null ? position - 2 - mPostContents.size() : position - 2;
            final Comment comment = mComments.get(pos);
            if (!TextUtils.isEmpty(comment.getUser().getAvatar())) {
                Uri uri = Uri.parse(comment.getUser().getAvatar());
                int width = CommonUtils.dip2px(40);
                int height = width;
                ImageLoader.showThumb(uri, viewHolder.head, width, height);
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


    class HeadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.videoTitle)
        TextView videoTitle;
        @BindView(R.id.showMore)
        ImageView showMore;
        @BindView(R.id.playTimes)
        TextView playTimes;
        @BindView(R.id.reads)
        TextView reads;
        @BindView(R.id.praiseSum)
        TextView praiseSum;
        @BindView(R.id.praiseLayout)
        RelativeLayout praiseLayout;
        @BindView(R.id.collect)
        TextView collect;
        @BindView(R.id.collectLayout)
        RelativeLayout collectLayout;

        HeadViewHolder(View view) {
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
        //点赞、收藏头 +1
        //评论头 +1
        //推荐size
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

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        } else {
            if (mPostContents != null) {
                if (position <= mPostContents.size()) {
                    return TYPE_POST_VIDEO;
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



}
