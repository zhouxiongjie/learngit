package com.shuangling.software.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jaeger.library.StatusBarUtil;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.params.DialogParams;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.User;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ModifyUserInfoActivity extends AppCompatActivity implements Handler.Callback {

    public static final String TAG = ModifyUserInfoActivity.class.getName();

    private static final int CHOOSE_PHOTO = 0x0;
    private static final int TACK_PHOTO = 0x1;
    private static final int CUT_OK = 0x02;
    private static final int MSG_UPLOAD_HEAD = 0x03;

    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
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
    private Dialog mLoadDialog;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(MyApplication.getInstance().getCurrentTheme());
        setContentView(R.layout.activity_modify_userinfo);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        mHandler = new Handler(this);

        init();
    }


    private void init() {
        EventBus.getDefault().register(this);
        nickName.setText("" + User.getInstance().getNickname());
        if (!TextUtils.isEmpty(User.getInstance().getAvatar())) {
            Uri uri = Uri.parse(User.getInstance().getAvatar());
            ImageLoader.showThumb(uri, head, CommonUtils.dip2px(40), CommonUtils.dip2px(40));
        }
        tempFile = new File(CommonUtils.getStoragePublicDirectory(), "temp.png");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TACK_PHOTO) {
            if (resultCode == RESULT_OK && data != null) {
                //保存图片信息
                String path = data.getStringExtra("path");
                File file = new File(path);
                clipImage(Uri.fromFile(file));

            } else {
                Toast.makeText(this, "用户取消拍照", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK && data != null) {
//                List<Uri> selects = Matisse.obtainResult(data);
//                File file = new File(CommonUtils.getRealFilePath(this, selects.get(0)));
//                clipImage(Uri.fromFile(file));

            } else {
                Toast.makeText(this, "用户取消拍照", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CUT_OK) {
            if (resultCode == RESULT_OK && data != null) {
                // 获取裁剪的图片数据
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bitmap = extras.getParcelable("data");

                    CommonUtils.saveBitmap(tempFile.getAbsolutePath(), bitmap);
                    //saveBitmapToFile(bitmap);
//
                    // 2.把图片文件file上传到服务器
                    uploadImage();
                }
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
        intent.putExtra("return-data", true);
        // 你待会裁剪完之后需要获取数据   startActivityForResult
        startActivityForResult(intent, CUT_OK);
    }


    private void uploadImage() {

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
                final String[] items = { "从相册选择","拍照", "查看大图"};
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

                                }else if(position==1){

                                }else {

                                }
                            }
                        })
                        .setNegative("取消", null)
                        .show(getSupportFragmentManager());
                }
                break;
            case R.id.nickNameLayout:
                break;
            case R.id.sexLayout:
                break;
            case R.id.birthdayLayout:
                break;
            case R.id.zoneLayout:
                break;
            case R.id.quit:
                User.setInstance(null);
                finish();
                //注销用户信息
                break;
        }
    }


    @Subscribe
    public void getEventBus(Object msg) {

    }
}
