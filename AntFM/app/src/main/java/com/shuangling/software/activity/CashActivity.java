package com.shuangling.software.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.dialog.AccountSettingDialog;
import com.shuangling.software.dialog.BindWeixinDialog;
import com.shuangling.software.dialog.CashRegularDialog;
import com.shuangling.software.entity.AccountInfo;
import com.shuangling.software.entity.BaseModel;
import com.shuangling.software.entity.CashRegular;
import com.shuangling.software.entity.WeixinAccountInfo;
import com.shuangling.software.entity.ZhifubaoAccountInfo;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;

@EnableDragToClose()
public class CashActivity extends AppCompatActivity implements Handler.Callback, PlatformActionListener {

    public static final String TAG = "CashActivity";

    private static final int MSG_ACCOUNT_DETAIL = 0;
    private static final int MSG_TAKE_CASH = 1;
    private static final int MSG_CASH_REGULAR = 2;
    private static final int MSG_GET_MONEY = 3;
    private static final int MSG_WEIXIN_ACCOUNT_DETAIL = 4;

    private static final int MSG_AUTH_CANCEL = 5;
    private static final int MSG_AUTH_ERROR = 6;
    private static final int MSG_AUTH_COMPLETE = 7;

    @BindView(R.id.activtyTitle)
    TopTitleBar activtyTitle;
    @BindView(R.id.money)
    TextView money;
    @BindView(R.id.zfbSelectedIcon)
    FontIconView zfbSelectedIcon;
    @BindView(R.id.zfb)
    RelativeLayout zfb;
    @BindView(R.id.wxSelectedIcon)
    FontIconView wxSelectedIcon;
    @BindView(R.id.wx)
    RelativeLayout wx;
    @BindView(R.id.allMoney)
    TextView allMoney;
    @BindView(R.id.allSelectedIcon)
    FontIconView allSelectedIcon;
    @BindView(R.id.cashAll)
    RelativeLayout cashAll;
    @BindView(R.id.customAmount)
    EditText customAmount;
    @BindView(R.id.customSelectedIcon)
    FontIconView customSelectedIcon;
    @BindView(R.id.account)
    TextView account;
    @BindView(R.id.modifyAccount)
    TextView modifyAccount;
    @BindView(R.id.accountLayout)
    RelativeLayout accountLayout;
    @BindView(R.id.cash)
    TextView cash;
    @BindView(R.id.regular)
    LinearLayout regular;
    @BindView(R.id.customAmountLayout)
    RelativeLayout customAmountLayout;
    @BindView(R.id.weixinTip)
    TextView weixinTip;


    private int moneySum;
    private Handler mHandler;
    private ZhifubaoAccountInfo mZhifubaoAccountInfo;
    private WeixinAccountInfo mWeixinAccountInfo;
    private CashRegular mCashRegular;

    private DialogFragment mDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cash);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        init();
    }


    private void init() {
        mHandler = new Handler(this);
        EventBus.getDefault().register(this);
        moneySum = getIntent().getIntExtra("money", 0);
        money.setText(String.format("%.2f", (float) moneySum / 100));
        allMoney.setText(String.format("%.2f", (float) moneySum / 100) + "元");
        customAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CommonUtils.hideInput(CashActivity.this);
                    customAmount.setHint("自定义");

                    cashAll.setSelected(true);
                    customAmountLayout.setSelected(false);
                    allSelectedIcon.setVisibility(View.VISIBLE);
                    customSelectedIcon.setVisibility(View.GONE);

                } else {
                    customAmount.setHint("请输入金额");
                    cashAll.setSelected(false);
                    customAmountLayout.setSelected(true);
                    allSelectedIcon.setVisibility(View.GONE);
                    customSelectedIcon.setVisibility(View.VISIBLE);

                }
            }
        });
        customAmountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cashAll.setSelected(false);
                customAmountLayout.setSelected(true);
                allSelectedIcon.setVisibility(View.GONE);
                customSelectedIcon.setVisibility(View.VISIBLE);

                //cashAll.requestFocus();
//                customAmount.setSelected(false);
                customAmount.requestFocus();
                customAmount.setHint("请输入金额");
            }
        });
        //customAmount.clearFocus();
        cashAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cashAll.setSelected(true);
                customAmountLayout.setSelected(false);
                allSelectedIcon.setVisibility(View.VISIBLE);
                customSelectedIcon.setVisibility(View.GONE);

                CommonUtils.hideInput(CashActivity.this);
                customAmount.setHint("自定义");
                //cashAll.requestFocus();
