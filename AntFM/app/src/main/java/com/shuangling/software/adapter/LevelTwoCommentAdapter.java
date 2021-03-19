package com.shuangling.software.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.CommentDetailActivity;
import com.shuangling.software.customview.ReadMoreTextView;
import com.shuangling.software.entity.Comment;
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
public class LevelTwoCommentAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    static final int DEFAULT_SHOW_CHILD_NUMBER = 3;
    public static final int TYPE_HEAD = 0;                  //顶部评论
    public static final int TYPE_COMMENT_ROOT = 1;          //一级评论
    public static final int TYPE_COMMENT_CHILD = 2;         //二级评论
    private Map<Integer, TypeAndData> mPositionTypeMap = new LinkedHashMap<>();

    public interface OnItemReply {
        void replyItem(Comment comment);
    }

    private List<Comment> mComments;
    private Context mContext;
    private Comment mTopComment;
    private LayoutInflater inflater;
    private int mScrollToCommentId = -1;
    private OnPraise mOnPraise;

    public void setOnPraise(OnPraise onPraise) {
        this.mOnPraise = onPraise;
    }

    public interface OnPraise {
        void praiseItem(Comment comment, View v);

        void deleteItem(Comment comment, View v);

        void scrollToPosition(int position);
    }

    private OnItemReply mOnItemReply;

    public void setOnItemReply(OnItemReply onItemReply) {
        this.mOnItemReply = onItemReply;
    }

    public void setTopComment(Comment topComment) {
        this.mTopComment = topComment;
    }

    public void setScrollToCommentId(int commentId) {
        this.mScrollToCommentId = commentId;
    }

    public LevelTwoCommentAdapter(Context context, List<Comment> comments) {
        super();
        this.mComments = comments;
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    public void updateView(List<Comment> comments) {
        this.mComments = comments;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            return new HeadViewHolder(inflater.inflate(R.layout.comment_head_item, parent, false));
        }
        if (viewType == TYPE_COMMENT_ROOT) {
            return new RootCommentViewHolder(inflater.inflate(R.layout.root_comment_item, parent, false));
        } else {
            return new ChildCommentViewHolder(inflater.inflate(R.layout.child_comment_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_HEAD) {
            final Comment comment = mTopComment;
            HeadViewHolder viewHolder = (HeadViewHolder) holder;
            if (!TextUtils.isEmpty(comment.getUser().getAvatar())) {
                Uri uri = Uri.parse(comment.getUser().getAvatar());
                int width = CommonUtils.dip2px(40);
                int height = width;
                ImageLoader.showThumb(uri, viewHolder.head, width, height);
            } else {
                ImageLoader.showThumb(viewHolder.head, R.drawable.ic_user1);
            }
            viewHolder.account.setText(comment.getUser().getNickname());
            viewHolder.time.setText(TimeUtil.formatDateTime(comment.getCreated_at()));
            if (comment.getLike_count() > 0) {
                viewHolder.praiseSum.setText("" + comment.getLike_count());
            } else {
                viewHolder.praiseSum.setText("");
            }
            //viewHolder.praiseSum.setText(""+comment.getLike_count());
            if (comment.getFabulous() == 0) {
                //viewHolder.praise.setTextColor(CommonUtils.getThemeColor(mContext));
                viewHolder.praiseSum.setActivated(true);
            } else {
                //viewHolder.praise.setTextColor(mContext.getResources().getColor(R.color.textColorThree));
                viewHolder.praiseSum.setActivated(false);
            }
            viewHolder.replyNumber.setText("评论(" + comment.getComment_count() + ")");
            viewHolder.comments.setText(comment.getText().getContent());
            viewHolder.reply.setText("回复");
            viewHolder.reply.setBackgroundResource(R.color.white);
            viewHolder.reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemReply != null) {
                        mOnItemReply.replyItem(comment);
                    }
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
        } else if (viewType == TYPE_COMMENT_ROOT) {
            final RootCommentViewHolder vh = (RootCommentViewHolder) holder;
            //int pos = mPostContents != null ? position - 2 - mPostContents.size() : position - 2;
            final Comment comment = (Comment) mPositionTypeMap.get(position).data;
            if (!TextUtils.isEmpty(comment.getUser().getAvatar())) {
                Uri uri = Uri.parse(comment.getUser().getAvatar());
                int width = CommonUtils.dip2px(40);
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
            //vh.praiseSum.setText("" + comment.getLike_count());
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
            if (mScrollToCommentId != -1 && mScrollToCommentId == comment.getId()) {
                //滑动到此位置
                if (mOnPraise != null) {
                    vh.praiseSum.post(new Runnable() {
                        @Override
                        public void run() {
                            mOnPraise.scrollToPosition(position);
                        }
                    });
                }
            }
        } else if (viewType == TYPE_COMMENT_CHILD) {
            final ChildCommentViewHolder vh = (ChildCommentViewHolder) holder;
            //int pos = mPostContents != null ? position - 2 - mPostContents.size() : position - 2;
            final Comment comment = (Comment) mPositionTypeMap.get(position).data;
            if (!TextUtils.isEmpty(comment.getUser().getAvatar())) {
                Uri uri = Uri.parse(comment.getUser().getAvatar());
                int width = CommonUtils.dip2px(40);
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
            //vh.praiseSum.setText("" + comment.getLike_count());
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
            vh.reply.setVisibility(View.GONE);
            vh.circle.setVisibility(View.GONE);
//            if (comment.getComment_count() > 0) {
//                vh.reply.setText(comment.getComment_count() + "回复");
//                vh.reply.setBackgroundResource(R.drawable.write_comment_bg);
//            } else {
//                vh.reply.setText("回复");
//                vh.reply.setBackgroundResource(R.color.transparent);
//            }
            vh.reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, CommentDetailActivity.class);
                    it.putExtra("commentId", comment.getId());
                    mContext.startActivity(it);
                }
            });
            if (((Comment) mPositionTypeMap.get(position).parentData).isExpand()) {
                vh.showMore.setVisibility(View.GONE);
            } else {
                if (((Comment) mPositionTypeMap.get(position).parentData).getParent_comment().size() > DEFAULT_SHOW_CHILD_NUMBER && ((Comment) mPositionTypeMap.get(position).parentData).getParent_comment().get(DEFAULT_SHOW_CHILD_NUMBER - 1).getId() == comment.getId()) {
                    vh.showMore.setText("展开全部" + ((Comment) mPositionTypeMap.get(position).parentData).getParent_comment().size() + "条回复");
                    vh.showMore.setVisibility(View.VISIBLE);
                } else {
                    vh.showMore.setVisibility(View.GONE);
                }
            }
            vh.showMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //vh.showMore.setVisibility(View.GONE);
                    ((Comment) mPositionTypeMap.get(position).parentData).setExpand(true);
                    notifyDataSetChanged();
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
        @BindView(R.id.head)
        SimpleDraweeView head;
        //        @BindView(R.id.praise)
//        FontIconView praise;
        @BindView(R.id.praiseSum)
        TextView praiseSum;
        //        @BindView(R.id.praiseLayout)
//        LinearLayout praiseLayout;
        @BindView(R.id.account)
        TextView account;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.comments)
        TextView comments;
        @BindView(R.id.reply)
        TextView reply;
        @BindView(R.id.replyNumber)
        TextView replyNumber;

        HeadViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class RootCommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.head)
        SimpleDraweeView head;
        //@BindView(R.id.praise)
        //FontIconView praise;
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
        int position = 0;
        mPositionTypeMap.clear();
        mPositionTypeMap.put(position, new TypeAndData(TYPE_HEAD, null, null));
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
        position++;
        return position;
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
