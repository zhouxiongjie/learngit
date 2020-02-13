package com.shuangling.software.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.CashDetail;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

@EnableDragToClose()
public class CashDetailActivity extends AppCompatActivity implements Handler.Callback {

    public static final String TAG = "MyWalletsActivity";

    private static final int MSG_CASH_DETAIL = 0;
    private static final int MSG_CASH_REGULAR = 1;

    @BindView(R.id.activtyTitle)
    TopTitleBar activtyTitle;
    @BindView(R.id.money)
    TextView money;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.account)
    TextView account;
    @BindView(R.id.applyTime)
    TextView applyTime;
    @BindView(R.id.processIcon)
    ImageView processIcon;
    @BindView(R.id.processDes)
    TextView processDes;
    @BindView(R.id.processTime)
    TextView processTime;
    @BindView(R.id.finishIcon)
    ImageView finishIcon;
    @BindView(R.id.finishDes)
    TextView finishDes;
    @BindView(R.id.finishTime)
    TextView finishTime;
    @BindView(R.id.createTime)
    TextView createTime;
    @BindView(R.id.orderNumber)
    TextView orderNumber;
    @BindView(R.id.processDown)
    ImageView processDown;
    @BindView(R.id.finishUp)
    ImageView finishUp;
    @BindView(R.id.failReason)
    TextView failReason;
    @BindView(R.id.failLayout)
    RelativeLayout failLayout;

    private int mId;
    private Handler mHandler;
    private CashDetail mCashDetail;

    private int mWaitDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cash_detail);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        init();
    }


    private void init() {
        mHandler = new Handler(this);
        mId = getIntent().getIntExtra("id", 0);
        getCashRegular();
        getCashDetail(mId);


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

    private void getCashDetail(int id) {

        String url = ServerInfo.emc + ServerInfo.getCashDetail + id;
        Map<String, String> params = new HashMap<String, String>();


        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = mHandler.obtainMessage(MSG_CASH_DETAIL);
                msg.obj = response;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_CASH_DETAIL:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mCashDetail = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), CashDetail.class);
                        if (mCashDetail != null) {
                            money.setText(String.format("%.2f", (float) mCashDetail.getMoney() / 100));
                            if (mCashDetail.getStatus() == 1 || mCashDetail.getStatus() == 2) {
                                status.setText("处理中");
                                processIcon.setImageResource(R.drawable.ic_success);
                                processDes.setText("正在处理");
                                processDes.setTextColor(Color.parseColor("#06B4FD"));
                                status.setTextColor(Color.parseColor("#EB8C10"));


                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date createAt = dateFormat.parse(mCashDetail.getCreated_at());
                                Calendar rightNow = Calendar.getInstance();
                                rightNow.setTime(createAt);

                                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                                String format = sdf.format(rightNow.getTime());
                                rightNow.add(Calendar.DAY_OF_YEAR, mWaitDay);
                                String format1 = sdf.format(rightNow.getTime());
                                finishTime.setText("预计" + format1 + "前");
                                applyTime.setText(format);
                                processTime.setText(format);


                            } else if (mCashDetail.getStatus() == 3) {
                                status.setText("已到账");
                                status.setTextColor(Color.parseColor("#06B4FD"));
                                processIcon.setImageResource(R.drawable.ic_success);
                                processDes.setText("正在处理");
                                processDes.setTextColor(Color.parseColor("#06B4FD"));
                                finishIcon.setImageResource(R.drawable.ic_success);
                                finishDes.setTextColor(Color.parseColor("#06B4FD"));

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date createAt = dateFormat.parse(mCashDetail.getCreated_at());
                                Calendar rightNow = Calendar.getInstance();
                                rightNow.setTime(createAt);
                                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                                String format = sdf.format(rightNow.getTime());

                                Date sendTime = dateFormat.parse(mCashDetail.getSend_time());
                                rightNow.setTime(sendTime);
                                String format1 = sdf.format(rightNow.getTime());
                                applyTime.setText(format);
                                processTime.setText(format);
                                finishTime.setText(format1);

                            } else if (mCashDetail.getStatus() == 4) {
                                status.setText("失败");
                                status.setTextColor(Color.parseColor("#F32E0D"));
                                processIcon.setImageResource(R.drawable.ic_fail);
                                processDes.setText("提现失败");
                                processDes.setTextColor(Color.parseColor("#F32E0D"));
                                finishIcon.setImageResource(R.drawable.ic_process);
                                finishDes.setTextColor(Color.parseColor("#C6C6C8"));
                                finishUp.setBackgroundColor(Color.parseColor("#C6C6C8"));
                                processDown.setBackgroundColor(Color.parseColor("#C6C6C8"));
                                failLayout.setVisibility(View.VISIBLE);
                                failReason.setText(mCashDetail.getFail_msg());


                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date createAt = dateFormat.parse(mCashDetail.getCreated_at());
                                Calendar rightNow = Calendar.getInstance();
                                rightNow.setTime(createAt);
                                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                                String format = sdf.format(rightNow.getTime());

                                Date updateAt = dateFormat.parse(mCashDetail.getUpdated_at());
                                rightNow.setTime(updateAt);
                                String format1 = sdf.format(rightNow.getTime());
                                applyTime.setText(format);
                                processTime.setText(format1);

                            }
                            orderNumber.setText(mCashDetail.getTrade_no());
                            createTime.setText(mCashDetail.getCreated_at());
                            account.setText("支付宝(" + mCashDetail.getAccount() + ")");


                        }

                    } else if (jsonObject != null) {
                        ToastUtils.show(jsonObject.getString("msg"));
                    } else {
                        ToastUtils.show("获取详情失败");
                    }
                } catch (Exception e) {

                }
                break;
            case MSG_CASH_REGULAR:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        mWaitDay = jsonObject.getJSONObject("data").getInteger("transfer_day");

                        if (mCashDetail != null) {
                            if (mCashDetail.getStatus() == 1 || mCashDetail.getStatus() == 2) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {
                                    Date expectTim = dateFormat.parse(mCashDetail.getCreated_at());
                                    Calendar rightNow = Calendar.getInstance();
                                    rightNow.setTime(expectTim);
                                    rightNow.add(Calendar.DAY_OF_YEAR, mWaitDay);
                                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                                    String format = sdf.format(rightNow.getTime());
                                    finishTime.setText("预计" + format + "前");

                                } catch (Exception e) {

                                }
                            }
                        }

                    }
                } catch (Exception e) {

                }
                break;

        }
        return false;
    }
}
