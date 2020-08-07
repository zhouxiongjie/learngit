package com.shuangling.software.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.aliyun.apsara.alivclittlevideo.utils.Common;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.R;
import com.shuangling.software.activity.LiveDetailActivity;
import com.shuangling.software.activity.ModifyUserInfoActivity;
import com.shuangling.software.activity.RoomActivity;
import com.shuangling.software.customview.ChatInput;
import com.shuangling.software.entity.ChatMessage;
import com.shuangling.software.entity.OssInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.interf.ChatAction;
import com.shuangling.software.network.MyEcho;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.oss.OSSAKSKCredentialProvider;
import com.shuangling.software.oss.OssService;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.MyGlideEngine;
import com.shuangling.software.utils.ServerInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import net.mrbin99.laravelechoandroid.EchoCallback;
import net.mrbin99.laravelechoandroid.EchoOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import okhttp3.Call;


public class LiveChatFragment extends Fragment implements ChatAction ,OSSCompletedCallback<PutObjectRequest, PutObjectResult> {

    private static final int CHOOSE_PHOTO = 0x0;

    private String postMessageUrl = ServerInfo.live + "/v3/push_message";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.input_panel)
    ChatInput inputPanel;
    Unbinder unbinder;

    String mStreamName;
    private MyEcho echo;
    private Handler mHandler;
    private int mRoomId;
    private boolean hasApply;

    private ChatMessageListAdapter mChatMessageListAdapter;
    private HashMap<String, String> mMessageMap = new HashMap<>();

    private String mUploadFilePath;
    private OssInfo mOssInfo;
    private OssService mOssService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle args = getArguments();
        super.onCreate(savedInstanceState);
        mHandler= new Handler(Looper.getMainLooper());
        hasApply=false;
        mStreamName=args.getString("streamName");
        mRoomId=args.getInt("roomId");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_chat, null);
        unbinder = ButterKnife.bind(this, view);
        inputPanel.setChatAction(this );
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

