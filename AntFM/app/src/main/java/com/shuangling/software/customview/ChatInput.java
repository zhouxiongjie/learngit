package com.shuangling.software.customview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.svideo.common.utils.ToastUtils;
import com.shuangling.software.R;
import com.shuangling.software.interf.ChatAction;
import java.util.ArrayList;
import java.util.List;


/**
 * 聊天界面输入控件
 */
public class ChatInput extends RelativeLayout implements TextWatcher, View.OnClickListener {

    private static final String TAG = "ChatInput";


    EditText input;
    RelativeLayout textPanel;
    ImageButton btnAdd;
    LinearLayout btnImage;
    LinearLayout btnJoinRoom;
    LinearLayout morePanel;
    TextView btnSend;
    TextView mute;


    private InputMode inputMode = InputMode.NONE;
    private ChatAction chatAction;
    private boolean isSendVisible;
    private boolean isMuted=false;

    private final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    private Context context;


    public ChatInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.chat_input, this);
        initView();

    }


    private void initView() {
        input=findViewById(R.id.input);
        textPanel=findViewById(R.id.text_panel);
        btnAdd=findViewById(R.id.btn_add);
        btnImage=findViewById(R.id.btn_image);
        btnJoinRoom=findViewById(R.id.btn_join_room);
        morePanel=findViewById(R.id.morePanel);
        btnSend=findViewById(R.id.btn_send);
        mute=findViewById(R.id.mute);
        btnAdd.setOnClickListener(this);
        btnImage.setOnClickListener(this);
        btnJoinRoom.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        input.addTextChangedListener(this);
        input.setOnClickListener(this);
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    updateView(InputMode.TEXT);
                }
            }
        });
    }


    public void setJoinRoomVisible(boolean isVisible){
        if(isVisible){
            btnJoinRoom.setVisibility(VISIBLE);
        }else{
            btnJoinRoom.setVisibility(GONE);
        }
    }


    public void setMuted(boolean muted){
        isMuted=muted;
        if(isMuted){
            mute.setVisibility(VISIBLE);
            input.setVisibility(INVISIBLE);
        }else {
            input.setVisibility(VISIBLE);
            mute.setVisibility(GONE);
        }



    }



    private void updateView(InputMode mode) {
        if (mode == inputMode) return;
        leavingCurrentState();
        switch (inputMode = mode) {
            case MORE:
                morePanel.setVisibility(VISIBLE);
                break;
            case TEXT:
                if (input.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                }
                break;
            case VOICE:
                textPanel.setVisibility(GONE);
                break;
            case EMOTICON:
                break;
            case NONE:
                input.clearFocus();
                break;
        }
    }

    private void leavingCurrentState() {
        switch (inputMode) {
            case TEXT:
                //View view = ((Activity) getContext()).getCurrentFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                input.clearFocus();
                break;
            case MORE:
                morePanel.setVisibility(GONE);
                break;
            case VOICE:
                textPanel.setVisibility(VISIBLE);
                break;
            case EMOTICON:
                break;
        }
    }




    /**
     * 关联聊天界面逻辑
     */
    public void setChatAction(ChatAction chatAction) {
        this.chatAction = chatAction;
    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * are about to be replaced by new text with length <code>after</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * have just replaced old text that had length <code>before</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        isSendVisible = s != null && s.length() > 0;
        if (isSendVisible){
            btnAdd.setVisibility(GONE);
            btnSend.setVisibility(VISIBLE);
        }else{
            btnAdd.setVisibility(VISIBLE);
            btnSend.setVisibility(GONE);
        }
    }

    /**
     * This method is called to notify you that, somewhere within
     * <code>s</code>, the text has been changed.
     * It is legitimate to make further changes to <code>s</code> from
     * this callback, but be careful not to get yourself into an infinite
     * loop, because any changes you make will cause this method to be
     * called again recursively.
     * (You are not told where the change took place because other
     * afterTextChanged() methods may already have made other changes
     * and invalidated the offsets.  But if you need to know here,
     * you can use {@link Spannable#setSpan} in {@link #onTextChanged}
     * to mark your place and then look up from here where the span
     * ended up.
     *
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {

    }



    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Activity activity=null;
        try{
            activity= (Activity) getContext();
        }catch (Exception e){

        }

        int id = v.getId();
        switch (id) {
            case R.id.btn_send:
                if(isMuted){
                    com.hjq.toast.ToastUtils.show("禁言中");
                }else{
                    chatAction.sendText(input.getText().toString());
                }

                break;
            case R.id.btn_add:
                updateView(inputMode == InputMode.MORE ? InputMode.TEXT : InputMode.MORE);
                break;
            case R.id.btn_image:
                //if (activity != null && requestStorage(activity)) {
                if(isMuted){
                    com.hjq.toast.ToastUtils.show("禁言中");
                }else{
                    chatAction.sendImage();
                }

                //}
                break;
            case R.id.btn_join_room:
                if (activity != null){
                    requestVideo(activity);
                }
                chatAction.joinRoom();
                updateView(InputMode.NONE);

                break;
//            case R.id.input:{
//                updateView(InputMode.TEXT);
//                }
//                break;

        }


    }


    /**
     * 获取输入框文字
     */
    public Editable getText(){
        return input.getText();
    }


    /**
     * 获取输入框文字
     */
    public EditText getEditText(){
        return input;
    }

    /**
     * 设置输入框文字
     */
    public void setText(String text){
        input.setText(text);
    }


    /**
     * 设置输入模式
     */
    public void setInputMode(InputMode mode) {
        updateView(mode);
    }


    public enum InputMode {
        TEXT,
        VOICE,
        EMOTICON,
        MORE,
        VIDEO,
        NONE,
    }

    private boolean requestRtmp() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private boolean requestVideo(Activity activity) {
        if (afterM()) {
            final List<String> permissionsList = new ArrayList<>();
            if ((activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.CAMERA);
            if ((activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.RECORD_AUDIO);
            if (permissionsList.size() != 0) {
                activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
            int hasPermission = activity.checkSelfPermission(Manifest.permission.CAMERA);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean requestCamera(Activity activity) {
        if (afterM()) {
            int hasPermission = activity.checkSelfPermission(Manifest.permission.CAMERA);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean requestAudio(Activity activity) {
        if (afterM()) {
            int hasPermission = activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean requestStorage(Activity activity) {
        if (afterM()) {
            int hasPermission = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean afterM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    //录制视频计时器
    private class RecordCountDownTimer extends CountDownTimer {
        private long maxDuration;
        private long duration;

        public long getMaxDuration() {
            return maxDuration;
        }


        public long getDuration() {
            return duration;
        }


        RecordCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            maxDuration = millisInFuture;
            duration = 0;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            duration = maxDuration - millisUntilFinished;
        }

        @Override
        public void onFinish() {
            duration = maxDuration;


        }
    }

}
