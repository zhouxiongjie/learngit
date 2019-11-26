package com.shuangling.software.activity;

import android.content.Intent;
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
import com.gyf.immersionbar.ImmersionBar;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.RadioGroupAdapter;
import com.shuangling.software.adapter.RadioListAdapter;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.RadioSet;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

import static android.widget.ExpandableListView.PACKED_POSITION_TYPE_CHILD;
import static android.widget.ExpandableListView.PACKED_POSITION_TYPE_NULL;

@EnableDragToClose()
public class RadioListActivity extends AppCompatActivity implements Handler.Callback {

    public static final String TAG = "RadioListActivity";

    public static final int MSG_GET_RADIO_LIST = 0x1;
    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.categoryList)
    ListView categoryList;
    @BindView(R.id.contentList)
    ExpandableListView contentList;

    private String mType;
    private Handler mHandler;
    private RadioGroupAdapter mRadioGroupAdapter;
    private RadioListAdapter mRadioListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_radio_list);
        ButterKnife.bind(this);
        CommonUtils.transparentStatusBar(this);
        mHandler = new Handler(this);
        init();
        getRadioList();
    }


    public void getRadioList() {



        String url = ServerInfo.serviceIP + ServerInfo.getRadioList;
        Map<String,String> params =new HashMap<>();
        params.put("type",mType);
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_RADIO_LIST;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });


    }

    private void init() {
        mType=getIntent().getStringExtra("type");
        if(mType.equals("1")){
            activityTitle.setTitleText("电台");
        }else{
            activityTitle.setTitleText("电视台");
        }

    }


    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case MSG_GET_RADIO_LIST:

                String result = (String) msg.obj;
                try {

                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        final List<RadioSet> radioGroups=JSONArray.parseArray(jsonObject.getJSONArray("data").toJSONString(), RadioSet.class);

                        Iterator<RadioSet> iterator = radioGroups.iterator();
                        while (iterator.hasNext()) {
                            RadioSet radioSet = iterator.next();
                            if (radioSet.getList()==null||radioSet.getList().size()==0) {
                                iterator.remove();
                            }
                        }


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
                            contentList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                                @Override
                                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                    return true;
                                }
                            });


                            contentList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                @Override
                                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                                    if(mType.equals("1")){
                                        Intent it=new Intent(RadioListActivity.this,RadioDetailActivity.class);
                                        //it.putExtra("radioId",radioGroups.get(groupPosition).getList().get(childPosition));
                                        it.putExtra("radioId",radioGroups.get(groupPosition).getList().get(childPosition).getId());
                                        startActivity(it);
                                        return true;
                                    }else{
                                        Intent it=new Intent(RadioListActivity.this,TvDetailActivity.class);
//                                        it.putExtra("Radio",radioGroups.get(groupPosition).getList().get(childPosition));
//                                        ArrayList<RadioSet> radioGroupList=new ArrayList<>();
//                                        radioGroupList.addAll(radioGroups);
//                                        it.putParcelableArrayListExtra("RadioGroups",radioGroupList);
                                        it.putExtra("radioId",radioGroups.get(groupPosition).getList().get(childPosition).getId());
                                        startActivity(it);
                                        return true;
                                    }

                                }
                            });

                        }else{
                            mRadioListAdapter.updateListView(radioGroups);
                        }



                        contentList.setOnScrollListener(new AbsListView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(AbsListView view, int scrollState) {


                                switch (scrollState) {
                                    // 当不滚动时
                                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                                        // 判断滚动到底部
//                                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
//                                            //TODO
//                                        }

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

                                        break;
                                }
                            }

                            @Override
                            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                            }
                        });

                        mRadioGroupAdapter.setSelected(mRadioGroupAdapter.getItem(0));



                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }


                break;
        }
        return false;
    }



}
