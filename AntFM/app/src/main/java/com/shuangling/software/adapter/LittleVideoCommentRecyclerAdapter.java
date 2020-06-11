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
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.Comment;
import com.shuangling.software.entity.VideoDetail;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.TimeUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class LittleVideoCommentRecyclerAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    static final int DEFAULT_SHOW_CHILD_NUMBER = 3;

    public static final int TYPE_HEAD = 0;                  //顶部点赞，收藏等布局
    public static final int TYPE_POST_VIDEO = 1;            //推荐视频
    public static final int TYPE_COMMENT_TOP = 2;           //评论头
    public static final int TYPE_NO_COMMENT = 3;            //评论空白页
    //public static final int TYPE_COMMENT = 4;               //评论

    public static final int TYPE_COMMENT_ROOT = 4;     //一级评论
    public static final int TYPE_COMMENT_CHILD = 5;    //二级评论

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
    private OnItemDetail mOnItemDetail;
    private VideoItemClickListener mVideoItemClickListener;

    private Map<Integer, TypeAndData> mPositionTypeMap = new LinkedHashMap<>();

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

    public void setOnItemDetail(OnItemDetail mOnItemDetail) {
        this.mOnItemDetail = mOnItemDetail;
    }

    public void setVideoItemClickListener(VideoItemClickListener itemClickListener) {
        this.mVideoItemClickListener = itemClickListener;

    }

    public interface OnItemReply {
        void replyItem(Comment comment);
    }

    public interface OnItemDetail {
        void itemDetail(int commentId, int ScrollToCommentId);
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


    public LittleVideoCommentRecyclerAdapter(Context context) {
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
        } else if (viewType == TYPE_COMMENT_ROOT) {

            View v=inflater.inflate(R.layout.root_comment_item, parent, false);
            return new RootCommentViewHolder(v);
        } else {
            return new ChildCommentViewHolder(inflater.inflate(R.layout.child_comment_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        int type = getItemViewType(position);
        if (type == TYPE_HEAD) {
            final HeadViewHolder viewHolder = (HeadViewHolder) holder;
            if (mVideoDetail != null) {
                viewHolder.videoTitle.setText(mVideoDetail.getTitle());
                viewHolder.reads.setText(CommonUtils.getShowNumber(mVideoDetail.getView()) + "次");
                viewHolder.playTimes.setText("简介:" + mVideoDetail.getDes());
                viewHolder.playTimes.setVisibility(View.GONE);
                viewHolder.showMore.setTag(false);
                viewHolder.showMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean extend = (boolean) v.getTag();
                        extend = !extend;
                        v.setTag(extend);
                        if (extend) {
                            //viewHolder.videoTitle.setMaxLines(Integer.MAX_VALUE);
                            viewHolder.playTimes.setVisibility(View.VISIBLE);
                            viewHolder.showMore.setImageResource(R.drawable.more_up);
                        } else {
                            viewHolder.playTimes.setVisibility(View.GONE);
                            //viewHolder.videoTitle.setMaxLines(1);
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

        } else if (type == TYPE_POST_VIDEO) {


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


        } else if (type == TYPE_COMMENT_TOP) {
            CommentTopViewHolder viewHolder = (CommentTopViewHolder) holder;
            if (mTotalComments == 0) {
                viewHolder.commentNum.setText("");
            } else {
                viewHolder.commentNum.setText("(" + mTotalComments + ")");
            }


        } else if (type == TYPE_NO_COMMENT) {


        } else if (type == TYPE_COMMENT_ROOT) {
            final RootCommentViewHolder vh = (RootCommentViewHolder) holder;
            //int pos = mPostContents != null ? position - 2 - mPostContents.size() : position - 2;
            final Comment comment = (Comment) mPositionTypeMap.get(position).data;


            if (!TextUtils.isEmpty(comment.getUser().getAvatar())) {
                Uri uri = Uri.parse(comment.getUser().getAvatar());
                int width = CommonUtils.dip2px(25);
                int height = width;
                ImageLoader.showThumb(uri, vh.head, width, height);
            } else {
                ImageLoader.showThumb(vh.head, R.drawable.ic_user1);
            }
            vh.account.setText(comment.getUser().getNickname());
            vh.time.setText(TimeUtil.formatDateTime(comment.getCreated_at()));


            if (comment.getLike_count() > 0) {
                vh.praiseSum.setText("" + comment.getLike_count());
            } else {
                vh.praiseSum.setText("");
            }


            if (comment.getFabulous() == 0) {
                vh.praiseSum.setActivated(true);
            } else {
                vh.praiseSum.setActivated(false);
            }
            vh.comments.setText(comment.getText().getContent());

            if (comment.getDelete() == 0) {
                //不可删除
                vh.delete.setVisibility(View.GONE);
            } else {
                vh.delete.setVisibility(View.VISIBLE);
            }
            vh.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnPraise != null) {
                        mOnPraise.deleteItem(comment, v);
                    }
                }
            });

            //可省略
            vh.reply.setText("回复");
            vh.reply.setBackgroundResource(R.color.transparent);


            vh.reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent it = new Intent(mContext, CommentDetailActivity.class);
//                    it.putExtra("commentId", comment.getId());
//                    mContext.startActivity(it);
                    if (mOnItemReply != null) {
                        mOnItemReply.replyItem(comment);
                    }
                }
            });

            vh.praiseSum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnPraise != null) {
                        mOnPraise.praiseItem(comment, v);
                    }
                }
            });
        } else if (type == TYPE_COMMENT_CHILD) {
            final ChildCommentViewHolder vh = (ChildCommentViewHolder) holder;
            //int pos = mPostContents != null ? position - 2 - mPostContents.size() : position - 2;
            final Comment comment = (Comment) mPositionTypeMap.get(position).data;

            if (!TextUtils.isEmpty(comment.getUser().getAvatar())) {
                Uri uri = Uri.parse(comment.getUser().getAvatar());
                int width = CommonUtils.dip2px(25);
                int height = width;
                ImageLoader.showThumb(uri, vh.head, width, height);
            } else {
                ImageLoader.showThumb(vh.head, R.drawable.ic_user1);
            }
            vh.account.setText(comment.getUser().getNickname());
            vh.time.setText(TimeUtil.formatDateTime(comment.getCreated_at()));


            if (comment.getLike_count() > 0) {
                vh.praiseSum.setText("" + comment.getLike_count());
            } else {
                vh.praiseSum.setText("");
            }

            if (comment.getFabulous() == 0) {
                vh.praiseSum.setActivated(true);
            } else {
                vh.praiseSum.setActivated(false);
            }
            vh.comments.setText(comment.getText().getContent());

            if (comment.getDelete() == 0) {
                //不可删除
                vh.delete.setVisibility(View.GONE);
            } else {
                vh.delete.setVisibility(View.VISIBLE);
            }
            vh.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnPraise != null) {
                        mOnPraise.deleteItem(comment, v);
                    }
                }
            });

            if (comment.getComment_count() > 0) {
                vh.reply.setText(comment.getComment_count() + "回复");
                vh.reply.setBackgroundResource(R.drawable.write_comment_bg);
            } else {
                vh.reply.setText("回复");
                vh.reply.setBackgroundResource(R.color.transparent);
            }

            vh.reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent it = new Intent(mContext, CommentDetailActivity.class);
