package com.shuangling.software.adapter;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.shuangling.software.R;
import com.shuangling.software.entity.FeedBackInfo;
import com.shuangling.software.utils.TimeUtil;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class FeedbackListAdapter extends RecyclerView.Adapter implements View.OnClickListener {
private Context mContext;
    private List<FeedBackInfo> mFeedBackInfos;
    private LayoutInflater inflater;
private OnItemClickListener onItemClickListener;
public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
public interface OnItemClickListener {
void onItemClick(int pos);
    }
public FeedbackListAdapter(Context context, List<FeedBackInfo> feedBackInfos) {
        this.mContext = context;
        this.mFeedBackInfos = feedBackInfos;
        inflater = LayoutInflater.from(mContext);
}
public void updateView(List<FeedBackInfo> feedBackInfos) {
        this.mFeedBackInfos = feedBackInfos;
        notifyDataSetChanged();
}
@Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
return new FeedbackViewHolder(inflater.inflate(R.layout.feedback_history_item, parent, false));
}
@Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final FeedBackInfo feedBackInfo = mFeedBackInfos.get(position);
final FeedbackViewHolder feedbackViewHolder = (FeedbackViewHolder) holder;
        String content = feedBackInfo.getOpinion();
        for (int i = 0; feedBackInfo.getEnclosure() != null && i < feedBackInfo.getEnclosure().size(); i++) {
            content += "[图片]";
        }
if(feedBackInfo.getIs_reply()==1&&feedBackInfo.getIs_user_read()==0){
            feedbackViewHolder.noRead.setVisibility(View.VISIBLE);
        }else{
            feedbackViewHolder.noRead.setVisibility(View.INVISIBLE);
        }
        feedbackViewHolder.content.setText(content);
        feedbackViewHolder.time.setText(TimeUtil.formatDateTime(feedBackInfo.getCreated_at()));
feedbackViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
}
@Override
    public int getItemCount() {
if (mFeedBackInfos != null) {
            return mFeedBackInfos.size();
        } else {
            return 0;
        }
}
@Override
    public void onClick(View v) {
}
public class FeedbackViewHolder extends RecyclerView.ViewHolder {
@BindView(R.id.content)
        TextView content;
        @BindView(R.id.noRead)
        TextView noRead;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.root)
        LinearLayout root;
public FeedbackViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
