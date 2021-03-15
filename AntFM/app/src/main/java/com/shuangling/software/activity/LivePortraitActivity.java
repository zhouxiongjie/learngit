package com.shuangling.software.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.UrlSource;
import com.aliyun.vodplayerview.utils.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hjq.toast.ToastUtils;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.LiveChatListAdapter;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.dialog.HotRangeListDialog;
import com.shuangling.software.dialog.InviteRangeListDialog;
import com.shuangling.software.dialog.LiveAwardDialog;
import com.shuangling.software.dialog.LiveGoodsDialog;
import com.shuangling.software.dialog.LiveMoreDialog;
import com.shuangling.software.dialog.LuckAwardDialog;
import com.shuangling.software.dialog.RedPacketDetailDialog;
import com.shuangling.software.dialog.RedPacketDialog;
import com.shuangling.software.dialog.RewardListDialog;
import com.shuangling.software.dialog.ShareDialog;
import com.shuangling.software.dialog.ShareLivePosterDialog01;
import com.shuangling.software.dialog.ViewerListDialog;
import com.shuangling.software.entity.ChatMessage;
import com.shuangling.software.entity.LiveGoodsInfo;
import com.shuangling.software.entity.LiveOnlineViewer;
import com.shuangling.software.entity.LiveRoomInfo01;
import com.shuangling.software.entity.RedPacketInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.liverewardgift.AnimMessage;
import com.shuangling.software.liverewardgift.LPAnimationManager;
import com.shuangling.software.network.MyEcho;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.service.IAudioPlayer;
import com.shuangling.software.utils.AsyncHttpURLConnection;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.FloatWindowUtil;
import com.shuangling.software.utils.GsdFastBlur;
import com.shuangling.software.utils.HeightProvider;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.TimeUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.xbright.lebwebrtcsdk.LEBWebRTCEvents;
import com.tencent.xbright.lebwebrtcsdk.LEBWebRTCParameters;
import com.tencent.xbright.lebwebrtcsdk.LEBWebRTCStatsReport;
import com.tencent.xbright.lebwebrtcsdk.LEBWebRTCSurfaceView;

import net.mrbin99.laravelechoandroid.EchoCallback;
import net.mrbin99.laravelechoandroid.EchoOptions;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import io.reactivex.functions.Consumer;
import okhttp3.Call;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.view.animation.Animation.INFINITE;
import static com.tencent.xbright.lebwebrtcsdk.LEBWebRTCView.SCALE_KEEP_ASPECT_CROP;
import static com.tencent.xbright.lebwebrtcsdk.LEBWebRTCView.SCALE_KEEP_ASPECT_FIT;


public class LivePortraitActivity extends BaseAudioActivity implements LEBWebRTCEvents {

