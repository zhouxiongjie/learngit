package com.shuangling.software.dialog;


import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class UpdateDialog extends BaseCircleDialog {


    @BindView(R.id.update)
    TextView update;
    @BindView(R.id.version)
    TextView version;
    Unbinder unbinder;


    String mVersion;
    String mDesc;
    @BindView(R.id.desc)
    TextView desc;
    @BindView(R.id.noUpdate)
    TextView noUpdate;


    private OnUpdateClickListener mOnUpdateClickListener;
    private boolean showNoUpdate=false;

    public void setOnUpdateClickListener(OnUpdateClickListener onUpdateClickListener) {
        this.mOnUpdateClickListener = onUpdateClickListener;
    }


    public interface OnUpdateClickListener {
        void download();

    }


    public static UpdateDialog getInstance(String ver, String des) {
        UpdateDialog dialogFragment = new UpdateDialog();
        dialogFragment.setCanceledBack(false);
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(0.8f);
        dialogFragment.mVersion = ver;
        dialogFragment.mDesc = des;
        return dialogFragment;
    }


    public void showNoUpdate(boolean show){
        showNoUpdate=show;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_update_app, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        if(showNoUpdate){
            noUpdate.setVisibility(View.VISIBLE);
        }else{
            noUpdate.setVisibility(View.GONE);
        }
        version.setText(mVersion);
        desc.setText(mDesc);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.update,R.id.noUpdate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.update:
                dismiss();
                if (mOnUpdateClickListener != null) {
                    mOnUpdateClickListener.download();
                }
            break;
            case R.id.noUpdate:
                SharedPreferencesUtils.putPreferenceTypeValue("version", SharedPreferencesUtils.PreferenceType.String,mVersion);
                dismiss();
                break;

        }

    }

}
