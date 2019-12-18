package com.shuangling.software.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.entity.ColumnContent;
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
public class ColumnAlbumContentAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    public static final int TYPE_ALBUM_NORMAL = 1;             //专辑左文右图
    public static final int TYPE_ALBUM_SQUARE = 2;             //正方形专辑  上图下文


    private Context mContext;
    private List<ColumnContent> mColumnContent;
    private LayoutInflater inflater;

    public void setMode(int mode) {
        this.mode = mode;
    }

    private int mode=1;                                         //1  三图    2 三图+5图

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, ColumnContent content);

        void onItemClick(View view, int pos);
    }

    public ColumnAlbumContentAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);

    }

    public ColumnAlbumContentAdapter(Context context, List<ColumnContent> columnContent) {
        this.mContext = context;
        this.mColumnContent = columnContent;
        inflater = LayoutInflater.from(mContext);

    }

    public void setData(List<ColumnContent> columnContent) {
        this.mColumnContent = columnContent;

    }


    public List<ColumnContent> getData() {
        return this.mColumnContent;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if (viewType == TYPE_ALBUM_NORMAL) {
            return new AlbumViewHolder(inflater.inflate(R.layout.content_album_item, parent, false));
        } else {
            return new AlbumSquareViewHolder(inflater.inflate(R.layout.scrollview_album_item_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ColumnContent content=mColumnContent.get(position);

        int itemViewType = getItemViewType(position);
        if (itemViewType == TYPE_ALBUM_NORMAL) {
            final AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;
            albumViewHolder.root.setPadding(CommonUtils.dip2px(5),CommonUtils.dip2px(10),CommonUtils.dip2px(5),CommonUtils.dip2px(10));
            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, albumViewHolder.logo, width, height);
            }
            if (content.getAuthor_info() != null && content.getAuthor_info().getMerchant() != null) {
                albumViewHolder.merchant.setText(content.getAuthor_info().getMerchant().getName());
            }

            if (albumViewHolder.title.getTag() != null) {
                ViewTreeObserver.OnPreDrawListener listener = (ViewTreeObserver.OnPreDrawListener) albumViewHolder.title.getTag();
                albumViewHolder.title.getViewTreeObserver().removeOnPreDrawListener(listener);
            }

            ViewTreeObserver.OnPreDrawListener listener = new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    Log.i("test", "albumViewHolder onPreDraw");
                    Log.i("test", content.getTitle());
                    ViewTreeObserver obs = albumViewHolder.title.getViewTreeObserver();
                    obs.removeOnPreDrawListener(this);
                    albumViewHolder.title.setTag(null);
                    if (albumViewHolder.title.getLineCount() > 2) {
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                        layoutParams.topMargin = CommonUtils.dip2px(5);
                        layoutParams.addRule(RelativeLayout.BELOW, R.id.logo);
                        albumViewHolder.layout.setLayoutParams(layoutParams);
                    } else {


                        if (albumViewHolder.layout.getWidth() > albumViewHolder.title.getWidth()) {
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                            layoutParams.topMargin = CommonUtils.dip2px(5);
                            layoutParams.addRule(RelativeLayout.BELOW, R.id.logo);
                            albumViewHolder.layout.setLayoutParams(layoutParams);

                        } else {
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//工具类哦
                            layoutParams.topMargin = CommonUtils.dip2px(5);
                            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.logo);
                            albumViewHolder.layout.setLayoutParams(layoutParams);
                        }

                    }
                    return true;
                }
            };
            albumViewHolder.title.setTag(listener);
            albumViewHolder.title.getViewTreeObserver().addOnPreDrawListener(listener);
            albumViewHolder.publishTime.setText(TimeUtil.formatDateTime(content.getPublish_at()));
            if (content.getAlbums().getStatus() == 1) {
                //已完结
                albumViewHolder.title.setText(CommonUtils.tagKeyword("完~" + content.getTitle(), "完~", CommonUtils.getThemeColor(mContext)));
            } else {
                albumViewHolder.title.setText(content.getTitle());
            }

            albumViewHolder.count.setText("" + content.getAlbums().getCount() + "集");
            if(content.getComment()>=1){
                albumViewHolder.commentNum.setText(content.getComment()+"评论");
            }else if(content.getView()>=10&&content.getComment()<1){
                albumViewHolder.commentNum.setText(content.getView()+"阅读");
            }else{
                albumViewHolder.commentNum.setText("");
            }
            albumViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, AlbumDetailActivity.class);
                    it.putExtra("albumId", content.getId());

                    mContext.startActivity(it);
                }
            });

        } else if (itemViewType == TYPE_ALBUM_SQUARE) {
            AlbumSquareViewHolder albumViewHolder = (AlbumSquareViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = CommonUtils.getScreenWidth()/3;
                int height = width;
                ImageLoader.showThumb(uri, albumViewHolder.logo, width, height);
            }

            if (content.getAlbums().getStatus() == 1) {
                //已完结
                albumViewHolder.title.setText(CommonUtils.tagKeyword("完~" + content.getTitle(), "完~", CommonUtils.getThemeColor(mContext)));
            } else {
                albumViewHolder.title.setText(content.getTitle());
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
    }


    @Override
    public void onClick(View v) {

    }


    public class HeadViewHolder extends RecyclerView.ViewHolder {


        public HeadViewHolder(View view) {
            super(view);

        }
    }


    public class AlbumViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.merchant)
        TextView merchant;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.root)
        LinearLayout root;
        @BindView(R.id.commentNum)
        TextView commentNum;
        @BindView(R.id.layout)
        LinearLayout layout;

        public AlbumViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public class AlbumSquareViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.duration)
        TextView duration;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.root)
        LinearLayout root;

        public AlbumSquareViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public int getItemCount() {
        if (mColumnContent != null) {
            return mColumnContent.size();
        } else {
            return 0;
        }

    }

    @Override
    public int getItemViewType(int position) {
        int size=getItemCount();
        if(mode==1){
            //三图模式
            int page = size / 3;
            if (page > 0 && (position + 1) <= 3 * page) {
                return TYPE_ALBUM_SQUARE;
            } else {
                return TYPE_ALBUM_NORMAL;
            }
        }else{
            int page=size/8;
            if (page>0&&(position+1)<=8*page) {
                if((position+1)%8>0&&(position+1)%8<=3){
                    return TYPE_ALBUM_SQUARE;
                }else{
                    return TYPE_ALBUM_NORMAL;
                }
            }else{
                if(size%8==1||size%8==2){
                    return TYPE_ALBUM_NORMAL;
                }else if(size%8==3){
                    return TYPE_ALBUM_SQUARE;
                }else{
                    if((position+1)%8<=3&&(position+1)%8>0){
                        return TYPE_ALBUM_SQUARE;
                    }else {
                        return TYPE_ALBUM_NORMAL;
                    }
                }
            }
        }



    }



}
