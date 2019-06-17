package com.shuangling.software.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.LoginActivity;
import com.shuangling.software.activity.ModifyUserInfoActivity;
import com.shuangling.software.entity.User;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class PersonalCenterFragment extends Fragment {


    public static final int REQUEST_LOGIN = 0x00;


    @BindView(R.id.top)
    RelativeLayout top;
    @BindView(R.id.recentListen)
    RelativeLayout recentListen;
    @BindView(R.id.collect)
    RelativeLayout collect;
    @BindView(R.id.subscribe)
    RelativeLayout subscribe;
    @BindView(R.id.accountAndSecurity)
    RelativeLayout accountAndSecurity;
    @BindView(R.id.content)
    LinearLayout content;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

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

    @OnClick({R.id.recentListen, R.id.collect, R.id.subscribe, R.id.accountAndSecurity,R.id.headBg, R.id.login,R.id.loginLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.recentListen:
                break;
            case R.id.collect:
                break;
            case R.id.subscribe:
                break;
            case R.id.accountAndSecurity:
                break;
            case R.id.headBg:
                if(User.getInstance()!=null){
                    startActivity(new Intent(getContext(),ModifyUserInfoActivity.class));
                }
                break;
            case R.id.loginLayout:
                break;
            case R.id.login:
                if(User.getInstance()==null){
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    //startActivityForResult(it, REQUEST_LOGIN);
                    startActivity(it);
                }

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
            userName.setText(User.getInstance().getUsername());
            attentionNumber.setText("" + User.getInstance().getStatus());
        } else {
            loginLayout.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
            ImageLoader.showThumb(head, R.drawable.ic_user4);
        }

    }

}
