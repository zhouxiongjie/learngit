package com.shuangling.software.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.activity.CityListActivity;
import com.shuangling.software.activity.MainActivity;
import com.shuangling.software.activity.SearchActivity;
import com.shuangling.software.adapter.AnchorGridViewAdapter;
import com.shuangling.software.adapter.ColumnContentAdapter;
import com.shuangling.software.adapter.ServiceGridViewAdapter;
import com.shuangling.software.customview.BannerView;
import com.shuangling.software.customview.MyGridView;
import com.shuangling.software.entity.Anchor;
import com.shuangling.software.entity.BannerInfo;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.LocalService;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.FloatWindowUtil;
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
import cn.jake.share.frdialog.dialog.FRDialog;
import cn.jake.share.frdialog.interfaces.FRDialogClickListener;
import okhttp3.Call;
import okhttp3.Response;


public class IndexFragment extends Fragment implements Handler.Callback {

    public static final int MSG_GET_AHCHORS = 0x1;
    public static final int MSG_GET_SERVICE = 0x2;
    public static final int MSG_GET_CITY_CONTNET = 0x3;

    public static final int REQUEST_PERMISSION_CODE = 0x0110;
    @BindView(R.id.city)
    TextView city;
    @BindView(R.id.temperature)
    TextView temperature;
    @BindView(R.id.weather)
    TextView weather;
    @BindView(R.id.search)
    TextView search;
    @BindView(R.id.bannerView)
    BannerView bannerView;
    Unbinder unbinder;
    @BindView(R.id.anchorsLayout)
    LinearLayout anchorsLayout;
    @BindView(R.id.moreService)
    TextView moreService;
    @BindView(R.id.service)
    MyGridView service;
    @BindView(R.id.serviceLayout)
    LinearLayout serviceLayout;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.contentLayout)
    LinearLayout contentLayout;
    @BindView(R.id.backgroundImage)
    ImageView backgroundImage;

    private Handler mHandler;
    //private AnchorGridViewAdapter mAnchorAdapter;
    private ServiceGridViewAdapter mServiceAdapter;

    private int page = 1;
    private int pageSize = 2;

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
        if(!TextUtils.isEmpty(MyApplication.getInstance().getBackgroundImage())){
            Uri uri = Uri.parse(MyApplication.getInstance().getBackgroundImage());
            backgroundImage.setImageURI(uri);
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<BannerView.Banner> banners = new ArrayList<>();
                BannerInfo banner1 = new BannerInfo();
                banner1.setLogo("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1558523555589&di=2bbf96e7f5bea35ab6f29f1a738a045c&imgtype=0&src=http%3A%2F%2Fimg2.ph.126.net%2FjH7TQ40YikZVm13azaOqRQ%3D%3D%2F6597262181193553397.jpg");
                BannerInfo banner2 = new BannerInfo();
                banner2.setLogo("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1558523671340&di=9883ee3f1ecdbfaf967add34eda852f4&imgtype=0&src=http%3A%2F%2Fs9.sinaimg.cn%2Fmw690%2F006hikKrzy7pzDEQbFe68%26690");
                BannerInfo banner3 = new BannerInfo();
                banner3.setLogo("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1558523671338&di=7a78d5289720a87e0dbedcea1ef3f420&imgtype=0&src=http%3A%2F%2Fimg2.ph.126.net%2F8Y1u9aYRhqT4KHumTO_y1w%3D%3D%2F6619210632305894354.jpg");
                banners.add(banner1);
                banners.add(banner2);
                banners.add(banner3);
                bannerView.setData(banners);
            }
        }, 1000);

        temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatWindowUtil.getInstance().addOnPermissionListener(new FloatWindowUtil.OnPermissionListener() {
                    @Override
                    public void showPermissionDialog() {
                        FRDialog dialog = new FRDialog.MDBuilder(getContext())
                                .setTitle("悬浮窗权限")
                                .setMessage("您的手机没有授予悬浮窗权限，请开启后再试")
                                .setPositiveContentAndListener("现在去开启", new FRDialogClickListener() {
                                    @Override
                                    public boolean onDialogClick(View view) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                            intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                                            startActivityForResult(intent, REQUEST_PERMISSION_CODE);
                                        }
                                        return true;
                                    }
                                }).setNegativeContentAndListener("暂不开启", new FRDialogClickListener() {
                                    @Override
                                    public boolean onDialogClick(View view) {
                                        return true;
                                    }
                                }).create();
                        dialog.show();
                    }
                });
                FloatWindowUtil.getInstance().showFloatWindow(getContext());
            }
        });


        return view;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.city, R.id.search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.city:
                startActivity(new Intent(getContext(), CityListActivity.class));
                break;
            case R.id.search:
                startActivity(new Intent(getContext(), SearchActivity.class));

                break;
        }
    }


    public void getCityAnchors() {

        String url = ServerInfo.serviceIP + ServerInfo.getCityAnchors;
        Map<String, String> params = new HashMap<String, String>();
        params.put("city_code", "" + MainActivity.sCurrentCity.getCode());
        params.put("order_by", "3");
        params.put("page", "1");
        params.put("page_size", "" + Integer.MAX_VALUE);
        params.put("type", "2");
        params.put("mode", "one");
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                Message msg = Message.obtain();
                msg.what = MSG_GET_AHCHORS;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, IOException exception) {


            }
        });


    }


    public void getCityService() {

        String url = ServerInfo.serviceIP + ServerInfo.getCityService;
        Map<String, String> params = new HashMap<String, String>();
        params.put("city_code", "" + MainActivity.sCurrentCity.getCode());


        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                Message msg = Message.obtain();
                msg.what = MSG_GET_SERVICE;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, IOException exception) {


            }
        });


    }


    public void getCityColumns() {

        String url = ServerInfo.serviceIP + ServerInfo.getCityContent;
        Map<String, String> params = new HashMap<String, String>();
        params.put("city_code", "" + MainActivity.sCurrentCity.getCode());
        params.put("page", "" + page);
        params.put("page_size", "" + pageSize);
        params.put("sorce_type", "city");

        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_CITY_CONTNET;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, IOException exception) {


            }
        });


    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_AHCHORS:

                try {
                    String result = (String) msg.obj;
                    JSONObject jo = JSONObject.parseObject(result);
                    if (jo.getIntValue("code") == 100000 && jo.getJSONObject("data") != null && jo.getJSONObject("data").getJSONArray("data") != null) {
                        List<Anchor> anchorList = JSONArray.parseArray(jo.getJSONObject("data").getJSONArray("data").toJSONString(), Anchor.class);

                        for (int i = 0; i < anchorList.size(); i++) {
                            Anchor anchor = anchorList.get(i);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonUtils.dip2px(140), LinearLayout.LayoutParams.WRAP_CONTENT);
                            int margin = CommonUtils.dip2px(5);
                            params.setMargins(margin, margin, margin, margin);
                            View anchorView = LayoutInflater.from(getContext()).inflate(R.layout.anchor_item_layout, anchorsLayout, false);
                            TextView anchorName = anchorView.findViewById(R.id.anchorName);
                            SimpleDraweeView logo = anchorView.findViewById(R.id.logo);
                            TextView desc = anchorView.findViewById(R.id.desc);
                            TextView attention = anchorView.findViewById(R.id.attention);


                            if (!TextUtils.isEmpty(anchor.getLogo())) {
                                Uri uri = Uri.parse(anchor.getLogo());
                                int width = CommonUtils.dip2px(65);
                                int height = width;
                                ImageLoader.showThumb(uri, logo, width, height);
                            }
                            anchorName.setText(anchor.getName());
                            desc.setText(anchor.getDes());
                            attention.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                }
                            });
                            anchorsLayout.addView(anchorView, i, params);
                        }


                    }


                } catch (Exception e) {

                }
                break;
            case MSG_GET_SERVICE:
                try {
                    String result = (String) msg.obj;
                    LocalService localService = JSONObject.parseObject(result, LocalService.class);
                    if (localService.getCode() == 100000 && localService.getData() != null) {

                        if (getContext() == null) {
                            return false;
                        }
                        if (mServiceAdapter == null) {
                            mServiceAdapter = new ServiceGridViewAdapter(getContext(), localService);
                            service.setAdapter(mServiceAdapter);
                        } else {
                            mServiceAdapter.setData(localService);
                            mServiceAdapter.notifyDataSetChanged();
                        }

                    }


                } catch (Exception e) {

                }

                break;
            case MSG_GET_CITY_CONTNET:
                try {
                    String result = (String) msg.obj;
                    JSONObject jo = JSONObject.parseObject(result);
                    if (jo.getIntValue("code") == 100000 && jo.getJSONObject("data") != null && jo.getJSONObject("data").getJSONArray("data") != null) {
                        List<Column> columnList = JSONArray.parseArray(jo.getJSONObject("data").getJSONArray("data").toJSONString(), Column.class);

                        if (getContext() == null) {
                            return false;
                        }
                        if (page == 1) {

                            LayoutInflater inflater = LayoutInflater.from(getContext());
                            for (int i = 0; i < columnList.size(); i++) {
                                if (i == 0) {
                                    View view = inflater.inflate(R.layout.index_column_item, contentLayout, false);


                                    TextView column = view.findViewById(R.id.column);
                                    column.setText(columnList.get(i).getName());
                                    RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                                    TextView more = view.findViewById(R.id.more);
                                    ColumnContentAdapter adapter = new ColumnContentAdapter(getContext(), columnList.get(i).getContent());
                                    recyclerView.setAdapter(adapter);
                                    contentLayout.addView(view, 1);
                                } else {
                                    View view = inflater.inflate(R.layout.index_column_item, contentLayout, false);

                                    TextView column = view.findViewById(R.id.column);
                                    column.setText(columnList.get(i).getName());
                                    RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                    //recyclerView.addItemDecoration(new MyItemDecoration());
                                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                                    TextView more = view.findViewById(R.id.more);
                                    ColumnContentAdapter adapter = new ColumnContentAdapter(getContext(), columnList.get(i).getContent());
                                    recyclerView.setAdapter(adapter);
                                    contentLayout.addView(view, 3);
                                }


                            }
                        } else {
                            LayoutInflater inflater = LayoutInflater.from(getContext());
                            for (int i = 0; i < columnList.size(); i++) {
                                View view = inflater.inflate(R.layout.index_column_item, contentLayout, false);


                                TextView column = view.findViewById(R.id.column);
                                column.setText(columnList.get(i).getName());
                                RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                                TextView more = (TextView) view.findViewById(R.id.more);
                                ColumnContentAdapter adapter = new ColumnContentAdapter(getContext(), columnList.get(i).getContent());
                                recyclerView.setAdapter(adapter);
                                contentLayout.addView(view);

                            }


                        }
                        if (jo.getJSONObject("data").getIntValue("to") == pageSize) {
                            page++;
                        }


                    }


                } catch (Exception e) {

                }

                break;
        }
        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (FloatWindowUtil.checkFloatWindowPermission(getContext())) {
                    FloatWindowUtil.getInstance().showFloatWindow(getContext());
                } else {
                    //不显示悬浮窗 并提示
                }


            }
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("onLocationChanged")) {
            city.setText(MainActivity.sCurrentCity.getName());
            getCityAnchors();
            getCityService();
            getCityColumns();
        }
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
