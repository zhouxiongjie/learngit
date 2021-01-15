package com.shuangling.software.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hjq.toast.ToastUtils;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.OssInfo;
import com.shuangling.software.entity.Province;
import com.shuangling.software.entity.User;
import com.shuangling.software.entity.UserDetail;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.oss.OSSAKSKCredentialProvider;
import com.shuangling.software.oss.OssService;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.MyGlideEngine;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.youngfeng.snake.annotations.EnableDragToClose;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import okhttp3.Call;

import static android.os.Environment.DIRECTORY_PICTURES;

//@EnableDragToClose()
public class ModifyUserInfoActivity extends QMUIActivity/*QMUIFragmentActivity*/ implements Handler.Callback, OSSCompletedCallback<PutObjectRequest, PutObjectResult> {
    public static final String TAG = ModifyUserInfoActivity.class.getName();
    private static final int CHOOSE_PHOTO = 0x0;
    private static final int TACK_PHOTO = 0x1;
    private static final int CUT_OK = 0x02;
    private static final int MSG_UPLOAD_HEAD = 0x03;
    @BindView(R.id.activity_title)
    /*TopTitleBar*/ QMUITopBarLayout activityTitle;
    @BindView(R.id.head)
    SimpleDraweeView head;
    @BindView(R.id.headRightIcon)
    ImageView headRightIcon;
    @BindView(R.id.headLayout)
    RelativeLayout headLayout;
    @BindView(R.id.nickName)
    TextView nickName;
    @BindView(R.id.nickNameLayout)
    RelativeLayout nickNameLayout;
    @BindView(R.id.sex)
    TextView sex;
    @BindView(R.id.sexLayout)
    RelativeLayout sexLayout;
    @BindView(R.id.birthday)
    TextView birthday;
    @BindView(R.id.birthdayLayout)
    RelativeLayout birthdayLayout;
    @BindView(R.id.zone)
    TextView zone;
    @BindView(R.id.zoneLayout)
    RelativeLayout zoneLayout;
    @BindView(R.id.quit)
    TextView quit;
    @BindView(R.id.bottom)
    LinearLayout bottom;
    @BindView(R.id.root)
    LinearLayout root;
    private File tempFile;
    private UserDetail mUserDetail;
    private List<Province> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private OssInfo mOssInfo;
    //OSS的上传下载
    private OssService mOssService;
    private Uri uritempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_userinfo);
