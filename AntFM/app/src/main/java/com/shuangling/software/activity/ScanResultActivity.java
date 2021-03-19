package com.shuangling.software.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hjq.toast.ToastUtils;
import com.previewlibrary.GPreviewBuilder;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.entity.ImageInfo;
import com.shuangling.software.entity.ReportReason;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.MyGlideEngine;
import com.shuangling.software.utils.OSSUploadUtils;
import com.shuangling.software.utils.ServerInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import okhttp3.Call;

public class ScanResultActivity extends QMUIActivity {


    @BindView(R.id.topbar)
    QMUITopBarLayout topbar;
    @BindView(R.id.error)
    LinearLayout error;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.other)
    LinearLayout other;
    @BindView(R.id.url)
    TextView url;
    @BindView(R.id.access)
    TextView access;
    @BindView(R.id.webset)
    LinearLayout webset;

    private String mValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        ButterKnife.bind(this);
        //CommonUtils.transparentStatusBar(this);
        init();
    }

    private void init() {
        mValue=getIntent().getStringExtra("value");
        initTopBar();
        if(!mValue.startsWith("http://")&&!mValue.startsWith("https://")){
            topbar.setTitle("提示");
            other.setVisibility(View.VISIBLE);
            content.setText(mValue);
        }else{
            if(CommonUtils.isInlink(mValue)){
                CommonUtils.jumpTo(this,mValue,"");
                finish();
            }else{
                webset.setVisibility(View.VISIBLE);
                url.setText(mValue);
                access.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtils.jumpTo(ScanResultActivity.this,mValue,"");
                    }
                });
            }
        }

    }

    private void initTopBar() {


        QMUIStatusBarHelper.setStatusBarLightMode(this);
        topbar.addLeftImageButton(R.drawable.ic_left, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        mTopBar.addRightImageButton(R.drawable.ic_more, com.qmuiteam.qmui.R.id.right_icon).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });


    }


}
