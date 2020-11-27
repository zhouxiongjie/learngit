package com.shuangling.software.dialog;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
public class BindWeixinDialog extends BaseCircleDialog {


    Unbinder unbinder;

    @BindView(R.id.close)
    ImageView close;
    @BindView(R.id.bind)
    TextView bind;



    private OnBindClickListener mOnBindClickListener;

    private Handler mHandler;

    public void setOnBindClickListener(OnBindClickListener onBindClickListener) {
        this.mOnBindClickListener = onBindClickListener;
    }


    public interface OnBindClickListener {
        void bind();

    }


    public static BindWeixinDialog getInstance() {
        BindWeixinDialog dialogFragment = new BindWeixinDialog();
        dialogFragment.setCanceledBack(false);
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(0.8f);
        return dialogFragment;
    }


    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_bind_weixin, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        bind.setActivated(true);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.close,R.id.bind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.close:
                dismiss();

                break;
            case R.id.bind:
                if(mOnBindClickListener!=null){
                    mOnBindClickListener.bind();
                }
                dismiss();
                break;



        }

    }


}
