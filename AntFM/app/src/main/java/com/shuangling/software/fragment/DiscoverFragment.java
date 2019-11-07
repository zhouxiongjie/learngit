package com.shuangling.software.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.shuangling.software.R;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.activity.LoginActivity;
import com.shuangling.software.activity.MainActivity;
import com.shuangling.software.activity.SingleAudioDetailActivity;
import com.shuangling.software.activity.SpecialDetailActivity;
import com.shuangling.software.activity.VideoDetailActivity;
import com.shuangling.software.activity.WebViewActivity;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.AudioInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.event.PlayerEvent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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


public class DiscoverFragment extends Fragment implements Handler.Callback {

    private static final int LOGIN_RESULT = 0x1;
    public static final int MSG_GET_DETAIL = 0x2;
    private static final int SHARE_SUCCESS = 0x3;
    private static final int SHARE_FAILED = 0x4;

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.activtyTitle)
    TopTitleBar activtyTitle;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    Unbinder unbinder;
    private Handler mHandler;

    private int level=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mHandler = new Handler(this);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_descover, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;

    }






    private void init() {
        EventBus.getDefault().register(this);
        activtyTitle.setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(webView.canGoBack()){
                    webView.goBack();
                }
            }
        });
        String url = ServerInfo.h5IP + "/find";

        if (User.getInstance() == null) {
            if (MainActivity.sCurrentCity != null) {
                url = url + "?app=android&city=" + MainActivity.sCurrentCity.getCode();
            } else {
                url = url + "?app=android";
            }

        } else {
            if (MainActivity.sCurrentCity != null) {
                url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&city=" + MainActivity.sCurrentCity.getCode();
            } else {
                url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
            }

        }
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);       //js
        s.setDomStorageEnabled(true);       //localStorage
