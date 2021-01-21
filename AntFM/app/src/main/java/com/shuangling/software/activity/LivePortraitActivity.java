package com.shuangling.software.activity;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.vodplayerview.utils.ScreenUtils;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.params.ButtonParams;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.shuangling.software.R;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import net.mrbin99.laravelechoandroid.EchoCallback;
import net.mrbin99.laravelechoandroid.EchoOptions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import okhttp3.Call;

import static android.view.animation.Animation.INFINITE;


public class LivePortraitActivity extends QMUIActivity {

    private static final String TAG = LivePortraitActivity.class.getSimpleName();

    private static final int CHOOSE_PHOTO = 0x0;

    @BindView(R.id.viewerNumber)
    TextView viewerNumber;
    @BindView(R.id.viewerContainer)
    LinearLayout viewerContainer;
    @BindView(R.id.head)
    SimpleDraweeView head;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.praiseNumber)
    TextView praiseNumber;
    @BindView(R.id.hotRange)
    TextView hotRange;
    @BindView(R.id.inviteRange)
    TextView inviteRange;
    @BindView(R.id.giftContainer)
    LinearLayout giftContainer;
    @BindView(R.id.shakeRedPacket)
    ImageView shakeRedPacket;
    @BindView(R.id.redPacketStatus)
    TextView redPacketStatus;
    @BindView(R.id.redPacket)
    FrameLayout redPacket;
    @BindView(R.id.chat_msg_list)
    RecyclerView chatMsgList;
    @BindView(R.id.more_msg_btn)
    ImageButton moreMsgBtn;
    @BindView(R.id.chatBtn)
    TextView chatBtn;
    @BindView(R.id.goods)
    ImageButton goods;
    @BindView(R.id.invite)
    ImageButton invite;
    @BindView(R.id.more)
    ImageButton more;
    @BindView(R.id.exist)
    ImageButton exist;
    @BindView(R.id.bottomBar)
    LinearLayout bottomBar;
    @BindView(R.id.chatInput)
    EditText chatInput;
    @BindView(R.id.bottomLayout)
    FrameLayout bottomLayout;

    private List<Long> selectedGoodsId;

    public List<Long> getSelectedGoodsId() {
        return selectedGoodsId;
    }

    public void setSelectedGoodsId(List<Long> selectedGoodsId) {
        this.selectedGoodsId = selectedGoodsId;
    }

    private String push_url;

    private Handler mMainHandler;
    private int bitrate = 5000;
    private int frameRateValue = 25;

    private boolean mVideoPublish;
    private boolean openBeauty = false;
    private boolean mFrontCamera = true;

    private int mBeautyLevel = 9;
    private int mWhiteningLevel = 9;
    private int mRuddyLevel = 9;

    private static final int Quality_Normal = 1; //标清
    private static final int Quality_High = 2; //高清
    private int currentQuality = Quality_High;

    private Timer timer;
    private int secondCounts = 3;


    private PhoneStateListener mPhoneListener = null;
    private LiveChatListAdapter mAdapter;
    private MyChatListAdapter landscapeAdapter;
    private MyEcho echo;

    private String room_id = "180";
    private String stream_name = "F9B7C8FE2AF571CD74E0C3FDB799E030";

    private String msgUrl = ServerInfo.live + "/v1/push_message_c";
    private String stateUrl = ServerInfo.live + "/v1/set_state";

    private HashMap<String, String> msgMap = new HashMap<>();

    private boolean isPortrait = true;//默认是竖屏
    private PushPortraitMenuDialog pushPortraitMenuDialog;
    private PushPortraitManageDialog pushPortraitManageDialog;
    private PushLandscapeMenuDialog pushLandscapeMenuDialog;
    private PushLandscapeManageDialog pushLandscapeManageDialog;

    private HeightProvider heightProvider;
    private int autoRotate = 0;

    private CreatedLiveRoomInfo createdLiveRoomInfo;
    private LiveRoomInfo roomInfo;

    /**
     * 7牛推流相关
     */

    private RotateLayout mRotateLayout;

    private String mStatusMsgContent;
    private String mLogContent = "\n";

    private boolean mIsReady;
    private boolean mIsPreviewMirror = false;
    private boolean mIsEncodingMirror = false;
    private boolean mIsPlayingback = false;
    private boolean mAudioStereoEnable = false;
    private boolean mIsStreaming = false;

    private volatile boolean mIsSupportTorch = false;

    private int mCurrentZoom = 0;
    private int mMaxZoom = 0;
    private boolean mOrientationChanged = false;
    private int mCurrentCamFacingIndex;
    private int mFrameWidth = 100;
    private int mFrameHeight = 100;
    private Boolean isAddRoom = false;


    // private ControlFragment mControlFragment;

    // 用作演示自定义美颜实现逻辑
    private FBO mFBO = new FBO();

    protected EncodingConfig mEncodingConfig;
    private CameraConfig mCameraConfig;

    private boolean mIsQuicEnabled = false;
    private String mPicStreamingFilePath;

    // 推流操作管理类实例
    private MediaStreamingManager mMediaStreamingManager;
    // 推流编码配置类实例
    private StreamingProfile mProfile;
    // 推流采集配置类实例
    private CameraStreamingSetting mCameraStreamingSetting;
    // 推流水印配置类实例
    private WatermarkSetting mWatermarkSetting;
    // 推流混音管理类实例
    private AudioMixer mAudioMixer;
    private String mAudioFile;

    // 用于处理子线程操作
    private Handler mSubThreadHandler;

    // 用于处理图片推流延时切换图片
    private Handler mMainThreadHandler;
    private ImageSwitcher mImageSwitcher;
    private int mTimes = 0;
    private boolean mIsPictureStreaming = false;
    private int push_resolution = 1;

    private boolean mIsPushing;


    private DialogFragment mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_live_portrait);
        ButterKnife.bind(this);

        //显示主持人头像
        if (!TextUtils.isEmpty(User.getInstance().getAvatar())) {
            Uri uri = Uri.parse(User.getInstance().getAvatar());
            ImageLoader.showThumb(uri, head, CommonUtils.dip2px(32), CommonUtils.dip2px(32));
        }
        name.setText(User.getInstance().getNickname());


        LPAnimationManager.init(this);
        LPAnimationManager.addGiftContainer(giftContainer);

        mMainHandler = new Handler(Looper.getMainLooper());
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mSubThreadHandler = new Handler(handlerThread.getLooper());


        isAddRoom = getIntent().getBooleanExtra("isAddRoom", false);

        if (isAddRoom) {
            CreatedLiveRoomInfo cri = new CreatedLiveRoomInfo();
            if (User.getInstance().getUser_b().getIs_super_admin() == 1 && User.getInstance().getUser_b().getMerchant().getParent_id() > 0) {
                cri.setMerchant_id("" + User.getInstance().getUser_b().getMerchant().getParent_id());
                cri.setAnchor_id("" + User.getInstance().getUser_b().getMerchant_id());
            } else if (User.getInstance().getUser_b().getIs_super_admin() == 0) {
                cri.setMerchant_id("" + User.getInstance().getUser_b().getMerchant_id());
                cri.setAnchor_id("" + User.getInstance().getUser_b().getMerchant_id());
            } else {
                cri.setMerchant_id("" + User.getInstance().getUser_b().getMerchant_id());
                cri.setAnchor_id("" + User.getInstance().getUser_b().getMerchant_id());
            }
            createdLiveRoomInfo = cri;
            live_title_layout.setVisibility(View.VISIBLE);
            liveTitle.setText(User.getInstance().getNickname() + "的直播间");
        } else {
            live_title_layout.setVisibility(View.GONE);
            createdLiveRoomInfo = (CreatedLiveRoomInfo) getIntent().getSerializableExtra("CreatedLiveRoomInfo");
            roomInfo = (LiveRoomInfo) getIntent().getSerializableExtra("RoomInfo");
            initLive();
        }

        initChatView();
        initQiniu();


        checkPublishPermission();
