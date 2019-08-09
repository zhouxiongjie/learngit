package com.shuangling.software.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.entity.Subscribe;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class SubscribeAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    //"type": 4,//1 音频 2 专辑 3 文章 4 视频 5专题

    public static final int TYPE_ALBUM = 1;             //专辑


    private Context mContext;
    private List<Subscribe> mSubscribes;
    private LayoutInflater inflater;


    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {

        void onItemClick(int pos);
    }


    public SubscribeAdapter(Context context, List<Subscribe> subscribes) {
        this.mContext = context;
        this.mSubscribes = subscribes;
        inflater = LayoutInflater.from(mContext);

    }

    public void updateView(List<Subscribe> subscribes) {
        this.mSubscribes = subscribes;
        notifyDataSetChanged();

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        return new AlbumViewHolder(inflater.inflate(R.layout.own_album_item, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Subscribe content = mSubscribes.get(position);

        final AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;

        if (!TextUtils.isEmpty(content.getCover())) {
            Uri uri = Uri.parse(content.getCover());
            int width = CommonUtils.dip2px(100);
            int height = width;
            ImageLoader.showThumb(uri, albumViewHolder.logo, width, height);
        }
        albumViewHolder.title.setText(content.getTitle());
        albumViewHolder.desc.setText(content.getDes());
        albumViewHolder.count.setText(content.getAlbums().getCount()+"集");
        if(content.getAuthor_info()!=null&&content.getAuthor_info().getMerchant()!=null){
            albumViewHolder.organization.setText(content.getAuthor_info().getMerchant().getName());
        }
        albumViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, AlbumDetailActivity.class);
                it.putExtra("albumId", content.getId());
                mContext.startActivity(it);
            }
        });

    }


    @Override
    public int getItemCount() {

        if (mSubscribes != null) {
            return mSubscribes.size();
        } else {
            return 0;
        }

    }


    @Override
    public int getItemViewType(int position) {
        return TYPE_ALBUM;
    }

    @Override
    public void onClick(View v) {

    }


    public class AlbumViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.desc)
        TextView desc;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.organization)
        TextView organization;
        @BindView(R.id.root)
        RelativeLayout root;

        public AlbumViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }




}
