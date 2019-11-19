package com.shuangling.software.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.activity.AnchorDetailActivity;
import com.shuangling.software.activity.ArticleDetailActivity;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.activity.CityListActivity;
import com.shuangling.software.activity.ContentActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.activity.LoginActivity;
import com.shuangling.software.activity.MainActivity;
import com.shuangling.software.activity.MessageListActivity;
import com.shuangling.software.activity.MoreAnchorOrOrganizationActivity;
import com.shuangling.software.activity.MoreServiceActivity;
import com.shuangling.software.activity.OrganizationDetailActivity;
import com.shuangling.software.activity.RadioDetailActivity;
import com.shuangling.software.activity.RadioListActivity;
import com.shuangling.software.activity.SearchActivity;
import com.shuangling.software.activity.ServiceDetailActivity;
import com.shuangling.software.activity.SpecialDetailActivity;
import com.shuangling.software.activity.TvDetailActivity;
import com.shuangling.software.activity.VideoDetailActivity;
import com.shuangling.software.activity.WebViewActivity;
import com.shuangling.software.activity.WebViewBackActivity;
import com.shuangling.software.adapter.ColumnAlbumContentAdapter;
import com.shuangling.software.adapter.ColumnContentAdapter;
import com.shuangling.software.adapter.MoudleGridViewAdapter;
import com.shuangling.software.customview.BannerView;
import com.shuangling.software.customview.MyGridView;
import com.shuangling.software.entity.Anchor;
import com.shuangling.software.entity.AudioInfo;
import com.shuangling.software.entity.BannerInfo;
import com.shuangling.software.entity.City;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.DecorModule;
import com.shuangling.software.entity.Organization;
import com.shuangling.software.entity.Service;
import com.shuangling.software.entity.Station;
import com.shuangling.software.entity.User;
import com.shuangling.software.entity.Weather;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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



public class IndexFragment extends Fragment implements Handler.Callback {

    public static final int MSG_GET_AHCHORS = 0x1;
    public static final int MSG_GET_SERVICE = 0x2;
    public static final int MSG_GET_ORGANIZATION = 0x3;
    public static final int MSG_GET_CITY_CONTNET = 0x4;
    public static final int MSG_GET_CITY_TYPE_CONTENT = 0x5;
    public static final int MSG_GET_INDEX_DECOR = 0x6;
    public static final int MSG_ATTENTION_CALLBACK = 0x7;
    public static final int MSG_GET_COLUMN = 0x8;
    public static final int REQUEST_LOGIN = 0x9;

//    @BindView(R.id.city)
//    TextView city;
//    @BindView(R.id.temperature)
//    TextView temperature;
//    @BindView(R.id.weather)
//    TextView weather;
//    @BindView(R.id.search)
//    TextView search;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.contentLayout)
    LinearLayout contentLayout;
    @BindView(R.id.backgroundImage)
    ImageView backgroundImage;
    @BindView(R.id.columnContent)
    LinearLayout columnContent;
    Unbinder unbinder;


    private Handler mHandler;
    private PagerAdapter mModulePageAdapter;
    private LinearLayout anchorsLayout;
    private LinearLayout organizationsLayout;
    private LinearLayout servicesLayout;
    private TextView moreService;

    private List<Column> mColumns;
    private List<RecyclerView> mContentRecyclerView = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mHandler = new Handler(this);
        EventBus.getDefault().register(this);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (!TextUtils.isEmpty(MyApplication.getInstance().getBackgroundImage())) {
            Uri uri = Uri.parse(MyApplication.getInstance().getBackgroundImage());
            backgroundImage.setImageURI(uri);
        }
        //refreshLayout.setPrimaryColorsId(R.color.transparent, android.R.color.black);
        ((ClassicsHeader) refreshLayout.getRefreshHeader()).setEnableLastTime(false);
        //refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initStation();
                //indexDecorate();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {

            }
        });
