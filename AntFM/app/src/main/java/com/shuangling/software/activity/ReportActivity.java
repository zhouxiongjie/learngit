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
import com.hjq.toast.ToastUtils;
import com.previewlibrary.GPreviewBuilder;
import com.shuangling.software.R;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.customview.MyGridView;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.ImageInfo;
import com.shuangling.software.entity.OssInfo;
import com.shuangling.software.entity.ReportReason;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.oss.OssService;
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


public class ReportActivity extends AppCompatActivity {

    private static final int CHOOSE_PHOTO = 0x0;

    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.reason)
    MyGridView reason;
    @BindView(R.id.desc)
    EditText desc;
    @BindView(R.id.descLength)
    TextView descLength;
    @BindView(R.id.material)
    MyGridView material;
    @BindView(R.id.submit)
    TextView submit;


    private List<String> mImgs = new ArrayList<String>();
    private List<String> mUrls=new ArrayList<String>();

    private MaterialGridAdapter mMaterialGridAdapter;
    private ReasonGridAdapter mReasonGridAdapter;

    private DialogFragment mDialogFragment;

    private String mId;
    private String mReasonId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        CommonUtils.transparentStatusBar(this);
        init();
    }


    private void init() {

        mId=getIntent().getStringExtra("id");

        getReason();
        desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                descLength.setText("" + s.toString().length() + "/" + 200);
            }
        });

        mMaterialGridAdapter = new MaterialGridAdapter(this);
        material.setAdapter(mMaterialGridAdapter);


    }

    private void getReason() {
        String url = ServerInfo.serviceIP + "/v2/report_reason";
        Map<String, String> params = new HashMap<>();


        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        List<ReportReason> reportReasons = JSON.parseArray(jsonObject.getJSONArray("data").toJSONString(), ReportReason.class);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mReasonGridAdapter = new ReasonGridAdapter(ReportActivity.this, reportReasons);
                                reason.setAdapter(mReasonGridAdapter);
                            }
                        });


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test", exception.toString());

            }
        });
    }

    private void submit() {



        String url = ServerInfo.serviceIP + "/v2/user_report";
        Map<String, String> params = new HashMap<>();

        params.put("post_id",mId);
        params.put("reason_id",mReasonId);
        if(!TextUtils.isEmpty(desc.getText().toString().trim())){
            params.put("res",desc.getText().toString().trim());
        }
        for(int i=0;i<mUrls.size();i++){
            params.put("prove["+i+"]",mUrls.get(i));
        }

        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setResult(RESULT_OK);
                                finish();
                                ToastUtils.show("举报成功");
                            }
                        });
                    }else if(jsonObject != null){

                        ToastUtils.show(jsonObject.getString("msg"));
                    }else{
                        ToastUtils.show("举报失败，请稍后再试");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    mDialogFragment.dismiss();
                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test", exception.toString());

                mDialogFragment.dismiss();
            }
        });
    }


    @OnClick(R.id.submit)
    public void onViewClicked() {

        if (User.getInstance() == null) {
            Intent it=new Intent(ReportActivity.this, NewLoginActivity.class);
            startActivity(it);
        }

        if(TextUtils.isEmpty(mReasonId)){
            ToastUtils.show("请选择举报原因");
            return;
        }

        mDialogFragment=CommonUtils.showLoadingDialog(getSupportFragmentManager());
        mUrls.clear();

        OSSUploadUtils.getInstance().setOnCallbackListener(new OSSUploadUtils.OnCallbackListener() {
            @Override
            public void onSuccess(String url) {
                mUrls.add(url);
            }

            @Override
            public void onSuccessAll() {
                submit();
            }

            @Override
            public void onFailed() {
                mDialogFragment.dismiss();
                ToastUtils.show("上传图片失败");
            }
        }).uploadFile(this, mImgs);




    }





    public class MaterialGridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private Context context;

        public MaterialGridAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
            this.context = context;
        }

        public int getCount() {
            if (mImgs.size() < 5) {
                return mImgs.size() + 1;
            }
            return mImgs.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView = inflater.inflate(R.layout.grid_img_item, parent, false);
            int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(50)) / 3;
            int height = width;
            ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(width, height);
            convertView.setLayoutParams(param);
            ViewHolder holder = new ViewHolder();
            holder.image = convertView.findViewById(R.id.img);
            holder.delete = convertView.findViewById(R.id.delete);
            holder.add = convertView.findViewById(R.id.add);
            convertView.setTag(holder);

            if (position < mImgs.size()) {

                Uri uri = Uri.fromFile(new File(mImgs.get(position)));
                ImageLoader.showThumb(uri, holder.image, width, height);
                holder.add.setVisibility(View.GONE);
                holder.delete.setVisibility(View.VISIBLE);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mImgs.remove(position);
                        notifyDataSetChanged();
                    }
                });
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<ImageInfo> images = new ArrayList<>();
                        for (int i = 0; i < mImgs.size(); i++) {
                            ImageInfo image = new ImageInfo(mImgs.get(i));
                            images.add(image);
                            Rect bounds = new Rect();
                            holder.image.getGlobalVisibleRect(bounds);
                            image.setBounds(bounds);
                        }
                        GPreviewBuilder.from(ReportActivity.this)
                                .setData(images)
                                .setCurrentIndex(position)
                                .setDrag(true, 0.6f)
                                .setSingleFling(true)
                                .setType(GPreviewBuilder.IndicatorType.Number)
                                .start();
                    }
                });

            } else {
                holder.add.setVisibility(View.VISIBLE);
                holder.delete.setVisibility(View.GONE);
                holder.image.setVisibility(View.GONE);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RxPermissions rxPermissions = new RxPermissions(ReportActivity.this);
                        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean granted) throws Exception {
                                        if (granted) {
                                            String packageName = getPackageName();
                                            Matisse.from(ReportActivity.this)
                                                    .choose(MimeType.of(MimeType.JPEG, MimeType.PNG)) // 选择 mime 的类型
                                                    .countable(false)
                                                    .maxSelectable(5-mImgs.size()) // 图片选择的最多数量
                                                    .spanCount(4)
                                                    .capture(true)
                                                    .captureStrategy(new CaptureStrategy(true, packageName + ".fileprovider"))
                                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                                    .thumbnailScale(1.0f) // 缩略图的比例
                                                    .theme(R.style.Matisse_Zhihu)
                                                    .imageEngine(new MyGlideEngine()) // 使用的图片加载引擎
                                                    .forResult(CHOOSE_PHOTO);       // 设置作为标记的请求码
                                        } else {
                                            ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                                        }
                                    }
                                });
                    }
                });
            }

            return convertView;
        }

        class ViewHolder {
            SimpleDraweeView image;
            FontIconView delete;
            FontIconView add;
        }

    }


    public class ReasonGridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private Context context;
        private List<ReportReason> reasons;

        public ReasonGridAdapter(Context context, List<ReportReason> reasons) {
            this.inflater = LayoutInflater.from(context);
            this.context = context;
            this.reasons = reasons;
        }

        public int getCount() {
            if (reasons != null) {
                return reasons.size();
            }
            return 0;
        }

        @Override
        public ReportReason getItem(int position) {
            return reasons.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView = inflater.inflate(R.layout.grid_radio_item, parent, false);
            ViewHolder vh = new ViewHolder(convertView);
            ReportReason reason = reasons.get(position);
            vh.desc.setText(reason.getContent());

            if (reason.isSelected()) {
                vh.checkBox.setSelected(true);
            } else {
                vh.checkBox.setSelected(false);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!reason.isSelected()) {

                        reset(reason);
                    }
                }
            });
            return convertView;
        }


        private void reset(ReportReason rea) {
            for (int i = 0; i < reasons.size(); i++) {
                ReportReason reason = reasons.get(i);
                if (reason.getId() == rea.getId()) {
                    reason.setSelected(true);
                } else {
                    reason.setSelected(false);
                }
            }
            mReasonId=""+rea.getId();
            notifyDataSetChanged();
        }


        class ViewHolder {
            @BindView(R.id.checkBox)
            ImageView checkBox;
            @BindView(R.id.desc)
            TextView desc;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        if (requestCode == CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK && data != null) {

                List<String> paths = Matisse.obtainPathResult(data);
                //List<Uri> selects = Matisse.obtainResult(data);
                //File file = new File(CommonUtils.getRealFilePath(this, selects.get(0)));
                //File file = new File(paths.get(0));

                for(int i=0;i<paths.size();i++){
                    mImgs.add(paths.get(i));
                }
                mMaterialGridAdapter.notifyDataSetChanged();

            }
        }
    }





}
