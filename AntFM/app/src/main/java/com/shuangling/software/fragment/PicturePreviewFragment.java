package com.shuangling.software.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.previewlibrary.view.BasePhotoFragment;
import com.shuangling.software.R;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.entity.ImageInfo;
import com.shuangling.software.utils.CommonUtils;

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
