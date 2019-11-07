package com.shuangling.software.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.AccountAndSecurityActivity;
import com.shuangling.software.activity.AttentionActivity;
import com.shuangling.software.activity.CluesActivity;
import com.shuangling.software.activity.CollectActivity;
import com.shuangling.software.activity.FeedbackActivity;
import com.shuangling.software.activity.HistoryActivity;
import com.shuangling.software.activity.LoginActivity;
import com.shuangling.software.activity.MessageListActivity;
import com.shuangling.software.activity.ModifyUserInfoActivity;
import com.shuangling.software.activity.SettingActivity;
import com.shuangling.software.activity.SubscribeActivity;
import com.shuangling.software.activity.WebViewActivity;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Response;


public class PersonalCenterFragment extends Fragment {


    public static final int MSG_UPDATE_STATUS = 0x01;

    @BindView(R.id.top)
    RelativeLayout top;
    @BindView(R.id.history)
    RelativeLayout history;
    @BindView(R.id.collect)
    RelativeLayout collect;
    @BindView(R.id.subscribe)
    RelativeLayout subscribe;
    @BindView(R.id.accountAndSecurity)
    RelativeLayout accountAndSecurity;
//    @BindView(R.id.content)
//    LinearLayout content;
    @BindView(R.id.head)
    SimpleDraweeView head;
    @BindView(R.id.headBg)
    SimpleDraweeView headBg;
    @BindView(R.id.loginLayout)
    LinearLayout loginLayout;
    @BindView(R.id.login)
    TextView login;
    Unbinder unbinder;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.attentionNumber)
    TextView attentionNumber;
    @BindView(R.id.userLayout)
    RelativeLayout userLayout;
    @BindView(R.id.feedback)
    RelativeLayout feedback;
    @BindView(R.id.brokeNews)
    RelativeLayout brokeNews;
    @BindView(R.id.messageNumber)
    TextView messageNumber;
    @BindView(R.id.messageLayout)
    FrameLayout messageLayout;
    @BindView(R.id.setting)
    RelativeLayout setting;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mHandler = new Handler(Looper.getMainLooper());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personalcenter, container, false);

        unbinder = ButterKnife.bind(this, view);
        return view;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.history, R.id.collect, R.id.subscribe, R.id.accountAndSecurity, R.id.headBg, R.id.login, R.id.loginLayout, R.id.feedback, R.id.brokeNews, R.id.messageLayout,R.id.setting,R.id.attentionNumber})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.history:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), HistoryActivity.class));
                } else {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.collect:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), CollectActivity.class));
                } else {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.subscribe:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), SubscribeActivity.class));
                } else {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.accountAndSecurity:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), AccountAndSecurityActivity.class));
                } else {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.headBg:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), ModifyUserInfoActivity.class));
                } else {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.loginLayout:
                break;
            case R.id.login:
                if (User.getInstance() == null) {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }

                break;
            case R.id.feedback:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), FeedbackActivity.class));
                } else {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.brokeNews:
                if (User.getInstance() != null) {
                    Intent it = new Intent(getContext(), CluesActivity.class);
                    it.putExtra("url",ServerInfo.scs+"/broke-create");
                    startActivity(it);
                } else {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.messageLayout:
                if (User.getInstance() != null) {
                    startActivity(new Intent(getContext(), MessageListActivity.class));
                } else {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.setting:

                startActivity(new Intent(getContext(), SettingActivity.class));

                break;
            case R.id.attentionNumber:
                startActivity(new Intent(getContext(), AttentionActivity.class));
                break;
        }
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_LOGIN) {
//            if (User.getInstance() != null) {
//                loginLayout.setVisibility(View.VISIBLE);
//                login.setVisibility(View.GONE);
//                if (!TextUtils.isEmpty(User.getInstance().getAvatar())) {
//                    Uri uri = Uri.parse(User.getInstance().getAvatar());
//                    int width = CommonUtils.dip2px(50);
//                    int height = width;
//                    ImageLoader.showThumb(uri, head, width, height);
//                }
//                userName.setText(User.getInstance().getUsername());
//                attentionNumber.setText("" + User.getInstance().getStatus());
//            } else {
//                loginLayout.setVisibility(View.GONE);
//                login.setVisibility(View.VISIBLE);
//                ImageLoader.showThumb(head, R.drawable.ic_user3);
//            }
//        }
//    }


    @Override
    public void onResume() {
        super.onResume();
        if (User.getInstance() != null) {
            loginLayout.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(User.getInstance().getAvatar())) {
                Uri uri = Uri.parse(User.getInstance().getAvatar());
                int width = CommonUtils.dip2px(50);
                int height = width;
                ImageLoader.showThumb(uri, head, width, height);
            }
            userName.setText(User.getInstance().getNickname());
            //attentionNumber.setText("关注:" + User.getInstance().getStatus());
            updateStatistics();
        } else {
            loginLayout.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
            ImageLoader.showThumb(head, R.drawable.ic_user4);
            messageNumber.setVisibility(View.GONE);
        }

    }

    public void updateStatistics() {

        String url = ServerInfo.serviceIP + ServerInfo.statistics;

        Map<String, String> params = new HashMap<>();


        OkHttpUtils.get(url, null, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                String result = response;
                final JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            attentionNumber.setText("关注:" + jsonObject.getJSONObject("data").getInteger("follows_count"));
                            messageNumber.setText("" + jsonObject.getJSONObject("data").getInteger("not_read_num"));
                            if (jsonObject.getJSONObject("data").getInteger("not_read_num") > 0) {
                                messageNumber.setVisibility(View.VISIBLE);
                            } else {
                                messageNumber.setVisibility(View.GONE);
                            }


                        }
                    });
                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });

    }



}