//        默认先竖屏显示设置界面
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnQualitySelect.setVisibility(View.GONE);
        btnQualityCurrent.setText("高清");

        autoRotate = ScreenUtils.getHeight(this) - ScreenUtils.getWidth(this);
        heightProvider = new HeightProvider(this).init().setHeightListener(new HeightProvider.HeightListener() {
            @Override
            public void onHeightChanged(int height) {

                if (autoRotate == height) return;

                if (isPortrait) {//竖屏

                    if (height > 200) {
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
                        layoutParams.setMargins(0, 0, 0, height);
                        bottomLayout.setLayoutParams(layoutParams);
                    } else {
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
                        layoutParams.setMargins(0, 0, 0, 0);
                        bottomLayout.setLayoutParams(layoutParams);

                        bottomBar.setVisibility(View.VISIBLE);
                        chatInput.setVisibility(View.GONE);
                    }

                } else {//横屏
                    if (height > 200) {
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomToolView2.getLayoutParams();
                        layoutParams.setMargins(0, 0, 0, height);
                        bottomToolView2.setLayoutParams(layoutParams);
                    } else {
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomToolView2.getLayoutParams();
                        layoutParams.setMargins(0, 0, 0, 0);
                        bottomToolView2.setLayoutParams(layoutParams);

                        chatBtn2.setVisibility(View.VISIBLE);
                        menuBtn2.setVisibility(View.VISIBLE);
                        chatInput2.setVisibility(View.GONE);
                    }
                }

            }
        });
    }


    private void initLive() {

        if (roomInfo != null) {
            push_url = roomInfo.getPush_url();
            room_id = roomInfo.getId() + "";
            stream_name = roomInfo.getStream_name();
            getAuthKey(roomInfo);
            initChatRoom();
            getLiveRoomDetail();
            getRedPacketRecord();
        }


    }


    private void switchFlow(boolean open) {

        if (roomInfo == null) {
            return;
        }
        String url = ServerInfo.live + "/v1/merchants/rooms/" + roomInfo.getId() + "/flow";
        Map<String, String> params = new HashMap<>();
        params.put("state", open ? "1" : "0");

        OkHttpUtils.patch(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

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


    private void getLiveRoomDetail() {
        String url = ServerInfo.live + "/v3/get_room_details";
        Map<String, String> params = new HashMap<>();
        params.put("stream_name", roomInfo.getStream_name());

        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        List<LiveRoomInfo> liveRoomInfos = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), LiveRoomInfo.class);

                        if (liveRoomInfos != null && liveRoomInfos.size() > 0) {

                            LiveRoomInfo liveRoomInfo = liveRoomInfos.get(0);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (liveRoomInfo.getOnline_user_info() != null) {
                                        viewerNumber.setText("" + liveRoomInfo.getOnline_user_info().getOnline_count());

                                        viewerContainer.removeAllViews();
                                        praiseNumber.setText(CommonUtils.getShowNumber(liveRoomInfo.getHeart_num()) + "本场点赞");
                                        for (int i = 0; liveRoomInfo.getOnline_user_info().getAvatar_list() != null && i < liveRoomInfo.getOnline_user_info().getAvatar_list().size(); i++) {
                                            String avatar = liveRoomInfo.getOnline_user_info().getAvatar_list().get(i).getAvatar();
                                            SimpleDraweeView head = new SimpleDraweeView(getContext());
                                            head.setAspectRatio(1f);
                                            RoundingParams roundingParams = head.getHierarchy().getRoundingParams();
                                            if (roundingParams == null) {
                                                roundingParams = new RoundingParams();
                                            }
                                            roundingParams.setRoundAsCircle(true);
                                            head.getHierarchy().setRoundingParams(roundingParams);
                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonUtils.dip2px(30), CommonUtils.dip2px(30));
                                            params.rightMargin = CommonUtils.dip2px(5);
                                            viewerContainer.addView(head, params);
                                            Uri uri = Uri.parse(avatar);
                                            ImageLoader.showThumb(uri, head, CommonUtils.dip2px(30), CommonUtils.dip2px(30));

                                        }

                                    }

                                }
                            });

                        }
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


    protected void onActivityRotation() {

        // 自动旋转打开，Activity随手机方向旋转之后，需要改变推流方向
        int mobileRotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_DOWN;
        boolean screenCaptureLandscape = false;
        switch (mobileRotation) {
            case Surface.ROTATION_0:
                pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_DOWN;
                isPortrait = true;
                break;
            case Surface.ROTATION_180:
                pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_UP;
                isPortrait = true;
                break;
            case Surface.ROTATION_90:
                pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT;
                screenCaptureLandscape = true;
                isPortrait = false;
                break;
            case Surface.ROTATION_270:
                pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_LEFT;
                screenCaptureLandscape = true;
                isPortrait = false;
                break;
            default:
                break;
        }


        mProfile.setEncodingOrientation(isPortrait ? StreamingProfile.ENCODING_ORIENTATION.PORT : StreamingProfile.ENCODING_ORIENTATION.LAND);

        // 更新 StreamingProfile 的时候，需要重新推流才可以生效!!!


        stopStreamingInternal();
        mMediaStreamingManager.notifyActivityOrientationChanged();
        startStreamingInternal(200);

        if (mAdapter != null && landscapeAdapter != null) {
            if (isPortrait) {//竖屏
                pushLandscapeView.setVisibility(View.GONE);
                pushPortraitView.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
                if (mAdapter.getItemCount() > 0)
                    chatMsgList.scrollToPosition(mAdapter.getItemCount() - 1);
                moreMsgBtn.setVisibility(View.GONE);

            } else {//横屏
                pushPortraitView.setVisibility(View.GONE);
                pushLandscapeView.setVisibility(View.VISIBLE);
                landscapeAdapter.notifyDataSetChanged();
                if (landscapeAdapter.getItemCount() > 0)
                    chatMsgList2.scrollToPosition(landscapeAdapter.getItemCount() - 1);
                moreMsgBtn2.setVisibility(View.GONE);
                pushLandscapeView.requestLayout();
            }
        }


    }


    private void initQiniu() {


        int pushSizeLevel = StreamingProfile.VIDEO_ENCODING_HEIGHT_480;

        StreamingProfile.VideoProfile vProfile;
        CameraStreamingSetting.PREVIEW_SIZE_LEVEL prevSizeLevel = CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM;
        if (push_resolution == 1) {
            pushSizeLevel = StreamingProfile.VIDEO_ENCODING_HEIGHT_1088;
            prevSizeLevel = CameraStreamingSetting.PREVIEW_SIZE_LEVEL.LARGE;
            bitrate = 5000;
            vProfile = new StreamingProfile.VideoProfile(20, 1920 * 1080, 30);
        } else if (push_resolution == 2) {
            pushSizeLevel = StreamingProfile.VIDEO_ENCODING_HEIGHT_720;
            prevSizeLevel = CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM;
            bitrate = 1200;
            vProfile = new StreamingProfile.VideoProfile(20, 1080 * 720, 30);
        } else {
            pushSizeLevel = StreamingProfile.VIDEO_ENCODING_HEIGHT_480;
            prevSizeLevel = CameraStreamingSetting.PREVIEW_SIZE_LEVEL.SMALL;
            bitrate = 800;
            vProfile = new StreamingProfile.VideoProfile(20, 640 * 480, 30);
        }


        CameraPreviewFrameView cameraPreviewFrameView = findViewById(R.id.cameraPreview_surfaceView);

        //encoding setting
        mProfile = new StreamingProfile();
        mProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH3)
                .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)
                .setEncodingSizeLevel(pushSizeLevel)
                .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY)
                .setVideoAdaptiveBitrateRange(bitrate - 200, bitrate);

        StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(44100, 48 * 1024);

        StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);
        mProfile.setAVProfile(avProfile);
        mProfile.setBitrateAdjustMode(StreamingProfile.BitrateAdjustMode.Auto);

        //preview setting
        CameraStreamingSetting mCameraStreamingSetting = new CameraStreamingSetting();
        mCameraStreamingSetting.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT)
                .setContinuousFocusModeEnabled(true)
                .setCameraPrvSizeLevel(prevSizeLevel)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9)
                .setResetTouchFocusDelayInMs(3000)
                .setBuiltInFaceBeautyEnabled(true)
                .setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.0f, 0.0f, 0.0f));

        mCameraStreamingSetting.setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY);


        //streaming engine init and setListener
        mMediaStreamingManager = new MediaStreamingManager(this, cameraPreviewFrameView, AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC);  // soft codec
        mMediaStreamingManager.prepare(mCameraStreamingSetting, mProfile);
        mMediaStreamingManager.setStreamingStateListener(this);
        mMediaStreamingManager.setStreamingSessionListener(this);
        mMediaStreamingManager.setStreamStatusCallback(this);
        mMediaStreamingManager.setAudioSourceCallback(this);


    }


    private void initChatView() {

        if (mAdapter == null) {
            mAdapter = new LiveChatListAdapter(this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setStackFromEnd(true);
            chatMsgList.setLayoutManager(linearLayoutManager);
            chatMsgList.setAdapter(mAdapter);
            chatMsgList.setHasFixedSize(true);

            chatMsgList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!recyclerView.canScrollVertically(30)) {
                        moreMsgBtn.setVisibility(View.GONE);
                    }
                }
            });

        }

        if (landscapeAdapter == null) {
            landscapeAdapter = new MyChatListAdapter(this, 1);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setStackFromEnd(true);
            chatMsgList2.setLayoutManager(linearLayoutManager);
            chatMsgList2.setAdapter(landscapeAdapter);

            chatMsgList2.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!recyclerView.canScrollVertically(30)) {
                        moreMsgBtn2.setVisibility(View.GONE);
                    }
                }
            });
        }


    }


    //开始推流
    private void startPushRtmp() {
        if (TextUtils.isEmpty(push_url) || (!push_url.trim().toLowerCase().startsWith("rtmp://"))) {
            Toast.makeText(getApplicationContext(), "推流地址不合法，目前支持rtmp推流!", Toast.LENGTH_SHORT).show();
            return;
        }

        int customModeType = 0;
//        if (isActivityCanRotation()) {
//            onActivityRotation();
//        }

        try {
            mProfile.setPublishUrl(push_url);
            mMediaStreamingManager.setStreamingProfile(mProfile);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        startStreamingInternal(200);

        mIsPushing = true;

        //add by zxj   蓝牙适配
        if (getheadsetStatsu() == 2) {
            setBluetooth();
        }
//        mLivePusher.startPusher(push_url.trim());
        settingView.setVisibility(View.GONE);
        pushPortraitView.setVisibility(View.VISIBLE);


        //pushLiveroomState("1");
    }

    /**
     * 获取资源图片
     *
     * @param resources
     * @param id
     * @return
     */
    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }

    private void initChatRoom() {

        msgMap.put("room_id", room_id);//直播间ID
        msgMap.put("user_id", User.getInstance().getId() + "");//用户ID
        msgMap.put("message", "");//消息内容
        msgMap.put("type", "1");//发布端类型：1.主持人   2：用户    3:通知关注  4：通知进入直播间
        msgMap.put("stream_name", stream_name);//播间推流ID
        msgMap.put("nick_name", User.getInstance().getUser_b().getStaff_name());//昵称
        msgMap.put("message_type", "1");//消息类型 1.互动消息  2.直播状态更新消息  3.删除消息  4.题目 5.菜单设置 6图文保存  默认1
        msgMap.put("user_logo", User.getInstance().getAvatar());

        EchoOptions options = new EchoOptions();
        options.host = ServerInfo.echo_server;
        echo = new MyEcho(options);
        echo.connect(new EchoCallback() {
            @Override
            public void call(Object... args) {
                Log.i("test", "Success connect");
            }
        }, new EchoCallback() {
            @Override
            public void call(Object... args) {
                // Error connect
                Log.i("test", "Error connect");
            }
        });


        echo.channel(stream_name)
                .listen("Chat", new EchoCallback() {
                    @Override
                    public void call(Object... args) {
                        // Event thrown.
                        Log.i("test", "args");

                        try {
                            ChatMessage msg = JSON.parseObject(args[1].toString(), ChatMessage.class);

                            if (msg.getMessageType() == 1 || msg.getMessageType() == 13) {
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showMsg(msg);
                                    }
                                });
                            } else if (msg.getMessageType() == 17) {
                                //红包活动结束
                                getRedPacketRecord();
                            } else if (msg.getMessageType() == 12) {
                                //打赏礼物消息通知
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        String message = msg.getMsg();
                                        JSONObject object = JSONObject.parseObject(message);
                                        if (object != null) {
                                            String userName = object.getString("nick_name");
                                            String giftName = object.getString("prop_name");
                                            String headUrl = msg.getUserLog();
                                            int giftNum = object.getInteger("amount");
                                            String giftUrl = object.getString("prop_icon_url");

                                            LPAnimationManager.addAnimalMessage(new AnimMessage(userName, headUrl, giftNum, giftName, giftUrl));
                                        }


                                    }
                                });

                            }


