package com.shuangling.software.utils;

import android.app.Activity;

import com.shuangling.software.entity.ChatMessage;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.event.MessageEvent;
import com.shuangling.software.event.PlayerEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 *
 * @author Zhouxj
 * @version 1.0
 * @created 2019-09-09
 */
public class ChatMessageManager {

    private static ChatMessageManager sChatMessageManager;

    public static ChatMessageManager getInstance(){
        if(sChatMessageManager==null){
            synchronized (ChatMessageManager.class){
                if (sChatMessageManager == null) {
                    sChatMessageManager = new ChatMessageManager();
                }
            }
        }
        return sChatMessageManager;
    }


    public ArrayList<ChatMessage> getMessageList() {
        return messageList;
    }

    public void addMessage(ChatMessage message) {
        this.messageList.add(message);
        EventBus.getDefault().post(new CommonEvent("refreshMessageList"));
    }

    public void addMessages(List<ChatMessage> messages) {
        this.messageList.addAll(messages);
        EventBus.getDefault().post(new CommonEvent("refreshMessageList"));
    }

    public void clearMessages(){
        this.messageList.clear();
    }

    private ArrayList<ChatMessage> messageList = new ArrayList<>();



}
