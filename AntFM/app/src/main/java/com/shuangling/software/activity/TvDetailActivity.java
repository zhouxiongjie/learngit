package com.shuangling.software.activity;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.player.IPlayer;
import com.aliyun.player.source.UrlSource;
import com.aliyun.vodplayerview.constants.PlayParameter;
import com.aliyun.vodplayerview.utils.ScreenUtils;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.RadioGroupAdapter;
import com.shuangling.software.adapter.RadioListAdapter;
import com.shuangling.software.adapter.RadioProgramListAdapter;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.dialog.ShareDialog;
import com.shuangling.software.entity.RadioDetail;
import com.shuangling.software.entity.RadioSet;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.service.IAudioPlayer;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.FloatWindowUtil;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import okhttp3.Call;

import static android.widget.ExpandableListView.PACKED_POSITION_TYPE_CHILD;
import static android.widget.ExpandableListView.PACKED_POSITION_TYPE_NULL;

@EnableDragToClose()
public class TvDetailActivity extends BaseAudioActivity implements Handler.Callback {


    public static final String TAG = "RadioDetailActivity";

    public static final int MSG_GET_RADIO_DETAIL = 0x00;

    public static final int MSG_COLLECT_CALLBACK = 0x01;

    public static final int MSG_UPDATE_COLLECT_STATUS = 0x02;

    public static final int MSG_GET_RADIO_LIST = 0x3;

    public static final int REQUEST_LOGIN = 0x04;

    private static final int SHARE_SUCCESS = 0x5;

    private static final int SHARE_FAILED = 0x6;


    private static final String DEFAULT_URL = "http://player.alicdn.com/video/aliyunmedia.mp4";
    @BindView(R.id.aliyunVodPlayerView)
    AliyunVodPlayerView aliyunVodPlayerView;
    @BindView(R.id.collect)
    ImageView collect;
    @BindView(R.id.orientation)
    FontIconView orientation;
    @BindView(R.id.selectChannel)
    LinearLayout selectChannel;
    @BindView(R.id.yesterday)
    RadioButton yesterday;
    @BindView(R.id.today)
    RadioButton today;
    @BindView(R.id.tomorrow)
    RadioButton tomorrow;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.contentList)
    ExpandableListView contentList;
    @BindView(R.id.channelName)
    TextView channelName;
    @BindView(R.id.programList)
    ListView programList;
    @BindView(R.id.categoryList)
    ListView categoryList;
    @BindView(R.id.selectChannelLayout)
    LinearLayout selectChannelLayout;
    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