//        if(MyApplication.getInstance().getStation()!=null&&MyApplication.getInstance().getStation().getIs_league()==0){
//           city.setCompoundDrawables(null, null, null, null);
//        }
        //getRecommendColumns();
        if(MainActivity.sCurrentCity!=null){
            //city.setText(MainActivity.sCurrentCity.getName());
            //getCityAnchors();
            //weather();
            //getCityColumns("");
            indexDecorate();
        }
        return view;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }





    public void initStation(){
        String url = ServerInfo.serviceIP + ServerInfo.getStationInfo;
        Map<String, String> params = new HashMap<String, String>();

        OkHttpUtils.getNotAuthorization(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                if (refreshLayout.isRefreshing()) {
                    refreshLayout.finishRefresh();
                }
                try{

                    JSONObject jsonObject = JSONObject.parseObject(response);

                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        Station station = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), Station.class);
                        MyApplication.getInstance().setStation(station);

                        if(MyApplication.getInstance().getStation()!=null&&MyApplication.getInstance().getStation().getIs_league()==0){
                            MainActivity.sCurrentCity = new City(Integer.parseInt(MyApplication.getInstance().getStation().getCity_info().getCode()), MyApplication.getInstance().getStation().getCity_info().getName(), "#");
                        }
                    }

                    EventBus.getDefault().post(new CommonEvent("onLocationChanged"));



                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.finishRefresh();
                }
                EventBus.getDefault().post(new CommonEvent("onLocationChanged"));

            }
        });
    }

    public void indexDecorate() {

        String url = ServerInfo.serviceIP + ServerInfo.indexDecorate;
        Map<String, String> params = new HashMap<String, String>();
        params.put("city_code", "" + MainActivity.sCurrentCity.getCode());

        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                if (refreshLayout.isRefreshing()) {
                    refreshLayout.finishRefresh();
                }

                Message msg = Message.obtain();
                msg.what = MSG_GET_INDEX_DECOR;
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {

                if (refreshLayout.isRefreshing()) {
                    refreshLayout.finishRefresh();
                }
            }
        });


    }


    public void getCityAnchors(final int orderBy,int count) {

        String url = ServerInfo.serviceIP + ServerInfo.getCityAnchors;
        Map<String, String> params = new HashMap<String, String>();
        params.put("city_code", "" + MainActivity.sCurrentCity.getCode());
        params.put("order_by", ""+orderBy);
        params.put("page", "1");
        params.put("page_size", "" + count);
        params.put("type", "2");
        params.put("mode", "one");
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                Message msg = Message.obtain();
                msg.arg1=orderBy;
                msg.what = MSG_GET_AHCHORS;
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });


    }


    public void getCityService(String orderBy,int count) {

        String url = ServerInfo.serviceIP + ServerInfo.getCityService;
        Map<String, String> params = new HashMap<String, String>();
        params.put("city_code", "" + MainActivity.sCurrentCity.getCode());
        params.put("order_by", orderBy);
        params.put("limit", "" + count);

        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                Message msg = Message.obtain();
                msg.what = MSG_GET_SERVICE;
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });

    }

    public void weather() {

        String url = ServerInfo.serviceIP + ServerInfo.weather;
        Map<String, String> params = new HashMap<String, String>();
        params.put("location", "" + MainActivity.sCurrentCity.getCode() + "00");


        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                Message msg = Message.obtain();
                msg.what = MSG_GET_CITY_TYPE_CONTENT;
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });

    }


    public void attention(Anchor anchor, final boolean follow, final View view) {

        String url = ServerInfo.serviceIP + ServerInfo.attention;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + anchor.getId());
        if (follow) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }

        OkHttpUtils.post(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_ATTENTION_CALLBACK;
                msg.arg1 = follow ? 1 : 0;
                msg.arg2 = 0;
                Bundle bundle = new Bundle();
                bundle.putString("response", response);
                msg.setData(bundle);
                msg.obj = view;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });

    }


    public void attention(Organization organization, final boolean follow, final View view) {

        String url = ServerInfo.serviceIP + ServerInfo.attention;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + organization.getId());
        if (follow) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }

        OkHttpUtils.post(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_ATTENTION_CALLBACK;
                msg.arg1 = follow ? 1 : 0;
                msg.arg2 = 1;
                Bundle bundle = new Bundle();
                bundle.putString("response", response);
                msg.setData(bundle);
                msg.obj = view;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });

    }


    public void getCityOrganization(final int orderBy,int count) {

        String url = ServerInfo.serviceIP + ServerInfo.getCityAnchors;
        Map<String, String> params = new HashMap<String, String>();
        params.put("city_code", "" + MainActivity.sCurrentCity.getCode());
        params.put("order_by", ""+orderBy);
        params.put("page", "1");
        params.put("page_size", "" + count);
        params.put("type", "1");
        params.put("mode", "one");
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                Message msg = Message.obtain();
                msg.what = MSG_GET_ORGANIZATION;
                msg.arg1=orderBy;
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });

    }



    public void getDecorateContent(final int animated, final int orderBy, final int type, String columnId, String contentNumber, final int position) {

        String url = ServerInfo.serviceIP + ServerInfo.indexDecorateContent ;
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", columnId);
        params.put("type", ""+type);
        params.put("limit", contentNumber);
        params.put("sorce_type", "1");
        params.put("city_code", "" + MainActivity.sCurrentCity.getCode());
        params.put("order_by", ""+orderBy);


        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                Message msg = Message.obtain();
                msg.what = MSG_GET_CITY_TYPE_CONTENT;
                Bundle bundle=new Bundle();
                bundle.putInt("type",type);
                bundle.putInt("position",position);
                bundle.putInt("animated",animated);
                msg.setData(bundle);
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });

    }

    public void getContent(final int orderBy,String typeId, String contentNumber, final int position) {

        String url = ServerInfo.serviceIP + ServerInfo.getColumnContent + typeId;
        Map<String, String> params = new HashMap<String, String>();
        params.put("limit", contentNumber);
        params.put("sorce_type", "1");
        params.put("city_code", "" + MainActivity.sCurrentCity.getCode());
        params.put("order_by", ""+orderBy);


        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                Message msg = Message.obtain();
                msg.what = MSG_GET_CITY_CONTNET;
                msg.arg1 = position;
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });

    }


    public void getRecommendColumns() {

        String url = ServerInfo.serviceIP + ServerInfo.getRecommendColumns;
        Map<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("page_size", "" + Integer.MAX_VALUE);
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_COLUMN;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {
                exception.printStackTrace();


            }
        });


    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_AHCHORS:
                try {
                    String result = (String) msg.obj;
                    final int orderBy=msg.arg1;
                    JSONObject jo = JSONObject.parseObject(result);
                    if (jo.getIntValue("code") == 100000 && jo.getJSONObject("data") != null && jo.getJSONObject("data").getJSONArray("data") != null) {
                        List<Anchor> anchorList = JSONArray.parseArray(jo.getJSONObject("data").getJSONArray("data").toJSONString(), Anchor.class);



                        for (int i = 0; i < anchorList.size(); i++) {
                            final Anchor anchor = anchorList.get(i);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonUtils.dip2px(120), LinearLayout.LayoutParams.WRAP_CONTENT);
                            int marginLeft;
                            if(i==0){
                                marginLeft=CommonUtils.dip2px(20);
                            }else{
                                marginLeft=CommonUtils.dip2px(10);
                            }
                            int margin = CommonUtils.dip2px(10);
                            params.setMargins(marginLeft, margin, margin, margin);
                            View anchorView = LayoutInflater.from(getContext()).inflate(R.layout.anchor_item_layout, anchorsLayout, false);
                            TextView anchorName = anchorView.findViewById(R.id.anchorName);
                            SimpleDraweeView logo = anchorView.findViewById(R.id.logo);
                            TextView desc = anchorView.findViewById(R.id.desc);
                            desc.setVisibility(View.GONE);
                            TextView attention = anchorView.findViewById(R.id.attention);
                            if (anchor.getIs_follow() == 0) {
                                attention.setActivated(true);
                            } else {
                                attention.setActivated(false);
                            }

                            anchorView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it = new Intent(getContext(), AnchorDetailActivity.class);
                                    it.putExtra("anchorId", anchor.getId());
                                    startActivity(it);
                                }
                            });
                            attention.setTag(anchor);
                            attention.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (User.getInstance() == null) {
                                        startActivityForResult(new Intent(getContext(), LoginActivity.class), REQUEST_LOGIN);
                                    } else {
                                        attention(anchor, anchor.getIs_follow() == 0, v);
                                    }

                                }
                            });

                            if (!TextUtils.isEmpty(anchor.getLogo())) {
                                Uri uri = Uri.parse(anchor.getLogo());
                                int width = CommonUtils.dip2px(65);
                                int height = width;
                                ImageLoader.showThumb(uri, logo, width, height);
                            }
                            anchorName.setText(anchor.getName());
                            desc.setText(anchor.getDes());

                            anchorsLayout.addView(anchorView, i, params);
                        }
                        //更多

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonUtils.dip2px(120), LinearLayout.LayoutParams.WRAP_CONTENT);
                        int margin = CommonUtils.dip2px(5);
                        params.setMargins(margin, margin, margin, margin);
                        params.gravity=Gravity.CENTER_VERTICAL;
                        View more = LayoutInflater.from(getContext()).inflate(R.layout.more_item_layout, anchorsLayout, false);

                        more.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent it=new Intent(getContext(),MoreAnchorOrOrganizationActivity.class);
                                it.putExtra("type",2);
                                it.putExtra("orderBy",orderBy);
                                startActivity(it);

                            }
                        });

                        anchorsLayout.addView(more, params);


                    }


                } catch (Exception e) {

                }
                break;
            case MSG_GET_ORGANIZATION: {
                try {
                    String result = (String) msg.obj;
                    final int orderBy =msg.arg1;
                    JSONObject jo = JSONObject.parseObject(result);
                    if (jo.getIntValue("code") == 100000 && jo.getJSONObject("data") != null && jo.getJSONObject("data").getJSONArray("data") != null) {
                        List<Organization> organizationList = JSONArray.parseArray(jo.getJSONObject("data").getJSONArray("data").toJSONString(), Organization.class);

                        for (int i = 0; i < organizationList.size(); i++) {
                            final Organization organization = organizationList.get(i);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonUtils.dip2px(120), LinearLayout.LayoutParams.WRAP_CONTENT);
                            int marginLeft;
                            if(i==0){
                                marginLeft=CommonUtils.dip2px(20);
                            }else{
                                marginLeft=CommonUtils.dip2px(10);
                            }
                            int margin = CommonUtils.dip2px(10);
                            params.setMargins(marginLeft, margin, margin, margin);
                            View anchorView = LayoutInflater.from(getContext()).inflate(R.layout.anchor_item_layout, anchorsLayout, false);
                            TextView anchorName = anchorView.findViewById(R.id.anchorName);
                            SimpleDraweeView logo = anchorView.findViewById(R.id.logo);
                            TextView desc = anchorView.findViewById(R.id.desc);
                            ImageView authenticationLogo = anchorView.findViewById(R.id.authenticationLogo);
                            authenticationLogo.setBackgroundResource(R.drawable.ic_org_authentication);
                            desc.setVisibility(View.GONE);
                            TextView attention = anchorView.findViewById(R.id.attention);
                            if (organization.getIs_follow() == 0) {
                                attention.setActivated(true);
                            } else {
                                attention.setActivated(false);
                            }
                            anchorView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it = new Intent(getContext(), OrganizationDetailActivity.class);
                                    it.putExtra("organizationId", organization.getId());
                                    startActivity(it);
                                }
                            });
                            attention.setTag(organization);
                            attention.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (User.getInstance() == null) {
                                        startActivityForResult(new Intent(getContext(), LoginActivity.class), REQUEST_LOGIN);
                                    } else {
                                        attention(organization, organization.getIs_follow() == 0, v);
                                    }

                                }
                            });

                            if (!TextUtils.isEmpty(organization.getLogo())) {
                                Uri uri = Uri.parse(organization.getLogo());
                                int width = CommonUtils.dip2px(65);
                                int height = width;
                                ImageLoader.showThumb(uri, logo, width, height);
                            }
                            anchorName.setText(organization.getName());
                            desc.setText(organization.getDes());

                            organizationsLayout.addView(anchorView, i, params);
                        }


                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonUtils.dip2px(120), LinearLayout.LayoutParams.WRAP_CONTENT);
                        int margin = CommonUtils.dip2px(5);
                        params.setMargins(margin, margin, margin, margin);
                        params.gravity=Gravity.CENTER_VERTICAL;
                        View more = LayoutInflater.from(getContext()).inflate(R.layout.more_item_layout, anchorsLayout, false);

                        more.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent it=new Intent(getContext(),MoreAnchorOrOrganizationActivity.class);
                                it.putExtra("type",1);
                                it.putExtra("orderBy",orderBy);
                                startActivity(it);
                            }
                        });

                        organizationsLayout.addView(more, params);


                    }


                } catch (Exception e) {

                }

            }
            break;
            case MSG_GET_SERVICE:
                try {
                    String result = (String) msg.obj;

                    JSONObject jo = JSONObject.parseObject(result);
                    if (jo.getIntValue("code") == 100000 && jo.getJSONArray("data") != null) {
                        List<Service> serviceList = JSONArray.parseArray(jo.getJSONArray("data").toJSONString(), Service.class);

                        for (int i = 0; i < serviceList.size(); i++) {
                            final Service service = serviceList.get(i);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            int margin = CommonUtils.dip2px(5);
                            params.setMargins(margin, margin, margin, margin);
                            View serviceView = LayoutInflater.from(getContext()).inflate(R.layout.service_gridview_item, anchorsLayout, false);
                            SimpleDraweeView logo = serviceView.findViewById(R.id.service);
                            TextView serviceName = serviceView.findViewById(R.id.serviceName);

                            if (!TextUtils.isEmpty(service.getCover())) {
                                Uri uri = Uri.parse(service.getCover());
                                int width = CommonUtils.dip2px(35);
                                int height = width;
                                ImageLoader.showThumb(uri, logo, width, height);
                            }
                            serviceName.setText(service.getTitle());
                            serviceView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it=new Intent(getContext(),ServiceDetailActivity.class);
                                    it.putExtra("service",service);
                                    startActivity(it);
                                }
                            });
                            servicesLayout.addView(serviceView, i, params);
                        }

                    }


                } catch (Exception e) {

                }

                break;
            case MSG_GET_CITY_CONTNET:
                try {
                    String result = (String) msg.obj;
                    JSONObject jo = JSONObject.parseObject(result);
                    int position = msg.arg1;
                    if (jo.getIntValue("code") == 100000 && jo.getJSONArray("data") != null) {
                        List<ColumnContent> columnContents = JSONObject.parseArray(jo.getJSONArray("data").toJSONString(), ColumnContent.class);
                        RecyclerView recyclerView = mContentRecyclerView.get(position);
                        ColumnContentAdapter adapter = new ColumnContentAdapter(getContext(),recyclerView, columnContents);
                        recyclerView.setAdapter(adapter);


                    }


                } catch (Exception e) {

                }

                break;
            case MSG_GET_CITY_TYPE_CONTENT:{
                try {
                    String result = (String) msg.obj;
                    JSONObject jo = JSONObject.parseObject(result);
                    int position = msg.getData().getInt("position");
                    int animated=msg.getData().getInt("animated");
                    int type=msg.getData().getInt("type");

                    if(type==4){
                        if (jo.getIntValue("code") == 100000 && jo.getJSONArray("data") != null) {
                            List<ColumnContent> columnContents = JSONObject.parseArray(jo.getJSONArray("data").toJSONString(), ColumnContent.class);
                            if(animated==3){
                                RecyclerView recyclerView = mContentRecyclerView.get(position);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
                                divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycleview_divider_drawable));
                                recyclerView.addItemDecoration(divider);
                                ColumnContentAdapter adapter = new ColumnContentAdapter(getContext(), recyclerView,columnContents);
                                recyclerView.setAdapter(adapter);
                            }else if(animated==4){
                                RecyclerView recyclerView = mContentRecyclerView.get(position);
                                GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
                                recyclerView.setLayoutManager(manager);
                                DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
                                divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycleview_divider_drawable));
                                recyclerView.addItemDecoration(divider);
                                final ColumnContentAdapter adapter = new ColumnContentAdapter(getContext(),recyclerView, columnContents);
                                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        int size = adapter.getItemCount();
                                        if ((position+1) %2== 0) {
                                            return 1;
                                        } else if((position+1)==size){
                                            return 2;
                                        }else {
                                            return 1;
                                        }
                                    }
                                });

                                recyclerView.setAdapter(adapter);

                            }else if(animated==7){
                                //1+4
                                RecyclerView recyclerView = mContentRecyclerView.get(position);
                                GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
                                recyclerView.setLayoutManager(manager);
                                DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
                                divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycleview_divider_drawable));
                                recyclerView.addItemDecoration(divider);
                                final ColumnContentAdapter adapter = new ColumnContentAdapter(getContext(),recyclerView, columnContents);
                                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        int size = adapter.getItemCount();
                                        int page=size/5;
                                        if (page>0&&(position+1)<=5*page) {
                                            if((position+1)%5==1){
                                                return 2;
                                            }else{
                                                return 1;
                                            }
                                        } else{
                                            if(size%5==1){
                                                if((position+1)%5==1){
                                                    return 2;
                                                }
                                            }else if(size%5==2){
                                                if((position+1)%5==1){
                                                    return 2;
                                                }else if((position+1)%5==2){
                                                    return 2;
                                                }
                                            }else if(size%5==3){
                                                if((position+1)%5==1){
                                                    return 2;
                                                }else if((position+1)%5==2){
                                                    return 1;
                                                }else if((position+1)%5==3){
                                                    return 1;
                                                }
                                            }else if(size%5==4){
                                                if((position+1)%5==1){
                                                    return 2;
                                                }else if((position+1)%5==2){
                                                    return 1;
                                                }else if((position+1)%5==3){
                                                    return 1;
                                                }else if((position+1)%5==4){
                                                    return 2;
                                                }
                                            }
                                            return 2;
                                        }
                                    }
                                });

                                recyclerView.setAdapter(adapter);
                            }else if(animated==8){
                                //单行
                                RecyclerView recyclerView = mContentRecyclerView.get(position);
                                LayoutInflater inflater = LayoutInflater.from(getContext());
                                View anchorLayout = inflater.inflate(R.layout.index_scrollview_column_layout, null, false);
                                LinearLayout videoLayout= anchorLayout.findViewById(R.id.contentLayout);

                                for (int i = 0; i < columnContents.size(); i++) {
                                    final ColumnContent content = columnContents.get(i);

                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((CommonUtils.getScreenWidth()-CommonUtils.dip2px(10))*2/5, LinearLayout.LayoutParams.WRAP_CONTENT);

//                                int margin = CommonUtils.dip2px(10);
//                                params.setMargins(margin, margin, margin, margin);
                                    View anchorView = LayoutInflater.from(getContext()).inflate(R.layout.scrollview_video_item_layout, videoLayout, false);
                                    TextView videoTitle = anchorView.findViewById(R.id.title);
                                    SimpleDraweeView logo = anchorView.findViewById(R.id.logo);
                                    TextView duration = anchorView.findViewById(R.id.duration);

                                    videoTitle.setText(content.getTitle());
                                    if(content.getVideo()!=null){
                                        duration.setText(content.getVideo().getDuration());
                                    }
                                    if (!TextUtils.isEmpty(content.getCover())) {
                                        Uri uri = Uri.parse(content.getCover());
                                        int width = (CommonUtils.getScreenWidth()-CommonUtils.dip2px(10))*2/5;
                                        int height = (int) (2f * width / 3f);
                                        ImageLoader.showThumb(uri, logo, width, height);
                                    }

                                    anchorView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent it = new Intent(getContext(), VideoDetailActivity.class);
                                            it.putExtra("videoId", content.getId());
                                            startActivity(it);
                                        }
                                    });


                                    videoLayout.addView(anchorView,  params);
                                }



                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
                                divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycleview_divider_drawable));
                                recyclerView.addItemDecoration(divider);

                                ColumnContentAdapter adapter = new ColumnContentAdapter(getContext(),recyclerView);
                                adapter.addHeaderView(anchorLayout);
                                recyclerView.setAdapter(adapter);

                            }


                        }
                    }else if(type==3){
                        //文章
                        if (jo.getIntValue("code") == 100000 && jo.getJSONArray("data") != null) {
                            List<ColumnContent> columnContents = JSONObject.parseArray(jo.getJSONArray("data").toJSONString(), ColumnContent.class);
                            RecyclerView recyclerView = mContentRecyclerView.get(position);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                            divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
                            recyclerView.addItemDecoration(divider);
                            ColumnContentAdapter adapter = new ColumnContentAdapter(getContext(),recyclerView, columnContents);
                            recyclerView.setAdapter(adapter);
                        }

                    }else if(type==7){
                        //图集
                        if (jo.getIntValue("code") == 100000 && jo.getJSONArray("data") != null) {
                            List<ColumnContent> columnContents = JSONObject.parseArray(jo.getJSONArray("data").toJSONString(), ColumnContent.class);
                            RecyclerView recyclerView = mContentRecyclerView.get(position);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                            divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycleview_divider_drawable));
                            recyclerView.addItemDecoration(divider);
                            ColumnContentAdapter adapter = new ColumnContentAdapter(getContext(),recyclerView, columnContents);
                            recyclerView.setAdapter(adapter);
                        }

                    }else if(type==2){
                        //音频
                        if (jo.getIntValue("code") == 100000 && jo.getJSONArray("data") != null) {
                            List<ColumnContent> columnContents = JSONObject.parseArray(jo.getJSONArray("data").toJSONString(), ColumnContent.class);

                            if(animated==8){
                                //单行
                                RecyclerView recyclerView = mContentRecyclerView.get(position);
                                LayoutInflater inflater = LayoutInflater.from(getContext());
                                View anchorLayout = inflater.inflate(R.layout.index_scrollview_column_layout, null, false);
                                LinearLayout albumLayout= anchorLayout.findViewById(R.id.contentLayout);

                                for (int i = 0; i < columnContents.size(); i++) {
                                    final ColumnContent content = columnContents.get(i);

                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((CommonUtils.getScreenWidth()-CommonUtils.dip2px(10))*2/7, LinearLayout.LayoutParams.WRAP_CONTENT);

//                                int margin = CommonUtils.dip2px(10);
//                                params.setMargins(margin, margin, margin, margin);
                                    View anchorView = LayoutInflater.from(getContext()).inflate(R.layout.scrollview_album_item_layout, albumLayout, false);
                                    TextView videoTitle = anchorView.findViewById(R.id.title);
                                    SimpleDraweeView logo = anchorView.findViewById(R.id.logo);


                                    videoTitle.setText(content.getTitle());

                                    if (!TextUtils.isEmpty(content.getCover())) {
                                        Uri uri = Uri.parse(content.getCover());
                                        int width = (CommonUtils.getScreenWidth()-CommonUtils.dip2px(10))*2/7;
                                        int height = width;
                                        ImageLoader.showThumb(uri, logo, width, height);
                                    }

                                    anchorView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Intent it = new Intent(getContext(), AlbumDetailActivity.class);
                                            it.putExtra("albumId", content.getId());
                                            startActivity(it);
                                        }
                                    });


                                    albumLayout.addView(anchorView,  params);
                                }



                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
                                divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycleview_divider_drawable));
                                recyclerView.addItemDecoration(divider);

                                ColumnContentAdapter adapter = new ColumnContentAdapter(getContext(),recyclerView);
                                adapter.addHeaderView(anchorLayout);
                                recyclerView.setAdapter(adapter);

                            }else if(animated==5){
                                //三图
                                RecyclerView recyclerView = mContentRecyclerView.get(position);
                                GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
                                recyclerView.setLayoutManager(manager);
                                DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
                                divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycleview_divider_drawable));
                                recyclerView.addItemDecoration(divider);
                                final ColumnAlbumContentAdapter adapter = new ColumnAlbumContentAdapter(getContext(), columnContents);
                                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        int size = adapter.getItemCount();
                                        int page = size / 3;
                                        if (page > 0 && (position + 1) <= 3 * page) {
                                            return 1;
                                        } else {
                                            return 3;
                                        }
                                    }
                                });
                                adapter.setMode(1);

                                recyclerView.setAdapter(adapter);



                            }else if(animated==9){
                                //三图+5图
                                RecyclerView recyclerView = mContentRecyclerView.get(position);
                                GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
                                recyclerView.setLayoutManager(manager);
                                DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
                                divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycleview_divider_drawable));
                                recyclerView.addItemDecoration(divider);
                                final ColumnAlbumContentAdapter adapter = new ColumnAlbumContentAdapter(getContext(), columnContents);
                                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {

                                        int size = adapter.getItemCount();
                                        int page=size/8;
                                        if (page>0&&(position+1)<=8*page) {
                                            if((position+1)%8>0&&(position+1)%8<=3){
                                                return 1;
                                            }else{
                                                return 3;
                                            }
                                        }else{
                                            if(size%8==1||size%8==2){
                                                return 3;
                                            }else if(size%8==3){
                                                return 1;
                                            }else{
                                                if((position+1)%8<=3&&(position+1)%8>0){
                                                    return 1;
                                                }else {
                                                    return 3;
                                                }
                                            }
                                        }
                                    }
                                });
                                adapter.setMode(2);

                                recyclerView.setAdapter(adapter);
                            }

                        }

                    }



                } catch (Exception e) {

                }
            }
            break;
            case MSG_ATTENTION_CALLBACK:
                try {
                    String result = msg.getData().getString("response");
                    boolean follow = msg.arg1 == 1 ? true : false;

                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show(jsonObject.getString("msg"));
                        TextView attention = (TextView) msg.obj;
                        if (msg.arg2 == 0) {
                            Anchor anchor = (Anchor) attention.getTag();
                            if (follow) {
                                attention.setText("已关注");
                                attention.setActivated(false);
                                anchor.setIs_follow(1);
                            } else {
                                attention.setText("关注");
                                attention.setActivated(true);
                                anchor.setIs_follow(0);

                            }
                        } else {
                            Organization organization = (Organization) attention.getTag();
                            if (follow) {
                                attention.setText("已关注");
                                attention.setActivated(false);
                                organization.setIs_follow(1);
                            } else {
                                attention.setText("关注");
                                attention.setActivated(true);
                                organization.setIs_follow(0);

                            }
                        }

                    }


                } catch (Exception e) {

                }
                break;

            case MSG_GET_INDEX_DECOR:
                try {
                    String result = (String) msg.obj;
                    JSONObject jo = JSONObject.parseObject(result);
                    if (jo.getIntValue("code") == 100000 && jo.getJSONObject("data") != null) {

                        List<DecorModule> decorModules = JSONObject.parseArray(jo.getJSONObject("data").getJSONArray("modules").toJSONString(), DecorModule.class);
                        mContentRecyclerView.clear();
                        contentLayout.removeAllViews();
                        for (int i = 0; i < decorModules.size(); i++) {
                            final DecorModule module = decorModules.get(i);
                            //模块类型1轮播图，2金刚区，3主播，4生活服务，5机构，6资讯
                            if (module.getType() == 1) {
                                //轮播图
                                if (module.getStatus() == 1) {
                                    //显示轮播图

                                    BannerView bannerView = new BannerView(getContext());
                                    int width=CommonUtils.getScreenWidth()-CommonUtils.dip2px(40);
                                    int height=10*width/23;
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
                                    params.leftMargin = CommonUtils.dip2px(20);
                                    params.rightMargin = CommonUtils.dip2px(20);
                                    params.bottomMargin=CommonUtils.dip2px(5);
                                    //params.topMargin = CommonUtils.dip2px(20);
                                    contentLayout.addView(bannerView, params);
                                    List<BannerView.Banner> banners = new ArrayList<>();
                                    for (int j = 0; module.getContents() != null && j < module.getContents().size(); j++) {
                                        BannerInfo banner = new BannerInfo();
                                        banner.setLogo(module.getContents().get(j).getCover());
                                        banner.setUrl(module.getContents().get(j).getSource_url());
                                        banners.add(banner);

                                    }
                                    bannerView.setData(banners);
                                    bannerView.setOnItemClickListener(new BannerView.OnItemClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            BannerInfo banner=(BannerInfo)view.getTag();



//                                            Intent it=new Intent(getContext(),WebViewBackActivity.class);
//                                            it.putExtra("url",banner.getUrl());
//                                            startActivity(it);

                                            if(banner.getUrl().startsWith(ServerInfo.h5IP+"/tv")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/tv")){
                                                Intent it=new Intent(getContext(),RadioListActivity.class);
                                                it.putExtra("type","2");
                                                startActivity(it);
                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/radios/")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/radios/")){
                                                String radioId=banner.getUrl().substring(banner.getUrl().lastIndexOf("/")+1);
                                                Intent it=new Intent(getContext(),TvDetailActivity.class);
                                                it.putExtra("radioId",Integer.parseInt(radioId));
                                                startActivity(it);
                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/radios")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/radios")){
                                                Intent it=new Intent(getContext(),RadioListActivity.class);
                                                it.putExtra("type","1");
                                                startActivity(it);
                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/radios/")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/radios/")){
                                                String radioId=banner.getUrl().substring(banner.getUrl().lastIndexOf("/")+1);
                                                Intent it=new Intent(getContext(),RadioDetailActivity.class);
                                                it.putExtra("radioId",Integer.parseInt(radioId));
                                                startActivity(it);
                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/gover")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/gover")){
                                                Intent it=new Intent(getContext(),WebViewActivity.class);
                                                it.putExtra("url",banner.getUrl());
                                                startActivity(it);
                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/dj")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/dj")){
                                                Intent it=new Intent(getContext(),WebViewActivity.class);
                                                it.putExtra("url",banner.getUrl());
                                                startActivity(it);
                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/interact")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/interact")){
                                                Intent it=new Intent(getContext(),WebViewActivity.class);
                                                it.putExtra("url",banner.getUrl());
                                                startActivity(it);
                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/guide")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/guide")){
                                                Intent it=new Intent(getContext(),WebViewActivity.class);
                                                it.putExtra("url",banner.getUrl());
                                                startActivity(it);
                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/cates/")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/cates/")){
                                                //跳转栏目
                                                String columnid=banner.getUrl().substring(banner.getUrl().lastIndexOf("/")+1);
                                                Column column=new Column();
                                                column.setId(Integer.parseInt(columnid));
                                                ((MainActivity)getActivity()).switchRecommend(column);

                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/specials/")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/specials/")){
                                                //跳转栏目
                                                int columnid=0;
                                                for(int i=0;i<mColumns.size();i++){
                                                    if(mColumns.get(i).getType()==1){
                                                        columnid=mColumns.get(i).getId();
                                                        break;
                                                    }
                                                }
                                                Column column=new Column();
                                                column.setId(columnid);
                                                ((MainActivity)getActivity()).switchRecommend(column);

                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/orgs/")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/orgs/")){
                                                String organizationId=banner.getUrl().substring(banner.getUrl().lastIndexOf("/")+1);
                                                Intent it = new Intent(getContext(), OrganizationDetailActivity.class);
                                                it.putExtra("organizationId", Integer.parseInt(organizationId));
                                                startActivity(it);
                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/anchors/")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/anchors/")){
                                                String anchorId=banner.getUrl().substring(banner.getUrl().lastIndexOf("/")+1);
                                                Intent it = new Intent(getContext(), AnchorDetailActivity.class);
                                                it.putExtra("anchorId", Integer.parseInt(anchorId));
                                                startActivity(it);
                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/atlas/")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/atlas/")){
                                                String galleriaId=banner.getUrl().substring(banner.getUrl().lastIndexOf("/")+1);
                                                Intent it = new Intent(getContext(), GalleriaActivity.class);
                                                it.putExtra("galleriaId", Integer.parseInt(galleriaId));
                                                startActivity(it);
                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/albums/")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/albums/")){
                                                String albumId=banner.getUrl().substring(banner.getUrl().lastIndexOf("/")+1);
                                                Intent it = new Intent(getContext(), AlbumDetailActivity.class);
                                                it.putExtra("albumId", Integer.parseInt(albumId));
                                                startActivity(it);
                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/audios/")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/audios/")){
                                                String audioId=banner.getUrl().substring(banner.getUrl().lastIndexOf("/")+1);
                                                Intent it = new Intent(getContext(), AudioDetailActivity.class);
                                                it.putExtra("audioId", Integer.parseInt(audioId));
                                                startActivity(it);
                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/posts/")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/posts/")){
                                                String articleId=banner.getUrl().substring(banner.getUrl().lastIndexOf("/")+1);
                                                Intent it = new Intent(getContext(), ArticleDetailActivity.class);
                                                it.putExtra("articleId", Integer.parseInt(articleId));
                                                startActivity(it);
                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/specials/")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/specials/")){
                                                String specialId=banner.getUrl().substring(banner.getUrl().lastIndexOf("/")+1);
                                                Intent it = new Intent(getContext(), SpecialDetailActivity.class);
                                                it.putExtra("specialId", Integer.parseInt(specialId));
                                                startActivity(it);
                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/videos/")||banner.getUrl().startsWith(ServerInfo.h5HttpsIP+"/videos/")){
                                                String videoId=banner.getUrl().substring(banner.getUrl().lastIndexOf("/")+1);
                                                Intent it = new Intent(getContext(), VideoDetailActivity.class);
                                                it.putExtra("videoId",Integer.parseInt(videoId));
                                                startActivity(it);
                                            }else if(banner.getUrl().startsWith(ServerInfo.h5IP+"/subcates/")||banner.getUrl().startsWith(ServerInfo.h5IP+"/subcates/")){
                                                String url=banner.getUrl();
                                                String columnid=url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("?"));
                                                Column column=new Column();
                                                column.setId(Integer.parseInt(columnid));
                                                column.setName(url.substring(url.lastIndexOf("=")+1));
                                                Intent it = new Intent(getContext(), ContentActivity.class);
                                                it.putExtra("column", column);
                                                startActivity(it);
                                            }else {
                                                Intent it=new Intent(getContext(),WebViewBackActivity.class);
                                                it.putExtra("url",banner.getUrl());
                                                it.putExtra("title",banner.getTitle());
                                                startActivity(it);
                                            }
                                        }
                                    });


                                } else {
                                    //关闭轮播图
                                }

                            } else if (module.getType() == 2) {
                                //金刚区
                                if (module.getStatus() == 1) {
                                    //显示金刚区

                                    final int cols=module.getCols();
                                    final int flip=module.getPage_animated();
                                    if(flip==1){
                                        //翻页
                                        final ViewPager moduleViewPager = new ViewPager(getContext());
                                        moduleViewPager.setBackgroundResource(R.color.white);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, CommonUtils.dip2px(100));
                                        contentLayout.addView(moduleViewPager, params);
//                                    if (mModulePageAdapter == null) {

                                        mModulePageAdapter = new PagerAdapter() {


                                            @Override
                                            public int getItemPosition(Object object) {
                                                return POSITION_NONE;
                                            }

                                            @Override
                                            public void destroyItem(ViewGroup container, int position, Object object) {
                                                container.removeView(container.findViewWithTag(position));
                                            }

                                            @Override
                                            public Object instantiateItem(ViewGroup container, int position) {
                                                LayoutInflater inflater = LayoutInflater.from(getActivity());
                                                final View v = inflater.inflate(R.layout.index_module_item, moduleViewPager, false);
                                                MyGridView gv = v.findViewById(R.id.gridView);
                                                gv.setNumColumns(cols);
                                                List<DecorModule.ContentsBean> contents = new ArrayList<>();
                                                for (int i = position * cols*2; i < (position + 1) * cols*2 && i < module.getContents().size(); i++) {
                                                    contents.add(module.getContents().get(i));
                                                }
                                                final MoudleGridViewAdapter adapter = new MoudleGridViewAdapter(getActivity(), contents);
                                                gv.setAdapter(adapter);
                                                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        DecorModule.ContentsBean cb=adapter.getItem(position);
                                                        if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/tv")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/tv")){
                                                            Intent it=new Intent(getContext(),RadioListActivity.class);
                                                            it.putExtra("type","2");
                                                            startActivity(it);
                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/radios/")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/radios/")){
                                                            String radioId=cb.getSource_url().substring(cb.getSource_url().lastIndexOf("/")+1);
                                                            Intent it=new Intent(getContext(),TvDetailActivity.class);
                                                            it.putExtra("radioId",Integer.parseInt(radioId));
                                                            startActivity(it);
                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/radios")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/radios")){
                                                            Intent it=new Intent(getContext(),RadioListActivity.class);
                                                            it.putExtra("type","1");
                                                            startActivity(it);
                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/radios/")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/radios/")){
                                                            String radioId=cb.getSource_url().substring(cb.getSource_url().lastIndexOf("/")+1);
                                                            Intent it=new Intent(getContext(),RadioDetailActivity.class);
                                                            it.putExtra("radioId",Integer.parseInt(radioId));
                                                            startActivity(it);
                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/gover")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/gover")){
                                                            Intent it=new Intent(getContext(),WebViewActivity.class);
                                                            it.putExtra("url",cb.getSource_url());
                                                            startActivity(it);
                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/dj")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/dj")){
                                                            Intent it=new Intent(getContext(),WebViewActivity.class);
                                                            it.putExtra("url",cb.getSource_url());
                                                            startActivity(it);
                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/interact")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/interact")){
                                                            Intent it=new Intent(getContext(),WebViewActivity.class);
                                                            it.putExtra("url",cb.getSource_url());
                                                            startActivity(it);
                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/guide")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/guide")){
                                                            Intent it=new Intent(getContext(),WebViewActivity.class);
                                                            it.putExtra("url",cb.getSource_url());
                                                            startActivity(it);
                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/cates/")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/cates/")){
                                                            //跳转栏目
                                                            String columnid=cb.getSource_url().substring(cb.getSource_url().lastIndexOf("/")+1);
                                                            Column column=new Column();
                                                            column.setId(Integer.parseInt(columnid));
                                                            ((MainActivity)getActivity()).switchRecommend(column);

                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/specials/")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/specials/")){
                                                            //跳转栏目
                                                            int columnid=0;
                                                            for(int i=0;i<mColumns.size();i++){
                                                                if(mColumns.get(i).getType()==1){
                                                                    columnid=mColumns.get(i).getId();
                                                                    break;
                                                                }
                                                            }
                                                            Column column=new Column();
                                                            column.setId(columnid);
                                                            ((MainActivity)getActivity()).switchRecommend(column);

                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/orgs/")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/orgs/")){
                                                            String organizationId=cb.getSource_url().substring(cb.getSource_url().lastIndexOf("/")+1);
                                                            Intent it = new Intent(getContext(), OrganizationDetailActivity.class);
                                                            it.putExtra("organizationId", Integer.parseInt(organizationId));
                                                            startActivity(it);
                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/anchors/")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/anchors/")){
                                                            String anchorId=cb.getSource_url().substring(cb.getSource_url().lastIndexOf("/")+1);
                                                            Intent it = new Intent(getContext(), AnchorDetailActivity.class);
                                                            it.putExtra("anchorId", Integer.parseInt(anchorId));
                                                            startActivity(it);
                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/atlas/")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/atlas/")){
                                                            String galleriaId=cb.getSource_url().substring(cb.getSource_url().lastIndexOf("/")+1);
                                                            Intent it = new Intent(getContext(), GalleriaActivity.class);
                                                            it.putExtra("galleriaId", Integer.parseInt(galleriaId));
                                                            startActivity(it);
                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/albums/")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/albums/")){
                                                            String albumId=cb.getSource_url().substring(cb.getSource_url().lastIndexOf("/")+1);
                                                            Intent it = new Intent(getContext(), AlbumDetailActivity.class);
                                                            it.putExtra("albumId", Integer.parseInt(albumId));
                                                            startActivity(it);
                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/audios/")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/audios/")){
                                                            String audioId=cb.getSource_url().substring(cb.getSource_url().lastIndexOf("/")+1);
                                                            Intent it = new Intent(getContext(), AudioDetailActivity.class);
                                                            it.putExtra("audioId", Integer.parseInt(audioId));
                                                            startActivity(it);
                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/posts/")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/posts/")){
                                                            String articleId=cb.getSource_url().substring(cb.getSource_url().lastIndexOf("/")+1);
                                                            Intent it = new Intent(getContext(), ArticleDetailActivity.class);
                                                            it.putExtra("articleId", Integer.parseInt(articleId));
                                                            startActivity(it);
                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/specials/")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/specials/")){
                                                            String specialId=cb.getSource_url().substring(cb.getSource_url().lastIndexOf("/")+1);
                                                            Intent it = new Intent(getContext(), SpecialDetailActivity.class);
                                                            it.putExtra("specialId", Integer.parseInt(specialId));
                                                            startActivity(it);
                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/videos/")||cb.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/videos/")){
                                                            String videoId=cb.getSource_url().substring(cb.getSource_url().lastIndexOf("/")+1);
                                                            Intent it = new Intent(getContext(), VideoDetailActivity.class);
                                                            it.putExtra("videoId",Integer.parseInt(videoId));
                                                            startActivity(it);
                                                        }else if(cb.getSource_url().startsWith(ServerInfo.h5IP+"/subcates/")||cb.getSource_url().startsWith(ServerInfo.h5IP+"/subcates/")){
                                                            String url=cb.getSource_url();
                                                            String columnid=url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("?"));
                                                            Column column=new Column();
                                                            column.setId(Integer.parseInt(columnid));
                                                            column.setName(url.substring(url.lastIndexOf("=")+1));
                                                            Intent it = new Intent(getContext(), ContentActivity.class);
                                                            it.putExtra("column", column);
                                                            startActivity(it);
                                                        }else {
                                                            Intent it=new Intent(getContext(),WebViewBackActivity.class);
                                                            it.putExtra("url",cb.getSource_url());
                                                            it.putExtra("title",cb.getTitle());
                                                            startActivity(it);
                                                        }

                                                    }
                                                });
                                                v.setTag(position);
                                                container.addView(v);
                                                if(position==0){
                                                    v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                                        @Override
                                                        public void onGlobalLayout() {
                                                            int height=v.getHeight();
                                                            ViewGroup.LayoutParams lp=moduleViewPager.getLayoutParams();
                                                            lp.height=height;
                                                            moduleViewPager.setLayoutParams(lp);
                                                        }
                                                    });
                                                }
                                                return v;
                                            }

                                            @Override
                                            public boolean isViewFromObject(View arg0, Object arg1) {
                                                return arg0 == arg1;
                                            }

                                            @Override
                                            public int getCount() {
                                                return (module.getContents().size() + cols*2-1) / (cols*2);
                                            }
                                        };


                                        moduleViewPager.setAdapter(mModulePageAdapter);
                                        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

                                            @Override
                                            public void onPageScrolled(int i, float v, int i1) {

                                            }

                                            @Override
                                            public void onPageSelected(int i) {


                                            }

                                            @Override
                                            public void onPageScrollStateChanged(int i) {

                                            }
                                        };
                                        moduleViewPager.addOnPageChangeListener(pageChangeListener);
                                    }else{
                                        //连续
                                        LayoutInflater inflater = LayoutInflater.from(getContext());
                                        View root = inflater.inflate(R.layout.index_module_layout, contentLayout, false);
                                        LinearLayout moduleLayout = root.findViewById(R.id.moduleLayout);
                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        contentLayout.addView(root, lp);

                                        for (int j = 0; j < module.getContents().size(); j++) {
                                            final DecorModule.ContentsBean content = module.getContents().get(j);

                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonUtils.getScreenWidth()/cols, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params.gravity=Gravity.CENTER;
                                            params.topMargin=CommonUtils.dip2px(20);
                                            params.bottomMargin=CommonUtils.dip2px(20);
                                            View anchorView = LayoutInflater.from(getContext()).inflate(R.layout.anchor_gridview_item, moduleLayout, false);
                                            TextView anchorName = anchorView.findViewById(R.id.anchorName);
                                            SimpleDraweeView anchor = anchorView.findViewById(R.id.anchor);


                                            if(!TextUtils.isEmpty(content.getCover())){
                                                Uri uri = Uri.parse(content.getCover());
                                                int width=CommonUtils.dip2px(35);
                                                int height=width;
                                                ImageLoader.showThumb(uri,anchor,width,height);

                                            }

                                            anchorName.setText(content.getTitle());

                                            anchorView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    if(content.getSource_url().startsWith(ServerInfo.h5IP+"/tv")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/tv")){
                                                        Intent it=new Intent(getContext(),RadioListActivity.class);
                                                        it.putExtra("type","2");
                                                        startActivity(it);
                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/radios/")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/radios/")){
                                                        String radioId=content.getSource_url().substring(content.getSource_url().lastIndexOf("/")+1);
                                                        Intent it=new Intent(getContext(),TvDetailActivity.class);
                                                        it.putExtra("radioId",Integer.parseInt(radioId));
                                                        startActivity(it);
                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/radios")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/radios")){
                                                        Intent it=new Intent(getContext(),RadioListActivity.class);
                                                        it.putExtra("type","1");
                                                        startActivity(it);
                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/radios/")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/radios/")){
                                                        String radioId=content.getSource_url().substring(content.getSource_url().lastIndexOf("/")+1);
                                                        Intent it=new Intent(getContext(),RadioDetailActivity.class);
                                                        it.putExtra("radioId",Integer.parseInt(radioId));
                                                        startActivity(it);
                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/gover")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/gover")){
                                                        Intent it=new Intent(getContext(),WebViewActivity.class);
                                                        it.putExtra("url",content.getSource_url());
                                                        startActivity(it);
                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/dj")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/dj")){
                                                        Intent it=new Intent(getContext(),WebViewActivity.class);
                                                        it.putExtra("url",content.getSource_url());
                                                        startActivity(it);
                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/interact")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/interact")){
                                                        Intent it=new Intent(getContext(),WebViewActivity.class);
                                                        it.putExtra("url",content.getSource_url());
                                                        startActivity(it);
                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/guide")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/guide")){
                                                        Intent it=new Intent(getContext(),WebViewActivity.class);
                                                        it.putExtra("url",content.getSource_url());
                                                        startActivity(it);
                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/cates/")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/cates/")){
                                                        //跳转栏目
                                                        String columnid=content.getSource_url().substring(content.getSource_url().lastIndexOf("/")+1);
                                                        Column column=new Column();
                                                        column.setId(Integer.parseInt(columnid));
                                                        ((MainActivity)getActivity()).switchRecommend(column);

                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/specials/")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/specials/")){
                                                        //跳转栏目
                                                        int columnid=0;
                                                        for(int i=0;i<mColumns.size();i++){
                                                            if(mColumns.get(i).getType()==1){
                                                                columnid=mColumns.get(i).getId();
                                                                break;
                                                            }
                                                        }
                                                        Column column=new Column();
                                                        column.setId(columnid);
                                                        ((MainActivity)getActivity()).switchRecommend(column);

                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/orgs/")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/orgs/")){
                                                        String organizationId=content.getSource_url().substring(content.getSource_url().lastIndexOf("/")+1);
                                                        Intent it = new Intent(getContext(), OrganizationDetailActivity.class);
                                                        it.putExtra("organizationId", Integer.parseInt(organizationId));
                                                        startActivity(it);
                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/anchors/")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/anchors/")){
                                                        String anchorId=content.getSource_url().substring(content.getSource_url().lastIndexOf("/")+1);
                                                        Intent it = new Intent(getContext(), AnchorDetailActivity.class);
                                                        it.putExtra("anchorId", Integer.parseInt(anchorId));
                                                        startActivity(it);
                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/atlas/")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/atlas/")){
                                                        String galleriaId=content.getSource_url().substring(content.getSource_url().lastIndexOf("/")+1);
                                                        Intent it = new Intent(getContext(), GalleriaActivity.class);
                                                        it.putExtra("galleriaId", Integer.parseInt(galleriaId));
                                                        startActivity(it);
                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/albums/")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/albums/")){
                                                        String albumId=content.getSource_url().substring(content.getSource_url().lastIndexOf("/")+1);
                                                        Intent it = new Intent(getContext(), AlbumDetailActivity.class);
                                                        it.putExtra("albumId", Integer.parseInt(albumId));
                                                        startActivity(it);
                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/audios/")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/audios/")){
                                                        String audioId=content.getSource_url().substring(content.getSource_url().lastIndexOf("/")+1);
                                                        Intent it = new Intent(getContext(), AudioDetailActivity.class);
                                                        it.putExtra("audioId", Integer.parseInt(audioId));
                                                        startActivity(it);
                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/posts/")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/posts/")){
                                                        String articleId=content.getSource_url().substring(content.getSource_url().lastIndexOf("/")+1);
                                                        Intent it = new Intent(getContext(), ArticleDetailActivity.class);
                                                        it.putExtra("articleId", Integer.parseInt(articleId));
                                                        startActivity(it);
                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/specials/")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/specials/")){
                                                        String specialId=content.getSource_url().substring(content.getSource_url().lastIndexOf("/")+1);
                                                        Intent it = new Intent(getContext(), SpecialDetailActivity.class);
                                                        it.putExtra("specialId", Integer.parseInt(specialId));
                                                        startActivity(it);
                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/videos/")||content.getSource_url().startsWith(ServerInfo.h5HttpsIP+"/videos/")){
                                                        String videoId=content.getSource_url().substring(content.getSource_url().lastIndexOf("/")+1);
                                                        Intent it = new Intent(getContext(), VideoDetailActivity.class);
                                                        it.putExtra("videoId",Integer.parseInt(videoId));
                                                        startActivity(it);
                                                    }else if(content.getSource_url().startsWith(ServerInfo.h5IP+"/subcates/")||content.getSource_url().startsWith(ServerInfo.h5IP+"/subcates/")){
                                                        String url=content.getSource_url();
                                                        String columnid=url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("?"));
                                                        Column column=new Column();
                                                        column.setId(Integer.parseInt(columnid));
                                                        column.setName(url.substring(url.lastIndexOf("=")+1));
                                                        Intent it = new Intent(getContext(), ContentActivity.class);
                                                        it.putExtra("column", column);
                                                        startActivity(it);
                                                    }else {
                                                        Intent it=new Intent(getContext(),WebViewBackActivity.class);
                                                        it.putExtra("url",content.getSource_url());
                                                        it.putExtra("title",content.getTitle());
                                                        startActivity(it);
                                                    }




                                                }
                                            });


                                            moduleLayout.addView(anchorView, j, params);
                                        }
                                    }


                                } else {
                                    //关闭金刚区

                                }

                            } else if (module.getType() == 3) {
                                //主播

                                LayoutInflater inflater = LayoutInflater.from(getContext());
                                View anchorLayout = inflater.inflate(R.layout.index_anchor_layout, contentLayout, false);
                                anchorsLayout = anchorLayout.findViewById(R.id.anchorsLayout);
                                TextView anchorTitle = anchorLayout.findViewById(R.id.anchorTitle);
                                anchorTitle.setText(module.getTitle());
                                SimpleDraweeView logo=anchorLayout.findViewById(R.id.logo);
                                Station station=MyApplication.getInstance().getStation();
                                if(station!=null&&!TextUtils.isEmpty(station.getIcon3())){
                                    Uri uri = Uri.parse(station.getIcon3());
                                    int width=CommonUtils.dip2px(9);
                                    int height=width*2;
                                    ImageLoader.showThumb(uri,logo,width,height);
                                }else{
                                    logo.setVisibility(View.GONE);
                                }
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                contentLayout.addView(anchorLayout, params);
                                getCityAnchors( module.getOrder_by(),module.getContent_number());


                            } else if (module.getType() == 4) {
                                //生活服务

                                LayoutInflater inflater = LayoutInflater.from(getActivity());
                                View serviceLayout = inflater.inflate(R.layout.index_service_layout, contentLayout, false);
                                servicesLayout = serviceLayout.findViewById(R.id.servicesLayout);
                                SimpleDraweeView logo=serviceLayout.findViewById(R.id.logo);
                                Station station=MyApplication.getInstance().getStation();
                                if(station!=null&&!TextUtils.isEmpty(station.getIcon3())){
                                    Uri uri = Uri.parse(station.getIcon3());
                                    int width=CommonUtils.dip2px(9);
                                    int height=width*2;
                                    ImageLoader.showThumb(uri,logo,width,height);
                                }else{
                                    logo.setVisibility(View.GONE);
                                }
                                moreService = serviceLayout.findViewById(R.id.moreService);
                                moreService.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(getContext(),MoreServiceActivity.class));
                                    }
                                });
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                contentLayout.addView(serviceLayout, params);
                                if (module.getOrder_by() == 4) {
                                    getCityService("1",module.getContent_number());
                                } else {
                                    getCityService("2",module.getContent_number());
                                }


                            } else if (module.getType() == 5) {
                                //机构

                                LayoutInflater inflater = LayoutInflater.from(getContext());
                                View anchorLayout = inflater.inflate(R.layout.index_anchor_layout, contentLayout, false);
                                TextView title = anchorLayout.findViewById(R.id.anchorTitle);

                                title.setText("机构");
                                SimpleDraweeView logo=anchorLayout.findViewById(R.id.logo);
                                Station station=MyApplication.getInstance().getStation();
                                if(station!=null&&!TextUtils.isEmpty(station.getIcon3())){
                                    Uri uri = Uri.parse(station.getIcon3());
                                    int width=CommonUtils.dip2px(9);
                                    int height=width*2;
                                    ImageLoader.showThumb(uri,logo,width,height);
                                }else{
                                    logo.setVisibility(View.GONE);
                                }

                                organizationsLayout = anchorLayout.findViewById(R.id.anchorsLayout);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                contentLayout.addView(anchorLayout, params);
                                getCityOrganization( module.getOrder_by(),module.getContent_number());


                            } else if (module.getType() == 6){
                                //资讯
                                LayoutInflater inflater = LayoutInflater.from(getContext());
                                View clolumnLayout = inflater.inflate(R.layout.index_column_item, contentLayout, false);
                                SimpleDraweeView logo=clolumnLayout.findViewById(R.id.logo);
                                TextView column = clolumnLayout.findViewById(R.id.column);
                                TextView more= clolumnLayout.findViewById(R.id.more);
                                column.setText(module.getTitle());

                                Station station=MyApplication.getInstance().getStation();
                                if(station!=null&&!TextUtils.isEmpty(station.getIcon3())){
                                    Uri uri = Uri.parse(station.getIcon3());
                                    int width=CommonUtils.dip2px(9);
                                    int height=width*2;
                                    ImageLoader.showThumb(uri,logo,width,height);
                                }else{
                                    logo.setVisibility(View.GONE);
                                }


                                String[] ids= module.getData_source_id().split(",");
                                final String columnId=ids[ids.length-1];
                                more.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Column switchColumn=new Column();
                                        switchColumn.setId(Integer.parseInt(columnId));
                                        ((MainActivity)getActivity()).switchRecommend(switchColumn);
                                    }
                                });
                                RecyclerView recyclerView = clolumnLayout.findViewById(R.id.recyclerView);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
                                divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycleview_divider_drawable));
                                recyclerView.addItemDecoration(divider);
                                mContentRecyclerView.add(recyclerView);
                                contentLayout.addView(clolumnLayout);
                                getContent(module.getOrder_by(),columnId, "" + module.getContent_number(), mContentRecyclerView.size() - 1);

                            }else if (module.getType() == 15){
                                //视频
                                LayoutInflater inflater = LayoutInflater.from(getContext());
                                View clolumnLayout = inflater.inflate(R.layout.index_column_item, contentLayout, false);
                                SimpleDraweeView logo=clolumnLayout.findViewById(R.id.logo);
                                TextView column = clolumnLayout.findViewById(R.id.column);
                                TextView more= clolumnLayout.findViewById(R.id.more);
                                more.setVisibility(View.GONE);
                                column.setText(module.getTitle());

                                Station station=MyApplication.getInstance().getStation();
                                if(station!=null&&!TextUtils.isEmpty(station.getIcon3())){
                                    Uri uri = Uri.parse(station.getIcon3());
                                    int width=CommonUtils.dip2px(9);
                                    int height=width*2;
                                    ImageLoader.showThumb(uri,logo,width,height);
                                }else{
                                    logo.setVisibility(View.GONE);
                                }


                                String[] ids= module.getData_source_id().split(",");
                                final String columnId=ids[ids.length-1];
                                more.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                RecyclerView recyclerView = clolumnLayout.findViewById(R.id.recyclerView);

                                mContentRecyclerView.add(recyclerView);
                                contentLayout.addView(clolumnLayout);
                                getDecorateContent(module.getAnimated(),module.getOrder_by(),4,columnId, "" + module.getContent_number(), mContentRecyclerView.size() - 1);

                            }else if (module.getType() == 16){
                                //音频
                                LayoutInflater inflater = LayoutInflater.from(getContext());
                                View clolumnLayout = inflater.inflate(R.layout.index_column_item, contentLayout, false);
                                SimpleDraweeView logo=clolumnLayout.findViewById(R.id.logo);
                                TextView column = clolumnLayout.findViewById(R.id.column);
                                TextView more= clolumnLayout.findViewById(R.id.more);
                                more.setVisibility(View.GONE);
                                column.setText(module.getTitle());

                                Station station=MyApplication.getInstance().getStation();
                                if(station!=null&&!TextUtils.isEmpty(station.getIcon3())){
                                    Uri uri = Uri.parse(station.getIcon3());
                                    int width=CommonUtils.dip2px(9);
                                    int height=width*2;
                                    ImageLoader.showThumb(uri,logo,width,height);
                                }else{
                                    logo.setVisibility(View.GONE);
                                }


                                String[] ids= module.getData_source_id().split(",");
                                final String columnId=ids[ids.length-1];
                                more.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                RecyclerView recyclerView = clolumnLayout.findViewById(R.id.recyclerView);

                                mContentRecyclerView.add(recyclerView);
                                contentLayout.addView(clolumnLayout);
                                getDecorateContent(module.getAnimated(),module.getOrder_by(),2,columnId, "" + module.getContent_number(), mContentRecyclerView.size() - 1);

                            }else if (module.getType() == 17){
                                //文章
                                LayoutInflater inflater = LayoutInflater.from(getContext());
                                View clolumnLayout = inflater.inflate(R.layout.index_column_item, contentLayout, false);
                                SimpleDraweeView logo=clolumnLayout.findViewById(R.id.logo);
                                TextView column = clolumnLayout.findViewById(R.id.column);
                                TextView more= clolumnLayout.findViewById(R.id.more);
                                more.setVisibility(View.GONE);
                                column.setText(module.getTitle());

                                Station station=MyApplication.getInstance().getStation();
                                if(station!=null&&!TextUtils.isEmpty(station.getIcon3())){
                                    Uri uri = Uri.parse(station.getIcon3());
                                    int width=CommonUtils.dip2px(9);
                                    int height=width*2;
                                    ImageLoader.showThumb(uri,logo,width,height);
                                }else{
                                    logo.setVisibility(View.GONE);
                                }


                                String[] ids= module.getData_source_id().split(",");
                                final String columnId=ids[ids.length-1];
                                more.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                RecyclerView recyclerView = clolumnLayout.findViewById(R.id.recyclerView);

                                mContentRecyclerView.add(recyclerView);
                                contentLayout.addView(clolumnLayout);
                                getDecorateContent(module.getAnimated(),module.getOrder_by(),3,columnId, "" + module.getContent_number(), mContentRecyclerView.size() - 1);

                            }else if (module.getType() == 18){
                                //图集
                                LayoutInflater inflater = LayoutInflater.from(getContext());
                                View clolumnLayout = inflater.inflate(R.layout.index_column_item, contentLayout, false);
                                SimpleDraweeView logo=clolumnLayout.findViewById(R.id.logo);
                                TextView column = clolumnLayout.findViewById(R.id.column);
                                TextView more= clolumnLayout.findViewById(R.id.more);
                                more.setVisibility(View.GONE);
                                column.setText(module.getTitle());

                                Station station=MyApplication.getInstance().getStation();
                                if(station!=null&&!TextUtils.isEmpty(station.getIcon3())){
                                    Uri uri = Uri.parse(station.getIcon3());
                                    int width=CommonUtils.dip2px(9);
                                    int height=width*2;
                                    ImageLoader.showThumb(uri,logo,width,height);
                                }else{
                                    logo.setVisibility(View.GONE);
                                }


                                String[] ids= module.getData_source_id().split(",");
                                final String columnId=ids[ids.length-1];
                                more.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                RecyclerView recyclerView = clolumnLayout.findViewById(R.id.recyclerView);

                                mContentRecyclerView.add(recyclerView);
                                contentLayout.addView(clolumnLayout);
                                getDecorateContent(module.getAnimated(),module.getOrder_by(),7,columnId, "" + module.getContent_number(), mContentRecyclerView.size() - 1);

                            }

                        }


                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }

                break;
            case MSG_GET_COLUMN: {

                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        JSONObject jo = jsonObject.getJSONObject("data");
                        if (jo.getInteger("total") > 0) {
                            mColumns = JSONObject.parseArray(jo.getJSONArray("data").toJSONString(), Column.class);

                            for (int i = 0; mColumns != null && i < mColumns.size(); i++) {
                                Column column = mColumns.get(i);

                                LayoutInflater inflater = getLayoutInflater();
                                View view = inflater.inflate(R.layout.column_txt_layout, columnContent, false);
                                TextView columnTextView = view.findViewById(R.id.text);
                                SimpleDraweeView indicator = view.findViewById(R.id.indicator);
                                //columnTextView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                                columnTextView.setText(column.getName());
                                columnTextView.setTextColor(getActivity().getResources().getColorStateList(R.color.column_item_selector));
                                columnTextView.setSelected(false);
                                columnTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                indicator.setVisibility(View.INVISIBLE);
                                view.setTag(column);
                                view.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Column column=(Column)v.getTag();
                                        ((MainActivity)getActivity()).switchRecommend(column);
                                    }
                                });
                                columnContent.addView(view, i);
                            }


                        }
                    } else {

                    }


                } catch (Exception e) {

                }

            }

            break;

        }
        return false;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("onLocationChanged")) {
            //city.setText(MainActivity.sCurrentCity.getName());
            //getCityAnchors();
            //weather();
            //getCityColumns("");
            indexDecorate();
        }
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK) {
            indexDecorate();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
