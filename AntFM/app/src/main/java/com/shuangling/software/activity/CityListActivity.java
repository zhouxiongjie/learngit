package com.shuangling.software.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gyf.immersionbar.ImmersionBar;
import com.jaeger.library.StatusBarUtil;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.CitySortAdapter;
import com.shuangling.software.customview.SideBar;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.City;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.StatusBarManager;
import com.youngfeng.snake.annotations.EnableDragToClose;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

//@EnableDragToClose()
public class CityListActivity extends QMUIActivity/*AppCompatActivity*/ implements Handler.Callback {
    public static final String TAG = "CityListActivity";
    public static final int MSG_GET_CITY_LIST = 0x1;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.noData)
    LinearLayout noData;
    @BindView(R.id.sidebar)
    SideBar sidebar;
    @BindView(R.id.letters)
    TextView letters;
    @BindView(R.id.activity_title)
    /*TopTitleBar*/ QMUITopBarLayout activityTitle;
    private Handler mHandler;
    private CitySortAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        ButterKnife.bind(this);
//        CommonUtils.transparentStatusBar(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this); //
        activityTitle.addLeftImageButton(R.drawable.ic_left, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(view -> { //
            finish();
        });
        mHandler = new Handler(this);
        init();
        getCityList();
    }

    public void getCityList() {
        String url = ServerInfo.serviceIP + ServerInfo.getCityList;
        OkHttpUtils.get(url, null, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_GET_CITY_LIST;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    private void init() {
        if (MainActivity.sCurrentCity != null) {
//            activityTitle.setTitleText("当前城市-" + MainActivity.sCurrentCity.getName());
            activityTitle.setTitle("当前城市-" + MainActivity.sCurrentCity.getName());
        }
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);
        sidebar.setTextView(letters);
        sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    listView.setSelection(position);
                }
            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_CITY_LIST:
                String result = (String) msg.obj;
                try {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        JSONObject jo = jsonObject.getJSONObject("data");
                        List<City> cityList = new ArrayList<>();
                        for (char i = 'A'; i <= 'Z'; i++) {
                            if (jo.containsKey("" + i)) {
                                cityList.addAll(JSONArray.parseArray(jo.getJSONArray("" + i).toJSONString(), City.class));
                            }
                        }
                        Collections.sort(cityList, new PinyinComparator());
                        if (cityList.size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                        }
                        if (mAdapter == null) {
                            mAdapter = new CitySortAdapter(this, cityList);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    MainActivity.sCurrentCity = mAdapter.getItem(position);
                                    EventBus.getDefault().post(new CommonEvent("onLocationChanged"));
                                    finish();
                                }
                            });
//设置适配器
                            listView.setAdapter(mAdapter);
                        } else {
                            mAdapter.updateListView(cityList);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                }
                break;
        }
        return false;
    }

    public class PinyinComparator implements Comparator<City> {
        public int compare(City o1, City o2) {
            return o1.getInitials().compareTo(o2.getInitials());
        }
    }
}
