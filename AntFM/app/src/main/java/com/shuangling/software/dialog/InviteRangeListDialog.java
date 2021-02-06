package com.shuangling.software.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.adapter.LiveInviteListAdapter;
import com.shuangling.software.entity.InviteInfo;
import com.shuangling.software.entity.LiveRoomInfo;
import com.shuangling.software.entity.LiveRoomInfo01;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
public class InviteRangeListDialog extends BaseCircleDialog {


    Unbinder unbinder;

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.question)
    ImageView question;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.hotRangeLayout)
    RelativeLayout hotRangeLayout;
    @BindView(R.id.ruleLayout)
    LinearLayout ruleLayout;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.noData)
    LinearLayout noData;

    private LiveRoomInfo01.RoomInfoBean liveRoomInfo;

    private OnChatEventListener mOnChatEventListener;

    public void setOnChatEventListener(OnChatEventListener onChatEventListener) {
        this.mOnChatEventListener = onChatEventListener;
    }


    public interface OnChatEventListener {
        void sendImage();

        void sendText(String str);
    }


    public static InviteRangeListDialog getInstance(LiveRoomInfo01.RoomInfoBean roomInfo) {
        InviteRangeListDialog viewerListDialog = new InviteRangeListDialog();
        viewerListDialog.setCanceledBack(true);
        viewerListDialog.setCanceledOnTouchOutside(true);
        viewerListDialog.setGravity(Gravity.BOTTOM);
        viewerListDialog.setWidth(1f);
        viewerListDialog.liveRoomInfo = roomInfo;
        return viewerListDialog;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_invite_range_list, container, false);
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
        getInviteList();
        return rootView;
    }


    private void getInviteList() {
        String url = ServerInfo.live + "/v1/rooms/"+liveRoomInfo.getId()+"/invitation_list";
        Map<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("page_size", ""+Integer.MAX_VALUE);

        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                     if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        List<InviteInfo> inviteInfos = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), InviteInfo.class);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    if (inviteInfos.size() == 0) {
                                        noData.setVisibility(View.VISIBLE);
                                    } else {
                                        noData.setVisibility(View.GONE);

                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                        recyclerView.setLayoutManager(linearLayoutManager);
                                        recyclerView.setAdapter(new LiveInviteListAdapter(getContext(), inviteInfos));
                                    }
                                    } catch (Exception e) {
                                }
                            }
                        });
                    } else {
                        ToastUtils.show("数据请求失败");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                Log.e("test", exception.toString());
            }
        });
    }

    @OnClick({R.id.back, R.id.question})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                title.setText("热度榜");
                hotRangeLayout.setVisibility(View.VISIBLE);
                ruleLayout.setVisibility(View.GONE);
                question.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
                break;
            case R.id.question:
                title.setText("热度榜排名规则");
                hotRangeLayout.setVisibility(View.GONE);
                ruleLayout.setVisibility(View.VISIBLE);
                question.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
                break;
        }
    }

}