    private static final String TAG = LivePortraitActivity.class.getSimpleName();


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
    LinearLayout inviteRange;
    @BindView(R.id.smallRedPacket)
    ImageView smallRedPacket;
    @BindView(R.id.giftContainer)
    LinearLayout giftContainer;
    @BindView(R.id.surface)
    SurfaceView surface;
    @BindView(R.id.chat_msg_list)
    RecyclerView chatMsgList;
    @BindView(R.id.more_msg_btn)
    ImageButton moreMsgBtn;
    @BindView(R.id.chatBtn)
    TextView chatBtn;
    @BindView(R.id.goods)
    RelativeLayout goods;
    @BindView(R.id.goodsNumber)
    TextView goodsNumber;
    @BindView(R.id.invite)
    ImageButton invite;
    @BindView(R.id.more)
    ImageButton more;
    @BindView(R.id.gift)
    ImageButton gift;
    @BindView(R.id.bottomBar)
    LinearLayout bottomBar;
    @BindView(R.id.chatInput)
    EditText chatInput;
    @BindView(R.id.chatInputLayout)
    LinearLayout chatInputLayout;
    @BindView(R.id.bottomLayout)
    FrameLayout bottomLayout;
    @BindView(R.id.background)
    ImageView background;
    @BindView(R.id.lebSurfaceView)
    LEBWebRTCSurfaceView lebSurfaceView;
    @BindView(R.id.activityContainer)
    LinearLayout activityContainer;
    @BindView(R.id.luckRedPacketStatus)
    TextView luckRedPacketStatus;
    @BindView(R.id.luckRedPacket)
    RelativeLayout luckRedPacket;
    @BindView(R.id.answerStatus)
    TextView answerStatus;
    @BindView(R.id.answer)
    RelativeLayout answer;
    @BindView(R.id.luckAwardStatus)
    TextView luckAwardStatus;
    @BindView(R.id.luckAward)
    RelativeLayout luckAward;
    @BindView(R.id.passwordRedPacketStatus)
    TextView passwordRedPacketStatus;
    @BindView(R.id.passwordRedPacket)
    RelativeLayout passwordRedPacket;
    @BindView(R.id.picture)
    SimpleDraweeView picture;
    @BindView(R.id.goods_name)
    TextView goodsName;
    @BindView(R.id.source)
    TextView source;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.see)
    TextView see;
    @BindView(R.id.showingGoods)
    LinearLayout showingGoods;
    @BindView(R.id.close)
    FontIconView close;
    @BindView(R.id.noAnchor)
    RelativeLayout noAnchor;
    @BindView(R.id.phrase01)
    TextView phrase01;
    @BindView(R.id.phrase02)
    TextView phrase02;
    @BindView(R.id.phrase03)
    TextView phrase03;
    @BindView(R.id.coming)
    TextView coming;
    @BindView(R.id.shakeRedPacket)
    ImageView shakeRedPacket;
    @BindView(R.id.close1)
    FontIconView close1;
    @BindView(R.id.head01)
    SimpleDraweeView head01;
    @BindView(R.id.name01)
    TextView name01;
    @BindView(R.id.leaveLayout)
    RelativeLayout leaveLayout;
    @BindView(R.id.panel)
    RelativeLayout panel;
    @BindView(R.id.fullCreenIcon)
    FontIconView fullCreenIcon;
    @BindView(R.id.fullCreen)
    RelativeLayout fullCreen;
    @BindView(R.id.playerLayout)
    RelativeLayout playerLayout;

    private int onlineViewer;

    private List<Long> selectedGoodsId;


    private Handler mMainHandler;
    private Timer timer;
    private LiveChatListAdapter mAdapter;
    private MyEcho echo;
    private String mStreamName;
    private String msgUrl = ServerInfo.live + "/v1/push_message_c";
    private String mRequestPullUrl = "http://webrtc.liveplay.myqcloud.com/webrtc/v1/pullstream";

    private HashMap<String, String> msgMap = new HashMap<>();


    private HeightProvider heightProvider;
    private int autoRotate = 0;

    private LiveRoomInfo01 mliveRoomInfo;
    private LiveGoodsInfo mLiveGoodsInfo;
    private LEBWebRTCParameters mLEBWebRTCParameters;
    private int mAudioFormat = LEBWebRTCParameters.OPUS;
    private int mScaleType = SCALE_KEEP_ASPECT_CROP;


    //播放器类型  0 阿里播放器  1 腾讯播放器
    private int playerType = 0;
    //播放器
    private AliPlayer mAliyunVodPlayer;
    private int mPlayerState = IPlayer.idle;

    boolean mFullScreen=false;

    private Runnable mSocketRunnable;
    private ExecutorService mExecutorService;
    private boolean mNeedResumeAudioPlay = false;

    private long lastSendTimeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setmOnServiceConnectionListener(new BaseAudioActivity.OnServiceConnectionListener() {
            @Override
            public void onServiceConnection(IAudioPlayer audioPlayer) {
                try {
                    if (audioPlayer.getPlayerState() == IPlayer.started || audioPlayer.getPlayerState() == IPlayer.paused) {
                        audioPlayer.pause();
                        mNeedResumeAudioPlay = true;
                        FloatWindowUtil.getInstance().hideWindow();
                    }
                } catch (RemoteException e) {
                }
            }
        });

        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_live_portrait);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this);

        LPAnimationManager.init(this);
        LPAnimationManager.addGiftContainer(giftContainer);

        mMainHandler = new Handler(Looper.getMainLooper());

        initLive();
        initChatView();


        autoRotate = ScreenUtils.getHeight(this) - ScreenUtils.getWidth(this);
        heightProvider = new HeightProvider(this).init().setHeightListener(new HeightProvider.HeightListener() {
            @Override
            public void onHeightChanged(int height) {
                long id=Thread.currentThread().getId();

                if (autoRotate == height) return;

                if (height > 200) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
                    layoutParams.setMargins(0, 0, 0, height);
                    bottomLayout.setLayoutParams(layoutParams);
                    bottomBar.setVisibility(View.GONE);
                    chatInputLayout.setVisibility(View.VISIBLE);
                } else {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
                    layoutParams.setMargins(0, 0, 0, 0);
                    bottomLayout.setLayoutParams(layoutParams);

                    bottomBar.setVisibility(View.VISIBLE);
                    chatInputLayout.setVisibility(View.GONE);
                }


            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        //blurLayout.startBlur();
    }


    private void initLive() {
        mStreamName = getIntent().getStringExtra("streamName");

        mSocketRunnable=new Runnable() {
            @Override
            public void run() {
                getLiveRoomDetail(mStreamName);
                initChatRoom();
            }
        };

        mExecutorService=Executors.newCachedThreadPool();
        mExecutorService.submit(mSocketRunnable);

    }


    private void initGoods(LiveRoomInfo01.RoomInfoBean liveRoomInfo) {

        String url = ServerInfo.shop + "/v1/original_scenes/" + liveRoomInfo.getId() + "/c_goods";

        Map<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("page_size", "" + Integer.MAX_VALUE);
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {


            @Override
            public void onResponse(Call call, String response) throws IOException {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mLiveGoodsInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), LiveGoodsInfo.class);

                        if (mLiveGoodsInfo != null && mLiveGoodsInfo.getData().size() > 0) {

                            List<LiveGoodsInfo.DataBean> dataBeans = mLiveGoodsInfo.getData();
                            LiveGoodsInfo.DataBean goods = null;
                            for (int i = 0; i < dataBeans.size(); i++) {
                                if (dataBeans.get(i).getIn_explanation() == 1) {
                                    goods = dataBeans.get(i);
                                    break;
                                }
                            }
                            final LiveGoodsInfo.DataBean liveShowingGoods = goods;

                            if (liveShowingGoods != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            goodsNumber.setVisibility(View.VISIBLE);
                                            goodsNumber.setText(""+mLiveGoodsInfo.getData().size());
                                            showingGoods.setVisibility(View.VISIBLE);

                                            String pictureUrl = liveShowingGoods.getMerchant_goods() != null ? liveShowingGoods.getMerchant_goods().getPict_url() : "";
                                            if (!TextUtils.isEmpty(pictureUrl)) {
                                                Uri uri = Uri.parse(pictureUrl);
                                                ImageLoader.showThumb(uri, picture, CommonUtils.dip2px(56), CommonUtils.dip2px(56));
                                            }

                                            //点击图片
                                            see.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
//                                                    if(onItemClickListener!=null){
//                                                        onItemClickListener.onItemClick(goodsBean);
//                                                    }
                                                    if (liveShowingGoods.getMerchant_goods() != null) {
                                                        Uri uri = Uri.parse(liveShowingGoods.getMerchant_goods().getRedirect_url());
                                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });


                                            if (liveShowingGoods.getMerchant_goods() != null) {
                                                goodsName.setText(liveShowingGoods.getMerchant_goods().getGoods_name());
                                                price.setText("￥" + liveShowingGoods.getMerchant_goods().getAfter_coupon_price());
                                                switch (liveShowingGoods.getMerchant_goods().getSource()) {
                                                    case 0: {
                                                        source.setText("自定义");
                                                    }
                                                    break;
                                                    case 1:
                                                        source.setText("来自淘宝");
                                                        break;
                                                    case 2:
                                                        source.setText("来自京东");
                                                        break;
                                                    case 3:
                                                        source.setText("来自拼多多");
                                                        break;
                                                    default:
                                                        break;

                                                }
                                            }

                                        } catch (Exception e) {

                                        }

                                    }
                                });
                                mMainHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            showingGoods.setVisibility(View.GONE);
                                        } catch (Exception e) {

                                        }

                                    }
                                }, 5000);
                            }

                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    goodsNumber.setVisibility(View.GONE);
                                }
                            });

                        }

                    }
                } catch (Exception e) {

                }


            }

            @Override
            public void onFailure(Call call, Exception e) {

            }
        });
    }

    private void getLiveRoomDetail(String streamName) {
        String url = ServerInfo.live + "/v1/consumer/rooms/" + streamName;
        Map<String, String> params = new HashMap<>();


        OkHttpUtils.get(url, params, new OkHttpCallback(this) {


            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mliveRoomInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), LiveRoomInfo01.class);

                        if (mliveRoomInfo != null) {

                            getOnlineUsers(mliveRoomInfo);
                            getChatHistory(mliveRoomInfo);
                            initGoods(mliveRoomInfo.getRoom_info());

                            msgMap.put("room_id", "" + (mliveRoomInfo.getRoom_info() != null ? mliveRoomInfo.getRoom_info().getId() : ""));//直播间ID
                            msgMap.put("user_id", User.getInstance() != null ? User.getInstance().getId() + "" : "");//用户ID
                            msgMap.put("message", "");//消息内容
                            msgMap.put("type", "2");//发布端类型：1.主持人   2：用户    3:通知关注  4：通知进入直播间
                            msgMap.put("stream_name", mStreamName);//播间推流ID
                            msgMap.put("nick_name", User.getInstance() != null ? User.getInstance().getNickname() : "");//昵称
                            msgMap.put("message_type", "1");//消息类型 1.互动消息  2.直播状态更新消息  3.删除消息  4.题目 5.菜单设置 6图文保存  默认1
                            msgMap.put("user_logo", User.getInstance() != null ? User.getInstance().getAvatar() : "");


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    LiveRoomInfo01.RoomInfoBean roomInfoBean = mliveRoomInfo.getRoom_info();
                                    if (roomInfoBean != null) {

                                        if (roomInfoBean.getState() == 3) {
                                            //已结束
                                            leaveLayout.setVisibility(View.VISIBLE);
                                            panel.setVisibility(View.GONE);
                                            surface.setVisibility(View.GONE);
                                            playerLayout.setVisibility(View.GONE);

                                            if (roomInfoBean.getAnchor() != null) {
                                                if (!TextUtils.isEmpty(roomInfoBean.getAnchor().getLogo())) {
                                                    Uri uri = Uri.parse(roomInfoBean.getAnchor().getLogo());
                                                    ImageLoader.showThumb(uri, head01, CommonUtils.dip2px(70), CommonUtils.dip2px(70));
                                                }
                                                name01.setText(roomInfoBean.getAnchor().getName());
                                            }

                                        } else {
                                            ////显示主播头像、名称
                                            leaveLayout.setVisibility(View.GONE);
                                            panel.setVisibility(View.VISIBLE);
                                            if (roomInfoBean.getAnchor() != null) {
                                                if (!TextUtils.isEmpty(roomInfoBean.getAnchor().getLogo())) {
                                                    Uri uri = Uri.parse(roomInfoBean.getAnchor().getLogo());
                                                    ImageLoader.showThumb(uri, head, CommonUtils.dip2px(32), CommonUtils.dip2px(32));
                                                    ImageLoader.showThumb(uri, head01, CommonUtils.dip2px(70), CommonUtils.dip2px(70));
                                                }
                                                name.setText(roomInfoBean.getAnchor().getName());
                                                name01.setText(roomInfoBean.getAnchor().getName());

                                            }
                                            //毛玻璃封面

                                            Uri uri = Uri.parse(roomInfoBean.getCover_url()+CommonUtils.getOssResize(CommonUtils.getScreenWidth()/6,CommonUtils.getScreenHeight()/6));
//                                        background.setImageURI(uri);
                                            Glide.with(getContext()).asBitmap().load(uri).into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                    long id=Thread.currentThread().getId();
                                                    Bitmap bp = GsdFastBlur.fastblur(resource, 99);
                                                    background.setImageBitmap(bp);
                                                }
                                            });

                                            praiseNumber.setText("本场热度" + CommonUtils.getShowNumber(roomInfoBean.getHot_index()));


                                            if (mliveRoomInfo.getRoom_info() != null && mliveRoomInfo.getRoom_info().getInvite_rewards() == 1) {
                                                smallRedPacket.setVisibility(View.VISIBLE);
                                            }

                                            //显示活动
                                            List<LiveRoomInfo01.RoomInfoBean.MenusBean> menus = roomInfoBean.getMenus();
                                            //activityContainer.removeAllViews();
                                            for (int i = 0; menus != null && i < menus.size(); i++) {

                                                if (menus.get(i).getShowtype() == 6 && menus.get(i).getUsing() == 1) {
                                                    //拼手气红包
                                                    luckRedPacket.setVisibility(View.VISIBLE);
                                                    getRedPacketRecord();

                                                } else if (menus.get(i).getShowtype() == 5 && menus.get(i).getUsing() == 1) {
                                                    //直播答题4.1版本不做
                                                    //answer.setVisibility(View.VISIBLE);
                                                } else if (menus.get(i).getShowtype() == 15 && menus.get(i).getUsing() == 1) {
                                                    //幸运抽奖
                                                    luckAward.setVisibility(View.VISIBLE);
                                                } else if (menus.get(i).getShowtype() == 16 && menus.get(i).getUsing() == 1) {
                                                    //口令红包
                                                    passwordRedPacket.setVisibility(View.VISIBLE);
                                                } else if (menus.get(i).getShowtype() == 14 && menus.get(i).getUsing() == 1) {
                                                    //邀请榜
                                                    inviteRange.setVisibility(View.VISIBLE);
                                                } else if (menus.get(i).getShowtype() == 17 && menus.get(i).getUsing() == 1) {
                                                    //热度榜
                                                    hotRange.setVisibility(View.VISIBLE);
                                                } else if (menus.get(i).getShowtype() == 10 && menus.get(i).getUsing() == 1) {
                                                    //礼物
                                                    gift.setVisibility(View.VISIBLE);
                                                } else if (menus.get(i).getShowtype() == 13 && menus.get(i).getUsing() == 1) {
                                                    //菱选好物
                                                    goods.setVisibility(View.VISIBLE);
                                                }


                                            }


                                            //if(true){
                                            if (mliveRoomInfo.getRoom_info().getShow_model() == 2) {
                                                //竖屏模式
                                                fullCreen.setVisibility(View.GONE);
                                            } else {
                                                //竖横屏
                                                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) playerLayout.getLayoutParams();
                                                lp.height = CommonUtils.getScreenWidth() * 9 / 16;
                                                lp.gravity = Gravity.TOP;
                                                lp.topMargin = CommonUtils.dip2px(200);
                                                playerLayout.setLayoutParams(lp);

                                                fullCreen.setVisibility(View.VISIBLE);
                                                fullCreenIcon.setText(R.string.full_screen);
                                            }

                                            if (mliveRoomInfo.getRoom_info().getLive_driver().equals("tencent") && mliveRoomInfo.getRoom_info().getIs_rtslive() == 2) {
                                                playerType = 1;
                                                //腾讯快直播

                                                initLeb();


                                            } else {

                                                playerType = 0;
                                                //使用阿里播放器播放

                                                //initAliyunPlayerView();
                                                getAuthKey("" + mliveRoomInfo.getRoom_info().getId());


                                            }

                                            if (roomInfoBean.getStream_state() == 0) {
                                                //主播离开

                                                noAnchor.setVisibility(View.VISIBLE);
                                                lebSurfaceView.setVisibility(View.INVISIBLE);
                                                surface.setVisibility(View.INVISIBLE);

                                                if(fullCreen.getVisibility()==View.VISIBLE){
                                                    fullCreen.setVisibility(View.INVISIBLE);
                                                }

                                            } else {
                                                //
                                                noAnchor.setVisibility(View.INVISIBLE);

                                                if (playerType == 0) {
                                                    lebSurfaceView.setVisibility(View.INVISIBLE);
                                                    surface.setVisibility(View.VISIBLE);
                                                } else {
                                                    lebSurfaceView.setVisibility(View.VISIBLE);
                                                    surface.setVisibility(View.INVISIBLE);
                                                }
                                                if(fullCreen.getVisibility()==View.INVISIBLE){
                                                    fullCreen.setVisibility(View.VISIBLE);
                                                }

                                            }
                                        }


                                    }


                                }
                            });

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("test",e.getMessage());
                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test", exception.toString());

            }
        });


    }


    private void getOnlineUsers(LiveRoomInfo01 liveRoomInfo) {
        //String url = ServerInfo.live + "/v1/rooms/"+roomId+"/online_users";
        String url = ServerInfo.live + "/v1/play_users";
        Map<String, String> params = new HashMap<>();
        params.put("room_id",""+liveRoomInfo.getRoom_info()!=null?""+liveRoomInfo.getRoom_info().getId():"");
        params.put("user_id",User.getInstance()!=null?""+User.getInstance().getId():"");
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {


            @Override
            public void onResponse(Call call, String response) throws IOException {

                Log.e("test", response);
//                try {
//                    JSONObject jsonObject = JSONObject.parseObject(response);
//                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
//
//
//
//                        List<LiveOnlineViewer.MsgBean.AvatarListBean> liveOnlineViewer = JSON.parseArray(jsonObject.getJSONArray("data").toString(), LiveOnlineViewer.MsgBean.AvatarListBean.class);
//                        if (liveOnlineViewer != null&&liveOnlineViewer.size()>0) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        viewerContainer.removeAllViews();
//                                        for (int i = 0; liveOnlineViewer != null && i < liveOnlineViewer.size(); i++) {
//                                            String avatar = liveOnlineViewer.get(i).getAvatar();
//                                            SimpleDraweeView head = new SimpleDraweeView(LivePortraitActivity.this);
//                                            GenericDraweeHierarchy hierarchy = head.getHierarchy();
//                                            hierarchy.setPlaceholderImage(R.drawable.ic_user3);
//                                            head.setAspectRatio(1f);
//                                            RoundingParams roundingParams = head.getHierarchy().getRoundingParams();
//                                            if (roundingParams == null) {
//                                                roundingParams = new RoundingParams();
//                                            }
//                                            roundingParams.setRoundAsCircle(true);
//                                            head.getHierarchy().setRoundingParams(roundingParams);
//                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonUtils.dip2px(30), CommonUtils.dip2px(30));
//                                            params.rightMargin = CommonUtils.dip2px(5);
//                                            viewerContainer.addView(head, params);
//                                            Uri uri = Uri.parse(avatar);
//                                            ImageLoader.showThumb(uri, head, CommonUtils.dip2px(30), CommonUtils.dip2px(30));
//
//                                        }
//
//                                    } catch (Exception e) {
//
//                                    }
//
//                                }
//                            });
//
//
//                        }
//
//
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test", exception.toString());

            }
        });


    }

    //快直播初始化
    private void initLeb() {
        mLEBWebRTCParameters = new LEBWebRTCParameters();
        //webrtc://5664.liveplay.myqcloud.com/live/5664_harchar1
        // mLEBWebRTCParameters.setStreamUrl("webrtc://5664.liveplay.myqcloud.com/live/5664_harchar1");
        mLEBWebRTCParameters.setStreamUrl(mliveRoomInfo.getRoom_info().getRts_pull_url());
        mLEBWebRTCParameters.setLoggingSeverity(LEBWebRTCParameters.LOG_NONE);
        mLEBWebRTCParameters.setLoggable((String tag, int level, String message) -> {
            final String t = "[lebwebrtc]" + tag;
            switch (level) {
                case LEBWebRTCParameters.LOG_VERBOSE:
                    Log.v(t, message);
                    break;
                case LEBWebRTCParameters.LOG_INFO:
                    Log.i(t, message);
                    break;
                case LEBWebRTCParameters.LOG_WARNING:
                    Log.w(t, message);
                    break;
                case LEBWebRTCParameters.LOG_ERROR:
                    Log.e(t, message);
                    break;
                default:
                    Log.i(t, message);
                    break;
            }
        });
        mLEBWebRTCParameters.enableHwDecode(true);
        mLEBWebRTCParameters.disableEncryption(false);
        mLEBWebRTCParameters.enableSEICallback(false);
        mLEBWebRTCParameters.setConnectionTimeOutInMs(5000);//5s
        mLEBWebRTCParameters.setStatsReportPeriodInMs(1000);
        mLEBWebRTCParameters.setAudioFormat(mAudioFormat);

        lebSurfaceView.initilize(mLEBWebRTCParameters, LivePortraitActivity.this);
        lebSurfaceView.setScaleType(mScaleType);
        lebSurfaceView.startPlay();
    }


