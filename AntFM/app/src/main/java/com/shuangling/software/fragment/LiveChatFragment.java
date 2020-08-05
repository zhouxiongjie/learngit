package com.shuangling.software.fragment;

import android.content.Context;
import android.content.Intent;
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
import com.shuangling.software.activity.RoomActivity;
import com.shuangling.software.customview.ChatInput;
import com.shuangling.software.entity.ChatMessage;
import com.shuangling.software.entity.User;
import com.shuangling.software.interf.ChatAction;
import com.shuangling.software.network.MyEcho;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;

import net.mrbin99.laravelechoandroid.EchoCallback;
import net.mrbin99.laravelechoandroid.EchoOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;


public class LiveChatFragment extends Fragment implements ChatAction {


    private String postMessageUrl = ServerInfo.live + "/v1/push_message_c";

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
        cancelInteract(""+mRoomId);
        unbinder.unbind();
    }

    @Override
    public void sendImage() {

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


        if (TextUtils.isEmpty(str)) return;
        inputPanel.setText("");

        mMessageMap.put("message", str);
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


        mMessageMap.put("room_id", ""+mRoomId);//直播间ID
        mMessageMap.put("user_id", User.getInstance().getId() + "");//用户ID
        mMessageMap.put("message", "");//消息内容
        mMessageMap.put("type", "2");//发布端类型：1.主持人   2：用户    3:通知关注  4：通知进入直播间
        mMessageMap.put("stream_name", mStreamName);//播间推流ID
        mMessageMap.put("nick_name", User.getInstance().getNickname());//昵称
        mMessageMap.put("message_type", "1");//消息类型 1.互动消息  2.直播状态更新消息  3.删除消息  4.题目 5.菜单设置 6图文保存  默认1
        mMessageMap.put("user_logo",User.getInstance().getAvatar());

        EchoOptions options = new EchoOptions();
        options.host = "http://echo-live.review.slradio.cn";
        options.headers.put("Authorization", User.getInstance().getAuthorization());
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




}
