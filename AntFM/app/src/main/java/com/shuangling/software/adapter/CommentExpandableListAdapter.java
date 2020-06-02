package com.shuangling.software.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentExpandableListAdapter extends BaseExpandableListAdapter implements View.OnClickListener {


    static final int DEFAULT_SHOW_CHILD_NUMBER=3;
    private List<Comment> mComments;
    private Context mContext;

    private OnPraise mOnPraise;

    public void setOnPraise(OnPraise onPraise) {
        this.mOnPraise = onPraise;
    }

    public interface OnPraise {
        void praiseItem(Comment comment, View v);

        void deleteItem(Comment comment, View v);
    }


    public CommentExpandableListAdapter(Context context, List<Comment> list) {
        super();
        this.mComments = list;
        this.mContext = context;
    }


    public void updateListView(List<Comment> list) {
        this.mComments = list;
        notifyDataSetChanged();
    }


    @Override
    public int getGroupCount() {
        if (mComments != null) {
            return mComments.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mComments.get(groupPosition).isExpand()) {
            if (mComments.get(groupPosition).getParent_comment() != null) {
                return mComments.get(groupPosition).getParent_comment().size();
            } else {
                return 0;
            }
        } else {
            if (mComments.get(groupPosition).getParent_comment() != null) {
                if (mComments.get(groupPosition).getParent_comment().size() > DEFAULT_SHOW_CHILD_NUMBER) {
                    return DEFAULT_SHOW_CHILD_NUMBER;
                } else {
                    return mComments.get(groupPosition).getParent_comment().size();
                }
            } else {
                return 0;
            }
        }

    }

    @Override
    public Comment getGroup(int groupPosition) {
        return mComments.get(groupPosition);
    }

    @Override
    public Comment getChild(int groupPosition, int childPosition) {
        return mComments.get(groupPosition).getParent_comment().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return super.getCombinedChildId(groupPosition, childPosition);

    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.root_comment_item, parent, false);
        }

        RootCommentViewHolder vh = new RootCommentViewHolder(convertView);
        final Comment comment = getGroup(groupPosition);
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


        vh.praiseSum.setText("" + comment.getLike_count());
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
                Intent it = new Intent(mContext, CommentDetailActivity.class);
                it.putExtra("commentId", comment.getId());
                mContext.startActivity(it);
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
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.child_comment_item, parent, false);
        }
        final ChildCommentViewHolder vh = new ChildCommentViewHolder(convertView);
        final Comment comment = getChild(groupPosition, childPosition);
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


        vh.praiseSum.setText("" + comment.getLike_count());
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
                Intent it = new Intent(mContext, CommentDetailActivity.class);
                it.putExtra("commentId", comment.getId());
                mContext.startActivity(it);
            }
        });

        if(getGroup(groupPosition).isExpand()){
           vh.showMore.setVisibility(View.GONE);
        }else{
            if(getGroup(groupPosition).getParent_comment()==null||getGroup(groupPosition).getParent_comment().size()<=DEFAULT_SHOW_CHILD_NUMBER){
                vh.showMore.setVisibility(View.GONE);
            }else{
                vh.showMore.setText("查看全部"+getGroup(groupPosition).getParent_comment().size()+"条回复");
                vh.showMore.setVisibility(View.VISIBLE);
            }
        }

        vh.showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //vh.showMore.setVisibility(View.GONE);
                getGroup(groupPosition).setExpand(true);
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


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    @Override
    public void onClick(View v) {

    }


    class RootCommentViewHolder {
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
            ButterKnife.bind(this, view);
        }
    }

    static class ChildCommentViewHolder {
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
            ButterKnife.bind(this, view);
        }
    }


}
