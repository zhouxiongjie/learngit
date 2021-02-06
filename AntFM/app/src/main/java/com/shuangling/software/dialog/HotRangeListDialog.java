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
import com.shuangling.software.adapter.LiveHotListAdapter;
import com.shuangling.software.entity.HotInfo;
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
public class HotRangeListDialog extends BaseCircleDialog {


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


    public static HotRangeListDialog getInstance(LiveRoomInfo01.RoomInfoBean roomInfo) {
        HotRangeListDialog viewerListDialog = new HotRangeListDialog();
        viewerListDialog.setCanceledBack(true);
        viewerListDialog.setCanceledOnTouchOutside(true);
        viewerListDialog.setGravity(Gravity.BOTTOM);
        viewerListDialog.setWidth(1f);
        viewerListDialog.liveRoomInfo = roomInfo;
        return viewerListDialog;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_hot_range_list, container, false);
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
        getHotList();
        return rootView;
    }


    private void getHotList() {
        String url = ServerInfo.live + "/v1/consumer/hot_rooms";
        Map<String, String> params = new HashMap<>();


//        params.put("roomId", "" + liveRoomInfo.getId());

        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        List<HotInfo> hotInfos = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), HotInfo.class);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    if (hotInfos.size() == 0) {
                                        noData.setVisibility(View.VISIBLE);
                                    } else {
                                        noData.setVisibility(View.GONE);
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                        recyclerView.setLayoutManager(linearLayoutManager);
                                        recyclerView.setAdapter(new LiveHotListAdapter(getContext(), hotInfos));
                                    }


                                } catch (Exception e) {

                                }

                            }
                        });


                    } else {
                        ToastUtils.show("获取观众列表失败");
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
