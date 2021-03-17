package com.shuangling.software.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.Nullable;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
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
import com.shuangling.software.activity.SettingActivity;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.entity.ImageInfo;
import com.shuangling.software.utils.CommonUtils;

import java.util.Hashtable;

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
                if (re!= null) {
                    final Result result=re;
//                    QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(getContext());
//                    builder.setGravityCenter(true)
//                            .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
//                            .setTitle("非WiFi网络播放提醒")
//                            .setAddCancelBtn(true)
//                            .setAllowDrag(true)
//                            .setNeedRightMark(false)
//                            .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
//                                @Override
//                                public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
//
//                                    ToastUtils.show(result.getText());
////                                Logger.d("saomiao",result.getText());
////                                bundle.putParcelable("bitmap",result.get);
//
//                                    dialog.dismiss();
//
//                                }
//                            });
//
//                    builder.addItem("识别图中二维码");
//                    builder.build().show();
                    final String[] items = {"识别图中二维码"};
                    new CircleDialog.Builder()
                        .configDialog(new ConfigDialog() {
                            @Override
                            public void onConfig(DialogParams params) {
                                //params.backgroundColorPress = Color.CYAN;
                                //增加弹出动画
                                params.animStyle = R.style.dialogWindowAnim;
                            }
                        })
                        .setItems(items, new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                ToastUtils.show(result.getText());
                            }
                        })
                        .configItems(new ConfigItems() {
                            @Override
                            public void onConfig(ItemsParams params) {
                                params.textColor = Color.parseColor("#88000000");
                                params.textSize = (int) getResources().getDimension(R.dimen.font_size_26px);
                            }
                        })
                        .setNegative("取消", null)
                        .show(getParentFragmentManager());

                }
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
}
