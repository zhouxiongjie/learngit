package com.shuangling.software.activity;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.previewlibrary.GPreviewBuilder;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.customview.MyGridView;
import com.shuangling.software.entity.ImageInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.MyGlideEngine;
import com.shuangling.software.utils.OSSUploadUtils;
import com.shuangling.software.utils.ServerInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.youngfeng.snake.annotations.EnableDragToClose;
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
@EnableDragToClose()
public class FeedbackActivity extends AppCompatActivity implements Handler.Callback {
public static final String TAG = "CityListActivity";
    private static final int CHOOSE_PHOTO = 0x0;
    public static final int MSG_FEED_BACK = 0x1;
    public static final int MSG_NO_READ_REPLY = 0x2;
@BindView(R.id.suggestion)
    EditText suggestion;
    @BindView(R.id.textNumber)
    TextView textNumber;
    @BindView(R.id.phoneNum)
    EditText phoneNum;
    @BindView(R.id.submit)
    TextView submit;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.history)
    FontIconView history;
    @BindView(R.id.material)
    MyGridView material;
    @BindView(R.id.noRead)
    SimpleDraweeView noRead;
private Handler mHandler;
private List<String> mImgs = new ArrayList<String>();
    private List<String> mUrls=new ArrayList<String>();
private MaterialGridAdapter mMaterialGridAdapter;
    private DialogFragment mDialogFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
setContentView(R.layout.activity_feedback);
        ImmersionBar.with(this).statusBarDarkFont(true).fitsSystemWindows(true).keyboardEnable(true)  //解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE).init();
        //CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        init();
    }
public void submitSuggestion() {
String url = ServerInfo.serviceIP + ServerInfo.feedback;
Map<String, String> params = new HashMap<>();
        params.put("opinion", suggestion.getText().toString());
        params.put("phone", phoneNum.getText().toString());
        params.put("phone_type", "");
        params.put("browser", "");
        params.put("operation_system", "");
        for(int i=0;i<mUrls.size();i++){
            params.put("enclosure["+i+"]",mUrls.get(i));
        }
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
@Override
            public void onResponse(Call call, String response) throws IOException {
Message msg = Message.obtain();
                msg.what = MSG_FEED_BACK;
                msg.obj = response;
                mHandler.sendMessage(msg);
}
@Override
            public void onFailure(Call call, Exception exception) {
                Log.e("test", exception.toString());
                try{
                    mDialogFragment.dismiss();
                }catch (Exception e){
}
}
        });
}
public void getNoReadReply() {
String url = ServerInfo.serviceIP + "/v1/user_not_views";
Map<String, String> params = new HashMap<>();
OkHttpUtils.get(url, params, new OkHttpCallback(this) {
@Override
            public void onResponse(Call call, String response) throws IOException {
Message msg = Message.obtain();
                msg.what = MSG_NO_READ_REPLY;
                msg.obj = response;
                mHandler.sendMessage(msg);
}
@Override
            public void onFailure(Call call, Exception exception) {
                Log.e("test", exception.toString());
                try{
                    mDialogFragment.dismiss();
                }catch (Exception e){
}
}
        });
}
private void init() {
        mHandler = new Handler(this);
        phoneNum.setText("" + User.getInstance().getPhone());
        getNoReadReply();
        suggestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
}
@Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
}
@Override
            public void afterTextChanged(Editable s) {
                String sug = s.toString();
                textNumber.setText("" + s.toString().length() + "/" + 500);
                String phoneNumber = phoneNum.getText().toString();
                if (!TextUtils.isEmpty(sug) && CommonUtils.isMobileNO(phoneNumber)) {
                    submit.setActivated(true);
                    submit.setEnabled(true);
                } else {
                    submit.setActivated(false);
                    submit.setEnabled(false);
                }
}
        });
        phoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
}
@Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
}
@Override
            public void afterTextChanged(Editable s) {
                String phoneNumber = s.toString();
                String sug = suggestion.getText().toString();
                textNumber.setText("" + s.toString().length() + "/" + 300);
                if (!TextUtils.isEmpty(sug) && CommonUtils.isMobileNO(phoneNumber)) {
                    submit.setActivated(true);
                    submit.setEnabled(true);
                } else {
                    submit.setActivated(false);
                    submit.setEnabled(false);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideInput(FeedbackActivity.this);
                finish();
            }
        });
        mMaterialGridAdapter = new MaterialGridAdapter(this);
        material.setAdapter(mMaterialGridAdapter);
}
@Override
    public boolean handleMessage(Message msg) {
switch (msg.what) {
            case MSG_FEED_BACK:
try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show("提交成功，感谢您的反馈\n我们将尽快为您处理");
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    finish();
                                }catch (Exception e){
}
}
                        },1000);
}
} catch (Exception e) {
}finally {
                    try{
                        mDialogFragment.dismiss();
                    }catch (Exception e){
}
}
break;
            case MSG_NO_READ_REPLY:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        int noReadNumber=jsonObject.getInteger("data");
                        if(noReadNumber>0){
                            noRead.setVisibility(View.VISIBLE);
                        }else {
                            noRead.setVisibility(View.GONE);
                        }
}
} catch (Exception e) {
}
                break;
        }
        return false;
    }
@OnClick({R.id.submit,R.id.history})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.submit:
                if (User.getInstance() == null) {
                    Intent it=new Intent(FeedbackActivity.this, NewLoginActivity.class);
                    startActivity(it);
                }
mDialogFragment=CommonUtils.showLoadingDialog(getSupportFragmentManager());
                mUrls.clear();
if(mImgs.size()==0){
                    submitSuggestion();
                }else{
                    OSSUploadUtils.getInstance().setOnCallbackListener(new OSSUploadUtils.OnCallbackListener() {
                        @Override
                        public void onSuccess(String url) {
                            mUrls.add(url);
                        }
@Override
                        public void onSuccessAll() {
                            submitSuggestion();
                        }
@Override
                        public void onFailed() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mDialogFragment.dismiss();
                                    ToastUtils.show("上传图片失败");
                                }
                            });
}
                    }).uploadFile(this, mImgs);
                }
                break;
            case R.id.history:
startActivity(new Intent(this,FeedbackHistoryActivity.class));
                break;
        }
//submitSuggestion();
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
                        GPreviewBuilder.from(FeedbackActivity.this)
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
                        RxPermissions rxPermissions = new RxPermissions(FeedbackActivity.this);
                        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean granted) throws Exception {
                                        if (granted) {
                                            String packageName = getPackageName();
                                            Matisse.from(FeedbackActivity.this)
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
