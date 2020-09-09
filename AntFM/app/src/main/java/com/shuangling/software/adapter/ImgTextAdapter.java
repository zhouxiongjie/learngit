package com.shuangling.software.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.shuangling.software.R;
import com.shuangling.software.activity.ArticleDetailActivity02;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.activity.SpecialDetailActivity;
import com.shuangling.software.activity.VideoDetailActivity;
import com.shuangling.software.activity.WebViewBackActivity;
import com.shuangling.software.customview.MyGridView;
import com.shuangling.software.entity.ImgTextInfo;
import com.shuangling.software.entity.ServerCategory;
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
public class ImgTextAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    //"type": 4,//1 音频 2 专辑 3 文章 4 视频 5专题

    public static final int TYPE_ONE = 1;               //无图或者一图
    public static final int TYPE_TWO = 2;               //二图
    public static final int TYPE_OTHER = 3;             //三图


    private Context mContext;
    private List<ImgTextInfo> mImgTextInfos;
    private LayoutInflater inflater;


    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {

        void onItemClick(int pos);
    }


    public ImgTextAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);

    }

    public ImgTextAdapter(Context context, List<ImgTextInfo> imgTextInfos) {
        this.mContext = context;
        this.mImgTextInfos = imgTextInfos;
        inflater = LayoutInflater.from(mContext);

    }

    public void updateView(List<ImgTextInfo> imgTextInfos) {
        this.mImgTextInfos = imgTextInfos;
        notifyDataSetChanged();

    }

    public List<ImgTextInfo> getData() {
        return mImgTextInfos;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if (viewType == TYPE_ONE) {
            return new OneViewHolder(inflater.inflate(R.layout.img_text_one_item, parent, false));
        } else if (viewType == TYPE_TWO) {
            return new TwoViewHolder(inflater.inflate(R.layout.img_text_two_item, parent, false));
        } else {
            return new OtherViewHolder(inflater.inflate(R.layout.img_text_other_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ImgTextInfo content = mImgTextInfos.get(position);
        int viewType=getItemViewType(position);
        if ( viewType== TYPE_ONE) {
            final OneViewHolder oneViewHolder = (OneViewHolder) holder;
            oneViewHolder.name.setText(content.getNickName());
            oneViewHolder.time.setText(TimeUtil.formatDateTime(content.getSendTime()));
            oneViewHolder.content.setText(content.getContents());
            if (!TextUtils.isEmpty(content.getUserLog())) {
                Uri uri = Uri.parse(content.getUserLog());
                int width = CommonUtils.dip2px(30);
                int height = width;
                ImageLoader.showThumb(uri, oneViewHolder.head, width, height);
            } else {
                ImageLoader.showThumb(oneViewHolder.head, R.drawable.ic_user1);
            }

            if(content.getImgArray()==null||content.getImgArray().size()==0){
                oneViewHolder.image.setVisibility(View.GONE);
            }else{

                if (!TextUtils.isEmpty(content.getImgArray().get(0))) {


                    ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(
                                String id,
                                @Nullable ImageInfo imageInfo,
                                @Nullable Animatable anim) {
                            if (imageInfo == null) {
                                return;
                            }
                            QualityInfo qualityInfo = imageInfo.getQualityInfo();
                            FLog.d("Final image received! " +
                                            "Size %d x %d",
                                    "Quality level %d, good enough: %s, full quality: %s",
                                    imageInfo.getWidth(),
                                    imageInfo.getHeight(),
                                    qualityInfo.getQuality(),
                                    qualityInfo.isOfGoodEnoughQuality(),
                                    qualityInfo.isOfFullQuality());
                            float ratio = (float) imageInfo.getWidth() / (float) imageInfo.getHeight();
                            oneViewHolder.image.setAspectRatio(ratio);


                        }

                        @Override
                        public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {

                        }

                        @Override
                        public void onFailure(String id, Throwable throwable) {

                        }
                    };

                    Uri uri = Uri.parse(content.getImgArray().get(0));
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setControllerListener(controllerListener)
                            .setUri(uri)
                            // other setters
                            .build();
                    oneViewHolder.image.setController(controller);

                }

                oneViewHolder.image.setVisibility(View.VISIBLE);
            }


        } else if (getItemViewType(position) == TYPE_TWO) {
            final TwoViewHolder twoViewHolder = (TwoViewHolder) holder;
            twoViewHolder.name.setText(content.getNickName());
            twoViewHolder.time.setText(TimeUtil.formatDateTime(content.getSendTime()));
            twoViewHolder.content.setText(content.getContents());
            if (!TextUtils.isEmpty(content.getUserLog())) {
                Uri uri = Uri.parse(content.getUserLog());
                int width = CommonUtils.dip2px(30);
                int height = width;
                ImageLoader.showThumb(uri, twoViewHolder.head, width, height);
            } else {
                ImageLoader.showThumb(twoViewHolder.head, R.drawable.ic_user1);
            }


            if (!TextUtils.isEmpty(content.getImgArray().get(0))) {

                Uri uri = Uri.parse(content.getImgArray().get(0));
                int width = (CommonUtils.getScreenWidth()-CommonUtils.dip2px(90))/2;
                int height = (int)(width/1.5);
                ImageLoader.showThumb(uri, twoViewHolder.image01, width, height);
            }
            if (!TextUtils.isEmpty(content.getImgArray().get(1))) {
                Uri uri = Uri.parse(content.getImgArray().get(1));
                int width = (CommonUtils.getScreenWidth()-CommonUtils.dip2px(90))/2;
                int height = (int)(width/1.5);
                ImageLoader.showThumb(uri, twoViewHolder.image02, width, height);
            }






        } else  {
            final OtherViewHolder otherViewHolder = (OtherViewHolder) holder;

            otherViewHolder.name.setText(content.getNickName());
            otherViewHolder.time.setText(TimeUtil.formatDateTime(content.getSendTime()));
            otherViewHolder.content.setText(content.getContents());
            if (!TextUtils.isEmpty(content.getUserLog())) {
                Uri uri = Uri.parse(content.getUserLog());
                int width = CommonUtils.dip2px(30);
                int height = width;
                ImageLoader.showThumb(uri, otherViewHolder.head, width, height);
            } else {
                ImageLoader.showThumb(otherViewHolder.head, R.drawable.ic_user1);
            }



            final ImgTextGridViewAdapter adapter = new ImgTextGridViewAdapter(mContext, content.getImgArray());
            otherViewHolder.gridView.setAdapter(adapter);
            otherViewHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });



        }
    }


    @Override
    public int getItemCount() {

        if (mImgTextInfos != null) {
            return mImgTextInfos.size();
        } else {
            return 0;
        }

    }

    @Override
    public void onClick(View v) {

    }


    public class OneViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.head)
        SimpleDraweeView head;
        @BindView(R.id.emcee)
        TextView emcee;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.image)
        SimpleDraweeView image;

        public OneViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public class TwoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.head)
        SimpleDraweeView head;
        @BindView(R.id.emcee)
        TextView emcee;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.image01)
        SimpleDraweeView image01;
        @BindView(R.id.image02)
        SimpleDraweeView image02;

        public TwoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public class OtherViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.head)
        SimpleDraweeView head;
        @BindView(R.id.emcee)
        TextView emcee;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.gridView)
        MyGridView gridView;

        public OtherViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public int getItemViewType(int position) {

        List<String> list=mImgTextInfos.get(position).getImgArray();
        if (list==null||list.size() == 0||list.size() == 1) {
            return TYPE_ONE;
        } else if (list.size() == 2) {
            return TYPE_TWO;
        } else {
            return TYPE_OTHER;
        }

    }


}
