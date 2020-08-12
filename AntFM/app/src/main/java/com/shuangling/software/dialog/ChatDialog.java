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
import android.widget.RelativeLayout;

import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.activity.NewLoginActivity;
import com.shuangling.software.activity.RoomActivity;
import com.shuangling.software.adapter.ChatMessageListAdapter;
import com.shuangling.software.customview.ChatInput;
import com.shuangling.software.entity.ChatMessage;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.event.MessageEvent;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import okhttp3.Call;


/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class ChatDialog extends BaseCircleDialog implements ChatAction, View.OnClickListener {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.input_panel)
    ChatInput inputPanel;
    @BindView(R.id.selectionsLayout)
    RelativeLayout selectionsLayout;
    Unbinder unbinder;

    private OnChatEventListener mOnChatEventListener;

    public void setOnChatEventListener(OnChatEventListener onChatEventListener) {
        this.mOnChatEventListener = onChatEventListener;
    }

    public interface OnChatEventListener {
        void sendImage();
        void sendText(String str);
    }


    private ChatMessageListAdapter mChatMessageListAdapter;

    public static ChatDialog getInstance() {
        ChatDialog dialogFragment = new ChatDialog();
        dialogFragment.setCanceledBack(true);
        dialogFragment.setCanceledOnTouchOutside(true);
        dialogFragment.setGravity(Gravity.BOTTOM);
        dialogFragment.setWidth(1f);
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_chat, container, false);
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
    public void onClick(View v) {

    }


    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);

        EventBus.getDefault().register(this);

        inputPanel.setChatAction(this );
        mChatMessageListAdapter = new ChatMessageListAdapter(getContext(), 1);
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

        return rootView;
    }

    @Override
    public void sendImage() {

        if(mOnChatEventListener!=null){
            mOnChatEventListener.sendImage();
        }
    }

    @Override
    public void joinRoom() {

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("refreshMessageList")) {
           if(mChatMessageListAdapter!=null){
               mChatMessageListAdapter.notifyDataSetChanged();
           }
        }
    }

    @Override
    public void sendText(String str) {
        if(User.getInstance()==null){
            Intent it=new Intent(getContext(), NewLoginActivity.class);
            it.putExtra("jump_url",ServerInfo.h5IP);
            startActivity(it);
        }else {
            if(mOnChatEventListener!=null){
                inputPanel.setText("");
                mOnChatEventListener.sendText(str);
            }
        }

    }

}