//                            else if (msg.getMessageType() == 13) {
//
//
//                                mMainHandler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        ChatMessage message=new ChatMessage();
//                                        message.setContentType(1);
//                                        message.setMsg("点赞");
//                                        message.setType(msg.getType());
//                                        message.setUserLog(msg.getUserLog());
//                                        message.setNickName(msg.getNickName());
//                                        showMsg(message);
//                                    }
//                                });
//                            }

                        } catch (Exception e) {
                            Log.v("SL", e.getMessage());
                        }
                    }
                }).listen("LikeBroadcastEvent", new EchoCallback() {
            @Override
            public void call(Object... args) {
                // Event thrown.
                Log.i("test", "args");

                try {

                    JSONObject object = JSONObject.parseObject(args[1].toString());
                    if (object != null) {
                        int heartNum = object.getInteger("heart_num");
                        praiseNumber.setText(CommonUtils.getShowNumber(heartNum) + "本场点赞");
                    }


                } catch (Exception e) {
                    Log.v("SL", e.getMessage());
                }
            }
        }).listen("UserChangeEvent", new EchoCallback() {
            @Override
            public void call(Object... args) {
                // Event thrown.
                Log.i("test", "args");

                try {


                    LiveOnlineViewer liveOnlineViewer = JSON.parseObject(args[1].toString(), LiveOnlineViewer.class);
                    if (liveOnlineViewer != null && liveOnlineViewer.getMsg() != null) {
                        LiveOnlineViewer.MsgBean msgBean = liveOnlineViewer.getMsg();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    viewerNumber.setText("" + msgBean.getOnline_count());
                                    viewerContainer.removeAllViews();
                                    for (int i = 0; msgBean.getAvatar_list() != null && i < msgBean.getAvatar_list().size(); i++) {
                                        String avatar = msgBean.getAvatar_list().get(i).getAvatar();
                                        SimpleDraweeView head = new SimpleDraweeView(LDLivePushActivity.this);
                                        GenericDraweeHierarchy hierarchy = head.getHierarchy();
                                        hierarchy.setPlaceholderImage(R.drawable.ic_head01);
                                        head.setAspectRatio(1f);
                                        RoundingParams roundingParams = head.getHierarchy().getRoundingParams();
                                        if (roundingParams == null) {
                                            roundingParams = new RoundingParams();
                                        }
                                        roundingParams.setRoundAsCircle(true);
                                        head.getHierarchy().setRoundingParams(roundingParams);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonUtils.dip2px(30), CommonUtils.dip2px(30));
                                        params.rightMargin = CommonUtils.dip2px(5);
                                        viewerContainer.addView(head, params);
                                        Uri uri = Uri.parse(avatar);
                                        ImageLoader.showThumb(uri, head, CommonUtils.dip2px(30), CommonUtils.dip2px(30));

                                    }
                                } catch (Exception e) {

                                }

                            }
                        });


                    }

                } catch (Exception e) {
                    Log.v("SL", e.getMessage());
                }
            }
        });

        //echo.send(content.getText().toString().trim());
        //echo.leave("A501FB38FB4D79ACFFE1D85761BB0BF5");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onActivityRotation();
    }

    /**
     * 初始化电话监听、系统是否打开旋转监听
     */
    private void initListener() {
        //  mPhoneListener = new TXPhoneStateListener(mLivePusher);
        TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

//        mActivityRotationObserver = new ActivityRotationObserver(new Handler(Looper.getMainLooper()));
//        mActivityRotationObserver.startObserver();
    }


    /**
     * 判断系统 "自动旋转" 设置功能是否打开
     *
     * @return false---Activity可根据重力感应自动旋转
     */
    public boolean isActivityCanRotation() {
        // 判断自动旋转是否打开
        int flag = Settings.System.getInt(this.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
        if (flag == 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

            if (isPortrait) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMsg(chatInput.getText().toString());
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(chatInput, InputMethodManager.SHOW_IMPLICIT);
            } else {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMsg(chatInput2.getText().toString());
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(chatInput2, InputMethodManager.SHOW_IMPLICIT);
            }


            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    private void sendMsg(String msg) {
        if (TextUtils.isEmpty(msg)) return;

        if (isPortrait) {
            chatInput.setText(null);
        } else {
            chatInput2.setText(null);
        }

        msgMap.put("message", msg);
        OkHttpUtils.post(msgUrl, msgMap, new OkHttpCallback(this) {
            @Override
            public void onFailure(Call call, Exception e) {
                Log.e("test", e.getCause().getMessage());
            }

            @Override
            public void onResponse(Call call, String response) throws IOException {
                Log.e("test", response);
            }
        });

    }


    private void showMsg(ChatMessage msgModel) {
//        msgList.add(msgModel);
//        msgLandscapeList.add(msgModel);


        //判断横竖屏 看刷新哪一个listview
        if (isPortrait) {
            long id = Thread.currentThread().getId();

            mAdapter.showChatMessage(msgModel);
            if (chatMsgList.canScrollVertically(1)) {//还可以向下滑动（还没到底部）
                moreMsgBtn.setVisibility(View.VISIBLE);

            } else {//滑动到底部了

                ((LinearLayoutManager) chatMsgList.getLayoutManager()).scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);
                //chatMsgList.scrollToPosition(mAdapter.getItemCount() - 1);

                moreMsgBtn.setVisibility(View.GONE);
            }

//            landscapeAdapter.addChatMsg(msgModel);
//            mAdapter.showChatMsg(msgModel);
//            if (chatMsgList.canScrollVertically(1)) {//还可以向下滑动（还没到底部）
//                moreMsgBtn.setVisibility(View.VISIBLE);
//
//            } else {//滑动到底部了
//                chatMsgList.scrollToPosition(mAdapter.getItemCount() - 1);
//                moreMsgBtn.setVisibility(View.GONE);
//            }

        } else {
//            adapter.addChatMsg(msgModel);
//            landscapeAdapter.showChatMsg(msgModel);
//            if (chatMsgList2.canScrollVertically(1)) {//还可以向上滑动（还没到底部）
//                moreMsgBtn2.setVisibility(View.VISIBLE);
//
//            } else {//滑动到底部了
//                chatMsgList2.scrollToPosition(landscapeAdapter.getItemCount() - 1);
//                moreMsgBtn2.setVisibility(View.GONE);
//            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                break;
            default:
                break;
        }
    }

    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this,
                        permissions.toArray(new String[0]),
                        100);
                return false;
            }
        }
        return true;
    }

    @OnClick({R.id.btn_back, R.id.btn_switch_camera, R.id.btn_beauty, R.id.btn_quality_select,
            R.id.btn_quality_current, R.id.start_push, R.id.btn_back2, R.id.btn_switch_camera2,
            R.id.menu_btn, R.id.push_portrait_view, R.id.more_msg_btn,
            R.id.btn_back3, R.id.btn_switch_camera3, R.id.push_chat_view_open, R.id.more_msg_btn2,
            R.id.chat_btn2, R.id.menu_btn2, R.id.push_chat_view_close, R.id.setting_view,
            R.id.invite, R.id.exist, R.id.viewerNumber, R.id.more, R.id.hotRange, R.id.inviteRange,
            R.id.chatBtn, R.id.reversalCamera, R.id.beauty, R.id.qualitySetting, R.id.cover, R.id.goods})
    public void onViewClicked(View view) {
        if (ClickProxy.isFastDoubleClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_back: {
                //pushLiveroomState("2");
                finish();
            }
            break;

            case R.id.setting_view: {
                btnQualitySelect.setVisibility(View.GONE);
            }
            break;
            case R.id.btn_switch_camera: {
                //  mLivePusher.switchCamera();
                mMediaStreamingManager.switchCamera();

                if (mSubThreadHandler != null) {
                    mSubThreadHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //如果换了摄像头，要重新开美颜
                            if (openBeauty) {
                                mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.8f, 0.8f, 0.8f));
                            } else {
                                mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.0f, 0.0f, 0.0f));
                            }

                        }
                    }, 300);
                }
            }
            break;
            case R.id.btn_beauty: {
                openBeauty = !openBeauty;
                btnBeauty.setSelected(openBeauty);
                if (openBeauty) {
                    mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.8f, 0.8f, 0.8f));
                } else {
                    mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.0f, 0.0f, 0.0f));
                }
            }
            break;
            case R.id.btn_quality_select: {
                if (currentQuality == Quality_Normal) {//目前是标清
                    currentQuality = Quality_High;
                    btnQualitySelect.setVisibility(View.GONE);
                    btnQualityCurrent.setText("高清");
                    bitrate = 5000;
                    push_resolution = 1;
                    int pushSizeLevel = StreamingProfile.VIDEO_ENCODING_HEIGHT_1088;

                    //mMediaStreamingManager.pause();

                    CameraStreamingSetting.PREVIEW_SIZE_LEVEL prevSizeLevel = CameraStreamingSetting.PREVIEW_SIZE_LEVEL.LARGE;
                    mProfile.setEncodingSizeLevel(pushSizeLevel)
                            .setVideoAdaptiveBitrateRange(bitrate - 200, bitrate);
                    // mCameraStreamingSetting.setCameraPrvSizeLevel(prevSizeLevel);

                    StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(44100, 48 * 1024);
                    StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(20, 1080 * 1920, 30);
                    StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);
                    mProfile.setAVProfile(avProfile);
                    mProfile.setBitrateAdjustMode(StreamingProfile.BitrateAdjustMode.Auto);


                    mMediaStreamingManager.setStreamingProfile(mProfile);
                    // mMediaStreamingManager.resume();

                    // mCameraStreamingSetting.setCameraPrvSizeLevel(prevSizeLevel);

                } else {//目前是高清
                    // mMediaStreamingManager.pause();
                    currentQuality = Quality_Normal;
                    btnQualitySelect.setVisibility(View.GONE);
                    btnQualityCurrent.setText("标清");
                    bitrate = 1200;


                    push_resolution = 2;
                    int pushSizeLevel = StreamingProfile.VIDEO_ENCODING_HEIGHT_720;

                    //mMediaStreamingManager.pause();

                    CameraStreamingSetting.PREVIEW_SIZE_LEVEL prevSizeLevel = CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM;
                    mProfile.setEncodingSizeLevel(pushSizeLevel)
                            .setVideoAdaptiveBitrateRange(bitrate - 200, bitrate);
                    // mCameraStreamingSetting.setCameraPrvSizeLevel(prevSizeLevel);

                    StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(44100, 48 * 1024);
                    StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(20, 720 * 1080, 30);
                    StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);
                    mProfile.setAVProfile(avProfile);
                    mProfile.setBitrateAdjustMode(StreamingProfile.BitrateAdjustMode.Auto);
                    mMediaStreamingManager.setStreamingProfile(mProfile);
                    // mMediaStreamingManager.resume();
                }

            }

            break;
            case R.id.btn_quality_current: {
                if (currentQuality == Quality_Normal) {
                    btnQualitySelect.setText("高清");
                } else {
                    btnQualitySelect.setText("标清");
                }

                if (btnQualitySelect.isShown()) {
                    btnQualitySelect.setVisibility(View.GONE);
                } else {
                    btnQualitySelect.setVisibility(View.VISIBLE);
                }

            }
            break;
            case R.id.start_push: {

                if (roomInfo != null) {
                    if (roomInfo.getState() == 2) {
                        pushSetting.setVisibility(View.GONE);
                        setting.setVisibility(View.GONE);
                        countdownText.setVisibility(View.VISIBLE);
                        startPush.setVisibility(View.GONE);
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                secondCounts--;
                                if (secondCounts == 0) {
                                    timer.cancel();
                                    timer = null;
                                    mMainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            countdownText.setVisibility(View.GONE);
                                            startPushRtmp();
                                        }
                                    });
                                } else {

                                    mMainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            countdownText.setText(secondCounts + "");
                                        }
                                    });
                                }

                            }
                        }, 1000, 1000);
                    } else {
                        pushLiveroomState(roomInfo, "1");
                    }

                } else {

                    createLiveRoom();

                }


            }
            break;
            case R.id.btn_back2:
                //pushLiveroomState("2");
                AppManager.finishAllActivity();
                //finish();
                break;
            case R.id.btn_switch_camera2:
                mMediaStreamingManager.switchCamera();
                if (mSubThreadHandler != null) {
                    mSubThreadHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //如果换了摄像头，要重新开美颜

                            if (openBeauty) {
                                mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.8f, 0.8f, 0.8f));
                            } else {
                                mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.0f, 0.0f, 0.0f));
                            }

                            if (currentQuality == Quality_High) {//目前是标清
                                btnQualitySelect.setVisibility(View.GONE);
                                btnQualityCurrent.setText("高清");
                                bitrate = 5000;
                                push_resolution = 1;
                                int pushSizeLevel = StreamingProfile.VIDEO_ENCODING_HEIGHT_1088;

                                //mMediaStreamingManager.pause();

                                CameraStreamingSetting.PREVIEW_SIZE_LEVEL prevSizeLevel = CameraStreamingSetting.PREVIEW_SIZE_LEVEL.LARGE;
                                mProfile.setEncodingSizeLevel(pushSizeLevel)
                                        .setVideoAdaptiveBitrateRange(bitrate - 200, bitrate);
                                // mCameraStreamingSetting.setCameraPrvSizeLevel(prevSizeLevel);

                                StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(44100, 48 * 1024);
                                StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(20, 1920 * 1080, 30);
                                StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);
                                mProfile.setAVProfile(avProfile);
                                mProfile.setBitrateAdjustMode(StreamingProfile.BitrateAdjustMode.Auto);


                                mMediaStreamingManager.setStreamingProfile(mProfile);
                                // mMediaStreamingManager.resume();

                                // mCameraStreamingSetting.setCameraPrvSizeLevel(prevSizeLevel);

                            } else {//目前是高清
                                // mMediaStreamingManager.pause();
                                currentQuality = Quality_Normal;
                                btnQualitySelect.setVisibility(View.GONE);
                                btnQualityCurrent.setText("标清");
                                bitrate = 1200;


                                push_resolution = 2;
                                int pushSizeLevel = StreamingProfile.VIDEO_ENCODING_HEIGHT_720;

                                //mMediaStreamingManager.pause();

                                CameraStreamingSetting.PREVIEW_SIZE_LEVEL prevSizeLevel = CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM;
                                mProfile.setEncodingSizeLevel(pushSizeLevel)
                                        .setVideoAdaptiveBitrateRange(bitrate - 200, bitrate);
                                // mCameraStreamingSetting.setCameraPrvSizeLevel(prevSizeLevel);

                                StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(44100, 48 * 1024);
                                StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(20, 1080 * 720, 30);
                                StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);
                                mProfile.setAVProfile(avProfile);
                                mProfile.setBitrateAdjustMode(StreamingProfile.BitrateAdjustMode.Auto);
                                mMediaStreamingManager.setStreamingProfile(mProfile);
                                // mMediaStreamingManager.resume();
                            }

                        }
                    }, 300);
                }
                break;
            case R.id.chatBtn: {
                bottomBar.setVisibility(View.GONE);
                chatInput.setVisibility(View.VISIBLE);
                chatInput.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(chatInput, InputMethodManager.SHOW_IMPLICIT);
            }
            break;
            case R.id.menu_btn:

                showPushMenu();

                break;

            case R.id.push_portrait_view:

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(chatInput.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

                chatBtn.setVisibility(View.VISIBLE);
                menuBtn.setVisibility(View.VISIBLE);
                chatInput.setVisibility(View.GONE);

                break;

            case R.id.more_msg_btn: {
                moreMsgBtn.setVisibility(View.GONE);
                chatMsgList.scrollToPosition(mAdapter.getItemCount() - 1);
            }
            break;

            case R.id.btn_back3:
                //pushLivero  omState("2");
                AppManager.finishAllActivity();
                //finish();
                break;

            case R.id.btn_switch_camera3:
                switchCamera();
                break;

            case R.id.push_chat_view_open:
                pushChatViewOpen.setVisibility(View.GONE);
                btnSwitchCamera3.setVisibility(View.GONE);
                chatroomView.setVisibility(View.VISIBLE);
                pushChatViewClose.setVisibility(View.VISIBLE);
                break;

            case R.id.push_chat_view_close:
                pushChatViewOpen.setVisibility(View.VISIBLE);
                btnSwitchCamera3.setVisibility(View.VISIBLE);
                chatroomView.setVisibility(View.GONE);
                pushChatViewClose.setVisibility(View.GONE);
                break;

            case R.id.more_msg_btn2:
                moreMsgBtn2.setVisibility(View.GONE);
                chatMsgList2.scrollToPosition(landscapeAdapter.getItemCount() - 1);

                break;
            case R.id.chat_btn2:
                chatBtn2.setVisibility(View.GONE);
                menuBtn2.setVisibility(View.GONE);
                chatInput2.setVisibility(View.VISIBLE);
                chatInput2.requestFocus();
                InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.showSoftInput(chatInput2, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.menu_btn2:
                showLandscapePushMenu();
                break;
            case R.id.viewerNumber:
                ViewerListDialog.getInstance(roomInfo).show(getSupportFragmentManager(), "ViewerListDialog");
                break;
            case R.id.invite:

                break;
            case R.id.exist:
                exit();
                break;
            case R.id.more:
                LiveMoreDialog.getInstance(roomInfo, openBeauty)
                        .setOnClickEventListener(new LiveMoreDialog.OnClickEventListener() {
                            @Override
                            public void onBeauty(boolean beautyOpen) {
                                openBeauty = beautyOpen;
                                btnBeauty.setSelected(openBeauty);
                                if (openBeauty) {
                                    mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.8f, 0.8f, 0.8f));
                                } else {
                                    mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.0f, 0.0f, 0.0f));
                                }
                            }

                            @Override
                            public void onShare() {
                                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                // 创建普通字符型ClipData
                                ClipData clipData = ClipData.newPlainText("Label", ServerInfo.liveH5 + "/index?stream_name=" + roomInfo.getStream_name());
                                // 将ClipData内容放到系统剪贴板里。
                                cm.setPrimaryClip(clipData);
                                ToastUtils.show("复制成功，可以发给朋友们了。");
                            }

                            @Override
                            public void onSendRedPacket() {
                                SendRedPacketDialog.getInstance(roomInfo).setOnRedPacketSendSuccessEventListener(new SendRedPacketDialog.OnRedPacketSendSuccessEventListener() {
                                    @Override
                                    public void sendRedPacketSuccess(int redPacketId) {
                                        //红包发送成功
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    getRedPacketRecord();
                                                    //redPacket.setVisibility(View.VISIBLE);
                                                } catch (Exception e) {

                                                }
                                            }
                                        });
                                    }
                                }).show(getSupportFragmentManager(), "SendRedPacketDialog");
                            }

                            @Override
                            public void onReversalCamera() {
                                mMediaStreamingManager.switchCamera();
                                if (mSubThreadHandler != null) {
                                    mSubThreadHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //如果换了摄像头，要重新开美颜

                                            if (openBeauty) {
                                                mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.8f, 0.8f, 0.8f));
                                            } else {
                                                mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.0f, 0.0f, 0.0f));
                                            }

                                        }
                                    }, 300);
                                }
                            }
                        }).show(getSupportFragmentManager(), "LiveMoreDialog");
                break;

            case R.id.hotRange:

                HotRangeListDialog.getInstance(roomInfo).show(getSupportFragmentManager(), "HotRangeListDialog");
                break;

            case R.id.inviteRange:
                InviteRangeListDialog.getInstance(roomInfo).show(getSupportFragmentManager(), "InviteRangeListDialog");

                break;

            case R.id.reversalCamera:
                mMediaStreamingManager.switchCamera();
                if (mSubThreadHandler != null) {
                    mSubThreadHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //如果换了摄像头，要重新开美颜

                            if (openBeauty) {
                                mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.8f, 0.8f, 0.8f));
                            } else {
                                mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.0f, 0.0f, 0.0f));
                            }

                        }
                    }, 300);
                }
                break;
            case R.id.beauty:

                openBeauty = !openBeauty;
                //btnBeauty.setSelected(openBeauty);
                if (openBeauty) {
                    beautyIcon.setTextColor(Color.parseColor("#3699FF"));
                    beautyText.setText("美颜关");
                    mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.8f, 0.8f, 0.8f));
                } else {
                    beautyIcon.setTextColor(Color.parseColor("#ffffff"));
                    beautyText.setText("美颜开");
                    mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.0f, 0.0f, 0.0f));
                }


                break;
            case R.id.qualitySetting:

                if (currentQuality == Quality_Normal) {//目前是标清
                    currentQuality = Quality_High;
                    qualityText.setText("高清");
                    bitrate = 5000;
                    push_resolution = 1;
                    int pushSizeLevel = StreamingProfile.VIDEO_ENCODING_HEIGHT_1088;


                    //mMediaStreamingManager.pause();
                    CameraStreamingSetting.PREVIEW_SIZE_LEVEL prevSizeLevel = CameraStreamingSetting.PREVIEW_SIZE_LEVEL.LARGE;
                    mProfile.setEncodingSizeLevel(pushSizeLevel)
                            .setVideoAdaptiveBitrateRange(bitrate - 200, bitrate);
                    // mCameraStreamingSetting.setCameraPrvSizeLevel(prevSizeLevel);
                    mMediaStreamingManager.setStreamingProfile(mProfile);
                    // mMediaStreamingManager.resume();

                    // mCameraStreamingSetting.setCameraPrvSizeLevel(prevSizeLevel);

                } else {//目前是高清
                    // mMediaStreamingManager.pause();
                    currentQuality = Quality_Normal;
                    qualityText.setText("标清");
                    bitrate = 1200;


                    push_resolution = 2;
                    int pushSizeLevel = StreamingProfile.VIDEO_ENCODING_HEIGHT_720;

                    //mMediaStreamingManager.pause();

                    CameraStreamingSetting.PREVIEW_SIZE_LEVEL prevSizeLevel = CameraStreamingSetting.PREVIEW_SIZE_LEVEL.SMALL;
                    mProfile.setEncodingSizeLevel(pushSizeLevel)
                            .setVideoAdaptiveBitrateRange(bitrate - 200, bitrate);
                    // mCameraStreamingSetting.setCameraPrvSizeLevel(prevSizeLevel);
                    mMediaStreamingManager.setStreamingProfile(mProfile);
                    // mMediaStreamingManager.resume();


                }
                break;
            case R.id.cover:

                RxPermissions rxPermissions = new RxPermissions(this);
                rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean granted) throws Exception {
                                if (granted) {
                                    String packageName = getPackageName();
                                    Matisse.from(LDLivePushActivity.this)
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
                                            //.imageEngine(new GlideEngine())
                                            .forResult(CHOOSE_PHOTO); // 设置作为标记的请求码
                                } else {
                                    ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                                }
                            }
                        });
                break;

            case R.id.goods://带货


                //HotRangeListDialog.getInstance(roomInfo).show(getSupportFragmentManager(), "HotRangeListDialog");

                LiveSelectedGoodsDialog.getInstance(roomInfo).show(getSupportFragmentManager(), "LiveSelectedGoodsDialog");

                break;

        }
    }

    private void showPushMenu() {

        if (pushPortraitMenuDialog == null) {
            pushPortraitMenuDialog = new PushPortraitMenuDialog(this, openBeauty);
            pushPortraitMenuDialog.setListener(new PushPortraitMenuDialog.MenuDialogListener() {
                @Override
                public void pushMenuEvent(PushPortraitMenuDialog.PushMenuEventType type) {

                    if (type == PushMenuBeautyEventType) {
                        openBeauty = !openBeauty;
                        btnBeauty.setSelected(openBeauty);
                        if (openBeauty) {
                            mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.8f, 0.8f, 0.8f));
                        } else {
                            mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.0f, 0.0f, 0.0f));
                        }

                    } else if (type == PushMenuDirectionEventType) {//横竖屏

                        isPortrait = !isPortrait;
                        if (isPortrait) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        } else {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
//                        onActivityRotation();

                        heightProvider.setHeightListener(null);

                        heightProvider = new HeightProvider(LDLivePushActivity.this).init().setHeightListener(new HeightProvider.HeightListener() {
                            @Override
                            public void onHeightChanged(int height) {

                                if (autoRotate == height) return;

                                if (isPortrait) {//竖屏
//                                    if (height > 200) {
//                                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomToolView.getLayoutParams();
//                                        layoutParams.setMargins(0, 0, 0, height);
//                                        bottomToolView.setLayoutParams(layoutParams);
//                                    } else {
//                                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomToolView.getLayoutParams();
//                                        layoutParams.setMargins(0, 0, 0, 0);
//                                        bottomToolView.setLayoutParams(layoutParams);
//
//                                        chatBtn.setVisibility(View.VISIBLE);
//                                        menuBtn.setVisibility(View.VISIBLE);
//                                        chatInput.setVisibility(View.GONE);
//                                    }
                                    if (height > 200) {
                                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
                                        layoutParams.setMargins(0, 0, 0, height);
                                        bottomLayout.setLayoutParams(layoutParams);
                                    } else {
                                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
                                        layoutParams.setMargins(0, 0, 0, 0);
                                        bottomLayout.setLayoutParams(layoutParams);

                                        bottomBar.setVisibility(View.VISIBLE);
                                        chatInput.setVisibility(View.GONE);
                                    }

                                } else {//横屏
                                    if (height > 200) {
                                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomToolView2.getLayoutParams();
                                        layoutParams.setMargins(0, 0, 0, height);
                                        bottomToolView2.setLayoutParams(layoutParams);
                                    } else {
                                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomToolView2.getLayoutParams();
                                        layoutParams.setMargins(0, 0, 0, 0);
                                        bottomToolView2.setLayoutParams(layoutParams);

                                        chatBtn2.setVisibility(View.VISIBLE);
                                        menuBtn2.setVisibility(View.VISIBLE);
                                        chatInput2.setVisibility(View.GONE);
                                    }
                                }

                            }
                        });


                    } else if (type == PushMenuManageEventType) {//管理
                        showPushManage();
                    } else if (type == PushMenuShareEventType) {//分享
                        //复制播放地址到黏贴板
                        //获取剪贴板管理器：
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型ClipData
                        ClipData clipData = ClipData.newPlainText("Label", ServerInfo.liveH5 + "/index?stream_name=" + roomInfo.getStream_name());
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(clipData);
                        ToastUtils.show("复制成功，可以发给朋友们了。");

                    }
                }
            });
        }
        pushPortraitMenuDialog.show();
        pushPortraitMenuDialog.setBeautyStatus(openBeauty);
    }

    private void showLandscapePushMenu() {
        if (pushLandscapeMenuDialog == null) {
            pushLandscapeMenuDialog = new PushLandscapeMenuDialog(this, openBeauty);
            pushLandscapeMenuDialog.setListener(new PushLandscapeMenuDialog.MenuDialogListener() {
                @Override
                public void pushMenuEvent(PushMenuEventType type) {
                    if (type == PushMenuEventType.PushMenuBeautyEventType) {
                        openBeauty = !openBeauty;
                        btnBeauty.setSelected(openBeauty);
                        if (openBeauty) {
                            mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.8f, 0.8f, 0.8f));
                        } else {
                            mMediaStreamingManager.updateFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.0f, 0.0f, 0.0f));
                        }
                    } else if (type == PushMenuEventType.PushMenuDirectionEventType) {

                        isPortrait = !isPortrait;
                        if (isPortrait) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        } else {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
//                        onActivityRotation();

                        heightProvider.setHeightListener(null);

                        heightProvider = new HeightProvider(LDLivePushActivity.this).init().setHeightListener(new HeightProvider.HeightListener() {
                            @Override
                            public void onHeightChanged(int height) {

                                if (autoRotate == height) return;

                                if (isPortrait) {//竖屏
                                    if (height > 200) {
                                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomToolView.getLayoutParams();
                                        layoutParams.setMargins(0, 0, 0, height);
                                        bottomToolView.setLayoutParams(layoutParams);
                                    } else {
                                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomToolView.getLayoutParams();
                                        layoutParams.setMargins(0, 0, 0, 0);
                                        bottomToolView.setLayoutParams(layoutParams);

                                        chatBtn.setVisibility(View.VISIBLE);
                                        menuBtn.setVisibility(View.VISIBLE);
                                        chatInput.setVisibility(View.GONE);
                                    }

                                } else {//横屏
                                    if (height > 200) {
                                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomToolView2.getLayoutParams();
                                        layoutParams.setMargins(0, 0, 0, height);
                                        bottomToolView2.setLayoutParams(layoutParams);
                                    } else {
                                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomToolView2.getLayoutParams();
                                        layoutParams.setMargins(0, 0, 0, 0);
                                        bottomToolView2.setLayoutParams(layoutParams);

                                        chatBtn2.setVisibility(View.VISIBLE);
                                        menuBtn2.setVisibility(View.VISIBLE);
                                        chatInput2.setVisibility(View.GONE);
                                    }
                                }

                            }
                        });

                    } else if (type == PushMenuEventType.PushMenuManageEventType) {
                        showLandscapeManage();
                    } else if (type == PushMenuEventType.PushMenuShareEventType) {//分享
                        //复制播放地址到黏贴板
                        //获取剪贴板管理器：
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型ClipData
                        ClipData clipData = ClipData.newPlainText("Label", ServerInfo.liveH5 + "/index?stream_name=" + roomInfo.getStream_name());
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(clipData);
                        ToastUtils.show("复制成功，可以发给朋友们了。");
                    }
                }
            });
        }

        pushLandscapeMenuDialog.setBeautyStatus(openBeauty);
        pushLandscapeMenuDialog.show();
    }

    private void showLandscapeManage() {
        if (pushLandscapeManageDialog == null) {
            pushLandscapeManageDialog = new PushLandscapeManageDialog(this);
            pushLandscapeManageDialog.setListener(new PushLandscapeManageDialog.ManageDialogListener() {
                @Override
                public void pushManageEvent(PushManageEventType type) {
                    if (type == PushManageEventType.PushManageOverEventType) {
                        pushLiveroomState("2");
                        finish();
                    }
                }
            });
        }

        pushLandscapeManageDialog.show();
    }

    private void showPushManage() {
        if (pushPortraitManageDialog == null) {
            pushPortraitManageDialog = new PushPortraitManageDialog(this);
            pushPortraitManageDialog.setListener(new PushPortraitManageDialog.ManageDialogListener() {
                @Override
                public void pushManageEvent(PushPortraitManageDialog.PushManageEventType type) {

                    if (type == PushManageOverEventType) {
                        pushLiveroomState("2");
                        finish();
                    }
                }
            });
        }

        pushPortraitManageDialog.show();
    }

    // 1=开始直播  2=完成直播
    private void pushLiveroomState(String type) {
        HashMap<String, String> stateMap = new HashMap<>();
        stateMap.put("id", room_id);
        stateMap.put("state", type);

        OkHttpUtils.patch(stateUrl, stateMap, new OkHttpCallback(this) {
            @Override
            public void onFailure(Call call, Exception e) {
                //Log.e("test",e.getCause().getMessage());
            }

            @Override
            public void onResponse(Call call, String response) throws IOException {
                Log.e("test", response);
            }
        });
    }

    private AudioManager am;

    public void setBluetooth() {
        if (am == null)
            am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.startBluetoothSco();
        am.setBluetoothScoOn(true);
    }

    /**
     * 点击预览窗口时触发，用于手动对焦
     *
     * @param e 手势事件
     * @return 是否拦截并处理事件
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i(TAG, "onSingleTapUp X:" + e.getX() + ",Y:" + e.getY());
        if (mIsReady) {
            setFocusAreaIndicator();
            mMediaStreamingManager.doSingleTapUp((int) e.getX(), (int) e.getY());
            return true;
        }
        return false;
    }

    /**
     * 手势缩放时触发，用于进行画面的缩放
     *
     * @param factor 缩放因子
     * @return
     */
    @Override
    public boolean onZoomValueChanged(float factor) {
        if (mIsReady && mMediaStreamingManager.isZoomSupported()) {
            mCurrentZoom = (int) (mMaxZoom * factor);
            mCurrentZoom = Math.min(mCurrentZoom, mMaxZoom);
            mCurrentZoom = Math.max(0, mCurrentZoom);
            Log.d(TAG, "zoom ongoing, scale: " + mCurrentZoom + ",factor:" + factor + ",maxZoom:" + mMaxZoom);
            // 设置缩放值
            mMediaStreamingManager.setZoomValue(mCurrentZoom);
        }
        return false;
    }

    private static class TXPhoneStateListener extends PhoneStateListener {
        WeakReference<TXLivePusher> mPusher;

        public TXPhoneStateListener(TXLivePusher pusher) {
            mPusher = new WeakReference<TXLivePusher>(pusher);
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            TXLivePusher pusher = mPusher.get();
            switch (state) {
                //电话等待接听
                case TelephonyManager.CALL_STATE_RINGING:
                    if (pusher != null) pusher.pausePusher();
                    break;
                //电话接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (pusher != null) pusher.pausePusher();
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    if (pusher != null) pusher.resumePusher();
                    break;
            }
        }
    }

    /**
     * 观察屏幕旋转设置变化
     */
