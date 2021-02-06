package com.shuangling.software.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.activity.MainActivity;
import com.shuangling.software.adapter.LiveViewerListAdapter;
import com.shuangling.software.entity.LiveRoomInfo01;
import com.shuangling.software.entity.User;
import com.shuangling.software.entity.Viewer;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.PreloadWebView;
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
public class LuckAwardDialog extends BaseCircleDialog {

    @BindView(R.id.fl_web_container)
    FrameLayout flWebContainer;

    private LiveRoomInfo01.RoomInfoBean liveRoomInfo;
    Unbinder unbinder;


    public static LuckAwardDialog getInstance(LiveRoomInfo01.RoomInfoBean roomInfo) {
        LuckAwardDialog luckAwardDialog = new LuckAwardDialog();
        luckAwardDialog.setCanceledBack(true);
        luckAwardDialog.setCanceledOnTouchOutside(true);
        luckAwardDialog.setGravity(Gravity.BOTTOM);
        luckAwardDialog.setWidth(1f);
        luckAwardDialog.liveRoomInfo = roomInfo;
        return luckAwardDialog;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_luck_award, container, false);
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
        init();
        return rootView;
    }


    private void init() {
        WebView webView = PreloadWebView.getInstance().getWebView(getContext());
        flWebContainer.addView(webView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if(liveRoomInfo.getExpand_activities().size()>0){
            String url=liveRoomInfo.getExpand_activities().get(0).getUrl();
            url=initUrl(url);
            webView.loadUrl(url);
        }


    }

    private String initUrl(String url) {

        if (User.getInstance() == null) {
            if (MainActivity.sCurrentCity != null) {
                if (url.contains("?")) {
                    url = url + "&app=android&city=" + MainActivity.sCurrentCity.getCode() + "&multiple=" + CommonUtils.getFontSize();
                } else {
                    url = url + "?app=android&city=" + MainActivity.sCurrentCity.getCode() + "&multiple=" + CommonUtils.getFontSize();
                }
            } else {
                if (url.contains("?")) {
                    url = url + "&app=android" + "&multiple=" + CommonUtils.getFontSize();
                } else {
                    url = url + "?app=android" + "&multiple=" + CommonUtils.getFontSize();
                }
            }
        } else {
            if (MainActivity.sCurrentCity != null) {
                if (url.contains("?")) {
                    url = url + "&Authorization=" + User.getInstance().getAuthorization() + "&app=android&city=" + MainActivity.sCurrentCity.getCode() + "&multiple=" + CommonUtils.getFontSize();
                } else {
                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&city=" + MainActivity.sCurrentCity.getCode() + "&multiple=" + CommonUtils.getFontSize();
                }
            } else {
                if (url.contains("?")) {
                    url = url + "&Authorization=" + User.getInstance().getAuthorization() + "&app=android" + "&multiple=" + CommonUtils.getFontSize();
                } else {
                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android" + "&multiple=" + CommonUtils.getFontSize();
                }
            }
        }
        return url;


    }
}
