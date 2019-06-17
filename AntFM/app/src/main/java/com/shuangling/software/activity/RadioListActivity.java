package com.shuangling.software.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jaeger.library.StatusBarUtil;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.CitySortAdapter;
import com.shuangling.software.adapter.RadioGroupAdapter;
import com.shuangling.software.adapter.RadioListAdapter;
import com.shuangling.software.customview.SideBar;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.City;
import com.shuangling.software.entity.RadioGroup;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.StatusBarManager;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

import static android.widget.ExpandableListView.PACKED_POSITION_TYPE_CHILD;
import static android.widget.ExpandableListView.PACKED_POSITION_TYPE_NULL;
import static android.widget.ExpandableListView.getPackedPositionChild;


public class RadioListActivity extends AppCompatActivity implements Handler.Callback {

    public static final String TAG = "RadioListActivity";

    public static final int MSG_GET_RADIO_LIST = 0x1;
    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.categoryList)
    ListView categoryList;
    @BindView(R.id.contentList)
    ExpandableListView contentList;


    private Handler mHandler;
    private RadioGroupAdapter mRadioGroupAdapter;
    private RadioListAdapter mRadioListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(MyApplication.getInstance().getCurrentTheme());
        setContentView(R.layout.activity_radio_list);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        StatusBarManager.setImmersiveStatusBar(this, true);
        mHandler = new Handler(this);
        init();
        getRadioList();
    }


    public void getRadioList() {

        String url = ServerInfo.serviceIP + ServerInfo.getRadioList;
        Map<String,String> params =new HashMap<>();
        params.put("type","1");
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_RADIO_LIST;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, IOException exception) {


            }
        });


    }

    private void init() {

    }


    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case MSG_GET_RADIO_LIST:

                String result = (String) msg.obj;
                try {

                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        List<RadioGroup> radioGroups=JSONArray.parseArray(jsonObject.getJSONArray("data").toJSONString(), RadioGroup.class);

                        if(mRadioGroupAdapter==null){
                            mRadioGroupAdapter=new RadioGroupAdapter(this,radioGroups) ;
                            categoryList.setAdapter(mRadioGroupAdapter);
                            categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    mRadioGroupAdapter.setSelected(mRadioGroupAdapter.getItem(position));
                                    contentList.setSelectedGroup(position);
                                }
                            });
                        }else{
                            mRadioGroupAdapter.updateListView(radioGroups);
                        }


                        if(mRadioListAdapter==null){
                            mRadioListAdapter=new RadioListAdapter(this,radioGroups);
                            contentList.setAdapter(mRadioListAdapter);
                            for (int i = 0; i < mRadioListAdapter.getGroupCount(); i++) {
                                contentList.expandGroup(i);
                            }
                            contentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                }
                            });

                        }else{
                            mRadioListAdapter.updateListView(radioGroups);
                        }



                        contentList.setOnScrollListener(new AbsListView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(AbsListView view, int scrollState) {

                            }

                            @Override
                            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                int flatPosition=contentList.getFirstVisiblePosition();
                                // a. Flat list position -> Packed position
                                long packedPosition =contentList.getExpandableListPosition(flatPosition);
                                // b. Unpack packed position type
                                int positionType = contentList.getPackedPositionType(packedPosition);
                                //c. Unpack position values based on positionType
                                // 如果positionType不是空类型,就是Group,或者Child
                                if( positionType != PACKED_POSITION_TYPE_NULL ){
                                    // (Child类型时也有Group信息)
                                    int groupPosition = contentList.getPackedPositionGroup(packedPosition);
                                    // 如果是child类型,则取出childPosition
                                    mRadioGroupAdapter.setSelected(mRadioGroupAdapter.getItem(groupPosition));
                                    if(positionType == PACKED_POSITION_TYPE_CHILD){
                                        //childPosition = getPackedPositionChild(packedPosition);
                                    }
                                }else{
                                    Log.i("FooLabel", "positionType was NULL - header/footer?");
                                }
                            }
                        });

                        mRadioGroupAdapter.setSelected(mRadioGroupAdapter.getItem(0));



                    }


                } catch (Exception e) {

                }


                break;
        }
        return false;
    }



}
