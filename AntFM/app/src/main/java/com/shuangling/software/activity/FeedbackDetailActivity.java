package com.shuangling.software.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.previewlibrary.GPreviewBuilder;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.ImgTextGridViewAdapter;
import com.shuangling.software.customview.MyGridView;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.FeedBackDetail;
import com.shuangling.software.entity.ImageInfo;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.TimeUtil;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

@EnableDragToClose()
public class FeedbackDetailActivity extends QMUIActivity/*AppCompatActivity*/ implements Handler.Callback {
    public static final String TAG = "CityListActivity";
    public static final int MSG_GET_DETAIL = 0x1;
    @BindView(R.id.activtyTitle)
    /*TopTitleBar*/ QMUITopBarLayout activtyTitle;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.material)
    MyGridView material;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.replyTime)
    TextView replyTime;
    @BindView(R.id.noReply)
    TextView noReply;
    @BindView(R.id.replyContent)
    TextView replyContent;
    @BindView(R.id.srollView)
    ScrollView srollView;
    @BindView(R.id.replyLayout)
    LinearLayout replyLayout;
    private Handler mHandler;
    //private ViewSkeletonScreen mViewSkeletonScreen;
    private int mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_detail);
//        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this); //
        activtyTitle.addLeftImageButton(R.drawable.ic_left, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(view -> { //
            finish();
        });
        activtyTitle.setTitle("反馈详情");
        init();
    }

    public void getFeedbackDetail() {
        String url = ServerInfo.serviceIP + "/v1/my_feed_back/" + mId;
        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_GET_DETAIL;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ToastUtils.show("获取详情失败");
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
    }

    private void init() {
        mId = getIntent().getIntExtra("id", 0);
        mHandler = new Handler(this);
//        mViewSkeletonScreen = Skeleton.bind(srollView)
//                .load(R.layout.skeleton_video_detail)
//                .shimmer(false)
//                .angle(20)
//                .duration(1000)
//                .color(R.color.shimmer_color)
//                .show();
        getFeedbackDetail();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_DETAIL:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        FeedBackDetail feedBackDetail = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), FeedBackDetail.class);
                        content.setText(feedBackDetail.getOpinion());
                        time.setText(TimeUtil.formatDateTime(feedBackDetail.getCreated_at()));
                        final ImgTextGridViewAdapter adapter = new ImgTextGridViewAdapter(this, feedBackDetail.getEnclosure());
                        material.setAdapter(adapter);
                        material.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                ArrayList<ImageInfo> images = new ArrayList<>();
                                for (int i = 0; i < feedBackDetail.getEnclosure().size(); i++) {
                                    ImageInfo image = new ImageInfo(feedBackDetail.getEnclosure().get(i));
                                    images.add(image);
                                    Rect bounds = new Rect();
                                    view.getGlobalVisibleRect(bounds);
                                    image.setBounds(bounds);
                                }
                                GPreviewBuilder.from(FeedbackDetailActivity.this)
                                        .setData(images)
                                        .setCurrentIndex(position)
                                        .setDrag(true, 0.6f)
                                        .setSingleFling(true)
                                        .setType(GPreviewBuilder.IndicatorType.Number)
                                        .start();
                            }
                        });
                        if (feedBackDetail.getIs_reply() == 1) {
                            noReply.setVisibility(View.GONE);
                            replyLayout.setVisibility(View.VISIBLE);
                            replyTime.setText(TimeUtil.formatDateTime(feedBackDetail.getReply_time()));
                            replyContent.setText(feedBackDetail.getRemarks());
                        } else {
                            noReply.setVisibility(View.VISIBLE);
                            replyLayout.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                }
                break;
        }
        return false;
    }
}
