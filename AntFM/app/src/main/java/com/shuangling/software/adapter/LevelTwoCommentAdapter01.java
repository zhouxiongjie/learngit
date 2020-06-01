package com.shuangling.software.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.customview.ReadMoreTextView;
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
public class LevelTwoCommentAdapter01 extends RecyclerView.Adapter implements View.OnClickListener {


    public static final int TYPE_HEAD = 0;                  //顶部评论
    public static final int TYPE_COMMENT = 1;               //普通评论

    public interface OnItemReply{
        void replyItem(Comment comment);
    }

    private List<Comment> mComments;
    private Context mContext;
    private Comment mTopComment;
    private LayoutInflater inflater;
    private OnPraise mOnPraise;
    public void setOnPraise(OnPraise onPraise) {
        this.mOnPraise = onPraise;
    }

    public interface OnPraise{
        void praiseItem(Comment comment, View v);

        void deleteItem(Comment comment, View v);
    }


    private OnItemReply mOnItemReply;
    public void setOnItemReply(OnItemReply onItemReply) {
        this.mOnItemReply = onItemReply;
    }




    public void setTopComment(Comment topComment) {
        this.mTopComment = topComment;
    }

    public LevelTwoCommentAdapter01(Context context, List<Comment> comments) {
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
        } else {
            return new ViewHolder(inflater.inflate(R.layout.comment_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        if (getItemViewType(position) == TYPE_HEAD) {
            final Comment comment = mTopComment;
            HeadViewHolder viewHolder = (HeadViewHolder) holder;
            if(!TextUtils.isEmpty(comment.getUser().getAvatar())){
                Uri uri = Uri.parse(comment.getUser().getAvatar());
                int width = CommonUtils.dip2px(40);
                int height = width;
                ImageLoader.showThumb(uri, viewHolder.head, width, height);
            }else{
                ImageLoader.showThumb(viewHolder.head,R.drawable.ic_user1);
            }
            viewHolder.account.setText(comment.getUser().getNickname());
            viewHolder.time.setText(TimeUtil.formatDateTime(comment.getCreated_at()));
            viewHolder.praiseSum.setText(""+comment.getLike_count());
            if(comment.getFabulous()==0){
                //viewHolder.praise.setTextColor(CommonUtils.getThemeColor(mContext));
                viewHolder.praiseSum.setActivated(true);
            }else{
                //viewHolder.praise.setTextColor(mContext.getResources().getColor(R.color.textColorThree));
                viewHolder.praiseSum.setActivated(false);
            }
            viewHolder.comments.setText(comment.getText().getContent());

            viewHolder.reply.setText("回复");
            viewHolder.reply.setBackgroundResource(R.color.white);
            viewHolder.praiseSum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnPraise!=null){
                        mOnPraise.praiseItem(comment,v);
                    }
                }
            });


        } else {

            final Comment comment = mComments.get(position-1);
            ViewHolder viewHolder = (ViewHolder) holder;
            if(!TextUtils.isEmpty(comment.getUser().getAvatar())){
                Uri uri = Uri.parse(comment.getUser().getAvatar());
                int width = CommonUtils.dip2px(40);
                int height = width;
                ImageLoader.showThumb(uri, viewHolder.head, width, height);
            }
            viewHolder.account.setText(comment.getUser().getNickname());
            viewHolder.time.setText(TimeUtil.formatDateTime(comment.getCreated_at()));
            viewHolder.praiseSum.setText(""+comment.getLike_count());
            if(comment.getFabulous()==1){
                //viewHolder.praise.setTextColor(CommonUtils.getThemeColor(mContext));
                viewHolder.praiseSum.setActivated(false);
            }else{
                //viewHolder.praise.setTextColor(mContext.getResources().getColor(R.color.textColorThree));
                viewHolder.praiseSum.setActivated(true);
            }
//            if(comment.getParent()!=null&&mTopComment!=null&&comment.getParent().getId()!=mTopComment.getId()){
//                viewHolder.comments.setText(CommonUtils.tagKeyword(comment.getText().getContent()+"//@"+comment.getParent().getUser().getNickname()+":"+comment.getParent().getText().getContent(),"@"+comment.getParent().getUser().getNickname()));
//            }else{
                viewHolder.comments.setText(comment.getText().getContent());
//            }


            if(comment.getDelete()==0){
                //不可删除
                viewHolder.delete.setVisibility(View.GONE);
            }else{
                viewHolder.delete.setVisibility(View.VISIBLE);
            }
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnPraise!=null){
                        mOnPraise.deleteItem(comment,v);
                    }
                }
            });

            viewHolder.reply.setText("回复");
            viewHolder.reply.setBackgroundResource(R.color.white);


            viewHolder.reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mOnItemReply!=null){
                        mOnItemReply.replyItem(comment);
                    }

                }
            });

            viewHolder.praiseSum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnPraise!=null){
                        mOnPraise.praiseItem(comment,v);
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


    static class ViewHolder extends RecyclerView.ViewHolder {
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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public int getItemCount() {
        return mComments.size() + 1;

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        } else {
            return TYPE_COMMENT;
        }


    }



}