//                    it.putExtra("commentId", ((Comment) mPositionTypeMap.get(position).parentData).getId());
//                    it.putExtra("scrollToCommentId", comment.getId());
//                    mContext.startActivity(it);

                    if (mOnItemDetail != null) {
                        mOnItemDetail.itemDetail(((Comment) mPositionTypeMap.get(position).parentData).getId(), comment.getId());
                    }


                }
            });

            if (((Comment) mPositionTypeMap.get(position).parentData).isExpand()) {
                vh.showMore.setVisibility(View.GONE);
            } else {

                if (((Comment) mPositionTypeMap.get(position).parentData).getParent_comment().size() > DEFAULT_SHOW_CHILD_NUMBER && ((Comment) mPositionTypeMap.get(position).parentData).getParent_comment().get(DEFAULT_SHOW_CHILD_NUMBER - 1).getId() == comment.getId()) {
                    vh.showMore.setText("查看全部" + ((Comment) mPositionTypeMap.get(position).parentData).getParent_comment().size() + "条回复");
                    vh.showMore.setVisibility(View.VISIBLE);
                } else {
                    vh.showMore.setVisibility(View.GONE);
                }

            }

            vh.showMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ((Comment)mPositionTypeMap.get(position).parentData).setExpand(true);
//                    notifyDataSetChanged();

