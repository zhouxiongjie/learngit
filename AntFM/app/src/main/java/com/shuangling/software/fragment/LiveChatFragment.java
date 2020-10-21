package com.shuangling.software.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
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
import com.hjq.toast.ToastUtils;
import com.kaisengao.likeview.like.KsgLikeView;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.params.ButtonParams;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.R;
import com.shuangling.software.activity.LiveDetailActivity;
import com.shuangling.software.activity.NewLoginActivity;
import com.shuangling.software.activity.RedPacketDetailActivity;
import com.shuangling.software.activity.RoomActivity;
import com.shuangling.software.adapter.ChatMessageListAdapter;
import com.shuangling.software.customview.ChatInput;
import com.shuangling.software.dialog.RedPacketComingDialog;
import com.shuangling.software.dialog.RedPacketDialog;
import com.shuangling.software.dialog.ReplyDialog;
import com.shuangling.software.dialog.ShareLivePosterDialog;
import com.shuangling.software.entity.ChatMessage;
import com.shuangling.software.entity.LiveRoomInfo;
import com.shuangling.software.entity.OssInfo;
import com.shuangling.software.entity.RedPacketInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.interf.ChatAction;
import com.shuangling.software.network.MyEcho;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.oss.OSSAKSKCredentialProvider;
import com.shuangling.software.oss.OssService;
import com.shuangling.software.utils.ChatMessageManager;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.MyGlideEngine;
import com.shuangling.software.utils.ServerInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import net.mrbin99.laravelechoandroid.EchoCallback;
import net.mrbin99.laravelechoandroid.EchoOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import io.reactivex.functions.Consumer;
import okhttp3.Call;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.view.animation.Animation.INFINITE;


public class LiveChatFragment extends Fragment implements ChatAction, OSSCompletedCallback<PutObjectRequest, PutObjectResult> {

    private static final int CHOOSE_PHOTO = 0x0;

    public static final int SHARE_FAILED = 0x1;

    public static final int SHARE_SUCCESS = 0x2;

    private String postMessageUrl = ServerInfo.live + "/v3/push_message";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.input_panel)
    ChatInput inputPanel;
    @BindView(R.id.likeView)
    KsgLikeView likeView;
    @BindView(R.id.heartBtn)
    ImageView heartBtn;
    @BindView(R.id.redPacket)
    FrameLayout redPacket;
    @BindView(R.id.shakeRedPacket)
    ImageView shakeRedPacket;
    @BindView(R.id.redPacketStatus)
    TextView redPacketStatus;
    @BindView(R.id.referMe)
    TextView referMe;
    Unbinder unbinder;

    String mStreamName;
    private MyEcho echo;
    private Handler mHandler;
    private int mRoomId;
    private LiveRoomInfo mLiveRoomInfo;
    private boolean hasApply;

    private ChatMessageListAdapter mChatMessageListAdapter;
    private HashMap<String, String> mMessageMap = new HashMap<>();

    private String mUploadFilePath;
    private OssInfo mOssInfo;
    private OssService mOssService;

    private String mParentId = "0";
    private CountDownTimer mCountDownTimer;
    private RedPacketComingDialog mRedPacketComingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle args = getArguments();
        super.onCreate(savedInstanceState);
        mHandler = new Handler(Looper.getMainLooper());
        hasApply = false;
        mStreamName = args.getString("streamName");
        mRoomId = args.getInt("roomId");
        mLiveRoomInfo=(LiveRoomInfo)args.getSerializable("LiveRoomInfo");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_chat, null);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        inputPanel.setChatAction(this);
        if(((LiveDetailActivity)getActivity()).mType!=4){
            inputPanel.setJoinRoomVisible(false);
        }
        if (((LiveDetailActivity) getActivity()).hasInvite) {
            inputPanel.setInviteVisible(true);
        }else{
            inputPanel.setInviteVisible(false);
        }

        if(((LiveDetailActivity)getActivity()).getLiveRoomInfo()!=null){
            if(((LiveDetailActivity)getActivity()).getLiveRoomInfo().getChat()==0){
                inputPanel.setMuted(true);
            }
        }


        likeView.addLikeImage(R.drawable.ic_heart);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

