package com.shuangling.software.dialog;


import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.customview.FontIconView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class RedPacketComingDialog extends BaseCircleDialog {


    Unbinder unbinder;


    @BindView(R.id.close)
    FontIconView close;
    @BindView(R.id.remainTime)
    TextView remainTime;

    //private CountDownTimer mCountDownTimer;
    private OnCloseListener mOnCloseListener;


    public RedPacketComingDialog setOnCloseListener(OnCloseListener onCloseListener) {
        this.mOnCloseListener = onCloseListener;
        return this;
    }


    public interface OnCloseListener {
        void onClose();

    }


    public static RedPacketComingDialog getInstance() {
        RedPacketComingDialog dialogFragment = new RedPacketComingDialog();
        dialogFragment.setCanceledBack(false);
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(0.8f);
        return dialogFragment;
    }


    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_red_packet_coming, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
//        mCountDownTimer = new CountDownTimer(10 * 1000, 500) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                if(millisUntilFinished / 1000==0){
//                    remainTime.setText("1");
//                }else{
//                    remainTime.setText("" + millisUntilFinished / 1000);
//                }
//
//
//            }
//
//            @Override
//            public void onFinish() {
//
//                if(mOnFinishListener!=null){
//                    mOnFinishListener.onFinish();
//                }
//
//            }
//        };
//        mCountDownTimer.start();
        return rootView;
    }


    public void setRemainTime(String time){
        if(remainTime!=null){
            remainTime.setText(time);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.close:
                if(mOnCloseListener!=null){
                    mOnCloseListener.onClose();
                }
                dismiss();
                //mCountDownTimer.cancel();
                break;


        }

    }


}