//                customAmount.setSelected(false);
                customAmount.clearFocus();
            }
        });

        wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wx.setSelected(true);
                zfb.setSelected(false);
                wxSelectedIcon.setVisibility(View.VISIBLE);
                zfbSelectedIcon.setVisibility(View.GONE);
                weixinTip.setVisibility(View.VISIBLE);
                getThirdPlatformInfo();
            }
        });
        zfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wx.setSelected(false);
                zfb.setSelected(true);
                wxSelectedIcon.setVisibility(View.GONE);
                zfbSelectedIcon.setVisibility(View.VISIBLE);
                weixinTip.setVisibility(View.GONE);
                getAccountDetail();
            }
        });


//        customAmount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cashAll.setSelected(false);
//                customAmount.setSelected(true);
//            }
//        });
        //默认两位小数
        //customAmount.addTextChangedListener(new MoneyTextWatcher(customAmount));
        //手动设置其他位数，例如3
        customAmount.addTextChangedListener(new MoneyTextWatcher(customAmount).setDigits(2));

        getCashRegular();
    }


    private void getAccountDetail() {

        String url = ServerInfo.emc + ServerInfo.takeCashAccount;
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


    private void getThirdPlatformInfo() {
        String url = ServerInfo.serviceIP + ServerInfo.getThirdPlatformInfo;

        OkHttpUtils.get(url, null, new OkHttpCallback(this) {
            @Override
            public void onFailure(Call call, Exception e) {

            }

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = mHandler.obtainMessage(MSG_WEIXIN_ACCOUNT_DETAIL);
                msg.obj = response;
                mHandler.sendMessage(msg);


            }
        });

    }


    private void getMoney() {

        String url = ServerInfo.emc + ServerInfo.accountDetail;
        Map<String, String> params = new HashMap<String, String>();


        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = mHandler.obtainMessage(MSG_GET_MONEY);
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }


    private void getCashRegular() {

        String url = ServerInfo.emc + ServerInfo.getCashRegular;
        Map<String, String> params = new HashMap<String, String>();


        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = mHandler.obtainMessage(MSG_CASH_REGULAR);
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }

    private void weixinTakeCash(int money) {

        String url = ServerInfo.emc + ServerInfo.requestWeixinCash;
        Map<String, String> params = new HashMap<String, String>();
        params.put("money", "" + money);

        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = mHandler.obtainMessage(MSG_TAKE_CASH);
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.show("提现请求失败，稍候再试");
                    }
                });
            }
        });
    }


    private void takeCash(int money) {

        String url = ServerInfo.emc + ServerInfo.requestCash;
        Map<String, String> params = new HashMap<String, String>();
        params.put("money", "" + money);
        params.put("account", mZhifubaoAccountInfo.getAccount());
        params.put("name", mZhifubaoAccountInfo.getName());

        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = mHandler.obtainMessage(MSG_TAKE_CASH);
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.show("提现请求失败，稍候再试");
                    }
                });
            }
        });
    }


    @OnClick({R.id.cash, R.id.regular})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cash:

                if (zfb.isSelected()) {
                    if (mZhifubaoAccountInfo == null) {
                        ToastUtils.show("请先设置提现账号");
                    } else {
                        if (cashAll.isFocused()) {

                            if (mCashRegular != null) {
                                if (moneySum >= mCashRegular.getMin_money() && moneySum <= mCashRegular.getMax_money()) {
                                    //提现
                                    takeCash(moneySum);
                                } else if (moneySum < mCashRegular.getMin_money()) {
                                    ToastUtils.show("金额小于最小提现额");
                                } else if (moneySum > mCashRegular.getMax_money()) {
                                    ToastUtils.show("金额大于最大提现额");
                                }
                            } else {
                                ToastUtils.show("提现规则获取失败");
                            }


                        } else {
                            if (TextUtils.isEmpty(customAmount.getText().toString().trim())) {
                                ToastUtils.show("请输入提现金额");
                            } else {
                                if (mCashRegular != null) {

                                    float money = Float.parseFloat(customAmount.getText().toString().trim());

                                    if ((int) (money * 100) >= mCashRegular.getMin_money() && (int) (money * 100) <= moneySum && (int) (money * 100) <= mCashRegular.getMax_money()) {
                                        //提现
                                        takeCash((int) (money * 100));
                                    } else if ((int) (money * 100) > moneySum) {
                                        ToastUtils.show("余额不足");
                                    } else if ((int) (money * 100) > mCashRegular.getMax_money()) {
                                        ToastUtils.show("金额大于最大提现额");
                                    } else if ((int) (money * 100) < mCashRegular.getMin_money()) {
                                        ToastUtils.show("金额小于最小提现额");
                                    }
                                } else {
                                    ToastUtils.show("提现规则获取失败");
                                }


                            }

                        }
                    }


                } else if (wx.isSelected()) {
                    if (mWeixinAccountInfo == null) {
                        //ToastUtils.show("请先设置提现账号");
                        //显示微信账号绑定
                        BindWeixinDialog dialog = BindWeixinDialog.getInstance();
                        dialog.setOnBindClickListener(new BindWeixinDialog.OnBindClickListener() {
                            @Override
                            public void bind() {

                                getWechatInfo();

                            }
                        });
                        dialog.show(getSupportFragmentManager(), "BindWeixinDialog");


                    } else {
                        if (cashAll.isFocused()) {

                            if (mCashRegular != null) {
                                if (moneySum >= mCashRegular.getMin_money() && moneySum <= mCashRegular.getMax_money()) {
                                    //提现
                                    weixinTakeCash(moneySum);
                                } else if (moneySum < mCashRegular.getMin_money()) {
                                    ToastUtils.show("金额小于最小提现额");
                                } else if (moneySum > mCashRegular.getMax_money()) {
                                    ToastUtils.show("金额大于最大提现额");
                                }
                            } else {
                                ToastUtils.show("提现规则获取失败");
                            }


                        } else {
                            if (TextUtils.isEmpty(customAmount.getText().toString().trim())) {
                                ToastUtils.show("请输入提现金额");
                            } else {
                                if (mCashRegular != null) {

                                    float money = Float.parseFloat(customAmount.getText().toString().trim());

                                    if ((int) (money * 100) >= mCashRegular.getMin_money() && (int) (money * 100) <= moneySum && (int) (money * 100) <= mCashRegular.getMax_money()) {
                                        //提现
                                        weixinTakeCash((int) (money * 100));
                                    } else if ((int) (money * 100) > moneySum) {
                                        ToastUtils.show("余额不足");
                                    } else if ((int) (money * 100) > mCashRegular.getMax_money()) {
                                        ToastUtils.show("金额大于最大提现额");
                                    } else if ((int) (money * 100) < mCashRegular.getMin_money()) {
                                        ToastUtils.show("金额小于最小提现额");
                                    }
                                } else {
                                    ToastUtils.show("提现规则获取失败");
                                }


                            }

                        }
                    }


                } else {
                    ToastUtils.show("请选择提现方式");
                }


                break;
            case R.id.regular:

                getCashRegular();
                if (mCashRegular != null) {
                    String rule = mCashRegular.getRule();
                    //rule=rule.replace("\\\\n","\\n");
                    CashRegularDialog.getInstance(rule.replace("\\n", "\n")).show(getSupportFragmentManager(), "CashRegularDialog");
                }


                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_ACCOUNT_DETAIL:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        JSONObject jo = jsonObject.getJSONObject("data");
                        if (jo == null) {
                            account.setText("请输入提现账号");
                            modifyAccount.setText("");
                            modifyAccount.setTextColor(Color.parseColor("#4690FF"));
                            Drawable drawableRight = getResources().getDrawable(R.drawable.ic_right);
                            modifyAccount.setCompoundDrawablesWithIntrinsicBounds(null,null, drawableRight, null);

                            accountLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AccountSettingDialog dialog = AccountSettingDialog.getInstance(0, mZhifubaoAccountInfo);
                                    dialog.setOnOkClickListener(new AccountSettingDialog.OnOkClickListener() {
                                        @Override
                                        public void ok() {
                                            getAccountDetail();
                                        }
                                    });
                                    dialog.show(getSupportFragmentManager(), "AccountSettingDialog");
                                }
                            });
                        } else {
                            mZhifubaoAccountInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), ZhifubaoAccountInfo.class);

                            if (mZhifubaoAccountInfo != null) {

                                String acc = mZhifubaoAccountInfo.getAccount();
                                if (acc.length() > 7) {
                                    String sub = acc.substring(3, 7);
                                    account.setText("提现账号" + acc.replace(sub, "****"));
                                } else {
                                    account.setText("提现账号" + acc);
                                }

                                modifyAccount.setText("修改");
                                modifyAccount.setTextColor(Color.parseColor("#4690FF"));
                                Drawable drawableRight = getResources().getDrawable(R.drawable.ic_right);
                                modifyAccount.setCompoundDrawablesWithIntrinsicBounds(null,null, drawableRight, null);
                                modifyAccount.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AccountSettingDialog dialog = AccountSettingDialog.getInstance(1, mZhifubaoAccountInfo);
                                        dialog.setOnOkClickListener(new AccountSettingDialog.OnOkClickListener() {
                                            @Override
                                            public void ok() {
                                                getAccountDetail();
                                            }
                                        });
                                        dialog.show(getSupportFragmentManager(), "AccountSettingDialog");
                                    }
                                });

                            }
                        }

                    } else if (jsonObject != null) {
                        ToastUtils.show(jsonObject.getString("msg"));
                    }
                } catch (Exception e) {

                }
                break;
            case MSG_WEIXIN_ACCOUNT_DETAIL:
                try {

                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {


                        JSONArray ja = jsonObject.getJSONArray("data");
                        if (ja == null || ja.size() == 0) {
                            account.setText("提现账号");
                            modifyAccount.setText("去绑定");
                            Drawable drawableRight = getResources().getDrawable(R.drawable.ic_right);
                            modifyAccount.setCompoundDrawablesWithIntrinsicBounds(null,null, drawableRight, null);
                            modifyAccount.setTextColor(Color.parseColor("#4690FF"));
                            accountLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getWechatInfo();
                                }
                            });
                        } else {
                            mWeixinAccountInfo = JSON.parseObject(jsonObject.getJSONArray("data").getJSONObject(0).toJSONString(), WeixinAccountInfo.class);

                            if (mWeixinAccountInfo != null) {

                                account.setText("提现账号");
                                modifyAccount.setText(mWeixinAccountInfo.getNickname());
                                modifyAccount.setTextColor(Color.parseColor("#E5999999"));
                                modifyAccount.setCompoundDrawablesWithIntrinsicBounds(null,null, null, null);
                                //account.setText("提现账号" + mWeixinAccountInfo.getNickname());
                                accountLayout.setOnClickListener(null);
                            }
                        }


                    } else if (jsonObject != null) {
                        ToastUtils.show(jsonObject.getString("msg"));
                    }


                } catch (Exception e) {

                }
                break;
            case MSG_TAKE_CASH:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        EventBus.getDefault().post(new CommonEvent("OnMoneyChanged"));
                        int requestId = jsonObject.getJSONObject("data").getInteger("id");
                        Intent it = new Intent(this, CashDetailActivity.class);
                        it.putExtra("id", requestId);
                        startActivity(it);

                    } else if (jsonObject != null) {
                        ToastUtils.show(jsonObject.getString("msg"));
                    } else {
                        ToastUtils.show("提现请求失败");
                    }
                } catch (Exception e) {

                }
                break;
            case MSG_CASH_REGULAR:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mCashRegular = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), CashRegular.class);


                    } else if (jsonObject != null) {
                        ToastUtils.show(jsonObject.getString("msg"));
                    } else {
                        ToastUtils.show("获取规则说明失败");
                    }
                } catch (Exception e) {

                }
                break;
            case MSG_GET_MONEY:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        final AccountInfo accountInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), AccountInfo.class);

                        if (accountInfo != null) {
                            moneySum = accountInfo.getFree_balance();
                            money.setText(String.format("%.2f", (float) moneySum / 100));
                            allMoney.setText(String.format("%.2f", (float) moneySum / 100) + "元");


                        }


                    } else if (jsonObject != null) {
                        ToastUtils.show(jsonObject.getString("msg"));
                    }
                } catch (Exception e) {

                }


                break;
            case MSG_AUTH_CANCEL: {
                //取消授权
                ToastUtils.show(R.string.auth_cancel);
            }
            break;
            case MSG_AUTH_ERROR: {
                //授权失败
                ToastUtils.show(R.string.auth_error);
            }
            break;
            case MSG_AUTH_COMPLETE: {
                //授权成功
                ToastUtils.show("授权成功");
                Platform platform = (Platform) msg.obj;

                bindWechat(platform, "0");


            }
            break;
        }
        return false;
    }


    /**
     * 描述   ：金额输入字体监听类，限制小数点后输入位数
     * <p>
     * 默认限制小数点2位
     * 默认第一位输入小数点时，转换为0.
     * 如果起始位置为0,且第二位跟的不是".",则无法后续输入
     * <p>
     * 作者   ：Created by DuanRui on 2017/9/28.
     */
    public class MoneyTextWatcher implements TextWatcher {
        private EditText editText;
        private int digits = 2;

        public MoneyTextWatcher(EditText et) {
            editText = et;
        }

        public MoneyTextWatcher setDigits(int d) {
            digits = d;
            return this;
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //删除“.”后面超过2位后的数据
            if (s.toString().contains(".")) {
                if (s.length() - 1 - s.toString().indexOf(".") > digits) {
                    s = s.toString().subSequence(0,
                            s.toString().indexOf(".") + digits + 1);
                    editText.setText(s);
                    editText.setSelection(s.length()); //光标移到最后
                }
            }
            //如果"."在起始位置,则起始位置自动补0
            if (s.toString().trim().substring(0).equals(".")) {
                s = "0" + s;
                editText.setText(s);
                editText.setSelection(2);
            }

            //如果起始位置为0,且第二位跟的不是".",则无法后续输入
            if (s.toString().startsWith("0")
                    && s.toString().trim().length() > 1) {
                if (!s.toString().substring(1, 2).equals(".")) {
                    editText.setText(s.subSequence(0, 1));
                    editText.setSelection(1);
                    return;
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("OnMoneyChanged")) {
            getMoney();

        }
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    private void getWechatInfo() {
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.setPlatformActionListener(this);
        // true不使用SSO授权，false使用SSO授权
        wechat.SSOSetting(false);
        //获取用户资料
        wechat.showUser(null);
    }


    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {

        if (action == Platform.ACTION_USER_INFOR) {
            Message msg = mHandler.obtainMessage(MSG_AUTH_COMPLETE);
            msg.obj = platform;
            mHandler.sendMessage(msg);
        }


    }

    @Override
    public void onError(Platform platform, int action, Throwable throwable) {
        if (action == Platform.ACTION_USER_INFOR) {
            mHandler.sendEmptyMessage(MSG_AUTH_ERROR);
            if (platform != null) {
                if (platform.isAuthValid()) {
                    platform.removeAccount(true);
                }
            }
        }
        throwable.printStackTrace();
    }

    @Override
    public void onCancel(Platform platform, int action) {
        if (action == Platform.ACTION_USER_INFOR) {
            mHandler.sendEmptyMessage(MSG_AUTH_CANCEL);
        }
    }


    private void bindWechat(final Platform platform, String support) {

        PlatformDb platDB = platform.getDb();//获取数平台数据DB
        //通过DB获取各种数据
        String weixinUnionid = platDB.get("unionid");
        String weixinOpenid = platDB.get("openid");
        String weixinNickname = platDB.get("nickname");
        String weixinHeadimgurl = platDB.get("icon");

        String url = ServerInfo.serviceIP + ServerInfo.bindWechat;
        Map<String, String> params = new HashMap<>();
        params.put("type", "2");
        params.put("nickname", weixinNickname);
        params.put("headimgurl", weixinHeadimgurl);
        params.put("openid", weixinOpenid);
        params.put("unionid", weixinUnionid);
        params.put("support", support);

        if (mDialogFragment != null) mDialogFragment.dismiss();
        mDialogFragment = CommonUtils.showLoadingDialog(getSupportFragmentManager());

        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onFailure(Call call, Exception e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mDialogFragment.dismiss();
                            ToastUtils.show("绑定失败,请稍后再试");
                            Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                            wechat.removeAccount(true);
                        } catch (Exception e) {

                        }

                    }
                });
            }

            @Override
            public void onResponse(Call call, String response) throws IOException {
                try {
                    final BaseModel result = JSON.parseObject(response, BaseModel.class);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mDialogFragment.dismiss();
                                if (result.getCode() == 100000) {
                                    getThirdPlatformInfo();
                                    ToastUtils.show("绑定成功");
                                    Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                                    wechat.removeAccount(true);
                                } else {
                                    forceBind(platform);
                                }
                            } catch (Exception e) {

                            }


                        }
                    });

                } catch (Exception e) {
                    try {
                        mDialogFragment.dismiss();
                        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                        wechat.removeAccount(true);
                    } catch (Exception ex) {

                    }

                }
            }
        });
    }


    private void forceBind(final Platform platform) {
        new CircleDialog.Builder()
                .setTitle("提示")
                .setText("该第三方账号已经绑定其他账号，是否重新绑定此账号？")
                .setPositive("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bindWechat(platform, "1");
                    }
                })
                .setNegative("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.show("取消绑定");
                    }
                })
                .setCanceledOnTouchOutside(false)
                .setCancelable(false)
                .show(getSupportFragmentManager());
    }

}
