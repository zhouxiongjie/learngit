package com.shuangling.software.dialog;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.entity.ZhifubaoAccountInfo;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class AccountSettingDialog extends BaseCircleDialog {



    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.account)
    EditText account;
    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.ok)
    TextView ok;

    Unbinder unbinder;

    private int mType;          //0 设置   1 修改
    private ZhifubaoAccountInfo mAccountInfo;

    private OnOkClickListener mOnOkClickListener;

    private Handler mHandler;

    public void setOnOkClickListener(OnOkClickListener onOkClickListener) {
        this.mOnOkClickListener = onOkClickListener;
    }


    public interface OnOkClickListener {
        void ok();

    }


    public static AccountSettingDialog getInstance(int type, ZhifubaoAccountInfo accountInfo) {
        AccountSettingDialog dialogFragment = new AccountSettingDialog();
        dialogFragment.setCanceledBack(false);
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(0.8f);
        dialogFragment.mType = type;
        dialogFragment.mAccountInfo = accountInfo;
        dialogFragment.mHandler=new Handler(Looper.getMainLooper());
        return dialogFragment;
    }



    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_account_setting, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        if(mType==0){
            title.setText("输入提现账号");
        }else{
            title.setText("修改账号");
            name.setText(mAccountInfo.getName());
            account.setText(mAccountInfo.getAccount());
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.cancel, R.id.ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();

                break;
            case R.id.ok:
                String username=name.getText().toString().trim();
                String zhifubaoAccount=account.getText().toString().trim();
                if(!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(zhifubaoAccount)){
                    bindAccount(username,zhifubaoAccount);
                }else {
                    ToastUtils.show("姓名和支付宝账号必填");
                }

                break;

        }

    }


    private void bindAccount(String name,String account) {

        String url = ServerInfo.emc + ServerInfo.bindZhifubao;
        Map<String, String> params = new HashMap<String, String>();

        params.put("name",name);
        params.put("account",account);
        OkHttpUtils.post(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {
                    final JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                dismiss();
                                if(mOnOkClickListener!=null){
                                    mOnOkClickListener.ok();
                                }
                            }
                        });
                    } else if (jsonObject != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                dismiss();
                                ToastUtils.show(jsonObject.getString("msg"));
                            }
                        });
                    }else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                dismiss();
                                ToastUtils.show("绑定账号失败");
                            }
                        });
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(Call call, Exception exception) {
                try{
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                dismiss();
                                ToastUtils.show("绑定账号失败");
                            }catch (Exception e){

                            }

                        }
                    });
                }catch (Exception e){

                }



            }
        });
    }

}
