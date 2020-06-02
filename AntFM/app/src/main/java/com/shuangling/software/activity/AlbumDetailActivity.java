package com.shuangling.software.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.Album;
import com.shuangling.software.entity.User;
import com.shuangling.software.fragment.AlbumAudiosFragment;
import com.shuangling.software.fragment.AlbumIntroduceFragment;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import okhttp3.Call;

@EnableDragToClose()
public class AlbumDetailActivity extends BaseAudioActivity implements Handler.Callback {

    private static final String[] category = new String[]{"简介", "节目"};

    public static final int MSG_GET_ALBUM_DETAIL = 0x1;

    public static final int MSG_SUBSCRIBE_CALLBACK = 0x2;

    public static final int REQUEST_LOGIN = 0x3;

    private static final int SHARE_SUCCESS = 0x4;

    private static final int SHARE_FAILED = 0x5;

    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.logo)
    SimpleDraweeView logo;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.head)
    SimpleDraweeView head;
    @BindView(R.id.subscribe)
    TextView subscribe;

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.albumTitle)
    TextView albumTitle;
    @BindView(R.id.tabPageIndicator)
    TabLayout tabPageIndicator;


    private Handler mHandler;

    private int mAlbumId;
    private Album mAlbum;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private FragmentAdapter mFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        setContentView(R.layout.activity_album_detail);
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);
        CommonUtils.transparentStatusBar(this);
        mHandler = new Handler(this);

        getAlbumDetail();

        activityTitle.setMoreAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAlbum!=null){
                    String url;
                    if(User.getInstance()!=null){
                        url=ServerInfo.h5IP+"/albums/"+mAlbumId+"?from_user_id="+User.getInstance().getId()+"&from_url="+ServerInfo.h5IP+"/albums/"+mAlbumId;
                    }else{
                        url=ServerInfo.h5IP+"/albums/"+mAlbumId+"?from_url="+ServerInfo.h5IP+"/albums/"+mAlbumId;
                    }

                    showShare(mAlbum.getTitle(),mAlbum.getDes(),mAlbum.getCover(),url);
                    //shareTest();
                }

            }
        });

    }

    private void getAlbumDetail() {
        mAlbumId = getIntent().getIntExtra("albumId", 0);

        String url = ServerInfo.serviceIP + ServerInfo.getAlbumDetail + mAlbumId;
        OkHttpUtils.get(url, null, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_ALBUM_DETAIL;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }


    public void subscribe(final boolean subscribe) {


        String url = ServerInfo.serviceIP + ServerInfo.subscribes;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + mAlbum.getId());
        if (subscribe) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }

        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_SUBSCRIBE_CALLBACK;
                msg.arg1 = subscribe ? 1 : 0;
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
            case MSG_GET_ALBUM_DETAIL:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        mAlbum = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), Album.class);

                        if (!TextUtils.isEmpty(mAlbum.getCover())) {
                            Uri uri = Uri.parse(mAlbum.getCover());
                            int width = (int) getResources().getDimension(R.dimen.article_right_image_width);
                            int height = width;
                            ImageLoader.showThumb(uri, logo, width, height);
                        }
                        if (mAlbum.getAuthor_info() != null && mAlbum.getAuthor_info().getMerchant() != null) {
                            if (!TextUtils.isEmpty(mAlbum.getAuthor_info().getMerchant().getName())) {
                                name.setText(mAlbum.getAuthor_info().getMerchant().getName());
                            }
                            if (!TextUtils.isEmpty(mAlbum.getAuthor_info().getMerchant().getLogo())) {
                                Uri uri = Uri.parse(mAlbum.getAuthor_info().getMerchant().getLogo());
                                int width = CommonUtils.dip2px(30);
                                int height = width;
                                ImageLoader.showThumb(uri, head, width, height);
                            }
                        }

                        head.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mAlbum.getAuthor_info() != null && mAlbum.getAuthor_info().getMerchant() != null) {
//                                    Intent it = new Intent(AlbumDetailActivity.this, OrganizationDetailActivity.class);
//                                    it.putExtra("organizationId", mAlbum.getAuthor_info().getMerchant().getId());
//                                    startActivity(it);

                                    Intent it = new Intent(AlbumDetailActivity.this, WebViewActivity.class);
                                    it.putExtra("url", ServerInfo.h5HttpsIP+"/orgs/"+mAlbum.getAuthor_info().getMerchant().getId());
                                    startActivity(it);
                                }

                            }
                        });

                        if (mAlbum.getIs_sub() == 1) {
                            subscribe.setText(getResources().getString(R.string.has_subscribe));
                            subscribe.setActivated(false);
                        } else {
                            subscribe.setText(getResources().getString(R.string.subscribe));
                            subscribe.setActivated(true);
                        }

                        subscribe.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (User.getInstance() == null) {
                                    Intent it=new Intent(AlbumDetailActivity.this, NewLoginActivity.class);
                                    it.putExtra("jump_url",ServerInfo.h5IP + "/albums/"+mAlbumId);
                                    startActivityForResult(it, REQUEST_LOGIN);

                                } else {
                                    subscribe(mAlbum.getIs_sub() == 0);
                                }
                            }
                        });
                        albumTitle.setText(mAlbum.getTitle());
                        initFragment();

                    }


                } catch (Exception e) {

                }


                break;
            case MSG_SUBSCRIBE_CALLBACK:
                try {
                    String result = (String) msg.obj;
                    boolean sub = msg.arg1 == 1 ? true : false;

                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show(jsonObject.getString("msg"));

                        if (sub) {
                            subscribe.setText("已订阅");
                            subscribe.setActivated(false);
                            mAlbum.setIs_sub(1);
                        } else {
                            subscribe.setText("订阅");
                            subscribe.setActivated(true);
                            mAlbum.setIs_sub(0);

                        }


                    }


                } catch (Exception e) {

                }
                break;
        }
        return false;
    }


    private void initFragment() {
        if (mFragmentAdapter != null) {
            return;
        }
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mFragmentAdapter);
        tabPageIndicator.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);

    }


    public class FragmentAdapter extends FragmentStatePagerAdapter {

        private FragmentManager fm;

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }


        @Override
        public int getCount() {
            return category.length;
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                AlbumIntroduceFragment introduceFragment = new AlbumIntroduceFragment();
                Bundle introduceData = new Bundle();
                introduceData.putString("introduction", mAlbum.getDes());
                introduceData.putInt("albumId", mAlbum.getId());
                introduceFragment.setArguments(introduceData);
                return introduceFragment;
            } else {
                AlbumAudiosFragment audiosFragment = new AlbumAudiosFragment();
                Bundle audiosData = new Bundle();
                audiosData.putInt("albumId", mAlbum.getId());
                audiosFragment.setArguments(audiosData);
                return audiosFragment;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return category[position];
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            //得到缓存的fragment
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            return fragment;
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK) {
            getAlbumDetail();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void showShare(final String title, final String desc, final String logo, final String url) {
        final String cover;
        if(logo.startsWith("http://")){
            cover=logo.replace("http://","https://");
        }else{
            cover=logo;
        }
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        final Platform qq = ShareSDK.getPlatform(QQ.NAME);
        if (!qq.isClientValid()) {
            oks.addHiddenPlatform(QQ.NAME);
        }
        final Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
        if (!sina.isClientValid()) {
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            //自定义分享的回调想要函数
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                //点击新浪微博
                String chanel="1";
                if (SinaWeibo.NAME.equals(platform.getName())) {
                    //限制微博分享的文字不能超过20
                    chanel="2";
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                    paramsToShare.setText(title + url);
                } else if (QQ.NAME.equals(platform.getName())) {
                    chanel="3";
                    paramsToShare.setTitle(title);
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                    paramsToShare.setTitleUrl(url);
                    paramsToShare.setText(desc);

                } else if (Wechat.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(url);
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                    paramsToShare.setText(desc);

                    Log.d("ShareSDK", paramsToShare.toMap().toString());
                } else if (WechatMoments.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(url);
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                } else if (WechatFavorite.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(url);
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                }
                shareStatistics(chanel,""+mAlbum.getId(),url);
            }
        });
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Message msg = Message.obtain();
                msg.what = SHARE_FAILED;
                msg.obj = arg2.getMessage();
                mHandler.sendMessage(msg);
            }
            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                Message msg = Message.obtain();
                msg.what = SHARE_SUCCESS;
                mHandler.sendMessage(msg);

            }
            @Override
            public void onCancel(Platform arg0, int arg1) {

            }
        });
        // 启动分享GUI
        oks.show(this);
    }


    public void shareTest(){
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
                if ("SinaWeibo".equals(platform.getName())) {
                    paramsToShare.setText("玩美夏日，护肤也要肆意玩酷！" + "www.mob.com");
                    paramsToShare.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
                }
                if ("Wechat".equals(platform.getName())) {
                    paramsToShare.setTitle("小岳岳");
                    paramsToShare.setText("岳云鹏，德云社相声演员。1985年4月15日出生于河南省濮阳市，2004年师从郭德纲学习相声。");
                    //paramsToShare.setImageUrl("http://sl-cms.static.slradio.cn/merchants/1/imges/FaD108PBeCCB7mPiJy8Xyj69KfFbpXnx1552461501810.jpg");
                    paramsToShare.setUrl("http://www-cms-c.review.slradio.cn/albums/61");
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);

                    Log.d("ShareSDK", paramsToShare.toMap().toString());

                }
                if ("WechatMoments".equals(platform.getName())) {
                    paramsToShare.setTitle("标题");
                    paramsToShare.setText("我是共用的参数，这几个平台都有text参数要求，提取出来啦");
                    paramsToShare.setImageUrl("http://img.ugoshop.com/ugoimg/share/img/poster/190703/6d98eff22454c681834ea9ec44a71496.png");
                    paramsToShare.setShareType(Platform.SHARE_IMAGE);
                    Log.d("ShareSDK", paramsToShare.toMap().toString());
                }
                if ("QQ".equals(platform.getName())) {
                    paramsToShare.setTitle("标题");
                    paramsToShare.setTitleUrl("http://sharesdk.cn");
                    paramsToShare.setText("我是共用的参数，这几个平台都有text参数要求，提取出来啦");
                    paramsToShare.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
//                  paramsToShare.setImagePath("/storage/emulated/0/abcd.gif");
                    Log.d("ShareSDK", paramsToShare.toMap().toString());
                }
                if ("WhatsApp".equals(platform.getName())) {
                    paramsToShare.setText("我是共用的参数，这几个平台都有text参数要求，提取出来啦");
                    paramsToShare.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
                }
                if("Twitter".equals(platform.getName())){
                    paramsToShare.setText("我是共用的参数，这几个平台都有text参数要求，提取出来啦");
                    paramsToShare.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
                    /*paramsToShare.setUrl("http://sharesdk.cn");*/
                }
            }
        });


        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.d("ShareLogin", "onComplete ---->  分享成功");
                platform.getName();
                Toast.makeText(AlbumDetailActivity.this, "HHHHHHHHHH", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.d("ShareLogin", "onError ---->  失败" + throwable.getStackTrace());
                Log.d("ShareLogin", "onError ---->  失败" + throwable.getMessage());
                throwable.printStackTrace();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.d("ShareLogin", "onCancel ---->  分享取消");
            }
        });

// 启动分享GUI
        oks.show(this);

    }




    public void shareStatistics(String channel,String postId,String shardUrl) {

        String url = ServerInfo.serviceIP + ServerInfo.shareStatistics;
        Map<String, String> params = new HashMap<>();
        if(User.getInstance()!=null){
            params.put("user_id", ""+User.getInstance().getId());
        }
        params.put("channel", channel);
        params.put("post_id", postId);
        params.put("source_type", "3");
        params.put("type", "1");
        params.put("shard_url", shardUrl);
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {
                Log.i("test",response);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                Log.i("test",exception.toString());

            }
        });

    }
}
