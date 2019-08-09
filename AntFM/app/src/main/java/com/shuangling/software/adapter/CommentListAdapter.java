package com.shuangling.software.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.CommentDetailActivity;
import com.shuangling.software.customview.ExpandableTextView;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.customview.MySpannableTextView;
import com.shuangling.software.customview.ReadMoreTextView;
import com.shuangling.software.entity.Comment;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.TimeUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentListAdapter extends BaseAdapter {

    private List<Comment> mComments;
    private Context mContext;
    private OnPraise mOnPraise;
    public void setOnPraise(OnPraise onPraise) {
        this.mOnPraise = onPraise;
    }

    public interface OnPraise{
        void praiseItem(Comment comment,View v);
        void deleteItem(Comment comment,View v);
    }


    public CommentListAdapter(Context context, List<Comment> comments) {
        super();
        this.mComments = comments;
        this.mContext = context;
    }


    public void updateView(List<Comment> comments) {
        this.mComments = comments;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if(mComments==null){
            return 0;
        }

        return mComments.size();
    }

    @Override
    public Comment getItem(int position) {
        return mComments.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        }
        ViewHolder vh = new ViewHolder(convertView);
        final Comment comment = getItem(position);
        if(!TextUtils.isEmpty(comment.getUser().getAvatar())){
            Uri uri = Uri.parse(comment.getUser().getAvatar());
            int width = CommonUtils.dip2px(40);
            int height = width;
            ImageLoader.showThumb(uri, vh.head, width, height);
        }else{
            ImageLoader.showThumb(vh.head, R.drawable.ic_user1);
        }
        vh.account.setText(comment.getUser().getNickname());
        vh.time.setText(TimeUtil.formatDateTime(comment.getCreated_at()));


        vh.praiseSum.setText(""+comment.getLike_count());
        if(comment.getFabulous()==0){
            //vh.praise.setTextColor(CommonUtils.getThemeColor(mContext));
            vh.praiseSum.setActivated(true);
        }else{
            //vh.praise.setTextColor(mContext.getResources().getColor(R.color.textColorThree));
            vh.praiseSum.setActivated(false);
        }
        vh.comments.setText(comment.getText().getContent());

        if(comment.getDelete()==0){
            //不可删除
            vh.delete.setVisibility(View.GONE);
        }else{
            vh.delete.setVisibility(View.VISIBLE);
        }
        vh.delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnPraise!=null){
                    mOnPraise.deleteItem(comment,v);
                }
            }
        });

        if(comment.getComment_count()>0){
            vh.reply.setText(comment.getComment_count()+"回复");
            vh.reply.setBackgroundResource(R.drawable.write_comment_bg);
        }else{
            vh.reply.setText("回复");
            vh.reply.setBackgroundResource(R.color.transparent);
        }

        vh.reply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(mContext,CommentDetailActivity.class);
                it.putExtra("commentId",comment.getId());
                mContext.startActivity(it);
            }
        });

        vh.praiseSum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnPraise!=null){
                    mOnPraise.praiseItem(comment,v);
                }
            }
        });






        return convertView;
    }





    class ViewHolder {
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
            ButterKnife.bind(this, view);
        }
    }
}
