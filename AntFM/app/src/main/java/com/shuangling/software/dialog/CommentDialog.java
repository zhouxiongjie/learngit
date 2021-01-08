package com.shuangling.software.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;
import androidx.core.widget.NestedScrollView;

import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.svideo.common.utils.ToastUtils;
import com.shuangling.software.R;
import com.shuangling.software.customview.CircleIndicator;
import com.shuangling.software.utils.EmotionInputDetector;
import com.shuangling.software.utils.KeyBordUtil;

/**
 * Created by 你是我的 on 2018/12/30.
 */
public class CommentDialog extends DialogFragment implements View.OnClickListener {
    //点击发表，内容不为空时的回调
    public SendListener sendListener;
    Dialog dialog;
    EditText editText;
    TextView tvIssueSend;
    CheckBox expressionInputIcon;
    CircleIndicator circleIndicator;
    ViewPager viewPager;
    LinearLayout llEmojiInput;
    private EmotionInputDetector detector;
    NestedScrollView nestedScrollView;
    View contentView;
    String mUserName;
    private DismissListener mListener;

    public void setDismissListener(DismissListener listener) {
        this.mListener = listener;
    }

    public interface DismissListener {
        void onDismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
        dialog = new Dialog(getActivity(), R.style.Comment_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置Content前设定
        contentView = View.inflate(getActivity(), R.layout.comment_dialog, null);
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
        editText = contentView.findViewById(R.id.edit_text);
        tvIssueSend = contentView.findViewById(R.id.tv_issue);
        assert dialogType != null;
        if (dialogType.contains("video")) {
            //   nestedScrollView = getActivity().findViewById(R.id.nested_scroll_view_video);
        } else {
            //  nestedScrollView = getActivity().findViewById(R.id.nested_scroll_view);
        }
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        if (mUserName != null) {
            editText.setHint("@" + mUserName);
        } else {
            editText.setHint("优质评论将会被优先展示");
        }
        final Handler handler = new Handler();
//        //关掉dialog 关掉键盘
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialogInterface) {
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        KeyBordUtil.hideInput(getActivity(),contentView);
//                    }
//                },200);
//            }
//        });
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        KeyBordUtil.hideInput(getActivity(),contentView);
//                    }
//                },200);
//            }
//        });
        tvIssueSend.setOnClickListener(this);
        /*  setSoftKeyBordListener();*/ // 设置监听软件盘是否关闭
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_issue:
                if (editText.getText().length() == 0) {
                    ToastUtils.show(getContext(), "评论不能为空");
                    return;
                }
                sendListener.sendComment(editText.getText().toString());
                break;
        }
    }

    public interface SendListener {
        void sendComment(String inputText);
    }

    public void setSendListener(SendListener sendListener, String userName) {
        this.sendListener = sendListener;
        mUserName = userName;
        if (editText != null) {
            if (userName != null) {
                editText.setHint("@" + userName);
            } else {
                editText.setHint("优质评论将会被优先展示");
            }
        }
    }

    // 监听软件盘是否关闭的接口 ： 没有用到
    private void setSoftKeyBordListener() {
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
        if (mListener != null) {
            mListener.onDismiss();
        }
//        new Thread(){
//            @Override
//            public void run() {
//                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        dialog.dismiss();
//                    }
//                });
//            }
//        };
        editText.setText("");
        super.dismiss();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        if (mListener != null) {
            mListener.onDismiss();
        }
        super.onDestroy();
    }
}
