package com.shuangling.software.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.qiniu.droid.rtc.QNBeautySetting;
import com.qiniu.droid.rtc.QNCameraSwitchResultCallback;
import com.qiniu.droid.rtc.QNCustomMessage;
import com.qiniu.droid.rtc.QNErrorCode;
import com.qiniu.droid.rtc.QNRTCEngine;
import com.qiniu.droid.rtc.QNRTCEngineEventListener;
import com.qiniu.droid.rtc.QNRTCSetting;
import com.qiniu.droid.rtc.QNRoomState;
import com.qiniu.droid.rtc.QNSourceType;
import com.qiniu.droid.rtc.QNStatisticsReport;
import com.qiniu.droid.rtc.QNTrackInfo;
import com.qiniu.droid.rtc.QNTrackKind;
import com.qiniu.droid.rtc.QNVideoFormat;
import com.qiniu.droid.rtc.model.QNAudioDevice;
import com.qiniu.droid.rtc.model.QNMergeJob;
import com.shuangling.software.R;
import com.shuangling.software.activity.ui.UserTrackView;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.event.MessageEvent;
import com.shuangling.software.utils.Config;
import com.shuangling.software.utils.QNAppServer;
import com.viewpagerindicator.CirclePageIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.shuangling.software.utils.Config.DEFAULT_BITRATE;
import static com.shuangling.software.utils.Config.DEFAULT_FPS;
import static com.shuangling.software.utils.Config.DEFAULT_RESOLUTION;

public class RoomActivity extends Activity implements QNRTCEngineEventListener {


    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.switchCamera)
    FontIconView switchCamera;
    @BindView(R.id.interactChat)
    LinearLayout interactChat;
    @BindView(R.id.openMute)
    LinearLayout openMute;
    @BindView(R.id.closeVideo)
    LinearLayout closeVideo;
    @BindView(R.id.barrage)
    LinearLayout barrage;
    @BindView(R.id.bottomButtonLayout)
    TableLayout bottomButtonLayout;
    @BindView(R.id.panel)
    RelativeLayout panel;
    @BindView(R.id.timer)
    Chronometer timer;
    @BindView(R.id.quit)
    TextView quit;
    @BindView(R.id.indicator)
    CirclePageIndicator indicator;
//    @BindView(R.id.userTrackView)
//    UserTrackView userTrackView;


    private static final String TAG = "RoomActivity";

    private static final int BITRATE_FOR_SCREEN_VIDEO = (int) (1.5 * 1000 * 1000);

    public static final String EXTRA_USER_ID = "USER_ID";
    public static final String EXTRA_ROOM_TOKEN = "ROOM_TOKEN";
    public static final String EXTRA_ROOM_ID = "ROOM_ID";

    private static final String[] MANDATORY_PERMISSIONS = {
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO",
            "android.permission.INTERNET"
    };
    @BindView(R.id.muteStatus)
    FontIconView muteStatus;
    @BindView(R.id.videoStatus)
    FontIconView videoStatus;


    private Handler mHandler;

    private Toast mLogToast;
    private List<String> mHWBlackList = new ArrayList<>();

    private AlertDialog mKickOutDialog;

    private QNRTCEngine mEngine;
    private String mRoomToken;
    private String mUserId;
    private String mRoomId;
    private boolean mMicEnabled = true;
    private boolean mBeautyEnabled = false;
    private boolean mVideoEnabled = true;
    private boolean mSpeakerEnabled = true;
    private boolean mShowPannel = true;


    private boolean mIsError = false;
    private boolean mIsAdmin = false;
    private boolean mIsJoinedRoom = false;
    private List<QNTrackInfo> mLocalTrackList;

    private QNTrackInfo mLocalVideoTrack;
    private QNTrackInfo mLocalAudioTrack;
    private QNTrackInfo mLocalScreenTrack;

    private int mScreenWidth = 0;
    private int mScreenHeight = 0;
    private int mCaptureMode = Config.CAMERA_CAPTURE;

    //private TrackWindowMgr mTrackWindowMgr;

    private PagerAdapter mPagerAdapter;

    /**
     * 合流相关
     * <p>
     * 注意：一个房间仅需要一个用户可以配置合流布局即可，该用户可以基于 SDK 提供的远端用户相关回调对远端用户的动态进行监听，
     * 进而进行合流布局的实时更改。
     * <p>
     * demo 中默认 userId 为 "admin" 的用户可以控制合流布局的配置
     */
    //private MergeLayoutConfigView mMergeLayoutConfigView;
    private PopupWindow mPopWindow;
    //    private UserListAdapter mUserListAdapter;
