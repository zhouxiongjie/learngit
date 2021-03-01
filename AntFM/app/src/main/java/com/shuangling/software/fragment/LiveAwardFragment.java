package com.shuangling.software.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.R;
import com.shuangling.software.activity.CollectActivity;
import com.shuangling.software.activity.LivePortraitActivity;
import com.shuangling.software.activity.NewLoginActivity;
import com.shuangling.software.customview.CirclePageIndicator;
import com.shuangling.software.dialog.LiveAwardDialog;
import com.shuangling.software.entity.ChatMessage;
import com.shuangling.software.entity.LiveRoomInfo01;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

public class LiveAwardFragment extends Fragment {


    Unbinder unbinder;

    @BindView(R.id.vp)
    ViewPager viewPager;
    @BindView(R.id.pageIndicator)
    LinearLayout pageIndicator;
    @BindView(R.id.giftName)
    TextView giftName;
    @BindView(R.id.giftPrice)
    TextView giftPrice;
    @BindView(R.id.send)
    TextView send;
    @BindView(R.id.sub)
    TextView sub;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.add)
    TextView add;


    private LiveRoomInfo01 liveRoomInfo;
    private PagerAdapter mPagerAdapter;
    private List<View> mPagers;     //展示的礼物页
    private LiveRoomInfo01.PropsBean mSelectGift;
    private LiveAwardDialog mLiveAwardDialog;
    private List<GiftGridAdapter> mGiftGridAdapter = new ArrayList<>();

    public LiveAwardFragment(LiveAwardDialog dialog){
        mLiveAwardDialog=dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle args = getArguments();
        liveRoomInfo = (LiveRoomInfo01) args.getSerializable("liveRoomInfo");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.fragment_live_award, null);
        unbinder = ButterKnife.bind(this, view);
        send.setActivated(true);
        mPagers = getPagerList();
        mPagerAdapter = new PagerAdapter() {


            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {

                container.removeView((View) object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = mPagers.get(position);
                if (null != view) {
                    container.addView(view);
                }
                return view;
            }

            @Override
            public int getCount() {
                return null == mPagers ? 0 : mPagers.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        };
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(0);
        setupWithPagerPoint(viewPager,pageIndicator);

        return view;
    }

    public void setupWithPagerPoint(ViewPager viewPager, final LinearLayout pointLayout) {

        int pageCount = mPagerAdapter.getCount();
        for (int i = 0; i < pageCount; i++) {
            ImageView point = new ImageView(getContext());
            point.setImageResource(R.drawable.shape_vp_dot_unselected01);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
            params.rightMargin = CommonUtils.dip2px(10);
            if (i == 0) {
                point.setImageResource(R.drawable.shape_vp_dot_selected01);
            }
            pointLayout.addView(point, params);
        }


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //切换指示�?
                if (null != pointLayout && pointLayout.getChildCount() > 0) {
                    for (int i = 0; i < pointLayout.getChildCount(); i++) {
                        ((ImageView) pointLayout.getChildAt(i)).setImageResource(R.drawable.shape_vp_dot_unselected01);
                    }
                    ((ImageView) pointLayout.getChildAt(position)).setImageResource(R.drawable.shape_vp_dot_selected01);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public List<View> getPagerList() {
        List<View> pagers = null;

        if (null != liveRoomInfo.getProps() && liveRoomInfo.getProps().size() > 0) {
            pagers = new ArrayList<View>();
            int pageCount = liveRoomInfo.getProps().size() / 8;
            mGiftGridAdapter.clear();
            for (int i = 0; i < pageCount; i++) {
                GridView gridView = new GridView(getContext());
                gridView.setNumColumns(4);
                gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
                gridView.setCacheColorHint(Color.TRANSPARENT);
                int padding = CommonUtils.dip2px(10);
                gridView.setPadding(padding / 2, padding, padding / 2, padding);
                gridView.setVerticalSpacing(padding);
                gridView.setHorizontalSpacing(padding);
                gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
                gridView.setGravity(Gravity.CENTER);
                List<LiveRoomInfo01.PropsBean> gifts = new ArrayList<>();
                for (int j = i * 8; j < liveRoomInfo.getProps().size() && j < (i + 1) * 8; j++) {
                    gifts.add(liveRoomInfo.getProps().get(j));
                }
                GiftGridAdapter adapter = new GiftGridAdapter(getContext(), gifts);
                mGiftGridAdapter.add(adapter);
                gridView.setAdapter(adapter);
                //点击表情监听
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //获取选中的表情字�?
                        mSelectGift = (LiveRoomInfo01.PropsBean) parent.getAdapter().getItem(position);
//                        if (null != mEmojiClickListener) {
//                            mEmojiClickListener.onClick(emoji);
//                        }

                        number.setText("1");
                        sub.setEnabled(false);
                        if(mSelectGift.getLimit()<=1){
                            add.setEnabled(false);
                        }else{
                            add.setEnabled(true);
                        }

                        for (int i = 0; i < mGiftGridAdapter.size(); i++) {
                            mGiftGridAdapter.get(i).notifyDataSetChanged();
                        }
                        giftName.setText(mSelectGift.getName());
                        giftPrice.setText(mSelectGift.getPrice() + "分");
                    }
                });
                pagers.add(gridView);
            }
        }
        return pagers;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(String str) {

    }

    @OnClick({R.id.send, R.id.sub, R.id.add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.send:
                if (User.getInstance() == null) {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    startActivity(it);
                }else{
                    if(mSelectGift!=null){
                        sendGift(mSelectGift);
                    }
                }



                break;
            case R.id.sub:
                if(mSelectGift!=null){
                    int num=Integer.parseInt(number.getText().toString());
                    number.setText(""+(num-1));
                    if(num-1<=1){
                        sub.setEnabled(false);
                    }else {
                        sub.setEnabled(true);
                    }
                    add.setEnabled(true);

                }
                break;
            case R.id.add:
                if(mSelectGift!=null){
                    int num=Integer.parseInt(number.getText().toString());
                    number.setText(""+(num+1));
                    if(mSelectGift.getLimit()<=num+1){
                        add.setEnabled(false);
                    }else{
                        add.setEnabled(true);
                    }
                    sub.setEnabled(true);

                }
                break;
        }
    }


    private void sendGift(LiveRoomInfo01.PropsBean gift) {
        String url = ServerInfo.live + "/v3/reward";
        Map<String, String> params = new HashMap<>();
        params.put("room_id", "" + (liveRoomInfo.getRoom_info() != null ? liveRoomInfo.getRoom_info().getId() : ""));
        params.put("prop_id", ""+gift.getId());
        params.put("user_id", ""+User.getInstance().getId());
        params.put("amount", number.getText().toString().trim());
        params.put("stream_name", (liveRoomInfo.getRoom_info() != null ? liveRoomInfo.getRoom_info().getStream_name() : ""));
        params.put("nick_name", ""+User.getInstance().getNickname());
        params.put("prop_name", gift.getName());
        params.put("prop_price", ""+gift.getPrice());
        params.put("prop_icon_url", gift.getIcon_url());
        params.put("user_logo", User.getInstance().getAvatar());
        OkHttpUtils.post(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLiveAwardDialog.dismiss();
                            ToastUtils.show(jsonObject.getString("msg"));
                        }
                    });

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                ToastUtils.show("请求失败");
            }
        });
    }


    public class GiftGridAdapter extends BaseAdapter {
        private Context mContext;
        private List<LiveRoomInfo01.PropsBean> mGifts;

        public GiftGridAdapter(Context context, List<LiveRoomInfo01.PropsBean> gifts) {
            this.mContext = context;
            this.mGifts = gifts;
        }

        @Override
        public int getCount() {
            return null == mGifts ? 0 : mGifts.size();
        }

        @Override
        public LiveRoomInfo01.PropsBean getItem(int position) {
            return mGifts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (null == convertView) {
                convertView = View.inflate(mContext, R.layout.gift_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            LiveRoomInfo01.PropsBean gift = getItem(position);

            if (mSelectGift != null && gift.getId() == mSelectGift.getId()) {
                holder.bg.setSelected(true);
            } else {
                holder.bg.setSelected(false);
            }

            if (!TextUtils.isEmpty(gift.getIcon_url())) {
                Uri uri = Uri.parse(gift.getIcon_url());
                ImageLoader.showThumb(uri, holder.logo, CommonUtils.dip2px(50), CommonUtils.dip2px(50));
            }


            holder.giftName.setText(gift.getName());
            holder.giftPrice.setText("" + gift.getPrice() + "分");

            return convertView;
        }

        public class ViewHolder {
            @BindView(R.id.logo)
            SimpleDraweeView logo;
            @BindView(R.id.giftName)
            TextView giftName;
            @BindView(R.id.giftPrice)
            TextView giftPrice;
            @BindView(R.id.bg)
            LinearLayout bg;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }

    }
}