//        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
//        recyclerView.addItemDecoration(divider);

        ChatMessageManager.getInstance().clearMessages();
        mChatMessageListAdapter = new ChatMessageListAdapter(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mChatMessageListAdapter);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                if (!recyclerView.canScrollVertically(30)) {
                    //moreMsgBtn.setVisibility(View.GONE);
                }
            }
        });



        mChatMessageListAdapter.setOnItemReply(new ChatMessageListAdapter.OnItemReply() {
            @Override
            public void replyItem(ChatMessage message) {
                ReplyDialog replyDialog = ReplyDialog.getInstance(message, "" + mRoomId, mStreamName);

                replyDialog.setOnAction(new ReplyDialog.OnAction() {
                    @Override
                    public void sendImage() {
                        if (User.getInstance() == null) {
                            Intent it = new Intent(getContext(), NewLoginActivity.class);
                            startActivity(it);
                        } else {

                            RxPermissions rxPermissions = new RxPermissions(getActivity());
                            rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    .subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(Boolean granted) throws Exception {
                                            if (granted) {
                                                String packageName = getActivity().getPackageName();
                                                mParentId = "" + message.getChatsId();
                                                Matisse.from(LiveChatFragment.this)
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


                        }
                    }
                });
                replyDialog.show(getChildFragmentManager(), "replyDialog");


            }
        });


        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
            }
        });

        getChatHistory();
        joinChannel();

        if (((LiveDetailActivity) getActivity()).canGrabRedPacket) {
            getRedPacketRecord();
        }

        heartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (User.getInstance() != null) {
                    likeView.addFavor();
                    heart();
                } else {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    startActivity(it);
                }

            }
        });