//        webView.setWebViewClient(new WebViewClient());


        webView.setWebViewClient(new WebViewClient(){
            // url拦截
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
                view.loadUrl(url);
                // 相应完成返回true

                return true;
            }

            // 页面开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //progressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                //progressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);

                if(url.startsWith(ServerInfo.h5IP + "/find")){
                    activtyTitle.setCanBack(false);
                }else{
                    activtyTitle.setCanBack(true);
                }
//                if(!webView.canGoBack()){
//
//                }else{
//                    activtyTitle.setCanBack(true);
//
//                }

            }

            // WebView加载的所有资源url
            @Override
            public void onLoadResource(WebView view, String url) {
//                if(url.startsWith(ServerInfo.serviceIP + "/v1/services")){
//                    activtyTitle.setCanBack(true);
//                }else if(url.startsWith(ServerInfo.h5IP + "/find")){
//                    activtyTitle.setCanBack(false);
//                }
//                if(!webView.canGoBack()){
//                    activtyTitle.setCanBack(true);
//                }else{
//                    activtyTitle.setCanBack(false);
//                }
                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//				view.loadData(errorHtml, "text/html; charset=UTF-8", null);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });


        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            // 处理javascript中的alert
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                return super.onJsAlert(view, url, message, result);
            };

            @Override
            // 处理javascript中的confirm
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            };

            @Override
            // 处理javascript中的prompt
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            };

            // 设置网页加载的进度条
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
//                long id=Thread.currentThread().getId();
//                Log.i("onProgressChanged",""+newProgress);
//                if (newProgress == 100) {
//                    progressBar.setVisibility(GONE);
//                } else {
//                    if (progressBar.getVisibility() == GONE)
//                        progressBar.setVisibility(VISIBLE);
//                    progressBar.setProgress(newProgress);
//                }
                super.onProgressChanged(view, newProgress);
            }

            // 设置程序的Title
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
        webView.addJavascriptInterface(new JsToAndroid(), "clientJS");
        webView.loadUrl(url);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_DETAIL:
                try {


                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case SHARE_SUCCESS:
                break;
            case SHARE_FAILED:
                break;
        }
        return false;
    }

    private final class JsToAndroid {

        @JavascriptInterface
        public void backEvent(String str) {
            long id = Thread.currentThread().getId();
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                }
            });

        }

        @JavascriptInterface
        public void loginEvent(String str) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivityForResult(it, LOGIN_RESULT);
                }
            });


        }


        @JavascriptInterface
        public void shareEvent(final String id, final String title, final String des, final String logo) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    showShare(id, title, des, logo);
                }
            });


        }


        @JavascriptInterface
        public void pageEvent(final String id, final String type, final String title) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //1音频、2专辑、3文章、4视频、5专题、7图集
                    if (type.equals("1")) {
                        Intent it = new Intent(getContext(), AudioDetailActivity.class);
                        it.putExtra("audioId", Integer.parseInt(id));
                        startActivity(it);

                    } else if (type.equals("2")) {
                        Intent it = new Intent(getContext(), AlbumDetailActivity.class);
                        it.putExtra("albumId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("3")) {
                        Intent it = new Intent(getContext(), WebViewActivity.class);
                        it.putExtra("articleId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("4")) {
                        Intent it = new Intent(getContext(), VideoDetailActivity.class);
                        it.putExtra("videoId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("5")) {
                        Intent it = new Intent(getContext(), SpecialDetailActivity.class);
                        it.putExtra("specialId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("7")) {
                        Intent it = new Intent(getContext(), GalleriaActivity.class);
                        it.putExtra("galleriaId", Integer.parseInt(id));
                        startActivity(it);
                    }
                }
            });

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("OnLoginSuccess")) {
//            String url = webView.getUrl();
//            if(url.indexOf("?")>0){
//                url = url.substring(0, url.indexOf("?"));
//                if (User.getInstance() == null) {
//                    url = url + "?app=android";
//                } else {
//                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
//                }
//
//            }
//            webView.loadUrl(url);


            String url = ServerInfo.h5IP + "/find";

            if (User.getInstance() == null) {
                if (MainActivity.sCurrentCity != null) {
                    url = url + "?app=android&city=" + MainActivity.sCurrentCity.getCode();
                } else {
                    url = url + "?app=android";
                }

            } else {
                if (MainActivity.sCurrentCity != null) {
                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&city=" + MainActivity.sCurrentCity.getCode();
                } else {
                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
                }
            }
            webView.loadUrl(url);

        }else if(event.getEventName().equals("OnQuitLogin")){
//            String url = webView.getUrl();
//            if(url.indexOf("?")>0){
//                url = url.substring(0, url.indexOf("?"));
//                if (User.getInstance() == null) {
//                    url = url + "?app=android";
//                } else {
//                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
//                }
//
//            }


            String url = ServerInfo.h5IP + "/find";

            if (User.getInstance() == null) {
                if (MainActivity.sCurrentCity != null) {
                    url = url + "?app=android&city=" + MainActivity.sCurrentCity.getCode();
                } else {
                    url = url + "?app=android";
                }

            } else {
                if (MainActivity.sCurrentCity != null) {
                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&city=" + MainActivity.sCurrentCity.getCode();
                } else {
                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
                }
            }
            webView.loadUrl(url);
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            String url = ServerInfo.h5IP + "/find";
            if (User.getInstance() == null) {
                if (MainActivity.sCurrentCity != null) {
                    url = url + "?app=android&city=" + MainActivity.sCurrentCity.getCode();
                } else {
                    url = url + "?app=android";
                }
            } else {
                if (MainActivity.sCurrentCity != null) {
                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&city=" + MainActivity.sCurrentCity.getCode();
                } else {
                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
                }

            }
            webView.loadUrl(url);
            webView.clearHistory();
            activtyTitle.setCanBack(false);
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }


    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        webView.destroy();
        unbinder.unbind();
        super.onDestroyView();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_RESULT && resultCode == Activity.RESULT_OK) {
            String url = webView.getUrl();
            if(url.indexOf("?")>0){
                url = url.substring(0, url.indexOf("?"));
                if (User.getInstance() == null) {
                    url = url + "?app=android";
                } else {
                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android";
                }

            }
            webView.loadUrl(url);

        }
    }


    private void showShare(final String id, final String title, final String des, final String logo) {


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
                String chanel="1";
                //点击新浪微博
                if (SinaWeibo.NAME.equals(platform.getName())) {
                    //限制微博分享的文字不能超过20
                    chanel="2";
                    paramsToShare.setText(title + ServerInfo.activity + "qaa/game-result/" + id);
                } else if (QQ.NAME.equals(platform.getName())) {
                    chanel="3";
                    paramsToShare.setTitle(title);
                    if (!TextUtils.isEmpty(logo)) {
                        paramsToShare.setImageUrl(logo);
                    }
                    paramsToShare.setTitleUrl(ServerInfo.activity + "qaa/game-result/" + id);
                    paramsToShare.setText(des);

                } else if (Wechat.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(ServerInfo.activity + "qaa/game-result/" + id);
                    if (!TextUtils.isEmpty(logo)) {
                        paramsToShare.setImageUrl(logo);
                    }
                    paramsToShare.setText(des);
                } else if (WechatMoments.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(ServerInfo.activity + "qaa/game-result/" + id);
                    if (!TextUtils.isEmpty(logo)) {
                        paramsToShare.setImageUrl(logo);
                    }
                } else if (WechatFavorite.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(ServerInfo.activity + "qaa/game-result/" + id);
                    if (!TextUtils.isEmpty(logo)) {
                        paramsToShare.setImageUrl(logo);
                    }
                }
                shareStatistics(chanel,""+id,ServerInfo.activity + "qaa/game-result/" + id);

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
        oks.show(getContext());

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
        OkHttpUtils.post(url, params, new OkHttpCallback(getContext()) {

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
