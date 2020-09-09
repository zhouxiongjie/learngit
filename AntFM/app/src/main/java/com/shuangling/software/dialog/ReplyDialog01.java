package com.shuangling.software.dialog;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.activity.NewLoginActivity;
import com.shuangling.software.adapter.ChatMessageListAdapter;
import com.shuangling.software.customview.ChatReplyInput;
import com.shuangling.software.entity.ChatMessage;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.fragment.LiveChatFragment;
import com.shuangling.software.interf.ChatAction;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.MyGlideEngine;
import com.shuangling.software.utils.ServerInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import okhttp3.Call;


/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class ReplyDialog01 extends BaseCircleDialog  implements ChatAction{

    private String postMessageUrl = ServerInfo.live + "/v3/push_message";

    Unbinder unbinder;
    @BindView(R.id.chatReplyInput)
    ChatReplyInput chatReplyInput;

    private String mRoomId;
    private String mStreamName;

    private ChatMessage chatMessage;
    private HashMap<String, String> mMessageMap = new HashMap<>();


    private OnAction mOnAction;


    public interface OnAction {
        void sendImage();
    }

    public void setOnAction(OnAction onAction) {
        this.mOnAction = onAction;
    }


    public static ReplyDialog01 getInstance(ChatMessage msg,String roomId,String streamName) {
        ReplyDialog01 dialogFragment = new ReplyDialog01();
        dialogFragment.setCanceledBack(true);
        dialogFragment.setCanceledOnTouchOutside(true);
        dialogFragment.setGravity(Gravity.BOTTOM);
        dialogFragment.setWidth(1f);

        dialogFragment.chatMessage=msg;
        dialogFragment.mRoomId=roomId;
        dialogFragment.mStreamName=streamName;
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.chat_reply_dialog, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

//    public void addChatMsg(ChatMessage msg) {
//        mChatMessageListAdapter.addChatMsg(msg);
//    }
//
//
//    public void addChatMsgs(List<ChatMessage> msgs) {
//
//        mChatMessageListAdapter.addChatMsgs(msgs);
//
//    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        chatReplyInput.setChatAction(this);
        chatReplyInput.getEditText().setHint(chatMessage.getNickName());
        chatReplyInput.getEditText().setFocusable(true);
        chatReplyInput.getEditText().setFocusableInTouchMode(true);
        chatReplyInput.getEditText().requestFocus();
        showSoftInputView();
        return rootView;
    }


    @Override
    public void sendImage() {
        if(mOnAction!=null){
            mOnAction.sendImage();
            dismiss();
        }
    }

    @Override
    public void joinRoom() {

    }

    @Override
    public void sendText(String str) {

        if(User.getInstance()==null){
            Intent it=new Intent(getContext(), NewLoginActivity.class);
            startActivity(it);
        }else{
            if (TextUtils.isEmpty(str)) return;
            chatReplyInput.setText("");
            mMessageMap.clear();
            mMessageMap.put("room_id", ""+mRoomId);//直播间ID
            mMessageMap.put("parent_id",""+chatMessage.getChatsId());
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

            dismiss();
        }
    }
}
