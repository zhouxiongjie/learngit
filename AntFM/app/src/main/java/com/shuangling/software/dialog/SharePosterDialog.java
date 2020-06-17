package com.shuangling.software.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.svideo.common.utils.ToastUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.zxing.encoding.EncodingHandler;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.customview.SlideSelectView;
import com.shuangling.software.entity.Article;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.ViewBitmapUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;


/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class SharePosterDialog extends BaseCircleDialog {

    public static final String FONT_SIZE = "font_size";

    @BindView(R.id.download)
    LinearLayout download; //海报
    @BindView(R.id.weiXin)
    LinearLayout weiXin;
    @BindView(R.id.weiXinFriends)
    LinearLayout weiXinFriends;
    @BindView(R.id.weiXinCollect)
    LinearLayout weiXinCollect;
    @BindView(R.id.qq)
    LinearLayout qq;
    @BindView(R.id.qqZone)
    LinearLayout qqZone;
    @BindView(R.id.weibo)
    LinearLayout weibo;
    @BindView(R.id.cancel)
    TextView cancel;


    Unbinder unbinder;
    @BindView(R.id.iv_cover)
    SimpleDraweeView mIvCover;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_des)
    TextView mTvDes;
    @BindView(R.id.iv_app_icon)
    SimpleDraweeView mIvAppIcon;
    @BindView(R.id.iv_qr_code)
    ImageView mIvQrCode;
    @BindView(R.id.layout_poster)
    LinearLayout mLayoutPoster;
    @BindView(R.id.fontSizeBar)
    SlideSelectView mFontSizeBar;
    @BindView(R.id.selectionsLayout)
    LinearLayout mSelectionsLayout;
    Unbinder unbinder1;

    private Article mArticle;


    public interface ShareHandler {
        void onShare(String platform,Bitmap bitmap);
        void download(Bitmap bitmap);
    }

    public void setShareHandler(ShareHandler shareHandler) {
        this.mShareHandler = shareHandler;
    }

    public Article getArticle() {
        return mArticle;
    }

    public void setArticle(Article article) {
        this.mArticle = article;
    }

    private ShareHandler mShareHandler;


    public static SharePosterDialog getInstance(Article article) {
        SharePosterDialog dialogFragment = new SharePosterDialog();
        dialogFragment.setCanceledBack(true);
        dialogFragment.setCanceledOnTouchOutside(true);
        dialogFragment.setGravity(Gravity.BOTTOM);
        dialogFragment.setWidth(1f);
        dialogFragment.setArticle(article);
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_share_poster, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        unbinder = ButterKnife.bind(this, rootView);

        mTvTitle.setText(mArticle.getTitle());
        if(mArticle.getArticle() != null && mArticle.getArticle().getCovers() !=null &&  mArticle.getArticle().getCovers().size()>0){
           String cover = mArticle.getArticle().getCovers().get(0);
            Uri uri = Uri.parse(cover);
            int width = CommonUtils.dip2px(375);
            int height = width;
            ImageLoader.showThumb(uri, mIvCover, width, height);
            mIvCover.setVisibility(View.VISIBLE);
            if(mArticle.getDes() !=null ) {
                if(mArticle.getDes().length()>200) {
                    mTvDes.setText(mArticle.getDes().substring(0,199) + "...");
                }else {
                    mTvDes.setText(mArticle.getDes());
                }
            }
        }else {
            mIvCover.setVisibility(View.GONE);
            if(mArticle.getDes() !=null ) {
                if(mArticle.getDes().length()>400) {
                    mTvDes.setText(mArticle.getDes().substring(0,399) + "...");
                }else {
                    mTvDes.setText(mArticle.getDes());
                }
            }
        }

        //填充二维码
        String url = ServerInfo.h5IP + ServerInfo.getArticlePage + mArticle.getId();
        int qrWidth =  CommonUtils.dip2px(60);
        try {
            Bitmap bitmapQRCode = EncodingHandler.createQRCode(url,qrWidth);
            mIvQrCode.setImageBitmap(bitmapQRCode);
        }catch (Exception e){

        }


    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.download, R.id.weiXin, R.id.weiXinCollect, R.id.weiXinFriends, R.id.weibo, R.id.qq, R.id.qqZone, R.id.cancel})
    public void onViewClicked(View view) {

        //mLayoutPoster

        Bitmap bitmap = ViewBitmapUtils.getViewBitmap(mLayoutPoster);


        if(bitmap == null) {
            ToastUtils.show(getContext(),"生成图片失败");
            return;
        }

        switch (view.getId()) {

            case R.id.download:
                if (mShareHandler != null) {
                    mShareHandler.download(bitmap);
                }
                dismiss();

                break;
            case R.id.weiXin:
                if (mShareHandler != null) {
                    mShareHandler.onShare(Wechat.NAME,bitmap);
                }
                dismiss();

                break;
            case R.id.weiXinFriends:
                if (mShareHandler != null) {
                    mShareHandler.onShare(WechatMoments.NAME,bitmap);
                }
                dismiss();
                break;
            case R.id.weiXinCollect:
                if (mShareHandler != null) {
                    mShareHandler.onShare(WechatFavorite.NAME,bitmap);
                }
                dismiss();

                break;
            case R.id.weibo:
                if (mShareHandler != null) {
                    mShareHandler.onShare(SinaWeibo.NAME,bitmap);
                }
                dismiss();
                break;
            case R.id.qq:
                if (mShareHandler != null) {
                    mShareHandler.onShare(QQ.NAME,bitmap);
                }
                dismiss();
                break;
            case R.id.qqZone:
                if (mShareHandler != null) {
                    mShareHandler.onShare(QQ.NAME,bitmap);
                }
                dismiss();
                break;


            case R.id.cancel:

                dismiss();

                break;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }
}