//    private void initAliyunPlayerView() {
//        //保持屏幕敞亮
//        aliyunVodPlayerView.setKeepScreenOn(true);
//        String sdDir = CommonUtils.getStoragePrivateDirectory(Environment.DIRECTORY_MOVIES);
//        aliyunVodPlayerView.setPlayingCache(false, sdDir, 60 * 60 /*时长, s */, 300 /*大小，MB*/);
//        aliyunVodPlayerView.setTheme(AliyunVodPlayerView.Theme.Blue);
//        //aliyunVodPlayerView.setCirclePlay(true);
//        //aliyunVodPlayerView.setAutoPlay(true);
////        aliyunVodPlayerView.setReferer(ServerInfo.h5IP);
//        aliyunVodPlayerView.setOnPreparedListener(new IPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared() {
//                //准备完成触发
//            }
//        });
//        aliyunVodPlayerView.setOnErrorListener(new IPlayer.OnErrorListener() {
//            @Override
//            public void onError(ErrorInfo errorInfo) {
//                aliyunVodPlayerView.pause();
//                aliyunVodPlayerView.setVisibility(View.GONE);
//
//            }
//        });
//        aliyunVodPlayerView.setOnModifyFlowTipStatus(new AliyunVodPlayerView.OnModifyFlowTipStatus() {
//            @Override
//            public void onChangeFlowTipStatus() {
//                SharedPreferencesUtils.putIntValue(SettingActivity.NEED_TIP_PLAY, 0);
//            }
//        });
//    }

    /**
     * 初始化播放器
     */
    private void initAliVcPlayer(String url) {
        SurfaceHolder holder = surface.getHolder();
        //增加surfaceView的监听
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (mAliyunVodPlayer != null) {
                    mAliyunVodPlayer.setDisplay(surfaceHolder);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width,
                                       int height) {
                if (mAliyunVodPlayer != null) {
                    mAliyunVodPlayer.redraw();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            }
        });
        mAliyunVodPlayer = AliPlayerFactory.createAliPlayer(getApplicationContext());
        //设置准备回调
        mAliyunVodPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
            }
        });
        mAliyunVodPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FILL);
        //播放器出错监听
        mAliyunVodPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
            }
        });
