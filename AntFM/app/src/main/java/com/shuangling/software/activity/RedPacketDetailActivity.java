package com.shuangling.software.activity;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.RedPacketAdapter;
import com.shuangling.software.entity.RedPacketDetailInfo;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

@EnableDragToClose()
public class RedPacketDetailActivity extends AppCompatActivity {


    public static final int MSG_GET_DETAIL = 0x1;


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
//    @BindView(R.id.statusBar)
//    View statusBar;

    private String mId;
    private RedPacketAdapter mAdapter;
    private DialogFragment mDialogFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_red_packet_detail);
        //ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(true).statusBarView(statusBar).init();
        //ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(true).statusBarColor()statusBarView(statusBar).init();
        ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(true).init();
        //ImmersionBar.with(this).statusBarView(R.id.statusBar).init();
        ButterKnife.bind(this);
        init();

    }

    private void init() {
        mId = getIntent().getStringExtra("id");
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.recycleview_divider_drawable));
        recyclerView.addItemDecoration(divider);

        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setEnableRefresh(false);

        getDetail();

    }


    public void getDetail() {

        mDialogFragment=CommonUtils.showLoadingDialog(getSupportFragmentManager());

        String url = ServerInfo.live + "/v1/get_red_bag_details_c";
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", mId);
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            mDialogFragment.dismiss();
                            JSONObject jsonObject = JSONObject.parseObject(response);
                            if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                                RedPacketDetailInfo redPacketDetailInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), RedPacketDetailInfo.class);

                                mAdapter = new RedPacketAdapter(RedPacketDetailActivity.this,redPacketDetailInfo);
                                recyclerView.setAdapter(mAdapter);


                            }

                        }catch (Exception e){

                        }


                    }
                });

            }

            @Override
            public void onFailure(Call call, Exception exception) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            mDialogFragment.dismiss();
                            ToastUtils.show("查询失败");
                        }catch (Exception e){

                        }


                    }
                });

            }
        });

    }



}