//    @BindView(R.id.flow)
//    TextView flow;
//    @BindView(R.id.goOn)
//    TextView goOn;
//    @BindView(R.id.flowLayout)
//    RelativeLayout flowLayout;

    private Handler mHandler;
    //private Radio mRadio;
    private int mRadioId;
    private List<RadioSet> mRadioSetList;
    private RadioDetail mRadioDetail;
    private boolean mShowSelectChannel = false;
    private RadioGroupAdapter mRadioGroupAdapter;
    private RadioListAdapter mRadioListAdapter;

    private int mNetPlay;
    private int mNeedTipPlay;
    private boolean mNeedResumeAudioPlay=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setmOnServiceConnectionListener(new OnServiceConnectionListener() {
            @Override
            public void onServiceConnection(IAudioPlayer audioPlayer) {
                try{
                    if(audioPlayer.getPlayerState()==IPlayer.started||audioPlayer.getPlayerState()==IPlayer.paused){
                        audioPlayer.pause();
                        mNeedResumeAudioPlay=true;
                        FloatWindowUtil.getInstance().hideWindow();
                    }
                }catch (RemoteException e){

                }

            }
        });
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tv_detail);
        //CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        init();

    }

    private void init() {

        mHandler = new Handler(this);
        //mRadio = getIntent().getParcelableExtra("Radio");
        mRadioId = getIntent().getIntExtra("radioId", 0);
        activityTitle.setMoreAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRadioDetail != null) {


                    ShareDialog dialog = ShareDialog.getInstance(false,false);
                    dialog.setIsHideSecondGroup(true);
//                    dialog.setIsShowPosterButton(false);
//                    dialog.setIsShowReport(true);
//                    dialog.setIsShowCollect(false);
//                    dialog.setIsShowCopyLink(false);
//                    dialog.setIsShowFontSize(false);
//                    dialog.setIsShowRefresh(false);

                    dialog.setShareHandler(new ShareDialog.ShareHandler() {
                        @Override
                        public void onShare(String platform) {


                            String url;
                            if(User.getInstance()!=null){
                                url=ServerInfo.h5IP + "/lives/" + mRadioDetail.getChannel().getId()+"?from_user_id="+User.getInstance().getId()+"&from_url="+ServerInfo.h5IP + "/lives/" + mRadioDetail.getChannel().getId();
                            }else{
                                url=ServerInfo.h5IP + "/lives/" + mRadioDetail.getChannel().getId()+"?from_url="+ServerInfo.h5IP + "/lives/" + mRadioDetail.getChannel().getId();
                            }
                            showShare(platform,mRadioDetail.getChannel().getName(), mRadioDetail.getChannel().getDes(), mRadioDetail.getChannel().getLogo(), url);

                        }

                        @Override
                        public void poster() {

                        }

                        @Override
                        public void report() {

                        }

                        @Override
                        public void copyLink() {


                        }

                        @Override
                        public void refresh() {
                        }

                        @Override
                        public void collectContent() {

                        }
                    });
                    dialog.show(getSupportFragmentManager(), "ShareDialog");



                    //shareTest();
                }

            }
        });
        getRadioDetail();
        getRadioList();
        initAliyunPlayerView();

    }


    public void getRadioList() {


        String url = ServerInfo.serviceIP + ServerInfo.getRadioList;
        Map<String, String> params = new HashMap<>();
        params.put("type", "2");
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_RADIO_LIST;
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });


    }




    private void initAliyunPlayerView() {
        //保持屏幕敞亮
        ViewGroup.LayoutParams lp = aliyunVodPlayerView.getLayoutParams();
        lp.width = CommonUtils.getScreenWidth();
        lp.height = 9 * lp.width / 16;
        aliyunVodPlayerView.setLayoutParams(lp);
        aliyunVodPlayerView.setKeepScreenOn(true);
        PlayParameter.PLAY_PARAM_URL = DEFAULT_URL;
        String sdDir = CommonUtils.getStoragePrivateDirectory(Environment.DIRECTORY_MOVIES);
        aliyunVodPlayerView.setPlayingCache(false, sdDir, 60 * 60 /*时长, s */, 300 /*大小，MB*/);
        aliyunVodPlayerView.setTheme(AliyunVodPlayerView.Theme.Blue);
        //mAliyunVodPlayerView.setCirclePlay(true);
        aliyunVodPlayerView.setAutoPlay(true);

        if(mNetPlay==0){
            //每次提醒
            aliyunVodPlayerView.setShowFlowTip(true);
        }else{
            //提醒一次
            if(mNeedTipPlay==1) {
                aliyunVodPlayerView.setShowFlowTip(true);
            }else{
                aliyunVodPlayerView.setShowFlowTip(false);
            }
        }
        aliyunVodPlayerView.setOnModifyFlowTipStatus(new AliyunVodPlayerView.OnModifyFlowTipStatus() {
            @Override
            public void onChangeFlowTipStatus() {
                SharedPreferencesUtils.putIntValue(SettingActivity.NEED_TIP_PLAY, 0);
            }
        });


    }


    private void getRadioDetail() {


        String url = ServerInfo.serviceIP + ServerInfo.getRadioDetail;
        Map<String, String> params = new HashMap<>();
        params.put("channel_id", "" + mRadioId);

        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_RADIO_DETAIL;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }


    private void addRadioHistory() {


        String url = ServerInfo.serviceIP + ServerInfo.addRadioHistory;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + mRadioId);

        OkHttpUtils.put(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

            }

            @Override
            public void onFailure(Call call, Exception exception) {

            }
        });
    }

    public void collect() {
        if (mRadioDetail == null) {
            return;
        }
        String url = ServerInfo.serviceIP + ServerInfo.collect;
        Map<String, String> params = new HashMap<>();
        params.put("channel_id", "" + mRadioDetail.getChannel().getId());
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_COLLECT_CALLBACK;
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });

    }


    public void updateCollectStatus() {
        String url = ServerInfo.serviceIP + ServerInfo.getRadioDetail;
        Map<String, String> params = new HashMap<>();
        params.put("channel_id", "" + mRadioId);

        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_UPDATE_COLLECT_STATUS;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }


    private void setPlaySource(String url,String title) {
//        if ("localSource".equals(PlayParameter.PLAY_PARAM_TYPE)) {
//            AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
//            PlayParameter.PLAY_PARAM_URL = url;
//            alsb.setSource(PlayParameter.PLAY_PARAM_URL);
//            Uri uri = Uri.parse(PlayParameter.PLAY_PARAM_URL);
//            if ("rtmp".equals(uri.getScheme())) {
//                alsb.setTitle("");
//            }
//            AliyunLocalSource localSource = alsb.build();
//            aliyunVodPlayerView.setLocalSource(localSource);
//
//        }

        UrlSource urlSource = new UrlSource();
        urlSource.setUri(url);
        urlSource.setTitle(title);
        aliyunVodPlayerView.setLocalSource(urlSource);
    }


    private void updatePlayerViewMode() {
        if (aliyunVodPlayerView != null) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                //转为竖屏了。
                //显示状态栏
                //                if (!isStrangePhone()) {
                //                    getSupportActionBar().show();
                //                }

                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                aliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                //设置view的布局，宽高之类
                LinearLayout.LayoutParams aliVcVideoViewLayoutParams = (LinearLayout.LayoutParams) aliyunVodPlayerView
                        .getLayoutParams();
                aliVcVideoViewLayoutParams.height = (int) (ScreenUtils.getWidth(this) * 9.0f / 16);
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                //                if (!isStrangePhone()) {
                //                    aliVcVideoViewLayoutParams.topMargin = getSupportActionBar().getHeight();
                //                }
                activityTitle.setVisibility(View.VISIBLE);
                aliyunVodPlayerView.setBackBtnVisiable(View.INVISIBLE);

            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //转到横屏了。
                //隐藏状态栏
                if (!isStrangePhone()) {
                    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    aliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }

                //设置view的布局，宽高
                LinearLayout.LayoutParams aliVcVideoViewLayoutParams = (LinearLayout.LayoutParams) aliyunVodPlayerView
                        .getLayoutParams();
                aliVcVideoViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                //                if (!isStrangePhone()) {
                //                    aliVcVideoViewLayoutParams.topMargin = 0;
                //                }
                activityTitle.setVisibility(View.GONE);
                aliyunVodPlayerView.setBackBtnVisiable(View.VISIBLE);
            }

        }
    }


    private boolean isStrangePhone() {
        boolean strangePhone = "mx5".equalsIgnoreCase(Build.DEVICE)
                || "Redmi Note2".equalsIgnoreCase(Build.DEVICE)
                || "Z00A_1".equalsIgnoreCase(Build.DEVICE)
                || "hwH60-L02".equalsIgnoreCase(Build.DEVICE)
                || "hermes".equalsIgnoreCase(Build.DEVICE)
                || ("V4".equalsIgnoreCase(Build.DEVICE) && "Meitu".equalsIgnoreCase(Build.MANUFACTURER))
                || ("m1metal".equalsIgnoreCase(Build.DEVICE) && "Meizu".equalsIgnoreCase(Build.MANUFACTURER));

        return strangePhone;
    }


    @Override
    protected void onResume() {
        super.onResume();

        updatePlayerViewMode();
        if (aliyunVodPlayerView != null) {
            aliyunVodPlayerView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if(mNeedResumeAudioPlay){
//            try{

                FloatWindowUtil.getInstance().visibleWindow();
//                mAudioPlayer.start();
//            }catch (RemoteException e){
//
//            }

        }
        super.onDestroy();
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (aliyunVodPlayerView != null) {
            aliyunVodPlayerView.onStop();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updatePlayerViewMode();
    }


    @OnClick({R.id.collect, R.id.selectChannel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.collect:
                if (User.getInstance() != null) {
                    collect();
                } else {
                    Intent it=new Intent(this, NewLoginActivity.class);
                    it.putExtra("jump_url",ServerInfo.h5IP+"/lives/"+mRadioId);
                    startActivityForResult(it, REQUEST_LOGIN);
                }

                break;
            case R.id.selectChannel:
                mShowSelectChannel = !mShowSelectChannel;
                if (mShowSelectChannel) {

                    orientation.setText(getResources().getString(R.string.arrow_down));
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.select_channel_enter_anim);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            selectChannelLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            selectChannelLayout.clearAnimation();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    selectChannelLayout.startAnimation(animation);
                } else {
                    orientation.setText(getResources().getString(R.string.arrow_up));
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.select_channel_exit_anim);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            selectChannelLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            selectChannelLayout.clearAnimation();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    selectChannelLayout.startAnimation(animation);
                }
                break;

        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_RADIO_DETAIL:

                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {


                        mRadioDetail = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), RadioDetail.class);

                        channelName.setText(mRadioDetail.getChannel().getName());
                        if (mRadioDetail.getCollection() == 1) {
                            //collect.setTextColor(getResources().getColor(R.color.textColorEleven));
                            collect.setActivated(true);
                        } else {
                            //collect.setTextColor(CommonUtils.getThemeColor(this));
                            collect.setActivated(false);
                        }



                        RadioProgramListAdapter adapter = new RadioProgramListAdapter(this, mRadioDetail.getProgram_list().get(1));
                        adapter.setType(1);
                        adapter.setInPlayBean(mRadioDetail.getIn_play());
                        programList.setAdapter(adapter);
                        today.setChecked(true);
                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                if (checkedId == R.id.yesterday) {
                                    RadioProgramListAdapter adapter = new RadioProgramListAdapter(TvDetailActivity.this, mRadioDetail.getProgram_list().get(0));
                                    adapter.setType(1);
                                    programList.setAdapter(adapter);
                                } else if (checkedId == R.id.today) {
                                    RadioProgramListAdapter adapter = new RadioProgramListAdapter(TvDetailActivity.this, mRadioDetail.getProgram_list().get(1));
                                    adapter.setType(1);
                                    adapter.setInPlayBean(mRadioDetail.getIn_play());
                                    programList.setAdapter(adapter);
                                } else {
                                    RadioProgramListAdapter adapter = new RadioProgramListAdapter(TvDetailActivity.this, mRadioDetail.getProgram_list().get(2));
                                    adapter.setType(1);
                                    programList.setAdapter(adapter);
                                }
                            }
                        });
                        mNetPlay=SharedPreferencesUtils.getIntValue(SettingActivity.NET_PLAY,0);
                        mNeedTipPlay=SharedPreferencesUtils.getIntValue(SettingActivity.NEED_TIP_PLAY,0);
                        setPlaySource(mRadioDetail.getChannel().getStream(),mRadioDetail.getChannel().getName());


                        addRadioHistory();
