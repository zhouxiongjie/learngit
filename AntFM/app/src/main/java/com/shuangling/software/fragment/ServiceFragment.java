package com.shuangling.software.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.components.SimpleImmersionFragment;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shuangling.software.R;
import com.shuangling.software.activity.MainActivity;
import com.shuangling.software.adapter.ServerListAdapter;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.ServerCategory;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class ServiceFragment extends QMUIFragment/*SimpleImmersionFragment*/ implements Handler.Callback {
    public static final int MSG_GET_SERVERS = 0x00;
    @BindView(R.id.statusBar)
    View statusBar;
    @BindView(R.id.activtyTitle)
    TopTitleBar activtyTitle;
    @BindView(R.id.expandablelistview)
    ExpandableListView expandablelistview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.noScriptText)
    TextView noScriptText;
    @BindView(R.id.noData)
    LinearLayout noData;
    private Handler mHandler;
    //private Unbinder unbinder;
    private ServerListAdapter mListAdapter;
    private String mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(this);
        mTitle = getArguments().getString("title");
        EventBus.getDefault().register(this);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_service, container, false);
//        unbinder = ButterKnife.bind(this, view);
//        init();
//        return view;
//    }

    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_service, null);
        ButterKnife.bind(this, rootView);
        CommonUtils.setStatusHeight(getContext(),statusBar);
        QMUIStatusBarHelper.setStatusBarLightMode(getActivity());
        init();
        return rootView;
    }

    private void init() {
        activtyTitle.setTitleText(mTitle);
        TextView tv = activtyTitle.getTitleTextView();
        tv.setTextColor(getResources().getColor(R.color.white));
        getServers();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("onFontSizeChanged")) {
        }
    }

    private void getServers() {
        String url = ServerInfo.serviceIP + ServerInfo.getServices;
        Map<String, String> params = new HashMap<>();
        if (MainActivity.sCurrentCity != null) {
            params.put("city_code", "" + MainActivity.sCurrentCity.getCode());
        }
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_GET_SERVERS;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
//noData.setVisibility(VISIBLE);
            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_SERVERS:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        List<ServerCategory> serverCategorys = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ServerCategory.class);
                        Iterator<ServerCategory> iterator = serverCategorys.iterator();
                        while (iterator.hasNext()) {
                            ServerCategory serverCategory = iterator.next();
                            if (serverCategory.getService() == null || serverCategory.getService().size() == 0) {
                                iterator.remove();
                            }
                        }
                        if (mListAdapter == null) {
                            mListAdapter = new ServerListAdapter(getContext(), serverCategorys);
                            expandablelistview.setAdapter(mListAdapter);
                            for (int i = 0; i < mListAdapter.getGroupCount(); i++) {
                                expandablelistview.expandGroup(i);
                            }
                            expandablelistview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                                @Override
                                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                    return true;
                                }
                            });
                        } else {
                            mListAdapter.updateView(serverCategorys);
                            for (int i = 0; i < mListAdapter.getGroupCount(); i++) {
                                expandablelistview.expandGroup(i);
                            }
                            mListAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }
//
//    @Override
//    public void initImmersionBar() {
//        ImmersionBar.with(this).statusBarView(statusBar).init();
//    }

    @Override
    public void onDestroyView() {
        //unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