//        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        activityTitle.addLeftImageButton(R.drawable.ic_left, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(view -> { //
            finish();
        });
        activityTitle.setTitle("编辑资料");
        init();
        initCity();
        getOSSinfo();
    }

    private void initCity() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 子线程中解析省市区数据
                String str = getJson(ModifyUserInfoActivity.this, "province.json");
                options1Items = JSONObject.parseArray(str, Province.class);
                for (int i = 0; i < options1Items.size(); i++) {//遍历省份
                    ArrayList<String> cityList = new ArrayList<>();//该省的城市列表（第二级）
                    ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）
                    for (int c = 0; c < options1Items.get(i).getCity().size(); c++) {//遍历该省份的所有城市
                        String cityName = options1Items.get(i).getCity().get(c).getName();
                        cityList.add(cityName);//添加城市
                        ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表
//如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                /*if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }*/
                        city_AreaList.addAll(options1Items.get(i).getCity().get(c).getArea());
                        province_AreaList.add(city_AreaList);//添加该省所有地区数据
                    }
/**
 * 添加城市数据
 */
                    options2Items.add(cityList);
/**
 * 添加地区数据
 */
                    options3Items.add(province_AreaList);
                }
            }
        });
        thread.start();
    }

    private void init() {
        EventBus.getDefault().register(this);
        nickName.setText(User.getInstance() != null ? User.getInstance().getNickname() : "");
        if (User.getInstance() != null && !TextUtils.isEmpty(User.getInstance().getAvatar())) {
            Uri uri = Uri.parse(User.getInstance().getAvatar());
            ImageLoader.showThumb(uri, head, CommonUtils.dip2px(40), CommonUtils.dip2px(40));
        }
        //tempFile = new File(CommonUtils.getStoragePrivateDirectory(Environment.DIRECTORY_PICTURES), "head.jpg");
        getUerInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("updateAvatar")) {
            if (!TextUtils.isEmpty(User.getInstance().getAvatar())) {
                Uri uri = Uri.parse(User.getInstance().getAvatar());
                ImageLoader.showThumb(uri, head, CommonUtils.dip2px(40), CommonUtils.dip2px(40));
            }
        } else if (event.getEventName().equals("updateNickname")) {
            nickName.setText(User.getInstance().getNickname());
        }
    }

    public void getUerInfo() {
        String url = ServerInfo.serviceIP + ServerInfo.modifyUserInfo;
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                String result = response;
                final JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    mUserDetail = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), UserDetail.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mUserDetail.getProfile().getSex() == 0) {
                                sex.setText("保密");
                            } else if (mUserDetail.getProfile().getSex() == 1) {
                                sex.setText("男");
                            } else {
                                sex.setText("女");
                            }
                            birthday.setText(mUserDetail.getProfile().getBirthdate());
                            zone.setText(mUserDetail.getProfile().getHome_address());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    public void modifyUerInfo(final String key, final String value) {
        String url = ServerInfo.serviceIP + ServerInfo.modifyUserInfo;
        Map<String, String> params = new HashMap<>();
        params.put(key, value);
        OkHttpUtils.put(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                String result = response;
                final JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    if (key.equals("avatar")) {
                        User.getInstance().setAvatar(value);
                        SharedPreferencesUtils.saveUser(User.getInstance());
                        //清理缓存
//                        ImagePipeline imagePipeline = Fresco.getImagePipeline();
//                        Uri uri = Uri.parse(User.getInstance().getAvatar());
//                        imagePipeline.evictFromCache(uri);
                        EventBus.getDefault().post(new CommonEvent("updateAvatar"));
                    } else if (key.equals("sex")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (value.equals("0")) {
                                    sex.setText("保密");
                                } else if (value.equals("1")) {
                                    sex.setText("男");
                                } else {
                                    sex.setText("女");
                                }
                            }
                        });
                    } else if (key.equals("birthdate")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                birthday.setText(value);
                            }
                        });
                    } else if (key.equals("home_address")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                zone.setText(value);
                            }
                        });
                    }
                } else if (jsonObject != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.show(jsonObject.getString("msg"));
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.show("修改用户信息失败");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TACK_PHOTO) {
            if (resultCode == RESULT_OK && data != null) {
                //保存图片信息
                String path = data.getStringExtra("path");
                File file = new File(path);
                //clipImage(Uri.fromFile(file));
            } else {
                //Toast.makeText(this, "用户取消拍照", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK && data != null) {
                List<String> paths = Matisse.obtainPathResult(data);
                //List<Uri> selects = Matisse.obtainResult(data);
                //File file = new File(CommonUtils.getRealFilePath(this, selects.get(0)));
                File file = new File(paths.get(0));
                clipImage(Uri.fromFile(file));
            } else {
                //ToastUtils.show("用户取消拍照");
            }
        } else if (requestCode == CUT_OK) {
            if (resultCode == RESULT_OK && data != null) {
                // 获取裁剪的图片数据
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
                    Random rand = new Random();
                    int randNum = rand.nextInt(1000);
                    tempFile = new File(CommonUtils.getStoragePrivateDirectory(DIRECTORY_PICTURES), CommonUtils.getCurrentTimeString() + randNum + ".jpg");
                    CommonUtils.saveBitmap(tempFile.getAbsolutePath(), bitmap);
                    //saveBitmapToFile(bitmap);
//
                    // 2.把图片文件file上传到服务器
                    if (mOssInfo != null && mOssService != null) {
                        mOssService.asyncUploadFile(mOssInfo.getDir() + tempFile.getName(), tempFile.getAbsolutePath(), null, this);
                    } else {
                        ToastUtils.show("OSS初始化失败,请稍后再试");
                    }
                } catch (Exception e) {
                }
//                Bundle extras = data.getExtras();
//                if (extras != null) {
//                    Bitmap bitmap = extras.getParcelable("data");
//                    Random rand = new Random();
//                    int randNum = rand.nextInt(1000);
//                    tempFile = new File(CommonUtils.getStoragePrivateDirectory(DIRECTORY_PICTURES), CommonUtils.getCurrentTimeString()+randNum+".jpg");
//                    CommonUtils.saveBitmap(tempFile.getAbsolutePath(), bitmap);
//                    //saveBitmapToFile(bitmap);
////
//                    // 2.把图片文件file上传到服务器
//                    if(mOssInfo!=null){
//                        mOssService.asyncUploadFile(mOssInfo.getDir()+tempFile.getName(),tempFile.getAbsolutePath(),null,this);
//                    }else{
//                        ToastUtils.show("OSS初始化失败");
//                    }
//
//                }
            }
        }
    }

    /**
     * 调用系统的裁剪方法
     */
    private void clipImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        // 数据 uri 代表裁剪哪一张
        intent.setDataAndType(uri, "image/*");
        // 传递数据
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例，这里设置的是正方形（长宽比为1:1）
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
//        intent.putExtra("return-data", true);
        // 你待会裁剪完之后需要获取数据   startActivityForResult
        uritempFile = Uri.parse("file://" + "/" + CommonUtils.getStoragePublicDirectory(DIRECTORY_PICTURES) + File.separator + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CUT_OK);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_UPLOAD_HEAD: {
            }
            break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick({R.id.headLayout, R.id.nickNameLayout, R.id.sexLayout, R.id.birthdayLayout, R.id.zoneLayout, R.id.quit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.headLayout: {
//                if(mOssService==null){
//                    ToastUtils.show("OSS上传服务初始化失败，请稍后再试");
//                    return;
//                }
                RxPermissions rxPermissions = new RxPermissions(ModifyUserInfoActivity.this);
                rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean granted) throws Exception {
                                if (granted) {
                                    String packageName = getPackageName();
                                    Matisse.from(ModifyUserInfoActivity.this)
                                            .choose(MimeType.of(MimeType.JPEG, MimeType.PNG)) // 选择 mime 的类型
                                            .countable(false)
                                            .maxSelectable(1) // 图片选择的最多数量
                                            .spanCount(4)
                                            .capture(true)
                                            .captureStrategy(new CaptureStrategy(true, packageName + ".fileprovider"))
                                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                            .thumbnailScale(1.0f) // 缩略图的比例
                                            .theme(R.style.Matisse_Zhihu)
                                            .imageEngine(new MyGlideEngine()) // 使用的图片加载引擎
                                            .forResult(CHOOSE_PHOTO); // 设置作为标记的请求码
                                } else {
                                    ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                                }
                            }
                        });
