package com.shuangling.software.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.entity.ChatMessage;
import com.shuangling.software.entity.Viewer;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class LiveViewerListAdapter extends RecyclerView.Adapter {

    int TYPE_ITEM = 0x1;
    int TYPE_OTHER = 0x2;

    private LayoutInflater inflater;
    private Context mContext;
    private int sumPeopel;
    List<Viewer> mViewers = new ArrayList<>();

    public interface OnItemReply {
        void replyItem(ChatMessage message);
    }

    public void setOnItemReply(LiveChatListAdapter.OnItemReply onItemReply) {

    }


    public LiveViewerListAdapter(Context context, List<Viewer> viewers,int sum) {

        mContext = context;
        mViewers = viewers;
        sumPeopel=sum;
        inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if (viewType == TYPE_ITEM) {
            return new ViewerViewHolder(inflater.inflate(R.layout.live_viewer_item, viewGroup, false));
        } else {
            return new OtherViewHolder(inflater.inflate(R.layout.live_viewer_other_item, viewGroup, false));
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int type=getItemViewType(position);
        if(type==TYPE_ITEM){
            ViewerViewHolder vh = (ViewerViewHolder) viewHolder;
            Viewer viewer = mViewers.get(position);
            vh.order.setText("" + (position + 1));
            vh.name.setText(viewer.getNick_name());

            if (!TextUtils.isEmpty(viewer.getAvatar())) {
                Uri uri = Uri.parse(viewer.getAvatar());
                int width = CommonUtils.dip2px(40);
                int height = width;
                ImageLoader.showThumb(uri, vh.logo, width, height);
            } else {
                ImageLoader.showThumb(vh.logo, R.drawable.ic_user3);
            }
        }else {
            OtherViewHolder vh = (OtherViewHolder) viewHolder;
            if(mViewers!=null&&mViewers.size()>=50){
                vh.desc.setText("最多显示50名观众哦");
            }else {
                int number=(mViewers!=null?sumPeopel-mViewers.size():sumPeopel);
                if(number>=1){
                    vh.desc.setText("还有"+number+"名未登录用户在观看");
                }else{
                    vh.root.setVisibility(View.GONE);
                }

            }
        }



    }

    @Override
    public int getItemCount() {
        if (mViewers == null) {
            return 1;
        } else if (mViewers.size() >= 50) {
            return 51;
        } else {
            return mViewers.size() + 1;
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (mViewers == null) {
            return TYPE_OTHER;
        } else if ((mViewers.size() >= 50 && position == 50)) {
            return TYPE_OTHER;
        } else if ((mViewers.size() < 50) && position >= mViewers.size()) {
            return TYPE_OTHER;
        } else {
            return TYPE_ITEM;
        }
    }


    static class ViewerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.order)
        TextView order;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.root)
        RelativeLayout root;


        ViewerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    static class OtherViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.desc)
        TextView desc;
        @BindView(R.id.root)
        RelativeLayout root;
        OtherViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }



}
