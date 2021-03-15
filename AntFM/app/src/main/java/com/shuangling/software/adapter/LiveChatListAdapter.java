package com.shuangling.software.adapter;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ReplacementSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.previewlibrary.GPreviewBuilder;
import com.shuangling.software.R;
import com.shuangling.software.entity.ChatMessage;
import com.shuangling.software.entity.ImageInfo;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveChatListAdapter extends RecyclerView.Adapter{

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_PICTURE = 2;

    private LayoutInflater inflater;
    private Activity mContext;
    private OnItemReply mOnItemReply;
    ArrayList<ChatMessage> mChatMessages = new ArrayList<>();

    public interface OnItemReply {
        void replyItem(ChatMessage message);
    }

    public void setOnItemReply(OnItemReply onItemReply) {
        this.mOnItemReply = onItemReply;
    }




    public LiveChatListAdapter(Activity context) {

        mContext = context;
        inflater = LayoutInflater.from(context);
    }


    public void showChatMessage(ChatMessage msg) {
        mChatMessages.add(msg);
        notifyDataSetChanged();
    }

    public void showChatMessages(List<ChatMessage> msgs) {
        mChatMessages.addAll(msgs);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(inflater.inflate(R.layout.chat_msg_header01, viewGroup, false));
        } else if(viewType == TYPE_TEXT) {
            return new TextViewHolder(inflater.inflate(R.layout.chat_msg_text01, viewGroup, false));
        } else {
            return new PictureViewHolder(inflater.inflate(R.layout.chat_msg_text01, viewGroup, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        int viewType = getItemViewType(position);
        if (viewType == TYPE_HEADER) {

        } else if (viewType == TYPE_TEXT) {


            TextViewHolder vh = (TextViewHolder) viewHolder;
            vh.root.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    return false;
                }
            });
            ChatMessage msg = mChatMessages.get(position - 1);

            if (!TextUtils.isEmpty(msg.getUserLog())) {
                Uri uri = Uri.parse(msg.getUserLog());
                int width = CommonUtils.dip2px(30);
                int height = width;
                ImageLoader.showThumb(uri, vh.head, width, height);
            } else {
                ImageLoader.showThumb(vh.head, R.drawable.ic_user3);
            }

            if (msg.getType() == 1) {
                //主持人

                SpannableString spannableString = new SpannableString("主播"+" "+msg.getNickName()+"："+msg.getMsg());

                BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.parseColor("#1890FF"));
                //spannableString.setSpan(backgroundColorSpan, 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new RelativeSizeSpan(0.7f), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new RoundBackgroundColorSpan(Color.parseColor("#1890FF"),Color.parseColor("#F7F7F7")),0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#FFD873"));
                if(msg.getNickName()!=null){
                    spannableString.setSpan(foregroundColorSpan, 3, msg.getNickName().length()+4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }

                vh.content.setText(spannableString);

            } else {

                SpannableString spannableString = new SpannableString(msg.getNickName()+"："+msg.getMsg());

                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#FFD873"));
                if(msg.getNickName()!=null){
                    spannableString.setSpan(foregroundColorSpan, 0, msg.getNickName().length()+1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                vh.content.setText(spannableString);

            }


            if (msg.getParentMsgInfo() != null) {
                vh.parentContent.setVisibility(View.VISIBLE);
                vh.divider.setVisibility(View.VISIBLE);

                if (msg.getParentMsgInfo().getContentType() == 1) {

                    vh.parentContent.setText("「"+msg.getParentMsgInfo().getNickName()+"：" + msg.getParentMsgInfo().getMsg().trim() + "」");
                } else {

                    SpannableString spannableString = new SpannableString("「"+msg.getParentMsgInfo().getNickName()+"：" + "图片" + "」");

                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.placeholder_message_picture);
                    drawable.setBounds(0, 0, CommonUtils.dip2px(29), CommonUtils.dip2px(17));
                    ImageSpan imageSpan = new ImageSpan(drawable);
                    spannableString.setSpan(imageSpan, 2+msg.getParentMsgInfo().getNickName().length(), spannableString.length()-1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                    ClickableSpan clickableSpan=new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            ArrayList<ImageInfo> images = new ArrayList<>();
                            ImageInfo image = new ImageInfo(msg.getParentMsgInfo().getMsg());
                            images.add(image);
                            Rect bounds = new Rect();
                            vh.parentContent.getGlobalVisibleRect(bounds);
                            image.setBounds(bounds);

                            GPreviewBuilder.from(mContext)
                                    .setData(images)
                                    .setCurrentIndex(0)
                                    .setDrag(true, 0.6f)
                                    .setSingleFling(true)
                                    .setType(GPreviewBuilder.IndicatorType.Number)
                                    .start();
                        }
                        @Override
                        public void updateDrawState(TextPaint ds) {

                        }
                    };

                    spannableString.setSpan(clickableSpan, 2+msg.getParentMsgInfo().getNickName().length(), spannableString.length()-1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    vh.parentContent.setMovementMethod(LinkMovementMethod.getInstance());
                    vh.parentContent.setHighlightColor(Color.parseColor("#36969696"));
                    vh.parentContent.setText(spannableString);



                }
            } else {
                vh.parentContent.setVisibility(View.GONE);
                vh.divider.setVisibility(View.GONE);
            }

        } else if (getItemViewType(position) == TYPE_PICTURE) {
            final PictureViewHolder vh = (PictureViewHolder) viewHolder;
            vh.root.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
//                    CommonUtils.hideInput(mContext);
                    return false;
                }
            });
            ChatMessage msg = mChatMessages.get(position - 1);

            if (!TextUtils.isEmpty(msg.getUserLog())) {
                Uri uri = Uri.parse(msg.getUserLog());
                int width = CommonUtils.dip2px(30);
                int height = width;
                ImageLoader.showThumb(uri, vh.head, width, height);

            } else {
                ImageLoader.showThumb(vh.head, R.drawable.ic_user3);
            }


            if (msg.getType() == 1) {
                //主持人

                SpannableString spannableString = new SpannableString("主播"+" "+msg.getNickName()+"："+"图片");

                BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.parseColor("#1890FF"));
                //spannableString.setSpan(backgroundColorSpan, 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new RelativeSizeSpan(0.7f), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new RoundBackgroundColorSpan(Color.parseColor("#1890FF"),Color.parseColor("#F7F7F7")),0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#FFD873"));
                spannableString.setSpan(foregroundColorSpan, 3, msg.getNickName().length()+4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                Drawable drawable = mContext.getResources().getDrawable(R.drawable.placeholder_message_picture);
                drawable.setBounds(0, 0, CommonUtils.dip2px(29), CommonUtils.dip2px(17));
                ImageSpan imageSpan = new ImageSpan(drawable);
                spannableString.setSpan(imageSpan, 5+msg.getNickName().length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                ClickableSpan clickableSpan=new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        ArrayList<ImageInfo> images = new ArrayList<>();
                        ImageInfo image = new ImageInfo(msg.getMsg());
                        images.add(image);
                        Rect bounds = new Rect();
                        vh.content.getGlobalVisibleRect(bounds);
                        image.setBounds(bounds);

                        GPreviewBuilder.from(mContext)
                                .setData(images)
                                .setCurrentIndex(0)
                                .setDrag(true, 0.6f)
                                .setSingleFling(true)
                                .setType(GPreviewBuilder.IndicatorType.Number)
                                .start();
                    }
                    @Override
                    public void updateDrawState(TextPaint ds) {

                    }
                };

                spannableString.setSpan(clickableSpan, 4+msg.getNickName().length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                vh.content.setMovementMethod(LinkMovementMethod.getInstance());
                vh.content.setHighlightColor(Color.parseColor("#36969696"));
                vh.content.setText(spannableString);

            } else {

                SpannableString spannableString = new SpannableString(msg.getNickName()+"："+"图片");

                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#FFD873"));
                spannableString.setSpan(foregroundColorSpan, 0, msg.getNickName().length()+1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                vh.content.setText(spannableString);

                Drawable drawable = mContext.getResources().getDrawable(R.drawable.placeholder_message_picture);
                drawable.setBounds(0, 0, CommonUtils.dip2px(29), CommonUtils.dip2px(17));
                ImageSpan imageSpan = new ImageSpan(drawable);
                spannableString.setSpan(imageSpan, msg.getNickName().length()+2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);


                ClickableSpan clickableSpan=new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        ArrayList<ImageInfo> images = new ArrayList<>();
                        ImageInfo image = new ImageInfo(msg.getMsg());
                        images.add(image);
                        Rect bounds = new Rect();
                        vh.content.getGlobalVisibleRect(bounds);
                        image.setBounds(bounds);

                        GPreviewBuilder.from(mContext)
                                .setData(images)
                                .setCurrentIndex(0)
                                .setDrag(true, 0.6f)
                                .setSingleFling(true)
                                .setType(GPreviewBuilder.IndicatorType.Number)
                                .start();
                    }
                    @Override
                    public void updateDrawState(TextPaint ds) {

                    }
                };

                spannableString.setSpan(clickableSpan, 2+msg.getNickName().length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                vh.content.setMovementMethod(LinkMovementMethod.getInstance());
                vh.content.setHighlightColor(Color.parseColor("#36969696"));
                vh.content.setText(spannableString);
            }




            if (msg.getParentMsgInfo() != null) {
                vh.parentContent.setVisibility(View.VISIBLE);
                vh.divider.setVisibility(View.VISIBLE);

                if (msg.getParentMsgInfo().getContentType() == 1) {

                    vh.parentContent.setText("「"+msg.getParentMsgInfo().getNickName()+"：" + msg.getParentMsgInfo().getMsg().trim() + "」");
                } else {

                    SpannableString spannableString = new SpannableString("「"+msg.getParentMsgInfo().getNickName()+"：" + "图片" + "」");

                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.placeholder_message_picture);
                    drawable.setBounds(0, 0, CommonUtils.dip2px(29), CommonUtils.dip2px(17));
                    ImageSpan imageSpan = new ImageSpan(drawable);
                    spannableString.setSpan(imageSpan, 2+msg.getParentMsgInfo().getNickName().length(), spannableString.length()-1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                    ClickableSpan clickableSpan=new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            ArrayList<ImageInfo> images = new ArrayList<>();
                            ImageInfo image = new ImageInfo(msg.getParentMsgInfo().getMsg());
                            images.add(image);
                            Rect bounds = new Rect();
                            vh.parentContent.getGlobalVisibleRect(bounds);
                            image.setBounds(bounds);

                            GPreviewBuilder.from(mContext)
                                    .setData(images)
                                    .setCurrentIndex(0)
                                    .setDrag(true, 0.6f)
                                    .setSingleFling(true)
                                    .setType(GPreviewBuilder.IndicatorType.Number)
                                    .start();
                        }
                        @Override
                        public void updateDrawState(TextPaint ds) {

                        }
                    };

                    spannableString.setSpan(clickableSpan, 2+msg.getParentMsgInfo().getNickName().length(), spannableString.length()-1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    vh.parentContent.setMovementMethod(LinkMovementMethod.getInstance());
                    vh.parentContent.setHighlightColor(Color.parseColor("#36969696"));
                    vh.parentContent.setText(spannableString);



                }
            } else {
                vh.parentContent.setVisibility(View.GONE);
                vh.divider.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public int getItemCount() {
        if (mChatMessages.size() == 0) {
            return 1;
        } else {
            return mChatMessages.size() + 1;
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (mChatMessages.get(position - 1).getContentType() == 1) {
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
        @BindView(R.id.root)
        LinearLayout root;
        @BindView(R.id.head)
        SimpleDraweeView head;
        @BindView(R.id.parentContent)
        TextView parentContent;
        @BindView(R.id.divider)
        ImageView divider;
        @BindView(R.id.content)
        TextView content;


        TextViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    static class PictureViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.root)
        LinearLayout root;
        @BindView(R.id.head)
        SimpleDraweeView head;
        @BindView(R.id.parentContent)
        TextView parentContent;
        @BindView(R.id.divider)
        ImageView divider;
        @BindView(R.id.content)
        TextView content;

        PictureViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }



    public class RoundBackgroundColorSpan extends ReplacementSpan {
        private int bgColor;
        private int textColor;
        private int padingLeftRight=8;
        public RoundBackgroundColorSpan(int bgColor, int textColor) {
            super();
            this.bgColor = bgColor;
            this.textColor = textColor;
        }
        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            //设置宽度为文字宽度加16dp
            return ((int)paint.measureText(text, start, end)+CommonUtils.dip2px(padingLeftRight));
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            int originalColor = paint.getColor();
            paint.setColor(this.bgColor);
            //画圆角矩形背景
            canvas.drawRoundRect(new RectF(x,
                            top,
                            x + ((int) paint.measureText(text, start, end)+ CommonUtils.dip2px(padingLeftRight)),
                            bottom),

                    CommonUtils.dip2px(2),
                    CommonUtils.dip2px(2),
                    paint);
            paint.setColor(this.textColor);
            //画文字,两边各增加8dp
            canvas.drawText(text, start, end, x+CommonUtils.dip2px(padingLeftRight/2), y-CommonUtils.dip2px(2), paint);
            //将paint复原
            paint.setColor(originalColor);
        }
    }
}