//                    Intent it = new Intent(mContext, CommentDetailActivity.class);
//                    it.putExtra("commentId", ((Comment) mPositionTypeMap.get(position).parentData).getId());
//                    it.putExtra("scrollToCommentId", comment.getId());
//                    mContext.startActivity(it);

                    if (mOnItemDetail != null) {
                        mOnItemDetail.itemDetail(((Comment) mPositionTypeMap.get(position).parentData).getId(), comment.getId());
                    }
                }
            });

            vh.praiseSum.setOnClickListener(new View.OnClickListener() {
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
        RelativeLayout noData;

        NoCommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    class RootCommentViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.head)
//        SimpleDraweeView head;
//        //@BindView(R.id.praise)
//        //FontIconView praise;
//        @BindView(R.id.praiseSum)
//        TextView praiseSum;
//        @BindView(R.id.delete)
//        ImageView delete;
//        @BindView(R.id.account)
//        TextView account;
//        @BindView(R.id.time)
//        TextView time;
//        @BindView(R.id.comments)
//        ReadMoreTextView comments;
//        @BindView(R.id.reply)
//        TextView reply;
//

        @BindView(R.id.head)
        SimpleDraweeView head;
        @BindView(R.id.praiseSum)
        TextView praiseSum;
        @BindView(R.id.delete)
        ImageView delete;
        @BindView(R.id.account)
        TextView account;
        @BindView(R.id.comments)
        ReadMoreTextView comments;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.circle)
        SimpleDraweeView circle;
        @BindView(R.id.reply)
        TextView reply;

        RootCommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }


    }

    static class ChildCommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.head)
        SimpleDraweeView head;
        @BindView(R.id.praiseSum)
        TextView praiseSum;
        @BindView(R.id.account)
        TextView account;
        @BindView(R.id.comments)
        ReadMoreTextView comments;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.circle)
        SimpleDraweeView circle;
        @BindView(R.id.reply)
        TextView reply;
        @BindView(R.id.delete)
        ImageView delete;
        @BindView(R.id.showMore)
        TextView showMore;

        ChildCommentViewHolder(View view) {
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
//        int count = 2;
//        if (mPostContents != null) {
//            count += mPostContents.size();
//        }
//        if (mComments != null && mComments.size() > 0) {
//            count += mComments.size();
//        } else {
//            count += 1;
//        }
//        return count;


        int position = -1;
        mPositionTypeMap.clear();

        //音频详情头 +1
        //评论头 +1
        //相关推荐size
        //评论size

        if (mComments == null || mComments.size() == 0) {
            position++;
            mPositionTypeMap.put(position, new TypeAndData(TYPE_NO_COMMENT, null, null));
        } else {
            for (int i = 0; i < mComments.size(); i++) {
                position++;
                mPositionTypeMap.put(position, new TypeAndData(TYPE_COMMENT_ROOT, mComments.get(i), null));

                if (mComments.get(i).getParent_comment() != null && mComments.get(i).getParent_comment().size() > 0) {
                    if (mComments.get(i).isExpand()) {
                        for (int j = 0; j < mComments.get(i).getParent_comment().size(); j++) {
                            position++;
                            mPositionTypeMap.put(position, new TypeAndData(TYPE_COMMENT_CHILD, mComments.get(i).getParent_comment().get(j), mComments.get(i)));
                        }
                    } else {
                        for (int j = 0; j < mComments.get(i).getParent_comment().size() && j < DEFAULT_SHOW_CHILD_NUMBER; j++) {
                            position++;
                            mPositionTypeMap.put(position, new TypeAndData(TYPE_COMMENT_CHILD, mComments.get(i).getParent_comment().get(j), mComments.get(i)));
                        }
                    }
                }
            }
        }


        int count = position + 1;
        return count;


    }

    @Override
    public int getItemViewType(int position) {


        int viewType = mPositionTypeMap.get(position).type;
        return viewType;

    }


    static class TypeAndData {

        int type;
        Object data;
        Object parentData;

        public TypeAndData(int type, Object data, Object parentData) {
            this.type = type;
            this.data = data;
            this.parentData = parentData;
        }
    }



}
