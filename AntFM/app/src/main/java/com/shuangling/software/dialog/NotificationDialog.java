package com.shuangling.software.dialog;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class NotificationDialog extends BaseCircleDialog {


    Unbinder unbinder;

    @BindView(R.id.openNotification)
    TextView openNotification;


    private OnOkClickListener mOnOkClickListener;

    private Handler mHandler;

    public NotificationDialog setOnOkClickListener(OnOkClickListener onOkClickListener) {
        this.mOnOkClickListener = onOkClickListener;
        return this;
    }


    public interface OnOkClickListener {
        void openNoticafition();

    }


    public static NotificationDialog getInstance() {
        NotificationDialog dialogFragment = new NotificationDialog();
        dialogFragment.setCanceledBack(false);
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(0.8f);
        return dialogFragment;
    }


    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_notification, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        openNotification.setActivated(true);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.openNotification})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.openNotification:
                dismiss();
                if(mOnOkClickListener!=null){
                    mOnOkClickListener.openNoticafition();
                }
                break;


        }

    }


}
