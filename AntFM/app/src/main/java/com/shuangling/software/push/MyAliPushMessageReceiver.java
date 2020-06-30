package com.shuangling.software.push;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.activity.ArticleDetailActivity02;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.activity.SpecialDetailActivity;
import com.shuangling.software.activity.VideoDetailActivity;
import com.shuangling.software.activity.WebViewBackActivity;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Created by Administrator on 2018-12-17.
 */

public class MyAliPushMessageReceiver extends MessageReceiver {
    // 消息接收部分的LOG_TAG
    public static final String REC_TAG = "receiver";
    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        // TODO 处理推送通知
        Log.e("MyMessageReceiver", "Receive notification, title: " + title + ", summary: " + summary + ", extraMap: " + extraMap);
    }
    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        Log.e("MyMessageReceiver", "onMessage, messageId: " + cPushMessage.getMessageId() + ", title: " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
    }
    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        Log.e("MyMessageReceiver", "onNotificationOpened, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);

//        JSONObject jsonObject = JSONObject.parseObject(extraMap);
//        Intent intent = new Intent(context,WebViewBackActivity.class);
//        intent.putExtra("title",title);
//        intent.putExtra("url",jsonObject.getString("url"));
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
        long sd=Thread.currentThread().getId();

//        Intent intent = new Intent(context, ArticleDetailActivity02.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("articleId", 28210);
//        context.startActivity(intent);

    }
    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        Log.e("MyMessageReceiver", "onNotificationClickedWithNoAction, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
        JSONObject jsonObject = JSONObject.parseObject(extraMap);
//        Intent intent = new Intent(context,WebViewBackActivity.class);
//        intent.putExtra("title",title);
//        intent.putExtra("url",jsonObject.getString("url"));
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
        String type=jsonObject.getString("target_type");
        String id=jsonObject.getString("post_id");

        long sd=Thread.currentThread().getId();

        if(type.equals("2")){
            //专辑
            Intent intent = new Intent(context, AlbumDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("albumId", Integer.parseInt(id));
            context.startActivity(intent);
        }else if(type.equals("3")){
            //文章
            Intent intent = new Intent(context, ArticleDetailActivity02.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("articleId", Integer.parseInt(id));
            context.startActivity(intent);
        }else if(type.equals("4")){
            //视频
            Intent intent = new Intent(context, VideoDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("videoId", Integer.parseInt(id));
            context.startActivity(intent);
        }else if(type.equals("5")){
            //专题
            Intent intent = new Intent(context, SpecialDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("specialId", Integer.parseInt(id));
            context.startActivity(intent);
        }else if(type.equals("6")){
            //图集
            Intent intent = new Intent(context, GalleriaActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("galleriaId", Integer.parseInt(id));
            context.startActivity(intent);
        }

    }
    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        Log.e("MyMessageReceiver", "onNotificationReceivedInApp, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap + ", openType:" + openType + ", openActivity:" + openActivity + ", openUrl:" + openUrl);
    }
    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        Log.e("MyMessageReceiver", "onNotificationRemoved");
    }
}
