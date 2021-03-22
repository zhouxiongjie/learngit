package com.shuangling.software.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.palette.graphics.Palette;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.shuangling.software.entity.BannerColorInfo;
import com.shuangling.software.interf.LoadImageInterface;

import java.util.List;

import io.sentry.util.Nullable;

/**
 * 项目名称：AntFM
 * 创建人：YoungBean
 * 创建时间：2021/3/5 10:26
 * 类描述：通过fresco、pattle获取bannerview中bitmap的color封装成list方便外部获取
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class BannerViewImageLoader implements LoadImageInterface {
    private Context context;
    private List<BannerColorInfo> colorList;
    private Palette palette;



    public BannerViewImageLoader(List<BannerColorInfo> colorList) {
        this.colorList = colorList;
    }



    /**
     * 获取bitmap，方便palette获取当前bitmap的color值并封装到list中
     */
    @Override
    public void getBitmap(Uri uri) {
        /**
         * fresco请求图片加载：
         * step1:Builder模式初始化ImageRequest，分别通过uri和缓存资源id获取图片资源
         * step2:初始化ImagePipeline,负责加载图像
         *
         * 另：获取Bitmap需要另外获取datasource进行监听，回调成功通过网络请求或者本地缓存获取bitmap
         */
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri).build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(imageRequest, context);

        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(@Nullable Bitmap bitmap) {
                //回调成功，取得bitmap，调用palette进行bitmap颜色获取并封装
                setColorList(bitmap, uri.toString());
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }
        }, CallerThreadExecutor.getInstance());

    }

    /**
     * Vibrant （有活力）
     * Vibrant dark（有活力 暗色）
     * Vibrant light（有活力 亮色）
     * Muted （柔和）
     * Muted dark（柔和 暗色）
     * Muted light（柔和 亮色）
     */
    private void setColorList(Bitmap bitmap, String imgUri) {
        //防止null
        if (imgUri == null) {
            return;
        }
        //初始化palette
        palette = Palette.from(bitmap).generate();

        for (int i = 0; i < colorList.size(); i++) {
            if (colorList.get(i).getImgUrl().equals(imgUri))//imguri作为标识位
            {
                if (palette.getVibrantSwatch() != null) {
                    colorList.get(i).setVibrantColor(palette.getVibrantSwatch().getRgb());
                }
                if (palette.getDarkVibrantSwatch() != null) {
                    colorList.get(i).setVibrantDarkColor(palette.getDarkVibrantSwatch().getRgb());
                }
                if (palette.getLightVibrantSwatch() != null) {
                    colorList.get(i).setVibrantLightColor(palette.getLightVibrantSwatch().getRgb());
                }
                if (palette.getMutedSwatch() != null) {
                    colorList.get(i).setMutedColor(palette.getMutedSwatch().getRgb());
                }
                if (palette.getDarkMutedSwatch() != null) {
                    colorList.get(i).setMutedDarkColor(palette.getDarkMutedSwatch().getRgb());
                }
                if (palette.getLightMutedSwatch() != null) {
                    colorList.get(i).setMutedLightColor(palette.getLightMutedSwatch().getRgb());
                }
                if (palette.getDominantSwatch() != null){
                    colorList.get(i).setDominantColor(palette.getDominantSwatch().getRgb());
                }
            }
        }
    }


    public int getVibrantColor(int position) {
        return colorList.get(position).getVibrantColor();
    }

    public int getVibrantDarkColor(int position) {
        return colorList.get(position).getVibrantDarkColor();
    }

    public int getVibrantLightColor(int position) {
        return colorList.get(position).getVibrantLightColor();
    }

    public int getMutedColor(int position) {
        return colorList.get(position).getMutedColor();
    }

    public int getMutedDarkColor(int position) {
        return colorList.get(position).getMutedDarkColor();
    }

    public int getMutedLightColor(int position) {
        return colorList.get(position).getMutedLightColor();
    }

    public int getDominantColor(int position){
        return colorList.get(position).getDominantColor();
    }


}
