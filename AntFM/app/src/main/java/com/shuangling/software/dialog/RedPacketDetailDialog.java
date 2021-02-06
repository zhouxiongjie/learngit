package com.shuangling.software.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.adapter.RedPacketAdapter;
import com.shuangling.software.adapter.RedPacketAdapter01;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.entity.RedPacketDetailInfo;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class RedPacketDetailDialog extends BaseCircleDialog {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.close)
    FontIconView close;
    @BindView(R.id.tip)
    TextView tip;

    private int mId;


    public static RedPacketDetailDialog getInstance(int id) {
        RedPacketDetailDialog dialogFragment = new RedPacketDetailDialog();
        dialogFragment.setCanceledBack(false);
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(0.8f);
        dialogFragment.mId = id;
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_red_packet_detail, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        getDetail();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public void getDetail() {

        String url = ServerInfo.live + "/v1/get_red_bag_details_c";
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", ""+mId);
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                JSONObject jsonObject = JSONObject.parseObject(response);
                                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                                    RedPacketDetailInfo redPacketDetailInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), RedPacketDetailInfo.class);
                                    RedPacketAdapter01 mAdapter = new RedPacketAdapter01(getContext(), redPacketDetailInfo);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                    DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                                    divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable02));
                                    recyclerView.addItemDecoration(divider);
                                    recyclerView.setAdapter(mAdapter);
                                }
                            } catch (Exception e) {
                            }
                        }
                    });
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(Call call, Exception exception) {
                try{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tip.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                            }
                        }
                    });
                }catch (Exception e){

                }
            }
        });
    }


    @OnClick({R.id.close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.close:
                dismiss();
                break;

        }
    }


}
