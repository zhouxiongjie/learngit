package com.shuangling.software.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.Nullable;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.activity.CaptureActivity;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.decoding.RGBLuminanceSource;
import com.google.zxing.qrcode.QRCodeReader;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.callback.ConfigItems;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.ItemsParams;
import com.previewlibrary.view.BasePhotoFragment;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.shuangling.software.R;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.activity.LivePortraitActivity;
import com.shuangling.software.activity.NewLoginActivity;
import com.shuangling.software.activity.ReportActivity;
import com.shuangling.software.activity.ScanResultActivity;
import com.shuangling.software.activity.SettingActivity;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.dialog.ShareDialog;
import com.shuangling.software.entity.ImageInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import io.reactivex.functions.Consumer;

import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * author  yangc
 * date 2017/11/22
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public class PicturePreviewFragment extends BasePhotoFragment {
    /****用户具体数据模型***/
    private ImageInfo b;
    FontIconView download;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        download = view.findViewById(R.id.download);
        download.setVisibility(View.GONE);
        b = (ImageInfo) getBeanViewInfo();
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                Log.d("SmoothImageView","onLongClick");
                //Toast.makeText(getContext(), "长按事件:" + b.getUser(), Toast.LENGTH_LONG).show();
                Hashtable<DecodeHintType, String> hints = new Hashtable<>();
                hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

                Bitmap obmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                RGBLuminanceSource source = new RGBLuminanceSource(obmp);
                BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
                QRCodeReader reader = new QRCodeReader();
                Result re = null;
                try {
                    re = reader.decode(bitmap1,hints);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final Result result=re;
                ShareDialog dialog = ShareDialog.getInstance(false, false);
                dialog.setmIsShowQrCode(result!= null);
                dialog.setmIsShowDownLoad(true);

                dialog.setShareHandler(new ShareDialog.ShareHandler() {
                    @Override
                    public void onShare(String platform) {
                        showShareImage(platform,obmp);
                    }

                    @Override
                    public void download() {
                        final Bitmap saveBitmap = obmp;
//获取写文件权限
                        RxPermissions rxPermissions = new RxPermissions(getActivity());
                        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean granted) throws Exception {
                                        if (granted) {
                                            Random rand = new Random();
                                            int randNum = rand.nextInt(1000);
                                            File tempFile = new File(CommonUtils.getStoragePublicDirectory(DIRECTORY_PICTURES), CommonUtils.getCurrentTimeString() + randNum + ".png");
                                            CommonUtils.saveBitmapToPNG(tempFile.getAbsolutePath(), saveBitmap);
                                            ToastUtils.show("图片保存成功");
//发送广播更新相册
                                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                            Uri uri = Uri.fromFile(tempFile);
                                            intent.setData(uri);
                                            getContext().sendBroadcast(intent);
                                        } else {
                                            ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                                        }
                                    }
                                });
                        dialog.dismiss();
                    }

                    @Override
                    public void qrCode() {
                        Intent it=new Intent(getContext(), ScanResultActivity.class);
                        it.putExtra("value",result.getText());
                        startActivity(it);
                        dialog.dismiss();

                    }
                });
                dialog.show(getParentFragmentManager(), "ShareDialog");


//                if (re!= null) {
//                    final Result result=re;
//
//                    final String[] items = {"识别图中二维码"};
//                    new CircleDialog.Builder()
//                        .configDialog(new ConfigDialog() {
//                            @Override
//                            public void onConfig(DialogParams params) {
//                                //params.backgroundColorPress = Color.CYAN;
//                                //增加弹出动画
//                                params.animStyle = R.style.dialogWindowAnim;
//                            }
//                        })
//                        .setItems(items, new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                ToastUtils.show(result.getText());
//                            }
//                        })
//                        .configItems(new ConfigItems() {
//                            @Override
//                            public void onConfig(ItemsParams params) {
//                                params.textColor = Color.parseColor("#88000000");
//                                params.textSize = (int) getResources().getDimension(R.dimen.font_size_26px);
//                            }
//                        })
//                        .setNegative("取消", null)
//                        .show(getParentFragmentManager());
//
//                }
                return false;

            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.downloadPic(getActivity(),b.getUrl());
            }
        });
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picture_preview_layout, container, false);
    }


    private void showShareImage(String platform, final Bitmap bitmap) {
        final Bitmap saveBitmap = bitmap;
        final OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setPlatform(platform);
        final Platform qq = ShareSDK.getPlatform(QQ.NAME);
        if (!qq.isClientValid()) {
            oks.addHiddenPlatform(QQ.NAME);
        }
        final Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
        if (!sina.isClientValid()) {
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }
        Random rand = new Random();
        int randNum = rand.nextInt(1000);
        final String childPath = CommonUtils.getCurrentTimeString() + randNum + ".png";
        if (QQ.NAME.equals(platform)) {
//获取写文件权限
            RxPermissions rxPermissions = new RxPermissions(getActivity());
            rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            if (granted) {
                                final File tempFile = new File(CommonUtils.getStoragePublicDirectory(DIRECTORY_PICTURES), childPath);
                                CommonUtils.saveBitmapToPNG(tempFile.getAbsolutePath(), saveBitmap);
                                //ToastUtils.show("图片保存成功");
//发送广播更新相册
                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Uri uri = Uri.fromFile(tempFile);
                                intent.setData(uri);
                                getActivity().sendBroadcast(intent);
// oks.setImagePath(filePath);
                                oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                                    //自定义分享的回调想要函数
                                    @Override
                                    public void onShare(Platform platform, final Platform.ShareParams paramsToShare) {
                                        paramsToShare.setShareType(Platform.SHARE_IMAGE);
                                        // paramsToShare.setImageData(bitmap);
                                        paramsToShare.setImagePath(tempFile.getAbsolutePath());
                                    }
                                });
                            } else {
                                ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                            }
                        }
                    });
        } else {
            oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                //自定义分享的回调想要函数
                @Override
                public void onShare(Platform platform, final Platform.ShareParams paramsToShare) {
                    paramsToShare.setShareType(Platform.SHARE_IMAGE);
                    paramsToShare.setImageData(bitmap);
                }
            });
        }
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {

            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {


            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
            }
        });
        // 启动分享GUI
        oks.show(getContext());
    }
}