//                        if (CommonUtils.getNetWorkType(this) == CommonUtils.NETWORKTYPE_MOBILE) {
//                            if (mNetPlay == 0) {
//                                //每次提醒
//                                flowLayout.setVisibility(View.VISIBLE);
//                                flow.setText("播放将消耗流量,请注意流量使用");
//
//                            } else {
//                                //提醒一次
//                                if (mNeedTipPlay == 1) {
//                                    flowLayout.setVisibility(View.VISIBLE);
//                                    flow.setText("播放将消耗流量,请注意流量使用");
//
//                                } else {
//                                    flowLayout.setVisibility(View.GONE);
//                                    setPlaySource(mRadioDetail.getChannel().getStream());
//                                }
//
//                            }
//                        } else {
//                            flowLayout.setVisibility(View.GONE);
//                            setPlaySource(mRadioDetail.getChannel().getStream());
//                        }

                    }


                } catch (Exception e) {

                }
                break;
            case MSG_COLLECT_CALLBACK:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show(jsonObject.getString("msg"));
                        if (jsonObject.getInteger("data") == 1) {
                            //collect.setTextColor(getResources().getColor(R.color.textColorEleven));
                            collect.setActivated(true);
                        } else {
                            //collect.setTextColor(CommonUtils.getThemeColor(this));
                            collect.setActivated(false);
                        }

                    }


                } catch (Exception e) {

                }
                break;
            case MSG_UPDATE_COLLECT_STATUS:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        mRadioDetail = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), RadioDetail.class);

                        if (mRadioDetail.getCollection() == 1) {
                            //collect.setTextColor(getResources().getColor(R.color.textColorEleven));
                            collect.setActivated(true);
                        } else {
                            //collect.setTextColor(CommonUtils.getThemeColor(this));
                            collect.setActivated(false);
                        }

                    }


                } catch (Exception e) {

                }
                break;
            case MSG_GET_RADIO_LIST:

                String result = (String) msg.obj;
                try {

                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        mRadioSetList = JSONArray.parseArray(jsonObject.getJSONArray("data").toJSONString(), RadioSet.class);

                        Iterator<RadioSet> iterator = mRadioSetList.iterator();
                        while (iterator.hasNext()) {
                            RadioSet radioSet = iterator.next();
                            if (radioSet.getList()==null||radioSet.getList().size()==0) {
                                iterator.remove();
                            }
                        }

                        if (mRadioGroupAdapter == null) {
                            mRadioGroupAdapter = new RadioGroupAdapter(this, mRadioSetList);
                            categoryList.setAdapter(mRadioGroupAdapter);
                            categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    mRadioGroupAdapter.setSelected(mRadioGroupAdapter.getItem(position));
                                    contentList.setSelectedGroup(position);
                                }
                            });
                        } else {
                            mRadioGroupAdapter.updateListView(mRadioSetList);
                        }


                        if (mRadioListAdapter == null) {
                            mRadioListAdapter = new RadioListAdapter(this, mRadioSetList);
                            contentList.setAdapter(mRadioListAdapter);
                            for (int i = 0; i < mRadioListAdapter.getGroupCount(); i++) {
                                contentList.expandGroup(i);
                            }

                            contentList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                                @Override
                                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                    return true;
                                }
                            });

                            contentList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                @Override
                                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                                    //选择了某台
