package com.shuangling.software.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shuangling.software.R;
import com.shuangling.software.customview.ChatReplyInput;
import com.shuangling.software.customview.CircleIndicator;
import com.shuangling.software.interf.ChatAction;
import com.shuangling.software.utils.EmotionInputDetector;
import com.shuangling.software.utils.KeyBordUtil;

/**
 * 评论详情
 */
public class ReplyDialog extends DialogFragment implements ChatAction {
    //点击发表，内容不为空时的回调
    public SendListener sendListener;

    private Dialog dialog;
    private ChatReplyInput chatReplyInput;

    private View contentView;

    private String toWho;


    private DismissListener mListener;

    public void setDismissListener(DismissListener listener) {
        this.mListener = listener;
    }

    @Override
    public void sendImage() {

    }

    @Override
    public void joinRoom() {

    }

    @Override
    public void sendText(String str) {

    }

    public interface DismissListener {
        void onDismiss();
    }

    public void setToWho(String toWho) {
        this.toWho = toWho;
        chatReplyInput.getEditText().setHint(toWho);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
        dialog = new Dialog(getActivity(), R.style.Comment_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置Content前设定
        contentView = View.inflate(getActivity(), R.layout.chat_reply_dialog, null);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);// 外部点击取消

        String dialogType = this.getTag();

        // 设置宽度为屏宽，靠近屏幕底部
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;// 紧贴底部
        layoutParams.alpha = 1;
        layoutParams.dimAmount = 0.0f;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;// 宽度持平
        window.setAttributes(layoutParams);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        chatReplyInput = contentView.findViewById(R.id.chatReplyInput);
        assert dialogType != null;


        chatReplyInput.getEditText().setFocusable(true);
        chatReplyInput.getEditText().setFocusableInTouchMode(true);
        chatReplyInput.getEditText().requestFocus();

        return dialog;
    }





    public interface SendListener {
        void sendComment(String inputText);
    }

    public void setSendListener(SendListener sendListener) {
        this.sendListener = sendListener;
    }


    // 监听软件盘是否关闭的接口 ： 没有用到
    private void setSoftKeyBordListener(){
        KeyBordUtil.SoftKeyBoardListener softKeyBoardListener = new KeyBordUtil.SoftKeyBoardListener(getActivity());
        softKeyBoardListener.setListener(new KeyBordUtil.SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {

            }

            @Override
            public void keyBoardHide(int height) {
                dismiss();
            }
        });
    }

    // 重写 dismiss 设置延迟，否则会又残留dialog不能及时关闭
    @Override
    public void dismiss() {

        if (mListener != null){
            mListener.onDismiss();
        }
        chatReplyInput.getEditText().setText("");
        super.dismiss();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        if (mListener != null){
            mListener.onDismiss();
        }

        super.onDestroy();
    }
}