//        redPacket.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RedPacketComingDialog redPacketComingDialog = RedPacketComingDialog.getInstance();
//                redPacketComingDialog.setOnFinishListener(new RedPacketComingDialog.OnFinishListener() {
//                    @Override
//                    public void onFinish() {
//
//                            redPacketComingDialog.dismiss();
//                        RedPacketDialog.getInstance().setOnOkClickListener(new RedPacketDialog.OnOkClickListener() {
//                            @Override
//                            public void openNoticafition() {
//
//                            }
//                        }).show(getChildFragmentManager(), "RedPacketDialog");
//                    }
//                }).show(getChildFragmentManager(), "RedPacketComingDialog");
//            }
//        });
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mCountDownTimer!=null){
            mCountDownTimer.cancel();
        }

        shakeRedPacket.clearAnimation();
        EventBus.getDefault().unregister(this);
        if (echo != null) {
            echo.leave(mStreamName);
            echo.disconnect();
        }
        if (hasApply) {
            ((LiveDetailActivity) getActivity()).cancelInteract("" + mRoomId);
            hasApply = false;
            ((LiveDetailActivity) getActivity()).leaveChannel();
        }


        unbinder.unbind();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("refreshMessageList")) {
            if (mChatMessageListAdapter != null) {
                mChatMessageListAdapter.notifyDataSetChanged();

                recyclerView.scrollToPosition(mChatMessageListAdapter.getItemCount() - 1);
            }
        }else if(event.getEventName().equals("liveRoomInfo")){
            if(((LiveDetailActivity)getActivity()).getLiveRoomInfo()!=null){
                if(((LiveDetailActivity)getActivity()).getLiveRoomInfo().getChat()==0){
                    inputPanel.setMuted(true);
                }else {
                    inputPanel.setMuted(false);
                }
            }
        }
    }

    @Override
    public void sendImage() {

        if (User.getInstance() == null) {
            Intent it = new Intent(getContext(), NewLoginActivity.class);
            startActivity(it);
        } else {

            RxPermissions rxPermissions = new RxPermissions(getActivity());
            rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            if (granted) {
                                String packageName = getActivity().getPackageName();
                                mParentId = "0";
                                Matisse.from(LiveChatFragment.this)
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


        }
    }

    @Override
    public void joinRoom() {
        if (User.getInstance() == null) {
            Intent it = new Intent(getContext(), NewLoginActivity.class);
            startActivity(it);
        } else {
            //getRoomToken("359");
            applyInteract("" + mRoomId);
        }
    }

    @Override
    public void sendText(String str) {

        if (User.getInstance() == null) {
            Intent it = new Intent(getContext(), NewLoginActivity.class);
            startActivity(it);
        } else {
            if (TextUtils.isEmpty(str)) return;
            inputPanel.setText("");
            CommonUtils.hideInput(getActivity());
            mMessageMap.clear();
            mMessageMap.put("room_id", "" + mRoomId);//直播间ID
            mMessageMap.put("parent_id", "0");
            mMessageMap.put("user_id", User.getInstance().getId() + "");//用户ID
            mMessageMap.put("type", "2");//发布端类型：1.主持人   2：用户    3:通知关注  4：通知进入直播间
            mMessageMap.put("stream_name", mStreamName);//播间推流ID
            mMessageMap.put("nick_name", User.getInstance().getNickname());//昵称
            mMessageMap.put("message_type", "1");//消息类型 1.互动消息  2.直播状态更新消息  3.删除消息  4.题目 5.菜单设置 6图文保存  默认1
            mMessageMap.put("user_logo", User.getInstance().getAvatar());
            mMessageMap.put("message", str);
            mMessageMap.put("content_type", "1");
            OkHttpUtils.post(postMessageUrl, mMessageMap, new OkHttpCallback(getContext()) {
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


    }

    @Override
    public void invite() {
        showPosterShare(ServerInfo.mlive+"/index?stream_name="+mLiveRoomInfo.getStream_name()+"&from_url="+
                ServerInfo.mlive+"/index?stream_name="+mLiveRoomInfo.getStream_name()+"&from_id="+User.getInstance()!=null?""+User.getInstance().getId():"");
    }


    private void showPosterShare(String shareUrl){

        ShareLivePosterDialog dialog =  ShareLivePosterDialog.getInstance(mLiveRoomInfo,shareUrl);
        dialog.setShareHandler(new ShareLivePosterDialog.ShareHandler() {
            @Override
            public void onShare(String platform,Bitmap bitmap) {
                showShareImage(platform,bitmap);

            }

            @Override
            public void download(Bitmap bitmap) {

                final Bitmap saveBitmap =bitmap;

                //获取写文件权限
                RxPermissions rxPermissions = new RxPermissions(getActivity());
                rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean granted) throws Exception {
                                if(granted){
                                    Random rand = new Random();
                                    int randNum = rand.nextInt(1000);
                                    File tempFile = new File(CommonUtils.getStoragePublicDirectory(DIRECTORY_PICTURES), CommonUtils.getCurrentTimeString()+randNum+".png");
                                    CommonUtils.saveBitmapToPNG(tempFile.getAbsolutePath(), saveBitmap);
                                    ToastUtils.show("图片保存成功");

                                    //发送广播更新相册
                                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    Uri uri = Uri.fromFile(tempFile);
                                    intent.setData(uri);
                                    getActivity().sendBroadcast(intent);

                                }else{
                                    ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                                }
                            }
                        });

            }


        });
        dialog.show(getChildFragmentManager(), "ShareDialog");

    }


    private void showShareImage(String platform, final Bitmap bitmap) {


        final Bitmap saveBitmap =bitmap;


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
        final String childPath =  CommonUtils.getCurrentTimeString()+randNum+".png";




        if(QQ.NAME.equals(platform)){

            //获取写文件权限
            RxPermissions rxPermissions = new RxPermissions(getActivity());
            rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            if(granted){

                                final  File tempFile = new File(CommonUtils.getStoragePublicDirectory(DIRECTORY_PICTURES), childPath);
                                CommonUtils.saveBitmapToPNG(tempFile.getAbsolutePath(), saveBitmap);
                                //ToastUtils.show("图片保存成功");

                                //发送广播更新相册
                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Uri uri = Uri.fromFile(tempFile);
                                intent.setData(uri);
                                getActivity().sendBroadcast(intent);

                                // oks.setImagePath(filePath);

                                oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                                    //自定义分享的回调想要函数
                                    @Override
                                    public void onShare(Platform platform, final  Platform.ShareParams paramsToShare) {

                                        paramsToShare.setShareType(Platform.SHARE_IMAGE);
                                        // paramsToShare.setImageData(bitmap);
                                        paramsToShare.setImagePath(tempFile.getAbsolutePath());

                                    }
                                });


                            }else{
                                ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                            }
                        }
                    });




        }else{
            oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                //自定义分享的回调想要函数
                @Override
                public void onShare(Platform platform, final  Platform.ShareParams paramsToShare) {
                    paramsToShare.setShareType(Platform.SHARE_IMAGE);
                    paramsToShare.setImageData(bitmap);
                }
            });

        }



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
        oks.show(getContext());
    }


    private void heart() {
        String url = ServerInfo.live + ServerInfo.heart;
        Map<String, String> params = new HashMap<>();

        params.put("room_id", "" + mRoomId);
        params.put("user_id", "" + User.getInstance().getId());
        params.put("amount", "1");
        params.put("stream_name", mStreamName);
        params.put("nick_name", User.getInstance().getNickname());
        params.put("user_logo", User.getInstance().getAvatar());
        OkHttpUtils.post(url, params, new OkHttpCallback(getContext()) {
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


    private void getChatHistory() {
        String url = ServerInfo.live + "/v3/chats_history";
        Map<String, String> params = new HashMap<>();

        params.put("room_id", "" + mRoomId);
        params.put("page", "1");
        params.put("page_size", "" + Integer.MAX_VALUE);
        params.put("state", "1");
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        List<ChatMessage> msgs = JSON.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), ChatMessage.class);
                        Collections.reverse(msgs);
                        ChatMessageManager.getInstance().clearMessages();
                        ChatMessageManager.getInstance().addMessages(msgs);


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


    private void getRedPacketRecord() {
        String url = ServerInfo.live + "/v1/get_red_bag_record_c";
        Map<String, String> params = new HashMap<>();

        params.put("room_id", "" + mRoomId);
        params.put("page", "1");
        params.put("rows", "" + Integer.MAX_VALUE);
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        List<RedPacketInfo> redPacketInfos = JSON.parseArray(jsonObject.getJSONObject("data").getJSONArray("my_clip").toJSONString(), RedPacketInfo.class);


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(redPacketInfos==null||redPacketInfos.size()==0){
                                    redPacket.setVisibility(View.GONE);
                                }else{
                                    redPacket.setVisibility(View.VISIBLE);
                                    RedPacketInfo redPacketInfo=redPacketInfos.get(redPacketInfos.size()-1);
                                    if(redPacketInfo.getState()==0){
                                        //定时红包，即将开始
                                        redPacketStatus.setText("即将开始");
                                    }else{
                                        if(redPacketInfo.getState()==1&&redPacketInfo.getState_end()==0){
                                            //正在进行
                                            redPacketStatus.setText("进行中");
//                                            TranslateAnimation animation = new TranslateAnimation(0, -5, 0, 0);
//                                            animation.setInterpolator(new OvershootInterpolator());
//                                            animation.setDuration(100);
//                                            animation.setRepeatCount(INFINITE);
//                                            animation.setRepeatMode(Animation.REVERSE);
//                                            shakeRedPacket.startAnimation(animation);

                                            Animation anim =new RotateAnimation(-15f, 15f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                            anim.setDuration(600); // 设置动画时间
                                            anim.setRepeatCount(INFINITE);
                                            anim.setRepeatMode(Animation.REVERSE);
                                            anim.setInterpolator(new DecelerateInterpolator()); // 设置插入器
                                            shakeRedPacket.startAnimation(anim);

                                            redPacket.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    if (User.getInstance() == null) {
                                                        Intent it = new Intent(getContext(), NewLoginActivity.class);
                                                        startActivity(it);
                                                    }else{
                                                        //抢红包操作
                                                        RedPacketDialog.getInstance(redPacketInfo,mStreamName).setOnOkClickListener(new RedPacketDialog.OnGrabClickListener() {
                                                            @Override
                                                            public void onGrab() {

                                                                if (User.getInstance() == null) {
                                                                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                                                                    startActivity(it);
                                                                }else{
                                                                    Intent it=new Intent(getContext(),RedPacketDetailActivity.class);
                                                                    it.putExtra("id",""+redPacketInfo.getId());
                                                                    startActivity(it);
                                                                }


                                                            }
                                                        }).show(getChildFragmentManager(), "RedPacketDialog");
                                                    }

                                                }
                                            });


                                        }else if(redPacketInfo.getState()==1&&redPacketInfo.getState_end()==1){
                                            //已结束
                                            redPacketStatus.setText("已结束");
                                            shakeRedPacket.clearAnimation();

                                            redPacket.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    if (User.getInstance() == null) {
                                                        Intent it = new Intent(getContext(), NewLoginActivity.class);
                                                        startActivity(it);
                                                    }else{
                                                        Intent it=new Intent(getContext(),RedPacketDetailActivity.class);
                                                        it.putExtra("id",""+redPacketInfo.getId());
                                                        startActivity(it);
                                                    }

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





    private void getRoomToken(final String roomId) {
        String url = ServerInfo.live + "/v4/get_room_token_c";
        Map<String, String> params = new HashMap<>();
        params.put("userId", "user_" + User.getInstance().getId());
        params.put("roomId", roomId);
        params.put("permission", "user");
        OkHttpUtils.post(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        final String roomToken = jsonObject.getString("data");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (TextUtils.isEmpty(roomToken)) {
                                    return;
                                }
                                Intent intent = new Intent(getContext(), RoomActivity.class);
                                intent.putExtra(RoomActivity.EXTRA_ROOM_ID, roomId);
                                intent.putExtra(RoomActivity.EXTRA_ROOM_TOKEN, roomToken);
                                intent.putExtra(RoomActivity.EXTRA_USER_ID, "user_" + User.getInstance().getId());
                                intent.putExtra("streamName", mStreamName);
                                startActivity(intent);
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

    //申请连麦
    private void applyInteract(String roomId) {
        String url = ServerInfo.live + "/v4/user_apply_interact";
        Map<String, String> params = new HashMap<>();
        params.put("userId", "" + User.getInstance().getId());
        params.put("roomId", roomId);
        params.put("type", "1");
        OkHttpUtils.post(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        hasApply = true;
                        ((LiveDetailActivity) getActivity()).joinChannel();
                        ToastUtils.show("申请成功,请等待主持人通过");
                        //getRoomToken(""+mRoomId);


//                        String roomToken = jsonObject.getString("data");
//
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (TextUtils.isEmpty(roomToken)) {
//                                    return;
//                                }
//                                Intent intent = new Intent(getContext(), RoomActivity.class);
//                                intent.putExtra(RoomActivity.EXTRA_ROOM_ID, roomId);
//                                intent.putExtra(RoomActivity.EXTRA_ROOM_TOKEN, roomToken);
//                                intent.putExtra(RoomActivity.EXTRA_USER_ID, "user_"+User.getInstance().getId());
//                                startActivity(intent);
//                            }
//                        });

                    } else if (jsonObject != null) {
                        ToastUtils.show(jsonObject.getString("msg"));
                    } else {

                        ToastUtils.show("申请连麦失败");
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


    private void joinChannel() {


        EchoOptions options = new EchoOptions();
        options.host = ServerInfo.echo_server;
        //options.headers.put("Authorization", User.getInstance().getAuthorization());
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
                            final ChatMessage msg = JSON.parseObject(args[1].toString(), ChatMessage.class);

                            if (msg.getMessageType() == 1){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showMsg(msg);
                                    }
                                });
                            }else if(msg.getMessageType() == 16){

                                long id=Thread.currentThread().getId();

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRedPacketComingDialog = RedPacketComingDialog.getInstance();
                                        mRedPacketComingDialog.setOnCloseListener(new RedPacketComingDialog.OnCloseListener() {
                                            @Override
                                            public void onClose() {
                                                mRedPacketComingDialog=null;
                                            }
                                        }).show(getChildFragmentManager(), "RedPacketComingDialog");

                                        redPacket.setVisibility(View.VISIBLE);

                                        redPacket.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mRedPacketComingDialog = RedPacketComingDialog.getInstance();
                                                mRedPacketComingDialog.setOnCloseListener(new RedPacketComingDialog.OnCloseListener() {
                                                    @Override
                                                    public void onClose() {
                                                        mRedPacketComingDialog=null;
                                                    }
                                                }).show(getChildFragmentManager(), "RedPacketComingDialog");
                                            }
                                        });

                                        //红包活动开始倒计时
                                        mCountDownTimer = new CountDownTimer(10 * 1000, 500) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                if(millisUntilFinished / 1000==0){
                                                    if(mRedPacketComingDialog!=null){
                                                        mRedPacketComingDialog.setRemainTime("1");
                                                    }
                                                    redPacketStatus.setText("1s");
                                                }else{
                                                    if(mRedPacketComingDialog!=null){
                                                        mRedPacketComingDialog.setRemainTime("" + millisUntilFinished / 1000);
                                                    }
                                                    redPacketStatus.setText(millisUntilFinished / 1000+"s");


                                                }


                                            }

                                            @Override
                                            public void onFinish() {

                                                if(mRedPacketComingDialog!=null){
                                                    mRedPacketComingDialog.dismiss();
                                                }
                                                getRedPacketRecord();

                                            }
                                        };
                                        mCountDownTimer.start();
                                    }
                                });





                            }else if(msg.getMessageType() == 17){
                                //红包活动结束
                                getRedPacketRecord();
                            }else if(msg.getMessageType()==13){
                                //点心
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            likeView.addFavor();
                                        }catch (Exception e){

                                        }

                                    }
                                });
                            }else if(msg.getMessageType() == 15){
                                //图文直播更新
                                EventBus.getDefault().post(new CommonEvent("ImgTextLiveUpdate"));
                            }else if(msg.getMessageType() == 5){
                                //菜单设置
                                EventBus.getDefault().post(new CommonEvent("modifyMenu"));
                            }else if(msg.getMessageType() == 2){
                                //直播状态更新
                                if(msg.getMsg().equals("关闭直播间")){
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try{
                                                new CircleDialog.Builder()
                                                        .setText("直播已结束")
                                                        .setPositive("知道了", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {

                                                            }
                                                        })
                                                        .configPositive(new ConfigButton() {
                                                            @Override
                                                            public void onConfig(ButtonParams params) {
                                                                params.textColor = CommonUtils.getThemeColor(getContext());
                                                            }
                                                        })
                                                        .setCancelable(false)
                                                        .show(getChildFragmentManager());


                                            }catch (Exception e){

                                            }

                                        }
                                    });
                                }

                            }


                        } catch (Exception e) {
                            Log.v("SL", e.getMessage());
                        }
                    }
                });