//               final String[] items = { "选择头像"/*,"拍照"*/};
                /*
                new CircleDialog.Builder()
                        .configDialog(new ConfigDialog() {
                            @Override
                            public void onConfig(DialogParams params) {
                                //params.backgroundColorPress = Color.CYAN;
                                //增加弹出动画
                                params.animStyle = R.style.dialogWindowAnim;
                            }
                        })
                        .setTitle("头像")
                        .setItems(items, new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if(position==0){
                                    if(mOssService==null){
                                        ToastUtils.show("OSS上传服务初始化失败，请稍后再试");
                                    }
RxPermissions rxPermissions = new RxPermissions(ModifyUserInfoActivity.this);
                                    rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            .subscribe(new Consumer<Boolean>() {
                                                @Override
                                                public void accept(Boolean granted) throws Exception {
                                                    if(granted){
                                                        Matisse.from(ModifyUserInfoActivity.this)
                                                                .choose(MimeType.of(MimeType.JPEG,MimeType.PNG)) // 选择 mime 的类型
                                                                .countable(false)
                                                                .maxSelectable(1) // 图片选择的最多数量
                                                                .spanCount(4)
                                                                .capture(true)
                                                                .captureStrategy(new CaptureStrategy(true,"com.shuangling.software.fileprovider"))
                                                                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                                                .thumbnailScale(1.0f) // 缩略图的比例
                                                                .theme(R.style.Matisse_Zhihu)
                                                                .imageEngine(new GlideEngine()) // 使用的图片加载引擎
                                                                .forResult(CHOOSE_PHOTO); // 设置作为标记的请求码
                                                    }else{
                                                        ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                                                    }
                                                }
                                            });
}else{
                                    RxPermissions rxPermissions = new RxPermissions(ModifyUserInfoActivity.this);
                                    rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            .subscribe(new Consumer<Boolean>() {
                                                @Override
                                                public void accept(Boolean granted) throws Exception {
                                                    if(granted){
                                                        //Intent it = new Intent(ModifyUserInfoActivity.this, CameraActivity.class);
                                                        //startActivityForResult(it, TACK_PHOTO);
                                                    }else{
                                                        ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                                                    }
                                                }
                                            });
                                }
                            }
                        })
                        .setNegative("取消", null)
                        .show(getSupportFragmentManager());*/
            }
            break;
            case R.id.nickNameLayout: {
                startActivity(new Intent(this, ModifyNicknameActivity.class));
            }
            break;
            case R.id.sexLayout: {

                QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(this);
                builder.setGravityCenter(true)
                        .setSkinManager(QMUISkinManager.defaultInstance(this))
                        //.setTitle(title)
                        .setAddCancelBtn(true)
                        .setAllowDrag(true)
                        .setNeedRightMark(false)
                        .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                            @Override
                            public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                                dialog.dismiss();
                                modifyUerInfo("sex", "" + position);
                            }
                        });

                builder.addItem("保密");
                builder.addItem("男");
                builder.addItem("女");
                builder.build().show();


