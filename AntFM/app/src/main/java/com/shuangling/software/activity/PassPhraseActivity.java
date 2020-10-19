package com.shuangling.software.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.previewlibrary.GPreviewBuilder;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.entity.ImageInfo;
import com.shuangling.software.entity.LiveRoomInfo;
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


public class PassPhraseActivity extends AppCompatActivity {


    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.indicator)
    ImageView indicator;
    @BindView(R.id.verifyCode)
    EditText verifyCode;
    @BindView(R.id.verify)
    TextView verify;


    private LiveRoomInfo mLiveRoomInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_phrase);
        ButterKnife.bind(this);
        ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(false).init();
        init();
    }


    private void init() {
        mLiveRoomInfo=(LiveRoomInfo)getIntent().getSerializableExtra("LiveRoomInfo");

        verifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=verifyCode.getText().toString();
                if(TextUtils.isEmpty(code)){
                    ToastUtils.show("请输入口令");
                    return;
                }
                if(code.equals(mLiveRoomInfo.getEntry_password())){
                    Intent it=new Intent(PassPhraseActivity.this,LiveDetailActivity.class);


                    String streamName=mLiveRoomInfo.getStream_name();
                    it.putExtra("streamName",streamName);
                    it.putExtra("roomId",mLiveRoomInfo.getId());
                    //it.putExtra("url",content.getLive().getHls_play_url());
                    //it.putExtra("url",content.getLive().getRtmp_play_url());
                    it.putExtra("verify",false);
                    it.putExtra("url",mLiveRoomInfo.getRtmp_play_url());
                    it.putExtra("type",mLiveRoomInfo.getType());

                    startActivity(it);

                    finish();
                }else{
                    ToastUtils.show("口令不正确");
                }
            }
        });



    }








}
