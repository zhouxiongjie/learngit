package com.shuangling.software.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.activity.MyWalletsActivity;
import com.shuangling.software.activity.NewLoginActivity;
import com.shuangling.software.entity.RedPacketDetailInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 666 on 2017/1/3.
 * 首页分类
 */
public class RedPacketAdapter01 extends RecyclerView.Adapter {
    //"type": 4,//1 音频 2 专辑 3 文章 4 视频 5专题
    public static final int TYPE_HEAD = 0;              //头
    public static final int TYPE_USER_INFO = 1;         //条目
    private Context mContext;
    private RedPacketDetailInfo mRedPacketDetailInfo;
    private LayoutInflater mInflater;

    public RedPacketAdapter01(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public RedPacketAdapter01(Context context, RedPacketDetailInfo redPacketDetailInfo) {
        this.mContext = context;
        this.mRedPacketDetailInfo = redPacketDetailInfo;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            return new HeadViewHolder(mInflater.inflate(R.layout.red_packet_detail_header01, parent, false));
        } else {
            return new UserInfoViewHolder(mInflater.inflate(R.layout.red_packet_item01, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == TYPE_HEAD) {
            HeadViewHolder vh = (HeadViewHolder) holder;
            //ImmersionBar.with((Activity) mContext).statusBarView(vh.statusBar).init();
//            ViewGroup.LayoutParams lp = vh.statusBar.getLayoutParams();
//            lp.height = CommonUtils.getStatusBarHeight(mContext);
//            vh.statusBar.setLayoutParams(lp);
//            vh.back.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ((Activity) mContext).finish();
//                }
//            });
            if (!TextUtils.isEmpty(mRedPacketDetailInfo.getBusiness_logo())) {
                int width = CommonUtils.dip2px(20);
                int height = width;
                Uri uri = Uri.parse(mRedPacketDetailInfo.getBusiness_logo());
                ImageLoader.showThumb(uri, vh.head, width, height);
            }
            if (!TextUtils.isEmpty(mRedPacketDetailInfo.getBusiness())) {
                vh.name.setVisibility(View.VISIBLE);
                vh.name.setText(mRedPacketDetailInfo.getBusiness() + "的红包");
            } else {
                vh.name.setVisibility(View.GONE);
            }
            vh.desc.setText(mRedPacketDetailInfo.getWish());
            vh.moneyLayout.setVisibility(View.GONE);
            vh.tip.setVisibility(View.GONE);
            vh.tip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (User.getInstance() != null) {
                        mContext.startActivity(new Intent(mContext, MyWalletsActivity.class));
                    } else {
                        Intent it = new Intent(mContext, NewLoginActivity.class);
                        mContext.startActivity(it);
                    }
                }
            });
            for (int i = 0; mRedPacketDetailInfo.getHistory() != null && i < mRedPacketDetailInfo.getHistory().size(); i++) {
                if (User.getInstance().getId() == mRedPacketDetailInfo.getHistory().get(i).getUser_id()) {
                    vh.moneyLayout.setVisibility(View.VISIBLE);
                    vh.tip.setVisibility(View.VISIBLE);
                    vh.money.setText(String.format("%.2f", (float) mRedPacketDetailInfo.getHistory().get(i).getMoney() / 100));
                    break;
                }
            }
            if (mRedPacketDetailInfo.getSend_num() == mRedPacketDetailInfo.getTotal_num()) {
                vh.summary.setText(mRedPacketDetailInfo.getTotal_num() + "个红包，" + TimeUtil.getTimeSpan(mRedPacketDetailInfo.getSend_time(), mRedPacketDetailInfo.getSend_end_time()) + "被抢光");
            } else {
                vh.summary.setText("已领取" + mRedPacketDetailInfo.getSend_num() + "/" + mRedPacketDetailInfo.getTotal_num() + "个");
            }
        } else {
            UserInfoViewHolder vh = (UserInfoViewHolder) holder;
            RedPacketDetailInfo.HistoryBean historyBean = mRedPacketDetailInfo.getHistory().get(position - 1);
            if (!TextUtils.isEmpty(historyBean.getUser().getAvatar())) {
                int width = CommonUtils.dip2px(30);
                int height = width;
                Uri uri = Uri.parse(historyBean.getUser().getAvatar());
                ImageLoader.showThumb(uri, vh.head, width, height);
            } else {
                ImageLoader.showThumb(vh.head, R.drawable.ic_user3);
            }
            vh.money.setText(String.format("%.2f", (float) historyBean.getMoney() / 100) + "元");
            if (historyBean.getBest_luck() == 1) {
                vh.bestLuck.setVisibility(View.VISIBLE);
            } else {
                vh.bestLuck.setVisibility(View.INVISIBLE);
            }
            vh.name.setText(historyBean.getUser().getNickname());
            vh.time.setText(timeFormatConvert(historyBean.getCreated_at()));
        }
    }

    public class HeadViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.back)
//        ImageView back;
        @BindView(R.id.head)
        SimpleDraweeView head;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.desc)
        TextView desc;
        @BindView(R.id.money)
        TextView money;
        @BindView(R.id.summary)
        TextView summary;
        @BindView(R.id.moneyLayout)
        LinearLayout moneyLayout;
        @BindView(R.id.tip)
        TextView tip;
//        @BindView(R.id.statusBar)
//        View statusBar;

        public HeadViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class UserInfoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.head)
        SimpleDraweeView head;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.money)
        TextView money;
        @BindView(R.id.bestLuck)
        TextView bestLuck;

        public UserInfoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getItemCount() {
        if (mRedPacketDetailInfo == null) {
            return 0;
        } else if (mRedPacketDetailInfo.getHistory() == null) {
            return 1;
        } else {
            return 1 + mRedPacketDetailInfo.getHistory().size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        }
        return TYPE_USER_INFO;
    }

    private String timeFormatConvert(String time) {
        String timeString = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        try {
            Date parse = df.parse(time);
            timeString = dateFormat.format(parse);
        } catch (ParseException e) {
            e.printStackTrace();
            timeString = time;
        }
        return timeString;
    }
}
