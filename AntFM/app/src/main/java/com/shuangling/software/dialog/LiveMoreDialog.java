package com.shuangling.software.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.entity.LiveRoomInfo01;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class LiveMoreDialog extends BaseCircleDialog {

    Unbinder unbinder;



    @BindView(R.id.share)
    LinearLayout share;
    @BindView(R.id.wallet)
    LinearLayout wallet;


    private LiveRoomInfo01.RoomInfoBean liveRoomInfo;

    private OnClickEventListener mOnClickEventListener;

    public LiveMoreDialog setOnClickEventListener(OnClickEventListener onClickEventListener) {
        this.mOnClickEventListener = onClickEventListener;
        return this;
    }


    public interface OnClickEventListener {

        void onShare();

        void wallet();


    }


    public static LiveMoreDialog getInstance(LiveRoomInfo01.RoomInfoBean roomInfo) {
        LiveMoreDialog liveMoreDialog = new LiveMoreDialog();
        liveMoreDialog.setCanceledBack(true);
        liveMoreDialog.setCanceledOnTouchOutside(true);
        liveMoreDialog.setGravity(Gravity.BOTTOM);
        liveMoreDialog.setWidth(1f);
        liveMoreDialog.liveRoomInfo = roomInfo;

        return liveMoreDialog;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_live_more, container, false);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);


        return rootView;
    }


    @OnClick({R.id.share, R.id.wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.share:
                dismiss();

                if (mOnClickEventListener != null) {
                    mOnClickEventListener.onShare();
                }
                break;
            case R.id.wallet:
                dismiss();

                if (mOnClickEventListener != null) {
                    mOnClickEventListener.wallet();
                }
                break;
        }
    }


}
