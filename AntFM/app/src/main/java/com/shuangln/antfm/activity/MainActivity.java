package com.shuangln.antfm.activity;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.jaeger.library.StatusBarUtil;
import com.shuangln.antfm.R;
import com.shuangln.antfm.fragment.DiscoverFragment;
import com.shuangln.antfm.fragment.IndexFragment;
import com.shuangln.antfm.fragment.PersonalCenterFragment;
import com.shuangln.antfm.fragment.RecommendFragment;
import com.shuangln.antfm.service.AudioPlayerService;
import com.shuangln.antfm.utils.StatusBarManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    @BindView(R.id.index)
    TextView index;
    @BindView(R.id.recommend)
    TextView recommend;
    @BindView(R.id.discover)
    TextView discover;
    @BindView(R.id.personalCenter)
    TextView personalCenter;
    @BindView(R.id.content)
    FrameLayout content;


    private Fragment indexFragment;
    private Fragment recommendFragment;
    private Fragment discoverFragment;
    private Fragment personalCenterFragment;


    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setTransparent(this);
        StatusBarManager.setImmersiveStatusBar(this, true);
        ButterKnife.bind(this);
        Intent it = new Intent(this, AudioPlayerService.class);
        startService(it);

        showFragment(0);

    }






    @OnClick({R.id.index, R.id.recommend, R.id.discover, R.id.personalCenter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.index:
                showFragment(0);
                break;
            case R.id.recommend:
                showFragment(1);
                break;
            case R.id.discover:
                showFragment(2);
                break;
            case R.id.personalCenter:
                showFragment(3);
                break;
        }
    }


    private void settingBackgound(TextView table) {
        table.setSelected(true);
        if(table!=index){
            index.setSelected(false);
        }
        if(table!=recommend){
            recommend.setSelected(false);
        }
        if(table!=discover){
            discover.setSelected(false);
        }
        if(table!=personalCenter){
            personalCenter.setSelected(false);
        }

    }


    private void hideFragments(FragmentTransaction transaction) {

        if (!index.isSelected()) {
            if(indexFragment!=null){
                transaction.hide(indexFragment);
            }
        }
        if (!recommend.isSelected()) {
            if(recommendFragment!=null){
                transaction.hide(recommendFragment);
            }

        }
        if (!discover.isSelected()) {
            if(discoverFragment!=null){
                transaction.hide(discoverFragment);
            }

        }
        if (!personalCenter.isSelected()) {
            if(personalCenterFragment!=null){
                transaction.hide(personalCenterFragment);
            }
        }

    }


    private void showFragment(int order){
        if(order==0){
            settingBackgound(index);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (indexFragment == null) {
                indexFragment = new IndexFragment();
                transaction.add(R.id.content, indexFragment);
            } else {
                transaction.show(indexFragment);
            }
            hideFragments(transaction);
            transaction.commit();
        }else if(order==1){
            settingBackgound(recommend);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (recommendFragment == null) {
                recommendFragment = new RecommendFragment();
                transaction.add(R.id.content, recommendFragment);
            } else {
                transaction.show(recommendFragment);
            }
            hideFragments(transaction);
            transaction.commit();
        }else if(order==2){
            settingBackgound(discover);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (discoverFragment == null) {
                discoverFragment = new DiscoverFragment();
                transaction.add(R.id.content, discoverFragment);
            } else {
                transaction.show(discoverFragment);
            }
            hideFragments(transaction);
            transaction.commit();
        }else if(order==3){
            settingBackgound(personalCenter);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (personalCenterFragment == null) {
                personalCenterFragment = new PersonalCenterFragment();
                transaction.add(R.id.content, personalCenterFragment);
            } else {
                transaction.show(personalCenterFragment);
            }
            hideFragments(transaction);
            transaction.commit();
        }
    }


    @Override
    protected void onDestroy() {
        Intent it = new Intent(this, AudioPlayerService.class);
        stopService(it);
        super.onDestroy();
    }
}