//        echo.privateChannel("Chat")
//                .listen("Chat", new EchoCallback() {
//                    @Override
//                    public void call(Object... args) {
//                        // Event thrown.
//                        Log.i("test", "args");
//
//                        ToastUtils.show(args.toString());
//
//                    }
//                });
    }


    private void showMsg(ChatMessage chatMessage) {
//        msgList.add(msgModel);
//        msgLandscapeList.add(msgModel);


        ChatMessageManager.getInstance().addMessage(chatMessage);
        if (chatMessage.getParentMsgInfo()!=null&&User.getInstance()!=null&&chatMessage.getParentMsgInfo().getUserId().equals(""+User.getInstance().getId())) {
            referMe.setVisibility(View.VISIBLE);
            final int pos=ChatMessageManager.getInstance().getMessageList().size();
            referMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    referMe.setVisibility(View.GONE);
                    recyclerView.scrollToPosition(pos);
                }
            });
        }


//        if (recyclerView.canScrollVertically(1)) {//还可以向下滑动（还没到底部）
//            //moreMsgBtn.setVisibility(View.VISIBLE);
//
//        } else {//滑动到底部了
//            recyclerView.scrollToPosition(mChatMessageListAdapter.getItemCount() - 1);
//            //moreMsgBtn.setVisibility(View.GONE);
//        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_PHOTO && resultCode == Activity.RESULT_OK) {

            //List<Uri> selects = Matisse.obtainResult(data);
            List<String> paths = Matisse.obtainPathResult(data);
            //List<Uri> selects = Matisse.obtainResult(data);
            //File file = new File(CommonUtils.getRealFilePath(this, selects.get(0)));
            mUploadFilePath = paths.get(0);
            getOSSinfo();


        }
    }


    public void getOSSinfo() {

        String url = ServerInfo.serviceIP + ServerInfo.appOss;

        Map<String, String> params = new HashMap<>();


        OkHttpUtils.get(url, null, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                String result = response;
                final JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    mOssInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), OssInfo.class);
                    mOssService = initOSS(mOssInfo.getHost(), mOssInfo.getBucket(), mOssInfo.getAccess_key_id(), mOssInfo.getAccess_key_secret(), mOssInfo.getExpiration(), mOssInfo.getSecurity_token());
                    //mOssService.setCallbackAddress(Config.callbackAddress);

                    // 2.把图片文件file上传到服务器
                    if (mOssInfo != null && mOssService != null) {
                        mOssService.asyncUploadFile(mOssInfo.getDir() + mUploadFilePath.substring(mUploadFilePath.lastIndexOf(File.separator) + 1), mUploadFilePath, null, LiveChatFragment.this);
                    } else {
                        ToastUtils.show("OSS初始化失败,请稍后再试");
                    }
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
        OSS oss = new OSSClient(getActivity().getApplicationContext(), endpoint, new OSSStsTokenCredentialProvider(accessKey, accessKeySecret, securityToken), conf);
        OSSLog.enableLog();
        return new OssService(oss, bucket);

    }


    @Override
    public void onSuccess(PutObjectRequest request, PutObjectResult result) {

        sendPicture(mOssInfo.getHost() + "/" + mOssInfo.getDir() + mUploadFilePath.substring(mUploadFilePath.lastIndexOf(File.separator) + 1));


    }

    @Override
    public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {

    }


    private void sendPicture(String pictureUrl) {
        mMessageMap.clear();
        mMessageMap.put("room_id", "" + mRoomId);//直播间ID
        mMessageMap.put("user_id", User.getInstance().getId() + "");//用户ID
        mMessageMap.put("parent_id", mParentId);
        mMessageMap.put("type", "2");//发布端类型：1.主持人   2：用户    3:通知关注  4：通知进入直播间
        mMessageMap.put("stream_name", mStreamName);//播间推流ID
        mMessageMap.put("nick_name", User.getInstance().getNickname());//昵称
        mMessageMap.put("message_type", "1");//消息类型 1.互动消息  2.直播状态更新消息  3.删除消息  4.题目 5.菜单设置 6图文保存  默认1
        mMessageMap.put("user_logo", User.getInstance().getAvatar());
        mMessageMap.put("message", pictureUrl);
        mMessageMap.put("content_type", "2");
        OkHttpUtils.post(postMessageUrl, mMessageMap, new OkHttpCallback(getContext()) {
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




}
