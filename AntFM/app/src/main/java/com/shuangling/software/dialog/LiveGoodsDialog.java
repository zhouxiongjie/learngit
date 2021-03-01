package com.shuangling.software.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.params.ButtonParams;
import com.shuangling.software.R;
import com.shuangling.software.adapter.LiveGoodsAdapter;
import com.shuangling.software.entity.Comment;
import com.shuangling.software.entity.LiveGoodsInfo;
import com.shuangling.software.entity.LiveRoomInfo01;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ServerInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
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
public class LiveGoodsDialog extends BaseCircleDialog {


    Unbinder unbinder;

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.add_goods)
    TextView add_goods;

    LiveGoodsAdapter adapter;
    private LiveRoomInfo01.RoomInfoBean liveRoomInfo;
    private int currentPage = 1;

    private LiveGoodsInfo liveGoodsInfo;
    private OnChatEventListener mOnChatEventListener;

    public void setOnChatEventListener(OnChatEventListener onChatEventListener) {
        this.mOnChatEventListener = onChatEventListener;
    }

    public interface OnChatEventListener {

    }


    public static LiveGoodsDialog getInstance(LiveRoomInfo01.RoomInfoBean roomInfo) {
        LiveGoodsDialog liveGoodsDialog = new LiveGoodsDialog();
        liveGoodsDialog.setCanceledBack(true);
        liveGoodsDialog.setCanceledOnTouchOutside(true);
        liveGoodsDialog.setGravity(Gravity.BOTTOM);
        liveGoodsDialog.setWidth(1f);
        liveGoodsDialog.liveRoomInfo = roomInfo;
        return liveGoodsDialog;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_live_goods_list, container, false);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        //EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this, rootView);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);


        loadData();


        return rootView;
    }



    @Override
    public void onResume() {
        super.onResume();

    }


    private void loadData() {

        String url = ServerInfo.shop + "/v1/original_scenes/" +  liveRoomInfo.getId() + "/c_goods";

        Map<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("page_size", ""+Integer.MAX_VALUE);
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                JSONObject jsonObject = JSONObject.parseObject(response);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                    liveGoodsInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), LiveGoodsInfo.class);

                    if( liveGoodsInfo!= null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    title.setText("共" + liveGoodsInfo.getData().size() + "件商品" );
                                    adapter = new LiveGoodsAdapter(getContext(), liveGoodsInfo);
                                    recyclerView.setAdapter(adapter);
                                    adapter.setOnItemClickListener(new LiveGoodsAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(LiveGoodsInfo.DataBean seletedGoods) {
                                            if(seletedGoods.getMerchant_goods()!=null){
                                                Uri uri = Uri.parse(seletedGoods.getMerchant_goods().getRedirect_url());
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);
                                            }

                                        }

                                        @Override
                                        public void onItemDeleteClick(LiveGoodsInfo.DataBean seletedGoods) {

                                        }

                                    });
                                }catch (Exception e){

                                }




                            }
                        });

                    }


                }
            }
            @Override
            public void onFailure(Call call, Exception exception) {

            }
        });



    }



    @OnClick({R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                dismiss();
                break;

        }
    }

}