//        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
//        recyclerView.addItemDecoration(divider);


        mChatMessageListAdapter = new ChatMessageListAdapter(getContext(), 1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
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

        joinChannel();
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (echo != null) {
            echo.disconnect();
        }
        if(hasApply){
            cancelInteract(""+mRoomId);
        }


        unbinder.unbind();
    }

    @Override
    public void sendImage() {

        if(User.getInstance()==null){
            ToastUtils.show("请先登录");
        }else{

            RxPermissions rxPermissions = new RxPermissions(getActivity());
            rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            if(granted){
                                String packageName = getActivity().getPackageName();
                                Matisse.from(LiveChatFragment.this)
                                        .choose(MimeType.of(MimeType.JPEG,MimeType.PNG)) // 选择 mime 的类型
                                        .countable(false)
                                        .maxSelectable(1) // 图片选择的最多数量
                                        .spanCount(4)
                                        .capture(true)
                                        .captureStrategy(new CaptureStrategy(true,packageName+".fileprovider"))
                                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                        .thumbnailScale(1.0f) // 缩略图的比例
                                        .theme(R.style.Matisse_Zhihu)
                                        .imageEngine(new MyGlideEngine()) // 使用的图片加载引擎
                                        .forResult(CHOOSE_PHOTO); // 设置作为标记的请求码
                            }else{
                                ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                            }
                        }
                    });


        }
    }

    @Override
    public void joinRoom() {
        if(User.getInstance()==null){
            ToastUtils.show("请先登录");
        }else{
            //getRoomToken("359");
            applyInteract(""+mRoomId);
        }
    }

    @Override
    public void sendText(String str) {

        if(User.getInstance()==null){
            ToastUtils.show("请先登录");
        }else{
            if (TextUtils.isEmpty(str)) return;
            inputPanel.setText("");
            mMessageMap.clear();
            mMessageMap.put("room_id", ""+mRoomId);//直播间ID
            mMessageMap.put("parent_id","0");
            mMessageMap.put("user_id", User.getInstance().getId() + "");//用户ID
            mMessageMap.put("type", "2");//发布端类型：1.主持人   2：用户    3:通知关注  4：通知进入直播间
            mMessageMap.put("stream_name", mStreamName);//播间推流ID
            mMessageMap.put("nick_name", User.getInstance().getNickname());//昵称
            mMessageMap.put("message_type", "1");//消息类型 1.互动消息  2.直播状态更新消息  3.删除消息  4.题目 5.菜单设置 6图文保存  默认1
            mMessageMap.put("user_logo",User.getInstance().getAvatar());
            mMessageMap.put("message", str);
            mMessageMap.put("content_type","1");
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

    private void getRoomToken(String roomId) {
        String url = ServerInfo.live + "/v4/get_room_token_c";
        Map<String, String> params = new HashMap<>();
        params.put("userId","user_"+User.getInstance().getId());
        params.put("roomId",roomId);
        params.put("permission","user");
        OkHttpUtils.post(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try{

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        String roomToken = jsonObject.getString("data");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (TextUtils.isEmpty(roomToken)) {
                                    return;
                                }
                                Intent intent = new Intent(getContext(), RoomActivity.class);
                                intent.putExtra(RoomActivity.EXTRA_ROOM_ID, roomId);
                                intent.putExtra(RoomActivity.EXTRA_ROOM_TOKEN, roomToken);
                                intent.putExtra(RoomActivity.EXTRA_USER_ID, "user_"+User.getInstance().getId());
                                startActivity(intent);
                            }
                        });

                    }

                }catch (Exception e){

                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test",exception.toString());

            }
        });
    }

    //申请连麦
    private void applyInteract(String roomId) {
        String url = ServerInfo.live + "/v4/user_apply_interact";
        Map<String, String> params = new HashMap<>();
        params.put("userId",""+User.getInstance().getId());
        params.put("roomId",roomId);
        params.put("type","1");
        OkHttpUtils.post(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try{

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        hasApply=true;
                        ((LiveDetailActivity)getActivity()).joinChannel();

                        //getRoomToken("359");


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

                    }else if(jsonObject != null){
                        ToastUtils.show(jsonObject.getString("msg"));
                    }else{

                        ToastUtils.show("申请连麦失败");
                    }

                }catch (Exception e){

                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test",exception.toString());

            }
        });
    }



    //申请连麦
    private void cancelInteract(String roomId) {
        String url = ServerInfo.live + "/v4/user_apply_interact";
        Map<String, String> params = new HashMap<>();
        params.put("userId",""+User.getInstance().getId());
        params.put("roomId",roomId);
        OkHttpUtils.post(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try{

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        hasApply=false;


                        //getRoomToken("359");


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

                    }else if(jsonObject != null){
                        ToastUtils.show(jsonObject.getString("msg"));
                    }else{

                        ToastUtils.show("取消连麦失败");
                    }

                }catch (Exception e){

                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test",exception.toString());

            }
        });
    }


    private void joinChannel(){


        EchoOptions options = new EchoOptions();
        options.host = "http://echo-live.review.slradio.cn";
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
                            ChatMessage msg = JSON.parseObject(args[1].toString(), ChatMessage.class);

                            if (msg.getMessageType() != 1) return;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showMsg(msg);
                                }
                            });

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


        mChatMessageListAdapter.showChatMsg(chatMessage);
        if (recyclerView.canScrollVertically(1)) {//还可以向下滑动（还没到底部）
            //moreMsgBtn.setVisibility(View.VISIBLE);

        } else {//滑动到底部了
            recyclerView.scrollToPosition(mChatMessageListAdapter.getItemCount() - 1);
            //moreMsgBtn.setVisibility(View.GONE);
        }

        

    }




    static class ChatMessageListAdapter extends RecyclerView.Adapter {

        public static final int TYPE_HEADER = 0;
        public static final int TYPE_TEXT = 1;
        public static final int TYPE_PICTURE = 2;

        private LayoutInflater inflater;
        ArrayList<ChatMessage> msgList = new ArrayList<>();

        int directionType = 0; //0竖屏 1横屏

        public void showChatMsg(ChatMessage msg) {
            msgList.add(msg);
            //notifyDataSetChanged();
            notifyItemInserted(msgList.size());
        }

        public void addChatMsg(ChatMessage msg) {

            msgList.add(msg);

        }

        public ChatMessageListAdapter(Context context, int directionType) {
            this.directionType = directionType;
            inflater = LayoutInflater.from(context);
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

            int viewType=getItemViewType(position);
            if (viewType == TYPE_HEADER) {

            }else if(viewType == TYPE_TEXT){
                TextViewHolder vh = (TextViewHolder) viewHolder;
                ChatMessage  msg = msgList.get(position - 1);

                if (!TextUtils.isEmpty(msg.getUserLog())) {
                    Uri uri = Uri.parse(msg.getUserLog());
                    int width = CommonUtils.dip2px(25);
                    int height = width;
                    ImageLoader.showThumb(uri, vh.head, width, height);
                }else{
                    ImageLoader.showThumb( vh.head,R.drawable.ic_user1);
                }

                if(msg.getType() == 1){
                    //主持人
                    vh.emcee.setVisibility(View.VISIBLE);
                }else{
                    vh.emcee.setVisibility(View.GONE);
                }
                vh.name.setText(msg.getNickName());
                vh.content.setText(msg.getMsg());
            }else if(getItemViewType(position) == TYPE_PICTURE){
                PictureViewHolder vh = (PictureViewHolder) viewHolder;
                ChatMessage  msg = msgList.get(position - 1);

                if (!TextUtils.isEmpty(msg.getUserLog())) {
                    Uri uri = Uri.parse(msg.getUserLog());
                    int width = CommonUtils.dip2px(25);
                    int height = width;
                    ImageLoader.showThumb(uri, vh.head, width, height);
                }else{
                    ImageLoader.showThumb( vh.head,R.drawable.ic_user1);
                }

                if(msg.getType() == 1){
                    //主持人
                    vh.emcee.setVisibility(View.VISIBLE);
                }else{
                    vh.emcee.setVisibility(View.GONE);
                }
                vh.name.setText(msg.getNickName());


                if(!TextUtils.isEmpty(msg.getMsg())){

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
                            float ratio=(float) imageInfo.getWidth()/(float)imageInfo.getHeight();
                            vh.picture.setAspectRatio(ratio);





                        }

                        @Override
                        public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {

                        }

                        @Override
                        public void onFailure(String id, Throwable throwable) {

                        }
                    };

                    Uri uri=Uri.parse(msg.getMsg());
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_PHOTO && resultCode == Activity.RESULT_OK) {

            //List<Uri> selects = Matisse.obtainResult(data);
            List<String> paths=Matisse.obtainPathResult(data);
            //List<Uri> selects = Matisse.obtainResult(data);
            //File file = new File(CommonUtils.getRealFilePath(this, selects.get(0)));
            mUploadFilePath = paths.get(0);
            getOSSinfo();


        }
    }



    public void getOSSinfo() {

        String url = ServerInfo.serviceIP + ServerInfo.appOss;

        Map<String, String> params = new HashMap<>();


        OkHttpUtils.get(url, null, new OkHttpCallback(getContext() ) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                String result = response;
                final JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    mOssInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), OssInfo.class);
                    mOssService=initOSS(mOssInfo.getHost(),mOssInfo.getBucket(),mOssInfo.getAccess_key_id(),mOssInfo.getAccess_key_secret(),mOssInfo.getExpiration(),mOssInfo.getSecurity_token());
                    //mOssService.setCallbackAddress(Config.callbackAddress);

                    // 2.把图片文件file上传到服务器
                    if(mOssInfo != null&&mOssService!=null){
                        mOssService.asyncUploadFile(mOssInfo.getDir()+mUploadFilePath.substring(mUploadFilePath.lastIndexOf(File.separator)+1),mUploadFilePath,null,LiveChatFragment.this);
                    }else{
                        ToastUtils.show("OSS初始化失败,请稍后再试");
                    }
                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test",exception.toString());
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

        oSSAKSKCredentialProvider = new OSSAKSKCredentialProvider(accessKey,accessKeySecret,securityToken,expiration);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSS oss = new OSSClient(getActivity().getApplicationContext(), endpoint,  new OSSStsTokenCredentialProvider(accessKey, accessKeySecret, securityToken), conf);
        OSSLog.enableLog();
        return new OssService(oss, bucket);

    }



    @Override
    public void onSuccess(PutObjectRequest request, PutObjectResult result) {

        sendPicture(mOssInfo.getHost()+"/"+mOssInfo.getDir()+mUploadFilePath.substring(mUploadFilePath.lastIndexOf(File.separator)+1));





    }

    @Override
    public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {

    }



    private void sendPicture(String pictureUrl){
        mMessageMap.clear();
        mMessageMap.put("room_id", ""+mRoomId);//直播间ID
        mMessageMap.put("user_id", User.getInstance().getId() + "");//用户ID
        mMessageMap.put("parent_id","0");
        mMessageMap.put("type", "2");//发布端类型：1.主持人   2：用户    3:通知关注  4：通知进入直播间
        mMessageMap.put("stream_name", mStreamName);//播间推流ID
        mMessageMap.put("nick_name", User.getInstance().getNickname());//昵称
        mMessageMap.put("message_type", "1");//消息类型 1.互动消息  2.直播状态更新消息  3.删除消息  4.题目 5.菜单设置 6图文保存  默认1
        mMessageMap.put("user_logo",User.getInstance().getAvatar());
        mMessageMap.put("message", pictureUrl);
        mMessageMap.put("content_type","2");
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