//                final String[] items = {"保密", "男", "女"};
//                new CircleDialog.Builder()
//                        .configDialog(new ConfigDialog() {
//                            @Override
//                            public void onConfig(DialogParams params) {
//                                //params.backgroundColorPress = Color.CYAN;
//                                //增加弹出动画
//                                //params.animStyle = R.style.dialogWindowAnim;
//                            }
//                        })
//                        //.setTitle("头像")
//                        .setItems(items, new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                modifyUerInfo("sex", "" + position);
//                            }
//                        })
//                        .setNegative("取消", null)
//                        .show(getSupportFragmentManager());
            }
            break;
            case R.id.birthdayLayout: {
                Calendar startDate = Calendar.getInstance();
                Date date = new Date();
                startDate.setTime(date);
                startDate.add(Calendar.YEAR, -100);
                Calendar endDate = Calendar.getInstance();
                endDate.setTime(date);
                TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        //birthday.setText(format.format(date));
                        modifyUerInfo("birthdate", format.format(date));
                    }
                }).setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                        .setCancelText("取消")//取消按钮文字
                        .setSubmitText("确定")//确认按钮文字
                        .setContentTextSize(20)//滚轮文字大小
                        .setTitleSize(22)//标题文字大小
                        .setTitleText("生日")//标题文字
                        .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                        .isCyclic(false)//是否循环滚动
                        .setTitleColor(0xFF3297FC)//标题文字颜色
                        .setSubmitColor(0xFF3297FC)//确定按钮文字颜色
                        .setCancelColor(0xFF3297FC)//取消按钮文字颜色
                        .setTitleBgColor(0xFFEEEEEE)//标题背景颜色 Night mode
                        .setBgColor(Color.WHITE)//滚轮背景颜色 Night mode
                        //.setDate(mCurrentDate)// 如果不设置的话，默认是系统时间*/
                        .setRangDate(startDate, endDate)//起始终止年月日设定
                        .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                        .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                        .isDialog(false)//是否显示为对话框样式
                        .build();
                pvTime.show();
            }
            break;
            case R.id.zoneLayout: {
                showPickerView();
            }
            break;
            case R.id.quit:
                User.setInstance(null);
                SharedPreferencesUtils.resetUser();
                //解绑消息推送账号
                final CloudPushService pushService = PushServiceFactory.getCloudPushService();
                pushService.unbindAccount(new CommonCallback() {
                    @Override
                    public void onSuccess(String s) {
                        Log.i("unbindAccount-onSuccess", s);
                    }

                    @Override
                    public void onFailed(String s, String s1) {
                        Log.i("unbindAccount-onFailed", s);
                        Log.i("unbindAccount-onFailed", s1);
                    }
                });
                finish();
                EventBus.getDefault().post(new CommonEvent("OnQuitLogin"));
                //注销用户信息
                break;
        }
    }

    @Override
    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
        Log.d("PutObject", "UploadSuccess");
        Log.d("ETag", result.getETag());
        Log.d("RequestId", result.getRequestId());
        long upload_end = System.currentTimeMillis();
        modifyUerInfo("avatar", mOssInfo.getHost() + "/" + mOssInfo.getDir() + tempFile.getName());
    }

    @Override
    public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
        String info = "";
        if (serviceException != null) {
            // 服务异常
            Log.e("ErrorCode", serviceException.getErrorCode());
            Log.e("RequestId", serviceException.getRequestId());
            Log.e("HostId", serviceException.getHostId());
            Log.e("RawMessage", serviceException.getRawMessage());
            info = serviceException.toString();
        }
    }

    public String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private void showPickerView() {// 弹出选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String opt1tx = options1Items.size() > 0 ?
                        options1Items.get(options1).getPickerViewText() : "";
                String opt2tx = options2Items.size() > 0
                        && options2Items.get(options1).size() > 0 ?
                        options2Items.get(options1).get(options2) : "";
                String opt3tx = options2Items.size() > 0
                        && options3Items.get(options1).size() > 0
                        && options3Items.get(options1).get(options2).size() > 0 ?
                        options3Items.get(options1).get(options2).get(options3) : "";
                String tx = opt1tx + " " + opt2tx + " " + opt3tx;
                //zone.setText(tx);
                modifyUerInfo("home_address", tx);
            }
        })
                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();
