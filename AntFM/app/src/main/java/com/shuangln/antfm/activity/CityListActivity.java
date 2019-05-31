package com.shuangln.antfm.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jaeger.library.StatusBarUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shuangln.antfm.R;
import com.shuangln.antfm.adapter.CitySortAdapter;
import com.shuangln.antfm.customview.SideBar;
import com.shuangln.antfm.entity.City;
import com.shuangln.antfm.network.OkHttpCallback;
import com.shuangln.antfm.network.OkHttpUtils;
import com.shuangln.antfm.utils.ServerInfo;
import com.shuangln.antfm.utils.StatusBarManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;


public class CityListActivity extends AppCompatActivity implements Handler.Callback {

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


    private Handler mHandler;
    private CitySortAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        StatusBarManager.setImmersiveStatusBar(this, true);
        mHandler=new Handler(this);
        init();
        getCityList();
    }


    public void getCityList() {

        String url = ServerInfo.serviceIP + ServerInfo.getCityList;
        OkHttpUtils.get(url, null, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_CITY_LIST;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, IOException exception) {


            }
        });


    }

    private void init() {
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);
        sidebar.setTextView(letters);

        sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener()
        {

            @Override
            public void onTouchingLetterChanged(String s)
            {
                // 该字母首次出现的位置
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    listView.setSelection(position);
                }


            }
        });
    }


    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what){
            case MSG_GET_CITY_LIST:

                String result = (String) msg.obj;
                try {

                    JSONObject jsonObject=JSONObject.parseObject(result);
                    if(jsonObject!=null&&jsonObject.getIntValue("code")==100000){

                        JSONObject jo=jsonObject.getJSONObject("data");
                        List<City> cityList=new ArrayList<>();
                        for(char i='A';i<='Z';i++){
                            if(jo.containsKey(""+i)){
                                cityList.addAll(JSONArray.parseArray(jo.getJSONArray(""+i).toJSONString(),City.class));
                            }

                        }

                        Collections.sort(cityList, new PinyinComparator());
                        if(cityList.size()==0){
                            noData.setVisibility(View.VISIBLE);
                        }
                        if(mAdapter==null){
                            mAdapter = new CitySortAdapter(this,cityList);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                }
                            });

                            //设置适配器
                            listView.setAdapter(mAdapter);

                        }else{
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
