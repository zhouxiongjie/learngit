package com.shuangling.software.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.activity.NewLoginActivity;
import com.shuangling.software.adapter.LiveInviteListAdapter;
import com.shuangling.software.adapter.LiveViewerListAdapter;
import com.shuangling.software.entity.InviteInfo;
import com.shuangling.software.entity.LiveRoomInfo01;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
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
public class InviteRangeListDialog extends BaseCircleDialog {


    Unbinder unbinder;

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.question)
    ImageView question;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.noData)
    LinearLayout noData;
    @BindView(R.id.totalAward)
    TextView totalAward;
    @BindView(R.id.order)
    TextView order;
    @BindView(R.id.logo)
    SimpleDraweeView logo;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.desc)
    TextView desc;
    @BindView(R.id.gotoInvite)
    TextView gotoInvite;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.noScriptText)
    TextView noScriptText;
    @BindView(R.id.login)
    RelativeLayout login;
    @BindView(R.id.gotoInvite01)
    TextView gotoInvite01;
    @BindView(R.id.logout)
    RelativeLayout logout;
    @BindView(R.id.a)
    RelativeLayout a;
    @BindView(R.id.b)
    LinearLayout b;
    @BindView(R.id.root)
    LinearLayout root;

    private LiveRoomInfo01.RoomInfoBean liveRoomInfo;

    private OnInviteListener mOnInviteListener;

    public void setOnInviteListener(OnInviteListener onInviteListener) {
        this.mOnInviteListener = onInviteListener;
    }


    public interface OnInviteListener {
        void invite();
    }


    public static InviteRangeListDialog getInstance(LiveRoomInfo01.RoomInfoBean roomInfo) {
        InviteRangeListDialog viewerListDialog = new InviteRangeListDialog();
        viewerListDialog.setCanceledBack(true);
        viewerListDialog.setCanceledOnTouchOutside(true);
        viewerListDialog.setGravity(Gravity.BOTTOM);
        viewerListDialog.setWidth(1f);
        viewerListDialog.liveRoomInfo = roomInfo;
        return viewerListDialog;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_invite_range_list, container, false);
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
        if (liveRoomInfo.getInvite_rewards() == 1) {
            totalAward.setVisibility(View.VISIBLE);
        } else {
            totalAward.setVisibility(View.GONE);
        }

        gotoInvite.setActivated(true);
        gotoInvite01.setActivated(true);

        if (User.getInstance() != null) {
            login.setVisibility(View.VISIBLE);
            logout.setVisibility(View.GONE);
        } else {
            login.setVisibility(View.GONE);
            logout.setVisibility(View.VISIBLE);
        }
        getInviteList();
        getInviteInfo();

        return rootView;
    }


    private void getInviteList() {
        String url = ServerInfo.live + "/v1/rooms/" + liveRoomInfo.getId() + "/invitation_list";
        Map<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("page_size", "" + Integer.MAX_VALUE);

        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        List<InviteInfo> inviteInfos = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), InviteInfo.class);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    if (inviteInfos==null||inviteInfos.size() == 0) {
                                        ViewGroup.LayoutParams lp=root.getLayoutParams();
                                        lp.height=CommonUtils.dip2px(350);
                                        root.setLayoutParams(lp);
                                        noData.setVisibility(View.VISIBLE);
                                    } else {
                                        noData.setVisibility(View.GONE);

                                        LiveInviteListAdapter adapter=new LiveInviteListAdapter(getContext(), inviteInfos, liveRoomInfo.getInvite_rewards() == 1);
                                        int height=adapter.getItemCount()*60+160;
                                        if(height>=350){
                                            height=350;
                                        }
                                        ViewGroup.LayoutParams lp=root.getLayoutParams();

                                        lp.height=CommonUtils.dip2px(height);
                                        root.setLayoutParams(lp);
                                        a.setVisibility(View.VISIBLE);
                                        b.setVisibility(View.VISIBLE);
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                        recyclerView.setLayoutManager(linearLayoutManager);
                                        recyclerView.setAdapter(adapter);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        });
                    } else {
                        ToastUtils.show("数据请求失败");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                Log.e("test", exception.toString());
            }
        });
    }


    private void getInviteInfo() {
        String url = ServerInfo.live + "/v1/rooms/" + liveRoomInfo.getId() + "/invitation_num";
        Map<String, String> params = new HashMap<>();

        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {

                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject jo = jsonObject.getJSONObject("data");
                                    int rank = jo.getInteger("invite_rank");
                                    if (rank == 0) {
                                        order.setText("-");
                                    } else {
                                        order.setText("" + rank);
                                    }
                                    if (User.getInstance() != null && !TextUtils.isEmpty(User.getInstance().getAvatar())) {
                                        Uri uri = Uri.parse(User.getInstance().getAvatar());
                                        int width = CommonUtils.dip2px(40);
                                        int height = width;
                                        ImageLoader.showThumb(uri, logo, width, height);
                                    }
                                    name.setText(User.getInstance() != null ? User.getInstance().getNickname() : "");
                                    String str = "邀请" + jo.getInteger("invite_num") + "人  获得￥" + (jo.getInteger("invite_bonus") == 0 ? "-" : String.format("%.2f", (float) jo.getInteger("invite_bonus") / 100));
                                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#EB4B29"));
                                    SpannableString spannableString = new SpannableString(str);
                                    spannableString.setSpan(foregroundColorSpan, str.indexOf("￥"), str.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                    desc.setText(spannableString);
                                    number.setText("" + jo.getInteger("pre_distance") + "人");

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                Log.e("test", exception.toString());
            }
        });
    }

    @OnClick({R.id.back, R.id.question, R.id.gotoInvite, R.id.gotoInvite01})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                title.setText("热度榜");

                question.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
                break;
            case R.id.question:
                title.setText("热度榜排名规则");
                question.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
                break;
            case R.id.gotoInvite:
            case R.id.gotoInvite01:
                if (User.getInstance() == null) {
                    dismiss();
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    startActivity(it);
                } else {
                    if (mOnInviteListener != null) {
                        mOnInviteListener.invite();
                    }
                }
                break;
        }
    }


}