//    private RoomUserList mRoomUserList;
//    private RTCUser mChooseUser;
    private volatile boolean mIsStreaming;
    /**
     * 如果 QNMergeJob 为 null，则表示使用默认合流任务
     * <p>
     * 默认合流任务的宽高、码率等参数可以通过登录后台 https://portal.qiniu.com/rtn 并对连麦应用进行编辑来配置
     * 自定义合流任务可以通过自定义 {@link QNMergeJob} 并调用 QNRTCEngine.createMergeJob 接口来创建
     * 注意：创建自定义合流任务需要在加入房间之后才可执行
     */
    private QNMergeJob mCurrentMergeJob;

    //private LinkedHashMap<String, UserTrackView> mUserWindowMap = new LinkedHashMap<>();

    //private ArrayList<UserTrackView> mUserWindowList = new ArrayList<>();

    private ArrayList<UserTrackInfo> mUserTrackInfos = new ArrayList<>();

    private int mCurrentPage=0;

    //支持人是否禁言
    private boolean canSpeak=true;
    //支持人是否禁止视频
    private boolean canVideo=true;

    private String mScreenUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());
        EventBus.getDefault().register(this);
        mHandler = new Handler();

        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;

        setContentView(R.layout.activity_room);
        ButterKnife.bind(this);


        Intent intent = getIntent();
        mRoomToken = intent.getStringExtra(EXTRA_ROOM_TOKEN);
        mUserId = intent.getStringExtra(EXTRA_USER_ID);
        mRoomId = intent.getStringExtra(EXTRA_ROOM_ID);
        //mIsAdmin = mUserId.equals(QNAppServer.ADMIN_USER);

        mPagerAdapter = new PagerAdapter() {


            @Override
            public int getItemPosition(Object object) {

                if((int)((View)object).getTag()==mCurrentPage){
                    return POSITION_NONE;
                }else {
                   return POSITION_UNCHANGED;
                }


            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                //container.removeView(container.findViewWithTag(position));
                container.removeView((View) object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                if (position == 0) {
                    LayoutInflater inflater = LayoutInflater.from(RoomActivity.this);
                    final View v = inflater.inflate(R.layout.primary_user_video_item, container, false);
                    UserTrackView userTrackView = v.findViewById(R.id.userTrackView);
                    UserTrackInfo userTrackInfo = mUserTrackInfos.get(0);
                    List<QNTrackInfo> trackInfos = new ArrayList<>(userTrackInfo.getQNVideoTrackInfos());
                    if (userTrackInfo.getQNAudioTrackInfo() != null) {
                        trackInfos.add(userTrackInfo.getQNAudioTrackInfo());
                    }
                    userTrackView.setUserTrackInfo(mEngine, userTrackInfo.getUserId(), trackInfos);
                    userTrackView.changeViewBackgroundByPos(0);
                    mUserTrackInfos.get(0).setUserTrackView(userTrackView);
                    container.addView(v);
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            mShowPannel = !mShowPannel;
                            if (!mShowPannel) {
                                panel.setVisibility(View.GONE);
                            } else {
                                panel.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    v.setTag(position);
                    return v;
                } else {
                    LayoutInflater inflater = LayoutInflater.from(RoomActivity.this);
                    final View v = inflater.inflate(R.layout.normal_user_video_item, container, false);
                    int num=0;
                    for (int i = (position - 1) * 4 + 1; i < mUserTrackInfos.size() && num < 4; i++, num++) {
                        UserTrackView userTrackView;
                        if (num == 0) {
                            userTrackView = v.findViewById(R.id.userTrackView1);
                        } else if (num == 1) {
                            userTrackView = v.findViewById(R.id.userTrackView2);
                        } else if (num == 2) {
                            userTrackView = v.findViewById(R.id.userTrackView3);
                        } else {
                            userTrackView = v.findViewById(R.id.userTrackView4);
                        }

                        UserTrackInfo userTrackInfo = mUserTrackInfos.get(i);
                        //ViewGroup.LayoutParams lp = userTrackView.getLayoutParams();
                        List<QNTrackInfo> trackInfos = new ArrayList<>(userTrackInfo.getQNVideoTrackInfos());
                        if (userTrackInfo.getQNAudioTrackInfo() != null) {
                            trackInfos.add(userTrackInfo.getQNAudioTrackInfo());
                        }
                        userTrackView.setUserTrackInfo(mEngine, userTrackInfo.getUserId(), trackInfos);
                        userTrackView.changeViewBackgroundByPos(i);
                        mUserTrackInfos.get(i).setUserTrackView(userTrackView);
                    }
                    if(num==1){
                        v.findViewById(R.id.userTrackView2).setVisibility(View.INVISIBLE);
                        v.findViewById(R.id.userTrackView3).setVisibility(View.INVISIBLE);
                        v.findViewById(R.id.userTrackView4).setVisibility(View.INVISIBLE);
                    }else if(num==2){
                        v.findViewById(R.id.userTrackView3).setVisibility(View.INVISIBLE);
                        v.findViewById(R.id.userTrackView4).setVisibility(View.INVISIBLE);
                    }else if(num==3){
                        v.findViewById(R.id.userTrackView4).setVisibility(View.INVISIBLE);
                    }

                    container.addView(v);
                    v.setTag(position);
                    return v;
                }


            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                if (mUserTrackInfos.size() == 0) {
                    return 0;
                } else {
                    return (mUserTrackInfos.size() + 2) / 4 + 1;
                }

            }
        };

        viewPager.setAdapter(mPagerAdapter);
        indicator.setViewPager(viewPager);
        indicator.setSnap(true);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPagerAdapter.notifyDataSetChanged();
                mCurrentPage=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // 权限申请
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                logAndToast("Permission " + permission + " is not granted");
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
        }

        // 初始化 rtcEngine 和本地 TrackInfo 列表
        initQNRTCEngine();
        // 初始化本地音视频 track
        initLocalTrackInfoList();
        // 初始化合流相关配置
        initMergeLayoutConfig();

//        mTrackWindowMgr = new TrackWindowMgr(mUserId, mScreenWidth, mScreenHeight, outMetrics.density
//                , mEngine, mTrackWindowFullScreen, mTrackWindowsList);

        List<QNTrackInfo> localTrackListExcludeScreenTrack = new ArrayList<>(mLocalTrackList);
        localTrackListExcludeScreenTrack.remove(mLocalScreenTrack);
        addTrackInfo(mUserId, localTrackListExcludeScreenTrack);
        //test(mUserId, localTrackListExcludeScreenTrack);
    }

    public void test(String userId, List<QNTrackInfo> trackInfoList) {

        //userTrackView.setUserTrackInfo(mEngine, userId, trackInfoList);

    }


    public void addTrackInfo(String userId, List<QNTrackInfo> trackInfoList) {
//        if (mTrackCandidateWins.size() == 0) {
//            Logging.e(TAG, "There were more than 9 published users in the room, with no unUsedWindow to draw.");
//            return;
//        }
        UserTrackInfo userTrackInfo = null;
        for (int var = 0; var < mUserTrackInfos.size(); var++) {
            if (mUserTrackInfos.get(var).getUserId().equals(userId)) {
                userTrackInfo = mUserTrackInfos.get(var);
                break;
            }

        }
        if (userTrackInfo != null) {
            // user has already displayed in screen
            userTrackInfo.onAddTrackInfo(trackInfoList);
            if(userId.equals(mScreenUserId)){
                mUserTrackInfos.remove(userTrackInfo);
                mUserTrackInfos.add(0,userTrackInfo);

            }
            indicator.notifyDataSetChanged();
        } else {
            // allocate new track windows
            userTrackInfo = new UserTrackInfo(userId);
            userTrackInfo.onAddTrackInfo(trackInfoList);
            if(userId.equals(mScreenUserId)){
                mUserTrackInfos.add(0,userTrackInfo);
            }else {
                mUserTrackInfos.add(userTrackInfo);
            }

            mPagerAdapter.notifyDataSetChanged();
            indicator.notifyDataSetChanged();
            // update whole layout
            //updateTrackWindowsLayout();
        }
    }


    public void removeTrackInfo(String userId, List<QNTrackInfo> trackInfoList) {
        //UserTrackView remoteVideoView = mUserWindowList.get(userId);
        UserTrackInfo remoteVideoView = null;
        for (int var = 0; var < mUserTrackInfos.size(); var++) {
            if (mUserTrackInfos.get(var).getUserId().equals(userId)) {
                remoteVideoView = mUserTrackInfos.get(var);
                break;
            }

        }
        if (remoteVideoView == null) {
            return;
        }
        boolean trackInfoRemains = remoteVideoView.onRemoveTrackInfo(trackInfoList);
        if (userId.equals(mUserId)) {
            // always show myself in screen
            return;
        }
        if (!trackInfoRemains) {
            // check, if no more tracks for this user. remove it
            removeTrackWindow(userId);
        }
    }

    private void removeTrackWindow(String remoteUserId) {
        UserTrackInfo remoteVideoView = null;
        for (int var = 0; var < mUserTrackInfos.size(); var++) {
            if (mUserTrackInfos.get(var).getUserId().equals(remoteUserId)) {
                remoteVideoView = mUserTrackInfos.get(var);
                break;
            }

        }
        if (remoteVideoView != null) {
            boolean removed = mUserTrackInfos.remove(remoteVideoView);
            if (!removed) {
                return;
            }
        }
        //从父控件中移除
//        ViewParent parent = remoteVideoView.getParent();
//        if (parent != null && parent instanceof ViewGroup) {
//            ViewGroup group = (ViewGroup) parent;
//            group.removeView(remoteVideoView);
//        }
        mPagerAdapter.notifyDataSetChanged();
        indicator.notifyDataSetChanged();

    }


    public void onTrackInfoMuted(String remoteUserId,List<QNTrackInfo> trackInfoList) {
        //UserTrackView window = mUserWindowMap.get(remoteUserId);
        UserTrackInfo userTrackInfo = null;
        for (int var = 0; var < mUserTrackInfos.size(); var++) {
            if (mUserTrackInfos.get(var).getUserId().equals(remoteUserId)) {
                userTrackInfo = mUserTrackInfos.get(var);
                break;
            }

        }

        if (userTrackInfo != null) {

            for(QNTrackInfo trackInfo:trackInfoList){
                if (QNTrackKind.AUDIO.equals(trackInfo.getTrackKind())) {
                    userTrackInfo.setQNAudioTrackInfo(trackInfo);
                } else {
                    for(QNTrackInfo track:userTrackInfo.getQNVideoTrackInfos()){
                        if(track.getTrackId().equals(trackInfo.getTrackId())){
                            userTrackInfo.getQNVideoTrackInfos().remove(track);
                            userTrackInfo.getQNVideoTrackInfos().add(trackInfo);
                            break;
                        }
                    }

                }
            }

            if(userTrackInfo.getUserTrackView()!=null){

                userTrackInfo.getUserTrackView().setQNAudioTrackInfo(userTrackInfo.getQNAudioTrackInfo());
                userTrackInfo.getUserTrackView().setQNVideoTrackInfos(userTrackInfo.getQNVideoTrackInfos());
                userTrackInfo.getUserTrackView().onTracksMuteChanged();
            }

            //todo
            //window.onTracksMuteChanged();

        }
    }

    /**
     * 初始化 QNRTCEngine
     */
    private void initQNRTCEngine() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        int videoWidth = preferences.getInt(Config.WIDTH, DEFAULT_RESOLUTION[1][0]);
        int videoHeight = preferences.getInt(Config.HEIGHT, DEFAULT_RESOLUTION[1][1]);
        int fps = preferences.getInt(Config.FPS, DEFAULT_FPS[1]);
        boolean isHwCodec = preferences.getInt(Config.CODEC_MODE, Config.HW) == Config.HW;
        int videoBitrate = preferences.getInt(Config.BITRATE, DEFAULT_BITRATE[1]);
        /**
         * 默认情况下，网络波动时 SDK 内部会降低帧率或者分辨率来保证带宽变化下的视频质量；
         * 如果打开分辨率保持开关，则只会调整帧率来适应网络波动。
         */
        boolean isMaintainRes = preferences.getBoolean(Config.MAINTAIN_RES, false);
        boolean isLowSampleRateEnabled = preferences.getInt(Config.SAMPLE_RATE, Config.HIGH_SAMPLE_RATE) == Config.LOW_SAMPLE_RATE;
        boolean isAec3Enabled = preferences.getBoolean(Config.AEC3_ENABLE, false);
        mCaptureMode = preferences.getInt(Config.CAPTURE_MODE, Config.CAMERA_CAPTURE);

        // 1. VideoPreviewFormat 和 VideoEncodeFormat 建议保持一致
        // 2. 如果远端连麦出现回声的现象，可以通过配置 setLowAudioSampleRateEnabled(true) 或者 setAEC3Enabled(true) 后再做进一步测试，并将设备信息反馈给七牛技术支持
        QNVideoFormat format = new QNVideoFormat(videoWidth, videoHeight, fps);
        QNRTCSetting setting = new QNRTCSetting();
        setting.setCameraID(QNRTCSetting.CAMERA_FACING_ID.FRONT)
                .setHWCodecEnabled(isHwCodec)
                .setMaintainResolution(isMaintainRes)
                .setVideoBitrate(videoBitrate)
                .setLowAudioSampleRateEnabled(isLowSampleRateEnabled)
                .setAEC3Enabled(isAec3Enabled)
                .setVideoEncodeFormat(format)
                .setVideoPreviewFormat(format);
        mEngine = QNRTCEngine.createEngine(getApplicationContext(), setting, this);
    }

    /**
     * 初始化本地音视频 track
     * 关于 Track 的概念介绍 https://doc.qnsdk.com/rtn/android/docs/preparation#5
     */
    private void initLocalTrackInfoList() {
        mLocalTrackList = new ArrayList<>();
        mLocalAudioTrack = mEngine.createTrackInfoBuilder()
                .setSourceType(QNSourceType.AUDIO)
                .setMaster(true)
                .create();
        mLocalTrackList.add(mLocalAudioTrack);

        QNVideoFormat screenEncodeFormat = new QNVideoFormat(mScreenWidth / 2, mScreenHeight / 2, 15);
        switch (mCaptureMode) {
            case Config.CAMERA_CAPTURE:
                // 创建 Camera 采集的视频 Track
                mLocalVideoTrack = mEngine.createTrackInfoBuilder()
                        .setSourceType(QNSourceType.VIDEO_CAMERA)
                        .setMaster(true)
                        .setTag(UserTrackView.TAG_CAMERA).create();
                mLocalTrackList.add(mLocalVideoTrack);
                break;
            case Config.ONLY_AUDIO_CAPTURE:
                //mControlFragment.setAudioOnly(true);
                break;
            case Config.SCREEN_CAPTURE:
                // 创建屏幕录制的视频 Track
                mLocalScreenTrack = mEngine.createTrackInfoBuilder()
                        .setVideoPreviewFormat(screenEncodeFormat)
                        .setBitrate(BITRATE_FOR_SCREEN_VIDEO)
                        .setSourceType(QNSourceType.VIDEO_SCREEN)
                        .setMaster(true)
                        .setTag(UserTrackView.TAG_SCREEN).create();
                mLocalTrackList.add(mLocalScreenTrack);
                //mControlFragment.setAudioOnly(true);
                break;
            case Config.MUTI_TRACK_CAPTURE:
                // 视频通话 + 屏幕共享两路 track
                mLocalScreenTrack = mEngine.createTrackInfoBuilder()
                        .setSourceType(QNSourceType.VIDEO_SCREEN)
                        .setVideoPreviewFormat(screenEncodeFormat)
                        .setBitrate(BITRATE_FOR_SCREEN_VIDEO)
                        .setMaster(true)
                        .setTag(UserTrackView.TAG_SCREEN).create();
                mLocalVideoTrack = mEngine.createTrackInfoBuilder()
                        .setSourceType(QNSourceType.VIDEO_CAMERA)
                        .setTag(UserTrackView.TAG_CAMERA).create();
                mLocalTrackList.add(mLocalScreenTrack);
                mLocalTrackList.add(mLocalVideoTrack);
                break;
        }
    }

    private void initMergeLayoutConfig() {
//        mMergeLayoutConfigView = new MergeLayoutConfigView(this);
//        mMergeLayoutConfigView.setRoomId(mRoomId);
//        mUserListAdapter = new UserListAdapter();
//        mRoomUserList = new RoomUserList();
//        mMergeLayoutConfigView.getUserListView().setAdapter(mUserListAdapter);
//        mMergeLayoutConfigView.setOnClickedListener(new MergeLayoutConfigView.OnClickedListener() {
//            @Override
//            public void onConfirmClicked() {
//                if (mEngine == null) {
//                    return;
//                }
//                if (!mMergeLayoutConfigView.isStreamingEnabled()) {
//                    // 处理停止合流逻辑
//                    if (mIsStreaming) {
//                        // 如果正在推流，则停止之前的合流任务
//                        // 传入 null，则处理默认的合流任务
//                        mEngine.stopMergeStream(mCurrentMergeJob == null ? null : mCurrentMergeJob.getMergeJobId());
//                        mIsStreaming = false;
//                        ToastUtils.s(RoomActivity.this, "停止合流！！！");
//                    } else {
//                        ToastUtils.s(RoomActivity.this, "未开启合流，配置未生效！！！");
//                    }
//                    if (mPopWindow != null) {
//                        mPopWindow.dismiss();
//                    }
//                    return;
//                }
//                if (mMergeLayoutConfigView.isCustomMergeJob()) {
//                    // 处理自定义合流任务的逻辑
//                    QNMergeJob mergeJob = mMergeLayoutConfigView.getCustomMergeJob();
//                    if (mergeJob != null) {
//                        // 如果正在推流，则停止之前的合流任务
//                        if (mIsStreaming) {
//                            mEngine.stopMergeStream(mCurrentMergeJob == null ? null : mCurrentMergeJob.getMergeJobId());
//                        }
//                        mCurrentMergeJob = mergeJob;
//                        // 创建自定义合流任务
//                        mEngine.createMergeJob(mCurrentMergeJob);
//                    }
//                }
//                List<UserTrack> userTracks = mMergeLayoutConfigView.updateMergeOptions();
//                List<QNMergeTrackOption> addedTrackOptions = new ArrayList<>();
//                List<QNMergeTrackOption> removedTrackOptions = new ArrayList<>();
//                for (UserTrack item : userTracks) {
//                    if (item.isTrackInclude()) {
//                        addedTrackOptions.add(item.getQNMergeTrackOption());
//                    } else {
//                        removedTrackOptions.add(item.getQNMergeTrackOption());
//                    }
//                }
//                if (!addedTrackOptions.isEmpty()) {
//                    // 配置对应 tracks 的合流配置信息
//                    mEngine.setMergeStreamLayouts(addedTrackOptions, mCurrentMergeJob == null ? null : mCurrentMergeJob.getMergeJobId());
//                }
//                if (!removedTrackOptions.isEmpty()) {
//                    // 移除对应 tracks 的合流配置，移除后相应 track 的数据将不会参与合流
//                    mEngine.removeMergeStreamLayouts(removedTrackOptions, mCurrentMergeJob == null ? null : mCurrentMergeJob.getMergeJobId());
//                }
//                if (mPopWindow != null) {
//                    mPopWindow.dismiss();
//                }
//                mIsStreaming = true;
//                ToastUtils.s(RoomActivity.this, "已发送合流配置，请等待合流画面生效");
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 开始视频采集
        mEngine.startCapture();
        if (!mIsJoinedRoom) {
            // 加入房间
            mEngine.joinRoom(mRoomToken);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(MessageEvent event) {
        if (event.getEventName().equals("message")) {
            String msg=event.getMessageBody();

            JSONObject jo=JSONObject.parseObject(msg);
            if(jo.getString("type").equals("99")){
                JSONObject jsonObject=jo.getJSONObject("data");
                if(jsonObject!=null){
                    if(jsonObject.getString("video").equals("1")){
                        canVideo=true;
                    }else if(jsonObject.getString("video").equals("0")){
                        canVideo=false;
                        if(mVideoEnabled){
                            if (mEngine != null && mLocalVideoTrack != null) {
                                mVideoEnabled = !mVideoEnabled;
                                mLocalVideoTrack.setMuted(!mVideoEnabled);
                                if (mLocalScreenTrack != null) {
                                    mLocalScreenTrack.setMuted(!mVideoEnabled);
                                    mEngine.muteTracks(Arrays.asList(mLocalScreenTrack, mLocalVideoTrack));
                                } else {
                                    mEngine.muteTracks(Collections.singletonList(mLocalVideoTrack));
                                }

                                List<QNTrackInfo> localTrackListExcludeScreenTrack = new ArrayList<>(mLocalTrackList);
                                localTrackListExcludeScreenTrack.remove(mLocalScreenTrack);
                                addTrackInfo(mUserId, localTrackListExcludeScreenTrack);
                                onTrackInfoMuted(mUserId,localTrackListExcludeScreenTrack);
                            }
                            videoStatus.setText(mVideoEnabled ?  R.string.menus_video:R.string.menus_cancel_video);

                        }
                    }
                    if(jsonObject.getString("audio").equals("1")){

                        canSpeak=true;
                    }else if(jsonObject.getString("audio").equals("2")){
                        canSpeak=false;
                        if(mMicEnabled){
                            if (mEngine != null && mLocalAudioTrack != null) {
                                mMicEnabled = !mMicEnabled;
                                mLocalAudioTrack.setMuted(!mMicEnabled);
                                mEngine.muteTracks(Collections.singletonList(mLocalAudioTrack));
                                List<QNTrackInfo> localTrackListExcludeScreenTrack = new ArrayList<>(mLocalTrackList);
                                localTrackListExcludeScreenTrack.remove(mLocalScreenTrack);
                                addTrackInfo(mUserId, localTrackListExcludeScreenTrack);
                                onTrackInfoMuted(mUserId,localTrackListExcludeScreenTrack);
                            }
                            muteStatus.setText(mMicEnabled ? R.string.menus_cancel_mute : R.string.menus_mute);

                        }
                    }


                    mScreenUserId=jsonObject.getString("screenUserId");
                    for(int i=0;i<mUserTrackInfos.size();i++){
                        if(mUserTrackInfos.get(i).getUserId().equals(mScreenUserId)){
                            if(i!=0){
                                UserTrackInfo userTrackInfo=mUserTrackInfos.remove(i);
                                mUserTrackInfos.add(0,userTrackInfo);
                                mPagerAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }



            }else if(jo.getString("type").equals("2")){
                //切换主屏
                JSONObject jsonObject=jo.getJSONObject("data");
                if(jsonObject!=null){
                    mScreenUserId=jsonObject.getString("userId");

                    for(int i=0;i<mUserTrackInfos.size();i++){
                        if(mUserTrackInfos.get(i).getUserId().equals(mScreenUserId)){
                            if(i!=0){
                                UserTrackInfo userTrackInfo=mUserTrackInfos.remove(i);
                                mUserTrackInfos.add(0,userTrackInfo);
                                mPagerAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }

                }
            }else if(jo.getString("type").equals("1")){
                JSONObject jsonObject=jo.getJSONObject("data");
                if(jsonObject!=null){
                    if(jsonObject.getString("video").equals("1")){
                        ToastUtils.show("全体成员视频已开启");
                        canVideo=true;
                    }else if(jsonObject.getString("video").equals("2")){
                        canVideo=false;
                        ToastUtils.show("全体成员视频已禁用");
                        if(mVideoEnabled){
                            if (mEngine != null && mLocalVideoTrack != null) {
                                mVideoEnabled = !mVideoEnabled;
                                mLocalVideoTrack.setMuted(!mVideoEnabled);
                                if (mLocalScreenTrack != null) {
                                    mLocalScreenTrack.setMuted(!mVideoEnabled);
                                    mEngine.muteTracks(Arrays.asList(mLocalScreenTrack, mLocalVideoTrack));
                                } else {
                                    mEngine.muteTracks(Collections.singletonList(mLocalVideoTrack));
                                }

                                List<QNTrackInfo> localTrackListExcludeScreenTrack = new ArrayList<>(mLocalTrackList);
                                localTrackListExcludeScreenTrack.remove(mLocalScreenTrack);
                                addTrackInfo(mUserId, localTrackListExcludeScreenTrack);
                                onTrackInfoMuted(mUserId,localTrackListExcludeScreenTrack);
                            }
                            videoStatus.setText(mVideoEnabled ?  R.string.menus_video:R.string.menus_cancel_video);

                        }
                    }

                    if(jsonObject.getString("audio").equals("1")){
                        ToastUtils.show("全体成员语音已开启");
                        canSpeak=true;
                    }else if(jsonObject.getString("audio").equals("2")){
                        canSpeak=false;
                        ToastUtils.show("主持人已将全体成员静音");
                        if(mMicEnabled){
                            if (mEngine != null && mLocalAudioTrack != null) {
                                mMicEnabled = !mMicEnabled;
                                mLocalAudioTrack.setMuted(!mMicEnabled);
                                mEngine.muteTracks(Collections.singletonList(mLocalAudioTrack));
                                List<QNTrackInfo> localTrackListExcludeScreenTrack = new ArrayList<>(mLocalTrackList);
                                localTrackListExcludeScreenTrack.remove(mLocalScreenTrack);
                                addTrackInfo(mUserId, localTrackListExcludeScreenTrack);
                                onTrackInfoMuted(mUserId,localTrackListExcludeScreenTrack);
                            }
                            muteStatus.setText(mMicEnabled ? R.string.menus_cancel_mute : R.string.menus_mute);

                        }
                    }



                }


            }else if(jo.getString("type").equals("4")){
                JSONObject jsonObject=jo.getJSONObject("data");
                if(jsonObject!=null){

                    if(jsonObject.getString("id").equals(mUserId)){
                        if(jsonObject.getString("video").equals("1")){
                            ToastUtils.show("支持人已允许您的视频请求");
                            canVideo=true;
                        }else if(jsonObject.getString("video").equals("2")){
                            canVideo=false;
                            ToastUtils.show("支持人已禁止您的视频请求");
                            if(mVideoEnabled){
                                if (mEngine != null && mLocalVideoTrack != null) {
                                    mVideoEnabled = !mVideoEnabled;
                                    mLocalVideoTrack.setMuted(!mVideoEnabled);
                                    if (mLocalScreenTrack != null) {
                                        mLocalScreenTrack.setMuted(!mVideoEnabled);
                                        mEngine.muteTracks(Arrays.asList(mLocalScreenTrack, mLocalVideoTrack));
                                    } else {
                                        mEngine.muteTracks(Collections.singletonList(mLocalVideoTrack));
                                    }

                                    List<QNTrackInfo> localTrackListExcludeScreenTrack = new ArrayList<>(mLocalTrackList);
                                    localTrackListExcludeScreenTrack.remove(mLocalScreenTrack);
                                    addTrackInfo(mUserId, localTrackListExcludeScreenTrack);
                                    onTrackInfoMuted(mUserId,localTrackListExcludeScreenTrack);
                                }
                                videoStatus.setText(mVideoEnabled ?  R.string.menus_video:R.string.menus_cancel_video);

                            }
                        }

                        if(jsonObject.getString("audio").equals("1")){
                            ToastUtils.show("支持人已允许您的语音请求");
                            canSpeak=true;
                        }else if(jsonObject.getString("audio").equals("2")){
                            canSpeak=false;
                            ToastUtils.show("支持人已禁止您的语音请求");
                            if(mMicEnabled){
                                if (mEngine != null && mLocalAudioTrack != null) {
                                    mMicEnabled = !mMicEnabled;
                                    mLocalAudioTrack.setMuted(!mMicEnabled);
                                    mEngine.muteTracks(Collections.singletonList(mLocalAudioTrack));
                                    List<QNTrackInfo> localTrackListExcludeScreenTrack = new ArrayList<>(mLocalTrackList);
                                    localTrackListExcludeScreenTrack.remove(mLocalScreenTrack);
                                    addTrackInfo(mUserId, localTrackListExcludeScreenTrack);
                                    onTrackInfoMuted(mUserId,localTrackListExcludeScreenTrack);
                                }
                                muteStatus.setText(mMicEnabled ? R.string.menus_cancel_mute : R.string.menus_mute);

                            }
                        }
                    }





                }


            }


        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        // 停止视频采集
        mEngine.stopCapture();
        if (mPopWindow != null && mPopWindow.isShowing()) {
            mPopWindow.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().register(this);
        if (mEngine != null) {
            if (mIsAdmin && mIsStreaming) {
                // 如果当前正在合流，则停止
                mEngine.stopMergeStream(mCurrentMergeJob == null ? null : mCurrentMergeJob.getMergeJobId());
                mIsStreaming = false;
            }
            // 释放相关资源
            mEngine.destroy();
            mEngine = null;
        }
//        if (mTrackWindowFullScreen != null) {
//            mTrackWindowFullScreen.dispose();
//        }
//        for (UserTrackView item : mTrackWindowsList) {
//            item.dispose();
//        }
//        mTrackWindowsList.clear();
        mPopWindow = null;
    }

    private void logAndToast(final String msg) {
        Log.d(TAG, msg);
        if (mLogToast != null) {
            mLogToast.cancel();
        }
        mLogToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        mLogToast.show();
    }

    private void disconnectWithErrorMessage(final String errorMessage) {
        new AlertDialog.Builder(this)
                .setTitle(getText(R.string.channel_error_title))
                .setMessage(errorMessage)
                .setCancelable(false)
                .setNeutralButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        })
                .create()
                .show();
    }

    private void reportError(final String description) {
        // TODO: handle error.
        if (!mIsError) {
            mIsError = true;
            disconnectWithErrorMessage(description);
        }
    }

//    private void showKickoutDialog(final String userId) {
//        if (mKickOutDialog == null) {
//            mKickOutDialog = new AlertDialog.Builder(this)
//                    .setNegativeButton(R.string.negative_dialog_tips, null)
//                    .create();
//        }
//        mKickOutDialog.setMessage(getString(R.string.kickout_tips, userId));
//        mKickOutDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.positive_dialog_tips),
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mEngine.kickOutUser(userId);
//                    }
//                });
//        mKickOutDialog.show();
//    }

//    private void updateRemoteLogText(final String logText) {
//        Log.i(TAG, logText);
//        mControlFragment.updateRemoteLogText(logText);
//    }

//    private void resetMergeStream() {
//        Log.d(TAG, "resetMergeStream()");
//        List<QNMergeTrackOption> configuredMergeTracksOptions = new ArrayList<>();
//
//        // video tracks merge layout options.
//        List<UserTrack> remoteVideoTrackInfoList = mRoomUserList.getRTCVideoTracks();
//        if (!remoteVideoTrackInfoList.isEmpty()) {
//            List<QNMergeTrackOption> mergeTrackOptions = SplitUtils.split(remoteVideoTrackInfoList.size(),
//                    mCurrentMergeJob == null ? QNAppServer.STREAMING_WIDTH : mCurrentMergeJob.getWidth(),
//                    mCurrentMergeJob == null ? QNAppServer.STREAMING_HEIGHT : mCurrentMergeJob.getHeight());
//            if (mergeTrackOptions.size() != remoteVideoTrackInfoList.size()) {
//                Log.e(TAG, "split option error.");
//                return;
//            }
//
//            for (int i = 0; i < mergeTrackOptions.size(); i++) {
//                UserTrack userTrack = remoteVideoTrackInfoList.get(i);
//
//                if (!userTrack.isTrackInclude()) {
//                    continue;
//                }
//                QNMergeTrackOption item = mergeTrackOptions.get(i);
//                userTrack.updateQNMergeTrackOption(item);
//                configuredMergeTracksOptions.add(userTrack.getQNMergeTrackOption());
//            }
//        }
//
//        // audio tracks merge layout options
//        List<UserTrack> remoteAudioTrackInfoList = mRoomUserList.getRTCAudioTracks();
//        if (!remoteAudioTrackInfoList.isEmpty()) {
//            for (UserTrack userTrack : remoteAudioTrackInfoList) {
//                if (!userTrack.isTrackInclude()) {
//                    continue;
//                }
//                configuredMergeTracksOptions.add(userTrack.getQNMergeTrackOption());
//            }
//        }
//
//        if (mIsStreaming) {
//            mEngine.setMergeStreamLayouts(configuredMergeTracksOptions, mCurrentMergeJob == null ? null : mCurrentMergeJob.getMergeJobId());
//        }
//    }

//    private void userJoinedForStreaming(String userId, String userData) {
//        mRoomUserList.onUserJoined(userId, userData);
//        if (mUserListAdapter != null) {
//            mUserListAdapter.notifyDataSetChanged();
//        }
//    }

//    private void userLeftForStreaming(String userId) {
//        mRoomUserList.onUserLeft(userId);
//        if (mUserListAdapter != null) {
//            mUserListAdapter.notifyDataSetChanged();
//        }
//    }

    @TargetApi(19)
    private static int getSystemUiVisibility() {
        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        return flags;
    }

    /**
     * 房间状态改变时会回调此方法
     * 房间状态回调只需要做提示用户，或者更新相关 UI； 不需要再做加入房间或者重新发布等其他操作！
     *
     * @param state 房间状态，可参考 {@link QNRoomState}
     */
    @Override
    public void onRoomStateChanged(QNRoomState state) {
        Log.i(TAG, "onRoomStateChanged:" + state.name());
        switch (state) {
            case RECONNECTING:
                logAndToast(getString(R.string.reconnecting_to_room));
                timer.stop();
//                if (mIsAdmin) {
//                    mRoomUserList.onTracksUnPublished(mUserId, mLocalTrackList);
//                    userLeftForStreaming(mUserId);
//                }
                break;
            case CONNECTED:
//                if (mIsAdmin) {
//                    userJoinedForStreaming(mUserId, "");
//                }
                // 加入房间后可以进行 tracks 的发布
                mEngine.publishTracks(mLocalTrackList);
                logAndToast(getString(R.string.connected_to_room));
                mIsJoinedRoom = true;
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();
                break;
            case RECONNECTED:
                logAndToast(getString(R.string.connected_to_room));
                timer.stop();
//                if (mIsAdmin) {
//                    userJoinedForStreaming(mUserId, "");
//                }
                break;
            case CONNECTING:
                logAndToast(getString(R.string.connecting_to, mRoomId));
                break;
        }
    }

    /**
     * 当退出房间执行完毕后触发该回调，可用于切换房间
     */
    @Override
    public void onRoomLeft() {
        //logAndToast("onRoomLeft");
        Log.i(TAG, "onRoomLeft");
    }

    /**
     * 远端用户加入房间时会回调此方法
     *
     * @param remoteUserId 远端用户的 userId
     * @param userData     透传字段，用户自定义内容
     * @see QNRTCEngine#joinRoom(String, String) 可指定 userData 字段
     */
    @Override
    public void onRemoteUserJoined(String remoteUserId, String userData) {
//        updateRemoteLogText("onRemoteUserJoined:remoteUserId = " + remoteUserId + " ,userData = " + userData);
//        if (mIsAdmin) {
//            userJoinedForStreaming(remoteUserId, userData);
//        }
        //logAndToast("onRemoteUserJoined,remoteUserId="+remoteUserId);
        Log.i(TAG, "onRemoteUserJoined,remoteUserId=" + remoteUserId);
    }

    /**
     * 远端用户离开房间时会回调此方法
     *
     * @param remoteUserId 远端离开用户的 userId
     */
    @Override
    public void onRemoteUserLeft(final String remoteUserId) {
//        updateRemoteLogText("onRemoteUserLeft:remoteUserId = " + remoteUserId);
//        if (mIsAdmin) {
//            userLeftForStreaming(remoteUserId);
        Log.i(TAG, "onRemoteUserLeft,remoteUserId=" + remoteUserId);
//        }
    }

    /**
     * 本地 tracks 成功发布时会回调此方法
     *
     * @param trackInfoList 已发布的 tracks 列表
     */
    @Override
    public void onLocalPublished(List<QNTrackInfo> trackInfoList) {
        //updateRemoteLogText("onLocalPublished");
        //logAndToast("onLocalPublished");
        Log.i(TAG, "onLocalPublished");
        mEngine.enableStatistics();
//        if (mIsAdmin) {
//            mRoomUserList.onTracksPublished(mUserId, mLocalTrackList);
//            resetMergeStream();
//        }
    }

    /**
     * 远端用户 tracks 成功发布时会回调此方法
     *
     * @param remoteUserId  远端用户 userId
     * @param trackInfoList 远端用户发布的 tracks 列表
     */
    @Override
    public void onRemotePublished(String remoteUserId, List<QNTrackInfo> trackInfoList) {
        Log.i(TAG, "onRemotePublished");
//        updateRemoteLogText("onRemotePublished:remoteUserId = " + remoteUserId);
//        mRoomUserList.onTracksPublished(remoteUserId, trackInfoList);
//        // 如果希望在远端发布音视频的时候，自动配置合流，则可以在此处重新调用 setMergeStreamLayouts 进行配置
//        if (mIsAdmin) {
//            resetMergeStream();
//        }
    }

    /**
     * 远端用户 tracks 成功取消发布时会回调此方法
     *
     * @param remoteUserId  远端用户 userId
     * @param trackInfoList 远端用户取消发布的 tracks 列表
     */
    @Override
    public void onRemoteUnpublished(final String remoteUserId, List<QNTrackInfo> trackInfoList) {
        //updateRemoteLogText("onRemoteUnpublished:remoteUserId = " + remoteUserId);
//        if (mTrackWindowMgr != null) {
//            mTrackWindowMgr.removeTrackInfo(remoteUserId, trackInfoList);
//        }
//        mRoomUserList.onTracksUnPublished(remoteUserId, trackInfoList);
//        if (mIsAdmin) {
//            resetMergeStream();
//        }
        Log.i(TAG, "onRemoteUnpublished");
        removeTrackInfo(remoteUserId, trackInfoList);
    }

    /**
     * 远端用户成功操作静默 tracks 时会回调此方法
     *
     * @param remoteUserId  远端用户 userId
     * @param trackInfoList 远端用户静默的 tracks 列表，是否静默可以通过读取 {@link QNTrackInfo} 的 isMuted() 方法获取
     */
    @Override
    public void onRemoteUserMuted(String remoteUserId, List<QNTrackInfo> trackInfoList) {
//        updateRemoteLogText("onRemoteUserMuted:remoteUserId = " + remoteUserId);
//        if (mTrackWindowMgr != null) {
//            mTrackWindowMgr.onTrackInfoMuted(remoteUserId);
//        }
        Log.i(TAG, "onRemoteUserMuted");



        onTrackInfoMuted(remoteUserId,trackInfoList);
    }

    /**
     * 成功订阅远端用户的 tracks 时会回调此方法
     *
     * @param remoteUserId  远端用户 userId
     * @param trackInfoList 订阅的远端用户 tracks 列表
     */
    @Override
    public void onSubscribed(String remoteUserId, List<QNTrackInfo> trackInfoList) {
//        updateRemoteLogText("onSubscribed:remoteUserId = " + remoteUserId);
//        if (mTrackWindowMgr != null) {
//            mTrackWindowMgr.addTrackInfo(remoteUserId, trackInfoList);
//        }
        Log.i(TAG, "onSubscribed");
        addTrackInfo(remoteUserId, trackInfoList);
    }

    /**
     * 当自己被踢出房间时会回调此方法
     *
     * @param userId 踢人方的 userId
     */
    @Override
    public void onKickedOut(String userId) {
        ToastUtils.show(getString(R.string.kicked_by_admin));
        finish();
    }

    /**
     * 当媒体状态更新时会回调此方法
     * <p>
     * QNStatisticsReport#audioPacketLostRate（音频丢包率）和 QNStatisticsReport#videoPacketLostRate (视频丢包率)
     * 可以用来向用户提示自己网络状态不佳（比如，连续一段时间丢包路超过 10%）。
     *
     * @param report 媒体信息，详情请参考 {@link QNStatisticsReport}
     */
    @Override
    public void onStatisticsUpdated(final QNStatisticsReport report) {
        if (report.userId == null || report.userId.equals(mUserId)) {
            if (QNTrackKind.AUDIO.equals(report.trackKind)) {
                final String log = "音频码率:" + report.audioBitrate / 1000 + "kbps \n" +
                        "音频丢包率:" + report.audioPacketLostRate;
                //mControlFragment.updateLocalAudioLogText(log);
            } else if (QNTrackKind.VIDEO.equals(report.trackKind)) {
                final String log = "视频码率:" + report.videoBitrate / 1000 + "kbps \n" +
                        "视频丢包率:" + report.videoPacketLostRate + " \n" +
                        "视频的宽:" + report.width + " \n" +
                        "视频的高:" + report.height + " \n" +
                        "视频的帧率:" + report.frameRate;
                //mControlFragment.updateLocalVideoLogText(log);
            }
        }
    }

    /**
     * 当收到远端用户的媒体状态时会回调此方法
     * <p>
     * QNStatisticsReport#audioPacketLostRate（音频丢包率）和 QNStatisticsReport#videoPacketLostRate (视频丢包率)
     * 可以用来向用户提示对方用户网络状态不佳（比如，连续一段时间丢包路超过 10%）。
     *
     * @param reports 媒体信息，详情请参考 {@link QNStatisticsReport}
     */
    @Override
    public void onRemoteStatisticsUpdated(List<QNStatisticsReport> reports) {
        for (QNStatisticsReport report : reports) {
            int lost = report.trackKind.equals(QNTrackKind.VIDEO) ? report.videoPacketLostRate : report.audioPacketLostRate;
            Log.i(TAG, "remote user " + report.userId
                    + " rtt " + report.rtt
                    + " grade " + report.networkGrade
                    + " track " + report.trackId
                    + " kind " + (report.trackKind.name())
                    + " lostRate " + lost);
        }
    }

    /**
     * 当音频路由发生变化时会回调此方法
     *
     * @param routing 音频设备, 详情请参考{@link QNAudioDevice}
     */
    @Override
    public void onAudioRouteChanged(QNAudioDevice routing) {
        //updateRemoteLogText("onAudioRouteChanged: " + routing.name());
    }

    /**
     * 当合流任务创建成功的时候会回调此方法
     *
     * @param mergeJobId 合流任务 id
     */
    @Override
    public void onCreateMergeJobSuccess(String mergeJobId) {
        ToastUtils.show("合流任务 " + mergeJobId + " 创建成功！");
    }

    /**
     * 当发生错误时会回调此方法
     *
     * @param errorCode   错误码，详情请参考 {@link QNErrorCode}
     * @param description 错误描述
     */
    @Override
    public void onError(int errorCode, String description) {
        /**
         * 关于错误异常的相关处理，都应在该回调中完成; 需要处理的错误码及建议处理逻辑如下:
         *
         *【TOKEN 相关】
         * 1. QNErrorCode.ERROR_TOKEN_INVALID 和 QNErrorCode.ERROR_TOKEN_ERROR 表示您提供的房间 token 不符合七牛 token 签算规则,
         *    详情请参考【服务端开发说明.RoomToken 签发服务】https://doc.qnsdk.com/rtn/docs/server_overview#1
         * 2. QNErrorCode.ERROR_TOKEN_EXPIRED 表示您的房间 token 过期, 需要重新生成 token 再加入；
         *
         *【房间设置相关】以下情况可以与您的业务服务开发确认具体设置
         * 1. QNErrorCode.ERROR_ROOM_FULL 当房间已加入人数超过每个房间的人数限制触发；请确认后台服务的设置；
         * 2. QNErrorCode.ERROR_PLAYER_ALREADY_EXIST 后台如果配置为开启【禁止自动踢人】,则同一用户重复加入/未正常退出再加入会触发此错误，您的业务可根据实际情况选择配置；
         * 3. QNErrorCode.ERROR_NO_PERMISSION 用户对于特定操作，如合流需要配置权限，禁止出现未授权的用户操作；
         * 4. QNErrorCode.ERROR_ROOM_CLOSED 房间已被管理员关闭；
         *
         *【其他错误】
         * 1. QNErrorCode.ERROR_AUTH_FAIL 服务验证时出错，可能为服务网络异常。建议重新尝试加入房间；
         * 2. QNErrorCode.ERROR_PUBLISH_FAIL 发布失败, 会有如下3种情况:
         * 1 ）请确认成功加入房间后，再执行发布操作
         * 2 ）请确定对于音频/视频 Track，分别最多只能有一路为 master
         * 3 ）请确认您的网络状况是否正常

         * 3. QNErrorCode.ERROR_RECONNECT_TOKEN_ERROR 内部重连后出错，一般出现在网络非常不稳定时出现，建议提示用户并尝试重新加入房间；
         * 4. QNErrorCode.ERROR_INVALID_PARAMETER 服务交互参数错误，请在开发时注意合流、踢人动作等参数的设置。
         * 5. QNErrorCode.ERROR_DEVICE_CAMERA 系统摄像头错误, 建议提醒用户检查
         */
        switch (errorCode) {
            case QNErrorCode.ERROR_TOKEN_INVALID:
            case QNErrorCode.ERROR_TOKEN_ERROR:
                logAndToast("roomToken 错误，请检查后重新生成，再加入房间");
                break;
            case QNErrorCode.ERROR_TOKEN_EXPIRED:
                logAndToast("roomToken过期");
                mRoomToken = QNAppServer.getInstance().requestRoomToken(RoomActivity.this, mUserId, mRoomId);
                mEngine.joinRoom(mRoomToken);
                break;
            case QNErrorCode.ERROR_ROOM_FULL:
                logAndToast("房间人数已满!");
                break;
            case QNErrorCode.ERROR_PLAYER_ALREADY_EXIST:
                logAndToast("不允许同一用户重复加入");
                break;
            case QNErrorCode.ERROR_NO_PERMISSION:
                logAndToast("请检查用户权限:" + description);
                break;
            case QNErrorCode.ERROR_INVALID_PARAMETER:
                logAndToast("请检查参数设置:" + description);
                break;
            case QNErrorCode.ERROR_PUBLISH_FAIL: {
                if (mEngine.getRoomState() != QNRoomState.CONNECTED
                        && mEngine.getRoomState() != QNRoomState.RECONNECTED) {
                    logAndToast("发布失败，请加入房间发布: " + description);
                    mEngine.joinRoom(mRoomToken);
                } else {
                    logAndToast("发布失败: " + description);
                    mEngine.publishTracks(mLocalTrackList);
                }
            }
            break;
            case QNErrorCode.ERROR_AUTH_FAIL:
            case QNErrorCode.ERROR_RECONNECT_TOKEN_ERROR: {
                // reset TrackWindowMgr
                reset();
                // display local videoTrack
                List<QNTrackInfo> localTrackListExcludeScreenTrack = new ArrayList<>(mLocalTrackList);
                localTrackListExcludeScreenTrack.remove(mLocalScreenTrack);
                addTrackInfo(mUserId, localTrackListExcludeScreenTrack);
                if (errorCode == QNErrorCode.ERROR_RECONNECT_TOKEN_ERROR) {
                    logAndToast("ERROR_RECONNECT_TOKEN_ERROR 即将重连，请注意网络质量！");
                }
                if (errorCode == QNErrorCode.ERROR_AUTH_FAIL) {
                    logAndToast("ERROR_AUTH_FAIL 即将重连");
                }
                // rejoin Room
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mEngine.joinRoom(mRoomToken);
                    }
                }, 1000);
            }
            break;
            case QNErrorCode.ERROR_ROOM_CLOSED:
                reportError("房间被关闭");
                break;
            case QNErrorCode.ERROR_DEVICE_CAMERA:
                logAndToast("请检查摄像头权限，或者被占用");
                break;
            default:
                logAndToast("errorCode:" + errorCode + " description:" + description);
                break;
        }
    }

    /**
     * 当收到自定义消息时回调此方法
     *
     * @param message 自定义信息，详情请参考 {@link QNCustomMessage}
     */
    @Override
    public void onMessageReceived(QNCustomMessage message) {

    }

    // Demo control
//    @Override
//    public void onCallHangUp() {
//        if (mEngine != null) {
//            mEngine.leaveRoom();
//        }
//        finish();
//    }

//    @Override
//    public void onCameraSwitch() {
//        if (mEngine != null) {
//            mEngine.switchCamera(new QNCameraSwitchResultCallback() {
//                @Override
//                public void onCameraSwitchDone(boolean isFrontCamera) {
//                }
//
//                @Override
//                public void onCameraSwitchError(String errorMessage) {
//                }
//            });
//        }
//    }

//    @Override
//    public boolean onToggleMic() {
//        if (mEngine != null && mLocalAudioTrack != null) {
//            mMicEnabled = !mMicEnabled;
//            mLocalAudioTrack.setMuted(!mMicEnabled);
//            mEngine.muteTracks(Collections.singletonList(mLocalAudioTrack));
//            if (mTrackWindowMgr != null) {
//                mTrackWindowMgr.onTrackInfoMuted(mUserId);
//            }
//        }
//        return mMicEnabled;
//    }

//    @Override
//    public boolean onToggleVideo() {
//        if (mEngine != null && mLocalVideoTrack != null) {
//            mVideoEnabled = !mVideoEnabled;
//            mLocalVideoTrack.setMuted(!mVideoEnabled);
//            if (mLocalScreenTrack != null) {
//                mLocalScreenTrack.setMuted(!mVideoEnabled);
//                mEngine.muteTracks(Arrays.asList(mLocalScreenTrack, mLocalVideoTrack));
//            } else {
//                mEngine.muteTracks(Collections.singletonList(mLocalVideoTrack));
//            }
//            if (mTrackWindowMgr != null) {
//                mTrackWindowMgr.onTrackInfoMuted(mUserId);
//            }
//        }
//        return mVideoEnabled;
//    }

//    @Override
//    public boolean onToggleSpeaker() {
//        if (mEngine != null) {
//            mSpeakerEnabled = !mSpeakerEnabled;
//            mEngine.muteRemoteAudio(!mSpeakerEnabled);
//        }
//        return mSpeakerEnabled;
//    }

//    @Override
//    public boolean onToggleBeauty() {
//        if (mEngine != null) {
//            mBeautyEnabled = !mBeautyEnabled;
//            QNBeautySetting beautySetting = new QNBeautySetting(0.5f, 0.5f, 0.5f);
//            beautySetting.setEnable(mBeautyEnabled);
//            mEngine.setBeauty(beautySetting);
//        }
//        return mBeautyEnabled;
//    }

//    @Override
//    public void onCallStreamingConfig() {
//        if (!mIsAdmin) {
//            ToastUtils.s(RoomActivity.this, "只有 \"admin\" 用户可以开启推流！！！");
//            return;
//        }
//        //配置页
//        if (mRoomUserList.size() == 0) {
//            return;
//        }
//        mChooseUser = mRoomUserList.getRoomUserByPosition(0);
//        mMergeLayoutConfigView.updateConfigInfo(mChooseUser);
//        mMergeLayoutConfigView.updateMergeJobConfigInfo();
//        mUserListAdapter.notifyDataSetChanged();
//
//        if (mPopWindow == null) {
//            mPopWindow = new PopupWindow(mMergeLayoutConfigView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//            mPopWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.popupWindowBackground)));
//        }
//        mPopWindow.showAtLocation(getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
//    }

    @OnClick({R.id.switchCamera, R.id.quit, R.id.openMute, R.id.closeVideo, R.id.panel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.switchCamera:
                if (mEngine != null) {
                    mEngine.switchCamera(new QNCameraSwitchResultCallback() {
                        @Override
                        public void onCameraSwitchDone(boolean isFrontCamera) {
                        }

                        @Override
                        public void onCameraSwitchError(String errorMessage) {
                        }
                    });
                }
                break;
            case R.id.quit:
                if (mEngine != null) {
                    mEngine.leaveRoom();
                }
                finish();
                break;
            case R.id.openMute:
                if(canSpeak){
                    if (mEngine != null && mLocalAudioTrack != null) {
                        mMicEnabled = !mMicEnabled;
                        mLocalAudioTrack.setMuted(!mMicEnabled);
                        mEngine.muteTracks(Collections.singletonList(mLocalAudioTrack));
//                    if (mTrackWindowMgr != null) {
//                        mTrackWindowMgr.onTrackInfoMuted(mUserId);
//                    }
                        List<QNTrackInfo> localTrackListExcludeScreenTrack = new ArrayList<>(mLocalTrackList);
                        localTrackListExcludeScreenTrack.remove(mLocalScreenTrack);
                        addTrackInfo(mUserId, localTrackListExcludeScreenTrack);
                        onTrackInfoMuted(mUserId,localTrackListExcludeScreenTrack);
                    }
                    muteStatus.setText(mMicEnabled ? R.string.menus_cancel_mute : R.string.menus_mute);
                }else{
                    ToastUtils.show("支持人已经禁言");
                }


                //return mMicEnabled;

                break;
            case R.id.closeVideo:
                if(canVideo){
                    if (mEngine != null && mLocalVideoTrack != null) {
                        mVideoEnabled = !mVideoEnabled;
                        mLocalVideoTrack.setMuted(!mVideoEnabled);
                        if (mLocalScreenTrack != null) {
                            mLocalScreenTrack.setMuted(!mVideoEnabled);
                            mEngine.muteTracks(Arrays.asList(mLocalScreenTrack, mLocalVideoTrack));
                        } else {
                            mEngine.muteTracks(Collections.singletonList(mLocalVideoTrack));
                        }
//                    if (mTrackWindowMgr != null) {
//                        mTrackWindowMgr.onTrackInfoMuted(mUserId);
//                    }
                        //onTrackInfoMuted(mUserId);
                        List<QNTrackInfo> localTrackListExcludeScreenTrack = new ArrayList<>(mLocalTrackList);
                        localTrackListExcludeScreenTrack.remove(mLocalScreenTrack);
                        addTrackInfo(mUserId, localTrackListExcludeScreenTrack);
                        onTrackInfoMuted(mUserId,localTrackListExcludeScreenTrack);
                    }
                    videoStatus.setText(mVideoEnabled ?  R.string.menus_video:R.string.menus_cancel_video);
                }else {
                    ToastUtils.show("支持人已禁止视频");
                }

                //return mVideoEnabled;
                break;
            case R.id.a:
                if (mEngine != null) {
                    mSpeakerEnabled = !mSpeakerEnabled;
                    mEngine.muteRemoteAudio(!mSpeakerEnabled);
                }
                //return mSpeakerEnabled;
                break;
            case R.id.aa:
                if (mEngine != null) {
                    mBeautyEnabled = !mBeautyEnabled;
                    QNBeautySetting beautySetting = new QNBeautySetting(0.5f, 0.5f, 0.5f);
                    beautySetting.setEnable(mBeautyEnabled);
                    mEngine.setBeauty(beautySetting);
                }
                //return mBeautyEnabled;
                break;
            case R.id.panel:
                mShowPannel = !mShowPannel;
                if (!mShowPannel) {
                    panel.setVisibility(View.GONE);
                } else {
                    panel.setVisibility(View.VISIBLE);
                }
        }
    }

    /**
     * 合流配置相关
     */
//    private class UserListAdapter extends RecyclerView.Adapter<ViewHolder> {
//        int[] mColor = {
//                Color.parseColor("#588CEE"),
//                Color.parseColor("#F8CF5F"),
//                Color.parseColor("#4D9F67"),
//                Color.parseColor("#F23A48")
//        };
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return new ViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_user, parent, false));
//        }
//
//        @Override
//        public void onBindViewHolder(final ViewHolder holder, int position) {
//            RTCUser rtcUser = mRoomUserList.getRoomUserByPosition(position);
//            String userId = rtcUser.getUserId();
//            holder.username.setText(userId);
//            holder.username.setCircleColor(mColor[position % 4]);
//            if (mChooseUser != null && mChooseUser.getUserId().equals(userId)) {
//                holder.itemView.setBackground(getResources().getDrawable(R.drawable.white_background));
//            } else {
//                holder.itemView.setBackgroundResource(0);
//            }
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mChooseUser = mRoomUserList.getRoomUserByPosition(holder.getAdapterPosition());
//                    mMergeLayoutConfigView.updateConfigInfo(mChooseUser);
//                    notifyDataSetChanged();
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return mRoomUserList.size();
//        }
//    }
//
//    private class ViewHolder extends RecyclerView.ViewHolder {
//        CircleTextView username;
//
//        private ViewHolder(View itemView) {
//            super(itemView);
//            username = (CircleTextView) itemView.findViewById(R.id.user_name_text);
//        }
//    }
    public void reset() {

        for (int var = 0; var < mUserTrackInfos.size(); var++) {
            if (!TextUtils.isEmpty(mUserTrackInfos.get(var).getUserId())) {
                removeTrackWindow(mUserTrackInfos.get(var).getUserId());
            }
        }

    }


    public static class UserTrackInfo {


        private String mUserId;
        private QNTrackInfo mQNAudioTrackInfo;
        private List<QNTrackInfo> mQNVideoTrackInfos = new ArrayList<>();
        private UserTrackView mUserTrackView;


        public UserTrackInfo(String mUserId) {
            this.mUserId = mUserId;
        }

        public String getUserId() {
            return mUserId;
        }

        public void setUserId(String mUserId) {
            this.mUserId = mUserId;
        }

        public QNTrackInfo getQNAudioTrackInfo() {
            return mQNAudioTrackInfo;
        }

        public void setQNAudioTrackInfo(QNTrackInfo mQNAudioTrackInfo) {
            this.mQNAudioTrackInfo = mQNAudioTrackInfo;
        }

        public List<QNTrackInfo> getQNVideoTrackInfos() {
            return mQNVideoTrackInfos;
        }

        public void setQNVideoTrackInfos(List<QNTrackInfo> mQNVideoTrackInfos) {
            this.mQNVideoTrackInfos = mQNVideoTrackInfos;
        }

        public UserTrackView getUserTrackView() {
            return mUserTrackView;
        }

        public void setUserTrackView(UserTrackView mUserTrackView) {
            this.mUserTrackView = mUserTrackView;
        }


        private void onAddTrackInfo(QNTrackInfo trackInfo) {
            if (QNTrackKind.AUDIO.equals(trackInfo.getTrackKind())) {
                mQNAudioTrackInfo = trackInfo;
            } else {
                mQNVideoTrackInfos.add(trackInfo);
            }

        }

        public void onAddTrackInfo(List<QNTrackInfo> trackInfos) {
            for (QNTrackInfo item : trackInfos) {
                onAddTrackInfo(item);
            }
        }

        public boolean onRemoveTrackInfo(List<QNTrackInfo> trackInfos) {
            for (QNTrackInfo item : trackInfos) {
                onRemoveTrackInfo(item, false);
            }
            return mQNAudioTrackInfo != null || !mQNVideoTrackInfos.isEmpty();
        }

        public void onRemoveTrackInfo(QNTrackInfo trackInfo, boolean notify) {
            if (QNTrackKind.AUDIO.equals(trackInfo.getTrackKind())) {
                mQNAudioTrackInfo = null;
            } else {
                mQNVideoTrackInfos.remove(trackInfo);
            }

        }
    }


}
