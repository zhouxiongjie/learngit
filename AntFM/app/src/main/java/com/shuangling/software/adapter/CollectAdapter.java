package com.shuangling.software.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.ArticleDetailActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.activity.SingleAudioDetailActivity;
import com.shuangling.software.activity.SpecialDetailActivity;
import com.shuangling.software.activity.VideoDetailActivity;
import com.shuangling.software.entity.Collect;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class CollectAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    //"type": 4,//1 音频 2 专辑 3 文章 4 视频 5专题

    //public static final int TYPE_AUDIO = 0;             //音频
    public static final int TYPE_ARTICLE = 2;           //文章
    public static final int TYPE_VIDEO = 3;             //视频
    public static final int TYPE_SPECIAL = 4;           //专题
    //public static final int TYPE_RADIO = 5;           //电台
    public static final int TYPE_GALLERIE = 7;          //图集


    private Context mContext;
    private List<Collect> mCollects;
    private LayoutInflater inflater;
    private boolean mEditorMode = false;
    private int[] mItemSelected;
    public void setEditorMode(boolean editorMode) {
        if(editorMode){
            mItemSelected = new int[mCollects.size()];
            resetItemSelected();
        }
        this.mEditorMode = editorMode;
        notifyDataSetChanged();
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {

        void onItemClick(int pos);
    }


    public CollectAdapter(Context context, List<Collect> collects) {
        this.mContext = context;
        this.mCollects = collects;
        inflater = LayoutInflater.from(mContext);

    }

    public void setData(List<Collect> collects) {
        this.mCollects = collects;

    }

    public List<Collect> getData() {
        return mCollects;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


//        if (viewType == TYPE_AUDIO) {
//            return new AudioViewHolder(inflater.inflate(R.layout.history_audio_item, parent, false));
//        } else
        if (viewType == TYPE_ARTICLE) {
            return new ArticleViewHolder(inflater.inflate(R.layout.history_article_item, parent, false));
        } else if (viewType == TYPE_VIDEO) {
            return new VideoViewHolder(inflater.inflate(R.layout.history_video_item, parent, false));
        } else if (viewType == TYPE_SPECIAL) {
            return new SpecialViewHolder(inflater.inflate(R.layout.history_special_item, parent, false));
        } else {
            return new GallerieViewHolder(inflater.inflate(R.layout.history_gallerie_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Collect content = mCollects.get(position);
//        if (getItemViewType(position) == TYPE_AUDIO) {
//            final AudioViewHolder audioViewHolder = (AudioViewHolder) holder;
//            if (!TextUtils.isEmpty(content.getCover())) {
//                Uri uri = Uri.parse(content.getCover());
//                int width = CommonUtils.dip2px(70);
//                int height = width;
//                ImageLoader.showThumb(uri, audioViewHolder.logo, width, height);
//            }
//            if(mEditorMode){
//                audioViewHolder.checkBox.setVisibility(View.VISIBLE);
//                if(mItemSelected[position]==1){
//                    audioViewHolder.checkBox.setChecked(true);
//                }else{
//                    audioViewHolder.checkBox.setChecked(false);
//                }
//            }else{
//                audioViewHolder.checkBox.setVisibility(View.GONE);
//            }
//
//            audioViewHolder.title.setText(content.getTitle());
//            audioViewHolder.name.setText(content.getDes());
//            audioViewHolder.duration.setText(CommonUtils.getShowTime((long) Float.parseFloat(content..getAudio().getDuration())));
//            audioViewHolder.root.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(mEditorMode){
//                        if(audioViewHolder.checkBox.isChecked()){
//                            audioViewHolder.checkBox.setChecked(false);
//                            mItemSelected[position]=0;
//                        }else {
//                            audioViewHolder.checkBox.setChecked(true);
//                            mItemSelected[position]=1;
//                        }
//
//                    }else{
//                        Intent it = new Intent(mContext, SingleAudioDetailActivity.class);
//                        it.putExtra("audioId", content.getPost_id());
//                        mContext.startActivity(it);
//                    }
//
//                }
//            });
//
//        } else
        if (getItemViewType(position) == TYPE_ARTICLE) {
            final ArticleViewHolder articleViewHolder = (ArticleViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, articleViewHolder.logo, width, height);
            }else{
                ImageLoader.showThumb(articleViewHolder.logo, R.drawable.article_placeholder);
            }
            if(mEditorMode){
                articleViewHolder.checkBox.setVisibility(View.VISIBLE);
                if(mItemSelected[position]==1){
                    articleViewHolder.checkBox.setChecked(true);
                }else{
                    articleViewHolder.checkBox.setChecked(false);
                }
            }else{
                articleViewHolder.checkBox.setVisibility(View.GONE);
            }
            articleViewHolder.title.setText(content.getTitle());
            //articleViewHolder.time.setText(content.getCreated_at());
            articleViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mEditorMode){
                        if(articleViewHolder.checkBox.isChecked()){
                            articleViewHolder.checkBox.setChecked(false);
                            mItemSelected[position]=0;
                        }else {
                            articleViewHolder.checkBox.setChecked(true);
                            mItemSelected[position]=1;
                        }

                    }else{
                        Intent it = new Intent(mContext, ArticleDetailActivity.class);
                        it.putExtra("articleId", content.getId());
                        mContext.startActivity(it);
                    }

                }
            });
        } else if (getItemViewType(position) == TYPE_VIDEO) {
            final VideoViewHolder videoViewHolder = (VideoViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, videoViewHolder.logo, width, height);
            }else{
                ImageLoader.showThumb(videoViewHolder.logo, R.drawable.video_placeholder);
            }

            if(mEditorMode){
                videoViewHolder.checkBox.setVisibility(View.VISIBLE);
                if(mItemSelected[position]==1){
                    videoViewHolder.checkBox.setChecked(true);
                }else{
                    videoViewHolder.checkBox.setChecked(false);
                }
            }else{
                videoViewHolder.checkBox.setVisibility(View.GONE);
            }
            videoViewHolder.title.setText(content.getTitle());
            if(!TextUtils.isEmpty(content.getVideo().getDuration())){
                videoViewHolder.duration.setText(CommonUtils.getShowTime((long) Float.parseFloat(content.getVideo().getDuration()) * 1000));
            }else{
                videoViewHolder.duration.setText("00:00");
            }
            videoViewHolder.progress.setText("暂无");
            videoViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mEditorMode){
                        if(videoViewHolder.checkBox.isChecked()){
                            videoViewHolder.checkBox.setChecked(false);
                            mItemSelected[position]=0;
                        }else {
                            videoViewHolder.checkBox.setChecked(true);
                            mItemSelected[position]=1;
                        }

                    }else {
                        Intent it = new Intent(mContext, VideoDetailActivity.class);
                        it.putExtra("videoId", content.getId());
                        mContext.startActivity(it);
                    }

                }
            });

        } else if (getItemViewType(position) == TYPE_SPECIAL) {
            final SpecialViewHolder specialViewHolder = (SpecialViewHolder) holder;

            if (!TextUtils.isEmpty(content.getCover())) {
                Uri uri = Uri.parse(content.getCover());
                int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, specialViewHolder.logo, width, height);
            }else{
                ImageLoader.showThumb(specialViewHolder.logo, R.drawable.article_placeholder);
            }

            if(mEditorMode){
                specialViewHolder.checkBox.setVisibility(View.VISIBLE);
                if(mItemSelected[position]==1){
                    specialViewHolder.checkBox.setChecked(true);
                }else{
                    specialViewHolder.checkBox.setChecked(false);
                }
            }else{
                specialViewHolder.checkBox.setVisibility(View.GONE);
            }

            specialViewHolder.title.setText(content.getTitle());
            //specialViewHolder.time.setText(content.getCreated_at());
            specialViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mEditorMode){
                        if(specialViewHolder.checkBox.isChecked()){
                            specialViewHolder.checkBox.setChecked(false);
                            mItemSelected[position]=0;
                        }else {
                            specialViewHolder.checkBox.setChecked(true);
                            mItemSelected[position]=1;
                        }

                    }else {
                        Intent it = new Intent(mContext, SpecialDetailActivity.class);
                        it.putExtra("specialId", content.getId());
                        mContext.startActivity(it);
                    }

                }
            });


        } else {
            final GallerieViewHolder gallerieViewHolder = (GallerieViewHolder) holder;

            if (content.getGallerie().getCovers()!=null&&content.getGallerie().getCovers().size()>0&&!TextUtils.isEmpty(content.getGallerie().getCovers().get(0))) {
                Uri uri = Uri.parse(content.getGallerie().getCovers().get(0));
                int width = (int) mContext.getResources().getDimension(R.dimen.article_right_image_width);
                int height = (int) (2f * width / 3f);
                ImageLoader.showThumb(uri, gallerieViewHolder.logo, width, height);
            }else{
                ImageLoader.showThumb(gallerieViewHolder.logo, R.drawable.article_placeholder);
            }

            if(mEditorMode){
                gallerieViewHolder.checkBox.setVisibility(View.VISIBLE);
                if(mItemSelected[position]==1){
                    gallerieViewHolder.checkBox.setChecked(true);
                }else{
                    gallerieViewHolder.checkBox.setChecked(false);
                }
            }else{
                gallerieViewHolder.checkBox.setVisibility(View.GONE);
            }

            gallerieViewHolder.title.setText(content.getTitle());
            gallerieViewHolder.count.setText(content.getGallerie().getCount()+"图");
            //gallerieViewHolder.time.setText(content.getCreated_at());
            gallerieViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mEditorMode){
                        if(gallerieViewHolder.checkBox.isChecked()){
                            gallerieViewHolder.checkBox.setChecked(false);
                            mItemSelected[position]=0;
                        }else {
                            gallerieViewHolder.checkBox.setChecked(true);
                            mItemSelected[position]=1;
                        }

                    }else{
                        Intent it = new Intent(mContext, GalleriaActivity.class);
                        it.putExtra("galleriaId", content.getId());
                        mContext.startActivity(it);
                    }

                }
            });

        }
    }


    @Override
    public int getItemCount() {

        if (mCollects != null) {
            return mCollects.size();
        } else {
            return 0;
        }

    }

    @Override
    public void onClick(View v) {

    }


    public class AudioViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.checkBox)
        CheckBox checkBox;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.duration)
        TextView duration;
        @BindView(R.id.progress)
        TextView progress;
        @BindView(R.id.root)
        LinearLayout root;

        public AudioViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public class SpecialViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.checkBox)
        CheckBox checkBox;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.root)
        LinearLayout root;

        public SpecialViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }


    public class VideoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.checkBox)
        CheckBox checkBox;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.logoLayout)
        RelativeLayout logoLayout;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.duration)
        TextView duration;
        @BindView(R.id.progress)
        TextView progress;
        @BindView(R.id.root)
        LinearLayout root;

        public VideoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.checkBox)
        CheckBox checkBox;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.root)
        LinearLayout root;

        public ArticleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class RadioViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.root)
        RelativeLayout root;

        public RadioViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public class TvViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.root)
        RelativeLayout root;

        public TvViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public class GallerieViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.checkBox)
        CheckBox checkBox;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.logoLayout)
        RelativeLayout logoLayout;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.root)
        LinearLayout root;

        public GallerieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public int getItemViewType(int position) {

//        if (mCollects.get(position).getType() == 1) {
//            return TYPE_AUDIO;
//        } else
        if (mCollects.get(position).getType() == 3) {
            return TYPE_ARTICLE;
        } else if (mCollects.get(position).getType() == 4) {
            return TYPE_VIDEO;
        } else if (mCollects.get(position).getType() == 5) {
            return TYPE_SPECIAL;
        } else {
            return TYPE_GALLERIE;
        }

    }


    static class ViewHolder {
        @BindView(R.id.checkBox)
        CheckBox checkBox;
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.logoLayout)
        RelativeLayout logoLayout;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.root)
        LinearLayout root;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    public void resetItemSelected() {
        for (int i = 0; i < mItemSelected.length; i++) {
            mItemSelected[i] = 0;
        }
    }

    public void selectAllItem() {
        for (int i = 0; i < mItemSelected.length; i++) {
            mItemSelected[i] = 1;
        }
        notifyDataSetChanged();
    }

    public int[] getItemSelected() {
        return mItemSelected;
    }
}
