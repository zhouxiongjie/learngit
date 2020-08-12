package com.shuangling.software.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.shuangling.software.R;
import com.shuangling.software.entity.Anchor;
import com.shuangling.software.entity.ChatMessage;
import com.shuangling.software.fragment.LiveChatFragment;
import com.shuangling.software.utils.ChatMessageManager;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatMessageListAdapter extends RecyclerView.Adapter{

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_PICTURE = 2;

    private LayoutInflater inflater;
    //ArrayList<ChatMessage> msgList = new ArrayList<>();

    int directionType = 0; //0竖屏 1横屏

//    public void showChatMsg(ChatMessage msg) {
//        msgList.add(msg);
//        //notifyDataSetChanged();
//        notifyItemInserted(msgList.size());
//    }
//
//    public void addChatMsg(ChatMessage msg) {
//        msgList.add(msg);
//        notifyItemInserted(msgList.size());
//    }
//
//
//    public void addChatMsgs(List<ChatMessage> msgs) {
//
//        msgList.addAll(msgs);
//        notifyItemInserted(msgList.size());
//
//    }

//    public ChatMessageListAdapter(Context context,ArrayList<ChatMessage> msg, int directionType) {
//        this.directionType = directionType;
//        inflater = LayoutInflater.from(context);
//        msgList.addAll(msg);
//    }


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
            ChatMessage  msg = ChatMessageManager.getInstance().getMessageList().get(position - 1);

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
            ChatMessage  msg = ChatMessageManager.getInstance().getMessageList().get(position - 1);

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
        if (ChatMessageManager.getInstance().getMessageList().size() == 0) {
            return 1;
        } else {
            return ChatMessageManager.getInstance().getMessageList().size() + 1;
        }

//            return 10;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (ChatMessageManager.getInstance().getMessageList().get(position - 1).getContentType() == 1) {
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
