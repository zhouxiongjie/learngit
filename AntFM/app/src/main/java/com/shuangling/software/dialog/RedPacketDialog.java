package com.shuangling.software.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.activity.RedPacketDetailActivity;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.entity.RedPacketInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.AwardRotateAnimation;
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

/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class RedPacketDialog extends BaseCircleDialog {
    Unbinder unbinder;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.note)
    TextView note;
    @BindView(R.id.desc)
    TextView desc;
    @BindView(R.id.grab)
    RelativeLayout grab;
    @BindView(R.id.close)
    FontIconView close;
    @BindView(R.id.head)
    SimpleDraweeView head;
    @BindView(R.id.detail)
    TextView detail;
    @BindView(R.id.money)
    TextView money;
    @BindView(R.id.moneyLayout)
    LinearLayout moneyLayout;
    private RedPacketInfo mRedPacketInfo;
    private String mStreamName;
    private OnGrabClickListener mOnGrabClickListener;
    private boolean isGrabing = false;
    private Handler mHandler;

    public RedPacketDialog setOnOkClickListener(OnGrabClickListener onGrabClickListener) {
        this.mOnGrabClickListener = onGrabClickListener;
        return this;
    }

    public interface OnGrabClickListener {
        //void onGrab();
        void onGrabSuccess();

        void onDetail();
    }

    public static RedPacketDialog getInstance(RedPacketInfo redPacketInfo, String streamName) {
        RedPacketDialog dialogFragment = new RedPacketDialog();
        dialogFragment.setCanceledBack(false);
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(0.8f);
        dialogFragment.mRedPacketInfo = redPacketInfo;
        dialogFragment.mStreamName = streamName;
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_red_packet, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        if (!TextUtils.isEmpty(mRedPacketInfo.getSponsor_log())) {
            int width = CommonUtils.dip2px(90);
            int height = width;
            Uri uri = Uri.parse(mRedPacketInfo.getSponsor_log());
            ImageLoader.showThumb(uri, head, width, height);
        }
        if(TextUtils.isEmpty(mRedPacketInfo.getSponsor_nm())){
            name.setText("灵动直播");
        }else {
            name.setText(mRedPacketInfo.getSponsor_nm());
        }

        desc.setText(mRedPacketInfo.getWish());
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.close, R.id.grab, R.id.detail})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.close:
                dismiss();
                break;
            case R.id.grab:
                if (isGrabing) {
                    return;
                }
                isGrabing = true;
                AwardRotateAnimation animation = new AwardRotateAnimation();
                animation.setRepeatCount(1);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        grab.clearAnimation();
//                        if(mOnGrabClickListener!=null){
//                            mOnGrabClickListener.onGrab();
//                        }
                        grab(mRedPacketInfo);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                grab.startAnimation(animation);
                break;
            case R.id.detail:
                dismiss();
                if(mOnGrabClickListener!=null){
                    mOnGrabClickListener.onDetail();
                }

//                Intent it = new Intent(getContext(), RedPacketDetailActivity.class);
//                it.putExtra("id", "" + mRedPacketInfo.getId());
//                startActivity(it);
                break;
        }
    }

    private void grab(RedPacketInfo redPacketInfo) {
        String url = ServerInfo.live + "/v1/robbery";
        Map<String, String> params = new HashMap<>();
        params.put("redbag_id", "" + redPacketInfo.getId());
        params.put("user_id", "" + User.getInstance().getId());
        params.put("streamName", mStreamName);
        params.put("id_asc", "" + redPacketInfo.getId_asc());
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = JSONObject.parseObject(response);
                                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                                    //100000 领取成功
//                                    note.setVisibility(View.GONE);
//                                    name.setText(mRedPacketInfo.getSponsor_nm() + "的红包");
//                                    desc.setVisibility(View.GONE);
//                                    grab.setVisibility(View.INVISIBLE);
//                                    detail.setVisibility(View.VISIBLE);
//                                    moneyLayout.setVisibility(View.VISIBLE);
//                                    int amount=jsonObject.getJSONObject("data").getInteger("money");
//                                    money.setText(String.format("%.2f",(float)amount/100));
                                    if (mOnGrabClickListener != null) {
                                        mOnGrabClickListener.onGrabSuccess();
                                    }
                                    dismiss();
                                } else if (jsonObject != null && jsonObject.getIntValue("code") == 407002) {
                                    //407002 手慢了，红包派完了
                                    note.setVisibility(View.GONE);
                                    if (!TextUtils.isEmpty(mRedPacketInfo.getSponsor_nm())) {
                                        name.setText(mRedPacketInfo.getSponsor_nm() + "的红包");
                                    } else {
                                        name.setText("");
                                    }
                                    desc.setText("手慢了，红包派完了");
                                    grab.setVisibility(View.INVISIBLE);
                                    detail.setVisibility(View.VISIBLE);
                                    moneyLayout.setVisibility(View.GONE);
                                } else if (jsonObject != null && jsonObject.getIntValue("code") == 407003) {
                                    //407003 红包活动已结束
                                    note.setVisibility(View.GONE);
                                    if (!TextUtils.isEmpty(mRedPacketInfo.getSponsor_nm())) {
                                        name.setText(mRedPacketInfo.getSponsor_nm() + "的红包");
                                    } else {
                                        name.setText("");
                                    }
                                    desc.setText("红包活动已结束");
                                    grab.setVisibility(View.INVISIBLE);
                                    detail.setVisibility(View.VISIBLE);
                                    moneyLayout.setVisibility(View.GONE);
                                } else if (jsonObject != null && jsonObject.getIntValue("code") == 407004) {
                                    //407004 这个红包已经领过了哦
                                    note.setVisibility(View.GONE);
                                    if (!TextUtils.isEmpty(mRedPacketInfo.getSponsor_nm())) {
                                        name.setText(mRedPacketInfo.getSponsor_nm() + "的红包");
                                    } else {
                                        name.setText("");
                                    }
                                    desc.setText("这个红包已经领过了哦");
                                    grab.setVisibility(View.INVISIBLE);
                                    detail.setVisibility(View.VISIBLE);
                                    moneyLayout.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                            }
                        }
                    });
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                Log.e("test", exception.toString());
            }
        });
    }
}