//    private class ActivityRotationObserver extends ContentObserver {
//        ContentResolver mResolver;
//
//        public ActivityRotationObserver(Handler handler) {
//            super(handler);
//            mResolver = LDLivePushActivity.this.getContentResolver();
//        }
//
//        //屏幕旋转设置改变时调用
//        @Override
//        public void onChange(boolean selfChange) {
//            super.onChange(selfChange);
//        }
//
//        public void startObserver() {
//            mResolver.registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false, this);
//        }
//
//        public void stopObserver() {
//            mResolver.unregisterContentObserver(this);
//        }
//    }

    //add by zxj   蓝牙适配
    //获取蓝牙耳机的连接状态
    private BluetoothAdapter ba;

    public int getheadsetStatsu() {
        ba = BluetoothAdapter.getDefaultAdapter();

//      int isBlueCon;//蓝牙适配器是否存在，即是否发生了错误
        if (ba == null) {
//         isBlueCon = -1;     //error
            return -1;
        } else if (ba.isEnabled()) {
            int a2dp = ba.getProfileConnectionState(BluetoothProfile.A2DP);              //可操控蓝牙设备，如带播放暂停功能的蓝牙耳机
            int headset = ba.getProfileConnectionState(BluetoothProfile.HEADSET);        //蓝牙头戴式耳机，支持语音输入输出
            int health = ba.getProfileConnectionState(BluetoothProfile.HEALTH);          //蓝牙穿戴式设备

            //查看是否蓝牙是否连接到三种设备的一种，以此来判断是否处于连接状态还是打开并没有连接的状态
            int flag = -1;
            if (a2dp == BluetoothProfile.STATE_CONNECTED) {
                flag = a2dp;
            } else if (headset == BluetoothProfile.STATE_CONNECTED) {
                flag = headset;
            } else if (health == BluetoothProfile.STATE_CONNECTED) {
                flag = health;
            }
            //说明连接上了三种设备的一种
            if (flag != -1) {
//            isBlueCon = 1;            //connected
                return 2;
            }
        }
        return -2;
    }


    static class MyChatListAdapter extends RecyclerView.Adapter {

        public static final int TYPE_HEADER = 0;
        public static final int TYPE_TEXT = 1;
        public static final int TYPE_PICTURE = 2;

        private LayoutInflater inflater;
        ArrayList<ChatMsgModel> msgList = new ArrayList<>();

        int directionType = 0; //0竖屏 1横屏

        public void showChatMsg(ChatMsgModel msg) {
            msgList.add(msg);
            //notifyDataSetChanged();
            notifyItemInserted(msgList.size());
        }

        public void addChatMsg(ChatMsgModel msg) {

            msgList.add(msg);

        }

        public MyChatListAdapter(Context context, int directionType) {
            this.directionType = directionType;
            inflater = LayoutInflater.from(context);
//            String richMsg = "<span style=\"color: #FFBF25; \">" + "欢迎来到直播间！菱动直播倡导绿色健康直播。直播内容和评论严禁发布政治、低俗、色情等内容。" + "</span>";
//            msgList.add(richMsg);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

            if (viewType == TYPE_HEADER) {
                HeaderViewHolder hvh = new HeaderViewHolder(inflater.inflate(R.layout.chat_msg_header, viewGroup, false));
                if (directionType == 1) {
                    hvh.content.setBackground(null);
                }
                return hvh;


            } else if (viewType == TYPE_TEXT) {
                TextViewHolder tvh = new TextViewHolder(inflater.inflate(R.layout.chat_msg_text, viewGroup, false));
                if (directionType == 1) {
                    tvh.layout.setBackground(null);
                }
                return tvh;
            } else {
                PictureViewHolder pvh = new PictureViewHolder(inflater.inflate(R.layout.chat_msg_picture, viewGroup, false));
                if (directionType == 1) {
                    pvh.layout.setBackground(null);
                }
                return pvh;
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

            int viewType = getItemViewType(position);
            if (viewType == TYPE_HEADER) {

            } else if (viewType == TYPE_TEXT) {
                TextViewHolder vh = (TextViewHolder) viewHolder;
                ChatMsgModel msg = msgList.get(position - 1);

                if (!TextUtils.isEmpty(msg.getUserLog())) {
                    Uri uri = Uri.parse(msg.getUserLog());
                    int width = CommonUtils.dip2px(25);
                    int height = width;
                    ImageLoader.showThumb(uri, vh.head, width, height);
                } else {
                    ImageLoader.showThumb(vh.head, R.drawable.ic_head01);
                }

                if (msg.getType() == 1) {
                    //主持人
                    vh.emcee.setVisibility(View.VISIBLE);
                } else {
                    vh.emcee.setVisibility(View.GONE);
                }
                vh.name.setText(msg.getNickName());
                vh.content.setText(msg.getMsg());
            } else if (getItemViewType(position) == TYPE_PICTURE) {
                PictureViewHolder vh = (PictureViewHolder) viewHolder;
                ChatMsgModel msg = msgList.get(position - 1);

                if (!TextUtils.isEmpty(msg.getUserLog())) {
                    Uri uri = Uri.parse(msg.getUserLog());
                    int width = CommonUtils.dip2px(25);
                    int height = width;
                    ImageLoader.showThumb(uri, vh.head, width, height);
                } else {
                    ImageLoader.showThumb(vh.head, R.drawable.ic_head01);
                }

                if (msg.getType() == 1) {
                    //主持人
                    vh.emcee.setVisibility(View.VISIBLE);
                } else {
                    vh.emcee.setVisibility(View.GONE);
                }
                vh.name.setText(msg.getNickName());


                if (!TextUtils.isEmpty(msg.getMsg())) {

                    ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(
                                String id,
                                @Nullable ImageInfo imageInfo,
                                @Nullable Animatable anim) {
                            if (imageInfo == null) {
                                return;
                            }
                            QualityInfo qualityInfo = imageInfo.getQualityInfo();
                            FLog.d("Final image received! " +
                                            "Size %d x %d",
                                    "Quality level %d, good enough: %s, full quality: %s",
                                    imageInfo.getWidth(),
                                    imageInfo.getHeight(),
                                    qualityInfo.getQuality(),
                                    qualityInfo.isOfGoodEnoughQuality(),
                                    qualityInfo.isOfFullQuality());
                            float ratio = (float) imageInfo.getWidth() / (float) imageInfo.getHeight();
                            vh.picture.setAspectRatio(ratio);

                        }

                        @Override
                        public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {

                        }

                        @Override
                        public void onFailure(String id, Throwable throwable) {

                        }
                    };

                    Uri uri = Uri.parse(msg.getMsg());
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setControllerListener(controllerListener)
                            .setUri(uri)
                            // other setters
                            .build();
                    vh.picture.setController(controller);

                }

            }

        }

        @Override
        public int getItemCount() {
            if (msgList == null || msgList.size() == 0) {
                return 1;
            } else {
                return msgList.size() + 1;
            }

//            return 10;
        }


        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_HEADER;
            } else if (msgList.get(position - 1).getContentType() == 1) {
                return TYPE_TEXT;
            } else {
                return TYPE_PICTURE;
            }
        }


        static class HeaderViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.content)
            TextView content;

            HeaderViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }

        static class TextViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.head)
            SimpleDraweeView head;
            @BindView(R.id.emcee)
            TextView emcee;
            @BindView(R.id.name)
            TextView name;
            @BindView(R.id.content)
            TextView content;
            @BindView(R.id.layout)
            LinearLayout layout;

            TextViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }


        static class PictureViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.head)
            SimpleDraweeView head;
            @BindView(R.id.emcee)
            TextView emcee;
            @BindView(R.id.name)
            TextView name;
            @BindView(R.id.picture)
            SimpleDraweeView picture;
            @BindView(R.id.layout)
            LinearLayout layout;

            PictureViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mMediaStreamingManager != null) {
            mMediaStreamingManager.pause();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        if (mIsPushing) {
            switchFlow(true);
        }
        if (mMediaStreamingManager != null) {
            mMediaStreamingManager.resume();
        }

    }

    @Override
    public void onStop() {

        switchFlow(false);
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LPAnimationManager.release();
        stopStreamingInternal();
        if (echo != null) {
            echo.disconnect();
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        mVideoPublish = false;
        if (mSubThreadHandler != null) {
            mSubThreadHandler.getLooper().quit();
        }
        mMediaStreamingManager.destroy();

    }


    // 1=开始直播  2=完成直播
    private void pushLiveroomState(LiveRoomInfo room, String type) {
        String stateUrl = ServerInfo.live + "/v1/set_state";
        HashMap<String, String> stateMap = new HashMap<>();
        stateMap.put("id", "" + room.getId());
        stateMap.put("state", type);

        OkHttpUtils.patch(stateUrl, stateMap, new OkHttpCallback(this) {
            @Override
            public void onFailure(Call call, Exception e) {
                //Log.e("test",e.getCause().getMessage());
            }

            @Override
            public void onResponse(Call call, String response) throws IOException {
                try {

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {


                                pushSetting.setVisibility(View.GONE);
                                setting.setVisibility(View.GONE);
                                countdownText.setVisibility(View.VISIBLE);
                                startPush.setVisibility(View.GONE);
                                timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        secondCounts--;
                                        if (secondCounts == 0) {
                                            timer.cancel();
                                            timer = null;
                                            mMainHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    countdownText.setVisibility(View.GONE);
                                                    startPushRtmp();
                                                }
                                            });
                                        } else {

                                            mMainHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    countdownText.setText(secondCounts + "");
                                                }
                                            });
                                        }

                                    }
                                }, 1000, 1000);

                            }
                        });
                    } else if (jsonObject != null) {
                        ToastUtils.show(jsonObject.getString("msg"));
                    }

                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }

                } catch (Exception e) {

                }


            }
        });
    }


    // 1=开始直播  2=完成直播
    private void getAuthKey(LiveRoomInfo room) {
        String stateUrl = ServerInfo.live + "/v1/get_auth_key";
        HashMap<String, String> stateMap = new HashMap<>();
        stateMap.put("room_id", "" + room.getId());
        stateMap.put("type", "live");

        OkHttpUtils.get(stateUrl, stateMap, new OkHttpCallback(this) {
            @Override
            public void onFailure(Call call, Exception e) {
                //Log.e("test",e.getCause().getMessage());
            }

            @Override
            public void onResponse(Call call, String response) throws IOException {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        if (!TextUtils.isEmpty(jsonObject.getString("data"))) {
                            push_url += ("?" + jsonObject.getString("data"));
                        }
                    } else if (jsonObject != null) {
                        ToastUtils.show(jsonObject.getString("msg"));
                    }
                } catch (Exception e) {

                }


            }
        });
    }


    private void createLiveRoom() {

        createdLiveRoomInfo.setName(liveTitle.getText().toString());

        if (TextUtils.isEmpty(createdLiveRoomInfo.getCover_url())) {
            ToastUtils.show("请选择封面");
            return;
        }


        if (TextUtils.isEmpty(createdLiveRoomInfo.getName())) {
            ToastUtils.show("请输入标题");
            return;
        }


        String url = ServerInfo.live + ServerInfo.createRoom;
        Map<String, String> params = new HashMap<>();

        params.put("merchant_id", createdLiveRoomInfo.getMerchant_id());
        params.put("name", createdLiveRoomInfo.getName());
        params.put("anchor_id", createdLiveRoomInfo.getAnchor_id());
        params.put("cover_url", createdLiveRoomInfo.getCover_url());
        params.put("cover_url_vertical", createdLiveRoomInfo.getCover_url());
        params.put("type", "1");
        params.put("is_record", "1");
        params.put("state", "1");
        params.put("audit", "0");
        params.put("is_rtslive", "2");
        params.put("ai_audit", "0");
        params.put("estimate_play_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        params.put("show_model", "2");//竖屏


        mLoadingDialog = CommonUtils.showLoadingDialog(getSupportFragmentManager());

        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {
//                Message msg = mHandler.obtainMessage(MSG_CREATE_ROOM);
//                msg.obj = response;
//                mHandler.sendMessage(msg);

                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        roomInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), LiveRoomInfo.class);
                        roomInfo.setFlv_play_url(jsonObject.getJSONObject("data").getString("FlvPlayUrl"));
                        roomInfo.setRtmp_play_url(jsonObject.getJSONObject("data").getString("RtmpPlayUrl"));
                        roomInfo.setHls_play_url(jsonObject.getJSONObject("data").getString("HLSPlayUrl"));

                        push_url = roomInfo.getPush_url();
                        room_id = roomInfo.getId() + "";
                        stream_name = roomInfo.getStream_name();
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                initLive();
                                pushLiveroomState(roomInfo, "1");
                            }
                        });

                    } else if (jsonObject != null) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }
                        ToastUtils.show(jsonObject.getString("msg"));
                    } else {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }
                        ToastUtils.show("创建直播间失败");
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call call, Exception e) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        ToastUtils.show("创建直播间异常，请稍后再试");
                    }
                });
            }

        });


    }


    @Override
    public void onBackPressed() {

        if (roomInfo != null) {
            if (mIsPushing) {
                exit();
            } else {
                AppManager.finishAllActivity();
            }
        } else {
            super.onBackPressed();
        }

    }


    private void startStreamingInternal() {
        startStreamingInternal(0);
    }

    private void startStreamingInternal(long delayMillis) {
        if (mMediaStreamingManager == null) {
            return;
        }
        // startStreaming 为耗时操作，建议放到子线程执行
        if (mSubThreadHandler != null) {
            mSubThreadHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final boolean res = mMediaStreamingManager.startStreaming();
                    switchFlow(true);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mControlFragment.setShutterButtonPressed(res);
                        }
                    });
                }
            }, delayMillis);
        }
    }

    private void stopStreamingInternal() {

        mIsPushing = false;
        if (mMediaStreamingManager == null) {
            return;
        }
        final boolean res = mMediaStreamingManager.stopStreaming();
    }

    private boolean isPictureStreaming() {
        if (mIsPictureStreaming) {
            Toast.makeText(LDLivePushActivity.this, "is picture streaming, operation failed!", Toast.LENGTH_SHORT).show();
        }
        return mIsPictureStreaming;
    }

    private boolean isFrontFacing() {
        return mCurrentCamFacingIndex == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT.ordinal();
    }

    private void saveToSDCard(String filename, Bitmap bmp) throws IOException {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(file));
                bmp.compress(Bitmap.CompressFormat.PNG, 90, bos);
                bmp.recycle();
                bmp = null;
            } finally {
                if (bos != null) {
                    bos.close();
                }
            }

            final String info = "Save frame to:" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LDLivePushActivity.this, info, Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    protected void setFocusAreaIndicator() {
        if (mRotateLayout == null) {
            mRotateLayout = (RotateLayout) findViewById(R.id.focus_indicator_rotate_layout);
            mMediaStreamingManager.setFocusAreaIndicator(mRotateLayout,
                    mRotateLayout.findViewById(R.id.focus_indicator));
        }
    }


    @Override
    public void onStateChanged(StreamingState streamingState, Object extra) {

        Log.e(TAG, "streamingState = " + streamingState + "extra = " + extra);
        switch (streamingState) {
            case PREPARING:
                Log.e(TAG, "PREPARING");
                break;
            case READY:
                Log.e(TAG, "READY");
                // start streaming when READY
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mMediaStreamingManager != null) {
                            mMediaStreamingManager.startStreaming();
                        }
                    }
                }).start();
                break;
            case CONNECTING:
                Log.e(TAG, "连接中");
                break;
            case STREAMING:
                Log.e(TAG, "推流中");
                // The av packet had been sent.
                break;
            case SHUTDOWN:
                Log.e(TAG, "直播中断");
                // The streaming had been finished.
                break;
            case IOERROR:
                // Network connect error.
                Log.e(TAG, "网络连接失败");
                break;
            case OPEN_CAMERA_FAIL:
                Log.e(TAG, "摄像头打开失败");
                // Failed to open camera.
                break;
            case DISCONNECTED:
                Log.e(TAG, "已经断开连接");
                // The socket is broken while streaming
                break;
            case TORCH_INFO:
                Log.e(TAG, "开启闪光灯");
                break;

        }
    }


    @Override
    public void notifyStreamStatusChanged(StreamingProfile.StreamStatus status) {
        Log.e(TAG, "StreamStatus = " + status);
    }

    @Override
    public void onAudioSourceAvailable(ByteBuffer srcBuffer, int size, long tsInNanoTime, boolean isEof) {

    }

    @Override
    public boolean onRecordAudioFailedHandled(int code) {
        Log.i(TAG, "onRecordAudioFailedHandled");
        return false;
    }

    @Override
    public boolean onRestartStreamingHandled(int code) {
        Log.i(TAG, "onRestartStreamingHandled");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mMediaStreamingManager != null) {
                    mMediaStreamingManager.startStreaming();
                }
            }
        }).start();
        return false;
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//    }

    @Override
    public Camera.Size onPreviewSizeSelected(List<Camera.Size> list) {
        return null;
    }

    @Override
    public int onPreviewFpsSelected(List<int[]> list) {
        return -1;
    }


    private void switchCamera() {

        mCurrentCamFacingIndex = (mCurrentCamFacingIndex + 1) % CameraStreamingSetting.getNumberOfCameras();
        CameraStreamingSetting.CAMERA_FACING_ID facingId;
        if (mCurrentCamFacingIndex == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK.ordinal()) {
            facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK;
        } else if (mCurrentCamFacingIndex == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT.ordinal()) {
            facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT;
        } else {
            facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD;
        }
        Log.i(TAG, "switchCamera:" + facingId);
    }


    public void getRedPacketRecord() {
        String url = ServerInfo.live + "/v1/get_red_bag_record_c";
        Map<String, String> params = new HashMap<>();

        params.put("room_id", "" + roomInfo.getId());
        params.put("page", "1");
        params.put("rows", "" + Integer.MAX_VALUE);
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        List<RedPacketInfo> redPacketInfos = JSON.parseArray(jsonObject.getJSONObject("data").getJSONArray("my_clip").toJSONString(), RedPacketInfo.class);


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (redPacketInfos == null || redPacketInfos.size() == 0) {
                                    redPacket.setVisibility(View.GONE);
                                } else {
                                    redPacket.setVisibility(View.VISIBLE);
                                    RedPacketInfo redPacketInfo = redPacketInfos.get(redPacketInfos.size() - 1);
                                    if (redPacketInfo.getState() == 0) {
                                        //定时红包，即将开始
                                        redPacketStatus.setText("即将开始");
                                    } else {
                                        if (redPacketInfo.getState() == 1 && redPacketInfo.getState_end() == 0) {
                                            //正在进行
                                            redPacketStatus.setText("进行中");
//                                            TranslateAnimation animation = new TranslateAnimation(0, -5, 0, 0);
//                                            animation.setInterpolator(new OvershootInterpolator());
//                                            animation.setDuration(100);
//                                            animation.setRepeatCount(INFINITE);
//                                            animation.setRepeatMode(Animation.REVERSE);
//                                            shakeRedPacket.startAnimation(animation);

                                            Animation anim = new RotateAnimation(-15f, 15f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                            anim.setDuration(600); // 设置动画时间
                                            anim.setRepeatCount(INFINITE);
                                            anim.setRepeatMode(Animation.REVERSE);
                                            anim.setInterpolator(new DecelerateInterpolator()); // 设置插入器
                                            shakeRedPacket.startAnimation(anim);

                                            redPacket.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent it = new Intent(getContext(), RedPacketDetailActivity.class);
                                                    it.putExtra("id", "" + redPacketInfo.getId());
                                                    startActivity(it);

                                                }
                                            });


                                        } else if (redPacketInfo.getState() == 1 && redPacketInfo.getState_end() == 1) {
                                            //已结束
                                            redPacketStatus.setText("已结束");
                                            shakeRedPacket.clearAnimation();

                                            redPacket.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    Intent it = new Intent(getContext(), RedPacketDetailActivity.class);
                                                    it.putExtra("id", "" + redPacketInfo.getId());
                                                    startActivity(it);


                                                }
                                            });
                                        }
                                    }

                                }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK && data != null) {

                List<String> paths = Matisse.obtainPathResult(data);
                //List<Uri> selects = Matisse.obtainResult(data);
                //File file = new File(CommonUtils.getRealFilePath(this, selects.get(0)));
                // File file = new File(paths.get(0));

                // cover.setImageURI(Uri.fromFile(file));
                coverTip.setText("更换封面");

                mLoadingDialog = CommonUtils.showLoadingDialog(getSupportFragmentManager());

                OSSUploadUtils.getInstance().setOnCallbackListener(new OSSUploadUtils.OnCallbackListener() {
                    @Override
                    public void onSuccess(String url) {

                        LDLivePushActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mLoadingDialog != null) {
                                    mLoadingDialog.dismiss();
                                }
                                Log.d("LDLivePushActivity", url);
                                Uri uri = Uri.parse(url);
                                ImageLoader.showThumb(uri, cover, CommonUtils.dip2px(90), CommonUtils.dip2px(60));
                                createdLiveRoomInfo.setCover_url(url);
                            }
                        });
                    }

                    @Override
                    public void onSuccessAll() {

                    }

                    @Override
                    public void onFailed() {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }
                        ToastUtils.show("上传失败");
                    }
                }).uploadFile(this, paths.get(0));


            } else {
                ToastUtils.show("用户取消拍照");
            }

        }
    }


    //退出直播
    private void exit() {
        new CircleDialog.Builder()
                .setCanceledOnTouchOutside(false)
                .setCancelable(false)

                .setText("确定结束直播吗")
                .setNegative("取消", null)
                .configNegative(new ConfigButton() {
                    @Override
                    public void onConfig(ButtonParams params) {
                        //按钮字体颜色
                        params.textColor = Color.parseColor("#cc001426");
                    }
                })
                .setPositive("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (roomInfo != null) {
                            AppManager.finishAllActivity();
                        } else {
                            finish();
                        }

                    }
                })
                .configPositive(new ConfigButton() {
                    @Override
                    public void onConfig(ButtonParams params) {
                        params.textColor = Color.parseColor("#3183FF");
                    }
                })
                .show(getSupportFragmentManager());
    }

}