/*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    public void getOSSinfo() {
        String url = ServerInfo.serviceIP + ServerInfo.appOss;
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, null, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                String result = response;
                final JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    mOssInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), OssInfo.class);
                    mOssService = initOSS(mOssInfo.getHost(), mOssInfo.getBucket(), mOssInfo.getAccess_key_id(), mOssInfo.getAccess_key_secret(), mOssInfo.getExpiration(), mOssInfo.getSecurity_token());
                    //mOssService.setCallbackAddress(Config.callbackAddress);
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                Log.e("test", exception.toString());
            }
        });
    }

    public OssService initOSS(String endpoint, String bucket, String accessKey, String accessKeySecret, String expiration,
                              String securityToken) {
//        移动端是不安全环境，不建议直接使用阿里云主账号ak，sk的方式。建议使用STS方式。具体参
//        https://help.aliyun.com/document_detail/31920.html
//        注意：SDK 提供的 PlainTextAKSKCredentialProvider 只建议在测试环境或者用户可以保证阿里云主账号AK，SK安全的前提下使用。具体使用如下
//        主账户使用方式
//        String AK = "******";
//        String SK = "******";
//        credentialProvider = new PlainTextAKSKCredentialProvider(AK,SK)
//        以下是使用STS Sever方式。
//        如果用STS鉴权模式，推荐使用OSSAuthCredentialProvider方式直接访问鉴权应用服务器，token过期后可以自动更新。
//        详见：https://help.aliyun.com/document_detail/31920.html
//        OSSClient的生命周期和应用程序的生命周期保持一致即可。在应用程序启动时创建一个ossClient，在应用程序结束时销毁即可。
        OSSAKSKCredentialProvider oSSAKSKCredentialProvider;
        //使用自己的获取STSToken的类
        oSSAKSKCredentialProvider = new OSSAKSKCredentialProvider(accessKey, accessKeySecret, securityToken, expiration);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSS oss = new OSSClient(getApplicationContext(), endpoint, new OSSStsTokenCredentialProvider(accessKey, accessKeySecret, securityToken), conf);
        OSSLog.enableLog();
        return new OssService(oss, bucket);
    }
}