//                                    mRadioId = mRadioSetList.get(groupPosition).getList().get(childPosition).getId();
//                                    getRadioDetail();
//
//                                    return true;


                                    if(childPosition<mRadioSetList.get(groupPosition).getList().size()){
                                        mRadioId = mRadioSetList.get(groupPosition).getList().get(childPosition).getId();
                                        getRadioDetail();

                                    }
                                    return true;

                                }
                            });

                        } else {
                            mRadioListAdapter.updateListView(mRadioSetList);
                        }


                        contentList.setOnScrollListener(new AbsListView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(AbsListView view, int scrollState) {


                                switch (scrollState) {
                                    // 当不滚动时
                                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                                        int flatPosition = contentList.getFirstVisiblePosition();
                                        // a. Flat list position -> Packed position
                                        long packedPosition = contentList.getExpandableListPosition(flatPosition);
                                        // b. Unpack packed position type
                                        int positionType = contentList.getPackedPositionType(packedPosition);
                                        //c. Unpack position values based on positionType
                                        // 如果positionType不是空类型,就是Group,或者Child
                                        if (positionType != PACKED_POSITION_TYPE_NULL) {
                                            // (Child类型时也有Group信息)
                                            int groupPosition = contentList.getPackedPositionGroup(packedPosition);
                                            // 如果是child类型,则取出childPosition
                                            mRadioGroupAdapter.setSelected(mRadioGroupAdapter.getItem(groupPosition));
                                            if (positionType == PACKED_POSITION_TYPE_CHILD) {
                                                //childPosition = getPackedPositionChild(packedPosition);
                                            }
                                        } else {
                                            Log.i("FooLabel", "positionType was NULL - header/footer?");
                                        }

                                        break;
                                }
                            }

                            @Override
                            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                            }
                        });

                        mRadioGroupAdapter.setSelected(mRadioGroupAdapter.getItem(0));


                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }


                break;
        }
        return false;
    }


    private void showShare(String platform,final String title, final String desc, final String logo, final String url) {
        final String cover;
        if(logo.startsWith("http://")){
            cover=logo.replace("http://","https://");
        }else{
            cover=logo;
        }
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setPlatform(platform);
        final Platform qq = ShareSDK.getPlatform(QQ.NAME);
        if (!qq.isClientValid()) {
            oks.addHiddenPlatform(QQ.NAME);
        }
        final Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
        if (!sina.isClientValid()) {
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            //自定义分享的回调想要函数
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                //点击新浪微博
                String chanel="1";
                if (SinaWeibo.NAME.equals(platform.getName())) {
                    chanel="2";
                    //限制微博分享的文字不能超过20
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                    paramsToShare.setText(title + url);
                } else if (QQ.NAME.equals(platform.getName())) {
                    chanel="3";
                    paramsToShare.setTitle(title);
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                    paramsToShare.setTitleUrl(url);
                    paramsToShare.setText(desc);

                } else if (Wechat.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(url);
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                    paramsToShare.setText(desc);

                    Log.d("ShareSDK", paramsToShare.toMap().toString());
                } else if (WechatMoments.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(url);
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                } else if (WechatFavorite.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(url);
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                }
                shareStatistics(chanel,""+mRadioDetail.getChannel().getId(),url);
            }
        });
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Message msg = Message.obtain();
                msg.what = SHARE_FAILED;
                msg.obj = arg2.getMessage();
                mHandler.sendMessage(msg);
            }
            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                Message msg = Message.obtain();
                msg.what = SHARE_SUCCESS;
                mHandler.sendMessage(msg);


            }
            @Override
            public void onCancel(Platform arg0, int arg1) {

            }
        });
        // 启动分享GUI
        oks.show(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_LOGIN && resultCode == RESULT_OK) {
            updateCollectStatus();
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }



    public void shareStatistics(String channel,String postId,String shardUrl) {

        String url = ServerInfo.serviceIP + ServerInfo.shareStatistics;
        Map<String, String> params = new HashMap<>();
        if(User.getInstance()!=null){
            params.put("user_id", ""+User.getInstance().getId());
        }
        params.put("channel", channel);
        params.put("post_id", postId);
        params.put("source_type", "3");
        params.put("type", "1");
        params.put("shard_url", shardUrl);
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {
                Log.i("test",response);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                Log.i("test",exception.toString());

            }
        });

    }

}