//播放器加载回调
        mAliyunVodPlayer.setOnLoadingStatusListener(new IPlayer.OnLoadingStatusListener() {
            @Override
            public void onLoadingBegin() {
            }

            @Override
            public void onLoadingProgress(int i, float v) {
            }

            @Override
            public void onLoadingEnd() {
            }
        });
        //播放结束
        mAliyunVodPlayer.setOnCompletionListener(new IPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
            }
        });
        mAliyunVodPlayer.setOnInfoListener(new IPlayer.OnInfoListener() {
            @Override
            public void onInfo(InfoBean infoBean) {
            }
        });
        //播放信息监听
        mAliyunVodPlayer.setOnInfoListener(new IPlayer.OnInfoListener() {
            @Override
            public void onInfo(InfoBean infoBean) {
            }
        });
        //切换清晰度结果事件
        mAliyunVodPlayer.setOnTrackChangedListener(new IPlayer.OnTrackChangedListener() {
            @Override
            public void onChangedSuccess(TrackInfo trackInfo) {
            }

            @Override
            public void onChangedFail(TrackInfo trackInfo, ErrorInfo errorInfo) {
            }
        });
        mAliyunVodPlayer.setOnStateChangedListener(new IPlayer.OnStateChangedListener() {
            @Override
            public void onStateChanged(int i) {
                mPlayerState = i;
            }
        });
//seek结束事件
        mAliyunVodPlayer.setOnSeekCompleteListener(new IPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete() {
            }
        });
//第一帧显示
        mAliyunVodPlayer.setOnRenderingStartListener(new IPlayer.OnRenderingStartListener() {
            @Override
            public void onRenderingStart() {
            }
        });
        mAliyunVodPlayer.setDisplay(surface.getHolder());
        mAliyunVodPlayer.setAutoPlay(true);
