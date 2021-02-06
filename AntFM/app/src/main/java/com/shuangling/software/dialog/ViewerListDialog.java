package com.shuangling.software.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.adapter.LiveViewerListAdapter;
import com.shuangling.software.entity.LiveRoomInfo;
import com.shuangling.software.entity.LiveRoomInfo01;
import com.shuangling.software.entity.Viewer;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;


/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class ViewerListDialog extends BaseCircleDialog {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    Unbinder unbinder;


    private LiveRoomInfo01.RoomInfoBean liveRoomInfo;

    private OnChatEventListener mOnChatEventListener;

    public void setOnChatEventListener(OnChatEventListener onChatEventListener) {
        this.mOnChatEventListener = onChatEventListener;
    }

    public interface OnChatEventListener {
        void sendImage();

        void sendText(String str);
    }


    public static ViewerListDialog getInstance(LiveRoomInfo01.RoomInfoBean roomInfo) {
        ViewerListDialog viewerListDialog = new ViewerListDialog();
        viewerListDialog.setCanceledBack(true);
        viewerListDialog.setCanceledOnTouchOutside(true);
        viewerListDialog.setGravity(Gravity.BOTTOM);
        viewerListDialog.setWidth(1f);
        viewerListDialog.liveRoomInfo = roomInfo;
        return viewerListDialog;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_viewer_list, container, false);
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
        getViewerList();
        return rootView;
    }



    private void getViewerList() {
        String url = ServerInfo.live + "/v1/rooms/"+liveRoomInfo.getId()+"/online_users";
        Map<String, String> params = new HashMap<>();


        params.put("roomId", "" + liveRoomInfo.getId());

        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        List<Viewer> viewers  = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), Viewer.class);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                    recyclerView.setAdapter(new LiveViewerListAdapter(getContext(),viewers));
                                }catch (Exception e){

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


}
