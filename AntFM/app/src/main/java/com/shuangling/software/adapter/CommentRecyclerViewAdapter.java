package com.shuangling.software.adapter;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.shuangling.software.R;
import com.shuangling.software.entity.CommentBean;
import com.shuangling.software.utils.EmojiUtil;
import java.util.List;
/**
 * Created by 你是我的 on 2019/1/1.
 */
public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {
    private List<CommentBean> commentBeanList;
    private Context context;
public CommentRecyclerViewAdapter(List<CommentBean> commentBeanList, Context context) {
        this.commentBeanList = commentBeanList;
        this.context = context;
    }
@NonNull
    @Override
    public CommentRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.comment_recycler_view_item, viewGroup, false);
        return new ViewHolder(view);
    }
@Override
    public void onBindViewHolder(@NonNull CommentRecyclerViewAdapter.ViewHolder viewHolder, int i) {
        CommentBean commentBean = commentBeanList.get(i);
        viewHolder.userName.setText(commentBean.getUserName());
        viewHolder.userComment.setText(EmojiUtil.parseEmoJi(1,context,commentBean.getUserComment()));
}
@Override
    public int getItemCount() {
        return commentBeanList.size();
    }
public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView userComment;
public ViewHolder(@NonNull View itemView) {
            super(itemView);
userName = itemView.findViewById(R.id.tv_user_name);
            userComment = itemView.findViewById(R.id.tv_user_comment);
        }
    }
}