//        AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
//        alsb.setSource(url);
//        Uri uri = Uri.parse(url);
//        if ("rtmp".equals(uri.getScheme())) {
//            alsb.setTitle("");
//        }
//        AliyunLocalSource localSource = alsb.build();
//        mAliyunVodPlayer.prepareAsync(localSource);
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(url);
        //urlSource.setTitle(title);
        mAliyunVodPlayer.setDataSource(urlSource);
        mAliyunVodPlayer.prepare();
    }


    private void getAuthKey(String roomId) {
        String url = ServerInfo.live + ServerInfo.getRtsAuthKey;
        Map<String, String> params = new HashMap<>();
        params.put("room_id", roomId);
        params.put("type", "play");
        //params.put("suffix",".m3u8");
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        final String key = jsonObject.getString("data");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //setPlaySource(mUrl + "?" + key);
                                    //setPlaySource(mliveRoomInfo.getRoom_info().getRts_pull_url() + "?" + key);
                                    initAliVcPlayer(mliveRoomInfo.getRoom_info().getRts_pull_url() + "?" + key);
                                } catch (Exception e) {
                                    e.printStackTrace();
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

//    private void setPlaySource(String url) {
//        UrlSource urlSource = new UrlSource();
//        urlSource.setUri(url);
//        aliyunVodPlayerView.setLocalSource(urlSource);
//    }


    private void initChatView() {

        if (mAdapter == null) {
            mAdapter = new LiveChatListAdapter(this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setStackFromEnd(true);
            linearLayoutManager.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, Integer.MIN_VALUE);
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


    }


    private void initChatRoom() {


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


        echo.channel(mStreamName)
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
                            } else if (msg.getMessageType() == 17 || msg.getMessageType() == 16) {
                                //红包活动结束/开始
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

                            } else if (msg.getMessageType() == 5) {
                                //菜单状态变化
                                JSONObject jo = JSON.parseObject(args[1].toString());
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (jo.getJSONObject("msg").getInteger("showtype") == 6) {
                                            if (jo.getJSONObject("msg").getString("using").equals("0")) {
                                                luckRedPacket.setVisibility(View.GONE);
                                            } else {
                                                luckRedPacket.setVisibility(View.VISIBLE);
                                                getRedPacketRecord();
                                            }
                                        } else if (jo.getJSONObject("msg").getInteger("showtype") == 15) {
                                            if (jo.getJSONObject("msg").getString("using").equals("0")) {
                                                luckAward.setVisibility(View.GONE);
                                            } else {
                                                luckAward.setVisibility(View.VISIBLE);
                                            }
                                        } else if (jo.getJSONObject("msg").getInteger("showtype") == 16) {
                                            if (jo.getJSONObject("msg").getString("using").equals("0")) {
                                                passwordRedPacket.setVisibility(View.GONE);
                                            } else {
                                                passwordRedPacket.setVisibility(View.VISIBLE);
                                            }
                                        } else if (jo.getJSONObject("msg").getInteger("showtype") == 14) {
                                            if (jo.getJSONObject("msg").getString("using").equals("0")) {
                                                inviteRange.setVisibility(View.GONE);
                                            } else {
                                                inviteRange.setVisibility(View.VISIBLE);
                                            }
                                        } else if (jo.getJSONObject("msg").getInteger("showtype") == 17) {
                                            if (jo.getJSONObject("msg").getString("using").equals("0")) {
                                                hotRange.setVisibility(View.GONE);
                                            } else {
                                                hotRange.setVisibility(View.VISIBLE);
                                            }
                                        } else if (jo.getJSONObject("msg").getInteger("showtype") == 10) {
                                            if (jo.getJSONObject("msg").getString("using").equals("0")) {
                                                gift.setVisibility(View.GONE);
                                            } else {
                                                gift.setVisibility(View.VISIBLE);
                                            }
                                        } else if (jo.getJSONObject("msg").getInteger("showtype") == 13) {
                                            if (jo.getJSONObject("msg").getString("using").equals("0")) {
                                                goods.setVisibility(View.GONE);
                                            } else {
                                                goods.setVisibility(View.VISIBLE);
                                            }
                                        }


                                    }
                                });


                            } else if (msg.getMessageType() == 2) {
                                //
                                if (msg.getMsg().equals("关闭直播间")) {
                                    //直播间结束
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            leaveLayout.setVisibility(View.VISIBLE);
                                            panel.setVisibility(View.GONE);
                                            playerLayout.setVisibility(View.GONE);

                                            if (mAliyunVodPlayer != null) {
                                                mAliyunVodPlayer.stop();
                                                mAliyunVodPlayer.release();
                                                mAliyunVodPlayer = null;
                                            }
                                            if (playerType == 1) {
                                                lebSurfaceView.release();
                                            }
                                        }
                                    });


                                }

                            }

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
                        //praiseNumber.setText(CommonUtils.getShowNumber(heartNum) + "本场点赞");
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    praiseNumber.setText("本场热度" + CommonUtils.getShowNumber(heartNum));
                                }catch (Exception e){

                                }

                            }
                        });

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
                                    onlineViewer = msgBean.getOnline_count();
                                    viewerNumber.setText("" + onlineViewer);
                                    viewerContainer.removeAllViews();
                                    for (int i = 0; msgBean.getAvatar_list() != null && i < msgBean.getAvatar_list().size(); i++) {
                                        String avatar = msgBean.getAvatar_list().get(i).getAvatar();
                                        SimpleDraweeView head = new SimpleDraweeView(LivePortraitActivity.this);
                                        GenericDraweeHierarchy hierarchy = head.getHierarchy();
                                        hierarchy.setPlaceholderImage(R.drawable.ic_user3);
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
                    //Log.v("SL", e.getMessage());
                    Log.i("SL", e.getMessage());
                }
            }
        }).listen("ExpandActivityToggleEvent", new EchoCallback() {
            @Override
            public void call(Object... args) {
                // Event thrown.
                Log.i("test", "args");

                try {

                    String str = args[1].toString();
                    String aa = str;
                    JSONObject jo = JSON.parseObject(args[1].toString());
                    if (mliveRoomInfo != null && mliveRoomInfo.getRoom_info() != null) {
                        if (mliveRoomInfo.getRoom_info().getExpand_activities().size() > 0) {
                            mliveRoomInfo.getRoom_info().getExpand_activities().get(0).setUrl(jo.getString("url"));
                        }
                    }


                } catch (Exception e) {
                    Log.v("SL", e.getMessage());
                }
            }
        }).listen("FlowChangeEvent", new EchoCallback() {
            @Override
            public void call(Object... args) {
                // Event thrown.
                Log.i("test", "args");

                try {

                    String str = args[1].toString();
                    JSONObject jo = JSON.parseObject(str);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (jo.getString("state").equals("0")) {
                                //主播离开
                                noAnchor.setVisibility(View.VISIBLE);
                                lebSurfaceView.setVisibility(View.INVISIBLE);
                                surface.setVisibility(View.INVISIBLE);
                                if(fullCreen.getVisibility()==View.VISIBLE){
                                    fullCreen.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                noAnchor.setVisibility(View.INVISIBLE);
                                if (playerType == 0) {
                                    lebSurfaceView.setVisibility(View.INVISIBLE);
                                    surface.setVisibility(View.VISIBLE);
                                } else {
                                    lebSurfaceView.setVisibility(View.VISIBLE);
                                    surface.setVisibility(View.INVISIBLE);
                                }
                                if(fullCreen.getVisibility()==View.INVISIBLE){
                                    fullCreen.setVisibility(View.VISIBLE);
                                }

                            }
                        }
                    });


                } catch (Exception e) {
                    Log.v("SL", e.getMessage());
                }
            }
        }).listen("UserJoinEvent", new EchoCallback() {
            @Override
            public void call(Object... args) {
                // Event thrown.
                Log.i("test", "args");

                try {

                    String str = args[1].toString();
                    JSONObject jo = JSON.parseObject(str);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            coming.setVisibility(View.VISIBLE);
                            coming.setText(jo.getString("user_name") + " 来了");
                            mMainHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    coming.setVisibility(View.INVISIBLE);
                                }
                            }, 2000);
                        }
                    });


                } catch (Exception e) {
                    Log.v("SL", e.getMessage());
                }
            }
        });


    }


    private void getChatHistory(LiveRoomInfo01 liveRoomInfo) {
        String url = ServerInfo.live + "/v3/chats_history";
        Map<String, String> params = new HashMap<>();
        params.put("room_id", "" + (liveRoomInfo.getRoom_info() != null ? liveRoomInfo.getRoom_info().getId() : ""));
        params.put("page", "1");
        params.put("page_size", "5");
        params.put("state", "1");
        OkHttpUtils.get(url, params, new OkHttpCallback(LivePortraitActivity.this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        List<ChatMessage> msgs = JSON.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), ChatMessage.class);
                        Collections.reverse(msgs);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    showMsgs(msgs);
                                } catch (Exception e) {

                                }

                            }
                        });

                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                Log.e("test", exception.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
//        if(mLebInitialized&&lebSurfaceView.getVisibility()==View.VISIBLE){
//            lebSurfaceView.startPlay();
//        }
        if (mAliyunVodPlayer != null) {
            if (mPlayerState == IPlayer.paused || mPlayerState == IPlayer.prepared) {
                mAliyunVodPlayer.start();
            }
        }
        if (playerType == 1) {
            lebSurfaceView.startPlay();
        }

    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();
        if (playerType == 1) {
            lebSurfaceView.stopPlay();
        }

    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop");
        super.onStop();
        //blurLayout.pauseBlur();
//        if(lebSurfaceView.getVisibility()==View.VISIBLE){
//            lebSurfaceView.stopPlay();
//        }
        // 可以不调signalStop(), 后台在连接断开后会保底停止下发数据和计费
        //signalingStop();
        if (mAliyunVodPlayer != null) {
            if (mPlayerState == IPlayer.started || mPlayerState == IPlayer.prepared) {
                mAliyunVodPlayer.pause();
            }
        }
        if (playerType == 1) {
            lebSurfaceView.stopPlay();
        }

    }


    @Override
    protected void onDestroy() {
        Log.i("TG","onDestroy");
        super.onDestroy();
        if(mExecutorService!=null){
            mExecutorService.shutdownNow();
        }

        LPAnimationManager.release();
        shakeRedPacket.clearAnimation();
        if (echo != null) {
            echo.disconnect();
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (mAliyunVodPlayer != null) {
            mAliyunVodPlayer.stop();
            mAliyunVodPlayer.release();
            mAliyunVodPlayer = null;
        }

        if (playerType == 1) {
            lebSurfaceView.release();
        }

//        if (mSubThreadHandler != null) {
//            mSubThreadHandler.getLooper().quit();
//        }
//        if(lebSurfaceView.getVisibility()==View.VISIBLE) {
//            lebSurfaceView.release();
//        }
        heightProvider.dismiss();

    }



    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                sendMsg(chatInput.getText().toString());
                CommonUtils.hideInput(this);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() ==  MotionEvent.ACTION_DOWN ) {
            View view = getCurrentFocus();
            CommonUtils.hideInput(this);
        }
        return super.dispatchTouchEvent(ev);
    }



    private void sendMsg(String msg) {
        if (TextUtils.isEmpty(msg)) return;
        if (User.getInstance() == null) {
            Intent it = new Intent(this, NewLoginActivity.class);
            startActivity(it);
            return;
        }
        long timecurrentTimeMillis = System.currentTimeMillis();
        if((timecurrentTimeMillis-lastSendTimeMillis)/1000<1){
            ToastUtils.show("发送太快，请稍后再试");
            return;
        }
        msgMap.put("user_id", User.getInstance() != null ? User.getInstance().getId() + "" : "");//用户ID
        msgMap.put("nick_name", User.getInstance() != null ? User.getInstance().getNickname() : "");//昵称
        msgMap.put("user_logo", User.getInstance() != null ? User.getInstance().getAvatar() : "");
        chatInput.setText(null);


        msgMap.put("message", msg);
        OkHttpUtils.post(msgUrl, msgMap, new OkHttpCallback(this) {
            @Override
            public void onFailure(Call call, Exception e) {
                Log.e("test", e.getMessage());
            }

            @Override
            public void onResponse(Call call, String response) throws IOException {
                Log.e("test", response);
            }
        });
        lastSendTimeMillis=timecurrentTimeMillis;
    }


    private void showMsg(ChatMessage msgModel) {

        mAdapter.showChatMessage(msgModel);

        if (User.getInstance() != null && msgModel.getUserId().equals("" + User.getInstance().getId())) {
            //chatMsgList.scrollToPosition(mAdapter.getItemCount() - 1);
            ((LinearLayoutManager) chatMsgList.getLayoutManager()).scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);
        }

        if (chatMsgList.canScrollVertically(1)) {//还可以向下滑动（还没到底部）
            moreMsgBtn.setVisibility(View.VISIBLE);
        } else {//滑动到底部了
            //((LinearLayoutManager) chatMsgList.getLayoutManager()).scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);
            moreMsgBtn.setVisibility(View.GONE);
        }
    }

    private void showMsgs(List<ChatMessage> msgs) {

        mAdapter.showChatMessages(msgs);
        //chatMsgList.scrollToPosition(mAdapter.getItemCount() - 1);
        ((LinearLayoutManager) chatMsgList.getLayoutManager()).scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);
        if (chatMsgList.canScrollVertically(1)) {//还可以向下滑动（还没到底部）
            moreMsgBtn.setVisibility(View.VISIBLE);
        } else {//滑动到底部了
            //((LinearLayoutManager) chatMsgList.getLayoutManager()).scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);
            moreMsgBtn.setVisibility(View.GONE);
        }

    }


    @OnClick({R.id.close, R.id.close1,R.id.fullCreen, R.id.more_msg_btn, R.id.invite, R.id.gift, R.id.viewerNumber, R.id.more,
            R.id.hotRange, R.id.inviteRange, R.id.luckAward, R.id.passwordRedPacket, R.id.chatBtn, R.id.goods, R.id.phrase01, R.id.phrase02, R.id.phrase03})
    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.chatBtn: {
                bottomBar.setVisibility(View.GONE);
                chatInput.setVisibility(View.VISIBLE);
                chatInput.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(chatInput, InputMethodManager.SHOW_IMPLICIT);
            }
            break;

            case R.id.fullCreen:
                mFullScreen=!mFullScreen;
                if(mFullScreen){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }else{
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }

            case R.id.more_msg_btn: {
                moreMsgBtn.setVisibility(View.GONE);
                chatMsgList.scrollToPosition(mAdapter.getItemCount() - 1);
            }
            break;

            case R.id.viewerNumber:
                if (mliveRoomInfo != null && mliveRoomInfo.getRoom_info() != null) {
                    ViewerListDialog.getInstance(mliveRoomInfo.getRoom_info(), onlineViewer).show(getSupportFragmentManager(), "ViewerListDialog");
                }

                break;
            case R.id.invite:

                break;
            case R.id.gift:

                if (mliveRoomInfo != null && mliveRoomInfo.getRoom_info() != null) {
                    LiveAwardDialog.getInstance(mliveRoomInfo).show(getSupportFragmentManager(), "LiveAwardDialog");
                }

                //
                break;
            case R.id.close:
            case R.id.close1:
                exit();
                break;
            case R.id.more:
                if (mliveRoomInfo != null && mliveRoomInfo.getRoom_info() != null) {
                    LiveMoreDialog.getInstance(mliveRoomInfo.getRoom_info())
                            .setOnClickEventListener(new LiveMoreDialog.OnClickEventListener() {

                                @Override
                                public void onShare() {
                                    String url = ServerInfo.mlive + "/index?stream_name=" + mliveRoomInfo.getRoom_info().getStream_name() + "&from_url=" + ServerInfo.mlive + "/index?stream_name=" + mliveRoomInfo.getRoom_info().getStream_name() + "&from_user_id=" + ((User.getInstance() != null) ? (User.getInstance().getId()) : "");
                                    showPosterShare(mliveRoomInfo.getRoom_info(), url);


                                    //showShareDialog(mliveRoomInfo.getRoom_info().getName(), mliveRoomInfo.getRoom_info().getDes(), mliveRoomInfo.getRoom_info().getCover_url(), url);
                                }

                                @Override
                                public void wallet() {
                                    if (User.getInstance() != null) {
                                        startActivity(new Intent(LivePortraitActivity.this, MyWalletsActivity.class));
                                    } else {
                                        Intent it = new Intent(LivePortraitActivity.this, NewLoginActivity.class);
                                        startActivity(it);
                                    }
                                }

                            }).show(getSupportFragmentManager(), "LiveMoreDialog");

                }
                break;

            case R.id.hotRange:
                if (mliveRoomInfo != null && mliveRoomInfo.getRoom_info() != null) {
                    HotRangeListDialog.getInstance(mliveRoomInfo.getRoom_info()).show(getSupportFragmentManager(), "HotRangeListDialog");
                }


                break;

            case R.id.inviteRange:
                if (mliveRoomInfo != null && mliveRoomInfo.getRoom_info() != null) {
                    InviteRangeListDialog dialog = InviteRangeListDialog.getInstance(mliveRoomInfo.getRoom_info());
                    dialog.setOnInviteListener(new InviteRangeListDialog.OnInviteListener() {
                        @Override
                        public void invite() {
                            String url = ServerInfo.mlive + "/index?stream_name=" + mliveRoomInfo.getRoom_info().getStream_name() + "&from_url=" + ServerInfo.mlive + "/index?stream_name=" + mliveRoomInfo.getRoom_info().getStream_name() + "&from_user_id=" + ((User.getInstance() != null) ? (User.getInstance().getId()) : "");
                            showPosterShare(mliveRoomInfo.getRoom_info(), url);
                        }
                    });

                    dialog.show(getSupportFragmentManager(), "InviteRangeListDialog");
                }


                break;

            case R.id.luckAward:
                if (mliveRoomInfo != null && mliveRoomInfo.getRoom_info() != null) {
                    LuckAwardDialog.getInstance(mliveRoomInfo.getRoom_info()).show(getSupportFragmentManager(), "LuckAwardDialog");
                }
                break;

            case R.id.goods://带货
                if (mliveRoomInfo != null && mliveRoomInfo.getRoom_info() != null) {
                    LiveGoodsDialog.getInstance(mliveRoomInfo.getRoom_info()).show(getSupportFragmentManager(), "LiveGoodsDialog");
                }


                break;
            case R.id.passwordRedPacket:
                if (mliveRoomInfo != null && mliveRoomInfo.getRoom_info() != null) {
                    RewardListDialog.getInstance(mliveRoomInfo.getRoom_info()).show(getSupportFragmentManager(), "RewardListDialog");
                }
                break;
            case R.id.phrase01:
            case R.id.phrase02:
            case R.id.phrase03:
                sendMsg(((TextView) view).getText().toString());
                break;

        }
    }

    private void showPosterShare(LiveRoomInfo01.RoomInfoBean liveRoom, String shareUrl) {
        ShareLivePosterDialog01 dialog = ShareLivePosterDialog01.getInstance(liveRoom, shareUrl);
        dialog.setShareHandler(new ShareLivePosterDialog01.ShareHandler() {
            @Override
            public void onShare(String platform, Bitmap bitmap) {
                showShareImage(platform, bitmap);
            }

            @Override
            public void onShare(String platform, LiveRoomInfo01.RoomInfoBean liveRoom) {
                showShare(platform,liveRoom.getName(),liveRoom.getDes(),liveRoom.getCover_url(),ServerInfo.mlive + "/index?stream_name=" + mliveRoomInfo.getRoom_info().getStream_name() + "&from_url=" + ServerInfo.mlive + "/index?stream_name=" + mliveRoomInfo.getRoom_info().getStream_name() + "&from_user_id=" + ((User.getInstance() != null) ? (User.getInstance().getId()) : ""));
            }

            @Override
            public void download(Bitmap bitmap) {
                final Bitmap saveBitmap = bitmap;
//获取写文件权限
                RxPermissions rxPermissions = new RxPermissions(LivePortraitActivity.this);
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean granted) throws Exception {
                                if (granted) {
                                    Random rand = new Random();
                                    int randNum = rand.nextInt(1000);
                                    File tempFile = new File(CommonUtils.getStoragePublicDirectory(DIRECTORY_PICTURES), CommonUtils.getCurrentTimeString() + randNum + ".png");
                                    CommonUtils.saveBitmapToPNG(tempFile.getAbsolutePath(), saveBitmap);
                                    ToastUtils.show("图片保存成功");
//发送广播更新相册
                                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    Uri uri = Uri.fromFile(tempFile);
                                    intent.setData(uri);
                                    sendBroadcast(intent);
                                } else {
                                    ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                                }
                            }
                        });
            }
        });
        dialog.show(getSupportFragmentManager(), "ShareDialog");
    }

    private void showShareImage(String platform, final Bitmap bitmap) {
        final Bitmap saveBitmap = bitmap;
        final OnekeyShare oks = new OnekeyShare();
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
        Random rand = new Random();
        int randNum = rand.nextInt(1000);
        final String childPath = CommonUtils.getCurrentTimeString() + randNum + ".png";
        if (QQ.NAME.equals(platform)) {
//获取写文件权限
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            if (granted) {
                                final File tempFile = new File(CommonUtils.getStoragePublicDirectory(DIRECTORY_PICTURES), childPath);
                                CommonUtils.saveBitmapToPNG(tempFile.getAbsolutePath(), saveBitmap);
                                //ToastUtils.show("图片保存成功");
//发送广播更新相册
                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Uri uri = Uri.fromFile(tempFile);
                                intent.setData(uri);
                                sendBroadcast(intent);
// oks.setImagePath(filePath);
                                oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                                    //自定义分享的回调想要函数
                                    @Override
                                    public void onShare(Platform platform, final Platform.ShareParams paramsToShare) {
                                        paramsToShare.setShareType(Platform.SHARE_IMAGE);
                                        // paramsToShare.setImageData(bitmap);
                                        paramsToShare.setImagePath(tempFile.getAbsolutePath());
                                    }
                                });
                            } else {
                                ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                            }
                        }
                    });
        } else {
            oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                //自定义分享的回调想要函数
                @Override
                public void onShare(Platform platform, final Platform.ShareParams paramsToShare) {
                    paramsToShare.setShareType(Platform.SHARE_IMAGE);
                    paramsToShare.setImageData(bitmap);
                }
            });
        }
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {

            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {

            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
            }
        });
        // 启动分享GUI
        oks.show(this);
    }


    private void showShareDialog(final String title, final String desc, final String logo, final String url) {
        ShareDialog dialog = ShareDialog.getInstance(false, false);
        dialog.setIsHideSecondGroup(false);
        dialog.setIsShowPosterButton(false);
        dialog.setIsShowReport(false);
        dialog.setIsShowCollect(false);
        dialog.setIsShowCopyLink(false);
        dialog.setIsShowFontSize(false);
        dialog.setIsShowRefresh(false);
        dialog.setShareHandler(new ShareDialog.ShareHandler() {
            @Override
            public void onShare(String platform) {
                showShare(platform, title, desc, logo, url);
            }

            @Override
            public void poster() {
            }

            @Override
            public void report() {
            }

            @Override
            public void copyLink() {
//获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData clipData = ClipData.newPlainText("Label", url);
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(clipData);
                ToastUtils.show("复制成功，可以发给朋友们了。");
            }

            @Override
            public void refresh() {
            }

            @Override
            public void collectContent() {
            }
        });
        dialog.show(getSupportFragmentManager(), "ShareDialog");
    }


    private void showShare(String platform, final String title, final String desc, final String logo, final String url) {
        final String cover;
        if (logo.startsWith("http://")) {
            cover = logo.replace("http://", "https://");
        } else {
            cover = logo;
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
                String chanel = "1";
                if (SinaWeibo.NAME.equals(platform.getName())) {
                    chanel = "2";
                    //限制微博分享的文字不能超过20
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                    paramsToShare.setText(title + url);
                } else if (QQ.NAME.equals(platform.getName())) {
                    chanel = "3";
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
            }
        });
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
            }
        });
        // 启动分享GUI
        oks.show(this);
    }

    public void getRedPacketRecord() {
        if (mliveRoomInfo != null && mliveRoomInfo.getRoom_info() != null) {
            String url = ServerInfo.live + "/v1/get_red_bag_record_c";
            Map<String, String> params = new HashMap<>();

            params.put("room_id", "" + mliveRoomInfo.getRoom_info().getId());
            params.put("page", "1");
            params.put("rows", "" + Integer.MAX_VALUE);
            OkHttpUtils.get(url, params, new OkHttpCallback(this) {
                @Override
                public void onResponse(Call call, String response) throws IOException {

                    try {

                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                            RedPacketInfo redPacketInfo = JSON.parseObject(jsonObject.getJSONObject("data").toJSONString(), RedPacketInfo.class);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (redPacketInfo == null) {
                                        luckRedPacket.setVisibility(View.GONE);
                                    } else {
                                        luckRedPacket.setVisibility(View.VISIBLE);
                                        if (redPacketInfo.getState() == 0) {
                                            //定时红包，即将开始
                                            luckRedPacketStatus.setText("即将开始");
                                        } else {
                                            if (redPacketInfo.getState() == 1 && redPacketInfo.getState_end() == 0) {
                                                //正在进行
                                                luckRedPacketStatus.setText("进行中");


                                                Animation anim = new RotateAnimation(-15f, 15f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                                anim.setDuration(600); // 设置动画时间
                                                anim.setRepeatCount(INFINITE);
                                                anim.setRepeatMode(Animation.REVERSE);
                                                anim.setInterpolator(new DecelerateInterpolator()); // 设置插入器
                                                shakeRedPacket.startAnimation(anim);

                                                luckRedPacket.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (User.getInstance() == null) {
                                                            Intent it = new Intent(getContext(), NewLoginActivity.class);
                                                            startActivity(it);
                                                        } else {
                                                            //抢红包操作
                                                            if (redPacketInfo.getUser() != null) {
                                                                List<RedPacketInfo.UserBean> userBeans = redPacketInfo.getUser();
                                                                boolean hasGrabed = false;
                                                                for (RedPacketInfo.UserBean user : redPacketInfo.getUser()) {
                                                                    if (user.getUser_id() == User.getInstance().getId()) {
                                                                        hasGrabed = true;
                                                                        break;
                                                                    }
                                                                }
                                                                if (hasGrabed) {
                                                                    RedPacketDetailDialog.getInstance(redPacketInfo.getId())
                                                                            .show(getSupportFragmentManager(), "RedPacketDialog");

                                                                } else {
                                                                    RedPacketDialog.getInstance(redPacketInfo, mStreamName).setOnOkClickListener(new RedPacketDialog.OnGrabClickListener() {
                                                                        @Override
                                                                        public void onGrabSuccess() {
                                                                            if (User.getInstance() == null) {
                                                                                Intent it = new Intent(getContext(), NewLoginActivity.class);
                                                                                startActivity(it);
                                                                            } else {
                                                                                getRedPacketRecord();
                                                                                RedPacketDetailDialog.getInstance(redPacketInfo.getId())
                                                                                        .show(getSupportFragmentManager(), "RedPacketDialog");
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onDetail() {
                                                                            if (User.getInstance() == null) {
                                                                                Intent it = new Intent(getContext(), NewLoginActivity.class);
                                                                                startActivity(it);
                                                                            } else {
//                                                                    Intent it = new Intent(getContext(), RedPacketDetailActivity.class);
//                                                                    it.putExtra("id", "" + redPacketInfo.getId());
//                                                                    startActivity(it);
                                                                                RedPacketDetailDialog.getInstance(redPacketInfo.getId())
                                                                                        .show(getSupportFragmentManager(), "RedPacketDialog");
                                                                            }

                                                                        }


                                                                    }).show(getSupportFragmentManager(), "RedPacketDialog");
                                                                }
                                                            }


                                                        }

                                                    }
                                                });


                                            } else if (redPacketInfo.getState() == 1 && redPacketInfo.getState_end() == 1) {
                                                //已结束
                                                luckRedPacketStatus.setText("已结束");
                                                shakeRedPacket.clearAnimation();

                                                luckRedPacket.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        RedPacketDetailDialog.getInstance(redPacketInfo.getId())
                                                                .show(getSupportFragmentManager(), "RedPacketDialog");

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


    }


    @Override
    public void doOnBackPressed() {
        if (mNeedResumeAudioPlay) {
            FloatWindowUtil.getInstance().visibleWindow();
        }
        super.doOnBackPressed();
    }
    //退出直播
    private void exit() {
        doOnBackPressed();
//        new CircleDialog.Builder()
//                .setCanceledOnTouchOutside(false)
//                .setCancelable(false)
//
//                .setText("确定结束直播吗")
//                .setNegative("取消", null)
//                .configNegative(new ConfigButton() {
//                    @Override
//                    public void onConfig(ButtonParams params) {
//                        //按钮字体颜色
//                        params.textColor = Color.parseColor("#cc001426");
//                    }
//                })
//                .setPositive("确定", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        finish();
//
//                    }
//                })
//                .configPositive(new ConfigButton() {
//                    @Override
//                    public void onConfig(ButtonParams params) {
//                        params.textColor = Color.parseColor("#3183FF");
//                    }
//                })
//                .show(getSupportFragmentManager());
    }


    @Override
    public void onEventOfferCreated(String sdp) {
        Log.v(TAG, "LEBWebRTC onEventOfferCreated");
        signalingStart(sdp);
    }

    @Override
    public void onEventConnected() {

    }

    @Override
    public void onEventConnectFailed(ConnectionState connectionState) {

    }

    @Override
    public void onEventDisconnect() {

    }

    @Override
    public void onEventFirstPacketReceived(int i) {

    }

    @Override
    public void onEventFirstFrameRendered() {

    }

    @Override
    public void onEventResolutionChanged(int i, int i1) {

    }

    @Override
    public void onEventStatsReport(LEBWebRTCStatsReport lebWebRTCStatsReport) {

    }

    @Override
    public void onEventSEIReceived(ByteBuffer byteBuffer) {

    }


    // 向信令服务器发送offer，获取remote sdp, 通过setRemoteSDP接口设置给sdk
    private void signalingStart(final String sdp) {
        org.json.JSONObject jsonObject = new org.json.JSONObject();
        org.json.JSONObject lsdp = new org.json.JSONObject();
        jsonPut(lsdp, "sdp", sdp);
        jsonPut(lsdp, "type", "offer");
        jsonPut(jsonObject, "localsdp", lsdp);
        jsonPut(jsonObject, "sessionid", "xxxxxx");//业务生成的唯一key, 标识本次拉流, 用户可自定义
        jsonPut(jsonObject, "clientinfo", "xxxxxx");//终端类型信息, 用户可自定义

        jsonPut(jsonObject, "streamurl", mLEBWebRTCParameters.getStreamUrl());
        Log.d(TAG, "Connecting to signaling server: " + mRequestPullUrl);
        Log.d(TAG, "send offer sdp: " + sdp);
        AsyncHttpURLConnection httpConnection =
                new AsyncHttpURLConnection("POST", mRequestPullUrl, jsonObject.toString(), "origin url", "application/json", new AsyncHttpURLConnection.AsyncHttpEvents() {
                    @Override
                    public void onHttpError(String errorMessage) {
                        Log.e(TAG, "connection error: " + errorMessage);
                        //events.onSignalingParametersError(errorMessage);
                    }

                    @Override
                    public void onHttpComplete(String response) {
                        try {
                            org.json.JSONObject json = new org.json.JSONObject(response);
                            int errcode = json.optInt("errcode");
                            String mSvrSig = json.optString("svrsig");
                            org.json.JSONObject rsdp = new org.json.JSONObject(json.optString("remotesdp"));
                            String type = rsdp.optString("type");
                            String sdp = (rsdp.optString("sdp"));
                            Log.d(TAG, "response from signaling server: " + response);
                            Log.d(TAG, "svrsig info: " + mSvrSig);
                            if (errcode == 0 && type.equals("answer") && sdp.length() > 0) {
                                Log.d(TAG, "answer sdp = " + sdp);
                                lebSurfaceView.setRemoteSDP(sdp);
                            }
                        } catch (JSONException e) {
                            Log.d(TAG, "response JSON parsing error: " + e.toString());
                        }
                    }
                });
        httpConnection.send();
    }

    // Put a |key|->|value| mapping in |json|.
    private void jsonPut(org.json.JSONObject json, String key, Object value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updatePlayerViewMode();
    }


    private void updatePlayerViewMode() {

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) playerLayout.getLayoutParams();
            lp.height = (int) (ScreenUtils.getWidth(this) * 9.0f / 16);
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.TOP;
            lp.topMargin = CommonUtils.dip2px(200);
            playerLayout.setLayoutParams(lp);
            panel.setVisibility(View.VISIBLE);
            fullCreenIcon.setText(R.string.full_screen);
            QMUIStatusBarHelper.setStatusBarLightMode(this);
            if(playerType==1){
                lebSurfaceView.setScaleType(mScaleType);
            }
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //转到横屏了。
            //隐藏状态栏

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) playerLayout.getLayoutParams();
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.TOP;
            lp.topMargin = CommonUtils.dip2px(0);
            playerLayout.setLayoutParams(lp);
            panel.setVisibility(View.GONE);
            fullCreenIcon.setText(R.string.full_screen_cancel);
            if(playerType==1){
                lebSurfaceView.setScaleType(SCALE_KEEP_ASPECT_FIT);
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


}
