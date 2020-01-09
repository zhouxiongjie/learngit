package com.shuangling.software.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.AccountInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.fragment.AttentionFragment;
import com.shuangling.software.fragment.CashIncomeFragment;
import com.shuangling.software.fragment.TakeCashFragment;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

@EnableDragToClose()
public class MyWalletsActivity extends AppCompatActivity implements Handler.Callback {

    public static final String TAG = "MyWalletsActivity";

    private static final int MSG_ACCOUNT_DETAIL = 0;


    public static final int[] category = new int[]{R.string.income, R.string.cash_record};
    @BindView(R.id.activtyTitle)
    TopTitleBar activtyTitle;
    @BindView(R.id.money)
    TextView money;
    @BindView(R.id.cash)
    TextView cash;
    @BindView(R.id.tabPageIndicator)
    TabLayout tabPageIndicator;
    @BindView(R.id.viewPager)
    ViewPager viewPager;


    private FragmentAdapter mFragmentPagerAdapter;
    public int mCurrentItem;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_wallets);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        init();
    }


    private void init() {
        mHandler=new Handler(this);
        mFragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mFragmentPagerAdapter);
        tabPageIndicator.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mCurrentItem = i;

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        getAccountDetail();

    }


    private void getAccountDetail() {

        String url = ServerInfo.emc + ServerInfo.accountDetail;
        Map<String, String> params = new HashMap<String, String>();


        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = mHandler.obtainMessage(MSG_ACCOUNT_DETAIL);
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }



//    @OnClick({R.id.cash})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.cash:
//                Intent it=new Intent(this,CashDetailActivity.class);
//                startActivity(it);
//                break;
//
//        }
//    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case MSG_ACCOUNT_DETAIL:
                try{
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        final AccountInfo accountInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), AccountInfo.class);

                        if(accountInfo!=null){
                            money.setText(String.format("%.2f",(float)accountInfo.getFree_balance()/100));

                            cash.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it=new Intent(MyWalletsActivity.this,CashActivity.class);
                                    it.putExtra("money",accountInfo.getFree_balance());
                                    startActivity(it);
                                }
                            });
                        }


                    }else if(jsonObject != null){
                        ToastUtils.show(jsonObject.getString("msg"));
                    }
                }catch (Exception e){

                }
                break;
        }
        return false;
    }


    public class FragmentAdapter extends FragmentStatePagerAdapter {

        //private FragmentManager fm;

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
            //this.fm = fm;
        }


        @Override
        public int getCount() {
            return category.length;
        }

        @Override
        public Fragment getItem(int position) {


            if(position==0){
                CashIncomeFragment fragment = new CashIncomeFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("category", category[position]);
                fragment.setArguments(bundle);
                return fragment;
            }else{
                TakeCashFragment fragment = new TakeCashFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("category", category[position]);
                fragment.setArguments(bundle);
                return fragment;
            }




        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getString(category[position]);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);

            return fragment;
        }

    }

}
