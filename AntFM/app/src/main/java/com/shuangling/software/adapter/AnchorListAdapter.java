package com.shuangling.software.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.entity.Anchor;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AnchorListAdapter extends RecyclerView.Adapter <AnchorListAdapter.AnchorViewHolder>{

    private List<Anchor> mAnchors;
    private Context mContext;
    private LayoutInflater mInflater;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {

        void onItemClick(int pos);
    }


    public AnchorListAdapter(Context context, List<Anchor> anchors) {
        super();
        // TODO Auto-generated constructor stub
        this.mAnchors = anchors;
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }


    public void updateView(List<Anchor> anchors) {
        this.mAnchors = anchors;
        notifyDataSetChanged();
    }


    @Override
    public AnchorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new AnchorViewHolder(mInflater.inflate(R.layout.attention_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AnchorViewHolder anchorViewHolder, final int position) {

        Anchor anchor = mAnchors.get(position);
        if (!TextUtils.isEmpty(anchor.getLogo())) {
            Uri uri = Uri.parse(anchor.getLogo());
            int width = CommonUtils.dip2px(65);
            int height = width;
            ImageLoader.showThumb(uri, anchorViewHolder.logo, width, height);

        }else{
            ImageLoader.showThumb(anchorViewHolder.logo,R.drawable.head_placeholder);
        }

        anchorViewHolder.title.setText(anchor.getName());

        anchorViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mAnchors.size();
    }






    public static class AnchorViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.root)
        RelativeLayout root;

        AnchorViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }



}
