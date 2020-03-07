package com.shuangling.software.dialog;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.activity.WebViewActivity;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class InformationDialog extends BaseCircleDialog {

    Unbinder unbinder;


    @BindView(R.id.useProtocol)
    TextView useProtocol;
    @BindView(R.id.and)
    TextView and;
    @BindView(R.id.secretProtocol)
    TextView secretProtocol;
    @BindView(R.id.protocol)
    LinearLayout protocol;
    @BindView(R.id.ok)
    TextView ok;


    private OnUpdateClickListener mOnUpdateClickListener;
    private boolean showNoUpdate = false;

    public void setOnUpdateClickListener(OnUpdateClickListener onUpdateClickListener) {
        this.mOnUpdateClickListener = onUpdateClickListener;
    }


    public interface OnUpdateClickListener {
        void download();

    }


    public static InformationDialog getInstance() {
        InformationDialog dialogFragment = new InformationDialog();
        dialogFragment.setCanceledBack(false);
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(0.8f);
        return dialogFragment;
    }



    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_information, container, false);
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
        if (!TextUtils.isEmpty(MyApplication.getInstance().useProtocolTitle)||!TextUtils.isEmpty(MyApplication.getInstance().secretProtocolTitle)){
            protocol.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(MyApplication.getInstance().useProtocolTitle)){
                useProtocol.setText("《"+MyApplication.getInstance().useProtocolTitle+"》");
            }
            if(!TextUtils.isEmpty(MyApplication.getInstance().secretProtocolTitle)){
                secretProtocol.setText("《"+MyApplication.getInstance().secretProtocolTitle+"》");
            }

            if(!TextUtils.isEmpty(MyApplication.getInstance().useProtocolTitle)&&!TextUtils.isEmpty(MyApplication.getInstance().secretProtocolTitle)){
                and.setVisibility(View.VISIBLE);

            }else{
                and.setVisibility(View.GONE);
            }
        }else{
            protocol.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.ok, R.id.useProtocol,R.id.secretProtocol})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ok:
                dismiss();
                break;
            case R.id.useProtocol: {
                Intent it = new Intent(getContext(), WebViewActivity.class);
                it.putExtra("url", ServerInfo.h5HttpsIP + "/qulity-info?type=2");
                startActivity(it);
            }

            break;
            case R.id.secretProtocol: {
                Intent it = new Intent(getContext(), WebViewActivity.class);
                it.putExtra("url", ServerInfo.h5HttpsIP + "/qulity-info?type=1");
                startActivity(it);
            }
            break;

        }

    }

}
